# 🎯 Exercices Pratiques Frontend - DarQuran

## 📋 Introduction
Ces exercices sont conçus pour évaluer et améliorer les compétences frontend sur la plateforme DarQuran. Chaque exercice simule des situations réelles rencontrées dans l'application.

**Stack technique :** Angular 21, TypeScript 5.9, Socket.IO, HLS.js, Angular Signals

---

## 🚨 **Exercice 1 : Gestion d'Incident LiveStream**

### 📝 **Contexte**
Un professeur donne un cours de Tajwid en direct via le dashboard professeur. Pendant le cours, plusieurs étudiants signalent que :
- Le stream s'interrompt toutes les 2-3 minutes
- Les messages du chat arrivent en retard ou ne s'affichent pas
- Après reconnexion, le stream reprend mais pas au bon moment
- L'indicateur de connexion ne reflète pas l'état réel

### 🎯 **Objectifs**
Diagnostiquer et résoudre les problèmes de stabilité du LiveStream.

### 📋 **Tâches à réaliser**

#### **1.1 Diagnostic des Connexions WebSocket**
```typescript
// À compléter dans le service streaming
export class StreamingService {
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  // TODO: Implémenter la logique de reconnexion automatique
  handleSocketDisconnection() {
    // Votre code ici
  }

  // TODO: Gérer les timeouts et heartbeat
  setupHeartbeat() {
    // Votre code ici
  }
}
```

#### **1.2 Gestion des Erreurs HLS**
```typescript
// À compléter dans le composant de lecture vidéo
export class VideoPlayerComponent {
  private hls?: Hls;

  // TODO: Implémenter la récupération automatique d'erreurs HLS
  private handleHLSError(event: string, data: ErrorData) {
    // Types d'erreurs à gérer :
    // - NETWORK_ERROR
    // - MEDIA_ERROR
    // - FATAL
  }

  // TODO: Implémenter le fallback vers un autre serveur
  private switchToBackupStream() {
    // Votre code ici
  }
}
```

#### **1.3 Indicateur Visual de Statut**
```html
<!-- À compléter dans le template -->
<div class="connection-status">
  @if (connectionStatus() === 'connected') {
    <span class="status-indicator connected">🟢 Connecté</span>
  } @else if (connectionStatus() === 'reconnecting') {
    <!-- TODO: Ajouter l'indicateur de reconnexion -->
  } @else {
    <!-- TODO: Ajouter l'indicateur de déconnexion -->
  }
</div>
```

### ✅ **Critères d'Évaluation**
- [ ] **Robustesse :** Le stream se reconnecte automatiquement sans intervention
- [ ] **UX :** L'utilisateur est informé en temps réel du statut de connexion
- [ ] **Performance :** La reconnexion prend moins de 5 secondes
- [ ] **Synchronisation :** Le chat et le stream restent synchronisés
- [ ] **Fallback :** Un mécanisme de secours est en place

---

## ⚡ **Exercice 2 : Optimisation Performance Dashboard Étudiant**

### 📝 **Contexte**
Le dashboard étudiant de Ahmed contient :
- 150+ cours sur 6 mois
- 300+ notes réparties sur différentes matières
- 50+ absences enregistrées
- Calendrier hebdomadaire avec 15 créneaux par semaine

**Problème :** La page met 8 secondes à charger et lag pendant le scroll.

### 🎯 **Objectifs**
Optimiser le dashboard pour qu'il se charge en moins de 2 secondes et scroll fluidement.

### 📋 **Tâches à réaliser**

#### **2.1 Implémentation du Lazy Loading**
```typescript
// À compléter dans le service de données étudiant
export class StudentDataService {
  private cache = new Map();

  // TODO: Implémenter la pagination des cours
  getCoursesPaginated(page: number = 1, limit: number = 20) {
    // Votre code ici - utiliser Angular Signals
  }

  // TODO: Implémenter le cache intelligent
  private cacheData(key: string, data: any, ttl: number = 300000) {
    // Votre code ici
  }
}
```

#### **2.2 Virtualisation de Liste**
```typescript
// À créer : composant de virtualisation personnalisé
@Component({
  selector: 'app-virtual-scroll',
  standalone: true,
  // TODO: Implémenter la virtualisation pour les grandes listes
})
export class VirtualScrollComponent {
  @Input() items = signal<any[]>([]);
  @Input() itemHeight = 60;

  visibleItems = computed(() => {
    // TODO: Calculer les items visible dans le viewport
  });

  // TODO: Gérer le scroll et le rendu optimisé
}
```

#### **2.3 Optimisation du Calendrier**
```typescript
// À optimiser dans le composant calendrier
@Component({
  selector: 'app-weekly-calendar',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WeeklyCalendarComponent {

  // TODO: Remplacer les observables par des signals
  currentWeek = signal<Date>(new Date());
  courses = signal<Course[]>([]);

  // TODO: Implémenter un computed optimisé
  weekCourses = computed(() => {
    // Filtrer seulement les cours de la semaine actuelle
  });

  // TODO: Éviter les recreations d'objets
  trackByFn = (index: number, item: Course) => item.id;
}
```

#### **2.4 Métriques de Performance**
```typescript
// À ajouter : service de monitoring des performances
export class PerformanceMonitorService {

  // TODO: Mesurer le temps de chargement initial
  measureInitialLoad() {
    // Utiliser Performance API
  }

  // TODO: Monitorer les interactions utilisateur
  monitorScrollPerformance() {
    // Mesurer le FPS pendant le scroll
  }
}
```

