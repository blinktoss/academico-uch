package net.uch.academica.jsf;

import net.uch.academica.dao.HSTurnoDAO;
import net.uch.academica.dao.HSTurnoDetalleDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.common.SystemHelper;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.AcSemestre;
import net.uch.mapping.AcTurno;
import net.uch.mapping.AcTurnoDetalle;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.sql.Time;
import java.util.*;

import static net.uch.common.Constants.ACTION_IMAGES_ACTIVAR;
import static net.uch.common.Constants.ACTION_IMAGES_DESACTIVAR;

public class MbTurno {

    private static final Logger LOG = LoggerFactory.getLogger(MbTurno.class);

    private final HSTurnoDAO daoTurno = ServiceFinder.findBean("springHibernateDaoTurno", HSTurnoDAO.class);

    private SystemHelper helper;
    private int rowId;
    private int mpId;
    private String rowCodigo;
    private String mpCodigo;
    private String rowNombre;
    private String mpNombre;
    private int rowPeriodo;
    private int mpPeriodo = 6;
    private String rowInicio;
    private String mpInicioHor = "00";
    private String mpInicioMin = "00";
    private String mpInicioMeridiano;
    private String rowVigenteValue;
    private String rowVigenteImg;
    private boolean mpVigente;
    private int rowSemestreValue;
    private String rowSemestreDes;
    private int mpSemestre;
    private int rowTiempo;
    private int mpTiempo;
    private List<MbTurno> tablaTurno;
    private List<MbTurno> tablaTurnoDetalle;
    private String paramCodigo;
    private String paramNombre;
    private int paramSemestre;
    private SelectItem[] mpComboSemestres;
    private SelectItem[] paramComboSemestres;
    private SelectItem[] mpComboMeridianos;
    private int mpDetId;
    private Date mpDetInicio;
    private Date mpDetFin;

