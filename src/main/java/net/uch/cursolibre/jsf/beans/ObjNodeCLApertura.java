/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.jsf.beans;

/**
 * @author cesardl
 */
public class ObjNodeCLApertura {

    private String id;
    private int cursoId;
    private String cursoNombre;
    private String modDescripcion;

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModDescripcion() {
        return modDescripcion;
    }

    public void setModDescripcion(String modDescripcion) {
        this.modDescripcion = modDescripcion;
    }
}
