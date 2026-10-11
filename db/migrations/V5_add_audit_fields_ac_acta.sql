-- Audit fields + lifecycle status for ac_acta (Acta Draft/Published immutability).
--
-- act_creacion already existed as a DATE column; widened to DATETIME and repurposed as the
-- audit "created at" timestamp (mirrors the ac_semestre precedent). created_by/updated_at/
-- updated_by are new and NULL for pre-existing rows, since the original actor for historical
-- data is unknown and there is no reliable way to backfill it.
--
-- act_status backfills existing rows as PUBLISHED via the column default: every Acta created
-- before this feature predates the Draft/Published distinction and should be treated as
-- already final.

ALTER TABLE ac_acta
    MODIFY COLUMN act_creacion DATETIME NULL;

ALTER TABLE ac_acta
    ADD COLUMN act_creado_por      INT      NULL AFTER act_creacion,
    ADD COLUMN act_actualizado_en  DATETIME NULL AFTER act_creado_por,
    ADD COLUMN act_actualizado_por INT      NULL AFTER act_actualizado_en;

ALTER TABLE ac_acta
    ADD COLUMN act_status VARCHAR(10) NOT NULL DEFAULT 'PUBLISHED' AFTER act_tipo;
