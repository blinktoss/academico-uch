# Spike: Spring 3.2 Features Audit & Alternatives

**Status:** Analysis only (no changes)  
**Date:** 2026-06-28  
**Scope:** What Spring 3.2 features could improve Sistema_Academico

---

## Executive Summary

Your codebase uses **~15% of Spring 3.2's capabilities**. Most unused features won't apply to JSF 1.2 architecture, but **5 features could significantly improve code quality** without major refactoring:

1. **@Transactional** (replace 56 TransactionProxyFactoryBean configs)
2. **JSR-303 Validation** (replace manual isEmpty/length checks)
3. **Spring Cache Abstraction** (standardize 5 custom caching implementations)
4. **Exception Translation** (PersistenceExceptionTranslator)
5. **Annotation-based Configuration** (gradual, optional)

---

## Current Spring 3.2 Usage

```
✅ In Use (GOOD):
   - HibernateTemplate + HibernateDaoSupport (56 DAOs)
   - Spring Test Framework (51 test classes)
   - XML bean configuration (applicationContext-*.xml)
   - TransactionProxyFactoryBean (56 beans)
   - Custom ServiceFinder for JSF bean lookup
   - AuditInterceptor (custom, working well)

❌ NOT Used (MISSED OPPORTUNITIES):
   - @Autowired / @Qualifier / @Repository / @Service / @Component
   - @Transactional annotation
   - JSR-303 Validation (@Valid, @NotNull, @Pattern)
   - Spring Cache abstraction (@Cacheable, @CacheEvict)
   - Spring Scheduling (@Scheduled)
   - Spring Async (@Async, TaskExecutor)
   - AOP (@Aspect)
   - RestTemplate (HTTP client)
   - Spring Security (@Secured, @PreAuthorize)
   - @Profile (environment-specific configuration)
```

---

## Feature-by-Feature Analysis

### 1. @Transactional → Replace TransactionProxyFactoryBean

**Current Pattern (XML, 56 beans):**
```xml
<!-- In applicationContext-hibernate.xml -->
<bean id="springHibernateDao..." class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
    <property name="transactionManager" ref="transactionManager"/>
    <property name="target" ref="hibernateSpringDaoTarget..."/>
    <property name="transactionAttributes">
        <props>
            <prop key="get*">PROPAGATION_REQUIRED,readOnly</prop>
            <prop key="find*">PROPAGATION_REQUIRED,readOnly</prop>
            <prop key="store*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>
```

**Alternative (Annotations, ~10 lines per interface):**
```java
public interface HSAlumnoDAO {
    
    @Transactional(readOnly = true)
    AcAlumno seleccionarAlumno(int id);
    
    @Transactional(readOnly = true)
    List<AcAlumno> findAlumnos(...);
    
    @Transactional  // read-write default
    void insertarAlumno(AcAlumno alumno);
    
    @Transactional
    void actualizarAlumno(AcAlumno alumno);
}
```

**Impact:**
- ✅ **Pro:** Cleaner, more explicit, less XML boilerplate
- ✅ Pro: IDE support (jump to definition, refactoring)
- ✅ Pro: Easier to override per-method (current XML forces batch rules)
- ⚠️ Con: Requires enabling `<tx:annotation-driven/>` in XML
- ⚠️ Con: No JSF 1.2 incompatibility, but untested with your lifecycle

**Effort:** ~2-3 hours (DAO interface edits + XML config change)  
**Risk:** LOW (transactional semantics unchanged, just different mechanism)  
**Recommendation:** ✅ **GOOD CANDIDATE** — doable as separate task after Spring 4 upgrade

---

### 2. JSR-303 Validation → Replace isEmpty/length Checks

**Current Pattern (manual, scattered):**
```java
// Throughout codebase: manual validation
if (codigo.trim().length() != 0) {
    c.add(Restrictions.eq(...));
}
if (paterno.trim().length() != 0) {
    c.add(Restrictions.like(...));
}
if (!listRegular.isEmpty()) {
    // process
}
```

**Alternative (Declarative, centralized):**
```java
public class AcAlumno {
    @NotNull(message = "DNI is required")
    @Pattern(regexp = "^[0-9]{8}$", message = "DNI must be 8 digits")
    private String docDni;
    
    @NotBlank(message = "Student name is required")
    private String aluNombre;
    
    @Min(0)
    @Max(20)
    private Integer aluActivo;
}
```

Then in JSF managed bean:
```java
public class MbAlumno {
    private Validator validator;  // Spring-injected
    
    public void guardarAlumno() {
        Set<ConstraintViolation<AcAlumno>> violations = validator.validate(alumno);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<?> v : violations) {
                // Display error in UI
                mensaje = v.getMessage();
            }
            return;
        }
        // Save...
    }
}
```

