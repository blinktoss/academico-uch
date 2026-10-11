package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSAreaDAO;
import net.uch.mapping.ClArea;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSAreaDAOImpl extends TypedHibernateDao implements HSAreaDAO {

    @Override
    public List<ClArea> seleccionarArea(String descripcion) {
        DetachedCriteria c = DetachedCriteria.forClass(ClArea.class).
                add(Restrictions.eq("areActivo", "1"));

        if (descripcion.trim().length() != 0) {
            c.add(Restrictions.like("areDescripcion", "%" + descripcion + "%"));
        }
        return findByCriteria(ClArea.class, c);
    }

    @Override
    public void eliminarArea(int id) {
        getHibernateTemplate().execute(session ->
                session.createQuery("update ClArea set areActivo = :activo where areId = :id").
                        setParameter("activo", "0").setParameter("id", id).
                        executeUpdate());
    }

    @Override
    public void insertarArea(ClArea area) {
        this.getHibernateTemplate().save(area);
    }

    @Override
    public void actualizarArea(ClArea area) {
        this.getHibernateTemplate().update(area);
    }

    @Override
    public ClArea buscarArea(int id) {
        return getHibernateTemplate().get(ClArea.class, id);
    }
}
