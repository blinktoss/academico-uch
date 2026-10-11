package net.uch.academica.dao;

import net.uch.mapping.AcAlumnoDato;
import net.uch.mapping.AcAlumnoDocumento;

import java.util.List;

public interface HSAlumnoDatoDAO {

    void insertarAlumnoDato(AcAlumnoDato alu);

    AcAlumnoDato seleccionarAlumnoDato(int alumnoId);

    void actualizarAlumnoDato(AcAlumnoDato aludat);

    int eliminarDocumentos(int alumnoId);

    void insertarDocumentos(AcAlumnoDocumento aluDoc);

    List<AcAlumnoDocumento> seleccionarDocumentos(int alumnoId);
}
