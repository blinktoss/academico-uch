package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.mapping.TbCatalogo;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

import static net.uch.mapping.TbCatalogo.*;

public class HSCatalogoDAOImpl extends TypedHibernateDao implements HSCatalogoDAO {

    @Override
    public List<TbCatalogo> seleccionarCatalogo(String codigo) {
        return findByCriteria(TbCatalogo.class, DetachedCriteria.forClass(TbCatalogo.class).add(Restrictions.eq(PROP_CAT_ACTIVO, "1")).
                add(Restrictions.not(Restrictions.eq(PROP_CAT_CODIGO_ELEMENTO, "000"))).
                add(Restrictions.eq(PROP_CAT_CODIGO_GRUPO, codigo)));
    }

    @Override
    public String seleccionarDescripcion(String codigo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(TbCatalogo.class).
                add(Restrictions.eq(PROP_CAT_ACTIVO, "1")).
                add(Restrictions.eq(PROP_CAT_CODIGO_GRUPO, codigo.substring(0, 3))).
                add(Restrictions.eq(PROP_CAT_CODIGO_ELEMENTO, codigo.substring(3, 6)));

        TbCatalogo cat = (TbCatalogo) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
        if (cat == null) {
            return "VALOR NO REGISTRADO";
        } else {
            return cat.getCatDescripcionElemento();
        }
    }

    @Override
    public String seleccionarValor(String codigo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(TbCatalogo.class)
                .add(Restrictions.eq(PROP_CAT_CODIGO_GRUPO, codigo.substring(0, 3)))
                .add(Restrictions.eq(PROP_CAT_CODIGO_ELEMENTO, codigo.substring(3, 6)))
                .add(Restrictions.eq(PROP_CAT_ACTIVO, "1"));

        TbCatalogo cat = (TbCatalogo) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));

        return cat.getCatValor();
    }

    @Override
    public List<TbCatalogo> seleccionarGrupo(String codigo) {
        return findByCriteria(TbCatalogo.class, DetachedCriteria.forClass(TbCatalogo.class).add(Restrictions.eq(PROP_CAT_CODIGO_GRUPO, codigo)).
                add(Restrictions.eq(PROP_CAT_ACTIVO, "1")));
    }
}
