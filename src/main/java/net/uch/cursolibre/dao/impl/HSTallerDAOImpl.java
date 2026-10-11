package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSTallerDAO;
import net.uch.mapping.ClTaller;
import net.uch.mapping.ClTallerAperturado;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSTallerDAOImpl extends TypedHibernateDao implements HSTallerDAO {

    @Override
    public List<ClTaller> seleccionarTalleres(int cursoId) {
        return findByCriteria(ClTaller.class, DetachedCriteria.forClass(ClTaller.class, "t").createCriteria("t.clPlancurricular").
                add(Restrictions.eq("t.talActivo", "1")).
                add(Restrictions.eq("t.clCurso.curId", cursoId)).
                addOrder(Order.asc("t.talNumero")));
    }

    @Override
    public void insertarTaller(ClTaller taller) {
        this.getHibernateTemplate().save(taller);
    }

    @Override
    public void actualizarTaller(ClTaller taller) {
        this.getHibernateTemplate().update(taller);
    }

    @Override
    public int eliminarTalleres(List<Integer> talsId) {
        return this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update ClTaller set talActivo = :newName where talId in (:oldName)";
            return session.createQuery(hqlUpdate).
                    setParameter("newName", "0").
                    setParameterList("oldName", talsId).
                    executeUpdate();
        });
    }

    @Override
    public boolean tieneTalleresAperturados(int tallerId) {
        List<Long> results = findByCriteria(Long.class, DetachedCriteria.forClass(ClTallerAperturado.class).add(Restrictions.eq("talapeActivo", "1")).
                add(Restrictions.eq("talapeAperturado", "1")).
                add(Restrictions.eq("clTaller.talId", tallerId)).
                setProjection(Projections.rowCount()));
        long r = DataAccessUtils.requiredUniqueResult(results);
        logger.trace(String.format("Talleres aperturados: %d", r));
        return r != 0;
    }

    @Override
    public ClTaller buscarTaller(int tallerId) {
        return this.getHibernateTemplate().get(ClTaller.class, tallerId);
    }

    @Override
    public List<ClTaller> seleccionarTalleresXMod(int modId) {
        return findByCriteria(ClTaller.class, DetachedCriteria.forClass(ClTaller.class, "ClTaller").createCriteria("ClTaller.clCurso", "ClCurso").
                add(Restrictions.eq("ClTaller.talActivo", "1")).
                add(Restrictions.eq("ClCurso.clModulo.modId", modId)).
                addOrder(Order.asc("ClTaller.talNumero")));
    }
}
