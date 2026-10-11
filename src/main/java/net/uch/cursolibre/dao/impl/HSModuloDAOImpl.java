package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.CLModuloRow;
import net.uch.cursolibre.dao.HSModuloDAO;
import net.uch.mapping.ClModulo;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSModuloDAOImpl extends TypedHibernateDao implements HSModuloDAO {

    @Override
    public List<ClModulo> seleccionarModulos(int areaId, String descripcion) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ClModulo.class).
                add(Restrictions.eq("modActivo", "1"));

        if (descripcion.trim().length() != 0) {
            criteria.add(Restrictions.like("modDescripcion", "%" + descripcion + "%"));
        }
        if (areaId > 0) {
            criteria.add(Restrictions.eq("clArea.areId", areaId));
        }
        return findByCriteria(ClModulo.class, criteria);
    }

    @Override
    public List<CLModuloRow> findModulosByAreas(List<Integer> areaIds) {
        var hql = "select new net.uch.cursolibre.dao.CLModuloRow(area.areId, m.modId, m.modDescripcion) " +
                "from ClModulo m " +
                "inner join m.clArea area " +
                "where m.modActivo = '1' " +
                "and area.areId in (:areaIds) " +
                "order by area.areId, m.modId";
        return find(CLModuloRow.class, hql, "areaIds", areaIds);
    }

    @Override
    public void insertarModulo(ClModulo modulo) {
        this.getHibernateTemplate().save(modulo);
    }

    @Override
    public void eliminarModulo(int moduloId) {
        this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update ClModulo set modActivo = :newName where modId = :oldName";
            return session.createQuery(hqlUpdate)
                    .setParameter("newName", "0")
                    .setParameter("oldName", moduloId)
                    .executeUpdate();
        });
    }

    @Override
    public void actualizarModulo(ClModulo modulo) {
        this.getHibernateTemplate().update(modulo);
    }

    @Override
    public ClModulo buscarModulo(int moduloId) {
        return getHibernateTemplate().get(ClModulo.class, moduloId);
    }

    @Override
    public List<ClModulo> listarModulos() {
        return findByCriteria(ClModulo.class, DetachedCriteria.forClass(ClModulo.class).add(Restrictions.eq("modActivo", "1")));
    }
}
