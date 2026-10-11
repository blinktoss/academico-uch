package net.uch.academica.jsf;

import net.uch.academica.dao.HSDocenteDAO;
import net.uch.common.ServiceFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.faces.event.ActionEvent;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created for sistema_academico on 15/09/2024.
 *
 * @author Cesardl
 */
public class MbDocenteDisponibilidad {

    private static final Logger LOG = LoggerFactory.getLogger(MbDocenteDisponibilidad.class);

    private final HSDocenteDAO daoDocente = ServiceFinder.findBean("springHibernateDaoDocente", HSDocenteDAO.class);

    private String paramCode;
    private String paramName;
    private String oncomplete;
    private List<MbDocenteDisponibilidad> availabilities;
    private int rowTeacherId;
    private String rowTeacherCode;
    private String rowTeacherNames;
    private int rowTeacherAvailability;

    private List<MbDocenteDisponibilidad> details;
    private String mpAvailableDay;
    private Date mpAvailableHour;
    private String mpAvailableObservation;

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking by teacher code: '{}' | name: '{}'", event.getPhaseId(), paramCode, paramName);
        if (!StringUtils.hasText(paramCode) && !StringUtils.hasText(paramName)) {
            this.setOncomplete("alert('Ingrese valor de busqueda');");
            return;
        }
        this.setOncomplete("");
        List<MbDocenteDisponibilidad> teachers = daoDocente.findByCodeAndGetAvailability(paramCode, paramName);
        setAvailabilities(teachers);
        LOG.info("Total teachers with availabilities {}", availabilities.size());
    }

    public void openModalForAvailabilities(ActionEvent event) {
        var teacherId = Integer.parseInt(event.getComponent().getAttributes().get("teacher_id").toString());
        LOG.debug("[{}] Getting details of {}", event.getPhaseId(), teacherId);

        List<Object[]> objs = daoDocente.findAvailabilitiesByTeacher(teacherId);
        setDetails(objs.stream().map(obj -> {
            var dd = new MbDocenteDisponibilidad();
            dd.setMpAvailableDay(obj[2].toString());
            dd.setMpAvailableHour((Date) obj[1]);
            dd.setMpAvailableObservation(obj[0].toString());
            return dd;
        }).collect(Collectors.toList()));
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public List<MbDocenteDisponibilidad> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<MbDocenteDisponibilidad> availabilities) {
        this.availabilities = availabilities;
    }

    public int getRowTeacherId() {
        return rowTeacherId;
    }

    public void setRowTeacherId(int rowTeacherId) {
        this.rowTeacherId = rowTeacherId;
    }

    public String getRowTeacherCode() {
        return rowTeacherCode;
    }

    public void setRowTeacherCode(String rowTeacherCode) {
        this.rowTeacherCode = rowTeacherCode;
    }

    public String getRowTeacherNames() {
        return rowTeacherNames;
    }

    public void setRowTeacherNames(String rowTeacherNames) {
        this.rowTeacherNames = rowTeacherNames;
    }

    public int getRowTeacherAvailability() {
        return rowTeacherAvailability;
    }

    public void setRowTeacherAvailability(int rowTeacherAvailability) {
        this.rowTeacherAvailability = rowTeacherAvailability;
    }

    public List<MbDocenteDisponibilidad> getDetails() {
        return details;
    }

    public void setDetails(List<MbDocenteDisponibilidad> details) {
        this.details = details;
    }

    public String getMpAvailableDay() {
        return mpAvailableDay;
    }

    public void setMpAvailableDay(String mpAvailableDay) {
        this.mpAvailableDay = mpAvailableDay;
    }

    public Date getMpAvailableHour() {
        return mpAvailableHour;
    }

    public void setMpAvailableHour(Date mpAvailableHour) {
        this.mpAvailableHour = mpAvailableHour;
    }

    public String getMpAvailableObservation() {
        return mpAvailableObservation;
    }

    public void setMpAvailableObservation(String mpAvailableObservation) {
        this.mpAvailableObservation = mpAvailableObservation;
    }
}
