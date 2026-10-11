package net.uch.cursolibre.dao;

import net.uch.mapping.ClAlumnoTarifa;
import net.uch.mapping.ClEstructuraPagos;
import net.uch.mapping.ClEstructuraPagosDetalle;

import java.util.List;

public interface HSEstructuraPagoDAO {

    List<ClEstructuraPagos> seleccionarEstructuras(String descripcion, int areId, int modId);

    List<ClEstructuraPagosDetalle> seleccionarDetalle(int estpagId);

    List<ClEstructuraPagos> seleccionarEstructurasXModulo(int modId);

    List<ClEstructuraPagos> seleccionarEstructurasXTaller(int talId);

    void insertarEstructuraPagos(ClEstructuraPagos estructura);

    void actualizarEstructuraPagos(ClEstructuraPagos estructura);

    void eliminarEstructuraPagos(int estpagId);

    ClEstructuraPagos buscarEstructuraPagos(int estpagId);

    void insertarEstructuraPagoDetalle(ClEstructuraPagosDetalle estDetalle);

    void actualizarEstructuraPagoDetalle(ClEstructuraPagosDetalle estDetalle);

    void eliminarEstructuraPagosDetalle(List<Integer> estpagdetIds);

    boolean verificarEliminar(int estpagId);

    boolean verificarEliminarDetalle(int estpagdetId);

    List<ClEstructuraPagosDetalle> listaEstructurasExistentes(int modId, int estpagId, int talId);

    List<ClAlumnoTarifa> listaAlumnoTarifaExistentes(int aluId, int modId, int estpagId);
}
