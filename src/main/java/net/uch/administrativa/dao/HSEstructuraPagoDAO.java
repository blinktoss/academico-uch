package net.uch.administrativa.dao;

import net.uch.mapping.AdEstructuraPagos;
import net.uch.mapping.AdEstructuraPagosDetalle;

import java.util.List;

public interface HSEstructuraPagoDAO {

    void insertarEstructuraPagos(AdEstructuraPagos siseva);

    List<AdEstructuraPagos> seleccionarEstructuraPagos(int espId, int semId);

    AdEstructuraPagos seleccionarUnaEstructuraPagos(int id);

    void actualizarEstructuraPagos(AdEstructuraPagos siseva);

    void eliminarEstructuraPagos(int id);

    AdEstructuraPagosDetalle seleccionarUnaEstructuraPagosDet(int id);

    List<AdEstructuraPagosDetalle> seleccionarEstructuraPagosDet();

}
