package net.uch.cursolibre.dao;

/**
 * Flat projection row for building the Free Courses (Cursos Libres) tree: one module with just the
 * columns the tree needs plus its parent area id, so the tree can prefetch every module for a set of
 * areas in a single query instead of one query per area. Built by an HQL constructor expression in
 * {@link HSModuloDAO#findModulosByAreas(java.util.List)}.
 */
public class CLModuloRow {

    private final Integer areaId;
    private final Integer moduloId;
    private final String moduloDescripcion;

    public CLModuloRow(Integer areaId, Integer moduloId, String moduloDescripcion) {
        this.areaId = areaId;
        this.moduloId = moduloId;
        this.moduloDescripcion = moduloDescripcion;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public Integer getModuloId() {
        return moduloId;
    }

    public String getModuloDescripcion() {
        return moduloDescripcion;
    }
}
