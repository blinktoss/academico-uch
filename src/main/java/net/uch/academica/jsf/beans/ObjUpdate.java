package net.uch.academica.jsf.beans;

/**
 * Clase temporal para almacenar los cambios a realizarse
 *
 * @author cesardl
 */
public class ObjUpdate {

    private final String key;
    private final Object value;

    /**
     * Constructor de clase:
     *
     * @param key:   ingresa el alias del objeto a actualizar
     * @param value: setea el valor
     */
    public ObjUpdate(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
