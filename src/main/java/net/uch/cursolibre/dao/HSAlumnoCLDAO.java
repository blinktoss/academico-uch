package net.uch.cursolibre.dao;

import net.uch.mapping.ClAlumno;
import net.uch.mapping.ClAlumnoTarifa;

import java.util.List;

/**
 * @author cesardl
 */
public interface HSAlumnoCLDAO {

    void insertarAlumnoCl(ClAlumno alu);

    void modificarAlumnoCl(ClAlumno alu);

    void eliminarAlumnoCl(String aluId);

    List<ClAlumno> seleccionarAlumnosLibres(String codigo, String paterno, String materno, String nombre);

    String maximoCodigo();

    ClAlumno buscarAlumnoPorAluId(Integer aluId);

    List<ClAlumno> seleccionarAlumnosPorCodigo(String codigo);

    List<ClAlumno> seleccionarAlumnosPorApellidos(String apellidos);

    List<ClAlumno> listaAlumnosDatos(String code, String paterno, String materno, String nombres, int modId);

    List<ClAlumnoTarifa> listaAlumnoTarifa(int aluId, int modId);

    boolean existeAlumnoUnivRegistrado(Integer id);
}