**Impact:**
- ✅ **Pro:** Single source of truth (validation rules in entity)
- ✅ Pro: Reusable across UI/REST/batch contexts
- ✅ Pro: Cleaner code (no scattered if-checks)
- ⚠️ Con: JSF 1.2 integration requires custom work (JSF 2.0 has native support)
- ⚠️ Con: Requires adding `javax.validation:validation-api` dependency

**Effort:** ~4-5 hours (entity annotations + JSF integration)  
**Risk:** MEDIUM (JSF 1.2 doesn't auto-integrate; requires custom validator bean)  
**Recommendation:** ⚠️ **POSSIBLE, but wait** — easier post-Spring 4 or post-JSF 2.0 migration

---

### 3. Spring Cache Abstraction → Standardize Caching

**Current Pattern (5 custom implementations, all different):**

a) **Servlet-based caching (ActivityImageServlet):**
```java
private static final Map<Integer, byte[]> cache = new ConcurrentHashMap<>();

public void doGet(HttpServletRequest req, HttpServletResponse res) {
    int id = Integer.parseInt(req.getParameter("id"));
    byte[] imageBytes = cache.get(id);
    if (imageBytes == null) {
        imageBytes = loadFromDatabase(id);
        cache.put(id, imageBytes);  // Manual cache management
    }
    res.getOutputStream().write(imageBytes);
}
```

b) **Custom servlet cache (StudentPhotoServlet):**
```java
// Similar pattern, slightly different implementation
```

c) **ReportType caching:**
```java
// Custom enum-based caching
```

**Alternative (Spring Abstraction, unified):**
```java
@Component
public class ImageService {
    @Autowired
    private ImageRepository imageRepo;
    
    @Cacheable(value = "images", key = "#id")  // Cached by ID
    public byte[] loadTeacherImage(int id) {
        return imageRepo.getImageBytes(id);
    }
    
    @CacheEvict(value = "images", key = "#id")  // Invalidate on update
    public void updateImage(int id, byte[] bytes) {
        imageRepo.saveImage(id, bytes);
    }
}
```

Then in servlet:
```java
public void doGet(HttpServletRequest req, HttpServletResponse res) {
    int id = Integer.parseInt(req.getParameter("id"));
    byte[] imageBytes = imageService.loadTeacherImage(id);  // Spring handles caching
    res.getOutputStream().write(imageBytes);
}
```

**Impact:**
- ✅ **Pro:** Unified caching strategy (ConcurrentHashMap today, Redis/Memcached tomorrow)
- ✅ Pro: Cleaner servlet code (no manual cache management)
- ✅ Pro: Easy to add cache invalidation rules
- ⚠️ Con: Requires Spring configuration (simple, but one-time setup)
- ⚠️ Con: 5 different implementations need refactoring to single pattern

**Effort:** ~3-4 hours (refactor 5 servlets + add Spring cache config)  
**Risk:** LOW (caching logic is straightforward)  
**Recommendation:** ✅ **GOOD CANDIDATE** — good ROI for code quality + flexibility

---

### 4. Spring Exception Translation (PersistenceExceptionTranslator)

**Current Pattern (catch and log):**
```java
public void insertarAlumno(AcAlumno alumno) {
    try {
        this.getHibernateTemplate().save(alumno);
    } catch (Exception e) {
        logger.error("Error inserting student", e);
        throw new RuntimeException(e);  // Wraps Hibernate exception
    }
}
```

**Alternative (Spring handles translation):**
```java
@Repository
@Component
public class HSAlumnoDAOImpl extends HibernateDaoSupport {
    // No try-catch needed — Spring translates HibernateException → DataAccessException
    public void insertarAlumno(AcAlumno alumno) {
        this.getHibernateTemplate().save(alumno);  // Exception auto-translated
    }
}
```

Then in service/JSF layer:
```java
try {
    docenteDAO.insertarAlumno(alumno);
} catch (DataAccessException e) {
    // Spring-translated exception — consistent across DAOs
    if (e.getRootCause() instanceof ConstraintViolationException) {
        // Handle duplicate key, etc.
    }
}
```

**Impact:**
- ✅ **Pro:** Consistent exception hierarchy across all DAOs
- ✅ Pro: No need for try-catch in DAO layer
- ⚠️ Con: Doesn't change current behavior (exception still thrown)
- ⚠️ Con: Service/JSF layer still needs exception handling

