package net.uch.academica.jsf;

import net.uch.academica.dao.*;
import net.uch.academica.jsf.beans.ObjNodeGnral;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.AbstractBaseMbViewTree;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSAulaDAO;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.*;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIOutput;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.*;

public class MbHorario extends AbstractBaseMbViewTree {

    private static final Logger LOG = LoggerFactory.getLogger(MbHorario.class);

    private final HSHorarioDAO daoHorario = ServiceFinder.findBean("springHibernateDaoHorario", HSHorarioDAO.class);
    private final HSTurnoDAO daoTurno = ServiceFinder.findBean("springHibernateDaoTurno", HSTurnoDAO.class);

    private final List<AcHorario> listaHorario = new ArrayList<>();

    private SelectItem[] comboAulas;
    private SelectItem[] comboTipos;
    private int mpAulaId;
    private String mpTipoId;
    private int mpDocenteId;
    private SelectItem[] comboDocentes;
    private int mpRowIndexIdLun = 0;
    private int mpRowIndexIdMar = 0;
    private int mpRowIndexIdMie = 0;
    private int mpRowIndexIdJue = 0;
    private int mpRowIndexIdVie = 0;
    private int mpRowIndexIdSab = 0;
    private int mpRowIndexIdDom = 0;
    //############################################
    private int ide = 0;
    private List<MbHorario> listaSecciones;
    private int rowIdSem;
    private int rowIdSec;
    private String rowCodigoSec;
    private String rowNombreSec;
    private int rowVacantesSec;
    private List<MbHorario> tablaHorario;
    private int mpRowIdHor;
    private Date mpRowInicioHor;
    private Date mpRowFinHor;
    private String mpRowHorLun = "";
    private String mpRowHorMar = "";
    private String mpRowHorMie = "";
    private String mpRowHorJue = "";
    private String mpRowHorVie = "";
    private String mpRowHorSab = "";
    private String mpRowHorDom = "";
    private SelectItem[] comboTurnos;
    private int mpTurnoId;
    private String oncomplete;
    private String rowVacantesOcupadas;
    private List<ObjNodeGnral> nodos;
    private String lblCursoAperturado;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        comboTurnos = new SelectItem[0];

