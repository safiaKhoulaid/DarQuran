# Flux Complet du Live Streaming - DarQuran

## Architecture Globale

```
┌─────────────┐        ┌──────────────┐        ┌─────────────┐
│   Frontend  │───────▶│   Backend    │───────▶│  PostgreSQL │
│  (Angular)  │        │ (Spring Boot)│        │             │
└─────────────┘        └──────────────┘        └─────────────┘
       │                       │
       │                       │
       ▼                       ▼
┌─────────────┐        ┌──────────────┐
│   Streaming │        │     NGINX    │
│    Bridge   │───────▶│  RTMP Server │
│  (Node.js)  │        │              │
└─────────────┘        └──────────────┘
                              │
                              ▼
                       ┌──────────────┐
                       │  HLS Stream  │
                       │   (.m3u8)    │
                       └──────────────┘
                              │
                              ▼
                       ┌──────────────┐
                       │   Viewers    │
                       │  (Frontend)  │
                       └──────────────┘
```

---

# ÉTAPE 1 : Création de Session Live

## 1.1 Côté Frontend (Angular)

### Composant : `LiveManagementComponent` ou `DashboardComponent`

**Utilisateur :** Admin, Enseignant, ou Super Admin

**Action :** Cliquer sur "Créer un Live"

```typescript
// live-management.component.ts
createLiveSession() {
  const dialogRef = this.dialog.open(CreateLiveSessionDialogComponent, {
    width: '600px'
  });

  dialogRef.afterClosed().subscribe((formData: LiveSessionRequest) => {
    if (formData) {
      this.liveService.createSession(formData).subscribe({
        next: (response: LiveSessionResponse) => {
          console.log('Session créée:', response);
          this.toast.success('Session live créée avec succès');
          this.loadSessions(); // Recharger la liste
        },
        error: (err) => {
          console.error('Erreur création session:', err);
          this.toast.error('Impossible de créer la session');
        }
      });
    }
  });
}
```

### Formulaire de Création

**Champs requis :**
```typescript
interface LiveSessionRequest {
  title: string;              // Ex: "Cours de Tajwid - Niveau 1"
  description?: string;       // Description détaillée
  streamKey: string;          // Clé unique générée (UUID)
  scheduledStartAt: string;   // Date/heure prévue (ISO)
  accessType: 'INTERNAL' | 'EXTERNAL'; // Type d'accès
  section?: 'HOMME' | 'FEMME'; // Section (si INTERNAL)
  userId?: string;            // ID de l'animateur (optionnel)
  recordingEnabled?: boolean; // Enregistrement activé ?
}
```

**Génération du Stream Key :**
```typescript
// Génération automatique côté frontend
generateStreamKey(): string {
  return 'live_' + crypto.randomUUID();
}
```

### Service HTTP

```typescript
// live.service.ts
createSession(request: LiveSessionRequest): Observable<LiveSessionResponse> {
  return this.http.post<LiveSessionResponse>(
    `${this.apiUrl}/api/live/sessions`,
    request
  );
}
```

## 1.2 Côté Backend (Spring Boot)

### Endpoint REST : `POST /api/live/sessions`

**Contrôleur :**
```java
// LiveController.java
@PostMapping("/sessions")
public ResponseEntity<LiveSessionResponse> create(
        Authentication auth,
        @Valid @RequestBody LiveSessionRequest request) {

    // Vérification des droits
    if (!canLaunchLive(auth)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Récupération de l'utilisateur connecté
    String userId = currentUserId(auth);
    if (userId == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Création de la session
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(liveSessionService.create(userId, request));
}
```

### Service Métier : `LiveSessionServiceImpl`

```java
@Override
@Transactional
public LiveSessionResponse create(String currentUserId, LiveSessionRequest request) {

    // 1. Résoudre l'utilisateur animateur
    User user = userRepository.findById(currentUserId).orElse(null);
    if (user == null && request.getUserId() != null) {
        user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    // 2. Vérifier l'unicité de la stream key
    if (liveSessionRepository.existsByStreamKey(request.getStreamKey())) {
        throw new IllegalArgumentException("Stream key already in use");
    }

    // 3. Mapper la requête vers l'entité
    LiveSession entity = liveSessionMapper.toEntity(request);

    // 4. Générer l'URL HLS automatiquement
    entity.setHlsPlaybackUrl(hlsBaseUrl + "/" + request.getStreamKey() + ".m3u8");

    // 5. Assigner l'utilisateur et la section
    entity.setUser(user);
    entity.setSection(user.getSection());

    // 6. Si enseignant, forcer INTERNAL
    if (user.getRole() == Role.ENSEIGNANT) {
        entity.setAccessType(LiveAccessType.INTERNAL);
    }

    // 7. Sauvegarder en base de données
    LiveSession saved = liveSessionRepository.save(entity);

    // 8. Retourner la réponse avec les URLs
    return withStreamingUrls(liveSessionMapper.toResponse(saved));
}
```

