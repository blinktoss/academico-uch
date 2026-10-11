package net.uch.configuracion.dao;

import net.uch.mapping.AcSemestre;

import java.util.List;

public interface HSSemestreDAO {

    Integer insertarSemestre(AcSemestre semestre);

    List<AcSemestre> seleccionarSemestre(String codigo, String nombre);

    void eliminarSemestre(String id);

    void actualizarSemestre(AcSemestre se);

    List<AcSemestre> seleccionarSemestresVigentes();

    List<AcSemestre> seleccionarSemestreActivo();

    AcSemestre seleccionarSemestreActual();

    AcSemestre getSemestre(Integer semestreId);
}
