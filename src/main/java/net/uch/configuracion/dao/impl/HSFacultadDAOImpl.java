package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.mapping.AcFacultad;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSFacultadDAOImpl extends TypedHibernateDao implements HSFacultadDAO {

    @Override
    public void insertarFacultad(AcFacultad facultad) {
        getHibernateTemplate().save(facultad);
    }

    @Override
    public List<AcFacultad> seleccionarFacultad(final String codigo, final String nombre) {
        var criteria = DetachedCriteria.forClass(AcFacultad.class);
        if (!codigo.isEmpty()) {
            criteria.add(Restrictions.eq(AcFacultad.PROP_FAC_CODIGO, codigo));
        }
        if (!nombre.isEmpty()) {
            criteria.add(Restrictions.like(AcFacultad.PROP_FAC_NOMBRE, nombre, MatchMode.ANYWHERE));
        }
        criteria.add(Restrictions.eq(AcFacultad.PROP_FAC_ACTIVO, "1"))
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return findByCriteria(AcFacultad.class, criteria);
    }

    @Override
    public void actualizarFacultad(AcFacultad facultad) {
        getHibernateTemplate().update(facultad);
    }

    @Override
    public void eliminarFacultad(AcFacultad facultad) {
        getHibernateTemplate().delete(facultad);
    }
}