**Effort:** ~1-2 hours (add @Repository annotations, remove try-catch blocks)  
**Risk:** LOW (Spring's translation is transparent)  
**Recommendation:** ✅ **EASY WIN** — good practice, minimal effort

---

### 5. Annotation-based DI → Replace ServiceFinder.findBean()

**Current Pattern (manual lookup, 52 locations):**
```java
public class MbAlumno {
    private HSAlumnoDAO alumnoDAO;
    
    @PostConstruct
    public void init() throws UserNotFoundException {
        // Manual lookup (ServiceFinder is custom helper)
        alumnoDAO = ServiceFinder.findBean("springHibernateDAOAlumno", HSAlumnoDAO.class);
        
        BeanUser user = ServiceFinder.getLoggedUser();
        if (user == null) throw new UserNotFoundException();
    }
}
```

**Alternative (Annotations, cleaner):**
```java
@Component
public class MbAlumno {
    @Autowired
    @Qualifier("springHibernateDAOAlumno")
    private HSAlumnoDAO alumnoDAO;  // Spring injects automatically
    
    @PostConstruct
    public void init() throws UserNotFoundException {
        BeanUser user = ServiceFinder.getLoggedUser();
        if (user == null) throw new UserNotFoundException();
    }
}
```

**Impact:**
- ✅ **Pro:** IDE support, refactoring safety, less boilerplate
- ✅ Pro: No need for custom ServiceFinder (Spring-native)
- ⚠️ Con: JSF 1.2 beans need `@Component` or `@ManagedBean` annotation
- ⚠️ Con: Requires Spring `<context:component-scan>` in XML
- ❌ **Con:** ServiceFinder.getLoggedUser() is custom; still needed for JSF session handling

**Effort:** ~5-6 hours (52 JSF managed beans + config change)  
**Risk:** MEDIUM (JSF lifecycle complexity; needs testing)  
**Recommendation:** ⚠️ **DEFER** — good long-term, but risky before Spring 4 upgrade + JSF 2.0

---

### 6. Spring Scheduling (@Scheduled)

**Current Pattern (if any):**
```
❌ NOT USED
```

**Use Case (hypothetical):**
```java
@Component
public class BatchScheduler {
    @Scheduled(cron = "0 0 * * * *")  // Daily at midnight
    public void dailyDataCleanup() {
        // Delete old test data, generate reports, etc.
    }
    
    @Scheduled(fixedRate = 3600000)  // Every 1 hour
    public void refreshCache() {
        cacheService.invalidateAll();
    }
}
```

**Impact:**
- ✅ **Pro:** Cleaner than Quartz for simple jobs
- ⚠️ Con: Single-server only (not distributed)
- ❌ **Con:** No current use case in your codebase

**Effort:** N/A (only if you need background jobs)  
**Risk:** N/A  
**Recommendation:** ❌ **SKIP** — not applicable unless new feature requires it

---

### 7. Spring Async (@Async, TaskExecutor)

**Current Pattern (if any):**
```
❌ NOT USED
```

**Use Case (hypothetical):**
```java
@Component
public class ReportService {
    @Async
    public Future<byte[]> generateLargeReport(int semestreId) {
        // Long-running task, non-blocking
        byte[] pdfBytes = createComplexReport(semestreId);
        return AsyncResult.forValue(pdfBytes);
    }
}
```

Then in JSF:
```java
public void exportGrades() {
    Future<byte[]> reportFuture = reportService.generateLargeReport(currentSemestre);
    // Don't wait; show "generating..." spinner, poll later
}
```

**Impact:**
- ⚠️ **Con:** Complex JSF integration (futures + AJAX polling)
- ❌ **Con:** No current use case

**Effort:** N/A (only if you need long-running tasks)  
**Risk:** MEDIUM (JSF lifecycle + async coordination)  
**Recommendation:** ❌ **SKIP** — not applicable; would break JSF form state

---

### 8. Spring Security (@Secured, @PreAuthorize)

**Current Pattern (custom):**
```java
// Custom: BeanUser + ServiceFinder.getLoggedUser() + manual role checks
public class MbAlumno {
    public void guardarAlumno() {
        BeanUser user = ServiceFinder.getLoggedUser();
        if (!user.hasRole("DOCENTE")) {
            mensaje = "No permission";
            return;
        }
        // Save...
    }
}
```

**Alternative (Spring Security):**
```java
@Component
public class MbAlumno {
    @PreAuthorize("hasRole('DOCENTE')")  // Declarative access control
    public void guardarAlumno() {
        // Only called if user has DOCENTE role
        // ...
    }
}
```

**Impact:**
- ✅ **Pro:** Cleaner, more secure (declarative, harder to bypass)
- ✅ Pro: Centralized authorization rules
- ❌ **Con:** Requires replacing custom BeanUser + ServiceFinder model
- ❌ **Con:** JSF 1.2 integration non-trivial
- ❌ **Con:** Would affect all 52 managed beans

**Effort:** ~8-10 hours (significant refactor)  
**Risk:** HIGH (impacts authentication/authorization globally)  
**Recommendation:** ❌ **DEFER** — good long-term goal, but too risky now

---

## Recommendation Summary

### Immediate Wins (Do These Next)

| Feature | Effort | Risk | ROI | Priority |
|---------|--------|------|-----|----------|
| **@Transactional** | 2-3h | LOW | HIGH | ⭐⭐⭐ |
| **Cache Abstraction** | 3-4h | LOW | MEDIUM | ⭐⭐⭐ |
| **Exception Translation** | 1-2h | LOW | LOW | ⭐⭐ |

### Medium-Term (Post-Spring 4 Upgrade)

| Feature | Effort | Risk | ROI | Priority |
|---------|--------|------|-----|----------|
| **JSR-303 Validation** | 4-5h | MEDIUM | MEDIUM | ⭐⭐ |
| **Annotation-based DI** | 5-6h | MEDIUM | MEDIUM | ⭐⭐ |

### Long-Term (Post-JSF 2.0 Migration)

| Feature | Effort | Risk | ROI | Priority |
|---------|--------|------|-----|----------|
| **Spring Security** | 8-10h | HIGH | HIGH | ⭐ |

### Not Applicable

| Feature | Reason |
|---------|--------|
| **Scheduling** | No current use case |
| **Async** | Breaks JSF form lifecycle |

---

## Implementation Roadmap

```
Now (v2.0):
  ✅ Spring 4.3 upgrade (planned)

Next Phase (v2.1):
  🔲 @Transactional (2-3h, replaces TransactionProxyFactoryBean)
  🔲 Cache Abstraction (3-4h, unifies 5 caching implementations)
  🔲 Exception Translation (1-2h, cleaner error handling)

Future (v3.0):
  🔲 JSR-303 Validation (4-5h, after Spring 4 + testing)
  🔲 Annotation-based DI (5-6h, after validation works)

Post-JSF 2.0 Migration:
  🔲 Spring Security (8-10h, replaces custom auth model)
```

---

## Quick Code Examples by Feature

### @Transactional Example

**Before:**
```xml
<!-- 56 of these in XML -->
<bean id="springHibernateDAOAlumno" class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
    <property name="transactionManager" ref="transactionManager"/>
    <property name="target" ref="hibernateSpringDaoTargetAlumno"/>
    <property name="transactionAttributes">
        <props>
            <prop key="get*">PROPAGATION_REQUIRED,readOnly</prop>
            <prop key="find*">PROPAGATION_REQUIRED,readOnly</prop>
            <prop key="store*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>
```

**After:**
```java
public interface HSAlumnoDAO {
    @Transactional(readOnly = true)
    AcAlumno seleccionarAlumno(int id);
    
    @Transactional
    void insertarAlumno(AcAlumno alumno);
}

// Single line in XML:
<tx:annotation-driven transaction-manager="transactionManager"/>
```

### Cache Abstraction Example

**Before:**
```java
public class StudentPhotoServlet extends HttpServlet {
    private static final Map<Integer, byte[]> cache = new ConcurrentHashMap<>();
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        int id = Integer.parseInt(req.getParameter("id"));
        byte[] bytes = cache.get(id);
        if (bytes == null) {
            bytes = loadFromDatabase(id);
            cache.put(id, bytes);
        }
        res.getOutputStream().write(bytes);
    }
}
```

**After:**
```java
@Component
public class PhotoService {
    @Cacheable(value = "studentPhotos", key = "#id")
    public byte[] getStudentPhoto(int id) {
        return loadFromDatabase(id);
    }
}

public class StudentPhotoServlet extends HttpServlet {
    @Autowired private PhotoService photoService;
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        int id = Integer.parseInt(req.getParameter("id"));
        byte[] bytes = photoService.getStudentPhoto(id);  // Spring caches
        res.getOutputStream().write(bytes);
    }
}
```

---

## Decision Point

**After you complete the Spring 4.3 upgrade:**

1. Would you like to tackle **@Transactional** migration (2-3 hours, high value)?
2. Or focus on **Cache Abstraction** (3-4 hours, consolidates custom code)?
3. Or both as a follow-up task?

---

## References

- **Spring 3.2 TX:** https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/transaction.html
- **Spring Cache:** https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/cache.html
- **JSR-303 Validation:** https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/validation.html
- **Spring Security 3.2:** https://docs.spring.io/spring-security/site/docs/3.2.x/reference/htmlsingle/

---

**Next Session:** Review this audit, decide which features to prioritize, and I can help implement them in order of ROI.
