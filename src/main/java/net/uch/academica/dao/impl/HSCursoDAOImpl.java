package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSCursoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcCurso;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HSCursoDAOImpl extends TypedHibernateDao implements HSCursoDAO {

    @Override
    public void insertOrUpdateCurso(AcCurso cur) {
        getHibernateTemplate().saveOrUpdate(cur);
    }

    @Override
    public List<Object[]> seleccionarCurso(String codigo, String nombre, int facultad, int especialidad) {
        var hqlUpdate = "select curso.Id, curso.CurNombre, curso.CurCodigo,"
                + " curso.Esp.Id, curso.Esp.EspNombre,"
                + " curso.Esp.Fac.Id, curso.Esp.Fac.FacNombre,"
                + " curso.CurActivo"
                + " from AcCurso as curso"
                + " where curso.CurCodigo like :codigo"
                + " and curso.CurNombre like :nombre"
                + " and curso.CurActivo = :activo";

        List<String> names = new ArrayList<>(Arrays.asList("codigo", "nombre", "activo"));
        List<Object> values = new ArrayList<>(Arrays.asList("%" + codigo + "%", "%" + nombre + "%", "1"));

        if (facultad != 0) {
            hqlUpdate += " and curso.Esp.Fac.Id = :facultadId";
            names.add("facultadId");
            values.add(facultad);
        }
        if (especialidad != 0) {
            hqlUpdate += " and curso.Esp.Id = :especialidadId";
            names.add("especialidadId");
            values.add(especialidad);
        }

        return find(Object[].class,
                hqlUpdate, names.toArray(new String[0]), values.toArray(new Object[0]));
    }

    @Override
    public List<AcCurso> seleccionarCurso(int especialidad) {
        var criteria = DetachedCriteria.forClass(AcCurso.class);
        if (especialidad != 0) {
            criteria.add(Restrictions.eq("Esp.Id", especialidad));
        }
        criteria.add(Restrictions.eq(AcCurso.PROP_CUR_ACTIVO, "1"));
        return findByCriteria(AcCurso.class, criteria);
    }

    @Override
    public void eliminarCurso(Integer id) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AcCurso set CurActivo = :newName where Id = :oldName";
            return session.createQuery(hqlUpdate)
                    .setParameter("newName", "0")
                    .setParameter("oldName", id).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }
}
