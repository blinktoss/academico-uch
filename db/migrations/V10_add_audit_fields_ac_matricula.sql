-- Audit fields for ac_matricula (created/updated tracking for enrollment records).
--
-- mat_fecha already exists but is a business field (the enrollment's effective date, set by the
-- bean and shown on printed forms) - not the same concept as a system audit timestamp, so it is
-- left untouched and all four audit columns are new. NULL for pre-existing rows: the original
-- actor for historical data is unknown.

ALTER TABLE ac_matricula
    ADD COLUMN mat_creado_en       DATETIME NULL AFTER usu_id,
    ADD COLUMN mat_creado_por      INT      NULL AFTER mat_creado_en,
    ADD COLUMN mat_actualizado_en  DATETIME NULL AFTER mat_creado_por,
    ADD COLUMN mat_actualizado_por INT      NULL AFTER mat_actualizado_en;
