package net.uch.academica.dao;

import net.uch.mapping.AcTurno;

import java.util.List;

public interface HSTurnoDAO {

    void insertarTurno(AcTurno turno);

    List<Object[]> seleccionarTurno(String codigo, String nombre, Integer semestre);

    void actualizarTurno(AcTurno turno);

    void eliminarTurno(int id, int semestre);

    List<AcTurno> seleccionarTurno(int semestre);

    AcTurno obtenerTurno(int id);
}
