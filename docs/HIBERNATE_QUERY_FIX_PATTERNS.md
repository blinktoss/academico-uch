# Hibernate 4.1 Query Fixes: Pattern Reference

**192 query calls across 56 DAO impl files need generics handling**

---

## Pattern 1: findByCriteria() — Most Common (~120 calls)

### Before (Hibernate 3.6)
```java
@Override
public List<AcAlumno> findAlumnos(String codigo, String paterno) {
    DetachedCriteria criteria = DetachedCriteria.forClass(AcAlumno.class)
        .add(Restrictions.eq(AcAlumno.PROP_ALU_ACTIVO, "1"));
    
    if (codigo != null && !codigo.trim().isEmpty()) {
        criteria.add(Restrictions.ilike(AcAlumno.PROP_ALU_CODIGO, "%" + codigo + "%"));
    }
    
    return this.getHibernateTemplate().findByCriteria(criteria);  // ← Raw List
}
```

### After (Hibernate 4.1)
```java
@Override
public List<AcAlumno> findAlumnos(String codigo, String paterno) {
    DetachedCriteria criteria = DetachedCriteria.forClass(AcAlumno.class)
        .add(Restrictions.eq(AcAlumno.PROP_ALU_ACTIVO, "1"));
    
    if (codigo != null && !codigo.trim().isEmpty()) {
        criteria.add(Restrictions.ilike(AcAlumno.PROP_ALU_CODIGO, "%" + codigo + "%"));
    }
    
    return (List<AcAlumno>) this.getHibernateTemplate().findByCriteria(criteria);  // ← Cast added
}
```

### Decision Tree
1. **Does method have `@SuppressWarnings("unchecked")`?**
   - YES → Leave as-is ✅
   - NO → Add explicit cast `(List<AcAlumno>)` ✅

2. **How to infer the return type?**
   - Check method signature: `public List<AcAlumno> ...`
   - Extract: `AcAlumno`
   - Cast: `(List<AcAlumno>)`

---

## Pattern 2: createSQLQuery() — ~40 calls

### Before (Hibernate 3.6)
```java
@Override
@SuppressWarnings("unchecked")
public List<Object[]> obtenerSaldosEstudiante(int alumnoId) {
    String sql = "SELECT ... FROM ad_saldo_alumno WHERE alu_id = ? AND saldo_activo = '1'";
    List list = this.getHibernateTemplate().execute(session -> 
        session.createSQLQuery(sql)
            .setInteger(0, alumnoId)
            .list()
    );
    return list;
}
```

### After (Hibernate 4.1)
```java
@Override
@SuppressWarnings("unchecked")
public List<Object[]> obtenerSaldosEstudiante(int alumnoId) {
    String sql = "SELECT ... FROM ad_saldo_alumno WHERE alu_id = ? AND saldo_activo = '1'";
    List list = this.getHibernateTemplate().execute(session -> 
        session.createSQLQuery(sql)
            .setInteger(0, alumnoId)
            .list()
    );
    return list;  // ← Already suppressed, no change needed
}
```

### Decision Tree
1. **Does method have `@SuppressWarnings("unchecked")`?**
   - YES → No change needed ✅
   - NO → Add annotation and cast both

```java
@SuppressWarnings("unchecked")
@Override
public List<Object[]> obtenerSaldosEstudiante(int alumnoId) {
    String sql = "...";
    return (List<Object[]>) this.getHibernateTemplate().execute(...);
}
```

---

## Pattern 3: createQuery() — HQL Queries (~32 calls)

### Before (Hibernate 3.6)
```java
@Override
public List<AcActa> obtenerActasDelDocente(int docenteId) {
    String hql = "FROM AcActa act WHERE act.docente.docId = :docId AND act.actActivo = '1'";
    List result = this.getHibernateTemplate().findByNamedParam(
        hql,
        new String[]{"docId"},
        new Object[]{docenteId}
    );
    return result;  // ← Raw List
}
```

### After (Hibernate 4.1)
```java
@Override
public List<AcActa> obtenerActasDelDocente(int docenteId) {
    String hql = "FROM AcActa act WHERE act.docente.docId = :docId AND act.actActivo = '1'";
    List result = this.getHibernateTemplate().findByNamedParam(
        hql,
        new String[]{"docId"},
        new Object[]{docenteId}
    );
    return (List<AcActa>) result;  // ← Cast added
}
```

---

## Pattern 4: Already Suppressed (No Change Needed)

Many methods already have `@SuppressWarnings("unchecked")`. **Leave these alone:**

