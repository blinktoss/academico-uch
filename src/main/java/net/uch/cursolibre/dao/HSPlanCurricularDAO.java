package net.uch.cursolibre.dao;

import net.uch.mapping.ClPlancurricular;

import java.util.List;

public interface HSPlanCurricularDAO {

    List<ClPlancurricular> seleccionarPlanCurricular(int moduleId, String description);

    void insertarPlanCurricular(ClPlancurricular planCurricular);

    void actualizarPlanCurricular(ClPlancurricular planCurricular);

    void eliminarPlanCurricular(int id);

    ClPlancurricular buscarPlanCurricular(int id);

    void desactualizarPlanes(int moduleId);

    ClPlancurricular buscarPlanActual(int moduleId);
}
