/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.dao;

import net.uch.mapping.ClNota;

import java.util.List;

/**
 * @author cesardl
 */
public interface HSNotasCLDAO {

    List<ClNota> seleccionarNotas(int secId);

    void insertarActualizarNotas(List<ClNota> lista);
}
