# Planification Jira - Frontend DarQuran (Angular)

## Vue d'ensemble
Ce document contient la planification complète des tâches frontend pour la plateforme DarQuran, organisée selon la hiérarchie Jira : Epic > Feature > User Story > Tâche.

---

# EPIC 1: Architecture et Configuration Projet

**Description:** Mettre en place l'architecture frontend Angular avec les bonnes pratiques, la configuration des environnements, les modules partagés et la structure des dossiers.

## Feature 1.1: Setup Initial

### US-1.1.1: Configuration du projet Angular
**Description:** En tant que développeur, je veux une architecture projet bien structurée afin de maintenir un code propre et évolutif.

**Critères d'acceptation:**
- Structure modulaire (core, features, shared, layouts)
- Configuration des environnements (dev, staging, prod)
- ESLint et Prettier configurés
- Tests unitaires configurés (Vitest)

**Tâches:**
- [ ] T-1.1.1.1: Initialiser le projet Angular avec standalone components
- [ ] T-1.1.1.2: Configurer la structure des dossiers (core, features, shared, layouts)
- [ ] T-1.1.1.3: Configurer les fichiers d'environnement
- [ ] T-1.1.1.4: Configurer ESLint et Prettier
- [ ] T-1.1.1.5: Configurer Vitest pour les tests unitaires
- [ ] T-1.1.1.6: Configurer les alias de chemins (tsconfig.paths)

### US-1.1.2: Configuration du routing
**Description:** En tant que développeur, je veux un système de routing bien organisé afin de gérer la navigation.

**Critères d'acceptation:**
- Lazy loading des modules
- Guards d'authentification
- Guards de rôles
- Routes protégées

**Tâches:**
- [ ] T-1.1.2.1: Configurer `app.routes.ts` avec lazy loading
- [ ] T-1.1.2.2: Créer `AuthGuard` dans core/guards
- [ ] T-1.1.2.3: Créer `RoleGuard` pour la protection par rôle
- [ ] T-1.1.2.4: Configurer les redirections par défaut
- [ ] T-1.1.2.5: Écrire les tests des guards

## Feature 1.2: Services Core

### US-1.2.1: Configuration HTTP
**Description:** En tant que développeur, je veux des intercepteurs HTTP configurés afin de gérer l'authentification et les erreurs globalement.

**Critères d'acceptation:**
- Intercepteur d'authentification (ajout Bearer token)
- Intercepteur d'erreurs global
- Intercepteur API (base URL)
- Refresh token automatique

**Tâches:**
- [ ] T-1.2.1.1: Créer `AuthInterceptor` pour ajouter le token JWT
- [ ] T-1.2.1.2: Créer `ApiInterceptor` pour la base URL
- [ ] T-1.2.1.3: Créer `ErrorInterceptor` pour la gestion globale des erreurs
- [ ] T-1.2.1.4: Implémenter le refresh token automatique sur 401
- [ ] T-1.2.1.5: Configurer les intercepteurs dans `app.config.ts`
- [ ] T-1.2.1.6: Écrire les tests des intercepteurs

---

# EPIC 2: Authentification

**Description:** Implémenter les fonctionnalités d'authentification utilisateur incluant la connexion, l'inscription, la réinitialisation de mot de passe et la gestion des tokens.

## Feature 2.1: Page de Connexion

### US-2.1.1: Formulaire de connexion
**Description:** En tant qu'utilisateur, je veux pouvoir me connecter avec mon email et mot de passe afin d'accéder à mon espace.

**Critères d'acceptation:**
- Formulaire avec validation
- Affichage des erreurs
- Redirection après connexion selon le rôle
- Option "Se souvenir de moi"

**Tâches:**
- [ ] T-2.1.1.1: Créer le composant `LoginComponent`
- [ ] T-2.1.1.2: Créer le template avec formulaire réactif
- [ ] T-2.1.1.3: Créer le modèle `LoginRequest`
- [ ] T-2.1.1.4: Créer le modèle `LoginResponse`
- [ ] T-2.1.1.5: Implémenter la validation du formulaire
- [ ] T-2.1.1.6: Gérer l'affichage des erreurs
- [ ] T-2.1.1.7: Intégrer avec `AuthService`
- [ ] T-2.1.1.8: Implémenter la redirection par rôle
- [ ] T-2.1.1.9: Styliser avec composants UI (PrimeNG/Angular Material)
- [ ] T-2.1.1.10: Écrire les tests unitaires

## Feature 2.2: Page d'Inscription

### US-2.2.1: Formulaire d'inscription
**Description:** En tant que nouvel utilisateur, je veux pouvoir créer un compte afin d'accéder à la plateforme.

**Critères d'acceptation:**
- Formulaire complet avec tous les champs requis
- Validation en temps réel
- Vérification de la force du mot de passe
- Confirmation du mot de passe
- Sélection de la section (HOMME/FEMME)

