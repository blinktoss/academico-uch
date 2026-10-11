package net.uch.academica.dao;

import net.uch.mapping.AcNota;

import java.util.List;

public interface HSNotaDAO {

    void insertarOrActualizarNotas(List<AcNota> notas);

    List<AcNota> seleccionarNotas(int seccion);

    AcNota listarNotaPorAlumno(int alumnoId, int seccionId, int sisevaPerId);

}
