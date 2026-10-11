package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSHorarioAreaDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcArea;
import net.uch.mapping.AcHorarioArea;

import java.util.List;

/**
 * Created for sistema_academico on 02/09/2024.
 *
 * @author Cesardl
 */
public class HSHorarioAreaDAOImpl extends TypedHibernateDao implements HSHorarioAreaDAO {

    @Override
    public List<AcArea> findActiveAreas() {
        return find(AcArea.class, "from AcArea where areActivo = '1'");
    }

    @Override
    public List<AcHorarioArea> listHorarioAreaByArea(int areaId) {
        String hql = "from AcHorarioArea ha " +
                "inner join fetch ha.acArea are " +
                "inner join fetch ha.acTurnoDetalle turdet " +
                "where ha.horareActivo = '1' " +
                "and are.areActivo = '1' " +
                "and turdet.TurdetActivo = '1' " +
                "and are.id = :areaId " +
                "order by ha.horareOrden";
        return find(AcHorarioArea.class, hql, "areaId", areaId);
    }
}
