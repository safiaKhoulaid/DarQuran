# Liste de Questions Techniques - Backend DarQuran

Had liste t9der tsta3mlha l préparation interview, révision équipe, ou code review technique.

## 1) Java Core
1. Chno الفرق بين `List`, `Set`, w `Map` ? f backend dyalna fin tsta3ml kol wa7ed ?
2. `equals()` vs `hashCode()` : 3lach khasshom يكونو متوافقين ?
3. Chno الفرق بين `checked` w `unchecked exceptions` ?
4. `Optional` : imta nsta3mlouh w imta la ?
5. Chno الفرق بين programmation impérative w fonctionnelle b Java Streams ?
6. `Stream.map()` vs `flatMap()` b exemple mn `Course -> Lessons`.
7. Chno المخاطر ديال `parallelStream()` f appli web ?
8. `record` f Java: fin y9der ykoun mzyan (DTO), w fin la ?
9. Chno الفرق بين `StringBuilder` w `StringBuffer` ?
10. `Comparator` multi-critères: kifach nرتب b `thenComparing` ?

## 2) Spring Boot
1. Chno الفرق بين `@Component`, `@Service`, `@Repository`, `@Controller` ?
2. Spring IOC/DI: kifach container kay injecti les dépendances ?
3. `@Configuration` + `@Bean`: imta nحتاجها بدل stereotype annotations ?
4. `application.yml` profiles (`dev`, `prod`): kifach tkhddem mzyan ?
5. `@Transactional`: chno scope dyal transaction, w chno impact dyal propagation ?
6. Chno الفرق بين `@RestController` w `@Controller` ?
7. Kifach tdir global error handling b `@ControllerAdvice` ?
8. `Bean lifecycle`: chno kayوقع f init/destroy ?
9. Kifach tكتب endpoint paginé (`Pageable`) ?
10. Chno أحسن طريقة باش تدير validation dyal input (`@Valid`, constraints) ?

## 3) Spring Security + JWT
1. Flow dyal JWT f projet: login -> token -> filter -> authorization.
2. Chno الفرق بين `Authentication` w `Authorization` ?
3. 3lach password khasseha تتخزن hashed (`BCrypt`) ?
4. Kifach nتعامل m3a token expiration w refresh token ?
5. Chno role dyal `OncePerRequestFilter` f JWT auth ?
6. Kifach tفرض access b role (`@PreAuthorize`) ?
7. Chno هي أخطاء أمنية شائعة f APIs (IDOR, weak CORS, no rate-limit) ?
8. Kifach تدير logout f JWT-based stateless system ?
9. CSRF: imta ykoun مهم, imta ykoun أقل أهمية f REST stateless ?
10. Kifach تحمي endpoints publics vs privés f config security ?

## 4) JPA / Hibernate
1. Chno الفرق بين `FetchType.LAZY` w `EAGER` ?
2. N+1 problem: kifach tكتشفو w tصلحو (`join fetch`, entity graph) ?
3. `@OneToMany` / `@ManyToOne`: chno أحسن طرف يملك relation ?
4. 3lach `CascadeType.ALL` momkin ykoun خطير ila tsta3mal bghalat ?
5. `orphanRemoval = true`: chno katdir bddabt ?
6. Chno الفرق بين `save()` w `saveAndFlush()` ?
7. `@Transactional(readOnly = true)` chno الفايدة منها ?
8. Kifach tكتب query optimisée b JPQL ou native SQL ?
9. `UUID` as id: avantages w inconvénients ?
10. Kifach تدير pagination w sorting f repositories ?

## 5) API Design
1. Chno conventions dyal routes REST mzyanin (`/courses`, `/courses/{id}/lessons`)?
2. Imta nرجعو `200`, `201`, `204`, `400`, `401`, `403`, `404`, `409` ?
3. Chno الفرق بين `PUT` w `PATCH` ?
4. Chno structure موحدة dyal error response (code, message, details, timestamp) ?
5. Kifach تضمن backward compatibility ila بدلت API ?
6. Versioning API: path vs header, achno أنسب ؟
7. Kifach tمنع over-fetching / under-fetching ف REST ?
8. Kifach تتعامل m3a idempotency f operations حساسة ?
9. Validation business rules fin khassha tkoun: controller/service/domain ?
10. Chno logging minimum li khas API endpoint يدير ؟

