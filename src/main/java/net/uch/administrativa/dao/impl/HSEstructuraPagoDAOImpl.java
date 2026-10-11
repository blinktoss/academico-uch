package net.uch.administrativa.dao.impl;

import net.uch.administrativa.dao.HSEstructuraPagoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AdEstructuraPagos;
import net.uch.mapping.AdEstructuraPagosDetalle;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSEstructuraPagoDAOImpl extends TypedHibernateDao implements HSEstructuraPagoDAO {

    @Override
    public void insertarEstructuraPagos(AdEstructuraPagos estpag) {
        getHibernateTemplate().save(estpag);
    }

    @Override
    public List<AdEstructuraPagos> seleccionarEstructuraPagos(int espId, int semId) {
        var criteria = DetachedCriteria.forClass(AdEstructuraPagos.class);
        if (espId != 0 && semId != 0) {
            criteria.add(Restrictions.eq("Esp.Id", espId));
            criteria.add(Restrictions.eq("Sem.Id", semId));
        } else {
            if (espId != 0) {
                criteria.add(Restrictions.eq("Esp.Id", espId));
            }
            if (semId != 0) {
                criteria.add(Restrictions.eq("Sem.Id", semId));
            }
        }
        criteria.add(Restrictions.eq(AdEstructuraPagos.PROP_ESTPAG_ACTIVO, "1"));
        return findByCriteria(AdEstructuraPagos.class, criteria);
    }

    @Override
    public AdEstructuraPagos seleccionarUnaEstructuraPagos(int id) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AdEstructuraPagos.class).
                add(Restrictions.eq(AdEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagos.PROP_ID, id));
        List<AdEstructuraPagos> results = findByCriteria(AdEstructuraPagos.class, criteria);
        return DataAccessUtils.requiredUniqueResult(results);
    }

    @Override
    public void actualizarEstructuraPagos(AdEstructuraPagos siseva) {
        getHibernateTemplate().saveOrUpdate(siseva);
    }

    @Override
    public void eliminarEstructuraPagos(int id) {
        int r = this.getHibernateTemplate().execute(session ->
                session.createQuery("update AdEstructuraPagos set EstpagActivo=:activo where Id= :id").
                        setParameter("id", id).
                        setParameter("activo", "0").executeUpdate());
        logger.trace("Rows affected: " + r);
    }

    @Override
    public AdEstructuraPagosDetalle seleccionarUnaEstructuraPagosDet(int id) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AdEstructuraPagosDetalle.class).
                add(Restrictions.eq(AdEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq(AdEstructuraPagosDetalle.PROP_ID, id));
        List<AdEstructuraPagosDetalle> results = findByCriteria(AdEstructuraPagosDetalle.class, criteria);
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AdEstructuraPagosDetalle> seleccionarEstructuraPagosDet() {
        return this.getSessionFactory().getCurrentSession().createCriteria(AdEstructuraPagosDetalle.class).
                add(Restrictions.eq(AdEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                setFetchMode("Estpag", FetchMode.JOIN).
                setFetchMode("Estpag.Sem", FetchMode.JOIN).
                setFetchMode("Estpag.Esp", FetchMode.JOIN).
                setFetchMode("Estpag.Esp.Fac", FetchMode.JOIN).
                addOrder(Order.asc(AdEstructuraPagosDetalle.PROP_ESTPAGDET_NOMBRE)).list();
    }
}