### Entité LiveSession créée

```java
LiveSession {
    id: "c04f302b-297d-4ccb-8456-dabe9517ed15"
    title: "Cours de Tajwid - Niveau 1"
    description: "Introduction aux règles de Tajwid"
    streamKey: "live_a8b3c2d4-e5f6-7890-1234-567890abcdef"
    status: SCHEDULED  // Statut initial
    hlsPlaybackUrl: "http://localhost:8081/hls/live_a8b3c2d4-e5f6-7890-1234-567890abcdef.m3u8"
    scheduledStartAt: "2026-03-27T14:00:00"
    accessType: INTERNAL
    section: HOMME
    user: User(id="teacher123", nom="Ahmed", role=ENSEIGNANT)
    recordingEnabled: true
    createdAt: "2026-03-26T10:30:00"
}
```

### Réponse HTTP au Frontend

```json
{
  "id": "c04f302b-297d-4ccb-8456-dabe9517ed15",
  "title": "Cours de Tajwid - Niveau 1",
  "description": "Introduction aux règles de Tajwid",
  "streamKey": "live_a8b3c2d4-e5f6-7890-1234-567890abcdef",
  "status": "SCHEDULED",
  "hlsPlaybackUrl": "http://localhost:8081/hls/live_a8b3c2d4-e5f6-7890-1234-567890abcdef.m3u8",
  "rtmpIngestUrl": "rtmp://localhost:1935/live",
  "scheduledStartAt": "2026-03-27T14:00:00",
  "accessType": "INTERNAL",
  "section": "HOMME",
  "userName": "Ahmed",
  "userId": "teacher123",
  "recordingEnabled": true,
  "createdAt": "2026-03-26T10:30:00"
}
```

### Base de Données PostgreSQL

**Table : `live_sessions`**
```sql
INSERT INTO live_sessions (
    id,
    title,
    description,
    stream_key,
    status,
    hls_playback_url,
    scheduled_start_at,
    access_type,
    section,
    user_id,
    recording_enabled,
    created_at
) VALUES (
    'c04f302b-297d-4ccb-8456-dabe9517ed15',
    'Cours de Tajwid - Niveau 1',
    'Introduction aux règles de Tajwid',
    'live_a8b3c2d4-e5f6-7890-1234-567890abcdef',
    'SCHEDULED',
    'http://localhost:8081/hls/live_a8b3c2d4-e5f6-7890-1234-567890abcdef.m3u8',
    '2026-03-27 14:00:00',
    'INTERNAL',
    'HOMME',
    'teacher123',
    true,
    NOW()
);
```

---

# ÉTAPE 2 : Affichage de la Session

## 2.1 Frontend : Liste des Sessions

**Composant :** `LiveManagementComponent`

```typescript
// Chargement automatique après création
ngOnInit() {
  this.loadSessions();
}

loadSessions() {
  this.liveService.getAllSessions().subscribe({
    next: (response) => {
      this.sessions = response.content;
      console.log('Sessions chargées:', this.sessions);
    }
  });
}
```

