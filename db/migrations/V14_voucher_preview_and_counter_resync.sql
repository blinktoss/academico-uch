-- Companion to V12/V13 (voucher-number race fix). Two concerns:
--
-- 1) sp_preview_voucher_number: read-only twin of sp_allocate_voucher_number, so the
--    form-open "next number" PREVIEW also comes from the shared counter. Until now each
--    app kept its own legacy max-query for the preview (academico: string max keyed by
--    serie only; caja: numeric max over a procedencia whitelist of active rows), so the
--    two apps displayed different numbers for the same (serie, tip_doc) — and neither
--    matched what the allocator would actually stamp. The preview stays NON-BINDING:
--    it takes no lock and advances nothing; a concurrent commit may take the shown
--    number, and the commit-time allocation remains the only authoritative stamp.
--
-- 2) Counter re-sync: bump each counter to the max over active dash-format vouchers
--    (all procedencias except VALE), because the banco import flow (MbProcesoBanco)
--    hand-numbered its batch inserts from a user-typed start number without advancing
--    the counter, leaving active rows AHEAD of it (e.g. 004/039001 rows 523..529 vs
--    counter 522) — numbers the allocator would eventually re-issue as duplicates.
--    Unlike the V12 seed, COALESCE keeps NULL-procedencia rows in the bound (V12's
--    `<> '015004'` silently dropped them). Idempotent and re-runnable: GREATEST never
--    lowers a counter, and annulled rows (compag_activo='0') stay out of the bound but
--    their numbers stay consumed because allocation never rolls the counter back.
--
-- Run with the mysql CLI (DELIMITER is a client directive).

DROP PROCEDURE IF EXISTS sp_preview_voucher_number;

DELIMITER $$

CREATE PROCEDURE sp_preview_voucher_number(
    IN  p_serie    CHAR(3),
    IN  p_tip_doc  CHAR(6),
    OUT p_next_nro INT
)
BEGIN
    -- Common path: the number the allocator will issue next. Plain read, no lock.
    SET p_next_nro = (SELECT last_nro + 1
                        FROM comprobante_correlativo
                       WHERE serie = p_serie AND tip_doc = p_tip_doc);

    IF p_next_nro IS NULL THEN
        -- No counter row yet for this (serie, tip_doc): mirror the allocator's
        -- first-allocation seed (V13) exactly, so the preview agrees with what the
        -- first allocation will stamp. VALE (015004) excluded — separate 8-digit scheme;
        -- REGEXP keeps CAST away from legacy junk suffixes (error 1292 on strict sql_mode).
        SELECT COALESCE(MAX(CAST(SUBSTRING(compag_nro, 5) AS UNSIGNED)), 0) + 1
          INTO p_next_nro
        FROM ad_comprobante_pago
        WHERE compag_activo = '1'
          AND compag_nro LIKE CONCAT(p_serie, '-%')
          AND SUBSTRING(compag_nro, 5) REGEXP '^[0-9]+$'
          AND compag_tip_doc = p_tip_doc
          AND compag_procedencia <> '015004';
    END IF;
END$$

DELIMITER ;

-- Re-sync counters past any active dash-format voucher written outside the allocator.
-- REGEXP guard: same strict-sql_mode protection as the V12 seed.
INSERT INTO comprobante_correlativo (serie, tip_doc, last_nro)
SELECT SUBSTRING(compag_nro, 1, 3)                     AS serie,
       compag_tip_doc                                  AS tip_doc,
       MAX(CAST(SUBSTRING(compag_nro, 5) AS UNSIGNED)) AS last_nro
FROM ad_comprobante_pago
WHERE compag_activo = '1'
  AND compag_nro LIKE '___-%'
  AND SUBSTRING(compag_nro, 5) REGEXP '^[0-9]+$'
  AND compag_tip_doc IS NOT NULL
  AND compag_tip_doc <> ''
  AND COALESCE(compag_procedencia, '') <> '015004'
GROUP BY SUBSTRING(compag_nro, 1, 3), compag_tip_doc
ON DUPLICATE KEY UPDATE last_nro = GREATEST(last_nro, VALUES(last_nro));