**Tâches:**
- [ ] T-2.2.1.1: Créer le composant `SignupComponent`
- [ ] T-2.2.1.2: Créer le template avec formulaire multi-étapes (optionnel)
- [ ] T-2.2.1.3: Implémenter la validation des champs
- [ ] T-2.2.1.4: Créer le composant de force du mot de passe
- [ ] T-2.2.1.5: Implémenter la confirmation du mot de passe
- [ ] T-2.2.1.6: Intégrer avec l'API d'inscription
- [ ] T-2.2.1.7: Gérer les erreurs (email déjà utilisé, etc.)
- [ ] T-2.2.1.8: Styliser le composant
- [ ] T-2.2.1.9: Écrire les tests unitaires

## Feature 2.3: Réinitialisation du Mot de Passe

### US-2.3.1: Demande de réinitialisation
**Description:** En tant qu'utilisateur, je veux pouvoir demander la réinitialisation de mon mot de passe afin de récupérer l'accès à mon compte.

**Critères d'acceptation:**
- Formulaire avec email
- Message de confirmation
- Email envoyé avec lien

**Tâches:**
- [ ] T-2.3.1.1: Créer le composant `ForgotPasswordComponent`
- [ ] T-2.3.1.2: Créer le formulaire de demande
- [ ] T-2.3.1.3: Intégrer avec l'API
- [ ] T-2.3.1.4: Afficher le message de confirmation
- [ ] T-2.3.1.5: Écrire les tests

### US-2.3.2: Nouveau mot de passe
**Description:** En tant qu'utilisateur, je veux pouvoir définir un nouveau mot de passe via le lien reçu par email.

**Critères d'acceptation:**
- Validation du token
- Formulaire de nouveau mot de passe
- Confirmation et redirection

**Tâches:**
- [ ] T-2.3.2.1: Créer le composant `ResetPasswordComponent`
- [ ] T-2.3.2.2: Récupérer et valider le token depuis l'URL
- [ ] T-2.3.2.3: Créer le formulaire de nouveau mot de passe
- [ ] T-2.3.2.4: Intégrer avec l'API
- [ ] T-2.3.2.5: Rediriger vers la connexion après succès
- [ ] T-2.3.2.6: Écrire les tests

## Feature 2.4: Service d'Authentification

### US-2.4.1: Gestion des tokens
**Description:** En tant que système, je veux gérer les tokens JWT afin de maintenir la session utilisateur.

**Critères d'acceptation:**
- Stockage sécurisé des tokens (localStorage/sessionStorage)
- Décodage du JWT pour info utilisateur
- Refresh automatique avant expiration
- Déconnexion et nettoyage

**Tâches:**
- [ ] T-2.4.1.1: Créer `AuthService` avec méthodes login/logout/register
- [ ] T-2.4.1.2: Implémenter le stockage des tokens
- [ ] T-2.4.1.3: Créer les méthodes helper (isAuthenticated, getUser, getRoles)
- [ ] T-2.4.1.4: Implémenter le refresh token automatique
- [ ] T-2.4.1.5: Créer les Observables pour l'état d'authentification
- [ ] T-2.4.1.6: Écrire les tests du service

---

# EPIC 3: Layout et Navigation

**Description:** Créer les layouts principaux de l'application avec header, footer, sidebar et navigation adaptative selon le rôle de l'utilisateur.

## Feature 3.1: Layout Principal

### US-3.1.1: Header
**Description:** En tant qu'utilisateur, je veux voir un header avec navigation afin de naviguer facilement dans l'application.

**Critères d'acceptation:**
- Logo et titre
- Menu de navigation
- Informations utilisateur connecté
- Menu déroulant profil
- Bouton de déconnexion
- Responsive (mobile menu)

**Tâches:**
- [ ] T-3.1.1.1: Créer le composant `HeaderComponent`
- [ ] T-3.1.1.2: Intégrer le logo et titre
- [ ] T-3.1.1.3: Créer le menu de navigation dynamique selon le rôle
- [ ] T-3.1.1.4: Afficher les infos utilisateur
- [ ] T-3.1.1.5: Créer le menu dropdown profil
- [ ] T-3.1.1.6: Implémenter le menu hamburger mobile
- [ ] T-3.1.1.7: Styliser le composant
- [ ] T-3.1.1.8: Écrire les tests

### US-3.1.2: Footer
**Description:** En tant qu'utilisateur, je veux voir un footer avec les informations légales et liens utiles.

**Critères d'acceptation:**
- Copyright
- Liens utiles
- Contact
- Réseaux sociaux

**Tâches:**
- [ ] T-3.1.2.1: Créer le composant `FooterComponent`
- [ ] T-3.1.2.2: Intégrer les informations légales
- [ ] T-3.1.2.3: Ajouter les liens utiles
- [ ] T-3.1.2.4: Styliser le composant
- [ ] T-3.1.2.5: Écrire les tests

### US-3.1.3: Layout principal
**Description:** En tant qu'utilisateur, je veux un layout cohérent sur toutes les pages.

