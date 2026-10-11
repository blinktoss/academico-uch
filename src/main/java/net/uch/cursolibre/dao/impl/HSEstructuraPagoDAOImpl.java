package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSEstructuraPagoDAO;
import net.uch.mapping.ClAlumnoTarifa;
import net.uch.mapping.ClEstructuraPagos;
import net.uch.mapping.ClEstructuraPagosDetalle;
import org.hibernate.criterion.*;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSEstructuraPagoDAOImpl extends TypedHibernateDao implements HSEstructuraPagoDAO {

    @Override
    public List<ClEstructuraPagos> seleccionarEstructuras(String descripcion, int areId, int modId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ClEstructuraPagos.class, "ep").
                createCriteria("ep.clModulo", "mod").
                add(Restrictions.eq("ep." + ClEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        if (areId != 0) {
            criteria.add(Restrictions.eq("mod.clArea.areId", areId));
        }
        if (modId != 0) {
            criteria.add(Restrictions.eq("ep.clModulo.modId", modId));
        }
        if (!descripcion.isEmpty()) {
            criteria.add(Restrictions.like("ep.estpagNombre", descripcion, MatchMode.ANYWHERE));
        }

        return findByCriteria(ClEstructuraPagos.class, criteria);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClEstructuraPagosDetalle> seleccionarDetalle(int estpagId) {
        return this.getSessionFactory().getCurrentSession().createCriteria(ClEstructuraPagosDetalle.class).
                add(Restrictions.eq(ClEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq("clEstructuraPagos.estpagId", estpagId)).list();
    }

    @Override
    public void insertarEstructuraPagos(ClEstructuraPagos estructura) {
        this.getHibernateTemplate().save(estructura);
    }

    @Override
    public void insertarEstructuraPagoDetalle(ClEstructuraPagosDetalle estDetalle) {
        this.getHibernateTemplate().save(estDetalle);
    }

    @Override
    public void actualizarEstructuraPagoDetalle(ClEstructuraPagosDetalle estDetalle) {
        this.getHibernateTemplate().update(estDetalle);
    }

    @Override
    public void actualizarEstructuraPagos(ClEstructuraPagos estructura) {
        this.getHibernateTemplate().update(estructura);
    }

    @Override
    public boolean verificarEliminar(int estpagId) {
        List<Long> results = findByCriteria(Long.class, DetachedCriteria.forClass(ClAlumnoTarifa.class, "ClAlumnoTarifa").createCriteria("ClAlumnoTarifa.clEstructuraPagosDetalle", "ClEstructuraPagosDetalle").
                add(Restrictions.eq("ClAlumnoTarifa.alutarActivo", "1")).
                add(Restrictions.eq("ClEstructuraPagosDetalle.clEstructuraPagos.estpagId", estpagId)).
                setProjection(Projections.rowCount()));
        long r = DataAccessUtils.requiredUniqueResult(results);
        logger.trace("Estructura de Pagos CL: " + r);
        return r != 0;
    }

    @Override
    public void eliminarEstructuraPagos(int estpagId) {
        int r = this.getHibernateTemplate().execute(session -> session.createQuery(
                "update ClEstructuraPagos set estpagActivo = :newName where estpagId = :oldName").
                setParameter("newName", "0").
                setParameter("oldName", estpagId).
                executeUpdate());
        logger.trace("Rows affected: " + r);
    }

    @Override
    public ClEstructuraPagos buscarEstructuraPagos(int estpagId) {
        return this.getHibernateTemplate().get(ClEstructuraPagos.class, estpagId);
    }

    @Override
    public boolean verificarEliminarDetalle(int estpagdetId) {
        List<Long> results = findByCriteria(Long.class, DetachedCriteria.forClass(ClAlumnoTarifa.class).add(Restrictions.eq("alutarActivo", "1")).
                add(Restrictions.eq("clEstructuraPagosDetalle.estpagdetId", estpagdetId)).
                setProjection(Projections.rowCount()));
        long r = DataAccessUtils.requiredUniqueResult(results);
        logger.trace("Estructura de Pagos Detalle CL: " + r);
        return r != 0;
    }

    @Override
    public void eliminarEstructuraPagosDetalle(List<Integer> estpagdetIds) {
        int r = this.getHibernateTemplate().execute(session -> session.createQuery(
                "update ClEstructuraPagosDetalle set estpagdetActivo = :active where estpagdetId in (:ids)").
                setParameter("active", "0").
                setParameterList("ids", estpagdetIds).executeUpdate());
        logger.trace("Rows affected: " + r);
    }

    @Override
    public List<ClEstructuraPagos> seleccionarEstructurasXModulo(int modId) {
        return findByCriteria(ClEstructuraPagos.class, DetachedCriteria.forClass(ClEstructuraPagos.class).add(Restrictions.eq(ClEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq("clModulo.modId", modId)).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
    }

    @Override
    public List<ClEstructuraPagos> seleccionarEstructurasXTaller(int talId) {
        return findByCriteria(ClEstructuraPagos.class, DetachedCriteria.forClass(ClEstructuraPagos.class, ClEstructuraPagos.REF).createCriteria("estpag.clEstructuraPagosDetalles", ClEstructuraPagosDetalle.REF).
                add(Restrictions.eq(ClEstructuraPagos.REF + "." + ClEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(ClEstructuraPagosDetalle.REF + "." + ClEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq(ClEstructuraPagosDetalle.REF + ".talId", talId)).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
    }

    @Override
    public List<ClEstructuraPagosDetalle> listaEstructurasExistentes(int modId, int estpagId, int talId) {
        DetachedCriteria c = DetachedCriteria.forClass(ClEstructuraPagosDetalle.class, ClEstructuraPagosDetalle.REF).
                createCriteria(ClEstructuraPagosDetalle.REF + ".clEstructuraPagos", ClEstructuraPagos.REF).
                add(Restrictions.eq(ClEstructuraPagos.REF + "." + ClEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq(ClEstructuraPagosDetalle.REF + "." + ClEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq("estpag.estpagId", estpagId)).
                add(Restrictions.eq("estpag.clModulo.modId", modId));

        if (talId != -1) {
            c.add(Restrictions.eq(ClEstructuraPagosDetalle.REF + ".talId", talId));
        }

        c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return findByCriteria(ClEstructuraPagosDetalle.class, c);
    }

    @Override
    public List<ClAlumnoTarifa> listaAlumnoTarifaExistentes(int aluId, int modId, int estpagId) {
        return findByCriteria(ClAlumnoTarifa.class, DetachedCriteria.forClass(ClAlumnoTarifa.class, ClAlumnoTarifa.REF).createCriteria("alutar.clEstructuraPagosDetalle", ClEstructuraPagosDetalle.REF).
                createCriteria(ClEstructuraPagosDetalle.REF + ".clEstructuraPagos", ClEstructuraPagos.REF).
                add(Restrictions.eq("alutar.alutarActivo", "1")).
                add(Restrictions.eq(ClEstructuraPagosDetalle.REF + "." + ClEstructuraPagosDetalle.PROP_ESTPAGDET_ACTIVO, "1")).
                add(Restrictions.eq(ClEstructuraPagos.REF + "." + ClEstructuraPagos.PROP_ESTPAG_ACTIVO, "1")).
                add(Restrictions.eq("alutar.clAlumno.aluId", aluId)).
                add(Restrictions.eq("estpag.estpagId", estpagId)).
                add(Restrictions.eq("estpag.clModulo.modId", modId)));
    }
}
