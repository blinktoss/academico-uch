package net.uch.academica.dao;

import net.uch.academica.jsf.MbDocenteDisponibilidad;
import net.uch.mapping.AcDocente;

import java.util.List;

public interface HSDocenteDAO {

    void insertarDocente(AcDocente doc);

    List<Object[]> seleccionarDocente(String codigo, String nombre);

    void eliminarDocente(Integer id);

    void actualizarDocente(AcDocente doc);

    List<AcDocente> seleccionarDocente();

    AcDocente seleccionarDocente(int id);

    boolean existeDocente(String dni);

    List<MbDocenteDisponibilidad> findByCodeAndGetAvailability(String code, String name);

    List<Object[]> findAvailabilitiesByTeacher(int teacherId);
}
