package net.uch.academica.jsf;

import net.uch.academica.dao.HSCursoDAO;
import net.uch.academica.dao.HSPlanCurricularCursoDAO;
import net.uch.academica.dao.HSPlanCurricularDAO;
import net.uch.academica.jsf.beans.BeanPlanCurricularCurso;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.common.SystemHelper;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.mapping.*;
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

public class MbPlanCurricularCurso {

    private static final Logger LOG = LoggerFactory.getLogger(MbPlanCurricularCurso.class);

    private final SystemHelper helper = ServiceFinder.findBean("helper", SystemHelper.class);

    private final HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
    private final HSEspecialidadDAO daoEspecialidad = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
    private final HSPlanCurricularDAO daoPlanCurricular = ServiceFinder.findBean("springHibernateDaoPlanCurricular", HSPlanCurricularDAO.class);
    private final HSPlanCurricularCursoDAO daoPlanCurricularCurso = ServiceFinder.findBean("springHibernateDaoPlanCurricularCurso", HSPlanCurricularCursoDAO.class);

    private String bModificacionAux = "0";
    private int nombre1;
    private List<BeanPlanCurricularCurso> listaCursos;
    private List<SelectItem> comboCursos;
    private List<SelectItem> comboSemestre;
    private SelectItem[] comboTipos;
    private String codigo1;
    private SelectItem[] mpFacultades;
    private List<SelectItem> mpEspecialidades;
    private int plancurId;
    private int mpId;
    private int paramFacultadId;
    private int mpFacultadId;
    private int paramEspecialidadId;
    private int mpEspecialidadId = 0;
    private String paramCodigo;
    private String mpCodigo;
    private String paramDescripcion;
    private String mpDescripcion;
    private String lblCreacionDescripcion;
    private Date mpCreacion;
    private String mpResolucion;
    private List<BeanPlanCurricularCurso> listaPlanCurricular;
    private List<SelectItem> comboEspecialidadesS;
    private SelectItem[] paramFacultades;
    private String lblFacultadDescripcion;
    private String lblEspecialidadDescripcion;
    private String ciclo;
    private String oncomplete;
    private String tipo1;
    private String creditos1;
    private List<BeanPlanCurricularCurso> equivalences;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSFacultadDAO dao = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);

        SelectItem[] facultades = dao.seleccionarFacultad("", "").stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .toArray(SelectItem[]::new);

        setParamFacultades(facultades);
        setMpFacultades(facultades);

        setComboEspecialidadesS(new ArrayList<>());
        setMpEspecialidades(new ArrayList<>());

        setComboSemestre(new ArrayList<>());
        setComboCursos(new ArrayList<>());

        setComboTipos(daoCatalogo.seleccionarCatalogo("012").stream()
                .map(c -> new SelectItem(c.getCatCodigoGrupo() + c.getCatCodigoElemento(),
                        c.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));
        LOG.info("En total {} tipos", comboTipos.length);
    }

    public String getbModificacionAux() {
        return bModificacionAux;
    }

    public void setbModificacionAux(String bModificacionAux) {
        this.bModificacionAux = bModificacionAux;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public List<SelectItem> getComboSemestre() {
        LOG.debug("Getting combobox de semestres");
        return comboSemestre;
    }

    public void setComboSemestre(List<SelectItem> comboSemestre) {
        this.comboSemestre = comboSemestre;
    }

    private void loadComboboxSemestreForModal() {
        LOG.debug("Building Semestre Combobox for especialidad {}", mpEspecialidadId);
        var listaCatalogo = daoCatalogo.seleccionarCatalogo("006");

        AcEspecialidad especialidad = daoEspecialidad.seleccionarUnaEspecialidad(mpEspecialidadId);

        var tamano = Integer.parseInt(especialidad.getEspNroperiodos());
        comboSemestre.clear();

        for (var i = 0; i < tamano; i++) {
            comboSemestre.add(new SelectItem(listaCatalogo.get(i).getCatCodigoGrupo() + listaCatalogo.get(i).getCatCodigoElemento(),
                    listaCatalogo.get(i).getCatDescripcionElemento()));
        }
        LOG.info("En total {} semestres", comboSemestre.size());
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getLblCreacionDescripcion() {
        return lblCreacionDescripcion;
    }

    public void setLblCreacionDescripcion(String lblCreacionDescripcion) {
        this.lblCreacionDescripcion = lblCreacionDescripcion;
    }

    public String getLblFacultadDescripcion() {
        return lblFacultadDescripcion;
    }

    public void setLblFacultadDescripcion(String lblFacultadDescripcion) {
        this.lblFacultadDescripcion = lblFacultadDescripcion;
    }

    public String getLblEspecialidadDescripcion() {
        return lblEspecialidadDescripcion;
    }

    public void setLblEspecialidadDescripcion(String lblEspecialidadDescripcion) {
        this.lblEspecialidadDescripcion = lblEspecialidadDescripcion;
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}] Opening modal", event.getPhaseId());
        this.setMpId(0);
        this.setMpCodigo("");
        this.setMpCreacion(null);
        this.setMpDescripcion("");
        this.setMpResolucion("");
        this.setMpFacultadId(0);
        this.setMpEspecialidadId(0);
        cargarEspecialidades(event);
        setbModificacionAux("0");
    }

    public List<SelectItem> getMpEspecialidades() {
        return mpEspecialidades;
    }

    public void setMpEspecialidades(List<SelectItem> mpEspecialidades) {
        this.mpEspecialidades = mpEspecialidades;
    }

    public void cargarEspecialidades(ActionEvent event) {
        LOG.debug("[{}] Para facultad {} for modal", event.getPhaseId(), mpFacultadId);

        mpEspecialidades.clear();
        if (mpFacultadId != 0) {
            mpEspecialidades.addAll(daoEspecialidad.seleccionarEspecialidad(mpFacultadId).stream()
                    .map(esp -> new SelectItem(esp.getId(), esp.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public SelectItem[] getMpFacultades() {
        return mpFacultades;
    }

    public void setMpFacultades(SelectItem[] mpFacultades) {
        this.mpFacultades = mpFacultades;
    }

    public int getPlancurId() {
        return plancurId;
    }

    public void setPlancurId(int plancurId) {
        this.plancurId = plancurId;
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

    public List<BeanPlanCurricularCurso> getListaPlanCurricular() {
        return listaPlanCurricular;
    }

    public void setListaPlanCurricular(List<BeanPlanCurricularCurso> listaPlanCurricular) {
        this.listaPlanCurricular = listaPlanCurricular;
    }

    public String getMpCodigo() {
        return mpCodigo;
    }

    public void setMpCodigo(String mpCodigo) {
        this.mpCodigo = mpCodigo;
    }

    public String getMpDescripcion() {
        return mpDescripcion;
    }

    public void setMpDescripcion(String mpDescripcion) {
        this.mpDescripcion = mpDescripcion;
    }

    public int getMpId() {
        return mpId;
    }

    public void setMpId(int mpId) {
        this.mpId = mpId;
    }

    public int getMpFacultadId() {
        return mpFacultadId;
    }

    public void setMpFacultadId(int mpFacultadId) {
        this.mpFacultadId = mpFacultadId;
    }

    public int getMpEspecialidadId() {
        return mpEspecialidadId;
    }

    public void setMpEspecialidadId(int mpEspecialidadId) {
        this.mpEspecialidadId = mpEspecialidadId;
    }

    public Date getMpCreacion() {
        return mpCreacion;
    }

    public void setMpCreacion(Date mpCreacion) {
        this.mpCreacion = mpCreacion;
    }

    public String getMpResolucion() {
        return mpResolucion;
    }

    public void setMpResolucion(String mpResolucion) {
        this.mpResolucion = mpResolucion;
    }

    public String getTipo1() {
        return tipo1;
    }

    public void setTipo1(String tipo1) {
        this.tipo1 = tipo1;
    }

    public String getCreditos1() {
        return creditos1;
    }

    public void setCreditos1(String creditos1) {
        this.creditos1 = creditos1;
    }

    public String getCodigo1() {
        return codigo1;
    }

    public void setCodigo1(String codigo1) {
        this.codigo1 = codigo1;
    }

    public SelectItem[] getComboTipos() {
        LOG.debug("Getting Tipos Combobox");
        return comboTipos;
    }

    public void setComboTipos(SelectItem[] comboTipos) {
        this.comboTipos = comboTipos;
    }

    public void grabar() {
        if (this.getMpCodigo().length() == 0 || this.getMpDescripcion().length() == 0 || this.getMpFacultadId() == 0 || this.getMpEspecialidadId() == 0) {
            this.setOncomplete("alert('Ingrese los datos necesarios.')");
        } else {
            var plancurricular = new AcPlancurricular();
            plancurricular.setPlanCodigo(this.getMpCodigo());
            plancurricular.setPlanDescripcion(this.getMpDescripcion());
            plancurricular.setPlanCreacion(this.getMpCreacion());
            plancurricular.setPlanResolucion(this.getMpResolucion());
            plancurricular.setEsp(new AcEspecialidad(this.getMpEspecialidadId()));
            plancurricular.setPlanActivo("1");
            setMpId(daoPlanCurricular.insertarPlanCurricular(plancurricular));
            this.setOncomplete("alert('Se creo un Plan correctamente.');Richfaces.hideModalPanel('mp')");
            LOG.info("Curricular plan saved {}", mpId);
        }
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for '{}' | '{}' | '{}' | '{}'", event.getPhaseId(), paramCodigo, paramDescripcion, paramFacultadId, paramEspecialidadId);
        List<Object[]> lista = daoPlanCurricular.seleccionarPlanCurricular(this.getParamCodigo(), this.getParamDescripcion(), this.getParamFacultadId(), this.getParamEspecialidadId());
        listaPlanCurricular = new ArrayList<>();
        for (Object[] bC : lista) {
            var bPC = new BeanPlanCurricularCurso();
            bPC.setRowId((int) bC[0]);
            bPC.setRowCodigo(bC[1].toString());
            bPC.setRowDescripcion(bC[2].toString());
            bPC.setRowCreacion(bC[3].toString().substring(0, 10));
            bPC.setFacultadId(Integer.parseInt(bC[4].toString()));
            bPC.setEspecialidadId(Integer.parseInt(bC[5].toString()));
            bPC.setRowResolucion(bC[6].toString());
            bPC.setRowFacultadDesc(bC[8].toString());
            bPC.setRowEspecialidadDesc(bC[9].toString());
            listaPlanCurricular.add(bPC);
        }
        LOG.info("En total {} plan curriculares", listaPlanCurricular.size());
    }

    public void openModalEquivalencias(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var tmpPlancurId = Integer.parseInt(event.getComponent().getAttributes().get("plancur_id").toString());
        this.setEquivalences(daoPlanCurricularCurso.findEquivalences(tmpPlancurId).stream()
                .map(e -> {
                    var b = new BeanPlanCurricularCurso();
                    b.setRowCodigo(e.getEquCursoEquivalente());
                    b.setRowDescripcion(e.getEquObservacion());
                    return b;
                }).collect(Collectors.toList()));
        LOG.info("Total equivalences {} of {}", equivalences.size(), tmpPlancurId);
    }

    public void eliminarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter delete = (UIParameter) event.getComponent().findComponent("delete");
        daoPlanCurricular.eliminarPlanCurricular(delete.getValue().toString());
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("p_id");
        UIParameter codigo = (UIParameter) event.getComponent().findComponent("p_codigo");
        UIParameter descripcion = (UIParameter) event.getComponent().findComponent("p_descripcion");
        UIParameter facultad = (UIParameter) event.getComponent().findComponent("p_facultad");
        UIParameter especialidad = (UIParameter) event.getComponent().findComponent("p_especialidad");
        UIParameter facultadDesc = (UIParameter) event.getComponent().findComponent("p_facultad_d");
        UIParameter especialidadDesc = (UIParameter) event.getComponent().findComponent("p_especialidad_d");
        UIParameter resolucion = (UIParameter) event.getComponent().findComponent("p_resolucion");
        UIParameter creacion = (UIParameter) event.getComponent().findComponent("p_creacion");

        Date fecha = helper.parse(creacion.getValue().toString());
        this.setMpCreacion(fecha);
        this.setMpId(Integer.parseInt(id.getValue().toString()));
        this.setMpFacultadId(Integer.parseInt(facultad.getValue().toString()));
        this.setMpEspecialidadId(Integer.parseInt(especialidad.getValue().toString()));
        this.setMpCodigo(codigo.getValue().toString());
        this.setMpDescripcion(descripcion.getValue().toString());
        this.setMpResolucion(resolucion.getValue().toString());
        // For adding courses
        this.setLblCreacionDescripcion(creacion.getValue().toString());
        this.setLblFacultadDescripcion(facultadDesc.getValue().toString());
        this.setLblEspecialidadDescripcion(especialidadDesc.getValue().toString());
        setbModificacionAux("1");
    }

    public void editar() {
        setbModificacionAux("0");
        if (this.getMpCodigo().length() == 0 ||
                this.getMpDescripcion().length() == 0 ||
                this.getMpFacultadId() == 0 ||
                this.getMpEspecialidadId() == 0) {
            this.setOncomplete("alert('Ingrese los datos necesarios.')");
            setbModificacionAux("1");
        } else {
            var plan = new AcPlancurricular();
            plan.setId(this.getMpId());
            plan.setPlanCodigo(this.getMpCodigo());
            plan.setPlanDescripcion(this.getMpDescripcion());
            plan.setPlanResolucion(this.getMpResolucion());
            plan.setPlanCreacion(this.getMpCreacion());
            var esp = new AcEspecialidad();
            esp.setId(this.getMpEspecialidadId());
            plan.setEsp(esp);
            plan.setPlanActivo("1");
            daoPlanCurricular.actualizarPlanCurricular(plan);
            this.setOncomplete("alert('Se Actualizaron los datos del Plan correctamente.');Richfaces.hideModalPanel('mp')");
            LOG.info("Curricular plan updated {}", mpId);
        }
    }

    public void enviar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (getbModificacionAux().equals("1")) {
            editar();
        } else {
            grabar();
        }
    }

    public SelectItem[] getParamFacultades() {
        return paramFacultades;
    }

    public void setParamFacultades(SelectItem[] paramFacultades) {
        this.paramFacultades = paramFacultades;
    }

    public List<SelectItem> getComboEspecialidadesS() {
        return comboEspecialidadesS;
    }

    public void setComboEspecialidadesS(List<SelectItem> comboEspecialidadesS) {
        this.comboEspecialidadesS = comboEspecialidadesS;
    }

    public void cargarParamEspecialidades(ActionEvent event) {
        LOG.debug("[{}] Para facultad {}", event.getPhaseId(), paramFacultadId);

        comboEspecialidadesS.clear();
        if (paramFacultadId != 0) {
            comboEspecialidadesS.addAll(daoEspecialidad.seleccionarEspecialidad(paramFacultadId).stream()
                    .map(especialidade -> new SelectItem(especialidade.getId(), especialidade.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public void agregarFila(ActionEvent event) {
        LOG.debug("[{}] Adding row {} | {} | {}", event.getPhaseId(), this.getMpId(), this.getNombre1(), this.getCiclo());

        var plancurso = new AcPlanCurso();
        if (daoPlanCurricularCurso.verificarRepitencia(this.getMpId(), this.getNombre1(), this.getCiclo())) {
            this.setOncomplete("alert('El curso ya fue agregado')");
        } else {
            var cur = new AcCurso();
            cur.setId(this.getNombre1());
            plancurso.setCur(cur);
            var plan = new AcPlancurricular();
            plan.setId(this.getMpId());
            plancurso.setPlan(plan);
            plancurso.setPlancurCiclo(this.getCiclo());
            plancurso.setPlancurTipo(this.getTipo1());
            plancurso.setPlancurCredito(this.getCreditos1());
            plancurso.setPlancurCodigo(this.getCodigo1());
            plancurso.setPlancurActivo("1");
            setPlancurId(daoPlanCurricularCurso.insertarPlanCurricularCurso(plancurso));
            this.setOncomplete("alert('Se asigno un curso correctamente')");
            LOG.info("Course added {}", getPlancurId());

            loadListCoursesForModal();
        }
    }

    public void quitarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter delete = (UIParameter) event.getComponent().findComponent("p_id1");
        daoPlanCurricularCurso.eliminarPlanCurricularCurso(delete.getValue().toString());
        loadListCoursesForModal();
    }

    public int getNombre1() {
        return nombre1;
    }

    public void setNombre1(int nombre1) {
        this.nombre1 = nombre1;
    }

    public List<BeanPlanCurricularCurso> getListaCursos() {
        return listaCursos;
    }

    public void setListaCursos(List<BeanPlanCurricularCurso> listaCursos) {
        this.listaCursos = listaCursos;
    }

    public void openModalForInsertCourses() {
        loadComboboxSemestreForModal();
        setCodigo1("");
        loadComboboxCursosForModal();
        setCreditos1("");

        loadListCoursesForModal();
    }

    private void loadListCoursesForModal() {
        LOG.debug("Building list courses assigned to Plan curricular for modal");
        listaCursos = new ArrayList<>();
        List<Object[]> lista = daoPlanCurricularCurso.seleccionarPlanCurricularCurso(this.getMpId());
        for (Object[] bC : lista) {
            var cursos = new BeanPlanCurricularCurso();
            cursos.setRowId(Integer.parseInt(bC[0].toString()));
            cursos.setRowDescripcion(bC[7].toString());
            cursos.setRowCodigo(bC[2].toString());
            cursos.setCiclo(bC[4].toString());
            cursos.setRowCreditos(bC[6].toString());
            cursos.setRowTipos(bC[5].toString());
            listaCursos.add(cursos);
        }
        LOG.info("En total {} cursos asignados", listaCursos.size());
    }

    public List<SelectItem> getComboCursos() {
        LOG.debug("Getting combobox de cursos");
        return comboCursos;
    }

    public void setComboCursos(List<SelectItem> comboCursos) {
        this.comboCursos = comboCursos;
    }

    public void loadComboboxCursosForModal() {
        LOG.debug("Building Courses Combobox for speciality {}", mpEspecialidadId);
        HSCursoDAO dao = ServiceFinder.findBean("springHibernateDaoCurso", HSCursoDAO.class);
        List<AcCurso> cursos = dao.seleccionarCurso(this.getMpEspecialidadId());
        comboCursos.clear();
        for (AcCurso curso : cursos) {
            comboCursos.add(new SelectItem(curso.getId(), curso.getCurNombre()));
        }
        LOG.info("En total {} cursos", comboCursos.size());
    }

    public List<BeanPlanCurricularCurso> getEquivalences() {
        return equivalences;
    }

    public void setEquivalences(List<BeanPlanCurricularCurso> equivalences) {
        this.equivalences = equivalences;
    }
}
