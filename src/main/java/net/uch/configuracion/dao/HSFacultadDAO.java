package net.uch.configuracion.dao;

import net.uch.mapping.AcFacultad;

import java.util.List;

public interface HSFacultadDAO {

    void insertarFacultad(AcFacultad facultad);

    List<AcFacultad> seleccionarFacultad(String codigo, String nombre);

    void actualizarFacultad(AcFacultad facultad);

    void eliminarFacultad(AcFacultad facultad);
}
