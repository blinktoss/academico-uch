-- Login brute-force lockout for tb_usuario.
--
-- Additive and backward-compatible (safe defaults), so the other UCH-stack apps
-- that share this database keep working unchanged.
--
-- usu_failed_attempts: consecutive failed login attempts; reset to 0 on success.
-- usu_locked:          '0' = active, '1' = locked after reaching the attempt
--                      threshold. Permanent lockout: an administrator clears it
--                      by setting usu_locked = '0' (which also resets the counter).
--
-- A dedicated usu_locked flag is used rather than overloading usu_vigente, which
-- means "enabled/current" — keeping "locked by brute force" distinct from
-- "deactivated".

ALTER TABLE tb_usuario
    ADD COLUMN usu_failed_attempts INT      NOT NULL DEFAULT 0   AFTER usu_psw,
    ADD COLUMN usu_locked          CHAR(1)  NOT NULL DEFAULT '0' AFTER usu_failed_attempts;
