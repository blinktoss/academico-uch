package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSMatriculaDAO;
import net.uch.academica.jsf.beans.BeanCursos;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.*;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.*;
import org.springframework.dao.support.DataAccessUtils;

import java.util.*;

import static net.uch.common.Constants.*;

public class HSMatriculaDAOImpl extends TypedHibernateDao implements HSMatriculaDAO {

    @Override
    @SuppressWarnings("unchecked")
    public BeanCursos seleccionarCursosAprobados(int alumnoId) {
        var evaluacion = new BeanCursos();
        List<Integer> aprobados = new ArrayList<>();
        List<Integer> desaprobadosId = new ArrayList<>();
        List<AcPlanCurso> desaprobados = new ArrayList<>();

        Criteria criteria = this.getSessionFactory().getCurrentSession().createCriteria(AcActa.class, "AcActa").
                createCriteria("AcActa.AcActaDetalles", AcActa.PROP_AC_ACTA_DETALLES).
                add(Restrictions.eq("AcActaDetalles.AluId", alumnoId)).
                add(Restrictions.disjunction().
                        add(Restrictions.eq(AcActa.PROP_AC_ACTA_DETALLES.concat(".").concat(AcActaDetalle.PROP_ACTDET_TIPO_NOTA), CAT_TIPO_NOTA_PROMEDIO_FINAL)).
                        add(Restrictions.eq(AcActa.PROP_AC_ACTA_DETALLES.concat(".").concat(AcActaDetalle.PROP_ACTDET_TIPO_NOTA), CAT_TIPO_NOTA_CONVALIDACION))).
                createCriteria("AcActa.Sec", AcActa.PROP_SEC).
                createCriteria(AcActa.PROP_SEC + "." + AcSeccion.PROP_CURAPE, AcSeccion.PROP_CURAPE).
                addOrder(Order.desc(AcSeccion.PROP_CURAPE + "." + AcCursoAperturado.PROP_PLANCUR + "." + AcCursoAperturado.PROP_ID));

        var projection = Projections.projectionList();
        projection.add(Projections.property(AcSeccion.PROP_CURAPE + "." + AcCursoAperturado.PROP_PLANCUR + "." + AcCursoAperturado.PROP_ID));//obj[0]
        projection.add(Projections.property("AcActaDetalles.ActdetNota"));//obj[1]
        projection.add(Projections.count(AcActa.PROP_SEC + "." + AcSeccion.PROP_ID));//obj[2]
        projection.add(Projections.property("AcActa.Id"));//obj[3]
        projection.add(Projections.property(AcSeccion.PROP_CURAPE + "." + AcCursoAperturado.PROP_PLANCUR));//obj[4]
        projection.add(Projections.max(AcActa.PROP_SEC + "." + AcSeccion.PROP_ID));//obj[5]
        projection.add(Projections.groupProperty(AcSeccion.PROP_CURAPE + "." + AcCursoAperturado.PROP_PLANCUR + "." + AcCursoAperturado.PROP_ID));
        criteria.setProjection(projection);
        List<Object[]> lista = criteria.list();

        Object[] obj;
        AcPlanCurso plancur;
        for (Object[] objects : lista) {
            obj = objects;
            if ((Long) obj[2] > 1) {
                List<AcActa> li = this.getSessionFactory().getCurrentSession().createCriteria(AcActa.class)
                        .add(Restrictions.eq(AcActa.PROP_SEC + "." + AcSeccion.PROP_ID, obj[5]))
                        .add(Restrictions.eq(AcActa.PROP_ACT_ACTIVO, "1"))
                        .addOrder(Order.desc(AcActa.PROP_ACT_TIPO)).createCriteria(AcActa.PROP_AC_ACTA_DETALLES, AcActa.PROP_AC_ACTA_DETALLES)
                        .add(Restrictions.eq("AcActaDetalles.AluId", alumnoId))
                        .add(Restrictions.eq(AcActa.PROP_AC_ACTA_DETALLES.concat(".").concat(AcActaDetalle.PROP_ACTDET_TIPO_NOTA), CAT_TIPO_NOTA_PROMEDIO_FINAL))
                        .add(Restrictions.eq(AcActa.PROP_AC_ACTA_DETALLES + "." + AcActaDetalle.PROP_ACTDET_ACTIVO, "1"))
                        .list();
                List<AcActaDetalle> list = Collections.synchronizedList(new LinkedList<>(li.get(0).getAcActaDetalles()));
                for (AcActaDetalle acActaDetalle : list) {
                    if (acActaDetalle.getActdetTipoNota().equals(CAT_TIPO_NOTA_PROMEDIO_FINAL) && acActaDetalle.getAluId().equals(alumnoId)) {
                        obj[1] = acActaDetalle.getActdetNota();
                    }
                }
            }
            if ((Float) obj[1] >= 10.5) {
                aprobados.add((Integer) obj[0]);
            } else {
                plancur = (AcPlanCurso) obj[4];
                desaprobados.add(plancur);
                desaprobadosId.add(((AcPlanCurso) obj[4]).getId());
            }
        }
        if (lista.isEmpty() || aprobados.isEmpty()) {
            aprobados.add(-1);
        }
        evaluacion.setAprobados(aprobados);
        evaluacion.setDesaprobados(desaprobados);
        evaluacion.setDesaprobadosId(desaprobadosId);
        return evaluacion;
    }

