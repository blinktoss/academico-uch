package net.uch.academica.jsf;

import net.uch.academica.dao.HSPlanCurricularDAO;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.mapping.AcEspecialidad;
import net.uch.mapping.AcFacultad;
import net.uch.mapping.AcPlancurricular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MbPlanCurricular {

    private static final Logger LOG = LoggerFactory.getLogger(MbPlanCurricular.class);

    private final HSPlanCurricularDAO daoPlanCurricular = ServiceFinder.findBean("springHibernateDaoPlanCurricular", HSPlanCurricularDAO.class);

    private int paramFacultadId;
    private SelectItem[] paramComboFacultades;
    private int paramEspecialidadId;
    private List<SelectItem> paramComboEspecialidades;
    private String paramCodigo;
    private String paramDescripcion;

    private List<MbPlanCurricular> listaPlanCurricular;
    private int pcId;
    private String pcCodigo;
    private String pcDescripcion;
    private String pcResolucion;
    private Date pcCreacion;
    private int pcFacultadId;
    private String pcFacultadDescripcion;
    private int pcEspecialidadId;
    private String pcEspecialidadDescripcion;

    private String oncomplete;

    private List<SelectItem> comboEspecialidades;

    private int auxModificacion = 0;

    @PostConstruct
    public void init() {
        LOG.debug("Loading Main Faculties Combobox");
        HSFacultadDAO dao = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        List<AcFacultad> lista = dao.seleccionarFacultad("", "");
        SelectItem[] facultades = lista.stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .toArray(SelectItem[]::new);
        setParamComboFacultades(facultades);
        setParamComboEspecialidades(new ArrayList<>());
        setComboEspecialidades(new ArrayList<>());
    }

    public int getParamFacultadId() {
        return paramFacultadId;
    }

    public void setParamFacultadId(int paramFacultadId) {
        this.paramFacultadId = paramFacultadId;
    }

    public SelectItem[] getParamComboFacultades() {
        return paramComboFacultades;
    }

    public void setParamComboFacultades(SelectItem[] paramComboFacultades) {
        this.paramComboFacultades = paramComboFacultades;
    }

    public void onChangeParamFacultad(ActionEvent event) {
        LOG.debug("[{}] On change {}", event.getPhaseId(), paramFacultadId);
        paramComboEspecialidades.clear();
        if (paramFacultadId != 0) {
            HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            paramComboEspecialidades.addAll(dao.seleccionarEspecialidad(paramFacultadId).stream()
                    .map(e -> new SelectItem(e.getId(), e.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public int getParamEspecialidadId() {
        return paramEspecialidadId;
    }

    public void setParamEspecialidadId(int paramEspecialidadId) {
        this.paramEspecialidadId = paramEspecialidadId;
    }

    public List<SelectItem> getParamComboEspecialidades() {
        return paramComboEspecialidades;
    }

    public void setParamComboEspecialidades(List<SelectItem> paramComboEspecialidades) {
        this.paramComboEspecialidades = paramComboEspecialidades;
    }

    public String getParamCodigo() {
        return paramCodigo;
    }

    public void setParamCodigo(String paramCodigo) {
        this.paramCodigo = paramCodigo;
    }

    public String getParamDescripcion() {
        return paramDescripcion;
    }

    public void setParamDescripcion(String paramDescripcion) {
        this.paramDescripcion = paramDescripcion;
    }

    public List<MbPlanCurricular> getListaPlanCurricular() {
        return listaPlanCurricular;
    }

    public void setListaPlanCurricular(List<MbPlanCurricular> listaPlanCurricular) {
        this.listaPlanCurricular = listaPlanCurricular;
    }

    public int getPcId() {
        return pcId;
    }

    public void setPcId(int pcId) {
        this.pcId = pcId;
    }

    public String getPcCodigo() {
        return pcCodigo;
    }

    public void setPcCodigo(String pcCodigo) {
        this.pcCodigo = pcCodigo;
    }

    public String getPcDescripcion() {
        return pcDescripcion;
    }

    public void setPcDescripcion(String pcDescripcion) {
        this.pcDescripcion = pcDescripcion;
    }

    public String getPcResolucion() {
        return pcResolucion;
    }

    public void setPcResolucion(String pcResolucion) {
        this.pcResolucion = pcResolucion;
    }

    public Date getPcCreacion() {
        return pcCreacion;
    }

    public void setPcCreacion(Date pcCreacion) {
        this.pcCreacion = pcCreacion;
    }

    public int getPcFacultadId() {
        return pcFacultadId;
    }

    public void setPcFacultadId(int pcFacultadId) {
        this.pcFacultadId = pcFacultadId;
    }

    public String getPcFacultadDescripcion() {
        return pcFacultadDescripcion;
    }

    public void setPcFacultadDescripcion(String pcFacultadDescripcion) {
        this.pcFacultadDescripcion = pcFacultadDescripcion;
    }

    public void onChangeMpFacultad(ActionEvent event) {
        LOG.debug("[{}] On change {}", event.getPhaseId(), pcFacultadId);
        comboEspecialidades.clear();
        if (pcFacultadId != 0) {
            HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            comboEspecialidades.addAll(dao.seleccionarEspecialidad(pcFacultadId).stream()
                    .map(e -> new SelectItem(e.getId(), e.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public int getPcEspecialidadId() {
        return pcEspecialidadId;
    }

    public void setPcEspecialidadId(int pcEspecialidadId) {
        this.pcEspecialidadId = pcEspecialidadId;
    }

    public String getPcEspecialidadDescripcion() {
        return pcEspecialidadDescripcion;
    }

    public void setPcEspecialidadDescripcion(String pcEspecialidadDescripcion) {
        this.pcEspecialidadDescripcion = pcEspecialidadDescripcion;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public List<SelectItem> getComboEspecialidades() {
        return comboEspecialidades;
    }

    public void setComboEspecialidades(List<SelectItem> comboEspecialidades) {
        this.comboEspecialidades = comboEspecialidades;
    }

    public void seleccionar() {
        LOG.debug("Looking for '{}' | '{}' | '{}' | '{}'", this.getParamCodigo(), this.getParamDescripcion(), this.getParamFacultadId(), this.getParamEspecialidadId());
        listaPlanCurricular = new ArrayList<>();
        List<Object[]> lista = daoPlanCurricular.seleccionarPlanCurricular(this.getParamCodigo(), this.getParamDescripcion(), this.getParamFacultadId(), this.getParamEspecialidadId());
        MbPlanCurricular bPC;
        for (Object[] bC : lista) {
            bPC = new MbPlanCurricular();
            bPC.setPcId(Integer.parseInt(bC[0].toString()));
            bPC.setPcCodigo(bC[1].toString());
            bPC.setPcDescripcion(bC[2].toString());
            bPC.setPcCreacion((Date) bC[3]);
            bPC.setPcFacultadId(Integer.parseInt(bC[4].toString()));
            bPC.setPcEspecialidadId(Integer.parseInt(bC[5].toString()));
            bPC.setPcResolucion(bC[6].toString());
            bPC.setPcFacultadDescripcion(bC[8].toString());
            bPC.setPcEspecialidadDescripcion(bC[9].toString());
            listaPlanCurricular.add(bPC);
        }
        this.setListaPlanCurricular(listaPlanCurricular);
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setPcId(0);
        this.setPcCodigo("");
        this.setPcCreacion(null);
        this.setPcDescripcion("");
        this.setPcResolucion("");
        this.setPcFacultadId(0);
        this.setPcEspecialidadId(0);
        auxModificacion = 0;
        comboEspecialidades.clear();
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("p_id");
        UIParameter codigo = (UIParameter) event.getComponent().findComponent("p_codigo");
        UIParameter descripcion = (UIParameter) event.getComponent().findComponent("p_descripcion");
        UIParameter facultad = (UIParameter) event.getComponent().findComponent("p_facultad");
        UIParameter especialidad = (UIParameter) event.getComponent().findComponent("p_especialidad");
        UIParameter resolucion = (UIParameter) event.getComponent().findComponent("p_resolucion");
        UIParameter creacion = (UIParameter) event.getComponent().findComponent("p_creacion");

        this.setPcId(Integer.parseInt(id.getValue().toString()));
        this.setPcFacultadId(Integer.parseInt(facultad.getValue().toString()));
        this.setPcEspecialidadId(Integer.parseInt(especialidad.getValue().toString()));
        onChangeMpFacultad(event);
        this.setPcCodigo(codigo.getValue().toString());
        this.setPcDescripcion(descripcion.getValue().toString());

        this.setPcCreacion((Date) creacion.getValue());

        this.setPcResolucion(resolucion.getValue().toString());
        auxModificacion = 1;
    }

    public void eliminarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter delete = (UIParameter) event.getComponent().findComponent("delete");
        var id = delete.getValue().toString();

        daoPlanCurricular.eliminarPlanCurricular(id);
    }

    public void saveOrUpdate(ActionEvent event) {
        LOG.debug("[{}] Register or Uptade? {}", event.getPhaseId(), auxModificacion);
        if (auxModificacion == 1) {
            editar();
        } else {
            grabar();
        }
    }

    public void grabar() {
        if (this.getPcCodigo().length() == 0 || this.getPcDescripcion().length() == 0 || this.getPcFacultadId() == 0 || this.getPcEspecialidadId() == 0) {
            this.setOncomplete("alert('Ingrese los datos necesarios.')");
        } else {
            var plancurricular = new AcPlancurricular();
            plancurricular.setId(this.getPcId());
            plancurricular.setPlanCodigo(this.getPcCodigo());
            plancurricular.setPlanDescripcion(this.getPcDescripcion());
            plancurricular.setPlanCreacion(this.getPcCreacion());
            plancurricular.setPlanResolucion(this.getPcResolucion());
            plancurricular.setEsp(new AcEspecialidad(this.getPcEspecialidadId()));
            plancurricular.setPlanActivo("1");
            setPcId(daoPlanCurricular.insertarPlanCurricular(plancurricular));
            LOG.info("Curricular plan saved {}", pcId);
            this.setOncomplete("alert('Se creo un Plan correctamente.');Richfaces.hideModalPanel('mp')");
        }
    }

    public void editar() {
        auxModificacion = 1;
        if (this.getPcCodigo().length() == 0 || this.getPcDescripcion().length() == 0 || this.getPcFacultadId() == 0 || this.getPcEspecialidadId() == 0) {
            this.setOncomplete("alert('Ingrese los datos necesarios.')");
        } else {
            var plan = new AcPlancurricular();
            plan.setId(this.getPcId());
            plan.setPlanCodigo(this.getPcCodigo());
            plan.setPlanDescripcion(this.getPcDescripcion());
            var esp = new AcEspecialidad();
            esp.setId(this.getPcEspecialidadId());
            plan.setEsp(esp);
            plan.setPlanResolucion(this.getPcResolucion());
            plan.setPlanCreacion(this.getPcCreacion());
            plan.setPlanActivo("1");
            daoPlanCurricular.actualizarPlanCurricular(plan);
            LOG.info("Curricular plan updated {}", pcId);
            this.setOncomplete("alert('Se Actualizaron los datos del Plan correctamente.');Richfaces.hideModalPanel('mp')");
        }
    }
}
