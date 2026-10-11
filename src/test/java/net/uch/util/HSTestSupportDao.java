package net.uch.util;

import net.uch.Stubs;
import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

/**
 * Created for sistema_academico on 18/08/2024.
 *
 * @author Cesardl
 */
@Repository
@Transactional // Hibernate 5.2+ requires an active transaction for executeUpdate()/flush();
// tx:annotation-driven (applicationContext-services.xml) proxies this bean.
public class HSTestSupportDao extends TypedHibernateDao {

    @Autowired
    @Qualifier("springHibernateDaoAlumno")
    private HSAlumnoDAO daoAcAlumno;

    @Autowired
    public void init(SessionFactory factory) {
        setSessionFactory(factory);
    }

    public void cleanAcMatriculaCursoConv() {
        List<AcMatricula> results = findByCriteria(AcMatricula.class,
                DetachedCriteria.forClass(AcMatricula.class, "mat")
                        .createCriteria("mat.AcMatriculaCursos", "matcur")
                        .add(Restrictions.eq("matcur.MatcurObs", "MAT-CUR-CONV"))
                        .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));

        int r = this.getHibernateTemplate().execute(session -> session.createSQLQuery(
                "DELETE FROM ac_matricula_curso WHERE matcur_obs = 'MAT-CUR-CONV'").
                executeUpdate());
        logger.trace("Total rows deleted: " + r);
        for (AcMatricula mat : results) {
            r = this.getHibernateTemplate().execute(session -> session.createSQLQuery(
                    "DELETE FROM ac_matricula WHERE mat_id = :mat_id").
                    setParameter("mat_id", mat.getId()).
                    executeUpdate());
            logger.trace("Total rows deleted: " + r + ", from matricula: " + mat.getId());
        }
    }

    public void deleteComprobantesByCliente(Integer cliente) {
        int r = getHibernateTemplate().execute(session -> session.createSQLQuery(
                "DELETE FROM ad_comprobante_pago WHERE compag_cliente = :c")
                .setParameter("c", cliente).executeUpdate());
        logger.trace("Test comprobantes deleted: " + r);
    }

    public void seedCorrelativo(String serie, String tipDoc, Integer lastNro) {
        getHibernateTemplate().execute(session -> session.createSQLQuery(
                "INSERT INTO comprobante_correlativo (serie, tip_doc, last_nro) VALUES (:s,:t,:n) "
                        + "ON DUPLICATE KEY UPDATE last_nro = :n")
                .setParameter("s", serie).setParameter("t", tipDoc).setParameter("n", lastNro)
                .executeUpdate());
    }

    public void deleteCorrelativo(String serie) {
        int r = getHibernateTemplate().execute(session -> session.createSQLQuery(
                "DELETE FROM comprobante_correlativo WHERE serie = :s")
                .setParameter("s", serie).executeUpdate());
        logger.trace("Test correlativo rows deleted: " + r);
    }

    public void updateSecVacantes(Integer seccionId, Integer vacantes) {
        int r = getHibernateTemplate().execute(session ->
                session.createQuery("update AcSeccion set SecVacantes = :v_vacantes where Id = :v_id")
                        .setParameter("v_vacantes", vacantes)
                        .setParameter("v_id", seccionId)
                        .executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void deleteAcMatricula(int matriculaId) {
        int r = this.getHibernateTemplate().execute(session -> session.createSQLQuery(
                "DELETE FROM ac_matricula_curso WHERE mat_id = :mat_id").
                setParameter("mat_id", matriculaId).
                executeUpdate());
        logger.trace("Total matricula_curso rows deleted: " + r);
        r = this.getHibernateTemplate().execute(session -> session.createSQLQuery(
                "DELETE FROM ac_matricula WHERE mat_id = :mat_id").
                setParameter("mat_id", matriculaId).
                executeUpdate());
        logger.trace("Total matricula rows deleted: " + r + ", matricula: " + matriculaId);
    }

    public void cleanTbBancoEntrada() {
        int r = this.getHibernateTemplate().execute(session -> session.createSQLQuery(
                "TRUNCATE TABLE tb_banco_entrada").
                executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void updateClSeccion(List<Integer> args) {
        int r = this.getHibernateTemplate().execute(session -> {
            String hql = "update ClSeccion set " +
                    "secFinicio = :v_inicio, secFfin = :v_fin, " +
                    "secFinicioMatricula = :v_inicio_mat, secFfinMatricula = :v_fin_mat " +
                    "where id in (:v_id)";
            LocalDate currentDate = LocalDate.now();
            return session.createQuery(hql).
                    setParameter("v_inicio", java.sql.Date.valueOf(currentDate.plusMonths(1))).
                    setParameter("v_fin", java.sql.Date.valueOf(currentDate.plusMonths(4))).
                    setParameter("v_inicio_mat", java.sql.Date.valueOf(currentDate.minusDays(10))).
                    setParameter("v_fin_mat", java.sql.Date.valueOf(currentDate.plusDays(10))).
                    setParameterList("v_id", args).executeUpdate();
        });
        logger.trace("Total rows affected: " + r);
    }

    public void deleteOpenedCourse(int curapeId) {
        logger.trace("Deleting AcCursoAperturado " + curapeId);
        AcCursoAperturado curape = this.getHibernateTemplate().load(AcCursoAperturado.class, curapeId);
        this.getHibernateTemplate().delete(curape);
    }

    public void updateAluEstado(int alumnoId, String aluEstado) {
        int r = getHibernateTemplate().execute(session ->
                session.createQuery("update AcAlumno set AluEstado = :v_estado where Id = :v_id")
                        .setParameter("v_estado", aluEstado)
                        .setParameter("v_id", alumnoId)
                        .executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void updateAlumnoTarifaEstado(List<Integer> alutarIds, String estado) {
        int r = getHibernateTemplate().execute(session ->
                session.createQuery("update AdAlumnoTarifa set AlutarEstado = :v_estado where Id in (:v_ids)")
                        .setParameter("v_estado", estado)
                        .setParameterList("v_ids", alutarIds)
                        .executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void updateAluTipo(int alumnoId, String aluTipo) {
        int r = getHibernateTemplate().execute(session ->
                session.createQuery("update AcAlumno set AluTipo = :v_tipo where Id = :v_id")
                        .setParameter("v_tipo", aluTipo)
                        .setParameter("v_id", alumnoId)
                        .executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void cleanAcNotas(int seccionId) {
        int r = getHibernateTemplate().execute(session ->
                session.createQuery("DELETE from AcNota where SecId = :v_id")
                        .setParameter("v_id", seccionId)
                        .executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void cleanAcActa(int seccionId) {
        int r = this.getHibernateTemplate().execute(session -> {
            int rowsAffected = session.createSQLQuery(
                    "DELETE ad FROM ac_acta_detalle ad " +
                            "INNER JOIN ac_acta a ON a.act_id = ad.act_id WHERE a.sec_id = :sec_id")
                    .setParameter("sec_id", seccionId).executeUpdate();

            rowsAffected += session.createSQLQuery(
                    "DELETE FROM ac_acta WHERE sec_id = :sec_id")
                    .setParameter("sec_id", seccionId).executeUpdate();

            return rowsAffected;
        });
        logger.trace("Total rows deleted: " + r);
    }

    public void cleanAcActaConvalidacionByAlumno(int alumnoId) {
        int r = this.getHibernateTemplate().execute(session -> {
            int rowsAffected = session.createSQLQuery(
                    "DELETE FROM ac_acta_detalle WHERE alu_id = :alu_id AND actdet_tipo_nota = :tipo_nota")
                    .setParameter("alu_id", alumnoId)
                    .setParameter("tipo_nota", CAT_TIPO_NOTA_CONVALIDACION)
                    .executeUpdate();

            rowsAffected += session.createSQLQuery(
                    "DELETE a FROM ac_acta a WHERE a.act_tipo = :tipo_acta " +
                            "AND NOT EXISTS (SELECT 1 FROM ac_acta_detalle d WHERE d.act_id = a.act_id)")
                    .setParameter("tipo_acta", CAT_TIPO_ACTA_CONVALIDACION)
                    .executeUpdate();

            return rowsAffected;
        });
        logger.trace("Total rows deleted: " + r);
    }

    public void cleanAcImportacionNotas(int seccionId) {
        int r = getHibernateTemplate().execute(session ->
                session.createQuery("DELETE from AcImportacionNotas where acSeccion.Id = :v_id")
                        .setParameter("v_id", seccionId)
                        .executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void prepareAcNotasTemporales(int secId, int sisevaperId) {
        int r = this.getHibernateTemplate().execute(session -> {
            List<AcAlumno> alumnos = daoAcAlumno.listarAlumnosMatriculaRegularPorSeccion(secId);

            java.util.Date nottemFecha = new java.util.Date();

            int size = alumnos.size();
            for (int i = 0; i < size; i++) {
                AcNotasTemporales nt = new AcNotasTemporales();
                nt.setNottemId(60000 + i);
                nt.setNottemFecha(nottemFecha);
                nt.setNottemNota(new BigDecimal(Stubs.randomNumeric(5, 21)));
                nt.setSisevaPerId(sisevaperId);
                nt.setAluId(alumnos.get(i).getId());
                nt.setNottemActivo("1");
                nt.setAcSeccion(new AcSeccion(secId));

                session.saveOrUpdate(nt);
                if (i % 5 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            return size;
        });
        logger.trace("Total rows inserted: " + r);
    }

    public void cleanAcNotaAndAcNotasTemporalesAndAcImportacionNotas(int secId, int sisevaperId) {
        int r = this.getHibernateTemplate().execute(session -> {
            int rowAffected = session.createQuery("DELETE FROM AcNota " +
                    "WHERE SecId = :sec_id AND SisevaPer.Id = :sis_eva_per_id")
                    .setParameter("sec_id", secId)
                    .setParameter("sis_eva_per_id", sisevaperId).executeUpdate();

            rowAffected += session.createQuery("DELETE FROM AcNotasTemporales " +
                    "WHERE acSeccion.Id = :sec_id AND sisevaPerId = :sis_eva_per_id")
                    .setParameter("sec_id", secId)
                    .setParameter("sis_eva_per_id", sisevaperId).executeUpdate();

            rowAffected += session.createQuery("DELETE FROM AcImportacionNotas " +
                    "WHERE acSeccion.Id = :sec_id AND acSisEvaPersonalizado.Id = :sis_eva_per_id")
                    .setParameter("sec_id", secId)
                    .setParameter("sis_eva_per_id", sisevaperId).executeUpdate();

            return rowAffected;
        });
        logger.trace("Total rows deleted: " + r);
    }

    public AcMatricula getAcMatricula(int id) {
        return getHibernateTemplate().get(AcMatricula.class, id);
    }

    public AcAmpliaciones insertAcAmpliacion(int studentId, int semesterId) {
        AcAmpliaciones ampl = new AcAmpliaciones();
        ampl.setAmpCreditos(RandomStringUtils.randomNumeric(2));
        ampl.setAlu(new AcAlumno(studentId));
        ampl.setSemId(semesterId);
        ampl.setAmpActivo("1");
        getHibernateTemplate().save(ampl);
        return ampl;
    }

    public void deleteAcAmpliacion(AcAmpliaciones ampl) {
        this.getHibernateTemplate().delete(ampl);
    }

    public void insertAcEquivalencia(int plancurId, int size) {
        AcPlanCurso plancur = new AcPlanCurso();
        plancur.setId(plancurId);

        List<AcEquivalencia> equivs = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            AcEquivalencia e = new AcEquivalencia();
            e.setEquCursoEquivalente(RandomStringUtils.randomAlphanumeric(100));
            e.setEquObservacion(RandomStringUtils.randomAlphanumeric(100));
            e.setPlancur(plancur);
            e.setEquCreacion(new java.util.Date());
            e.setEquActivo("1");
            equivs.add(e);
        }
        for (AcEquivalencia equiv : equivs) {
            getHibernateTemplate().saveOrUpdate(equiv);
        }
    }

    public void deleteAcEquivalencia() {
        int r = this.getHibernateTemplate().execute(session ->
                session.createSQLQuery("TRUNCATE TABLE ac_equivalencia")
                        .executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void insertAcDisponibilidades() {
        List<Integer> teacherIds = Arrays.asList(39, 191, 39, 39, 284, 284, 178, 178, 39);

        List<AcDisponibilidad> entities = teacherIds.stream()
                .map(teacherId -> {
                    AcDisponibilidad disp = new AcDisponibilidad();
                    disp.setDisObservacion(RandomStringUtils.randomAlphanumeric(50));
                    disp.setTurdetId(Stubs.randomNumeric(1, 21));
                    disp.setDoc(new AcDocente(teacherId));
                    disp.setDisActivo("1");
                    disp.setDisDia("01600" + Stubs.randomNumeric(1, 8));
                    return disp;
                }).collect(Collectors.toList());

        for (AcDisponibilidad entity : entities) {
            getHibernateTemplate().saveOrUpdate(entity);
        }
    }

    public void cleanAcDisponibilidades() {
        int r = this.getHibernateTemplate().execute(session ->
                session.createSQLQuery("TRUNCATE TABLE ac_disponibilidad")
                        .executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void insertAdSaldos() {
        List<Integer> studentIds = Arrays.asList(2954, 3015, 3102, 3147, 3633, 4284, 4466, 4873);

        List<AdSaldo> entities = studentIds.stream()
                .map(studentId -> {
                    Date date = new Date();

                    AdSaldo saldo = new AdSaldo();
                    saldo.setSalMonto(200f);
                    saldo.setSalFechaIngresoMonto(date);
                    saldo.setAlu(new AcAlumno(studentId));
                    saldo.setSalCreacion(date);
                    return saldo;
                }).collect(Collectors.toList());

        for (AdSaldo entity : entities) {
            getHibernateTemplate().saveOrUpdate(entity);
        }
    }

    public void cleanAdSaldo() {
        int r = this.getHibernateTemplate().execute(session -> session.createSQLQuery(
                "TRUNCATE TABLE ad_saldo").
                executeUpdate());
        logger.trace("Total rows affected: " + r);
    }

    public void deleteAdComprobantePago(int studentId, String receiptNumber) {
        var hql = "from AdComprobantePago where CompagCliente = :studentId and CompagNro = :receiptNumber";
        List<AdComprobantePago> results = find(AdComprobantePago.class,
                hql, new String[]{"studentId", "receiptNumber"}, new Object[]{studentId, receiptNumber});

        AdComprobantePago compag = DataAccessUtils.requiredSingleResult(results);
        this.getHibernateTemplate().delete(compag);
    }

    public void cleanClMatricula(int secId) {
        var hql = "select mat from ClMatricula mat " +
                "inner join mat.clMatriculaTallers mattal " +
                "where mattal.clSeccion.id = :secId";
        List<ClMatricula> enrollments = find(ClMatricula.class, hql, "secId", secId);

        // Enrollment saves cl_alumno_tarifa rows separately from the ClMatricula graph
        // (MbSeccionCL#guardarMatriculados, MbMatriculaCL#matricularSeleccionados) — they are linked
        // only by mat_id and are NOT reachable through ClMatricula, so deleteAll() below never removes
        // them. Delete them here by mat_id so test runs leave no orphaned active tariffs behind
        // (production's un-enroll path, MbMatriculaCL#anularMatricula, likewise deactivates them).
        List<Integer> matIds = enrollments.stream()
                .map(ClMatricula::getMatId)
                .collect(Collectors.toList());
        if (!matIds.isEmpty()) {
            int tarifas = this.getHibernateTemplate().execute(session -> session.createQuery(
                    "delete from ClAlumnoTarifa where matId in (:matIds)")
                    .setParameterList("matIds", matIds)
                    .executeUpdate());
            logger.trace("Total cl_alumno_tarifa rows deleted: " + tarifas);
        }

        this.getHibernateTemplate().deleteAll(enrollments);
    }

    // ------------------------------------------------------------------
    // Characterization-test support (2026-07-18) - snapshot/mutate/restore
    // helpers for pinning CURRENT behavior of the critical flows.
    // ------------------------------------------------------------------

    public AdAlumnoTarifa getAlumnoTarifa(int alutarId) {
        return getHibernateTemplate().get(AdAlumnoTarifa.class, alutarId);
    }

    /**
     * Simulates a tariff row written by a sibling app with no effective deadline recorded.
     */
    public void clearAlutarFechaProrroga(int alutarId) {
        int r = getHibernateTemplate().execute(session -> session.createSQLQuery(
                "UPDATE ad_alumno_tarifa SET alutar_fecha_prorroga = NULL WHERE alutar_id = :id")
                .setParameter("id", alutarId).executeUpdate());
        logger.trace("Rows affected: " + r);
    }

    public void updateAlutarFechaProrroga(Integer alutarId, Date fecha) {
        int r = getHibernateTemplate().execute(session -> session.createSQLQuery(
                "UPDATE ad_alumno_tarifa SET alutar_fecha_prorroga = :f WHERE alutar_id = :id")
                .setParameter("f", fecha).setParameter("id", alutarId).executeUpdate());
        logger.trace("Rows affected: " + r);
    }

    /**
     * Inserts an acta (with the given status) holding a single PROMEDIO_FINAL detail row.
     */
    public int insertActaConDetalleFinal(int secId, String actTipo, String actStatus, int aluId, float nota) {
        var acta = new AcActa();
        acta.setActActivo("1");
        acta.setActTipo(actTipo);
        acta.setActStatus(actStatus);
        acta.setSec(new AcSeccion(secId));
        getHibernateTemplate().save(acta);

        var det = new AcActaDetalle();
        det.setAct(acta);
        det.setActdetActivo("1");
        det.setActdetNota(nota);
        det.setActdetTipoNota(CAT_TIPO_NOTA_PROMEDIO_FINAL);
        det.setAluId(aluId);
        det.setSisevaDetalleId(0);
        getHibernateTemplate().save(det);

        return acta.getId();
    }

    public void deleteActaCascade(int actaId) {
        int r = getHibernateTemplate().execute(session -> {
            int rows = session.createSQLQuery("DELETE FROM ac_acta_detalle WHERE act_id = :id")
                    .setParameter("id", actaId).executeUpdate();
            rows += session.createSQLQuery("DELETE FROM ac_acta WHERE act_id = :id")
                    .setParameter("id", actaId).executeUpdate();
            return rows;
        });
        logger.trace("Rows deleted: " + r);
    }

    /**
     * @return the id of the first voucher whose payments reference real student tariffs.
     */
    public int findFirstComprobanteIdWithTariffPagos() {
        Number id = getHibernateTemplate().execute(session -> (Number) session.createSQLQuery(
                "SELECT c.compag_id FROM ad_comprobante_pago c " +
                        "JOIN ad_pago p ON p.compag_id = c.compag_id " +
                        "JOIN ad_alumno_tarifa t ON t.alutar_id = p.alutar_id " +
                        "ORDER BY c.compag_id LIMIT 1").uniqueResult());
        return id.intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> findAlutarIdsForComprobante(int compagId) {
        return getHibernateTemplate().execute(session -> ((List<Number>) session.createSQLQuery(
                "SELECT DISTINCT p.alutar_id FROM ad_pago p " +
                        "JOIN ad_alumno_tarifa t ON t.alutar_id = p.alutar_id " +
                        "WHERE p.compag_id = :id")
                .setParameter("id", compagId).list())
                .stream().map(Number::intValue).collect(Collectors.toList()));
    }

    public TbUsuario getUsuario(int usuId) {
        return getHibernateTemplate().get(TbUsuario.class, usuId);
    }

    public void updateUsuarioPassword(int usuId, String encryptedPassword) {
        TbUsuario u = getHibernateTemplate().get(TbUsuario.class, usuId);
        u.setUsuPassword(encryptedPassword);
        getHibernateTemplate().update(u);
    }

    public void updateUsuarioVigente(int usuId, String vigente) {
        TbUsuario u = getHibernateTemplate().get(TbUsuario.class, usuId);
        u.setUsuVigente(vigente);
        getHibernateTemplate().update(u);
    }

    public int maxProId(int alutarId) {
        Number max = getHibernateTemplate().execute(session -> (Number) session.createSQLQuery(
                "SELECT COALESCE(MAX(pro_id), 0) FROM ad_prorroga WHERE alutar_id = :aid")
                .setParameter("aid", alutarId).uniqueResult());
        return max.intValue();
    }

    /**
     * Issue #33 test cleanup: physically remove the prorrogas a test just registered (pro_id beyond
     * the pre-test high-water mark) and re-sync the tariff's deadline to the highest remaining active
     * pro_numero, per the production rule — so test runs don't accumulate ad_prorroga rows.
     */
    public void cleanProrrogasCreatedDuringTest(int alutarId, int lastKnownProId) {
        int r = getHibernateTemplate().execute(session -> {
            int deleted = session.createSQLQuery(
                    "DELETE FROM ad_prorroga WHERE alutar_id = :aid AND pro_id > :pid")
                    .setParameter("aid", alutarId).setParameter("pid", lastKnownProId).executeUpdate();
            session.createSQLQuery(
                    "UPDATE ad_alumno_tarifa t SET t.alutar_fecha_prorroga = ("
                            + " SELECT p.pro_fecha FROM ad_prorroga p"
                            + " WHERE p.alutar_id = t.alutar_id AND p.pro_activo = '1'"
                            + " ORDER BY CAST(p.pro_numero AS UNSIGNED) DESC, p.pro_id DESC LIMIT 1)"
                            + " WHERE t.alutar_id = :aid"
                            + " AND EXISTS (SELECT 1 FROM ad_prorroga p2 WHERE p2.alutar_id = t.alutar_id AND p2.pro_activo = '1')")
                    .setParameter("aid", alutarId).executeUpdate();
            return deleted;
        });
        logger.trace("Test prorrogas deleted: " + r);
    }

    public int deleteProrrogasByComentario(String comentario) {
        int r = getHibernateTemplate().execute(session -> session.createSQLQuery(
                "DELETE FROM ad_prorroga WHERE pro_comentario = :c")
                .setParameter("c", comentario).executeUpdate());
        logger.trace("Test prorrogas deleted by comment: " + r);
        return r;
    }

    /**
     * First active tariff id matching the block-editor filter (semester + specialty + amount) - lets a
     * massive-update test snapshot and restore the single affected row.
     */
    public Integer findAlutarIdByMontoSemEsp(Integer semId, Integer esp, Float monto) {
        Number id = getHibernateTemplate().execute(session -> (Number) session.createSQLQuery(
                "SELECT t.alutar_id FROM ad_alumno_tarifa t "
                        + "JOIN ac_alumno a ON a.alu_id = t.alu_id "
                        + "JOIN ad_estructura_pagos_detalle d ON d.estpagdet_id = t.estpagdet_id "
                        + "JOIN ad_estructura_pagos e ON e.estpag_id = d.estpag_id "
                        + "WHERE t.alutar_activo='1' AND a.alu_activo='1' AND d.estpagdet_activo='1' "
                        + "AND e.estpag_activo='1' AND t.alutar_estado <> 2 "
                        + "AND e.sem_id = :sem AND a.esp_id = :esp AND t.alutar_monto = :monto LIMIT 1")
                .setParameter("sem", semId).setParameter("esp", esp).setParameter("monto", monto).uniqueResult());
        return id == null ? null : id.intValue();
    }
}
