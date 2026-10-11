USE db_sis_academica;

-- For migration 5.6 to 5.7
ALTER TABLE `db_sis_academica`.`tb_asistencia_prof`
CHANGE COLUMN `asis_dia` `asis_dia` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ;

-- Time fields with 0000-00-00 as default
/* SELECT * FROM `db_sis_academica`.`ac_sesion_asistencia` as sesasis
WHERE sesasis.ses_fecha_sesion = '0000-00-00'
ORDER BY sesasis.ses_id; */

DELETE FROM ac_sesion_asistencia WHERE ses_fecha_sesion = '0000-00-00';

-- -----------------------
/* SELECT * FROM `db_sis_academica`.`ac_sesion_efectiva_asis_doc`
WHERE sesefeasisdoc_salida = '0000-00-00 00:00:00.000000'; */

UPDATE ac_sesion_efectiva_asis_doc
SET sesefeasisdoc_salida = '2009-10-27'
WHERE sesefeasisdoc_id = 3567;

-- -----------------------
/* SELECT * FROM `db_sis_academica`.`ad_comprobante_pago`
WHERE compag_voucher_fecha = '0000-00-00'; */

UPDATE ad_comprobante_pago
SET compag_voucher_fecha = '2009-07-31'
WHERE compag_id = 19203;

-- -----------------------
/* SELECT * FROM `db_sis_academica`.`tb_alumno_salud`
WHERE alu_dato_fecha_intervencion = '0000-00-00'; */

UPDATE tb_alumno_salud
SET alu_dato_fecha_intervencion = '2010-01-15'
WHERE alu_dato_fecha_intervencion = '0000-00-00';

-- -----------------------
/* SELECT * FROM `db_sis_academica`.`tb_asistencia`
WHERE asis_dia = '0000-00-00 00:00:00.000000'; */

DELETE FROM tb_asistencia WHERE asis_id='78291';

-- Update Last Code
UPDATE ac_alumno
SET alu_codigo='10205046', alu_activo='1'
WHERE alu_codigo = 'xxxxxxxx';

-- Update Teacher Assistance, `050001` is Tipo Asistencia = `ASISTENCIA DE SECCION`
UPDATE ac_sesion_asistencia
SET doc_id =275, doc_id_bk = 275, ses_activo = '1', ses_tipo ='047001', ses_tipo_asistencia = '050001'
WHERE doc_id = 0;

-- For new users
INSERT INTO tb_menu_rol (rol_id, men_id, menrol_activo)
VALUES (6, 25, '1'),
       (6, 26, '1');
UPDATE tb_usuario t SET t.usu_nivel = '1' WHERE t.usu_id = 1;

-- Adding new pages
UPDATE tb_menu t SET t.men_accion = 'HorarioArea' WHERE t.men_id = 30;
UPDATE tb_menu t SET t.men_accion = 'DocenteDisponibilidad' WHERE t.men_id = 32;
UPDATE tb_menu t SET t.men_accion = 'Saldos' WHERE t.men_id = 45;

-- For Assistance in saturday and sunday
INSERT INTO tb_asistencia(asis_codigo, asis_dia, hora_entrada)
VALUES ('09204196', '2009-09-05 13:00:00', '13:00:00'),
       ('09204196', '2009-09-06 13:00:00', '13:00:00')
;

--
INSERT INTO tb_catalogo(cat_codigo_grupo, cat_codigo_elemento, cat_descripcion_elemento, cat_valor)
VALUES ('024', '003', 'CURSOS LIBRES', '3');

-- Fix data integrity in `ac_curso_aperturado`
UPDATE ac_curso_aperturado ac1
    INNER JOIN (
        SELECT curape.curape_Id as id
        FROM ac_curso_aperturado curape
                 inner join ac_plan_curso plancur on curape.plancur_id = plancur.plancur_Id
                 inner join ac_curso cur on plancur.cur_id = cur.cur_id
        WHERE curape.sem_id in (5, 9)
          AND curape.curape_activo = '1'
          AND plancur.plancur_activo = '1'
          AND cur.cur_activo = '1'
          AND curape.curape_aperturado = '0'
    ) ac2 ON ac1.curape_Id = ac2.id
SET ac1.curape_activo = '0';
