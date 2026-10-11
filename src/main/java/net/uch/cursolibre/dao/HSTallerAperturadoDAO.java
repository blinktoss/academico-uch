package net.uch.cursolibre.dao;

import net.uch.mapping.ClTallerAperturado;

import java.util.List;

public interface HSTallerAperturadoDAO {

    ClTallerAperturado seleccionarTallerAperturado(int talapeId);

    List<ClTallerAperturado> seleccionarTalleresAperturados();

    List<ClTallerAperturado> seleccionarTallerAperturadoPorCurso(int curId);

    /**
     * All opened workshops for the given courses as a flat projection, in a single query, so the
     * tree builder can group them in-memory by course instead of running one query per course.
     * Ordered by (course, opened-workshop id) so each course's workshops come out in the same order
     * as {@link #seleccionarTallerAperturadoPorCurso(int)}.
     */
    List<CLTallerAperturadoRow> findOpenedWorkshopsByCourses(List<Integer> courseIds);

    List<ClTallerAperturado> seleccionarTalleresAperturadosPorTaller(int talId);

    List<ClTallerAperturado> seleccionarTalleresAperturadosPorModulo(int modId, int curId);

    void insertarOrActualizarTallerAperturado(ClTallerAperturado talape);

    void eliminarTallerAperturado(Integer talapeId);
}
