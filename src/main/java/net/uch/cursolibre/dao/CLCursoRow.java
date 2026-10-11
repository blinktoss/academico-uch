package net.uch.cursolibre.dao;

/**
 * Flat projection row for building the Free Courses (Cursos Libres) tree: one course with just the
 * columns the tree needs plus its parent module id, so the tree can prefetch every course for a set
 * of modules in a single query instead of one query per module. Built by an HQL constructor
 * expression in {@link HSCursoCLDAO#findCursosByModulos(java.util.List)}.
 */
public class CLCursoRow {

    private final Integer moduloId;
    private final Integer cursoId;
    private final String cursoNombre;

    public CLCursoRow(Integer moduloId, Integer cursoId, String cursoNombre) {
        this.moduloId = moduloId;
        this.cursoId = cursoId;
        this.cursoNombre = cursoNombre;
    }

    public Integer getModuloId() {
        return moduloId;
    }

    public Integer getCursoId() {
        return cursoId;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }
}
