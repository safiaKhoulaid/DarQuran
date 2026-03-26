# Planification Jira - Backend DarQuran

## Vue d'ensemble
Ce document contient la planification complète des tâches backend pour la plateforme DarQuran, organisée selon la hiérarchie Jira : Epic > Feature > User Story > Tâche.

---

# EPIC 1: Authentification et Sécurité

**Description:** Mettre en place un système d'authentification sécurisé avec JWT, gestion des tokens de rafraîchissement, et blacklist des tokens invalidés. Ce module garantit l'accès sécurisé à l'application selon les rôles utilisateurs.

## Feature 1.1: Authentification JWT

### US-1.1.1: Connexion utilisateur
**Description:** En tant qu'utilisateur, je veux pouvoir me connecter avec mon email et mot de passe afin d'accéder à mon espace personnel.

**Critères d'acceptation:**
- L'utilisateur peut se connecter avec email/mot de passe
- Un JWT et refresh token sont retournés
- Les erreurs d'authentification retournent des messages appropriés

**Tâches:**
- [ ] T-1.1.1.1: Créer le DTO `LoginRequest` avec validation
- [ ] T-1.1.1.2: Créer le DTO `LoginResponse` avec tokens
- [ ] T-1.1.1.3: Implémenter la méthode `authenticate()` dans `AuthenticationService`
- [ ] T-1.1.1.4: Créer l'endpoint POST `/api/auth/login` dans `AuthenticationController`
- [ ] T-1.1.1.5: Écrire les tests unitaires pour l'authentification
- [ ] T-1.1.1.6: Écrire les tests d'intégration pour l'endpoint login

### US-1.1.2: Inscription utilisateur
**Description:** En tant que nouvel utilisateur, je veux pouvoir créer un compte afin d'accéder à la plateforme.

**Critères d'acceptation:**
- L'utilisateur peut s'inscrire avec ses informations personnelles
- Le mot de passe est hashé avec BCrypt
- Un email de confirmation est envoyé

**Tâches:**
- [ ] T-1.1.2.1: Créer le DTO `RegisterRequest` avec validations
- [ ] T-1.1.2.2: Créer le DTO `RegisterResponse`
- [ ] T-1.1.2.3: Implémenter la validation du mot de passe (value object `Password`)
- [ ] T-1.1.2.4: Implémenter la méthode `register()` dans `AuthenticationService`
- [ ] T-1.1.2.5: Créer l'endpoint POST `/api/auth/register`
- [ ] T-1.1.2.6: Intégrer l'envoi d'email de confirmation
- [ ] T-1.1.2.7: Écrire les tests unitaires et d'intégration

### US-1.1.3: Déconnexion utilisateur
**Description:** En tant qu'utilisateur connecté, je veux pouvoir me déconnecter afin de sécuriser ma session.

**Critères d'acceptation:**
- Le token JWT est ajouté à la blacklist
- Le refresh token est invalidé
- L'utilisateur est redirigé vers la page de connexion

**Tâches:**
- [ ] T-1.1.3.1: Créer le DTO `LogoutRequest`
- [ ] T-1.1.3.2: Implémenter `BlacklistService` pour gérer les tokens invalidés
- [ ] T-1.1.3.3: Implémenter la méthode `logout()` dans `AuthenticationService`
- [ ] T-1.1.3.4: Créer l'endpoint POST `/api/auth/logout`
- [ ] T-1.1.3.5: Écrire les tests

## Feature 1.2: Gestion des Refresh Tokens

### US-1.2.1: Rafraîchissement du token
**Description:** En tant qu'utilisateur connecté, je veux que mon token se rafraîchisse automatiquement afin de maintenir ma session active.

**Critères d'acceptation:**
- Un nouveau JWT est généré à partir du refresh token valide
- Le refresh token expire après 7 jours
- Les tokens expirés sont rejetés

**Tâches:**
- [ ] T-1.2.1.1: Créer l'entité `RefreshToken` avec stockage Redis
- [ ] T-1.2.1.2: Créer le repository `RefreshTokenRepository`
- [ ] T-1.2.1.3: Implémenter `RefreshTokenService` (génération, validation, suppression)
- [ ] T-1.2.1.4: Créer l'endpoint POST `/api/auth/refresh`
- [ ] T-1.2.1.5: Configurer la durée de vie des refresh tokens
- [ ] T-1.2.1.6: Écrire les tests

## Feature 1.3: Sécurité et Filtres

### US-1.3.1: Filtrage des requêtes JWT
**Description:** En tant que système, je veux valider chaque requête entrante afin de garantir que seuls les utilisateurs authentifiés accèdent aux ressources protégées.

