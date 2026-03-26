/**
 * DarQuran Streaming Bridge
 * Reçoit le flux WebM via Socket.io (navigateur) et le relaie vers NGINX RTMP via ffmpeg.
 *
 * Workflow:
 * 1. Le client Angular émet 'start-stream' avec streamKey
 * 2. Ensuite il envoie des blobs binaires via 'binarystream'
 * 3. Ce serveur pipe les chunks dans ffmpeg qui encode vers rtmp://.../live/{streamKey}
 */

const path = require('path');
const fs = require('fs');
const http = require('http');
const { Server } = require('socket.io');
const { spawn } = require('child_process');

const PORT = parseInt(process.env.WS_PORT || '9090', 10);
const RTMP_BASE = (process.env.RTMP_URL || 'rtmp://localhost:1935/live').replace(/\/+$/, '');

/**
 * Chemin réel du binaire dans node_modules (ignore FFMPEG_BIN=ffmpeg qui casse spawn sans PATH).
 */
function resolveFfmpegStaticFile() {
  try {
    const pkgJson = require.resolve('ffmpeg-static/package.json');
    const dir = path.dirname(pkgJson);
    const name = process.platform === 'win32' ? 'ffmpeg.exe' : 'ffmpeg';
    const full = path.join(dir, name);
    return fs.existsSync(full) ? full : null;
  } catch (_) {
    return null;
  }
}

/**
 * 1) FFMPEG_PATH si le fichier existe
 * 2) FFMPEG_BIN si le fichier existe (chemin absolu)
 * 3) binaire téléchargé par ffmpeg-static (npm install)
 * 4) « ffmpeg » (PATH système)
 */
function resolveFfmpegBinary() {
  const fromPath = process.env.FFMPEG_PATH?.trim();
  if (fromPath && fs.existsSync(fromPath)) return fromPath;
  if (fromPath) {
    console.warn('[Bridge] FFMPEG_PATH ignoré (fichier introuvable):', fromPath);
  }

  const fromBin = process.env.FFMPEG_BIN?.trim();
  if (fromBin && fs.existsSync(fromBin)) return fromBin;
  if (fromBin && !fromBin.includes(path.sep) && !fromBin.includes('/')) {
    console.warn(
      '[Bridge] FFMPEG_BIN=',
      fromBin,
      'sans chemin absolu : ignoré.',
    );
  } else if (fromBin) {
    console.warn('[Bridge] FFMPEG_BIN ignoré (fichier introuvable):', fromBin);
  }

  const staticFile = resolveFfmpegStaticFile();
  if (staticFile) return staticFile;

  return 'ffmpeg';
}

const FFMPEG_BIN = resolveFfmpegBinary();

// Créer le serveur HTTP + Socket.io
const httpServer = http.createServer();
const io = new Server(httpServer, {
  cors: {
    origin: '*',
    methods: ['GET', 'POST']
  },
  transports: ['websocket', 'polling']
});

console.log(`[Bridge] Socket.io server starting on http://localhost:${PORT}`);
console.log(`[Bridge] FFmpeg: ${FFMPEG_BIN}`);
if (FFMPEG_BIN === 'ffmpeg') {
  console.warn(
    '[Bridge] Aucun binaire embarqué détecté : « ffmpeg » doit être dans le PATH.',
  );
}
console.log(`[Bridge] RTMP target: ${RTMP_BASE}/<streamKey>`);

io.on('connection', (socket) => {
  console.log('[Bridge] Client connected:', socket.id);

  let streamKey = null;
  let ffmpegProcess = null;

  // Le client envoie 'start-stream' avec la streamKey
  socket.on('start-stream', (key) => {
    if (!key || typeof key !== 'string') {
      socket.emit('bridge-error', 'streamKey invalide');
      return;
    }

    streamKey = key.trim();
    console.log('[Bridge] Starting stream for key:', streamKey);
    startFfmpeg(streamKey);
  });

  // Le client envoie les chunks binaires via 'binarystream'
  socket.on('binarystream', (data) => {
    if (!streamKey || !ffmpegProcess) {
      return;
    }

    if (ffmpegProcess.stdin && !ffmpegProcess.stdin.destroyed) {
      const buffer = Buffer.isBuffer(data) ? data : Buffer.from(data);
      ffmpegProcess.stdin.write(buffer, (err) => {
        if (err) {
          console.error('[Bridge] stdin write error:', err.message);
        }
      });
    }
  });

  // Le client demande l'arrêt
  socket.on('stop-stream', () => {
    console.log('[Bridge] Stop stream requested');
    cleanup();
    socket.emit('bridge-ended', 'Stream arrêté par le client');
  });

  function startFfmpeg(key) {
    const rtmpUrl = `${RTMP_BASE}/${key}`;
    console.log('[Bridge] FFmpeg target:', rtmpUrl);

    const args = [
      '-hide_banner', '-loglevel', 'warning',
      '-f', 'webm',
      '-i', 'pipe:0',
      '-c:v', 'libx264', '-preset', 'veryfast', '-tune', 'zerolatency',
      '-c:a', 'aac', '-b:a', '128k',
      '-f', 'flv',
      rtmpUrl
    ];

    ffmpegProcess = spawn(FFMPEG_BIN, args, { stdio: ['pipe', 'ignore', 'pipe'] });

    ffmpegProcess.stderr.on('data', (chunk) => {
      const line = chunk.toString().trim();
      if (line) console.log('[ffmpeg]', line);
    });

    ffmpegProcess.on('error', (err) => {
      console.error('[Bridge] ffmpeg spawn error:', err.message);
      socket.emit('bridge-error', 'FFmpeg introuvable ou erreur de lancement.');
    });

    ffmpegProcess.on('exit', (code, signal) => {
      console.log(`[Bridge] ffmpeg exited code=${code} signal=${signal}`);
      ffmpegProcess = null;
      socket.emit('bridge-ended', `FFmpeg terminé (code=${code})`);
    });

    // FFmpeg est prêt, notifier le client
    socket.emit('bridge-ready', 'FFmpeg démarré, envoyez les chunks');
  }

  function cleanup() {
    if (ffmpegProcess) {
      if (ffmpegProcess.stdin && !ffmpegProcess.stdin.destroyed) {
        ffmpegProcess.stdin.end();
      }
      ffmpegProcess = null;
    }
    streamKey = null;
  }

  socket.on('disconnect', () => {
    console.log('[Bridge] Client disconnected:', socket.id);
    cleanup();
  });
});

httpServer.listen(PORT, () => {
  console.log(`[Bridge] Server listening on http://localhost:${PORT}`);
});
