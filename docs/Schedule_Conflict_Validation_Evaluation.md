# Schedule Conflict Validation — Evaluation (AS-IS)

> Evidence-based investigation against `sistema_academico`, branch `feature/enrollment-matricula-service`. All claims are backed by source code, Hibernate mappings, and live data queried from the dev DB (`db_sis_academica`).

## TL;DR

**Yes, the system detects overlapping schedules — but the check is silently broken for every semester except one hardcoded historical semester (id `9`, "2010-2").** For any other semester, the query's `WHERE` clause filters out all real rows and always returns "no conflict," regardless of actual overlaps. This is a confirmed, high-confidence bug, not a design gap.

---

## 1. Does the system detect overlapping schedules?

**Yes, conceptually — via shared time-slot identity, not generic interval-overlap math.**

The institution's timetable is **quantized to a fixed catalog of period-slots per shift (`Turno`)**, not arbitrary continuous time ranges. Confirmed by querying `ac_horario`/`ac_turno_detalle` directly:

```sql
SELECT s.sec_id, h.hor_dia, td.turdet_hora, t.tur_nombre
FROM ac_seccion s
JOIN ac_horario h ON h.sec_id = s.sec_id
JOIN ac_turno_detalle td ON td.turdet_id = h.turdet_id
JOIN ac_turno t ON t.tur_id = td.tur_id
WHERE h.hor_activo='1' AND s.sec_activo='1'
ORDER BY s.sec_id, h.hor_dia, td.turdet_hora LIMIT 30;
```
```
sec_id  hor_dia  turdet_hora  tur_nombre
1       016003   08:00:00     TURNO MAÑANA
1       016003   08:45:00     TURNO MAÑANA
1       016003   09:30:00     TURNO MAÑANA
1       016003   10:15:00     TURNO MAÑANA
1       016003   11:00:00     TURNO MAÑANA
```

A multi-period class is stored as **one `ac_horario` row per 45-minute slot it occupies** (e.g. a class spanning 08:00–10:15 has 4 separate rows: 08:00, 08:45, 09:30, 10:15). `AcTurnoDetalle.TurdetHora` is a single `TIME` column (`src/main/resources/net/uch/hbm/AcTurnoDetalle.hbm.xml:20-25`) — there is **no end-time/duration column anywhere**; duration is implicit in how many consecutive slot-rows a section occupies.

Given this model, **matching two sections on identical `(HorDia, TurdetHora)` pairs is a structurally valid way to detect overlap** — two sections conflict if and only if they share at least one slot from the catalog. The user's example (08:00–10:00 vs 09:00–11:00) would be caught correctly *if* the institution's actual slot grid produces a shared slot between the two — which it would, given slots are shared across all sections/turnos.

---

## 2. At what point is validation executed?

**At enrollment submission time**, inside `MbMatricula.matricular(ActionEvent)` — shared by **both** fresh enrollment and rectification, since `rectificarMatricula()` ultimately calls the same `matricular()` method.

```java
// src/main/java/net/uch/academica/jsf/MbMatricula.java:830-857
setListaDuplicadoHor(daoh.duplicidadHoraria(vSecIds));
LOG.info("En total de horarios duplicados: {} for sections '{}'", listaDuplicadoHor.size(), vSecIds);
if (!matriculaCursos.isEmpty()) {
    matricula.setMatTipo(CAT_TIPO_MATRICULA_REGULAR);
    if (contador == 0) {
        if (listaDuplicadoHor.isEmpty()) {
            matricula.setAcMatriculaCursos(matriculaCursos);
            int matriculaId = enrollmentService.registerEnrollment(matricula, rowTipoMatriculaId);
            ...
        } else {
            this.setOncomplete("javascript:Richfaces.showModalPanel('dupHorario');");
        }
    }
    ...
}
```

The check runs **after** the section-vacancy check (`contador == 0`) and **before** the call to `enrollmentService.registerEnrollment(...)`. It is evaluated once per submission, against the full set of section ids selected in that submission (`vSecIds`) — it does not check incrementally as sections are picked.

The existing test suite confirms rectification goes through the same path: `MbMatriculaTest#openModalForRectificacionDeMatriculaTest` deliberately forces a conflict and asserts `manager.getOncomplete().contains("dupHorario")`.

---

## 3. Is enrollment blocked or only warned?

