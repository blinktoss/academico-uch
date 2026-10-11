package net.uch.academica.dao;

import net.uch.mapping.AcAlumno;

import java.util.List;

public interface HSAlumnoDAO {

    Integer insertarAlumno(AcAlumno alu);

    List<AcAlumno> seleccionarAlumnos(String codigo, String paterno, String materno, String nombre, int especialidad);

    /**
     * Same search as {@link #seleccionarAlumnos}, additionally restricted at the query level to
     * AluTipo=ESTUDIANTE and AluEstado=REGULAR - the two enrollment-eligibility checks that must
     * not apply to {@link #seleccionarAlumnos}'s other callers (MbAlumno's admin search,
     * MbConvalidacion, MbMatriculaCL all need to find every student type/status).
     */
    List<AcAlumno> seleccionarAlumnosElegiblesParaMatricula(String codigo, String paterno, String materno, String nombre, int especialidad);

    /**
     * Re-checks the same eligibility rule as {@link #seleccionarAlumnosElegiblesParaMatricula} for
     * a single student id, by id rather than by search criteria - meant to be called again right
     * before persisting an enrollment, since the search result a submission is built from may be
     * stale by the time the user submits.
     */
    boolean esElegibleParaMatricula(int alumnoId);

    void eliminarAlumno(String id);

    void actualizarAlumno(AcAlumno alu);

    List<AcAlumno> seleccionarAlumnoEstPago(int especialidadId, String tipo, int semestreId);

    List<AcAlumno> seleccionarAlumnoCliente(String buscar);

    AcAlumno seleccionarUnAlumno(int id);

    String maximoCodigo();

    List<AcAlumno> listarAlumnosMatriculaRegularPorSeccion(int seccionId);

    AcAlumno buscarAlumnoCodigo(String codigo);

    long getActiveStudentCount();
}
