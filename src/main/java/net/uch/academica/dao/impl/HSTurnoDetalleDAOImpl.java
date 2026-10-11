package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSTurnoDetalleDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcTurnoDetalle;

import java.util.List;

public class HSTurnoDetalleDAOImpl extends TypedHibernateDao implements HSTurnoDetalleDAO {

    @Override
    public List<AcTurnoDetalle> seleccionarTurnoDetalle(int idTurno) {
        var hql = "from AcTurnoDetalle "
                + "where Tur.Id= :turno and TurdetActivo = :activo";

        return find(AcTurnoDetalle.class, hql, new String[]{"turno", "activo"},
                new Object[]{idTurno, "1"});
    }

    @Override
    public void delete(List<AcTurnoDetalle> turnosDetalle) {
        this.getHibernateTemplate().deleteAll(turnosDetalle);
    }
}
