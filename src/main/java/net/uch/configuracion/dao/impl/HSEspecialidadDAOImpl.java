package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.mapping.AcEspecialidad;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSEspecialidadDAOImpl extends TypedHibernateDao implements HSEspecialidadDAO {

    @Override
    public void insertarEspecialidad(AcEspecialidad esp) {
        getHibernateTemplate().save(esp);
    }

    @Override
    public List<AcEspecialidad> seleccionarEspecialidad(String codigo, String nombre, int facultadId) {
        var criteria = DetachedCriteria.forClass(AcEspecialidad.class);
        if (!codigo.equals("")) {
            criteria.add(Restrictions.eq("EspCodigo", codigo));
        }
        criteria.add(Restrictions.like("EspNombre", nombre, MatchMode.ANYWHERE));
        if (facultadId != 0) {
            criteria.add(Restrictions.eq("Fac.Id", facultadId));
        }
        criteria.add(Restrictions.eq("EspActivo", "1"));
        return findByCriteria(AcEspecialidad.class, criteria);
    }

    @Override
    public List<AcEspecialidad> seleccionarEspecialidad(int facultadId) {
        return findByCriteria(AcEspecialidad.class, DetachedCriteria.forClass(AcEspecialidad.class)
                .add(Restrictions.eq("Fac.Id", facultadId))
                .add(Restrictions.eq(AcEspecialidad.PROP_ESP_ACTIVO, "1")));
    }

    @Override
    public AcEspecialidad seleccionarUnaEspecialidad(int especialidadId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AcEspecialidad.class)
                .add(Restrictions.eq(AcEspecialidad.PROP_ID, especialidadId))
                .add(Restrictions.eq(AcEspecialidad.PROP_ESP_ACTIVO, "1"));

        return (AcEspecialidad) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
    }

    @Override
    public int eliminarEspecialidad(Integer id) {
        return this.getHibernateTemplate().execute(session -> {
            var s = "update AcEspecialidad set EspActivo = :newName where Id = :oldName";
            return session.createQuery(s).setParameter("newName", "0").setParameter("oldName", id).executeUpdate();
        });
    }

    @Override
    public void actualizarEspecialidad(AcEspecialidad especialidad) {
        getHibernateTemplate().update(especialidad);
    }

    @Override
    public List<AcEspecialidad> seleccionarEspecialidades() {
        return findByCriteria(AcEspecialidad.class, DetachedCriteria.forClass(AcEspecialidad.class)
                .add(Restrictions.eq("EspActivo", "1")));
    }
}
