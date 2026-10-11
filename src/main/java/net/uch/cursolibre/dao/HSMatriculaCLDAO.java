package net.uch.cursolibre.dao;

import net.uch.mapping.ClMatricula;

import java.util.List;

public interface HSMatriculaCLDAO {

    List<ClMatricula> seleccionarMatriculas(Integer aluId);

    void insertarMatricula(ClMatricula matricula);

    void insertarMatriculas(List<ClMatricula> matriculas);

    void anularMatricula(int matId);

    /**
     * caso 0 -> total matriculados, caso 1 -> lista alumnos
     *
     * @param secId    section id
     * @param talapeId opened workshop id
     * @param caso     0 or 1 depending on the case
     * @return results
     */
    List<ClMatricula> totalMatriculadosSeccion(int secId, int talapeId, int caso);

    List<ClMatricula> listarMatriculadoTaller(int aluId, int talId);

    /**
     * Revisa si el alumno esta matriculado en la seccion seleccionada
     *
     * @param aluId student id
     * @param secId section id
     * @return Consulta a la base de datos y pregunta si el total de registros es '0'
     */
    boolean estaMatriculado(int aluId, int secId);

    void actualizarEstadoMatricula(String matTipo, Integer matId);
}