    @PostConstruct
    public void init() {
        helper = ServiceFinder.findBean("helper", SystemHelper.class);

        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        HSSemestreDAO dao = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        List<AcSemestre> lista = dao.seleccionarSemestre("", "");
        SelectItem[] semesters = lista.stream()
                .map(s -> new SelectItem(s.getId(), s.getSemNombre()))
                .toArray(SelectItem[]::new);

        setParamComboSemestres(semesters);
        setMpComboSemestres(semesters);

        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        setMpComboMeridianos(daoCatalogo.seleccionarCatalogo("008").stream()
                .map(m -> new SelectItem(m.getCatValor(), m.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getMpId() {
        return mpId;
    }

    public void setMpId(int mpId) {
        this.mpId = mpId;
    }

    public String getRowCodigo() {
        return rowCodigo;
    }

    public void setRowCodigo(String rowCodigo) {
        this.rowCodigo = rowCodigo;
    }

    public String getMpCodigo() {
        return mpCodigo;
    }

    public void setMpCodigo(String mpCodigo) {
        this.mpCodigo = mpCodigo;
    }

    public String getRowNombre() {
        return rowNombre;
    }

    public void setRowNombre(String rowNombre) {
        this.rowNombre = rowNombre;
    }

    public String getMpNombre() {
        return mpNombre;
    }

    public void setMpNombre(String mpNombre) {
        this.mpNombre = mpNombre;
    }

    public int getRowPeriodo() {
        return rowPeriodo;
    }

    public void setRowPeriodo(int rowPeriodo) {
        this.rowPeriodo = rowPeriodo;
    }

    public int getMpPeriodo() {
        return mpPeriodo;
    }

    public void setMpPeriodo(int mpPeriodo) {
        this.mpPeriodo = mpPeriodo;
    }

    public String getRowInicio() {
        return rowInicio;
    }

    public void setRowInicio(String rowInicio) {
        this.rowInicio = rowInicio;
    }

    public String getMpInicioHor() {
        return mpInicioHor;
    }

    public void setMpInicioHor(String mpInicioHor) {
        this.mpInicioHor = mpInicioHor;
    }

    public String getMpInicioMin() {
        return mpInicioMin;
    }

    public void setMpInicioMin(String mpInicioMin) {
        this.mpInicioMin = mpInicioMin;
    }

    public String getMpInicioMeridiano() {
        return mpInicioMeridiano;
    }

    public void setMpInicioMeridiano(String mpInicioMeridiano) {
        this.mpInicioMeridiano = mpInicioMeridiano;
    }

    public String getRowVigenteValue() {
        return rowVigenteValue;
    }

    public void setRowVigenteValue(String rowVigenteValue) {
        this.rowVigenteValue = rowVigenteValue;
    }

    public String getRowVigenteImg() {
        return rowVigenteImg;
    }

    public void setRowVigenteImg(String rowVigenteImg) {
        this.rowVigenteImg = rowVigenteImg;
    }

    public boolean isMpVigente() {
        return mpVigente;
    }

    public void setMpVigente(boolean mpVigente) {
        this.mpVigente = mpVigente;
    }

    public int getRowSemestreValue() {
        return rowSemestreValue;
    }

    public void setRowSemestreValue(int rowSemestreValue) {
        this.rowSemestreValue = rowSemestreValue;
    }

    public String getRowSemestreDes() {
        return rowSemestreDes;
    }

    public void setRowSemestreDes(String rowSemestreDes) {
        this.rowSemestreDes = rowSemestreDes;
    }

    public int getMpSemestre() {
        return mpSemestre;
    }

    public void setMpSemestre(int mpSemestre) {
        this.mpSemestre = mpSemestre;
    }

    public int getRowTiempo() {
        return rowTiempo;
    }

    public void setRowTiempo(int rowTiempo) {
        this.rowTiempo = rowTiempo;
    }

    public int getMpTiempo() {
        return mpTiempo;
    }

    public void setMpTiempo(int mpTiempo) {
        this.mpTiempo = mpTiempo;
    }

    public List<MbTurno> getTablaTurno() {
        return tablaTurno;
    }

    public void setTablaTurno(List<MbTurno> tablaTurno) {
        this.tablaTurno = tablaTurno;
    }

    public String getParamCodigo() {
        return paramCodigo;
    }

    public void setParamCodigo(String paramCodigo) {
        this.paramCodigo = paramCodigo;
    }

    public String getParamNombre() {
        return paramNombre;
    }

    public void setParamNombre(String paramNombre) {
        this.paramNombre = paramNombre;
    }

    public int getParamSemestre() {
        return paramSemestre;
    }

    public void setParamSemestre(int paramSemestre) {
        this.paramSemestre = paramSemestre;
    }

    public int getMpDetId() {
        return mpDetId;
    }

    public void setMpDetId(int mpDetId) {
        this.mpDetId = mpDetId;
    }

    public Date getMpDetInicio() {
        return mpDetInicio;
    }

    public void setMpDetInicio(Date mpDetInicio) {
        this.mpDetInicio = mpDetInicio;
    }

    public Date getMpDetFin() {
        return mpDetFin;
    }

    public void setMpDetFin(Date mpDetFin) {
        this.mpDetFin = mpDetFin;
    }

    public List<MbTurno> getTablaTurnoDetalle() {
        HSTurnoDetalleDAO dao = ServiceFinder.findBean("springHibernateDaoTurnoDetalle", HSTurnoDetalleDAO.class);
        List<AcTurnoDetalle> lista = dao.seleccionarTurnoDetalle(mpId);
        List<MbTurno> detalleTurnos = new ArrayList<>();
        MbTurno turno;

        for (AcTurnoDetalle o : lista) {
            turno = new MbTurno();
            turno.setMpDetId(o.getId());
            turno.setMpDetInicio(o.getTurdetHora());

            var calendar = Calendar.getInstance();
            calendar.setTime(o.getTurdetHora());
            calendar.add(Calendar.MINUTE, mpTiempo);

            turno.setMpDetFin(calendar.getTime());
            detalleTurnos.add(turno);
        }
        this.setTablaTurnoDetalle(detalleTurnos);
        return tablaTurnoDetalle;
    }

    public void setTablaTurnoDetalle(List<MbTurno> tablaTurnoDetalle) {
        this.tablaTurnoDetalle = tablaTurnoDetalle;
    }

    public SelectItem[] getMpComboSemestres() {
        return mpComboSemestres;
    }

    public void setMpComboSemestres(SelectItem[] mpComboSemestres) {
        this.mpComboSemestres = mpComboSemestres;
    }

    public SelectItem[] getParamComboSemestres() {
        return paramComboSemestres;
    }

    public void setParamComboSemestres(SelectItem[] paramComboSemestres) {
        this.paramComboSemestres = paramComboSemestres;
    }

    public SelectItem[] getMpComboMeridianos() {
        return mpComboMeridianos;
    }

    public void setMpComboMeridianos(SelectItem[] mpComboMeridianos) {
        this.mpComboMeridianos = mpComboMeridianos;
    }

    public void seleccionar() {
        LOG.debug("Looking for turns '{}' | '{}' | '{}'", this.getParamCodigo(), this.getParamNombre(), this.getParamSemestre());
        List<Object[]> lista = daoTurno.seleccionarTurno(this.getParamCodigo(), this.getParamNombre(), this.getParamSemestre());
        List<MbTurno> listaTurno = new ArrayList<>();
        MbTurno turno;
        for (Object[] o : lista) {
            turno = new MbTurno();
            turno.setRowId(Integer.parseInt(o[0].toString()));
            turno.setRowCodigo(o[1].toString());
            turno.setRowNombre(o[2].toString());
            turno.setRowPeriodo(Integer.parseInt(o[3].toString()));
            turno.setRowInicio(o[4].toString());
            var v = o[5].toString();
            if ("1".equals(v)) {
                turno.setRowVigenteImg(ACTION_IMAGES_ACTIVAR);
            } else {
                turno.setRowVigenteImg(ACTION_IMAGES_DESACTIVAR);
            }
            turno.setRowVigenteValue(v);
            turno.setRowSemestreValue(Integer.parseInt(o[6].toString()));
            turno.setRowSemestreDes(o[7].toString());
            turno.setRowTiempo(Integer.parseInt(o[8].toString()));
            listaTurno.add(turno);
        }
        this.setTablaTurno(listaTurno);
        LOG.info("Total of turnos: {}", listaTurno.size());
    }

    public String nuevo() {
        LOG.debug("Opening modal for new Turno");
        this.setMpId(0);
        this.setMpNombre("");
        this.setMpCodigo("");
        this.setMpPeriodo(6);
        this.setMpSemestre(0);
        this.setMpVigente(false);
        this.setMpTiempo(0);
        return "";
    }

    public void insertar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var minutosTotales = 0;
        AcTurno turno;
        if (this.getMpId() == 0) {
            turno = new AcTurno();
            turno.setAcTurnoDetalles(new HashSet<>());
        } else {
            turno = daoTurno.obtenerTurno(this.getMpId());
        }
        turno.setTurCodigo(this.getMpCodigo());
        turno.setTurNombre(this.getMpNombre());
        turno.setTurPeriodo(this.getMpPeriodo());
        String h = this.getMpInicioHor() + ":" + this.getMpInicioMin() + " " + this.getMpInicioMeridiano();
        if (this.getMpInicioMeridiano().equals("AM")) {
            minutosTotales = 60 * Integer.parseInt(this.getMpInicioHor()) + Integer.parseInt(this.getMpInicioMin());
        } else if (this.getMpInicioMeridiano().equals("PM")) {
            minutosTotales = 60 * Integer.parseInt(this.getMpInicioHor()) + Integer.parseInt(this.getMpInicioMin()) + 720;
        }
        turno.setTurHoraInicio(new Time(helper.parseHourMinutes(h)));
        turno.setSemId(this.getMpSemestre());
        turno.setTurVigente(this.isMpVigente() ? "1" : "0");
        turno.setTurTiempoPeriodo(this.getMpTiempo());
        List<AcTurnoDetalle> lista = new ArrayList<>(turno.getAcTurnoDetalles());
        int limit = this.getMpPeriodo();
        int orig = lista.size();
        for (var i = 0; i < limit; i++) {
            AcTurnoDetalle turnoDetalle;
            if (i < orig) {
                turnoDetalle = lista.get(i);
            } else {
                turnoDetalle = new AcTurnoDetalle();
                lista.add(turnoDetalle);
            }
            turnoDetalle.setTur(turno);
            turnoDetalle.setTurdetHora(new Time(helper.parseHourMinutes(h)));
            turnoDetalle.setTurdetActivo("1");

            minutosTotales += this.getMpTiempo();
            h = calculate(minutosTotales);
        }
        List<AcTurnoDetalle> toDelete = new ArrayList<>();
        if (orig > limit) {
            for (int index = limit; index < orig; index++) {
                toDelete.add(lista.get(index));
            }
            lista.removeAll(toDelete);
            HSTurnoDetalleDAO dao = ServiceFinder.findBean("springHibernateDaoTurnoDetalle", HSTurnoDetalleDAO.class);
            dao.delete(toDelete);
        }

        turno.setAcTurnoDetalles(new HashSet<>(lista));
        turno.setTurActivo("1");
        if (this.getMpId() == 0) {
            daoTurno.insertarTurno(turno);
            this.setMpId(turno.getId());
            LOG.info("Turn saved {}", mpId);
        } else {
            daoTurno.actualizarTurno(turno);
            LOG.info("Turn updated {}", turno.getId());
        }
    }

    private String calculate(int minutosTotales) {
        int hora = minutosTotales / 60;
        if (minutosTotales > 720) {
            return hora - 12 + ":" + (minutosTotales % 60) + " " + "PM";
        } else {
            return hora + ":" + (minutosTotales % 60) + " " + "AM";
        }
    }


    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_s");
        UIParameter codigo = (UIParameter) event.getComponent().findComponent("codigo_s");
        UIParameter nombre = (UIParameter) event.getComponent().findComponent("nombre_s");
        UIParameter periodos = (UIParameter) event.getComponent().findComponent("periodos_s");
        UIParameter inicio = (UIParameter) event.getComponent().findComponent("inicio_s");
        UIParameter vigente = (UIParameter) event.getComponent().findComponent("vigente_s");
        UIParameter semestre = (UIParameter) event.getComponent().findComponent("semestre_s");
        UIParameter tiempo = (UIParameter) event.getComponent().findComponent("tiempo_s");
        mpId = Integer.parseInt(id.getValue().toString());
        mpCodigo = codigo.getValue().toString();
        mpNombre = nombre.getValue().toString();
        mpPeriodo = Integer.parseInt(periodos.getValue().toString());
        mpSemestre = Integer.parseInt(semestre.getValue().toString());
        rowSemestreDes = ServiceFinder.getLabel(Arrays.asList(paramComboSemestres), mpSemestre);
        var inicioAux = inicio.getValue().toString();
        mpInicioHor = inicioAux.substring(0, 2);
        mpInicioMin = inicioAux.substring(3, 5);
        mpVigente = vigente.getValue().toString().equals("1");
        mpTiempo = Integer.parseInt(tiempo.getValue().toString());
    }

    public void eliminar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_s");
        UIParameter semestre = (UIParameter) event.getComponent().findComponent("semestre_s");
        daoTurno.eliminarTurno(Integer.parseInt(id.getValue().toString()),
                Integer.parseInt(semestre.getValue().toString()));
    }
}
