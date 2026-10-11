# Retrospective & Tech Planning — Enrollment Cycle Close-out + State Machine Foundation

> Session date: 2026-06-28. Branch context: enrollment modernization (PRs #25, #26, #39) just merged to `main`. This document captures the retrospective discussion and the resulting decision to close the state-machine analysis before resuming edge-case work, so the reasoning survives past this session.

---

## 1. Retrospective — Enrollment Modernization Cycle (PRs #25, #26, #39)

**What shipped:** service-layer extraction (`EnrollmentService`), N+1 fix, audit fields, 4 Quick Wins, a fail-fast validation pipeline (Sprint 2), and two evidence-based state assessments:
- [`docs/Student_Enrollment_State_Analysis.md`](../Student_Enrollment_State_Analysis.md)
- [`docs/System_State_Management_Discovery.md`](../System_State_Management_Discovery.md)

**The symptom that triggered this planning session already happened twice inside this same cycle** — recorded here as the evidence behind the decision in §2, not just an instinct:

1. Sprint 2's prerequisite re-check (`cursosNoPermitidos`) initially only checked `findRegularEnrollment`. It missed that `matricular()` *also* confirms Pre-Matrícula into Regular — a write path that exists only because `AcMatricula` has no single authority over "what counts as already-enrolled." Fixing it surfaced a second, unrelated bug (`seleccionarPrematricula` missing eager-fetch). Two patches to land one rule, because the enrollment state model has no single source of truth.
2. The discovery work itself found `AcMatriculaCurso.matcurEstado` — a column and Hibernate mapping for per-course withdrawal that exists in schema, is written once as `null`, and read nowhere. Someone already tried to model this state and gave up mid-build. That's not a missing feature, that's a previous loop that didn't close.

Both are the same root cause: `MatTipo`, `ActStatus`, `AlutarEstado`, `semActual`, `matcurEstado` are all read/written via scattered `.equals()` calls with no single owner, no transition table, and no guard. Every new business rule (Sprint 2's eligibility/debt/prerequisite checks, the next edge-case batch) has to re-derive "what's a valid state, what's a valid transition" from scratch, in whichever bean happens to need it that week.

## 2. Decision: close the state model first, before edge cases

Validated against two SOLID violations already named in the discovery doc:
- **Single Responsibility** — no module owns "what state can this entity be in"; that knowledge is duplicated across `MbMatricula`, `MbConvalidacion`, `MbAsistenciaDocente`, etc.
- **Open/Closed** — every new rule (a new edge case) currently means editing the entity's consuming bean directly, not extending a closed transition contract. Edge-case work will keep reopening `MbMatricula`/`MbNota`/`MbConvalidacion` until that's fixed.

Discovery is done (both docs above). What's **not** done is the decision layer — turning "here's what's implicit" into one governed contract every module conforms to. **Decision: close that gap before resuming edge-case work**, otherwise each edge case becomes its own one-off guard clause, repeating the two examples in §1.

## 3. Close-out plan (agreed sequencing)

| Phase | Deliverable | Exit criterion |
|---|---|---|
| **1. ADR / design decision** | Formalize the `Lifecycle<S>` contract sketched in §3 of the discovery doc: enum of states, `Map<S,Set<S>>` transition table, `transition(current,target)` guard, one state-owning service per entity. Decide where it lives (`net.uch.common.state`?) and the convention for wiring it into the existing service-layer pattern (`EnrollmentService`/`GradeRecordService`). | Written ADR, reviewed, no code yet. |
| **2. Pilot — Free Courses** | Implement `Lifecycle<S>` end-to-end on `ClMatricula`/`ClNota` (smallest blast radius, validates the pattern is cheap and adds no DB round-trips). | Pilot merged, pattern proven, documented in `docs/modules/` the way `GradeRecordService` already is. |
| **3. Apply to the 3 confirmed live bugs surfaced by discovery** (not new design — running the pilot's pattern over already-found gaps): Convalidación's missing publish guard, the Prórroga/`AlutarFechaProrroga` disconnect ([#33](https://github.com/cesardl/sistema_academico-uch/issues/33)), the duplicated debt predicate in `MbMatricula`. | All three fixed using the same contract, not three more bespoke patches. |
| **4. Resume edge-case work** | Every edge case that touches student/enrollment/grade state extends an existing transition table instead of adding another scattered `.equals()` check. | — |

**Explicitly out of scope for this close-out** (real, but net-new feature work, not foundation):
- The dormant `matcurEstado` (per-course withdrawal) — becomes an easy *addition* to the contract once it exists; building it now would expand Phase 3 into new functionality instead of closing the model.
- The unguarded attendance state (catalog group 046) — same reasoning, deferred to whenever attendance is next touched.

## 4. Status at end of session

Plan agreed; ADR (Phase 1) not yet written. César has other smaller items in mind before resuming the state-machine track — this document is the checkpoint to resume from when that's ready.

## Open items carried forward (not yet GitHub issues — see [[feedback-issue-granularity]] in memory: don't issue-ify until something concrete is being acted on)

- Phase 1 ADR — pending.
- Phase 2 pilot scope (Free Courses) — pending, blocked on Phase 1.
- Phase 3 bug fixes — already tracked individually (#33 exists; Convalidación guard gap and the duplicated debt predicate are documented in `System_State_Management_Discovery.md` §2 and `project_enrollment_module` memory, not yet issues).