## 6) Testing
1. Chno الفرق بين unit test, integration test, end-to-end test ?
2. `@SpringBootTest` vs `@WebMvcTest` vs pure JUnit + Mockito.
3. Kifach tmock repository/service mzyan bla ma tdir tests fragiles ?
4. Chno test cases essentiels l endpoint login ?
5. Kifach تختبر `@ControllerAdvice` و error mapping ?
6. Chno معنى coverage mzyan b JaCoCo, w 3lach 100% maشي دائمًا هدف صحيح ?
7. Kifach tكتب tests ل business rules f `Enrollment` ?
8. Kifach tضمن tests deterministic (ma kayعتمدوch 3la الوقت الحقيقي) ?
9. Kifach tكتب test dyal pagination/sorting ؟
10. Chno المعايير ديال test قابل للصيانة ؟

## 7) Performance & Scalability
1. Kifach tقيّس performance dyal endpoint (latency, throughput) ?
2. Caching (`@Cacheable`) : imta ykoun مفيد f project bhal DarQuran ?
3. Kifach tتفادى queries كثيرة بلا فايدة ف dashboard endpoints ?
4. Batch operations: imta nحتاجهم ?
5. Chno impact dyal large DTOs 3la network w frontend ?
6. Kifach tقدر تحسن endpoints ديال live sessions comments ?
7. Connection pool: chno إعدادات مهمة ؟
8. Kifach tتعامل m3a slow external services (retry, timeout, circuit breaker) ?
9. Chno monitoring minimum f prod (CPU, heap, p95 latency, DB time) ?
10. Kifach tكتشف memory leaks ف Java app ?

## 8) DevOps & Production
1. Chno الفرق بين dev/prod config f Dockerized Spring app ?
2. Kifach tكتب Dockerfile mزيان (layers, image size, security) ?
3. Environment variables: fin nخزن secrets ؟
4. Health checks (`/actuator/health`) kifach nستخدمهم f deployment ?
5. Logging strategy f prod: levels, trace ID, structure JSON ?
6. Migration DB (Flyway/Liquibase): 3lach مهمين ؟
7. Rollback strategy ila deployment فشل ؟
8. CI/CD: chno checks minimum قبل merge (build, tests, coverage, lint) ?
9. Kifach tأمن access l database w storage (principle of least privilege) ?
10. Incident response: chno أول 5 خطوات ملي endpoint كيطيح ؟

## 9) Questions Spécifiques DarQuran
1. Kifach tضمن section-based access (`HOMME`/`FEMME`) f live sessions ?
2. Chno business rules dyal `Enrollment` (active/inactive, duplicate) ?
3. Kifach nتحسب student dashboard metrics بلا N+1 ?
4. Chno strategy باش tحافظ 3la ordre dyal `Lesson.orderIndex` ؟
5. Kifach tدير moderation بسيطة l live comments ?
6. Chno validation rules l `StudentGrade.value` ?
7. Kifach tضمن consistency بين `Course.status` w availability ?
8. Chno audit trail minimum l actions الحساسة (grades, absences, users) ?
9. Kifach tدير soft delete ila احتجتيه مستقبلا ؟
10. Kifach tقسم service layer باش تبقى maintainable m3a growth dyal projet ?

## 10) Mini Cas Pratiques (à résoudre)
1. Donne code Stream kayرجع top 3 courses b active enrollments.
2. Écris endpoint pagination pour lister live sessions triées par date.
3. Propose refactor d'une méthode service طويلة بزاف.
4. Détecte bug sécurité محتمل f flow JWT.
5. Propose stratégie tests l feature "create live session".

## Utilisation conseillée
- Tختار 10 questions kol nhar.
- Jawaab b voix haute b style simple + exemples mn code dyalk.
- Kol question li ma 3رفتihach, dir لها note w rje3 l docs/code.

Ila bghiti, nقدر نوجد لك version "QCM + réponses" أو version "entretien technique simulé". 
