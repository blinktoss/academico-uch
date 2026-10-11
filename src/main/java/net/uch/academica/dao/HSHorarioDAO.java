package net.uch.academica.dao;

import net.uch.academica.jsf.beans.BeanDuplicadoHorario;
import net.uch.mapping.AcHorario;

import java.util.List;

public interface HSHorarioDAO {

    void insertarHorarios(List<AcHorario> cur);

    List<AcHorario> seleccionarHorario(int sec);

    AcHorario seleccionarUnHorario(int horarioId);

    List<BeanDuplicadoHorario> duplicidadHoraria(List<Integer> seccionIds, int semestreId);

    int seleccionarHorarioPorDocente(Integer docenteId, Integer semestreId);
}
