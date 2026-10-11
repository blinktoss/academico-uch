/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSSistemaEvaluacionCLDAO;
import net.uch.mapping.ClSisEvaPersonalizado;
import net.uch.mapping.ClSisEvaluacion;
import net.uch.mapping.ClTallerAperturado;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author cesardl
 */
public class HSSistemaEvaluacionCLDAOImpl extends TypedHibernateDao implements HSSistemaEvaluacionCLDAO {

    @Override
    @SuppressWarnings("unchecked")
    public List<ClSisEvaPersonalizado> seleccionarSisEvaPerClTalape(ClTallerAperturado talape) {
        return this.getSessionFactory().getCurrentSession().createCriteria(ClSisEvaPersonalizado.class, "sisevaper").
                createCriteria("sisevaper.clSisEvaDetalle", "sisevadet").
                createCriteria("sisevadet.clSisEvaluacion", "siseva").
                add(Restrictions.eq("sisevadet.sisevaDetalleActivo", "1")).
                add(Restrictions.eq("sisevaper.sisevaPerActivo", "1")).
                add(Restrictions.eq("siseva.sisevaActivo", "1")).
                add(Restrictions.eq("sisevaper.clTallerAperturado.talapeId", talape.getTalapeId())).list();
    }

    @Override
    public ClSisEvaluacion seleccionarSistemaEvaluacion(int sisevaId) {
        var siseva = (ClSisEvaluacion) this.getSessionFactory().getCurrentSession().createCriteria(ClSisEvaluacion.class).
                add(Restrictions.eq(ClSisEvaluacion.PROP_SISEVA_ACTIVO, "1")).
                add(Restrictions.eq("sisevaId", sisevaId)).uniqueResult();
        if (siseva != null) {
            Hibernate.initialize(siseva.getClSisEvaDetalles());
        }
        return siseva;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClSisEvaluacion> seleccionarSistemasEvaluacion() {
        return this.getSessionFactory().getCurrentSession().createCriteria(ClSisEvaluacion.class).
                add(Restrictions.eq(ClSisEvaluacion.PROP_SISEVA_ACTIVO, "1")).list();
    }

    @Override
    public void insertarActualizarSisEvaPersonalizado(List<ClSisEvaPersonalizado> lPer) {
        for (ClSisEvaPersonalizado per : lPer) {
            this.getHibernateTemplate().saveOrUpdate(per);
        }
    }

    @Override
    public void eliminarSistemaEvaluacionPersonalizado(int id) {
        var hql = "update ClSisEvaPersonalizado set sisevaPerActivo=:activo where sisevaPerId= :id";

        this.getSessionFactory().getCurrentSession().createQuery(hql).setParameter("id", "" + id).
                setParameter("activo", "0").executeUpdate();
    }
}