### ✅ **Critères d'Évaluation**
- [ ] **Temps de chargement :** < 2 secondes pour le chargement initial
- [ ] **Fluidité :** 60 FPS constant pendant le scroll
- [ ] **Mémoire :** Pas d'augmentation excessive de l'utilisation RAM
- [ ] **UX :** Skeleton/loading states pendant les chargements
- [ ] **Cache :** Données frequently utilisées mises en cache

---

## 🔐 **Exercice 3 : Sécurisation des Permissions Multi-Rôles**

### 📝 **Contexte**
L'administratrice Fatima gère la section "Femmes" de l'école. Elle doit pouvoir :
- Voir SEULEMENT les étudiantes et professeurs femmes
- Créer des cours pour la section féminine
- Accéder aux statistiques de sa section uniquement

**Problème détecté :** Fatima voit parfois des données de la section "Hommes" dans certains écrans.

### 🎯 **Objectifs**
Implémenter un système de permissions étanche par section.

### 📋 **Tâches à réaliser**

#### **3.1 Renforcement des Guards**
```typescript
// À compléter dans auth.guard.ts
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // TODO: Vérifier l'authentification ET les permissions de section
  const userPermissions = authService.getUserPermissions();

  // TODO: Valider l'accès en fonction de la route et la section
  if (route.url.includes('/admin/section/')) {
    // Votre logique ici
  }

  return true; // À adapter
};
```

#### **3.2 Intercepteur de Sécurité**
```typescript
// À compléter dans security.interceptor.ts
export const securityInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // TODO: Ajouter automatiquement les filtres de section aux requêtes API
  const userSection = authService.getCurrentUserSection();

  if (req.url.includes('/api/students') || req.url.includes('/api/teachers')) {
    // TODO: Modifier la requête pour inclure le filtre de section
    const modifiedReq = req.clone({
      // Votre code ici
    });

    return next(modifiedReq);
  }

  return next(req);
};
```

#### **3.3 Composant Conditionnel par Rôle**
```typescript
// À créer : directive de permissions
@Directive({
  selector: '[appHasPermission]',
  standalone: true
})
export class HasPermissionDirective {
  private templateRef = inject(TemplateRef);
  private viewContainer = inject(ViewContainerRef);
  private authService = inject(AuthService);

  @Input() set appHasPermission(permissions: string[]) {
    // TODO: Implémenter la logique d'affichage conditionnel
    this.checkPermissions(permissions);
  }

  private checkPermissions(requiredPermissions: string[]) {
    // Votre code ici
  }
}
```

#### **3.4 Service de Gestion des Permissions**
```typescript
// À compléter dans permission.service.ts
export class PermissionService {
  private userPermissions = signal<UserPermission[]>([]);

  // TODO: Implémenter la vérification granulaire des permissions
  hasPermission(resource: string, action: 'read' | 'write' | 'delete', section?: string): boolean {
    // Votre code ici
  }

  // TODO: Filtrer automatiquement les données par section
  filterDataByUserSection<T>(data: T[], sectionField: keyof T): T[] {
    // Votre code ici
  }

  // TODO: Valider les actions utilisateur en temps réel
  validateAction(action: UserAction): ValidationResult {
    // Votre code ici
  }
}
```

#### **3.5 Tests de Sécurité**
```typescript
// À créer : tests de sécurité automatisés
describe('Security Tests', () => {

  // TODO: Tester l'isolation des données par section
  it('should prevent cross-section data access', () => {
    // Test que Fatima ne peut pas voir les données hommes
  });

  // TODO: Tester la persistance des permissions après rafraîchissement
  it('should maintain permissions after page refresh', () => {
    // Votre test ici
  });

  // TODO: Tester les cas limites de permissions
  it('should handle edge cases in permission validation', () => {
    // Cas : utilisateur avec permissions multiples sections
  });
});
```

### ✅ **Critères d'Évaluation**
- [ ] **Étanchéité :** Aucune fuite de données entre sections
- [ ] **Performance :** Vérifications de permissions n'impactent pas les performances
- [ ] **UX :** Interface s'adapte automatiquement aux permissions
- [ ] **Auditabilité :** Logs de toutes les tentatives d'accès
- [ ] **Tests :** Couverture de test > 90% sur les composants sécurisés

---

## 🏆 **Exercice Bonus : Intégration Complète**

### 📝 **Scenario Final**
Créer un composant qui combine les 3 exercices précédents :
- Un dashboard professeur qui stream en direct (**Exercice 1**)
- Avec une liste optimisée des étudiants connectés (**Exercice 2**)
- Filtré par section selon les permissions (**Exercice 3**)

### 📋 **Livrables attendus**
1. **Code complet** des composants/services modifiés
2. **Tests unitaires** pour chaque fonctionnalité
3. **Documentation** des décisions techniques
4. **Métriques** de performance avant/après
5. **Rapport de sécurité** validant l'étanchéité des permissions

---

## 📚 **Ressources**

### **Documentation Angular**
- [Angular Signals Guide](https://angular.dev/guide/signals)
- [Change Detection Strategy](https://angular.dev/api/core/ChangeDetectionStrategy)
- [Guards and Interceptors](https://angular.dev/guide/routing/common-router-tasks)

### **Stack Technique**
- **Socket.IO Client :** [Documentation officielle](https://socket.io/docs/v4/client-api/)
- **HLS.js :** [API Reference](https://github.com/video-dev/hls.js/blob/master/docs/API.md)

### **Performance**
- [Web Vitals](https://web.dev/vitals/)
- [Angular Performance Checklist](https://angular-checklist.io/)

---

> **Note :** Ces exercices sont progressifs et peuvent être réalisés individuellement ou en équipe. L'accent est mis sur les bonnes pratiques Angular 21 et la robustesse du code produit.