**Critères d'acceptation:**
- Chaque requête avec un Bearer token est validée
- Les tokens blacklistés sont rejetés
- Les endpoints publics sont accessibles sans token

**Tâches:**
- [ ] T-1.3.1.1: Implémenter `JwtService` (génération, extraction, validation)
- [ ] T-1.3.1.2: Implémenter `JwtAuthenticationFilter`
- [ ] T-1.3.1.3: Configurer `SecurityConfig` avec les règles d'accès
- [ ] T-1.3.1.4: Implémenter `CustomUserDetailsService`
- [ ] T-1.3.1.5: Configurer CORS dans `CorsConfig`
- [ ] T-1.3.1.6: Écrire les tests de sécurité

---

# EPIC 2: Gestion des Utilisateurs

**Description:** Gérer les différents types d'utilisateurs de la plateforme (SuperAdmin, Admin, Enseignant, Élève) avec leurs rôles, permissions et données spécifiques selon leurs sections (Homme/Femme).

## Feature 2.1: Gestion des Super Administrateurs

### US-2.1.1: CRUD Super Admin
**Description:** En tant que Super Admin, je veux pouvoir gérer les autres super admins afin d'administrer la plateforme.

**Critères d'acceptation:**
- Seul un Super Admin peut créer/modifier/supprimer un autre Super Admin
- Les données sont validées avant enregistrement
- L'historique des modifications est tracé

**Tâches:**
- [ ] T-2.1.1.1: Créer l'entité `SuperAdmin` étendant `User`
- [ ] T-2.1.1.2: Créer `SuperAdminRepository`
- [ ] T-2.1.1.3: Créer les DTOs (Request/Response)
- [ ] T-2.1.1.4: Implémenter le service CRUD
- [ ] T-2.1.1.5: Créer les endpoints REST
- [ ] T-2.1.1.6: Écrire les tests

## Feature 2.2: Gestion des Administrateurs

### US-2.2.1: CRUD Administrateur
**Description:** En tant que Super Admin, je veux pouvoir gérer les administrateurs de section afin de déléguer la gestion des utilisateurs.

**Critères d'acceptation:**
- Un admin est assigné à une section (HOMME/FEMME)
- L'admin peut uniquement gérer les utilisateurs de sa section
- Les permissions sont correctement appliquées

**Tâches:**
- [ ] T-2.2.1.1: Créer l'entité `Admin` avec section
- [ ] T-2.2.1.2: Créer `AdminRepository`
- [ ] T-2.2.1.3: Créer `AdminRequest` et `AdminResponse` DTOs
- [ ] T-2.2.1.4: Créer `AdminMapper`
- [ ] T-2.2.1.5: Implémenter `AdminService` avec filtrage par section
- [ ] T-2.2.1.6: Créer `AdminController` avec endpoints CRUD
- [ ] T-2.2.1.7: Écrire les tests

### US-2.2.2: Statistiques administrateur
**Description:** En tant qu'Admin, je veux voir les statistiques de ma section afin de suivre l'activité.

**Critères d'acceptation:**
- Nombre d'élèves actifs
- Nombre d'enseignants
- Taux de présence
- Cours en cours

**Tâches:**
- [ ] T-2.2.2.1: Créer le DTO `AdminDashboardStats`
- [ ] T-2.2.2.2: Implémenter les méthodes de calcul des statistiques
- [ ] T-2.2.2.3: Créer l'endpoint GET `/api/admin/dashboard/stats`
- [ ] T-2.2.2.4: Écrire les tests

## Feature 2.3: Gestion des Enseignants

### US-2.3.1: CRUD Enseignant
**Description:** En tant qu'Admin, je veux pouvoir gérer les enseignants de ma section afin d'organiser les cours.

**Critères d'acceptation:**
- L'enseignant est assigné à une section
- Les spécialités sont renseignées
- Le planning peut être configuré

**Tâches:**
- [ ] T-2.3.1.1: Créer l'entité `Teacher` avec spécialités
- [ ] T-2.3.1.2: Créer `TeacherRepository`
- [ ] T-2.3.1.3: Créer `TeacherRequest` et `TeacherResponse` DTOs
- [ ] T-2.3.1.4: Créer `TeacherMapper`
- [ ] T-2.3.1.5: Implémenter `TeacherService`
- [ ] T-2.3.1.6: Créer `TeacherController`
- [ ] T-2.3.1.7: Écrire les tests

### US-2.3.2: Dashboard Enseignant
**Description:** En tant qu'Enseignant, je veux voir mon tableau de bord afin de gérer mes cours et élèves.

**Critères d'acceptation:**
- Liste des cours assignés
- Liste des élèves par cours
- Prochains cours programmés
- Absences à valider

