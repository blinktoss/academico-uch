package net.uch.academica.dao;

import net.uch.mapping.AcPlancurricular;

import java.util.List;

public interface HSPlanCurricularDAO {

    Integer insertarPlanCurricular(AcPlancurricular pc);

    List<Object[]> seleccionarPlanCurricular(String codigo, String nombre, int facultad, int especialidad);

    void actualizarPlanCurricular(AcPlancurricular pc);

    void eliminarPlanCurricular(String id);

    List<AcPlancurricular> seleccionarPlanActivo(int especialidadId);

    List<AcPlancurricular> seleccionarPlanCurricular(int especialidad);

    /**
     * All active curricular plans for the given specialties, with their {@code AcPlanCursos}
     * join-fetched, in a single query - so the tree builder can group them in-memory by specialty
     * instead of running one query per specialty. Same filters, fetch and result transformer as
     * {@link #seleccionarPlanCurricular(int)}, so each specialty's plans (and their courses) come out
     * in the same order.
     */
    List<AcPlancurricular> findPlanesByEspecialidades(List<Integer> especialidadIds);
}
