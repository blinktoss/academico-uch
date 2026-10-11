package net.uch.academica.jsf;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.academica.dao.HSAlumnoDatoDAO;
import net.uch.academica.dao.HSAsistenciaDAO;
import net.uch.academica.dao.HSPlanCurricularDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.common.SystemHelper;
import net.uch.configuracion.dao.*;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

public class MbAlumno {

    private static final Logger LOG = LoggerFactory.getLogger(MbAlumno.class);

    private static final String COLOR_BLUE = "color: blue;";

    private String alumnoTipoId;
    private String alumnoModalidadId;
    private String alumnoEstadoId;
    private int alumnoEspIngId;
    private int alumnoSemestreId;
    private int alumnoPlanActId;
    private int alumnoPlanIngId;
    private int alumnoFacIngId;
    private List<MbAlumno> listaAlumno;
    private String alumnoDni;
    private String semestreActual;
    private int modalIdAlumno;
    private String modalCodigoAlumno;
    private String modalPaternoAlumno;
    private String modalMaternoAlumno;
    private String modalNombreAlumno;
    private String modalDniAlumno;
    private String modalMailAlumno;
    private String modalIdTipoAlumno;
    private String modalIdModalidadAlumno;
    private String modalIdEstadoAlumno;
    private int modalIdEspActAlumno;
    private int modalIdEspIngAlumno;
    private int modalIdSemestreAlumno;
    private int modalIdPlanActAlumno;
    private int modalIdPlanIngAlumno;
    private int modalIdFacIngAlumno;
    private int modalIdFacActAlumno;
    // Variables
    private int alumnoId;
    private String alumnoMail;
    private String alumnoTipoDescription;
    private String espActual;
    private String paramCodigo;
    private String paramPaterno;
    private String paramMaterno;
    private String paramNombre;
    private int paramIdEspAct;
    private int paramIdFacAct;

    // DAOs
    private HSUbigeoDAO daoUbigeo;
    private HSEspecialidadDAO daoEspecialidad;
    private HSAlumnoDAO daoAlumno;
    private SystemHelper helper;
    private SelectItem[] comboCatalogoTipo;
    private SelectItem[] comboCatalogoModa;
    private SelectItem[] comboCatalogoEstado;
    private SelectItem[] comboFacultadesIngreso;
    private List<SelectItem> comboEspecialidadesIngreso;
    private SelectItem[] comboFacultadesActual;
    private List<SelectItem> comboEspecialidadesActual;
    private SelectItem[] comboSemestre;
    private List<SelectItem> comboPlanIngreso;
    private List<SelectItem> comboPlanActual;
    private List<SelectItem> comboEspecialidadBuscar;
    private SelectItem[] comboFacultadBuscar;
    private Month mes;
    private int anio = 0;
    private String oncomplete;
    private String alumnoProcedenciaDesc;
    private String alumnoProcedenciaId;
    private String modalFamiliarAlumno;
    private String modalIdTipoFamiliarAlumno;
    private String modalIdFormaPagoAlumno;
    private String modalUnidadAlumno;
    private float modalMontoPagoAlumno;
    private List<MbAlumno> listaAsistencia;
    private String labelMonth;
    private String labelYear;
    private String nroLunes;
    private String nroMartes;
    private String nroMiercoles;
    private String nroJueves;
    private String nroViernes;
    private String nroSabado;
    private String nroDomingo;
    private String labelLunes;
    private String labelMartes;
    private String labelMiercoles;
    private String labelJueves;
    private String labelViernes;
    private String labelSabado;
    private String labelDomingo;
    private String styleLunes;
    private String styleMartes;
    private String styleMiercoles;
    private String styleJueves;
    private String styleViernes;
    private String styleSabado;
    private String styleDomingo;
    private SelectItem[] comboTipoDocumento;
    private String tipoDocumento;
    private List<SelectItem> comboDistritoLima;
    private String distritoLima;
    private SelectItem[] comboAniosPreparacion;
    private String aniosPreparacion;
    private SelectItem[] comboMesesPreparacion;
    private String mesesPreparacion;
    private SelectItem[] combosexo;
    private String sexo;
    private SelectItem[] comboDepartamentos;
    private String departamentos = CAT_DEFAULT;
    private List<SelectItem> comboProvincias;
    private String provincias = CAT_DEFAULT;
    private List<SelectItem> comboDistrito;
    private String distrito = CAT_DEFAULT;
    private SelectItem[] comboDepartamentos1;
    private String departamentos1 = CAT_DEFAULT;
    private List<SelectItem> comboProvincias1;
    private String provincias1 = CAT_DEFAULT;
    private List<SelectItem> comboDistrito1;
    private String distrito1 = CAT_DEFAULT;
    private SelectItem[] comboTipoColegio;
    private String tipoColegio;
    private SelectItem[] comboAcademias;
    private String academia;
    private SelectItem[] comboPreparacion;
    private String preparacion;
    private String direccionLima;
    private String telefonoFijo;
    private String academiaOtros;
    private boolean disableAcabemiaOtros;
    private String styleAcabemiaOtros;
    private String colegio;
    private String correo;
    private String telefonoCelular;
    private String numeroDocumento;
    private Date fechaNacimiento;
    private SelectItem[] comboProcedencia;
    private SelectItem[] comboDocumentos;
    private List<String> documentos;
    private SelectItem[] comboTipoFamiliar;
    private SelectItem[] comboFormaPago;
    private SelectItem[] comboUnidad;
    // Para la busqueda
    private List<TbCatalogo> listaClasificacion;
    private List<TbCatalogo> listaTipoAlumno;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        daoUbigeo = ServiceFinder.findBean("springHibernateDaoUbigeo", HSUbigeoDAO.class);
        daoEspecialidad = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
        daoAlumno = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
        helper = ServiceFinder.findBean("helper", SystemHelper.class);

        this.setListaAlumno(new ArrayList<>());
        this.setListaAsistencia(new ArrayList<>());

        HSFacultadDAO dao0 = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        SelectItem[] faculties = dao0.seleccionarFacultad("", "").stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .toArray(SelectItem[]::new);
        this.setComboFacultadBuscar(faculties);
        this.setComboEspecialidadBuscar(new ArrayList<>());

        /*estado*/
        this.setComboCatalogoEstado(collect(daoCatalogo.seleccionarCatalogo("011")));

        /*modalidad*/
        this.setComboCatalogoModa(collect(daoCatalogo.seleccionarCatalogo("010")));

        /*TipoFamiliar*/
        this.setComboTipoFamiliar(collect(daoCatalogo.seleccionarCatalogo("040")));

        /*FormaPago*/
        this.setComboFormaPago(collect(daoCatalogo.seleccionarCatalogo("041")));

        /*Unidad*/
        this.setComboUnidad(collect(daoCatalogo.seleccionarCatalogo("042")));

        // facultad ingreso
        this.setComboFacultadesIngreso(faculties);
        this.setComboEspecialidadesIngreso(new ArrayList<>());
        this.setComboPlanIngreso(new ArrayList<>());

        /*Tipo*/
        this.setComboCatalogoTipo(collect(daoCatalogo.seleccionarCatalogo("009")));

        /**/
        this.setComboFacultadesActual(faculties);
        this.setComboEspecialidadesActual(new ArrayList<>());
        this.setComboPlanActual(new ArrayList<>());

        /*Semestre Actual*/
        HSSemestreDAO dao5 = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        this.setSemestreActual(dao5.seleccionarSemestreActual().getSemNombre());

        /*Academias*/
        this.setComboAcademias(collect(daoCatalogo.seleccionarCatalogo("028")));

        /*Tipo de Documento*/
        this.setComboTipoDocumento(collect(daoCatalogo.seleccionarCatalogo("027")));
        /*preparacion*/
        this.setComboPreparacion(collect(daoCatalogo.seleccionarCatalogo("029")));

        /*Documentos*/
        this.setComboDocumentos(collect(daoCatalogo.seleccionarCatalogo("031")));
        /*Tipo de Colegio*/
        this.setComboTipoColegio(collect(daoCatalogo.seleccionarCatalogo("030")));
        /*Sexo*/
        this.setCombosexo(collect(daoCatalogo.seleccionarCatalogo("004")));
        /*Procedencia*/
        this.setComboProcedencia(collect(daoCatalogo.seleccionarCatalogo("023")));