**Tâches:**
- [ ] T-2.3.2.1: Créer les DTOs pour le dashboard enseignant
- [ ] T-2.3.2.2: Implémenter les requêtes agrégées
- [ ] T-2.3.2.3: Créer `TeacherDashboardController`
- [ ] T-2.3.2.4: Écrire les tests

## Feature 2.4: Gestion des Élèves

### US-2.4.1: CRUD Élève
**Description:** En tant qu'Admin, je veux pouvoir gérer les élèves de ma section afin de les inscrire aux cours.

**Critères d'acceptation:**
- L'élève est assigné à une section
- L'adresse et coordonnées sont enregistrées
- Le niveau est défini

**Tâches:**
- [ ] T-2.4.1.1: Créer l'entité `Student` avec adresse
- [ ] T-2.4.1.2: Créer `StudentRepository`
- [ ] T-2.4.1.3: Créer les DTOs Student
- [ ] T-2.4.1.4: Créer `StudentMapper`
- [ ] T-2.4.1.5: Implémenter `StudentService`
- [ ] T-2.4.1.6: Créer `StudentController`
- [ ] T-2.4.1.7: Écrire les tests

### US-2.4.2: Dashboard Élève
**Description:** En tant qu'Élève, je veux voir mon tableau de bord afin de suivre ma progression.

**Critères d'acceptation:**
- Mes cours inscrits
- Ma progression
- Mes prochains cours
- Mes absences

**Tâches:**
- [ ] T-2.4.2.1: Créer les DTOs pour le dashboard élève
- [ ] T-2.4.2.2: Implémenter `StudentDashboardController`
- [ ] T-2.4.2.3: Écrire les tests

---

# EPIC 3: Gestion des Cours et Leçons

**Description:** Permettre la création, organisation et gestion des cours coranique avec leurs leçons et ressources associées. Les cours sont catégorisés par niveau et statut.

## Feature 3.1: Gestion des Cours

### US-3.1.1: CRUD Cours
**Description:** En tant qu'Admin/Enseignant, je veux pouvoir créer et gérer des cours afin d'organiser l'apprentissage.

**Critères d'acceptation:**
- Un cours a un titre, description, niveau et statut
- Un cours peut avoir plusieurs leçons
- Un cours est assigné à un enseignant

**Tâches:**
- [ ] T-3.1.1.1: Créer l'entité `Course` avec relations
- [ ] T-3.1.1.2: Créer les enums `CourseStatus` et `CourseLevel`
- [ ] T-3.1.1.3: Créer `CourseRepository`
- [ ] T-3.1.1.4: Créer `CourseRequest` et `CourseResponse` DTOs
- [ ] T-3.1.1.5: Créer `CourseMapper`
- [ ] T-3.1.1.6: Implémenter `CourseService`
- [ ] T-3.1.1.7: Créer `CourseController`
- [ ] T-3.1.1.8: Écrire les tests

### US-3.1.2: Filtrage et recherche de cours
**Description:** En tant qu'utilisateur, je veux pouvoir rechercher et filtrer les cours afin de trouver celui qui me convient.

**Critères d'acceptation:**
- Recherche par titre/description
- Filtrage par niveau, statut, enseignant
- Pagination des résultats

**Tâches:**
- [ ] T-3.1.2.1: Implémenter les spécifications JPA pour le filtrage
- [ ] T-3.1.2.2: Ajouter la pagination aux endpoints
- [ ] T-3.1.2.3: Créer l'endpoint de recherche
- [ ] T-3.1.2.4: Écrire les tests

## Feature 3.2: Gestion des Leçons

### US-3.2.1: CRUD Leçon
**Description:** En tant qu'Enseignant, je veux pouvoir créer des leçons dans un cours afin de structurer l'apprentissage.

**Critères d'acceptation:**
- Une leçon appartient à un cours
- Une leçon a un type (VIDEO, DOCUMENT, QURAN_PAGE, etc.)
- L'ordre des leçons est configurable

**Tâches:**
- [ ] T-3.2.1.1: Créer l'entité `Lesson` avec relation vers Course
- [ ] T-3.2.1.2: Créer l'enum `LessonType`
- [ ] T-3.2.1.3: Créer `LessonRepository`
- [ ] T-3.2.1.4: Créer `LessonRequest` et `LessonResponse` DTOs
- [ ] T-3.2.1.5: Implémenter `LessonService`
- [ ] T-3.2.1.6: Créer `LessonController`
- [ ] T-3.2.1.7: Écrire les tests

## Feature 3.3: Gestion des Ressources

