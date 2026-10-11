-- V15 (issue #33): ad_prorroga.pro_numero is the per-tariff payment-extension sequence number. It
-- stores a numeric value and must not be capped at a single digit (it was CHAR(1)). Change it to
-- SMALLINT, then re-align the denormalized effective deadline.
--
-- This lives in a Vxx migration (not db_sis_academica_setup.sql) because it is a true schema
-- evolution bundled with its dependent data sanity update. Backward-compatible and idempotent:
-- re-running matches zero rows once the deadlines are in sync.

ALTER TABLE ad_prorroga MODIFY pro_numero SMALLINT;

-- Re-align the denormalized effective deadline (ad_alumno_tarifa.alutar_fecha_prorroga) to the
-- LATEST-REGISTERED active extension, identified by the highest pro_numero (registration order)
-- rather than the maximum date - the sequence number is the true business intent (V11 used
-- max(pro_fecha), which is only coincidentally the same). Tie-break by pro_id. Only tariffs that
-- have an active extension are touched.
UPDATE ad_alumno_tarifa t
SET t.alutar_fecha_prorroga = (
    SELECT p.pro_fecha
    FROM ad_prorroga p
    WHERE p.alutar_id = t.alutar_id
      AND p.pro_activo = '1'
    ORDER BY p.pro_numero DESC, p.pro_id DESC
    LIMIT 1
)
WHERE EXISTS (
    SELECT 1 FROM ad_prorroga p2
    WHERE p2.alutar_id = t.alutar_id
      AND p2.pro_activo = '1'
);
