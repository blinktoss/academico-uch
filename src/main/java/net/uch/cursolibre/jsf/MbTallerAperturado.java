package net.uch.cursolibre.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.AbstractBaseMbViewTree;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.*;
import net.uch.cursolibre.jsf.beans.BeanClSisEvaPersonalizado;
import net.uch.cursolibre.jsf.beans.ObjNodeCLApertura;
import net.uch.mapping.*;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.*;

import static net.uch.common.Constants.*;

public class MbTallerAperturado extends AbstractBaseMbViewTree {

    private static final Logger LOG = LoggerFactory.getLogger(MbTallerAperturado.class);

    private final HSTallerAperturadoDAO daoCLTallerAperturado = ServiceFinder.findBean("springHibernateDaoCLTallerAperturado", HSTallerAperturadoDAO.class);
    private final HSSistemaEvaluacionCLDAO daoSE = ServiceFinder.findBean("springHibernateDaoCLSistemaEvaluacion", HSSistemaEvaluacionCLDAO.class);

    //CAMPOS
    private String lblModulo = "";
    private String lblCurso = "";
    private List<MbTallerAperturado> talleres = new ArrayList<>();
    private List<MbTallerAperturado> subTalleresAperturados = new ArrayList<>();
    private String mpModulo = "";
    private String mpCurso = "";
    private int subTalapeId;
    private String subTalapeDesc;
    private int mpSisevaId;
    private int subTalapeNumHoras;
    private boolean mpTalapeAperturado;
    private int mpTalId;
    private boolean mpTalapeVigente;
    private String mpTalapeActual;
    private String mpTalapeActivo;
    private String tipo;
    private String oncomplete;
    private SelectItem[] mpSisEvaluaciones;
    private int rowId;
    private String rowCodigo;
    private String rowDescripcion;
    private String subSisEvaluacion;
    private String subImagenAperturar;
    private String subImagenVigente;
    private String subImagenActual;
    private int lblCurId;
    //Subtabla de Talleres aperturados
    private boolean verDetalle;
    private String rowImagenMostrar;
    private String rowTextoMostrar;
    //General
    private List<ObjNodeCLApertura> nodos;
    private boolean personalizado = true;
    private String efecto;
    private List<BeanClSisEvaPersonalizado> mpnSisEvaPer;
    private List<BeanClSisEvaPersonalizado> mpqSisEvaPer;
    private BeanClSisEvaPersonalizado sep = new BeanClSisEvaPersonalizado();
    private String mpElimMensaje;
    private boolean mpElimBand;
    private int mpElimTalapeId;
    private String mpElimTalapeDesc;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        this.setMpSisEvaluaciones(daoSE.seleccionarSistemasEvaluacion().stream()
                .map(se -> new SelectItem(se.getSisevaId(), se.getSisevaNombre()))
                .toArray(SelectItem[]::new));
    }

    public List<ObjNodeCLApertura> getNodos() {
        return nodos;
    }

    public void setNodos(List<ObjNodeCLApertura> nodos) {
        this.nodos = nodos;
    }

    public String getRowImagenMostrar() {
        return rowImagenMostrar;
    }

    public void setRowImagenMostrar(String rowImagenMostrar) {
        this.rowImagenMostrar = rowImagenMostrar;
    }

    public String getRowTextoMostrar() {
        return rowTextoMostrar;
    }

    public void setRowTextoMostrar(String rowTextoMostrar) {
        this.rowTextoMostrar = rowTextoMostrar;
    }

    public boolean isVerDetalle() {
        return verDetalle;
    }

    public void setVerDetalle(boolean verDetalle) {
        this.verDetalle = verDetalle;
    }

    //METODOS
    public String armarEstructura() {
        var estructuraArea = new StringBuilder();
        var estructuraModulo = new StringBuilder();
        var estructuraCurso = new StringBuilder();
        HSAreaDAO daoA = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);
        HSModuloDAO daoM = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
        HSCursoCLDAO daoC = ServiceFinder.findBean("springHibernateDaoCLCurso", HSCursoCLDAO.class);
        List<ClArea> areas = daoA.seleccionarArea("");

        // Prefetch modules and courses for the whole tree in one query per level (instead of one
        // query per parent node - the same N+1 already fixed in the academica trees), grouped
        // in-memory by parent id. Rows come back ordered by (parent, own id), so the tree structure
        // string and the node ids are byte-identical to the per-parent-query version.
        List<Integer> areaIds = new ArrayList<>();
        for (ClArea area : areas) {
            areaIds.add(area.getAreId());
        }
        Map<Integer, List<CLModuloRow>> modulosPorArea = new HashMap<>();
        List<Integer> moduloIds = new ArrayList<>();
        for (CLModuloRow row : areaIds.isEmpty() ? Collections.<CLModuloRow>emptyList() : daoM.findModulosByAreas(areaIds)) {
            modulosPorArea.computeIfAbsent(row.getAreaId(), k -> new ArrayList<>()).add(row);
            moduloIds.add(row.getModuloId());
        }
        Map<Integer, List<CLCursoRow>> cursosPorModulo = new HashMap<>();
        for (CLCursoRow row : moduloIds.isEmpty() ? Collections.<CLCursoRow>emptyList() : daoC.findCursosByModulos(moduloIds)) {
            cursosPorModulo.computeIfAbsent(row.getModuloId(), k -> new ArrayList<>()).add(row);
        }

        List<ObjNodeCLApertura> lnodo = new ArrayList<>();
        for (var i = 0; i < areas.size(); i++) {
            estructuraArea.append(i + 1).append("=").append(areas.get(i).getAreDescripcion()).append("\n");
            List<CLModuloRow> modulos = modulosPorArea.getOrDefault(areas.get(i).getAreId(), Collections.emptyList());
            for (var j = 0; j < modulos.size(); j++) {
                estructuraModulo.append(i + 1).append(".").append(j + 1).append("=").append(modulos.get(j).getModuloDescripcion()).append("\n");
                List<CLCursoRow> cursos = cursosPorModulo.getOrDefault(modulos.get(j).getModuloId(), Collections.emptyList());
                for (var k = 0; k < cursos.size(); k++) {
                    estructuraCurso.append(i + 1).append(".").append(j + 1).append(".").append(k + 1).append("=").append(cursos.get(k).getCursoNombre()).append("\n");

                    var node = new ObjNodeCLApertura();
                    node.setId((i + 1) + ":" + (j + 1) + ":" + (k + 1));
                    node.setCursoId(cursos.get(k).getCursoId());
                    node.setCursoNombre(cursos.get(k).getCursoNombre());
                    node.setModDescripcion(modulos.get(j).getModuloDescripcion());
                    lnodo.add(node);
                }
            }
        }
        this.setNodos(new ArrayList<>(lnodo));
        return estructuraArea + estructuraModulo.toString() + estructuraCurso;
    }

    private ObjNodeCLApertura buscarNodo(String id) {
        return nodos.stream().filter(nodo -> nodo.getId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

    public void seleccion(NodeSelectedEvent event) {
        UITree tree = (UITree) event.getComponent();
        var a = tree.getRowKey().toString();
        int length = a.length();
        LOG.debug("[{}] Selected node '{}'", event.getPhaseId(), a);

        if (length >= 5) {
            ObjNodeCLApertura nodo = Objects.requireNonNull(buscarNodo(a));

            lblCurId = nodo.getCursoId();
            this.setLblModulo(nodo.getModDescripcion());
            this.setLblCurso(nodo.getCursoNombre());

            cargarTalleres(lblCurId);
        }
    }

    public void cargarTalleres(int courseId) {
        this.setTalleres(new ArrayList<>());

        HSTallerDAO daoT = ServiceFinder.findBean("springHibernateDaoCLTaller", HSTallerDAO.class);

        List<ClTaller> listaTaller = daoT.seleccionarTalleres(courseId);
        for (ClTaller taller : listaTaller) {
            var tal = new MbTallerAperturado();
            tal.setRowId(taller.getTalId());
            tal.setRowCodigo(taller.getTalCodigo());
            tal.setRowDescripcion(taller.getTalDescripcion());

            List<ClTallerAperturado> listaTallerAperturado = daoCLTallerAperturado.seleccionarTalleresAperturadosPorTaller(taller.getTalId());
            for (ClTallerAperturado talape : listaTallerAperturado) {
                var talApe = new MbTallerAperturado();

                talApe.setRowId(talape.getClTaller().getTalId());
                talApe.setSubTalapeId(talape.getTalapeId());
                talApe.setSubTalapeDesc(talape.getTalapeDescripcion());
                talApe.setSubSisEvaluacion(daoSE.seleccionarSistemaEvaluacion(talape.getSisevaId()).getSisevaNombre());
                talApe.setSubTalapeNumHoras(talape.getTalapeNumHoras());

                if (talape.getTalapeAperturado().equalsIgnoreCase("1")) {
                    talApe.setSubImagenAperturar(ACTION_IMAGES_ABIERTO);
                } else {
                    talApe.setSubImagenAperturar(ACTION_IMAGES_CERRADO);
                }

                if (talape.getTalapeVigente().equalsIgnoreCase("1")) {
                    talApe.setSubImagenVigente(ACTION_IMAGES_ACTIVAR);
                } else {
                    talApe.setSubImagenVigente(ACTION_IMAGES_DESACTIVAR);
                }

                if (talape.getTalapeActual().equalsIgnoreCase("1")) {
                    talApe.setSubImagenActual(ACTION_IMAGES_ACTIVAR);
                } else {
                    talApe.setSubImagenActual(ACTION_IMAGES_DESACTIVAR);
                }
                tal.addTalleresAperturados(talApe);
            }
            tal.setVerDetalle(false);
            tal.setRowImagenMostrar(ACTION_IMAGES_DOWN);
            tal.setRowTextoMostrar("Mostrar Detalle");
            this.addTalleres(tal);
        }
    }

    public void mostrarDetalle(ActionEvent event) {
        LOG.debug("[{}] show details {}", event.getPhaseId(), this.verDetalle);
        if (this.isVerDetalle()) {
            this.setVerDetalle(false);
            this.setRowImagenMostrar(ACTION_IMAGES_DOWN);
            this.setRowTextoMostrar("Mostrar Detalle");
        } else {
            this.setVerDetalle(true);
            this.setRowImagenMostrar(ACTION_IMAGES_UP);
            this.setRowTextoMostrar("Ocultar Detalle");
        }
    }

    public void aperturarTaller(ActionEvent event) {
        this.setOncomplete("");
        this.setMpModulo(this.getLblModulo());
        this.setMpCurso(this.getLblCurso());
        mpnSisEvaPer = new ArrayList<>();
        mpqSisEvaPer = new ArrayList<>();
        UIParameter uipIdTallerAperTmp = (UIParameter) event.getComponent().findComponent("id_taller_aper_tmp");

        if (uipIdTallerAperTmp == null) {
            LOG.debug("[{}] Opening modal for new Open Workshop", event.getPhaseId());
            HSTallerDAO daoT = ServiceFinder.findBean("springHibernateDaoCLTaller", HSTallerDAO.class);
            var idTallerTmp = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_taller_tmp")).getValue().toString());

            ClTaller tal = daoT.buscarTaller(idTallerTmp);
            this.setSubTalapeId(0);
            this.setSubTalapeDesc(tal.getTalDescripcion());
            this.setSubTalapeNumHoras(0);
            this.setMpTalapeAperturado(true);
            this.setMpTalapeVigente(true);
            this.setMpTalapeActual("1");
            this.setMpTalapeActivo("1");
            this.setMpTalId(tal.getTalId());
            this.setMpSisEvaluaciones(getMpSisEvaluaciones());
            this.setMpSisevaId(0);
        } else {
            LOG.debug("[{}] Opening modal for edit Open Workshop", event.getPhaseId());
            var idTallerAperTmp = Integer.parseInt(uipIdTallerAperTmp.getValue().toString());

            var talape = daoCLTallerAperturado.seleccionarTallerAperturado(idTallerAperTmp);
            this.setSubTalapeId(talape.getTalapeId());
            this.setSubTalapeDesc(talape.getTalapeDescripcion());
            this.setSubTalapeNumHoras(talape.getTalapeNumHoras());
            this.setMpTalapeAperturado(talape.getTalapeAperturado().equals("1"));
            this.setMpTalapeVigente(talape.getTalapeVigente().equals("1"));
            this.setMpTalapeActual(talape.getTalapeActual());
            this.setMpTalapeActivo(talape.getTalapeActivo());
            this.setMpTalId(talape.getClTaller().getTalId());
            this.setMpSisEvaluaciones(getMpSisEvaluaciones());
            this.setMpSisevaId(talape.getSisevaId());

            List<ClSisEvaPersonalizado> lSisEvaPer = daoSE.seleccionarSisEvaPerClTalape(talape);
            for (ClSisEvaPersonalizado siseva_per : lSisEvaPer) {
                mpnSisEvaPer.add(new BeanClSisEvaPersonalizado(siseva_per, mpnSisEvaPer.size()));
            }
        }
        sep = new BeanClSisEvaPersonalizado(this.getMpSisevaId());
        this.setPersonalizado(true);
        this.setOncomplete("showDiv();Richfaces.showModalPanel('mpApertura');");
    }

    public boolean edicionActivada() {
        for (BeanClSisEvaPersonalizado bean : mpnSisEvaPer) {
            if (bean.isEditActive()) {
                return true;
            }
        }
        return false;
    }

    public void guardarTaller(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (this.getSubTalapeNumHoras() == 0 || this.getMpSisevaId() == 0) {
            this.setOncomplete("alert('Debe ingresar un número de horas y un sistema de evaluación para Guardar.')");
        } else if (isPersonalizado() && mpnSisEvaPer.isEmpty()) {
            this.setOncomplete("alert('Debe ingresar los Sistemas de Evaluacion personalizados para Guardar.')");
        } else if (edicionActivada()) {
            this.setOncomplete("alert('Termine de editar los Sistemas de Evaluacion personalizados.')");
        } else {
            var flag = true;
            if (!this.isMpTalapeAperturado() && this.getSubTalapeId() != 0) {
                Set<ClSeccion> talapeSeccs = daoCLTallerAperturado.seleccionarTallerAperturado(this.getSubTalapeId()).getClSeccions();
                Iterator<ClSeccion> it = talapeSeccs.iterator();
                var band = false;
                while (it.hasNext() && !band) {
                    ClSeccion sec = it.next();
                    if (sec.getSecActivo().equalsIgnoreCase("1")) {
                        band = true;
                    }
                }
                if (band) {
                    this.setOncomplete("alert('No se puede desaperturar el Taller ya que posee secciones activas.')");
                    flag = false;
                }
            }
            if (flag) {
                var talape = new ClTallerAperturado();
                if (this.getSubTalapeId() == 0) {
                    talape.setClSeccions(new LinkedHashSet<>());
                    talape.setClSisEvaPersonalizados(new LinkedHashSet<>());
                    this.setOncomplete("alert('Apertura Satisfactoria.');Richfaces.hideModalPanel('mpApertura')");
                } else {
                    talape.setTalapeId(this.getSubTalapeId());
                    this.setOncomplete("alert('Actualización Satisfactoria.');Richfaces.hideModalPanel('mpApertura')");
                }
                talape.setTalapeDescripcion(this.getSubTalapeDesc().trim().toUpperCase());
                talape.setSisevaId(this.getMpSisevaId());
                talape.setTalapeNumHoras(this.getSubTalapeNumHoras());
                if (this.isMpTalapeAperturado()) {
                    talape.setTalapeAperturado("1");
                } else {
                    talape.setTalapeAperturado("0");
                }

                talape.setClTaller(new ClTaller(this.getMpTalId()));
                talape.setTalapeFecha(new Date());
                if (this.isMpTalapeVigente()) {
                    talape.setTalapeVigente("1");
                } else {
                    talape.setTalapeVigente("0");
                }
                talape.setTalapeActual(this.getMpTalapeActual());
                talape.setTalapeActivo(this.getMpTalapeActivo());

                daoCLTallerAperturado.insertarOrActualizarTallerAperturado(talape);
                this.setSubTalapeId(talape.getTalapeId());
                LOG.info("CL Taller Aperturado saved/updated {}", this.getSubTalapeId());
                List<ClSisEvaPersonalizado> sisEvaPers = new ArrayList<>();

                if (isPersonalizado()) {
                    for (BeanClSisEvaPersonalizado tmp : mpnSisEvaPer) {
                        var sisevaPer = new ClSisEvaPersonalizado();

                        if (tmp.getId() != 0) {
                            sisevaPer.setSisevaPerId(tmp.getId());
                        }
                        sisevaPer.setSisevaPerNombre(tmp.getNombre());
                        sisevaPer.setSisevaPerExaSemana(tmp.getExaSemana());
                        sisevaPer.setClSisEvaDetalle(new ClSisEvaDetalle(tmp.getDetalle()));
                        sisevaPer.setClTallerAperturado(talape);
                        sisevaPer.setSisevaCodigo(tmp.getCodigo());
                        sisevaPer.setSisevaPerPeso(String.valueOf(tmp.getPeso()));
                        sisevaPer.setSisevaPerOrden(tmp.getOrden());
                        sisevaPer.setSisevaPerActivo("1");
                        sisevaPer.setClNotas(new LinkedHashSet<>());

                        sisEvaPers.add(sisevaPer);
                    }
                } else if (talape.getClSisEvaPersonalizados().isEmpty()) {
                    Set<ClSisEvaDetalle> sisevaDetalles = daoSE.seleccionarSistemaEvaluacion(talape.getSisevaId()).getClSisEvaDetalles();
                    var i = 1;
                    for (ClSisEvaDetalle sisevaDet : sisevaDetalles) {
                        var sisevaPer = new ClSisEvaPersonalizado();

                        sisevaPer.setSisevaPerNombre(sisevaDet.getSisevaDetalleNombre());
                        sisevaPer.setSisevaPerExaSemana(String.valueOf(i));
                        sisevaPer.setClSisEvaDetalle(sisevaDet);
                        sisevaPer.setClTallerAperturado(talape);
                        sisevaPer.setSisevaCodigo(sisevaDet.getSisevaDetalleCodigo());
                        sisevaPer.setSisevaPerPeso("1");
                        sisevaPer.setSisevaPerOrden(i++);
                        sisevaPer.setSisevaPerActivo("1");
                        sisevaPer.setClNotas(new LinkedHashSet<>());

                        sisEvaPers.add(sisevaPer);
                    }
                }
                daoSE.insertarActualizarSisEvaPersonalizado(sisEvaPers);
                LOG.info("CL Taller Aperturado siseva per saved/updated {} | {}", this.getSubTalapeId(), sisEvaPers.size());

                for (BeanClSisEvaPersonalizado tmp : mpqSisEvaPer) {
                    daoSE.eliminarSistemaEvaluacionPersonalizado(tmp.getId());
                }

                mpnSisEvaPer.clear();
                mpqSisEvaPer.clear();

                cargarTalleres(lblCurId);
            }
        }
    }

    public void prepararEliminacionTalape(ActionEvent event) {
        var idTallerAperTmp = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_taller_aper_tmp")).getValue().toString());
        LOG.debug("[{}] Trying to delete {}", event.getPhaseId(), idTallerAperTmp);
        var talape = daoCLTallerAperturado.seleccionarTallerAperturado(idTallerAperTmp);
        this.setMpElimTalapeId(talape.getTalapeId());
        this.setMpElimTalapeDesc(talape.getTalapeDescripcion());

        if (talape.getTalapeAperturado().equalsIgnoreCase("1")) {
            this.setMpElimMensaje("No se puede eliminar un taller que este aperturado.");
            this.setMpElimBand(false);
        } else {
            this.setMpElimMensaje("Se eliminara el taller aperturado.");
            this.setMpElimBand(true);
        }
    }

    public void eliminarTallerAperturado(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        daoCLTallerAperturado.eliminarTallerAperturado(this.getMpElimTalapeId());

        this.setMpElimTalapeId(0);
        this.setMpElimTalapeDesc("");
        cargarTalleres(lblCurId);
    }

    //GETTERS AND SETTERS
    public void addTalleres(MbTallerAperturado tmp) {
        this.talleres.add(tmp);
    }

    public List<MbTallerAperturado> getTalleres() {
        return talleres;
    }

    public void setTalleres(List<MbTallerAperturado> talleres) {
        this.talleres = talleres;
    }

    public void addTalleresAperturados(MbTallerAperturado tmp) {
        this.subTalleresAperturados.add(tmp);
    }

    public List<MbTallerAperturado> getSubTalleresAperturados() {
        return subTalleresAperturados;
    }

    public void setSubTalleresAperturados(List<MbTallerAperturado> subTalleresAperturados) {
        this.subTalleresAperturados = subTalleresAperturados;
    }

    public String getLblModulo() {
        return lblModulo;
    }

    public void setLblModulo(String lblModulo) {
        this.lblModulo = lblModulo;
    }

    public String getLblCurso() {
        return lblCurso;
    }

    public void setLblCurso(String lblCurso) {
        this.lblCurso = lblCurso;
    }

    public int getSubTalapeId() {
        return subTalapeId;
    }

    public void setSubTalapeId(int subTalapeId) {
        this.subTalapeId = subTalapeId;
    }

    public String getSubTalapeDesc() {
        return subTalapeDesc;
    }

    public void setSubTalapeDesc(String subTalapeDesc) {
        this.subTalapeDesc = subTalapeDesc;
    }

    public int getMpSisevaId() {
        return mpSisevaId;
    }

    public void setMpSisevaId(int mpSisevaId) {
        this.mpSisevaId = mpSisevaId;
    }

    public int getSubTalapeNumHoras() {
        return subTalapeNumHoras;
    }

    public void setSubTalapeNumHoras(int subTalapeNumHoras) {
        this.subTalapeNumHoras = subTalapeNumHoras;
    }

    public int getMpTalId() {
        return mpTalId;
    }

    public void setMpTalId(int mpTalId) {
        this.mpTalId = mpTalId;
    }

    public String getMpTalapeActual() {
        return mpTalapeActual;
    }

    public void setMpTalapeActual(String mpTalapeActual) {
        this.mpTalapeActual = mpTalapeActual;
    }

    public String getMpTalapeActivo() {
        return mpTalapeActivo;
    }

    public void setMpTalapeActivo(String mpTalapeActivo) {
        this.mpTalapeActivo = mpTalapeActivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public SelectItem[] getMpSisEvaluaciones() {
        return mpSisEvaluaciones;
    }

    public void setMpSisEvaluaciones(SelectItem[] mpSisEvaluaciones) {
        this.mpSisEvaluaciones = mpSisEvaluaciones;
    }

    public String getRowDescripcion() {
        return rowDescripcion;
    }

    public void setRowDescripcion(String rowDescripcion) {
        this.rowDescripcion = rowDescripcion;
    }

    public String getSubImagenActual() {
        return subImagenActual;
    }

    public void setSubImagenActual(String subImagenActual) {
        this.subImagenActual = subImagenActual;
    }

    public String getSubSisEvaluacion() {
        return subSisEvaluacion;
    }

    public void setSubSisEvaluacion(String subSisEvaluacion) {
        this.subSisEvaluacion = subSisEvaluacion;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getRowCodigo() {
        return rowCodigo;
    }

    public void setRowCodigo(String rowCodigo) {
        this.rowCodigo = rowCodigo;
    }

    public String getSubImagenAperturar() {
        return subImagenAperturar;
    }

    public void setSubImagenAperturar(String subImagenAperturar) {
        this.subImagenAperturar = subImagenAperturar;
    }

    public String getSubImagenVigente() {
        return subImagenVigente;
    }

    public void setSubImagenVigente(String subImagenVigente) {
        this.subImagenVigente = subImagenVigente;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getMpModulo() {
        return mpModulo;
    }

    public void setMpModulo(String mpModulo) {
        this.mpModulo = mpModulo;
    }

    public String getMpCurso() {
        return mpCurso;
    }

    public void setMpCurso(String mpCurso) {
        this.mpCurso = mpCurso;
    }

    public boolean isMpTalapeAperturado() {
        return mpTalapeAperturado;
    }

    public void setMpTalapeAperturado(boolean mpTalapeAperturado) {
        this.mpTalapeAperturado = mpTalapeAperturado;
    }

    public boolean isMpTalapeVigente() {
        return mpTalapeVigente;
    }

    public void setMpTalapeVigente(boolean mpTalapeVigente) {
        this.mpTalapeVigente = mpTalapeVigente;
    }

    public List<BeanClSisEvaPersonalizado> getMpnSisEvaPer() {
        return mpnSisEvaPer;
    }

    public void setMpnSisEvaPer(List<BeanClSisEvaPersonalizado> mpnSisEvaPer) {
        this.mpnSisEvaPer = mpnSisEvaPer;
    }

    public boolean isPersonalizado() {
        return personalizado;
    }

    public void setPersonalizado(boolean personalizado) {
        this.personalizado = personalizado;
    }

    public String getEfecto() {
        return efecto;
    }

    public void setEfecto(String efecto) {
        this.efecto = efecto;
    }

    public BeanClSisEvaPersonalizado getSep() {
        return sep;
    }

    public void setSep(BeanClSisEvaPersonalizado sep) {
        this.sep = sep;
    }

    public void cambiarEfecto(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.isPersonalizado()) {
            this.setEfecto("showDiv()");
        } else {
            this.setEfecto("hideDiv({duration:0.7})");
        }
    }

    public void cambioSisEvaluacion(ActionEvent event) {
        LOG.debug("[{}] On Change {}", event.getPhaseId(), this.getMpSisevaId());
        sep = new BeanClSisEvaPersonalizado(this.getMpSisevaId());
    }

    public void insertarSisEvaPer(ActionEvent event) {
        this.setOncomplete("");
        UIParameter uipTalapeId = (UIParameter) event.getComponent().findComponent("p_talape_id");
        var pTalapeId = Integer.parseInt(String.valueOf(uipTalapeId.getValue()));
        LOG.debug("[{}] For Taller Aperturado Id {}", event.getPhaseId(), pTalapeId);

        if (sep.getDetalle() == 0) {
            this.setOncomplete("alert('Debe de escoger un tipo de promedio.');");
        } else if (sep.getCodigo().trim().length() == 0) {
            this.setOncomplete("alert('Debe de ingresar el codigo.');");
        } else if (sep.getNombre().trim().length() == 0) {
            this.setOncomplete("alert('Debe de ingresar el nombre.');");
        } else if (sep.getPeso() == 0) {
            this.setOncomplete("alert('Debe de ingresar el peso de la nota.');");
        } else if (sep.getOrden() == 0) {
            this.setOncomplete("alert('Debe de ingresar el Nº de orden.');");
        } else {
            if (mpnSisEvaPer == null) {
                mpnSisEvaPer = new ArrayList<>();
            }
            var tmp = new BeanClSisEvaPersonalizado();
            tmp.setPos(mpnSisEvaPer.size());
            tmp.setAluTipo(sep.getAluTipo());
            tmp.setCodigo(sep.getCodigo());
            tmp.setNombre(sep.getNombre());
            tmp.setOrden(sep.getOrden());
            tmp.setPeso(sep.getPeso());
            tmp.setExaSemana(sep.getExaSemana());
            tmp.setDetallen(sep.getNombreDetalle(sep.getDetalle()));
            tmp.setDetalle(sep.getDetalle());
            tmp.setId(sep.getId());
            tmp.setIdSiseva(sep.getIdSiseva());
            tmp.setTalapeId(pTalapeId);

            mpnSisEvaPer.add(tmp);
            sep.limpiar();
        }
    }

    public void quitarSisEvaPer(ActionEvent event) {
        UIParameter p2 = (UIParameter) event.getComponent().findComponent("pos_det");
        var posDet = Integer.parseInt(String.valueOf(p2.getValue()));
        LOG.debug("[{}] For position {}", event.getPhaseId(), posDet);

        if (mpqSisEvaPer == null) {
            mpqSisEvaPer = new ArrayList<>();
        }
        mpqSisEvaPer.add(mpnSisEvaPer.remove(posDet));
        for (var i = 0; i < mpnSisEvaPer.size(); i++) {
            mpnSisEvaPer.get(i).setOrden(i + 1);
            mpnSisEvaPer.get(i).setPos(i);
        }
    }

    public boolean isMpElimBand() {
        return mpElimBand;
    }

    public void setMpElimBand(boolean mpElimBand) {
        this.mpElimBand = mpElimBand;
    }

    public String getMpElimMensaje() {
        return mpElimMensaje;
    }

    public void setMpElimMensaje(String mpElimMensaje) {
        this.mpElimMensaje = mpElimMensaje;
    }

    public String getMpElimTalapeDesc() {
        return mpElimTalapeDesc;
    }

    public void setMpElimTalapeDesc(String mpElimTalapeDesc) {
        this.mpElimTalapeDesc = mpElimTalapeDesc;
    }

    public int getMpElimTalapeId() {
        return mpElimTalapeId;
    }

    public void setMpElimTalapeId(int mpElimTalapeId) {
        this.mpElimTalapeId = mpElimTalapeId;
    }
}