### US-3.3.1: CRUD Ressources
**Description:** En tant qu'Enseignant, je veux pouvoir ajouter des ressources aux leçons afin d'enrichir le contenu.

**Critères d'acceptation:**
- Une ressource a un type (PDF, VIDEO, AUDIO, IMAGE)
- Les fichiers sont stockés sur S3/MinIO
- Les métadonnées sont enregistrées

**Tâches:**
- [ ] T-3.3.1.1: Créer l'entité `Resource`
- [ ] T-3.3.1.2: Créer l'enum `ResourceType`
- [ ] T-3.3.1.3: Créer `ResourceRepository`
- [ ] T-3.3.1.4: Créer les DTOs et Mapper
- [ ] T-3.3.1.5: Implémenter `ResourceService`
- [ ] T-3.3.1.6: Créer `ResourceController`
- [ ] T-3.3.1.7: Écrire les tests

---

# EPIC 4: Live Streaming

**Description:** Permettre la diffusion de cours en direct via RTMP/HLS avec gestion des sessions, commentaires en temps réel, et contrôle d'accès basé sur les sections et rôles.

## Feature 4.1: Gestion des Sessions Live

### US-4.1.1: Création de session live
**Description:** En tant qu'Enseignant/Admin, je veux pouvoir créer une session de live afin de diffuser un cours en direct.

**Critères d'acceptation:**
- Une stream key unique est générée
- L'URL HLS est configurée automatiquement
- Le type d'accès (PUBLIC/INTERNAL) est défini

**Tâches:**
- [ ] T-4.1.1.1: Créer l'entité `LiveSession` avec tous les attributs
- [ ] T-4.1.1.2: Créer les enums `LiveSessionStatus` et `LiveAccessType`
- [ ] T-4.1.1.3: Créer `LiveSessionRepository`
- [ ] T-4.1.1.4: Créer `StartStreamRequest` et DTOs associés
- [ ] T-4.1.1.5: Implémenter le service de génération de stream key
- [ ] T-4.1.1.6: Implémenter `LiveService.createSession()`
- [ ] T-4.1.1.7: Créer endpoint POST `/api/live/sessions`
- [ ] T-4.1.1.8: Écrire les tests

### US-4.1.2: Démarrage de session live
**Description:** En tant qu'Enseignant/Admin, je veux pouvoir démarrer une session planifiée afin de lancer la diffusion.

**Critères d'acceptation:**
- Le statut passe de SCHEDULED à LIVE
- Les notifications sont envoyées aux utilisateurs concernés
- Le timestamp de démarrage est enregistré

**Tâches:**
- [ ] T-4.1.2.1: Implémenter `LiveService.startSession()`
- [ ] T-4.1.2.2: Intégrer l'envoi de notifications par email
- [ ] T-4.1.2.3: Implémenter le filtrage des destinataires par section/rôle
- [ ] T-4.1.2.4: Créer endpoint POST `/api/live/sessions/{id}/start`
- [ ] T-4.1.2.5: Écrire les tests

### US-4.1.3: Arrêt de session live
**Description:** En tant qu'Enseignant/Admin, je veux pouvoir arrêter une session en cours afin de terminer la diffusion.

**Critères d'acceptation:**
- Le statut passe de LIVE à ENDED
- Le timestamp de fin est enregistré
- L'enregistrement est finalisé si activé

**Tâches:**
- [ ] T-4.1.3.1: Implémenter `LiveService.endSession()`
- [ ] T-4.1.3.2: Gérer la finalisation de l'enregistrement
- [ ] T-4.1.3.3: Créer endpoint POST `/api/live/sessions/{id}/end`
- [ ] T-4.1.3.4: Écrire les tests

### US-4.1.4: Liste des sessions live
**Description:** En tant qu'utilisateur, je veux voir la liste des sessions live disponibles afin de rejoindre un cours.

**Critères d'acceptation:**
- Les sessions sont filtrées selon les droits de l'utilisateur
- Les sessions INTERNAL ne sont visibles que par la même section
- Les sessions EXTERNAL sont publiques

**Tâches:**
- [ ] T-4.1.4.1: Implémenter le filtrage par section et type d'accès
- [ ] T-4.1.4.2: Créer endpoint GET `/api/live/sessions`
- [ ] T-4.1.4.3: Créer endpoint GET `/api/live/sessions/{id}`
- [ ] T-4.1.4.4: Écrire les tests

## Feature 4.2: Commentaires en Direct

### US-4.2.1: Poster un commentaire
**Description:** En tant que spectateur, je veux pouvoir poster des commentaires pendant un live afin d'interagir.

