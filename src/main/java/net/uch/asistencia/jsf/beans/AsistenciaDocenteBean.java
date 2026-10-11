package net.uch.asistencia.jsf.beans;

import java.util.Date;

/**
 * @author richard
 */
public class AsistenciaDocenteBean {

    private int sesefeasisdocId;
    private int sesId;
    private int docId;
    private Date sesefeasisdocFecha;
    private int regHora;
    private int regMin;
    private int salHora;
    private int salMin;
    private Date registro;
    private Date salida;
    private String sesefeasisdocObs;
    private String docNombre;
    private String secNombre;
    private String curNombre;
    private String estasisCode;

    public String getEstasisCode() {
        return estasisCode;
    }

    public void setEstasisCode(String estasisCode) {
        this.estasisCode = estasisCode;
    }

    public String getCurNombre() {
        return curNombre;
    }

    public void setCurNombre(String curNombre) {
        this.curNombre = curNombre;
    }

    public String getSecNombre() {
        return secNombre;
    }

    public void setSecNombre(String secNombre) {
        this.secNombre = secNombre;
    }

    public String getDocNombre() {
        return docNombre;
    }

    public void setDocNombre(String docNombre) {
        this.docNombre = docNombre;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getSesId() {
        return sesId;
    }

    public void setSesId(int sesId) {
        this.sesId = sesId;
    }

    public int getSesefeasisdocId() {
        return sesefeasisdocId;
    }

    public void setSesefeasisdocId(int sesefeasisdocId) {
        this.sesefeasisdocId = sesefeasisdocId;
    }

    public String getSesefeasisdocObs() {
        return sesefeasisdocObs;
    }

    public void setSesefeasisdocObs(String sesefeasisdocObs) {
        this.sesefeasisdocObs = sesefeasisdocObs;
    }

    public Date getSesefeasisdocFecha() {
        return sesefeasisdocFecha;
    }

    public void setSesefeasisdocFecha(Date sesefeasisdocFecha) {
        this.sesefeasisdocFecha = sesefeasisdocFecha;
    }

    public int getRegHora() {
        return regHora;
    }

    public void setRegHora(int regHora) {
        this.regHora = regHora;
    }

    public int getRegMin() {
        return regMin;
    }

    public void setRegMin(int regMin) {
        this.regMin = regMin;
    }

    public int getSalHora() {
        return salHora;
    }

    public void setSalHora(int salHora) {
        this.salHora = salHora;
    }

    public int getSalMin() {
        return salMin;
    }

    public void setSalMin(int salMin) {
        this.salMin = salMin;
    }

    public Date getRegistro() {
        return registro;
    }

    public void setRegistro(Date registro) {
        this.registro = registro;
    }

    public Date getSalida() {
        return salida;
    }

    public void setSalida(Date salida) {
        this.salida = salida;
    }
}
