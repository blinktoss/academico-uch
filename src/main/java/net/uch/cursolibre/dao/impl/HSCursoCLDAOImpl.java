package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.CLCursoRow;
import net.uch.cursolibre.dao.HSCursoCLDAO;
import net.uch.mapping.ClCurso;
import net.uch.mapping.ClTaller;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

/**
 * @author cesardl
 */
public class HSCursoCLDAOImpl extends TypedHibernateDao implements HSCursoCLDAO {

    @Override
    public List<ClCurso> seleccionarCursos(int moduloId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ClCurso.class).
                add(Restrictions.eq("curActivo", "1"));
        if (moduloId > 0) {
            criteria.add(Restrictions.eq("clModulo.modId", moduloId));
        }
        return findByCriteria(ClCurso.class, criteria);
    }

    @Override
    public List<CLCursoRow> findCursosByModulos(List<Integer> moduloIds) {
        var hql = "select new net.uch.cursolibre.dao.CLCursoRow(m.modId, c.curId, c.curNombre) " +
                "from ClCurso c " +
                "inner join c.clModulo m " +
                "where c.curActivo = '1' " +
                "and m.modId in (:moduloIds) " +
                "order by m.modId, c.curId";
        return find(CLCursoRow.class, hql, "moduloIds", moduloIds);
    }

    @Override
    public boolean verificarEliminarCurso(int cursoId) {
        List<Long> results = findByCriteria(Long.class, DetachedCriteria.forClass(ClTaller.class)
                .add(Restrictions.eq("talActivo", "1"))
                .add(Restrictions.eq("clCurso.curId", cursoId))
                .setProjection(Projections.rowCount()));
        long x = DataAccessUtils.requiredUniqueResult(results);
        logger.trace("Talleres asignados: " + x);
        return x == 0;
    }

    @Override
    public void eliminarCurso(int cursoId) {
        this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update ClCurso set curActivo = :newName where curId = :oldName";
            return session.createQuery(hqlUpdate)
                    .setParameter("newName", "0")
                    .setParameter("oldName", cursoId)
                    .executeUpdate();
        });
    }

    @Override
    public void actualizarCurso(ClCurso curso) {
        this.getHibernateTemplate().update(curso);
    }

    @Override
    public void insertarCurso(ClCurso curso) {
        this.getHibernateTemplate().save(curso);
    }

    @Override
    public ClCurso buscarCurso(int cursoId) {
        List<ClCurso> results = findByCriteria(ClCurso.class, DetachedCriteria.forClass(ClCurso.class, "cur").
                createCriteria("cur.clModulo").
                add(Restrictions.eq("cur.curId", cursoId)));
        return DataAccessUtils.uniqueResult(results);
    }
}
