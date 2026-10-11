package net.uch.administrativa.dao;

import net.uch.mapping.AdClientes;

import java.util.List;

public interface HSClienteDAO {

    void insertarCliente(AdClientes cli);

    List<AdClientes> seleccionarCliente(String buscar);

    int seleccionarUnCliente(String ruc);

    AdClientes seleccionarUnCliente(int id);
}