**Template HTML :**
```html
<div class="sessions-list">
  <mat-card *ngFor="let session of sessions" class="session-card">
    <mat-card-header>
      <mat-card-title>{{ session.title }}</mat-card-title>
      <span class="status-badge" [class]="session.status">
        {{ session.status }}
      </span>
    </mat-card-header>

    <mat-card-content>
      <p>{{ session.description }}</p>
      <div class="session-info">
        <span><i>📅</i> {{ session.scheduledStartAt | date:'medium' }}</span>
        <span><i>👤</i> {{ session.userName }}</span>
        <span><i>🔐</i> {{ session.accessType }}</span>
      </div>

      <!-- Stream Key (visible uniquement pour l'animateur) -->
      <div class="stream-key" *ngIf="canManageSession(session)">
        <strong>Stream Key:</strong>
        <code>{{ session.streamKey }}</code>
        <button mat-icon-button (click)="copyStreamKey(session.streamKey)">
          <mat-icon>content_copy</mat-icon>
        </button>
      </div>
    </mat-card-content>

    <mat-card-actions>
      <!-- Boutons selon l'état -->
      <button mat-raised-button color="primary"
              *ngIf="session.status === 'SCHEDULED'"
              (click)="goToBroadcast(session)">
        <mat-icon>play_circle</mat-icon> Démarrer
      </button>

      <button mat-raised-button color="warn"
              *ngIf="session.status === 'LIVE'"
              (click)="endStream(session.id)">
        <mat-icon>stop_circle</mat-icon> Arrêter
      </button>

      <button mat-button
              (click)="editSession(session)">
        <mat-icon>edit</mat-icon> Modifier
      </button>
    </mat-card-actions>
  </mat-card>
</div>
```

---

# ÉTAPE 3 : Préparation de la Diffusion

## 3.1 Frontend : Page de Broadcast

**Navigation :**
```typescript
goToBroadcast(session: LiveSessionResponse) {
  this.router.navigate(['/live/broadcast', session.id]);
}
```

**Route :**
```typescript
// app.routes.ts
{
  path: 'live/broadcast/:id',
  component: LiveBroadcastComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ENSEIGNANT', 'ADMIN_SECTION', 'SUPER_ADMIN'] }
}
```

## 3.2 Composant LiveBroadcastComponent

**Chargement de la session :**
```typescript
ngOnInit() {
  this.route.params.subscribe(params => {
    const sessionId = params['id'];
    this.loadSession(sessionId);
  });
}

loadSession(id: string) {
  this.liveService.getSessionById(id).subscribe({
    next: (session) => {
      this.session = session;
      this.streamKey = session.streamKey;
      console.log('Session chargée pour broadcast:', session);
    },
    error: (err) => {
      console.error('Erreur chargement session:', err);
      this.router.navigate(['/live/manage']);
    }
  });
}
```

**Template de préparation :**
```html
<div class="broadcast-container">
  <h2>{{ session?.title }}</h2>

  <!-- Informations de la session -->
  <mat-card class="info-card">
    <h3>Configuration du Live</h3>
    <div class="config-info">
      <div class="info-row">
        <strong>Stream Key:</strong>
        <code>{{ streamKey }}</code>
        <button mat-icon-button (click)="copyToClipboard(streamKey)">
          <mat-icon>content_copy</mat-icon>
        </button>
      </div>

      <div class="info-row">
        <strong>URL RTMP (OBS):</strong>
        <code>{{ session?.rtmpIngestUrl }}</code>
      </div>

      <div class="info-row">
        <strong>URL de visionnage:</strong>
        <code>{{ session?.hlsPlaybackUrl }}</code>
      </div>
    </div>
  </mat-card>

  <!-- Choix du mode de diffusion -->
  <mat-card class="mode-selection">
    <h3>Mode de Diffusion</h3>
    <mat-radio-group [(ngModel)]="broadcastMode">
      <mat-radio-button value="browser">
        🌐 Diffuser depuis le navigateur
      </mat-radio-button>
      <mat-radio-button value="obs">
        🎥 Utiliser OBS Studio (recommandé)
      </mat-radio-button>
    </mat-radio-group>
  </mat-card>

  <!-- Instructions OBS -->
  <mat-card *ngIf="broadcastMode === 'obs'" class="obs-instructions">
    <h3>Configuration OBS</h3>
    <ol>
      <li>Ouvrez OBS Studio</li>
      <li>Allez dans <strong>Paramètres → Flux</strong></li>
      <li>Service: <code>Personnalisé</code></li>
      <li>Serveur: <code>{{ session?.rtmpIngestUrl }}</code></li>
      <li>Clé de stream: <code>{{ streamKey }}</code></li>
      <li>Cliquez sur <strong>Commencer le streaming</strong> dans OBS</li>
      <li>Puis cliquez sur le bouton ci-dessous pour marquer le live comme "LIVE"</li>
    </ol>
  </mat-card>

  <!-- Interface de diffusion navigateur -->
  <div *ngIf="broadcastMode === 'browser'">
    <app-browser-broadcaster
      [sessionId]="session?.id"
      [streamKey]="streamKey"
      (streamStarted)="onStreamStarted()"
      (streamEnded)="onStreamEnded()">
    </app-browser-broadcaster>
  </div>

  <!-- Boutons d'action -->
  <div class="actions">
    <button mat-raised-button color="primary"
            *ngIf="!isLive"
            (click)="startLiveSession()"
            [disabled]="broadcastMode === 'obs' && !obsConnected">
      <mat-icon>play_arrow</mat-icon>
      Marquer comme LIVE
    </button>

    <button mat-raised-button color="warn"
            *ngIf="isLive"
            (click)="endLiveSession()">
      <mat-icon>stop</mat-icon>
      Terminer le Live
    </button>
  </div>
</div>
```