```java
@SuppressWarnings("unchecked")
@Override
public List<AcAlumno> seleccionarAlumnos(int facultyId) {
    DetachedCriteria c = DetachedCriteria.forClass(AcAlumno.class)
        .add(Restrictions.eq(AcAlumno.PROP_FAC_ID, facultyId))
        .add(Restrictions.eq(AcAlumno.PROP_ALU_ACTIVO, "1"));
    return this.getHibernateTemplate().findByCriteria(c);  // ✅ No change
}
```

The `@SuppressWarnings` already tells the compiler to ignore the type mismatch.

---

## Quick Reference: What to Do

| Scenario | Action | Example |
|----------|--------|---------|
| **Method has `@SuppressWarnings`** | ✅ No change | See Pattern 4 |
| **Method has `findByCriteria()`** | Add cast `(List<Type>)` | See Pattern 1 |
| **Method has `createSQLQuery()`** | Add `@SuppressWarnings` + cast | See Pattern 2 |
| **Method has `createQuery()`** | Add cast `(List<Type>)` | See Pattern 3 |
| **Named parameters** | Cast result | See Pattern 3 |
| **Multiple return types** | Cast each `list()` call | Each HQL/SQL call |

---

## Automated Detection Scripts

### Find ALL query calls (192 total)
```bash
grep -rn "\.findByCriteria\|\.createSQLQuery\|\.createQuery\|\.findByNamedParam" \
  src/main/java/net/uch/*/dao/impl --include="*.java" | wc -l
```

### Find ones NEEDING fixes (already suppressed = skip)
```bash
# Methods with queries but NO @SuppressWarnings
for file in src/main/java/net/uch/*/dao/impl/HS*DAOImpl.java; do
  grep -A 10 "public List" "$file" | grep -B 5 "\.findByCriteria\|\.createQuery" | \
    grep -v "@SuppressWarnings" > /dev/null && \
    echo "$file needs fixes"
done
```

### Get exact line numbers needing casts
```bash
grep -n "\.findByCriteria\|\.createQuery" src/main/java/net/uch/academica/dao/impl/HSAlumnoDAOImpl.java
# Output shows lines that need `(List<Type>)` cast added
```

---

## Common Mistake: Forgetting the Cast

❌ **Wrong** (compiles in H3.6, fails in H4.1)
```java
return this.getHibernateTemplate().findByCriteria(criteria);
```

✅ **Right** (explicit cast)
```java
return (List<AcAlumno>) this.getHibernateTemplate().findByCriteria(criteria);
```

✅ **Also Right** (annotation suppresses warning)
```java
@SuppressWarnings("unchecked")
return this.getHibernateTemplate().findByCriteria(criteria);
```

---

## Manual Process (File-by-File)

For each of the ~56 DAO impl files:

1. **Open file** in editor
2. **Search for:** `findByCriteria` or `createSQLQuery` or `createQuery`
3. **For each match:**
   - Check if method has `@SuppressWarnings("unchecked")`
   - If YES: ✅ Leave as-is
   - If NO: Add cast `(List<EntityType>)`
4. **Extract entity type from method signature**
   - Method signature: `public List<AcAlumno> findAlumnos(...)`
   - Type: `AcAlumno`
   - Cast: `(List<AcAlumno>)`

**Example fix:**
```java
// BEFORE
return this.getHibernateTemplate().findByCriteria(criteria);

// AFTER
return (List<AcAlumno>) this.getHibernateTemplate().findByCriteria(criteria);
```

---

## Files by Query Count (Fix Highest First = Highest ROI)

```bash
for file in src/main/java/net/uch/*/dao/impl/HS*DAOImpl.java; do
  count=$(grep -c "\.findByCriteria\|\.createQuery\|\.createSQLQuery" "$file" 2>/dev/null || echo 0)
  [ "$count" -gt 0 ] && echo "$count queries in $(basename $file)"
done | sort -rn | head -20
```

**Strategy:** Fix files with most queries first (e.g., HSActaDAOImpl if it has 12 queries = 12 fixes in one file = high efficiency).

---

## Validation

After fixing, **compile without tests:**
```bash
mvn clean compile -DskipTests -q
```

**Should produce:**
```
BUILD SUCCESS
```

If errors remain, they're edge cases — group by type and tackle systematically.

---

## Notes

- **No runtime behavior changes** — just type safety improvements
- **All fixes are mechanical** — no logic changes
- **Can be done incrementally** — fix one file at a time if needed
- **Safe to verify early** — compile check after each DAO file fixed

---

**This guide covers ~95% of the 192 query calls. Edge cases (custom types, streaming queries) handled case-by-case.**