        /*Semestres*/
        List<AcSemestre> lista11 = dao5.seleccionarSemestresVigentes();
        this.setComboSemestre(lista11.stream()
                .map(x -> new SelectItem(x.getId(), x.getSemNombre()))
                .toArray(SelectItem[]::new));

        /*Departamentos1*/
        SelectItem[] districts = daoUbigeo.seleccionarDepartamento().stream()
                .map(d -> new SelectItem(d.getId(), d.getDisDes()))
                .toArray(SelectItem[]::new);
        this.setComboDepartamentos1(districts);
        this.setComboProvincias1(new ArrayList<>());
        this.setComboDistrito1(new ArrayList<>());
        this.setComboDepartamentos(districts);
        this.setComboProvincias(new ArrayList<>());
        this.setComboDistrito(new ArrayList<>());
        /*Anios de Preparacion*/
        this.setComboAniosPreparacion(collect(daoCatalogo.seleccionarCatalogo("025")));
        this.setComboMesesPreparacion(collect(daoCatalogo.seleccionarCatalogo("026")));

        this.setComboDistritoLima(new ArrayList<>());
        this.comboDistritoLima.add(new SelectItem("070101", "CALLAO"));
        this.comboDistritoLima.addAll(daoUbigeo.seleccionarDistrito("15", "1501").stream()
                .map(d -> new SelectItem(d.getId(), d.getDisDes())).collect(Collectors.toList()));

        listaClasificacion = daoCatalogo.seleccionarGrupo("023");
        listaTipoAlumno = daoCatalogo.seleccionarGrupo("009");