**Critères d'acceptation:**
- Un utilisateur authentifié utilise son nom automatiquement
- Un utilisateur non-authentifié doit fournir un nom d'affichage
- Le commentaire est horodaté

**Tâches:**
- [ ] T-4.2.1.1: Créer l'entité `LiveComment`
- [ ] T-4.2.1.2: Créer `LiveCommentRepository`
- [ ] T-4.2.1.3: Créer `LiveCommentRequest` et `LiveCommentResponse`
- [ ] T-4.2.1.4: Créer `LiveCommentMapper`
- [ ] T-4.2.1.5: Implémenter le service de commentaires
- [ ] T-4.2.1.6: Créer endpoint POST `/api/live/sessions/{id}/comments`
- [ ] T-4.2.1.7: Écrire les tests

### US-4.2.2: Récupérer les commentaires
**Description:** En tant que spectateur, je veux voir les commentaires en temps réel afin de suivre les interactions.

**Critères d'acceptation:**
- Les commentaires sont triés par date
- La pagination est supportée
- Le polling est efficace

**Tâches:**
- [ ] T-4.2.2.1: Implémenter la récupération paginée des commentaires
- [ ] T-4.2.2.2: Créer endpoint GET `/api/live/sessions/{id}/comments`
- [ ] T-4.2.2.3: Optimiser les requêtes pour le polling fréquent
- [ ] T-4.2.2.4: Écrire les tests

## Feature 4.3: Enregistrement des Sessions

### US-4.3.1: Enregistrement automatique
**Description:** En tant qu'Admin, je veux que les sessions soient enregistrées afin de les revoir plus tard.

**Critères d'acceptation:**
- L'enregistrement est optionnel par session
- Les fichiers sont stockés sur MinIO/S3
- L'URL de l'enregistrement est disponible après la fin

**Tâches:**
- [ ] T-4.3.1.1: Configurer NGINX pour l'enregistrement
- [ ] T-4.3.1.2: Implémenter l'upload vers MinIO après fin de session
- [ ] T-4.3.1.3: Mettre à jour `recordingUrl` dans LiveSession
- [ ] T-4.3.1.4: Créer endpoint GET `/api/live/sessions/{id}/recording`
- [ ] T-4.3.1.5: Écrire les tests

---

# EPIC 5: Gestion des Absences

**Description:** Permettre le suivi des absences des élèves et des enseignants avec justifications et statistiques pour un meilleur suivi pédagogique.

## Feature 5.1: Absences des Élèves

### US-5.1.1: Marquer une absence élève
**Description:** En tant qu'Enseignant, je veux pouvoir marquer l'absence d'un élève afin de suivre sa présence.

**Critères d'acceptation:**
- L'absence est liée à une session/cours
- Le statut est configurable (NON_JUSTIFIEE, JUSTIFIEE, EN_ATTENTE)
- Un motif peut être ajouté

**Tâches:**
- [ ] T-5.1.1.1: Créer l'entité `StudentAbsence`
- [ ] T-5.1.1.2: Créer l'enum `AbsenceStatus`
- [ ] T-5.1.1.3: Créer `StudentAbsenceRepository`
- [ ] T-5.1.1.4: Créer `StudentAbsenceRequest` et `StudentAbsenceResponse`
- [ ] T-5.1.1.5: Implémenter `StudentAbsenceService`
- [ ] T-5.1.1.6: Créer `StudentAbsenceController`
- [ ] T-5.1.1.7: Écrire les tests

### US-5.1.2: Justifier une absence
**Description:** En tant qu'Admin, je veux pouvoir justifier une absence afin de mettre à jour le dossier de l'élève.

**Critères d'acceptation:**
- Le statut passe à JUSTIFIEE
- Le motif de justification est enregistré
- L'historique est conservé

**Tâches:**
- [ ] T-5.1.2.1: Implémenter la méthode de justification
- [ ] T-5.1.2.2: Créer endpoint PUT `/api/absences/students/{id}/justify`
- [ ] T-5.1.2.3: Écrire les tests

## Feature 5.2: Absences des Enseignants

### US-5.2.1: Déclarer une absence enseignant
**Description:** En tant qu'Enseignant, je veux pouvoir déclarer mon absence afin de prévenir l'administration.

**Critères d'acceptation:**
- La période d'absence est définie
- Un remplaçant peut être suggéré
- L'admin est notifié

**Tâches:**
- [ ] T-5.2.1.1: Créer l'entité `TeacherAbsence`
- [ ] T-5.2.1.2: Créer `TeacherAbsenceRepository`
- [ ] T-5.2.1.3: Créer `TeacherAbsenceRequest` et `TeacherAbsenceResponse`
- [ ] T-5.2.1.4: Créer `TeacherAbsenceMapper`
- [ ] T-5.2.1.5: Implémenter `TeacherAbsenceService`
- [ ] T-5.2.1.6: Créer `TeacherAbsenceController`
- [ ] T-5.2.1.7: Écrire les tests

