# Diagrammes UML - DarQuran

Ce dossier contient les diagrammes UML du projet au format **PlantUML**.

## Fichiers

| Fichier | Description |
|---------|-------------|
| `diagramme-classes.puml` | Diagramme de classes du domaine (entités, attributs, méthodes, relations) |
| `diagramme-cas-utilisation.puml` | Diagramme de cas d'utilisation (acteurs, cas d'usage, include/extend) |

## Génération des images

### Option 1 : PlantUML en ligne
- Copier le contenu du fichier `.puml`
- Coller sur [plantuml.com/plantuml](https://www.plantuml.com/plantuml/uml) et générer l’image

### Option 2 : Ligne de commande (Java + PlantUML)
```bash
# Télécharger plantuml.jar depuis https://plantuml.com/download
java -jar plantuml.jar docs/diagramme-classes.puml docs/diagramme-cas-utilisation.puml
```
Les PNG seront créés à côté de chaque fichier (ex. `diagramme-classes.png`).

### Option 3 : VS Code / Cursor
- Installer l’extension **PlantUML**
- Ouvrir un fichier `.puml` et utiliser « Preview Current Diagram » (Alt+D)

## Conventions respectées

- **Diagramme de classes** : noms en français, attributs privés (-), méthodes publiques (+), héritage (triangle), associations avec multiplicités, dépendances vers les énumérations.
- **Cas d’utilisation** : acteurs à gauche, système au centre, relations `<<include>>` et `<<extend>>` explicites, noms de cas d’usage en français.
