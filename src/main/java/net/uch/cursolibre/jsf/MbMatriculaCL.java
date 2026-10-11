package net.uch.cursolibre.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.cursolibre.dao.*;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

public class MbMatriculaCL {

    private static final Logger LOG = LoggerFactory.getLogger(MbMatriculaCL.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    private final HSMatriculaCLDAO daoCLMatricula = ServiceFinder.findBean("springHibernateDaoCLMatricula", HSMatriculaCLDAO.class);
    private final HSAlumnoTarifaCLDAO daoCLAlumnoTarifa = ServiceFinder.findBean("springHibernateDaoCLAlumnoTarifa", HSAlumnoTarifaCLDAO.class);

    private int nivelEliminar;
    // CAMPOS
    private String paramAluCodigo;
    private String paramAluAppaterno;
    private String paramAluApmaterno;
    private String paramAluNombres;
    private List<MbMatriculaCL> matriculas = new ArrayList<>();
    private int detMatriculaId;
    private Date detMatFecha;
    private String detMatTipoCode;
    private String detMatTipoValue;
    private String rowAluTipo;
    private String detTaller;
    private String detFechaInicio;
    private String detSeccion;
    private String mensajeEliminar;
    private String oncomplete;
    private boolean verDetalle;
    private String rowImagenMostrar;
    private String rowTextoMostrar;
    private String suggestion;
    private int studentId;
    private String studentCode;
    private String studentNames;
    private List<MbMatriculaCL> secciones;
    private boolean verCursos;
    private int seccionId;
    private String mpModulo;
    private String mpRowCurso;
    private String mpRowSeccion;
    private int mpRowNroMat;
    private String mpRowNroMatStyle;
    private int seccionVacMax;
    private String mpRowTtMessage;
    private boolean mpRowVerMessage;
    private Date mpRowFecInicio;
    private float mpRowMontoBase;
    private float mpRowMontoPagar;
    private boolean mpRowAgregar;
    private boolean mpRowEditar;
    private float mpRowMontoEditar;
    private int mpAreaId = 0;
    private SelectItem[] mpAreas;
    private int mpModId = 0;
    private List<SelectItem> mpModulos;
    private int mpEstpagId;
    private List<SelectItem> mpEstructurasPago;
    //campos para cl_alumno_tarifa//
    private int mpRowEstpagdetId;
    private int mpRowConpagId;
    private Date mpRowFechaPago;
    private Date mpRowFechaProrroga;
    private String mpRowAlutarTipo;
    private String mensajeError;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSAreaDAO dao = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);
        List<ClArea> areas = dao.seleccionarArea("");

        this.setMpAreas(areas.stream()
                .map(a -> new SelectItem(a.getAreId(), a.getAreDescripcion()))
                .toArray(SelectItem[]::new));

