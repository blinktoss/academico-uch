package net.uch.banco.jsf.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cesardl
 */
public class InfoBanco {

    private Cabecera cabecera;
    private List<Detalle> detalles;
    private List<Concepto> conceptos;

    public InfoBanco() {
        cabecera = new Cabecera();
        detalles = new ArrayList<>();
        conceptos = new ArrayList<>();
    }

    public Cabecera getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecera cabecera) {
        this.cabecera = cabecera;
    }

    public List<Concepto> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<Concepto> conceptos) {
        this.conceptos = conceptos;
    }

    public List<Detalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }

    public enum RowIdentifiers {

        H, D, T
    }
}
