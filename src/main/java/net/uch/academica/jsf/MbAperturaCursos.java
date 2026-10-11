package net.uch.academica.jsf;

import net.uch.academica.dao.HSAperturaCursosDAO;
import net.uch.academica.dao.HSPlanCurricularCursoDAO;
import net.uch.academica.dao.HSPlanCurricularDAO;
import net.uch.academica.dao.HSSistemaEvaluacionDAO;
import net.uch.academica.jsf.beans.ObjNodeApertura;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.AbstractBaseMbViewTree;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.*;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.ACTION_IMAGES_ABIERTO;
import static net.uch.common.Constants.ACTION_IMAGES_CERRADO;

public class MbAperturaCursos extends AbstractBaseMbViewTree {

    private static final Logger LOG = LoggerFactory.getLogger(MbAperturaCursos.class);

    private final HSAperturaCursosDAO daoCursoAperturado = ServiceFinder.findBean("springHibernateDaoCursoAperturado", HSAperturaCursosDAO.class);
    private final HSSistemaEvaluacionDAO daoSistemaEvaluacion = ServiceFinder.findBean("springHibernateDaoSistemaEvaluacion", HSSistemaEvaluacionDAO.class);

    private List<ObjNodeApertura> nodos;
    private int comboSemestreId;
    private SelectItem[] comboSemestres;
    private ObjNodeApertura selectedNode;

    private List<MbAperturaCursos> listaCursos;

    private String alert = "";

    // Table
    private int cursoId;
    private int planCursoId;
    private String cursoCicloLabel;
    private String cursoCodigo;
    private String cursoNombre;
    private String cursoSemestre;
    private String cursoEstaAperturado;
    private int curapeId;
    private int curapeSisEvaluacionId;
    private String curapeSisEvaluacion;
    private String curapeImgCandado;
    // Modal
    private int modalCurapeId;
    private int modalCursoId;
    private int modalPlancurId;
    private String modalCiclo;
    private String modalCodigo;
    private String modalNombre;
    private boolean modalEstaAperturado = true;
    private int modalSistemaEvaluacionId;
    private SelectItem[] comboSistemaEvaluacion;
    private List<SelectItem> comboPromediosExamenes;
    private List<MbAperturaCursos> tablaDetallePromedios;
    private List<Integer> idsToDelete;

    private boolean personalizado = true;
    private String efecto;

    private boolean editable;

    private int sisevaperId = 0;
    private String sisevaperCodigo;
    private String sisevaperNombre;
    private int sisevaperPromedioId;
    private String sisevaperPromedio;
    private String sisevaperPeso = "1";
    private String sisevaperSemana;
    private int sisevaperOrden;

    private String modalSistemaEvaluacion;
    private String secCodigo;
    private String secNombre;
    private String secVacantes;
    private String secActivo;
    private List<MbAperturaCursos> tablaSecciones;

    private String tmpCodigo;
    private String tmpNombre;
    private String tmpPeso;
    private String tmpSemana;
    private int tmpOrden;

    private List<AcSemestre> currentSemestres;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        List<AcSemestre> semestres = daoSemestre.seleccionarSemestresVigentes();
        this.setComboSemestres(semestres.stream()
                .map(s -> new SelectItem(s.getId(), s.getSemNombre()))
                .toArray(SelectItem[]::new));
        currentSemestres = daoSemestre.seleccionarSemestreActivo();
        setComboPromediosExamenes(new ArrayList<>());

        List<AcSisEvaluacion> lista = daoSistemaEvaluacion.seleccionarComboSistemaEvaluacion();
        this.setComboSistemaEvaluacion(lista.stream()
                .map(siseva -> new SelectItem(siseva.getId(), siseva.getSisevaNombre()))
                .toArray(SelectItem[]::new));

