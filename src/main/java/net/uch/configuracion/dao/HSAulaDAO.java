package net.uch.configuracion.dao;

import net.uch.mapping.AcAula;

import java.util.List;

public interface HSAulaDAO {

    Integer insertarAula(AcAula aula);

    List<AcAula> seleccionarAula(String nombre, int local, int pabellon);

    void eliminarAula(Integer id);

    void actualizarAula(AcAula aula);

    List<AcAula> seleccionarAula();

    AcAula buscarAula(int aulaId);
}
