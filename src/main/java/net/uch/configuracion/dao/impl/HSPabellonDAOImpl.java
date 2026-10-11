package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSPabellonDAO;
import net.uch.mapping.AcPabellon;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSPabellonDAOImpl extends TypedHibernateDao implements HSPabellonDAO {

    @Override
    public void insertarPabellon(AcPabellon pabellon) {
        getHibernateTemplate().save(pabellon);
    }

    @Override
    public List<AcPabellon> seleccionarPabellon(String nombre, int local) {
        var criteria = DetachedCriteria.forClass(AcPabellon.class);
        criteria.add(Restrictions.like(AcPabellon.PROP_PAB_NOMBRE, nombre, MatchMode.ANYWHERE));
        if (local != 0) {
            criteria.add(Restrictions.eq(AcPabellon.PROP_LOC + ".Id", local));
        }
        criteria.add(Restrictions.eq(AcPabellon.PROP_PAB_ACTIVO, "1"))
                .createCriteria("Loc");
        return findByCriteria(AcPabellon.class, criteria);
    }

    @Override
    public void actualizarPabellon(AcPabellon pabellon) {
        getHibernateTemplate().update(pabellon);
    }

    @Override
    public void eliminarPabellon(AcPabellon pabellon) {
        getHibernateTemplate().delete(pabellon);
    }
}
