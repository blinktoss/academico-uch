# Investigation Results — Enrollment Module (AS-IS)

> Reverse-engineering report produced 2026-06-26 against `sistema_academico` on branch `feature/enrollment-matricula-service`. Findings are evidence-based (source code, Hibernate mappings, SQL/HQL) — anything not confirmed is explicitly marked UNKNOWN.

## 1. Student Lifecycle / Status Validation

### Findings
- `AcAlumno` has three status-like fields: `AluEstado` (`alu_estado`, varchar 6), `AluActivo` (`alu_activo`, varchar 1, "0"/"1"), `AluTipo` (`alu_tipo`, varchar 6).
- A catalog constant exists for type: `CAT_TIPO_ALUMNO_ESTUDIANTE = "009001"` (`net.uch.common.Constants`).
- `AluEstado` is `TbCatalogo`-backed, group `011` ("ESTADO DEL ALUMNO"), with 3 defined elements: `011001` REGULAR, `011002` OBSERVADO, `011003` CONAFU.
- **Live-data check (dev DB, `db_sis_academica`):** every single row in `ac_alumno` (2851 rows) has `alu_estado = '011001'` (REGULAR). `OBSERVADO`/`CONAFU` are defined in the catalog but have **never actually been assigned** to a student in this dataset.
- `AluTipo` is `TbCatalogo`-backed, group `009` ("TIPO DE ALUMNOS"), with elements: `009001` ESTUDIANTE, `009002` EGRESADO, `009003` RETIRADO, `009004` ANULACIÓN DE INGRESO, `009005` EN ESPERA.
- **Live-data check:** `009001` ESTUDIANTE = 2819 rows, `009003` RETIRADO = 20, `009004` ANULACIÓN = 11, `009005` EN ESPERA = 1. `009002` EGRESADO has zero rows.
- **Cross-tab `AluTipo` × `AluActivo` (real finding, not just an academic gap):** all 20 RETIRADO rows, 9 of 11 ANULACIÓN rows, and the 1 EN ESPERA row are still `AluActivo = '1'`. Withdrawing/cancelling a student does **not** flip the soft-delete flag in this dataset.
- The student-search DAO filters only on `AluActivo`. No code path filters or blocks on `AluEstado` or `AluTipo` during enrollment. Combined with the cross-tab above, **30 withdrawn/cancelled-admission/waitlisted students are currently findable and enrollable through the standard enrollment search**, with no code-level block.

### Evidence
- File: `src/main/java/net/uch/mapping/base/BaseAcAlumno.java:25-27,31`
- File: `src/main/resources/net/uch/hbm/AcAlumno.hbm.xml:63-82,104-109` (`AluEstado` mapped at lines 77-82)
- File: `src/main/java/net/uch/academica/dao/impl/HSAlumnoDAOImpl.java:27-52`
  ```java
  .add(Restrictions.eq(AcAlumno.REF + "." + AcAlumno.PROP_ALU_ACTIVO, "1"))
  ```
- File: `src/main/java/net/uch/academica/jsf/MbMatricula.java:277-412` (`seleccionar()`) — no `AluEstado`/`AluTipo` check anywhere in this method.
- SQL (run against dev DB):
  ```sql
  SELECT alu_estado, COUNT(*) FROM ac_alumno GROUP BY alu_estado;
  -- 011001 | 2851  (only value present)

  SELECT cat_codigo_elemento, cat_descripcion_elemento, cat_activo
  FROM tb_catalogo WHERE cat_codigo_grupo = '011';
  -- 000 ESTADO DEL ALUMNO (group header) | 1
  -- 001 REGULAR  | 1
  -- 002 OBSERVADO | 1
  -- 003 CONAFU   | 1

  SELECT alu_tipo, COUNT(*) FROM ac_alumno GROUP BY alu_tipo;
  -- 009001 | 2819   009003 | 20   009004 | 11   009005 | 1

  SELECT cat_codigo_elemento, cat_descripcion_elemento, cat_activo
  FROM tb_catalogo WHERE cat_codigo_grupo = '009';
  -- 000 TIPO DE ALUMNOS (group header) | 1
  -- 001 ESTUDIANTE | 1
  -- 002 EGRESADO   | 1
  -- 003 RETIRADO   | 1
  -- 004 ANULACION DE INGRESO | 1
  -- 005 EN ESPERA  | 1

  SELECT alu_tipo, alu_activo, COUNT(*) FROM ac_alumno GROUP BY alu_tipo, alu_activo;
  -- 009001 | 0 | 15      009001 | 1 | 2804
  -- 009003 | 1 | 20      (all RETIRADO still active)
  -- 009004 | 0 | 2       009004 | 1 | 9   (9 of 11 ANULACION still active)
  -- 009005 | 1 | 1       (the one EN ESPERA row still active)
  ```
