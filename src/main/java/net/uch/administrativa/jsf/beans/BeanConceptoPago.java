package net.uch.administrativa.jsf.beans;

/**
 * @author chano
 */
public class BeanConceptoPago {

    private int id;
    private String desc;
    private float monto;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }
}
