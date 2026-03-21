# Tester le module Live Streaming

## 1. Prérequis

- **PostgreSQL** et **Redis** démarrés (ex. `docker-compose up -d` si vous utilisez le compose du projet).
- **Backend** Spring Boot démarré sur le port 8080.

```bash
# Démarrer l'application
mvn spring-boot:run
```

---

## 2. Obtenir un token JWT (login)

Les routes **internes** du live (`/api/live/sessions`, start, end, etc.) exigent une authentification JWT.

### Exemple avec curl

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"VOTRE_EMAIL_ENSEIGNANT_OU_ADMIN\", \"password\": \"VOTRE_MOT_DE_PASSE\"}"
```

Réponse attendue (ex.) :

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "...",
  "user": { ... }
}
```

Copiez `accessToken` pour les requêtes suivantes (header `Authorization: Bearer <accessToken>`).

---

## 3. Tester les routes Live

Remplacez `VOTRE_TOKEN` par le token reçu au login, et `ID_SESSION` par l’id d’une session existante quand il y a un `{id}`.

### 3.1 Créer une session (Enseignant ou Admin/SuperAdmin)

**Enseignant** (il devient automatiquement le professeur de la session) :

```bash
curl -X POST http://localhost:8080/api/live/sessions \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"Cours de Tajwid - Session 1\",
    \"description\": \"Révision des règles de lecture\",
    \"streamKey\": \"tajwid-session-1\",
    \"accessType\": \"EXTERNAL\",
    \"scheduledStartAt\": \"2025-12-01T14:00:00\"
  }"
```

**Admin/SuperAdmin** (peuvent fournir un `userId` animateur) :

```bash
curl -X POST http://localhost:8080/api/live/sessions \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"Conférence Ramadan\",
    \"streamKey\": \"ramadan-2025\",
    \"accessType\": \"INTERNAL\",
    \"userId\": \"ID_ANIMATEUR\",
    \"scheduledStartAt\": \"2025-12-01T20:00:00\"
  }"
```

Réponse attendue : **201 Created** avec le JSON de la session (dont `id`).

---

### 3.2 Lister les sessions

```bash
curl -X GET "http://localhost:8080/api/live/sessions?page=0&size=10" \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

---

### 3.3 Démarrer un live (start)

```bash
curl -X POST http://localhost:8080/api/live/sessions/ID_SESSION/start \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

Réponse attendue : **200 OK** avec la session dont `status` = `LIVE`. Le corps de la réponse contient aussi **`rtmpIngestUrl`** et **`streamKey`** pour lancer la caméra (voir ci‑dessous).

---

### 3.4 Lancer la caméra (OBS / logiciel de streaming)

Quand vous avez démarré le live via l’API, le **DTO retourné** (création, start ou GET session) contient :

| Champ            | Usage |
|------------------|--------|
| **`rtmpIngestUrl`** | URL du serveur RTMP (ex. `rtmp://localhost:1935/live`). À mettre dans OBS comme **Serveur de flux** (ou "Stream URL"). |
| **`streamKey`**     | Clé du flux (ex. `tajwid-session-1`). À mettre dans OBS comme **Clé de flux** ("Stream key"). |
| **`hlsPlaybackUrl`**| URL de lecture pour les viewers (ex. `http://localhost:8081/hls/tajwid-session-1.m3u8`). |

**Étapes pour lancer la caméra avec OBS :**

1. Ouvrir **OBS Studio**.
2. **Paramètres** → **Diffusion** (Stream) :
   - **Service** : "Personnalisé…".
   - **Serveur** : coller la valeur de **`rtmpIngestUrl`** (ex. `rtmp://localhost:1935/live`).
   - **Clé de diffusion** : coller la valeur de **`streamKey`** (ex. `tajwid-session-1`).
3. Cliquer sur **Démarrer la diffusion** dans OBS.

Votre flux part alors vers NGINX RTMP. Les spectateurs utilisent **`hlsPlaybackUrl`** dans un lecteur vidéo (navigateur avec hls.js, VLC, etc.).

**Configuration** : l’URL RTMP est définie dans `application.yml` :

```yaml
app.live.rtmp-server-url: rtmp://localhost:1935/live   # ou votre domaine/IP
```

En production, utilisez l’IP ou le domaine de votre serveur (ex. `rtmp://votre-serveur.com:1935/live`).

---

### 3.5 Arrêter un live (end)

```bash
curl -X POST http://localhost:8080/api/live/sessions/ID_SESSION/end \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

Réponse attendue : **200 OK** avec `status` = `ENDED`.

---

### 3.6 Modifier une session

```bash
curl -X PUT http://localhost:8080/api/live/sessions/ID_SESSION \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"Nouveau titre\",
    \"streamKey\": \"meme-stream-key\",
    \"accessType\": \"EXTERNAL\",
    \"scheduledStartAt\": \"2025-12-02T10:00:00\"
  }"
