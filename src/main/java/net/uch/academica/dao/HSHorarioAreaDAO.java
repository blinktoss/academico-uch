package net.uch.academica.dao;

import net.uch.mapping.AcArea;
import net.uch.mapping.AcHorarioArea;

import java.util.List;

/**
 * Created for sistema_academico on 02/09/2024.
 *
 * @author Cesardl
 */
public interface HSHorarioAreaDAO {

    List<AcArea> findActiveAreas();

    List<AcHorarioArea> listHorarioAreaByArea(int areaId);
}
