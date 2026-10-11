package net.uch.academica.jsf;

import net.uch.academica.dao.HSCursoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.mapping.AcCurso;
import net.uch.mapping.AcEspecialidad;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MbCurso {

    private static final Logger LOG = LoggerFactory.getLogger(MbCurso.class);

    private final HSCursoDAO daoCurso = ServiceFinder.findBean("springHibernateDaoCurso", HSCursoDAO.class);

    private int rowId;
    private int mpId;
    private String paramCodigo;
    private String mpCodigo;
    private String paramNombre;
    private String mpNombre;
    private List<MbCurso> listaCursos;
    private SelectItem[] comboFacultades;
    private List<SelectItem> mpEspecialidades;
    private int paramFacultadId;
    private int paramEspecialidadId;
    private String rowEspecialidadDescripcion;
    private String rowFacultadDescripcion;
    private int mpFacultadId;
    private int mpEspecialidadId;
    private List<SelectItem> paramEspecialidades;
    private String oncomplete;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        HSFacultadDAO dao = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        setComboFacultades(dao.seleccionarFacultad("", "").stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .toArray(SelectItem[]::new));
        setParamEspecialidades(new ArrayList<>());
        setMpEspecialidades(new ArrayList<>());
    }

    public int getMpEspecialidadId() {
        return mpEspecialidadId;
    }

    public void setMpEspecialidadId(int mpEspecialidadId) {
        this.mpEspecialidadId = mpEspecialidadId;
    }

    public int getMpFacultadId() {
        return mpFacultadId;
    }

    public void setMpFacultadId(int mpFacultadId) {
        this.mpFacultadId = mpFacultadId;
    }

    public SelectItem[] getComboFacultades() {
        return comboFacultades;
    }

    public void setComboFacultades(SelectItem[] comboFacultades) {
        this.comboFacultades = comboFacultades;
    }

    public void onChangeParamFacultades(ActionEvent event) {
        LOG.info("[{}]", event.getPhaseId());
        paramEspecialidades.clear();
        if (this.getParamFacultadId() != 0) {
            HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            LOG.trace("facu:::: {}", this.getParamFacultadId());
            paramEspecialidades.addAll(dao.seleccionarEspecialidad(this.getParamFacultadId()).stream()
                    .map(esp -> new SelectItem(esp.getId(), esp.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getParamEspecialidades() {
        return paramEspecialidades;
    }

    public void setParamEspecialidades(List<SelectItem> paramEspecialidades) {
        this.paramEspecialidades = paramEspecialidades;
    }

    public void onChangeModalFacultades(ActionEvent event) {
        LOG.info("[{}]", event.getPhaseId());
        mpEspecialidades.clear();
        if (this.getMpFacultadId() != 0) {
            HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            LOG.trace("facu:::: {}", this.getMpFacultadId());
            mpEspecialidades.addAll(dao.seleccionarEspecialidad(this.getMpFacultadId()).stream()
                    .map(esp -> new SelectItem(esp.getId(), esp.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getMpEspecialidades() {
        return mpEspecialidades;
    }

    public void setMpEspecialidades(List<SelectItem> mpEspecialidades) {
        this.mpEspecialidades = mpEspecialidades;
    }

    public List<MbCurso> getListaCursos() {
        return listaCursos;
    }

    public void setListaCursos(List<MbCurso> listaCursos) {
        this.listaCursos = listaCursos;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMpId(0);
        this.setMpCodigo("");
        this.setMpNombre("");
        this.setMpEspecialidadId(0);
        this.setMpFacultadId(0);
        mpEspecialidades.clear();
    }

    public String getMpCodigo() {
        return mpCodigo;
    }

    public void setMpCodigo(String mpCodigo) {
        this.mpCodigo = mpCodigo;
    }

    public String getMpNombre() {
        return mpNombre;
    }

    public void setMpNombre(String mpNombre) {
        this.mpNombre = mpNombre;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
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

    public void enviar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getMpCodigo().length() == 0 ||
                this.getMpNombre().length() == 0 ||
                this.getMpFacultadId() == 0 ||
                this.getMpEspecialidadId() == 0) {
            this.setOncomplete("alert('Ingrese los datos necesarios.')");
        } else {
            var curso = new AcCurso();
            var msj = "Se creo un Curso correctamente";
            LOG.debug("Insertara {}", this.getMpId());
            if (this.getMpId() != 0) {
                curso.setId(this.getMpId());
                msj = "Se actualizo el Curso correctamente";
            }
            curso.setCurCodigo(this.getMpCodigo());
            curso.setCurNombre(this.getMpNombre());
            var esp = new AcEspecialidad();
            esp.setId(this.getMpEspecialidadId());
            curso.setEsp(esp);
            curso.setCurActivo("1");
            daoCurso.insertOrUpdateCurso(curso);
            this.setMpId(curso.getId());
            LOG.info("Course saved/updated {}", this.mpId);
            this.setOncomplete("alert('" + msj + ".');Richfaces.hideModalPanel('mp')");
        }
    }

    public void seleccionar() {
        listaCursos = new ArrayList<>();
        LOG.debug("Looking for {} | {} | {} | {}", this.getParamCodigo(), this.getParamNombre(), this.getParamFacultadId(), this.getParamEspecialidadId());
        List<Object[]> lista = daoCurso.seleccionarCurso(this.getParamCodigo(), this.getParamNombre(), this.getParamFacultadId(), this.getParamEspecialidadId());
        MbCurso cursos;
        for (Object[] bC : lista) {
            cursos = new MbCurso();
            cursos.setRowId(Integer.parseInt(bC[0].toString()));
            cursos.setParamNombre(bC[1].toString());
            cursos.setParamCodigo(bC[2].toString());
            cursos.setParamEspecialidadId(Integer.parseInt(bC[3].toString()));
            cursos.setRowEspecialidadDescripcion(bC[4].toString());
            cursos.setParamFacultadId(Integer.parseInt(bC[5].toString()));
            cursos.setRowFacultadDescripcion(bC[6].toString());
            listaCursos.add(cursos);
        }
        LOG.info("Total of courses {}", listaCursos.size());
        this.setListaCursos(listaCursos);
    }

    public void eliminarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter delete = (UIParameter) event.getComponent().findComponent("delete");
        var id = Integer.valueOf(delete.getValue().toString());

        daoCurso.eliminarCurso(id);
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}] editando", event.getPhaseId());
        mpId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_id")).getValue().toString());
        mpCodigo = ((UIParameter) event.getComponent().findComponent("p_codigo")).getValue().toString();
        mpNombre = ((UIParameter) event.getComponent().findComponent("p_nombre")).getValue().toString();
        mpFacultadId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_facultad")).getValue().toString());
        mpEspecialidadId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_especialidad")).getValue().toString());
        onChangeModalFacultades(event);
        LOG.debug("/{}/{}/{}/{}/{}", mpId, mpCodigo, mpNombre, mpFacultadId, mpEspecialidadId);
    }

    public int getMpId() {
        return mpId;
    }

    public void setMpId(int mpId) {
        this.mpId = mpId;
    }

    public int getParamFacultadId() {
        return paramFacultadId;
    }

    public void setParamFacultadId(int paramFacultadId) {
        this.paramFacultadId = paramFacultadId;
    }

    public int getParamEspecialidadId() {
        return paramEspecialidadId;
    }

    public void setParamEspecialidadId(int paramEspecialidadId) {
        this.paramEspecialidadId = paramEspecialidadId;
    }

    public String getRowEspecialidadDescripcion() {
        return rowEspecialidadDescripcion;
    }

    public void setRowEspecialidadDescripcion(String rowEspecialidadDescripcion) {
        this.rowEspecialidadDescripcion = rowEspecialidadDescripcion;
    }

    public String getRowFacultadDescripcion() {
        return rowFacultadDescripcion;
    }

    public void setRowFacultadDescripcion(String rowFacultadDescripcion) {
        this.rowFacultadDescripcion = rowFacultadDescripcion;
    }
}
