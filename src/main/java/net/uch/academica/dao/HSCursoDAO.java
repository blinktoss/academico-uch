package net.uch.academica.dao;

import net.uch.mapping.AcCurso;

import java.util.List;

public interface HSCursoDAO {

    void insertOrUpdateCurso(AcCurso cur);

    List<Object[]> seleccionarCurso(String codigo, String nombre, int facultad, int especilidad);

    List<AcCurso> seleccionarCurso(int especilidad);

    void eliminarCurso(Integer id);
}
