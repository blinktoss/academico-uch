package net.uch.academica.dao;

/**
 * Flat projection row for building the academic tree: one opened course with just the columns the
 * tree needs, so the query never hydrates an {@code AcCurso} entity (which would eagerly load its
 * {@code AcPlanCursos} collection, one query per course). Built directly by an HQL constructor
 * expression in {@link HSAperturaCursosDAO#findOpenedCoursesBySemesters(java.util.List)}.
 */
public class OpenedCourseRow {

    private final Integer especialidadId;
    private final Integer semestreId;
    private final Integer curapeId;
    private final String plancurCiclo;
    private final String cursoNombre;

    public OpenedCourseRow(Integer especialidadId, Integer semestreId, Integer curapeId,
                           String plancurCiclo, String cursoNombre) {
        this.especialidadId = especialidadId;
        this.semestreId = semestreId;
        this.curapeId = curapeId;
        this.plancurCiclo = plancurCiclo;
        this.cursoNombre = cursoNombre;
    }

    public Integer getEspecialidadId() {
        return especialidadId;
    }

    public Integer getSemestreId() {
        return semestreId;
    }

    public Integer getCurapeId() {
        return curapeId;
    }

    public String getPlancurCiclo() {
        return plancurCiclo;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }
}
