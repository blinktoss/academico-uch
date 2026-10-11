package net.uch.administrativa.dao.impl;

import net.uch.administrativa.dao.HSPagoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AdComprobantePago;
import net.uch.mapping.AdConceptoPago;
import net.uch.mapping.AdPago;
import net.uch.mapping.ClAlumnoTarifa;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.dao.support.DataAccessUtils;

import javax.persistence.TemporalType;
import java.util.*;

import static net.uch.common.Constants.CAT_ESTADO_BOLETA_VALIDO;
import static net.uch.common.Constants.CAT_TIPO_CLIENTE_ALUMNO_CP;

public class HSPagoDAOImpl extends TypedHibernateDao implements HSPagoDAO {

    private static final String PROCEDENCIA_VALE = "015004";

    @Override
    public int insertarComprobantePago(AdComprobantePago compag) {
        return insertarComprobantePago(compag, false);
    }

    @Override
    public int insertarComprobantePago(AdComprobantePago compag, boolean allocateNumber) {
        // VALE (015004) numbers are the manually-entered physical vale document number and use a
        // separate scheme (excluded from the sequence) — never re-stamp them.
        if (allocateNumber && !PROCEDENCIA_VALE.equals(compag.getCompagProcedencia())) {
            String preview = compag.getCompagNro();          // "SSS-NNNNNNN" form-open preview
            String serie = preview.substring(0, 3);
            String allocated = allocateComprobanteNro(serie, compag.getCompagTipDoc());
            compag.setCompagNro(serie + "-" + allocated);
        }
        this.getHibernateTemplate().save(compag);
        return compag.getId();
    }

    /**
     * Atomically allocates the next voucher number for (serie, tip_doc) via the shared
     * sp_allocate_voucher_number procedure (V13), on the current Hibernate session's connection
     * so it shares this transaction: the row lock serialises concurrent allocators (across both
     * apps), and a rollback reverts the counter — no duplicates, no gaps. The allocation logic
     * lives once, at the database — the only seam shared with sigu_caja's raw-JDBC stack.
     */
    private String allocateComprobanteNro(String serie, String tipDoc) {
        Integer next = this.getHibernateTemplate().execute(session ->
                session.doReturningWork(connection -> {
                    try (var cs = connection.prepareCall("{call sp_allocate_voucher_number(?, ?, ?)}")) {
                        cs.setString(1, serie);
                        cs.setString(2, tipDoc);
                        cs.registerOutParameter(3, java.sql.Types.INTEGER);
                        cs.execute();
                        return cs.getInt(3);
                    }
                }));
        return String.format("%07d", next);
    }

    @Override
    public void insertarComprobantesPago(List<AdComprobantePago> compags) {
        for (AdComprobantePago compag : compags) {
            // Batch rows (banco import) arrive hand-numbered from the form's start number.
            // That is only a preview: stamp each voucher from the shared counter inside this
            // transaction, like the single-voucher flows, so a batch can never collide with
            // concurrent cashiers in either app. VALE keeps its physical document number.
            // Entities are updated in place, so callers read back the persisted numbers.
            if (!PROCEDENCIA_VALE.equals(compag.getCompagProcedencia())) {
                String serie = compag.getCompagNro().substring(0, 3);
                compag.setCompagNro(serie + "-" + allocateComprobanteNro(serie, compag.getCompagTipDoc()));
            }
            this.getHibernateTemplate().saveOrUpdate(compag);
        }
    }

    @Override
    public List<AdPago> seleccionarMostrarPagos(int id) {
        return findByCriteria(AdPago.class, DetachedCriteria.forClass(AdPago.class).add(Restrictions.eq(AdPago.PROP_PAG_ACTIVO, "1")).
                add(Restrictions.eq(AdPago.PROP_ALUTAR_ID, id)).
                add(Restrictions.not(Restrictions.eq(AdPago.PROP_CONPAG + "." + AdConceptoPago.PROP_ID, 49))).
                add(Restrictions.not(Restrictions.eq(AdPago.PROP_CONPAG + "." + AdConceptoPago.PROP_ID, 23))).
                createCriteria("Compag").
                add(Restrictions.eq("CompagActivo", "1")));
    }