---

# ÉTAPE 4 : Démarrage du Stream

## Option A : OBS Studio (Recommandé)

### 4.1 Configuration OBS

**Se connecter à l'enseignant's OBS:**

1. **Serveur RTMP:** `rtmp://localhost:1935/live`
2. **Stream Key:** `live_a8b3c2d4-e5f6-7890-1234-567890abcdef`

### 4.2 OBS pousse le flux vers NGINX

```
OBS Studio ──RTMP──▶ rtmp://localhost:1935/live/<streamKey>
                              │
                              ▼
                         NGINX RTMP Module
                              │
                  ┌───────────┴───────────┐
                  │                       │
                  ▼                       ▼
            HLS Output              Recording (optionnel)
    /hls/<streamKey>.m3u8         /recordings/<streamKey>.flv
```

### 4.3 NGINX RTMP Configuration

```nginx
# nginx.conf
rtmp {
    server {
        listen 1935;
        chunk_size 4096;

        application live {
            live on;

            # HLS
            hls on;
            hls_path /tmp/hls;
            hls_fragment 3s;
            hls_playlist_length 60s;

            # Recording (optionnel)
            record all;
            record_path /tmp/recordings;
            record_suffix _%Y-%m-%d_%H-%M-%S.flv;

            # Callback on_publish (optionnel)
            on_publish http://localhost:8080/api/live/callback/publish;
            on_publish_done http://localhost:8080/api/live/callback/publish_done;
        }
    }
}

http {
    server {
        listen 8081;

        location /hls {
            types {
                application/vnd.apple.mpegurl m3u8;
                video/mp2t ts;
            }
            root /tmp;
            add_header Cache-Control no-cache;
            add_header Access-Control-Allow-Origin *;
        }
    }
}
```

---

## Option B : Navigateur (WebSocket Bridge)

### 4.4 Streaming Bridge (Node.js)

**Architecture:**
```
Browser (MediaRecorder) ──WebSocket──▶ Streaming Bridge (Node.js)
                                              │
                                              ▼
                           ffmpeg ──RTMP──▶ NGINX RTMP Server
```

**Code du Bridge:**
```javascript
// streaming-bridge/server.js
const WebSocket = require('ws');
const { spawn } = require('child_process');

const wss = new WebSocket.Server({ port: 9090 });

console.log('[Bridge] WebSocket server listening on ws://localhost:9090');

wss.on('connection', (ws) => {
    let ffmpegProcess = null;

    ws.on('message', (message) => {
        try {
            const data = JSON.parse(message);

            if (data.type === 'start') {
                const streamKey = data.streamKey;
                const rtmpUrl = `rtmp://localhost:1935/live/${streamKey}`;

                console.log(`[Bridge] Starting stream to: ${rtmpUrl}`);

                // Lancer ffmpeg
                ffmpegProcess = spawn('ffmpeg', [
                    '-f', 'webm',
                    '-i', 'pipe:0',
                    '-c:v', 'libx264',
                    '-preset', 'ultrafast',
                    '-tune', 'zerolatency',
                    '-c:a', 'aac',
                    '-f', 'flv',
                    rtmpUrl
                ]);

                ffmpegProcess.stderr.on('data', (data) => {
                    console.log(`[ffmpeg] ${data}`);
                });

                ws.send(JSON.stringify({ type: 'ready' }));
            } else if (data.type === 'stop') {
                if (ffmpegProcess) {
                    ffmpegProcess.kill('SIGINT');
                    ffmpegProcess = null;
                }
                ws.send(JSON.stringify({ type: 'stopped' }));
            }
        } catch (error) {
            // Message binaire (chunk vidéo)
            if (ffmpegProcess && ffmpegProcess.stdin.writable) {
                ffmpegProcess.stdin.write(message);
            }
        }
    });

    ws.on('close', () => {
        if (ffmpegProcess) {
            ffmpegProcess.kill('SIGINT');
        }
        console.log('[Bridge] Client disconnected');
    });
});
```

### 4.5 Frontend Browser Broadcaster

```typescript
// browser-broadcaster.component.ts
export class BrowserBroadcasterComponent {
  @Input() streamKey!: string;
  @Output() streamStarted = new EventEmitter();
  @Output() streamEnded = new EventEmitter();

