package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSPlanCurricularDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcPlancurricular;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HSPlanCurricularDAOImpl extends TypedHibernateDao implements HSPlanCurricularDAO {

    @Override
    public Integer insertarPlanCurricular(AcPlancurricular pc) {
        this.getHibernateTemplate().save(pc);
        return pc.getId();
    }

    @Override
    public List<Object[]> seleccionarPlanCurricular(String codigo, String descripcion, int facultad, int especialidad) {
        List<String> names = new ArrayList<>(Arrays.asList("codigo", "descripcion"));
        List<Object> values = new ArrayList<>(Arrays.asList("%" + codigo + "%", "%" + descripcion + "%"));

        String hql = "select pc.Id,"
                + "pc.PlanCodigo,"
                + "pc.PlanDescripcion,"
                + "pc.PlanCreacion,"
                + "fac.Id,"
                + "pc.Esp.Id,"
                + "pc.PlanResolucion,"
                + "pc.PlanActivo,"
                + "fac.FacNombre,"
                + "esp.EspNombre "
                + "from AcPlancurricular pc, AcEspecialidad esp, AcFacultad fac "
                + "where pc.Esp.Id =esp.Id "
                + "and pc.PlanCodigo like :codigo "
                + "and pc.PlanDescripcion like :descripcion ";
        if (facultad != 0) {
            hql += "and fac.Id = :facultadId ";

            names.add("facultadId");
            values.add(facultad);

            if (especialidad != 0) {
                hql += "and pc.Esp.Id = :especialidadId ";

                names.add("especialidadId");
                values.add(especialidad);
            }
        }
        hql += "and esp.Fac.Id = fac.Id and pc.PlanActivo = :activo";

        names.add("activo");
        values.add("1");

        return find(Object[].class,
                hql, names.toArray(new String[0]), values.toArray(new Object[0]));
    }

    @Override
    public List<AcPlancurricular> seleccionarPlanActivo(int especialidadId) {
        return findByCriteria(AcPlancurricular.class, DetachedCriteria.forClass(AcPlancurricular.class)
                .add(Restrictions.eq(AcPlancurricular.PROP_PLAN_ACTIVO, "1"))
                .add(Restrictions.eq("Esp.Id", especialidadId))
                .addOrder(Order.desc(AcPlancurricular.PROP_PLAN_CREACION)));
    }

    @Override
    public void eliminarPlanCurricular(String id) {
        this.getHibernateTemplate().execute(session -> {
            var queryString = "update AcPlancurricular set PlanActivo = :newName where Id = :oldName";
            return session.createQuery(queryString)
                    .setParameter("newName", "0")
                    .setParameter("oldName", Integer.parseInt(id))
                    .executeUpdate();
        });
    }

    @Override
    public void actualizarPlanCurricular(AcPlancurricular pc) {
        getHibernateTemplate().update(pc);
    }

    @Override
    public List<AcPlancurricular> seleccionarPlanCurricular(int especialidad) {
        return findByCriteria(AcPlancurricular.class, DetachedCriteria.forClass(AcPlancurricular.class)
                .add(Restrictions.eq("Esp.Id", especialidad))
                .add(Restrictions.eq("PlanActivo", "1"))
                .setFetchMode("AcPlanCursos", FetchMode.JOIN)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
    }

    @Override
    public List<AcPlancurricular> findPlanesByEspecialidades(List<Integer> especialidadIds) {
        // Same criteria as seleccionarPlanCurricular(int) but for many specialties at once (eq -> in).
        // The AcPlanCursos collection is join-fetched (order-by="plancur_ciclo" from the mapping) and
        // DISTINCT_ROOT_ENTITY preserves first-encounter order, so grouping the result by specialty
        // reproduces each per-specialty query's plan and course order exactly.
        return findByCriteria(AcPlancurricular.class, DetachedCriteria.forClass(AcPlancurricular.class)
                .add(Restrictions.in("Esp.Id", especialidadIds))
                .add(Restrictions.eq("PlanActivo", "1"))
                .setFetchMode("AcPlanCursos", FetchMode.JOIN)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
    }
}
