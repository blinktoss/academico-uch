# UC-CSH-007 — Print voucher & payment listings

## Objective
Reprint an issued payment voucher (comprobante) and print the payment listings produced by the
collection screen's search. Rendering runs through `ComprobanteServlet` (JasperReports/HTML),
separate from the `MbPago` managed bean (SRP split, BR-CSH-06).

## Actors
Cashier (reprints vouchers, prints listings).

## Preconditions
- The payment(s) exist in `AdComprobantePago` / `AdPago` (issued during collection, UC-CSH-005).
- For a voucher reprint, a comprobante is in context (selected from a search result).

## Main flow — reprint a voucher
1. Cashier locates the payment via the collection-screen search (see *Retrieval* below) and triggers
   print (`MbPago.imprimirComprobante`).
2. `ComprobanteServlet` loads the comprobante's `AdPago` lines and builds the voucher, one line per
   payment.
3. Per line, the servlet branches on the payment **concept type** (`ConpagTipo`) — BR-CSH-08:
   - `007001` (INTERNO): resolve the linked tariff (`AdAlumnoTarifa` by `AlutarId`) and print its
     payment-state label — "CANCELADO" if `AlutarEstado = "2"`, otherwise "A CUENTA".
   - any other concept: print the concept description (`ConpagDescripcion`).
4. The assembled voucher is returned for display / print.

## Retrieval that feeds print & listings — the Internal/External toggle
The collection screen (`MbPago.seleccionar`) retrieves rows through a UI toggle that drives two
**unrelated** queries (BR-CSH-07):

| Toggle | Method | Actually filters on |
|---|---|---|
| **"Internal"** | `performInternalSearch` → `HSAlumnoTarifaDAO.seleccionarAlumnoTarifa` | `AdAlumnoTarifa` (tariff commitments) by student / faculty / specialty / semester |
| **"External"** | `performExternalSearch` → `HSPagoDAOImpl.seleccionarMostrarPagosExternos` | `AdComprobantePago`/`AdPago` where `pag.AlutarId = 0` (payment *not linked to a tariff*) |

The two branches read different tables and are never reconciled into one model.

> **As built (Decision A):** a three-way *payer-linkage* filter — INTERNOS (Ad tariff-linked) /
> EXTERNOS (unlinked, `AlutarId = 0`) / CURSOS LIBRES (CL tariff-linked) — decoupled from catalog
> group 024 (fixed UI options). Retrieval runs through `PaymentSearchService` with server-side
> pagination, and the concept filter is type-dependent (`007003` only for CURSOS LIBRES). Delivered
> on branch `claude/issue-4-architecture-strategy-4jrqvg`.
>
> The CURSOS LIBRES listing is **consult-only**: CL rows are tariffs, so neither the INTERNOS row
> actions (pagar/carnet/mora — Ad-only flows whose payer/tariff writes assume `ac_alumno` /
> `ad_alumno_tarifa` id spaces) nor the EXTERNOS voucher actions (edit/void/delete/print — empty
> voucher state on a tariff row) are rendered. CL **collection** is designed in UC-CSH-005, where
> the issue #49 payer/beneficiary semantics get resolved.