**Critères d'acceptation:**
- Structure header/content/footer
- Zone de contenu principale
- Support des routes enfants

**Tâches:**
- [ ] T-3.1.3.1: Créer `MainLayoutComponent`
- [ ] T-3.1.3.2: Intégrer Header et Footer
- [ ] T-3.1.3.3: Configurer le router-outlet
- [ ] T-3.1.3.4: Styliser le layout
- [ ] T-3.1.3.5: Écrire les tests

## Feature 3.2: Navigation par Rôle

### US-3.2.1: Menu dynamique selon le rôle
**Description:** En tant qu'utilisateur, je veux voir un menu adapté à mon rôle afin d'accéder rapidement à mes fonctionnalités.

**Critères d'acceptation:**
- Menu Super Admin complet
- Menu Admin section
- Menu Enseignant
- Menu Élève

**Tâches:**
- [ ] T-3.2.1.1: Créer la configuration des menus par rôle
- [ ] T-3.2.1.2: Implémenter la logique de filtrage des menus
- [ ] T-3.2.1.3: Créer le composant de navigation dynamique
- [ ] T-3.2.1.4: Écrire les tests

---

# EPIC 4: Dashboard Super Admin

**Description:** Créer le tableau de bord complet du Super Administrateur avec gestion de tous les utilisateurs, statistiques globales et configuration système.

## Feature 4.1: Vue d'ensemble

### US-4.1.1: Dashboard principal
**Description:** En tant que Super Admin, je veux voir une vue d'ensemble de la plateforme afin de monitorer l'activité.

**Critères d'acceptation:**
- Statistiques globales (utilisateurs, cours, lives)
- Graphiques d'activité
- Alertes et notifications
- Accès rapide aux fonctions principales

**Tâches:**
- [ ] T-4.1.1.1: Créer `DashboardSuperAdminComponent`
- [ ] T-4.1.1.2: Créer `DashboardService` pour récupérer les stats
- [ ] T-4.1.1.3: Créer les widgets de statistiques
- [ ] T-4.1.1.4: Intégrer les graphiques (Chart.js ou ngx-charts)
- [ ] T-4.1.1.5: Créer le panneau de notifications
- [ ] T-4.1.1.6: Styliser le dashboard
- [ ] T-4.1.1.7: Écrire les tests

## Feature 4.2: Gestion des Administrateurs

### US-4.2.1: Liste des administrateurs
**Description:** En tant que Super Admin, je veux voir la liste de tous les administrateurs afin de les gérer.

**Critères d'acceptation:**
- Tableau avec pagination
- Filtres (section, statut)
- Recherche
- Actions (modifier, supprimer)

**Tâches:**
- [ ] T-4.2.1.1: Créer `AdminManagementComponent`
- [ ] T-4.2.1.2: Créer `AdminService` avec méthodes CRUD
- [ ] T-4.2.1.3: Créer le modèle `Admin`
- [ ] T-4.2.1.4: Implémenter le tableau avec pagination
- [ ] T-4.2.1.5: Implémenter les filtres et recherche
- [ ] T-4.2.1.6: Créer les modales de confirmation
- [ ] T-4.2.1.7: Styliser le composant
- [ ] T-4.2.1.8: Écrire les tests

### US-4.2.2: Création/Modification administrateur
**Description:** En tant que Super Admin, je veux pouvoir créer et modifier des administrateurs.

**Critères d'acceptation:**
- Formulaire complet
- Validation
- Attribution de section
- Confirmation

**Tâches:**
- [ ] T-4.2.2.1: Créer le formulaire admin (modal ou page)
- [ ] T-4.2.2.2: Implémenter la validation
- [ ] T-4.2.2.3: Intégrer création/modification avec le service
- [ ] T-4.2.2.4: Écrire les tests

## Feature 4.3: Gestion des Enseignants

### US-4.3.1: Liste des enseignants
**Description:** En tant que Super Admin, je veux voir la liste de tous les enseignants.

**Critères d'acceptation:**
- Tableau paginé avec filtres
- Informations détaillées
- Actions CRUD

**Tâches:**
- [ ] T-4.3.1.1: Créer `TeacherManagementComponent`
- [ ] T-4.3.1.2: Créer `TeacherService` avec méthodes CRUD
- [ ] T-4.3.1.3: Créer le modèle `Teacher`
- [ ] T-4.3.1.4: Implémenter le tableau avec fonctionnalités
- [ ] T-4.3.1.5: Écrire les tests

### US-4.3.2: CRUD Enseignant
**Description:** En tant que Super Admin, je veux pouvoir créer, modifier et supprimer des enseignants.

**Tâches:**
- [ ] T-4.3.2.1: Créer le formulaire enseignant
- [ ] T-4.3.2.2: Implémenter la validation
- [ ] T-4.3.2.3: Intégrer avec le service
- [ ] T-4.3.2.4: Écrire les tests

## Feature 4.4: Gestion des Élèves

