package net.uch.cursolibre.dao;

/**
 * Flat projection row for building the Free Courses (Cursos Libres) tree: one opened workshop
 * (taller aperturado) with just the columns the tree needs plus its parent course id, so the tree
 * can prefetch every opened workshop for a set of courses in a single query instead of one query per
 * course. Built by an HQL constructor expression in
 * {@link HSTallerAperturadoDAO#findOpenedWorkshopsByCourses(java.util.List)}.
 */
public class CLTallerAperturadoRow {

    private final Integer cursoId;
    private final Integer tallerAperturadoId;
    private final String tallerAperturadoDescripcion;

    public CLTallerAperturadoRow(Integer cursoId, Integer tallerAperturadoId, String tallerAperturadoDescripcion) {
        this.cursoId = cursoId;
        this.tallerAperturadoId = tallerAperturadoId;
        this.tallerAperturadoDescripcion = tallerAperturadoDescripcion;
    }

    public Integer getCursoId() {
        return cursoId;
    }

    public Integer getTallerAperturadoId() {
        return tallerAperturadoId;
    }

    public String getTallerAperturadoDescripcion() {
        return tallerAperturadoDescripcion;
    }
}
