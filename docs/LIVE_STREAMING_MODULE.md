# Module Live Streaming - DarQuran

## Backend (Spring Boot)

### Entités JPA
- **LiveSession** (`domain/model/entities/live/LiveSession.java`) : session de diffusion (titre, streamKey, HLS URL, statut, type d'accès INTERNAL/EXTERNAL, enregistrement, planning).
- **LiveComment** (`domain/model/entities/live/LiveComment.java`) : commentaire lié à une session (contenu, auteur User ou nom affiché pour le public).

### Enums
- **LiveSessionStatus** : SCHEDULED, LIVE, ENDED, CANCELLED, RECORDING.
- **LiveAccessType** : INTERNAL (élèves/profs), EXTERNAL (public).

### API REST
- **Interne (authentifié)** : `POST/GET/PUT/DELETE /api/live/sessions`, `GET /api/live/sessions/{id}/comments`, `POST /api/live/sessions/{id}/comments`, `POST /api/live/sessions/{id}/start|end`.
- **Public** : `GET /api/live/public/sessions`, `GET /api/live/public/sessions/{id}`, `GET|POST /api/live/public/sessions/{id}/comments`.

### WebSocket (STOMP)
- Connexion : `http://localhost:8080/ws-live` (SockJS).
- Envoi message chat : destination `/app/live/{sessionId}/chat`, body `{ sender, content, type, timestamp }`.
- Réception : s'abonner à `/topic/live/{sessionId}`.
- Présence : `/app/live/{sessionId}/presence` (JOIN/LEAVE).

### Sécurité
- `/api/live/public/**` : `permitAll()`.
- Autres endpoints live : authentification JWT requise ; `canAccess` vérifie INTERNAL vs EXTERNAL.

### Configuration
- `app.live.hls-base-url` dans `application.yml` : base URL des flux HLS (ex. `http://localhost:8081/hls` pour NGINX RTMP).

---

## Frontend (Angular)

- **Composant** : `frontend/src/app/features/live-streaming/live-streaming.component.ts` (Standalone, Signals, `inject()`, `ChangeDetectionStrategy.OnPush`, template inline).
- **Services** : `LiveApiService` (REST), `LiveWebSocketService` (STOMP pour le chat en temps réel).
- **Routes** : `/live` (liste des sessions publiques), `/live/:id` (lecture + chat).
- **UI** : Tailwind CSS, champs avec labels accessibles (WCAG), `NgOptimizedImage` disponible pour les médias statiques.

### Installation frontend
```bash
cd frontend && npm install && npm start
```
Proxy Angular vers le backend : configurer `proxy.conf.json` vers `http://localhost:8080` pour `/api` et `/ws-live` si besoin.
