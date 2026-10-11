# Use case inventory (Deliverable 2)

Every use case identified in the AS-IS system, organized by functional module. Derived from the
managed beans' action methods, the JSP views, the navigation map (`faces-config.xml`), the
servlets, and previously verified documentation. **Identification only** — full documentation
exists solely for use cases marked *Documented* (file linked).

Status: **Documented** (full UC file) · **Identified** (scoped, not yet written) ·
**Discovery needed** (behavior exists, rules unconfirmed).

## Enrollment (`enrollment/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-ENR-001 | Search eligible students for enrollment | `MbMatricula.seleccionar` | **Documented** — [file](enrollment/use-cases/UC-ENR-001-search-eligible-students.md) |
| UC-ENR-002 | Register regular enrollment | `MbMatricula.matricular`, `cursosAbiertos` | **Documented** — [file](enrollment/use-cases/UC-ENR-002-register-enrollment.md) |
| UC-ENR-003 | Rectify enrollment | `MbMatricula.rectificarMatricula` | **Documented** — [file](enrollment/use-cases/UC-ENR-003-rectify-enrollment.md) |
| UC-ENR-004 | Reserve enrollment | `MbMatricula.reservarMatricula` | **Documented** — [file](enrollment/use-cases/UC-ENR-004-reserve-enrollment.md) |
| UC-ENR-005 | Cancel enrollment (logical delete) | `MbMatricula.eliminarMatricula` | Identified |
| UC-ENR-006 | View student's enrolled courses / history | `MbMatricula.verCursosMatriculados` | Identified |
| UC-ENR-007 | View student debt detail | `MbMatricula.verDetalleDeuda` | Identified |
| UC-ENR-008 | Enrollment-period extensions ("ampliaciones") | `MbMatricula.openModalforAmpliaciones` | Discovery needed |

## Students (`students/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-STU-001 | Search students | `MbAlumno.seleccionar` | Identified |
| UC-STU-002 | Register new student | `MbAlumno.nuevo`, `insertar` | Identified |
| UC-STU-003 | Edit student data (incl. specialty/plan changes) | `MbAlumno.editarFila`, `guardar` | Identified |
| UC-STU-004 | Deactivate student (logical delete) | `MbAlumno.eliminarFila` | Identified |
| UC-STU-005 | View student attendance calendar | `MbAlumno.mostrarAsistencia`, `llenarCalendario` | Discovery needed |
| UC-STU-006 | Serve student photo | `StudentPhotoServlet` | Identified |

## Grades & official records (`grades/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-GRD-001 | Browse sections and select one for grading | `MbNota.armarEstructura`, `seleccion` | Identified |
| UC-GRD-002 | Register/update grades for a section | `MbNota.insertar` | **Documented** — [file](grades/use-cases/UC-GRD-002-register-grades.md) |
| UC-GRD-003 | Publish Principal Acta | `MbNota.asignarNotasActas`, `insertarActa` | **Documented** — [file](grades/use-cases/UC-GRD-003-publish-principal-acta.md) |
| UC-GRD-004 | Publish Substitute-exam Acta (Sustitutoria) | `MbNota.llenarActaSustitutoria`, `insertarSustitutorios` | **Documented** — [file](grades/use-cases/UC-GRD-004-substitute-acta.md) |
| UC-GRD-005 | Convalidación (transfer-credit recognition) | `MbConvalidacion.*` | **Documented** — [file](grades/use-cases/UC-GRD-005-convalidacion.md) |
| UC-GRD-006 | Lock/unlock web grade entry | `MbNota.bloquearNotas`, `openModalForBloqueoWeb` | Discovery needed |
| UC-GRD-007 | Import grades | `MbNota.openModalForImport`, `guardarImporte` | Discovery needed |
| UC-GRD-008 | Preview/print grade reports (actas, consolidados) | `MbNota.vistaPrevia`, `ReportServlet` | Identified |

## Schedules & course offering (`schedules/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-SCH-001 | Manage shifts (turnos) and period slots | `MbTurno.*` | Identified |
| UC-SCH-002 | Open courses for a semester (apertura) | `MbAperturaCursos.insertar`, `agregar` | Identified |
| UC-SCH-003 | Manage sections & vacancies of an opened course | `MbAperturaCursos.agregarSec`, `editSec`, `quitarSec` | Identified |
| UC-SCH-004 | Assign weekly schedule to a section | `MbHorario.asignarHorario`, `seleccionar<Día>`, `insertar` | Identified |
| UC-SCH-005 | View schedules by area | `MbHorarioArea.seleccionar` | Identified |
| UC-SCH-006 | Assign section teacher / exam averages load | `MbAperturaCursos.loadPromediosExamenes`, `detallar` | Discovery needed |

## Curriculum (`curriculum/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-CUR-001 | Manage course catalog | `MbCurso.*` | Identified |
| UC-CUR-002 | Manage curricular plans | `MbPlanCurricular.*` | Identified |
| UC-CUR-003 | Assign courses to a plan (cycle, credits, equivalences) | `MbPlanCurricularCurso.*`, `openModalEquivalencias` | Identified |
| UC-CUR-004 | Manage course prerequisites | `MbPreRequisito.*` | Identified |
| UC-CUR-005 | Manage thematic units (syllabus content) | `MbUnidadTematica.*` | Identified |
| UC-CUR-006 | Manage evaluation systems (components & weights) | `MbSistemaEvaluacion.*` | Identified |

