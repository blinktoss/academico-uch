package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSAperturaCursosDAO;
import net.uch.academica.dao.OpenedCourseRow;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcCursoAperturado;
import net.uch.mapping.AcSeccion;
import net.uch.mapping.AcSisEvaPersonalizado;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.LinkedHashSet;
import java.util.List;

public class HSAperturaCursosDAOImpl extends TypedHibernateDao implements HSAperturaCursosDAO {

    @Override
    public void aperturarCurso(AcCursoAperturado cur) {
        getHibernateTemplate().saveOrUpdate(cur);
    }

    @Override
    public AcCursoAperturado verificarCursoAperturado(int plancurId, int semestreId) {
        List<AcCursoAperturado> results = findByCriteria(AcCursoAperturado.class, DetachedCriteria.forClass(AcCursoAperturado.class)
                .add(Restrictions.eq("Plancur.Id", plancurId))
                .add(Restrictions.eq("Sem.Id", semestreId))
                .add(Restrictions.eq(AcCursoAperturado.PROP_CURAPE_ACTIVO, "1")));
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public void actualizarCursoAperturado(AcCursoAperturado cur) {
        getHibernateTemplate().update(cur);
    }

    /**
     * Processing separate queries because in some cases an `AcCursoAperturado` will have assigned
     * `AcSisEvaPersonalizado` but no `AcSeccion` and viceversa, so the if we made an `ìnner join` the query
     * will return `null`.
     *
     * @param curapeId `AcCursoAperturado` id.
     * @return the entity
     */
    @Override
    public AcCursoAperturado seleccionarUnCursosAperturado(int curapeId) {
        var hql = "from AcCursoAperturado curape " +
                "where curape.CurapeActivo = '1' and curape.Id = :curapeId";
        List<AcCursoAperturado> results = find(AcCursoAperturado.class, hql, "curapeId", curapeId);
        AcCursoAperturado curape = DataAccessUtils.uniqueResult(results);
        if (curape == null) {
            return null;
        }

        List<AcSisEvaPersonalizado> sisEvaPersonalizados = find(AcSisEvaPersonalizado.class, "from AcSisEvaPersonalizado sisEvaPer " +
                "where sisEvaPer.SisevaPerActivo = '1' " +
                "and sisEvaPer.Curape.Id = :curapeId", "curapeId", curape.getId());
        curape.setAcSisEvaPersonalizados(new LinkedHashSet<>(sisEvaPersonalizados));

        List<AcSeccion> secciones = find(AcSeccion.class, "from AcSeccion sec " +
                "where sec.SecActivo = '1' " +
                "and sec.Curape.Id = :curapeId", "curapeId", curape.getId());
        curape.setAcSeccions(new LinkedHashSet<>(secciones));
        return curape;
    }

    @Override
    public List<AcCursoAperturado> seleccionarAperturaporEspecialidad(int especialidadId, int semestreId) {
        var hql = "from AcCursoAperturado curape " +
                "inner join fetch curape.Plancur plancur " +
                "inner join fetch plancur.Cur cur " +
                "where curape.CurapeActivo = '1' " +
                "and curape.CurapeAperturado = '1' " +
                "and cur.Esp.Id = :especialidadId " +
                "and curape.Sem.Id = :semestreId " +
                "order by plancur.PlancurCiclo";

        return find(AcCursoAperturado.class, hql,
                new String[]{"especialidadId", "semestreId"}, new Object[]{especialidadId, semestreId});
    }

    @Override
    public List<OpenedCourseRow> findOpenedCoursesBySemesters(List<Integer> semesterIds) {
        var hql = "select new net.uch.academica.dao.OpenedCourseRow(" +
                "cur.Esp.Id, curape.Sem.Id, curape.Id, plancur.PlancurCiclo, cur.CurNombre) " +
                "from AcCursoAperturado curape " +
                "inner join curape.Plancur plancur " +
                "inner join plancur.Cur cur " +
                "where curape.CurapeActivo = '1' " +
                "and curape.CurapeAperturado = '1' " +
                "and curape.Sem.Id in (:semesterIds) " +
                "order by cur.Esp.Id, curape.Sem.Id, plancur.PlancurCiclo";

        return find(OpenedCourseRow.class, hql, "semesterIds", semesterIds);
    }

    @Override
    public void deleteSisEvaPersonalizados(List<Integer> idsToDelete) {
        int r = this.getHibernateTemplate().execute(session -> session.createQuery(
                "delete from AcSisEvaPersonalizado where Id in (:v_ids)")
                .setParameterList("v_ids", idsToDelete)
                .executeUpdate());
        logger.trace("Rows affected: " + r);
    }
}
