package net.uch.academica.dao;

import net.uch.academica.jsf.beans.BeanCursos;
import net.uch.mapping.AcAmpliaciones;
import net.uch.mapping.AcMatricula;
import net.uch.mapping.AcPlanCurso;

import java.util.List;
import java.util.Map;

public interface HSMatriculaDAO {

    BeanCursos seleccionarCursosAprobados(int alumnoId);

    List<AcPlanCurso> seleccionarCursosParaIngresantes(int alumnoId);

    int numeroMatriculadosporSeccion(int seccionId);

    int numeroMatriculadosporSeccionSinAlumno(int seccionId, int alumnoId);

    /**
     * Locks the ac_seccion rows (SELECT ... FOR UPDATE) and returns each section's capacity.
     * Must run inside an active transaction: competing enrollments serialize on these locks,
     * so a follow-up seat count always sees the winner's committed rows (BUG-02).
     */
    Map<Integer, Integer> lockSectionCapacities(List<Integer> seccionIds);

    void registrarMatricula(AcMatricula matricula);

    List<AcMatricula> seleccionarMatriculas(int seccionId);

    AcMatricula listarCursosPorMatricula(int matriculaId);

    void eliminarMatricula(int matriculaId);

    void actualizarTipoMatricula(AcMatricula.Type tipo, int matId);

    List<AcMatricula> seleccionarMatricularRegular(int alumnoId, int semestreId);

    List<AcMatricula> seleccionarMatriculaReserva(int alumnoId, int semestreId);

    List<AcMatricula> seleccionarPrematricula(int alumnoId, int semestreId);

    List<AcMatricula> seleccionarMatricularRegular(List<Integer> alumnoIds, int semestreId);

    List<AcMatricula> seleccionarMatriculaReserva(List<Integer> alumnoIds, int semestreId);

    List<AcMatricula> seleccionarPrematricula(List<Integer> alumnoIds, int semestreId);

    AcMatricula seleccionarMatriculaAlumnoConvalidacion(int alumnoId);

    AcAmpliaciones findAmpliacion(int studentId, int semesterId);

    long getEnrolledStudentCount(int semesterId);
}
