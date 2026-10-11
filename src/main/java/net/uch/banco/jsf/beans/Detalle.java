package net.uch.banco.jsf.beans;

/**
 * Clase que hace referencia a los detalles de pago
 * Indetificador <b>D</b>
 *
 * @author cesardl
 */
public class Detalle {

    private final String tipoRegistro;
    private final String cuentaEmpresa;
    private final String secuencia;
    private final String codigoUsuario;
    private final String numeroRecibo;
    private final String nombreUsuario;
    private final String monedaCobro;
    private final String importe1;
    private final String importe2;
    private final String importe3;
    private final String importe4;
    private final String importe5;
    private final String importe6Mora;
    private final String fechaVencimiento;
    private final String fechaPago;
    private final String tipoPago;
    private final String medioPago;
    private final String numeroOperacion;
    private final String referenciaCobro;

    public Detalle(String cadena) {
        this.tipoRegistro = cadena.substring(0, 1);
        this.cuentaEmpresa = cadena.substring(1, 15);
        this.secuencia = cadena.substring(15, 18);
        this.codigoUsuario = cadena.substring(18, 33);
        this.numeroRecibo = cadena.substring(33, 48).trim();
        this.nombreUsuario = cadena.substring(48, 68);
        this.monedaCobro = cadena.substring(68, 72).trim();
        this.importe1 = cadena.substring(72, 81);
        this.importe2 = cadena.substring(81, 90);
        this.importe3 = cadena.substring(90, 99);
        this.importe4 = cadena.substring(99, 108);
        this.importe5 = cadena.substring(108, 117);
        this.importe6Mora = cadena.substring(117, 126);
        this.fechaVencimiento = cadena.substring(126, 134);
        this.fechaPago = cadena.substring(134, 142);
        this.tipoPago = cadena.substring(142, 143);
        this.medioPago = cadena.substring(143, 144);
        this.numeroOperacion = cadena.substring(144, 157);
        this.referenciaCobro = cadena.substring(157, 177);
    }

    /**
     * Devuelve el numero de recibo.
     *
     * @return el <i>alutar_id</i>
     */
    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public String getImporte1() {
        return importe1;
    }

    public String getImporte2() {
        return importe2;
    }

    public String getImporte3() {
        return importe3;
    }

    public String getImporte4() {
        return importe4;
    }

    public String getImporte5() {
        return importe5;
    }

    public String getImporte6Mora() {
        return importe6Mora;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    /**
     * Devuelve el numero de operacion otorgado por el banco
     *
     * @return el numero de operacion
     */
    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    public String[] getImportes() {
        return new String[]{this.importe1, this.importe2, this.importe3,
                this.importe4, this.importe5, this.importe6Mora
        };
    }

    @Override
    public String toString() {
        return "Detalle{" +
                "\ntipo_registro='" + tipoRegistro + '\'' +
                ",\ncuenta_empresa='" + cuentaEmpresa + '\'' +
                ",\nsecuencia='" + secuencia + '\'' +
                ",\ncodigo_usuario='" + codigoUsuario + '\'' +
                ",\nnumero_recibo='" + numeroRecibo + '\'' +
                ",\nnombre_usuario='" + nombreUsuario + '\'' +
                ",\nmoneda_cobro='" + monedaCobro + '\'' +
                ",\nimporte1='" + importe1 + '\'' +
                ",\nimporte2='" + importe2 + '\'' +
                ",\nimporte3='" + importe3 + '\'' +
                ",\nimporte4='" + importe4 + '\'' +
                ",\nimporte5='" + importe5 + '\'' +
                ",\nimporte6_mora='" + importe6Mora + '\'' +
                ",\nfecha_vencimiento='" + fechaVencimiento + '\'' +
                ",\nfecha_pago='" + fechaPago + '\'' +
                ",\ntipo_pago='" + tipoPago + '\'' +
                ",\nmedio_pago='" + medioPago + '\'' +
                ",\nnumero_operacion='" + numeroOperacion + '\'' +
                ",\nreferencia_cobro='" + referenciaCobro + '\'' +
                '}';
    }
}