### US-4.4.1: Liste des élèves
**Description:** En tant que Super Admin, je veux voir la liste de tous les élèves.

**Critères d'acceptation:**
- Tableau avec pagination et filtres
- Export possible
- Actions CRUD

**Tâches:**
- [ ] T-4.4.1.1: Créer `StudentManagementComponent`
- [ ] T-4.4.1.2: Créer `StudentService` avec méthodes CRUD
- [ ] T-4.4.1.3: Créer les modèles Student
- [ ] T-4.4.1.4: Implémenter le tableau avec fonctionnalités
- [ ] T-4.4.1.5: Implémenter l'export CSV/Excel
- [ ] T-4.4.1.6: Écrire les tests

### US-4.4.2: CRUD Élève
**Description:** En tant que Super Admin, je veux pouvoir créer, modifier et supprimer des élèves.

**Tâches:**
- [ ] T-4.4.2.1: Créer le formulaire élève
- [ ] T-4.4.2.2: Implémenter la validation
- [ ] T-4.4.2.3: Intégrer avec le service
- [ ] T-4.4.2.4: Écrire les tests

## Feature 4.5: Gestion des Classes

### US-4.5.1: CRUD Classes
**Description:** En tant que Super Admin, je veux pouvoir gérer les classes/groupes d'apprentissage.

**Critères d'acceptation:**
- Liste des classes
- Création/modification
- Attribution élèves et enseignants

**Tâches:**
- [ ] T-4.5.1.1: Créer `ClassManagementComponent`
- [ ] T-4.5.1.2: Créer `ClassService`
- [ ] T-4.5.1.3: Créer le modèle `Class`
- [ ] T-4.5.1.4: Implémenter le CRUD complet
- [ ] T-4.5.1.5: Écrire les tests

## Feature 4.6: Gestion des Cours

### US-4.6.1: CRUD Cours
**Description:** En tant que Super Admin, je veux pouvoir gérer tous les cours de la plateforme.

**Critères d'acceptation:**
- Liste des cours avec filtres
- Création/modification
- Attribution enseignant
- Gestion des leçons

**Tâches:**
- [ ] T-4.6.1.1: Créer `CourseManagementComponent`
- [ ] T-4.6.1.2: Créer `CourseService`
- [ ] T-4.6.1.3: Créer les modèles Course et Lesson
- [ ] T-4.6.1.4: Implémenter le CRUD cours
- [ ] T-4.6.1.5: Implémenter la gestion des leçons
- [ ] T-4.6.1.6: Écrire les tests

---

# EPIC 5: Dashboard Admin Section

**Description:** Créer le tableau de bord de l'administrateur de section avec gestion des utilisateurs de sa section uniquement.

## Feature 5.1: Dashboard Admin

### US-5.1.1: Vue d'ensemble section
**Description:** En tant qu'Admin de section, je veux voir les statistiques de ma section.

**Critères d'acceptation:**
- Stats limités à la section
- Enseignants et élèves de la section
- Cours actifs

**Tâches:**
- [ ] T-5.1.1.1: Créer `DashboardAdminComponent`
- [ ] T-5.1.1.2: Adapter les services pour filtrer par section
- [ ] T-5.1.1.3: Créer les widgets de stats
- [ ] T-5.1.1.4: Écrire les tests

## Feature 5.2: Gestion Section

### US-5.2.1: Gestion enseignants section
**Description:** En tant qu'Admin, je veux gérer les enseignants de ma section.

**Tâches:**
- [ ] T-5.2.1.1: Réutiliser les composants du Super Admin avec filtrage
- [ ] T-5.2.1.2: Appliquer les restrictions de section
- [ ] T-5.2.1.3: Écrire les tests

### US-5.2.2: Gestion élèves section
**Description:** En tant qu'Admin, je veux gérer les élèves de ma section.

**Tâches:**
- [ ] T-5.2.2.1: Réutiliser les composants avec filtrage section
- [ ] T-5.2.2.2: Écrire les tests

---

# EPIC 6: Dashboard Enseignant

**Description:** Créer le tableau de bord de l'enseignant avec gestion de ses cours, élèves et planning.

## Feature 6.1: Dashboard Enseignant

### US-6.1.1: Vue d'ensemble enseignant
**Description:** En tant qu'Enseignant, je veux voir mon dashboard avec mes cours et élèves.

**Critères d'acceptation:**
- Mes cours assignés
- Mes élèves
- Mon planning
- Lives à venir
- Absences à valider

**Tâches:**
- [ ] T-6.1.1.1: Créer `DashboardTeacherComponent`
- [ ] T-6.1.1.2: Créer `TeacherDashboardService`
- [ ] T-6.1.1.3: Créer les modèles de dashboard
- [ ] T-6.1.1.4: Créer les widgets (cours, planning, etc.)
- [ ] T-6.1.1.5: Intégrer le calendrier des cours
- [ ] T-6.1.1.6: Écrire les tests

## Feature 6.2: Gestion des Cours Enseignant

