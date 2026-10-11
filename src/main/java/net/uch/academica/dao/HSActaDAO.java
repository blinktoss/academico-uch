package net.uch.academica.dao;

import net.uch.mapping.AcActa;
import net.uch.mapping.AcActaDetalle;

import java.util.List;

public interface HSActaDAO {

    void saveOrUpdateActa(AcActa acta);

    void insertarActaDetalles(List<AcActaDetalle> actasDetalle);

    AcActa seleccionarActa(int secId, String actaTipo);

    List<Object[]> seleccionarAlumnosActa(int secId);

    void eliminarActa(AcActa acta);

    /**
     * Deletes actaVieja (if not null) and saves actaNueva in the same transaction, so a
     * Sustitutoria replacement can't leave the old record deleted with nothing rebuilt if the
     * app fails partway through.
     */
    void reemplazarActaSustitutoria(AcActa actaVieja, AcActa actaNueva);
}
