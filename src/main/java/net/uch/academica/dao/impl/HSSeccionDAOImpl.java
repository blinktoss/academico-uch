package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSSeccionDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.*;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSSeccionDAOImpl extends TypedHibernateDao implements HSSeccionDAO {

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> seleccionarSecciones(int curapeId) {
        String sql = "SELECT sec.sec_id, sec.sec_codigo, sec.sec_nombre, sec.sec_vacantes, " +
                "(SELECT COUNT(*)" +
                " FROM ac_matricula mat" +
                " INNER JOIN ac_matricula_curso matcur ON matcur.mat_id = mat.mat_id" +
                " INNER JOIN ac_seccion s ON matcur.sec_id = s.sec_id" +
                " WHERE matcur.matcur_activo = '1'" +
                "  AND mat.mat_activo = '1'" +
                "  AND s.sec_activo = '1'" +
                "  AND mat.mat_tipo = '022001'" +
                "  AND s.sec_id = sec.sec_id) as matriculados " +
                "FROM ac_seccion sec " +
                "WHERE sec.sec_activo = '1' " +
                "AND sec.curape_id = :curape_id";

        return getHibernateTemplate().execute(session -> session.createSQLQuery(sql)
                .setParameter("curape_id", curapeId).list());
    }

    @Override
    public List<AcSeccion> obtenerSecciones(int plancurId, int semestreId) {
        return findByCriteria(AcSeccion.class, DetachedCriteria.forClass(AcSeccion.class).add(Restrictions.eq(AcSeccion.PROP_SEC_ACTIVO, "1")).
                createCriteria("Curape").
                add(Restrictions.eq(AcCursoAperturado.PROP_CURAPE_ACTIVO, "1")).
                add(Restrictions.eq(AcCursoAperturado.PROP_CURAPE_APERTURADO, "1")).
                add(Restrictions.eq("Sem.Id", semestreId)).
                createCriteria("Plancur").
                add(Restrictions.eq(AcPlanCurso.PROP_PLANCUR_ACTIVO, "1")).
                add(Restrictions.eq("Id", plancurId)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public AcSeccion seleccionarSeccion(int seccionId) {
        List<AcSeccion> results = this.getSessionFactory().getCurrentSession().createCriteria(AcSeccion.class)
                .add(Restrictions.eq(AcSeccion.PROP_ID, seccionId))
                .add(Restrictions.eq(AcSeccion.PROP_SEC_ACTIVO, "1"))
                .setFetchMode("Curape", FetchMode.JOIN)
                .setFetchMode("Curape.Plancur", FetchMode.JOIN)
                .setFetchMode("Curape.Sem", FetchMode.JOIN)
                .list();
        AcSeccion seccion = DataAccessUtils.uniqueResult(results);
        if (seccion != null) {
            Hibernate.initialize(seccion.getCurape().getAcSisEvaPersonalizados());
        }
        return seccion;
    }

    @Override
    public List<String> listarSecciones(int especialidadId, int semestreId) {
        return findByCriteria(String.class, DetachedCriteria.forClass(AcSeccion.class, "AcSeccion").add(Restrictions.eq("AcSeccion.SecActivo", "1")).
                createCriteria("AcSeccion.Curape", "AcCursoAperturado").
                add(Restrictions.eq("AcCursoAperturado.Sem.Id", semestreId)).
                add(Restrictions.eq("AcCursoAperturado.CurapeAperturado", "1")).
                add(Restrictions.eq("AcCursoAperturado.CurapeActivo", "1")).
                createCriteria("AcCursoAperturado.Plancur", AcPlanCurso.REF).
                add(Restrictions.eq("AcPlanCurso.PlancurActivo", "1")).
                createCriteria("AcPlanCurso.Cur", "AcCurso").
                add(Restrictions.eq("AcCurso.CurActivo", "1")).
                add(Restrictions.eq("AcCurso.Esp.Id", especialidadId)).
                setProjection(Projections.projectionList().add(Projections.groupProperty("AcSeccion.SecNombre"))));
    }

    @Override
    public List<AcSeccion> listarCursosPorSeccion(String nombre) {
        return findByCriteria(AcSeccion.class, DetachedCriteria.forClass(AcSeccion.class, "AcSeccion").add(Restrictions.eq("AcSeccion.SecActivo", "1")).
                add(Restrictions.eq("AcSeccion.SecNombre", nombre)).
                createCriteria("AcSeccion.Curape", AcCursoAperturado.REF).
                add(Restrictions.eq("AcCursoAperturado.Sem.Id", 9)).
                add(Restrictions.eq("AcCursoAperturado.CurapeAperturado", "1")).
                add(Restrictions.eq("AcCursoAperturado.CurapeActivo", "1")).
                createCriteria("AcCursoAperturado.Plancur", AcPlanCurso.REF).
                add(Restrictions.eq("AcPlanCurso.PlancurActivo", "1")).
                createCriteria("AcPlanCurso.Cur", AcCurso.REF).
                add(Restrictions.eq("AcCurso.CurActivo", "1")));
    }

    @Override
    public List<SpConvalidacionCursos> listaConvalidacion(int especialidadId, int alumnoId) {
        return findByNamedQueryAndNamedParam(SpConvalidacionCursos.class, "sp_convalidacion_cursos", new String[]{"p_esp_id", "p_alu_id"},
                new Object[]{especialidadId, alumnoId});
    }
}
