-- Audit fields for ac_alumno (created/updated tracking for student records).
--
-- No pre-existing creation column to repurpose, so all four columns are new.
-- NULL for pre-existing rows: the original actor for historical data is unknown.

ALTER TABLE ac_alumno
    ADD COLUMN alu_creado_en       DATETIME NULL AFTER alu_monto_pago,
    ADD COLUMN alu_creado_por      INT      NULL AFTER alu_creado_en,
    ADD COLUMN alu_actualizado_en  DATETIME NULL AFTER alu_creado_por,
    ADD COLUMN alu_actualizado_por INT      NULL AFTER alu_actualizado_en;
