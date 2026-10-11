package net.uch.cursolibre.dao;

import net.uch.mapping.ClCurso;

import java.util.List;

/**
 * @author cesardl
 */
public interface HSCursoCLDAO {

    List<ClCurso> seleccionarCursos(int moduloId);

    /**
     * All active courses for the given modules as a flat projection, in a single query, so the tree
     * builder can group them in-memory by module instead of running one query per module. Ordered by
     * (module, course id) so each module's courses come out in the same order as
     * {@link #seleccionarCursos(int)}.
     */
    List<CLCursoRow> findCursosByModulos(List<Integer> moduloIds);

    boolean verificarEliminarCurso(int cursoId);

    void eliminarCurso(int cursoId);

    void actualizarCurso(ClCurso curso);

    void insertarCurso(ClCurso curso);

    ClCurso buscarCurso(int cursoId);
}
