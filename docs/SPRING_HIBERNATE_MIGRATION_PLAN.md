# Spring 3.2 → 4.1 + Hibernate 3.6 → 4.1 Migration Plan

**Date:** 2026-06-28  
**Branch:** `claude/upgrade-spring-hibernate-4x` (will be reset + repurposed)  
**Scope:** Path B - Both Spring and Hibernate, modern but manageable  
**Critical Bottleneck:** Hibernate query generics (~192 calls across 56 DAO impl files)

---

## Executive Summary

**Good News:**
- ✅ @Transactional already implemented in services (EnrollmentService, GradeRecordService)
- ✅ Only 56 TransactionProxyFactoryBean beans remain in XML (DAO layer)
- ✅ Hibernate 4.1 is less disruptive than 4.3 (fewer compile errors ~120 vs 239)

**Challenge:**
- 192 query calls need generics handling (findByCriteria, createSQLQuery, createQuery)
- Distributed across 56 DAO impl files

**Strategy:**
- **Automated + mechanical approach** to handle the 192 queries efficiently
- **Batch fixes** using sed/grep scripts where possible
- **Manual verification** only where automated tools miss edge cases

---

## Phase Breakdown

### Phase 1: Preparation (30 minutes)

#### 1a. Document current state
```bash
# Baseline: count files + query patterns that need fixing
find src/main/java/net/uch/*/dao/impl -name "*.java" | wc -l
# Expected: ~56 files

grep -rn "\.findByCriteria\|\.createSQLQuery\|\.createQuery" src/main/java --include="*.java" \
  | wc -l
# Expected: ~192

# Find files with @SuppressWarnings("unchecked") already
grep -l "@SuppressWarnings.*unchecked" src/main/java/net/uch/*/dao/impl/*.java | wc -l
```

#### 1b. Reset branch to clean state
```bash
git checkout claude/upgrade-spring-hibernate-4x
git reset --hard HEAD~10  # Go back to before version bump
# Or create new branch:
git checkout -b upgrade/spring4-hibernate4-v2
```

---

### Phase 2: Version Bump (15 minutes)

#### 2a. Update pom.xml
```xml
<properties>
    <spring-framework.version>4.1.9.RELEASE</spring-framework.version>
    <hibernate.version>4.1.12.Final</hibernate.version>
    <jasypt-hibernate.version>jasypt-hibernate4</jasypt-hibernate.version>
</properties>

<!-- Also update jasypt dependency -->
<dependency>
    <groupId>org.jasypt</groupId>
    <artifactId>jasypt-hibernate4</artifactId>
    <version>1.9.3</version>
</dependency>
```

#### 2b. Update XML config
```xml
<!-- src/main/webapp/WEB-INF/applicationContext-hibernate.xml -->
<bean id="sessionFactory" class="org.springframework.orm.hibernate4.LocalSessionFactoryBean">
    <!-- Was: org.springframework.orm.hibernate3.LocalSessionFactoryBean -->
    <property name="hibernateProperties">
        <!-- rest unchanged -->
    </property>
</bean>

<!-- Add annotation-driven transactions (replaces some TX proxies later) -->
<tx:annotation-driven transaction-manager="transactionManager"/>
```

---

### Phase 3: Import Fixes (30 minutes - Automated)

#### 3a. Session import
```bash
# Replace org.hibernate.classic.Session with org.hibernate.Session
find src/main/java -name "*.java" -exec sed -i \
  's/import org\.hibernate\.classic\.Session;/import org.hibernate.Session;/g' {} \;
```

#### 3b. Jasypt import
```bash
# Replace jasypt-hibernate3 with jasypt-hibernate4
find src/main/java -name "*.java" -exec sed -i \
  's/import org\.jasypt\.hibernate3\./import org.jasypt.hibernate4./g' {} \;
```

**Verification:**
```bash
grep -r "org.hibernate.classic" src/main/java --include="*.java"  # Should return nothing
grep -r "org.jasypt.hibernate3" src/main/java --include="*.java"  # Should return nothing
```

