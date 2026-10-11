package net.uch.administrativa.service;

/**
 * Immutable filter holder for the "free courses" (Curso Libre, {@code cl_*}) payment search. The
 * CL domain has no faculty/specialty/semester — tariffs hang off módulo/taller — so this carries
 * only the filters that screen exposes: student code, surnames, tariff state and payment concept.
 */
public final class FreeCourseCriteria {

    private final String codAlu;
    private final String estadoId;
    private final int conceptoPagoId;
    private final String apPaterno;
    private final String apMaterno;

    public FreeCourseCriteria(String codAlu, String estadoId, int conceptoPagoId, String apPaterno, String apMaterno) {
        this.codAlu = codAlu;
        this.estadoId = estadoId;
        this.conceptoPagoId = conceptoPagoId;
        this.apPaterno = apPaterno;
        this.apMaterno = apMaterno;
    }

    public String getCodAlu() {
        return codAlu;
    }

    public String getEstadoId() {
        return estadoId;
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
}