- File: `src/main/java/net/uch/academica/dao/impl/HSAlumnoDAOImpl.java:27-52` confirms `seleccionarAlumnos()` never references `PROP_ALU_TIPO`. Other code (`HSMatriculaDAOImpl.seleccionarMatriculas:130,134`, `HSAlumnoDAOImpl:170-171`) does filter on `AluTipo = CAT_TIPO_ALUMNO_ESTUDIANTE`, but those are roster/count queries elsewhere, not the enrollment-search path.

### AS-IS Behavior
Enrollment search returns only students with `AluActivo = '1'`. Beyond that single active-flag filter, the enrollment flow performs **no validation** against `AluEstado` or `AluTipo`. `AluEstado` is a real catalog-backed status (Regular/Observado/Conafu), but in practice every student in this dataset is `REGULAR` — so the absence of `AluEstado` validation has never been exercised against a non-Regular student. `AluTipo`, by contrast, **does** have live counterexamples: 30 students typed RETIRADO/ANULACIÓN/EN ESPERA remain `AluActivo = '1'` and are therefore reachable by the same search used for fresh enrollment, with nothing in `MbMatricula.seleccionar()` or `matricular()` checking their type before allowing a new `AcMatricula` row to be created for them.

### Unknown / Pending
- The precise institutional meaning of `OBSERVADO`/`CONAFU` and whether either is *intended* to block enrollment (no usage anywhere in the current dataset to observe actual behavior against) — **UNKNOWN**.
- Whether `AluTipo` is enforced at a different layer (UI-level disable in `Alumnos.jsp`, a confirmation dialog, manual admin discipline) rather than in the DAO/service layer — **UNKNOWN**, not investigated outside `MbMatricula`/`HSAlumnoDAOImpl`.
- Whether this is a known/accepted gap or a genuine bug worth fixing — **flag for César's judgment**, not something to infer from code alone.

---

## 2. Debt Validation During Enrollment

### Findings
- Debtor status is **not a stored flag** — it's computed at read time by comparing `AdAlumnoTarifa.AlutarEstado` and `AlutarFechaProrroga` against "now".
- A tariff row counts as blocking debt when **both**: `AlutarEstado != "2"` AND `today > AlutarFechaProrroga`.
- No partial-payment field exists on `AdAlumnoTarifa` — debt is binary (state code), not amount-based reconciliation.

### Evidence
- File: `src/main/java/net/uch/academica/jsf/MbMatricula.java:391-405`
  ```java
  var fecPro = Calendar.getInstance();
  fecPro.setTime(adAlumnoTarifa.getAlutarFechaProrroga());
  if (!(adAlumnoTarifa.getAlutarEstado().equals("2"))
          && fecActual.getTimeInMillis() > fecPro.getTimeInMillis()) {
      bA.setVerDeuda(true);
      bA.setVerMatricular(false);
      ...
  }
  ```
- File: `src/main/java/net/uch/administrativa/dao/impl/HSAlumnoTarifaDAOImpl.java:97-107` (`seleccionarAlumnoTarifa(int alumnoId)`).
- File: `src/main/java/net/uch/mapping/base/BaseAdAlumnoTarifa.java:20-28` — fields `AlutarMonto`, `AlutarFechaPago`, `AlutarFechaProrroga`, `AlutarEstado`, `AlutarActivo`. No paid-amount/balance field.

