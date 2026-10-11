package net.uch.academica.dao;

import net.uch.mapping.TbAsistencia;

import java.util.Date;
import java.util.List;

public interface HSAsistenciaDAO {

    /**
     * Evaluate assitance record, from ´sigu_intranet´, teacher can register student
     * assistant in ´ac_sesion_efectiva_asistencia´.
     *
     * @param aluId  student id
     * @param codigo student code
     * @param desde  date from
     * @param hasta  date to
     * @return assitance list
     */
    List<TbAsistencia> seleccionarAsistencia(int aluId, String codigo, Date desde, Date hasta);
}
