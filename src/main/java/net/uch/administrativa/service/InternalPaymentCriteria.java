package net.uch.administrativa.service;

/**
 * Immutable filter holder for the "internal" (tariff-linked) payment search — the seven fields the
 * {@code MbPago} screen exposes. Keeps the service/DAO signatures readable instead of threading
 * seven positional arguments through every layer.
 */
public final class InternalPaymentCriteria {

    private final String codAlu;
    private final String estadoId;
    private final int especialidadId;
    private final int conceptoPagoId;
    private final String apPaterno;
    private final String apMaterno;
    private final int semestreId;

    public InternalPaymentCriteria(String codAlu, String estadoId, int especialidadId, int conceptoPagoId,
                                   String apPaterno, String apMaterno, int semestreId) {
        this.codAlu = codAlu;
        this.estadoId = estadoId;
        this.especialidadId = especialidadId;
        this.conceptoPagoId = conceptoPagoId;
        this.apPaterno = apPaterno;
        this.apMaterno = apMaterno;
        this.semestreId = semestreId;
    }

    public String getCodAlu() {
        return codAlu;
    }

    public String getEstadoId() {
        return estadoId;
    }

    public int getEspecialidadId() {
        return especialidadId;
    }

    public int getConceptoPagoId() {
        return conceptoPagoId;
    }

    public String getApPaterno() {
        return apPaterno;
    }

    public String getApMaterno() {
        return apMaterno;
    }

    public int getSemestreId() {
        return semestreId;
    }
}