        LOG.trace("TERMINO");
    }

    private SelectItem[] collect(List<TbCatalogo> l) {
        return l.stream().map(cat ->
                new SelectItem(cat.getCatCodigoGrupo() + cat.getCatCodigoElemento(), cat.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new);
    }

    public String getModalFamiliarAlumno() {
        return modalFamiliarAlumno;
    }

    public void setModalFamiliarAlumno(String modalFamiliarAlumno) {
        this.modalFamiliarAlumno = modalFamiliarAlumno;
    }

    public String getModalIdFormaPagoAlumno() {
        return modalIdFormaPagoAlumno;
    }

    public void setModalIdFormaPagoAlumno(String modalIdFormaPagoAlumno) {
        this.modalIdFormaPagoAlumno = modalIdFormaPagoAlumno;
    }

    public String getModalIdTipoFamiliarAlumno() {
        return modalIdTipoFamiliarAlumno;
    }

    public void setModalIdTipoFamiliarAlumno(String modalIdTipoFamiliarAlumno) {
        this.modalIdTipoFamiliarAlumno = modalIdTipoFamiliarAlumno;
    }

    public String getModalUnidadAlumno() {
        return modalUnidadAlumno;
    }

    public void setModalUnidadAlumno(String modalUnidadAlumno) {
        this.modalUnidadAlumno = modalUnidadAlumno;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getParamCodigo() {
        return paramCodigo;
    }

    public void setParamCodigo(String paramCodigo) {
        this.paramCodigo = paramCodigo;
    }

    public String getParamPaterno() {
        return paramPaterno;
    }

    public void setParamPaterno(String paramPaterno) {
        this.paramPaterno = paramPaterno;
    }

    public String getParamMaterno() {
        return paramMaterno;
    }

    public void setParamMaterno(String paramMaterno) {
        this.paramMaterno = paramMaterno;
    }

    public String getParamNombre() {
        return paramNombre;
    }

    public void setParamNombre(String paramNombre) {
        this.paramNombre = paramNombre;
    }

    public String getAlumnoDni() {
        return alumnoDni;
    }

    public void setAlumnoDni(String alumnoDni) {
        this.alumnoDni = alumnoDni;
    }

    public String getAlumnoProcedenciaDesc() {
        return alumnoProcedenciaDesc;
    }

    public void setAlumnoProcedenciaDesc(String alumnoProcedenciaDesc) {
        this.alumnoProcedenciaDesc = alumnoProcedenciaDesc;
    }

    public String getAlumnoMail() {
        return alumnoMail;
    }

    public void setAlumnoMail(String alumnoMail) {
        this.alumnoMail = alumnoMail;
    }

    public String getAlumnoTipoDescription() {
        return alumnoTipoDescription;
    }

    public void setAlumnoTipoDescription(String alumnoTipoDescription) {
        this.alumnoTipoDescription = alumnoTipoDescription;
    }

    public String getAlumnoTipoId() {
        return alumnoTipoId;
    }

    public void setAlumnoTipoId(String alumnoTipoId) {
        this.alumnoTipoId = alumnoTipoId;
    }

    public String getAlumnoModalidadId() {
        return alumnoModalidadId;
    }

    public void setAlumnoModalidadId(String alumnoModalidadId) {
        this.alumnoModalidadId = alumnoModalidadId;
    }

    public String getAlumnoEstadoId() {
        return alumnoEstadoId;
    }

    public void setAlumnoEstadoId(String alumnoEstadoId) {
        this.alumnoEstadoId = alumnoEstadoId;
    }

    public String getEspActual() {
        return espActual;
    }

    public void setEspActual(String espActual) {
        this.espActual = espActual;
    }

    public int getParamIdEspAct() {
        return paramIdEspAct;
    }

    public void setParamIdEspAct(int paramIdEspAct) {
        this.paramIdEspAct = paramIdEspAct;
    }

    public int getAlumnoEspIngId() {
        return alumnoEspIngId;
    }

    public void setAlumnoEspIngId(int alumnoEspIngId) {
        this.alumnoEspIngId = alumnoEspIngId;
    }

    public int getAlumnoSemestreId() {
        return alumnoSemestreId;
    }

    public void setAlumnoSemestreId(int alumnoSemestreId) {
        this.alumnoSemestreId = alumnoSemestreId;
    }

    public int getAlumnoPlanActId() {
        return alumnoPlanActId;
    }

    public void setAlumnoPlanActId(int alumnoPlanActId) {
        this.alumnoPlanActId = alumnoPlanActId;
    }

    public int getAlumnoPlanIngId() {
        return alumnoPlanIngId;
    }

    public void setAlumnoPlanIngId(int alumnoPlanIngId) {
        this.alumnoPlanIngId = alumnoPlanIngId;
    }

    public int getParamIdFacAct() {
        return paramIdFacAct;
    }

    public void setParamIdFacAct(int paramIdFacAct) {
        this.paramIdFacAct = paramIdFacAct;
    }

    public int getAlumnoFacIngId() {
        return alumnoFacIngId;
    }

    public void setAlumnoFacIngId(int alumnoFacIngId) {
        this.alumnoFacIngId = alumnoFacIngId;
    }

    public List<MbAlumno> getListaAlumno() {
        return listaAlumno;
    }

    public void setListaAlumno(List<MbAlumno> listaAlumno) {
        this.listaAlumno = listaAlumno;
    }

    public SelectItem[] getComboCatalogoTipo() {
        return comboCatalogoTipo;
    }

    public void setComboCatalogoTipo(SelectItem[] comboCatalogoTipo) {
        this.comboCatalogoTipo = comboCatalogoTipo;
    }

    public SelectItem[] getComboCatalogoModa() {
        return comboCatalogoModa;
    }

    public void setComboCatalogoModa(SelectItem[] comboCatalogoModa) {
        this.comboCatalogoModa = comboCatalogoModa;
    }

    public SelectItem[] getComboCatalogoEstado() {
        return comboCatalogoEstado;
    }

    public void setComboCatalogoEstado(SelectItem[] comboCatalogoEstado) {
        this.comboCatalogoEstado = comboCatalogoEstado;
    }

    public SelectItem[] getComboSemestre() {
        return comboSemestre;
    }

    public void setComboSemestre(SelectItem[] comboSemestre) {
        this.comboSemestre = comboSemestre;
    }

    public String getSemestreActual() {
        return semestreActual;
    }

    public void setSemestreActual(String semestreActual) {
        this.semestreActual = semestreActual;
    }

    public SelectItem[] getComboFacultadesIngreso() {
        return comboFacultadesIngreso;
    }

    public void setComboFacultadesIngreso(SelectItem[] comboFacultades) {
        this.comboFacultadesIngreso = comboFacultades;
    }

    public List<SelectItem> getComboEspecialidadesIngreso() {
        return comboEspecialidadesIngreso;
    }

    public void setComboEspecialidadesIngreso(List<SelectItem> comboEspecialidadesIngreso) {
        this.comboEspecialidadesIngreso = comboEspecialidadesIngreso;
    }

    public void cambiarEspecialidadIngreso(ActionEvent event) {
        LOG.debug("[{}] On change Facultad {}", event.getPhaseId(), this.modalIdFacIngAlumno);
        this.comboEspecialidadesIngreso.clear();
        this.comboPlanIngreso.clear();
        if (modalIdFacIngAlumno != 0) {
            List<AcEspecialidad> lista = daoEspecialidad.seleccionarEspecialidad(modalIdFacIngAlumno);
            this.comboEspecialidadesIngreso.addAll(lista.stream()
                    .map(e -> new SelectItem(e.getId(), e.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getComboPlanIngreso() {
        return comboPlanIngreso;
    }

    public void setComboPlanIngreso(List<SelectItem> comboPlanIngreso) {
        this.comboPlanIngreso = comboPlanIngreso;
    }

    public void cambiarPlanIngreso(ActionEvent event) {
        LOG.debug("[{}] On change Especialidad {}", event.getPhaseId(), this.modalIdEspIngAlumno);
        this.comboPlanIngreso.clear();
        if (modalIdEspIngAlumno != 0) {
            HSPlanCurricularDAO dao = ServiceFinder.findBean("springHibernateDaoPlanCurricular", HSPlanCurricularDAO.class);
            List<AcPlancurricular> lista = dao.seleccionarPlanActivo(modalIdEspIngAlumno);
            this.comboPlanIngreso.addAll(lista.stream()
                    .map(pc -> new SelectItem(pc.getId(), pc.getPlanDescripcion()))
                    .collect(Collectors.toList()));
        }
    }

    public SelectItem[] getComboFacultadesActual() {
        return comboFacultadesActual;
    }

    public void setComboFacultadesActual(SelectItem[] comboFacultades) {
        this.comboFacultadesActual = comboFacultades;
    }

    public List<SelectItem> getComboEspecialidadesActual() {
        return comboEspecialidadesActual;
    }

    public void setComboEspecialidadesActual(List<SelectItem> comboEspecialidadesActual) {
        this.comboEspecialidadesActual = comboEspecialidadesActual;
    }

    public void cambiarEspecialidadActual(ActionEvent event) {
        LOG.debug("[{}] On change Facultad {}", event.getPhaseId(), this.modalIdFacActAlumno);
        this.comboEspecialidadesActual.clear();
        this.comboPlanActual.clear();
        if (modalIdFacActAlumno != 0) {
            List<AcEspecialidad> lista = daoEspecialidad.seleccionarEspecialidad(modalIdFacActAlumno);
            this.comboEspecialidadesActual.addAll(lista.stream()
                    .map(e -> new SelectItem(e.getId(), e.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getComboPlanActual() {
        return comboPlanActual;
    }

    public void setComboPlanActual(List<SelectItem> comboPlanActual) {
        this.comboPlanActual = comboPlanActual;
    }

    public void cambiarPlanActual(ActionEvent event) {
        LOG.debug("[{}] On change Especialidad {}", event.getPhaseId(), this.modalIdEspActAlumno);
        this.comboPlanActual.clear();
        if (modalIdEspActAlumno != 0) {
            HSPlanCurricularDAO dao = ServiceFinder.findBean("springHibernateDaoPlanCurricular", HSPlanCurricularDAO.class);
            List<AcPlancurricular> lista = dao.seleccionarPlanActivo(modalIdEspActAlumno);
            this.comboPlanActual.addAll(lista.stream()
                    .map(pc -> new SelectItem(pc.getId(), pc.getPlanDescripcion()))
                    .collect(Collectors.toList()));
        }
    }

    public int getModalIdAlumno() {
        return modalIdAlumno;
    }

    public void setModalIdAlumno(int modalIdAlumno) {
        this.modalIdAlumno = modalIdAlumno;
    }

    public String getModalCodigoAlumno() {
        return modalCodigoAlumno;
    }

    public void setModalCodigoAlumno(String modalCodigoAlumno) {
        this.modalCodigoAlumno = modalCodigoAlumno;
    }

    public String getModalPaternoAlumno() {
        return modalPaternoAlumno;
    }

    public void setModalPaternoAlumno(String modalPaternoAlumno) {
        this.modalPaternoAlumno = modalPaternoAlumno;
    }

    public String getModalMaternoAlumno() {
        return modalMaternoAlumno;
    }

    public void setModalMaternoAlumno(String modalMaternoAlumno) {
        this.modalMaternoAlumno = modalMaternoAlumno;
    }

    public String getModalNombreAlumno() {
        return modalNombreAlumno;
    }

    public void setModalNombreAlumno(String modalNombreAlumno) {
        this.modalNombreAlumno = modalNombreAlumno;
    }

    public String getModalDniAlumno() {
        return modalDniAlumno;
    }

    public void setModalDniAlumno(String modalDniAlumno) {
        this.modalDniAlumno = modalDniAlumno;
    }

    public String getModalMailAlumno() {
        return modalMailAlumno;
    }

    public void setModalMailAlumno(String modalMailAlumno) {
        this.modalMailAlumno = modalMailAlumno;
    }

    public String getModalIdTipoAlumno() {
        return modalIdTipoAlumno;
    }

    public void setModalIdTipoAlumno(String modalIdTipoAlumno) {
        this.modalIdTipoAlumno = modalIdTipoAlumno;
    }

    public String getModalIdEstadoAlumno() {
        return modalIdEstadoAlumno;
    }

    public void setModalIdEstadoAlumno(String modalIdEstadoAlumno) {
        this.modalIdEstadoAlumno = modalIdEstadoAlumno;
    }

    public String getModalIdModalidadAlumno() {
        return modalIdModalidadAlumno;
    }

    public void setModalIdModalidadAlumno(String modalIdModalidadAlumno) {
        this.modalIdModalidadAlumno = modalIdModalidadAlumno;
    }

    public int getModalIdEspActAlumno() {
        return modalIdEspActAlumno;
    }

    public void setModalIdEspActAlumno(int modalIdEspActAlumno) {
        this.modalIdEspActAlumno = modalIdEspActAlumno;
    }

    public int getModalIdEspIngAlumno() {
        return modalIdEspIngAlumno;
    }

    public void setModalIdEspIngAlumno(int modalIdEspIngAlumno) {
        this.modalIdEspIngAlumno = modalIdEspIngAlumno;
    }

    public int getModalIdSemestreAlumno() {
        return modalIdSemestreAlumno;
    }

    public void setModalIdSemestreAlumno(int modalIdSemestreAlumno) {
        this.modalIdSemestreAlumno = modalIdSemestreAlumno;
    }

    public int getModalIdPlanActAlumno() {
        return modalIdPlanActAlumno;
    }

    public void setModalIdPlanActAlumno(int modalIdPlanActAlumno) {
        this.modalIdPlanActAlumno = modalIdPlanActAlumno;
    }

    public int getModalIdPlanIngAlumno() {
        return modalIdPlanIngAlumno;
    }

    public void setModalIdPlanIngAlumno(int modalIdPlanIngAlumno) {
        this.modalIdPlanIngAlumno = modalIdPlanIngAlumno;
    }

    public int getModalIdFacIngAlumno() {
        return modalIdFacIngAlumno;
    }

    public void setModalIdFacIngAlumno(int modalIdFacIngAlumno) {
        this.modalIdFacIngAlumno = modalIdFacIngAlumno;
    }

    public int getModalIdFacActAlumno() {
        return modalIdFacActAlumno;
    }

    public void setModalIdFacActAlumno(int modalIdFacActAlumno) {
        this.modalIdFacActAlumno = modalIdFacActAlumno;
    }

    public SelectItem[] getComboFacultadBuscar() {
        return comboFacultadBuscar;
    }

    public void setComboFacultadBuscar(SelectItem[] comboFacultad) {
        this.comboFacultadBuscar = comboFacultad;
    }

    public List<SelectItem> getComboEspecialidadBuscar() {
        return comboEspecialidadBuscar;
    }

    public void setComboEspecialidadBuscar(List<SelectItem> comboEspecialidadBuscar) {
        this.comboEspecialidadBuscar = comboEspecialidadBuscar;
    }

    public void cambiarEspecialidadBuscar(ActionEvent event) {
        LOG.debug("[{}] Current facultad: {}", event.getPhaseId(), paramIdFacAct);
        this.comboEspecialidadBuscar.clear();
        if (paramIdFacAct != 0) {
            List<AcEspecialidad> lista = daoEspecialidad.seleccionarEspecialidad(paramIdFacAct);
            this.comboEspecialidadBuscar.addAll(lista.stream()
                    .map(e -> new SelectItem(e.getId(), e.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public void seleccionar(ActionEvent event) {
        listaAlumno.clear();

        LOG.debug("[{}] Looking for '{}' | '{}' | '{}' | '{}' | {} | {}", event.getPhaseId(),
                this.getParamCodigo(), this.getParamPaterno(), this.getParamMaterno(), this.getParamNombre(), this.getParamIdFacAct(), this.getParamIdEspAct());

        List<AcAlumno> lAlumnos = daoAlumno.seleccionarAlumnos(
                this.getParamCodigo(), this.getParamPaterno(), this.getParamMaterno(), this.getParamNombre(), this.getParamIdEspAct());
        MbAlumno bA;

        for (AcAlumno lAlumno : lAlumnos) {
            bA = new MbAlumno();
            bA.setAlumnoId(lAlumno.getId());
            bA.setParamCodigo(lAlumno.getAluCodigo());
            bA.setParamPaterno(lAlumno.getAluAppaterno());
            bA.setParamMaterno(lAlumno.getAluApmaterno());
            bA.setParamNombre(lAlumno.getAluNombres());
            bA.setAlumnoDni(lAlumno.getAluDni());
            bA.setAlumnoMail(lAlumno.getAluMail());
            bA.setAlumnoTipoId(lAlumno.getAluTipo());
            for (TbCatalogo catalogo : listaTipoAlumno) {
                if (("009" + catalogo.getCatCodigoElemento()).equals(lAlumno.getAluTipo())) {
                    bA.setAlumnoTipoDescription(catalogo.getCatDescripcionElemento());
                }
            }
            bA.setAlumnoModalidadId(lAlumno.getAluModalidad());
            bA.setAlumnoEstadoId(lAlumno.getAluEstado());
            bA.setAlumnoSemestreId(lAlumno.getSemId());
            bA.setAlumnoPlanIngId(lAlumno.getPlanIdIngreso());
            bA.setAlumnoPlanActId(lAlumno.getPlanIdActual());
            bA.setAlumnoProcedenciaId(lAlumno.getAluProcedencia());
            bA.setModalFamiliarAlumno(lAlumno.getAluFamiliar());
            bA.setModalIdTipoFamiliarAlumno(lAlumno.getAluTipoFamiliar());
            bA.setModalIdFormaPagoAlumno(lAlumno.getAluFormaPago());
            bA.setModalUnidadAlumno(lAlumno.getAluUnidad());
            bA.setModalMontoPagoAlumno(lAlumno.getAluMontoPago());

            var acEspecialidad = lAlumno.getEsp();
            bA.setParamIdFacAct(acEspecialidad.getFac().getId());
            bA.setParamIdEspAct(acEspecialidad.getId());
            bA.setEspActual(acEspecialidad.getEspNombre());

            AcEspecialidad espIng = daoEspecialidad.seleccionarUnaEspecialidad(lAlumno.getEspIdIngreso());
            bA.setAlumnoFacIngId(espIng.getFac().getId());
            bA.setAlumnoEspIngId(lAlumno.getEspIdIngreso());

            for (TbCatalogo tbCatalogo : listaClasificacion) {
                if (("023" + tbCatalogo.getCatCodigoElemento()).equals(lAlumno.getAluProcedencia())) {
                    bA.setAlumnoProcedenciaDesc(tbCatalogo.getCatDescripcionElemento());
                }
            }
            listaAlumno.add(bA);
        }
        LOG.info("Total of students {}", listaAlumno.size());
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        limpiar();
    }

    public void eliminarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter delete = (UIParameter) event.getComponent().findComponent("delete");
        var id = delete.getValue().toString();

        daoAlumno.eliminarAlumno(id);
        LOG.info("Student deleted {}", id);
    }

    public void limpiar() {
        modalIdAlumno = 0;
        modalCodigoAlumno = "";
        modalPaternoAlumno = "";
        modalMaternoAlumno = "";
        modalNombreAlumno = "";
        modalDniAlumno = "";
        modalDniAlumno = "";
        modalMailAlumno = "";
        modalMailAlumno = "";
        modalIdTipoAlumno = "";
        modalIdModalidadAlumno = "";
        modalIdEstadoAlumno = "";
        modalIdFacIngAlumno = 0;
        modalIdEspIngAlumno = 0;
        modalIdFacActAlumno = 0;
        modalIdEspActAlumno = 0;
        modalIdSemestreAlumno = 0;
        modalIdPlanIngAlumno = 0;
        modalIdPlanActAlumno = 0;
        alumnoProcedenciaDesc = "";
        modalFamiliarAlumno = "";
        modalIdTipoFamiliarAlumno = "";
        modalIdFormaPagoAlumno = "";
        modalUnidadAlumno = "";
        modalMontoPagoAlumno = 0;
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        limpiar();
        MbAlumno alu = (MbAlumno) event.getComponent().getAttributes().get("p_alu");
        modalDniAlumno = alu.alumnoDni;
        modalMailAlumno = alu.alumnoMail;
        modalIdAlumno = alu.alumnoId;
        modalCodigoAlumno = alu.paramCodigo;
        modalPaternoAlumno = alu.paramPaterno;
        modalMaternoAlumno = alu.paramMaterno;
        modalNombreAlumno = alu.paramNombre;
        modalIdTipoAlumno = alu.alumnoTipoId;
        modalIdModalidadAlumno = alu.alumnoModalidadId;
        modalIdEstadoAlumno = alu.alumnoEstadoId;
        modalIdFacIngAlumno = alu.alumnoFacIngId;
        modalIdEspIngAlumno = alu.alumnoEspIngId;
        cambiarEspecialidadIngreso(event);
        modalIdFacActAlumno = alu.paramIdFacAct;
        modalIdEspActAlumno = alu.paramIdEspAct;
        cambiarEspecialidadActual(event);
        modalIdSemestreAlumno = alu.alumnoSemestreId;
        modalIdPlanIngAlumno = alu.alumnoPlanIngId;
        cambiarPlanIngreso(event);
        modalIdPlanActAlumno = alu.alumnoPlanActId;
        cambiarPlanActual(event);
        alumnoProcedenciaId = alu.alumnoProcedenciaId;
        modalFamiliarAlumno = alu.modalFamiliarAlumno;
        modalIdTipoFamiliarAlumno = alu.modalIdTipoFamiliarAlumno;
        modalIdFormaPagoAlumno = alu.modalIdFormaPagoAlumno;
        modalUnidadAlumno = alu.modalUnidadAlumno;
        modalMontoPagoAlumno = alu.modalMontoPagoAlumno;
    }

    private boolean validateModalFields() {
        return modalPaternoAlumno.trim().length() == 0 ||
                modalMaternoAlumno.trim().length() == 0 ||
                modalNombreAlumno.trim().length() == 0 ||
                modalIdEspIngAlumno <= 0 ||
                modalIdPlanIngAlumno <= 0 ||
                modalIdSemestreAlumno <= 0 ||
                modalIdPlanActAlumno <= 0 ||
                modalIdEspActAlumno <= 0;
    }

    private int generateStudentCodeForModal() {
        String codMax = daoAlumno.maximoCodigo();
        if (Integer.parseInt(codMax) >= 9999 || codMax.equals("0000")) {
            this.setOncomplete("alert('No se pudo ingresar al Alumno porque se ha agotado la numeracion de Codigos.Por favor Contactese con el area de Sistemas.')");
            return 0;
        } else {
            int codM = Integer.parseInt(codMax) + 1;
            String c = codM + "";
            var cd = "";
            if (c.length() == 1) {
                cd = "000" + c;
            }
            if (c.length() == 2) {
                cd = "00" + c;
            }
            if (c.length() == 3) {
                cd = "0" + c;
            }
            var date = new Date();
            var ano = String.format("%ty", date);
            String codNuevo = ano + "0" + modalIdEspActAlumno + cd;
            modalCodigoAlumno = codNuevo.concat(codMax);
            LOG.info("New code generated {}", modalCodigoAlumno);
            return 1;
        }
    }

    public void insertar(ActionEvent event) throws IOException, SQLException {
        LOG.debug("[{}]", event.getPhaseId());
        if (modalIdAlumno == 0) {
            if (validateModalFields()) {
                this.setOncomplete("alert('Ingrese todos los datos Requeridos.')");
            } else {
                int estado = generateStudentCodeForModal();
                if (estado == 1) {
                    var alu = new AcAlumno();
                    alu.setId(modalIdAlumno);
                    alu.setAluCodigo(modalCodigoAlumno);
                    alu.setAluAppaterno(modalPaternoAlumno);
                    alu.setAluApmaterno(modalMaternoAlumno);
                    alu.setAluNombres(modalNombreAlumno);
                    alu.setAluDni(modalDniAlumno);
                    alu.setAluMail(modalMailAlumno);
                    alu.setAluTipo(modalIdTipoAlumno);
                    alu.setAluModalidad(modalIdModalidadAlumno);
                    alu.setAluEstado(modalIdEstadoAlumno);
                    alu.setEspIdIngreso(modalIdEspIngAlumno);
                    alu.setSemId(modalIdSemestreAlumno);
                    alu.setPlanIdIngreso(modalIdPlanIngAlumno);
                    alu.setPlanIdActual(modalIdPlanActAlumno);
                    alu.setAluDuplicado("");
                    var esp = new AcEspecialidad();
                    esp.setId(modalIdEspActAlumno);
                    alu.setEsp(esp);
                    alu.setAluFoto(newImage());
                    alu.setAluActivo("1");
                    alu.setAluDuplicado("");
                    alu.setAluProcedencia(alumnoProcedenciaId);
                    alu.setAluFamiliar(modalFamiliarAlumno);
                    alu.setAluTipoFamiliar(modalIdTipoFamiliarAlumno);
                    alu.setAluFormaPago(modalIdFormaPagoAlumno);
                    alu.setAluUnidad(modalUnidadAlumno);
                    alu.setAluMontoPago(modalMontoPagoAlumno);
                    setModalIdAlumno(daoAlumno.insertarAlumno(alu));
                    LOG.info("Student saved {}", modalIdAlumno);
                    this.setOncomplete("alert('Se creo una Alumno correctamente.');Richfaces.hideModalPanel('mp')");
                }
            }
        } else {
            if (validateModalFields()) {
                this.setOncomplete("alert('Ingrese todos los datos Requeridos.')");
            } else {
                AcAlumno alu = daoAlumno.seleccionarUnAlumno(modalIdAlumno);
                alu.setAluCodigo(modalCodigoAlumno);
                alu.setAluAppaterno(modalPaternoAlumno);
                alu.setAluApmaterno(modalMaternoAlumno);
                alu.setAluNombres(modalNombreAlumno);
                alu.setAluDni(modalDniAlumno);
                alu.setAluMail(modalMailAlumno);
                alu.setAluTipo(modalIdTipoAlumno);
                alu.setAluModalidad(modalIdModalidadAlumno);
                alu.setAluEstado(modalIdEstadoAlumno);
                alu.setEspIdIngreso(modalIdEspIngAlumno);
                alu.setSemId(modalIdSemestreAlumno);
                alu.setPlanIdIngreso(modalIdPlanIngAlumno);
                alu.setPlanIdActual(modalIdPlanActAlumno);
                var esp = new AcEspecialidad();
                esp.setId(modalIdEspActAlumno);
                alu.setEsp(esp);
                alu.setAluActivo("1");
                alu.setAluProcedencia(alumnoProcedenciaId);
                alu.setAluFamiliar(modalFamiliarAlumno);
                alu.setAluTipoFamiliar(modalIdTipoFamiliarAlumno);
                alu.setAluFormaPago(modalIdFormaPagoAlumno);
                alu.setAluUnidad(modalUnidadAlumno);
                alu.setAluMontoPago(modalMontoPagoAlumno);
                daoAlumno.actualizarAlumno(alu);
                LOG.info("Student updated {}", modalIdAlumno);
                this.setOncomplete("alert('Se Actualizaron los datos del Alumno correctamente.');Richfaces.hideModalPanel('mp')");
            }
        }
    }

    private Blob newImage() throws IOException, SQLException {
        InputStream initialStream = FacesContext.getCurrentInstance().
                getExternalContext().getResourceAsStream(ACTION_IMAGES_USER_INFO);

        var targetArray = new byte[initialStream.available()];
        int read = initialStream.read(targetArray);
        LOG.debug("Bytes read {}", read);
        return new SerialBlob(targetArray);
    }

    public SelectItem[] getComboTipoDocumento() {
        return comboTipoDocumento;
    }

    public void setComboTipoDocumento(SelectItem[] comboTipoDocumento) {
        this.comboTipoDocumento = comboTipoDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public List<SelectItem> getComboDistritoLima() {
        return comboDistritoLima;
    }

    public void setComboDistritoLima(List<SelectItem> comboDistritoLima) {
        this.comboDistritoLima = comboDistritoLima;
    }

    public String getDistritoLima() {
        return distritoLima;
    }

    public void setDistritoLima(String distritoLima) {
        this.distritoLima = distritoLima;
    }

    public SelectItem[] getComboAniosPreparacion() {
        return comboAniosPreparacion;
    }

    public void setComboAniosPreparacion(SelectItem[] comboAniosPreparacion) {
        this.comboAniosPreparacion = comboAniosPreparacion;
    }

    public String getAniosPreparacion() {

        return aniosPreparacion;
    }

    public void setAniosPreparacion(String aniosPreparacion) {
        this.aniosPreparacion = aniosPreparacion;
    }

    public SelectItem[] getComboMesesPreparacion() {
        return comboMesesPreparacion;
    }

    public void setComboMesesPreparacion(SelectItem[] comboMesesPreparacion) {
        this.comboMesesPreparacion = comboMesesPreparacion;
    }

    public String getMesesPreparacion() {
        return mesesPreparacion;
    }

    public void setMesesPreparacion(String mesesPreparacion) {
        this.mesesPreparacion = mesesPreparacion;
    }

    public SelectItem[] getCombosexo() {
        return combosexo;
    }

    public void setCombosexo(SelectItem[] combosexo) {
        this.combosexo = combosexo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public SelectItem[] getComboDepartamentos() {
        return comboDepartamentos;
    }

    public void setComboDepartamentos(SelectItem[] comboDepartamentos) {
        this.comboDepartamentos = comboDepartamentos;
    }

    public void cambiaDepartamentoNac(ActionEvent event) {
        LOG.debug("[{}] On change Dep {}", event.getPhaseId(), this.departamentos);
        this.comboProvincias.clear();
        this.comboDistrito.clear();
        if (!CAT_DEFAULT.equals(departamentos)) {
            List<TbDistrito> lista = daoUbigeo.seleccionarProvincia(this.getDepartamentos());
            this.comboProvincias.addAll(lista.stream()
                    .map(p -> new SelectItem(p.getId(), p.getDisDes()))
                    .collect(Collectors.toList()));
        }
    }

    public String getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(String departamentos) {
        this.departamentos = departamentos;
    }

    public List<SelectItem> getComboProvincias() {
        return comboProvincias;
    }

    public void setComboProvincias(List<SelectItem> comboProvincias) {
        this.comboProvincias = comboProvincias;
    }

    public void cambiaProvinciaNac(ActionEvent event) {
        LOG.debug("[{}] On change Dep {}, Prov {}", event.getPhaseId(), this.departamentos, this.provincias);
        this.comboDistrito.clear();
        if (!CAT_DEFAULT.equals(provincias)) {
            List<TbDistrito> districts = daoUbigeo.seleccionarDistrito(this.getDepartamentos(), this.getProvincias());
            this.comboDistrito.addAll(districts.stream()
                    .map(d -> new SelectItem(d.getId(), d.getDisDes())).collect(Collectors.toList()));
        }
    }

    public String getProvincias() {
        return provincias;
    }

    public void setProvincias(String provincias) {
        this.provincias = provincias;
    }

    public List<SelectItem> getComboDistrito() {
        return comboDistrito;
    }

    public void setComboDistrito(List<SelectItem> comboDistrito) {
        this.comboDistrito = comboDistrito;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public SelectItem[] getComboDepartamentos1() {
        return comboDepartamentos1;
    }

    public void setComboDepartamentos1(SelectItem[] comboDepartamentos1) {
        this.comboDepartamentos1 = comboDepartamentos1;
    }

    public void cambiaDepartamentoCol(ActionEvent event) {
        LOG.debug("[{}] On change Dep {}", event.getPhaseId(), this.departamentos1);
        this.comboProvincias1.clear();
        this.comboDistrito1.clear();
        if (!CAT_DEFAULT.equals(departamentos1)) {
            List<TbDistrito> lista = daoUbigeo.seleccionarProvincia(this.getDepartamentos1());
            this.comboProvincias1.addAll(lista.stream()
                    .map(p -> new SelectItem(p.getId(), p.getDisDes()))
                    .collect(Collectors.toList()));
        }
    }

    public String getDepartamentos1() {
        return departamentos1;
    }

    public void setDepartamentos1(String departamentos1) {
        this.departamentos1 = departamentos1;
    }

    public List<SelectItem> getComboProvincias1() {
        return comboProvincias1;
    }

    public void setComboProvincias1(List<SelectItem> comboProvincias1) {
        this.comboProvincias1 = comboProvincias1;
    }

    public String getProvincias1() {
        return provincias1;
    }

    public void setProvincias1(String provincias1) {
        this.provincias1 = provincias1;
    }

    public List<SelectItem> getComboDistrito1() {
        return comboDistrito1;
    }

    public void setComboDistrito1(List<SelectItem> comboDistrito1) {
        this.comboDistrito1 = comboDistrito1;
    }

    public void cambiaProvinciaCol(ActionEvent event) {
        LOG.debug("[{}] On change Dep {}, Prov {}", event.getPhaseId(), this.departamentos1, this.provincias1);
        this.comboDistrito1.clear();
        if (!CAT_DEFAULT.equals(provincias1)) {
            List<TbDistrito> districts = daoUbigeo.seleccionarDistrito(this.getDepartamentos1(), this.getProvincias1());
            this.comboDistrito1.addAll(districts.stream()
                    .map(d -> new SelectItem(d.getId(), d.getDisDes())).collect(Collectors.toList()));
        }
    }

    public String getDistrito1() {
        return distrito1;
    }

    public void setDistrito1(String distrito1) {
        this.distrito1 = distrito1;
    }

    public SelectItem[] getComboAcademias() {
        return comboAcademias;
    }

    public void setComboAcademias(SelectItem[] comboAcademias) {
        this.comboAcademias = comboAcademias;
    }

    public String getAcademia() {
        return academia;
    }

    public void setAcademia(String academia) {
        this.academia = academia;
    }

    public SelectItem[] getComboPreparacion() {
        return comboPreparacion;
    }

    public void setComboPreparacion(SelectItem[] comboPreparacion) {
        this.comboPreparacion = comboPreparacion;
    }

    public String getPreparacion() {
        return preparacion;
    }

    public void setPreparacion(String preparacion) {
        this.preparacion = preparacion;
    }

    public String getDireccionLima() {
        return direccionLima;
    }

    public void setDireccionLima(String direccionLima) {
        this.direccionLima = direccionLima;
    }

    public String getTelefonoFijo() {
        return telefonoFijo;
    }

    public void setTelefonoFijo(String telefonoFijo) {
        this.telefonoFijo = telefonoFijo;
    }

    public String getAcademiaOtros() {
        return academiaOtros;
    }

    public void setAcademiaOtros(String otros) {
        academiaOtros = otros;
    }

    public void cambiaAcabemia(ActionEvent event) {
        LOG.debug("[{}] On change academia {}", event.getPhaseId(), academia);
        this.setAcademiaOtros("");
        if ("028003".equals(academia)) {
            this.setDisableAcabemiaOtros(false);
            this.setStyleAcabemiaOtros("");
        } else {
            this.setDisableAcabemiaOtros(true);
            this.setStyleAcabemiaOtros("background: gray;");
        }
    }

    public boolean isDisableAcabemiaOtros() {
        return disableAcabemiaOtros;
    }

    public void setDisableAcabemiaOtros(boolean disableAcabemiaOtros) {
        this.disableAcabemiaOtros = disableAcabemiaOtros;
    }

    public String getStyleAcabemiaOtros() {
        return styleAcabemiaOtros;
    }

    public void setStyleAcabemiaOtros(String styleAcabemiaOtros) {
        this.styleAcabemiaOtros = styleAcabemiaOtros;
    }

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefonoCelular() {
        return telefonoCelular;
    }

    public void setTelefonoCelular(String telefonoCelular) {
        this.telefonoCelular = telefonoCelular;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String documento) {
        numeroDocumento = documento;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public SelectItem[] getComboProcedencia() {
        return comboProcedencia;
    }

    public void setComboProcedencia(SelectItem[] comboProcedencia) {
        this.comboProcedencia = comboProcedencia;
    }

    public SelectItem[] getComboTipoColegio() {
        return comboTipoColegio;
    }

    public void setComboTipoColegio(SelectItem[] comboTipoColegio) {
        this.comboTipoColegio = comboTipoColegio;
    }

    public String getTipoColegio() {
        return tipoColegio;
    }

    public void setTipoColegio(String tipoColegio) {
        this.tipoColegio = tipoColegio;
    }

    public SelectItem[] getComboDocumentos() {
        return comboDocumentos;
    }

    public void setComboDocumentos(SelectItem[] comboDocumentos) {
        this.comboDocumentos = comboDocumentos;
    }

    public List<String> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<String> documentos) {
        this.documentos = documentos;
    }

    public void editarFila2(ActionEvent event) {
        LOG.debug("[{}] Loading data for student {}", event.getPhaseId(), modalIdAlumno);
        MbAlumno alu = (MbAlumno) event.getComponent().getAttributes().get("p_alu");
        modalIdAlumno = alu.alumnoId;
        modalPaternoAlumno = alu.paramPaterno;
        modalMaternoAlumno = alu.paramMaterno;
        modalNombreAlumno = alu.paramNombre;

        HSAlumnoDatoDAO dao = ServiceFinder.findBean("springHibernateDaoAlumnoDato", HSAlumnoDatoDAO.class);
        AcAlumnoDato alumnoDato = dao.seleccionarAlumnoDato(modalIdAlumno);
        if (alumnoDato == null) {
            academia = "";
            academiaOtros = "";
            aniosPreparacion = "";
            mesesPreparacion = "";
            colegio = "";
            direccionLima = "";
            distritoLima = "";
            sexo = "";
            correo = "";
            tipoDocumento = "";
            telefonoCelular = "";
            telefonoFijo = "";
            numeroDocumento = "";
            preparacion = "";
            fechaNacimiento = null;
            departamentos = CAT_DEFAULT;
            provincias = CAT_DEFAULT;
            distrito = CAT_DEFAULT;
            departamentos1 = CAT_DEFAULT;
            provincias1 = CAT_DEFAULT;
            distrito1 = CAT_DEFAULT;
            tipoColegio = "";
            documentos = new ArrayList<>();
        } else {
            this.setAcademia(alumnoDato.getAluDatoAcademia());
            cambiaAcabemia(event);
            this.setAcademiaOtros(alumnoDato.getAluDatoAcademiaOtro());
            this.setAniosPreparacion(alumnoDato.getAluDatoAniosPrepa());
            this.setMesesPreparacion(alumnoDato.getAluDatoMesesPrepa());
            this.setColegio(alumnoDato.getAluDatoColegio());
            this.setDireccionLima(alumnoDato.getAluDatoDireccion());
            this.setDistritoLima(alumnoDato.getAluDatoResidenciaLima());
            this.setSexo(alumnoDato.getAluDatoSexo());
            this.setCorreo(alumnoDato.getAluDatoEmail());
            this.setTipoDocumento(alumnoDato.getAluDatoTdoc());
            this.setTelefonoCelular(alumnoDato.getAluDatoTelefonoC());
            this.setTelefonoFijo(alumnoDato.getAluDatoTelefonoF());
            this.setNumeroDocumento(alumnoDato.getAluDatoNrodoc());
            this.setPreparacion(alumnoDato.getAluDatoTipoPrepa());
            this.setFechaNacimiento(alumnoDato.getAluDatoFechaNacimiento());
            this.setDepartamentos(alumnoDato.getAluDatoLugarNacimiento().substring(0, 2) + "0000");
            cambiaDepartamentoNac(event);
            this.setProvincias(alumnoDato.getAluDatoLugarNacimiento().substring(0, 4) + "00");
            cambiaProvinciaNac(event);
            this.setDistrito(alumnoDato.getAluDatoLugarNacimiento());
            this.setDepartamentos1(alumnoDato.getAluDatoUbicolegio().substring(0, 2) + "0000");
            cambiaDepartamentoCol(event);
            this.setProvincias1(alumnoDato.getAluDatoUbicolegio().substring(0, 4) + "00");
            cambiaProvinciaCol(event);
            this.setDistrito1(alumnoDato.getAluDatoUbicolegio());
            this.setTipoColegio(alumnoDato.getAluDatoEstatal());

            List<AcAlumnoDocumento> listaDocumentos = dao.seleccionarDocumentos(modalIdAlumno);
            this.setDocumentos(listaDocumentos.stream()
                    .map(AcAlumnoDocumento::getAludocDocumento).collect(Collectors.toList()));
        }
    }

    public void guardar() {
        LOG.debug("Saving Student Data for student {}", modalIdAlumno);
        HSAlumnoDatoDAO dao = ServiceFinder.findBean("springHibernateDaoAlumnoDato", HSAlumnoDatoDAO.class);
        var aludat = new AcAlumnoDato();
        AcAlumnoDato alumnoDato = dao.seleccionarAlumnoDato(this.getModalIdAlumno());
        if (alumnoDato != null) {
            aludat = alumnoDato;
        }
        aludat.setAluId(this.getModalIdAlumno());
        aludat.setAluDatoResidenciaLima(this.getDistritoLima());
        aludat.setAluDatoUbicolegio(this.getDistrito1());
        aludat.setAluDatoAcademia(this.getAcademia());
        aludat.setAluDatoAcademiaOtro(this.getAcademiaOtros());
        aludat.setAluDatoAniosPrepa(this.getAniosPreparacion());
        aludat.setAluDatoMesesPrepa(this.getMesesPreparacion());
        aludat.setAluDatoColegio(this.getColegio());
        aludat.setAluDatoDireccion(this.getDireccionLima());
        aludat.setAluDatoEmail(this.getCorreo());
        aludat.setAluDatoFechaNacimiento(this.getFechaNacimiento());
        aludat.setAluDatoLugarNacimiento(this.getDistrito());
        aludat.setAluDatoUbicolegio(this.getDistrito1());
        aludat.setAluDatoResidenciaLima(this.getDistritoLima());
        aludat.setAluDatoTdoc(this.getTipoDocumento());
        aludat.setAluDatoTelefonoC(this.getTelefonoCelular());
        aludat.setAluDatoTelefonoF(this.getTelefonoFijo());
        aludat.setAluDatoTipoPrepa(this.getAlumnoTipoDescription());
        aludat.setAluDatoNrodoc(this.getNumeroDocumento());
        aludat.setAluDatoSexo(this.getSexo());
        aludat.setAluDatoEstatal(this.getTipoColegio());
        aludat.setAluDatoActivo("1");
        aludat.setUsuModificacion(new Date());

        BeanUser usu = ServiceFinder.getLoggedUser();
        aludat.setUsuId(usu.getRowUsuId());
        int r = dao.eliminarDocumentos(modalIdAlumno);
        LOG.info("All Student Documents deleted: {}", r);
        var aluDoc = new AcAlumnoDocumento();

        for (String documento : documentos) {
            aluDoc.setAludocActivo("1");
            var alu = new AcAlumno();
            alu.setId(this.getModalIdAlumno());
            aluDoc.setAlu(alu);
            aluDoc.setAludocDocumento(documento);
            dao.insertarDocumentos(aluDoc);
            LOG.info("Student document saved {}", aluDoc.getId());
        }
        if (alumnoDato == null) {
            dao.insertarAlumnoDato(aludat);
            LOG.info("Data Student saved {}", aludat.getId());
        } else {
            dao.actualizarAlumnoDato(aludat);
            LOG.info("Data Student updated {}", aludat.getId());
        }
    }

    public String getAlumnoProcedenciaId() {
        return alumnoProcedenciaId;
    }

    public void setAlumnoProcedenciaId(String alumnoProcedenciaId) {
        this.alumnoProcedenciaId = alumnoProcedenciaId;
    }

    public List<MbAlumno> getListaAsistencia() {
        return listaAsistencia;
    }

    public void setListaAsistencia(List<MbAlumno> listaAsistencia) {
        this.listaAsistencia = listaAsistencia;
    }

    public String getNroLunes() {
        return nroLunes;
    }

    public void setNroLunes(String nroLunes) {
        this.nroLunes = nroLunes;
    }

    public String getNroMartes() {
        return nroMartes;
    }

    public void setNroMartes(String nroMartes) {
        this.nroMartes = nroMartes;
    }

    public String getNroMiercoles() {
        return nroMiercoles;
    }

    public void setNroMiercoles(String nroMiercoles) {
        this.nroMiercoles = nroMiercoles;
    }

    public String getNroJueves() {
        return nroJueves;
    }

    public void setNroJueves(String nroJueves) {
        this.nroJueves = nroJueves;
    }

    public String getNroViernes() {
        return nroViernes;
    }

    public void setNroViernes(String nroViernes) {
        this.nroViernes = nroViernes;
    }

    public String getNroSabado() {
        return nroSabado;
    }

    public void setNroSabado(String nroSabado) {
        this.nroSabado = nroSabado;
    }

    public String getNroDomingo() {
        return nroDomingo;
    }

    public void setNroDomingo(String nroDomingo) {
        this.nroDomingo = nroDomingo;
    }

    public void mostrarAsistencia(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        modalIdAlumno = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_id")).getValue().toString());
        modalCodigoAlumno = ((UIParameter) event.getComponent().findComponent(PARAM_CODIGO)).getValue().toString();
        modalPaternoAlumno = ((UIParameter) event.getComponent().findComponent(PARAM_PATERNO)).getValue().toString();
        modalMaternoAlumno = ((UIParameter) event.getComponent().findComponent(PARAM_MATERNO)).getValue().toString();
        modalNombreAlumno = ((UIParameter) event.getComponent().findComponent(PARAM_NOMBRE)).getValue().toString();
        LOG.debug("Mostrando asistencia de {} | {} | {} | {} | {}",
                this.modalIdAlumno, this.modalCodigoAlumno, this.modalPaternoAlumno, this.modalMaternoAlumno, this.modalNombreAlumno);
        var date = LocalDate.now();
        setMes(date.getMonth());
        setAnio(date.getYear());
        evaluaAsistencia();
    }

    public String evaluaAsistencia() {
        listaAsistencia.clear();
        var weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        var desde = LocalDate.of(anio, mes, 1);
        var hasta = LocalDate.of(anio, mes.plus(1), 1);
        var months = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"};
        this.setLabelMonth(months[mes.getValue() - 1]);
        this.setLabelYear(String.valueOf(anio));

        LOG.debug("Looking for student: {} from {} to {}", modalCodigoAlumno, desde, hasta);

        HSAsistenciaDAO asis = ServiceFinder.findBean("springHibernateDaoAsistencia", HSAsistenciaDAO.class);
        List<TbAsistencia> asistencias = asis.seleccionarAsistencia(modalIdAlumno, modalCodigoAlumno,
                helper.convertToDate(desde), helper.convertToDate(hasta));
        DayOfWeek dia;
        int semana;
        MbAlumno alu;
        for (var nroSemana = 1; nroSemana <= 6; nroSemana++) {
            alu = new MbAlumno();
            llenarCalendario(nroSemana, desde, weekFields, alu);
            for (TbAsistencia asi : asistencias) {
                var temp = helper.convertToLocalDateTime(asi.getAsisDia());
                dia = temp.getDayOfWeek();
                semana = temp.get(weekFields.weekOfMonth());
                if (semana == nroSemana) {
                    if (DayOfWeek.MONDAY.equals(dia)) {
                        alu.setNroLunes(String.valueOf(temp.getDayOfMonth()));
                        alu.setLabelLunes(LABEL_INGRESO + temp.getHour() + ":" + temp.getMinute() + ":" + temp.getSecond());
                        alu.setStyleLunes(COLOR_BLUE);
                    } else if (DayOfWeek.TUESDAY.equals(dia)) {
                        alu.setNroMartes(String.valueOf(temp.getDayOfMonth()));
                        alu.setLabelMartes(LABEL_INGRESO + temp.getHour() + ":" + temp.getMinute() + ":" + temp.getSecond());
                        alu.setStyleMartes(COLOR_BLUE);
                    } else if (DayOfWeek.WEDNESDAY.equals(dia)) {
                        alu.setNroMiercoles(String.valueOf(temp.getDayOfMonth()));
                        alu.setLabelMiercoles(LABEL_INGRESO + temp.getHour() + ":" + temp.getMinute() + ":" + temp.getSecond());
                        alu.setStyleMiercoles(COLOR_BLUE);
                    } else if (DayOfWeek.THURSDAY.equals(dia)) {
                        alu.setNroJueves(String.valueOf(temp.getDayOfMonth()));
                        alu.setLabelJueves(LABEL_INGRESO + temp.getHour() + ":" + temp.getMinute() + ":" + temp.getSecond());
                        alu.setStyleJueves(COLOR_BLUE);
                    } else if (DayOfWeek.FRIDAY.equals(dia)) {
                        alu.setNroViernes(String.valueOf(temp.getDayOfMonth()));
                        alu.setLabelViernes(LABEL_INGRESO + temp.getHour() + ":" + temp.getMinute() + ":" + temp.getSecond());
                        alu.setStyleViernes(COLOR_BLUE);
                    } else if (DayOfWeek.SATURDAY.equals(dia)) {
                        alu.setNroSabado(String.valueOf(temp.getDayOfMonth()));
                        alu.setLabelSabado(LABEL_INGRESO + temp.getHour() + ":" + temp.getMinute() + ":" + temp.getSecond());
                        alu.setStyleSabado(COLOR_BLUE);
                    } else if (DayOfWeek.SUNDAY.equals(dia)) {
                        alu.setNroDomingo(String.valueOf(temp.getDayOfMonth()));
                        alu.setLabelDomingo(LABEL_INGRESO + temp.getHour() + ":" + temp.getMinute() + ":" + temp.getSecond());
                        alu.setStyleDomingo(COLOR_BLUE);
                    }
                }
            }
            listaAsistencia.add(alu);
        }
        return "";
    }

    public void llenarCalendario(int nroSemana, LocalDate fecha, WeekFields weekFields, MbAlumno semana) {
        int meses = fecha.getMonthValue();
        var yearMonthObject = YearMonth.of(fecha.getYear(), fecha.getMonthValue());
        int daysInMonth = yearMonthObject.lengthOfMonth();
        for (var i = 1; i <= daysInMonth; i++) {
            var dia = fecha.getDayOfWeek();
            int nSemana = fecha.get(weekFields.weekOfMonth());
            int nMes = fecha.getMonthValue();
            if (nSemana == nroSemana && meses == nMes) {
                if (DayOfWeek.MONDAY.equals(dia)) {
                    semana.setNroLunes(String.valueOf(fecha.getDayOfMonth()));

                } else if (DayOfWeek.TUESDAY.equals(dia)) {
                    semana.setNroMartes(String.valueOf(fecha.getDayOfMonth()));

                } else if (DayOfWeek.WEDNESDAY.equals(dia)) {
                    semana.setNroMiercoles(String.valueOf(fecha.getDayOfMonth()));

                } else if (DayOfWeek.THURSDAY.equals(dia)) {
                    semana.setNroJueves(String.valueOf(fecha.getDayOfMonth()));

                } else if (DayOfWeek.FRIDAY.equals(dia)) {
                    semana.setNroViernes(String.valueOf(fecha.getDayOfMonth()));

                } else if (DayOfWeek.SATURDAY.equals(dia)) {
                    semana.setNroSabado(String.valueOf(fecha.getDayOfMonth()));

                } else if (DayOfWeek.SUNDAY.equals(dia)) {
                    semana.setNroDomingo(String.valueOf(fecha.getDayOfMonth()));
                    semana.setStyleDomingo("color: red;");
                }
            }
            fecha = fecha.plusDays(1);
        }
    }

    public Month getMes() {
        return mes;
    }

    public void setMes(Month mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public void aumentaMes(ActionEvent action) {
        LOG.trace("[{}]", action.getPhaseId());
        if (Month.DECEMBER.equals(mes)) {
            anio++;
        }
        mes = mes.plus(1);
    }

    public void disminuyeMes(ActionEvent action) {
        LOG.trace("[{}]", action.getPhaseId());
        if (Month.JANUARY.equals(mes)) {
            anio--;
        }
        mes = mes.minus(1);
    }

    public void aumentaAnio(ActionEvent action) {
        LOG.trace("[{}]", action.getPhaseId());
        anio++;
    }

    public void disminuyeAnio(ActionEvent action) {
        LOG.trace("[{}]", action.getPhaseId());
        anio--;
    }

    public String getLabelMonth() {
        return labelMonth;
    }

    public void setLabelMonth(String labelMonth) {
        this.labelMonth = labelMonth;
    }

    public String getLabelYear() {
        return labelYear;
    }

    public void setLabelYear(String labelYear) {
        this.labelYear = labelYear;
    }

    public String getLabelLunes() {
        return labelLunes;
    }

    public void setLabelLunes(String labelLunes) {
        this.labelLunes = labelLunes;
    }

    public String getLabelMartes() {
        return labelMartes;
    }

    public void setLabelMartes(String labelMartes) {
        this.labelMartes = labelMartes;
    }

    public String getLabelMiercoles() {
        return labelMiercoles;
    }

    public void setLabelMiercoles(String labelMiercoles) {
        this.labelMiercoles = labelMiercoles;
    }

    public String getLabelJueves() {
        return labelJueves;
    }

    public void setLabelJueves(String labelJueves) {
        this.labelJueves = labelJueves;
    }

    public String getLabelViernes() {
        return labelViernes;
    }

    public void setLabelViernes(String labelViernes) {
        this.labelViernes = labelViernes;
    }

    public String getLabelSabado() {
        return labelSabado;
    }

    public void setLabelSabado(String labelSabado) {
        this.labelSabado = labelSabado;
    }

    public String getLabelDomingo() {
        return labelDomingo;
    }

    public void setLabelDomingo(String labelDomingo) {
        this.labelDomingo = labelDomingo;
    }

    public String getStyleLunes() {
        return styleLunes;
    }

    public void setStyleLunes(String styleLunes) {
        this.styleLunes = styleLunes;
    }

    public String getStyleMartes() {
        return styleMartes;
    }

    public void setStyleMartes(String styleMartes) {
        this.styleMartes = styleMartes;
    }

    public String getStyleMiercoles() {
        return styleMiercoles;
    }

    public void setStyleMiercoles(String styleMiercoles) {
        this.styleMiercoles = styleMiercoles;
    }

    public String getStyleJueves() {
        return styleJueves;
    }

    public void setStyleJueves(String styleJueves) {
        this.styleJueves = styleJueves;
    }

    public String getStyleViernes() {
        return styleViernes;
    }

    public void setStyleViernes(String styleViernes) {
        this.styleViernes = styleViernes;
    }

    public String getStyleSabado() {
        return styleSabado;
    }

    public void setStyleSabado(String styleSabado) {
        this.styleSabado = styleSabado;
    }

    public String getStyleDomingo() {
        return styleDomingo;
    }

    public void setStyleDomingo(String styleDomingo) {
        this.styleDomingo = styleDomingo;
    }

    public SelectItem[] getComboFormaPago() {
        return comboFormaPago;
    }

    public void setComboFormaPago(SelectItem[] comboFormaPago) {
        this.comboFormaPago = comboFormaPago;
    }

    public SelectItem[] getComboTipoFamiliar() {
        return comboTipoFamiliar;
    }

    public void setComboTipoFamiliar(SelectItem[] comboTipoFamiliar) {
        this.comboTipoFamiliar = comboTipoFamiliar;
    }

    public SelectItem[] getComboUnidad() {
        return comboUnidad;
    }

    public void setComboUnidad(SelectItem[] comboUnidad) {
        this.comboUnidad = comboUnidad;
    }

    public float getModalMontoPagoAlumno() {
        return modalMontoPagoAlumno;
    }

    public void setModalMontoPagoAlumno(float modalMontoPagoAlumno) {
        this.modalMontoPagoAlumno = modalMontoPagoAlumno;
    }
}
