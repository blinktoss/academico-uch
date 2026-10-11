# Cashier / payments (Administrativa)

Financial administration: payment concepts, payment structures, per-student commitments and
prórrogas, payment collection with voucher issuance, balances, and bank-file processing. AS-IS
implementation: `net.uch.administrativa` (`MbConceptoPago`, `MbEstructuraPago`,
`MbEdicionEstructPago(+Bloque)`, `MbPago`, `MbSaldo`, `ComprobanteServlet`) and `net.uch.banco`
(`MbProcesoBanco`).

> **Highest-uncertainty module.** `MbPago` alone exposes ~40 actions with no prior functional
> documentation. In production, enrollment-period payments were carved out into `sigu_caja`
> (checkout) — the AS-IS boundary between the two apps must be confirmed before documenting
> collection flows. See [gap-analysis](../gap-analysis.md).

## Actors
- **Cashier** — collects payments, issues/annuls vouchers.
- **Financial administrator** — defines concepts/structures, grants prórrogas, runs bank files.
- **Enrollment subsystem** — consumer: debt gating reads tariff data this module writes.

## Use cases

| ID | Use case | Status |
|---|---|---|
| UC-CSH-001 | Manage payment concepts | Identified |
| UC-CSH-002 | Define & publish payment structure | Identified |
| UC-CSH-003 | Edit student commitments / grant prórroga | Discovery needed |
| UC-CSH-004 | Bulk-edit payment structures | Discovery needed |
| UC-CSH-005 | Collect payment & issue voucher | Discovery needed |
| UC-CSH-006 | Void / delete payment | Discovery needed |
| UC-CSH-007 | Print voucher & listings | **Documented** — [file](use-cases/UC-CSH-007-print-voucher-and-listings.md) |
| UC-CSH-008 | Consult balances (Saldos) | Identified |
| UC-CSH-009 | Bank-file processing | Discovery needed |
| UC-CSH-010 | Personalize a student's payment plan | Discovery needed |

## Known business rules (confirmed)

- **BR-CSH-01** — Publishing a payment structure **bulk-generates** one tariff row per
  (student × structure line) (`AdAlumnoTarifa`), independent of enrollment (see BR-ENR-06).
- **BR-CSH-02** — A tariff line is settled when `AlutarEstado = "2"`; otherwise it counts as debt
  once its grace date passes (consumed by enrollment gating, BR-ENR-04).
- **BR-CSH-03** — Voucher type codes (`ad_comprobantes_pago`): `001` = Efectivo/Saldo,
  `002` = Voucher-Comprobante, `003` = Vale/Boleta-Vale, `004` = Transferencia/Pago Banco
  (catalog group `015`).
- **BR-CSH-04** — Prórroga records (`AdProrroga`) act as an audit log; the effective gate is the
  tariff row's grace date. **Suspected gap:** no confirmed path updates that date when a Prórroga
  is granted (see enrollment edge-cases).
- **BR-CSH-05** — Special collectible items exist beyond tariffs: carnet, mora (late fee),
  matrícula extemporánea — rules pending discovery.
- **BR-CSH-06** — Voucher printing goes through `ComprobanteServlet` (JasperReports), split out of
  the managed bean (SRP refactor); listings likewise printable.
- **BR-CSH-07** — The `MbPago` collection screen's payment-type filter is a **payer-linkage
  retrieval switch** (*direct vs indirect* payment against a fee commitment) — **not** a concept- or
  customer-type filter, and deliberately **not** catalog-driven (issue #4, Decision A). Three fixed
  options: **INTERNOS** = `AdAlumnoTarifa` commitments (tariff-linked); **EXTERNOS** = `AdPago` rows
  with `AlutarId = 0` (unlinked, `HSPagoDAOImpl.seleccionarMostrarPagosExternos`); **CURSOS LIBRES** =
  `ClAlumnoTarifa` commitments (CL tariff-linked). The branches query different tables and are not
  reconciled into one model. Catalog group `024` formerly fed this combo and is now **orphaned by
  design**. See [UC-CSH-007](use-cases/UC-CSH-007-print-voucher-and-listings.md).
- **BR-CSH-08** — Voucher printing renders each payment line by **concept type**: `ConpagTipo =
  007001` (INTERNO) resolves the linked `AdAlumnoTarifa` and prints its state ("CANCELADO" if
  `AlutarEstado = "2"`, else "A CUENTA"); any other concept prints the concept description.
- **BR-CSH-09** — BR-CSH-08 assumes every `007001` payment has a resolvable `AlutarId`. Nothing
  *enforces* it server-side (no FK/trigger/check); the invariant holds today only at the **UI layer**
  (`MbPago.autocomplete` hides `007001` from manual/external entry). Per the issue-#4 investigation
  that suffices in practice — no current workflow produces new violations — so the defect is bounded
  to **71 legacy rows, all matrícula**, of unreconstructable origin. **Defect (issue #4):** printing
  one such row NPEs. **Resolution (Decision B):** the 71 rows are accepted as historical debt (no
  backfill/flagging); the crash is handled by the application's generic unexpected-error page — not a
  null-guard or data fix — and server-side write enforcement is deferred to the collection-flow work
  (UC-CSH-005). See [UC-CSH-007](use-cases/UC-CSH-007-print-voucher-and-listings.md) Decisions A/B.
- **BR-CSH-10** — *(unresolved contradiction)* `AdAlumnoTarifa` (student fee assignment) accepts
  **any** payment concept regardless of `ConpagTipo` — the structure/tariff builder applies no
  007-type filter (`seleccionarConceptoPago()` lists all active concepts). Yet part of the logic
  assumes the biconditional `007001 ⟺ tariff`. Both directions are violated in live data: `007001`
  **without** a tariff (71 legacy matrícula rows, BR-CSH-09) and `007002 GENERAL` **inside** tariffs
  (14,355 active rows across 3 concepts). A tariff is therefore, in practice, a *concept-agnostic fee
  composition*, not an internal-only construct. Resolution:
  [UC-CSH-007](use-cases/UC-CSH-007-print-voucher-and-listings.md) **Decision C**.

## Pending discovery (priority list for the analysis sprint)
1. `MbPago` collection flows: payment against tariff lines vs free concepts; external clients
   (`asignarNewExternalClient`); series/numbering (`cambiaSerieN`).
2. Void/annul semantics (`anularPago` vs `eliminarPago`) and their effect on debt state.
3. Prórroga grant path (closes/refutes BR-CSH-04's gap).
4. Bank-file format & reconciliation rules (`MbProcesoBanco`) — export, import, voucher generation.
5. Personalization (`personalizar`/`savePersonalization`) — per-student overrides of structures.
6. AS-IS boundary vs `sigu_caja`: which collection paths remain live in this app in production.

## Placeholders
- User stories / edge cases / future enhancements — pending the discovery pass above.
