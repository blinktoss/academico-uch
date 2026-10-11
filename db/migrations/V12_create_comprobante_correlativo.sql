-- Voucher-number race fix (issue: compag_nro allocated at form-open, reused at commit ->
-- concurrent forms in sistema_academico and sigu_caja draw the same max+1 and both insert,
-- producing duplicate compag_nro. No unique constraint catches it; ~hundreds already exist).
--
-- Fix strategy B1: a per-(serie, tip_doc) atomic sequence. Both apps allocate the authoritative
-- number from this table INSIDE their insert transaction (row lock serializes concurrent
-- allocators; rollback reverts the counter -> no duplicates, no gaps). The form-open number
-- becomes a non-binding preview.
--
-- Canonical key = (serie, tip_doc), matching sigu_caja's model and Peru fiscal numbering.
-- Additive + backward-compatible: new table only, no change to ad_comprobante_pago.

CREATE TABLE IF NOT EXISTS comprobante_correlativo (
    serie    CHAR(3)  NOT NULL,
    tip_doc  CHAR(6)  NOT NULL,
    last_nro INT      NOT NULL DEFAULT 0,
    PRIMARY KEY (serie, tip_doc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Eager-seed from the current maximum per (serie, tip_doc) over active, dash-formatted
-- vouchers (SSS-NNNNNNN). Matches the population both apps' max queries already use:
--   * dash format only ("0000507", "SAL...", "VAL..." excluded so they never seed a bogus
--     counter; combos with no dash-format history self-seed on first allocation);
--   * NUMERIC suffix only — legacy data holds ~1,136 dash rows with junk suffixes
--     ("001-vale-08010217", "001-000064F", "...-0000071F"). Under a non-strict sql_mode
--     CAST silently truncates them; under STRICT_TRANS_TABLES (MySQL 5.7+/8 default) the
--     same CAST is a hard error 1292 that aborts this seed. The REGEXP guard keeps the
--     CAST from ever seeing them, so the script behaves identically on any sql_mode;
--   * procedencia 015004 (VALE) EXCLUDED — both apps exclude it (academico: != '015004';
--     caja: procedencia whitelist), and VALE uses a separate 8-digit scheme that would
--     otherwise poison the counter into the millions.
-- Numeric MAX is a safe lower bound for the next number (never reissues an existing one).
-- Idempotent: re-running keeps the larger value.
INSERT INTO comprobante_correlativo (serie, tip_doc, last_nro)
SELECT SUBSTRING(compag_nro, 1, 3)                         AS serie,
       compag_tip_doc                                      AS tip_doc,
       MAX(CAST(SUBSTRING(compag_nro, 5) AS UNSIGNED))     AS last_nro
FROM ad_comprobante_pago
WHERE compag_activo = '1'
  AND compag_nro LIKE '___-%'
  AND SUBSTRING(compag_nro, 5) REGEXP '^[0-9]+$'
  AND compag_tip_doc IS NOT NULL
  AND compag_tip_doc <> ''
  AND compag_procedencia <> '015004'
GROUP BY SUBSTRING(compag_nro, 1, 3), compag_tip_doc
ON DUPLICATE KEY UPDATE last_nro = GREATEST(last_nro, VALUES(last_nro));
