/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSNotasCLDAO;
import net.uch.mapping.ClNota;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author cesardl
 */
public class HSNotasCLDAOImpl extends TypedHibernateDao implements HSNotasCLDAO {

    @Override
    @SuppressWarnings("unchecked")
    public List<ClNota> seleccionarNotas(int secId) {
        Criteria c = this.getSessionFactory().getCurrentSession().createCriteria(ClNota.class).
                add(Restrictions.eq("notActivo", "1")).
                add(Restrictions.eq("secId", secId));
        return c.list();
    }

    @Override
    public void insertarActualizarNotas(List<ClNota> lista) {
        for (ClNota nota : lista) {
            this.getHibernateTemplate().saveOrUpdate(nota);
        }
    }
}
