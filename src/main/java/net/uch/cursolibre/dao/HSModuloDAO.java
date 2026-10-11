package net.uch.cursolibre.dao;

import net.uch.mapping.ClModulo;

import java.util.List;

public interface HSModuloDAO {

    List<ClModulo> seleccionarModulos(int areaId, String descripcion);

    /**
     * All active modules for the given areas as a flat projection, in a single query, so the tree
     * builder can group them in-memory by area instead of running one query per area. Ordered by
     * (area, module id) so each area's modules come out in the same order as
     * {@link #seleccionarModulos(int, String)}.
     */
    List<CLModuloRow> findModulosByAreas(List<Integer> areaIds);

    void insertarModulo(ClModulo modulo);

    void eliminarModulo(int moduloId);

    void actualizarModulo(ClModulo modulo);

    ClModulo buscarModulo(int moduloId);

    List<ClModulo> listarModulos();
}
