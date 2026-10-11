-- Tracks the last successful login per user.
--
-- Additive and backward-compatible (nullable, no default needed), so the other
-- UCH-stack apps that share this database keep working unchanged.
--
-- usu_last_login: timestamp of the most recent successful login; NULL until the
--                 user has ever logged in.

ALTER TABLE tb_usuario
    ADD COLUMN usu_last_login DATETIME NULL AFTER usu_locked;
