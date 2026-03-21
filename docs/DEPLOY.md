# Déploiement DarQuran sur un serveur

Ce guide décrit comment déployer l’application DarQuran (backend + base de données + Redis + streaming) sur un serveur avec Docker.

---

## Serveur cible (167.71.23.224)

**Connexion SSH :**
```bash
ssh root@167.71.23.224
```
*(Entrez le mot de passe lorsque demandé. Ne committez jamais ce mot de passe dans le dépôt.)*

**Après connexion**, exécutez les étapes ci‑dessous directement sur le serveur.

### Checklist rapide (sur le serveur)

```bash
# 1. Installer Docker + Docker Compose si pas déjà fait
apt update && apt install -y docker.io docker-compose-plugin
systemctl enable docker && systemctl start docker

# 2. Cloner le projet (remplacez par l’URL réelle de votre dépôt)
cd /root
git clone https://github.com/VOTRE_ORG/DarQuran.git
cd DarQuran

# 3. Créer le fichier .env (nano .env) avec au minimum :
#    POSTGRES_PASSWORD=un_mot_de_passe_fort
#    JWT_SECRET=une_cle_secrete_longue_et_aleatoire
#    HLS_BASE_URL=http://167.71.23.224:8081/hls
#    RTMP_SERVER_URL=rtmp://167.71.23.224:1935/live
#    (+ optionnel : APP_MAIL_FROM, SPRING_MAIL_*, FRONT_URL)

# 4. Lancer tous les services
docker compose up -d --build

# 5. Vérifier
docker compose ps
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080   # 200, 401 ou 404 = API OK
```

L’API sera accessible à : **http://167.71.23.224:8080**

---

## Prérequis

- **Docker** et **Docker Compose** installés sur le serveur
- Ports ouverts : **8080** (API), **5432** (PostgreSQL, optionnel en externe), **6379** (Redis, optionnel), **1935** (RTMP), **8081** (HLS)

## 1. Récupérer le projet sur le serveur

```bash
git clone <url-du-repo> DarQuran
cd DarQuran
```

## 2. Variables d’environnement (production)

Créez un fichier `.env` à la racine du projet (ne pas le committer) :

```env
# Base de données
POSTGRES_PASSWORD=votre_mot_de_passe_fort

# JWT (obligatoire en prod)
JWT_SECRET=votre_secret_jwt_long_et_aleatoire

# Email (Brevo / SMTP)
APP_MAIL_FROM=noreply@votredomaine.com
SPRING_MAIL_HOST=smtp-relay.brevo.com
SPRING_MAIL_USERNAME=votre_username_brevo
SPRING_MAIL_PASSWORD=votre_password_brevo

# Optionnel : URL du front pour les liens dans les emails
FRONT_URL=https://votredomaine.com

# URLs publiques du streaming (IP ou domaine du serveur) pour que les clients (navigateur, OBS) puissent s’y connecter
# Exemple pour le serveur 167.71.23.224 :
HLS_BASE_URL=http://167.71.23.224:8081/hls
RTMP_SERVER_URL=rtmp://167.71.23.224:1935/live
```

## 3. Premier déploiement

Le profil `prod` utilise `ddl-auto: update` : les tables sont créées ou mises à jour au premier démarrage. Pour un environnement très strict, vous pouvez passer à `validate` dans `application-prod.yml` après la première exécution.

## 4. Lancer les services

```bash
# Build + démarrage (backend, postgres, redis, media-server)
docker compose up -d --build
```

Vérifier les conteneurs :

```bash
docker compose ps
```

L’API est disponible sur `http://<IP_SERVEUR>:8080`.

## 5. (Optionnel) Bridge de streaming navigateur

Si vous utilisez le streaming depuis le navigateur (sans OBS), lancez aussi le bridge WebSocket → RTMP :

```bash
docker compose --profile with-bridge up -d --build
```

Le bridge écoute sur le port **9090**.

## 6. Exposer l’API et le front (reverse proxy)

En production, placez un reverse proxy (Nginx ou Caddy) devant :

- **API** : `http://localhost:8080` → `https://api.votredomaine.com`
- **HLS** (streaming) : `http://localhost:8081` → par ex. `https://stream.votredomaine.com`
- **Front** : servez les fichiers Angular (build) ou une app hébergée ailleurs.

Exemple Nginx minimal pour l’API :

```nginx
server {
    listen 80;
    server_name api.votredomaine.com;
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Pensez à configurer HTTPS (Let’s Encrypt / Certbot).

## 7. Mise à jour après modification du code

```bash
git pull
docker compose up -d --build
```

## 8. Logs et dépannage

```bash
# Logs backend
docker compose logs -f backend

# Logs de tous les services
docker compose logs -f
```

En cas d’erreur au démarrage du backend, vérifier que PostgreSQL et Redis sont bien « healthy » :

```bash
docker compose ps
```

## Résumé des ports

| Service        | Port | Rôle                    |
|----------------|------|-------------------------|
| backend        | 8080 | API REST DarQuran       |
| postgres       | 5432 | Base de données         |
| redis          | 6379 | Cache / refresh tokens  |
| media-server   | 1935 | RTMP (OBS)              |
| media-server   | 8081 | HLS (lecture vidéo)     |
| streaming-bridge | 9090 | WebSocket → RTMP (optionnel) |
