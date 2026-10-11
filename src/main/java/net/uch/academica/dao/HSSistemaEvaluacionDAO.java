package net.uch.academica.dao;

import net.uch.mapping.AcSisEvaluacion;

import java.util.List;

public interface HSSistemaEvaluacionDAO {

    void insertarSistemaEvaluacion(AcSisEvaluacion siseva);

    List<AcSisEvaluacion> obtenerSistemaEvaluacion(String descripcion);

    AcSisEvaluacion obtenerSistemaEvaluacion(int id);

    void actualizarSistemaEvaluacion(AcSisEvaluacion siseva);

    void eliminarSistemaEvaluacion(Integer id);

    List<AcSisEvaluacion> seleccionarComboSistemaEvaluacion();
}