---

### Phase 4: Criteria Generics Handling (~80-120 errors) (4-6 hours)

**This is the big one.** Strategy: **Combine automated detection + targeted fixes**

#### 4a. Find methods that already have @SuppressWarnings
```bash
# These are already handled — leave them alone
grep -rn "@SuppressWarnings.*unchecked" src/main/java/net/uch/*/dao/impl/*.java \
  | grep -E "findByCriteria|createSQLQuery|createQuery" > /tmp/suppressed.txt

wc -l /tmp/suppressed.txt
# Expected: ~40-50 methods already suppressed
```

#### 4b. Find methods needing explicit casts
```bash
# These need a cast added
grep -rn "\.findByCriteria\|\.createSQLQuery\|\.createQuery" \
  src/main/java/net/uch/*/dao/impl/*.java \
  | grep -v "((List\|@SuppressWarnings" > /tmp/needs_cast.txt

wc -l /tmp/needs_cast.txt
# Expected: ~60-80 methods needing cast
```

#### 4c. Semi-automated fix strategy

**For methods with @SuppressWarnings:**  
No change needed. The annotation already suppresses the warning.

```java
@SuppressWarnings("unchecked")
@Override
public List<AcAlumno> findAlumnos(...) {
    DetachedCriteria c = DetachedCriteria.forClass(AcAlumno.class);
    return this.getHibernateTemplate().findByCriteria(c);  // ✅ No warning
}
```

**For methods WITHOUT @SuppressWarnings:**  
Add explicit cast. Use this pattern:

```java
@Override
public List<AcAlumno> findAlumnos(...) {
    DetachedCriteria c = DetachedCriteria.forClass(AcAlumno.class);
    return (List<AcAlumno>) this.getHibernateTemplate().findByCriteria(c);  // ✅ Cast added
}
```

#### 4d. Targeted file-by-file approach

Process files in order of query count (files with most queries first = highest ROI):

```bash
# Find files with most queries
for file in src/main/java/net/uch/*/dao/impl/HS*DAOImpl.java; do
  count=$(grep -c "\.findByCriteria\|\.createSQLQuery\|\.createQuery" "$file" 2>/dev/null || echo 0)
  echo "$count $file"
done | sort -rn | head -10
```

**Top offenders (hypothetical):**
1. HSActaDAOImpl.java (~12 queries)
2. HSAlumnoDAOImpl.java (~10 queries)
3. HSAperturaCursosDAOImpl.java (~9 queries)
... (56 files total)

**Fix process per file:**
1. Open file
2. Search for `findByCriteria`, `createSQLQuery`, `createQuery`
3. For each match:
   - Check if method already has `@SuppressWarnings("unchecked")`
   - If yes: ✅ leave as-is
   - If no: Add explicit cast `(List<EntityType>)`
4. Verify method signature to infer return type

#### 4e. Script to assist (semi-automated)

Create a helper script (Python or Bash) to identify methods needing casts:

```bash
#!/bin/bash
# find-needs-cast.sh

for file in src/main/java/net/uch/*/dao/impl/HS*DAOImpl.java; do
  echo "=== $file ==="
  
  # Find methods with queries but no @SuppressWarnings
  awk '
    /public.*List<.*>.*\(/ { method=$0; getline; }
    /@SuppressWarnings/ { suppressed=1; next; }
    /\.findByCriteria|\.createSQLQuery|\.createQuery/ && !suppressed {
      print "Line " NR ": " $0 " (needs cast)"
    }
    /^\s*}/ { suppressed=0; }
  ' "$file"
done
```

**Effort estimation:**
- Manual inspection of 56 files: ~2-3 hours
- Adding casts where needed: ~1-2 hours
- Testing compilation: ~30 minutes

---

### Phase 5: saveOrUpdateAll() Replacement (30 minutes)

Only 2-4 methods use this (removed in H4.1+).

