package net.uch.administrativa.dao;

import net.uch.mapping.AdComprobantePago;
import net.uch.mapping.AdPago;
import net.uch.mapping.ClAlumnoTarifa;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HSPagoDAO {

    int insertarComprobantePago(AdComprobantePago compag);

    /**
     * @param allocateNumber when true, the voucher number is (re)assigned atomically from the
     *                       shared comprobante_correlativo sequence inside this transaction,
     *                       overriding the form-open preview held in CompagNro. Prevents the
     *                       cross-app allocation race on compag_nro.
     */
    int insertarComprobantePago(AdComprobantePago compag, boolean allocateNumber);

    void insertarComprobantesPago(List<AdComprobantePago> compags);

    List<AdPago> seleccionarMostrarPagos(int id);

    List<AdPago> seleccionarMostrarPagosMora(int id);

    /**
     * Active payments of a single concept (e.g. mora, catalog 021004) applied to a tariff, on
     * valid vouchers — the mora side of the total-debt rule when re-deriving AlutarEstado.
     */
    List<AdPago> seleccionarPagosPorConcepto(int alutarId, int conpagId);

    List<AdComprobantePago> seleccionarMostrarPagosExternos(Date desde, Date hasta, int conpagId);

    /**
     * Paged variant of {@link #seleccionarMostrarPagosExternos}. Pages over <em>distinct</em>
     * comprobantes (the underlying query inner-joins the pagos collection), ordered by voucher date
     * desc, so {@code max} bounds comprobantes rather than joined rows.
     */
    List<AdComprobantePago> seleccionarMostrarPagosExternos(Date desde, Date hasta, int conpagId, int first, int max);

    /**
     * Distinct comprobante count matching the {@link #seleccionarMostrarPagosExternos} filters.
     */
    long contarPagosExternos(Date desde, Date hasta, int conpagId);

    /**
     * Sum of valid-voucher payments per tariff for a batch of tariff ids (one query, replacing the
     * per-row {@link #seleccionarMostrarPagos(int)} N+1 in the internal payment listing). Only active
     * payments on active, valid ({@code 037001}) vouchers count, excluding the special concepts 49
     * and 23 — mirroring {@link #seleccionarMostrarPagos(int)} plus the caller's valid-voucher filter.
     */
    Map<Integer, Float> sumarPagosValidosPorTarifa(List<Integer> alutarIds);

    String seleccionarUnComprobantePago(String comp);

    void actualizarComprobantePago(AdComprobantePago compag);

    /**
     * Non-binding preview of the next voucher number for (serie, tip doc) — the value the
     * commit-time allocation will issue next, read from the shared comprobante_correlativo
     * counter (sp_preview_voucher_number) so sistema_academico and sigu_caja always display
     * the same number. Never locks nor advances the counter.
     * <p>
     * Deliberately NOT named under the read-only transaction rules (get*, find*, load*):
     * Connector/J with noAccessToProcedureBodies=true rejects any CALL on a read-only
     * connection, since it cannot prove the procedure does not write.
     */
    String previewComprobanteNro(String serie, String tipDoc);

    boolean seleccionarPagosExternos(int aluId, int conpagId);

    void anularComprobantePago(int compagId);

    void eliminarComprobantePago(int compagId);

    AdComprobantePago seleccionarUnComprobantePago(int compId);

    List<AdPago> listaPagosAlumno(int alutarId, int caso);

    void actualizarEstadoPago(int alutarId, String estado);

    ClAlumnoTarifa getClAlumnoTarifa(int alutarID);
}
