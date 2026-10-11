package net.uch.banco.jsf.beans;

import net.uch.mapping.TbBancoEntrada;

/**
 * @author cesardl
 */
public class RespuestaBanco extends TbBancoEntrada {

    private static final long serialVersionUID = 7680243123040844238L;

    private int banentNumeroLinea;
    private String banentNomAlumno;
    private String banentConceptoPago;
    private String banentComentario;

    public RespuestaBanco(int banentNumeroLinea, String banentNroOperacion, String banentComentario) {
        this.banentNumeroLinea = banentNumeroLinea;
        this.banentComentario = banentComentario;
        this.setBanentNroOperacion(banentNroOperacion);
    }

    public RespuestaBanco(TbBancoEntrada bancoEntrada) {
        this.setBanentId(bancoEntrada.getBanentId());
        this.setBanentMonto1(bancoEntrada.getBanentMonto1());
        this.setBanentMonto2(bancoEntrada.getBanentMonto2());
        this.setBanentMonto3(bancoEntrada.getBanentMonto3());
        this.setBanentMonto4(bancoEntrada.getBanentMonto4());
        this.setBanentMonto5(bancoEntrada.getBanentMonto5());
        this.setBanentMora(bancoEntrada.getBanentMora());
        this.setBanentFechaMovimiento(bancoEntrada.getBanentFechaMovimiento());
        this.setBanentFechaPago(bancoEntrada.getBanentFechaPago());
        this.setBanentNroOperacion(bancoEntrada.getBanentNroOperacion());
        this.setBanentProcesado(bancoEntrada.getBanentProcesado());
        this.setBanentActivo(bancoEntrada.getBanentActivo());
        this.setBanentNroRecibo(bancoEntrada.getBanentNroRecibo());
    }

    public int getBanentNumeroLinea() {
        return banentNumeroLinea;
    }

    public void setBanentNumeroLinea(int banentNumeroLinea) {
        this.banentNumeroLinea = banentNumeroLinea;
    }

    public String getBanentNomAlumno() {
        return banentNomAlumno;
    }

    public void setBanentNomAlumno(String banentNomAlumno) {
        this.banentNomAlumno = banentNomAlumno;
    }

    public String getBanentConceptoPago() {
        return banentConceptoPago;
    }

    public void setBanentConceptoPago(String banentConceptoPago) {
        this.banentConceptoPago = banentConceptoPago;
    }

    public String getBanentComentario() {
        return banentComentario;
    }

    public void setBanentComentario(String banentComentario) {
        this.banentComentario = banentComentario;
    }
}
