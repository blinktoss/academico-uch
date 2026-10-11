# Functional map — UCH suite (Deliverable 1)

High-level map of business capabilities, organized by capability rather than by implementation
class. Built from AS-IS inspection (managed beans, views, navigation, servlets) of
`sistema_academico` plus the ecosystem description of the other six repos.

## The suite at a glance

```
                         ┌─────────────────────────────────────────────┐
                         │        Shared MySQL 5.6 database            │
                         │            db_sis_academica                 │
                         └─────────────────────────────────────────────┘
     writes/owns ▲                    ▲ reads/writes            ▲ reads
┌────────────────┴───────┐   ┌────────┴────────┐   ┌────────────┴───────────┐
│ sistema_academico      │   │ sigu_caja       │   │ sigu_intranet (portal) │
│ (core, this repo)      │   │ (checkout)      │   │ sigu_reportes (reports)│
│                        │   │ sigu_admision   │   │ test (surveys)        │
│                        │   │ wcali (scoring) │   │                        │
└────────────────────────┘   └─────────────────┘   └────────────────────────┘
```

`sistema_academico` is the **source of truth** for students, academic structures, enrollment,
grades, and (historically) payments. The other apps carve out or consume slices of that data.

## Capability map — sistema_academico (this repo)

### 1. Academic structure (Curriculum) — `docs/business/curriculum/`
Definition of *what can be taught*: courses catalog, curricular plans per specialty, plan↔course
assignment with equivalences, prerequisites graph, thematic units (syllabus content), and
evaluation systems (component/weight structures used for grading).

### 2. Term planning & scheduling — `docs/business/schedules/`
Definition of *what is offered each semester*: course opening (*apertura de cursos*) per
specialty/plan/semester, sections with vacancy ceilings, shift (*turno*) definitions with
quantized 45-minute period slots, section timetables per weekday, schedule view per area, and
teacher availability windows.

### 3. Student management — `docs/business/students/`
Student registry (personal data, origin, previous schooling), specialty/plan assignment (entry vs
current), student type/state lifecycle (Estudiante / Retirado / Reserva…, Regular / Observado…),
photo service, and a per-student attendance calendar view.

### 4. Enrollment (Matrícula) — `docs/business/enrollment/`
The most critical operational process: eligibility search (type/state + financial gating),
regular enrollment against open sections (vacancy + schedule-conflict validation +
prerequisites), reservation, rectification (append-only correction), logical cancellation,
enrollment history, and debt detail consultation.

### 5. Grades & official records — `docs/business/grades/`
Grade entry per section against the section's evaluation system, official grade records (Actas):
Principal (publish-once, immutable), Sustitutoria (substitute-exam recalculation), and
Convalidación (transfer credit, stored-procedure-driven); web grade-entry locking; grade import;
report preview (actas/consolidados via JasperReports).

### 6. Financial administration (Cashier) — `docs/business/cashier/`
Payment concepts catalog, payment structures (define, publish → bulk-generates per-student tariff
rows), per-student commitment editing incl. prórrogas (grace-date extensions), bulk structure
editing, payment collection with voucher issuance (incl. carnet, late fees/mora, extemporaneous
enrollment), payment voiding, voucher printing (servlet), balances (Saldos), and bank-file
processing (export payment file / import bank response / reconcile / generate vouchers).
Financially **gates enrollment** (capability 4). Partially superseded in production by
`sigu_caja` (checkout), which carved out enrollment-period payments.

### 7. Free courses (Cursos Libres) — `docs/business/free-courses/`
A **parallel** curricular/enrollment/grading/payment model for complementary offerings (Office
tools, sports…): areas → modules → courses → workshops hierarchy, workshop opening with
evaluation system, sections & schedules, its own student registry (external students and
university affiliations), enrollment with payment verification, grades, and its own payment
structures.

### 8. Teacher management — `docs/business/teachers/`
Teacher registry, availability declaration, and teacher attendance tracking (class sessions,
attendance marks per session).

### 9. Platform administration & security — `docs/business/administration/`
Authentication (login with lockout), users (create/unlock), role-based menu authorization
(`tb_menu_rol` RBAC), reference data (semesters, faculties, specialties, locals, pavilions,
classrooms), institutional activities/announcements (published with image, consumed by the
intranet), and the home dashboard.

## Capabilities owned by other repos (documented here only as pointers)

| Capability | Repo | Relationship to this repo |
|---|---|---|
| Admissions (candidate → exam) | `sigu_admision` | Feeds candidates; qualifying ones become students here — see `docs/business/admission/` |
| Exam scoring | `wcali` | Processes admission-exam results; vacancy winners enter the student registry |
| Enrollment-period checkout | `sigu_caja` | Carved out of capability 6; writes payment data this repo reads for gating |
| Student portal | `sigu_intranet` | Reads students/grades/schedules; displays activities (capability 9) and surveys |
| Surveys | `test` | Popup in intranet at student login |
| Report collection | `sigu_reportes` | Reads shared schema for boletas/actas/consolidados |

## Cross-capability dependencies (the operational cycle)

A semester's operation follows this dependency chain — each capability must be configured before
the next can run:

1. **Curriculum** (plans, courses, prerequisites, evaluation systems) — mostly stable between terms.
2. **Administration** creates the **semester**; **Cashier** defines & publishes **payment structures** for it.
3. **Term planning** opens courses/sections and assigns schedules & teachers.
4. **Enrollment** runs (gated by cashier data; validates against term-planning data).
5. Teaching happens: **teacher attendance**, ongoing **grade entry**.
6. **Actas** are published (Principal → Sustitutoria); records become immutable.
7. Progression feeds the next cycle's enrollment eligibility (approved courses drive prerequisites).
