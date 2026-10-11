package net.uch.administrativa.dao.impl;

import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AdAlumnoTarifa;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.*;

import java.util.List;

public class HSAlumnoTarifaDAOImpl extends TypedHibernateDao implements HSAlumnoTarifaDAO {

    @Override
    @SuppressWarnings("unchecked")
    public List<AdAlumnoTarifa> seleccionarAlumnoTarifa(String codAlu, String idEstado, int idEsp, int idConPag, String apPaterno, String apMaterno, int semId) {
        return fetchForView(ordered(criteriaFor(codAlu, idEstado, idEsp, idConPag, apPaterno, apMaterno, semId))).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AdAlumnoTarifa> seleccionarAlumnoTarifa(String codAlu, String idEstado, int idEsp, int idConPag, String apPaterno, String apMaterno, int semId, int first, int max) {
        return fetchForView(ordered(criteriaFor(codAlu, idEstado, idEsp, idConPag, apPaterno, apMaterno, semId)))
                .setFirstResult(first).setMaxResults(max).list();
    }

    @Override
    public long contarAlumnoTarifa(String codAlu, String idEstado, int idEsp, int idConPag, String apPaterno, String apMaterno, int semId) {
        Number count = (Number) criteriaFor(codAlu, idEstado, idEsp, idConPag, apPaterno, apMaterno, semId)
                .setProjection(Projections.rowCount()).uniqueResult();
        return count == null ? 0L : count.longValue();
    }

    /**
     * Shared filtered criteria for the internal (tariff-linked) payment search: the to-one joins to
     * Estpagdet/Estpag/Alu plus the active-row and optional filters. Uses {@code createAlias} (which
     * returns the root criteria) so callers layer order, paging, projection or fetch joins onto the
     * same root — the same predicate backs both the page query and its count. All joins are to-one,
     * so the row count is not inflated.
     */
    private Criteria criteriaFor(String codAlu, String idEstado, int idEsp, int idConPag, String apPaterno, String apMaterno, int semId) {
        Criteria criteria = this.getSessionFactory().getCurrentSession().createCriteria(AdAlumnoTarifa.class, "AdAlumnoTarifa")
                .createAlias("AdAlumnoTarifa.Estpagdet", "AdEstructuraPagosDetalle")
                .createAlias("AdEstructuraPagosDetalle.Estpag", "AdEstructuraPagos")
                .createAlias("AdAlumnoTarifa.Alu", "Alu")
                .add(Restrictions.eq("AdEstructuraPagosDetalle.EstpagdetActivo", "1"))
                .add(Restrictions.eq("AdEstructuraPagos.EstpagActivo", "1"))
                .add(Restrictions.eq("AdAlumnoTarifa.AlutarActivo", "1"));
        if (apPaterno.trim().length() != 0) {
            criteria.add(Restrictions.like("Alu.AluAppaterno", apPaterno, MatchMode.ANYWHERE));
        }
        if (apMaterno.trim().length() != 0) {
            criteria.add(Restrictions.like("Alu.AluApmaterno", apMaterno, MatchMode.ANYWHERE));
        }
        if (codAlu.trim().length() != 0) {
            criteria.add(Restrictions.eq("Alu.AluCodigo", codAlu));
        }
        if (idConPag != 0) {
            criteria.add(Restrictions.eq("AdAlumnoTarifa.Conpag.Id", idConPag));
        }
        if (idEsp != 0) {
            criteria.add(Restrictions.eq("Alu.Esp.Id", idEsp));
        }
        if (!idEstado.equals("-1")) {
            criteria.add(Restrictions.eq("AdAlumnoTarifa.AlutarEstado", idEstado));
        }
        if (semId != 0) {
            criteria.add(Restrictions.eq("AdEstructuraPagos.Sem.Id", semId));
        }
        return criteria;
    }

    private Criteria ordered(Criteria criteria) {
        return criteria.addOrder(Order.asc("Alu.Id")).addOrder(Order.asc("AdAlumnoTarifa.AlutarFechaPago"));
    }

    /**
     * Eager-fetches the to-one associations the payment listing renders (structure → semester /
     * specialty / faculty, plus student and concept) so the managed bean can read them after this
     * query's transaction closes without a lazy-init error. All to-one, so paging stays correct.
     */
    private Criteria fetchForView(Criteria criteria) {
        return criteria
                .setFetchMode("Estpagdet", FetchMode.JOIN)
                .setFetchMode("Estpagdet.Estpag", FetchMode.JOIN)
                .setFetchMode("Estpagdet.Estpag.Sem", FetchMode.JOIN)
                .setFetchMode("Estpagdet.Estpag.Esp", FetchMode.JOIN)
                .setFetchMode("Estpagdet.Estpag.Esp.Fac", FetchMode.JOIN)
                .setFetchMode("Alu", FetchMode.JOIN)
                .setFetchMode("Conpag", FetchMode.JOIN);
    }

    @Override
    public void insertarTotal(List<AdAlumnoTarifa> alumnoTarifas) {
        int r = this.getHibernateTemplate().execute(session -> {
            int size = alumnoTarifas.size();
            for (var i = 0; i < size; i++) {
                AdAlumnoTarifa alutar = alumnoTarifas.get(i);
                session.save(alutar);
                if (i % 20 == 0) { //20, same as the JDBC batch size
                    //flush a batch of inserts and release memory:
                    session.flush();
                    session.clear();
                }
            }
            return size;
        });
        logger.trace("Total rows affected: " + r);
    }

    @Override
    public AdAlumnoTarifa seleccionarUnAlumnoTarifa(int alutarId) {
        return getHibernateTemplate().get(AdAlumnoTarifa.class, alutarId);
    }

    @Override
    public void cambiarEstado(int id, String estado) {
        int r = this.getHibernateTemplate().execute(session -> {
            var query = "update AdAlumnoTarifa set AlutarEstado=:estado where Id= :id";
            return session.createQuery(query)
                    .setParameter("id", id)
                    .setParameter("estado", estado).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public void actualizarAlumnoTarifa(AdAlumnoTarifa alumnoTarifa) {
        getHibernateTemplate().update(alumnoTarifa);
    }

    @Override
    public List<AdAlumnoTarifa> seleccionarAlumnoTarifa(int alumnoId) {
        return findByCriteria(AdAlumnoTarifa.class, DetachedCriteria.forClass(AdAlumnoTarifa.class, "AdAlumnoTarifa").
                createCriteria("AdAlumnoTarifa.Estpagdet", "AdEstructuraPagosDetalle").
                createCriteria("AdEstructuraPagosDetalle.Estpag", "AdEstructuraPagos").
                add(Restrictions.eq("AdAlumnoTarifa.Alu.Id", alumnoId)).
                add(Restrictions.eq("AdEstructuraPagosDetalle.EstpagdetActivo", "1")).
                add(Restrictions.eq("AdEstructuraPagos.EstpagActivo", "1")).
                add(Restrictions.eq("AdAlumnoTarifa.AlutarActivo", "1")).
                addOrder(Order.asc("Id")));
    }
}
