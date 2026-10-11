package net.uch.configuracion.dao;

import net.uch.mapping.TbDistrito;

import java.util.List;

public interface HSUbigeoDAO {

    List<TbDistrito> seleccionarDepartamento();

    List<TbDistrito> seleccionarProvincia(String codigoDepartamento);

    List<TbDistrito> seleccionarDistrito(String codigoDepartamento, String codigoProvincia);
}
