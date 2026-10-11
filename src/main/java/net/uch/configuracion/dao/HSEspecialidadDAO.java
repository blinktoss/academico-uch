package net.uch.configuracion.dao;

import net.uch.mapping.AcEspecialidad;

import java.util.List;

public interface HSEspecialidadDAO {

    void insertarEspecialidad(AcEspecialidad esp);

    List<AcEspecialidad> seleccionarEspecialidad(String codigo, String nombre, int facultadId);

    List<AcEspecialidad> seleccionarEspecialidad(int facultadId);

    AcEspecialidad seleccionarUnaEspecialidad(int especialidadId);

    int eliminarEspecialidad(Integer id);

    void actualizarEspecialidad(AcEspecialidad esp);

    List<AcEspecialidad> seleccionarEspecialidades();
}