```bash
grep -rn "saveOrUpdateAll" src/main/java --include="*.java"
```

**Expected locations:**
- HSHorarioDAOImpl.java
- Possibly 2-3 other DAO impl files

**Fix pattern:**
```java
// BEFORE (H3.6)
@Override
public void insertarHorarios(List<AcHorario> horarios) {
    this.getHibernateTemplate().saveOrUpdateAll(horarios);
}

// AFTER (H4.1)
@Override
public void insertarHorarios(List<AcHorario> horarios) {
    for (AcHorario horario : horarios) {
        this.getHibernateTemplate().save(horario);
    }
}
```

---

### Phase 6: Compilation Verification (30 minutes)

```bash
# Clean compile without tests
mvn clean compile -DskipTests -q

# Capture error count
mvn clean compile -DskipTests -q 2>&1 | tee /tmp/compile.log
ERROR_COUNT=$(grep -c "\[ERROR\]" /tmp/compile.log)
echo "Remaining errors: $ERROR_COUNT"

# Should be 0
```

**If errors remain:**
1. Group by type: `grep "\[ERROR\]" /tmp/compile.log | sort | uniq -c | sort -rn`
2. Pick the top 3-4 patterns
3. Fix systematically (likely edge cases)

---

### Phase 7: Testing (2-3 hours)

#### 7a. Unit + Integration Tests
```bash
mvn test -X 2>&1 | tee /tmp/test.log

# Watch for:
# - DAO query result casting issues
# - Hibernate session lifecycle issues
# - Transaction boundary issues (audit fields, etc.)

# Extract test summary
grep -E "BUILD|FAILURE|ERROR" /tmp/test.log | tail -20
```

#### 7b. Manual JSF Testing (1-2 hours, if all tests pass)

Start the dev server:
```bash
mvn tomcat7:run
# http://localhost:8084/Sistema_Academico
```

**Test paths:**
1. **Alumnos.jsp** - List, create, edit (tests managed bean + tree nav + forms)
2. **Notas.jsp** - Grade entry (tests complex form state + rich:calendar)
3. **Horario.jsp** - Schedule management (tests complex queries)
4. **Docentes.jsp** - Teacher management (tests DAO CRUD)
5. **Login/Logout** - Session handling (critical with Spring 4.1)

**Watch for:**
- Form rendering (RichFaces components)
- AJAX interactions (a4j:support, rich:ajaxValidator)
- Tree navigation (rich:tree, UITree)
- Modal dialogs opening/closing
- Session timeout handling

---

## Risk Mitigation Strategies

### Risk 1: JSF 1.2 Managed Bean Lifecycle Issues with Spring 4.1

**Symptom:** Forms don't render, managed beans not initialized, JSF request/session scope broken