## Business rules
BR-CSH-06 (print via `ComprobanteServlet`), **BR-CSH-07** (Internal/External retrieval semantics),
**BR-CSH-08** (voucher concept-type branch), **BR-CSH-09** (the *unenforced* `007001 ⟹ resolvable
AlutarId` assumption). Definitions live in the
[cashier README](../README.md#known-business-rules-confirmed).

## Edge cases
- **NPE reprinting a `007001` voucher with `AlutarId = 0` — issue #4.**
  [`ComprobanteServlet.java:153`](../../../../src/main/java/net/uch/administrativa/web/ComprobanteServlet.java)
  calls `daoAlumnoTarifa.seleccionarUnAlumnoTarifa(pago.getAlutarId())` — a Hibernate `get()` that
  returns **null** for the non-existent id `0` — then dereferences `.getAlutarEstado()`, throwing an
  NPE. Trigger: any payment carrying concept `007001` but written with `AlutarId = 0` (BR-CSH-09).
  The crash is the *symptom*; the root cause is that BR-CSH-08's assumption is enforced in this one
  reader and by nothing that writes.
- The servlet resolves the same tariff **twice** per line (once to test state, once to render) — a
  redundant DB round-trip; noted for the eventual print rework, not part of issue #4.
- **`compag_alu` misresolution on CL vouchers — issue #49 interaction.** `compag_alu` (the voucher's
  *beneficiary* student) is written **untyped** by this app (`compag_alu_tipo` stays NULL; the only
  producer is `MbPago.insertarNuevo`, which stores whatever id the customer search picked — AC *or*
  CL). The EXTERNOS listing used to resolve it blindly against `ac_alumno`, so a Curso Libre
  self-payment (payer type `014003`, `compag_alu` = `cl_alumno` id) displayed an **unrelated AC
  student** on id collision (dev DB example: voucher 10812 — CL student FLORES rendered as AC student
  REMIGIO). Fixed on the issue #4 branch: the reader skips the AC lookup when `compag_cliente_tipo =
  014003` and falls through to the payer switch, which resolves the CL student (self-pay ⇒ same id).
  The **write side** (typing `compag_alu`, Options A–D) remains an open decision in issue #49.

## Data state — issue #4 investigation (verified)
A production cross-check (`AdAlumnoTarifa` × payment concept × concept type) found **71 legacy records**
in the `007001`-without-`AlutarId` state, **all of them enrollment (matrícula) concepts**. No current
application workflow reproduces it: internal academic payments originate from `AdAlumnoTarifa`, manual
registration does not permit selecting `007001` (BR-CSH-09), and external customers can only register
non-internal concepts. The 71 rows' origin cannot be reconstructed from source, DB, or app behaviour —
they are **bounded legacy inconsistency, not an active leak**. This answers the issue's open questions:
the records are legacy data (not a valid business case), and the search filter is neither concept type
nor customer type (BR-CSH-07).

## Decisions (issue #4 fix tracks — A and B RESOLVED 2026-07-17, C open)

**Decision A — Domain scope. ✅ RESOLVED.** "Internal/External" overloaded three independently-set
concepts: (1) payment **concept type** — catalog 007 (`007001` INTERNO / `007002` GENERAL / `007003`
CURSO LIBRE, a property of *what* is paid for); (2) **customer type** — the polymorphic payer
reference on the comprobante (`compag_cliente_tipo` + `compag_cliente` → `ac_alumno` / `ad_clientes`
/ `cl_alumno`, a property of *who* pays); (3) the **search filter** (BR-CSH-07).

*Resolution (PO):* the search filter's dimension is **payer linkage** — whether the payment is made
**directly** against a fee commitment (a tariff row) or **indirectly** (unlinked) — a dimension of
its own, distinct from concept type and customer type. Consequences:
- The filter is **deliberately not catalog-driven**: three fixed UI options — `1 INTERNOS` (Ad
  tariff-linked), `2 EXTERNOS` (unlinked, `AlutarId = 0`), `3 CURSOS LIBRES` (CL tariff-linked, new
  branch). Catalog group `024` (which fed the old combo) becomes **orphaned by design** — the
  catalog speaks concept-type language and must not be conflated with this filter again.
- The **concept filter becomes dependent on the selected type**, aligned to catalog 007: CURSOS
  LIBRES → only `007003` concepts; INTERNOS/EXTERNOS → all except `007003`.
- Historical note kept for the record: catalog 007 classified *payment concepts* when the module was
  built (~2007–08); Curso Libre (~2010) overloaded "External" onto the *payer*, and the senses
  drifted until this resolution.

**Decision B — Ingest integrity. ✅ RESOLVED.** The concept↔`AlutarId` invariant is upheld at the UI
layer (`MbPago.autocomplete` hides `007001` from manual/external entry; the `AlutarId = 0` write
sites are never reached with an internal concept in practice — verified, no active producer).

*Resolution (PO):*
- The **71 legacy matrícula rows** are accepted as **historical debt** — origin unreconstructable
  from source, DB, or app behavior; no backfill, no flagging, no data migration.
- The print-time NPE they cause is **not fixed at the data or print layer**; instead, unhandled
  exceptions now land on the application's **generic unexpected-error page** (replacing the old
  redirect-to-Login behavior), telling the user to contact Systems.
- Prevention is strengthened at the UI layer via the type-dependent concept filter (Decision A).
  Full **server-side write validation is deferred** — it belongs with the eventual collection-flow
  service work (UC-CSH-005), not this search-side change.

**Decision C — `AlumnoTarifa` ↔ concept-type relationship (BR-CSH-10).** The data model lets a tariff
hold any concept, yet part of the logic assumes a tariff is always `007001`. Evidence (dev DB): ~20%
of active tariff rows — **14,355** — carry a `007002 GENERAL` concept, apparently by intentional
composition (three concepts bulk-added at identical counts of 4,785), while the 71 issue-#4 rows are
`007001` *without* a tariff. The assumed biconditional `007001 ⟺ tariff` holds in **neither**
direction. *Decide:*
- **(a) Restrict** `AdAlumnoTarifa` to `007001` concepts — makes the code's assumption true, but
  contradicts 14k+ existing GENERAL rows (a data migration + rule change with shared-DB blast
  radius); or
- **(b) Decouple** the logic from concept type — treat a tariff as a concept-agnostic fee commitment
  and stop inferring concept type from tariff existence (and vice-versa). **The data leans strongly
  toward (b).**

C remains **open** as a data-model decision, independent of the A/B resolution above (the error page
and the payer-linkage filter hold under either outcome). It resurfaces when collection-flow write
rules are designed (UC-CSH-005): under (b), the print branch (BR-CSH-08) would stop keying on
`007001` altogether.

> **Issue #4 resolution (2026-07-17):** the crash is handled by the application's **generic
> unexpected-error page** (Technical Req. 1), *not* a targeted null-guard — the 71 legacy rows are
> accepted as historical debt (Decision B). The forward-looking work — payer-linkage filter with a
> CURSOS LIBRES branch, type-dependent concept filter, and a `PaymentSearchService` with server-side
> pagination — lands on branch `claude/issue-4-architecture-strategy-4jrqvg`.

## Related GitHub issues
- #4 — NullPointer printing voucher for external payment and internal concept (root cause: BR-CSH-09).
- #49 — `compag_alu` inconsistently populated (voucher↔student beneficiary link). The listing reader
  is now type-aware (see Edge cases); the write-side decision (Options A–D) remains open there.
  Full model: `uch/docs/AS_IS_Comprobante_Customer_Student_Model.md`.

## Related gaps
- Chips at **G-09** (payment collection flows, void semantics, series/numbering): this doc covers the
  print + listing-retrieval slice. Collection (UC-CSH-005), void (UC-CSH-006), and series/numbering
  remain *discovery needed*.