### US-6.2.1: Mes cours
**Description:** En tant qu'Enseignant, je veux voir et gérer mes cours assignés.

**Critères d'acceptation:**
- Liste de mes cours
- Détail d'un cours
- Gestion des leçons
- Liste des élèves inscrits

**Tâches:**
- [ ] T-6.2.1.1: Créer la vue liste/détail des cours
- [ ] T-6.2.1.2: Implémenter la gestion des leçons
- [ ] T-6.2.1.3: Afficher les élèves par cours
- [ ] T-6.2.1.4: Écrire les tests

## Feature 6.3: Gestion des Absences

### US-6.3.1: Marquer les absences
**Description:** En tant qu'Enseignant, je veux pouvoir marquer les absences de mes élèves.

**Critères d'acceptation:**
- Sélection du cours/session
- Liste des élèves
- Marquage présent/absent
- Ajout de commentaire

**Tâches:**
- [ ] T-6.3.1.1: Créer le composant de gestion des absences
- [ ] T-6.3.1.2: Implémenter la sélection cours/session
- [ ] T-6.3.1.3: Créer l'interface de marquage
- [ ] T-6.3.1.4: Intégrer avec l'API absences
- [ ] T-6.3.1.5: Écrire les tests

---

# EPIC 7: Dashboard Élève

**Description:** Créer le tableau de bord de l'élève avec vue sur ses cours, sa progression et son planning.

## Feature 7.1: Dashboard Élève

### US-7.1.1: Vue d'ensemble élève
**Description:** En tant qu'Élève, je veux voir mon dashboard avec ma progression et mes cours.

**Critères d'acceptation:**
- Mes cours inscrits
- Ma progression
- Mon planning
- Prochains lives
- Mes absences

**Tâches:**
- [ ] T-7.1.1.1: Créer `DashboardStudentComponent`
- [ ] T-7.1.1.2: Créer `StudentDashboardService`
- [ ] T-7.1.1.3: Créer les modèles (DashboardSummary, etc.)
- [ ] T-7.1.1.4: Créer les widgets de progression
- [ ] T-7.1.1.5: Intégrer le calendrier
- [ ] T-7.1.1.6: Écrire les tests

## Feature 7.2: Mes Cours

### US-7.2.1: Liste de mes cours
**Description:** En tant qu'Élève, je veux voir mes cours inscrits et accéder aux leçons.

**Critères d'acceptation:**
- Liste des cours
- Accès aux leçons
- Progression par cours
- Ressources téléchargeables

**Tâches:**
- [ ] T-7.2.1.1: Créer la vue liste des cours élève
- [ ] T-7.2.1.2: Créer la vue détail cours avec leçons
- [ ] T-7.2.1.3: Afficher la progression
- [ ] T-7.2.1.4: Implémenter le téléchargement des ressources
- [ ] T-7.2.1.5: Écrire les tests

---

# EPIC 8: Live Streaming Frontend

**Description:** Implémenter les fonctionnalités de live streaming côté frontend incluant le player HLS, l'administration des lives et le chat en direct.

## Feature 8.1: Administration des Lives

### US-8.1.1: Gestion des sessions live
**Description:** En tant qu'Admin/Enseignant, je veux pouvoir créer et gérer des sessions de live.

**Critères d'acceptation:**
- Liste des sessions (planifiées, en cours, terminées)
- Création de session
- Démarrage/arrêt
- Récupération de la stream key

**Tâches:**
- [ ] T-8.1.1.1: Créer `LiveAdminComponent`
- [ ] T-8.1.1.2: Créer `LiveStreamingService`
- [ ] T-8.1.1.3: Créer les modèles (LiveSession, etc.)
- [ ] T-8.1.1.4: Implémenter la liste des sessions
- [ ] T-8.1.1.5: Créer le formulaire de création
- [ ] T-8.1.1.6: Implémenter les contrôles (start/stop)
- [ ] T-8.1.1.7: Afficher la stream key et instructions OBS
- [ ] T-8.1.1.8: Écrire les tests

## Feature 8.2: Diffusion Navigateur

### US-8.2.1: Streaming depuis le navigateur
**Description:** En tant qu'Enseignant, je veux pouvoir diffuser directement depuis mon navigateur sans OBS.

**Critères d'acceptation:**
- Capture caméra/micro
- Envoi via WebSocket
- Prévisualisation locale
- Contrôles (mute, switch camera)

**Tâches:**
- [ ] T-8.2.1.1: Créer `LiveBroadcastComponent`
- [ ] T-8.2.1.2: Créer `StreamingBridgeService` pour WebSocket
- [ ] T-8.2.1.3: Implémenter la capture MediaRecorder
- [ ] T-8.2.1.4: Créer la prévisualisation vidéo
- [ ] T-8.2.1.5: Implémenter les contrôles (mute, stop)
- [ ] T-8.2.1.6: Gérer la sélection caméra/micro
- [ ] T-8.2.1.7: Styliser l'interface de diffusion
- [ ] T-8.2.1.8: Écrire les tests

