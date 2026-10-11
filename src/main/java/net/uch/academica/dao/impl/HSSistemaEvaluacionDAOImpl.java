package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSSistemaEvaluacionDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcSisEvaluacion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSSistemaEvaluacionDAOImpl extends TypedHibernateDao implements HSSistemaEvaluacionDAO {

    @Override
    public void insertarSistemaEvaluacion(AcSisEvaluacion siseva) {
        getHibernateTemplate().save(siseva);
    }

    @Override
    public List<AcSisEvaluacion> obtenerSistemaEvaluacion(String descripcion) {
        return findByCriteria(AcSisEvaluacion.class, DetachedCriteria.forClass(AcSisEvaluacion.class).add(Restrictions.like(AcSisEvaluacion.PROP_SISEVA_NOMBRE, descripcion, MatchMode.ANYWHERE)).
                add(Restrictions.eq(AcSisEvaluacion.PROP_SISEVA_ACTIVO, "1")));
    }

    @Override
    public AcSisEvaluacion obtenerSistemaEvaluacion(int id) {
        return this.getHibernateTemplate().get(AcSisEvaluacion.class, id);
    }

    @Override
    public void actualizarSistemaEvaluacion(AcSisEvaluacion siseva) {
        getHibernateTemplate().saveOrUpdate(siseva);
    }

    @Override
    public void eliminarSistemaEvaluacion(Integer id) {
        int r = this.getHibernateTemplate().execute(session ->
                session.createQuery("update AcSisEvaluacion set SisevaActivo=:activo where Id= :id")
                        .setParameter("id", id)
                        .setParameter("activo", "0").executeUpdate());
        logger.trace("Rows affected: " + r);
    }

    @Override
    public List<AcSisEvaluacion> seleccionarComboSistemaEvaluacion() {
        return findByCriteria(AcSisEvaluacion.class, DetachedCriteria.forClass(AcSisEvaluacion.class).add(Restrictions.eq("SisevaActivo", "1")));
    }
}
