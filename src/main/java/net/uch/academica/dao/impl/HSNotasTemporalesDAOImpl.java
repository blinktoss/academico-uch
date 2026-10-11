package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSNotasTemporalesDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcNotasTemporales;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author richard
 */
public class HSNotasTemporalesDAOImpl extends TypedHibernateDao implements HSNotasTemporalesDAO {

    @Override
    public List<AcNotasTemporales> listarNotasTemporales(int secId, int sisevaPerId) {
        return findByCriteria(AcNotasTemporales.class, DetachedCriteria.forClass(AcNotasTemporales.class).add(Restrictions.eq("acSeccion.Id", secId)).
                add(Restrictions.eq("sisevaPerId", sisevaPerId)).
                add(Restrictions.eq("nottemActivo", "1")));
    }
}
