# Flux de Live Streaming DarQuran : Guide Complet Étape par Étape

## 🎯 Vue d'Ensemble

Le système de live streaming de DarQuran permet aux enseignants de diffuser des cours en direct avec un système de chat intégré, une gestion fine des permissions par sections (HOMME/FEMME), et un enregistrement automatique des sessions.

---

## 🔧 Outils et Technologies Utilisés

### 📱 **Frontend (Interface Utilisateur)**
- **Angular** : Application web pour regarder les lives et interagir
- **HLS Player** : Lecteur vidéo adaptatif intégré au navigateur
- **WebSocket** : Communication temps réel pour les commentaires
- **MediaRecorder API** : Capture vidéo/audio depuis le navigateur (optionnel)

### 🖥️ **Backend (Serveur API)**
- **Spring Boot** : API REST pour la gestion des sessions et utilisateurs
- **PostgreSQL** : Base de données pour la persistance des sessions et commentaires
- **Redis** : Cache et gestion des sessions utilisateurs
- **JWT Security** : Authentification et autorisation par rôles
- **EmailService** : Notifications automatiques par email

### 🎥 **Infrastructure de Streaming**
- **NGINX RTMP** : Serveur de streaming principal (ingestion RTMP → diffusion HLS)
- **FFmpeg** : Transcodage vidéo (dans le bridge WebSocket)
- **MinIO S3** : Stockage des enregistrements
- **Docker Compose** : Orchestration de tous les services

### 🔗 **Protocoles Réseau**
- **RTMP** : Ingestion des flux depuis OBS Studio
- **HLS (HTTP Live Streaming)** : Diffusion vers les navigateurs
- **WebSocket** : Streaming depuis le navigateur vers RTMP
- **HTTP REST** : Communication API frontend ↔ backend

---

## 📋 Flux Détaillé : De la Création à la Diffusion

### 🗓️ **ÉTAPE 1 : Planification du Live**

**Qui peut créer ?** Enseignants, Admins de Section, Super Admins

```bash
# Via l'interface Angular ou appel API direct
POST /api/live/sessions
{
  "title": "Cours de Tajwid - Règles de Noon Saakinah",
  "description": "Introduction aux règles de prononciation",
  "streamKey": "tajwid_001_20241201",
  "accessType": "INTERNAL",  // ou "EXTERNAL" pour public
  "scheduledStartAt": "2024-12-01T20:00:00",
  "recordingEnabled": true
}
```

**Ce qui se passe côté système :**
- ✅ Génération automatique de l'URL HLS : `/hls/tajwid_001_20241201.m3u8`
- ✅ Attribution de la section du professeur (HOMME/FEMME)
- ✅ Sauvegarde en base avec statut `SCHEDULED`
- ✅ Retour des URLs de streaming :
  - **RTMP pour OBS** : `rtmp://localhost:1935/live/tajwid_001_20241201`
  - **HLS pour lecteur** : `http://localhost:8081/hls/tajwid_001_20241201.m3u8`

---

### 🎬 **ÉTAPE 2 : Démarrage du Streaming**

#### **Option A : Via OBS Studio (Recommandée pour la qualité)**

**Configuration OBS :**
```
Serveur RTMP : rtmp://localhost:1935/live
Clé de Stream : tajwid_001_20241201
Encodeur Vidéo : H.264 x264
Encodeur Audio : AAC
Résolution : 1280x720 ou 1920x1080
Débit : 2000-5000 Kbps selon la connexion
```

**Flux technique :**
1. **OBS capture** écran/caméra/micro
2. **Encodage temps réel** en H.264/AAC
3. **Push RTMP** vers `nginx:1935/live/tajwid_001_20241201`
4. **NGINX RTMP reçoit** et traite le flux

#### **Option B : Via Navigateur (Bridge WebSocket)**

**Pour streaming direct depuis l'interface web :**
1. **MediaRecorder API** capture caméra/micro du navigateur
2. **Chunks WebM** envoyés via WebSocket vers `localhost:9090`
3. **Bridge Node.js** réceptionne et lance **FFmpeg**
4. **FFmpeg transcode** WebM → H.264/AAC → FLV → RTMP
5. **Relay vers NGINX** sur le même endpoint RTMP

---

### ⚡ **ÉTAPE 3 : Activation du Direct**

**Action enseignant :**
```bash
POST /api/live/sessions/tajwid_001_20241201/start
```

**Traitement automatique :**
1. **Mise à jour BDD** : `SCHEDULED` → `LIVE` + timestamp `startedAt`
2. **Déclenchement notifications email** selon les règles :
   - **Super Admin** → tous les utilisateurs de la plateforme
   - **Admin Section** → utilisateurs de la même section uniquement
   - **Enseignant** → utilisateurs de sa section uniquement
