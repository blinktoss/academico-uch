package net.uch.academica.dao;

import net.uch.mapping.AcEquivalencia;
import net.uch.mapping.AcPlanCurso;

import java.util.List;

public interface HSPlanCurricularCursoDAO {

    Integer insertarPlanCurricularCurso(AcPlanCurso pc);

    List<Object[]> seleccionarPlanCurricularCurso(int plan);

    List<Object[]> seleccionarPlanCurricularCurso(int plan, String cicloId);

    void eliminarPlanCurricularCurso(String id);

    boolean verificarRepitencia(int plan, int curso, String ciclo);

    List<AcPlanCurso> seleccionarCursos(int plan, String ciclo);

    List<AcEquivalencia> findEquivalences(int plancurId);
}
