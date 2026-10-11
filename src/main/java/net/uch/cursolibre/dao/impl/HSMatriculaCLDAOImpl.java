package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSMatriculaCLDAO;
import net.uch.mapping.ClAlumno;
import net.uch.mapping.ClMatricula;
import net.uch.mapping.ClMatriculaTaller;
import net.uch.mapping.ClSeccion;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.*;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSMatriculaCLDAOImpl extends TypedHibernateDao implements HSMatriculaCLDAO {

    @Override
    @SuppressWarnings("unchecked")
    public List<ClMatricula> seleccionarMatriculas(Integer aluId) {
        Criteria root = this.getSessionFactory().getCurrentSession().createCriteria(ClMatricula.class, ClMatricula.REF);
        root.createCriteria(ClMatricula.REF + ".clAlumno", ClAlumno.REF).
                add(Restrictions.eq(ClAlumno.REF + "." + ClAlumno.PROP_ALU_ACTIVO, "1")).
                add(Restrictions.eq(ClAlumno.REF + "." + ClAlumno.PROP_ALU_ID, aluId));
        root.add(Restrictions.eq(ClMatricula.REF + "." + ClMatricula.PROP_MAT_ACTIVO, "1")).
                addOrder(Order.desc(ClMatricula.REF + ".matFecha")).
                setFetchMode("clMatriculaTallers", FetchMode.JOIN).
                setFetchMode("clMatriculaTallers.clSeccion", FetchMode.JOIN).
                setFetchMode("clMatriculaTallers.clSeccion.clTallerAperturado", FetchMode.JOIN).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return root.list();
    }

    @Override
    public void insertarMatricula(ClMatricula matricula) {
        this.getHibernateTemplate().save(matricula);
    }

    @Override
    public void insertarMatriculas(List<ClMatricula> matriculas) {
        for (ClMatricula matricula : matriculas) {
            this.getHibernateTemplate().saveOrUpdate(matricula);
        }
    }

    @Override
    public void anularMatricula(int matId) {
        var hqlUpdate = "update ClMatricula set matActivo = :activo where matId = :id";
        this.getSessionFactory().getCurrentSession().createQuery(hqlUpdate).
                setParameter("activo", "0").
                setParameter("id", matId).executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClMatricula> totalMatriculadosSeccion(int secId, int talapeId, int caso) {
        Criteria c = this.getSessionFactory().getCurrentSession().createCriteria(ClMatricula.class, ClMatricula.REF).
                createCriteria(ClMatricula.REF + "." + ClMatricula.PROP_MAT_TALLERES, ClMatriculaTaller.REF).
                createCriteria(ClMatricula.REF + ".clAlumno", ClAlumno.REF).
                createCriteria(ClMatriculaTaller.REF + "." + ClMatriculaTaller.PROP_SECCION, ClSeccion.REF).
                createCriteria(ClSeccion.REF + ".clTallerAperturado", "talape").
                add(Restrictions.eq(ClMatricula.REF + "." + ClMatricula.PROP_MAT_ACTIVO, "1")).
                add(Restrictions.eq(ClAlumno.REF + "." + ClAlumno.PROP_ALU_ACTIVO, "1")).
                add(Restrictions.eq(ClMatriculaTaller.REF + "." + ClMatriculaTaller.PROP_MATTAL_ACTIVO, "1")).
                add(Restrictions.eq(ClMatricula.REF + ".matTipo", "022001")).
                add(Restrictions.eq(ClSeccion.REF + "." + ClSeccion.PROP_SEC_ACTIVO, "1")).
                add(Restrictions.eq(ClSeccion.REF + ".secId", secId));

        if (caso == 0) {
            c.add(Restrictions.eq("talape.talapeId", talapeId));
        } else if (caso == 1) {
            c.addOrder(Order.asc(ClAlumno.REF + ".aluAppaterno")).
                    addOrder(Order.asc(ClAlumno.REF + ".aluApmaterno")).
                    addOrder(Order.asc(ClAlumno.REF + ".aluNombres"));
        }

        return c.list();
    }

    @Override
    public List<ClMatricula> listarMatriculadoTaller(int aluId, int talId) {
        return findByCriteria(ClMatricula.class, DetachedCriteria.forClass(ClMatricula.class, ClMatricula.REF).createCriteria(ClMatricula.REF + "." + ClMatricula.PROP_MAT_TALLERES, ClMatriculaTaller.REF).
                createCriteria(ClMatriculaTaller.REF + "." + ClMatriculaTaller.PROP_SECCION, ClSeccion.REF).
                createCriteria(ClSeccion.REF + ".clTallerAperturado", "talape").
                add(Restrictions.eq(ClMatricula.REF + "." + ClMatricula.PROP_MAT_ACTIVO, "1")).
                add(Restrictions.eq(ClMatriculaTaller.REF + "." + ClMatriculaTaller.PROP_MATTAL_ACTIVO, "1")).
                add(Restrictions.eq(ClSeccion.REF + "." + ClSeccion.PROP_SEC_ACTIVO, "1")).
                add(Restrictions.eq("talape.talapeActivo", "1")).
                add(Restrictions.eq(ClMatricula.REF + ".clAlumno.aluId", aluId)).
                add(Restrictions.eq("talape.clTaller.talId", talId)));
    }

    @Override
    public boolean estaMatriculado(int aluId, int secId) {
        List<Long> results = findByCriteria(Long.class, DetachedCriteria.forClass(ClAlumno.class, ClAlumno.REF).createCriteria(ClAlumno.REF + ".clMatriculas", ClMatricula.REF).
                createCriteria(ClMatricula.REF + "." + ClMatricula.PROP_MAT_TALLERES, ClMatriculaTaller.REF).
                createCriteria(ClMatriculaTaller.REF + "." + ClMatriculaTaller.PROP_SECCION, ClSeccion.REF).
                add(Restrictions.eq(ClAlumno.REF + "." + ClAlumno.PROP_ALU_ACTIVO, "1")).
                add(Restrictions.eq(ClMatricula.REF + "." + ClMatricula.PROP_MAT_ACTIVO, "1")).
                add(Restrictions.eq(ClMatriculaTaller.REF + "." + ClMatriculaTaller.PROP_MATTAL_ACTIVO, "1")).
                add(Restrictions.eq(ClSeccion.REF + "." + ClSeccion.PROP_SEC_ACTIVO, "1")).
                add(Restrictions.eq(ClAlumno.REF + ".aluId", aluId)).
                add(Restrictions.eq(ClSeccion.REF + ".secId", secId)).
                setProjection(Projections.rowCount()));
        long r = DataAccessUtils.requiredUniqueResult(results);
        logger.trace(String.format("Matriculas del alumno: %d", r));
        return r != 0;
    }

    @Override
    public void actualizarEstadoMatricula(String matTipo, Integer matId) {
        var hql = this.getSessionFactory().getCurrentSession().createQuery(
                "update ClMatricula set matTipo = :mat_tipo "
                        + "where matId = :mat_id and matActivo = '1'");

        hql.setParameter("mat_tipo", matTipo).setParameter("mat_id", matId);

        int r = hql.executeUpdate();
        logger.trace("Rows affected: " + r);
    }
}
