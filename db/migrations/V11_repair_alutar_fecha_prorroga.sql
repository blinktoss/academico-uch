-- BUG-04 data repair (issue #45): prórrogas granted through the modal were recorded in
-- ad_prorroga (history) but never propagated to ad_alumno_tarifa.alutar_fecha_prorroga,
-- the denormalized effective deadline read by the enrollment debt gate and by sigu_caja's
-- mora calculation. At the time of writing, 1,199 of 1,618 active prórrogas were newer
-- than the column value.
--
-- Rule (same as the code fix in MbEdicionEstructPago.syncEffectiveDeadline):
-- the effective deadline is the latest active prórroga date for the tariff.
-- Rows whose extensions were all logically deleted keep their current value
-- (already the due date or a manually maintained deadline).
--
-- Backward-compatible: data-only, no schema change. Idempotent: re-running it
-- matches zero rows once deadlines are in sync.

UPDATE ad_alumno_tarifa tarifa
JOIN (
    SELECT alutar_id, MAX(pro_fecha) AS max_pro_fecha
    FROM ad_prorroga
    WHERE pro_activo = '1'
    GROUP BY alutar_id
) pro ON pro.alutar_id = tarifa.alutar_id
SET tarifa.alutar_fecha_prorroga = pro.max_pro_fecha
WHERE tarifa.alutar_fecha_prorroga IS NULL
   OR pro.max_pro_fecha > tarifa.alutar_fecha_prorroga;
