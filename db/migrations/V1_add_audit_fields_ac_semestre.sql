-- Audit fields for ac_semestre (first core entity to get created/updated tracking).
--
-- sem_creacion already existed as an undocumented, unused DATE column; it is widened to
-- DATETIME and repurposed as the audit "created at" timestamp instead of adding a duplicate
-- column. Existing rows keep their current date (time defaults to 00:00:00).
--
-- created_by/updated_at/updated_by are new and NULL for pre-existing rows, since the
-- original actor for historical data is unknown and there is no reliable way to backfill it.

ALTER TABLE ac_semestre
    MODIFY COLUMN sem_creacion DATETIME NULL;

ALTER TABLE ac_semestre
    ADD COLUMN sem_creado_por      INT      NULL AFTER sem_creacion,
    ADD COLUMN sem_actualizado_en  DATETIME NULL AFTER sem_creado_por,
    ADD COLUMN sem_actualizado_por INT      NULL AFTER sem_actualizado_en;