  private ws: WebSocket | null = null;
  private mediaRecorder: MediaRecorder | null = null;
  private localStream: MediaStream | null = null;

  async startBroadcast() {
    try {
      // 1. Capturer caméra + micro
      this.localStream = await navigator.mediaDevices.getUserMedia({
        video: { width: 1280, height: 720 },
        audio: true
      });

      // 2. Afficher la prévisualisation
      const videoPreview = document.getElementById('preview') as HTMLVideoElement;
      videoPreview.srcObject = this.localStream;

      // 3. Connecter au WebSocket bridge
      this.ws = new WebSocket('ws://localhost:9090');

      this.ws.onopen = () => {
        console.log('[Broadcaster] Connected to bridge');

        // Envoyer la commande start avec la streamKey
        this.ws!.send(JSON.stringify({
          type: 'start',
          streamKey: this.streamKey
        }));
      };

      this.ws.onmessage = (event) => {
        const data = JSON.parse(event.data);
        if (data.type === 'ready') {
          console.log('[Broadcaster] Bridge ready, starting recording');
          this.startRecording();
        }
      };

      this.streamStarted.emit();
    } catch (error) {
      console.error('[Broadcaster] Error:', error);
    }
  }

  private startRecording() {
    // MediaRecorder pour encoder le flux
    this.mediaRecorder = new MediaRecorder(this.localStream!, {
      mimeType: 'video/webm;codecs=vp8,opus',
      videoBitsPerSecond: 2500000
    });

    this.mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0 && this.ws?.readyState === WebSocket.OPEN) {
        // Envoyer les chunks au bridge
        this.ws.send(event.data);
      }
    };

    // Envoyer un chunk toutes les 100ms
    this.mediaRecorder.start(100);
    console.log('[Broadcaster] Recording started');
  }

  stopBroadcast() {
    if (this.mediaRecorder) {
      this.mediaRecorder.stop();
    }

    if (this.ws) {
      this.ws.send(JSON.stringify({ type: 'stop' }));
      this.ws.close();
    }

    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop());
    }

    this.streamEnded.emit();
  }
}
```

---

# ÉTAPE 5 : Marquer le Live comme "LIVE"

## 5.1 Frontend : Appel API

```typescript
// live-broadcast.component.ts
startLiveSession() {
  this.liveService.startSession(this.session!.id).subscribe({
    next: (updatedSession) => {
      this.session = updatedSession;
      this.isLive = true;
      this.toast.success('Le live est maintenant en cours !');
      console.log('Session status:', updatedSession.status); // "LIVE"
    },
    error: (err) => {
      console.error('Erreur démarrage live:', err);
      this.toast.error('Impossible de démarrer le live');
    }
  });
}
```

## 5.2 Backend : Démarrage de Session

**Endpoint:** `POST /api/live/sessions/{id}/start`

```java
// LiveController.java
@PostMapping("/sessions/{id}/start")
public ResponseEntity<LiveSessionResponse> startStream(
        Authentication auth,
        @PathVariable("id") String id) {

    if (!canLaunchLive(auth)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(liveSessionService.startStream(id));
}
```

**Service:**
```java
// LiveSessionServiceImpl.java
@Override
@Transactional
public LiveSessionResponse startStream(String id) {
    // 1. Charger la session
    LiveSession entity = liveSessionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Live session not found: " + id));

    // 2. Vérifier si c'était planifiée
    boolean wasScheduled = entity.getStatus() == LiveSessionStatus.SCHEDULED;

    // 3. Mettre à jour le statut
    entity.setStatus(LiveSessionStatus.LIVE);
    entity.setStartedAt(LocalDateTime.now());

    // 4. Sauvegarder
    LiveSession saved = liveSessionRepository.save(entity);

    // 5. Envoyer les notifications (email, WhatsApp, in-app)
    if (wasScheduled) {
        userNotificationService.dispatchLiveStarted(saved);
    }

    return withStreamingUrls(liveSessionMapper.toResponse(saved));
}
```

## 5.3 Service de Notifications

```java
// UserNotificationServiceImpl.java
public void dispatchLiveStarted(LiveSession session) {
    // 1. Déterminer les destinataires
    List<User> recipients = determineRecipients(session);

    // 2. Créer le message
    String message = String.format(
        "🔴 Le live \"%s\" a commencé ! Rejoignez maintenant.",
        session.getTitle()
    );

    String actionUrl = "/live/watch/" + session.getId();

    // 3. Envoyer notifications
    for (User user : recipients) {
        // Notification in-app
        createNotification(user, "LIVE_STARTED", message, actionUrl);

        // Email
        if (user.getEmailNotificationsEnabled()) {
            emailService.sendLiveStartedEmail(user.getEmail(), session);
        }

        // WhatsApp (optionnel)
        if (user.getWhatsappNotificationsEnabled()) {
            whatsAppService.sendLiveNotification(user.getTelephone(), session);
        }
    }
}

private List<User> determineRecipients(LiveSession session) {
    if (session.getAccessType() == LiveAccessType.EXTERNAL) {
        // Tous les utilisateurs
        return userRepository.findAll();
    } else {
        // Utilisateurs de la même section
        return userRepository.findBySection(session.getSection());
    }
}
```

---

# ÉTAPE 6 : Visionnage du Live

## 6.1 Frontend : Liste des Lives en Cours

**Composant:** `LiveListComponent`

```typescript
ngOnInit() {
  this.loadLiveSessions();

  // Polling toutes les 30 secondes pour mettre à jour
  interval(30000).subscribe(() => {
    this.loadLiveSessions();
  });
}

loadLiveSessions() {
  this.liveService.getLiveSessions('LIVE').subscribe({
    next: (sessions) => {
      this.liveSessions = sessions;
    }
  });
}
```

**Template:**
```html
<div class="live-sessions">
  <h2>🔴 Lives en Cours</h2>

  <mat-card *ngFor="let session of liveSessions" class="live-card">
    <div class="live-badge">LIVE</div>

    <mat-card-header>
      <mat-card-title>{{ session.title }}</mat-card-title>
    </mat-card-header>

    <mat-card-content>
      <p>{{ session.description }}</p>
      <span class="teacher">👤 {{ session.userName }}</span>
    </mat-card-content>

    <mat-card-actions>
      <button mat-raised-button color="primary"
              (click)="watchLive(session.id)">
        <mat-icon>play_circle</mat-icon>
        Regarder
      </button>
    </mat-card-actions>
  </mat-card>
</div>
```

## 6.2 Page de Visionnage

**Navigation:**
```typescript
watchLive(sessionId: string) {
  this.router.navigate(['/live/watch', sessionId]);
}
```

**Composant:** `LiveWatchComponent`

```typescript
// live-watch.component.ts
export class LiveWatchComponent implements OnInit, OnDestroy {
  session: LiveSessionResponse | null = null;
  comments: LiveCommentResponse[] = [];
  private commentsInterval: any;

  ngOnInit() {
    this.route.params.subscribe(params => {
      const sessionId = params['id'];
      this.loadSession(sessionId);
      this.loadComments(sessionId);
      this.startCommentPolling(sessionId);
    });
  }

  loadSession(id: string) {
    this.liveService.getSessionById(id).subscribe({
      next: (session) => {
        this.session = session;
        console.log('HLS URL:', session.hlsPlaybackUrl);
      },
      error: (err) => {
        console.error('Erreur:', err);
        if (err.status === 403) {
          this.toast.error('Vous n\'avez pas accès à ce live');
          this.router.navigate(['/']);
        }
      }
    });
  }

  loadComments(sessionId: string) {
    this.liveService.getComments(sessionId).subscribe({
      next: (comments) => {
        this.comments = comments;
      }
    });
  }

  startCommentPolling(sessionId: string) {
    // Polling toutes les 3 secondes
    this.commentsInterval = setInterval(() => {
      this.loadComments(sessionId);
    }, 3000);
  }

  ngOnDestroy() {
    if (this.commentsInterval) {
      clearInterval(this.commentsInterval);
    }
  }
}
```

## 6.3 Player HLS

**Composant:** `HlsPlayerComponent`

```typescript
// hls-player.component.ts
import Hls from 'hls.js';

export class HlsPlayerComponent implements AfterViewInit, OnDestroy {
  @Input() hlsUrl!: string;
  @ViewChild('videoPlayer') videoElement!: ElementRef<HTMLVideoElement>;

  private hls: Hls | null = null;

  ngAfterViewInit() {
    this.initPlayer();
  }

  initPlayer() {
    const video = this.videoElement.nativeElement;

    if (Hls.isSupported()) {
      this.hls = new Hls({
        debug: false,
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: 90
      });

      this.hls.loadSource(this.hlsUrl);
      this.hls.attachMedia(video);

      this.hls.on(Hls.Events.MANIFEST_PARSED, () => {
        console.log('[HLS] Manifest loaded, playing...');
        video.play();
      });

      this.hls.on(Hls.Events.ERROR, (event, data) => {
        console.error('[HLS] Error:', data);
        if (data.fatal) {
          switch (data.type) {
            case Hls.ErrorTypes.NETWORK_ERROR:
              console.log('[HLS] Network error, trying to recover...');
              this.hls!.startLoad();
              break;
            case Hls.ErrorTypes.MEDIA_ERROR:
              console.log('[HLS] Media error, trying to recover...');
              this.hls!.recoverMediaError();
              break;
            default:
              console.log('[HLS] Fatal error, destroying player');
              this.hls!.destroy();
              break;
          }
        }
      });
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      // Support natif Safari
      video.src = this.hlsUrl;
      video.addEventListener('loadedmetadata', () => {
        video.play();
      });
    }
  }

  ngOnDestroy() {
    if (this.hls) {
      this.hls.destroy();
    }
  }
}
```

**Template:**
```html
<div class="video-container">
  <video #videoPlayer
         controls
         autoplay
         class="video-player">
  </video>

  <div class="video-controls">
    <button mat-icon-button (click)="toggleFullscreen()">
      <mat-icon>fullscreen</mat-icon>
    </button>
  </div>
</div>
```

---

# ÉTAPE 7 : Chat en Direct

## 7.1 Composant de Chat

```typescript
// live-chat.component.ts
export class LiveChatComponent {
  @Input() sessionId!: string;
  comments: LiveCommentResponse[] = [];
  newComment = '';
  displayName = '';

  isAuthenticated = false;

  ngOnInit() {
    this.isAuthenticated = this.authService.isAuthenticated();
    this.loadComments();
    this.startPolling();
  }

  loadComments() {
    this.liveService.getComments(this.sessionId).subscribe({
      next: (comments) => {
        this.comments = comments;
        this.scrollToBottom();
      }
    });
  }

  sendComment() {
    if (!this.newComment.trim()) return;

    const request: LiveCommentRequest = {
      content: this.newComment,
      authorDisplayName: this.isAuthenticated ? undefined : this.displayName
    };

    this.liveService.addComment(this.sessionId, request).subscribe({
      next: (comment) => {
        this.comments.push(comment);
        this.newComment = '';
        this.scrollToBottom();
      }
    });
  }

  private scrollToBottom() {
    setTimeout(() => {
      const container = document.querySelector('.chat-messages');
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    }, 100);
  }
}
```

**Template:**
```html
<div class="live-chat">
  <div class="chat-header">
    <h3>💬 Chat en Direct</h3>
  </div>

  <div class="chat-messages">
    <div *ngFor="let comment of comments" class="message">
      <div class="message-author">
        {{ comment.authorName || comment.authorDisplayName }}
      </div>
      <div class="message-content">
        {{ comment.content }}
      </div>
      <div class="message-time">
        {{ comment.createdAt | date:'short' }}
      </div>
    </div>
  </div>

  <div class="chat-input">
    <!-- Si non connecté, demander un nom -->
    <input *ngIf="!isAuthenticated"
           [(ngModel)]="displayName"
           placeholder="Votre nom"
           class="display-name-input">

    <textarea [(ngModel)]="newComment"
              placeholder="Votre message..."
              (keydown.enter)="sendComment()"
              rows="2">
    </textarea>

    <button mat-raised-button color="primary"
            (click)="sendComment()"
            [disabled]="!newComment.trim() || (!isAuthenticated && !displayName.trim())">
      <mat-icon>send</mat-icon>
      Envoyer
    </button>
  </div>
</div>
```

---

# ÉTAPE 8 : Fin du Live

## 8.1 Frontend : Arrêt du Live

```typescript
// live-broadcast.component.ts
endLiveSession() {
  const confirmDialog = this.dialog.open(ConfirmDialogComponent, {
    data: {
      title: 'Terminer le Live',
      message: 'Êtes-vous sûr de vouloir terminer le live ?'
    }
  });

  confirmDialog.afterClosed().subscribe(confirmed => {
    if (confirmed) {
      this.liveService.endSession(this.session!.id).subscribe({
        next: (updatedSession) => {
          this.session = updatedSession;
          this.isLive = false;
          this.toast.success('Le live est terminé');

          // Arrêter le broadcast
          if (this.broadcastMode === 'browser') {
            this.stopBrowserBroadcast();
          }

          // Rediriger
          this.router.navigate(['/live/manage']);
        },
        error: (err) => {
          console.error('Erreur fin live:', err);
          this.toast.error('Impossible de terminer le live');
        }
      });
    }
  });
}
```

## 8.2 Backend : Fin de Session

**Endpoint:** `POST /api/live/sessions/{id}/end`

```java
// LiveController.java
@PostMapping("/sessions/{id}/end")
public ResponseEntity<LiveSessionResponse> endStream(
        Authentication auth,
        @PathVariable("id") String id) {

    if (!canLaunchLive(auth)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(liveSessionService.endStream(id));
}
```

**Service:**
```java
// LiveSessionServiceImpl.java
@Override
@Transactional
public LiveSessionResponse endStream(String id) {
    // 1. Charger la session
    LiveSession entity = liveSessionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Live session not found: " + id));

    // 2. Mettre à jour le statut
    entity.setStatus(LiveSessionStatus.ENDED);
    entity.setEndedAt(LocalDateTime.now());

    // 3. Sauvegarder
    LiveSession saved = liveSessionRepository.save(entity);

    // 4. Si enregistrement activé, uploader vers MinIO
    if (entity.getRecordingEnabled()) {
        storageService.uploadRecording(entity.getStreamKey());
    }

    return withStreamingUrls(liveSessionMapper.toResponse(saved));
}
```

---

# RÉSUMÉ DU FLUX

```
┌─────────────────────────────────────────────────────────────────┐
│                      1. CRÉATION SESSION                         │
│  Admin/Enseignant → Frontend → POST /api/live/sessions          │
│  ✓ Génère streamKey unique                                       │
│  ✓ Crée entité en BDD (status=SCHEDULED)                        │
│  ✓ Génère URL HLS automatiquement                               │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    2. PRÉPARATION BROADCAST                      │
│  Enseignant → Page Broadcast                                     │
│  ✓ Affiche streamKey et URLs                                    │
│  ✓ Choix: OBS Studio ou Navigateur                              │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                     3. DÉMARRAGE STREAM                          │
│  Option A: OBS → RTMP → NGINX                                    │
│  Option B: Browser → WebSocket → ffmpeg → RTMP → NGINX          │
│  ✓ NGINX génère HLS (.m3u8 + .ts)                               │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   4. MARQUER COMME LIVE                          │
│  POST /api/live/sessions/{id}/start                             │
│  ✓ Status: SCHEDULED → LIVE                                     │
│  ✓ Envoie notifications aux utilisateurs                        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      5. VISIONNAGE                               │
│  Spectateurs → Page Watch                                        │
│  ✓ Player HLS lit le .m3u8                                       │
│  ✓ Affiche le stream en temps réel                              │
│  ✓ Chat en direct (polling)                                     │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      6. FIN DU LIVE                              │
│  POST /api/live/sessions/{id}/end                               │
│  ✓ Status: LIVE → ENDED                                         │
│  ✓ Upload enregistrement vers MinIO (optionnel)                │
│  ✓ Arrêt du stream                                              │
└─────────────────────────────────────────────────────────────────┘
```

---

Voilà le flux complet ! Maintenant discutons de chaque partie en détail si vous voulez approfondir. 🎥
