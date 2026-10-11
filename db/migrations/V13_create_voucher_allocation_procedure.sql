-- Companion to V12 (comprobante_correlativo): moves the voucher-number allocation idiom into
-- a stored procedure so the logic exists ONCE at the database - the only seam shared by
-- sistema_academico (Hibernate) and sigu_caja (raw JDBC) - instead of being duplicated in two
-- Java codebases that could drift apart.
--
-- Runs inside the CALLER's transaction (no transaction control here): the row lock taken by
-- the UPDATE serialises concurrent allocators, and the caller's rollback reverts the counter,
-- so numbers have no gaps and no duplicates.
--
-- Both apps' JDBC URLs use noAccessToProcedureBodies=true; CallableStatement calls work
-- (established pattern - sigu_admision already relies on stored procedures).
--
-- Run with the mysql CLI (DELIMITER is a client directive).

DROP PROCEDURE IF EXISTS sp_allocate_voucher_number;

DELIMITER $$

CREATE PROCEDURE sp_allocate_voucher_number(
    IN  p_serie    CHAR(3),
    IN  p_tip_doc  CHAR(6),
    OUT p_next_nro INT
)
BEGIN
    -- Common path: plain UPDATE on the seeded counter row - a single row lock,
    -- no gap/insert-intention locks, so concurrent allocators never deadlock.
    UPDATE comprobante_correlativo
       SET last_nro = LAST_INSERT_ID(last_nro + 1)
     WHERE serie = p_serie AND tip_doc = p_tip_doc;

    IF ROW_COUNT() = 0 THEN
        -- First voucher ever for this (serie, tip_doc): seed from the current max over
        -- active dash-format vouchers, excluding VALE (015004 - separate 8-digit scheme).
        -- The REGEXP guard keeps CAST away from legacy junk suffixes ("001-vale-...",
        -- "001-000064F") - a hard error 1292 under strict sql_mode, silent truncation
        -- otherwise. ON DUPLICATE covers a concurrent first-allocation.
        INSERT INTO comprobante_correlativo (serie, tip_doc, last_nro)
        SELECT p_serie, p_tip_doc,
               LAST_INSERT_ID(COALESCE(MAX(CAST(SUBSTRING(compag_nro, 5) AS UNSIGNED)), 0) + 1)
        FROM ad_comprobante_pago
        WHERE compag_activo = '1'
          AND compag_nro LIKE CONCAT(p_serie, '-%')
          AND SUBSTRING(compag_nro, 5) REGEXP '^[0-9]+$'
          AND compag_tip_doc = p_tip_doc
          AND compag_procedencia <> '015004'
        ON DUPLICATE KEY UPDATE last_nro = LAST_INSERT_ID(last_nro + 1);
    END IF;

    SET p_next_nro = LAST_INSERT_ID();
END$$

DELIMITER ;