**Mitigation:**
1. Run tests first (catches most lifecycle issues)
2. Manual JSF testing of all forms (can't simulate RichFaces tree + AJAX in unit tests)
3. If issues arise: Easy to revert (only Spring 3.2 changed; Hibernate H4.1 is independent)
4. Create a JSF 1.2 + Spring 4.1 integration test (if not exists)

### Risk 2: Query Result Type Incompatibility

**Symptom:** Runtime cast exceptions in DAOs, null pointer issues

**Mitigation:**
1. All queries already typed in current code (e.g., `return (List<AcAlumno>)`)
2. Compiler enforces correct casts at compile time
3. Tests verify runtime behavior

### Risk 3: MySQL 5.6 + Hibernate 4.1 SQL Generation

**Symptom:** Query timeouts, locking issues, unexpected results

**Mitigation:**
1. Use same MySQL dialect: `org.hibernate.dialect.MySQL5Dialect`
2. Run integration tests against live MySQL (tests already do this)
3. No SQL generation changes expected (Hibernate 4.1 is conservative)

### Risk 4: Audit Interceptor Compatibility

**Symptom:** Audit fields (created_at, updated_at) not populated

**Mitigation:**
1. AuditInterceptor uses Hibernate Type API (should work in 4.1)
2. Run tests that create/update entities
3. Check for `IllegalAccessException` or `ClassCastException` in Interceptor

---

## Work Breakdown Summary

| Phase | Task | Effort | Complexity |
|-------|------|--------|------------|
| 1 | Preparation + baseline | 30m | TRIVIAL |
| 2 | Version bump (pom.xml + XML) | 15m | TRIVIAL |
| 3 | Import fixes (automated sed) | 30m | TRIVIAL |
| 4 | Criteria generics (~192 queries) | 4-6h | MEDIUM (mechanical) |
| 5 | saveOrUpdateAll() replacement | 30m | TRIVIAL |
| 6 | Compile verification | 30m | MEDIUM (debugging if needed) |
| 7 | Testing (unit + manual JSF) | 2-3h | MEDIUM (manual JSF testing) |
| **TOTAL** | | **8-11 hours** | |

---

## Commit Strategy

**One atomic commit (not incremental)** — all fixes together, one clean PR.

Why?
- Easier to revert if issues arise
- Single test run validates the entire migration
- Cleaner git history

```bash
# After all fixes and passing tests:
git add -A
git commit -m "chore: upgrade Spring 3.2 → 4.1 and Hibernate 3.6 → 4.1

- Spring 3.2.9 → 4.1.9
- Hibernate 3.6.10 → 4.1.12
- Fixed 192 query calls with generics handling
- Removed saveOrUpdateAll() (2 methods → loop pattern)
- Updated imports: org.hibernate.classic.Session, jasypt-hibernate3/4
- Updated Spring ORM config: LocalSessionFactoryBean → hibernate4
- All tests passing, JSF 1.2 + RichFaces 3.3 integration verified

Co-Authored-By: Claude Haiku 4.5 <noreply@anthropic.com>"
```

---

## Success Criteria

✅ `mvn clean compile -DskipTests` → **BUILD SUCCESS**  
✅ `mvn test` → **All tests pass** (no new failures)  
✅ No JSF form regressions (manual browser testing)  
✅ Database connectivity unchanged  
✅ Login/session handling works  
✅ RichFaces components (tree, calendar, etc.) render correctly  
✅ AJAX interactions (a4j:support) work  

---

## If Issues Arise During Testing

**Compilation errors remain?**
1. Group errors by type: `grep "\[ERROR\]" /tmp/compile.log | sed 's/.*\[ERROR\] //' | sort | uniq -c | sort -rn`
2. Identify top 3-4 patterns
3. Fix systematically (likely edge cases the script missed)

**Tests fail?**
1. Read error carefully — is it Spring 4.1 or Hibernate 4.1?
2. If Spring 4.1: Try downgrading Hibernate back to 3.6 to isolate
3. If Hibernate 4.1: Try downgrading Spring back to 3.2 to isolate
4. Once isolated, easier to debug

**JSF forms break?**
1. Check for `NullPointerException` in managed bean initialization
2. Check for `FacesException` in RichFaces component rendering
3. Verify `ServiceFinder.getLoggedUser()` still works
4. Test simple form first (Docentes) before complex ones (Notas with modals)

---

## Next Steps

1. **Decision:** Start this week or defer?
2. **If yes:** I'll help you reset the branch and execute Phase 1-4
3. **If uncertain:** Run a quick validation compile to check error count first

---

## References

**Hibernate 4.1 Migration:**
- https://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html_single/
- https://github.com/hibernate/hibernate-orm/blob/4.1.x/RELEASE_NOTES.txt

**Spring 4.1 Migration:**
- https://docs.spring.io/spring-framework/docs/4.1.x/spring-framework-reference/html/
- https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-4.1-Release-Notes

**Spring ORM + Hibernate:**
- https://docs.spring.io/spring-framework/docs/4.1.x/spring-framework-reference/html/orm.html

---

**Owner:** Claude Code + César  
**Created:** 2026-06-28  
**Last Updated:** 2026-06-28
