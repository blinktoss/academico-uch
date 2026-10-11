/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.banco.dao;

import net.uch.mapping.*;

import java.util.List;

/**
 * @author richard
 */
public interface HSBancoDAO {

    List<TbBanco> listarBanco(int banId);

    List<SpDataBanco> listarDataBanco();

    List<SpConceptosBanco> listarConceptosBanco();

    void guardarRespuestaBanco(List<TbBancoEntrada> entradas);

    TbBancoEntrada existeNroOperacion(String nroOperacion);

    List<TbPagoBanco> pagosBanco(Integer codigoGrupo);
}