```

---

### 3.7 Routes publiques (sans token)

Liste des sessions publiques (EXTERNAL) :

```bash
curl -X GET "http://localhost:8080/api/live/public/sessions?status=LIVE"
```

Détail d’une session publique :

```bash
curl -X GET http://localhost:8080/api/live/public/sessions/ID_SESSION
```

---

### 3.8 Commentaires (avec token)

Récupérer les commentaires :

```bash
curl -X GET http://localhost:8080/api/live/sessions/ID_SESSION/comments \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

Ajouter un commentaire :

```bash
curl -X POST http://localhost:8080/api/live/sessions/ID_SESSION/comments \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"content\": \"Très bon cours !\"}"
```

---

## 4. Vérifications rapides

| Cas | Attendu |
|-----|--------|
| Création en tant qu’**Enseignant** (sans `teacherId`) | 201, session créée avec lui comme professeur |
| Création en tant qu’**Admin** sans `teacherId` | 201 si l’admin est aussi dans la table teachers, sinon erreur métier (aucun professeur) |
| Création en tant qu’**Admin** avec `teacherId` valide | 201 |
| **Start** / **End** avec un token Enseignant ou Admin ou SuperAdmin | 200 |
| Appel **sans token** sur `POST /api/live/sessions` | 401 |
| Appel avec token **Élève** sur `POST /api/live/sessions/ID/start` | 403 |
| `GET /api/live/public/sessions` sans token | 200 |

---

## 5. Tests automatisés

Exécuter les tests du projet (dont les tests du module Live) :

```bash
mvn test
```

Les tests du contrôleur Live simulent un utilisateur Enseignant et vérifient que la création, le start et l’end répondent correctement (voir `LiveControllerTest`).

---

## 6. Workflow No-OBS (browser-based streaming)

Permet de diffuser en direct depuis le navigateur **sans OBS** : la caméra/micro sont capturés via `getUserMedia`, encodés en WebM par le navigateur, envoyés au **bridge Node.js** via WebSocket, puis le bridge relaie le flux vers NGINX RTMP via ffmpeg.

### 6.1 Architecture

```
[Angular] getUserMedia → MediaRecorder (WebM) → WebSocket (binary)
                                                      ↓
[Node.js Bridge] réception WebSocket → ffmpeg (webm → H.264/AAC) → RTMP
                                                      ↓
[NGINX RTMP] rtmp://localhost:1935/live/{streamKey} → HLS
                                                      ↓
[Viewers] http://localhost:8081/hls/{streamKey}.m3u8
```

### 6.2 Démarrer le bridge Node.js

```bash
cd streaming-bridge
npm install
npm start
```

Le serveur WebSocket écoute sur **ws://localhost:9090** (configurable via `WS_PORT`). L'URL RTMP cible est `rtmp://localhost:1935/live` (configurable via `RTMP_URL`).

### 6.3 Connexion Angular ↔ Bridge avec le streamKey

1. **Backend (LiveController)**  
   - Créer une session : `POST /api/live/sessions` (avec `streamKey` dans le body).  
   - Démarrer le live : `POST /api/live/sessions/{id}/start`.  
   - La réponse contient `streamKey`, `rtmpIngestUrl`, `hlsPlaybackUrl`.

2. **Frontend (live-broadcast)**  
   - L'utilisateur crée/charge une session et clique sur « بدء البث (server) » (start stream).  
   - Il lance la caméra (« تشغيل الكاميرا »).  
   - Il clique sur « إرسال البث (بدون OBS) » : le composant appelle `StreamingBridgeService.startStreaming(session.streamKey, mediaStream)`.  
   - Le service ouvre une WebSocket vers `environment.streamingBridgeWsUrl` (ex. `ws://localhost:9090`), envoie d'abord `{ type: 'config', streamKey: session.streamKey }`, puis envoie les chunks binaires WebM du `MediaRecorder`.

3. **Bridge**  
   - Reçoit le message `config`, lance ffmpeg avec entrée WebM (pipe) et sortie `rtmp://localhost:1935/live/{streamKey}`.  
   - Chaque message binaire reçu est écrit dans l'entrée standard de ffmpeg.

4. **Lecture**  
   - Les viewers utilisent `hlsPlaybackUrl` (ex. `http://localhost:8081/hls/{streamKey}.m3u8`) dans un lecteur HLS (ex. hls.js).

### 6.4 Configuration frontend

Dans `src/environments/environments.ts` (ou équivalent) :

```ts
streamingBridgeWsUrl: 'ws://localhost:9090'
```

En production, remplacer par l'URL du serveur hébergeant le bridge (ex. `wss://votre-domaine.com/streaming-ws` si un reverse proxy expose le WebSocket).