        setMpModulos(new ArrayList<>());
        setMpEstructurasPago(new ArrayList<>());
        setSecciones(new ArrayList<>());
    }

    // METODOS
    private boolean casillasVacias() {
        return this.getParamAluCodigo().trim().length() == 0
                && this.getParamAluAppaterno().trim().length() == 0
                && this.getParamAluApmaterno().trim().length() == 0
                && this.getParamAluNombres().trim().length() == 0;
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMatriculas(new ArrayList<>());
        this.setOncomplete("");
        if (casillasVacias()) {
            this.setOncomplete("alert('Ingrese algun parametro para buscar');");
            return;
        }
        HSCatalogoDAO catalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        HSAlumnoCLDAO daoAlumno = ServiceFinder.findBean("springHibernateDaoCLAlumno", HSAlumnoCLDAO.class);

        List<ClAlumno> tAlumnos = daoAlumno.seleccionarAlumnosLibres(this.getParamAluCodigo(), this.getParamAluAppaterno(), this.getParamAluApmaterno(), this.getParamAluNombres());

        for (ClAlumno alu : tAlumnos) {
            var master = new MbMatriculaCL();

            master.setStudentId(alu.getAluId());
            master.setParamAluCodigo(alu.getAluCodigo());

            String tipo = alu.getAluTipo();
            switch (tipo) {
                case "057002":
                    master.setRowAluTipo("INTERNO");
                    break;
                case "057003":
                    master.setRowAluTipo("DOCENTE");
                    break;
                case "057004":
                    master.setRowAluTipo("ADMINISTRATIVO");
                    break;
                default:
                    master.setRowAluTipo("EXTERNO");
                    break;
            }
            master.setParamAluNombres(alu.getAluNombres().toUpperCase().trim());
            master.setParamAluAppaterno(alu.getAluAppaterno().toUpperCase().trim());
            master.setParamAluApmaterno(alu.getAluApmaterno().toUpperCase().trim());

            List<ClMatricula> tMatriculas = daoCLMatricula.seleccionarMatriculas(alu.getAluId());
            for (ClMatricula mat : tMatriculas) {
                var child = new MbMatriculaCL();

                child.setDetMatriculaId(mat.getMatId());
                child.setDetMatFecha(mat.getMatFecha());
                child.setDetMatTipoCode(mat.getMatTipo());
                child.setDetMatTipoValue(catalogo.seleccionarDescripcion(mat.getMatTipo()));

                var tmpTaller = "";
                var tmpSeccion = "";
                var tmpInicio = "";
                for (ClMatriculaTaller mattal : mat.getClMatriculaTallers()) {
                    LOG.trace("mat.id {} ::: is active? {}", child.getDetMatriculaId(), mattal.getMattalActivo());
                    tmpTaller = tmpTaller.concat("- " + mattal.getClSeccion().getClTallerAperturado().getTalapeDescripcion() + "<br>");
                    tmpSeccion = tmpSeccion.concat("- " + mattal.getClSeccion().getSecNombre() + "<br>");
                    Date fInicio = mattal.getClSeccion().getSecFinicio();
                    if (fInicio != null) {
                        tmpInicio = tmpInicio.concat("- " + dateFormat.format(fInicio) + "<br>");
                    } else {
                        tmpInicio = tmpInicio.concat("- No especifica<br>");
                    }
                }
                child.setDetTaller(tmpTaller);
                child.setDetSeccion(tmpSeccion);
                child.setDetFechaInicio(tmpInicio);

                master.addMatriculas(child);
            }//FIN FOR SUB-TABLE MATRICULAS
            master.setVerDetalle(false);
            master.setRowImagenMostrar(ACTION_IMAGES_DOWN);
            master.setRowTextoMostrar("Mostrar Detalle");
            this.addMatriculas(master);
        }//FIN FOR TABLE ALUMNOS
        LOG.info("Total de alumnos con matricula {}", matriculas.size());
    }

    public void mostrarDetalle(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
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

    public void abrirMatricula(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        this.setSuggestion("");
        this.setStudentId(0);
        this.setStudentCode("");
        this.setStudentNames("");

        this.getSecciones().clear();
        this.setMpAreaId(0);
        this.setMpModId(0);
        this.setMpEstpagId(0);
        this.setMpModulo("");
        this.setVerCursos(false);
    }

    public List<MbMatriculaCL> autocomplete(Object suggest) {
        if ("null".equals(suggest) || ObjectUtils.isEmpty(suggest)) {
            return Collections.emptyList();
        }
        String pref = (String) suggest;
        List<MbMatriculaCL> lAlumnos = new ArrayList<>();
        List<ClAlumno> alus;
        LOG.debug("Looking for student '{}'", pref);
        HSAlumnoCLDAO dao = ServiceFinder.findBean("springHibernateDaoCLAlumno", HSAlumnoCLDAO.class);
        if (pref.matches("^\\d*")) {
            alus = dao.seleccionarAlumnosPorCodigo(pref);
        } else {
            alus = dao.seleccionarAlumnosPorApellidos(pref);
        }
        for (ClAlumno alu : alus) {
            var al = new MbMatriculaCL();

            al.setStudentId(alu.getAluId());
            al.setStudentCode(alu.getAluCodigo());
            al.setStudentNames(alu.getAluAppaterno().toUpperCase().trim() + " "
                    + alu.getAluApmaterno().toUpperCase().trim() + " "
                    + alu.getAluNombres().toUpperCase().trim());
            lAlumnos.add(al);
        }
        return lAlumnos;
    }

    public void verificarPagosMatricula(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMensajeEliminar("");
        this.setOncomplete("");

        detMatriculaId = Integer.parseInt(event.getComponent().getAttributes().get("p_mat_id_delete").toString());
        studentId = Integer.parseInt(event.getComponent().getAttributes().get("p_alu_id_delete").toString());
        nivelEliminar = 0;

        var flag = 0;
        List<ClAlumnoTarifa> alutars = daoCLAlumnoTarifa.seleccionarTarifasXMatricula(detMatriculaId, studentId);
        if (!alutars.isEmpty()) {
            for (ClAlumnoTarifa alutar : alutars) {
                if (daoCLAlumnoTarifa.existenPagosMatricula(alutar.getClAlumno().getAluId(), alutar.getAlutarId())) {
                    flag++;
                }
            }
            if (flag > 0) {
                this.setMensajeEliminar("La matrícula tiene un pago válido asociado, SOLO se eliminará la matrícula. ¿Desea continuar?");
                nivelEliminar = 1;
            } else {
                this.setMensajeEliminar("¿Está seguro de Eliminar la matrícula?");
                nivelEliminar = 2;
            }
        } else {
            this.setMensajeEliminar("¿Está seguro de Eliminar la matrícula?");
            nivelEliminar = 1;
        }
        this.setOncomplete("Richfaces.showModalPanel('mp2')");
    }

    public void anularMatricula(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");

        daoCLMatricula.anularMatricula(detMatriculaId);
        if (nivelEliminar != 1) {
            List<ClAlumnoTarifa> alutars = daoCLAlumnoTarifa.seleccionarTarifasXMatricula(detMatriculaId, studentId);
            List<Integer> alutarIds = new ArrayList<>(alutars.size());
            for (ClAlumnoTarifa alutar : alutars) {
                alutarIds.add(alutar.getAlutarId());
            }
            daoCLAlumnoTarifa.eliminarAlumnosTarifa(alutarIds);
        }
        this.setOncomplete("alert('Proceso de Eliminación Exitoso.');"
                + "Richfaces.hideModalPanel('mp2')");
    }

    public void mostrarCursos(ActionEvent event) {
        LOG.debug("[{}] Loading courses for module '{}' | payment structure '{}'", event.getPhaseId(), this.mpModId, this.mpEstpagId);

        this.mensajeError = "";
        this.mpModulo = "";
        this.verCursos = false;
        if (this.getMpEstpagId() == 0) {
            return;
        }

        HSHorarioDAO daoH = ServiceFinder.findBean("springHibernateDaoCLHorario", HSHorarioDAO.class);
        HSEstructuraPagoDAO daoEP = ServiceFinder.findBean("springHibernateDaoCLEstructuraPago", HSEstructuraPagoDAO.class);
        HSModuloDAO daoM = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
        HSSeccionCLDAO daoS = ServiceFinder.findBean("springHibernateDaoCLSeccion", HSSeccionCLDAO.class);

        List<ClSeccion> listaSecModulo = daoS.seleccionarSeccionesXModulo(this.getMpModId());
        if (listaSecModulo.isEmpty()) {
            this.setMensajeError("No hay secciones abiertas o con horario asignado para proceder a matricular.");
        } else {
            List<ClEstructuraPagosDetalle> detalles = daoEP.seleccionarDetalle(this.getMpEstpagId());

            if (detalles.isEmpty()) {
                this.setMensajeError("No existen estructuras de pago generadas.");
            } else {
                this.setMpModulo(daoM.buscarModulo(this.getMpModId()).getModDescripcion());
                this.getSecciones().clear();

                for (ClSeccion clSeccion : listaSecModulo) {
                    var sec = new MbMatriculaCL();
                    if (!CollectionUtils.isEmpty(clSeccion.getClHorarias())) {
                        int matriculadosSeccion = daoH.matriculadosSeccion(clSeccion.getSecId()).size();

                        sec.setSeccionId(clSeccion.getSecId());
                        sec.setMpModulo(clSeccion.getClTallerAperturado().getClTaller().getClCurso().getClModulo().getModDescripcion());
                        sec.setMpRowCurso(clSeccion.getClTallerAperturado().getTalapeDescripcion());
                        sec.setMpRowSeccion(clSeccion.getSecNombre());
                        sec.setMpRowFecInicio(clSeccion.getSecFinicio());
                        sec.setMpRowNroMat(matriculadosSeccion);
                        sec.setMpRowTtMessage("");
                        if (matriculadosSeccion >= clSeccion.getSecVacMax() - 10) {
                            sec.setMpRowNroMatStyle("font-size: 17px; font-weight: bold; color: red;");
                            sec.setMpRowTtMessage("La sección se encuentra cerca del máximo de vacantes(" + clSeccion.getSecVacMax() + "). Es necesario generar una nueva sección.");
                            sec.setMpRowVerMessage(true);
                        } else {
                            sec.setMpRowNroMatStyle("font-size: 17px; font-weight: bold;");
                            sec.setMpRowVerMessage(false);
                        }
                        sec.setSeccionVacMax(clSeccion.getSecVacMax());
                        sec.setMpRowMontoBase(0);
                        sec.setMpRowMontoPagar(0);
                        sec.setMpRowMontoEditar(0);
                        sec.setMpRowAgregar(true);

                        var encontro = false;
                        int secModuloTalapeTalId = clSeccion.getClTallerAperturado().getClTaller().getTalId();
                        for (ClEstructuraPagosDetalle detalle : detalles) {
                            int detTalId = detalle.getTalId();
                            encontro = false;

                            if (detTalId == secModuloTalapeTalId) {
                                sec.setMpRowEstpagdetId(detalle.getEstpagdetId());
                                sec.setMpRowConpagId(detalle.getAdConceptoPago().getId());
                                sec.setMpRowFechaPago(clSeccion.getSecFinicio());
                                sec.setMpRowFechaProrroga(clSeccion.getSecFinicio());
                                sec.setMpRowMontoBase(detalle.getEstpagdetMonto());
                                sec.setMpRowMontoPagar(detalle.getEstpagdetMonto());
                                sec.setMpRowAlutarTipo(CAT_TIPO_CLIENTE_ALUMNO_CL);

                                encontro = true;
                                break;
                            }
                        }

                        if (encontro) {
                            sec.setMpRowEditar(false);
                            this.getSecciones().add(sec);
                        }
                    }
                }
                this.setVerCursos(true);
            }
        }
    }

    public void deseleccionarTodos(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.getSecciones().forEach(s -> s.setMpRowAgregar(false));
    }

    public void seleccionarTodos(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.getSecciones().forEach(s -> s.setMpRowAgregar(true));
    }

    public void editarMonto(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMpRowMontoEditar(this.getMpRowMontoPagar());
        this.setMpRowEditar(true);
    }

    public void guardarMonto(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMpRowMontoPagar(this.getMpRowMontoEditar());
        this.setMpRowEditar(false);
    }

    public void cancelarMonto(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMpRowMontoPagar(this.getMpRowMontoPagar());
        this.setMpRowEditar(false);
    }

    public void matricularSeleccionados(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");

        if (this.getStudentId() == 0) {
            this.setOncomplete("alert('Seleccione un alumno')");
        } else {
            HSHorarioDAO daoH = ServiceFinder.findBean("springHibernateDaoCLHorario", HSHorarioDAO.class);
            if (!this.getSecciones().isEmpty()) {
                var flag = 0;
                for (var i = 0; i < this.getSecciones().size(); i++) {
                    if (this.getSecciones().get(i).isMpRowAgregar()) {
                        flag++;
                    }
                }
                if (flag > 0) {
                    var flag2 = 0;
                    for (var i = 0; i < this.getSecciones().size(); i++) {
                        if (this.getSecciones().get(i).isMpRowAgregar() &&
                                this.getSecciones().get(i).getSeccionVacMax() <= daoH.matriculadosSeccion(this.getSecciones().get(i).getSeccionId()).size()) {
                            flag2++;
                        }
                    }
                    if (flag2 == 0) {
                        BeanUser usu = ServiceFinder.getLoggedUser();

                        var matricula = new ClMatricula();

                        var alu = new ClAlumno();
                        alu.setAluId(this.getStudentId());
                        matricula.setClAlumno(alu);

                        matricula.setMatCodigo("MAT-SA");
                        matricula.setMatTipo(CAT_TIPO_MATRICULA_PRE_MATRICULA);
                        matricula.setMatActivo("1");
                        matricula.setUsuId(usu.getRowUsuId());

                        var fec = new Date();
                        var time = new Timestamp(fec.getTime());
                        matricula.setMatFecha(time);

                        Set<ClMatriculaTaller> cursos = new LinkedHashSet<>();

                        List<ClAlumnoTarifa> alumnosTarifa = new ArrayList<>();
                        var matriculado = false;
                        var seccion = "";
                        for (var i = 0; i < this.getSecciones().size(); i++) {
                            if (this.getSecciones().get(i).isMpRowAgregar()) {
                                matriculado = daoCLMatricula.estaMatriculado(this.getStudentId(), this.getSecciones().get(i).getSeccionId());
                                seccion = this.getSecciones().get(i).getMpRowSeccion();

                                //secciones
                                var tal = new ClMatriculaTaller();
                                tal.setClMatricula(matricula);
                                tal.setMattalActivo("1");
                                tal.setMattalEstado("");
                                tal.setMattalObs("");

                                tal.setClSeccion(new ClSeccion(this.getSecciones().get(i).getSeccionId()));

                                cursos.add(tal);

                                //alumnoTarifas
                                var estpagdet = new ClEstructuraPagosDetalle();
                                estpagdet.setEstpagdetId(this.getSecciones().get(i).getMpRowEstpagdetId());

                                var alumnoTarifa = new ClAlumnoTarifa();
                                alumnoTarifa.setClEstructuraPagosDetalle(estpagdet);
                                alumnoTarifa.setClAlumno(alu);
                                alumnoTarifa.setAlutarMonto(this.getSecciones().get(i).getMpRowMontoPagar());
                                alumnoTarifa.setConpagId(this.getSecciones().get(i).getMpRowConpagId());
                                alumnoTarifa.setAlutarFechaPago(this.getSecciones().get(i).getMpRowFechaPago());
                                alumnoTarifa.setAlutarFechaProrroga(this.getSecciones().get(i).getMpRowFechaProrroga());
                                alumnoTarifa.setAlutarEstado("0");
                                alumnoTarifa.setAlutarActivo("1");
                                alumnoTarifa.setAlutarAluTipo(this.getSecciones().get(i).getMpRowAlutarTipo());
                                alumnoTarifa.setSecId(this.getSecciones().get(i).getSeccionId());
                                alumnoTarifa.setAlutarAluTipo(CAT_TIPO_CLIENTE_ALUMNO_CL);
                                alumnosTarifa.add(alumnoTarifa);
                            }
                        }
                        if (matriculado) {
                            this.setOncomplete("alert('Ya es matriculado en la seccion: " + seccion + ".');");
                        } else {
                            matricula.setClMatriculaTallers(cursos);
                            daoCLMatricula.insertarMatricula(matricula);

                            for (ClAlumnoTarifa clAlumnoTarifa : alumnosTarifa) {
                                clAlumnoTarifa.setMatId(matricula.getMatId());
                                daoCLAlumnoTarifa.generarAlumnoTarifa(clAlumnoTarifa);
                            }
                            this.setOncomplete("alert('Matrícula Satisfactoria.');Richfaces.hideModalPanel('mpMatricula')");
                        }
                    } else {
                        this.setOncomplete("alert('Una de las secciones ya llegó al máximo de vacantes. Es necesario generar una nueva sección.')");
                    }
                } else {
                    this.setOncomplete("alert('Debe seleccionar cursos para proceder a Matricular')");
                }
            }// Fin del IF de las secciones
        }
    }

    // GETTERS AND SETTERS
    public String getParamAluCodigo() {
        return paramAluCodigo;
    }

    public void setParamAluCodigo(String paramAluCodigo) {
        this.paramAluCodigo = paramAluCodigo;
    }

    public String getParamAluAppaterno() {
        return paramAluAppaterno;
    }

    public void setParamAluAppaterno(String paramAluAppaterno) {
        this.paramAluAppaterno = paramAluAppaterno;
    }

    public String getParamAluApmaterno() {
        return paramAluApmaterno;
    }

    public void setParamAluApmaterno(String paramAluApmaterno) {
        this.paramAluApmaterno = paramAluApmaterno;
    }

    public String getParamAluNombres() {
        return paramAluNombres;
    }

    public void setParamAluNombres(String paramAluNombres) {
        this.paramAluNombres = paramAluNombres;
    }

    public void addMatriculas(MbMatriculaCL tmp) {
        this.matriculas.add(tmp);
    }

    public List<MbMatriculaCL> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<MbMatriculaCL> matriculas) {
        this.matriculas = matriculas;
    }

    public int getDetMatriculaId() {
        return detMatriculaId;
    }

    public void setDetMatriculaId(int detMatriculaId) {
        this.detMatriculaId = detMatriculaId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Date getDetMatFecha() {
        return detMatFecha;
    }

    public void setDetMatFecha(Date detMatFecha) {
        this.detMatFecha = detMatFecha;
    }

    public String getDetMatTipoCode() {
        return detMatTipoCode;
    }

    public void setDetMatTipoCode(String detMatTipoCode) {
        this.detMatTipoCode = detMatTipoCode;
    }

    public String getRowAluTipo() {
        return rowAluTipo;
    }

    public void setRowAluTipo(String rowAluTipo) {
        this.rowAluTipo = rowAluTipo;
    }

    public String getDetMatTipoValue() {
        return detMatTipoValue;
    }

    public void setDetMatTipoValue(String detMatTipoValue) {
        this.detMatTipoValue = detMatTipoValue;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getDetTaller() {
        return detTaller;
    }

    public void setDetTaller(String detTaller) {
        this.detTaller = detTaller;
    }

    public String getDetFechaInicio() {
        return detFechaInicio;
    }

    public void setDetFechaInicio(String detFechaInicio) {
        this.detFechaInicio = detFechaInicio;
    }

    public boolean isVerDetalle() {
        return verDetalle;
    }

    public void setVerDetalle(boolean verDetalle) {
        this.verDetalle = verDetalle;
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

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentNames() {
        return studentNames;
    }

    public void setStudentNames(String studentNames) {
        this.studentNames = studentNames;
    }

    public List<MbMatriculaCL> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<MbMatriculaCL> secciones) {
        this.secciones = secciones;
    }

    public boolean isVerCursos() {
        return verCursos;
    }

    public void setVerCursos(boolean verCursos) {
        this.verCursos = verCursos;
    }

    public int getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(int seccionId) {
        this.seccionId = seccionId;
    }

    public String getMpModulo() {
        return mpModulo;
    }

    public void setMpModulo(String mpModulo) {
        this.mpModulo = mpModulo;
    }

    public String getMpRowCurso() {
        return mpRowCurso;
    }

    public void setMpRowCurso(String mpRowCurso) {
        this.mpRowCurso = mpRowCurso;
    }

    public Date getMpRowFecInicio() {
        return mpRowFecInicio;
    }

    public void setMpRowFecInicio(Date mpRowFecInicio) {
        this.mpRowFecInicio = mpRowFecInicio;
    }

    public float getMpRowMontoBase() {
        return mpRowMontoBase;
    }

    public void setMpRowMontoBase(float mpRowMontoBase) {
        this.mpRowMontoBase = mpRowMontoBase;
    }

    public float getMpRowMontoPagar() {
        return mpRowMontoPagar;
    }

    public void setMpRowMontoPagar(float mpRowMontoPagar) {
        this.mpRowMontoPagar = mpRowMontoPagar;
    }

    public boolean isMpRowAgregar() {
        return mpRowAgregar;
    }

    public void setMpRowAgregar(boolean mpRowAgregar) {
        this.mpRowAgregar = mpRowAgregar;
    }

    public boolean isMpRowEditar() {
        return mpRowEditar;
    }

    public void setMpRowEditar(boolean mpRowEditar) {
        this.mpRowEditar = mpRowEditar;
    }

    public float getMpRowMontoEditar() {
        return mpRowMontoEditar;
    }

    public void setMpRowMontoEditar(float mpRowMontoEditar) {
        this.mpRowMontoEditar = mpRowMontoEditar;
    }

    public String getMpRowSeccion() {
        return mpRowSeccion;
    }

    public void setMpRowSeccion(String mpRowSeccion) {
        this.mpRowSeccion = mpRowSeccion;
    }

    public String getDetSeccion() {
        return detSeccion;
    }

    public void setDetSeccion(String detSeccion) {
        this.detSeccion = detSeccion;
    }

    public int getMpRowNroMat() {
        return mpRowNroMat;
    }

    public void setMpRowNroMat(int mpRowNroMat) {
        this.mpRowNroMat = mpRowNroMat;
    }

    public String getMpRowNroMatStyle() {
        return mpRowNroMatStyle;
    }

    public void setMpRowNroMatStyle(String mpRowNroMatStyle) {
        this.mpRowNroMatStyle = mpRowNroMatStyle;
    }

    public int getSeccionVacMax() {
        return seccionVacMax;
    }

    public void setSeccionVacMax(int seccionVacMax) {
        this.seccionVacMax = seccionVacMax;
    }

    public String getMpRowTtMessage() {
        return mpRowTtMessage;
    }

    public void setMpRowTtMessage(String mpRowTtMessage) {
        this.mpRowTtMessage = mpRowTtMessage;
    }

    public boolean isMpRowVerMessage() {
        return mpRowVerMessage;
    }

    public void setMpRowVerMessage(boolean mpRowVerMessage) {
        this.mpRowVerMessage = mpRowVerMessage;
    }

    public int getMpAreaId() {
        return mpAreaId;
    }

    public void setMpAreaId(int mpAreaId) {
        this.mpAreaId = mpAreaId;
    }

    public SelectItem[] getMpAreas() {
        return mpAreas;
    }

    public void setMpAreas(SelectItem[] mpAreas) {
        this.mpAreas = mpAreas;
    }

    public void verModulos(ActionEvent event) {
        LOG.debug("[{}] Loading modules for area '{}'", event.getPhaseId(), mpAreaId);
        mpModulos.clear();
        mpEstructurasPago.clear();
        secciones.clear();
        mensajeError = "";
        mpModulo = "";
        verCursos = false;
        if (mpAreaId != 0) {
            HSModuloDAO dao = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
            List<ClModulo> modulos = dao.seleccionarModulos(this.mpAreaId, "");
            mpModulos.addAll(modulos.stream()
                    .map(m -> new SelectItem(m.getModId(), m.getModDescripcion()))
                    .collect(Collectors.toList()));
        }
    }

    public int getMpModId() {
        return mpModId;
    }

    public void setMpModId(int mpModId) {
        this.mpModId = mpModId;
    }

    public List<SelectItem> getMpModulos() {
        return mpModulos;
    }

    public void setMpModulos(List<SelectItem> mpModulos) {
        this.mpModulos = mpModulos;
    }

    public int getMpEstpagId() {
        return mpEstpagId;
    }

    public void setMpEstpagId(int mpEstpagId) {
        this.mpEstpagId = mpEstpagId;
    }

    public List<SelectItem> getMpEstructurasPago() {
        return mpEstructurasPago;
    }

    public void setMpEstructurasPago(List<SelectItem> mpEstructurasPago) {
        this.mpEstructurasPago = mpEstructurasPago;
    }

    public void verEstructurasPago(ActionEvent event) {
        LOG.debug("[{}] Loading payment structures for module '{}'", event.getPhaseId(), this.mpModId);
        mpEstructurasPago.clear();
        secciones.clear();
        mensajeError = "";
        mpModulo = "";
        verCursos = false;
        if (this.mpModId != 0) {
            HSEstructuraPagoDAO dao = ServiceFinder.findBean("springHibernateDaoCLEstructuraPago", HSEstructuraPagoDAO.class);
            List<ClEstructuraPagos> estructuraPagos = dao.seleccionarEstructurasXModulo(this.getMpModId());
            mpEstructurasPago.addAll(estructuraPagos.stream()
                    .map(ep -> new SelectItem(ep.getEstpagId(), ep.getEstpagNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public int getMpRowEstpagdetId() {
        return mpRowEstpagdetId;
    }

    public void setMpRowEstpagdetId(int mpRowEstpagdetId) {
        this.mpRowEstpagdetId = mpRowEstpagdetId;
    }

    public int getMpRowConpagId() {
        return mpRowConpagId;
    }

    public void setMpRowConpagId(int mpRowConpagId) {
        this.mpRowConpagId = mpRowConpagId;
    }

    public Date getMpRowFechaPago() {
        return mpRowFechaPago;
    }

    public void setMpRowFechaPago(Date mpRowFechaPago) {
        this.mpRowFechaPago = mpRowFechaPago;
    }

    public Date getMpRowFechaProrroga() {
        return mpRowFechaProrroga;
    }

    public void setMpRowFechaProrroga(Date mpRowFechaProrroga) {
        this.mpRowFechaProrroga = mpRowFechaProrroga;
    }

    public String getMpRowAlutarTipo() {
        return mpRowAlutarTipo;
    }

    public void setMpRowAlutarTipo(String mpRowAlutarTipo) {
        this.mpRowAlutarTipo = mpRowAlutarTipo;
    }

    public String getMensajeEliminar() {
        return mensajeEliminar;
    }

    public void setMensajeEliminar(String mensajeEliminar) {
        this.mensajeEliminar = mensajeEliminar;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}
