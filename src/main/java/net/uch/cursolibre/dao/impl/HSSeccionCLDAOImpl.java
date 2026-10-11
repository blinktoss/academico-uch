/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSSeccionCLDAO;
import net.uch.mapping.ClMatriculaTaller;
import net.uch.mapping.ClSeccion;
import org.hibernate.FetchMode;
import org.hibernate.criterion.*;
import org.springframework.dao.support.DataAccessUtils;

import java.util.Date;
import java.util.List;

/**
 * @author cesardl
 */
public class HSSeccionCLDAOImpl extends TypedHibernateDao implements HSSeccionCLDAO {

    @Override
    public ClSeccion seleccionarSeccion(Integer seccionId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ClSeccion.class, "s")
                .createCriteria("s.clTallerAperturado", "ta")
                .createCriteria("ta.clTaller", "t")
                .createCriteria("t.clCurso", "c")
                .add(Restrictions.eq("s.secId", seccionId))
                .add(Restrictions.eq("s.secActivo", "1"))
                .add(Restrictions.eq("ta.talapeActivo", "1"))
                .add(Restrictions.eq("t.talActivo", "1"))
                .add(Restrictions.eq("c.curActivo", "1"));

        List<ClSeccion> results = findByCriteria(ClSeccion.class, criteria);
        return DataAccessUtils.requiredSingleResult(results);
    }

    @Override
    public List<ClSeccion> seleccionarSecciones(int talapeId) {
        return findByCriteria(ClSeccion.class, DetachedCriteria.forClass(ClSeccion.class).add(Restrictions.eq("secActivo", "1")).
                add(Restrictions.eq("clTallerAperturado.talapeId", talapeId)).
                addOrder(Order.desc("secFinicio")));
    }

    @Override
    public List<ClSeccion> listarSeccionXTaller(int tallerId) {
        var current = new Date();
        return findByCriteria(ClSeccion.class, DetachedCriteria.forClass(ClSeccion.class, "sec").createCriteria("sec.clTallerAperturado", "talape").
                createCriteria("talape.clTaller", "tal").
                add(Restrictions.eq("talape.talapeActivo", "1")).
                add(Restrictions.eq("talape.talapeVigente", "1")).
                add(Restrictions.eq("talape.talapeAperturado", "1")).
                add(Restrictions.eq("tal.talActivo", "1")).
                add(Restrictions.eq("tal.talId", tallerId)).
                add(Restrictions.eq("sec.secActivo", "1")).
                add(Restrictions.le("sec.secFinicioMatricula", current)).
                add(Restrictions.ge("sec.secFfinMatricula", current)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClSeccion> seleccionarSeccionesXModulo(int modId) {
        var root = this.getSessionFactory().getCurrentSession().createCriteria(ClSeccion.class, "sec");
        root.createCriteria("sec.clTallerAperturado", "talape").
                createCriteria("talape.clTaller", "tal").
                createCriteria("tal.clCurso", "cur").
                createCriteria("cur.clModulo", "modulo").
                add(Restrictions.eq("talape.talapeActivo", "1")).
                add(Restrictions.eq("talape.talapeVigente", "1")).
                add(Restrictions.eq("talape.talapeAperturado", "1")).
                add(Restrictions.eq("tal.talActivo", "1")).
                add(Restrictions.eq("cur.curActivo", "1")).
                add(Restrictions.eq("modulo.modId", modId));
        return root.add(Restrictions.eq("sec.secActivo", "1")).
                add(Restrictions.le("sec.secFinicioMatricula", new Date())).
                add(Restrictions.ge("sec.secFfinMatricula", new Date())).
                setFetchMode("clHorarias", FetchMode.JOIN).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    public void insertarSeccion(ClSeccion sec) {
        this.getHibernateTemplate().save(sec);
    }

    @Override
    public void actualizarSeccion(ClSeccion sec) {
        this.getHibernateTemplate().update(sec);
    }

    @Override
    public void eliminarSeccion(int secId) {
        this.getHibernateTemplate().execute(session ->
                session.createQuery("update ClSeccion set secActivo = '0' where secId = :sec_id")
                        .setParameter("sec_id", secId).executeUpdate());
    }

    @Override
    public boolean existenMatriculasSeccion(int secId) {
        List<Long> results = findByCriteria(Long.class, DetachedCriteria.forClass(ClMatriculaTaller.class, "ClMatriculaTaller").createCriteria("ClMatriculaTaller.clMatricula", "ClMatricula").
                add(Restrictions.eq("ClMatricula.matActivo", "1")).
                add(Restrictions.eq("ClMatriculaTaller.mattalActivo", "1")).
                add(Restrictions.eq("ClMatriculaTaller.clSeccion.secId", secId)).
                setProjection(Projections.rowCount()));
        long x = DataAccessUtils.requiredUniqueResult(results);
        logger.trace("Alumnos matriculados: " + x);
        return x != 0;
    }
}
