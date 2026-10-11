package net.uch.cursolibre.dao;

import net.uch.mapping.ClArea;

import java.util.List;

public interface HSAreaDAO {

    List<ClArea> seleccionarArea(String descripcion);

    void eliminarArea(int id);

    void insertarArea(ClArea area);

    void actualizarArea(ClArea area);

    ClArea buscarArea(int id);
}
