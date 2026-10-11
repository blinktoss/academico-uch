package net.uch.autenticacion.jsf.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * Created for sistema_academico on 15/11/2024.
 *
 * @author Cesardl
 */
public class BeanUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private int rowUsuId;
    private String nivel;
    private int rolId;
    private boolean superAdmin;
    private Set<String> grantedModules = Collections.emptySet();

    public int getRowUsuId() {
        return rowUsuId;
    }

    public void setRowUsuId(int rowUsuId) {
        this.rowUsuId = rowUsuId;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    /**
     * @return {@code true} for the god-admin (usu_id = 1), who bypasses module authorization.
     */
    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    /**
     * @return the screen modules ({@code men_accion} outcomes) the user's role may access; never null.
     */
    public Set<String> getGrantedModules() {
        return grantedModules;
    }

    public void setGrantedModules(Set<String> grantedModules) {
        this.grantedModules = grantedModules == null ? Collections.emptySet() : grantedModules;
    }
}
