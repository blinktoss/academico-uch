package net.uch.asistencia.jsf.beans;

import java.util.Date;

/**
 * @author cesardl
 */
public class NuevaAsistenciaBean {

    private int docId;
    private Date fecSesion;
    private Date fecSesionIni;
    private Date fecSesionFin;
    private String tipasisCode;//050
    private String tipsesCode;//047
    private String seccionCode;
    private int especialidadCode;
    private int cursoSeccion;
    private int regHora;
    private int regMin;
    private int salHora;
    private int salMin;

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

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getCursoSeccion() {
        return cursoSeccion;
    }

    public void setCursoSeccion(int cursoSeccion) {
        this.cursoSeccion = cursoSeccion;
    }

    public int getEspecialidadCode() {
        return especialidadCode;
    }

    public void setEspecialidadCode(int especialidadCode) {
        this.especialidadCode = especialidadCode;
    }

    public Date getFecSesion() {
        return fecSesion;
    }

    public void setFecSesion(Date fecSesion) {
        this.fecSesion = fecSesion;
    }

    public Date getFecSesionFin() {
        return fecSesionFin;
    }

    public void setFecSesionFin(Date fecSesionFin) {
        this.fecSesionFin = fecSesionFin;
    }

    public Date getFecSesionIni() {
        return fecSesionIni;
    }

    public void setFecSesionIni(Date fecSesionIni) {
        this.fecSesionIni = fecSesionIni;
    }

    public String getSeccionCode() {
        return seccionCode;
    }

    public void setSeccionCode(String seccionCode) {
        this.seccionCode = seccionCode;
    }

    /**
     * 050
     *
     * @return codigo del tipo de asistencia
     */
    public String getTipasisCode() {
        return tipasisCode;
    }

    public void setTipasisCode(String tipasisCode) {
        this.tipasisCode = tipasisCode;
    }

    /**
     * 047
     *
     * @return codigo del tipo de sesion
     */
    public String getTipsesCode() {
        return tipsesCode;
    }

    public void setTipsesCode(String tipsesCode) {
        this.tipsesCode = tipsesCode;
    }
}
