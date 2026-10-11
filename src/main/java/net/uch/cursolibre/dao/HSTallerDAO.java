package net.uch.cursolibre.dao;

import net.uch.mapping.ClTaller;

import java.util.List;

public interface HSTallerDAO {

    List<ClTaller> seleccionarTalleres(int cursoId);

    void insertarTaller(ClTaller taller);

    void actualizarTaller(ClTaller taller);

    int eliminarTalleres(List<Integer> talsId);

    boolean tieneTalleresAperturados(int tallerId);

    ClTaller buscarTaller(int tallerId);

    List<ClTaller> seleccionarTalleresXMod(int modId);
}
