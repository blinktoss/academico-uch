package net.uch.academica.jsf;

import net.uch.academica.dao.*;
import net.uch.academica.jsf.WeightedGradeCalculator.GradeEntry;
import net.uch.academica.jsf.beans.*;
import net.uch.academica.service.GradeRecordAlreadyPublishedException;
import net.uch.academica.service.GradeRecordService;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

public class MbNota extends AbstractBaseMbViewTree {

    private static final Logger LOG = LoggerFactory.getLogger(MbNota.class);

    private static final String ATTR_SECCION = "seccion";

    private final HSActaDAO daoActa = ServiceFinder.findBean("springHibernateDaoActa", HSActaDAO.class);
    private final HSAperturaCursosDAO daoCursoAperturado = ServiceFinder.findBean("springHibernateDaoCursoAperturado", HSAperturaCursosDAO.class);
    private final HSMatriculaDAO daoMatricula = ServiceFinder.findBean("springHibernateDaoMatricula", HSMatriculaDAO.class);
    private final HSNotaDAO daoNota = ServiceFinder.findBean("springHibernateDaoNota", HSNotaDAO.class);
    private final HSSistemaEvaluacionDAO daoSistemaEvaluacion = ServiceFinder.findBean("springHibernateDaoSistemaEvaluacion", HSSistemaEvaluacionDAO.class);
    private final HSImportacionNotasDAO daoImportacionNotas = ServiceFinder.findBean("springHibernateDaoImportacionNotas", HSImportacionNotasDAO.class);
    private final HSNotasTemporalesDAO daoNotasTemporales = ServiceFinder.findBean("springHibernateDaoNotasTemporales", HSNotasTemporalesDAO.class);
    private final GradeRecordService gradeRecordService = ServiceFinder.findBean("gradeRecordService", GradeRecordService.class);

    private List<MbNota> listaNotas;
    private int secId;
    private String teacherName;
    private String secCodigo;
    private String secNombre;
    private int secVacantes;
    private String oncomplete;
    private int nodeCurapeId;
    private String nodeCurapeDesc;
    private int nroOrden;
    private List<String> cabecerasNotas;
    private List<BeanNotas> registroNotas;
    private String nombreActa;
    private String codigoActa;
    private String numeroActa;
    // Acta Sustitutoria grid: dynamic columns (headers) + one row per student.
    private List<String> substituteHeaders;
    private List<SubstituteActRow> substituteActRows;
    // Computed principal-acta data (per student) used to persist the published record.
    private List<PublishedActaRow> actaPublishRows;
    private int alumnoId;
    private String alumnoCodigoFinal;
    private String alumnoNombreFinal;
    private String finales;
    private String finalFinales;
    //persistir valor de Seleccion
    private List<ObjNodeGnral> nodos;
    private List<MbNota> mpSistemaEvaluacion;
    private int mpSisevaPerId;
    private String mpSisevaPerCodigo;
    private String mpSisevaPerDescripcion;
    private boolean mpMarcar;
    private SelectItem[] cboRadioSisEva;
    private List<MbNota> listarAlumnoNota;

    /**
     * Live grades: student id -> (component/personalizado id -> grade).
     */
    private static Map<Integer, Map<Integer, Float>> gradesFromNotas(List<AcNota> grades) {
        Map<Integer, Map<Integer, Float>> byStudent = new HashMap<>();
        for (AcNota nota : grades) {
            byStudent.computeIfAbsent(nota.getAlu().getId(), k -> new HashMap<>())
                    .put(nota.getSisevaPer().getId(), nota.getNotNota().floatValue());
        }
        return byStudent;
    }

    /**
     * Frozen grades from a published Acta: the PROMEDIO_PARCIAL components keyed by personalizado id.
     */
    private static Map<Integer, Map<Integer, Float>> gradesFromActa(AcActa acta) {
        Map<Integer, Map<Integer, Float>> byStudent = new HashMap<>();
        for (AcActaDetalle detalle : acta.getAcActaDetalles()) {
            if (detalle.getSisevaDetalleId() != null && detalle.getSisevaDetalleId() != 0
                    && CAT_TIPO_NOTA_PROMEDIO_PARCIAL.equals(detalle.getActdetTipoNota())) {
                byStudent.computeIfAbsent(detalle.getAluId(), k -> new HashMap<>())
                        .put(detalle.getSisevaDetalleId(), detalle.getActdetNota());
            }
        }
        return byStudent;
    }