### AS-IS Behavior
On every `seleccionar()` call, each matched student's active tariff rows are fetched fresh and evaluated against the clock. If any row is unpaid (`AlutarEstado != "2"`) and its grace-period date has passed, the student is flagged a debtor and `verMatricular`/`verRectificar`/`verReservar` are all disabled for that row. There is no DB column that says "is debtor" — it's recomputed on every search.

### Unknown / Pending
- The full meaning of `AlutarEstado` codes besides `"2"` (paid) — **UNKNOWN**.
- Whether paid amounts are tracked/reconciled in a receipts table elsewhere and just not surfaced here — **UNKNOWN**.

---

## 3. Prórroga Rules

### Findings
- `AdProrroga` (`ad_prorroga`) is a standalone audit-style entity: `proNumero`, `proFecha`, `proComentario`, `proCreacion`, `proActivo`, FK to `AdAlumnoTarifa`, FK to `TbUsuario` (who granted it).
- Created in `net.uch.administrativa.jsf.MbEdicionEstructPago.java:1070-1080`, persisted via `HSProrrogaDAOImpl.insertarProrroga()` (plain `session.save()`).
- **Enrollment never reads the `AdProrroga` table at all.** It only reads `AdAlumnoTarifa.AlutarFechaProrroga` directly (same column referenced in section 2).

### Evidence
- File: `src/main/resources/net/uch/hbm/AdProrroga.hbm.xml`
- File: `src/main/java/net/uch/administrativa/jsf/MbEdicionEstructPago.java:1070-1080`
  ```java
  var prorroga = new AdProrroga();
  prorroga.setProNumero(bmp.getProNumero());
  prorroga.setProFecha(bmp.getProFecha());
  prorroga.setProComentario(bmp.getProComentario());
  prorroga.setProActivo("1");
  prorroga.setAdAlumnoTarifa(new AdAlumnoTarifa(this.getTmpAlutar()));
  ```
- File: `src/main/java/net/uch/academica/jsf/MbMatricula.java:394-397` (same comparison quoted in section 2).

### AS-IS Behavior
`AdProrroga` functions as a **log/audit record** of grace periods granted (who, when, why) — it is not itself consulted by enrollment. The actual effect of granting a Prórroga must come from a separate write that pushes `AdAlumnoTarifa.AlutarFechaProrroga` forward (not confirmed in code reviewed — see Unknown below); once that column is in the future, the section-2 comparison naturally stops flagging debt. There is no override flag; the override is implicit in whatever process updates the date column.

