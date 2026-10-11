package net.uch.banco.jsf.beans;

/**
 * Clase que hace referencia a la cabezera
 * Indetificador <b>H</b>
 *
 * @author cesardl
 */
public class Cabecera {

    private final String tipoRegistro;
    private final String cuentaEmpresa;
    private final String secuencia;
    private final String documentos;
    private final String totalSoles;
    private final String totalDolares;
    private final String fechaMovimiento;

    /**
     * Inicializa los campos de la cabezera en blanco
     */
    public Cabecera() {
        this.tipoRegistro = "";
        this.cuentaEmpresa = "";
        this.secuencia = "";
        this.documentos = "";
        this.totalSoles = "";
        this.totalDolares = "";
        this.fechaMovimiento = "";
    }

    public Cabecera(String cadena) {
        this.tipoRegistro = cadena.substring(0, 1);
        this.cuentaEmpresa = cadena.substring(1, 15);
        this.secuencia = cadena.substring(15, 18);
        this.documentos = cadena.substring(18, 25);
        this.totalSoles = cadena.substring(25, 40);
        this.totalDolares = cadena.substring(40, 52);
        this.fechaMovimiento = cadena.substring(52, 60);
    }

    public String getFechaMovimiento() {
        return fechaMovimiento;
    }

    @Override
    public String toString() {
        return "Cabecera{" +
                "\ntipo_registro='" + tipoRegistro + '\'' +
                ",\ncuenta_empresa='" + cuentaEmpresa + '\'' +
                ",\nsecuencia='" + secuencia + '\'' +
                ",\ndocumentos='" + documentos + '\'' +
                ",\ntotal_soles='" + totalSoles + '\'' +
                ",\ntotal_dolares='" + totalDolares + '\'' +
                ",\nfecha_movimiento='" + fechaMovimiento + '\'' +
                '}';
    }
}
