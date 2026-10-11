/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.jsf.beans;

import net.uch.academica.dao.HSDocenteDAO;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSAulaDAO;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.mapping.AcAula;
import net.uch.mapping.AcDocente;
import net.uch.mapping.ClHoraria;
import net.uch.mapping.ClSeccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ActionEvent;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.uch.common.Constants.CAT_DEFAULT;

/**
 * @author cesardl
 */
public class BeanClHoraria extends ClHoraria {

    private static final long serialVersionUID = -8107658791216804036L;

    private static final Logger LOG = LoggerFactory.getLogger(BeanClHoraria.class);

    //Temporales
    private int auxAula;
    private int auxDocente;
    private String auxDia;
    private String auxTipoClase;
    private String[] auxFechaIni;
    private String[] auxFechaFin;
    private int posicion;
    private int seccionId;
    private String horaInicio;
    private String minutoInicio;
    private String horaFin;
    private String minutoFin;
    private String meridianoInicio;
    private String meridianoFin;
    private String horDiaValue;
    private String horTipoClaseValue;
    private boolean editable;

    public BeanClHoraria(Integer secId) {
        this.setSeccionId(secId);
        this.setHorDia(CAT_DEFAULT);
        this.setHorTipoClase(CAT_DEFAULT);
        this.setHoraInicio("00");
        this.setMinutoInicio("00");
        this.setHoraFin("00");
        this.setMinutoFin("00");
        this.setMeridianoInicio("AM");
        this.setMeridianoFin("AM");

        this.setAcAula(new AcAula());
        this.setAcDocente(new AcDocente());
        this.setClSeccion(new ClSeccion(secId));
    }

    public BeanClHoraria(ClHoraria horaria) {
        this.setSeccionId(horaria.getClSeccion().getSecId());
        this.setHorId(horaria.getHorId());

        this.setHorDia(horaria.getHorDia());
        this.setHorTipoClase(horaria.getHorTipoClase());

        this.setHorHini(horaria.getHorHini());
        this.setHorHfin(horaria.getHorHfin());

        this.setHorActivo(horaria.getHorActivo());
        this.setAcAula(horaria.getAcAula());
        this.setAcDocente(horaria.getAcDocente());
    }