    /**
     * Display helper: whole numbers without a decimal, others rounded to one decimal place.
     */
    private static String formatGrade(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((int) value);
        }
        return String.valueOf(Math.round(value * 10.0) / 10.0);
    }

    private static String gradeCellStyle(double grade) {
        String color = grade < GradePolicy.PASSING_GRADE ? "red" : "blue";
        return "width:25px;color: " + color + ";background-color: #ffffff";
    }

    private static float[] toFloatArray(List<Float> values) {
        var array = new float[values.size()];
        for (var i = 0; i < array.length; i++) {
            array[i] = values.get(i);
        }
        return array;
    }

    /**
     * Weighted entries for a substitute row's replaceable (numeric) component grades.
     */
    private static List<GradeEntry> replaceableEntries(SubstituteActRow row) {
        List<GradeEntry> entries = new ArrayList<>();
        for (SubstituteComponent component : row.getComponents()) {
            if (component.isReplaceable()) {
                entries.add(new GradeEntry(Float.parseFloat(component.getValue()),
                        component.getComponentWeight(), component.getGroupId(), component.getGroupWeight()));
            }
        }
        return entries;
    }

    private static List<GradeEntry> replaceEntryGrade(List<GradeEntry> entries, int index, float newGrade) {
        List<GradeEntry> copy = new ArrayList<>(entries);
        if (index >= 0 && index < copy.size()) {
            copy.set(index, copy.get(index).withGrade(newGrade));
        }
        return copy;
    }

    private static AcActaDetalle componentDetalle(AcActa acta, int aluId, float nota, String tipoNota, int sisevaDetalleId) {
        var detalle = new AcActaDetalle();
        detalle.setActdetNota(nota);
        detalle.setActdetTipoNota(tipoNota);
        detalle.setAluId(aluId);
        detalle.setAct(acta);
        detalle.setSisevaDetalleId(sisevaDetalleId);
        detalle.setActdetActivo("1");
        return detalle;
    }

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        setListarAlumnoNota(new ArrayList<>());
        setListaNotas(new ArrayList<>());
        setCabecerasNotas(new ArrayList<>());
        setRegistroNotas(new ArrayList<>());
        setSubstituteHeaders(new ArrayList<>());
        setSubstituteActRows(new ArrayList<>());
        setActaPublishRows(new ArrayList<>());
        setTeacherName("");
        setMpSistemaEvaluacion(new ArrayList<>());
        setCboRadioSisEva(new SelectItem[0]);
    }

    public List<MbNota> getListarAlumnoNota() {
        return listarAlumnoNota;
    }

    public void setListarAlumnoNota(List<MbNota> listarAlumnoNota) {
        this.listarAlumnoNota = listarAlumnoNota;
    }

    public SelectItem[] getCboRadioSisEva() {
        return cboRadioSisEva;
    }

    public void setCboRadioSisEva(SelectItem[] cboRadioSisEva) {
        this.cboRadioSisEva = cboRadioSisEva;
    }

    public boolean isMpMarcar() {
        return mpMarcar;
    }

    public void setMpMarcar(boolean mpMarcar) {
        this.mpMarcar = mpMarcar;
    }

    public String getMpSisevaPerCodigo() {
        return mpSisevaPerCodigo;
    }

    public void setMpSisevaPerCodigo(String mpSisevaPerCodigo) {
        this.mpSisevaPerCodigo = mpSisevaPerCodigo;
    }

    public String getMpSisevaPerDescripcion() {
        return mpSisevaPerDescripcion;
    }

    public void setMpSisevaPerDescripcion(String mpSisevaPerDescripcion) {
        this.mpSisevaPerDescripcion = mpSisevaPerDescripcion;
    }

    public int getMpSisevaPerId() {
        return mpSisevaPerId;
    }

    public void setMpSisevaPerId(int mpSisevaPerId) {
        this.mpSisevaPerId = mpSisevaPerId;
    }

    public List<MbNota> getMpSistemaEvaluacion() {
        return mpSistemaEvaluacion;
    }

    public void setMpSistemaEvaluacion(List<MbNota> mpSistemaEvaluacion) {
        this.mpSistemaEvaluacion = mpSistemaEvaluacion;
    }

    public List<ObjNodeGnral> getNodos() {
        return nodos;
    }

    public void setNodos(List<ObjNodeGnral> nodos) {
        this.nodos = nodos;
    }

    public int getSecId() {
        return secId;
    }

    public void setSecId(int secId) {
        this.secId = secId;
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

    public int getSecVacantes() {
        return secVacantes;
    }

    public void setSecVacantes(int secVacantes) {
        this.secVacantes = secVacantes;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public int getNodeCurapeId() {
        return nodeCurapeId;
    }

    public void setNodeCurapeId(int nodeCurapeId) {
        this.nodeCurapeId = nodeCurapeId;
    }

    public String getNodeCurapeDesc() {
        return nodeCurapeDesc;
    }

    public void setNodeCurapeDesc(String nodeCurapeDesc) {
        this.nodeCurapeDesc = nodeCurapeDesc;
    }

    public int getNroOrden() {
        return nroOrden;
    }

    public void setNroOrden(int nroOrden) {
        this.nroOrden = nroOrden;
    }

    public List<String> getCabecerasNotas() {
        return cabecerasNotas;
    }

    public void setCabecerasNotas(List<String> cabecerasNotas) {
        this.cabecerasNotas = cabecerasNotas;
    }

    public List<BeanNotas> getRegistroNotas() {
        return registroNotas;
    }

    public void setRegistroNotas(List<BeanNotas> registroNotas) {
        this.registroNotas = registroNotas;
    }

    public String armarEstructura() {
        var b = 0;
        var estructuraFacultades = new StringBuilder();
        var estructuraEspecialidades = new StringBuilder();
        var estructuraSemestres = new StringBuilder();
        var estructuraCurape = new StringBuilder();
        HSFacultadDAO daoFacultad = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        List<AcFacultad> lFacultad = daoFacultad.seleccionarFacultad("", "");
        List<AcSemestre> listaSemestres = daoSemestre.seleccionarSemestresVigentes();
        var ciclos = new HashMap<String, String>();
        daoCatalogo.seleccionarCatalogo("006")
                .forEach(cat -> ciclos.put(cat.getCatCodigoGrupo() + cat.getCatCodigoElemento(), cat.getCatDescripcionElemento()));

        // Prefetch all opened courses for the vigent semesters in one query and group them by
        // (especialidad, semestre), instead of one query per (especialidad x semestre) inside the
        // loop (the same N+1 already fixed in MbHorario). Same data, same order -> the structure
        // string stays byte-identical. Reuses HSAperturaCursosDAO.findOpenedCoursesBySemesters.
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

        List<ObjNodeGnral> lNodo = new ArrayList<>();
        for (var i = 0; i < lFacultad.size(); i++) {
            AcFacultad facultad = lFacultad.get(i);
            estructuraFacultades.append(i + 1).append("=").append(facultad.getFacNombre()).append("\n");
            LOG.trace("---------------------------------------------------------------------- ESPECIALIDAD");
            Set<AcEspecialidad> esp = facultad.getAcEspecialidads();
            List<AcEspecialidad> lEspecialidad = Collections.synchronizedList(new LinkedList<>(esp));
            for (var j = 0; j < lEspecialidad.size(); j++) {
                AcEspecialidad especialidad = lEspecialidad.get(j);
                estructuraEspecialidades.append(i + 1).append(".").append(j + 1).append("=").append(especialidad.getEspNombre()).append("\n");
                LOG.trace("###################################################################### SEMESTRES");
                for (var w = 0; w < listaSemestres.size(); w++) {
                    AcSemestre semestre = listaSemestres.get(w);
                    estructuraSemestres.append(i + 1).append(".").append(j + 1).append(".").append(w + 1).append("=").append(semestre.getSemNombre()).append("\n");
                    int f = especialidad.getId();
                    LOG.trace("********************************************************************** CURSOS APERTURADOS");
                    Map<Integer, List<OpenedCourseRow>> curapePorSem = curapePorEspPorSem.get(f);
                    List<OpenedCourseRow> lis = curapePorSem == null
                            ? Collections.emptyList()
                            : curapePorSem.getOrDefault(semestre.getId(), Collections.emptyList());
                    List<Integer> ultimosDigitos = new ArrayList<>();
                    ultimosDigitos.add(-1);
                    var contado = 0;
                    for (OpenedCourseRow row : lis) {
                        int m = contado;
                        var a = Integer.parseInt(row.getPlancurCiclo().substring(5, 6));
                        int y = verificarIndice(a, ultimosDigitos);
                        LOG.trace("---------------------------------------------------------------------- CICLO");
                        estructuraCurape.append(i + 1).append(".").append(j + 1).append(".").append(w + 1).append(".").append(y).append("=").append(ciclos.get(row.getPlancurCiclo())).append("\n");
                        if (b != y) {
                            m = 0;
                            contado = 0;
                        }
                        LOG.trace("###################################################################### CURSO");
                        estructuraCurape.append(i + 1).append(".").append(j + 1).append(".").append(w + 1).append(".").append(y).append(".").append(m + 1).append("=").append(row.getCursoNombre()).append("\n");
                        b = y;
                        var o = new ObjNodeGnral();
                        o.setId((i + 1) + ":" + (j + 1) + ":" + (w + 1) + ":" + y + ":" + (m + 1));
                        o.setCurapeId(row.getCurapeId());
                        o.setCurNom(row.getCursoNombre());
                        o.setSemId(semestre.getId());
                        lNodo.add(o);
                        contado++;
                    }
                }
            }
        }
        this.setNodos(new ArrayList<>(lNodo));
        String estructura = estructuraFacultades + estructuraEspecialidades.toString() + estructuraSemestres + estructuraCurape;
        this.listaNotas.clear();
        return estructura;
    }

    private ObjNodeGnral buscarNodo(String id) {
        LOG.debug("buscando nodo {}", id);
        return nodos.stream().filter(nodo -> nodo.getId().equalsIgnoreCase(id))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public void seleccion(NodeSelectedEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UITree tree = (UITree) event.getComponent();
        var a = tree.getRowKey().toString();

        int length = a.length();
        if (length >= 9) {
            ObjNodeGnral nodo = buscarNodo(a);

            setNodeCurapeId(nodo.getCurapeId());
            setNodeCurapeDesc(nodo.getCurNom());
            mostrarTabla();
        }
    }

    private void mostrarTabla() {
        LOG.debug("CURAPE_ID: {}", nodeCurapeId);
        HSSeccionDAO dao = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
        List<Object[]> li = dao.seleccionarSecciones(nodeCurapeId);
        listaNotas.clear();
        for (Object[] acSeccion : li) {
            var b = new MbNota();
            b.setSecId(Integer.parseInt(acSeccion[0].toString()));
            b.setSecCodigo(acSeccion[1].toString());
            b.setSecNombre(acSeccion[2].toString());
            b.setSecVacantes(Integer.parseInt(acSeccion[4].toString()));
            b.setNodeCurapeId(nodeCurapeId);
            listaNotas.add(b);
        }
        LOG.info("section size {}", listaNotas.size());
    }

    public List<MbNota> getListaNotas() {
        return listaNotas;
    }

    public void setListaNotas(List<MbNota> listaNotas) {
        this.listaNotas = listaNotas;
    }

    public void asignarNotas(ActionEvent event) {
        LOG.debug("[{}][{}]", event.getPhaseId(), nodeCurapeId);
        MbNota row = (MbNota) event.getComponent().getAttributes().get(ATTR_SECCION);
        this.nodeCurapeId = row.getNodeCurapeId();
        this.secId = row.getSecId();

        // A published Acta freezes the grades: registering grades again must be blocked so the
        // official record stays immutable (corrections go through the Acta Sustitutoria).
        AcActa actaPublicada = daoActa.seleccionarActa(secId, CAT_TIPO_ACTA_PRINCIPAL);
        if (actaPublicada != null && ACT_STATUS_PUBLISHED.equals(actaPublicada.getActStatus())) {
            setOncomplete("alert('A Grade Record has already been published.')");
            return;
        }

        AcCursoAperturado curape = daoCursoAperturado.seleccionarUnCursosAperturado(nodeCurapeId);
        if (curape == null) {
            setOncomplete("alert('Este seccion no posee cursos Aperturados.');");
        } else {
            LOG.debug("Building headers for table of notes for section {}", secId);
            this.teacherName = seccionTeacherName(secId);
            List<AcMatricula> students = daoMatricula.seleccionarMatriculas(secId);

            List<AcSisEvaPersonalizado> evaluations = Collections.synchronizedList(
                    new LinkedList<>(curape.getAcSisEvaPersonalizados()));

            cabecerasNotas = evaluations.stream()
                    .map(AcSisEvaPersonalizado::getSisevaPerNombre).collect(Collectors.toList());

            List<AcNota> grades = daoNota.seleccionarNotas(secId);

            armarTablaNotas(evaluations, students, grades);

            setOncomplete("Richfaces.showModalPanel('mp',{top:40});");
        }
    }

    private void armarTablaNotas(List<AcSisEvaPersonalizado> evaluations, List<AcMatricula> students, List<AcNota> grades) {
        LOG.debug("Building body for table of notes columns: {} | rows: {} | grades: {}",
                evaluations.size(), students.size(), grades.size());

        String valorNota;
        int valorId;
        registroNotas = new ArrayList<>();
        for (var i = 0; i < students.size(); i++) {
            var bean = new BeanNotas();
            AcMatricula mat = students.get(i);
            bean.setStudentId(mat.getAlu().getId());
            bean.setStudent((i + 1) + ".- " + mat.getAlu().getAluAppaterno() + " " + mat.getAlu().getAluApmaterno() + " " + mat.getAlu().getAluNombres());
            for (var j = 0; j < evaluations.size(); j++) {
                AcSisEvaPersonalizado evaluation = evaluations.get(j);
                var input = new BeanNotas();

                valorNota = "0";
                valorId = 0;
                for (AcNota o : grades) {
                    if (o.getAlu().getId().toString().equals(mat.getAlu().getId().toString()) &&
                            o.getSisevaPer().getId().toString().equals(evaluation.getId().toString())) {
                        valorNota = o.getNotNota().toString();
                        valorId = o.getId();
                    }
                }
                input.setSisEvaluationId(evaluation.getId());
                input.setGradeId(valorId);
                if (j % 2 == 0) {
                    input.setStyle("width: 35px; background-color: #ffffff");
                } else {
                    input.setStyle("width: 35px; background-color: #bbbbbb");
                }
                input.setGradeValue(valorNota);
                var nota = Float.parseFloat(valorNota);
                if (nota < 10.5) {
                    input.setStyle("color: #cc0000;" + input.getStyle());
                } else {
                    input.setStyle("color: #000099;" + input.getStyle());
                }
                bean.getEvaluations().add(input);
            }
            registroNotas.add(bean);
        }
        LOG.info("Total of grades {}", registroNotas.size());
    }

    public void insertar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        // Backend grade validation: reject invalid input even if the frontend guard was bypassed.
        String invalid = firstInvalidGrade();
        if (invalid != null) {
            setOncomplete("alert('Nota invalida: \"" + invalid +
                    "\". Ingrese un valor entre 0 y 20 en incrementos de 0.5.')");
            return;
        }
        List<AcNota> notas = new ArrayList<>();
        for (BeanNotas acMatricula : this.registroNotas) {
            for (BeanNotas acSisEvaPersonalizado : acMatricula.getEvaluations()) {
                String valorNota = acSisEvaPersonalizado.getGradeValue();
                if (valorNota.trim().isEmpty()) {
                    valorNota = "0";
                }
                int idNota = acSisEvaPersonalizado.getGradeId();
                var nota = new AcNota();
                if (idNota != 0) {
                    nota.setId(idNota);
                }
                LOG.trace("Id nota {} :: {} [{}]", idNota, nota.getId(), valorNota);
                nota.setNotActivo("1");
                nota.setNotNota(BigDecimal.valueOf(Double.parseDouble(valorNota)));
                var alu = new AcAlumno();
                alu.setId(acMatricula.getStudentId());
                nota.setAlu(alu);
                var sisevaper = new AcSisEvaPersonalizado();
                sisevaper.setId(acSisEvaPersonalizado.getSisEvaluationId());
                nota.setSisevaPer(sisevaper);
                nota.setNotCreacion(new Date());
                nota.setSecId(this.secId);
                nota.setNotObservacion("BY_SISTEMA_ACADEMICO");
                notas.add(nota);
            }
        }
        daoNota.insertarOrActualizarNotas(notas);
        LOG.info("Grades saved/updated {}", notas.size());
        setOncomplete("alert('Se guardo el registro correctamente');Richfaces.hideModalPanel('mp')");
    }

    /**
     * @return the first non-empty grade in the registration grid that violates {@link GradePolicy}
     * (an empty cell means 0 and is allowed), or {@code null} when every grade is valid.
     */
    private String firstInvalidGrade() {
        for (BeanNotas student : this.registroNotas) {
            for (BeanNotas evaluation : student.getEvaluations()) {
                String value = evaluation.getGradeValue();
                if (value != null && !value.trim().isEmpty() && !GradePolicy.isValid(value)) {
                    return value.trim();
                }
            }
        }
        return null;
    }

    /**
     * @return the section's teacher full name (from its schedule), or {@code ""} when the section
     * has no schedule/teacher assigned yet.
     */
    private String seccionTeacherName(int sectionId) {
        HSHorarioDAO daoHorario = ServiceFinder.findBean("springHibernateDaoHorario", HSHorarioDAO.class);
        List<AcHorario> horarios = daoHorario.seleccionarHorario(sectionId);
        for (AcHorario horario : horarios) {
            AcDocente docente = horario.getDoc();
            if (docente != null) {
                return (docente.getDocAppaterno() + " " + docente.getDocApmaterno()
                        + " " + docente.getDocNombres()).trim();
            }
        }
        return "";
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void asignarNotasActas(ActionEvent event) {
        LOG.debug("[{}][{}]", event.getPhaseId(), nodeCurapeId);
        MbNota row = (MbNota) event.getComponent().getAttributes().get(ATTR_SECCION);
        this.nodeCurapeId = row.getNodeCurapeId();
        this.secId = row.getSecId();

        AcCursoAperturado curape = daoCursoAperturado.seleccionarUnCursosAperturado(nodeCurapeId);
        if (curape == null) {
            setOncomplete("alert('Esta seccion no posee cursos Aperturados.');");
            return;
        }
        LOG.debug("Building headers for minute of notes");

        AcActa actaPrincipal = daoActa.seleccionarActa(secId, CAT_TIPO_ACTA_PRINCIPAL);
        if (actaPrincipal == null) {
            HSSeccionDAO daoSeccion = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
            HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
            AcSeccion seccion = daoSeccion.seleccionarSeccion(secId);
            AcSemestre semestre = daoSemestre.getSemestre(curape.getSem().getId());
            this.setCodigoActa(GradeRecordNomenclature.code(semestre, seccion, CAT_TIPO_ACTA_PRINCIPAL));
            this.setNombreActa(GradeRecordNomenclature.name(semestre, seccion, CAT_TIPO_ACTA_PRINCIPAL));
            this.setNumeroActa(GradeRecordNomenclature.pendingNumber());
        } else {
            this.setCodigoActa(actaPrincipal.getActCodigo());
            this.setNombreActa(actaPrincipal.getActNombre());
            this.setNumeroActa(actaPrincipal.getActNumero());
        }

        List<AcMatricula> students = daoMatricula.seleccionarMatriculas(secId);
        List<AcSisEvaPersonalizado> evaluations = Collections.synchronizedList(
                new LinkedList<>(curape.getAcSisEvaPersonalizados()));

        // Immutability (issue #6): a published Acta is rendered from its OWN frozen grades, never
        // recomputed from the live ac_nota table, which may change after the record is published.
        Map<Integer, Map<Integer, Float>> gradesByStudent =
                (actaPrincipal != null && ACT_STATUS_PUBLISHED.equals(actaPrincipal.getActStatus()))
                        ? gradesFromActa(actaPrincipal)
                        : gradesFromNotas(daoNota.seleccionarNotas(secId));

        armarTablaNotasActas(curape, evaluations, students, gradesByStudent);

        setOncomplete("Richfaces.showModalPanel('mp2',{top:40});");
    }

    private void armarTablaNotasActas(AcCursoAperturado curape, List<AcSisEvaPersonalizado> evaluations,
                                      List<AcMatricula> students, Map<Integer, Map<Integer, Float>> gradesByStudent) {
        LOG.debug("Building body for minute of notes {} | {}", evaluations.size(), students.size());

        cabecerasNotas = new ArrayList<>();
        for (AcSisEvaPersonalizado personalizado : evaluations) {
            cabecerasNotas.add(personalizado.getSisevaCodigo());
        }

        AcSisEvaluacion sistema = daoSistemaEvaluacion.obtenerSistemaEvaluacion(curape.getSisevaId());
        List<AcSisEvaDetalle> groups = Collections.synchronizedList(
                new LinkedList<>(sistema.getAcSisEvaDetalles()));
        for (AcSisEvaDetalle group : groups) {
            cabecerasNotas.add(group.getSisevaDetalleCodigo());
        }
        cabecerasNotas.add(sistema.getSisevaCodigo());

        nroOrden = evaluations.size() + 1 + groups.size() + 1;

        registroNotas = new ArrayList<>();
        actaPublishRows = new ArrayList<>();
        for (var i = 0; i < students.size(); i++) {
            AcAlumno alumno = students.get(i).getAlu();
            Map<Integer, Float> studentGrades =
                    gradesByStudent.getOrDefault(alumno.getId(), Collections.emptyMap());

            var student = new BeanNotas();
            student.setStudentId(alumno.getId());
            student.setStudent((i + 1) + ".- " + alumno.getAluAppaterno() + " "
                    + alumno.getAluApmaterno() + " " + alumno.getAluNombres());

            var publishRow = new PublishedActaRow();
            publishRow.setStudentId(alumno.getId());

            List<GradeEntry> entries = new ArrayList<>();
            for (AcSisEvaPersonalizado personalizado : evaluations) {
                float grade = studentGrades.getOrDefault(personalizado.getId(), 0f);
                var cell = new BeanNotas();
                cell.setSisEvaluationId(personalizado.getId());
                cell.setGradeValue(formatGrade(grade));
                cell.setStyle(gradeCellStyle(grade));
                student.getEvaluations().add(cell);

                AcSisEvaDetalle group = personalizado.getSisevaDetalle();
                entries.add(new GradeEntry(grade, Float.parseFloat(personalizado.getSisevaPerPeso()),
                        group.getId(), group.getSisevaDetallePeso()));
                publishRow.addComponent(personalizado.getId(), grade);
            }

            // Group averages are display-only; the final keeps full precision and is truncated once.
            Map<Integer, Double> groupAverages = WeightedGradeCalculator.groupAverages(entries);
            for (AcSisEvaDetalle group : groups) {
                var cell = new BeanNotas();
                cell.setGradeValue(formatGrade(groupAverages.getOrDefault(group.getId(), 0d)));
                cell.setStyle("font-style: italic");
                student.getEvaluations().add(cell);
            }

            double rawFinal = WeightedGradeCalculator.rawFinal(entries);
            int finalGrade = GradePolicy.truncate(rawFinal);
            var finalCell = new BeanNotas();
            finalCell.setGradeValue(String.valueOf(finalGrade));
            finalCell.setStyle(gradeCellStyle(finalGrade));
            student.getEvaluations().add(finalCell);

            publishRow.setRawFinal(rawFinal);
            publishRow.setFinalGrade(finalGrade);
            publishRow.setStatus(GradePolicy.status(finalGrade));

            registroNotas.add(student);
            actaPublishRows.add(publishRow);
        }
        LOG.info("Total of grades for acta {}", registroNotas.size());
    }

    public void insertarActa(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.codigoActa.trim().isEmpty()) {
            setOncomplete("alert('Ingrese el codigo del acta')");
            return;
        }
        if (this.nombreActa.trim().isEmpty()) {
            setOncomplete("alert('Ingrese el nombre del acta')");
            return;
        }
        if (this.numeroActa.trim().isEmpty()) {
            setOncomplete("alert('Ingrese el numero del acta')");
            return;
        }

        var acta = new AcActa();
        acta.setActCodigo(this.getCodigoActa());
        acta.setActNombre(this.getNombreActa());
        acta.setActNumero(this.getNumeroActa());
        acta.setActActivo("1");
        acta.setActTipo(CAT_TIPO_ACTA_PRINCIPAL);
        acta.setActStatus(ACT_STATUS_PUBLISHED);
        var sec = new AcSeccion();
        sec.setId(this.secId);
        acta.setSec(sec);
        acta.setAcActaDetalles(new LinkedHashSet<>());
        for (PublishedActaRow publishRow : this.actaPublishRows) {
            // One PROMEDIO_PARCIAL detail per component (the immutable snapshot the substitute and
            // the re-rendered published Acta read back from).
            for (Map.Entry<Integer, Float> component : publishRow.getComponentGrades().entrySet()) {
                acta.getAcActaDetalles().add(componentDetalle(acta, publishRow.getStudentId(),
                        component.getValue(), CAT_TIPO_NOTA_PROMEDIO_PARCIAL, component.getKey()));
            }
            // One PROMEDIO_FINAL detail carrying the official truncated grade plus the auditable raw
            // grade and PASS/FAIL status (Grade Policy item 7).
            var finalDetalle = componentDetalle(acta, publishRow.getStudentId(),
                    (float) publishRow.getFinalGrade(), CAT_TIPO_NOTA_PROMEDIO_FINAL, 0);
            finalDetalle.setActdetRawGrade(BigDecimal.valueOf(publishRow.getRawFinal())
                    .setScale(2, RoundingMode.HALF_UP));
            finalDetalle.setActdetStatus(publishRow.getStatus());
            acta.getAcActaDetalles().add(finalDetalle);
        }
        try {
            gradeRecordService.publishPrincipal(acta);
            setOncomplete("alert('Se guardo el registro correctamente');Richfaces.hideModalPanel('mp2')");
        } catch (GradeRecordAlreadyPublishedException e) {
            LOG.warn("Rejected republish of Acta Principal for section {}", secId);
            setOncomplete("alert('Esta Acta ya fue publicada y no puede modificarse. " +
                    "Use el Acta Sustitutoria para corregir notas.')");
        }
    }

    public void llenarActaSustitutoria(ActionEvent event) {
        var row = event.getComponent().getAttributes().get("id_seccion").toString();
        this.secId = Integer.parseInt(row);
        LOG.debug("[{}] for section {}", event.getPhaseId(), secId);

        AcActa actaPrincipal = daoActa.seleccionarActa(secId, CAT_TIPO_ACTA_PRINCIPAL);
        AcActa actaSusti = daoActa.seleccionarActa(secId, CAT_TIPO_ACTA_SUSTITUTORIA);

        if (actaPrincipal == null || !ACT_STATUS_PUBLISHED.equals(actaPrincipal.getActStatus())) {
            setOncomplete("alert('Esta seccion no posee un acta registrada.');");
            return;
        }

        // Columns are driven by the section's evaluation system, not hardcoded EP/EF/PA codes: a new
        // evaluation system configured in the DB is rendered without touching this code.
        HSSeccionDAO daoSeccion = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
        AcSeccion seccion = daoSeccion.seleccionarSeccion(secId);
        AcCursoAperturado curape = seccion.getCurape();

        // The Substitute Act has its own identity (code "...-S", "Acta Sustitutoria ..." name, own
        // number), mirroring the Principal Act instead of borrowing the principal's values.
        HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        AcSemestre semestre = daoSemestre.getSemestre(curape.getSem().getId());
        this.setCodigoActa(GradeRecordNomenclature.code(semestre, seccion, CAT_TIPO_ACTA_SUSTITUTORIA));
        this.setNombreActa(GradeRecordNomenclature.name(semestre, seccion, CAT_TIPO_ACTA_SUSTITUTORIA));
        this.setNumeroActa(actaSusti != null
                ? actaSusti.getActNumero() : GradeRecordNomenclature.pendingNumber());

        List<AcSisEvaPersonalizado> components = new ArrayList<>(curape.getAcSisEvaPersonalizados());
        components.sort(Comparator.comparing(AcSisEvaPersonalizado::getSisevaPerOrden));
        this.setSubstituteHeaders(components.stream()
                .map(AcSisEvaPersonalizado::getSisevaCodigo).collect(Collectors.toList()));

        // Substitute scores already entered in a non-published draft, keyed by student.
        Map<Integer, String> existingSubstitute = new HashMap<>();
        if (actaSusti != null) {
            for (AcActaDetalle d : actaSusti.getAcActaDetalles()) {
                if (CAT_TIPO_NOTA_SUSTITUTORIO.equals(d.getActdetTipoNota())) {
                    existingSubstitute.put(d.getAluId(), d.getActdetNota().toString());
                }
            }
        }

        // Student identities (ordered by aluId by the query); component grades come strictly from the
        // principal act so an unrelated sustitutoria draft cannot leak in.
        Map<Integer, AcAlumno> studentById = new LinkedHashMap<>();
        for (Object[] obj : daoActa.seleccionarAlumnosActa(secId)) {
            var alumno = (AcAlumno) obj[0];
            studentById.putIfAbsent(alumno.getId(), alumno);
        }
        Map<Integer, List<AcActaDetalle>> detallesByStudent = new HashMap<>();
        for (AcActaDetalle d : actaPrincipal.getAcActaDetalles()) {
            detallesByStudent.computeIfAbsent(d.getAluId(), k -> new ArrayList<>()).add(d);
        }

        List<SubstituteActRow> rows = new ArrayList<>();
        for (AcAlumno alumno : studentById.values()) {
            List<AcActaDetalle> studentDetalles =
                    detallesByStudent.getOrDefault(alumno.getId(), Collections.emptyList());

            AcActaDetalle convalidacion = studentDetalles.stream()
                    .filter(d -> CAT_TIPO_NOTA_CONVALIDACION.equals(d.getActdetTipoNota()))
                    .findFirst().orElse(null);
            boolean convalidated = convalidacion != null;

            // Index the student's principal grades by component id (the stored siseva_detalle_id is
            // the personalizado id, as confirmed against the live schema).
            Map<Integer, AcActaDetalle> gradeByComponentId = new HashMap<>();
            for (AcActaDetalle d : studentDetalles) {
                gradeByComponentId.put(d.getSisevaDetalleId(), d);
            }

            var actRow = new SubstituteActRow();
            actRow.setStudentId(alumno.getId());
            actRow.setStudentCode(alumno.getAluCodigo());
            actRow.setStudentName(alumno.getAluAppaterno() + " " + alumno.getAluApmaterno() + " " + alumno.getAluNombres());
            actRow.setConvalidated(convalidated);

            List<SubstituteComponent> cells = new ArrayList<>();
            for (AcSisEvaPersonalizado component : components) {
                String value;
                Float numeric = null;
                if (convalidated) {
                    value = SubstituteComponent.CONVALIDATED;
                } else {
                    AcActaDetalle grade = gradeByComponentId.get(component.getId());
                    if (grade != null) {
                        value = grade.getActdetNota().toString();
                        numeric = grade.getActdetNota();
                    } else {
                        value = SubstituteComponent.EMPTY;
                    }
                }
                var cell = new SubstituteComponent(component.getSisevaCodigo(), value, component.getId());
                AcSisEvaDetalle group = component.getSisevaDetalle();
                cell.setComponentWeight(Float.parseFloat(component.getSisevaPerPeso()));
                cell.setGroupId(group.getId());
                cell.setGroupWeight(group.getSisevaDetallePeso());
                // Color criteria (issue #4): red below the passing grade, like the registration screen.
                cell.setStyle(numeric == null ? "" : gradeCellStyle(numeric));
                cells.add(cell);
            }
            actRow.setComponents(cells);

            if (convalidated) {
                actRow.setOriginalFinal(convalidacion.getActdetNota().toString());
            } else {
                List<GradeEntry> entries = replaceableEntries(actRow);
                actRow.setOriginalFinal(entries.isEmpty() ? SubstituteComponent.EMPTY
                        : String.valueOf(GradePolicy.truncate(WeightedGradeCalculator.rawFinal(entries))));
            }

            actRow.setSubstitute(existingSubstitute.getOrDefault(alumno.getId(), SubstituteComponent.EMPTY));
            rows.add(actRow);
        }

        this.setSubstituteActRows(rows);
        LOG.info("Substitute rows loaded {}", rows.size());
        setOncomplete("Richfaces.showModalPanel('mp3',{width:650, top:40});");
    }

    public void insertarSustitutorios(ActionEvent event) {
        if (substituteEditingActive()) {
            this.setOncomplete("alert('Termine de editar los horarios.')");
            return;
        }
        LOG.debug("[{}]", event.getPhaseId());
        // Backend grade validation for the substitute scores (defense-in-depth).
        for (SubstituteActRow actRow : substituteActRows) {
            if (actRow.hasSubstitute() && !GradePolicy.isValid(actRow.getSubstitute())) {
                setOncomplete("alert('Nota sustitutoria invalida: \"" + actRow.getSubstitute().trim() +
                        "\". Ingrese un valor entre 0 y 20 en incrementos de 0.5.')");
                return;
            }
        }
        var acta = new AcActa();
        acta.setActTipo(CAT_TIPO_ACTA_SUSTITUTORIA);
        var seccion = new AcSeccion();
        seccion.setId(secId);
        acta.setSec(seccion);

        // Own code/name/number, mirroring the Principal Act (number is assigned after the act_id
        // exists, just like insertarActa).
        HSSeccionDAO daoSeccion = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
        HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        AcSeccion seccionReal = daoSeccion.seleccionarSeccion(secId);
        AcSemestre semestre = daoSemestre.getSemestre(seccionReal.getCurape().getSem().getId());
        acta.setActCodigo(GradeRecordNomenclature.code(semestre, seccionReal, CAT_TIPO_ACTA_SUSTITUTORIA));
        acta.setActNombre(GradeRecordNomenclature.name(semestre, seccionReal, CAT_TIPO_ACTA_SUSTITUTORIA));
        acta.setActNumero(GradeRecordNomenclature.pendingNumber());
        acta.setActActivo("1");
        acta.setActStatus(ACT_STATUS_PUBLISHED);
        Set<AcActaDetalle> actaDetalles = new LinkedHashSet<>();
        for (SubstituteActRow actRow : substituteActRows) {
            if (!actRow.hasSubstitute()) {
                continue; // no substitute entered, or a convalidación row: nothing to replace
            }
            float substituteGrade = Float.parseFloat(actRow.getSubstitute());

            // Persist each original component grade (the immutable inputs to the recalculation) and
            // build the weighted entries. Iterating the row's components keeps this independent of
            // any particular evaluation system, so a new system needs no code change here.
            List<Float> originalGrades = new ArrayList<>();
            List<GradeEntry> entries = new ArrayList<>();
            for (SubstituteComponent component : actRow.getComponents()) {
                if (!component.isReplaceable()) {
                    continue;
                }
                float grade = Float.parseFloat(component.getValue());
                originalGrades.add(grade);
                entries.add(new GradeEntry(grade, component.getComponentWeight(),
                        component.getGroupId(), component.getGroupWeight()));
                actaDetalles.add(componentDetalle(acta, actRow.getStudentId(), grade,
                        CAT_TIPO_NOTA_PROMEDIO_PARCIAL, component.getSisevaDetalleId()));
            }

            actaDetalles.add(componentDetalle(acta, actRow.getStudentId(), substituteGrade,
                    CAT_TIPO_NOTA_SUSTITUTORIO, 0));

            if (!entries.isEmpty()) {
                // Peru-common rule: the substitute replaces the single lowest original grade,
                // regardless of its weight. The course grade is then recomputed with the evaluation
                // weights (Grade Policy item 6), preserving precision and truncating once.
                int lowest = SubstituteGradeCalculator.lowestGradeIndex(toFloatArray(originalGrades));
                List<GradeEntry> adjusted = replaceEntryGrade(entries, lowest, substituteGrade);
                double rawFinal = WeightedGradeCalculator.rawFinal(adjusted);
                int finalGrade = GradePolicy.truncate(rawFinal);
                LOG.trace("student {} original {} substitute {} raw {} final {}",
                        actRow.getStudentId(), originalGrades, substituteGrade, rawFinal, finalGrade);
                var finalDetalle = componentDetalle(acta, actRow.getStudentId(), (float) finalGrade,
                        CAT_TIPO_NOTA_PROMEDIO_FINAL, 0);
                finalDetalle.setActdetRawGrade(BigDecimal.valueOf(rawFinal).setScale(2, RoundingMode.HALF_UP));
                finalDetalle.setActdetStatus(GradePolicy.status(finalGrade));
                actaDetalles.add(finalDetalle);
            }
        }
        acta.setAcActaDetalles(actaDetalles);
        try {
            gradeRecordService.publishSubstitute(acta);
            setOncomplete("alert('Se guardo el registro correctamente');Richfaces.hideModalPanel('mp3')");
        } catch (GradeRecordAlreadyPublishedException e) {
            LOG.warn("Rejected republish of Acta Sustitutoria for section {}", secId);
            setOncomplete("alert('Esta Acta Sustitutoria ya fue publicada y no puede modificarse.')");
        }
    }

    private boolean substituteEditingActive() {
        return substituteActRows.stream().anyMatch(SubstituteActRow::isEditable);
    }

    public String getNombreActa() {
        return nombreActa;
    }

    public void setNombreActa(String nombreActa) {
        this.nombreActa = nombreActa;
    }

    public String getCodigoActa() {
        return codigoActa;
    }

    public void setCodigoActa(String codigoActa) {
        this.codigoActa = codigoActa;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    public List<String> getSubstituteHeaders() {
        return substituteHeaders;
    }

    public void setSubstituteHeaders(List<String> substituteHeaders) {
        this.substituteHeaders = substituteHeaders;
    }

    public List<SubstituteActRow> getSubstituteActRows() {
        return substituteActRows;
    }

    public void setSubstituteActRows(List<SubstituteActRow> substituteActRows) {
        this.substituteActRows = substituteActRows;
    }

    public List<PublishedActaRow> getActaPublishRows() {
        return actaPublishRows;
    }

    public void setActaPublishRows(List<PublishedActaRow> actaPublishRows) {
        this.actaPublishRows = actaPublishRows;
    }

    public String getFinales() {
        return finales;
    }

    public void setFinales(String finales) {
        this.finales = finales;
    }

    public String getFinalFinales() {
        return finalFinales;
    }

    public void setFinalFinales(String finalFinales) {
        this.finalFinales = finalFinales;
    }

    public String getAlumnoNombreFinal() {
        return alumnoNombreFinal;
    }

    public void setAlumnoNombreFinal(String alumnoNombreFinal) {
        this.alumnoNombreFinal = alumnoNombreFinal;
    }

    public String getAlumnoCodigoFinal() {
        return alumnoCodigoFinal;
    }

    public void setAlumnoCodigoFinal(String alumnoCodigoFinal) {
        this.alumnoCodigoFinal = alumnoCodigoFinal;
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public void openModalForBloqueoWeb(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        mpSistemaEvaluacion.clear();
        MbNota row = (MbNota) event.getComponent().getAttributes().get(ATTR_SECCION);
        this.nodeCurapeId = row.getNodeCurapeId();
        this.secId = row.getSecId();

        List<AcSisEvaPersonalizado> lsisvaper = daoImportacionNotas.listarSistemaEvaluacionPersonalizadoPorSeccion(nodeCurapeId);
        LOG.debug("el valor es: {} cantidad -> {}", nodeCurapeId, lsisvaper.size());
        for (AcSisEvaPersonalizado acSisEvaPersonalizado : lsisvaper) {
            AcImportacionNotas blonota = daoImportacionNotas.listarImportacionNotaPorSeccion(secId, acSisEvaPersonalizado.getId());

            var importe = new MbNota();
            if (blonota == null) {
                importe.setMpMarcar(false);
            } else {
                importe.setMpMarcar(blonota.getImpnotEstado().equals(CAT_TIPO_IMPORTACION_IMPORTADO));
            }

            importe.setMpSisevaPerId(acSisEvaPersonalizado.getId());
            importe.setMpSisevaPerCodigo(acSisEvaPersonalizado.getSisevaCodigo());
            importe.setMpSisevaPerDescripcion(acSisEvaPersonalizado.getSisevaPerNombre());
            importe.setSecId(this.secId);
            mpSistemaEvaluacion.add(importe);
        }
    }

    public void bloquearNotas(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.oncomplete = "";
        for (MbNota siseva : this.getMpSistemaEvaluacion()) {
            var impoNota = new AcImportacionNotas();

            var sec = new AcSeccion();
            var per = new AcSisEvaPersonalizado();
            sec.setId(secId);
            per.setId(siseva.getMpSisevaPerId());
            impoNota.setAcSeccion(sec);
            impoNota.setAcSisEvaPersonalizado(per);
            impoNota.setImpnotActivo("1");
            if (siseva.isMpMarcar()) {
                impoNota.setImpnotEstado(CAT_TIPO_IMPORTACION_IMPORTADO);
            } else {
                impoNota.setImpnotEstado(CAT_TIPO_IMPORTACION_NO_IMPORTADO);
            }
            AcImportacionNotas valor = daoImportacionNotas.listarImportacionNotaPorSeccion(secId, siseva.getMpSisevaPerId());
            if (valor == null) {
                impoNota.setImpnotId(0);
                daoImportacionNotas.agregarImportacionNota(impoNota);
            } else {
                impoNota.setImpnotId(valor.getImpnotId());
                daoImportacionNotas.actualizarImportacionNota(impoNota);
            }
        }
        this.oncomplete = "alert('Registros actualizados correctamente');Richfaces.hideModalPanel('mpbloquea')";
        mpSistemaEvaluacion.clear();
    }

    public void openModalForImport(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        MbNota row = (MbNota) event.getComponent().getAttributes().get(ATTR_SECCION);
        this.nodeCurapeId = row.getNodeCurapeId();
        this.secId = row.getSecId();

        List<AcSisEvaPersonalizado> lsisvaper = daoImportacionNotas.listarSistemaEvaluacionPersonalizadoPorSeccion(nodeCurapeId);
        if (!lsisvaper.isEmpty()) {
            cboRadioSisEva = new SelectItem[lsisvaper.size()];

            for (var i = 0; i < cboRadioSisEva.length; i++) {
                List<AcNotasTemporales> notasTemporales = daoNotasTemporales.listarNotasTemporales(secId, lsisvaper.get(i).getId());
                var canti = notasTemporales.size();
                String ingresado = canti > 0 ? "Ingresado" : "No ingresado";
                cboRadioSisEva[i] = new SelectItem(lsisvaper.get(i).getId(), lsisvaper.get(i).getSisevaPerNombre() + "  -  " + ingresado);
            }
            mpSisevaPerId = 0;
        } else {
            cboRadioSisEva = new SelectItem[0];
        }
    }

    public void guardarImporte(ActionEvent event) {
        LOG.debug("[{}] For section {}", event.getPhaseId(), secId);
        oncomplete = "";
        if (this.getMpSisevaPerId() > 0) {
            String mensaje;
            List<AcNotasTemporales> notasTemporales = daoNotasTemporales.listarNotasTemporales(secId, mpSisevaPerId);
            if (notasTemporales.isEmpty()) {
                mensaje = "No hay notas temporales";
            } else {
                List<AcNota> gradesToUpdate = new ArrayList<>();
                for (AcNotasTemporales nt : notasTemporales) {
                    AcNota nota = daoNota.listarNotaPorAlumno(nt.getAluId(), nt.getAcSeccion().getId(), nt.getSisevaPerId());
                    if (nota == null) {
                        LOG.trace("agregar");
                        nota = new AcNota();
                        var alu = new AcAlumno();
                        var per = new AcSisEvaPersonalizado();
                        alu.setId(nt.getAluId());
                        per.setId(nt.getSisevaPerId());
                        nota.setAlu(alu);
                        nota.setSisevaPer(per);
                        nota.setNotActivo("1");
                        nota.setNotCreacion(nt.getNottemFecha());
                        nota.setNotObservacion("FROM_TMP");
                        nota.setSecId(nt.getAcSeccion().getId());
                    } else {
                        LOG.trace("editar");
                        nota.setNotNota(nt.getNottemNota());
                    }
                    gradesToUpdate.add(nota);
                }
                daoNota.insertarOrActualizarNotas(gradesToUpdate);
                AcImportacionNotas imporT = daoImportacionNotas.listarImportacionNotaPorSeccion(secId, mpSisevaPerId);
                var impor = new AcImportacionNotas();
                var sec = new AcSeccion();
                var per = new AcSisEvaPersonalizado();
                sec.setId(secId);
                per.setId(mpSisevaPerId);
                impor.setAcSeccion(sec);
                impor.setAcSisEvaPersonalizado(per);
                impor.setImpnotActivo("1");
                if (imporT == null) {
                    impor.setImpnotId(0);
                    impor.setImpnotEstado(CAT_TIPO_IMPORTACION_IMPORTADO);
                    daoImportacionNotas.agregarImportacionNota(impor);
                } else {
                    impor.setImpnotId(imporT.getImpnotId());
                    impor.setImpnotEstado(CAT_TIPO_IMPORTACION_IMPORTADO);
                    daoImportacionNotas.actualizarImportacionNota(impor);
                }

                mensaje = "Los registros han sido importados con exito ;)";
            }
            oncomplete = "alert('" + mensaje + "');Richfaces.hideModalPanel('mpimporta')";
        } else {
            oncomplete = "alert('Seleccione el tipo de nota')";
        }
    }

    public void vistaPrevia(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        listarAlumnoNota.clear();
        oncomplete = "";
        if (this.getMpSisevaPerId() == 0) {
            oncomplete = "alert('Seleccione el tipo de nota')";
        } else {
            LOG.debug("el sisper_id -> {}", this.getMpSisevaPerId());
            LOG.debug("el sec_id -> {}", secId);

            HSAlumnoDAO dao = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
            List<AcAlumno> alumnos = dao.listarAlumnosMatriculaRegularPorSeccion(secId);
            List<AcNotasTemporales> notaT = daoNotasTemporales.listarNotasTemporales(secId, mpSisevaPerId);

            for (var i = 0; i < alumnos.size(); i++) {
                AcNota nota = daoNota.listarNotaPorAlumno(alumnos.get(i).getId(), secId, mpSisevaPerId);
                var alu = new MbNota();

                if (nota == null) {
                    alu.setFinales("");
                } else {
                    alu.setFinales(nota.getNotNota().toString());
                }
                if (notaT.isEmpty()) {
                    alu.setFinalFinales("");
                } else {
                    for (AcNotasTemporales acNotasTemporales : notaT) {
                        if (alumnos.get(i).getId().intValue() == acNotasTemporales.getAluId().intValue()) {
                            alu.setFinalFinales(notaT.get(i).getNottemNota().toString());
                        }
                    }
                }
                alu.setAlumnoNombreFinal(alumnos.get(i).getAluAppaterno() + " " + alumnos.get(i).getAluApmaterno() + " " + alumnos.get(i).getAluNombres());
                alu.setAlumnoCodigoFinal(alumnos.get(i).getAluCodigo());
                alu.setNroOrden(i + 1);
                alu.setAlumnoId(alumnos.get(i).getId());

                listarAlumnoNota.add(alu);
            }
            oncomplete = "Richfaces.showModalPanel('mpvista',{top:80})";
        }
    }
}
