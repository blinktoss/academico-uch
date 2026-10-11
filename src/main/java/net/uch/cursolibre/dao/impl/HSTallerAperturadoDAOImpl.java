package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.CLTallerAperturadoRow;
import net.uch.cursolibre.dao.HSTallerAperturadoDAO;
import net.uch.mapping.ClTallerAperturado;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSTallerAperturadoDAOImpl extends TypedHibernateDao implements HSTallerAperturadoDAO {

    @Override
    public ClTallerAperturado seleccionarTallerAperturado(int talapeId) {
        var talape = (ClTallerAperturado) this.getSessionFactory().getCurrentSession().createCriteria(ClTallerAperturado.class).
                add(Restrictions.eq(ClTallerAperturado.PROP_TALAPE_ACTIVO, "1")).
                add(Restrictions.eq("talapeId", talapeId)).
                uniqueResult();
        if (talape != null) {
            Hibernate.initialize(talape.getClSeccions());
        }
        return talape;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClTallerAperturado> seleccionarTalleresAperturados() {
        return this.getSessionFactory().getCurrentSession().createCriteria(ClTallerAperturado.class).
                add(Restrictions.eq(ClTallerAperturado.PROP_TALAPE_ACTIVO, "1")).
                add(Restrictions.eq("talapeVigente", "1")).
                add(Restrictions.eq("talapeAperturado", "1")).
                list();
    }

    @Override
    public List<ClTallerAperturado> seleccionarTallerAperturadoPorCurso(int curId) {
        return findByCriteria(ClTallerAperturado.class, DetachedCriteria.forClass(ClTallerAperturado.class, "ClTallerAperturado").createCriteria("ClTallerAperturado.clTaller", "ClTaller").
                add(Restrictions.eq("ClTallerAperturado.talapeActivo", "1")).
                add(Restrictions.eq("ClTallerAperturado.talapeVigente", "1")).
                add(Restrictions.eq("ClTallerAperturado.talapeAperturado", "1")).
                add(Restrictions.eq("ClTaller.talActivo", "1")).
                add(Restrictions.eq("ClTaller.clCurso.curId", curId)));
    }

    @Override
    public List<CLTallerAperturadoRow> findOpenedWorkshopsByCourses(List<Integer> courseIds) {
        var hql = "select new net.uch.cursolibre.dao.CLTallerAperturadoRow(cur.curId, talape.talapeId, talape.talapeDescripcion) " +
                "from ClTallerAperturado talape " +
                "inner join talape.clTaller tal " +
                "inner join tal.clCurso cur " +
                "where talape.talapeActivo = '1' " +
                "and talape.talapeVigente = '1' " +
                "and talape.talapeAperturado = '1' " +
                "and tal.talActivo = '1' " +
                "and cur.curId in (:courseIds) " +
                // Match the per-course query's implicit order: the join drives on cl_taller, so rows
                // come back by taller id (tal.talId), not talape.talapeId. talapeId is a stable
                // tiebreak for a taller opened more than once.
                "order by cur.curId, tal.talId, talape.talapeId";
        return find(CLTallerAperturadoRow.class, hql, "courseIds", courseIds);
    }

    @Override
    public List<ClTallerAperturado> seleccionarTalleresAperturadosPorModulo(int modId, int curId) {
        return findByCriteria(ClTallerAperturado.class, DetachedCriteria.forClass(ClTallerAperturado.class, "talape").createCriteria("talape.clTaller", "tal").
                createCriteria("tal.clCurso", "cur").
                add(Restrictions.eq("talape.talapeActivo", "1")).
                add(Restrictions.eq("talape.talapeVigente", "1")).
                add(Restrictions.eq("talape.talapeAperturado", "1")).
                add(Restrictions.eq("tal.talActivo", "1")).
                add(Restrictions.eq("cur.curActivo", "1")).
                add(Restrictions.eq("cur.clModulo.modId", modId)).
                add(Restrictions.ne("cur.curId", curId)));
    }

    @Override
    public List<ClTallerAperturado> seleccionarTalleresAperturadosPorTaller(int talId) {
        return findByCriteria(ClTallerAperturado.class, DetachedCriteria.forClass(ClTallerAperturado.class).add(Restrictions.eq(ClTallerAperturado.PROP_TALAPE_ACTIVO, "1")).
                add(Restrictions.eq("clTaller.talId", talId)));
    }

    @Override
    public void insertarOrActualizarTallerAperturado(ClTallerAperturado talape) {
        this.getHibernateTemplate().saveOrUpdate(talape);
    }

    @Override
    public void eliminarTallerAperturado(Integer talapeId) {
        var hqlUpdate = "update ClTallerAperturado set talapeActivo = '0' where talapeId = :talape_id";
        this.getSessionFactory().getCurrentSession().createQuery(hqlUpdate).setParameter("talape_id", talapeId).executeUpdate();
    }
}
