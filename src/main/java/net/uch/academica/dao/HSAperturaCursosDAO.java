package net.uch.academica.dao;

import net.uch.mapping.AcCursoAperturado;

import java.util.List;

public interface HSAperturaCursosDAO {

    void aperturarCurso(AcCursoAperturado cursoAperturado);

    void actualizarCursoAperturado(AcCursoAperturado cursoAperturado);

    AcCursoAperturado verificarCursoAperturado(int plancurId, int semestreId);

    AcCursoAperturado seleccionarUnCursosAperturado(int curapeId);

    List<AcCursoAperturado> seleccionarAperturaporEspecialidad(int especialidadId, int semestreId);

    /**
     * All opened courses for the given semesters as a flat projection, in a single query, for
     * building the academic tree without the per-(specialty x semester) N+1 - and without
     * hydrating {@code AcCurso} entities (whose eager {@code AcPlanCursos} collection would add one
     * query per course). Ordered so each (specialty, semester) group comes out in the same order as
     * {@link #seleccionarAperturaporEspecialidad(int, int)}.
     */
    List<OpenedCourseRow> findOpenedCoursesBySemesters(List<Integer> semesterIds);

    void deleteSisEvaPersonalizados(List<Integer> idsToDelete);
}
