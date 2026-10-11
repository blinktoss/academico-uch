/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.configuracion.dao;

import net.uch.mapping.TbMenu;

import java.util.List;
import java.util.Set;

/**
 * @author cesardl
 */
public interface HSMenuDAO {

    List<TbMenu> seleccionToolBar(int rolId);

    List<TbMenu> seleccionHijos(int menuId, int rolId);

    /**
     * Returns the access tokens granted to a role: the {@code men_accion} navigation outcomes of the
     * active leaf menu items ({@code men_opc = '2'}) linked to the role through an active
     * {@code tb_menu_rol} row. Each outcome maps 1:1 to a screen in {@code faces-config.xml}, so this
     * set is the authoritative "which modules may this role use" used to authorize screens and their
     * associated servlets/reports.
     *
     * @param rolId the role id
     * @return distinct granted {@code men_accion} outcomes (never null)
     */
    Set<String> findGrantedModules(int rolId);

    /**
     * Returns every screen module ({@code men_accion} of an active leaf menu item, excluding action
     * expressions like {@code #{...}}), across all roles — the universe of role-gated screens. Used
     * by the screen-access filter to tell a gated screen (must be granted) from a public one.
     *
     * @return all distinct gated module tokens (never null)
     */
    Set<String> findAllModules();
}