## Cashier / payments (`cashier/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-CSH-001 | Manage payment concepts | `MbConceptoPago.*` | Identified |
| UC-CSH-002 | Define & publish payment structure (bulk tariff generation) | `MbEstructuraPago.*`, `publicar` | Identified |
| UC-CSH-003 | Edit a student's payment commitments / grant prórroga | `MbEdicionEstructPago.*` | Discovery needed |
| UC-CSH-004 | Bulk-edit payment structures | `MbEdicionEstructPagoBloque.*` | Discovery needed |
| UC-CSH-005 | Collect payment & issue voucher (incl. carnet, mora, extemporaneous enrollment) | `MbPago.pagar`, `carnet`, `mora`, `matExtemporanea` | Discovery needed |
| UC-CSH-006 | Void / delete a payment | `MbPago.anularPago`, `eliminarPago` | Discovery needed |
| UC-CSH-007 | Print voucher & payment listings | `MbPago.imprimirComprobante`, `ComprobanteServlet` | **Documented** — [file](cashier/use-cases/UC-CSH-007-print-voucher-and-listings.md) |
| UC-CSH-008 | Consult balances (Saldos) | `MbSaldo.seleccionar` | Identified |
| UC-CSH-009 | Bank-file processing (export, import, reconcile, generate vouchers) | `MbProcesoBanco.*` | Discovery needed |
| UC-CSH-010 | Personalize a student's payment plan | `MbPago.personalizar`, `savePersonalization` | Discovery needed |

## Admission (`admission/`) — owned by `sigu_admision` / `wcali`

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-ADM-001 | Candidate registration → exam (full flow) | `sigu_admision` (other repo) | Identified (cross-repo) |
| UC-ADM-002 | Exam scoring & vacancy assignment | `wcali` (other repo) | Identified (cross-repo) |
| UC-ADM-003 | Admitted candidate becomes student in this system | boundary: student registry + convalidación | Discovery needed |

## Free courses (`free-courses/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-FCL-001 | Manage areas | `MbArea.*` | Identified |
| UC-FCL-002 | Manage modules & their courses | `MbModulo.*` | Identified |
| UC-FCL-003 | Manage workshops (talleres) | `MbTaller.*` | Identified |
| UC-FCL-004 | Manage curricular plan (module ↔ courses) | `MbPlanCurricularCL.*` | Identified |
| UC-FCL-005 | Open a workshop for a term (incl. evaluation system) | `MbTallerAperturado.*` | Identified |
| UC-FCL-006 | Manage workshop sections & schedules | `MbSeccionCL.*` | Identified |
| UC-FCL-007 | Register free-course students (external, university-affiliated) | `MbAlumnoCL.*` | Discovery needed |
| UC-FCL-008 | Enroll students in workshops (with payment verification, custom amounts) | `MbMatriculaCL.*` | Discovery needed |
| UC-FCL-009 | Register free-course grades | `MbNotasCL.*` | Identified |
| UC-FCL-010 | Manage free-course payment structures (define/edit/bulk) | `MbEstructuraPagoCL`, `MbEdicionEstructPagoCL(+Bloque)` | Discovery needed |

## Teachers (`teachers/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-TCH-001 | Manage teacher registry | `MbDocente.*` | Identified |
| UC-TCH-002 | Declare teacher availability | `MbDocenteDisponibilidad.*` | Identified |
| UC-TCH-003 | Track teacher attendance (sessions & marks) | `MbAsistenciaDocente.*` | Discovery needed |

## Administration & security (`administration/`)

| ID | Use case | AS-IS entry point | Status |
|---|---|---|---|
| UC-SEC-001 | Log in / log out (with account lockout) | `MbLogin.checkUser` | Identified |
| UC-SEC-002 | Manage users (create, unlock) | `MbUsuario.*` | Identified |
| UC-SEC-003 | Role-based menu authorization (RBAC) | `MbMenu.armarMenu`, `tb_menu_rol` | Identified |
| UC-SEC-004 | Manage semesters | `MbSemestre.*` | Identified |
| UC-SEC-005 | Manage faculties & specialties | `MbFacultad.*`, `MbEspecialidad.*` | Identified |
| UC-SEC-006 | Manage locals, pavilions, classrooms | `MbLocal`, `MbPabellon`, `MbAula` | Identified |
| UC-SEC-007 | Publish institutional activities/announcements | `MbActividad.*`, `ActivityImageServlet` | Identified |
| UC-SEC-008 | Home dashboard (role-based summary) | `MbDashboard.init` | Identified |

## Totals

| Module | Documented | Identified | Discovery needed | Total |
|---|---|---|---|---|
| Enrollment | 4 | 3 | 1 | 8 |
| Students | 0 | 5 | 1 | 6 |
| Grades | 4 | 2 | 2 | 8 |
| Schedules | 0 | 5 | 1 | 6 |
| Curriculum | 0 | 6 | 0 | 6 |
| Cashier | 1 | 2 | 7 | 10 |
| Admission | 0 | 2 | 1 | 3 |
| Free courses | 0 | 6 | 4 | 10 |
| Teachers | 0 | 2 | 1 | 3 |
| Administration | 0 | 8 | 0 | 8 |
| **Total** | **9** | **41** | **18** | **68** |
