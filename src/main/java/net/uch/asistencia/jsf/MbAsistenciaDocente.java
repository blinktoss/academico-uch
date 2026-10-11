package net.uch.asistencia.jsf;

import net.uch.academica.dao.HSDocenteDAO;
import net.uch.academica.dao.HSSeccionDAO;
import net.uch.asistencia.dao.HSAsistenciaDocenteDAO;
import net.uch.asistencia.jsf.beans.AsistenciaDocenteBean;
import net.uch.asistencia.jsf.beans.NuevaAsistenciaBean;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.common.SystemHelper;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.uch.common.Constants.LABEL_EMPTY;

/**
 * @author cesardl
 */
public class MbAsistenciaDocente {

    private static final Logger LOG = LoggerFactory.getLogger(MbAsistenciaDocente.class);

    private final HSAsistenciaDocenteDAO daoAsistencia = ServiceFinder.findBean("springHibernateDaoAsistenciaDocente", HSAsistenciaDocenteDAO.class);
    private final HSDocenteDAO daoDocente = ServiceFinder.findBean("springHibernateDaoDocente", HSDocenteDAO.class);

    private String suggestion;
    private int sugDocId;
    private String sugDocCodigo;
    private String sugDocNombre;
    private Date paramSessionFechaIni;
    private Date paramSessionFechaFin;
    private String paramTipasisCode;
    private SelectItem[] tiposAsistencia;
    private String selectedDocNombre;
    private String selectedDocCodigo;
    private int paramDocIdBk = 0;
    private String rowDocNombre;
    private String mpEspNombre;
    private Date mpHoraIni2;
    private Date mpHoraFin2;
    private int mpDocId;
    private int rowSesId;
    private Date rowFechaRegistro = new Date();
    private String rowSecNombre;
    private String rowCurNombre;
    private List<MbAsistenciaDocente> listarSessiones = new ArrayList<>();
    private Date rowSesHoraIni = new Date();
    private Date rowSesHoraFin = new Date();
    private AsistenciaDocenteBean asisDocente = new AsistenciaDocenteBean();
    private SelectItem[] estasis;

    private NuevaAsistenciaBean nuevAsisDocente = new NuevaAsistenciaBean();
    private SelectItem[] especialidades;
    private SelectItem[] tiposSesiones;
    private List<SelectItem> secciones;
    private List<SelectItem> cursos;
    private String oncomplete;
    private SelectItem[] cboDocente;
    private SystemHelper helper;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        helper = ServiceFinder.findBean("helper", SystemHelper.class);

