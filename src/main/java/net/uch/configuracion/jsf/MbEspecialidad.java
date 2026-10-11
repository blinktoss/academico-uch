package net.uch.configuracion.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.mapping.AcEspecialidad;
import net.uch.mapping.AcFacultad;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MbEspecialidad {

    private static final Logger LOG = LoggerFactory.getLogger(MbEspecialidad.class);

    private final HSEspecialidadDAO daoEspecialidad = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
    private final HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);

    private int rowId;
    private String paramNombre = "";
    private String mpNombre;
    private String auxNombre;
    private String paramCodigo = "";
    private String mpCodigo;
    private int rowNroPeriodos;
    private int auxNroPeriodos;
    private String rowTipoPeriodoId;
    private String rowTipoPeriodoDesc;
    private String auxTipoPeriodosId;
    private String auxCodigo;
    private Date rowFechacreacion;
    private Date auxFechacreacion;
    private String rowResolrectoral;
    private String auxResolrectoral;
    private int paramFacultadId;
    private int rowFacultadId;
    private String rowFacultadValue;
    private int auxFacultadId;
    private String auxFacultadValue;
    private SelectItem[] comboTipos;
    private List<SelectItem> comboFacultades;
    private List<MbEspecialidad> listaEspecialidad;
    private boolean editable = false;
    private boolean view = true;
    private boolean visible = false;
    private String oncomplete;
    private boolean editable2 = false;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSFacultadDAO daoFacultad = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        setComboFacultades(daoFacultad.seleccionarFacultad("", "").stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .collect(Collectors.toList()));

        // For modal
        setComboTipos(daoCatalogo.seleccionarCatalogo("005").stream()
                .map(tp -> new SelectItem(tp.getCatCodigoGrupo() + tp.getCatCodigoElemento(), tp.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));
    }

    public int getParamFacultadId() {
        return paramFacultadId;
    }

    public void setParamFacultadId(int paramFacultadId) {
        this.paramFacultadId = paramFacultadId;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public boolean isEditable2() {
        return editable2;
    }

    public void setEditable2(boolean editable2) {
        this.editable2 = editable2;
    }

    public List<SelectItem> getComboFacultades() {
        return comboFacultades;
    }

    public void setComboFacultades(List<SelectItem> comboFacultades) {
        this.comboFacultades = comboFacultades;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getParamNombre() {
        return paramNombre;
    }

    public void setParamNombre(String paramNombre) {
        this.paramNombre = paramNombre;
    }

    public String getParamCodigo() {
        return paramCodigo;
    }

    public void setParamCodigo(String paramCodigo) {
        this.paramCodigo = paramCodigo;
    }

    public Date getRowFechacreacion() {
        return rowFechacreacion;
    }

    public void setRowFechacreacion(Date rowFechacreacion) {
        this.rowFechacreacion = rowFechacreacion;
    }

    public String getRowResolrectoral() {
        return rowResolrectoral;
    }

    public void setRowResolrectoral(String rowResolrectoral) {
        this.rowResolrectoral = rowResolrectoral;
    }

    public int getRowFacultadId() {
        return rowFacultadId;
    }

    public void setRowFacultadId(int rowFacultadId) {
        this.rowFacultadId = rowFacultadId;
    }

    public String getRowFacultadValue() {
        return rowFacultadValue;
    }

    public void setRowFacultadValue(String rowFacultadValue) {
        this.rowFacultadValue = rowFacultadValue;
    }

    public String getMpNombre() {
        return mpNombre;
    }

    public void setMpNombre(String mpNombre) {
        this.mpNombre = mpNombre;
    }

    public String getMpCodigo() {
        return mpCodigo;
    }

    public void setMpCodigo(String mpCodigo) {
        this.mpCodigo = mpCodigo;
    }

    public String seleccionar() {
        LOG.debug("Looking for '{}' | '{}' | {}", this.paramCodigo, this.paramNombre, this.paramFacultadId);
        this.setListaEspecialidad(new ArrayList<>());
        List<AcEspecialidad> lista = daoEspecialidad.seleccionarEspecialidad(this.getParamCodigo(), this.getParamNombre(), this.getParamFacultadId());
        MbEspecialidad bE;
        for (AcEspecialidad acEspecialidad : lista) {
            bE = new MbEspecialidad();
            bE.setRowId(acEspecialidad.getId());
            bE.setParamCodigo(acEspecialidad.getEspCodigo());
            bE.setParamNombre(acEspecialidad.getEspNombre());
            bE.setRowFechacreacion(acEspecialidad.getEspFechacreacion());
            AcFacultad fac = acEspecialidad.getFac();
            bE.setRowFacultadId(fac.getId());
            bE.setRowFacultadValue(fac.getFacNombre());
            bE.setRowResolrectoral(acEspecialidad.getEspResolrectoral());
            bE.setRowNroPeriodos(Integer.parseInt(acEspecialidad.getEspNroperiodos()));
            bE.setRowTipoPeriodoId(acEspecialidad.getEspTipoperiodo());
            bE.setRowTipoPeriodoDesc(daoCatalogo.seleccionarDescripcion(acEspecialidad.getEspTipoperiodo()));
            bE.setComboFacultades(this.getComboFacultades());
            bE.setComboTipos(this.getComboTipos());
            listaEspecialidad.add(bE);
        }
        LOG.info("Total of specialities {}", listaEspecialidad.size());
        return "";
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setRowFechacreacion(this.getAuxFechacreacion());
        this.setParamCodigo(this.getAuxCodigo());
        this.setParamNombre(this.getAuxNombre());
        this.setRowResolrectoral(this.getAuxResolrectoral());
        this.setRowFacultadId(this.getAuxFacultadId());
        this.setRowFacultadValue(this.getAuxFacultadValue());
        this.setRowNroPeriodos(this.getAuxNroPeriodos());
        this.setRowTipoPeriodoId(this.getAuxTipoPeriodosId());
        this.setView(true);
        this.setEditable(false);
        this.setEditable2(false);
        this.setVisible(false);
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setAuxFechacreacion(this.getRowFechacreacion());
        this.setAuxCodigo(this.getParamCodigo());
        this.setAuxNombre(this.getParamNombre());
        this.setAuxResolrectoral(this.getRowResolrectoral());
        this.setAuxFacultadId(this.getRowFacultadId());
        this.setAuxFacultadValue(this.getRowFacultadValue());
        this.setAuxNroPeriodos(this.getRowNroPeriodos());
        this.setAuxTipoPeriodosId(this.getRowTipoPeriodoId());
        this.setView(false);
        this.setEditable(true);
        this.setEditable2(true);
        this.setVisible(true);
    }

    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("id");
        UIParameter codigo = (UIParameter) event.getComponent().findComponent("codigo");
        UIParameter nombre = (UIParameter) event.getComponent().findComponent("nombre");
        UIParameter resolucion = (UIParameter) event.getComponent().findComponent("resolucion");
        UIParameter facultad = (UIParameter) event.getComponent().findComponent("facultad");
        UIParameter numero = (UIParameter) event.getComponent().findComponent("numero_periodos");
        UIParameter tipo = (UIParameter) event.getComponent().findComponent("tipo_periodos");
        UIParameter creacion = (UIParameter) event.getComponent().findComponent("creacion");

        if (codigo.getValue().toString().trim().isEmpty() ||
                nombre.getValue().toString().trim().isEmpty() ||
                resolucion.getValue().toString().trim().isEmpty() ||
                Integer.parseInt(facultad.getValue().toString()) == 0 ||
                numero.getValue().toString().trim().isEmpty() ||
                creacion.getValue() == null) {
            LOG.warn("Complete mandatory fields");
            this.setOncomplete("alert('Ingrese Datos.')");
        } else {
            var especialidad = new AcEspecialidad();
            especialidad.setId(Integer.parseInt(id.getValue().toString()));
            especialidad.setEspCodigo(codigo.getValue().toString());
            especialidad.setEspNombre(nombre.getValue().toString());
            especialidad.setEspResolrectoral(resolucion.getValue().toString());
            especialidad.setEspFechacreacion((java.util.Date) creacion.getValue());
            var fac = new AcFacultad();
            var facultyId = facultad.getValue().toString();
            fac.setId(Integer.parseInt(facultyId));
            especialidad.setFac(fac);
            especialidad.setEspNroperiodos(numero.getValue().toString());
            var periodTypeId = tipo.getValue().toString();
            especialidad.setEspTipoperiodo(periodTypeId);
            especialidad.setEspActivo("1");
            daoEspecialidad.actualizarEspecialidad(especialidad);
            this.setView(true);
            this.setEditable(false);
            this.setEditable2(false);
            this.setVisible(false);
            SelectItem fse = comboFacultades.stream()
                    .filter(f -> facultyId.equals(f.getValue().toString()))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
            this.setRowFacultadValue(fse.getLabel());
            SelectItem ptse = Arrays.stream(comboTipos)
                    .filter(t -> periodTypeId.equals(t.getValue()))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
            this.setRowTipoPeriodoDesc(ptse.getLabel());
            this.setOncomplete("alert('Se actualizaron los datos de la Especialidad correctamente.')");
        }
    }

    public void eliminarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter delete = (UIParameter) event.getComponent().findComponent("delete");
        var id = Integer.valueOf(delete.getValue().toString());

        int r = daoEspecialidad.eliminarEspecialidad(id);
        LOG.info("Speciality {} deleted, total rows affected: {}", id, r);
    }

    public List<MbEspecialidad> getListaEspecialidad() {
        return listaEspecialidad;
    }

    public void setListaEspecialidad(List<MbEspecialidad> listaEspecialidad) {
        this.listaEspecialidad = listaEspecialidad;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getAuxNombre() {
        return auxNombre;
    }

    public void setAuxNombre(String auxNombre) {
        this.auxNombre = auxNombre;
    }

    public String getAuxCodigo() {
        return auxCodigo;
    }

    public void setAuxCodigo(String auxCodigo) {
        this.auxCodigo = auxCodigo;
    }

    public Date getAuxFechacreacion() {
        return auxFechacreacion;
    }

    public void setAuxFechacreacion(Date auxFechacreacion) {
        this.auxFechacreacion = auxFechacreacion;
    }

    public String getAuxResolrectoral() {
        return auxResolrectoral;
    }

    public void setAuxResolrectoral(String auxResolrectoral) {
        this.auxResolrectoral = auxResolrectoral;
    }

    public int getAuxFacultadId() {
        return auxFacultadId;
    }

    public void setAuxFacultadId(int auxFacultadId) {
        this.auxFacultadId = auxFacultadId;
    }

    public String getAuxFacultadValue() {
        return auxFacultadValue;
    }

    public void setAuxFacultadValue(String auxFacultadValue) {
        this.auxFacultadValue = auxFacultadValue;
    }

    public void newSpeciality(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMpCodigo("");
        this.setMpNombre("");
        this.setRowFechacreacion(null);
        this.setRowResolrectoral("");
        this.setRowTipoPeriodoId("005001");
        this.setRowNroPeriodos(0);
        this.setRowFacultadId(0);
    }

    public void grabar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getMpCodigo().trim().isEmpty() ||
                this.getMpNombre().trim().isEmpty() ||
                this.getRowFechacreacion() == null ||
                this.getRowResolrectoral().trim().isEmpty() ||
                this.getRowNroPeriodos() == 0 ||
                this.getRowFacultadId() == 0) {
            LOG.warn("Enter all required fields");
            this.setOncomplete("alert('Ingrese todos los datos Requeridos.')");
        } else {
            var especialidad = new AcEspecialidad();
            especialidad.setId(this.getRowId());
            especialidad.setEspCodigo(this.getMpCodigo());
            especialidad.setEspNombre(this.getMpNombre());
            especialidad.setEspResolrectoral(this.getRowResolrectoral());
            especialidad.setEspFechacreacion(this.getRowFechacreacion());
            var fac = new AcFacultad();
            fac.setId(this.getRowFacultadId());
            especialidad.setFac(fac);
            especialidad.setEspNroperiodos(String.valueOf(this.getRowNroPeriodos()));
            especialidad.setEspTipoperiodo(this.getRowTipoPeriodoId());
            especialidad.setEspActivo("1");
            daoEspecialidad.insertarEspecialidad(especialidad);
            rowId = especialidad.getId();
            LOG.info("Speciality saved {}", rowId);
            this.setOncomplete("alert('Se creo una Especialidad correctamente.');Richfaces.hideModalPanel('mp')");
        }
    }

    public int getRowNroPeriodos() {
        return rowNroPeriodos;
    }

    public void setRowNroPeriodos(int rowNroPeriodos) {
        this.rowNroPeriodos = rowNroPeriodos;
    }

    public int getAuxNroPeriodos() {
        return auxNroPeriodos;
    }

    public void setAuxNroPeriodos(int auxNroPeriodos) {
        this.auxNroPeriodos = auxNroPeriodos;
    }

    public String getRowTipoPeriodoId() {
        return rowTipoPeriodoId;
    }

    public void setRowTipoPeriodoId(String rowTipoPeriodoId) {
        this.rowTipoPeriodoId = rowTipoPeriodoId;
    }

    public String getRowTipoPeriodoDesc() {
        return rowTipoPeriodoDesc;
    }

    public void setRowTipoPeriodoDesc(String rowTipoPeriodoDesc) {
        this.rowTipoPeriodoDesc = rowTipoPeriodoDesc;
    }

    public String getAuxTipoPeriodosId() {
        return auxTipoPeriodosId;
    }

    public void setAuxTipoPeriodosId(String auxTipoPeriodosId) {
        this.auxTipoPeriodosId = auxTipoPeriodosId;
    }

    public SelectItem[] getComboTipos() {
        return comboTipos;
    }

    public void setComboTipos(SelectItem[] comboTipos) {
        this.comboTipos = comboTipos;
    }
}
