package net.uch.administrativa.dao.impl;

import net.uch.academica.jsf.beans.ObjUpdate;
import net.uch.administrativa.dao.HSEdicionEstructPagoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.*;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;

import java.util.Date;
import java.util.List;

import static net.uch.common.Constants.CAT_ESTADO_BOLETA_VALIDO;

/**
 * @author cesardl
 */
public class HSEdicionEstructPagoDAOImpl extends TypedHibernateDao implements HSEdicionEstructPagoDAO {

    @Override
    public void insertarAlumnoTarifa(AdAlumnoTarifa alumnoTarifa) {
        this.getHibernateTemplate().save(alumnoTarifa);
    }

    @Override
    public List<AcAlumno> listaAlumnoEstructura(String code, String paterno, String materno, String nombres, int semestre) {
        DetachedCriteria c = DetachedCriteria.forClass(AcAlumno.class, AcAlumno.REF).
                createCriteria(AcAlumno.REF + ".AdAlumnoTarifas", AdAlumnoTarifa.REF).
                createCriteria(AdAlumnoTarifa.REF + "." + AdAlumnoTarifa.PROP_ESTPAGDET, AdEstructuraPagosDetalle.REF).
                createCriteria(AdEstructuraPagosDetalle.REF + "." + AdEstructuraPagosDetalle.PROP_ESTPAG, AdEstructuraPagos.REF).
                add(Restrictions.eq(AcAlumno.REF + "." + AcAlumno.PROP_ALU_ACTIVO, "1")).
                add(Restrictions.eq(AdAlumnoTarifa.REF + "." + AdAlumnoTarifa.PROP_ALUTAR_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagosDetalle.REF + "." + AdEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.REF + "." + AdEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.REF + "." + AdEstructuraPagos.PROP_SEM + ".id", semestre));

        if (code.trim().length() != 0) {
            c.add(Restrictions.like(AcAlumno.REF + "." + AcAlumno.PROP_ALU_CODIGO, code, MatchMode.START));
        }
        if (paterno.trim().length() != 0) {
            c.add(Restrictions.like(AcAlumno.REF + "." + AcAlumno.PROP_ALU_APPATERNO, paterno, MatchMode.START));
        }
        if (materno.trim().length() != 0) {
            c.add(Restrictions.like(AcAlumno.REF + "." + AcAlumno.PROP_ALU_APMATERNO, materno, MatchMode.START));
        }
        if (nombres.trim().length() != 0) {
            c.add(Restrictions.like(AcAlumno.REF + "." + AcAlumno.PROP_ALU_NOMBRES, nombres, MatchMode.ANYWHERE));
        }

        c.createCriteria("AcAlumno.Esp").
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).
                addOrder(Order.asc("AcAlumno.AluAppaterno")).
                addOrder(Order.asc("AcAlumno.AluApmaterno"));
        return findByCriteria(AcAlumno.class, c);
    }

    @Override
    public List<AdAlumnoTarifa> listaTarifasAlumno(int semestre, int alumnoId) {
        return findByCriteria(AdAlumnoTarifa.class, DetachedCriteria.forClass(AdAlumnoTarifa.class, AdAlumnoTarifa.REF).createCriteria(AdAlumnoTarifa.REF + "." + AdAlumnoTarifa.PROP_ESTPAGDET, AdEstructuraPagosDetalle.REF).
                createCriteria(AdEstructuraPagosDetalle.REF + ".Estpag", AdEstructuraPagos.REF).
                add(Restrictions.eq(AdAlumnoTarifa.REF + "." + AdAlumnoTarifa.PROP_ALUTAR_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagosDetalle.REF + "." + AdEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.REF + "." + AdEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdAlumnoTarifa.REF + ".Alu.id", alumnoId)).
                add(Restrictions.eq(AdEstructuraPagos.REF + "." + AdEstructuraPagos.PROP_SEM + ".id", semestre)).
                addOrder(Order.asc(AdAlumnoTarifa.REF + ".Estpagdet.id")));
    }

    @Override
    public List<AcAlumno> listaAlumnoActivo(String dato) {
        return findByCriteria(AcAlumno.class, DetachedCriteria.forClass(AcAlumno.class).add(Restrictions.eq(AcAlumno.PROP_ALU_ACTIVO, "1")).
                add(Restrictions.eq(AcAlumno.PROP_ALU_TIPO, "009001")).
                add(Restrictions.or(Restrictions.like(AcAlumno.PROP_ALU_APPATERNO, dato + "%"),
                        Restrictions.or(Restrictions.like(AcAlumno.PROP_ALU_APMATERNO, dato + "%"),
                                Restrictions.like(AcAlumno.PROP_ALU_NOMBRES, dato + "%")))));
    }

    @Override
    public List<AdEstructuraPagos> listaEstructurasPago(int sem, int esp, int semIngreso) {
        return findByCriteria(AdEstructuraPagos.class, DetachedCriteria.forClass(AdEstructuraPagos.class).add(Restrictions.eq(AdEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.PROP_ESTPAGO_PUBLICADO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.PROP_SEM + ".id", sem)).
                add(Restrictions.eq(AdEstructuraPagos.PROP_SEM_IN + ".id", semIngreso)).
                add(Restrictions.eq(AdEstructuraPagos.PROP_ESP + ".id", esp)));
    }

    @Override
    public AdEstructuraPagos estructuraPagoPlantilla(int estpagId) {
        List<AdEstructuraPagos> results = findByCriteria(AdEstructuraPagos.class, DetachedCriteria.forClass(AdEstructuraPagos.class).add(Restrictions.eq(AdEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.PROP_ID, estpagId)));

        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public List<AdEstructuraPagos> listaEstructurasExistentes(int sem, int esp, int semIngreso) {
        return findByCriteria(AdEstructuraPagos.class, DetachedCriteria.forClass(AdEstructuraPagos.class, AdEstructuraPagos.REF).createCriteria(AdEstructuraPagos.REF + ".AdEstructuraPagosDetalles", AdEstructuraPagosDetalle.REF).
                add(Restrictions.eq(AdEstructuraPagos.REF + "." + AdEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagosDetalle.REF + "." + AdEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.REF + ".EstpagoPublicado", "1")).
                add(Restrictions.eq(AdEstructuraPagos.REF + ".Sem.id", sem)).
                add(Restrictions.eq(AdEstructuraPagos.REF + ".Esp.id", esp)).
                add(Restrictions.eq(AdEstructuraPagos.REF + ".SemIn.id", semIngreso)));
    }

    @Override
    public List<AdAlumnoTarifa> listaAlumnoTarifaExistentes(int aluId, int sem, int esp) {
        return findByCriteria(AdAlumnoTarifa.class, DetachedCriteria.forClass(AdAlumnoTarifa.class, AdAlumnoTarifa.REF).createCriteria(AdAlumnoTarifa.REF + "." + AdAlumnoTarifa.PROP_ESTPAGDET, AdEstructuraPagosDetalle.REF).
                createCriteria(AdEstructuraPagosDetalle.REF + ".Estpag", AdEstructuraPagos.REF).
                add(Restrictions.eq(AdAlumnoTarifa.REF + "." + AdAlumnoTarifa.PROP_ALUTAR_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagosDetalle.REF + "." + AdEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.REF + "." + AdEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdAlumnoTarifa.REF + ".Alu.id", aluId)).
                add(Restrictions.eq(AdEstructuraPagos.REF + ".Sem.id", sem)).
                add(Restrictions.eq(AdEstructuraPagos.REF + ".Esp.id", esp)));
    }

    @Override
    public void actualizarEstadoPago(int alutarId, String estado) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AdAlumnoTarifa set AlutarEstado = :v_estado where Id = :v_id";
            return session.createQuery(hqlUpdate).
                    setParameter("v_estado", estado).
                    setParameter("v_id", alutarId).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public List<AdPago> listaPagosAlumno(int alutarId, int caso) {
        DetachedCriteria c = DetachedCriteria.forClass(AdPago.class, AdPago.REF).
                createCriteria("AdPago.Compag", AdComprobantePago.REF).
                add(Restrictions.eq("AdPago.PagActivo", "1")).
                add(Restrictions.eq("AdComprobantePago.CompagActivo", "1")).
                add(Restrictions.eq("AdPago.AlutarId", alutarId));

        if (caso == 0) {
            c.add(Restrictions.eq("AdComprobantePago.CompagTipo", CAT_ESTADO_BOLETA_VALIDO));
        }

        return findByCriteria(AdPago.class, c);
    }

    @Override
    public void actualizarDatosAlumnoTarifa(Integer alutarId, Float monto, Date fechaPago) {
        // Deliberately does NOT touch AlutarFechaProrroga: the effective deadline is maintained only
        // through the prorroga flow (ProrrogaService), so this single-edit modal cannot bypass the
        // ad_prorroga history (issue #33).
        String hqlUpdate = "update AdAlumnoTarifa set AlutarMonto = :v_monto,"
                + " AlutarFechaPago = :v_pago"
                + " where Id = :v_id";
        int r = this.getHibernateTemplate().execute(session ->
                session.createQuery(hqlUpdate).
                        setParameter("v_monto", monto).
                        setParameter("v_pago", fechaPago).
                        setParameter("v_id", alutarId).executeUpdate());
        logger.trace("Rows affected: " + r);
    }

    @Override
    public int eliminarEstructurasPagoAlumnoTarifa(List<Integer> alutarIds) {
        return this.getHibernateTemplate().execute(session ->
                session.createQuery("update AdAlumnoTarifa set AlutarActivo = :v_activo where Id in (:v_id)").
                        setParameter("v_activo", "0").
                        setParameterList("v_id", alutarIds).executeUpdate());
    }

    @Override
    public int actualizarEstructurasPagoBloque(int semestreDestino, int esp, List<ObjUpdate> restricciones, List<ObjUpdate> valores) {
        return getHibernateTemplate().execute(session ->
                bulkUpdate(session, semestreDestino, esp, restricciones, valores));
    }

    @Override
    public int actualizarEstructurasPagoBloqueConHistorial(int semestreDestino, int esp, List<ObjUpdate> restricciones,
                                                           List<ObjUpdate> valores, Date fechaProrroga, String comentario, int usuId) {
        // Issue #33 review (F1): when a block edit moves the deadline, the ad_prorroga history rows and
        // the bulk column update must commit together. Both run inside one execute() and therefore share
        // a single Hibernate session/transaction - either both land or neither does, the same atomic
        // guarantee the single-grant path (ProrrogaService) provides, now for the massive path. History
        // is written first, so the enumerate reads pre-update pro_numero state.
        return getHibernateTemplate().execute(session -> {
            int inserted = insertProrrogaHistory(session, semestreDestino, esp, restricciones, fechaProrroga, comentario, usuId);
            logger.trace("Prorroga history rows inserted (massive): " + inserted);
            return bulkUpdate(session, semestreDestino, esp, restricciones, valores);
        });
    }

    /**
     * Bulk column update (monto / fecha_pago / fecha_prorroga) for every tariff matched by the block
     * filter. Extracted so it can run standalone or share a transaction with the history insert.
     */
    private int bulkUpdate(Session session, int semestreDestino, int esp, List<ObjUpdate> restricciones, List<ObjUpdate> valores) {
        var queryString = "update ad_alumno_tarifa "
                + "inner join ac_alumno "
                + "on ac_alumno.alu_id = ad_alumno_tarifa.alu_id "
                + "inner join ad_estructura_pagos_detalle "
                + "on ad_estructura_pagos_detalle.estpagdet_id = ad_alumno_tarifa.estpagdet_id "
                + "inner join ad_estructura_pagos "
                + "on ad_estructura_pagos.estpag_id = ad_estructura_pagos_detalle.estpag_id "
                + "set ";
        for (var i = 0; i < valores.size(); i++) {
            ObjUpdate o = valores.get(i);
            queryString = queryString.concat(o.getKey() + " = " + o.getValue());
            if (i != valores.size() - 1) {
                queryString = queryString.concat(", ");
            } else {
                queryString = queryString.concat(" ");
            }
        }

        queryString = queryString + "where ad_alumno_tarifa.alutar_activo='1' "
                + "and ac_alumno.alu_activo='1' "
                + "and ad_estructura_pagos_detalle.estpagdet_activo='1' "
                + "and ad_estructura_pagos.estpag_activo='1' "
                + "and ad_alumno_tarifa.alutar_estado <> 2 "
                + "and ad_estructura_pagos.sem_id = :semId "
                + "and ac_alumno.esp_id = :espId ";
        for (ObjUpdate o : restricciones) {
            queryString = queryString.concat("and " + o.getKey() + " = " + o.getValue() + " ");
        }

        Query q = session.createSQLQuery(queryString).
                setParameter("semId", semestreDestino).setParameter("espId", esp);
        return q.executeUpdate();
    }

    /**
     * Appends one ad_prorroga history row per tariff matched by the block filter (dated
     * {@code fechaProrroga}), enumerate-then-insert to avoid MySQL's self-referential INSERT..SELECT
     * restriction. Extracted so it can share a transaction with {@link #bulkUpdate}.
     */
    @SuppressWarnings("unchecked")
    private int insertProrrogaHistory(Session session, int semestreDestino, int esp, List<ObjUpdate> restricciones,
                                      Date fechaProrroga, String comentario, int usuId) {
        var selectSql = new StringBuilder(
                "select ad_alumno_tarifa.alutar_id as aid, "
                        + "coalesce(max(cast(p.pro_numero as unsigned)), 0) as maxnum "
                        + "from ad_alumno_tarifa "
                        + "inner join ac_alumno on ac_alumno.alu_id = ad_alumno_tarifa.alu_id "
                        + "inner join ad_estructura_pagos_detalle on ad_estructura_pagos_detalle.estpagdet_id = ad_alumno_tarifa.estpagdet_id "
                        + "inner join ad_estructura_pagos on ad_estructura_pagos.estpag_id = ad_estructura_pagos_detalle.estpag_id "
                        + "left join ad_prorroga p on p.alutar_id = ad_alumno_tarifa.alutar_id and p.pro_activo = '1' "
                        + "where ad_alumno_tarifa.alutar_activo='1' "
                        + "and ac_alumno.alu_activo='1' "
                        + "and ad_estructura_pagos_detalle.estpagdet_activo='1' "
                        + "and ad_estructura_pagos.estpag_activo='1' "
                        + "and ad_alumno_tarifa.alutar_estado <> 2 "
                        + "and ad_estructura_pagos.sem_id = :semId "
                        + "and ac_alumno.esp_id = :espId ");
        for (ObjUpdate o : restricciones) {
            selectSql.append("and ").append(o.getKey()).append(" = ").append(o.getValue()).append(' ');
        }
        selectSql.append("group by ad_alumno_tarifa.alutar_id");

        List<Object[]> rows = session.createSQLQuery(selectSql.toString())
                .setParameter("semId", semestreDestino).setParameter("espId", esp).list();

        var insertSql = "insert into ad_prorroga "
                + "(alutar_id, pro_numero, pro_fecha, usu_id, pro_comentario, pro_creacion, pro_activo) "
                + "values (:aid, :num, :fecha, :usu, :com, now(), '1')";
        var inserted = 0;
        for (Object[] row : rows) {
            int alutarId = ((Number) row[0]).intValue();
            int nextNum = ((Number) row[1]).intValue() + 1;
            session.createSQLQuery(insertSql)
                    .setParameter("aid", alutarId)
                    .setParameter("num", String.valueOf(nextNum))
                    .setParameter("fecha", fechaProrroga)
                    .setParameter("usu", usuId)
                    .setParameter("com", comentario)
                    .executeUpdate();
            inserted++;
        }
        return inserted;
    }
}
