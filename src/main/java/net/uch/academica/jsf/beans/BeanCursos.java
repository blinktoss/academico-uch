package net.uch.academica.jsf.beans;

import net.uch.mapping.AcPlanCurso;

import java.util.List;

/**
 * Created for sistema_academico on 29/01/2024.
 *
 * @author Cesardl
 */
public class BeanCursos {
    private List<Integer> aprobados;
    private List<AcPlanCurso> desaprobados;
    private List<Integer> desaprobadosId;

    public List<Integer> getAprobados() {
        return aprobados;
    }

    public void setAprobados(List<Integer> aprobados) {
        this.aprobados = aprobados;
    }

    public List<AcPlanCurso> getDesaprobados() {
        return desaprobados;
    }

    public void setDesaprobados(List<AcPlanCurso> desaprobados) {
        this.desaprobados = desaprobados;
    }

    public List<Integer> getDesaprobadosId() {
        return desaprobadosId;
    }

    public void setDesaprobadosId(List<Integer> desaprobadosId) {
        this.desaprobadosId = desaprobadosId;
    }
}
