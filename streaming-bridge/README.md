# DarQuran Streaming Bridge (No-OBS)

Service Node.js qui reçoit le flux WebM du navigateur via WebSocket et le relaie vers NGINX RTMP via ffmpeg.

## Prérequis

- **Node.js** >= 18
- **ffmpeg** installé et disponible dans le `PATH`

## Installation

```bash
cd streaming-bridge
npm install
```

## Démarrage

```bash
npm start
```

Variables d'environnement optionnelles :

- `WS_PORT` : port du serveur WebSocket (défaut : `9090`)
- `RTMP_URL` : URL de base RTMP (défaut : `rtmp://localhost:1935/live`)

Exemple :

```bash
RTMP_URL=rtmp://localhost:1935/live WS_PORT=9090 node server.js
```

## Protocole WebSocket

1. Le client se connecte à `ws://localhost:9090` (ou l’URL configurée).
2. Premier message (JSON) : `{ "type": "config", "streamKey": "votre-stream-key" }`.
3. Le serveur répond `{ "type": "ready", "streamKey": "..." }`.
4. Le client envoie ensuite des **messages binaires** (chunks WebM produits par `MediaRecorder`).
5. À la déconnexion, le flux ffmpeg est arrêté.

Le flux est encodé en H.264/AAC et poussé vers `rtmp://localhost:1935/live/{streamKey}`.
