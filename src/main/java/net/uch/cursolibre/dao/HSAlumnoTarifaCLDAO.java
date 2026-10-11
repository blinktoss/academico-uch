package net.uch.cursolibre.dao;

import net.uch.academica.jsf.beans.ObjUpdate;
import net.uch.mapping.ClAlumnoTarifa;

import java.util.Date;
import java.util.List;

/**
 * @author cesardl
 */
public interface HSAlumnoTarifaCLDAO {

    void generarAlumnoTarifa(ClAlumnoTarifa alumnoTarifa);

    void generarAlumnosTarifa(List<ClAlumnoTarifa> alumnosTarifa);

    void actualizarDatosAlumnoTarifa(int alutarID, float monto, Date fechaPago, Date fechaProrroga);

    void eliminarAlumnosTarifa(List<Integer> alutarIds);

    List<ClAlumnoTarifa> seleccionarTarifasXMatricula(int matId, int aluId);

    /**
     * One page of active CL tariffs matching the free-courses payment-search filters.
     */
    List<ClAlumnoTarifa> buscarTarifas(String codAlu, String estadoId, int conceptoId, String apPaterno, String apMaterno, int first, int max);

    /**
     * Total active CL tariffs matching the {@link #buscarTarifas} filters (for pagination).
     */
    long contarTarifas(String codAlu, String estadoId, int conceptoId, String apPaterno, String apMaterno);

    int actualizarEstructurasPagoBloque(int sec, List<ObjUpdate> restricciones, List<ObjUpdate> valores);

    boolean existenPagosMatricula(int aluId, int alutarId);
}
