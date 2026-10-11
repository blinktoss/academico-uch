package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSLocalDAO;
import net.uch.mapping.AcLocal;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSLocalDAOImpl extends TypedHibernateDao implements HSLocalDAO {

    @Override
    public void insertarLocal(AcLocal local) {
        getHibernateTemplate().save(local);
    }

    @Override
    public List<AcLocal> seleccionarLocal(String nombre) {
        var criteria = DetachedCriteria.forClass(AcLocal.class);
        criteria.add(Restrictions.like(AcLocal.PROP_LOC_DES, nombre, MatchMode.ANYWHERE));
        criteria.add(Restrictions.eq(AcLocal.PROP_LOC_ACTIVO, "1"));
        return findByCriteria(AcLocal.class, criteria);
    }

    @Override
    public void actualizarLocal(AcLocal local) {
        getHibernateTemplate().update(local);
    }

    @Override
    public void eliminarLocal(AcLocal local) {
        this.getHibernateTemplate().delete(local);
    }
}
