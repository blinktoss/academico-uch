package net.uch.configuracion.dao;

import net.uch.mapping.TbCatalogo;

import java.util.List;

public interface HSCatalogoDAO {

    List<TbCatalogo> seleccionarCatalogo(String codigo);

    String seleccionarDescripcion(String codigo);

    String seleccionarValor(String codigo);

    List<TbCatalogo> seleccionarGrupo(String codigo);
}
