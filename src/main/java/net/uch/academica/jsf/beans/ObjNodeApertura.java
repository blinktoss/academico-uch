package net.uch.academica.jsf.beans;

/**
 * @author cesardl
 */
public class ObjNodeApertura {

    private String id;
    private String arbolFacultad;
    private String arbolEspecialidad;
    private String arbolCiclo;
    private int arbolPlan;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArbolFacultad() {
        return arbolFacultad;
    }

    public void setArbolFacultad(String arbolFacultad) {
        this.arbolFacultad = arbolFacultad;
    }

    public String getArbolEspecialidad() {
        return arbolEspecialidad;
    }

    public void setArbolEspecialidad(String arbolEspecialidad) {
        this.arbolEspecialidad = arbolEspecialidad;
    }

    public String getArbolCiclo() {
        return arbolCiclo;
    }

    public void setArbolCiclo(String arbolCiclo) {
        this.arbolCiclo = arbolCiclo;
    }

    public int getArbolPlan() {
        return arbolPlan;
    }

    public void setArbolPlan(int arbolPlan) {
        this.arbolPlan = arbolPlan;
    }
}
