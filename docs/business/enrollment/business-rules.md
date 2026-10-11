# Enrollment — business rules (confirmed AS-IS)

All rules below are verified against code / hands-on investigation (2026-06). Source of evidence:
[`docs/modules/academica.md`](../../modules/academica.md) and
[`docs/Enrollment_Module_AS-IS_Report.md`](../../Enrollment_Module_AS-IS_Report.md).

## Eligibility

- **BR-ENR-01** — Only students with type *Estudiante* (`009001`) **and** state *Regular* are
  eligible for enrollment. Enforced at the query level (DB-side filter), not post-fetch.
- **BR-ENR-02** — Students flagged inactive (`AluActivo = '0'`) are never eligible.
- **BR-ENR-03** — States `OBSERVADO`/`CONAFU` exist in the catalog but have never been assigned in
  real data; the filter excludes them by construction (logically correct, empirically unexercised).

## Financial gating

- **BR-ENR-04** — Debt blocks enrollment, reservation, and rectification. A student is a debtor
  iff any active tariff row (`AdAlumnoTarifa`) is unpaid (`AlutarEstado != "2"`) **and** its grace
  date (`AlutarFechaProrroga`) has passed. Evaluated fresh on every search — debt is computed,
  never stored as a flag.
- **BR-ENR-05** — Debt is **binary per tariff line**; there is no partial-payment or
  amount-reconciliation concept in the gate.
- **BR-ENR-06** — Tariff rows are generated in bulk when a payment structure is published, one per
  (student × structure line), independent of the courses the student enrolls in. Enrollment never
  writes tariff data.

## Academic validation

- **BR-ENR-07** — **Prerequisites are enforced.** A course is offered to the student only if its
  full prerequisite chain is approved (grade ≥ 10.5 — note: this legacy threshold predates and is
  independent of the Acta passing grade of 11) or it has no prerequisites; courses already
  attempted are excluded; only courses opened for the term appear.
- **BR-ENR-08** — **Section capacity is a computed ceiling.** Vacancy = `SecVacantes` minus live
  count of active enrollments; enrollment into a full section is rejected at submission time.
  (Known race condition: check-then-insert is not atomic — see edge-cases.)
- **BR-ENR-09** — **Schedule conflict is a hard block.** Two sections conflict iff they share at
  least one (day, period-slot) pair — slot-identity matching over the institution's quantized
  45-minute grid. No "proceed anyway" path exists. Applies to fresh enrollment and rectification.
- **BR-ENR-10** — **No credit limit** and **no repeated-failure limit** exist (confirmed absent,
  not merely unverified). Open question for the product owner: is this institutional policy?

## Enrollment records

- **BR-ENR-11** — Three enrollment types share one table (`tb_catalogo` group `022`): Regular
  (`022001`), Reserva (`022002`), Pre-Matrícula (`022005`).
- **BR-ENR-12** — **Rectification is append-only.** It never edits the existing enrollment row: a
  new row is inserted with the corrected selection, and the superseded row is retagged
  Rectificada (`022004`/`022006`), preserving full history.
- **BR-ENR-13** — Enrollment rows are logically deleted (`MatActivo = '0'`), never physically.
- **BR-ENR-14** — Prórroga (grace extension) records (`AdProrroga`) are an audit log; the gate
  only reads the tariff row's grace date. (Linkage gap — see edge-cases.)

## Pending / unconfirmed rules

- Rules governing "ampliaciones" (UC-ENR-008) — modal exists (`openModalforAmpliaciones`),
  semantics not yet analyzed.
- Whether Reserva has a time/semester limit or affects vacancy counts — not yet confirmed.