    @Override
    public List<AdPago> seleccionarMostrarPagosMora(int id) {
        return findByCriteria(AdPago.class, DetachedCriteria.forClass(AdPago.class).add(Restrictions.eq("PagActivo", "1")).
                add(Restrictions.eq(AdPago.PROP_ALUTAR_ID, id)).
                createCriteria("Compag").
                add(Restrictions.eq("CompagActivo", "1")));
    }

    @Override
    public List<AdPago> seleccionarPagosPorConcepto(int alutarId, int conpagId) {
        return findByCriteria(AdPago.class, DetachedCriteria.forClass(AdPago.class).
                add(Restrictions.eq(AdPago.PROP_PAG_ACTIVO, "1")).
                add(Restrictions.eq(AdPago.PROP_ALUTAR_ID, alutarId)).
                add(Restrictions.eq(AdPago.PROP_CONPAG + "." + AdConceptoPago.PROP_ID, conpagId)).
                createCriteria("Compag").
                add(Restrictions.eq("CompagActivo", "1")).
                add(Restrictions.eq("CompagTipo", CAT_ESTADO_BOLETA_VALIDO)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String seleccionarUnComprobantePago(String comp) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        var nroComp = "0";
        List<AdComprobantePago> lista = session.createCriteria(AdComprobantePago.class).
                add(Restrictions.eq(AdComprobantePago.PROP_COMPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdComprobantePago.PROP_COMPAG_NRO, comp)).
                addOrder(Order.asc(AdComprobantePago.PROP_ID)).list();
        logger.trace(String.format("Total de comprobantes %d", lista.size()));
        if (!lista.isEmpty()) {
            nroComp = lista.get(0).getCompagNro();
        }
        session.close();
        return nroComp;
    }

    @Override
    public AdComprobantePago seleccionarUnComprobantePago(int compId) {
        List<AdComprobantePago> results = findByCriteria(AdComprobantePago.class, DetachedCriteria.forClass(AdComprobantePago.class).add(Restrictions.eq(AdComprobantePago.PROP_COMPAG_ACTIVO, "1")).
                add(Restrictions.eq(AdComprobantePago.PROP_ID, compId)).
                addOrder(Order.asc(AdComprobantePago.PROP_ID)));
        return DataAccessUtils.requiredUniqueResult(results);
    }

    @Override
    public void actualizarComprobantePago(AdComprobantePago compag) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AdComprobantePago " +
                    "set CompagNro=:v_num_com, " +
                    "CompagFecha=:v_fecha, " +
                    "CompagProcedencia=:v_proc, " +
                    "CompagTipDoc=:v_tipCom, " +
                    "CompagVoucherFecha=:v_fecVou, " +
                    "CompagVoucherCodAgencia=:v_codAge, " +
                    "CompagVoucherNroOperacion=:v_nroVou " +
                    "where Id = :v_id";
            return session.createQuery(hqlUpdate)
                    .setParameter("v_num_com", compag.getCompagNro())
                    .setParameter("v_fecha", compag.getCompagFecha(), TemporalType.DATE)
                    .setParameter("v_proc", compag.getCompagProcedencia())
                    .setParameter("v_tipCom", compag.getCompagTipDoc())
                    .setParameter("v_fecVou", compag.getCompagVoucherFecha(), TemporalType.DATE)
                    .setParameter("v_codAge", compag.getCompagVoucherCodAgencia())
                    .setParameter("v_nroVou", compag.getCompagVoucherNroOperacion())
                    .setParameter("v_id", compag.getId()).executeUpdate();
        });
        logger.trace("Invoices updated: " + r);
    }

    /**
     * Read-only twin of {@link #allocateComprobanteNro}: the number the allocator will issue
     * next for (serie, tip_doc), via sp_preview_voucher_number (V14) — shared with sigu_caja so
     * both apps always display the same form-open preview. Takes no lock and never advances the
     * counter; the commit-time allocation stays the only authoritative stamp. Named outside the
     * get*, find*, load* read-only rules on purpose: Connector/J (noAccessToProcedureBodies=true)
     * rejects CALL on a read-only connection, so this runs under the plain catch-all rule.
     */
    @Override
    public String previewComprobanteNro(String serie, String tipDoc) {
        Integer next = this.getHibernateTemplate().execute(session ->
                session.doReturningWork(connection -> {
                    try (var cs = connection.prepareCall("{call sp_preview_voucher_number(?, ?, ?)}")) {
                        cs.setString(1, serie);
                        cs.setString(2, tipDoc);
                        cs.registerOutParameter(3, java.sql.Types.INTEGER);
                        cs.execute();
                        return cs.getInt(3);
                    }
                }));
        return String.format("%07d", next);
    }

    @Override
    public List<AdComprobantePago> seleccionarMostrarPagosExternos(Date desde, Date hasta, int conpagId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AdComprobantePago.class, "compag").
                createCriteria("compag.AdPagos", "pag").
                createCriteria("pag.Conpag", "conpag").
                add(Restrictions.between("compag.CompagFecha", desde, hasta)).
                add(Restrictions.eq("compag.CompagActivo", "1")).
                add(Restrictions.eq("pag.AlutarId", 0));
        if (conpagId != 0) {
            criteria.add(Restrictions.eq("conpag.Id", conpagId));
        }
        criteria.addOrder(Order.desc("pag.PagFecha")).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return findByCriteria(AdComprobantePago.class, criteria);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AdComprobantePago> seleccionarMostrarPagosExternos(Date desde, Date hasta, int conpagId, int first, int max) {
        // Step 1: a page of DISTINCT comprobante ids. The base query inner-joins the pagos
        // collection to filter, so paging the joined rows would be wrong; group by (id, voucher
        // date) to collapse to one row per comprobante and page/order on that.
        List<Object[]> idRows = externosCriteria(desde, hasta, conpagId)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("compag." + AdComprobantePago.PROP_ID))
                        .add(Projections.groupProperty("compag.CompagFecha")))
                .addOrder(Order.desc("compag.CompagFecha"))
                .addOrder(Order.desc("compag." + AdComprobantePago.PROP_ID))
                .setFirstResult(first).setMaxResults(max).list();
        List<Integer> ids = new ArrayList<>(idRows.size());
        for (Object[] row : idRows) {
            ids.add((Integer) row[0]);
        }
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        // Step 2: fetch that page of comprobantes with the original query shape (same joins load the
        // AdPagos the caller maps), restricted to the page ids and re-ordered ('in' loses order).
        return externosCriteria(desde, hasta, conpagId)
                .add(Restrictions.in("compag." + AdComprobantePago.PROP_ID, ids))
                .addOrder(Order.desc("compag.CompagFecha"))
                .addOrder(Order.desc("compag." + AdComprobantePago.PROP_ID))
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public long contarPagosExternos(Date desde, Date hasta, int conpagId) {
        Number count = (Number) externosCriteria(desde, hasta, conpagId)
                .setProjection(Projections.countDistinct("compag." + AdComprobantePago.PROP_ID))
                .uniqueResult();
        return count == null ? 0L : count.longValue();
    }

    /**
     * Shared filtered criteria for the external (unlinked, {@code AlutarId = 0}) payment search:
     * comprobante joined to its pagos and their concept, date-bounded and active. Callers layer on
     * projection/order/paging so the id-page query, its count and the entity fetch share one predicate.
     */
    private Criteria externosCriteria(Date desde, Date hasta, int conpagId) {
        Criteria criteria = this.getSessionFactory().getCurrentSession()
                .createCriteria(AdComprobantePago.class, "compag")
                .createCriteria("compag.AdPagos", "pag")
                .createCriteria("pag.Conpag", "conpag")
                .add(Restrictions.between("compag.CompagFecha", desde, hasta))
                .add(Restrictions.eq("compag.CompagActivo", "1"))
                .add(Restrictions.eq("pag.AlutarId", 0));
        if (conpagId != 0) {
            criteria.add(Restrictions.eq("conpag.Id", conpagId));
        }
        return criteria;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, Float> sumarPagosValidosPorTarifa(List<Integer> alutarIds) {
        if (alutarIds == null || alutarIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object[]> rows = this.getSessionFactory().getCurrentSession()
                .createCriteria(AdPago.class, "pag")
                .add(Restrictions.eq("pag.PagActivo", "1"))
                .add(Restrictions.in("pag.AlutarId", alutarIds))
                .add(Restrictions.not(Restrictions.eq("pag.Conpag.Id", 49)))
                .add(Restrictions.not(Restrictions.eq("pag.Conpag.Id", 23)))
                .createAlias("pag.Compag", "compag")
                .add(Restrictions.eq("compag.CompagActivo", "1"))
                .add(Restrictions.eq("compag.CompagTipo", CAT_ESTADO_BOLETA_VALIDO))
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("pag.AlutarId"))
                        .add(Projections.sum("pag.PagMonto")))
                .list();
        Map<Integer, Float> sums = new HashMap<>();
        for (Object[] row : rows) {
            Number sum = (Number) row[1];
            sums.put((Integer) row[0], sum == null ? 0f : sum.floatValue());
        }
        return sums;
    }

    @Override
    public boolean seleccionarPagosExternos(int aluId, int conpagId) {
        List<Long> results = findByCriteria(Long.class, DetachedCriteria.forClass(AdComprobantePago.class).add(Restrictions.eq(AdComprobantePago.PROP_COMPAG_CLIENTE, aluId))
                .add(Restrictions.eq(AdComprobantePago.PROP_COMPAG_CLIENTE_TIPO, CAT_TIPO_CLIENTE_ALUMNO_CP))
                .createCriteria(AdComprobantePago.PROP_AD_PAGOS)
                .add(Restrictions.eq(AdPago.PROP_ALUTAR_ID, 0))
                .add(Restrictions.eq("Conpag.Id", conpagId))
                .setProjection(Projections.rowCount()));
        long x = DataAccessUtils.requiredUniqueResult(results);
        logger.trace("Payments registered: " + x);
        return x != 0;
    }

    @Override
    public void anularComprobantePago(int compagId) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AdComprobantePago set CompagTipo = :tipo where Id = :id";
            return session.createQuery(hqlUpdate)
                    .setParameter("tipo", "037002")
                    .setParameter("id", compagId).executeUpdate();
        });
        logger.trace("Invoices cancelled: " + r);
    }

    @Override
    public void eliminarComprobantePago(int compagId) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AdComprobantePago set CompagActivo = :activo where Id = :id";
            return session.createQuery(hqlUpdate)
                    .setParameter("activo", "0")
                    .setParameter("id", compagId).executeUpdate();
        });
        logger.trace("Invoices deleted: " + r);
    }

    @Override
    public List<AdPago> listaPagosAlumno(int alutarId, int caso) {
        var c = DetachedCriteria.forClass(AdPago.class, AdPago.REF).
                createCriteria("AdPago.Compag", AdComprobantePago.REF).
                createCriteria("AdPago.Conpag", AdConceptoPago.REF).
                add(Restrictions.eq("AdPago.PagActivo", "1")).
                add(Restrictions.eq("AdComprobantePago.CompagActivo", "1")).
                add(Restrictions.eq("AdConceptoPago.ConpagActivo", "1")).
                add(Restrictions.eq("AdConceptoPago.ConpagTipo", "007003")).
                add(Restrictions.eq("AdPago.AlutarId", alutarId));

        if (caso == 0) {
            c.add(Restrictions.eq("AdComprobantePago.CompagTipo", CAT_ESTADO_BOLETA_VALIDO));
        }

        return findByCriteria(AdPago.class, c);
    }

    @Override
    public void actualizarEstadoPago(int alutarId, String estado) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update ClAlumnoTarifa set alutarEstado = :v_estado where alutarId = :v_id";
            return session.createQuery(hqlUpdate).
                    setParameter("v_estado", estado).
                    setParameter("v_id", alutarId).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public ClAlumnoTarifa getClAlumnoTarifa(int alutarID) {
        var c = DetachedCriteria.forClass(ClAlumnoTarifa.class).
                add(Restrictions.eq("alutarActivo", "1")).
                add(Restrictions.eq("alutarId", alutarID));

        List<ClAlumnoTarifa> results = findByCriteria(ClAlumnoTarifa.class, c);
        return DataAccessUtils.requiredUniqueResult(results);
    }
}
