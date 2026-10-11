# Enrollment — future enhancements (candidates already surfaced)

- Close the **last-seat race condition** (DB-level constraint or pessimistic lock).
- Resolve the **Prórroga linkage gap** (grant must update the tariff grace date the gate reads).
- Product decision on **credit limit / repeated-failure limit** (currently absent by AS-IS).
- Explicit **enrollment state machine** (`Lifecycle<S>` ADR) replacing implicit type/state
  combinations — 4-phase plan already drafted (see state-machine planning retrospective).
- Open GitHub issues #33–#35 (edge cases from the service-migration sprint).
