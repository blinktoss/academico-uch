package net.uch.administrativa.dao;

import net.uch.mapping.AdConceptoPago;

import java.util.List;

public interface HSConceptoPagoDAO {

    void insertarConceptoPago(AdConceptoPago conPago);

    List<Object[]> seleccionarConceptoPago(String nombre, String codigo, String tipo);

    void eliminarConceptoPago(int id);

    void actualizarConceptoPago(AdConceptoPago conPago);

    List<AdConceptoPago> seleccionarConceptoPago();

    AdConceptoPago seleccionarUnaConceptoPago(int conPagId);

    List<AdConceptoPago> seleccionarConceptoCursoLibre(String descripcion);

    /**
     * Active payment concepts for the search screen's concept filter: only Curso Libre ({@code
     * 007003}) when {@code cursoLibre} is true, otherwise every concept <em>except</em> Curso Libre.
     */
    List<AdConceptoPago> seleccionarConceptosParaBusqueda(boolean cursoLibre);
}
