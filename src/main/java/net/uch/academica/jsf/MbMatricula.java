package net.uch.academica.jsf;

import net.uch.academica.dao.*;
import net.uch.academica.jsf.beans.BeanCursos;
import net.uch.academica.jsf.beans.BeanDuplicadoHorario;
import net.uch.academica.service.EnrollmentService;
import net.uch.academica.service.EnrollmentSnapshot;
import net.uch.academica.service.NoVacancyException;
import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.administrativa.dao.HSEstructuraPagoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.uch.common.Constants.*;

public class MbMatricula {

    private static final Logger LOG = LoggerFactory.getLogger(MbMatricula.class);

    private static final String P_ID_MATRICULA = "p_id_matricula";
    private static final String P_ID_SEMESTRE = "p_id_semestre";

    // Sprint 2 enrollment validation pipeline - standardized blocking messages, one per business rule.
    private static final String MSG_NO_ELEGIBLE = "El alumno no es elegible para matricularse. Comuníquese con Dirección Académica.";
    private static final String MSG_DEUDA_PENDIENTE = "El alumno tiene deuda pendiente. No puede matricularse hasta regularizar su situación.";
    private static final String MSG_PRERREQUISITOS_NO_CUMPLIDOS = "El alumno no cumple con los prerrequisitos de uno o más cursos seleccionados.";

    private final HSMatriculaDAO daoMatricula = ServiceFinder.findBean("springHibernateDaoMatricula", HSMatriculaDAO.class);
    private final HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
    private final HSAlumnoDAO daoAlumno = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
    private final EnrollmentService enrollmentService = ServiceFinder.findBean("enrollmentService", EnrollmentService.class);

    private List<MbMatricula> listaAlumno;
    private List<MbMatricula> listaCursosMatriculado;
    private String paramAluCodigo;
    private String paramAluPaterno;
    private String paramAluMaterno;
    private String paramAluNombre;
    private int paramFacId;
    private SelectItem[] comboFacultades;
    private int paramEspId;
    private List<SelectItem> comboEspecialidades;
    private int paramSemId;
    private SelectItem[] comboSemestres;

    private String semestreActual;

    private int studentId;
    private String rowAluCodigo;
    private String rowAluPaterno;
    private String rowAluMaterno;
    private String rowAluNombre;
    private String rowEspecialidad;
    private int rowMatriculaId;
    private String rowTipoMatriculaId;
    private String rowTipoMatriculaValue;
    private int rowSemestreId;
    private String rowSemestreCode;

    private int seccionId;
    private SelectItem[] comboSecciones;
    private List<TbCatalogo> tiposMatricula;
    private List<TbCatalogo> tiposCurso;
    private List<AcSemestre> semestres;
    private String oncomplete;
    private List<MbMatricula> listaCursos;
    private int mpRowCursoId;
    private String mpRowCursoCode;
    private String mpRowCursoName;
    private String mpRowCursoCiclo;
    private String mpRowCursoCreditos;
    private String mpRowCursoTipo;
    private boolean renderedSubTable = false;
    private String flagVer = "0";
    private String imagen = ACTION_IMAGES_DOWN;
    private String detCursoCodigo;
    private String detCursoNombre;
    private String detCursoCreditos;
    private String detSeccionCodigo;
    private int detCursoId;
    private List<TbCatalogo> listaCiclo;

    private boolean verDeuda;
    private boolean verMatricular;
    private boolean verRectificar;
    private boolean verEliminar;
    private boolean verImprimirFicha;
    private boolean verImprimirConsolidado;
    private boolean verReservar;
    private boolean verAlerta;
    //////////////////////////////
    private List<MbMatricula> detalleDeuda;
    ///////
    private String mpRowDeudaSemestre;
    private String mpRowDeudaConceptoPago;
    private String mpRowDeudaFechaPago;
    private String mpRowDeudaFechaProrroga;

    private List<BeanDuplicadoHorario> listaDuplicadoHor = new ArrayList<>();

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        HSFacultadDAO daoFacultad = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        List<AcFacultad> facultades = daoFacultad.seleccionarFacultad("", "");
        setComboFacultades(facultades.stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .toArray(SelectItem[]::new));
        setComboEspecialidades(new ArrayList<>());

