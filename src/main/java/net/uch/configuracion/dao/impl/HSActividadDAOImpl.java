/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSActividadDAO;
import net.uch.mapping.AcCalendario;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.Date;
import java.util.List;

/**
 * @author cesardl
 */
public class HSActividadDAOImpl extends TypedHibernateDao implements HSActividadDAO {

    @Override
    public void insertarActividad(AcCalendario calendario) {
        this.getHibernateTemplate().save(calendario);
    }

    @Override
    public void actualizarActividad(AcCalendario calendario) {
        this.getHibernateTemplate().saveOrUpdate(calendario);
    }

    @Override
    public List<AcCalendario> seleccionarActividadDetalle(Date calFecha) {
        return findByCriteria(AcCalendario.class, DetachedCriteria.forClass(AcCalendario.class, "AcCalendario").
                createCriteria("AcCalendario.acCalendarioActividades", "AcCalendarioActividades").
                createCriteria("AcCalendarioActividades.acActividadAlcances", "AcActividadAlcance").
                add(Restrictions.eq("AcCalendario.calActivo", "1")).
                add(Restrictions.eq("AcCalendarioActividades.calactActivo", "1")).
                add(Restrictions.eq("AcActividadAlcance.actalcActivo", "1")).
                add(Restrictions.eq("AcCalendario.calFecha", calFecha)).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).
                addOrder(Order.asc("AcCalendarioActividades.calactId")));
    }

    @Override
    public AcCalendario seleccionarActividadId(int id) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AcCalendario.class, "AcCalendario").
                createCriteria("AcCalendario.acCalendarioActividades", "AcCalendarioActividades").
                createCriteria("AcCalendarioActividades.acActividadAlcances", "AcActividadAlcance").
                add(Restrictions.eq("AcCalendario.calActivo", "1")).
                add(Restrictions.eq("AcCalendarioActividades.calactActivo", "1")).
                add(Restrictions.eq("AcActividadAlcance.actalcActivo", "1")).
                add(Restrictions.eq("AcCalendario.calId", id));
        List<AcCalendario> results = findByCriteria(AcCalendario.class, criteria);
        return DataAccessUtils.requiredUniqueResult(results);
    }

    @Override
    public void actualizarEstadoPublicado(int id, String publicado) {
        int r = getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AcCalendarioActividades set calactPublicado = :v_publicado where acCalendario.calId = :v_id";
            return session.createQuery(hqlUpdate).
                    setParameter("v_publicado", publicado).
                    setParameter("v_id", id).executeUpdate();
        });
        logger.trace("Publish status updated: " + r);
    }

    @Override
    public void eliminarActividad(int id) {
        int r = getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AcCalendario set calActivo = :v_activo where calId = :v_id";
            return session.createQuery(hqlUpdate).
                    setParameter("v_activo", "0").
                    setParameter("v_id", id).executeUpdate();
        });
        logger.trace("Activity deleted: " + r);
    }

    @Override
    public void eliminarAlcances(List<Integer> ids) {
        int r = getHibernateTemplate().execute(session -> {
            var hqlUpdate = "delete from AcActividadAlcance where actalcId in (:v_ids)";
            return session.createQuery(hqlUpdate).
                    setParameterList("v_ids", ids).executeUpdate();
        });
        logger.trace("Scopes removed: " + r);
    }
}