3. **Email envoyé** avec lien direct vers le live
4. **Session devient visible** dans les listes publiques/internes

---

### 📺 **ÉTAPE 4 : Conversion et Diffusion HLS**

**Processus automatique NGINX :**

```nginx
# Configuration dans nginx.conf
rtmp {
    application live {
        live on;
        hls on;
        hls_path /tmp/hls;           # Dossier de stockage temporaire
        hls_fragment 1;              # Segments de 1 seconde (faible latence)
        hls_playlist_length 4;       # Garde 4 segments en mémoire
    }
}
```

**Ce qui est généré automatiquement :**
- **Playlist M3U8** : `/tmp/hls/tajwid_001_20241201/index.m3u8`
- **Segments vidéo TS** : `segment001.ts`, `segment002.ts`, etc.
- **Mise à jour continue** : nouveaux segments toutes les secondes
- **Auto-cleanup** : anciens segments supprimés automatiquement

**Exemple de playlist générée :**
```m3u8
#EXTM3U
#EXT-X-VERSION:3
#EXT-X-TARGETDURATION:2
#EXT-X-MEDIA-SEQUENCE:1450
#EXTINF:1.000,
segment001450.ts
#EXTINF:1.000,
segment001451.ts
#EXTINF:1.000,
segment001452.ts
#EXT-X-ENDLIST
```

---

### 👥 **ÉTAPE 5 : Accès et Visualisation**

#### **Côté Utilisateurs (Angular Frontend)**

**Récupération des lives disponibles :**
```typescript
// Élèves/Public : seulement les lives autorisés à leur section
GET /api/live/sessions/my-section?status=LIVE

// Ou lives publics pour non-authentifiés
GET /api/live/public/sessions?status=LIVE
```

**Lecture du stream HLS :**
```typescript
// URL récupérée depuis l'API
const hlsUrl = 'http://localhost:8081/hls/tajwid_001_20241201.m3u8';

// Player HTML5 avec support HLS
<video controls>
  <source src="hlsUrl" type="application/vnd.apple.mpegurl">
</video>
```

**Configuration CORS automatique NGINX :**
```nginx
location /hls {
    add_header Access-Control-Allow-Origin * always;
    add_header Access-Control-Allow-Methods "GET, HEAD, OPTIONS";
    add_header Cache-Control no-cache;
}
```

#### **Système de Permissions**

**Contrôle d'accès automatique :**
```java
// Dans LiveController.java
public boolean canAccess(String sessionId, Section viewerSection) {
    LiveSession session = findById(sessionId);

    if (session.getAccessType() == EXTERNAL) {
        return true; // Ouvert à tous
    }

    if (session.getAccessType() == INTERNAL) {
        return viewerSection != null &&
               viewerSection == session.getSection(); // Même section seulement
    }
}
```

---

### 💬 **ÉTAPE 6 : Interaction Temps Réel (Chat)**

#### **Côté Utilisateur Authentifié**
```bash
# Poster un commentaire
POST /api/live/sessions/tajwid_001_20241201/comments
{
  "content": "Pouvez-vous répéter la règle du Idghaam ?"
}
# Le nom d'utilisateur est automatiquement récupéré du token JWT

# Récupérer les commentaires
GET /api/live/sessions/tajwid_001_20241201/comments
```

#### **Côté Utilisateur Public (Non-authentifié)**
```bash
# Pour les lives EXTERNAL seulement
POST /api/live/public/sessions/tajwid_001_20241201/comments
{
  "content": "Merci pour ce cours très clair !",
  "authorDisplayName": "Ahmed_92"  # Obligatoire pour les publics
}
```

**Persistance temps réel :**
- Commentaires stockés dans **PostgreSQL** avec timestamps
- **Polling côté frontend** pour updates (toutes les 2-5 secondes)
- **Pas de WebSocket push** implémenté actuellement (peut être ajouté)

---

### 🏁 **ÉTAPE 7 : Fin de Session**

#### **Arrêt du Streaming**
```bash
# L'enseignant stoppe le stream
POST /api/live/sessions/tajwid_001_20241201/end
```

**Processus automatique :**
1. **Mise à jour statut** : `LIVE` → `ENDED`
2. **Timestamp final** : `endedAt` enregistré
3. **Fermeture RTMP** : NGINX détecte la déconnexion
4. **Cleanup HLS** : segments temporaires conservés quelques minutes puis supprimés

#### **Enregistrement Permanent (si activé)**
```bash
# Si recordingEnabled = true dans la session
1. NGINX sauvegarde en MP4 pendant le live
2. Upload automatique vers MinIO S3
3. Génération URL permanente
4. Mise à jour BDD recordingUrl
```

