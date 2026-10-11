package net.uch.academica.dao;

import net.uch.mapping.AcContenidoDetalle;
import net.uch.mapping.AcContenidoTematico;

import java.util.List;

public interface HSContenidoTematicoDAO {

    void insertarContenidoTematico(AcContenidoTematico con);

    List<AcContenidoDetalle> listarDetalle(int contemId);

    AcContenidoTematico getContenidoTematico(int plancurId);

    Object[] seleccionarDatosCT(int plancurId);

    void eliminarContenidoTematicoDetalle(List<AcContenidoDetalle> det);
}
