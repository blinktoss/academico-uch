package net.uch.configuracion.dao;

import net.uch.mapping.AcLocal;

import java.util.List;

public interface HSLocalDAO {

    void insertarLocal(AcLocal local);

    List<AcLocal> seleccionarLocal(String nombre);

    void actualizarLocal(AcLocal local);

    void eliminarLocal(AcLocal local);
}
