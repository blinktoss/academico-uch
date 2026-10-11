# UC-GRD-003 — Publish Principal Acta

## Objective
Produce the official, immutable grade record (Acta Principal) for a section from the working
grades.

## Actors
Academic staff / registrar-authorized clerk.

## Preconditions
- All grades for the section registered (UC-GRD-002).
- No Principal Acta already published for the section (BR-GRD-08).

## Main flow
1. Clerk opens the Acta preview for the section. The system computes a DRAFT view from live
   grades: per-student component grades and the weighted, truncated final (BR-GRD-03/04); DRAFT is
   never persisted (BR-GRD-07).
2. Acta código/número are auto-generated and read-only (BR-GRD-10).
3. Clerk clicks **Publicar**; an explicit confirmation dialog warns that publication is final.
4. On confirm, the system — in a single transaction — re-checks "already published?", persists the
   Acta with all detail rows (official grade + raw grade + PASS/FAIL, BR-GRD-11), and assigns the
   número from the generated id.
5. The Acta is now PUBLISHED: reopening it renders the stored snapshot (BR-GRD-09).

## Alternative flows
- **A1 — Concurrent/duplicate publish:** the transactional guard rejects the second attempt with
  an "already published" alert; nothing is double-written.
- **A2 — Clerk cancels the confirmation dialog:** nothing happens.

## Business rules
BR-GRD-02, BR-GRD-03, BR-GRD-04, BR-GRD-06…BR-GRD-11.

## Edge cases
- Editing a student's working grade after publication does **not** change the published Acta and
  does not change what a later Sustitutoria starts from (it reads the stored snapshot).
- Publication with missing grades: behavior for null/absent component grades in the preview —
  verify explicitly (treated as 0 vs excluded) before relying on it.

## Open questions
- Formal actor split: in the target model, is publication a registrar-only privilege (vs the
  grade-entering clerk)? RBAC menu gating exists; per-action role policy not yet defined.

## Related GitHub issues
- Delivered by the acta-immutability project (all 6 phases); UI click-test still pending on the
  maintainer's desktop.
