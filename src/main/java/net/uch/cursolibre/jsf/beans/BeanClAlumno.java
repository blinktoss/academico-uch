package net.uch.cursolibre.jsf.beans;

import net.uch.mapping.ClAlumno;

/**
 * @author cesardl
 */
public class BeanClAlumno extends ClAlumno {

    private static final long serialVersionUID = 3076117467172187317L;

    private int aluContador;
    private boolean aluMatriculado;

    public BeanClAlumno(ClAlumno alumno) {
        this.setAluId(alumno.getAluId());
        this.setAluCodigo(alumno.getAluCodigo());
        this.setAluNombres(alumno.getAluNombres());
        this.setAluAppaterno(alumno.getAluAppaterno());
        this.setAluApmaterno(alumno.getAluApmaterno());
        this.setAluMatriculado(true);
    }

    public int getAluContador() {
        return aluContador;
    }

    public void setAluContador(int aluContador) {
        this.aluContador = aluContador;
    }

    public boolean isAluMatriculado() {
        return aluMatriculado;
    }

    public void setAluMatriculado(boolean aluMatriculado) {
        this.aluMatriculado = aluMatriculado;
    }
}
