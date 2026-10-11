package net.uch.banco.jsf.beans;

/**
 * Clase que hace referencia a los conceptos de pago
 * Indetificador <b>T</b>
 *
 * @author cesardl
 */
public class Concepto {

    private final String tipoRegistro;
    private final String cuentaEmpresa;
    private final String secuencia;
    private final String codigoConcepto;
    private final String descripcionConcepto;
    private final String cuentaAbono;
    private final String importe;

    public Concepto(String cadena) {
        this.tipoRegistro = cadena.substring(0, 1);
        this.cuentaEmpresa = cadena.substring(1, 15);
        this.secuencia = cadena.substring(15, 18);
        this.codigoConcepto = cadena.substring(18, 20);
        this.descripcionConcepto = cadena.substring(20, 50);
        this.cuentaAbono = cadena.substring(50, 64);
        this.importe = cadena.substring(64, 79);
    }

    @Override
    public String toString() {
        return "Concepto{" +
                "\ntipo_registro='" + tipoRegistro + '\'' +
                ",\ncuenta_empresa='" + cuentaEmpresa + '\'' +
                ",\nsecuencia='" + secuencia + '\'' +
                ",\ncodigo_concepto='" + codigoConcepto + '\'' +
                ",\ndescripcion_concepto='" + descripcionConcepto + '\'' +
                ",\ncuenta_abono='" + cuentaAbono + '\'' +
                ",\nimporte='" + importe + '\'' +
                '}';
    }
}