### US-5.2.2: Valider une absence enseignant
**Description:** En tant qu'Admin, je veux pouvoir valider/refuser une absence enseignant afin de gérer les remplacements.

**Critères d'acceptation:**
- Le statut est mis à jour (APPROUVEE/REFUSEE)
- L'enseignant est notifié
- Les cours affectés sont identifiés

**Tâches:**
- [ ] T-5.2.2.1: Implémenter la validation/refus
- [ ] T-5.2.2.2: Créer l'endpoint PUT `/api/absences/teachers/{id}/status`
- [ ] T-5.2.2.3: Intégrer les notifications
- [ ] T-5.2.2.4: Écrire les tests

---

# EPIC 6: Inscriptions et Classes

**Description:** Gérer les inscriptions des élèves aux cours, l'organisation des classes et le suivi des groupes d'apprentissage.

## Feature 6.1: Gestion des Inscriptions

### US-6.1.1: Inscrire un élève à un cours
**Description:** En tant qu'Admin, je veux pouvoir inscrire un élève à un cours afin de constituer les groupes.

**Critères d'acceptation:**
- L'élève est ajouté au cours
- La date d'inscription est enregistrée
- Le statut d'inscription est actif

**Tâches:**
- [ ] T-6.1.1.1: Créer l'entité `Enrollment`
- [ ] T-6.1.1.2: Créer `EnrollmentRepository`
- [ ] T-6.1.1.3: Créer `EnrollmentRequest` DTO
- [ ] T-6.1.1.4: Implémenter `EnrollmentService`
- [ ] T-6.1.1.5: Créer `EnrollmentController`
- [ ] T-6.1.1.6: Écrire les tests

### US-6.1.2: Désinscrire un élève
**Description:** En tant qu'Admin, je veux pouvoir désinscrire un élève d'un cours afin de gérer les effectifs.

**Critères d'acceptation:**
- L'inscription est désactivée (soft delete)
- L'historique est conservé
- L'élève est notifié

**Tâches:**
- [ ] T-6.1.2.1: Implémenter le soft delete des inscriptions
- [ ] T-6.1.2.2: Créer endpoint DELETE `/api/enrollments/{id}`
- [ ] T-6.1.2.3: Écrire les tests

## Feature 6.2: Gestion des Classes/Rooms

### US-6.2.1: CRUD Classes
**Description:** En tant qu'Admin, je veux pouvoir créer des classes virtuelles afin d'organiser les groupes.

**Critères d'acceptation:**
- Une classe a un nom et une capacité
- Une classe est liée à une section
- Les cours sont assignés aux classes

**Tâches:**
- [ ] T-6.2.1.1: Créer l'entité `Room`
- [ ] T-6.2.1.2: Créer `RoomRepository`
- [ ] T-6.2.1.3: Créer les DTOs Room
- [ ] T-6.2.1.4: Implémenter `RoomService`
- [ ] T-6.2.1.5: Créer `RoomController`
- [ ] T-6.2.1.6: Écrire les tests

---

# EPIC 7: Planification et Emploi du Temps

**Description:** Permettre la création et gestion des emplois du temps pour les cours, avec assignation des créneaux horaires aux enseignants et classes.

## Feature 7.1: Gestion des Créneaux

### US-7.1.1: Créer un créneau horaire
**Description:** En tant qu'Admin, je veux pouvoir créer des créneaux horaires afin de planifier les cours.

**Critères d'acceptation:**
- Un créneau a une date, heure début/fin
- Un créneau est lié à un cours, enseignant et classe
- Les conflits sont détectés

**Tâches:**
- [ ] T-7.1.1.1: Créer l'entité `ScheduleSlot`
- [ ] T-7.1.1.2: Créer `ScheduleSlotRepository`
- [ ] T-7.1.1.3: Créer `ScheduleSlotRequest` et `ScheduleSlotResponse`
- [ ] T-7.1.1.4: Implémenter la détection de conflits
- [ ] T-7.1.1.5: Implémenter `ScheduleService`
- [ ] T-7.1.1.6: Créer `ScheduleController`
- [ ] T-7.1.1.7: Écrire les tests

### US-7.1.2: Afficher l'emploi du temps
**Description:** En tant qu'utilisateur, je veux voir mon emploi du temps afin de connaître mes cours.

**Critères d'acceptation:**
- Vue par jour/semaine/mois
- Filtrage par enseignant/élève/classe
- Export possible

