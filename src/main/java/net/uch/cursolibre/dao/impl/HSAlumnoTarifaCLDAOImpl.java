package net.uch.cursolibre.dao.impl;

import net.uch.academica.jsf.beans.ObjUpdate;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSAlumnoTarifaCLDAO;
import net.uch.mapping.AdPago;
import net.uch.mapping.ClAlumnoTarifa;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.*;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

import static net.uch.common.Constants.CAT_TIPO_CLIENTE_ALUMNO_CL;

/**
 * @author cesardl
 */
public class HSAlumnoTarifaCLDAOImpl extends TypedHibernateDao implements HSAlumnoTarifaCLDAO {

    @Override
    public void generarAlumnoTarifa(ClAlumnoTarifa alumnoTarifa) {
        this.getHibernateTemplate().save(alumnoTarifa);
    }

    @Override
    public void generarAlumnosTarifa(List<ClAlumnoTarifa> alumnosTarifa) {
        for (ClAlumnoTarifa alumnoTarifa : alumnosTarifa) {
            this.getHibernateTemplate().saveOrUpdate(alumnoTarifa);
        }
    }

    @Override
    public void actualizarDatosAlumnoTarifa(int alutarID, float monto, Date fechaPago, Date fechaProrroga) {
        int r = this.getHibernateTemplate().execute(session -> {
            String hqlUpdate = "update ClAlumnoTarifa "
                    + "set alutarMonto = :v_monto, "
                    + "alutarFechaPago = :v_pago, "
                    + "alutarFechaProrroga = :v_prorroga "
                    + "where alutarId = :v_id";
            return session.createQuery(hqlUpdate).
                    setParameter("v_monto", monto).
                    setParameter("v_pago", fechaPago, TemporalType.DATE).
                    setParameter("v_prorroga", fechaProrroga, TemporalType.DATE).
                    setParameter("v_id", alutarID).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public void eliminarAlumnosTarifa(List<Integer> alutarIds) {
        int r = this.getHibernateTemplate().execute(session -> {
            String hqlUpdate = "update ClAlumnoTarifa "
                    + "set alutarActivo = :v_activo "
                    + "where alutarId in (:v_id)";

            return session.createQuery(hqlUpdate).
                    setParameter("v_activo", "0").
                    setParameterList("v_id", alutarIds).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClAlumnoTarifa> seleccionarTarifasXMatricula(int matId, int aluId) {
        return this.getSessionFactory().getCurrentSession().createCriteria(ClAlumnoTarifa.class).
                add(Restrictions.eq("alutarActivo", "1")).
                add(Restrictions.eq("clAlumno.aluId", aluId)).
                add(Restrictions.eq("matId", matId)).list();
    }

    @Override
    public int actualizarEstructurasPagoBloque(int sec, List<ObjUpdate> restricciones, List<ObjUpdate> valores) {
        return getHibernateTemplate().execute(session -> {
            var queryString = "update cl_alumno_tarifa "
                    + "inner join cl_alumno on cl_alumno.alu_id = cl_alumno_tarifa.alu_id "
                    + "inner join cl_estructura_pagos_detalle on cl_estructura_pagos_detalle.estpagdet_id = cl_alumno_tarifa.estpagdet_id "
                    + "inner join cl_estructura_pagos on cl_estructura_pagos.estpag_id = cl_estructura_pagos_detalle.estpag_id "
                    + "set ";
            for (var i = 0; i < valores.size(); i++) {
                ObjUpdate o = valores.get(i);
                if (i == valores.size() - 1) {
                    queryString = queryString.concat(o.getKey() + " = " + o.getValue() + " ");
                } else {
                    queryString = queryString.concat(o.getKey() + " = " + o.getValue() + ", ");
                }
            }

            queryString = queryString + "where cl_alumno_tarifa.alutar_activo = '1'"
                    + " and cl_alumno.alu_activo = '1'"
                    + " and cl_estructura_pagos_detalle.estpagdet_activo = '1'"
                    + " and cl_estructura_pagos.estpag_activo='1'"
                    + " and cl_alumno_tarifa.alutar_estado <> '2'"
                    + " and cl_alumno_tarifa.sec_id = :secId";
            for (ObjUpdate o : restricciones) {
                queryString = queryString.concat(" and " + o.getKey() + " = " + o.getValue());
            }

            Query q = session.createSQLQuery(queryString).setParameter("secId", sec);
            return q.executeUpdate();
        });
    }

    @Override
    public boolean existenPagosMatricula(int aluId, int alutarId) {
        List<Long> l = findByCriteria(Long.class, DetachedCriteria.forClass(AdPago.class, "AdPago").createCriteria("AdPago.Compag", "AdComprobantePago").
                add(Restrictions.eq("AdPago.PagActivo", "1")).
                add(Restrictions.eq("AdComprobantePago.CompagActivo", "1")).
                add(Restrictions.eq("AdComprobantePago.CompagClienteTipo", CAT_TIPO_CLIENTE_ALUMNO_CL)).
                add(Restrictions.eq("AdComprobantePago.CompagCliente", aluId)).
                add(Restrictions.eq("AdPago.AlutarId", alutarId)).
                setProjection(Projections.rowCount()));
        long x = DataAccessUtils.requiredUniqueResult(l);
        logger.trace("Pagos registrados: " + x);
        return x != 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClAlumnoTarifa> buscarTarifas(String codAlu, String estadoId, int conceptoId, String apPaterno, String apMaterno, int first, int max) {
        return fetchForView(ordered(criteriaFor(codAlu, estadoId, conceptoId, apPaterno, apMaterno)))
                .setFirstResult(first).setMaxResults(max).list();
    }

    @Override
    public long contarTarifas(String codAlu, String estadoId, int conceptoId, String apPaterno, String apMaterno) {
        Number count = (Number) criteriaFor(codAlu, estadoId, conceptoId, apPaterno, apMaterno)
                .setProjection(Projections.rowCount()).uniqueResult();
        return count == null ? 0L : count.longValue();
    }

    /**
     * Shared filtered criteria for the free-courses payment search: ClAlumnoTarifa joined to its
     * structure detalle/estructura and the CL student, active rows plus the optional filters. Uses
     * createAlias (returns the root) so callers layer order, paging, projection or fetch onto one root.
     */
    private Criteria criteriaFor(String codAlu, String estadoId, int conceptoId, String apPaterno, String apMaterno) {
        Criteria criteria = this.getSessionFactory().getCurrentSession().createCriteria(ClAlumnoTarifa.class, "alutar")
                .createAlias("alutar.clEstructuraPagosDetalle", "estpagdet")
                .createAlias("estpagdet.clEstructuraPagos", "estpag")
                .createAlias("alutar.clAlumno", "alu")
                .add(Restrictions.eq("alutar.alutarActivo", "1"))
                .add(Restrictions.eq("estpagdet.estpagdetActivo", "1"))
                .add(Restrictions.eq("estpag.estpagActivo", "1"));
        if (apPaterno.trim().length() != 0) {
            criteria.add(Restrictions.like("alu.aluAppaterno", apPaterno, MatchMode.ANYWHERE));
        }
        if (apMaterno.trim().length() != 0) {
            criteria.add(Restrictions.like("alu.aluApmaterno", apMaterno, MatchMode.ANYWHERE));
        }
        if (codAlu.trim().length() != 0) {
            criteria.add(Restrictions.eq("alu.aluCodigo", codAlu));
        }
        if (conceptoId != 0) {
            criteria.add(Restrictions.eq("alutar.conpagId", conceptoId));
        }
        if (!estadoId.equals("-1")) {
            criteria.add(Restrictions.eq("alutar.alutarEstado", estadoId));
        }
        return criteria;
    }

    private Criteria ordered(Criteria criteria) {
        return criteria.addOrder(Order.asc("alu.aluId")).addOrder(Order.asc("alutar.alutarFechaPago"));
    }

    /**
     * Eager-fetches the to-one paths the listing renders (student, and structure detalle → estructura
     * → módulo) so the bean can read them after the query's transaction closes. All to-one, paging-safe.
     */
    private Criteria fetchForView(Criteria criteria) {
        return criteria
                .setFetchMode("clAlumno", FetchMode.JOIN)
                .setFetchMode("clEstructuraPagosDetalle", FetchMode.JOIN)
                .setFetchMode("clEstructuraPagosDetalle.clEstructuraPagos", FetchMode.JOIN)
                .setFetchMode("clEstructuraPagosDetalle.clEstructuraPagos.clModulo", FetchMode.JOIN);
    }
}
