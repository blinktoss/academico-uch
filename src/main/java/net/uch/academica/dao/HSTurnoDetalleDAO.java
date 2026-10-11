package net.uch.academica.dao;

import net.uch.mapping.AcTurnoDetalle;

import java.util.List;

public interface HSTurnoDetalleDAO {

    List<AcTurnoDetalle> seleccionarTurnoDetalle(int idTurno);

    void delete(List<AcTurnoDetalle> turnosDetalle);
}
