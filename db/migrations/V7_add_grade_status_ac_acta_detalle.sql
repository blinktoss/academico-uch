-- Grade-policy storage for ac_acta_detalle (auditability of published grade records).
--
-- Per the Grade Policy Rules, a published record's final detail should keep both the raw
-- (decimal) calculated grade and the PASS/FAIL status alongside the official truncated grade
-- (actdet_nota). These are additive, nullable, and only populated for the PROMEDIO_FINAL detail
-- going forward; component/legacy rows keep them NULL, so existing data and the other UCH-stack
-- apps reading this shared table are unaffected.
--
-- actdet_raw_grade: raw weighted result before truncation (e.g. 12.857142).
-- actdet_status:    PASS / FAIL, derived from the truncated final vs the passing grade (11).

ALTER TABLE ac_acta_detalle
    ADD COLUMN actdet_raw_grade DECIMAL(5,2) NULL AFTER actdet_nota,
    ADD COLUMN actdet_status    VARCHAR(4)   NULL AFTER actdet_raw_grade;
