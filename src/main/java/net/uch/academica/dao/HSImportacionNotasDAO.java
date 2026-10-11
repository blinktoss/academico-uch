package net.uch.academica.dao;

import net.uch.mapping.AcImportacionNotas;
import net.uch.mapping.AcSisEvaPersonalizado;

import java.util.List;

/**
 * @author richard
 */
public interface HSImportacionNotasDAO {

    List<AcSisEvaPersonalizado> listarSistemaEvaluacionPersonalizadoPorSeccion(int seccionId);

    AcImportacionNotas listarImportacionNotaPorSeccion(int seccionId, int sisevaId);

    void agregarImportacionNota(AcImportacionNotas notas);

    void actualizarImportacionNota(AcImportacionNotas notas);
}
