package net.uch.academica.jsf.beans;

/**
 * Created for sistema_academico on 11/01/2024.
 *
 * @author Cesardl
 */
public class BeanPlanCurricularCurso {
    private int rowId;
    private String rowCodigo;
    private String rowDescripcion;
    private String rowCreacion;
    private int facultadId;
    private int especialidadId;
    private String rowResolucion;
    private String rowFacultadDesc;
    private String rowEspecialidadDesc;
    private String ciclo;
    private String rowCreditos;
    private String rowTipos;

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getRowCodigo() {
        return rowCodigo;
    }

    public void setRowCodigo(String rowCodigo) {
        this.rowCodigo = rowCodigo;
    }

    public String getRowDescripcion() {
        return rowDescripcion;
    }

    public void setRowDescripcion(String rowDescripcion) {
        this.rowDescripcion = rowDescripcion;
    }

    public String getRowCreacion() {
        return rowCreacion;
    }

    public void setRowCreacion(String rowCreacion) {
        this.rowCreacion = rowCreacion;
    }

    public int getFacultadId() {
        return facultadId;
    }

    public void setFacultadId(int facultadId) {
        this.facultadId = facultadId;
    }

    public int getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(int especialidadId) {
        this.especialidadId = especialidadId;
    }

    public String getRowResolucion() {
        return rowResolucion;
    }

    public void setRowResolucion(String rowResolucion) {
        this.rowResolucion = rowResolucion;
    }

    public String getRowFacultadDesc() {
        return rowFacultadDesc;
    }

    public void setRowFacultadDesc(String rowFacultadDesc) {
        this.rowFacultadDesc = rowFacultadDesc;
    }

    public String getRowEspecialidadDesc() {
        return rowEspecialidadDesc;
    }

    public void setRowEspecialidadDesc(String rowEspecialidadDesc) {
        this.rowEspecialidadDesc = rowEspecialidadDesc;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getRowCreditos() {
        return rowCreditos;
    }

    public void setRowCreditos(String rowCreditos) {
        this.rowCreditos = rowCreditos;
    }

    public String getRowTipos() {
        return rowTipos;
    }

    public void setRowTipos(String rowTipos) {
        this.rowTipos = rowTipos;
    }
}