        HSAulaDAO daoAula = ServiceFinder.findBean("springHibernateDaoAula", HSAulaDAO.class);
        setComboAulas(daoAula.seleccionarAula().stream()
                .map(a -> new SelectItem(a.getId(), a.getAulDes()))
                .toArray(SelectItem[]::new));

        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        setComboTipos(daoCatalogo.seleccionarCatalogo("018").stream()
                .map(cat -> new SelectItem(cat.getCatCodigoGrupo() + cat.getCatCodigoElemento(), cat.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        HSDocenteDAO daoDocente = ServiceFinder.findBean("springHibernateDaoDocente", HSDocenteDAO.class);
        setComboDocentes(daoDocente.seleccionarDocente().stream()
                .map(doc -> new SelectItem(doc.getId(), doc.getDocNombre()))
                .toArray(SelectItem[]::new));
    }

    public List<ObjNodeGnral> getNodos() {
        return nodos;
    }

    public void setNodos(List<ObjNodeGnral> nodos) {
        this.nodos = nodos;
    }

    public String getLblCursoAperturado() {
        return lblCursoAperturado;
    }

    public void setLblCursoAperturado(String lblCursoAperturado) {
        this.lblCursoAperturado = lblCursoAperturado;
    }

    public int getRowIdSem() {
        return rowIdSem;
    }

    public void setRowIdSem(int rowIdSem) {
        this.rowIdSem = rowIdSem;
    }

    public List<MbHorario> getListaSecciones() {
        return listaSecciones;
    }

    public void setListaSecciones(List<MbHorario> listaSecciones) {
        this.listaSecciones = listaSecciones;
    }

    public int getRowIdSec() {
        return rowIdSec;
    }

    public void setRowIdSec(int rowIdSec) {
        this.rowIdSec = rowIdSec;
    }

    public String getRowCodigoSec() {
        return rowCodigoSec;
    }

    public void setRowCodigoSec(String rowCodigoSec) {
        this.rowCodigoSec = rowCodigoSec;
    }

    public String getRowNombreSec() {
        return rowNombreSec;
    }

    public void setRowNombreSec(String rowNombreSec) {
        this.rowNombreSec = rowNombreSec;
    }

    public int getRowVacantesSec() {
        return rowVacantesSec;
    }

    public void setRowVacantesSec(int rowVacantesSec) {
        this.rowVacantesSec = rowVacantesSec;
    }

    public int getMpRowIdHor() {
        return mpRowIdHor;
    }

    public void setMpRowIdHor(int mpRowIdHor) {
        this.mpRowIdHor = mpRowIdHor;
    }

    public List<AcHorario> getListaHorario() {
        return listaHorario;
    }

    public int getIde() {
        return ide;
    }

    public void setIde(int ide) {
        this.ide = ide;
    }

    public Date getMpRowInicioHor() {
        return mpRowInicioHor;
    }

    public void setMpRowInicioHor(Date mpRowInicioHor) {
        this.mpRowInicioHor = mpRowInicioHor;
    }

    public Date getMpRowFinHor() {
        return mpRowFinHor;
    }

    public void setMpRowFinHor(Date mpRowFinHor) {
        this.mpRowFinHor = mpRowFinHor;
    }

    public String getMpRowHorLun() {
        return mpRowHorLun;
    }

    public void setMpRowHorLun(String mpRowHorLun) {
        this.mpRowHorLun = mpRowHorLun;
    }

    public String getMpRowHorMar() {
        return mpRowHorMar;
    }

    public void setMpRowHorMar(String mpRowHorMar) {
        this.mpRowHorMar = mpRowHorMar;
    }

    public String getMpRowHorMie() {
        return mpRowHorMie;
    }

    public void setMpRowHorMie(String mpRowHorMie) {
        this.mpRowHorMie = mpRowHorMie;
    }

    public String getMpRowHorJue() {
        return mpRowHorJue;
    }

    public void setMpRowHorJue(String mpRowHorJue) {
        this.mpRowHorJue = mpRowHorJue;
    }

    public String getMpRowHorVie() {
        return mpRowHorVie;
    }

    public void setMpRowHorVie(String mpRowHorVie) {
        this.mpRowHorVie = mpRowHorVie;
    }

    public String getMpRowHorSab() {
        return mpRowHorSab;
    }

    public void setMpRowHorSab(String mpRowHorSab) {
        this.mpRowHorSab = mpRowHorSab;
    }

    public String getMpRowHorDom() {
        return mpRowHorDom;
    }

    public void setMpRowHorDom(String mpRowHorDom) {
        this.mpRowHorDom = mpRowHorDom;
    }

    public int getMpTurnoId() {
        return mpTurnoId;
    }

    public void setMpTurnoId(int mpTurnoId) {
        this.mpTurnoId = mpTurnoId;
    }

    public int getMpDocenteId() {
        return mpDocenteId;
    }

    public void setMpDocenteId(int mpDocenteId) {
        this.mpDocenteId = mpDocenteId;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public SelectItem[] getComboDocentes() {
        return comboDocentes;
    }

    public void setComboDocentes(SelectItem[] comboDocentes) {
        this.comboDocentes = comboDocentes;
    }

    public SelectItem[] getComboTurnos() {
        return comboTurnos;
    }

    public void setComboTurnos(SelectItem[] comboTurnos) {
        this.comboTurnos = comboTurnos;
    }

    public SelectItem[] getComboAulas() {
        return comboAulas;
    }

    public void setComboAulas(SelectItem[] comboAulas) {
        this.comboAulas = comboAulas;
    }

    public SelectItem[] getComboTipos() {
        return comboTipos;
    }

    public void setComboTipos(SelectItem[] comboTipos) {
        this.comboTipos = comboTipos;
    }

    public int getMpAulaId() {
        return this.mpAulaId;
    }

    public void setMpAulaId(int mpAulaId) {
        this.mpAulaId = mpAulaId;
    }

    public String getMpTipoId() {
        return this.mpTipoId;
    }

    public void setMpTipoId(String mpTipoId) {
        this.mpTipoId = mpTipoId;
    }

    public int getMpRowIndexIdLun() {
        return mpRowIndexIdLun;
    }

    public void setMpRowIndexIdLun(int mpRowIndexIdLun) {
        this.mpRowIndexIdLun = mpRowIndexIdLun;
    }

    public int getMpRowIndexIdMar() {
        return mpRowIndexIdMar;
    }

    public void setMpRowIndexIdMar(int mpRowIndexIdMar) {
        this.mpRowIndexIdMar = mpRowIndexIdMar;
    }

    public int getMpRowIndexIdMie() {
        return mpRowIndexIdMie;
    }

    public void setMpRowIndexIdMie(int mpRowIndexIdMie) {
        this.mpRowIndexIdMie = mpRowIndexIdMie;
    }

    public int getMpRowIndexIdJue() {
        return mpRowIndexIdJue;
    }

    public void setMpRowIndexIdJue(int mpRowIndexIdJue) {
        this.mpRowIndexIdJue = mpRowIndexIdJue;
    }

    public int getMpRowIndexIdVie() {
        return mpRowIndexIdVie;
    }

    public void setMpRowIndexIdVie(int mpRowIndexIdVie) {
        this.mpRowIndexIdVie = mpRowIndexIdVie;
    }

    public int getMpRowIndexIdSab() {
        return mpRowIndexIdSab;
    }

    public void setMpRowIndexIdSab(int mpRowIndexIdSab) {
        this.mpRowIndexIdSab = mpRowIndexIdSab;
    }

    public int getMpRowIndexIdDom() {
        return mpRowIndexIdDom;
    }

    public void setMpRowIndexIdDom(int mpRowIndexIdDom) {
        this.mpRowIndexIdDom = mpRowIndexIdDom;
    }

    public String armarEstructura() {
        var b = 0;

        var estructuraFacultades = new StringBuilder();
        var estructuraEspecialidades = new StringBuilder();
        var estructuraSemestres = new StringBuilder();
        var estructuraCurape = new StringBuilder();
        HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        List<AcSemestre> listaSemestres = daoSemestre.seleccionarSemestresVigentes();
        HSFacultadDAO daoFacultad = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        List<AcFacultad> listaFacultad = daoFacultad.seleccionarFacultad("", "");

        HSAperturaCursosDAO daoCursoAperturado = ServiceFinder.findBean("springHibernateDaoCursoAperturado", HSAperturaCursosDAO.class);
        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);

        // Prefetch everything the loops need, to avoid the N+1: one query per (especialidad x
        // semestre) and one catalog query per course collapse into the ciclo catalog loaded once
        // into a map, plus all opened courses for the vigent semesters fetched once and grouped by
        // (especialidad, semestre). Same data, same order -> the structure string is byte-identical.
        Map<String, String> ciclos = new HashMap<>();
        for (TbCatalogo cat : daoCatalogo.seleccionarCatalogo("006")) {
            ciclos.put(cat.getCatCodigoGrupo() + cat.getCatCodigoElemento(), cat.getCatDescripcionElemento());
        }

        List<Integer> semestreIds = new ArrayList<>();
        for (AcSemestre semestre : listaSemestres) {
            semestreIds.add(semestre.getId());
        }
        Map<Integer, Map<Integer, List<OpenedCourseRow>>> curapePorEspPorSem = new HashMap<>();
        for (OpenedCourseRow row : daoCursoAperturado.findOpenedCoursesBySemesters(semestreIds)) {
            curapePorEspPorSem
                    .computeIfAbsent(row.getEspecialidadId(), k -> new HashMap<>())
                    .computeIfAbsent(row.getSemestreId(), k -> new ArrayList<>())
                    .add(row);
        }

        List<ObjNodeGnral> tmpNodos = new ArrayList<>();
        for (var i = 0; i < listaFacultad.size(); i++) {
            estructuraFacultades.append(i + 1).append("=").append(listaFacultad.get(i).getFacNombre()).append("\n");
            Set<AcEspecialidad> esp = listaFacultad.get(i).getAcEspecialidads();
            List<AcEspecialidad> listaEspecialidad = Collections.synchronizedList(new LinkedList<>(esp));

            for (var j = 0; j < listaEspecialidad.size(); j++) {
                estructuraEspecialidades.append(i + 1).append(".").append(j + 1).append("=").append(listaEspecialidad.get(j).getEspNombre()).append("\n");

                for (var w = 0; w < listaSemestres.size(); w++) {
                    estructuraSemestres.append(i + 1).append(".").append(j + 1).append(".").append(w + 1).append("=").append(listaSemestres.get(w).getSemNombre()).append("\n");
                    int f = listaEspecialidad.get(j).getId();
                    Map<Integer, List<OpenedCourseRow>> curapePorSem = curapePorEspPorSem.get(f);
                    List<OpenedCourseRow> listaCursoAperturado = curapePorSem == null
                            ? Collections.emptyList()
                            : curapePorSem.getOrDefault(listaSemestres.get(w).getId(), Collections.emptyList());

                    List<Integer> ultimosDigitos = new ArrayList<>();
                    ultimosDigitos.add(-1);
                    var contador = 0;
                    for (OpenedCourseRow row : listaCursoAperturado) {
                        int m = contador;
                        var a = aInteger(row.getPlancurCiclo().substring(5, 6));
                        int y = verificarIndice(a, ultimosDigitos);
                        estructuraCurape.append(i + 1).append(".").append(j + 1).append(".").append(w + 1).append(".").append(y).append("=").append(ciclos.getOrDefault(row.getPlancurCiclo(), "VALOR NO REGISTRADO")).append("\n");
                        if (b != y) {
                            m = 0;
                            contador = 0;
                        }
                        estructuraCurape.append(i + 1).append(".").append(j + 1).append(".").append(w + 1).append(".").append(y).append(".").append(m + 1).append("=").append(row.getCursoNombre()).append("\n");
                        b = y;
                        var o = new ObjNodeGnral();
                        o.setId((i + 1) + ":" + (j + 1) + ":" + (w + 1) + ":" + y + ":" + (m + 1));
                        o.setCurapeId(row.getCurapeId());
                        o.setCurNom(row.getCursoNombre());
                        o.setSemId(listaSemestres.get(w).getId());
                        tmpNodos.add(o);
                        contador++;
                    }
                }
            }
        }
        String estructura = estructuraFacultades + estructuraEspecialidades.toString() + estructuraSemestres + estructuraCurape;
        this.setListaSecciones(null);
        this.setNodos(new ArrayList<>(tmpNodos));
        return estructura;
    }

    private ObjNodeGnral buscarNodo(String id) {
        return nodos.stream().filter(nodo -> nodo.getId().equalsIgnoreCase(id))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public void seleccion(NodeSelectedEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UITree tree = (UITree) event.getComponent();
        var rowKey = tree.getRowKey().toString();

        int length = rowKey.length();
        if (length >= 9) {
            ObjNodeGnral nodo = buscarNodo(rowKey);

            int idCurape = nodo.getCurapeId();
            int idSemape = nodo.getSemId();
            String nombreCurape = nodo.getCurNom();
            mostrarTablaSecciones(idCurape, idSemape, nombreCurape);
        }
    }

    public void mostrarTablaSecciones(int idCurape, int idSemape, String nombreCurape) {
        LOG.debug("Id y nombre del curso {} - '{}' | Id del Semestre {}", idCurape, nombreCurape, idSemape);
        HSSeccionDAO daoSeccion = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);

        List<Object[]> lista = daoSeccion.seleccionarSecciones(idCurape);
        List<MbHorario> horarios = new ArrayList<>();
        for (Object[] acSeccion : lista) {
            var horario = new MbHorario();
            horario.setRowIdSec(Integer.parseInt(acSeccion[0].toString()));
            horario.setRowCodigoSec(acSeccion[1].toString());
            horario.setRowNombreSec(acSeccion[2].toString());
            horario.setRowVacantesSec(Integer.parseInt(acSeccion[3].toString()));
            horario.setRowVacantesOcupadas(acSeccion[4].toString());
            horario.setRowIdSem(idSemape);

            horarios.add(horario);
        }
        this.setLblCursoAperturado(nombreCurape);
        this.setListaSecciones(horarios);
    }

    public void asignarHorario(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_seccion");
        UIParameter idSemestre = (UIParameter) event.getComponent().findComponent("id_semestre");

        rowIdSec = aInteger(id.getValue().toString());
        rowIdSem = aInteger(idSemestre.getValue().toString());

        LOG.debug("Seccion Id Aux {} | Semestre Id Aux {}", rowIdSec, rowIdSem);

        limpiar();

        List<AcTurno> turnos = daoTurno.seleccionarTurno(rowIdSem);
        setComboTurnos(turnos.stream()
                .map(t -> new SelectItem(t.getId(), t.getTurNombre()))
                .toArray(SelectItem[]::new));
    }

    public void limpiar() {
        LOG.debug("Cleaning variables");
        tablaHorario = null;
        listaHorario.clear();
        this.mpAulaId = 0;
        this.mpDocenteId = 0;
        this.mpTurnoId = 0;
        this.setIde(0);
    }

    private void mostrarTablaHorarios() {
        HSTurnoDetalleDAO daoTurnoDetalle = ServiceFinder.findBean("springHibernateDaoTurnoDetalle", HSTurnoDetalleDAO.class);
        int turIdAux = this.getMpTurnoId();
        LOG.debug("Para el turno: {}", turIdAux);
        List<AcTurnoDetalle> lista = daoTurnoDetalle.seleccionarTurnoDetalle(turIdAux);

        AcTurno tmpTurno = daoTurno.obtenerTurno(turIdAux);

        int tiempo = tmpTurno == null ? 0 : tmpTurno.getTurTiempoPeriodo();

        List<MbHorario> tmpHorarios = new ArrayList<>();

        for (AcTurnoDetalle o : lista) {
            var horario = new MbHorario();
            horario.setMpRowIdHor(o.getId());
            horario.setMpRowInicioHor(o.getTurdetHora());

            var calendar = Calendar.getInstance();
            calendar.setTime(o.getTurdetHora());
            calendar.add(Calendar.MINUTE, tiempo);

            horario.setMpRowFinHor(calendar.getTime());
            llenarDias(horario, o.getId(), rowIdSec);
            tmpHorarios.add(horario);
        }
        this.setTablaHorario(tmpHorarios);
    }

    private void llenarDias(MbHorario horario, Integer idTurno, int seccion) {
        LOG.debug("Id del turno detalle {} | seccion: {}", idTurno, seccion);
        List<AcHorario> horarios = daoHorario.seleccionarHorario(seccion);
        for (AcHorario acHorario : horarios) {
            if (acHorario.getTurdet().getId().compareTo(idTurno) == 0) {
                String dias = acHorario.getHorDia();
                String mensaje = "id: " + acHorario.getId()
                        + " doc: " + acHorario.getDoc().getDocNombre()
                        + " aul:" + acHorario.getAul().getAulDes()
                        + " sec:" + acHorario.getSec().getSecNombre()
                        + " tur:" + acHorario.getTurdet().getId()
                        + " tipo:" + acHorario.getHorTipoClase();

                if (dias.equals("016001")) {
                    horario.setMpRowHorLun(mensaje);
                    horario.setMpRowIndexIdLun(acHorario.getId());
                }
                if (dias.equals("016002")) {
                    horario.setMpRowHorMar(mensaje);
                    horario.setMpRowIndexIdMar(acHorario.getId());
                }
                if (dias.equals("016003")) {
                    horario.setMpRowHorMie(mensaje);
                    horario.setMpRowIndexIdMie(acHorario.getId());
                }
                if (dias.equals("016004")) {
                    horario.setMpRowHorJue(mensaje);
                    horario.setMpRowIndexIdJue(acHorario.getId());
                }
                if (dias.equals("016005")) {
                    horario.setMpRowHorVie(mensaje);
                    horario.setMpRowIndexIdVie(acHorario.getId());
                }
                if (dias.equals("016006")) {
                    horario.setMpRowHorSab(mensaje);
                    horario.setMpRowIndexIdSab(acHorario.getId());
                }
                if (dias.equals("016007")) {
                    horario.setMpRowHorDom(mensaje);
                    horario.setMpRowIndexIdDom(acHorario.getId());
                }
            }
        }
    }

    public List<MbHorario> getTablaHorario() {
        return tablaHorario;
    }

    public void setTablaHorario(List<MbHorario> tablaHorario) {
        this.tablaHorario = tablaHorario;
    }

    private BeanHoraria build(ActionEvent event) {
        UIParameter uipIdTurnoDetalle = (UIParameter) event.getComponent().findComponent("param_id_turno");
        UIParameter uipIdAula = (UIParameter) event.getComponent().findComponent("ho_aula");
        UIParameter uipIdTipo = (UIParameter) event.getComponent().findComponent("ho_tipo");
        UIParameter uipIdDocente = (UIParameter) event.getComponent().findComponent("ho_docente");

        var turnoDetalleIdAux = aInteger(uipIdTurnoDetalle.getValue().toString());
        var aulaIdAux = aInteger(uipIdAula.getValue().toString());
        var tipoIdAux = uipIdTipo.getValue().toString();
        var docenteIdAux = aInteger(uipIdDocente.getValue().toString());

        LOG.debug("identificador: {} | aula: {} | tipo: {} | doc: {}", ide, aulaIdAux, tipoIdAux, docenteIdAux);

        return new BeanHoraria(turnoDetalleIdAux, aulaIdAux, tipoIdAux, docenteIdAux);
    }

    public void seleccionarLunes(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIOutput lunes = (UIOutput) event.getComponent().findComponent("lunes");

        var bh = build(event);

        if (lunes.getValue().toString().isEmpty()) {
            int agregar = agregarHorario(ide, "016001", bh, rowIdSec, "1");
            if (agregar == 1) {
                this.setMpRowIndexIdLun(ide + 1);
                this.setMpRowHorLun(buildDetalleAsignacionHorario(bh));
            }
        } else {
            this.setMpRowHorLun("");
            quitarHorario(event, 1);
        }
    }

    public void seleccionarMartes(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIOutput martes = (UIOutput) event.getComponent().findComponent("martes");

        var bh = build(event);

        if (martes.getValue().toString().equals("")) {
            int agregar = agregarHorario(ide, "016002", bh, rowIdSec, "1");
            if (agregar == 1) {
                this.setMpRowIndexIdMar(ide + 1);
                this.setMpRowHorMar(buildDetalleAsignacionHorario(bh));
            }
        } else {
            this.setMpRowHorMar("");
            quitarHorario(event, 2);
        }
    }

    public void seleccionarMiercoles(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIOutput miercoles = (UIOutput) event.getComponent().findComponent("miercoles");

        var bh = build(event);

        if (miercoles.getValue().toString().equals("")) {
            int agregar = agregarHorario(ide, "016003", bh, rowIdSec, "1");
            if (agregar == 1) {
                this.setMpRowIndexIdMie(ide + 1);
                this.setMpRowHorMie(buildDetalleAsignacionHorario(bh));
            }
        } else {
            this.setMpRowHorMie("");
            quitarHorario(event, 3);
        }
    }

    public void seleccionarJueves(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIOutput jueves = (UIOutput) event.getComponent().findComponent("jueves");

        var bh = build(event);

        if (jueves.getValue().toString().equals("")) {
            int agregar = agregarHorario(ide, "016004", bh, rowIdSec, "1");
            if (agregar == 1) {
                this.setMpRowIndexIdJue(ide + 1);
                this.setMpRowHorJue(buildDetalleAsignacionHorario(bh));
            }
        } else {
            this.setMpRowHorJue("");
            quitarHorario(event, 4);
        }
    }

    public void seleccionarViernes(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIOutput viernes = (UIOutput) event.getComponent().findComponent("viernes");

        var bh = build(event);

        if (viernes.getValue().toString().equals("")) {
            int agregar = agregarHorario(ide, "016005", bh, rowIdSec, "1");
            if (agregar == 1) {
                this.setMpRowIndexIdVie(ide + 1);
                this.setMpRowHorVie(buildDetalleAsignacionHorario(bh));
            }
        } else {
            this.setMpRowHorVie("");
            quitarHorario(event, 5);
        }
    }

    public void seleccionarSabado(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIOutput sabado = (UIOutput) event.getComponent().findComponent("sabado");

        var bh = build(event);

        if (sabado.getValue().toString().equals("")) {
            int agregar = agregarHorario(ide, "016006", bh, rowIdSec, "1");
            if (agregar == 1) {
                this.setMpRowIndexIdSab(ide + 1);
                this.setMpRowHorSab(buildDetalleAsignacionHorario(bh));
            }
        } else {
            this.setMpRowHorSab("");
            quitarHorario(event, 6);
        }
    }

    public void seleccionarDomingo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIOutput domingo = (UIOutput) event.getComponent().findComponent("domingo");

        var bh = build(event);

        if (domingo.getValue().toString().equals("")) {
            int agregar = agregarHorario(ide, "016007", bh, rowIdSec, "1");
            if (agregar == 1) {
                this.setMpRowIndexIdDom(ide + 1);
                this.setMpRowHorDom(buildDetalleAsignacionHorario(bh));
            }
        } else {
            this.setMpRowHorDom("");
            quitarHorario(event, 7);
        }
    }

    private String buildDetalleAsignacionHorario(BeanHoraria bh) {
        return "ide: " + (ide + 1)
                + "<br> doc :" + bh.docenteId + " aul :" + bh.aulaId
                + "<br> tur :" + bh.turnoDetalleId + " sec :" + rowIdSec
                + "<br> tipo :" + bh.tipoHorario;
    }

    private int agregarHorario(int identificador, String dia, BeanHoraria bh, int seccion, String activo) {
        if (bh.aulaId == 0) {
            this.setOncomplete("alert('Ingrese Aula.')");
            return 0;
        } else if (bh.docenteId == 0) {
            this.setOncomplete("alert('Ingrese Docente.')");
            return 0;
        } else {
            var horario = new AcHorario();
            var doc = new AcDocente();
            doc.setId(bh.docenteId);
            var aul = new AcAula();
            aul.setId(bh.aulaId);
            var sec = new AcSeccion();
            sec.setId(seccion);
            var turdet = new AcTurnoDetalle();
            turdet.setId(bh.turnoDetalleId);
            horario.setId(identificador);
            horario.setAul(aul);
            horario.setHorActivo(activo);
            horario.setHorDia(dia);
            horario.setSec(sec);
            horario.setTurdet(turdet);
            horario.setHorCreacion(new Date());
            horario.setHorTipoClase(bh.tipoHorario);
            horario.setDoc(doc);
            listaHorario.add(horario);
            LOG.debug("identificador: {} | ide: {} | size: {}", identificador, ide, listaHorario.size());
            ide--;
            this.setOncomplete("");
            return 1;
        }
    }

    public void quitarHorario(ActionEvent event, int dia) {
        UIParameter uipIdDay;
        switch (dia) {
            case 1:
                uipIdDay = (UIParameter) event.getComponent().findComponent("param_identificador_lunes");
                break;
            case 2:
                uipIdDay = (UIParameter) event.getComponent().findComponent("param_identificador_martes");
                break;
            case 3:
                uipIdDay = (UIParameter) event.getComponent().findComponent("param_identificador_miercoles");
                break;
            case 4:
                uipIdDay = (UIParameter) event.getComponent().findComponent("param_identificador_jueves");
                break;
            case 5:
                uipIdDay = (UIParameter) event.getComponent().findComponent("param_identificador_viernes");
                break;
            case 6:
                uipIdDay = (UIParameter) event.getComponent().findComponent("param_identificador_sabado");
                break;
            default:
                uipIdDay = (UIParameter) event.getComponent().findComponent("param_identificador_domingo");
                break;
        }

        if (aInteger(uipIdDay.getValue().toString()) > 0) {
            AcHorario hora = daoHorario.seleccionarUnHorario(aInteger(uipIdDay.getValue().toString()));
            var bh = new BeanHoraria(
                    hora.getTurdet().getId(), hora.getAul().getId(), hora.getHorTipoClase(), hora.getDoc().getId());
            agregarHorario(hora.getId(), hora.getHorDia(), bh, hora.getSec().getId(), "0");
        }
        if (aInteger(uipIdDay.getValue().toString()) <= 0) {
            listaHorario.remove(encontrarIndice(aInteger(uipIdDay.getValue().toString())));
        }
    }

    private int encontrarIndice(int identificador) {
        for (var i = 0; i < listaHorario.size(); i++) {
            if (listaHorario.get(i).getId() == identificador) {
                return i;
            }
        }
        return -1;
    }

    public void insertar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        List<AcHorario> lHorarios = new ArrayList<>();
        AcHorario horario;

        for (MbHorario mb : tablaHorario) {
            listaHorario.addAll(mb.getListaHorario());
        }

        for (AcHorario hor : listaHorario) {
            horario = new AcHorario();
            if (hor.getId() > 0) {
                horario.setId(hor.getId());
            }
            horario.setAul(hor.getAul());
            horario.setDoc(hor.getDoc());
            horario.setHorActivo(hor.getHorActivo());
            horario.setHorTipoClase(hor.getHorTipoClase());
            horario.setHorDia(hor.getHorDia());
            horario.setTurdet(hor.getTurdet());
            horario.setSec(hor.getSec());
            horario.setHorCreacion(hor.getHorCreacion());
            lHorarios.add(horario);
        }

        daoHorario.insertarHorarios(lHorarios);
    }

    public void setearTurno(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        mostrarTablaHorarios();
    }

    public String getRowVacantesOcupadas() {
        return rowVacantesOcupadas;
    }

    public void setRowVacantesOcupadas(String rowVacantesOcupadas) {
        this.rowVacantesOcupadas = rowVacantesOcupadas;
    }

    private int aInteger(String cadena) {
        return Integer.parseInt(cadena.trim());
    }

    private static class BeanHoraria {
        final int turnoDetalleId;
        final int aulaId;
        final String tipoHorario;
        final int docenteId;

        public BeanHoraria(int turnoDetalleId, int aulaId, String tipoHorario, int docenteId) {
            this.turnoDetalleId = turnoDetalleId;
            this.aulaId = aulaId;
            this.tipoHorario = tipoHorario;
            this.docenteId = docenteId;
        }
    }
}
