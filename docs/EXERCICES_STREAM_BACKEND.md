# Exercices Java Stream - Backend DarQuran

Had fichier فيه exercices dial Java Stream adaptés l entities li kaynin f backend dyalk.

## Objectif
- Tt7kem mzyan f `map`, `filter`, `flatMap`, `sorted`, `distinct`
- Tst3mel `Collectors` b7al `groupingBy`, `partitioningBy`, `counting`, `averagingDouble`
- Tdir agrégations clean b style fonctionnel

## Entities concernées
- `Course` (title, status, level, isPublic, lessons)
- `Lesson` (title, orderIndex, resources)
- `Enrollment` (student, course, enrolledAt, active)
- `StudentGrade` (student, course, value, gradeDate, teacher)
- `LiveSession` (title, status, section, scheduledStartAt, comments)
- `User` (nom, prenom, role, section, createdAt)

## Préparation (petit setup)
Crée une classe de pratique, par exemple:
- `src/test/java/com/darquran/streams/StreamExercisesTest.java`

Fais un jeu de données fake (listes) pour:
- `List<Course>`
- `List<Enrollment>`
- `List<StudentGrade>`
- `List<LiveSession>`
- `List<User>`

## Niveau 1 - Base
1. Retourner les emails des utilisateurs actifs (si tu ajoutes un flag actif dans ton jeu de données), en minuscule, triés A-Z.
2. Filtrer les cours `PUBLIC` et récupérer seulement leurs titres.
3. Compter le nombre d'inscriptions actives (`Enrollment.active == true`).
4. Récupérer les 5 derniers users créés (`createdAt` le plus récent).
5. Vérifier s'il existe au moins une session live avec status `LIVE`.

## Niveau 2 - Intermédiaire
6. Grouper les users par `role` -> `Map<Role, List<User>>`.
7. Compter les users par section -> `Map<Section, Long>`.
8. Calculer la moyenne des notes (`StudentGrade.value`) par cours -> `Map<String, Double>` (clé: `course.title`).
9. Trouver la meilleure note globale (`max`) + l'étudiant concerné.
10. Trier les cours par nombre de leçons (desc), puis par titre (asc).
11. Extraire tous les titres de leçons depuis tous les cours (avec `flatMap`) et enlever les doublons.

## Niveau 3 - Avancé
12. Pour chaque `Course`, retourner:
- nombre total d'inscriptions
- nombre d'inscriptions actives
- taux d'activité = actives / total

13. Créer un leaderboard enseignants:
- clé: enseignant (`Teacher`)
- valeur: moyenne des notes qu'il a attribuées
- trié de la meilleure moyenne vers la moins bonne

14. Construire un dashboard live:
- sessions `SCHEDULED` de la semaine courante
- sessions `LIVE`
- sessions `ENDED` des 7 derniers jours

15. Détecter les cours "à risque":
- moyenne < 10
- OU moins de 3 inscriptions actives
Retourner une liste d'objets résultat (DTO) avec raison(s).

16. Pour chaque section (`HOMME`, `FEMME`), trouver la prochaine session live planifiée (`scheduledStartAt` minimal > now).

17. Créer un `Map<String, Set<String>>`:
- clé: nom complet de l'étudiant
- valeur: ensemble des titres de cours où il est inscrit activement

## Niveau 4 - Challenge
18. Implémenter une méthode utilitaire générique:
- `topNBy(List<T> data, Function<T, K> classifier, int n, Comparator<T> comparator)`
- Objectif: grouper puis prendre top N de chaque groupe

19. Pipeline "qualité de données" sur `User`:
- email non null et valide basique (`contains("@")`)
- nom/prénom non vides
- supprimer doublons par email
- produire un rapport: total, valides, invalides

20. Sans boucle `for`, produire un résumé global:
- nombre de cours
- nombre total de leçons
- nombre total d'inscriptions actives
- moyenne globale des notes
- nombre de sessions live actuellement en direct

## Bonus (performance)
21. Compare `stream()` vs `parallelStream()` sur une grosse liste fake (100k+ éléments) pour une agrégation CPU-bound.
22. Ajoute un test qui vérifie que l'ordre est stable après `sorted` multi-critères.
23. Remplace un bloc impératif existant dans un service par une version Stream plus lisible (sans sur-optimiser).

## Règles de validation
- Écrire chaque exercice sous forme de test unitaire (`@Test`) avec `assertThat`.
- Favoriser des méthodes pures (pas d'accès DB dans ces exercices).
- Si résultat est complexe, créer un petit record/DTO dédié.

## Mini checklist (avant de passer au suivant)
- Résultat correct
- Code lisible
- Pas de NPE facile (gérer nulls si besoin)
- Complexité raisonnable

## Option solution
Ila bghiti, n9der nوجد ليك fichier ثاني فيه solutions détaillées exercice par exercice (`EXERCICES_STREAM_SOLUTIONS.md`).
