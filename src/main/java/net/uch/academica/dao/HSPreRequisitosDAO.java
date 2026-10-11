package net.uch.academica.dao;

import net.uch.mapping.AcPlanCurso;
import net.uch.mapping.AcPreRequisitos;

import java.util.List;

public interface HSPreRequisitosDAO {

    void insertarPreRequisitos(AcPreRequisitos alu);

    void eliminarPreRequisitos(Integer id);

    List<AcPlanCurso> seleccionarCurso(int plan, String ciclo);

    List<Object[]> seleccionarPreReq(int curso);

    List<Object[]> verificarRepitencia(Integer curso, Integer req);

    List<AcPlanCurso> cursoDet(int plan);

    List<AcPlanCurso> retonarCursosAbiertos(List<Integer> aprobados, List<Integer> desaprobados);

    List<AcPlanCurso> retonarCursosLibres(List<Integer> list, int esp);
}

