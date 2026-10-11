package net.uch.administrativa.jsf;

import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.administrativa.dao.HSEdicionEstructPagoDAO;
import net.uch.administrativa.dao.HSProrrogaDAO;
import net.uch.administrativa.service.ProrrogaService;
import net.uch.administrativa.service.ProrrogaValidationException;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

/**
 * @author cesardl
 */
public class MbEdicionEstructPago {

    private static final Logger LOG = LoggerFactory.getLogger(MbEdicionEstructPago.class);

    private final HSAlumnoTarifaDAO daoTarifa = ServiceFinder.findBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class);
    private final HSProrrogaDAO daoProrroga = ServiceFinder.findBean("springHibernateDaoProrroga", HSProrrogaDAO.class);
    private final HSEdicionEstructPagoDAO daoEdicionEstructura = ServiceFinder.findBean("springHibernateDaoEdicionEstructura", HSEdicionEstructPagoDAO.class);
    private final ProrrogaService prorrogaService = ServiceFinder.findBean("prorrogaService", ProrrogaService.class);

    private int alumnoId;
    private String parametroCodigo;
    private String parametroPaterno;
    private String parametroMaterno;
    private String parametroNombre;
    private String especialidad;
    private String semestreIngreso;
    private String semestreEstructura;
    private int semestreEstructuraId;
    private String subConcepto;
    private float subMonto;
    private int subAlutarId;
    private int subAlutarEstado;
    private Date subFechaPago;
    private Date subFechaProrroga;
    private String subImgCandado;
    private boolean subEditDisabled;
    private String subImgKlipper;
    private String subImgCalendar;
    private String subTitle;
    private String subMensaje;
    private List<Integer> alutarIds;
    //Parametro de busqueda
    private SelectItem[] parametroSemestres;
    private int parametroSemestre;
    //Input del modal nuevo
    private int modalEspecialidad;
    private int modalSemIngreso;
    private int modalId;
    private SelectItem[] modalSemestres;
    private int modalSemestre;
    private List<SelectItem> modalPlantillas;
    private int modalPlantilla;
    //Input del modal editar
    private Date modalPago;
    private Date modalProrroga;
    private float modalMonto;
    //Para el modal de la edicion d prorroga
    private String proInfoDescConcepto;
    private Date proInfoFechaPago;
    private Date proInfoFechaProrroga;
    private Date proParamFecha;
    private String proParamComentario;
    private String proNumero;
    private Date proFecha;
    private String proComentario;
    private List<MbEdicionEstructPago> proListaProrrogas;
    //Variables para los efectos
    private String oncomplete;
    private String imagenVer;
    private String estadoVer;
    private String sugAlumno;
    private String sugCodigo;
    private boolean mostrar = false;
    private List<MbEdicionEstructPago> listaEstructura;
    private List<MbEdicionEstructPago> subListaEstructura;
    private int tmpAlutar = 0;
    private int tmpEstado = 0;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        HSSemestreDAO dao = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        SelectItem[] l = dao.seleccionarSemestreActivo().stream()
                .map(tmp -> new SelectItem(tmp.getId(), tmp.getSemNombre()))
                .toArray(SelectItem[]::new);

        this.setParametroSemestres(l);
        // For Modal
        this.setModalSemestres(l);
        modalPlantillas = new ArrayList<>();
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getParametroCodigo() {
        return parametroCodigo;
    }

    public void setParametroCodigo(String parametroCodigo) {
        this.parametroCodigo = parametroCodigo;
    }

    public String getParametroMaterno() {
        return parametroMaterno;
    }

    public void setParametroMaterno(String parametroMaterno) {
        this.parametroMaterno = parametroMaterno;
    }

    public String getParametroNombre() {
        return parametroNombre;
    }

    public void setParametroNombre(String parametroNombre) {
        this.parametroNombre = parametroNombre;
    }

    public String getParametroPaterno() {
        return parametroPaterno;
    }

    public void setParametroPaterno(String parametroPaterno) {
        this.parametroPaterno = parametroPaterno;
    }

    public String getSemestreEstructura() {
        return semestreEstructura;
    }

    public void setSemestreEstructura(String semestreEstructura) {
        this.semestreEstructura = semestreEstructura;
    }

    public String getSubConcepto() {
        return subConcepto;
    }

    public void setSubConcepto(String subConcepto) {
        this.subConcepto = subConcepto;
    }

    public float getSubMonto() {
        return subMonto;
    }

    public void setSubMonto(float subMonto) {
        this.subMonto = subMonto;
    }

    public int getSubAlutarId() {
        return subAlutarId;
    }

    public void setSubAlutarId(int subAlutarId) {
        this.subAlutarId = subAlutarId;
    }

    public int getSubAlutarEstado() {
        return subAlutarEstado;
    }

    public void setSubAlutarEstado(int subAlutarEstado) {
        this.subAlutarEstado = subAlutarEstado;
    }

    public Date getSubFechaPago() {
        return subFechaPago;
    }

    public void setSubFechaPago(Date subFechaPago) {
        this.subFechaPago = subFechaPago;
    }

    public Date getSubFechaProrroga() {
        return subFechaProrroga;
    }

    public void setSubFechaProrroga(Date subFechaProrroga) {
        this.subFechaProrroga = subFechaProrroga;
    }

    public String getSubImgCandado() {
        return subImgCandado;
    }

    public void setSubImgCandado(String subImgCandado) {
        this.subImgCandado = subImgCandado;
    }

    public boolean isSubEditDisabled() {
        return subEditDisabled;
    }

    public void setSubEditDisabled(boolean subEditDisabled) {
        this.subEditDisabled = subEditDisabled;
    }

    public String getSubImgKlipper() {
        return subImgKlipper;
    }

    public void setSubImgKlipper(String subImgKlipper) {
        this.subImgKlipper = subImgKlipper;
    }

    public String getSubImgCalendar() {
        return subImgCalendar;
    }

    public void setSubImgCalendar(String subImgCalendar) {
        this.subImgCalendar = subImgCalendar;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSubMensaje() {
        return subMensaje;
    }

    public void setSubMensaje(String subMensaje) {
        this.subMensaje = subMensaje;
    }

    public List<Integer> getAlutarIds() {
        return alutarIds;
    }

    public void setAlutarIds(List<Integer> alutarIds) {
        this.alutarIds = alutarIds;
    }

    public String getSemestreIngreso() {
        return semestreIngreso;
    }

    public void setSemestreIngreso(String semestreIngreso) {
        this.semestreIngreso = semestreIngreso;
    }

    public int getSemestreEstructuraId() {
        return semestreEstructuraId;
    }

    public void setSemestreEstructuraId(int semestreEstructuraId) {
        this.semestreEstructuraId = semestreEstructuraId;
    }

    public List<MbEdicionEstructPago> getListaEstructura() {
        return listaEstructura;
    }

    public void setListaEstructura(List<MbEdicionEstructPago> listaEstructura) {
        this.listaEstructura = listaEstructura;
    }

    public List<MbEdicionEstructPago> getSubListaEstructura() {
        return subListaEstructura;
    }

    public void setSubListaEstructura(List<MbEdicionEstructPago> subListaEstructura) {
        this.subListaEstructura = subListaEstructura;
    }

    public SelectItem[] getParametroSemestres() {
        return parametroSemestres;
    }

    public void setParametroSemestres(SelectItem[] parametroSemestres) {
        this.parametroSemestres = parametroSemestres;
    }

    public int getParametroSemestre() {
        return parametroSemestre;
    }

    public void setParametroSemestre(int parametroSemestre) {
        this.parametroSemestre = parametroSemestre;
    }

    public int getModalPlantilla() {
        return modalPlantilla;
    }

    public void setModalPlantilla(int modalPlantilla) {
        this.modalPlantilla = modalPlantilla;
    }

    public float getModalMonto() {
        return modalMonto;
    }

    public void setModalMonto(float modalMonto) {
        this.modalMonto = modalMonto;
    }

    public Date getProFecha() {
        return proFecha;
    }

    public void setProFecha(Date proFecha) {
        this.proFecha = proFecha;
    }

    public String getProComentario() {
        return proComentario;
    }

    public void setProComentario(String proComentario) {
        this.proComentario = proComentario;
    }

    public String getProParamComentario() {
        return proParamComentario;
    }

    public void setProParamComentario(String proParamComentario) {
        this.proParamComentario = proParamComentario;
    }

    public int getTmpAlutar() {
        return tmpAlutar;
    }

    public void setTmpAlutar(int tmpAlutar) {
        this.tmpAlutar = tmpAlutar;
    }

    public int getTmpEstado() {
        return tmpEstado;
    }

    public void setTmpEstado(int tmpEstado) {
        this.tmpEstado = tmpEstado;
    }

    public List<MbEdicionEstructPago> getProListaProrrogas() {
        return proListaProrrogas;
    }

    public void setProListaProrrogas(List<MbEdicionEstructPago> proListaProrrogas) {
        this.proListaProrrogas = proListaProrrogas;
    }

    public String getProInfoDescConcepto() {
        return proInfoDescConcepto;
    }

    public void setProInfoDescConcepto(String proInfoDescConcepto) {
        this.proInfoDescConcepto = proInfoDescConcepto;
    }

    public Date getProInfoFechaPago() {
        return proInfoFechaPago;
    }

    public void setProInfoFechaPago(Date proInfoFechaPago) {
        this.proInfoFechaPago = proInfoFechaPago;
    }

    public Date getProInfoFechaProrroga() {
        return proInfoFechaProrroga;
    }

    public void setProInfoFechaProrroga(Date proInfoFechaProrroga) {
        this.proInfoFechaProrroga = proInfoFechaProrroga;
    }

    public Date getProParamFecha() {
        return proParamFecha;
    }

    public void setProParamFecha(Date proParamFecha) {
        this.proParamFecha = proParamFecha;
    }

    public String getProNumero() {
        return proNumero;
    }

    public void setProNumero(String proNumero) {
        this.proNumero = proNumero;
    }

    public Date getModalPago() {
        return modalPago;
    }

    public void setModalPago(Date modalPago) {
        this.modalPago = modalPago;
    }

    public Date getModalProrroga() {
        return modalProrroga;
    }

    public void setModalProrroga(Date modalProrroga) {
        this.modalProrroga = modalProrroga;
    }

    public List<SelectItem> getModalPlantillas() {
        return modalPlantillas;
    }

    public void setModalPlantillas(List<SelectItem> modalPlantillas) {
        this.modalPlantillas = modalPlantillas;
    }

    public int getModalSemestre() {
        return modalSemestre;
    }

    public void setModalSemestre(int modalSemestre) {
        this.modalSemestre = modalSemestre;
    }

    public SelectItem[] getModalSemestres() {
        return modalSemestres;
    }

    public void setModalSemestres(SelectItem[] modalSemestres) {
        this.modalSemestres = modalSemestres;
    }

    public void parametrosEstructura(ActionEvent event) {
        LOG.debug("[{}] Semestre {} | Especialidad {} | Semestre Ingreso {}", event.getPhaseId(), this.modalSemestre, this.modalEspecialidad, this.modalSemIngreso);
        if (this.getModalSemestre() == 0 ||
                this.getModalEspecialidad() == 0 ||
                this.getModalSemIngreso() == 0) {
            modalPlantillas.clear();
        } else {
            List<AdEstructuraPagos> l = daoEdicionEstructura.listaEstructurasPago(this.getModalSemestre(), this.getModalEspecialidad(), this.getModalSemIngreso());
            this.setModalPlantillas(l.stream()
                    .map(estpag -> new SelectItem(estpag.getId(), estpag.getEstpagoNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public int getModalSemIngreso() {
        return modalSemIngreso;
    }

    public void setModalSemIngreso(int modalSemIngreso) {
        this.modalSemIngreso = modalSemIngreso;
    }

    public int getModalEspecialidad() {
        return modalEspecialidad;
    }

    public void setModalEspecialidad(int modalEspecialidad) {
        this.modalEspecialidad = modalEspecialidad;
    }

    public int getModalId() {
        return modalId;
    }

    public void setModalId(int modalId) {
        this.modalId = modalId;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getImagenVer() {
        return imagenVer;
    }

    public void setImagenVer(String imagenVer) {
        this.imagenVer = imagenVer;
    }

    public String getEstadoVer() {
        return estadoVer;
    }

    public void setEstadoVer(String estadoVer) {
        this.estadoVer = estadoVer;
    }

    public String getSugAlumno() {
        return sugAlumno;
    }

    public void setSugAlumno(String sugAlumno) {
        this.sugAlumno = sugAlumno;
    }

    public String getSugCodigo() {
        return sugCodigo;
    }

    public void setSugCodigo(String sugCodigo) {
        this.sugCodigo = sugCodigo;
    }

    public boolean isMostrar() {
        return mostrar;
    }

    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setModalEspecialidad(0);
        this.setModalSemestre(0);
        this.setModalSemIngreso(0);
        this.setModalPlantilla(0);
        this.setOncomplete("");
        this.setSugCodigo("");
        this.setSugAlumno("");
        this.setTmpAlutar(0);
        this.setTmpEstado(0);
    }

    public void validarBusqueda(ActionEvent event) {
        LOG.debug("[{}] code: '{}' paterno: '{}' materno: '{}' nombre: '{}' semestre: {}", event.getPhaseId(),
                this.getParametroCodigo(), this.getParametroPaterno(), this.getParametroMaterno(), this.getParametroNombre(), this.getParametroSemestre());
        this.setOncomplete("");
        if (this.getParametroCodigo().trim().length() == 0 &&
                this.getParametroPaterno().trim().length() == 0 &&
                this.getParametroMaterno().trim().length() == 0 &&
                this.getParametroNombre().trim().length() == 0) {
            this.setOncomplete("alert('Ingrese algun parametro para la busqueda');");
        } else if (this.getParametroCodigo().trim().length() > 0 &&
                this.getParametroCodigo().trim().length() < 7) {
            this.setOncomplete("alert('Codigo muy corto para realizar busqueda');");
        } else {
            realizarBusqueda(this.getParametroCodigo(), this.getParametroPaterno(),
                    this.getParametroMaterno(), this.getParametroNombre(), this.getParametroSemestre());
        }
    }

    public void realizarBusqueda(String code, String paterno, String materno, String nombre, int semestre) {
        this.setListaEstructura(null);
        List<AcAlumno> alumnos = daoEdicionEstructura.listaAlumnoEstructura(code, paterno, materno, nombre, semestre);
        List<MbEdicionEstructPago> estructuras = new ArrayList<>();
        for (AcAlumno alumno : alumnos) {
            var tmpMantEstPag = new MbEdicionEstructPago();
            tmpMantEstPag.setAlumnoId(alumno.getId());
            tmpMantEstPag.setParametroCodigo(alumno.getAluCodigo());
            tmpMantEstPag.setParametroPaterno(alumno.getAluAppaterno());
            tmpMantEstPag.setParametroMaterno(alumno.getAluApmaterno());
            tmpMantEstPag.setParametroNombre(alumno.getAluNombres());
            tmpMantEstPag.setEspecialidad(alumno.getEsp().getEspNombre());
            tmpMantEstPag.setSemestreIngreso(nombreSemestre(alumno.getSemId()));
            tmpMantEstPag.setSemestreEstructuraId(semestre);
            tmpMantEstPag.setSemestreEstructura(nombreSemestre(semestre));
            tmpMantEstPag.setImagenVer(ACTION_IMAGES_DOWN);
            tmpMantEstPag.setEstadoVer("0");

            List<AdAlumnoTarifa> lTarifas = daoEdicionEstructura.listaTarifasAlumno(semestre, alumno.getId());
            List<MbEdicionEstructPago> subEstructuras = new ArrayList<>();
            List<Integer> vAtids = new ArrayList<>();

            for (AdAlumnoTarifa alumnoTarifa : lTarifas) {
                var subTmp = new MbEdicionEstructPago();
                subTmp.setSubAlutarId(alumnoTarifa.getId());
                subTmp.setSubAlutarEstado(Integer.parseInt(alumnoTarifa.getAlutarEstado()));
                subTmp.setSubFechaPago(alumnoTarifa.getAlutarFechaPago());
                subTmp.setSubFechaProrroga(alumnoTarifa.getAlutarFechaProrroga());
                subTmp.setSubMonto(alumnoTarifa.getAlutarMonto());
                subTmp.setSubConcepto(alumnoTarifa.getEstpagdet().getEstpagdetNombre());

                if (alumnoTarifa.getAlutarEstado().equals("2")) {
                    subTmp.setSubImgCandado(ACTION_IMAGES_CERRADO);
                    subTmp.setSubTitle("Abrir pago");
                    subTmp.setSubMensaje("de abrir el candado?");
                    subTmp.setSubEditDisabled(true);
                    subTmp.setSubImgKlipper("/Imagenes/actions/klipper_dock_gris.png");
                    subTmp.setSubImgCalendar("/Imagenes/actions/calendar_gris.png");
                } else {
                    subTmp.setSubImgCandado(ACTION_IMAGES_ABIERTO);
                    subTmp.setSubTitle("Cerrar pago");
                    subTmp.setSubMensaje("cerrar el candado? la siguiente accion se realizara bajo su responsabilidad?");
                    subTmp.setSubEditDisabled(false);
                    subTmp.setSubImgKlipper("/Imagenes/actions/klipper_dock.png");
                    subTmp.setSubImgCalendar("/Imagenes/actions/calendar.gif");
                }
                subEstructuras.add(subTmp);
                vAtids.add(alumnoTarifa.getId());
            }
            tmpMantEstPag.setSubListaEstructura(subEstructuras);
            tmpMantEstPag.setAlutarIds(vAtids);
            estructuras.add(tmpMantEstPag);
        }
        this.setListaEstructura(estructuras);
        LOG.info("Total of Estructuras de Pago {}", listaEstructura.size());
    }

    public void salvaEditar(ActionEvent event) {
        Object vAlutarId = ((UIParameter) event.getComponent().findComponent(PARAM_ALUTAR_ID)).getValue();
        var alutarId = Integer.parseInt(String.valueOf(vAlutarId));
        LOG.debug("[{}] Loading Alumno Tarifa '{}'", event.getPhaseId(), alutarId);

        AdAlumnoTarifa at = daoTarifa.seleccionarUnAlumnoTarifa(alutarId);
        this.setModalMonto(at.getAlutarMonto());
        this.setModalPago(at.getAlutarFechaPago());
        this.setModalProrroga(at.getAlutarFechaProrroga());
        this.setTmpAlutar(at.getId());
    }

    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        daoEdicionEstructura.actualizarDatosAlumnoTarifa(this.getTmpAlutar(), this.getModalMonto(),
                this.getModalPago());

        realizarBusqueda(this.getParametroCodigo(), this.getParametroPaterno(),
                this.getParametroMaterno(), this.getParametroNombre(), this.getParametroSemestre());

        this.setOncomplete("alert('SE ACTUALIZO CORRECTAMENTE LOS DATOS');"
                + "Richfaces.hideModalPanel('mpEditar')");
    }

    @SuppressWarnings("unchecked")
    public void elimParam(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter estpagAlutarIds = (UIParameter) event.getComponent().findComponent("estpag_alutarIds");
        List<Integer> vAlutarIds = (List<Integer>) estpagAlutarIds.getValue();

        this.setAlutarIds(vAlutarIds);
    }

    public void eliminarEstructura(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        List<Integer> vAlutarIds = this.getAlutarIds();
        //Suma los montos para un alutarId de AdPago
        float montoTotal = 0;
        for (Integer vAlutarId : vAlutarIds) {
            List<AdPago> ltmp = daoEdicionEstructura.listaPagosAlumno(vAlutarId, 1);

            for (AdPago adPago : ltmp) {
                montoTotal += adPago.getPagMonto();
            }
        }
        if (montoTotal == 0) {
            int r = daoEdicionEstructura.eliminarEstructurasPagoAlumnoTarifa(vAlutarIds);
            LOG.info("Total payment structures deleted: {}", r);
            realizarBusqueda(this.getParametroCodigo(), this.getParametroPaterno(),
                    this.getParametroMaterno(), this.getParametroNombre(), this.getParametroSemestre());

            this.setOncomplete("alert('Se eliminaron las estructuras, no habia pagos registrados');"
                    + "Richfaces.hideModalPanel('mpEliminar')");
        } else {
            this.setOncomplete("alert('No se puede eliminar, hay pagos realizados');"
                    + "Richfaces.hideModalPanel('mpEliminar')");
        }
    }

    public void mostrarDetalle(ActionEvent event) {
        UIParameter ui = (UIParameter) event.getComponent().findComponent("p_ver");
        var pVer = String.valueOf(ui.getValue());
        LOG.debug("[{}] Ver '{}'", event.getPhaseId(), pVer);
        if (pVer.equals("0")) {
            this.setMostrar(true);
            this.setEstadoVer("1");
            this.setImagenVer(ACTION_IMAGES_UP);
        } else {
            this.setMostrar(false);
            this.setEstadoVer("0");
            this.setImagenVer(ACTION_IMAGES_DOWN);
        }
    }

    public void salvaParam(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter mensaje = (UIParameter) event.getComponent().findComponent("mensaje");
        UIParameter alutarId = (UIParameter) event.getComponent().findComponent(PARAM_ALUTAR_ID);
        UIParameter alutarEstado = (UIParameter) event.getComponent().findComponent("alutar_estado");

        this.setTmpAlutar(Integer.parseInt(String.valueOf(alutarId.getValue())));
        this.setTmpEstado(Integer.parseInt(String.valueOf(alutarEstado.getValue())));
        this.setSubMensaje(String.valueOf(mensaje.getValue()));
    }

    public void actualizarEstado(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        List<AdPago> ltmp = daoEdicionEstructura.listaPagosAlumno(this.getTmpAlutar(), 0);

        /*
         * Monto de AdPago, lo que ha cancelado
         */
        float montoTotal = 0;
        for (AdPago adPago : ltmp) {
            montoTotal += adPago.getPagMonto();
        }
        String mensaje;
        if (this.getTmpEstado() == 2) {
            if (montoTotal == 0) {
                daoEdicionEstructura.actualizarEstadoPago(this.getTmpAlutar(), "0");
            } else {
                daoEdicionEstructura.actualizarEstadoPago(this.getTmpAlutar(), "1");
            }
            mensaje = "Se abre el candado";
        } else {
            /*
             * Monto que debe pagar, el que se genera en la Estructura
             */
            AdAlumnoTarifa tAlutar = daoTarifa.seleccionarUnAlumnoTarifa(this.getTmpAlutar());
            float montoExiste = tAlutar.getAlutarMonto();
            if (montoTotal >= montoExiste) {
                mensaje = "se cerrara el candado, tiene los pagos al dia";
                daoEdicionEstructura.actualizarEstadoPago(this.getTmpAlutar(), "2");
            } else if (montoTotal > 0) {
                mensaje = "Aun presenta deudas";
                daoEdicionEstructura.actualizarEstadoPago(this.getTmpAlutar(), "1");
            } else {
                mensaje = "No se puede cerrar el candado, presenta deudas";
            }
        }
        realizarBusqueda(this.getParametroCodigo(), this.getParametroPaterno(),
                this.getParametroMaterno(), this.getParametroNombre(), this.getParametroSemestre());

        this.setOncomplete("alert('" + mensaje + "');"
                + "Richfaces.hideModalPanel('mpCandado')");
    }

    public List<MbEdicionEstructPago> autocomplete(Object suggest) {
        if ("null".equals(suggest) || ObjectUtils.isEmpty(suggest)) {
            return Collections.emptyList();
        }
        var ref = String.valueOf(suggest);
        List<MbEdicionEstructPago> l = new ArrayList<>();
        LOG.debug("Looking for payment structure '{}'", ref);
        List<AcAlumno> alumnos = daoEdicionEstructura.listaAlumnoActivo(ref);
        for (AcAlumno alu : alumnos) {
            var tmp = new MbEdicionEstructPago();
            tmp.setAlumnoId(alu.getId());
            tmp.setParametroCodigo(alu.getAluCodigo());
            tmp.setParametroPaterno(alu.getAluAppaterno());
            tmp.setParametroMaterno(alu.getAluApmaterno());
            tmp.setParametroNombre(alu.getAluNombres());
            tmp.setModalEspecialidad(alu.getEspIdIngreso());
            tmp.setModalSemIngreso(alu.getSemId());
            l.add(tmp);
        }
        LOG.info("Total autocomplete {}", l.size());
        return l;
    }

    public void validarEstructuras(ActionEvent event) {
        this.setOncomplete("");
        int aluId = this.getModalId();
        int semIngreso = this.getModalSemIngreso();
        int esp = this.getModalEspecialidad();
        int sem = this.getModalSemestre();
        LOG.debug("[{}] {} | {} | {} | {}", event.getPhaseId(), aluId, semIngreso, esp, sem);

        List<AdEstructuraPagos> lEstruc = daoEdicionEstructura.listaEstructurasExistentes(sem, esp, semIngreso);
        List<AdAlumnoTarifa> lAlutar = daoEdicionEstructura.listaAlumnoTarifaExistentes(aluId, sem, esp);

        int iEstruc = lEstruc.size();
        int iAlutar = lAlutar.size();
        if (iEstruc == iAlutar) {
            LOG.warn("Payment structures are already completed.");
            this.setOncomplete("alert('ESTAN COMPLETAS LAS ESTRUCTURAS');"
                    + "Richfaces.hideModalPanel('mpEstructuraNuevo')");
        } else {
            AdEstructuraPagos estpag = daoEdicionEstructura.estructuraPagoPlantilla(this.getModalPlantilla());
            this.generarEstructuras(iAlutar, estpag, lAlutar);
        }
    }

    public void generarEstructuras(int iAlutar, AdEstructuraPagos estpag, List<AdAlumnoTarifa> lAlutar) {
        Set<AdEstructuraPagosDetalle> tmp = estpag.getAdEstructuraPagosDetalles();
        if (iAlutar == 0) {
            for (AdEstructuraPagosDetalle estpagdet : tmp) {
                var alumnoTarifa = new AdAlumnoTarifa();

                alumnoTarifa.setAlutarMonto(estpagdet.getEstpagdetMonto());
                alumnoTarifa.setAlutarFechaPago(estpagdet.getEstpagdetFechaPago());
                alumnoTarifa.setAlutarFechaProrroga(estpagdet.getEstpagdetFechaPago());
                alumnoTarifa.setAlutarActivo("1");
                alumnoTarifa.setAlutarEstado("0");
                alumnoTarifa.setConpag(new AdConceptoPago(estpagdet.getConpagId()));
                alumnoTarifa.setAlu(new AcAlumno(this.getModalId()));
                alumnoTarifa.setEstpagdet(estpagdet);

                daoEdicionEstructura.insertarAlumnoTarifa(alumnoTarifa);
            }
        } else {
            for (AdEstructuraPagosDetalle estpagdet : tmp) {
                var encontrado = false;
                int j;
                for (j = 0; j < lAlutar.size() && !encontrado; j++) {
                    AdAlumnoTarifa alutar = lAlutar.get(j);

                    int iat = alutar.getEstpagdet().getId();
                    int iep = estpagdet.getId();

                    if (iat == iep) {
                        encontrado = true;
                    }
                }
                if (!encontrado) {
                    var alumnoTarifa = new AdAlumnoTarifa();

                    alumnoTarifa.setAlutarMonto(estpagdet.getEstpagdetMonto());
                    alumnoTarifa.setAlutarFechaPago(estpagdet.getEstpagdetFechaPago());
                    alumnoTarifa.setAlutarFechaProrroga(estpagdet.getEstpagdetFechaPago());
                    alumnoTarifa.setAlutarActivo("1");
                    alumnoTarifa.setAlutarEstado("0");
                    alumnoTarifa.setConpag(new AdConceptoPago(estpagdet.getConpagId()));
                    alumnoTarifa.setAlu(new AcAlumno(this.getModalId()));
                    alumnoTarifa.setEstpagdet(estpagdet);

                    daoEdicionEstructura.insertarAlumnoTarifa(alumnoTarifa);
                }
            }
        }

        this.setOncomplete("alert('SE GENERARON CORRECTAMENTE LAS ESTRUCTURAS');"
                + "Richfaces.hideModalPanel('mpEstructuraNuevo')");
    }

    private String nombreSemestre(int key) {
        return Arrays.stream(parametroSemestres)
                .filter(selectItem -> Integer.parseInt(String.valueOf(selectItem.getValue())) == key)
                .findFirst().orElseThrow(IllegalArgumentException::new)
                .getLabel();
    }

    public void salvaCompromiso(ActionEvent event) {
        Object vAlutarId = ((UIParameter) event.getComponent().findComponent(PARAM_ALUTAR_ID)).getValue();
        var alutarId = Integer.parseInt(String.valueOf(vAlutarId));
        LOG.debug("[{}] Loading Alumno Tarifa '{}'", event.getPhaseId(), alutarId);

        AdAlumnoTarifa at = daoTarifa.seleccionarUnAlumnoTarifa(alutarId);

        this.setTmpAlutar(alutarId);
        this.setProInfoFechaPago(at.getAlutarFechaPago());
        this.setProInfoFechaProrroga(at.getAlutarFechaProrroga());
        this.setProInfoDescConcepto(at.getEstpagdet().getEstpagdetNombre());

        loadHistorialProrrogas();
        this.setProParamFecha(null);
        this.setProParamComentario("");
    }

    /**
     * Loads the read-only extension history (number, date, comment) for the current tariff.
     */
    private void loadHistorialProrrogas() {
        List<AdProrroga> l = daoProrroga.listaProrrogas(this.getTmpAlutar());
        List<MbEdicionEstructPago> historial = new ArrayList<>(l.size());
        for (AdProrroga prorroga : l) {
            var tmp = new MbEdicionEstructPago();
            tmp.setProNumero(prorroga.getProNumero());
            tmp.setProFecha(prorroga.getProFecha());
            tmp.setProComentario(prorroga.getProComentario());
            historial.add(tmp);
        }
        setProListaProrrogas(historial);
        LOG.info("Total of prorrogas {}", historial.size());
    }

    /**
     * Registers a single new payment extension - the only place one is created. The service validates
     * the commitment date, appends the auto-numbered ad_prorroga history row, and syncs the tariff's
     * effective deadline to it. There is no add-to-list staging and no delete: the history is an
     * append-only audit trail (issue #33).
     */
    public void guardarCompromiso(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        try {
            prorrogaService.registerExtension(this.getTmpAlutar(), this.getProParamFecha(),
                    this.getProParamComentario(), ServiceFinder.getLoggedUser().getRowUsuId());
        } catch (ProrrogaValidationException e) {
            this.setOncomplete("alert('" + e.getMessage() + "');");
            return;
        }
        this.setProParamFecha(null);
        this.setProParamComentario("");
        // F6: rebuild the master list so the reRendered grid shows the new deadline immediately -
        // consistent with editar(). The prorroga modal is always reached from a populated search.
        realizarBusqueda(this.getParametroCodigo(), this.getParametroPaterno(),
                this.getParametroMaterno(), this.getParametroNombre(), this.getParametroSemestre());
        this.setOncomplete("alert('Se registró el compromiso de pago.');"
                + "Richfaces.hideModalPanel('mpProrroga');");
    }
}