        HSCatalogoDAO catalogoDAO = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        setTiposAsistencia(catalogoDAO.seleccionarCatalogo("050").stream()
                .map(cat -> new SelectItem("050" + cat.getCatCodigoElemento(), cat.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        SelectItem[] tmpDocentes = daoDocente.seleccionarDocente().stream()
                .map(t -> new SelectItem(t.getId(), t.getDocNombre()))
                .toArray(SelectItem[]::new);
        setCboDocente(tmpDocentes);

        HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
        setEspecialidades(dao.seleccionarEspecialidades().stream()
                .map(esp -> new SelectItem(esp.getId(), esp.getEspNombre()))
                .toArray(SelectItem[]::new));

        setSecciones(new ArrayList<>());
        setCursos(new ArrayList<>());

        setTiposSesiones(catalogoDAO.seleccionarCatalogo("047").stream()
                .map(cat -> new SelectItem("047" + cat.getCatCodigoElemento(), cat.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        setEstasis(catalogoDAO.seleccionarCatalogo("046").stream()
                .map(cat -> new SelectItem("046" + cat.getCatCodigoElemento(), cat.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));
    }

    public SelectItem[] getTiposAsistencia() {
        return tiposAsistencia;
    }

    public void setTiposAsistencia(SelectItem[] tiposAsistencia) {
        this.tiposAsistencia = tiposAsistencia;
    }

    public String getParamTipasisCode() {
        return paramTipasisCode;
    }

    public void setParamTipasisCode(String paramTipasisCode) {
        this.paramTipasisCode = paramTipasisCode;
    }

    public String getMpEspNombre() {
        return mpEspNombre;
    }

    public void setMpEspNombre(String mpEspNombre) {
        this.mpEspNombre = mpEspNombre;
    }

    public int getMpDocId() {
        return mpDocId;
    }

    public void setMpDocId(int mpDocId) {
        this.mpDocId = mpDocId;
    }

    public Date getMpHoraFin2() {
        return mpHoraFin2;
    }

    public void setMpHoraFin2(Date mpHoraFin2) {
        this.mpHoraFin2 = mpHoraFin2;
    }

    public Date getMpHoraIni2() {
        return mpHoraIni2;
    }

    public void setMpHoraIni2(Date mpHoraIni2) {
        this.mpHoraIni2 = mpHoraIni2;
    }

    public Date getParamSessionFechaIni() {
        return paramSessionFechaIni;
    }

    public void setParamSessionFechaIni(Date paramSessionFechaIni) {
        this.paramSessionFechaIni = paramSessionFechaIni;
    }

    public Date getParamSessionFechaFin() {
        return paramSessionFechaFin;
    }

    public void setParamSessionFechaFin(Date paramSessionFechaFin) {
        this.paramSessionFechaFin = paramSessionFechaFin;
    }

    public SelectItem[] getCboDocente() {
        return cboDocente;
    }

    public void setCboDocente(SelectItem[] cboDocente) {
        this.cboDocente = cboDocente;
    }

    public String getRowDocNombre() {
        return rowDocNombre;
    }

    public void setRowDocNombre(String rowDocNombre) {
        this.rowDocNombre = rowDocNombre;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public AsistenciaDocenteBean getAsisDocente() {
        return asisDocente;
    }

    public void setAsisDocente(AsistenciaDocenteBean asisDocente) {
        this.asisDocente = asisDocente;
    }

    public SelectItem[] getEstasis() {
        return estasis;
    }

    public void setEstasis(SelectItem[] estasis) {
        this.estasis = estasis;
    }

    public NuevaAsistenciaBean getNuevAsisDocente() {
        return nuevAsisDocente;
    }

    public void setNuevAsisDocente(NuevaAsistenciaBean nuevAsisDocente) {
        this.nuevAsisDocente = nuevAsisDocente;
    }

    public SelectItem[] getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(SelectItem[] especialidades) {
        this.especialidades = especialidades;
    }

    public void onChangeEspecialidades(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        secciones.clear();

        if (this.getNuevAsisDocente().getEspecialidadCode() != 0) {
            HSSeccionDAO dao = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
            HSSemestreDAO daos = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
            AcSemestre semesterId = daos.seleccionarSemestreActual();

            List<String> lista = dao.listarSecciones(this.getNuevAsisDocente().getEspecialidadCode(), semesterId.getId());
            secciones.addAll(lista.stream()
                    .map(l -> new SelectItem(l, l))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<SelectItem> secciones) {
        this.secciones = secciones;
    }

    public void onChangeSecciones(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        cursos.clear();
        if (!"0".equals(this.getNuevAsisDocente().getSeccionCode())) {
            HSSeccionDAO dao = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
            List<AcSeccion> lista = dao.listarCursosPorSeccion(this.getNuevAsisDocente().getSeccionCode());
            cursos.addAll(lista.stream()
                    .map(l -> new SelectItem(l.getId(), l.getCurape().getPlancur().getCur().getCurNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getCursos() {
        return cursos;
    }

    public void setCursos(List<SelectItem> cursos) {
        this.cursos = cursos;
    }

    public SelectItem[] getTiposSesiones() {
        return tiposSesiones;
    }

    public void setTiposSesiones(SelectItem[] tiposSesiones) {
        this.tiposSesiones = tiposSesiones;
    }

    public Date getRowSesHoraFin() {
        return rowSesHoraFin;
    }

    public void setRowSesHoraFin(Date rowSesHoraFin) {
        this.rowSesHoraFin = rowSesHoraFin;
    }

    public Date getRowSesHoraIni() {
        return rowSesHoraIni;
    }

    public void setRowSesHoraIni(Date rowSesHoraIni) {
        this.rowSesHoraIni = rowSesHoraIni;
    }

    public List<MbAsistenciaDocente> getListarSessiones() {
        return listarSessiones;
    }

    public void setListarSessiones(List<MbAsistenciaDocente> listarSessiones) {
        this.listarSessiones = listarSessiones;
    }

    public String getRowCurNombre() {
        return rowCurNombre;
    }

    public void setRowCurNombre(String rowCurNombre) {
        this.rowCurNombre = rowCurNombre;
    }

    public Date getRowFechaRegistro() {
        return rowFechaRegistro;
    }

    public void setRowFechaRegistro(Date rowFechaRegistro) {
        this.rowFechaRegistro = rowFechaRegistro;
    }

    public String getRowSecNombre() {
        return rowSecNombre;
    }

    public void setRowSecNombre(String rowSecNombre) {
        this.rowSecNombre = rowSecNombre;
    }

    public int getRowSesId() {
        return rowSesId;
    }

    public void setRowSesId(int rowSesId) {
        this.rowSesId = rowSesId;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSugDocNombre() {
        return sugDocNombre;
    }

    public void setSugDocNombre(String sugDocNombre) {
        this.sugDocNombre = sugDocNombre;
    }

    public String getSugDocCodigo() {
        return sugDocCodigo;
    }

    public void setSugDocCodigo(String sugDocCodigo) {
        this.sugDocCodigo = sugDocCodigo;
    }

    public int getSugDocId() {
        return sugDocId;
    }

    public void setSugDocId(int sugDocId) {
        this.sugDocId = sugDocId;
    }

    public String getSelectedDocNombre() {
        return selectedDocNombre;
    }

    public void setSelectedDocNombre(String selectedDocNombre) {
        this.selectedDocNombre = selectedDocNombre;
    }

    public String getSelectedDocCodigo() {
        return selectedDocCodigo;
    }

    public void setSelectedDocCodigo(String selectedDocCodigo) {
        this.selectedDocCodigo = selectedDocCodigo;
    }

    public int getParamDocIdBk() {
        return paramDocIdBk;
    }

    public void setParamDocIdBk(int paramDocIdBk) {
        this.paramDocIdBk = paramDocIdBk;
    }

    public List<MbAsistenciaDocente> autocomplete(Object suggestion) {
        List<MbAsistenciaDocente> listaEmpleados = new ArrayList<>();
        String ref = (String) suggestion;
        List<AcDocente> empleados = daoAsistencia.buscarDocenteDato(ref);
        LOG.debug("entro al auto");
        for (AcDocente empleado : empleados) {
            var doc = new MbAsistenciaDocente();
            doc.setSugDocCodigo(empleado.getDocCodigo());
            doc.setSugDocId(empleado.getId());
            doc.setSugDocNombre(empleado.getDocNombre());
            listaEmpleados.add(doc);
        }
        return listaEmpleados;
    }

    /**
     * Muestra las sessiones de acuerdo a los parametros ingresados
     *
     * @param event the event
     */
    public void mostrarSessionAsistencia(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        LOG.debug("el doc_id - {}", getParamDocIdBk());
        LOG.debug("el fecha_ini - {}", getParamSessionFechaIni());
        LOG.debug("el fecha_fin - {}", getParamSessionFechaFin());
        LOG.debug("el tipo de asistencia - {}", getParamTipasisCode());

        List<MbAsistenciaDocente> listaEmpleados = new ArrayList<>();

        List<AcSesionAsistencia> empleados = daoAsistencia.listarSessionAsistenciaDocenteFecha(
                this.getParamDocIdBk(), this.getParamSessionFechaIni(), this.getParamSessionFechaFin(), this.getParamTipasisCode());

        for (AcSesionAsistencia sesasis : empleados) {
            var asis = new MbAsistenciaDocente();

            if (this.getParamTipasisCode().equalsIgnoreCase("050001")) {
                Object[] seccion = daoAsistencia.seleccionarSeccion(sesasis.getAcSeccion().getId());
                asis.setRowCurNombre(seccion[0].toString());
                asis.setRowSecNombre(seccion[1].toString());
            } else if (this.getParamTipasisCode().equalsIgnoreCase("050002")) {
                AcArea area = daoAsistencia.seleccionarArea(sesasis.getAcSeccion().getId());
                if (area == null) {
                    continue;
                }
                asis.setRowCurNombre(area.getAreNombre());
                asis.setRowSecNombre("AREA");
            } else {
                asis.setRowCurNombre(LABEL_EMPTY);
                asis.setRowSecNombre(LABEL_EMPTY);
            }
            asis.setRowFechaRegistro(sesasis.getSesFechaRegistro());
            asis.setRowSesHoraIni(sesasis.getSesFechaInicio());
            asis.setRowSesHoraFin(sesasis.getSesFechaFin());
            asis.setRowSesId(sesasis.getSesId());
            asis.setSugDocId(sesasis.getDocente().getId());
            asis.setParamDocIdBk(sesasis.getDocIdBk());
            asis.setRowDocNombre(sesasis.getDocente().getDocNombre());
            listaEmpleados.add(asis);
        }
        this.setListarSessiones(listaEmpleados);
        LOG.info("Total of assitances {}", listaEmpleados.size());
    }

    public void seleccionarMarcaDocente(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var pSesId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("w_ses_id")).getValue().toString());
        var pCurNombre = ((UIParameter) event.getComponent().findComponent("p_cur_nombre")).getValue().toString();
        var pSecNombre = ((UIParameter) event.getComponent().findComponent("p_sec_nombre")).getValue().toString();
        var pDocId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("w_doc_id")).getValue().toString());

        AcSesionEfectivaAsisDoc sesefeasisdoc = daoAsistencia.listaMarcadoDeAsistenciaDocente(pSesId);

        var asis = new AsistenciaDocenteBean();
        if (sesefeasisdoc == null) {
            AcDocente doc = daoDocente.seleccionarDocente(pDocId);
            AcSesionAsistencia ses = daoAsistencia.getSessionAsistencia(pSesId);
            asis.setRegistro(ses.getSesFechaInicio());
            asis.setSalida(ses.getSesFechaFin());
            asis.setSesefeasisdocFecha(ses.getSesFechaRegistro());
            asis.setDocId(doc.getId());
            asis.setDocNombre(doc.getDocNombre());
            asis.setSesId(pSesId);
            asis.setSecNombre(pSecNombre);
            asis.setCurNombre(pCurNombre);
        } else {
            asis.setDocId(sesefeasisdoc.getAcDocente().getId());
            asis.setDocNombre(sesefeasisdoc.getAcDocente().getDocNombre());
            asis.setSesId(sesefeasisdoc.getAcSesionAsistencia().getSesId());
            asis.setSecNombre(pSecNombre);
            asis.setCurNombre(pCurNombre);
            asis.setEstasisCode(sesefeasisdoc.getSesefeasisdocTipo());
            asis.setSesefeasisdocId(sesefeasisdoc.getSesefeasisdocId());

            var t1 = helper.convertToLocalDateTime(sesefeasisdoc.getSesefeasisdocRegistro());
            var t2 = helper.convertToLocalDateTime(sesefeasisdoc.getSesefeasisdocSalida());
            if (t1 != null) {
                asis.setRegHora(t1.getHour());
                asis.setRegMin(t1.getMinute());
            }
            if (t2 != null) {
                asis.setSalHora(t2.getHour());
                asis.setSalMin(t2.getMinute());
            }
            AcSesionAsistencia ses = daoAsistencia.getSessionAsistencia(pSesId);
            asis.setRegistro(ses.getSesFechaInicio());
            asis.setSalida(ses.getSesFechaFin());
            asis.setSesefeasisdocFecha(ses.getSesFechaRegistro());
            asis.setSesefeasisdocObs(sesefeasisdoc.getSesefeasisdocObs());
        }
        this.setAsisDocente(asis);
        this.setOncomplete("Richfaces.showModalPanel('mpAsisDocente')");
    }

    public void guardarMarcaAsistencia(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var pSesId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_ses_id")).getValue().toString());
        var sDocAuxId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_doc_id")).getValue().toString());
        var pSesefecId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_sesefec_id")).getValue().toString());

        if (this.getAsisDocente().getEstasisCode().equals("0")) {
            this.setOncomplete("alert('SELECCIONE UN TIPO DE ASISTENCIA');");
        } else if (this.getAsisDocente().getRegHora() == 0 && this.getAsisDocente().getRegMin() == 0) {
            this.setOncomplete("alert('INGRESE LA HORA DE INGRESO');");
        } else {
            LocalDateTime dateRegistro = helper.convertToLocalDateTime(this.getAsisDocente().getSesefeasisdocFecha())
                    .withHour(this.getAsisDocente().getRegHora())
                    .withMinute(this.getAsisDocente().getRegMin());

            LocalDateTime dateSalida = helper.convertToLocalDateTime(this.getAsisDocente().getSesefeasisdocFecha())
                    .withHour(this.getAsisDocente().getSalHora())
                    .withMinute(this.getAsisDocente().getSalMin());

            var sefec = new AcSesionEfectivaAsisDoc();

            if (pSesefecId != 0) {
                sefec.setSesefeasisdocId(pSesefecId);
            }
            var tfRegistro = helper.convertToDate(dateRegistro);
            sefec.setSesefeasisdocRegistro(tfRegistro);

            Date tfSalida = null;
            if (this.getAsisDocente().getSalHora() != 0 || this.getAsisDocente().getSalMin() != 0) {
                // A checkout earlier than the check-in means the session crossed midnight:
                // the checkout belongs to the following calendar day.
                if (dateSalida.isBefore(dateRegistro)) {
                    dateSalida = dateSalida.plusDays(1);
                }
                tfSalida = helper.convertToDate(dateSalida);
            }
            sefec.setSesefeasisdocSalida(tfSalida);
            sefec.setSesefeasisdocObs(this.getAsisDocente().getSesefeasisdocObs());
            sefec.setSesefeasisdocActivo("1");
            sefec.setSesefeasisdocTole(0);
            sefec.setSesefeasisdocTipo(this.getAsisDocente().getEstasisCode());
            sefec.setAcSesionAsistencia(daoAsistencia.getSessionAsistencia(pSesId));
            sefec.setAcDocente(new AcDocente(sDocAuxId));

            daoAsistencia.ingresarAsisteanDocente(sefec);
            LOG.info("Effective teacher assistance saved/updated {}", sefec.getSesefeasisdocId());
            this.mostrarSessionAsistencia(event);
            this.setOncomplete("alert('REGISTRO COMPLETO');"
                    + "Richfaces.hideModalPanel('mpAsisDocente')");
        }
    }

    public void seleccionarSesion(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");

        var pSessionId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("w_ses_id")).getValue().toString());

        AcSesionAsistencia sesasis = daoAsistencia.getSessionAsistencia(pSessionId);
        AcDocente doc = daoDocente.seleccionarDocente(sesasis.getDocente().getId());

        this.setRowSesId(pSessionId);
        this.setSugDocId(doc.getId());
        this.setMpDocId(doc.getId());
        this.setSugDocNombre(doc.getDocNombre());
        this.setParamDocIdBk(sesasis.getDocIdBk());
        if ("050001".equalsIgnoreCase(sesasis.getSesTipoAsistencia())) {
            Object[] seccion = daoAsistencia.seleccionarSeccion(sesasis.getAcSeccion().getId());
            this.setRowCurNombre(seccion[0].toString());
            this.setRowSecNombre(seccion[1].toString());
            this.setMpEspNombre(seccion[2].toString());
        } else if ("050002".equalsIgnoreCase(sesasis.getSesTipoAsistencia())) {
            String area = daoAsistencia.seleccionarArea(sesasis.getAcSeccion().getId()).getAreNombre();
            this.setRowCurNombre(area);
            this.setRowSecNombre("AREA");
            this.setMpEspNombre(area);
        } else {
            this.setRowCurNombre(LABEL_EMPTY);
            this.setRowSecNombre(LABEL_EMPTY);
            this.setMpEspNombre(LABEL_EMPTY);
        }

        this.setMpHoraIni2(sesasis.getSesFechaInicio());
        this.setMpHoraFin2(sesasis.getSesFechaFin());

        this.setOncomplete("Richfaces.showModalPanel('mpSessionAsis')");
    }

    public void actualizarSesion(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var sSesId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_ses_id")).getValue().toString());
        var sCambio = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_cambio")).getValue().toString());
        var sDocAnt = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_doc_ant")).getValue().toString());
        Object objInicio = ((UIParameter) event.getComponent().findComponent("p_inicio")).getValue();
        Object objFin = ((UIParameter) event.getComponent().findComponent("p_fin")).getValue();

        Date sInicio = (Date) objInicio;
        Date sFin = (Date) objFin;
        String tipo;
        if (sCambio != sDocAnt) {
            tipo = "047002";
        } else {
            tipo = "047001";
        }
        daoAsistencia.actualizarSesionDocente(sSesId, sCambio, sInicio, sFin, tipo);

        this.mostrarSessionAsistencia(event);

        this.setOncomplete("alert('SE ACTUALIZO CORRECTAMENTE LA SESSION');"
                + "Richfaces.hideModalPanel('mpSessionAsis')");
    }

    public String limpiar() {
        LOG.debug("Opening modal for new assistance");
        nuevAsisDocente = new NuevaAsistenciaBean();
        return "";
    }

    public String validarNuevaSesion() {
        LOG.debug("Saving new teacher assistance");
        this.setOncomplete("");
        Date d1 = this.getNuevAsisDocente().getFecSesion();
        if (d1 == null) {
            this.setOncomplete("alert('INGRESE FECHA DE LA SESION');");
            return "";
        }

        LocalDateTime checkIn = helper.convertToLocalDateTime(d1)
                .withHour(this.getNuevAsisDocente().getRegHora())
                .withMinute(this.getNuevAsisDocente().getRegMin());
        this.getNuevAsisDocente().setFecSesionIni(helper.convertToDate(checkIn));

        LocalDateTime checkOut = helper.convertToLocalDateTime(d1)
                .withHour(this.getNuevAsisDocente().getSalHora())
                .withMinute(this.getNuevAsisDocente().getSalMin());
        // A checkout earlier than the check-in means the session crossed midnight:
        // the checkout belongs to the following calendar day.
        if (checkOut.isBefore(checkIn)) {
            checkOut = checkOut.plusDays(1);
        }
        this.getNuevAsisDocente().setFecSesionFin(helper.convertToDate(checkOut));

        if (this.getNuevAsisDocente().getDocId() == 0) {
            this.setOncomplete("alert('SELECCIONE UN DOCENTE');");
        } else if (this.getNuevAsisDocente().getEspecialidadCode() == 0) {
            this.setOncomplete("alert('SELECCIONE UNA ESPECIALIDAD');");
        } else if (this.getNuevAsisDocente().getSeccionCode().equalsIgnoreCase("0")) {
            this.setOncomplete("alert('SELECCIONE UNA SECCION');");
        } else if (this.getNuevAsisDocente().getCursoSeccion() == 0) {
            this.setOncomplete("alert('SELECCIONE UN CURSO');");
        } else if (checkIn.isEqual(checkOut)) {
            this.setOncomplete("alert('LA HORA DE INICIO NO PUEDE SER IGUAL A LA DE FIN');");
        } else if (this.getNuevAsisDocente().getTipasisCode().equalsIgnoreCase("0")) {
            this.setOncomplete("alert('INGRESE UN TIPO DE ASISTENCIA');");
        } else if (this.getNuevAsisDocente().getTipsesCode().equalsIgnoreCase("0")) {
            this.setOncomplete("alert('INGRESE UN TIPO DE SESION');");
        } else {
            nuevaSesion();
            this.setOncomplete("alert('SE INGRESO LA NUEVA SESSION');"
                    + "Richfaces.hideModalPanel('mpNuevaSesion')");
        }
        return "";
    }

    private void nuevaSesion() {
        var sesasis = new AcSesionAsistencia();

        sesasis.setAcSeccion(new AcSeccion(this.getNuevAsisDocente().getCursoSeccion()));
        sesasis.setSesFechaSesion(this.getNuevAsisDocente().getFecSesion());
        sesasis.setSesFechaRegistro(this.getNuevAsisDocente().getFecSesion());
        sesasis.setSesFechaInicio(this.getNuevAsisDocente().getFecSesionIni());
        sesasis.setSesFechaFin(this.getNuevAsisDocente().getFecSesionFin());
        sesasis.setSesActivo("1");
        sesasis.setSesTimeTolerancia(20);
        sesasis.setSesTimeAntes(20);
        sesasis.setSesTimeTope(30);
        sesasis.setAulId(0);
        sesasis.setSesEstado("044001");
        sesasis.setSesFechaSalida(null);
        sesasis.setDocente(new AcDocente(this.getNuevAsisDocente().getDocId()));
        sesasis.setDocIdBk(this.getNuevAsisDocente().getDocId());
        sesasis.setSesTipo(this.getNuevAsisDocente().getTipsesCode());
        sesasis.setSesTipoAsistencia(this.getNuevAsisDocente().getTipasisCode());

        daoAsistencia.ingresarSesionAsistencia(sesasis);
        int sessionId = sesasis.getSesId();
        LOG.info("Teacher assistance saved {}", sessionId);
        this.setRowSesId(sessionId);
    }
}
