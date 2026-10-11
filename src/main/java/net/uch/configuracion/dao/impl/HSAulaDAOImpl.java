package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSAulaDAO;
import net.uch.mapping.AcAula;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSAulaDAOImpl extends TypedHibernateDao implements HSAulaDAO {

    private static final String ACTIVO = "activo";

    @Override
    public Integer insertarAula(AcAula aula) {
        getHibernateTemplate().save(aula);
        return aula.getId();
    }

    @Override
    public List<AcAula> seleccionarAula(String nombre, int local, int pabellon) {
        var c = DetachedCriteria.forClass(AcAula.class, "au")
                .createCriteria("Pab", "p")
                .createCriteria("p.Loc", "l")
                .add(Restrictions.like("au.AulDes", nombre, MatchMode.ANYWHERE))
                .add(Restrictions.eq("au.AulActivo", "1"));
        if (local != 0) {
            c.add(Restrictions.eq("l.Id", local));
        }
        if (pabellon != 0) {
            c.add(Restrictions.eq("p.Id", pabellon));
        }
        return findByCriteria(AcAula.class, c);
    }

    @Override
    public void eliminarAula(Integer id) {
        var hqlUpdate = "update AcAula set AulActivo = :activo where Id = :id";
        int r = getHibernateTemplate().execute(session ->
                session.createQuery(hqlUpdate).setParameter(ACTIVO, "0").setParameter("id", id).executeUpdate());
        logger.trace("Rows affected: " + r);
    }

    @Override
    public void actualizarAula(AcAula aula) {
        getHibernateTemplate().update(aula);
    }

    @Override
    public List<AcAula> seleccionarAula() {
        return findByCriteria(AcAula.class, DetachedCriteria.forClass(AcAula.class).add(Restrictions.eq("AulActivo", "1")));
    }

    @Override
    public AcAula buscarAula(int aulaId) {
        return this.getHibernateTemplate().get(AcAula.class, aulaId);
    }
}