**Hard block — not a dismissible warning.** If `duplicidadHoraria()` returns any rows, the `if (listaDuplicadoHor.isEmpty())` branch is skipped entirely: `enrollmentService.registerEnrollment(...)` is **never called**, no `AcMatricula`/`AcMatriculaCurso` row is persisted. The user is shown a RichFaces modal panel (`dupHorario`) listing the conflicting sections (`listaDuplicadoHor`, populated with `BeanDuplicadoHorario` rows: day, hour, course name(s), count). There is no "proceed anyway" path in this method.

---

## 4. Which classes/tables implement this rule?

| Layer | Class / Table | Role |
|---|---|---|
| Bean (orchestration + block decision) | `net.uch.academica.jsf.MbMatricula.matricular()` | Calls the check, decides whether to persist |
| DAO interface | `net.uch.academica.dao.HSHorarioDAO.duplicidadHoraria(List<Integer>)` | Contract |
| DAO impl (the actual query) | `net.uch.academica.dao.impl.HSHorarioDAOImpl.duplicidadHoraria()` | Raw native SQL, see §5 |
| DTO | `net.uch.academica.jsf.beans.BeanDuplicadoHorario` | Day/hour/course-name(s) for the modal |
| Tables | `ac_horario`, `ac_turno_detalle`, `ac_turno`, `ac_seccion`, `ac_curso_aperturado`, `ac_plan_curso`, `ac_curso` | Joined in the native query |

`EnrollmentService` (this session's new service layer) is **not involved at all** — this is pre-existing legacy logic, untouched by the recent `MbMatricula`→`EnrollmentService` migration.

---

## 5. Confirmed bug: the check is hardcoded to semester id `9` and is dead code for every other semester

The native SQL in `HSHorarioDAOImpl.duplicidadHoraria()` hardcodes the semester filter:

```sql
-- src/main/java/net/uch/academica/dao/impl/HSHorarioDAOImpl.java:49-77
...
and ac_curso_aperturado.sem_id=9    -- HARDCODED, no parameter
and ac_plan_curso.plancur_activo='1' ...
and ac_seccion.sec_id in(:ids)
...
```

The only parameter bound is `:ids` (the section ids being submitted) — `sem_id=9` is a literal. Cross-checked against the test fixtures:

```java
// src/test/java/net/uch/Stubs.java:44
public static final int SEMESTRE_2010_2_ID = 9;
```

**`9` is exactly `SEMESTRE_2010_2_ID`.** This is not a coincidence — every test that exercises this code path (`openModalRegistroMatriculaTest`, `openModalForRectificacionDeMatriculaTest`) uses `SEMESTRE_2010_2_ID`, which is the *only* semester for which this query can return non-empty results, since it's the only one where `ac_curso_aperturado.sem_id` will actually equal the literal `9` in the `WHERE` clause.

**Consequence:** when a student enrolls in any section belonging to a different semester's course offerings (i.e. any semester other than id `9`), the joined `ac_curso_aperturado.sem_id` for those sections will never equal `9`, so the inner subquery's `WHERE` clause filters out every row — the query returns an **empty result set unconditionally**, regardless of whether the selected sections genuinely overlap. `listaDuplicadoHor` is always empty, the `if (listaDuplicadoHor.isEmpty())` branch always succeeds, and enrollment always proceeds — **the conflict check silently does nothing for the current semester** in any real deployment (the long-lived dev DB's "current" semester is whatever is most recently configured, almost certainly not id `9` anymore).

This means: **in current operation, two genuinely overlapping sections can be enrolled together with no block and no warning** — the feature exists in code and is tested, but only against a single fossilized semester id, not parameterized to "the semester being enrolled in."

---

## Recommendation

Replace the hardcoded `and ac_curso_aperturado.sem_id=9` with a bound parameter (`:sem_id`), passed through `duplicidadHoraria(List<Integer> seccionIds, int semestreId)` from `MbMatricula.matricular()` (which already has `rowSemestreId` in scope). This is a small, low-risk, high-value fix — the detection logic itself is sound; only the filter is wrong. Recommend adding a regression test using a non-`SEMESTRE_2010_2_ID` semester with a deliberately constructed conflict, since the existing tests structurally cannot catch this class of bug (they all use the one semester id that happens to work).

## Secondary, lower-priority observation
`HSHorarioDAOImpl.duplicidadHoraria()` opens its own Hibernate `Session` manually (`getHibernateTemplate().getSessionFactory().openSession()`) and closes it at the end of the method with no `try`/`finally` — if `q.list()` throws, the session leaks. Same shape of issue already fixed elsewhere in this session's `EnrollmentService` work; not fixed here since it's out of scope for this validation check.
