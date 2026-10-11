package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSUbigeoDAO;
import net.uch.mapping.TbDistrito;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import static net.uch.mapping.TbDistrito.PROP_ID;

public class HSUbigeoDAOImpl extends TypedHibernateDao implements HSUbigeoDAO {

    @Override
    public List<TbDistrito> seleccionarDepartamento() {
        var criteria = DetachedCriteria.forClass(TbDistrito.class);
        criteria.add(Restrictions.like(PROP_ID, "%__0000%"));
        return findByCriteria(TbDistrito.class, criteria);
    }

    @Override
    public List<TbDistrito> seleccionarProvincia(String codigoDepartamento) {
        var criteria = DetachedCriteria.forClass(TbDistrito.class);
        codigoDepartamento = codigoDepartamento.substring(0, 2);
        criteria.add(Restrictions.like(PROP_ID, "%" + codigoDepartamento + "__" + "00%"));
        criteria.add(Restrictions.not(Restrictions.like(PROP_ID, "%__" + "0000%")));
        return findByCriteria(TbDistrito.class, criteria);
    }

    @Override
    public List<TbDistrito> seleccionarDistrito(String codigoDepartamento, String codigoProvincia) {
        var criteria = DetachedCriteria.forClass(TbDistrito.class);
        criteria.add(Restrictions.like(PROP_ID, "%" + codigoDepartamento.substring(0, 2) + codigoProvincia.substring(2, 4) + "__%"));
        criteria.add(Restrictions.not(Restrictions.like(PROP_ID, "%__" + "0000%")));
        criteria.add(Restrictions.not(Restrictions.like(PROP_ID, "%____" + "00%")));
        return findByCriteria(TbDistrito.class, criteria);
    }
}