        idsToDelete = new ArrayList<>();
    }

    public List<ObjNodeApertura> getNodos() {
        return nodos;
    }

    public void setNodos(List<ObjNodeApertura> nodos) {
        this.nodos = nodos;
    }

    public int getComboSemestreId() {
        return comboSemestreId;
    }

    public void setComboSemestreId(int comboSemestreId) {
        this.comboSemestreId = comboSemestreId;
    }

    public SelectItem[] getComboSemestres() {
        return comboSemestres;
    }

    public void setComboSemestres(SelectItem[] comboSemestres) {
        this.comboSemestres = comboSemestres;
    }

    public ObjNodeApertura getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(ObjNodeApertura selectedNode) {
        this.selectedNode = selectedNode;
    }

    public List<MbAperturaCursos> getListaCursos() {
        return listaCursos;
    }

    public void setListaCursos(List<MbAperturaCursos> listaCursos) {
        this.listaCursos = listaCursos;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    public int getPlanCursoId() {
        return planCursoId;
    }

    public void setPlanCursoId(int planCursoId) {
        this.planCursoId = planCursoId;
    }

    public String getCursoCicloLabel() {
        return cursoCicloLabel;
    }

    public void setCursoCicloLabel(String cursoCicloLabel) {
        this.cursoCicloLabel = cursoCicloLabel;
    }

    public String getCursoCodigo() {
        return cursoCodigo;
    }

    public void setCursoCodigo(String cursoCodigo) {
        this.cursoCodigo = cursoCodigo;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public String getCursoSemestre() {
        return cursoSemestre;
    }

    public void setCursoSemestre(String cursoSemestre) {
        this.cursoSemestre = cursoSemestre;
    }

    public String getCursoEstaAperturado() {
        return cursoEstaAperturado;
    }

    public void setCursoEstaAperturado(String cursoEstaAperturado) {
        this.cursoEstaAperturado = cursoEstaAperturado;
    }

    public int getCurapeId() {
        return curapeId;
    }

    public void setCurapeId(int curapeId) {
        this.curapeId = curapeId;
    }

    public int getCurapeSisEvaluacionId() {
        return curapeSisEvaluacionId;
    }

    public void setCurapeSisEvaluacionId(int curapeSisEvaluacionId) {
        this.curapeSisEvaluacionId = curapeSisEvaluacionId;
    }

    public String getCurapeSisEvaluacion() {
        return curapeSisEvaluacion;
    }

    public void setCurapeSisEvaluacion(String curapeSisEvaluacion) {
        this.curapeSisEvaluacion = curapeSisEvaluacion;
    }

    public String getCurapeImgCandado() {
        return curapeImgCandado;
    }

    public void setCurapeImgCandado(String curapeImgCandado) {
        this.curapeImgCandado = curapeImgCandado;
    }

    public int getModalCurapeId() {
        return modalCurapeId;
    }

    public void setModalCurapeId(int modalCurapeId) {
        this.modalCurapeId = modalCurapeId;
    }

    public int getModalCursoId() {
        return modalCursoId;
    }

    public void setModalCursoId(int modalCursoId) {
        this.modalCursoId = modalCursoId;
    }

    public int getSisevaperPromedioId() {
        return sisevaperPromedioId;
    }

    public void setSisevaperPromedioId(int sisevaperPromedioId) {
        this.sisevaperPromedioId = sisevaperPromedioId;
    }

    public String getSisevaperPromedio() {
        return sisevaperPromedio;
    }

    public void setSisevaperPromedio(String sisevaperPromedio) {
        this.sisevaperPromedio = sisevaperPromedio;
    }

    public String getSisevaperCodigo() {
        return sisevaperCodigo;
    }

    public void setSisevaperCodigo(String sisevaperCodigo) {
        this.sisevaperCodigo = sisevaperCodigo;
    }

    public String getSisevaperNombre() {
        return sisevaperNombre;
    }

    public void setSisevaperNombre(String sisevaperNombre) {
        this.sisevaperNombre = sisevaperNombre;
    }

    public String getSisevaperPeso() {
        return sisevaperPeso;
    }

    public void setSisevaperPeso(String sisevaperPeso) {
        this.sisevaperPeso = sisevaperPeso;
    }

    public String getSisevaperSemana() {
        return sisevaperSemana;
    }

    public void setSisevaperSemana(String sisevaperSemana) {
        this.sisevaperSemana = sisevaperSemana;
    }

    public List<MbAperturaCursos> getTablaDetallePromedios() {
        return tablaDetallePromedios;
    }

    public void setTablaDetallePromedios(List<MbAperturaCursos> tablaDetallePromedios) {
        this.tablaDetallePromedios = tablaDetallePromedios;
    }

    public int getSisevaperId() {
        return sisevaperId;
    }

    public void setSisevaperId(int sisevaperId) {
        this.sisevaperId = sisevaperId;
    }

    public String getSecActivo() {
        return secActivo;
    }

    public void setSecActivo(String secActivo) {
        this.secActivo = secActivo;
    }

    public boolean isPersonalizado() {
        return personalizado;
    }

    public void setPersonalizado(boolean personalizado) {
        this.personalizado = personalizado;
    }

    public void cambiarEfecto(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.isPersonalizado()) {
            this.setEfecto("showDiv()");
        } else {
            this.setEfecto("hideDiv({duration:0.3})");
        }
    }

    public String getEfecto() {
        return efecto;
    }

    public void setEfecto(String efecto) {
        this.efecto = efecto;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getModalSistemaEvaluacion() {
        return modalSistemaEvaluacion;
    }

    public void setModalSistemaEvaluacion(String modalSistemaEvaluacion) {
        this.modalSistemaEvaluacion = modalSistemaEvaluacion;
    }

    public String getSecCodigo() {
        return secCodigo;
    }

    public void setSecCodigo(String secCodigo) {
        this.secCodigo = secCodigo;
    }

    public String getSecNombre() {
        return secNombre;
    }

    public void setSecNombre(String secNombre) {
        this.secNombre = secNombre;
    }

    public String getSecVacantes() {
        return secVacantes;
    }

    public void setSecVacantes(String secVacantes) {
        this.secVacantes = secVacantes;
    }

    public int getModalPlancurId() {
        return modalPlancurId;
    }

    public void setModalPlancurId(int modalPlancurId) {
        this.modalPlancurId = modalPlancurId;
    }

    public List<MbAperturaCursos> getTablaSecciones() {
        return tablaSecciones;
    }

    public void setTablaSecciones(List<MbAperturaCursos> tablaSecciones) {
        this.tablaSecciones = tablaSecciones;
    }

    public String getModalCiclo() {
        return modalCiclo;
    }

    public void setModalCiclo(String modalCiclo) {
        this.modalCiclo = modalCiclo;
    }

    public String getModalCodigo() {
        return modalCodigo;
    }

    public void setModalCodigo(String modalCodigo) {
        this.modalCodigo = modalCodigo;
    }

    public String getModalNombre() {
        return modalNombre;
    }

    public void setModalNombre(String modalNombre) {
        this.modalNombre = modalNombre;
    }

    public boolean isModalEstaAperturado() {
        return modalEstaAperturado;
    }

    public void setModalEstaAperturado(boolean modalEstaAperturado) {
        this.modalEstaAperturado = modalEstaAperturado;
    }

    public int getModalSistemaEvaluacionId() {
        return modalSistemaEvaluacionId;
    }

    public void setModalSistemaEvaluacionId(int modalSistemaEvaluacionId) {
        this.modalSistemaEvaluacionId = modalSistemaEvaluacionId;
    }

    public SelectItem[] getComboSistemaEvaluacion() {
        return comboSistemaEvaluacion;
    }

    public void setComboSistemaEvaluacion(SelectItem[] comboSistemaEvaluacion) {
        this.comboSistemaEvaluacion = comboSistemaEvaluacion;
    }

    public void loadPromediosExamenes(ActionEvent event) {
        LOG.debug("[{}] On change sistema evaluacion {}", event.getPhaseId(), this.modalSistemaEvaluacionId);
        this.comboPromediosExamenes.clear();
        if (modalSistemaEvaluacionId != 0) {
            AcSisEvaluacion lista = daoSistemaEvaluacion.obtenerSistemaEvaluacion(this.getModalSistemaEvaluacionId());
            Set<AcSisEvaDetalle> listaDetalle = lista.getAcSisEvaDetalles();

            this.comboPromediosExamenes.addAll(listaDetalle.stream()
                    .map(sisevadet -> new SelectItem(sisevadet.getId(), sisevadet.getSisevaDetalleNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getComboPromediosExamenes() {
        return comboPromediosExamenes;
    }

    public void setComboPromediosExamenes(List<SelectItem> comboPromediosExamenes) {
        this.comboPromediosExamenes = comboPromediosExamenes;
    }

    public int getSisevaperOrden() {
        return sisevaperOrden;
    }

    public void setSisevaperOrden(int sisevaperOrden) {
        this.sisevaperOrden = sisevaperOrden;
    }

    public String armarEstructura() {
        LOG.debug("Armando estructura arbol");
        var estructuraFacultades = new StringBuilder();
        var estructuraEspecialidades = new StringBuilder();
        var estructuraPlan = new StringBuilder();
        var estructuraCiclo = new StringBuilder();
        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        List<TbCatalogo> catalogoCiclos = daoCatalogo.seleccionarGrupo("006");
        HSFacultadDAO daoFacultad = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        List<AcFacultad> lFacultad = daoFacultad.seleccionarFacultad("", "");

        HSPlanCurricularDAO daoPlanCurricular = ServiceFinder.findBean("springHibernateDaoPlanCurricular", HSPlanCurricularDAO.class);

        // Prefetch every specialty's curricular plans (with their AcPlanCursos join-fetched) in one
        // query and group them in-memory by specialty, instead of one query per specialty inside the
        // loop (the same N+1 fixed in the other trees). Same criteria, fetch and order -> the
        // structure string and node ids are byte-identical.
        List<Integer> especialidadIds = new ArrayList<>();
        for (AcFacultad facultad : lFacultad) {
            for (AcEspecialidad especialidad : facultad.getAcEspecialidads()) {
                especialidadIds.add(especialidad.getId());
            }
        }
        Map<Integer, List<AcPlancurricular>> planesPorEspecialidad = new HashMap<>();
        for (AcPlancurricular plan : especialidadIds.isEmpty() ? Collections.<AcPlancurricular>emptyList() : daoPlanCurricular.findPlanesByEspecialidades(especialidadIds)) {
            planesPorEspecialidad.computeIfAbsent(plan.getEsp().getId(), k -> new ArrayList<>()).add(plan);
        }

        List<ObjNodeApertura> lNodo = new ArrayList<>();
        for (var i = 0; i < lFacultad.size(); i++) {
            estructuraFacultades.append(i + 1).append("=").append(lFacultad.get(i).getFacNombre()).append("\n");
            Set<AcEspecialidad> esp = lFacultad.get(i).getAcEspecialidads();
            List<AcEspecialidad> list = Collections.synchronizedList(new LinkedList<>(esp));
            for (var j = 0; j < list.size(); j++) {
                estructuraEspecialidades.append(i + 1).append(".").append(j + 1).append("=").append(list.get(j).getEspNombre()).append("\n");
                List<AcPlancurricular> listPlan = planesPorEspecialidad.getOrDefault(list.get(j).getId(), Collections.emptyList());
                for (var k = 0; k < listPlan.size(); k++) {
                    AcPlancurricular planCurricular = listPlan.get(k);
                    estructuraPlan.append(i + 1).append(".").append(j + 1).append(".").append(k + 1).append("=").append(planCurricular.getPlanDescripcion()).append("\n");
                    Set<AcPlanCurso> plancur = planCurricular.getAcPlanCursos();
                    List<AcPlanCurso> pc = groupBy(Collections.synchronizedList(new LinkedList<>(plancur)));
                    for (var h = 0; h < pc.size(); h++) {
                        for (TbCatalogo catalogoCiclo : catalogoCiclos) {
                            if ((pc.get(h).getPlancurCiclo()).equals("006" + catalogoCiclo.getCatCodigoElemento())) {
                                estructuraCiclo.append(i + 1).append(".").append(j + 1).append(".").append(k + 1).append(".").append(h + 1).append("=").append(catalogoCiclo.getCatDescripcionElemento()).append("\n");
                            }
                        }

                        var node = new ObjNodeApertura();
                        node.setId((i + 1) + ":" + (j + 1) + ":" + (k + 1) + ":" + (h + 1));
                        node.setArbolFacultad(lFacultad.get(i).getFacNombre());
                        node.setArbolEspecialidad(list.get(j).getEspNombre());
                        node.setArbolCiclo(pc.get(h).getPlancurCiclo());
                        node.setArbolPlan(planCurricular.getId());
                        lNodo.add(node);
                    }
                }
            }
        }
        this.setNodos(new ArrayList<>(lNodo));
        LOG.info("Tree generated | Total nodes {}", lNodo.size());
        return estructuraFacultades + estructuraEspecialidades.toString() + estructuraPlan + estructuraCiclo;
    }

    public List<AcPlanCurso> groupBy(List<AcPlanCurso> lista) {
        List<String> grupo = new ArrayList<>();
        List<AcPlanCurso> resultado = new ArrayList<>();
        if (!lista.isEmpty()) {
            String ciclo = lista.get(0).getPlancurCiclo();
            llenar(ciclo, grupo, resultado);
            String flag;
            grupo.add(ciclo);
            for (AcPlanCurso acPlanCurso : lista) {
                ciclo = acPlanCurso.getPlancurCiclo();
                flag = "0";
                for (String s : grupo) {
                    if (ciclo.equals(s)) {
                        flag = "1";
                        break;
                    }
                }
                if (flag.equals("0")) {
                    llenar(ciclo, grupo, resultado);
                }
            }
        }
        return resultado;
    }

    public void llenar(String ciclo, List<String> grupo, List<AcPlanCurso> resultado) {
        grupo.add(ciclo);
        var plancur = new AcPlanCurso();
        plancur.setPlancurCiclo(ciclo);
        resultado.add(plancur);
    }

    private ObjNodeApertura buscarNodo(String id) {
        return nodos.stream().filter(nodo -> nodo.getId().equalsIgnoreCase(id))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public void seleccionNodo(NodeSelectedEvent event) {
        UITree tree = (UITree) event.getComponent();
        var a = String.valueOf(tree.getRowKey());
        cursoCicloLabel = (String) tree.getRowData();
        int length = a.length();

        LOG.debug("[{}] Selected node '{}'", event.getPhaseId(), a);
        if (length >= 7) {
            setSelectedNode(buscarNodo(a));

            mostrarTabla();
        }
    }

    public void mostrarTabla() {
        int plan = selectedNode.getArbolPlan();
        String ciclo = selectedNode.getArbolCiclo();
        LOG.debug("Mostrando tabla del plan '{}' | ciclo '{}' | semestre {}", plan, ciclo, comboSemestreId);
        HSPlanCurricularCursoDAO dao = ServiceFinder.findBean("springHibernateDaoPlanCurricularCurso", HSPlanCurricularCursoDAO.class);
        List<AcPlanCurso> list = dao.seleccionarCursos(plan, ciclo);
        listaCursos = new ArrayList<>();

        String semestreCodigo = currentSemestres.stream().filter(s -> s.getId() == this.comboSemestreId)
                .map(AcSemestre::getSemCodigo).findFirst().orElseThrow(IllegalArgumentException::new);

        for (AcPlanCurso acPlanCurso : list) {
            var apertura = new MbAperturaCursos();
            apertura.setCursoId(acPlanCurso.getCur().getId());
            apertura.setCursoCicloLabel(cursoCicloLabel);
            apertura.setPlanCursoId(acPlanCurso.getId());
            apertura.setCursoCodigo(acPlanCurso.getCur().getCurCodigo());
            apertura.setCursoNombre(acPlanCurso.getCur().getCurNombre());
            AcCursoAperturado cursoAperturado = daoCursoAperturado.verificarCursoAperturado(acPlanCurso.getId(), this.comboSemestreId);
            if (cursoAperturado == null) {
                apertura.setCursoEstaAperturado("No");
                apertura.setCurapeImgCandado(ACTION_IMAGES_CERRADO);
                apertura.setCurapeSisEvaluacion("No Especificado");
                apertura.setCurapeSisEvaluacionId(0);
                apertura.setCurapeId(-1);
            } else {
                if (cursoAperturado.getCurapeAperturado().equals("1")) {
                    apertura.setCursoEstaAperturado("Si");
                    apertura.setCurapeImgCandado(ACTION_IMAGES_ABIERTO);
                } else {
                    apertura.setCursoEstaAperturado("No");
                    apertura.setCurapeImgCandado(ACTION_IMAGES_CERRADO);
                }
                AcSisEvaluacion li = daoSistemaEvaluacion.obtenerSistemaEvaluacion(cursoAperturado.getSisevaId());
                if (li == null) {
                    apertura.setCurapeSisEvaluacion("No Asignado");
                } else {
                    apertura.setCurapeSisEvaluacion(li.getSisevaNombre());
                }

                apertura.setCurapeSisEvaluacionId(cursoAperturado.getSisevaId());
                apertura.setCurapeId(cursoAperturado.getId());
            }
            apertura.setCursoSemestre(semestreCodigo);
            listaCursos.add(apertura);
        }
        LOG.info("Total of courses opened {}", listaCursos.size());
    }

    public void seleccionPorSemestre(ActionEvent event) {
        LOG.debug("[{}] On change semester {}", event.getPhaseId(), comboSemestreId);
        if (selectedNode != null) {
            mostrarTabla();
        }
    }

    public void seleccion() {
        LOG.debug("[X]");
        mostrarTabla();
    }

    public void detallar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        MbAperturaCursos row = (MbAperturaCursos) event.getComponent().getAttributes().get("curso_aperturado");
        setTablaDetallePromedios(new ArrayList<>());
        setTablaSecciones(new ArrayList<>());
        modalCursoId = row.getCursoId();
        modalCiclo = row.getCursoCicloLabel();
        modalCodigo = row.getCursoCodigo();
        modalNombre = row.getCursoNombre();
        modalCurapeId = row.getCurapeId();
        modalSistemaEvaluacionId = row.getCurapeSisEvaluacionId();
        loadPromediosExamenes(event);
        modalSistemaEvaluacion = row.getCurapeSisEvaluacion();
        modalPlancurId = row.getPlanCursoId();
        modalEstaAperturado = row.getCursoEstaAperturado().equals("Si");
        personalizado = true;
        // Cleaning form
        sisevaperId = 0;
        idsToDelete.clear();
        sisevaperPromedioId = 0;
        sisevaperCodigo = "";
        sisevaperNombre = "";
        sisevaperPeso = "1";
        sisevaperSemana = "";
        sisevaperOrden = 0;

        AcCursoAperturado cursoAperturado = daoCursoAperturado.seleccionarUnCursosAperturado(modalCurapeId);
        if (cursoAperturado != null) {
            Set<AcSisEvaPersonalizado> sisevaper = cursoAperturado.getAcSisEvaPersonalizados();
            for (AcSisEvaPersonalizado acSisEvaPersonalizado : sisevaper) {
                var curape = new MbAperturaCursos();
                curape.setModalSistemaEvaluacionId(modalSistemaEvaluacionId);
                curape.setSisevaperId(acSisEvaPersonalizado.getId());
                curape.setSisevaperCodigo(acSisEvaPersonalizado.getSisevaCodigo());
                curape.setSisevaperNombre(acSisEvaPersonalizado.getSisevaPerNombre());
                curape.setSisevaperPeso(acSisEvaPersonalizado.getSisevaPerPeso());
                curape.setSisevaperPromedioId(acSisEvaPersonalizado.getSisevaDetalle().getId());
                curape.setSisevaperPromedio(acSisEvaPersonalizado.getSisevaDetalle().getSisevaDetalleNombre());
                curape.setSisevaperSemana(acSisEvaPersonalizado.getSisevaPerExaSemana());
                curape.setSisevaperOrden(acSisEvaPersonalizado.getSisevaPerOrden());
                curape.setSecActivo(acSisEvaPersonalizado.getSisevaPerActivo());

                tablaDetallePromedios.add(curape);
            }
        }
        if (cursoAperturado != null) {
            Set<AcSeccion> secciones = cursoAperturado.getAcSeccions();
            for (AcSeccion acSeccion : secciones) {
                var curape1 = new MbAperturaCursos();
                curape1.setModalSistemaEvaluacionId(modalSistemaEvaluacionId);
                curape1.setSisevaperId(acSeccion.getId());
                curape1.setSecCodigo(acSeccion.getSecCodigo());
                curape1.setSecNombre(acSeccion.getSecNombre());
                curape1.setSecVacantes(String.valueOf(acSeccion.getSecVacantes()));
                curape1.setSecActivo(acSeccion.getSecActivo());
                tablaSecciones.add(curape1);
            }
        }
    }

    public void agregar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setAlert("");
        for (MbAperturaCursos d : tablaDetallePromedios) {
            if (d.getSecActivo().equals("1") && d.getModalSistemaEvaluacionId() != this.getModalSistemaEvaluacionId()) {
                this.setAlert("alert('No se puede asignar planes diferentes')");
                break;
            }
        }
        var aperturaCursos = new MbAperturaCursos();
        aperturaCursos.setModalSistemaEvaluacionId(this.getModalSistemaEvaluacionId());
        aperturaCursos.setSisevaperId(sisevaperId);
        aperturaCursos.setSisevaperPromedioId(this.getSisevaperPromedioId());
        aperturaCursos.setSisevaperPromedio(
                ServiceFinder.getLabel(comboPromediosExamenes, sisevaperPromedioId));
        aperturaCursos.setSisevaperCodigo(this.getSisevaperCodigo());
        aperturaCursos.setSisevaperNombre(this.getSisevaperNombre());
        aperturaCursos.setSisevaperPeso(this.getSisevaperPeso());
        aperturaCursos.setSisevaperSemana(this.getSisevaperSemana());
        aperturaCursos.setSisevaperOrden(this.getSisevaperOrden());
        aperturaCursos.setSecActivo("1");
        tablaDetallePromedios.add(aperturaCursos);
        sisevaperId--;
    }

    public void quitar(ActionEvent event) {
        var s = event.getComponent().getAttributes().get("id_det").toString();
        var tmpSisevaperId = Integer.parseInt(s);
        LOG.debug("[{}] Sis eva per id: {}", event.getPhaseId(), tmpSisevaperId);
        for (var i = 0; i < tablaDetallePromedios.size(); i++) {
            if (tmpSisevaperId == tablaDetallePromedios.get(i).getSisevaperId()) {
                if (tmpSisevaperId > 0) {
                    idsToDelete.add(tmpSisevaperId);
                }
                tablaDetallePromedios.remove(i);
                break;
            }
        }
    }

    public void edit(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        tmpCodigo = this.getSisevaperCodigo();
        tmpNombre = this.getSisevaperNombre();
        tmpPeso = this.getSisevaperPeso();
        tmpSemana = this.getSisevaperSemana();
        tmpOrden = this.getSisevaperOrden();
        this.setEditable(true);
    }

    public void aceptar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setEditable(false);
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setSisevaperCodigo(tmpCodigo);
        this.setSisevaperNombre(tmpNombre);
        this.setSisevaperPeso(tmpPeso);
        this.setSisevaperSemana(tmpSemana);
        this.setSisevaperOrden(tmpOrden);
        this.setEditable(false);
    }

    public void insertar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var curape = new AcCursoAperturado();
        LOG.trace("this.getId_curape_u(): {}, semester: {}", this.getModalCurapeId(), comboSemestreId);
        if (this.getModalCurapeId() > 0) {
            curape.setId(this.getModalCurapeId());
        }
        curape.setSisevaId(this.getModalSistemaEvaluacionId());
        curape.setCurapeCreacion(new Date());
        if (this.isModalEstaAperturado()) {
            curape.setCurapeAperturado("1");
        } else {
            curape.setCurapeAperturado("0");
        }
        curape.setCurapeActivo("1");
        var sem = new AcSemestre(comboSemestreId);
        curape.setSem(sem);
        var plancur = new AcPlanCurso();
        plancur.setId(this.getModalPlancurId());
        curape.setPlancur(plancur);
        Set<AcSisEvaPersonalizado> listaDetalle = new LinkedHashSet<>();
        if (this.isPersonalizado() || !tablaDetallePromedios.isEmpty()) {
            for (MbAperturaCursos d : tablaDetallePromedios) {
                var sisevaper = new AcSisEvaPersonalizado();
                if (d.getSisevaperId() > 0) {
                    sisevaper.setId(d.getSisevaperId());
                }
                sisevaper.setSisevaCodigo(d.getSisevaperCodigo());
                sisevaper.setSisevaPerPeso(d.getSisevaperPeso());
                sisevaper.setSisevaPerNombre(d.getSisevaperNombre());
                sisevaper.setSisevaPerActivo(d.getSecActivo());
                sisevaper.setSisevaPerExaSemana(d.getSisevaperSemana());
                sisevaper.setSisevaPerOrden(d.getSisevaperOrden());
                var sisevadet = new AcSisEvaDetalle();
                sisevadet.setId(d.getSisevaperPromedioId());
                sisevaper.setSisevaDetalle(sisevadet);
                sisevaper.setCurape(curape);
                listaDetalle.add(sisevaper);
            }
        } else {
            AcSisEvaluacion lista = daoSistemaEvaluacion.obtenerSistemaEvaluacion(this.getModalSistemaEvaluacionId());
            Set<AcSisEvaDetalle> lis = lista.getAcSisEvaDetalles();
            List<AcSisEvaDetalle> list = Collections.synchronizedList(new LinkedList<>(lis));
            for (var i = 0; i < list.size(); i++) {
                var sisevaper = new AcSisEvaPersonalizado();
                sisevaper.setSisevaCodigo(list.get(i).getSisevaDetalleCodigo());
                sisevaper.setSisevaPerPeso(list.get(i).getSisevaDetallePeso().toString());
                sisevaper.setSisevaPerNombre(list.get(i).getSisevaDetalleNombre());
                sisevaper.setSisevaPerActivo(list.get(i).getSisevaDetalleActivo());
                sisevaper.setSisevaPerOrden(i + 1);
                var sisevadet = new AcSisEvaDetalle();
                sisevadet.setId(list.get(i).getId());
                sisevaper.setSisevaDetalle(sisevadet);
                sisevaper.setCurape(curape);
                listaDetalle.add(sisevaper);
            }
        }
        curape.setAcSisEvaPersonalizados(listaDetalle);

        Set<AcSeccion> listSecciones = new LinkedHashSet<>();
        for (MbAperturaCursos bAperturaCursos : tablaSecciones) {
            var seccion = new AcSeccion();
            if (bAperturaCursos.getSisevaperId() > 0) {
                seccion.setId(bAperturaCursos.getSisevaperId());
            }
            seccion.setSecCodigo(bAperturaCursos.getSecCodigo());
            seccion.setSecVacantes(Integer.parseInt(bAperturaCursos.getSecVacantes()));
            seccion.setSecNombre(bAperturaCursos.getSecNombre());
            seccion.setSecActivo(bAperturaCursos.getSecActivo());
            seccion.setCurape(curape);
            listSecciones.add(seccion);
        }
        curape.setAcSeccions(listSecciones);
        if (this.getModalCurapeId() == -1) {
            daoCursoAperturado.aperturarCurso(curape);
            LOG.debug("Inserta {}", curape.getId());
        } else {
            daoCursoAperturado.actualizarCursoAperturado(curape);
            LOG.debug("Actualiza {}", curape.getId());
        }
        if (!idsToDelete.isEmpty()) {
            daoCursoAperturado.deleteSisEvaPersonalizados(idsToDelete);
        }
        this.setCurapeId(curape.getId());
    }

    public void agregarSec(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var aperturaCursos = new MbAperturaCursos();
        aperturaCursos.setSecCodigo(this.getSecCodigo());
        aperturaCursos.setSisevaperId(sisevaperId);
        aperturaCursos.setSecNombre(this.getSecNombre());
        aperturaCursos.setSecVacantes(this.getSecVacantes());
        aperturaCursos.setSecActivo("1");
        tablaSecciones.add(aperturaCursos);
        sisevaperId--;
    }

    public void quitarSec(ActionEvent event) {
        var s = event.getComponent().getAttributes().get("id_det1").toString();
        var tmpSisevaperId = Integer.parseInt(s);
        LOG.debug("[{}] Sis eva per id: {}", event.getPhaseId(), tmpSisevaperId);

        for (var i = 0; i < tablaSecciones.size(); i++) {
            if (tmpSisevaperId == tablaSecciones.get(i).getSisevaperId()) {
                if (tmpSisevaperId > 0) {
                    tablaSecciones.get(i).setSecActivo("0");
                } else {
                    tablaSecciones.remove(i);
                }
                break;
            }
        }
    }

    public void editSec(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        tmpCodigo = this.getSecCodigo();
        tmpNombre = this.getSecNombre();
        tmpPeso = this.getSecVacantes();
        this.setEditable(true);
    }

    public void cancelarSec(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setSisevaperCodigo(tmpCodigo);
        this.setSisevaperNombre(tmpNombre);
        this.setSecVacantes(tmpPeso);
        this.setEditable(false);
    }
}