## Feature 8.3: Lecture des Lives

### US-8.3.1: Player HLS
**Description:** En tant que spectateur, je veux regarder un live en streaming HLS.

**Critères d'acceptation:**
- Player vidéo HLS.js
- Contrôles (play, pause, volume)
- Fullscreen
- Qualité adaptive

**Tâches:**
- [ ] T-8.3.1.1: Créer le composant player HLS
- [ ] T-8.3.1.2: Intégrer HLS.js
- [ ] T-8.3.1.3: Implémenter les contrôles personnalisés
- [ ] T-8.3.1.4: Gérer le fullscreen
- [ ] T-8.3.1.5: Gérer les erreurs de lecture
- [ ] T-8.3.1.6: Écrire les tests

### US-8.3.2: Page de visionnage
**Description:** En tant que spectateur, je veux une page complète pour regarder un live avec le chat.

**Critères d'acceptation:**
- Player principal
- Informations du live (titre, enseignant)
- Zone de chat
- Nombre de spectateurs

**Tâches:**
- [ ] T-8.3.2.1: Créer `LiveWatchComponent`
- [ ] T-8.3.2.2: Intégrer le player
- [ ] T-8.3.2.3: Afficher les métadonnées du live
- [ ] T-8.3.2.4: Intégrer la zone de chat
- [ ] T-8.3.2.5: Implémenter le layout responsive
- [ ] T-8.3.2.6: Écrire les tests

## Feature 8.4: Chat en Direct

### US-8.4.1: Commentaires live
**Description:** En tant que spectateur, je veux pouvoir commenter pendant un live.

**Critères d'acceptation:**
- Affichage des commentaires en temps réel
- Envoi de commentaire
- Nom automatique si connecté
- Demande de pseudo si non connecté

**Tâches:**
- [ ] T-8.4.1.1: Créer `LiveChatComponent`
- [ ] T-8.4.1.2: Créer les modèles LiveComment
- [ ] T-8.4.1.3: Implémenter l'affichage en temps réel (polling)
- [ ] T-8.4.1.4: Créer le formulaire d'envoi
- [ ] T-8.4.1.5: Gérer le cas utilisateur non connecté
- [ ] T-8.4.1.6: Auto-scroll vers le bas
- [ ] T-8.4.1.7: Écrire les tests

---

# EPIC 9: Gestion des Cours (Public)

**Description:** Créer les pages publiques de consultation et inscription aux cours.

## Feature 9.1: Catalogue des Cours

### US-9.1.1: Liste des cours disponibles
**Description:** En tant qu'utilisateur, je veux voir le catalogue des cours disponibles.

**Critères d'acceptation:**
- Liste avec filtres (niveau, catégorie)
- Recherche
- Pagination
- Vue carte ou liste

**Tâches:**
- [ ] T-9.1.1.1: Créer `CoursesComponent`
- [ ] T-9.1.1.2: Implémenter les filtres
- [ ] T-9.1.1.3: Implémenter la recherche
- [ ] T-9.1.1.4: Créer les vues carte et liste
- [ ] T-9.1.1.5: Écrire les tests

### US-9.1.2: Détail d'un cours
**Description:** En tant qu'utilisateur, je veux voir le détail d'un cours.

**Critères d'acceptation:**
- Description complète
- Liste des leçons
- Enseignant
- Bouton d'inscription

**Tâches:**
- [ ] T-9.1.2.1: Créer `CourseDetailComponent`
- [ ] T-9.1.2.2: Afficher les informations du cours
- [ ] T-9.1.2.3: Lister les leçons
- [ ] T-9.1.2.4: Intégrer le bouton d'inscription
- [ ] T-9.1.2.5: Écrire les tests

## Feature 9.2: Création de Cours (Enseignant)

### US-9.2.1: Créer un nouveau cours
**Description:** En tant qu'Enseignant, je veux pouvoir créer un nouveau cours.

**Critères d'acceptation:**
- Formulaire multi-étapes
- Upload d'image de couverture
- Ajout de leçons
- Prévisualisation

**Tâches:**
- [ ] T-9.2.1.1: Créer `CreateCourseComponent`
- [ ] T-9.2.1.2: Implémenter le formulaire étape par étape
- [ ] T-9.2.1.3: Intégrer l'upload d'image
- [ ] T-9.2.1.4: Créer l'interface d'ajout de leçons
- [ ] T-9.2.1.5: Implémenter la prévisualisation
- [ ] T-9.2.1.6: Écrire les tests

---

# EPIC 10: Notifications et Alertes

**Description:** Implémenter le système de notifications in-app avec panneau dédié et indicateurs.

## Feature 10.1: Centre de Notifications

### US-10.1.1: Panneau de notifications
**Description:** En tant qu'utilisateur, je veux voir mes notifications dans un panneau dédié.

