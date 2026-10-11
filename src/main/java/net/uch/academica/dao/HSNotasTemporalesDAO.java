package net.uch.academica.dao;

import net.uch.mapping.AcNotasTemporales;

import java.util.List;

/**
 * @author richard
 */
public interface HSNotasTemporalesDAO {

    List<AcNotasTemporales> listarNotasTemporales(int secId, int sisevaPerId);
}
