# Business documentation — functional knowledge base

Functional (business-analyst view) documentation for the Sistema Académico and its place in the
UCH suite. This is the long-term knowledge base built from **AS-IS inspection of the code**, not
from a target design: everything stated here reflects observed behavior or rules previously
verified through hands-on work. **Unknowns are marked as pending — nothing is invented.**

## What lives where (one home per fact)

| Surface | Owns | Audience |
|---|---|---|
| **`docs/business/`** (this tree) | Business capabilities, use cases, business rules, edge cases, gaps | Business analyst / product |
| `docs/modules/*.md` | Engineering view: design decisions, patterns, per-module tech notes | Developers |
| `README.md` → Technical Debt Backlog | Prioritized debt checklist | Developers |
| Root `CLAUDE.md` | Build/run/test, conventions, layering | Developers/AI |

When a business rule is already explained in a module guide, the use case links to it rather than
duplicating the full evidence trail.

## Structure

```
docs/business/
├── README.md                 ← you are here
├── functional-map.md         ← Deliverable 1: capability map of the whole suite
├── use-case-inventory.md     ← Deliverable 2: every identified use case, by module
├── gap-analysis.md           ← Deliverable 5: what we don't know yet (analysis backlog)
├── enrollment/               ← per-module functional docs (see layout below)
├── students/
├── grades/
├── schedules/
├── curriculum/
├── cashier/
├── admission/                ← cross-repo pointer (owned by sigu_admision)
├── free-courses/
├── teachers/
└── administration/
```

Each module folder contains:

| File | Content |
|---|---|
| `README.md` | Module purpose, actors, use-case list with status |
| `business-rules.md` | Confirmed business rules (with source), pending ones marked |
| `use-cases/UC-*.md` | Fully documented use cases (only where knowledge is verified) |
| `user-stories.md` | Placeholder — populated per future planning session |
| `edge-cases.md` | Known edge cases + placeholder for discovery |
| `future-enhancements.md` | Improvement candidates already surfaced |

## Use-case ID scheme

`UC-<MOD>-NNN` where MOD is: ENR (enrollment), STU (students), GRD (grades), SCH (schedules &
course offering), CUR (curriculum), CSH (cashier/payments), ADM (admission), FCL (free courses),
TCH (teachers), SEC (administration & security).

IDs are stable once assigned — do not renumber.

## Status vocabulary

- **Documented** — full use-case file exists, based on verified AS-IS behavior.
- **Identified** — named and scoped in the inventory; not yet documented.
- **Discovery needed** — behavior exists in code but rules are not yet understood/confirmed.

## Recommended analysis order

See [gap-analysis.md](gap-analysis.md#recommended-analysis-order) — cashier/payments first (highest
uncertainty × highest business criticality), then students lifecycle, schedules & offering,
curriculum, free courses, teachers/attendance, administration, and finally cross-repo modules.