        semestres = daoSemestre.seleccionarSemestreActivo();
        setComboSemestres(semestres.stream()
                .map(s -> new SelectItem(s.getId(), s.getSemNombre()))
                .toArray(SelectItem[]::new));

        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);

        AcSemestre semestre = daoSemestre.seleccionarSemestreActual();
        setSemestreActual(semestre.getSemNombre());

        tiposMatricula = daoCatalogo.seleccionarCatalogo("022");
        tiposCurso = daoCatalogo.seleccionarCatalogo("012");
        listaCiclo = daoCatalogo.seleccionarCatalogo("006");
    }

    public List<BeanDuplicadoHorario> getListaDuplicadoHor() {
        return listaDuplicadoHor;
    }

    public void setListaDuplicadoHor(List<BeanDuplicadoHorario> listaDuplicadoHor) {
        this.listaDuplicadoHor = listaDuplicadoHor;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getParamAluCodigo() {
        return paramAluCodigo;
    }

    public void setParamAluCodigo(String paramAluCodigo) {
        this.paramAluCodigo = paramAluCodigo;
    }

    public String getParamAluPaterno() {
        return paramAluPaterno;
    }

    public void setParamAluPaterno(String paramAluPaterno) {
        this.paramAluPaterno = paramAluPaterno;
    }

    public String getParamAluMaterno() {
        return paramAluMaterno;
    }

    public void setParamAluMaterno(String paramAluMaterno) {
        this.paramAluMaterno = paramAluMaterno;
    }

    public String getParamAluNombre() {
        return paramAluNombre;
    }

    public void setParamAluNombre(String paramAluNombre) {
        this.paramAluNombre = paramAluNombre;
    }

    public String getRowTipoMatriculaValue() {
        return rowTipoMatriculaValue;
    }

    public void setRowTipoMatriculaValue(String rowTipoMatriculaValue) {
        this.rowTipoMatriculaValue = rowTipoMatriculaValue;
    }

    public String getRowEspecialidad() {
        return rowEspecialidad;
    }

    public void setRowEspecialidad(String rowEspecialidad) {
        this.rowEspecialidad = rowEspecialidad;
    }

    public int getParamEspId() {
        return paramEspId;
    }

    public void setParamEspId(int paramEspId) {
        this.paramEspId = paramEspId;
    }

    public int getRowSemestreId() {
        return rowSemestreId;
    }

    public void setRowSemestreId(int rowSemestreId) {
        this.rowSemestreId = rowSemestreId;
    }

    public int getParamFacId() {
        return paramFacId;
    }

    public void setParamFacId(int paramFacId) {
        this.paramFacId = paramFacId;
    }

    public List<MbMatricula> getListaAlumno() {
        return listaAlumno;
    }

    public void setListaAlumno(List<MbMatricula> listaAlumno) {
        this.listaAlumno = listaAlumno;
    }

    public String getSemestreActual() {
        return semestreActual;
    }

    public void setSemestreActual(String semestreActual) {
        this.semestreActual = semestreActual;
    }

    public SelectItem[] getComboFacultades() {
        return comboFacultades;
    }

    public void setComboFacultades(SelectItem[] comboFacultades) {
        this.comboFacultades = comboFacultades;
    }

    public List<SelectItem> getComboEspecialidades() {
        return comboEspecialidades;
    }

    public void setComboEspecialidades(List<SelectItem> comboEspecialidades) {
        this.comboEspecialidades = comboEspecialidades;
    }

    public void cambiarEspecialidadBuscar(ActionEvent event) {
        LOG.debug("[{}] for faculty {}", event, paramFacId);
        comboEspecialidades.clear();
        if (paramFacId != 0) {
            HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            List<AcEspecialidad> lista = dao.seleccionarEspecialidad(paramFacId);
            comboEspecialidades.addAll(lista.stream()
                    .map(f -> new SelectItem(f.getId(), f.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public void seleccionar() {
        LOG.debug("Looking for '{}' | '{}' | '{}' | '{}' | {} | {} | {}",
                this.paramAluCodigo, this.paramAluPaterno, this.paramAluMaterno, this.paramAluNombre, this.paramFacId, this.paramEspId, this.paramSemId);
        listaAlumno = new ArrayList<>();
        HSAlumnoTarifaDAO daoTarifa = ServiceFinder.findBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class);
        // Eligibility (AluTipo=ESTUDIANTE, AluEstado=REGULAR) is enforced in the query itself, not
        // filtered in Java after fetch: this DAO method is enrollment-only, so unlike the shared
        // seleccionarAlumnos() (also used by MbAlumno's admin search, MbConvalidacion, MbMatriculaCL,
        // which need every type/status) it's safe to push the restriction to the DB.
        List<AcAlumno> lista = daoAlumno.seleccionarAlumnosElegiblesParaMatricula(this.getParamAluCodigo(), this.getParamAluPaterno(), this.getParamAluMaterno(), this.getParamAluNombre(), this.getParamEspId());
        MbMatricula bA;

        var fecActual = Calendar.getInstance();
        fecActual.setTime(new Date());

        List<Integer> studentIds = lista.stream().map(AcAlumno::getId).collect(Collectors.toList());
        Map<Integer, EnrollmentSnapshot> snapshots = enrollmentService.findEnrollmentSnapshots(studentIds, this.getParamSemId());

        for (AcAlumno acAlumno : lista) {
            bA = new MbMatricula();

            bA.setStudentId(acAlumno.getId());
            bA.setRowAluCodigo(acAlumno.getAluCodigo());
            bA.setRowAluPaterno(acAlumno.getAluAppaterno());
            bA.setRowAluMaterno(acAlumno.getAluApmaterno());
            bA.setRowAluNombre(acAlumno.getAluNombres());
            bA.setParamEspId(acAlumno.getEsp().getId());
            bA.setRowEspecialidad(acAlumno.getEsp().getEspNombre());

            EnrollmentSnapshot snapshot = snapshots.get(acAlumno.getId());
            List<AcMatricula> listRegular = snapshot.getRegular();
            List<AcMatricula> listReserva = snapshot.getReserved();
            List<AcMatricula> listPrematricula = snapshot.getPreEnrollment();

            if (!listRegular.isEmpty()) {
                LOG.trace("Matricula regular {}", listRegular.size());
                for (TbCatalogo tbCatalogo : tiposMatricula) {
                    if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(listRegular.get(0).getMatTipo())) {
                        bA.setRowTipoMatriculaValue(tbCatalogo.getCatDescripcionElemento());
                    }
                }
                bA.setRowMatriculaId(listRegular.get(0).getId());
                bA.setRowTipoMatriculaId(listRegular.get(0).getMatTipo());

                bA.setVerDeuda(false);
                bA.setVerMatricular(false);
                bA.setVerRectificar(true);
                bA.setVerEliminar(true);
                bA.setVerImprimirFicha(true);
                bA.setVerImprimirConsolidado(true);
                bA.setVerReservar(true);
                bA.setVerAlerta(false);
            } else if (!listReserva.isEmpty()) {
                LOG.trace("Matricula reservada {}", listReserva.size());
                for (TbCatalogo tbCatalogo : tiposMatricula) {
                    if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(listReserva.get(0).getMatTipo())) {
                        bA.setRowTipoMatriculaValue(tbCatalogo.getCatDescripcionElemento());
                    }
                }
                bA.setRowMatriculaId(listReserva.get(0).getId());
                bA.setRowTipoMatriculaId(listReserva.get(0).getMatTipo());

                bA.setVerDeuda(false);
                bA.setVerMatricular(false);
                bA.setVerRectificar(false);
                bA.setVerEliminar(true);
                bA.setVerImprimirFicha(false);
                bA.setVerImprimirConsolidado(false);
                bA.setVerReservar(false);
                bA.setVerAlerta(false);
            } else if (!listPrematricula.isEmpty()) {
                LOG.trace("Prematricula {}", listPrematricula.size());
                for (TbCatalogo tbCatalogo : tiposMatricula) {
                    if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(listPrematricula.get(0).getMatTipo())) {
                        bA.setRowTipoMatriculaValue(tbCatalogo.getCatDescripcionElemento());
                    }
                }
                bA.setRowMatriculaId(listPrematricula.get(0).getId());
                bA.setRowTipoMatriculaId(listPrematricula.get(0).getMatTipo());

                bA.setVerDeuda(false);
                bA.setVerMatricular(true);
                bA.setVerRectificar(false);
                bA.setVerEliminar(false);
                bA.setVerImprimirFicha(false);
                bA.setVerImprimirConsolidado(false);
                bA.setVerReservar(false);
                bA.setVerAlerta(false);
            } else {
                LOG.trace("Sin Matricula");
                bA.setRowTipoMatriculaValue("No registra Matricula");
                bA.setRowMatriculaId(0);
                bA.setRowTipoMatriculaId("0");

                bA.setVerDeuda(false);
                bA.setVerMatricular(true);
                bA.setVerRectificar(false);
                bA.setVerEliminar(false);
                bA.setVerImprimirFicha(false);
                bA.setVerImprimirConsolidado(false);
                bA.setVerReservar(false);
                bA.setVerAlerta(false);
            }

            if ((!listRegular.isEmpty() && !listReserva.isEmpty())
                    || (!listReserva.isEmpty() && !listPrematricula.isEmpty())
                    || (!listRegular.isEmpty() && !listPrematricula.isEmpty())) {
                bA.setVerAlerta(true);
            }

            for (AcSemestre acSemestre : semestres) {
                if (acSemestre.getId().equals(this.getParamSemId())) {
                    bA.setRowSemestreId(acSemestre.getId());
                    bA.setRowSemestreCode(acSemestre.getSemCodigo());
                }
            }

            List<AdAlumnoTarifa> deudas = daoTarifa.seleccionarAlumnoTarifa(acAlumno.getId());
            for (AdAlumnoTarifa adAlumnoTarifa : deudas) {
                if (esDeudaBloqueante(adAlumnoTarifa, fecActual)) {
                    LOG.warn("Student {} blocked from enrollment due to debt: AlumnoTarifa {} estado='{}' fechaProrroga={}",
                            acAlumno.getId(), adAlumnoTarifa.getId(), adAlumnoTarifa.getAlutarEstado(), adAlumnoTarifa.getAlutarFechaProrroga());
                    bA.setVerDeuda(true);
                    bA.setVerMatricular(false);
                    bA.setVerRectificar(false);
                    bA.setVerReservar(false);
                    break;
                }
            }

            bA.setListaCursosMatriculado(Collections.emptyList());
            listaAlumno.add(bA);
        }
        LOG.info("Total of students {}", listaAlumno.size());
        this.setListaAlumno(listaAlumno);
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        rowMatriculaId = Integer.parseInt(((UIParameter) event.getComponent().findComponent(P_ID_MATRICULA)).getValue().toString());
        rowTipoMatriculaId = ((UIParameter) event.getComponent().findComponent("p_mat_tipo")).getValue().toString();

        studentId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_id")).getValue().toString());
        rowAluCodigo = ((UIParameter) event.getComponent().findComponent("p_codigo")).getValue().toString();
        rowAluPaterno = ((UIParameter) event.getComponent().findComponent("p_paterno")).getValue().toString();
        rowAluMaterno = ((UIParameter) event.getComponent().findComponent("p_materno")).getValue().toString();
        rowAluNombre = ((UIParameter) event.getComponent().findComponent("p_nombre")).getValue().toString();
        paramEspId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_id_esp_act")).getValue().toString());
        rowSemestreId = Integer.parseInt(((UIParameter) event.getComponent().findComponent(P_ID_SEMESTRE)).getValue().toString());
    }

    public void cursosAbiertos() {
        LOG.debug("Retrieving student information for enrollment {} semester {} especialization {}",
                studentId, rowSemestreId, paramEspId);
        List<AcMatricula> matriculas = enrollmentService.findPreEnrollment(studentId, rowSemestreId);

        List<AcPlanCurso> abiertos = cursosAbiertos(studentId, paramEspId);

        List<MbMatricula> coursesToEnroll = new ArrayList<>();

        if (matriculas.isEmpty()) {
            for (AcPlanCurso abierto : abiertos) {
                var matricula = new MbMatricula();
                matricula.setMpRowCursoId(abierto.getId());
                matricula.setMpRowCursoCode(abierto.getCur().getCurCodigo());
                matricula.setMpRowCursoName(abierto.getCur().getCurNombre());
                for (TbCatalogo tbCatalogo : listaCiclo) {
                    if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(abierto.getPlancurCiclo())) {
                        matricula.setMpRowCursoCiclo(tbCatalogo.getCatDescripcionElemento());
                    }
                }
                matricula.setMpRowCursoCreditos(abierto.getPlancurCredito());
                for (TbCatalogo tbCatalogo : tiposCurso) {
                    if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(abierto.getPlancurTipo())) {
                        matricula.setMpRowCursoTipo(tbCatalogo.getCatDescripcionElemento());
                    }
                }
                matricula.setComboSecciones(llenarComboSecciones(abierto.getId()));
                coursesToEnroll.add(matricula);
            }
        } else {
            List<AcMatriculaCurso> matriculados = Collections.synchronizedList(new LinkedList<>(matriculas.get(0).getAcMatriculaCursos()));

            if (abiertos.size() > matriculados.size()) {
                for (var j = 0; j < abiertos.size(); j++) {
                    var matricula = new MbMatricula();
                    matricula.setMpRowCursoId(abiertos.get(j).getId());
                    matricula.setMpRowCursoCode(abiertos.get(j).getCur().getCurCodigo());
                    matricula.setMpRowCursoName(abiertos.get(j).getCur().getCurNombre());
                    for (TbCatalogo tbCatalogo : listaCiclo) {
                        if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(abiertos.get(j).getPlancurCiclo())) {
                            matricula.setMpRowCursoCiclo(tbCatalogo.getCatDescripcionElemento());
                        }
                    }
                    matricula.setMpRowCursoCreditos(abiertos.get(j).getPlancurCredito());
                    for (TbCatalogo tbCatalogo : tiposCurso) {
                        if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(abiertos.get(j).getPlancurTipo())) {
                            matricula.setMpRowCursoTipo(tbCatalogo.getCatDescripcionElemento());
                        }
                    }
                    matricula.setComboSecciones(llenarComboSecciones(abiertos.get(j).getId()));
                    matricula.setSeccionId(0);
                    coursesToEnroll.add(matricula);
                    for (AcMatriculaCurso matriculado : matriculados) {
                        if (abiertos.get(j).getId().equals(matriculado.getSec().getCurape().getPlancur().getId())) {
                            matricula = new MbMatricula();
                            matricula.setMpRowCursoId(matriculado.getSec().getCurape().getPlancur().getId());
                            matricula.setMpRowCursoCode(matriculado.getSec().getCurape().getPlancur().getCur().getCurCodigo());
                            matricula.setMpRowCursoName(matriculado.getSec().getCurape().getPlancur().getCur().getCurNombre());
                            for (TbCatalogo tbCatalogo : listaCiclo) {
                                if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(matriculado.getSec().getCurape().getPlancur().getPlancurCiclo())) {
                                    matricula.setMpRowCursoCiclo(tbCatalogo.getCatDescripcionElemento());
                                }
                            }
                            matricula.setMpRowCursoCreditos(matriculado.getSec().getCurape().getPlancur().getPlancurCredito());
                            for (TbCatalogo tbCatalogo : tiposCurso) {
                                if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(matriculado.getSec().getCurape().getPlancur().getPlancurTipo())) {
                                    matricula.setMpRowCursoTipo(tbCatalogo.getCatDescripcionElemento());
                                }
                            }
                            matricula.setComboSecciones(llenarComboSecciones(matriculado.getSec().getCurape().getPlancur().getId()));
                            matricula.setSeccionId(matriculado.getSec().getId());
                            coursesToEnroll.set(j, matricula);
                        }
                    }
                }
            } else {
                for (AcMatriculaCurso matriculado : matriculados) {
                    var matricula = new MbMatricula();
                    matricula.setMpRowCursoId(matriculado.getSec().getCurape().getPlancur().getId());
                    matricula.setMpRowCursoCode(matriculado.getSec().getCurape().getPlancur().getCur().getCurCodigo());
                    matricula.setMpRowCursoName(matriculado.getSec().getCurape().getPlancur().getCur().getCurNombre());
                    for (TbCatalogo tbCatalogo : listaCiclo) {
                        if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(matriculado.getSec().getCurape().getPlancur().getPlancurCiclo())) {
                            matricula.setMpRowCursoCiclo(tbCatalogo.getCatDescripcionElemento());
                        }
                    }
                    matricula.setMpRowCursoCreditos(matriculado.getSec().getCurape().getPlancur().getPlancurCredito());
                    for (TbCatalogo tbCatalogo : tiposCurso) {
                        if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(matriculado.getSec().getCurape().getPlancur().getPlancurTipo())) {
                            matricula.setMpRowCursoTipo(tbCatalogo.getCatDescripcionElemento());
                        }
                    }
                    matricula.setComboSecciones(llenarComboSecciones(matriculado.getSec().getCurape().getPlancur().getId()));
                    matricula.setSeccionId(matriculado.getSec().getId());
                    coursesToEnroll.add(matricula);
                }
            }
        }
        LOG.info("Total of courses to register {}", coursesToEnroll.size());
        this.setListaCursos(coursesToEnroll);
    }

    public String rectificarMatricula() {
        LOG.debug("Opening Modal for Rectificacion de matricula: Alumno {} | semestre {} | especialidad {}",
                studentId, rowSemestreId, paramEspId);
        List<AcMatricula> matriculas = enrollmentService.findRegularEnrollment(studentId, rowSemestreId);
        List<MbMatricula> cursosMatricular = new ArrayList<>();

        if (!matriculas.isEmpty()) {
            AcMatricula matriculaAlumno = matriculas.get(0);
            List<AcMatriculaCurso> matriculados = Collections.synchronizedList(new LinkedList<>(matriculaAlumno.getAcMatriculaCursos()));
            List<AcPlanCurso> abiertos = cursosAbiertos(studentId, paramEspId);
            MbMatricula matricula;

            if (abiertos.size() > matriculados.size()) {
                LOG.debug("Cursos abiertos");
                for (var j = 0; j < abiertos.size(); j++) {
                    matricula = new MbMatricula();
                    matricula.setMpRowCursoId(abiertos.get(j).getId());
                    matricula.setMpRowCursoCode(abiertos.get(j).getCur().getCurCodigo());
                    matricula.setMpRowCursoName(abiertos.get(j).getCur().getCurNombre());
                    for (TbCatalogo tbCatalogo : listaCiclo) {
                        if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(abiertos.get(j).getPlancurCiclo())) {
                            matricula.setMpRowCursoCiclo(tbCatalogo.getCatDescripcionElemento());
                        }
                    }
                    matricula.setMpRowCursoCreditos(abiertos.get(j).getPlancurCredito());
                    for (TbCatalogo tbCatalogo : tiposCurso) {
                        if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(abiertos.get(j).getPlancurTipo())) {
                            matricula.setMpRowCursoTipo(tbCatalogo.getCatDescripcionElemento());
                        }
                    }
                    matricula.setComboSecciones(llenarComboSecciones(abiertos.get(j).getId()));
                    matricula.setSeccionId(0);
                    cursosMatricular.add(matricula);
                    for (AcMatriculaCurso matriculado : matriculados) {
                        AcSeccion matriculadoSeccion = matriculado.getSec();
                        if (abiertos.get(j).getId().equals(matriculadoSeccion.getCurape().getPlancur().getId())) {
                            matricula = new MbMatricula();
                            matricula.setMpRowCursoId(matriculadoSeccion.getCurape().getPlancur().getId());
                            matricula.setMpRowCursoCode(matriculadoSeccion.getCurape().getPlancur().getCur().getCurCodigo());
                            matricula.setMpRowCursoName(matriculadoSeccion.getCurape().getPlancur().getCur().getCurNombre());
                            for (TbCatalogo tbCatalogo : listaCiclo) {
                                if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(matriculadoSeccion.getCurape().getPlancur().getPlancurCiclo())) {
                                    matricula.setMpRowCursoCiclo(tbCatalogo.getCatDescripcionElemento());
                                }
                            }
                            matricula.setMpRowCursoCreditos(matriculadoSeccion.getCurape().getPlancur().getPlancurCredito());
                            for (TbCatalogo tbCatalogo : tiposCurso) {
                                if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(matriculadoSeccion.getCurape().getPlancur().getPlancurTipo())) {
                                    matricula.setMpRowCursoTipo(tbCatalogo.getCatDescripcionElemento());
                                }
                            }
                            matricula.setComboSecciones(llenarComboSecciones(matriculadoSeccion.getCurape().getPlancur().getId()));
                            matricula.setSeccionId(matriculadoSeccion.getId());
                            cursosMatricular.set(j, matricula);
                        }
                    }
                }
            } else {
                LOG.debug("Cursos matriculados");
                for (AcMatriculaCurso matriculado : matriculados) {
                    matricula = new MbMatricula();
                    matricula.setMpRowCursoId(matriculado.getSec().getCurape().getPlancur().getId());
                    matricula.setMpRowCursoCode(matriculado.getSec().getCurape().getPlancur().getCur().getCurCodigo());
                    matricula.setMpRowCursoName(matriculado.getSec().getCurape().getPlancur().getCur().getCurNombre());
                    for (TbCatalogo tbCatalogo : listaCiclo) {
                        if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(matriculado.getSec().getCurape().getPlancur().getPlancurCiclo())) {
                            matricula.setMpRowCursoCiclo(tbCatalogo.getCatDescripcionElemento());
                        }
                    }
                    matricula.setMpRowCursoCreditos(matriculado.getSec().getCurape().getPlancur().getPlancurCredito());
                    for (TbCatalogo tbCatalogo : tiposCurso) {
                        if ((tbCatalogo.getCatCodigoGrupo() + tbCatalogo.getCatCodigoElemento()).equals(matriculado.getSec().getCurape().getPlancur().getPlancurTipo())) {
                            matricula.setMpRowCursoTipo(tbCatalogo.getCatDescripcionElemento());
                        }
                    }
                    matricula.setComboSecciones(llenarComboSecciones(matriculado.getSec().getCurape().getPlancur().getId()));
                    matricula.setSeccionId(matriculado.getSec().getId());
                    cursosMatricular.add(matricula);
                }
            }
            this.setOncomplete("javascript:Richfaces.showModalPanel('mpRectificar',{top:50})");
        } else {
            this.setOncomplete("alert('El alumno no presenta matricula')");
        }
        this.setListaCursos(cursosMatricular);
        return "";
    }

    public List<AcPlanCurso> cursosAbiertos(int alumnoId, int especialidad) {
        LOG.debug("Getting opened courses of {} for the speciality {}", alumnoId, especialidad);
        HSPreRequisitosDAO dao = ServiceFinder.findBean("springHibernateDaoPreRequisitos", HSPreRequisitosDAO.class);
        List<AcPlanCurso> abiertos;
        List<AcPlanCurso> libres;
        BeanCursos aprobados = daoMatricula.seleccionarCursosAprobados(alumnoId);

        List<Integer> ap = aprobados.getAprobados();
        List<Integer> da = aprobados.getDesaprobadosId();

        List<Integer> preLibres = new ArrayList<>();
        if (ap.get(0) == -1) {
            abiertos = filtroAperturados(daoMatricula.seleccionarCursosParaIngresantes(alumnoId));
        } else {
            preLibres.addAll(ap);
            preLibres.addAll(da);

            abiertos = filtroAperturados(dao.retonarCursosAbiertos(ap, da));

            libres = filtroAperturados(dao.retonarCursosLibres(preLibres, especialidad));

            abiertos.addAll(libres);

            List<AcPlanCurso> lis = filtroAperturados(aprobados.getDesaprobados());

            if (!lis.isEmpty()) {
                abiertos.addAll(lis);
            }
        }
        return abiertos;
    }

    public List<AcPlanCurso> filtroAperturados(List<AcPlanCurso> cursosTotales) {
        List<AcPlanCurso> cursos = new ArrayList<>();
        HSAperturaCursosDAO dao = ServiceFinder.findBean("springHibernateDaoCursoAperturado", HSAperturaCursosDAO.class);
        for (AcPlanCurso cursos_totale : cursosTotales) {
            AcCursoAperturado curape = dao.verificarCursoAperturado(cursos_totale.getId(), rowSemestreId);
            if (curape != null && "1".equals(curape.getCurapeAperturado())) {
                cursos.add(cursos_totale);
            }
        }
        return cursos;
    }

    public SelectItem[] llenarComboSecciones(int plancurId) {
        LOG.debug("Filling sections combobox of Plan Curricular '{}'", plancurId);
        HSSeccionDAO dao = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
        List<AcSeccion> lista = dao.obtenerSecciones(plancurId, rowSemestreId);
        LOG.info("Total available sections {}", lista.size());
        var cmbSecciones = new SelectItem[lista.size() + 1];
        cmbSecciones[0] = new SelectItem(0, "No registra Matricula");
        for (var i = 0; i < cmbSecciones.length - 1; i++) {
            AcSeccion s = lista.get(i);
            int totalMatriculadosSeccion = daoMatricula.numeroMatriculadosporSeccion(s.getId());
            LOG.trace("Matriculados en la seccion '{}': {}", s.getId(), totalMatriculadosSeccion);
            int matriculados = s.getSecVacantes() - totalMatriculadosSeccion;
            cmbSecciones[i + 1] = new SelectItem(s.getId(), s.getSecNombre() + "-" + matriculados);
        }
        return cmbSecciones;
    }

    public List<MbMatricula> getListaCursos() {
        return listaCursos;
    }

    public void setListaCursos(List<MbMatricula> listaCursos) {
        this.listaCursos = listaCursos;
    }

    public int getMpRowCursoId() {
        return mpRowCursoId;
    }

    public void setMpRowCursoId(int mpRowCursoId) {
        this.mpRowCursoId = mpRowCursoId;
    }

    public String getMpRowCursoCode() {
        return mpRowCursoCode;
    }

    public void setMpRowCursoCode(String mpRowCursoCode) {
        this.mpRowCursoCode = mpRowCursoCode;
    }

    public String getMpRowCursoName() {
        return mpRowCursoName;
    }

    public void setMpRowCursoName(String mpRowCursoName) {
        this.mpRowCursoName = mpRowCursoName;
    }

    public String getMpRowCursoCiclo() {
        return mpRowCursoCiclo;
    }

    public void setMpRowCursoCiclo(String mpRowCursoCiclo) {
        this.mpRowCursoCiclo = mpRowCursoCiclo;
    }

    public String getMpRowCursoCreditos() {
        return mpRowCursoCreditos;
    }

    public void setMpRowCursoCreditos(String mpRowCursoCreditos) {
        this.mpRowCursoCreditos = mpRowCursoCreditos;
    }

    public String getMpRowCursoTipo() {
        return mpRowCursoTipo;
    }

    public void setMpRowCursoTipo(String mpRowCursoTipo) {
        this.mpRowCursoTipo = mpRowCursoTipo;
    }

    public int getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(int seccionId) {
        this.seccionId = seccionId;
    }

    public SelectItem[] getComboSecciones() {
        return comboSecciones;
    }

    public void setComboSecciones(SelectItem[] comboSecciones) {
        this.comboSecciones = comboSecciones;
    }

    public String getRowAluCodigo() {
        return rowAluCodigo;
    }

    public void setRowAluCodigo(String rowAluCodigo) {
        this.rowAluCodigo = rowAluCodigo;
    }

    public String getRowAluPaterno() {
        return rowAluPaterno;
    }

    public void setRowAluPaterno(String rowAluPaterno) {
        this.rowAluPaterno = rowAluPaterno;
    }

    public String getRowAluMaterno() {
        return rowAluMaterno;
    }

    public void setRowAluMaterno(String rowAluMaterno) {
        this.rowAluMaterno = rowAluMaterno;
    }

    public String getRowAluNombre() {
        return rowAluNombre;
    }

    public void setRowAluNombre(String rowAluNombre) {
        this.rowAluNombre = rowAluNombre;
    }

    // Sprint 2 / Initiative 2 - same blocking rule as seleccionar()'s per-row debt check
    // (sections 388-399), reusable for a single student id at submission time.
    private boolean esDeudaBloqueante(AdAlumnoTarifa tarifa, Calendar ahora) {
        if (tarifa.getAlutarFechaProrroga() == null) {
            // No effective deadline recorded (row written by another app): an unpaid
            // tariff without a deadline blocks rather than slipping through unnoticed.
            return !tarifa.getAlutarEstado().equals("2");
        }
        var fecPro = Calendar.getInstance();
        fecPro.setTime(tarifa.getAlutarFechaProrroga());
        return !tarifa.getAlutarEstado().equals("2") && ahora.getTimeInMillis() > fecPro.getTimeInMillis();
    }

    private boolean tieneDeudaBloqueante(int alumnoId) {
        HSAlumnoTarifaDAO daoTarifa = ServiceFinder.findBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class);
        var ahora = Calendar.getInstance();
        ahora.setTime(new Date());
        for (AdAlumnoTarifa tarifa : daoTarifa.seleccionarAlumnoTarifa(alumnoId)) {
            if (esDeudaBloqueante(tarifa, ahora)) {
                return true;
            }
        }
        return false;
    }

    public void matricular(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        if (!daoAlumno.esElegibleParaMatricula(this.getStudentId())) {
            LOG.warn("Student {} blocked from enrollment: no longer eligible (AluTipo/AluEstado changed since search)", studentId);
            this.setOncomplete("javascript:alert('" + MSG_NO_ELEGIBLE + "')");
            return;
        }

        if (tieneDeudaBloqueante(this.getStudentId())) {
            LOG.warn("Student {} blocked from enrollment due to outstanding debt", studentId);
            this.setOncomplete("javascript:alert('" + MSG_DEUDA_PENDIENTE + "')");
            return;
        }

        HSSeccionDAO daos = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
        HSHorarioDAO daoh = ServiceFinder.findBean("springHibernateDaoHorario", HSHorarioDAO.class);

        var matricula = new AcMatricula();
        matricula.setMatActivo("1");
        matricula.setMatFecha(new Date());

        BeanUser usu = ServiceFinder.getLoggedUser();
        matricula.setUsuId(usu.getRowUsuId());

        matricula.setSemId(rowSemestreId);

        var alumno = new AcAlumno();
        alumno.setId(this.getStudentId());
        matricula.setAlu(alumno);

        LOG.debug("Alumno {} matriculado en el semestre: {}", studentId, rowSemestreId);

        // Sprint 2 / Initiative 2 + 3: single pass to build the enrollment + collect everything
        // the remaining validation steps need (course ids for prerequisites, vacancy outcome for
        // capacity) - no extra per-section queries beyond what already ran here before. The
        // decisions below are then evaluated in order, each returning immediately on the first
        // failure (fail-fast), so a later, more expensive check (schedule conflict) never runs
        // once an earlier one has already failed.
        Set<AcMatriculaCurso> matriculaCursos = new LinkedHashSet<>();
        List<Integer> plancurIds = new ArrayList<>();
        var mensaje = "";
        int vacantesVigentes;
        int vacantesUtilizados;
        int vacantes;
        var contador = 0;
        var vSecIds = new ArrayList<Integer>();
        for (MbMatricula bm : this.getListaCursos()) {
            if (bm.getSeccionId() != 0) {
                var matricurso = new AcMatriculaCurso();
                matricurso.setMat(matricula);
                matricurso.setMatcurActivo("1");

                var seccion = new AcSeccion();
                seccion.setId(bm.getSeccionId());
                vSecIds.add(bm.getSeccionId());
                matricurso.setSec(seccion);

                AcSeccion sec = daos.seleccionarSeccion(bm.getSeccionId());
                plancurIds.add(sec.getCurape().getPlancur().getId());
                vacantes = sec.getSecVacantes();
                vacantesUtilizados = daoMatricula.numeroMatriculadosporSeccionSinAlumno(bm.getSeccionId(), alumno.getId());
                vacantesVigentes = vacantes - vacantesUtilizados;
                if (vacantesVigentes <= 0) {
                    contador++;
                    mensaje = sec.getSecNombre() + " - " + sec.getCurape().getPlancur().getCur().getCurNombre();
                }
                matriculaCursos.add(matricurso);
            }
        }

        if (matriculaCursos.isEmpty()) {
            this.setOncomplete("javascript:alert('Debe seleccionar las secciones correspondientes.')");
            return;
        }

        matricula.setMatTipo(CAT_TIPO_MATRICULA_REGULAR);

        List<Integer> cursosNoPermitidos = cursosNoPermitidos(alumno.getId(), rowSemestreId, this.getParamEspId(), plancurIds);
        if (!cursosNoPermitidos.isEmpty()) {
            LOG.warn("Student {} blocked from enrollment: prerequisites not satisfied for course(s) {}", studentId, cursosNoPermitidos);
            this.setOncomplete("javascript:alert('" + MSG_PRERREQUISITOS_NO_CUMPLIDOS + "')");
            return;
        }

        if (contador != 0) {
            LOG.warn("Student {} blocked from enrollment due to no vacancies: '{}'", studentId, mensaje);
            this.setOncomplete("javascript:alert('no hay vacantes para " + mensaje + "')");
            return;
        }

        setListaDuplicadoHor(daoh.duplicidadHoraria(vSecIds, rowSemestreId));
        LOG.info("En total de horarios duplicados: {} for sections '{}'", listaDuplicadoHor.size(), vSecIds);
        if (!listaDuplicadoHor.isEmpty()) {
            LOG.warn("Student {} blocked from enrollment due to schedule conflict: {} conflicting section(s) for {}",
                    studentId, listaDuplicadoHor.size(), vSecIds);
            this.setOncomplete("javascript:Richfaces.showModalPanel('dupHorario');");
            return;
        }

        matricula.setAcMatriculaCursos(matriculaCursos);

        int matriculaId;
        try {
            matriculaId = enrollmentService.registerEnrollment(matricula, rowTipoMatriculaId);
        } catch (NoVacancyException e) {
            // The pre-check above passed, but a concurrent enrollment claimed the last seat
            // before this transaction acquired the section lock (BUG-02).
            AcSeccion full = daos.seleccionarSeccion(e.getSectionId());
            LOG.warn("Student {} lost the race for the last vacancy in section {}", studentId, e.getSectionId());
            this.setOncomplete("javascript:alert('no hay vacantes para "
                    + full.getSecNombre() + " - " + full.getCurape().getPlancur().getCur().getCurNombre() + "')");
            return;
        }
        this.setRowMatriculaId(matriculaId);
        LOG.info("Matricula saved {}", this.getRowMatriculaId());
        this.setOncomplete("alert('Se registro la matricula correctamente');Richfaces.hideModalPanel('mp')");

        if (matriculaId != 0) {
            if (CAT_TIPO_MATRICULA_REGULAR.equals(rowTipoMatriculaId)) {
                this.setOncomplete("alert('Se rectificó la matricula correctamente');Richfaces.hideModalPanel('mpRectificar')");
            } else if (CAT_TIPO_MATRICULA_PRE_MATRICULA.equals(rowTipoMatriculaId)) {
                this.setOncomplete("alert('Se registro la matricula correctamente');Richfaces.hideModalPanel('mp')");
            }
        }
    }

    // Sprint 2 / Initiative 2 (step 3) - re-validates that every NEWLY selected course is one the
    // student is allowed to take (prerequisites satisfied + not already attempted), reusing the
    // exact same query pattern cursosAbiertos() already uses to build the UI's course list.
    // Courses the student already has in their current-semester enrollment are excluded from this
    // check: cursosAbiertos() is keyed off published Acta history (approved/failed), not off the
    // in-progress current enrollment, so a rectification (or a Pre-Matricula confirmation, which
    // also writes through matricular()) that keeps an already-locked-in course unchanged must not
    // be re-blocked by that gap - only genuinely new course substitutions are re-validated. Checks
    // both REGULAR and PRE_MATRICULA existing enrollments for that semester: matricular() is the
    // write path for both rectifying a Regular enrollment AND confirming a Pre-Matricula into one
    // (it always writes MatTipo=REGULAR, regardless of which one preceded it), so excluding only
    // findRegularEnrollment would treat every already-locked-in Pre-Matricula course as "new" and
    // wrongly re-validate it. Two extra single-row calls per submission (existing EnrollmentService
    // methods, already joined-fetched), not per section.
    private List<Integer> cursosNoPermitidos(int alumnoId, int semestreId, int especialidad, List<Integer> plancurIds) {
        if (plancurIds.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> yaMatriculados = Stream.concat(
                enrollmentService.findRegularEnrollment(alumnoId, semestreId).stream(),
                enrollmentService.findPreEnrollment(alumnoId, semestreId).stream())
                .flatMap(m -> m.getAcMatriculaCursos().stream())
                .map(mc -> mc.getSec().getCurape().getPlancur().getId())
                .collect(Collectors.toSet());

        List<Integer> nuevos = plancurIds.stream()
                .filter(id -> !yaMatriculados.contains(id))
                .collect(Collectors.toList());

        if (nuevos.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> abiertos = cursosAbiertos(alumnoId, especialidad).stream()
                .map(AcPlanCurso::getId)
                .collect(Collectors.toSet());
        return nuevos.stream().filter(id -> !abiertos.contains(id)).collect(Collectors.toList());
    }

    public void reservarMatricula(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        var idAlumno = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_id")).getValue().toString());
        if (!daoAlumno.esElegibleParaMatricula(idAlumno)) {
            LOG.warn("Student {} blocked from reservation: no longer eligible (AluTipo/AluEstado changed since search)", idAlumno);
            this.setOncomplete("javascript:alert('" + MSG_NO_ELEGIBLE + "')");
            return;
        }

        var matricula = new AcMatricula();
        matricula.setMatActivo("1");
        matricula.setMatFecha(new Date());
        matricula.setMatCodigo("");
        matricula.setMatTipo(CAT_TIPO_MATRICULA_RESERVA);

        var matriculaSemestre = Integer.parseInt(((UIParameter) event.getComponent().findComponent(P_ID_SEMESTRE)).getValue().toString());
        matricula.setSemId(matriculaSemestre);

        BeanUser usu = ServiceFinder.getLoggedUser();
        matricula.setUsuId(usu.getRowUsuId());

        var alumno = new AcAlumno();
        alumno.setId(idAlumno);
        matricula.setAlu(alumno);

        var matriculaIdToCancel = ((UIParameter) event.getComponent().findComponent(P_ID_MATRICULA)).getValue().toString();
        int newMatriculaId = enrollmentService.reserveEnrollment(matricula,
                matriculaIdToCancel.equals("0") ? 0 : Integer.parseInt(matriculaIdToCancel));
        this.setRowMatriculaId(newMatriculaId);
        LOG.info("Matricula saved {}", this.getRowMatriculaId());
    }

    public void verCursos(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter flag = (UIParameter) event.getComponent().findComponent("flag");
        if (flag.getValue().toString().equals("0")) {
            this.setRenderedSubTable(true);
            this.setFlagVer("1");
            this.setImagen(ACTION_IMAGES_UP);
            verCursosMatriculados(rowMatriculaId);
        } else {
            this.setRenderedSubTable(false);
            this.setFlagVer("0");
            this.setImagen(ACTION_IMAGES_DOWN);
            this.getListaCursosMatriculado().clear();
        }
    }

    public String getFlagVer() {
        return flagVer;
    }

    public void setFlagVer(String flagVer) {
        this.flagVer = flagVer;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isRenderedSubTable() {
        return renderedSubTable;
    }

    public void setRenderedSubTable(boolean renderedSubTable) {
        this.renderedSubTable = renderedSubTable;
    }

    public String getDetCursoCodigo() {
        return detCursoCodigo;
    }

    public void setDetCursoCodigo(String detCursoCodigo) {
        this.detCursoCodigo = detCursoCodigo;
    }

    public String getDetCursoNombre() {
        return detCursoNombre;
    }

    public void setDetCursoNombre(String detCursoNombre) {
        this.detCursoNombre = detCursoNombre;
    }

    public String getDetCursoCreditos() {
        return detCursoCreditos;
    }

    public void setDetCursoCreditos(String detCursoCreditos) {
        this.detCursoCreditos = detCursoCreditos;
    }

    public String getDetSeccionCodigo() {
        return detSeccionCodigo;
    }

    public void setDetSeccionCodigo(String detSeccionCodigo) {
        this.detSeccionCodigo = detSeccionCodigo;
    }

    public List<MbMatricula> getListaCursosMatriculado() {
        return listaCursosMatriculado;
    }

    public void setListaCursosMatriculado(List<MbMatricula> listaCursosMatriculado) {
        this.listaCursosMatriculado = listaCursosMatriculado;
    }

    public void verCursosMatriculados(int matriculaId) {
        LOG.debug("Opening enrolled courses {}", matriculaId);
        AcMatricula matricula = daoMatricula.listarCursosPorMatricula(matriculaId);

        if (matricula != null) {
            Set<AcMatriculaCurso> matriculaCurso = matricula.getAcMatriculaCursos();
            List<AcMatriculaCurso> list = Collections.synchronizedList(new LinkedList<>(matriculaCurso));
            List<MbMatricula> a = new ArrayList<>();
            if (!list.isEmpty()) {
                for (AcMatriculaCurso acMatriculaCurso : list) {
                    var matriculacurso = new MbMatricula();
                    matriculacurso.setDetCursoId(acMatriculaCurso.getSec().getId());
                    matriculacurso.setDetCursoNombre(acMatriculaCurso.getSec().getCurape().getPlancur().getCur().getCurNombre());
                    matriculacurso.setDetCursoCodigo(acMatriculaCurso.getSec().getCurape().getPlancur().getPlancurCodigo());
                    matriculacurso.setDetCursoCreditos(acMatriculaCurso.getSec().getCurape().getPlancur().getPlancurCredito());
                    matriculacurso.setDetSeccionCodigo(acMatriculaCurso.getSec().getSecCodigo());
                    a.add(matriculacurso);
                }
            }
            this.setListaCursosMatriculado(a);
        }
    }

    public void verDetalleDeuda(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        HSAlumnoTarifaDAO daoAlumnoTarifa = ServiceFinder.findBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class);
        HSEstructuraPagoDAO daoEstructuraPagos = ServiceFinder.findBean("springHibernateDaoEstructuraPagos", HSEstructuraPagoDAO.class);
        List<AdAlumnoTarifa> listAlu = daoAlumnoTarifa.seleccionarAlumnoTarifa(Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_id")).getValue().toString()));
        var fecActual = Calendar.getInstance();
        fecActual.setTime(new Date());
        MbMatricula bA;
        if (!listAlu.isEmpty()) {
            setDetalleDeuda(new ArrayList<>());

            this.setRowAluCodigo(listAlu.get(0).getAlu().getAluCodigo());
            String nombre = listAlu.get(0).getAlu().getAluAppaterno() + " "
                    + listAlu.get(0).getAlu().getAluApmaterno() + ", "
                    + listAlu.get(0).getAlu().getAluNombres();
            this.setRowAluNombre(nombre);

            for (AdAlumnoTarifa adAlumnoTarifa : listAlu) {
                var fecPro = Calendar.getInstance();
                fecPro.setTime(adAlumnoTarifa.getAlutarFechaProrroga());

                if (!(adAlumnoTarifa.getAlutarEstado().equals("2"))
                        && fecActual.getTimeInMillis() > fecPro.getTimeInMillis()) {
                    bA = new MbMatricula();
                    int id = adAlumnoTarifa.getEstpagdet().getId();
                    int epdId = daoEstructuraPagos.seleccionarUnaEstructuraPagosDet(id).getEstpag().getId();
                    bA.setMpRowDeudaSemestre(daoEstructuraPagos.seleccionarUnaEstructuraPagos(epdId).getSem().getSemCodigo());
                    bA.setMpRowDeudaConceptoPago(adAlumnoTarifa.getEstpagdet().getEstpagdetNombre());
                    bA.setMpRowDeudaFechaPago(adAlumnoTarifa.getAlutarFechaPago().toString());
                    bA.setMpRowDeudaFechaProrroga(adAlumnoTarifa.getAlutarFechaProrroga().toString());
                    detalleDeuda.add(bA);
                }
            }
            LOG.info("Total deudas encontradas {}", detalleDeuda.size());
            this.setOncomplete("javascript:Richfaces.showModalPanel('mp6')");
        }
    }


    public String getRowSemestreCode() {
        return rowSemestreCode;
    }

    public void setRowSemestreCode(String rowSemestreCode) {
        this.rowSemestreCode = rowSemestreCode;
    }

    public SelectItem[] getComboSemestres() {
        return comboSemestres;
    }

    public void setComboSemestres(SelectItem[] comboSemestres) {
        this.comboSemestres = comboSemestres;
    }

    public int getParamSemId() {
        return paramSemId;
    }

    public void setParamSemId(int paramSemId) {
        this.paramSemId = paramSemId;
    }

    public int getDetCursoId() {
        return detCursoId;
    }

    public void setDetCursoId(int detCursoId) {
        this.detCursoId = detCursoId;
    }

    public void eliminarMatricula(ActionEvent event) {
        var matriculaId = Integer.parseInt(((UIParameter) event.getComponent().findComponent(P_ID_MATRICULA)).getValue().toString());
        LOG.debug("[{}] Eliminando matricula: {}", event.getPhaseId(), matriculaId);
        daoMatricula.eliminarMatricula(matriculaId);
    }

    public int getRowMatriculaId() {
        return rowMatriculaId;
    }

    public void setRowMatriculaId(int rowMatriculaId) {
        this.rowMatriculaId = rowMatriculaId;
    }

    public List<MbMatricula> getDetalleDeuda() {
        return detalleDeuda;
    }

    public void setDetalleDeuda(List<MbMatricula> detalleDeuda) {
        this.detalleDeuda = detalleDeuda;
    }

    public String getMpRowDeudaSemestre() {
        return mpRowDeudaSemestre;
    }

    public void setMpRowDeudaSemestre(String mpRowDeudaSemestre) {
        this.mpRowDeudaSemestre = mpRowDeudaSemestre;
    }

    public String getMpRowDeudaConceptoPago() {
        return mpRowDeudaConceptoPago;
    }

    public void setMpRowDeudaConceptoPago(String mpRowDeudaConceptoPago) {
        this.mpRowDeudaConceptoPago = mpRowDeudaConceptoPago;
    }

    public String getMpRowDeudaFechaPago() {
        return mpRowDeudaFechaPago;
    }

    public void setMpRowDeudaFechaPago(String mpRowDeudaFechaPago) {
        this.mpRowDeudaFechaPago = mpRowDeudaFechaPago;
    }

    public String getMpRowDeudaFechaProrroga() {
        return mpRowDeudaFechaProrroga;
    }

    public void setMpRowDeudaFechaProrroga(String mpRowDeudaFechaProrroga) {
        this.mpRowDeudaFechaProrroga = mpRowDeudaFechaProrroga;
    }

    public boolean isVerDeuda() {
        return verDeuda;
    }

    public void setVerDeuda(boolean verDeuda) {
        this.verDeuda = verDeuda;
    }

    public boolean isVerMatricular() {
        return verMatricular;
    }

    public void setVerMatricular(boolean verMatricular) {
        this.verMatricular = verMatricular;
    }

    public boolean isVerRectificar() {
        return verRectificar;
    }

    public void setVerRectificar(boolean verRectificar) {
        this.verRectificar = verRectificar;
    }

    public boolean isVerEliminar() {
        return verEliminar;
    }

    public void setVerEliminar(boolean verEliminar) {
        this.verEliminar = verEliminar;
    }

    public boolean isVerImprimirFicha() {
        return verImprimirFicha;
    }

    public void setVerImprimirFicha(boolean verImprimirFicha) {
        this.verImprimirFicha = verImprimirFicha;
    }

    public boolean isVerReservar() {
        return verReservar;
    }

    public void setVerReservar(boolean verReservar) {
        this.verReservar = verReservar;
    }

    public boolean isVerAlerta() {
        return verAlerta;
    }

    public void setVerAlerta(boolean verAlerta) {
        this.verAlerta = verAlerta;
    }

    public String getRowTipoMatriculaId() {
        return rowTipoMatriculaId;
    }

    public void setRowTipoMatriculaId(String rowTipoMatriculaId) {
        this.rowTipoMatriculaId = rowTipoMatriculaId;
    }

    public boolean isVerImprimirConsolidado() {
        return verImprimirConsolidado;
    }

    public void setVerImprimirConsolidado(boolean verImprimirConsolidado) {
        this.verImprimirConsolidado = verImprimirConsolidado;
    }

    public void openModalforAmpliaciones(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        MbMatricula attr = (MbMatricula) event.getComponent().getAttributes().get("student");
        AcAmpliaciones amp = daoMatricula.findAmpliacion(attr.getStudentId(), attr.getRowSemestreId());
        if (amp == null) {
            this.setDetCursoCreditos("0");
        } else {
            this.setDetCursoCreditos(amp.getAmpCreditos());
        }
    }
}
