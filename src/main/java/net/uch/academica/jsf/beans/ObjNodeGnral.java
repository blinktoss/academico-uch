package net.uch.academica.jsf.beans;

/**
 * @author cesardl
 */
public class ObjNodeGnral {

    private String id;
    private int curapeId;
    private String curNom;
    private int semId;

    public String getCurNom() {
        return curNom;
    }

    public void setCurNom(String curNom) {
        this.curNom = curNom;
    }

    public int getCurapeId() {
        return curapeId;
    }

    public void setCurapeId(int curapeId) {
        this.curapeId = curapeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSemId() {
        return semId;
    }

    public void setSemId(int semId) {
        this.semId = semId;
    }
}
