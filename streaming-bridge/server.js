/**
 * DarQuran Streaming Bridge
 * Reçoit le flux WebM via WebSocket (navigateur) et le relaie vers NGINX RTMP via ffmpeg.
 *
 * Workflow:
 * 1. Le client Angular envoie d'abord un message JSON: { type: 'config', streamKey: 'xxx' }
 * 2. Ensuite il envoie des blobs binaires (chunks WebM du MediaRecorder)
 * 3. Ce serveur pipe les chunks dans ffmpeg qui encode vers rtmp://.../live/{streamKey}
 */

const WebSocket = require('ws');
const { spawn } = require('child_process');
const path = require('path');

const WS_PORT = parseInt(process.env.WS_PORT || '9090', 10);
const RTMP_BASE = process.env.RTMP_URL || 'rtmp://localhost:1935/live';

const wss = new WebSocket.Server({ port: WS_PORT });

console.log(`[Bridge] WebSocket server listening on ws://localhost:${WS_PORT}`);
console.log(`[Bridge] RTMP target: ${RTMP_BASE}/<streamKey>`);

wss.on('connection', (ws, req) => {
  let streamKey = null;
  let ffmpegProcess = null;
  let configReceived = false;

  ws.on('message', (data, isBinary) => {
    if (!isBinary) {
      try {
        const msg = JSON.parse(data.toString());
        if (msg.type === 'config' && msg.streamKey) {
          streamKey = String(msg.streamKey).trim();
          configReceived = true;
          startFfmpeg(streamKey);
          ws.send(JSON.stringify({ type: 'ready', streamKey }));
          return;
        }
      } catch (_) {
        // ignore non-JSON
      }
      return;
    }

    if (!configReceived || !streamKey || !ffmpegProcess) {
      return;
    }

    if (ffmpegProcess.stdin && !ffmpegProcess.stdin.destroyed) {
      ffmpegProcess.stdin.write(Buffer.from(data), (err) => {
        if (err) {
          console.error('[Bridge] stdin write error:', err.message);
        }
      });
    }
  });

  function startFfmpeg(key) {
    const rtmpUrl = `${RTMP_BASE}/${key}`;
    // Lecture WebM depuis stdin, encodage H.264/AAC, sortie FLV vers RTMP
    const args = [
      '-hide_banner', '-loglevel', 'warning',
      '-f', 'webm',
      '-i', 'pipe:0',
      '-c:v', 'libx264', '-preset', 'veryfast', '-tune', 'zerolatency',
      '-c:a', 'aac', '-b:a', '128k',
      '-f', 'flv',
      rtmpUrl
    ];
    ffmpegProcess = spawn('ffmpeg', args, { stdio: ['pipe', 'ignore', 'pipe'] });

    ffmpegProcess.stderr.on('data', (chunk) => {
      const line = chunk.toString().trim();
      if (line) console.log('[ffmpeg]', line);
    });

    ffmpegProcess.on('error', (err) => {
      console.error('[Bridge] ffmpeg spawn error:', err.message);
      notifyError('FFmpeg non disponible. Installez ffmpeg et vérifiez PATH.');
    });

    ffmpegProcess.on('exit', (code, signal) => {
      ffmpegProcess = null;
      if (code !== 0 && code !== null) {
        console.log(`[Bridge] ffmpeg exited code=${code} signal=${signal}`);
      }
      try {
        ws.send(JSON.stringify({ type: 'ended', code, signal }));
      } catch (_) {}
    });
  }

  function notifyError(text) {
    try {
      ws.send(JSON.stringify({ type: 'error', message: text }));
    } catch (_) {}
  }

  ws.on('close', () => {
    if (ffmpegProcess && ffmpegProcess.stdin && !ffmpegProcess.stdin.destroyed) {
      ffmpegProcess.stdin.end();
    }
    ffmpegProcess = null;
    streamKey = null;
  });
});
