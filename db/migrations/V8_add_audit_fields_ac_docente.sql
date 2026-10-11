-- Audit fields for ac_docente (created/updated tracking for teacher records).
--
-- No pre-existing creation column to repurpose, so all four columns are new.
-- NULL for pre-existing rows: the original actor for historical data is unknown.

ALTER TABLE ac_docente
    ADD COLUMN doc_creado_en       DATETIME NULL AFTER doc_activo,
    ADD COLUMN doc_creado_por      INT      NULL AFTER doc_creado_en,
    ADD COLUMN doc_actualizado_en  DATETIME NULL AFTER doc_creado_por,
    ADD COLUMN doc_actualizado_por INT      NULL AFTER doc_actualizado_en;
