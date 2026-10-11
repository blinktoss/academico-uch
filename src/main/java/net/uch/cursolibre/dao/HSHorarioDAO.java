package net.uch.cursolibre.dao;

import net.uch.mapping.ClAlumno;
import net.uch.mapping.ClHoraria;

import java.util.List;

public interface HSHorarioDAO {

    List<ClHoraria> seleccionarHorario(int seccionId);

    void insertarActualizarHorarios(List<ClHoraria> horarias);

    void eliminarHorarios(List<Integer> horarioIds);

    List<ClAlumno> matriculadosSeccion(int seccionId);
}
