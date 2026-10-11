package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSTurnoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcTurno;
import net.uch.mapping.AcTurnoDetalle;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSTurnoDAOImpl extends TypedHibernateDao implements HSTurnoDAO {

    @Override
    public void insertarTurno(AcTurno turno) {
        getHibernateTemplate().save(turno);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> seleccionarTurno(String codigo, String nombre, Integer semestre) {
        var queryString = "SELECT t.Id, t.TurCodigo, t.TurNombre, t.TurPeriodo, t.TurHoraInicio, t.TurVigente, t.SemId, s.SemNombre ,t.TurTiempoPeriodo " +
                "FROM AcTurno AS t, AcSemestre AS s " +
                "WHERE s.Id = t.SemId AND t.TurCodigo LIKE :codigo AND t.TurNombre LIKE :nombre AND t.TurActivo= :activo ";
        var session = getHibernateTemplate().getSessionFactory().openSession();
        Query query;
        if (semestre == 0) {
            query = session.createQuery(queryString).
                    setParameter("codigo", "%" + codigo + "%").
                    setParameter("nombre", "%" + nombre + "%").
                    setParameter("activo", "1");
        } else {
            queryString += "AND t.SemId= :semestre";
            query = session.createQuery(queryString).
                    setParameter("codigo", "%" + codigo + "%").
                    setParameter("nombre", "%" + nombre + "%").
                    setParameter("activo", "1").
                    setParameter("semestre", semestre);
        }
        List<Object[]> results = query.list();
        session.close();
        return results;
    }

    @Override
    public void actualizarTurno(AcTurno turno) {
        getHibernateTemplate().update(turno);
    }

    @Override
    public void eliminarTurno(int id, int semestre) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AcTurnoDetalle.class)
                .add(Restrictions.eq("Tur.Id", id))
                .setProjection(Projections.projectionList()
                        .add(Projections.property(AcTurnoDetalle.PROP_ID), "id"))
                .setResultTransformer(Transformers.aliasToBean(AcTurnoDetalle.class));
        List<AcTurnoDetalle> turnoDetalle = findByCriteria(AcTurnoDetalle.class, criteria);
        var turno = new AcTurno(id, semestre);
        turnoDetalle.forEach(td -> {
            td.setTur(new AcTurno(id));
            turno.addToAcTurnoDetalles(td);
        });
        getHibernateTemplate().delete(turno);
    }

    @Override
    public List<AcTurno> seleccionarTurno(int semestre) {
        return findByCriteria(AcTurno.class, DetachedCriteria.forClass(AcTurno.class).add(Restrictions.eq("SemId", semestre))
                .add(Restrictions.eq(AcTurno.PROP_TUR_ACTIVO, "1")));
    }

    @Override
    public AcTurno obtenerTurno(int id) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AcTurno.class).
                add(Restrictions.eq(AcTurno.PROP_ID, id)).
                add(Restrictions.eq(AcTurno.PROP_TUR_ACTIVO, "1"));

        return (AcTurno) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
    }
}
