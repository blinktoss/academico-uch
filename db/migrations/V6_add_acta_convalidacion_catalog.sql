-- New Acta tipo for Convalidacion (credit transfer/equivalency), so it stops sharing the
-- same ac_acta row as the regular teacher-driven Principal Acta for a section. Additive:
-- 036000-036002 already exist (TIPO DE ACTA header, PRINCIPAL, SUSTITUTORIA); 036003 is free.

INSERT INTO tb_catalogo (cat_codigo_grupo, cat_codigo_elemento, cat_descripcion_elemento, cat_valor, cat_activo)
VALUES ('036', '003', 'CONVALIDACION', 'C', 1);