**Critères d'acceptation:**
- Badge avec nombre non-lues
- Panneau dropdown
- Liste des notifications
- Marquage lu/non-lu
- Actions (voir, supprimer)

**Tâches:**
- [ ] T-10.1.1.1: Créer `NotificationsPanelComponent`
- [ ] T-10.1.1.2: Créer `UserNotificationService` (ou store NgRx/signal)
- [ ] T-10.1.1.3: Créer le modèle UserNotification
- [ ] T-10.1.1.4: Implémenter le badge dans le header
- [ ] T-10.1.1.5: Implémenter le panneau dropdown
- [ ] T-10.1.1.6: Implémenter le polling ou WebSocket
- [ ] T-10.1.1.7: Écrire les tests

### US-10.1.2: Page historique notifications
**Description:** En tant qu'utilisateur, je veux accéder à l'historique complet de mes notifications.

**Tâches:**
- [ ] T-10.1.2.1: Créer `NotificationsPageComponent`
- [ ] T-10.1.2.2: Implémenter la liste paginée
- [ ] T-10.1.2.3: Implémenter les filtres (type, date)
- [ ] T-10.1.2.4: Écrire les tests

---

# EPIC 11: Page d'Accueil et Pages Statiques

**Description:** Créer les pages publiques de l'application (accueil, à propos, contact, etc.).

## Feature 11.1: Page d'Accueil

### US-11.1.1: Landing page
**Description:** En tant que visiteur, je veux voir une page d'accueil attractive présentant la plateforme.

**Critères d'acceptation:**
- Hero section avec CTA
- Présentation des fonctionnalités
- Témoignages
- Section cours populaires
- Call to action inscription

**Tâches:**
- [ ] T-11.1.1.1: Créer `HomeComponent`
- [ ] T-11.1.1.2: Créer la hero section
- [ ] T-11.1.1.3: Créer la section fonctionnalités
- [ ] T-11.1.1.4: Créer la section témoignages
- [ ] T-11.1.1.5: Intégrer les cours populaires
- [ ] T-11.1.1.6: Rendre responsive
- [ ] T-11.1.1.7: Écrire les tests

## Feature 11.2: Pages Statiques

### US-11.2.1: Page À Propos
**Description:** En tant que visiteur, je veux en savoir plus sur la plateforme.

**Tâches:**
- [ ] T-11.2.1.1: Créer `AboutComponent`
- [ ] T-11.2.1.2: Rédiger le contenu
- [ ] T-11.2.1.3: Styliser la page

### US-11.2.2: Page Contact
**Description:** En tant que visiteur, je veux pouvoir contacter l'équipe.

**Tâches:**
- [ ] T-11.2.2.1: Créer `ContactComponent`
- [ ] T-11.2.2.2: Créer le formulaire de contact
- [ ] T-11.2.2.3: Intégrer l'envoi (optionnel)

### US-11.2.3: Page Non autorisé
**Description:** En tant qu'utilisateur, je veux voir une page claire quand je n'ai pas accès à une ressource.

**Tâches:**
- [ ] T-11.2.3.1: Créer `UnauthorizedComponent`
- [ ] T-11.2.3.2: Styliser avec message clair
- [ ] T-11.2.3.3: Ajouter lien de retour

---

# EPIC 12: Composants Partagés

**Description:** Créer les composants réutilisables et utilitaires partagés dans toute l'application.

## Feature 12.1: Composants UI

### US-12.1.1: Composants de table
**Description:** En tant que développeur, je veux des composants de table réutilisables.

**Tâches:**
- [ ] T-12.1.1.1: Créer un composant data-table générique
- [ ] T-12.1.1.2: Implémenter la pagination
- [ ] T-12.1.1.3: Implémenter le tri
- [ ] T-12.1.1.4: Écrire la documentation

### US-12.1.2: Composants de formulaire
**Description:** En tant que développeur, je veux des composants de formulaire réutilisables.

**Tâches:**
- [ ] T-12.1.2.1: Créer des composants input personnalisés
- [ ] T-12.1.2.2: Créer des composants select/dropdown
- [ ] T-12.1.2.3: Créer des composants de validation d'erreurs
- [ ] T-12.1.2.4: Écrire la documentation

### US-12.1.3: Composants modales
**Description:** En tant que développeur, je veux un système de modales réutilisable.

**Tâches:**
- [ ] T-12.1.3.1: Créer un service de modal
- [ ] T-12.1.3.2: Créer la modal de confirmation
- [ ] T-12.1.3.3: Créer la modal de formulaire
- [ ] T-12.1.3.4: Écrire la documentation

## Feature 12.2: Services Utilitaires

### US-12.2.1: Service de stockage local
**Description:** En tant que développeur, je veux un service de stockage local abstrait.

**Tâches:**
- [ ] T-12.2.1.1: Créer `StorageService` (localStorage wrapper)
- [ ] T-12.2.1.2: Implémenter les méthodes CRUD
- [ ] T-12.2.1.3: Écrire les tests