**Tâches:**
- [ ] T-7.1.2.1: Implémenter les requêtes de filtrage
- [ ] T-7.1.2.2: Créer endpoint GET `/api/schedule`
- [ ] T-7.1.2.3: Créer endpoint GET `/api/schedule/teacher/{id}`
- [ ] T-7.1.2.4: Créer endpoint GET `/api/schedule/student/{id}`
- [ ] T-7.1.2.5: Écrire les tests

---

# EPIC 8: Stockage et Fichiers

**Description:** Gérer le stockage des fichiers (vidéos, documents, images) sur un système S3-compatible (MinIO) avec gestion des uploads et téléchargements sécurisés.

## Feature 8.1: Service de Stockage S3

### US-8.1.1: Upload de fichiers
**Description:** En tant qu'utilisateur autorisé, je veux pouvoir uploader des fichiers afin d'ajouter des ressources.

**Critères d'acceptation:**
- Les fichiers sont validés (type, taille)
- Les fichiers sont stockés sur MinIO
- Une URL présignée est générée pour l'accès

**Tâches:**
- [ ] T-8.1.1.1: Configurer le client S3 pour MinIO
- [ ] T-8.1.1.2: Implémenter `StorageService` (upload, download, delete)
- [ ] T-8.1.1.3: Implémenter la génération d'URLs présignées
- [ ] T-8.1.1.4: Créer `StorageController`
- [ ] T-8.1.1.5: Configurer les buckets par type de fichier
- [ ] T-8.1.1.6: Écrire les tests

### US-8.1.2: Gestion des médias
**Description:** En tant qu'Admin, je veux pouvoir gérer les fichiers stockés afin de maintenir l'espace.

**Critères d'acceptation:**
- Liste des fichiers avec métadonnées
- Suppression des fichiers orphelins
- Statistiques d'utilisation

**Tâches:**
- [ ] T-8.1.2.1: Implémenter la liste des fichiers par bucket
- [ ] T-8.1.2.2: Implémenter le nettoyage des fichiers orphelins
- [ ] T-8.1.2.3: Créer les endpoints de gestion
- [ ] T-8.1.2.4: Écrire les tests

---

# EPIC 9: Notifications

**Description:** Système de notifications multi-canal (Email, WhatsApp, In-App) pour informer les utilisateurs des événements importants de la plateforme.

## Feature 9.1: Notifications Email

### US-9.1.1: Envoi d'emails
**Description:** En tant que système, je veux pouvoir envoyer des emails afin de notifier les utilisateurs.

**Critères d'acceptation:**
- Templates HTML configurables
- Support des emails de masse
- Gestion des erreurs d'envoi

**Tâches:**
- [ ] T-9.1.1.1: Configurer le service SMTP
- [ ] T-9.1.1.2: Créer les templates email (Thymeleaf)
- [ ] T-9.1.1.3: Implémenter `EmailService`
- [ ] T-9.1.1.4: Implémenter l'envoi de masse asynchrone
- [ ] T-9.1.1.5: Écrire les tests

## Feature 9.2: Notifications WhatsApp

### US-9.2.1: Envoi WhatsApp
**Description:** En tant que système, je veux pouvoir envoyer des messages WhatsApp afin d'atteindre les utilisateurs rapidement.

**Critères d'acceptation:**
- Intégration API WhatsApp Business
- Messages templates approuvés
- Gestion des statuts de livraison

**Tâches:**
- [ ] T-9.2.1.1: Configurer l'intégration WhatsApp Business API
- [ ] T-9.2.1.2: Créer les templates de messages
- [ ] T-9.2.1.3: Implémenter `WhatsAppService`
- [ ] T-9.2.1.4: Écrire les tests

## Feature 9.3: Notifications In-App

### US-9.3.1: Notifications utilisateur
**Description:** En tant qu'utilisateur, je veux recevoir des notifications in-app afin d'être informé des événements.

**Critères d'acceptation:**
- Les notifications sont stockées
- Marquage lu/non-lu
- Historique accessible

**Tâches:**
- [ ] T-9.3.1.1: Créer l'entité `UserNotification`
- [ ] T-9.3.1.2: Créer `UserNotificationRepository`
- [ ] T-9.3.1.3: Créer les DTOs
- [ ] T-9.3.1.4: Implémenter `UserNotificationService`
- [ ] T-9.3.1.5: Créer `UserNotificationController`
- [ ] T-9.3.1.6: Écrire les tests

---

# EPIC 10: Chat en Temps Réel

**Description:** Permettre la communication en temps réel entre les utilisateurs via WebSocket pour les discussions de cours et le support.

## Feature 10.1: Chat WebSocket

