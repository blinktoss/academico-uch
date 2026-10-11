package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSPlanCurricularDAO;
import net.uch.mapping.ClPlancurricular;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSPlanCurricularDAOImpl extends TypedHibernateDao implements HSPlanCurricularDAO {

    @Override
    public List<ClPlancurricular> seleccionarPlanCurricular(int moduleId, String description) {
        DetachedCriteria c = DetachedCriteria.forClass(ClPlancurricular.class)
                .add(Restrictions.eq("plaActivo", "1"));

        if (!description.isEmpty()) {
            c.add(Restrictions.like("plaDescripcion", description, MatchMode.ANYWHERE));
        }
        c.createCriteria("clModulo")
                .add(Restrictions.eq("modId", moduleId));
        return findByCriteria(ClPlancurricular.class, c);
    }

    @Override
    public void insertarPlanCurricular(ClPlancurricular planCurricular) {
        this.getHibernateTemplate().save(planCurricular);
    }

    @Override
    public void actualizarPlanCurricular(ClPlancurricular planCurricular) {
        this.getHibernateTemplate().update(planCurricular);
    }

    @Override
    public void eliminarPlanCurricular(int id) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update ClPlancurricular set plaActivo = :newName where plaId = :oldName";
            return session.createQuery(hqlUpdate).setParameter("newName", "0").
                    setParameter("oldName", id).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public ClPlancurricular buscarPlanCurricular(int id) {
        var hql = "from ClPlancurricular plancur " +
                "inner join fetch plancur.clModulo mod " +
                "inner join fetch mod.clArea " +
                "where plancur.id = :id";
        List<ClPlancurricular> results = find(ClPlancurricular.class, hql, "id", id);
        return DataAccessUtils.requiredUniqueResult(results);
    }

    @Override
    public void desactualizarPlanes(int moduleId) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update ClPlancurricular set plaActual = :actual where clModulo.modId = :id";
            return session.createQuery(hqlUpdate).setParameter("actual", "0").
                    setParameter("id", moduleId).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public ClPlancurricular buscarPlanActual(int moduleId) {
        List<ClPlancurricular> results = findByCriteria(ClPlancurricular.class, DetachedCriteria.forClass(ClPlancurricular.class).
                add(Restrictions.eq("plaActivo", "1")).
                add(Restrictions.eq("plaVigente", "1")).
                add(Restrictions.eq("plaActual", "1")).
                add(Restrictions.eq("clModulo.modId", moduleId)));
        return DataAccessUtils.uniqueResult(results);
    }
}
