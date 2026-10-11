package net.uch.administrativa.dao;

import net.uch.mapping.AdProrroga;

import java.util.List;

/**
 * Created for sistema_academico on 27/07/2024.
 *
 * @author Cesardl
 */
public interface HSProrrogaDAO {

    void insertarProrroga(AdProrroga prorroga);

    List<AdProrroga> listaProrrogas(int alutarId);
}