### US-10.1.1: Envoi de messages
**Description:** En tant qu'utilisateur, je veux pouvoir envoyer des messages en temps réel afin de communiquer.

**Critères d'acceptation:**
- Messages envoyés via WebSocket
- Support des conversations de groupe
- Historique des messages

**Tâches:**
- [ ] T-10.1.1.1: Configurer WebSocket avec STOMP
- [ ] T-10.1.1.2: Créer `WebSocketConfig`
- [ ] T-10.1.1.3: Créer le DTO `ChatMessage`
- [ ] T-10.1.1.4: Implémenter `ChatController` avec @MessageMapping
- [ ] T-10.1.1.5: Configurer l'authentification WebSocket
- [ ] T-10.1.1.6: Écrire les tests

---

# EPIC 11: Infrastructure et DevOps

**Description:** Mettre en place l'infrastructure technique, la conteneurisation, le déploiement CI/CD et le monitoring de l'application.

## Feature 11.1: Conteneurisation Docker

### US-11.1.1: Docker Compose
**Description:** En tant que développeur, je veux pouvoir lancer l'environnement via Docker Compose afin de simplifier le développement.

**Critères d'acceptation:**
- Tous les services sont conteneurisés
- Les volumes sont configurés
- Les networks sont isolés

**Tâches:**
- [ ] T-11.1.1.1: Créer le Dockerfile optimisé pour Spring Boot
- [ ] T-11.1.1.2: Configurer le docker-compose.yml
- [ ] T-11.1.1.3: Configurer NGINX RTMP
- [ ] T-11.1.1.4: Configurer MinIO
- [ ] T-11.1.1.5: Configurer PostgreSQL et Redis
- [ ] T-11.1.1.6: Documenter le déploiement

## Feature 11.2: CI/CD

### US-11.2.1: Pipeline GitHub Actions
**Description:** En tant qu'équipe, je veux un pipeline CI/CD afin d'automatiser les déploiements.

**Critères d'acceptation:**
- Tests automatisés à chaque PR
- Build et push des images Docker
- Déploiement automatique en staging/production

**Tâches:**
- [ ] T-11.2.1.1: Créer le workflow de tests
- [ ] T-11.2.1.2: Créer le workflow de build
- [ ] T-11.2.1.3: Créer le workflow de déploiement
- [ ] T-11.2.1.4: Configurer les secrets GitHub
- [ ] T-11.2.1.5: Configurer les environnements (staging, prod)

## Feature 11.3: Monitoring

### US-11.3.1: Health Checks et Métriques
**Description:** En tant qu'ops, je veux monitorer l'application afin de détecter les problèmes.

**Critères d'acceptation:**
- Endpoints health check disponibles
- Métriques Prometheus exposées
- Logs structurés

**Tâches:**
- [ ] T-11.3.1.1: Configurer Spring Boot Actuator
- [ ] T-11.3.1.2: Exposer les métriques Prometheus
- [ ] T-11.3.1.3: Configurer les logs JSON
- [ ] T-11.3.1.4: Créer les dashboards Grafana
- [ ] T-11.3.1.5: Configurer les alertes

---

# Récapitulatif

| Epic | Features | User Stories | Tâches |
|------|----------|--------------|--------|
| 1. Authentification | 3 | 5 | 27 |
| 2. Utilisateurs | 4 | 6 | 28 |
| 3. Cours & Leçons | 3 | 4 | 22 |
| 4. Live Streaming | 3 | 6 | 29 |
| 5. Absences | 2 | 4 | 18 |
| 6. Inscriptions | 2 | 3 | 12 |
| 7. Planification | 1 | 2 | 12 |
| 8. Stockage | 1 | 2 | 10 |
| 9. Notifications | 3 | 3 | 14 |
| 10. Chat | 1 | 1 | 6 |
| 11. Infrastructure | 3 | 3 | 16 |
| **TOTAL** | **26** | **39** | **194** |

---

# Priorités Suggérées

## Sprint 1-2 (MVP)
- Epic 1: Authentification et Sécurité
- Epic 2: Gestion des Utilisateurs (basique)
- Epic 11: Infrastructure Docker

## Sprint 3-4
- Epic 3: Gestion des Cours et Leçons
- Epic 6: Inscriptions et Classes
- Epic 7: Planification

## Sprint 5-6
- Epic 4: Live Streaming
- Epic 10: Chat en Temps Réel

## Sprint 7-8
- Epic 5: Gestion des Absences
- Epic 8: Stockage et Fichiers
- Epic 9: Notifications

## Sprint 9+
- Epic 11: CI/CD et Monitoring complet
- Optimisations et améliorations
