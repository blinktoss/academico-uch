package net.uch.asistencia.dao;

import net.uch.mapping.AcArea;
import net.uch.mapping.AcDocente;
import net.uch.mapping.AcSesionAsistencia;
import net.uch.mapping.AcSesionEfectivaAsisDoc;

import java.util.Date;
import java.util.List;

/**
 * @author cesardl
 */
public interface HSAsistenciaDocenteDAO {

    List<AcDocente> buscarDocenteDato(String dato);

    List<AcSesionAsistencia> listarSessionAsistenciaDocenteFecha(int docId, Date fechaIni, Date fechaFin, String tipasis);

    Object[] seleccionarSeccion(int seccionId);

    AcArea seleccionarArea(int seccionId);

    AcSesionEfectivaAsisDoc listaMarcadoDeAsistenciaDocente(int sesionId);

    void ingresarAsisteanDocente(AcSesionEfectivaAsisDoc acSesionEfectivaAsisDoc);

    void ingresarSesionAsistencia(AcSesionAsistencia sesasis);

    AcSesionAsistencia getSessionAsistencia(int sesionId);

    void actualizarSesionDocente(int sesionId, int docenteId, Date horaIni, Date horaFin, String sesionTipo);
}
