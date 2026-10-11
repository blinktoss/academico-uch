# Free Courses (Cursolibre) Module

## Purpose & Scope

Parallel academic model for optional complementary offerings (Office tools, Databases, Sports, etc.),
separate from the core enrollment/grading flow. Students can freely enroll in these courses; grades
are recorded in a separate `Acta` track.

Package `net.uch.cursolibre` (`jsf` managed beans, `dao` + `dao.impl`, `service`).
Persistent entities live in `net.uch.mapping` (`Cl*` prefix).

---

## Architecture: Hierarchy & Trees

Free Courses use a four-level hierarchy:
- **Areas** (`ClArea`) — top-level groupings
- **Modules** (`ClModulo`) — subdivisions within areas
- **Courses** (`ClCurso`) — courses within modules
- **Workshops/Sections** (`ClTallerAperturado`) — scheduled offerings

Two managed beans expose this as trees:
- **`BaseMbViewTreeCL`** — full tree (areas → modules → courses → workshops)
- **`MbTallerAperturado`** — workshop management (modules → courses → workshops, abbreviated)

---

## Tree Builder N+1 Optimization

Both tree beans have been optimized to eliminate N+1 queries using the batch-query + in-memory-grouping
pattern (see **academica.md** for full details).

### Applied Optimizations

**`BaseMbViewTreeCL.armarEstructura()`:**
1. Batch load all modules for all areas → group by `areaId`
2. Batch load all courses for all modules → group by `moduloId`
3. Batch load all workshops for all courses → group by `cursoId`
4. Loop areas → loop grouped modules → loop grouped courses → loop grouped workshops

**`MbTallerAperturado.armarEstructura()`:**
1. Batch load modules for areas → group by `areaId`
2. Batch load courses for modules → group by `moduloId`
3. Loop areas → loop grouped modules → loop grouped courses (abbreviated, no workshops)

### Performance Impact

- **Test suite improvement:** 21% speedup (139.4s on combined test suite with all tree optimizations)
- **Query reduction:** 3 queries per hierarchy level instead of per-parent queries
- **Byte-identical output:** Tree structures and node IDs remain unchanged; all tests pass

### DAO Methods

| Bean | Method | Purpose |
|---|---|---|
| `HSModuloDAO` | `findModulosByAreas(List<Integer>)` | Batch load modules grouped by area |
| `HSCursoCLDAO` | `findCursosByModulos(List<Integer>)` | Batch load courses grouped by module |
| `HSTallerAperturadoDAO` | `findOpenedWorkshopsByCourses(List<Integer>)` | Batch load workshops grouped by course |

All use flat projection DTOs (`CLModuloRow`, `CLCursoRow`, `CLTallerAperturadoRow`) to avoid entity
hydration overhead.

---

## Related Cross-Cutting Patterns

- **N+1 Optimization Pattern** — documented in **academica.md**
- **Audit fields** — inherited pattern (created_at, created_by, etc.) applied to core entities
- **Logical deletion** — rows marked inactive via `*_activo` flags rather than physical delete

---

## Known Constraints

- Hierarchy is 4 levels deep; add operations may need to coordinate across all levels
- Shared MySQL 5.6 database schema (changes ripple to other UCH apps)

---

## Enrollment tariffs & test-data management (`cl_alumno_tarifa`)

### Write shape — tariffs are saved *outside* the matricula graph

Free-course enrollment persists in **two disconnected pieces**:

1. the **matricula graph** — `ClMatricula` + cascaded `ClMatriculaTaller`, via `HSMatriculaCLDAO#insertarMatricula(s)`; and
2. the **tariffs** — `ClAlumnoTarifa` rows saved **separately** via `HSAlumnoTarifaCLDAO#generarAlumnoTarifa(s)`, linked back to the enrollment only by the denormalized `mat_id` / `sec_id` **columns**. They are **not** reachable from the `ClMatricula` object graph.