    public ClHoraria getClHoraria() {
        var horaria = new ClHoraria();
        horaria.setHorId(this.getHorId());
        horaria.setAcAula(this.getAcAula());
        horaria.setAcDocente(this.getAcDocente());
        horaria.setClSeccion(new ClSeccion(this.seccionId));
        horaria.setHorDia(this.getHorDia());
        horaria.setHorHini(this.getHorHini());
        horaria.setHorHfin(this.getHorHfin());
        horaria.setHorTipoClase(this.getHorTipoClase());
        horaria.setHorActivo(this.getHorActivo());

        return horaria;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(int seccionId) {
        this.seccionId = seccionId;
    }

    public String getHorDiaValue() {
        return horDiaValue;
    }

    public void setHorDiaValue(String horDiaValue) {
        this.horDiaValue = horDiaValue;
    }

    public String getHorTipoClaseValue() {
        return horTipoClaseValue;
    }

    public void setHorTipoClaseValue(String horTipoClaseValue) {
        this.horTipoClaseValue = horTipoClaseValue;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getMinutoInicio() {
        return minutoInicio;
    }

    public void setMinutoInicio(String minutoInicio) {
        this.minutoInicio = minutoInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getMinutoFin() {
        return minutoFin;
    }

    public void setMinutoFin(String minutoFin) {
        this.minutoFin = minutoFin;
    }

    public String getMeridianoFin() {
        return meridianoFin;
    }

    public void setMeridianoFin(String meridianoFin) {
        this.meridianoFin = meridianoFin;
    }

    public String getMeridianoInicio() {
        return meridianoInicio;
    }

    public void setMeridianoInicio(String meridianoInicio) {
        this.meridianoInicio = meridianoInicio;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int getAuxAula() {
        return auxAula;
    }

    public void setAuxAula(int auxAula) {
        this.auxAula = auxAula;
    }

    public String getAuxDia() {
        return auxDia;
    }

    public void setAuxDia(String auxDia) {
        this.auxDia = auxDia;
    }

    public int getAuxDocente() {
        return auxDocente;
    }

    public void setAuxDocente(int auxDocente) {
        this.auxDocente = auxDocente;
    }

    public String getAuxTipoClase() {
        return auxTipoClase;
    }

    public void setAuxTipoClase(String auxTipoClase) {
        this.auxTipoClase = auxTipoClase;
    }

    public void edit(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.auxFechaIni = convertirTimeString(this.getHorHini());
        this.auxFechaFin = convertirTimeString(this.getHorHfin());
        this.setAuxAula(this.getAcAula().getId());
        this.setAuxDocente(this.getAcDocente().getId());
        this.setAuxDia(this.getHorDia());
        this.setAuxTipoClase(this.getHorTipoClase());

        this.setHoraInicio(auxFechaIni[0]);
        this.setMinutoInicio(auxFechaIni[1]);
        this.setMeridianoInicio(auxFechaIni[2]);
        this.setHoraFin(auxFechaFin[0]);
        this.setMinutoFin(auxFechaFin[1]);
        this.setMeridianoFin(auxFechaFin[2]);

        this.setEditable(true);
    }

    public void aceptar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.actualizarDatos(this.seccionId);

        this.setEditable(false);
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setHorHini(convertirFechaTime(auxFechaIni[0], auxFechaIni[1], auxFechaIni[2]));
        this.setHorHfin(convertirFechaTime(auxFechaFin[0], auxFechaFin[1], auxFechaFin[2]));
        this.getAcAula().setId(getAuxAula());
        this.getAcDocente().setId(getAuxDocente());
        this.setHorDia(getAuxDia());
        this.setHorTipoClase(getAuxTipoClase());

        this.setEditable(false);
    }

    public boolean esValido() {
        return (!(getHorDia().equalsIgnoreCase(CAT_DEFAULT))
                && !(getHorTipoClase().equalsIgnoreCase(CAT_DEFAULT))
                && convertirFechaTime(this.horaInicio, this.minutoInicio, meridianoInicio).getTime()
                < convertirFechaTime(this.horaFin, minutoFin, meridianoFin).getTime()
                && getAcAula().getId() != 0
                && getAcDocente().getId() != 0);
    }

    public String[] convertirTimeString(Time time) {
        var fecha = new String[3];
        var sdf = new SimpleDateFormat("hh:mm a");
        var fec = new Date(time.getTime());
        String formatted = sdf.format(fec);
        fecha[0] = formatted.substring(0, 2);
        fecha[1] = formatted.substring(3, 5);
        fecha[2] = formatted.substring(6, 8);
        return fecha;
    }

    public Time convertirFechaTime(String hora, String minuto, String meridiano) {
        try {
            Date fecha;
            String fec = hora.trim() + ":" + minuto.trim() + " " + meridiano.trim();
            var sdf = new SimpleDateFormat("hh:mm a");
            fecha = sdf.parse(fec);

            return new Time(fecha.getTime());
        } catch (ParseException e) {
            LOG.error("Hubo un problema en la conversion a Time", e);
        }
        return null;
    }

    public void actualizarDatos(int secId) {
        HSDocenteDAO daoD = ServiceFinder.findBean("springHibernateDaoDocente", HSDocenteDAO.class);
        HSAulaDAO daoA = ServiceFinder.findBean("springHibernateDaoAula", HSAulaDAO.class);
        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);

        this.setClSeccion(new ClSeccion(secId));
        this.setAcDocente(daoD.seleccionarDocente(this.getAcDocente().getId()));
        this.setAcAula(daoA.buscarAula(this.getAcAula().getId()));
        this.setHorHini(convertirFechaTime(this.horaInicio, this.minutoInicio, this.meridianoInicio));
        this.setHorHfin(convertirFechaTime(this.horaFin, this.minutoFin, this.meridianoFin));
        this.setHorActivo("1");
        this.setHorDiaValue(daoCatalogo.seleccionarDescripcion(this.getHorDia()));
        this.setHorTipoClaseValue(daoCatalogo.seleccionarDescripcion(this.getHorTipoClase()));
    }
}
