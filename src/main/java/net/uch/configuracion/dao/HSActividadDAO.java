/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.configuracion.dao;

import net.uch.mapping.AcCalendario;

import java.util.Date;
import java.util.List;

/**
 * @author cesardl
 */
public interface HSActividadDAO {

    void insertarActividad(AcCalendario calendario);

    void actualizarActividad(AcCalendario calendario);

    List<AcCalendario> seleccionarActividadDetalle(Date calFecha);

    AcCalendario seleccionarActividadId(int calId);

    void actualizarEstadoPublicado(int id, String publicado);

    void eliminarActividad(int id);

    void eliminarAlcances(List<Integer> ids);
}