Both enrollment entry points follow this shape: `MbMatriculaCL#matricularSeleccionados` (`MbMatriculaCL.java` ~L491) and `MbSeccionCL#perfomEnrollmentsToSection` (`MbSeccionCL.java` ~L405-433).

**Production un-enroll is correct** — `MbMatriculaCL#anularMatricula` (~L279) annuls the matricula **and** logically deletes its tariffs (`eliminarAlumnosTarifa` → `alutar_activo='0'`). So production leaves **no orphans**; the problem below is a **test-harness** gap only.

### The test-cleanup gap (fixed)

`HSTestSupportDao#cleanClMatricula(secId)` used to physically `deleteAll` **only** the matricula graph, never the separately-saved tariffs. Every enroll-test run (`MbSeccionCLTest#openModalMatriculadosAndEnrollTest`, the sole caller) therefore left its `cl_alumno_tarifa` rows behind as **orphans** — active rows whose `mat_id` points to a since-deleted matricula. Observed scale on the dev DB before the fix: **~2,160 orphans ≈ 49 % of all active CL tariffs**.

**Fix:** `cleanClMatricula` now also deletes the tariffs by the deleted matriculas' `mat_id`s (collect `matIds` from the loaded `ClMatricula` list → `delete from ClAlumnoTarifa where matId in (:matIds)`), mirroring what production's un-enroll already does.

### Why the orphans are a hazard, not just clutter — `mat_id` recycling

`cl_matricula.mat_id` is `AUTO_INCREMENT`, and **MySQL 5.6 InnoDB recomputes the counter to `MAX(id)+1` on every server restart** (the counter is not persisted). Once matriculas are deleted, the counter drops **below** the historical peak, so **new enrollments reuse `mat_id` values that orphan tariffs still reference** (observed: counter at `3542` while orphan tariffs referenced `mat_id` up to `4535`). A fresh matricula can then collide with a dead tariff, and `HSAlumnoTarifaCLDAO#seleccionarTarifasXMatricula(matId, aluId)` can return **stale rows** when the recycled `mat_id` + `alu_id` both match. (This recycling is also why, with the fix in place, the orphan count *declines* each run — the `mat_id`-scoped delete reclaims older orphans whose ids have been reused — rather than merely holding flat.)

### Query-shape lesson — the 0-vs-N divergence

The same student can yield **0 rows** from one query and **hundreds** from another:

- A **tariff-anchored** query (as used by `sigu_caja`) inner-joins each tariff's **own** `mat_id → live cl_matricula` and `sec_id → open taller`, and filters `alutar_estado <> '2'`. Orphans are invisible and counts are self-consistent → **0** when nothing live remains.
- A **student-anchored** query that reaches sections via `cl_matricula_taller` **without** correlating the tariff (missing `AND at.mat_id = cm.mat_id AND at.sec_id = cs.sec_id`) **cross-joins** the student's tariffs × enrollments and counts orphans — e.g. `74 active tariffs × 6 live enrollments = 444` rows for one student.

**Prefer the tariff-anchored shape for any tariff validation/report.** A student-anchored query must correlate `mat_id` (and `sec_id`) back to the tariff, or it over-counts.

### Deferred to the Cursos Libres redesign sprint

- **One-time purge** of the residual orphans — `DELETE FROM cl_alumno_tarifa WHERE mat_id IS NOT NULL AND mat_id NOT IN (SELECT mat_id FROM cl_matricula)`. This is a **dev-DB maintenance step** (like the Actas purge in the root `CLAUDE.md`), **not** a `Vxxx` migration and **not** `db_sis_academica_setup.sql` (a fresh restore of the 2010 backup has zero test orphans).
- **Deeper modeling fix** — `cl_alumno_tarifa` carries denormalized `sec_id` + `mat_id` with **no FK enforcement** (which is what allows orphans + id-recycling collisions), atop the duplicated `ac_alumnos` / `cl_alumnos` student model. See the cashier AS-IS boundary vs `sigu_caja` (`docs/business/cashier/README.md`).