### US-12.2.2: Service de toast/notification
**Description:** En tant que développeur, je veux afficher des notifications toast.

**Tâches:**
- [ ] T-12.2.2.1: Créer `ToastService`
- [ ] T-12.2.2.2: Créer le composant toast
- [ ] T-12.2.2.3: Implémenter les types (success, error, warning, info)
- [ ] T-12.2.2.4: Écrire les tests

---

# EPIC 13: Responsive et Mobile

**Description:** Assurer que l'application est entièrement responsive et utilisable sur tous les appareils.

## Feature 13.1: Design Responsive

### US-13.1.1: Adaptation mobile
**Description:** En tant qu'utilisateur mobile, je veux utiliser l'application confortablement sur mon téléphone.

**Critères d'acceptation:**
- Navigation mobile
- Tableaux scrollables
- Formulaires adaptés
- Touch-friendly

**Tâches:**
- [ ] T-13.1.1.1: Auditer tous les composants pour le responsive
- [ ] T-13.1.1.2: Implémenter le menu hamburger
- [ ] T-13.1.1.3: Adapter les tableaux (cards sur mobile)
- [ ] T-13.1.1.4: Optimiser les formulaires tactiles
- [ ] T-13.1.1.5: Tester sur différentes tailles d'écran

---

# EPIC 14: Tests et Qualité

**Description:** Mettre en place les tests automatisés et outils de qualité du code.

## Feature 14.1: Tests Unitaires

### US-14.1.1: Couverture tests services
**Description:** En tant que développeur, je veux une bonne couverture de tests sur les services.

**Tâches:**
- [ ] T-14.1.1.1: Tests AuthService
- [ ] T-14.1.1.2: Tests CourseService
- [ ] T-14.1.1.3: Tests LiveStreamingService
- [ ] T-14.1.1.4: Tests des autres services
- [ ] T-14.1.1.5: Configurer le rapport de couverture

## Feature 14.2: Tests E2E

### US-14.2.1: Tests end-to-end critiques
**Description:** En tant que développeur, je veux des tests E2E sur les parcours critiques.

**Tâches:**
- [ ] T-14.2.1.1: Configurer Cypress ou Playwright
- [ ] T-14.2.1.2: Tests parcours connexion
- [ ] T-14.2.1.3: Tests parcours inscription
- [ ] T-14.2.1.4: Tests parcours cours
- [ ] T-14.2.1.5: Tests parcours live

---

# Récapitulatif

| Epic | Features | User Stories | Tâches |
|------|----------|--------------|--------|
| 1. Architecture | 2 | 3 | 16 |
| 2. Authentification | 4 | 5 | 26 |
| 3. Layout/Navigation | 2 | 4 | 17 |
| 4. Dashboard Super Admin | 6 | 8 | 34 |
| 5. Dashboard Admin | 2 | 3 | 6 |
| 6. Dashboard Enseignant | 3 | 3 | 14 |
| 7. Dashboard Élève | 2 | 2 | 10 |
| 8. Live Streaming | 4 | 5 | 29 |
| 9. Cours (Public) | 2 | 3 | 12 |
| 10. Notifications | 1 | 2 | 8 |
| 11. Accueil/Statiques | 2 | 4 | 10 |
| 12. Composants Partagés | 2 | 5 | 14 |
| 13. Responsive | 1 | 1 | 5 |
| 14. Tests | 2 | 2 | 10 |
| **TOTAL** | **35** | **50** | **211** |

---

# Priorités Suggérées

## Sprint 1-2 (MVP)
- Epic 1: Architecture et Configuration
- Epic 2: Authentification
- Epic 3: Layout et Navigation

## Sprint 3-4
- Epic 11: Page d'Accueil et Pages Statiques
- Epic 4: Dashboard Super Admin (basique)
- Epic 12: Composants Partagés (essentiels)

## Sprint 5-6
- Epic 5: Dashboard Admin Section
- Epic 6: Dashboard Enseignant
- Epic 7: Dashboard Élève

## Sprint 7-8
- Epic 8: Live Streaming Frontend
- Epic 9: Gestion des Cours (Public)

## Sprint 9-10
- Epic 10: Notifications et Alertes
- Epic 13: Responsive et Mobile
- Epic 14: Tests et Qualité

---

# Technologies et Dépendances Suggérées

| Catégorie | Technologie | Usage |
|-----------|-------------|-------|
| Framework | Angular 17+ | Core framework avec standalone components |
| UI Library | PrimeNG ou Angular Material | Composants UI |
| Forms | Reactive Forms | Gestion des formulaires |
| HTTP | HttpClient | Appels API |
| State | Signals ou NgRx | Gestion d'état |
| Routing | Angular Router | Navigation |
| Video | HLS.js | Lecture streaming HLS |
| Charts | ngx-charts ou Chart.js | Graphiques |
| Tests | Vitest + Cypress | Tests unitaires et E2E |
| Styling | Tailwind CSS ou SCSS | Styles |