    @Override
    public List<AcPlanCurso> seleccionarCursosParaIngresantes(int alumnoId) {
        AcAlumno acAlumno = getHibernateTemplate().get(AcAlumno.class, alumnoId);

        List<AcPlanCurso> lista = new ArrayList<>();
        if (acAlumno != null) {
            lista = findByCriteria(AcPlanCurso.class, DetachedCriteria.forClass(AcPlanCurso.class)
                    .add(Restrictions.eq(AcPlanCurso.PROP_PLANCUR_CICLO, "006001"))
                    .add(Restrictions.eq("Plan.Id", acAlumno.getPlanIdActual()))
                    .add(Restrictions.eq(AcPlanCurso.PROP_PLANCUR_ACTIVO, "1")));
        }
        return lista;
    }

    @Override
    public int numeroMatriculadosporSeccion(int seccionId) {
        return this.getHibernateTemplate().findByCriteria(
                DetachedCriteria.forClass(AcMatricula.class)
                        .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1"))
                        .add(Restrictions.disjunction()
                                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_REGULAR))
                                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_PRE_MATRICULA)))
                        .createCriteria(AcMatricula.PROP_AC_MATRICULA_CURSOS)
                        .add(Restrictions.eq(AcMatriculaCurso.PROP_SEC + "." + AcSeccion.PROP_ID, seccionId))
                        .add(Restrictions.eq(AcMatriculaCurso.PROP_MATCUR_ACTIVO, "1"))).size();
    }

    @Override
    public void registrarMatricula(AcMatricula matricula) {
        getHibernateTemplate().saveOrUpdate(matricula);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> lockSectionCapacities(List<Integer> seccionIds) {
        List<Object[]> rows = getHibernateTemplate().execute(session -> session
                .createSQLQuery("SELECT sec_id, sec_vacantes FROM ac_seccion WHERE sec_id IN (:ids) ORDER BY sec_id FOR UPDATE")
                .setParameterList("ids", seccionIds)
                .list());
        Map<Integer, Integer> capacities = new LinkedHashMap<>();
        for (Object[] row : rows) {
            capacities.put(((Number) row[0]).intValue(), ((Number) row[1]).intValue());
        }
        return capacities;
    }

    @Override
    public List<AcMatricula> seleccionarMatriculas(int seccionId) {
        // Typed queries reject the legacy "select mat, matcur, alu" tuple select over fetch
        // joins; fetched associations already travel on the root entity.
        var hql = "from AcMatricula mat " +
                "inner join fetch mat.AcMatriculaCursos matcur " +
                "inner join fetch mat.Alu alu " +
                "where mat.MatActivo = '1' " +
                "and mat.MatTipo = :matTipo " +
                "and matcur.MatcurActivo = '1' " +
                "and alu.AluTipo = :aluTipo " +
                "and matcur.Sec.Id = :seccionId " +
                "order by alu.AluAppaterno asc, alu.AluApmaterno asc, alu.AluNombres asc";
        return find(AcMatricula.class, hql,
                new String[]{"matTipo", "aluTipo", "seccionId"},
                new Object[]{CAT_TIPO_MATRICULA_REGULAR, CAT_TIPO_ALUMNO_ESTUDIANTE, seccionId});
    }

    @Override
    @SuppressWarnings("unchecked")
    public AcMatricula listarCursosPorMatricula(int matriculaId) {
        List<AcMatricula> results = getSessionFactory().getCurrentSession().createCriteria(AcMatricula.class)
                .add(Restrictions.eq("Id", matriculaId))
                .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1"))
                .setFetchMode("AcMatriculaCursos", FetchMode.JOIN)
                .setFetchMode("AcMatriculaCursos.Sec", FetchMode.JOIN)
                .setFetchMode("AcMatriculaCursos.Sec.Curape", FetchMode.JOIN)
                .setFetchMode("AcMatriculaCursos.Sec.Curape.Plancur", FetchMode.JOIN)
                .setFetchMode("AcMatriculaCursos.Sec.Curape.Plancur.Cur", FetchMode.JOIN)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .list();
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public void eliminarMatricula(int matriculaId) {
        // Load-then-update instead of bulk HQL: bulk executeUpdate() bypasses Hibernate's
        // entity-level interceptor, which is what stamps updatedAt/updatedBy for the audit trail.
        AcMatricula matricula = this.getHibernateTemplate().get(AcMatricula.class, matriculaId);
        if (matricula == null) {
            logger.warn(String.format("Matricula %d not found, nothing to deactivate", matriculaId));
            return;
        }
        matricula.setMatActivo("0");
        this.getHibernateTemplate().update(matricula);
    }

    @Override
    public void actualizarTipoMatricula(AcMatricula.Type tipo, int matId) {
        // Load-then-update instead of bulk HQL: bulk executeUpdate() bypasses Hibernate's
        // entity-level interceptor, which is what stamps updatedAt/updatedBy for the audit trail.
        AcMatricula matricula = this.getHibernateTemplate().get(AcMatricula.class, matId);
        if (matricula == null) {
            logger.warn(String.format("Matricula %d not found, type not updated", matId));
            return;
        }
        switch (tipo) {
            case MATRICULA_RECTIFICADA:
                matricula.setMatTipo(CAT_TIPO_MATRICULA_RECTIFICADA);
                break;
            case PRE_MATRICULA_RECTIFICADA:
                matricula.setMatTipo(CAT_TIPO_PRE_MATRICULA_RECTIFICADA);
                break;
            case MATRICULA_ANULADA_RESERVA:
                matricula.setMatTipo(CAT_TIPO_MATRICULA_ANULADA);
                break;
            default:
                logger.warn(String.format("Unregistered index '%s'", tipo));
                break;
        }
        this.getHibernateTemplate().update(matricula);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AcMatricula> seleccionarMatricularRegular(int alumnoId, int semestreId) {
        // Eagerly fetches the AcMatriculaCursos -> Sec -> Curape -> Plancur graph: the bean's
        // rectification flow navigates it after this method's @Transactional scope (the service
        // layer) has closed, so leaving it lazy throws LazyInitializationException there.
        return this.getSessionFactory().getCurrentSession().createCriteria(AcMatricula.class)
                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_REGULAR))
                .add(Restrictions.eq(AcMatricula.PROP_ALU + "." + AcAlumno.PROP_ID, alumnoId))
                .add(Restrictions.eq(AcMatricula.PROP_SEM_ID, semestreId))
                .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1"))
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS, FetchMode.JOIN)
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS + "." + AcMatriculaCurso.PROP_SEC, FetchMode.JOIN)
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS + "." + AcMatriculaCurso.PROP_SEC + "." + AcSeccion.PROP_CURAPE, FetchMode.JOIN)
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS + "." + AcMatriculaCurso.PROP_SEC + "." + AcSeccion.PROP_CURAPE + "." + AcCursoAperturado.PROP_PLANCUR, FetchMode.JOIN)
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS + "." + AcMatriculaCurso.PROP_SEC + "." + AcSeccion.PROP_CURAPE + "." + AcCursoAperturado.PROP_PLANCUR + "." + AcPlanCurso.PROP_CUR, FetchMode.JOIN)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AcMatricula> seleccionarMatriculaReserva(int alumnoId, int semestreId) {
        return this.getSessionFactory().getCurrentSession().createCriteria(AcMatricula.class)
                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_RESERVA))
                .add(Restrictions.eq(AcMatricula.PROP_ALU + "." + AcAlumno.PROP_ID, alumnoId))
                .add(Restrictions.eq(AcMatricula.PROP_SEM_ID, semestreId))
                .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1")).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AcMatricula> seleccionarPrematricula(int alumnoId, int semestreId) {
        // Same eager-fetch rationale as seleccionarMatricularRegular(int, int) above: the bean's
        // Pre-Matricula confirmation flow (cursosAbiertos()'s no-arg JSF action) navigates the
        // AcMatriculaCursos -> Sec -> Curape -> Plancur graph after this method's @Transactional
        // scope has closed - this overload was missing the chain that overload already has,
        // throwing LazyInitializationException whenever that navigation actually runs.
        return this.getSessionFactory().getCurrentSession().createCriteria(AcMatricula.class)
                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_PRE_MATRICULA))
                .add(Restrictions.eq(AcMatricula.PROP_ALU + "." + AcAlumno.PROP_ID, alumnoId))
                .add(Restrictions.eq(AcMatricula.PROP_SEM_ID, semestreId))
                .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1"))
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS, FetchMode.JOIN)
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS + "." + AcMatriculaCurso.PROP_SEC, FetchMode.JOIN)
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS + "." + AcMatriculaCurso.PROP_SEC + "." + AcSeccion.PROP_CURAPE, FetchMode.JOIN)
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS + "." + AcMatriculaCurso.PROP_SEC + "." + AcSeccion.PROP_CURAPE + "." + AcCursoAperturado.PROP_PLANCUR, FetchMode.JOIN)
                .setFetchMode(AcMatricula.PROP_AC_MATRICULA_CURSOS + "." + AcMatriculaCurso.PROP_SEC + "." + AcSeccion.PROP_CURAPE + "." + AcCursoAperturado.PROP_PLANCUR + "." + AcPlanCurso.PROP_CUR, FetchMode.JOIN)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .list();
    }

    // Batched (List-of-ids) variants: used by the list-view lookup (one query for all matched
    // students instead of one query per student per type) - deliberately without the eager fetch
    // joins the single-id overloads above carry, since the list view only reads MatTipo/Id.
    @Override
    @SuppressWarnings("unchecked")
    public List<AcMatricula> seleccionarMatricularRegular(List<Integer> alumnoIds, int semestreId) {
        return this.getSessionFactory().getCurrentSession().createCriteria(AcMatricula.class)
                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_REGULAR))
                .add(Restrictions.in(AcMatricula.PROP_ALU + "." + AcAlumno.PROP_ID, alumnoIds))
                .add(Restrictions.eq(AcMatricula.PROP_SEM_ID, semestreId))
                .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1")).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AcMatricula> seleccionarMatriculaReserva(List<Integer> alumnoIds, int semestreId) {
        return this.getSessionFactory().getCurrentSession().createCriteria(AcMatricula.class)
                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_RESERVA))
                .add(Restrictions.in(AcMatricula.PROP_ALU + "." + AcAlumno.PROP_ID, alumnoIds))
                .add(Restrictions.eq(AcMatricula.PROP_SEM_ID, semestreId))
                .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1")).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AcMatricula> seleccionarPrematricula(List<Integer> alumnoIds, int semestreId) {
        return this.getSessionFactory().getCurrentSession().createCriteria(AcMatricula.class)
                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_PRE_MATRICULA))
                .add(Restrictions.in(AcMatricula.PROP_ALU + "." + AcAlumno.PROP_ID, alumnoIds))
                .add(Restrictions.eq(AcMatricula.PROP_SEM_ID, semestreId))
                .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1")).list();
    }

    @Override
    public int numeroMatriculadosporSeccionSinAlumno(int seccionId, int alumnoId) {
        return this.getHibernateTemplate().findByCriteria(
                DetachedCriteria.forClass(AcMatricula.class)
                        .add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1"))
                        .add(Restrictions.disjunction()
                                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_REGULAR))
                                .add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_PRE_MATRICULA)))
                        .add(Restrictions.not(Restrictions.eq(AcMatricula.PROP_ALU + "." + AcAlumno.PROP_ID, alumnoId)))
                        .createCriteria(AcMatricula.PROP_AC_MATRICULA_CURSOS).add(Restrictions.eq("Sec.Id", seccionId))
                        .add(Restrictions.eq(AcMatriculaCurso.PROP_MATCUR_ACTIVO, "1"))).size();
    }

    @Override
    public AcMatricula seleccionarMatriculaAlumnoConvalidacion(int alumnoId) {
        List<AcMatricula> results = findByCriteria(AcMatricula.class, DetachedCriteria.forClass(AcMatricula.class).
                add(Restrictions.eq(AcMatricula.PROP_MAT_ACTIVO, "1")).
                add(Restrictions.eq(AcMatricula.PROP_MAT_TIPO, CAT_TIPO_MATRICULA_REGULAR)).
                add(Restrictions.eq(AcMatricula.PROP_SEM_ID, 5)).
                add(Restrictions.eq(AcMatricula.PROP_ALU + "." + AcAlumno.PROP_ID, alumnoId)).
                createCriteria(AcMatricula.PROP_AC_MATRICULA_CURSOS).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public AcAmpliaciones findAmpliacion(int studentId, int semesterId) {
        var hql = "from AcAmpliaciones where AmpActivo = '1' " +
                "and Alu.Id = :studentId and SemId = :semesterId";
        List<AcAmpliaciones> results = find(AcAmpliaciones.class,
                hql, new String[]{"studentId", "semesterId"}, new Object[]{studentId, semesterId});
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public long getEnrolledStudentCount(int semesterId) {
        var hql = "select count(distinct m.Alu.Id) from AcMatricula m " +
                "where m.MatActivo = '1' and m.SemId = :semesterId and m.MatTipo in (:tipoRegular, :tipoPreMatricula)";
        return DataAccessUtils.longResult(this.getHibernateTemplate()
                .findByNamedParam(hql, new String[]{"semesterId", "tipoRegular", "tipoPreMatricula"},
                        new Object[]{semesterId, CAT_TIPO_MATRICULA_REGULAR, CAT_TIPO_MATRICULA_PRE_MATRICULA}));
    }
}