### Unknown / Pending
- ~~No code was found that actually updates `AdAlumnoTarifa.AlutarFechaProrroga` from the `AdProrroga`-creation flow.~~ **RESOLVED (#45, then #33):** registering a Prórroga now atomically appends the `AdProrroga` history row and syncs `AlutarFechaProrroga` in one transaction (`ProrrogaService.registerExtension`); the latest-registered extension (highest `pro_numero`) is the effective deadline. See `docs/modules/academica.md`.

---

## 4. AlumnoTarifa Behavior

### Findings
- `AdAlumnoTarifa` is generated in **bulk, per payment-structure publish**, not per enrollment — `MbEstructuraPago.java:700-745`, one row per `(student × payment-structure-detail-line)`.
- Field-by-field copy from template (`AdEstructuraPagosDetalle`) to instance (`AdAlumnoTarifa`): amount, due date (copied into both `AlutarFechaPago` and `AlutarFechaProrroga` initially), concept/detail FK.
- Update path exists: `HSAlumnoTarifaDAOImpl.actualizarAlumnoTarifa()` (plain `update()`) and a `cambiarEstado()` HQL update.
- **`MbMatricula`'s enrollment methods (`matricular`, `reservarMatricula`, `rectificarMatricula`) never call any AlumnoTarifa write** — confirmed by full-file search; only reads, for the debt check and the debt-detail modal.

### Evidence
- File: `src/main/java/net/uch/administrativa/jsf/MbEstructuraPago.java:709-741`
  ```java
  for (AcAlumno alumno : lista) {
      for (AdEstructuraPagosDetalle estructuraPagosDetalle : estdet) {
          alTa = new AdAlumnoTarifa();
          alTa.setEstpagdet(estructuraPagosDetalle);
          alTa.setAlu(alu);
          alTa.setAlutarEstado("0");
          alTa.setAlutarMonto(estructuraPagosDetalle.getEstpagdetMonto());
          alTa.setAlutarFechaPago(estructuraPagosDetalle.getEstpagdetFechaPago());
          alTa.setAlutarFechaProrroga(estructuraPagosDetalle.getEstpagdetFechaPago());
          alTa.setAlutarActivo("1");
          listaPubli.add(alTa);
      }
  }
  daoTarifa.insertarTotal(listaPubli);
  ```
- File: `src/main/java/net/uch/academica/jsf/MbMatricula.java` — full-file grep for `daoTarifa`/`AlumnoTarifa`: only at lines 282, 391, 995 (all reads).

### AS-IS Behavior
Enrollment (`MbMatricula`) and the payment-structure module (`MbEstructuraPago`/`MbEdicionEstructPago`) are **completely decoupled**: enrolling a student does not create or touch their tariff rows. Tariffs are generated separately, in bulk, when an administrator publishes a payment structure for a semester — every active student gets one `AdAlumnoTarifa` row per installment line in that structure, independent of which courses they enrolled in.

### Unknown / Pending
- Whether rectification (creating a new `AcMatricula` row) triggers any downstream tariff regeneration — confirmed **no**, not in `MbMatricula`. Whether some other listener/batch does so elsewhere — **UNKNOWN**, not investigated.

---

## 5. Payment Structure

### Findings
- `AdEstructuraPagos` is FK-linked to `AcSemestre` via **two** columns: `sem_id` (current semester) and `sem_id_ingreso` (intake semester), both `NOT NULL`.
- `AdEstructuraPagosDetalle` stores `estpagdet_nombre`, `estpagdet_fecha_pago`, `estpagdet_monto`, `estpagdet_activo`, plus a **raw integer** `conpag_id` — there is **no mapped Hibernate association** to `AdConceptoPago`, just an FK column.
- `AdConceptoPago` is the concept catalog: code, description, standard amount, type, timestamps, active flag.
- **No mora/penalty field exists anywhere** in the payment-structure or concept tables.
- `AcSemestre.SemNumSemanas`/`SemPensiones` (weeks/installment-count) are **never read** anywhere outside `MbSemestre` itself — confirmed by full grep, zero hits.

### Evidence
- File: `src/main/resources/net/uch/hbm/AdEstructuraPagos.hbm.xml:52-69`
  ```xml
  <many-to-one name="Sem" column="sem_id" class="AcSemestre" not-null="true"/>
  ```
- File: `src/main/java/net/uch/mapping/base/BaseAdEstructuraPagosDetalle.java:20-22` — `conpagId` typed `Integer`, not an entity reference.
- File: `src/main/java/net/uch/mapping/base/BaseAdConceptoPago.java:21-27`.

### AS-IS Behavior
Payment structures are configured per semester (and intake semester) with detail lines, each tagged to a concept by raw FK id (no enforced referential object graph in Hibernate — joins, if any, must happen by hand in queries). Despite `AcSemestre` exposing week-count and installment-count fields, **nothing in the payment-structure generation code reads them** — installment count/timing appears to be authored manually per detail line, not derived from those semester fields.

### Unknown / Pending
- Semantics of `conpag_tipo` taxonomy (tuition vs. fee vs. other) — **UNKNOWN**.
- Whether `SemNumSemanas`/`SemPensiones` are vestigial/unused fields entirely, or consumed by a different module (e.g. `checkout`, outside this repo) — **UNKNOWN**.

---

## 6. Courses / Sections / Capacity

### Findings
- `AcSeccion.SecVacantes` is a **static capacity** column — never decremented anywhere in code reviewed.
- Occupancy is a **live `COUNT` query** at every check: `HSMatriculaDAOImpl.numeroMatriculadosporSeccion`/`numeroMatriculadosporSeccionSinAlumno`, counting active `ac_matricula`/`ac_matricula_curso` rows for `REGULAR` + `PRE_MATRICULA` types.
- The vacancy check happens **at submission time** inside `matricular()`: `vacantesVigentes = secVacantes - liveCount`; if `<= 0`, the section is rejected with a JS alert, and **no row is decremented** — the next check will simply re-count.

### Evidence
- File: `src/main/java/net/uch/academica/dao/impl/HSMatriculaDAOImpl.java:104-114`
  ```java
  DetachedCriteria.forClass(AcMatricula.class)
      .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1"))
      .add(Restrictions.disjunction()
          .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_REGULAR))
          .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_PRE_MATRICULA)))
      .createCriteria(AcMatricula.PROP_AC_MATRICULA_CURSOS)
      .add(Restrictions.eq(AcMatriculaCurso.PROP_SEC + "." + AcSeccion.PROP_ID, seccionId))
      .add(Restrictions.eq(AcMatriculaCurso.PROP_MATCUR_ACTIVO, "1"))).size()
  ```
- File: `src/main/java/net/uch/academica/jsf/MbMatricula.java:819-857`
  ```java
  vacantes = sec.getSecVacantes();
  vacantesUtilizados = daoMatricula.numeroMatriculadosporSeccionSinAlumno(bm.getSeccionId(), alumno.getId());
  vacantesVigentes = vacantes - vacantesUtilizados;
  if (vacantesVigentes <= 0) {
      contador++;
      mensaje = sec.getSecNombre() + " - " + sec.getCurape().getPlancur().getCur().getCurNombre();
  }
  ...
  this.setOncomplete("javascript:alert('no hay vacantes para " + mensaje + "')");
  ```

### AS-IS Behavior
There is no occupancy bookkeeping column — capacity checking is fully dynamic, computed fresh on every relevant page load/submission by counting live enrollment rows against the static `sec_vacantes` ceiling. If full, the user sees a JavaScript alert naming the offending section and the enrollment is rejected; nothing is written. This is inherently race-condition-prone under concurrent submissions (two users could both pass the check before either commits), though no evidence of a guard against that was found.

### Unknown / Pending
- Whether any locking/transaction isolation prevents the race condition above — based on this session's earlier service-layer work, `matricular()` is not wrapped in a single atomic check-then-insert transaction at the section level, so this is a **plausible but unconfirmed** concurrency gap — **UNKNOWN/flag for follow-up**, not exercised in code read.

---

## 7. Credits Validation

### Findings
- Credits are stored per curriculum line: `AcPlanCurso.PlancurCredito` (String).
- **No sum-of-selected-credits or min/max validation exists anywhere** in `matricular()`, `cursosAbiertos()`, or `rectificarMatricula()` — confirmed by reading the full file. Credits are only ever displayed, never aggregated or compared against a limit.

### Evidence
- File: `src/main/java/net/uch/mapping/base/BaseAcPlanCurso.java:20`
- File: `src/main/java/net/uch/academica/jsf/MbMatricula.java:783-862` (`matricular()`) — loop builds `Set<AcMatriculaCurso>` (lines 810-831), validating only section vacancy, never credits.

### AS-IS Behavior
A student can enroll in any number/combination of courses regardless of total credit load — there is no institutional credit-limit enforcement in this code path.

### Unknown / Pending
- Whether a credit cap is enforced anywhere else in the system (e.g. tariff calculation, a different module outside `academica`) — **UNKNOWN**, not investigated outside enrollment.

---

## 8. Prerequisites / Academic Rules

### Findings
- Prerequisites **are** explicitly modeled: `AcPreRequisitos` links a course (`Plancur`) to its required course (`PlancurIdRequisito`) — a real dependency graph, not just curricular-plan ordering.
- Approval threshold is a hardcoded grade cutoff: **`>= 10.5`** in `HSMatriculaDAOImpl.seleccionarCursosAprobados()`.
- `HSPreRequisitosDAOImpl.retonarCursosAbiertos()` returns courses whose *all* prerequisites are in the student's approved list, excluding courses already attempted (approved or failed).
- `retonarCursosLibres()` returns courses with **no** prerequisites at all, scoped to the student's specialty.
- **No repeated-failure limit found** — a failed course can be retaken indefinitely; there is no failure counter gating eligibility.

### Evidence
- File: `src/main/resources/net/uch/hbm/AcPreRequisitos.hbm.xml:28-39` (many-to-one `Plancur`/`PlancurIdRequisito`).
- File: `src/main/java/net/uch/academica/dao/impl/HSMatriculaDAOImpl.java:70`
  ```java
  if ((Float) obj[1] >= 10.5) { aprobados.add((Integer) obj[0]); }
  ```
- File: `src/main/java/net/uch/academica/dao/impl/HSPreRequisitosDAOImpl.java:76-122` (`retonarCursosAbiertos`), `:125-153` (`retonarCursosLibres`, checks `acPreRequisitosByPlancurIdRequisito().isEmpty()`).

### AS-IS Behavior
`cursosAbiertos()` merges two pools: (a) courses whose prerequisite chain is fully satisfied by the student's approved-courses list, and (b) prerequisite-free courses in the student's specialty — both excluding anything already attempted — then filters by term aperture (`AcCursoAperturado.curapeAperturado == "1"`). This is genuine prerequisite enforcement, not mere sequencing. Repetition of a failed course is unrestricted.

### Unknown / Pending
- Whether the prerequisite graph guards against cycles — **UNKNOWN**, no explicit guard found.
- Whether `AcActaDetalle` tracks failure counts for some other (non-enrollment-blocking) purpose — **UNKNOWN**.

---

## 9. Rectificación de Matrícula

### Findings
- Rectification does **not** edit the existing `AcMatricula` row — it always **inserts a new one** via `EnrollmentService.registerEnrollment()`, then marks the *old* row's type as `MATRICULA_RECTIFICADA` (or `PRE_MATRICULA_RECTIFICADA`) via `actualizarTipoMatricula`.
- Course addition/removal is implicit: the new row's `AcMatriculaCurso` set contains only whatever sections are selected in the modal at submission time; anything left at "No registra Matrícula" (`seccionId = 0`) is simply absent from the new set.
- Section-occupancy validation is the **same** live-count check as a fresh enrollment (section 6) — rectification gets no special treatment there.
- **No AlumnoTarifa/payment interaction during rectification** — confirmed by full-file search (consistent with section 4).
- Audit fields (this session's work) behave as designed here: the new row gets `createdAt`/`createdBy` stamped on insert; the old row gets `updatedAt`/`updatedBy` stamped via the now-load-then-update `actualizarTipoMatricula`.

### Evidence
- File: `src/main/java/net/uch/academica/jsf/MbMatricula.java:530-616` (`rectificarMatricula()`), `:783-862` (`matricular()`), branch at `:847-851`.
- File: `src/main/java/net/uch/academica/service/impl/EnrollmentServiceImpl.java:84-94` (`registerEnrollment`).
- File: `src/main/java/net/uch/academica/dao/impl/HSMatriculaDAOImpl.java:160-175` (`actualizarTipoMatricula`, load-then-update, comment explicitly notes the audit-interceptor rationale).

### AS-IS Behavior
Rectification is an **append-only correction model**: every rectification produces a brand-new, fully audited `AcMatricula` row reflecting the corrected course selection, while the prior row is retagged (not deleted) as superseded. This preserves a full history of enrollment corrections per student per semester, at the cost of `ac_matricula` accumulating one row per rectification event.

### Unknown / Pending
- Whether the superseded (old) row is ever soft-deactivated (`MatActivo = "0"`) in addition to its type change, or stays `MatActivo = "1"` forever as a historical artifact — **UNKNOWN**, not confirmed in the code read for this topic.

---

## Cross-Cutting Flags Worth Attention

1. **Prórroga linkage (section 3) — RESOLVED (#45, then #33).** Registering a Prórroga atomically appends `AdProrroga` history and syncs `AdAlumnoTarifa.AlutarFechaProrroga` (`ProrrogaService`); the latest-registered extension wins and every writer keeps history.
2. **Section-capacity race condition (section 6)** — the vacancy check-then-insert in `matricular()` isn't atomic; two concurrent enrollments could both pass the check for the last seat.
3. **No credit-limit, no student-status, no repeated-failure enforcement** — three "expected" academic guardrails (sections 1, 7, and 8's failure-limit sub-question) are confirmed absent, not just unconfirmed. If these are supposed to exist per institutional policy, they're currently unenforced in code.
