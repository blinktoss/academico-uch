/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.jsf.beans;

import net.uch.mapping.ClSeccion;
import net.uch.mapping.ClTallerAperturado;

import java.util.LinkedHashSet;

/**
 * @author cesardl
 */
public class BeanClSeccion extends ClSeccion {

    private static final long serialVersionUID = -6600011402575704790L;

    private String imagenHorario;
    private int cantMatriculados;

    public BeanClSeccion(int talapeId) {
        this.setClTallerAperturado(new ClTallerAperturado(talapeId));

        this.setSecId(0);
        this.setSecCodigo("");
        this.setSecNombre("");
        this.setSecTurno("X");
        this.setSecVacMax(0);
        this.setSecVacMin(0);

        this.setSecActivo("1");

        this.setSecFfin(null);
        this.setSecFfinMatricula(null);
        this.setSecFinicio(null);
        this.setSecFinicioMatricula(null);
    }

    public BeanClSeccion(ClSeccion seccion) {
        this.setClTallerAperturado(seccion.getClTallerAperturado());

        this.setSecId(seccion.getSecId());
        this.setSecCodigo(seccion.getSecCodigo());
        this.setSecNombre(seccion.getSecNombre());
        this.setSecTurno(seccion.getSecTurno());
        this.setSecVacMax(seccion.getSecVacMax());
        this.setSecVacMin(seccion.getSecVacMin());

        this.setSecActivo(seccion.getSecActivo());

        this.setSecFfin(seccion.getSecFfin());
        this.setSecFfinMatricula(seccion.getSecFfinMatricula());
        this.setSecFinicio(seccion.getSecFinicio());
        this.setSecFinicioMatricula(seccion.getSecFinicioMatricula());
    }

    public boolean esValido() {
        return !(this.getSecCodigo().trim().length() == 0
                || this.getSecNombre().trim().length() == 0
                || this.getSecVacMax() == 0
                || this.getSecVacMin() == 0
                || this.getSecTurno().equalsIgnoreCase("X")
                || this.getSecFinicio() == null
                || this.getSecFfin() == null
                || this.getSecFinicioMatricula() == null
                || this.getSecFfinMatricula() == null);
    }

    public String getImagenHorario() {
        return imagenHorario;
    }

    public void setImagenHorario(String imagenHorario) {
        this.imagenHorario = imagenHorario;
    }

    public int getCantMatriculados() {
        return cantMatriculados;
    }

    public void setCantMatriculados(int cantMatriculados) {
        this.cantMatriculados = cantMatriculados;
    }

    public ClSeccion getSeccion() {
        var sec = new ClSeccion();
        sec.setClTallerAperturado(this.getClTallerAperturado());
        sec.setClMatriculaTallers(new LinkedHashSet<>());
        sec.setClHorarias(new LinkedHashSet<>());

        sec.setSecId(this.getSecId());
        sec.setSecCodigo(this.getSecCodigo());
        sec.setSecNombre(this.getSecNombre());
        sec.setSecTurno(this.getSecTurno());
        sec.setSecVacMax(this.getSecVacMax());
        sec.setSecVacMin(this.getSecVacMin());

        sec.setSecActivo(this.getSecActivo());

        sec.setSecFfin(this.getSecFfin());
        sec.setSecFfinMatricula(this.getSecFfinMatricula());
        sec.setSecFinicio(this.getSecFinicio());
        sec.setSecFinicioMatricula(this.getSecFinicioMatricula());

        return sec;
    }
}
