package net.uch.cursolibre.dao;

import net.uch.mapping.ClSisEvaPersonalizado;
import net.uch.mapping.ClSisEvaluacion;
import net.uch.mapping.ClTallerAperturado;

import java.util.List;

/**
 * @author cesardl
 */
public interface HSSistemaEvaluacionCLDAO {

    List<ClSisEvaPersonalizado> seleccionarSisEvaPerClTalape(ClTallerAperturado talape);

    ClSisEvaluacion seleccionarSistemaEvaluacion(int sisevaId);

    List<ClSisEvaluacion> seleccionarSistemasEvaluacion();

    void insertarActualizarSisEvaPersonalizado(List<ClSisEvaPersonalizado> lPer);

    void eliminarSistemaEvaluacionPersonalizado(int id);
}
