# UC-GRD-004 — Publish Substitute-exam Acta (Sustitutoria)

## Objective
Give students an end-of-semester second chance: apply a substitute-exam score and produce the
official Sustitutoria Acta.

## Actors
Academic staff / registrar-authorized clerk.

## Preconditions
- The section's **Principal Acta is PUBLISHED** (BR-GRD-12).

## Main flow
1. Clerk opens the Sustitutoria modal for the section. Columns are built dynamically from the
   section's evaluation system (BR-GRD-05); original grades come from the published Principal's
   stored snapshot (BR-GRD-09).
2. Clerk enters each participating student's substitute score (0–20, half-point steps).
3. For each student, the system replaces their **single lowest original grade** with the
   substitute score — unconditionally, even if the substitute is lower (BR-GRD-13).
4. The course final is recomputed with the full evaluation weights and truncated (BR-GRD-14) —
   identical math to the Principal.
5. Clerk clicks Publicar; explicit confirmation dialog; on confirm the Sustitutoria Acta
   (`036002`) is published transactionally with the same guard and nomenclature
   (`{Sem}-{Sec}-S`).

## Alternative flows
- **A1 — Principal not published:** flow unavailable.
- **A2 — Already-published Sustitutoria:** transactional guard rejects a second publish.

## Business rules
BR-GRD-01…BR-GRD-04, BR-GRD-12, BR-GRD-13, BR-GRD-14.

## Edge cases
- Tie among lowest grades: deterministic tie-break in `SubstituteGradeCalculator` (unit-tested,
  incl. n=0/1/2, all-equal, all-lowest cases).
- A substitute score lower than the replaced grade **lowers** the final — this is by rule, not a
  bug (maintainer-confirmed practice).
- Legacy behavior (flat average + rounding) was wrong on both counts and has been replaced;
  historical Actas published under the old math remain as published (immutable).

## Open questions
- Who is eligible to take the substitute exam (all students? only failing ones?) — the AS-IS UI
  lets the clerk enter scores for any student; institutional eligibility policy not encoded.

## Related GitHub issues
- Delivered by the acta-immutability project.
