package net.uch.administrativa.dao;

import net.uch.mapping.AdAlumnoTarifa;

import java.util.List;

public interface HSAlumnoTarifaDAO {

    List<AdAlumnoTarifa> seleccionarAlumnoTarifa(String codAlu, String idEstado, int idEsp, int idConPag, String apPaterno, String apMaterno, int semId);

    /**
     * Paged variant of {@link #seleccionarAlumnoTarifa}: same filters/order, one page window.
     */
    List<AdAlumnoTarifa> seleccionarAlumnoTarifa(String codAlu, String idEstado, int idEsp, int idConPag, String apPaterno, String apMaterno, int semId, int first, int max);

    /**
     * Total rows matching the {@link #seleccionarAlumnoTarifa} filters (for pagination).
     */
    long contarAlumnoTarifa(String codAlu, String idEstado, int idEsp, int idConPag, String apPaterno, String apMaterno, int semId);

    void insertarTotal(List<AdAlumnoTarifa> alumnoTarifas);

    void cambiarEstado(int id, String estado);

    void actualizarAlumnoTarifa(AdAlumnoTarifa alumnoTarifa);

    AdAlumnoTarifa seleccionarUnAlumnoTarifa(int alutarId);

    List<AdAlumnoTarifa> seleccionarAlumnoTarifa(int alumnoId);
}
