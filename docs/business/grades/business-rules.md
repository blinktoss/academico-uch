# Grades & Actas — business rules (confirmed AS-IS)

Verified through hands-on work 2026-06 (acta-immutability project). Evidence:
[`docs/modules/academica.md`](../../modules/academica.md#grades--actas).

## Grading policy (official, decided 2026-06)

- **BR-GRD-01** — Scale 0–20; **half-point increments only** (grade × 2 must be whole). Enforced
  client-side (UX) *and* server-side (real guard).
- **BR-GRD-02** — **Passing grade: 11** (≥ 11 pass, < 11 fail). UI red-text convention uses the
  same threshold.
- **BR-GRD-03** — **Final grade is truncated, never rounded** (12.9 → 12). Intermediate weighted
  math keeps full precision; truncation happens exactly once, at the end.
- **BR-GRD-04** — **Weighting is two-level**: components averaged within their group by component
  weight, then groups combined by group weight. One calculator produces both Principal and
  Substitute finals so they can never drift.
- **BR-GRD-05** — **Evaluation systems are data-driven** — a section's grade columns come entirely
  from its configured evaluation system; adding a new system requires zero code change.

## Actas (official records)

- **BR-GRD-06** — Acta types (catalog group `036`): Principal `036001`, Sustitutoria `036002`,
  Convalidación `036003`.
- **BR-GRD-07** — Lifecycle `DRAFT → PUBLISHED`. DRAFT is a *derived* preview recomputed from live
  grades, never persisted; only PUBLISHED rows exist in the table.
- **BR-GRD-08** — **Publishing is deliberate and final** — guarded by an explicit confirm dialog;
  once published, a Principal Acta cannot be modified. Corrections happen only through the
  Sustitutoria.
- **BR-GRD-09** — Published Actas are **frozen snapshots**: reopening renders the stored grades,
  not a recompute — later edits to working grades can never silently change the official record.
- **BR-GRD-10** — Acta identity is auto-generated: código `{Semester}-{Section}-{P|S}`, número
  assigned from the DB id on first save; both read-only in the UI.
- **BR-GRD-11** — Published detail rows keep an audit trail: official truncated grade, raw
  2-decimal grade, and PASS/FAIL status.

## Substitute exam (Sustitutoria)

- **BR-GRD-12** — Requires the Principal Acta to be **published first**.
- **BR-GRD-13** — The substitute score **unconditionally replaces the student's single lowest
  original grade** — regardless of component weight, and even if the new score is lower (common
  Peruvian practice, maintainer-confirmed).
- **BR-GRD-14** — The course final is then recomputed with the full evaluation weights and
  truncated — the same calculation as the Principal, not a flat average.

## Convalidación (transfer credit)

- **BR-GRD-15** — Separate track for transfer students; only courses the stored procedure
  `sp_convalidacion_cursos` deems equivalent are eligible.
- **BR-GRD-16** — Its Acta uses type `036003`, never shares a row with a Principal, and is created
  already PUBLISHED.

## Pending / unconfirmed
- Web grade-lock rules (UC-GRD-006): who locks, when, scope (section? term?).
- Grade import (UC-GRD-007): source format, validation, overwrite semantics.
