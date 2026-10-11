package net.uch.administrativa.dao.impl;

import net.uch.administrativa.dao.HSProrrogaDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AdProrroga;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created for sistema_academico on 27/07/2024.
 *
 * @author Cesardl
 */
public class HSProrrogaDAOImpl extends TypedHibernateDao implements HSProrrogaDAO {

    @Override
    public void insertarProrroga(AdProrroga prorroga) {
        this.getHibernateTemplate().save(prorroga);
    }

    @Override
    public List<AdProrroga> listaProrrogas(int alutarId) {
        return findByCriteria(AdProrroga.class, DetachedCriteria.forClass(AdProrroga.class).add(Restrictions.eq("proActivo", "1")).
                add(Restrictions.eq("adAlumnoTarifa.id", alutarId)).
                addOrder(Order.asc("proId")));
    }
}