**Exemple d'URL d'enregistrement :**
```
http://localhost:9000/darquran-media/recordings/2024/12/tajwid_001_20241201.mp4
```

---

## 🔐 Système de Sécurité et Permissions

### **Authentification JWT**
- Token généré à la connexion, validé à chaque requête
- Expiration configurable (défaut : 24h)
- Refresh token stocké dans Redis

### **Hiérarchie des Rôles**

| Rôle | Créer Live | Démarrer/Arrêter | Voir Tous | Commenter |
|------|------------|------------------|-----------|-----------|
| **SUPER_ADMIN** | ✅ Universal | ✅ Universal | ✅ All sessions | ✅ |
| **ADMIN_SECTION** | ✅ Sa section | ✅ Sa section | ✅ Sa section + PUBLIC | ✅ |
| **ENSEIGNANT** | ✅ Auto-assigné | ✅ Ses sessions | ✅ Sa section + PUBLIC | ✅ |
| **ELEVE** | ❌ | ❌ | ✅ Sa section + PUBLIC | ✅ |

### **Contrôle d'Accès par Section**

**Règles automatiques :**
- **Session INTERNAL + Section HOMME** → Visible uniquement pour utilisateurs Section HOMME
- **Session INTERNAL + Section FEMME** → Visible uniquement pour utilisateurs Section FEMME
- **Session EXTERNAL** → Visible pour tous (même non-authentifiés)

---

## 📊 Monitoring et Performance

### **Métriques Clés Surveillées**

**Côté Streaming :**
- **Latence HLS** : ~3-10 secondes (configurable via fragment size)
- **Débit RTMP** : monitored via logs NGINX
- **Concurrent viewers** : tracking via accès API sessions
- **Qualité adaptative** : gérée automatiquement côté player

**Côté Backend :**
- **Response time API** : Spring Boot Actuator metrics
- **Database performance** : PostgreSQL slow query log
- **Cache hit rate** : Redis monitoring
- **Storage usage** : MinIO dashboard

### **Logs Utiles pour Debug**

```bash
# Spring Boot - Sessions et API calls
docker-compose logs -f backend | grep "LiveSession"

# NGINX RTMP - Connexions streaming
docker-compose logs -f media-server | grep "rtmp"

# Bridge WebSocket - Transcoding
docker-compose logs -f streaming-bridge | grep "ffmpeg"
```

---

## ⚙️ Configuration et Optimisations

### **Variables d'Environnement Production**

```env
# Adapter selon l'infrastructure
HLS_BASE_URL=https://stream.darquran.com/hls
RTMP_SERVER_URL=rtmp://stream.darquran.com:1935/live
FRONT_URL=https://darquran.com

# Performance HLS
HLS_FRAGMENT_SIZE=1         # Segments de 1 seconde (faible latence)
HLS_PLAYLIST_LENGTH=6       # 6 segments en mémoire (6 sec de buffer)

# Stockage
S3_ENABLED=true
S3_BUCKET=darquran-recordings
S3_PUBLIC_BASE_URL=https://cdn.darquran.com/media
```

### **Optimisations Recommandées**

**Pour Performance :**
- **CDN** : Distribuer HLS via CloudFlare/AWS CloudFront
- **Load Balancing** : nginx-rtmp clustering
- **Database indexing** : Index sur streamKey, section, status

**Pour Scalabilité :**
- **Horizontal scaling** : Multiple instances backend
- **Shared storage** : NFS ou S3 pour segments HLS
- **Message Queue** : Redis Pub/Sub pour notifications temps réel

**Pour Monitoring :**
- **Prometheus + Grafana** : Métriques détaillées
- **ELK Stack** : Centralisation des logs
- **Health checks** : Spring Boot Actuator endpoints

---

## 🚀 Points Clés de l'Architecture

### ✅ **Avantages**
- **Faible latence** : 3-8 secondes grâce aux segments HLS de 1s
- **Scalabilité horizontale** : API stateless + cache Redis
- **Sécurité robuste** : JWT + contrôle granulaire par section
- **Qualité adaptative** : HLS gère automatiquement le bitrate
- **Enregistrement automatique** : Pas de perte de contenu
- **Multi-device** : Compatible tous navigateurs et mobiles

### 🔄 **Flux Simplifié**
1. **Enseignant** crée session → **API** génère URLs
2. **OBS/Navigateur** pousse RTMP → **NGINX** convertit en HLS
3. **Élèves** accèdent via Angular → **Player** lit HLS en temps réel
4. **Chat interactif** via API REST → **PostgreSQL** persiste
5. **Enregistrement** auto → **MinIO S3** stockage permanent

Cette architecture garantit une expérience de live streaming fluide et sécurisée, parfaitement adaptée aux besoins éducatifs de DarQuran ! 🎓📱