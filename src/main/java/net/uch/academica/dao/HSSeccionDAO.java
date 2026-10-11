package net.uch.academica.dao;

import net.uch.mapping.AcSeccion;
import net.uch.mapping.SpConvalidacionCursos;

import java.util.List;

public interface HSSeccionDAO {

    List<Object[]> seleccionarSecciones(int curapeId);

    List<AcSeccion> obtenerSecciones(int plancurId, int semestreId);

    AcSeccion seleccionarSeccion(int seccionId);

    List<String> listarSecciones(int especialidadId, int semestreId);

    List<AcSeccion> listarCursosPorSeccion(String nombre);

    List<SpConvalidacionCursos> listaConvalidacion(int especialidadId, int alumnoId);
}
