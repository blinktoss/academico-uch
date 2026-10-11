package net.uch.administrativa.dao;

import net.uch.academica.jsf.beans.ObjUpdate;
import net.uch.mapping.AcAlumno;
import net.uch.mapping.AdAlumnoTarifa;
import net.uch.mapping.AdEstructuraPagos;
import net.uch.mapping.AdPago;

import java.util.Date;
import java.util.List;

/**
 * @author cesardl
 */
public interface HSEdicionEstructPagoDAO {

    void insertarAlumnoTarifa(AdAlumnoTarifa alumnoTarifa);

    List<AcAlumno> listaAlumnoEstructura(String code, String paterno, String materno, String nombre, int semestre);

    List<AdAlumnoTarifa> listaTarifasAlumno(int semestre, int alumnoId);

    List<AcAlumno> listaAlumnoActivo(String dato);

    List<AdEstructuraPagos> listaEstructurasPago(int sem, int esp, int semIngreso);

    AdEstructuraPagos estructuraPagoPlantilla(int estpagId);

    List<AdEstructuraPagos> listaEstructurasExistentes(int sem, int esp, int semIngreso);

    List<AdAlumnoTarifa> listaAlumnoTarifaExistentes(int aluId, int sem, int esp);

    void actualizarEstadoPago(int alutarId, String estado);

    /**
     * Lista los pagos que ha realizado el alumno
     * Caso 0 - el estado de la boleta es 'valido' -> 037001
     *
     * @param alutarId id
     * @return list
     */
    List<AdPago> listaPagosAlumno(int alutarId, int caso);

    void actualizarDatosAlumnoTarifa(Integer alutarId, Float monto, Date fechaPago);

    int eliminarEstructurasPagoAlumnoTarifa(List<Integer> alutarIds);

    int actualizarEstructurasPagoBloque(int semestreDestino, int esp, List<ObjUpdate> restricciones, List<ObjUpdate> valores);

    /**
     * Issue #33 (review F1): atomic block edit for when the deadline moves. In a single transaction it
     * appends one ad_prorroga history row per tariff matched by the block filter (dated
     * {@code fechaProrroga}) AND applies the bulk column update ({@code valores}) - so the audit
     * history and the moved effective deadline commit together, or neither does. Enumerate-then-insert
     * avoids MySQL's self-referential INSERT..SELECT restriction.
     *
     * @return the number of tariffs updated by the bulk column update
     */
    int actualizarEstructurasPagoBloqueConHistorial(int semestreDestino, int esp, List<ObjUpdate> restricciones,
                                                    List<ObjUpdate> valores, Date fechaProrroga, String comentario, int usuId);
}
