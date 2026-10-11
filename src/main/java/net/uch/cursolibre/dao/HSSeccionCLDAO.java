/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.dao;

import net.uch.mapping.ClSeccion;

import java.util.List;

/**
 * @author cesardl
 */
public interface HSSeccionCLDAO {

    ClSeccion seleccionarSeccion(Integer seccionId);

    List<ClSeccion> seleccionarSecciones(int talapeId);

    List<ClSeccion> listarSeccionXTaller(int tallerId);

    List<ClSeccion> seleccionarSeccionesXModulo(int modId);

    void insertarSeccion(ClSeccion sec);

    void actualizarSeccion(ClSeccion sec);

    void eliminarSeccion(int secId);

    boolean existenMatriculasSeccion(int secId);
}
