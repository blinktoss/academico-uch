package net.uch.configuracion.dao;

import net.uch.mapping.AcPabellon;

import java.util.List;

public interface HSPabellonDAO {

    void insertarPabellon(AcPabellon pabellon);

    List<AcPabellon> seleccionarPabellon(String nombre, int local);

    void actualizarPabellon(AcPabellon pabellon);

    void eliminarPabellon(AcPabellon pabellon);
}
