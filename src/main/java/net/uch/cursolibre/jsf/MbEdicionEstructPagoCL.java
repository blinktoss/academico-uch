/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.jsf;

import net.uch.administrativa.dao.HSPagoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.*;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

/**
 * @author cesardl
 */
public class MbEdicionEstructPagoCL {

    private static final Logger LOG = LoggerFactory.getLogger(MbEdicionEstructPagoCL.class);

    private final HSEstructuraPagoDAO daoEstPag = ServiceFinder.findBean("springHibernateDaoCLEstructuraPago", HSEstructuraPagoDAO.class);
    private final HSAlumnoTarifaCLDAO daoAlumnoTarifa = ServiceFinder.findBean("springHibernateDaoCLAlumnoTarifa", HSAlumnoTarifaCLDAO.class);
    private final HSModuloDAO daoModulo = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);

    private int bAluId;
    private String bCodigo;
    private String bPaterno;
    private String bMaterno;
    private String bNombre;
    private String bAlumno;
    private String bModulo;
    private String bSubConcepto;
    private float bSubMonto;
    private int bSubAlutarId;
    private String bSubAlutarEstado;
    private Date bSubFechaPago;
    private Date bSubFechaProrroga;
    private String bSubImgCandado;
    private boolean bSubEditDisabled;
    private String bSubImgKlipper;
    private String bSubTitle;
    private String bSubMsj;
    private List<Integer> bAlutarIds;
    private List<SelectItem> parametroAreas;
    private int parametroArea = 0;
    private List<SelectItem> parametroModulos;
    private int parametroModulo;
    //Input del modal nuevo
    private String modalAlumno;
    private int modalId;
    private List<SelectItem> modalModulos;
    private int modalModulo;
    private List<SelectItem> modalPlantillas;
    private int modalPlantilla;
    private List<SelectItem> modalTalleres;
    private int modalTaller;
    private List<SelectItem> modalMatriculas;
    private int modalMatricula;
    //Input del modal editar
    private Date modalPago;
    private Date modalProrroga;
    private float modalMonto;
    //Variables para los efectos
    private String oncomplete;
    private String imagenVer;
    private String sugAlumno;
    private String sugCodigo;
    private boolean mostrar = false;
    private List<MbEdicionEstructPagoCL> listaEstructura;
    private List<MbEdicionEstructPagoCL> subListaEstructura;
    private int tmpAlutar = 0;
    private int tmpEstado = 0;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        // Main Page
        HSAreaDAO dao = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);
        List<ClArea> areas = dao.seleccionarArea("");
        this.setParametroAreas(areas.stream()
                .map(a -> new SelectItem(a.getAreId(), a.getAreDescripcion()))
                .collect(Collectors.toList()));

        setParametroModulos(new ArrayList<>());

        // Modal Nuevo
        List<ClModulo> lMod = daoModulo.listarModulos();
        this.setModalModulos(lMod.stream()
                .map(mod -> new SelectItem(mod.getModId(), mod.getClArea().getAreDescripcion().concat(" :: ").concat(mod.getModDescripcion())))
                .collect(Collectors.toList()));

        setModalPlantillas(new ArrayList<>());
        setModalTalleres(new ArrayList<>());
        setModalMatriculas(new ArrayList<>());
    }

    public String getbModulo() {
        return bModulo;
    }

    public void setbModulo(String bModulo) {
        this.bModulo = bModulo;
    }

    public int getParametroModulo() {
        return parametroModulo;
    }

    public void setParametroModulo(int parametroModulo) {
        this.parametroModulo = parametroModulo;
    }

    public int getParametroArea() {
        return parametroArea;
    }

    public void setParametroArea(int parametroArea) {
        this.parametroArea = parametroArea;
    }

    public List<SelectItem> getParametroAreas() {
        return parametroAreas;
    }

    public void setParametroAreas(List<SelectItem> parametroAreas) {
        this.parametroAreas = parametroAreas;
    }

    public void verModulos(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        parametroModulos.clear();
        if (this.parametroArea != 0) {
            List<ClModulo> modulos = daoModulo.seleccionarModulos(this.parametroArea, "");
            this.setParametroModulos(modulos.stream()
                    .map(m -> new SelectItem(m.getModId(), m.getModDescripcion()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getParametroModulos() {
        return parametroModulos;
    }

    public void setParametroModulos(List<SelectItem> parametroModulos) {
        this.parametroModulos = parametroModulos;
    }

    public int getbAluId() {
        return bAluId;
    }

    public void setbAluId(int bAluId) {
        this.bAluId = bAluId;
    }

    public String getbAlumno() {
        this.setbAlumno(this.getbPaterno() + " " + this.getbMaterno() + ", " + this.getbNombre());
        return bAlumno;
    }

    public void setbAlumno(String bAlumno) {
        this.bAlumno = bAlumno;
    }

    public String getbCodigo() {
        return bCodigo;
    }

    public void setbCodigo(String bCodigo) {
        this.bCodigo = bCodigo;
    }

    public String getbMaterno() {
        return bMaterno;
    }

    public void setbMaterno(String bMaterno) {
        this.bMaterno = bMaterno;
    }

    public String getbNombre() {
        return bNombre;
    }

    public void setbNombre(String bNombre) {
        this.bNombre = bNombre;
    }

    public String getbPaterno() {
        return bPaterno;
    }

    public void setbPaterno(String bPaterno) {
        this.bPaterno = bPaterno;
    }

    public String getbSubConcepto() {
        return bSubConcepto;
    }

    public void setbSubConcepto(String bSubConcepto) {
        this.bSubConcepto = bSubConcepto;
    }

    public float getbSubMonto() {
        return bSubMonto;
    }

    public void setbSubMonto(float bSubMonto) {
        this.bSubMonto = bSubMonto;
    }

    public int getbSubAlutarId() {
        return bSubAlutarId;
    }

    public void setbSubAlutarId(int bSubAlutarId) {
        this.bSubAlutarId = bSubAlutarId;
    }

    public String getbSubAlutarEstado() {
        return bSubAlutarEstado;
    }

    public void setbSubAlutarEstado(String bSubAlutarEstado) {
        this.bSubAlutarEstado = bSubAlutarEstado;
    }

    public Date getbSubFechaPago() {
        return bSubFechaPago;
    }

    public void setbSubFechaPago(Date bSubFechaPago) {
        this.bSubFechaPago = bSubFechaPago;
    }

    public Date getbSubFechaProrroga() {
        return bSubFechaProrroga;
    }

    public void setbSubFechaProrroga(Date bSubFechaProrroga) {
        this.bSubFechaProrroga = bSubFechaProrroga;
    }

    public String getbSubImgCandado() {
        return bSubImgCandado;
    }

    public void setbSubImgCandado(String bSubImgCandado) {
        this.bSubImgCandado = bSubImgCandado;
    }

    public boolean isbSubEditDisabled() {
        return bSubEditDisabled;
    }

    public void setbSubEditDisabled(boolean bSubEditDisabled) {
        this.bSubEditDisabled = bSubEditDisabled;
    }

    public String getbSubImgKlipper() {
        return bSubImgKlipper;
    }

    public void setbSubImgKlipper(String bSubImgKlipper) {
        this.bSubImgKlipper = bSubImgKlipper;
    }

    public String getbSubTitle() {
        return bSubTitle;
    }

    public void setbSubTitle(String bSubTitle) {
        this.bSubTitle = bSubTitle;
    }

    public String getbSubMsj() {
        return bSubMsj;
    }

    public void setbSubMsj(String bSubMsj) {
        this.bSubMsj = bSubMsj;
    }

    public List<Integer> getbAlutarIds() {
        return bAlutarIds;
    }

    public void setbAlutarIds(List<Integer> bAlutarIds) {
        this.bAlutarIds = bAlutarIds;
    }

    public List<MbEdicionEstructPagoCL> getListaEstructura() {
        return listaEstructura;
    }

    public void setListaEstructura(List<MbEdicionEstructPagoCL> listaEstructura) {
        this.listaEstructura = listaEstructura;
    }

    public List<MbEdicionEstructPagoCL> getSubListaEstructura() {
        return subListaEstructura;
    }

    public void setSubListaEstructura(List<MbEdicionEstructPagoCL> subListaEstructura) {
        this.subListaEstructura = subListaEstructura;
    }

    public float getModalMonto() {
        return modalMonto;
    }

    public void setModalMonto(float modalMonto) {
        this.modalMonto = modalMonto;
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

    public void parametrosPlantilla(ActionEvent event) {
        LOG.debug("[{}] For module {}", event.getPhaseId(), this.getModalModulo());
        modalPlantillas.clear();
        modalTalleres.clear();
        modalMatriculas.clear();
        if (this.getModalModulo() != 0) {
            List<ClEstructuraPagos> l = daoEstPag.seleccionarEstructurasXModulo(this.getModalModulo());
            this.setModalPlantillas(l.stream()
                    .map(tmp -> new SelectItem(tmp.getEstpagId(), tmp.getEstpagNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public int getModalModulo() {
        return modalModulo;
    }

    public void setModalModulo(int modalModulo) {
        this.modalModulo = modalModulo;
    }

    public List<SelectItem> getModalModulos() {
        return modalModulos;
    }

    public void setModalModulos(List<SelectItem> modalModulos) {
        this.modalModulos = modalModulos;
    }

    public int getModalPlantilla() {
        return modalPlantilla;
    }

    public void setModalPlantilla(int modalPlantilla) {
        this.modalPlantilla = modalPlantilla;
    }

    public int getModalTaller() {
        return modalTaller;
    }

    public void setModalTaller(int modalTaller) {
        this.modalTaller = modalTaller;
    }

    public List<SelectItem> getModalTalleres() {
        return modalTalleres;
    }

    public void setModalTalleres(List<SelectItem> modalTalleres) {
        this.modalTalleres = modalTalleres;
    }

    public void parametrosTaller(ActionEvent event) {
        LOG.debug("[{}] Module: {} | Template EP: {}", event.getPhaseId(), this.getModalModulo(), this.getModalPlantilla());
        modalTalleres.clear();
        modalMatriculas.clear();
        if (this.getModalModulo() != 0 &&
                this.getModalPlantilla() != 0) {
            HSTallerAperturadoDAO daoTalape = ServiceFinder.findBean("springHibernateDaoCLTallerAperturado", HSTallerAperturadoDAO.class);

            List<ClEstructuraPagosDetalle> lEstruc = daoEstPag.listaEstructurasExistentes(this.getModalModulo(), this.getModalPlantilla(), -1);

            modalTalleres = new ArrayList<>();
            for (ClEstructuraPagosDetalle tmp : lEstruc) {
                List<ClTallerAperturado> talapes = daoTalape.seleccionarTalleresAperturadosPorTaller(tmp.getTalId());
                for (ClTallerAperturado talape : talapes) {
                    modalTalleres.add(new SelectItem(talape.getClTaller().getTalId(), talape.getTalapeDescripcion()));
                }
            }
        }
    }

    public int getModalMatricula() {
        return modalMatricula;
    }

    public void setModalMatricula(int modalMatricula) {
        this.modalMatricula = modalMatricula;
    }

    public void parametrosMatricula(ActionEvent event) {
        LOG.debug("[{}] Alumno: {} | Taller: {}", event.getPhaseId(), this.getModalId(), this.getModalTaller());
        modalMatriculas.clear();
        if (this.getModalId() != 0 &&
                this.getModalTaller() != 0) {
            HSMatriculaCLDAO daoMat = ServiceFinder.findBean("springHibernateDaoCLMatricula", HSMatriculaCLDAO.class);
            List<ClMatricula> l = daoMat.listarMatriculadoTaller(this.getModalId(), this.getModalTaller());
            this.setModalMatriculas(l.stream()
                    .map(mat -> new SelectItem(mat.getMatId(), Objects.toString(mat.getMatCodigo(), "").concat(" " + mat.getMatFecha())))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getModalMatriculas() {
        return modalMatriculas;
    }

    public void setModalMatriculas(List<SelectItem> modalMatriculas) {
        this.modalMatriculas = modalMatriculas;
    }

    public String getModalAlumno() {
        return modalAlumno;
    }

    public void setModalAlumno(String modalAlumno) {
        this.modalAlumno = modalAlumno;
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
        LOG.debug("[{}] NUEVO ALUTAR", event.getPhaseId());
        this.setModalModulo(0);
        this.setModalPlantilla(0);
        this.setModalId(0);
        this.setModalAlumno("");
        this.setOncomplete("");
        this.setSugCodigo("");
        this.setSugAlumno("");
        this.setTmpAlutar(0);
        this.setTmpEstado(0);
    }

    public void validarBusqueda(ActionEvent event) {
        LOG.debug("[{}] Searching '{}' | '{}' | '{}' | '{}' | {}", event.getPhaseId(),
                bCodigo, bPaterno, bMaterno, bNombre, parametroModulo);
        this.setOncomplete("");
        if (this.getbCodigo().trim().isEmpty() &&
                this.getbPaterno().trim().isEmpty() &&
                this.getbMaterno().trim().isEmpty() &&
                this.getbNombre().trim().isEmpty()) {
            this.setOncomplete("alert('Ingrese algun parametro para la busqueda');");
        } else if (this.getParametroModulo() == 0) {
            this.setOncomplete("alert('Seleccione algun modulo');");
        } else {
            realizarBusqueda(this.getbCodigo(), this.getbPaterno(),
                    this.getbMaterno(), this.getbNombre(), this.getParametroModulo());
        }
    }

    public void realizarBusqueda(String code, String paterno, String materno, String nombre, int modulo) {
        this.setListaEstructura(null);
        HSAlumnoCLDAO dao = ServiceFinder.findBean("springHibernateDaoCLAlumno", HSAlumnoCLDAO.class);

        List<ClAlumno> l = dao.listaAlumnosDatos(code, paterno, materno, nombre, modulo);
        List<MbEdicionEstructPagoCL> estructuras = new ArrayList<>();
        for (ClAlumno alumno : l) {
            var tmpMantEstPag = new MbEdicionEstructPagoCL();
            tmpMantEstPag.setbAluId(alumno.getAluId());
            tmpMantEstPag.setbCodigo(alumno.getAluCodigo());
            tmpMantEstPag.setbPaterno(alumno.getAluAppaterno());
            tmpMantEstPag.setbMaterno(alumno.getAluApmaterno());
            tmpMantEstPag.setbNombre(alumno.getAluNombres());
            tmpMantEstPag.setbModulo(daoModulo.buscarModulo(modulo).getModDescripcion());

            tmpMantEstPag.setImagenVer(ACTION_IMAGES_DOWN);

            List<ClAlumnoTarifa> lTarifas = dao.listaAlumnoTarifa(alumno.getAluId(), modulo);
            List<MbEdicionEstructPagoCL> subEstructuras = new ArrayList<>();
            List<Integer> vAtids = new ArrayList<>();

            for (ClAlumnoTarifa alumnoTarifa : lTarifas) {
                var subTmp = new MbEdicionEstructPagoCL();
                subTmp.setbSubAlutarId(alumnoTarifa.getAlutarId());
                subTmp.setbSubAlutarEstado(alumnoTarifa.getAlutarEstado());
                subTmp.setbSubFechaPago(alumnoTarifa.getAlutarFechaPago());
                subTmp.setbSubFechaProrroga(alumnoTarifa.getAlutarFechaProrroga());
                subTmp.setbSubMonto(alumnoTarifa.getAlutarMonto());
                subTmp.setbSubConcepto(alumnoTarifa.getClEstructuraPagosDetalle().getEstpagdetNombre());

                if (alumnoTarifa.getAlutarEstado().equals("2")) {
                    subTmp.setbSubImgCandado(ACTION_IMAGES_CERRADO);
                    subTmp.setbSubTitle("Abrir pago");
                    subTmp.setbSubMsj("de abrir el candado?");
                    subTmp.setbSubEditDisabled(true);
                    subTmp.setbSubImgKlipper("/Imagenes/actions/klipper_dock_gris.png");
                } else {
                    subTmp.setbSubImgCandado(ACTION_IMAGES_ABIERTO);
                    subTmp.setbSubTitle("Cerrar pago");
                    subTmp.setbSubMsj("cerrar el candado? la siguiente accion se realizara bajo su responsabilidad?");
                    subTmp.setbSubEditDisabled(false);
                    subTmp.setbSubImgKlipper("/Imagenes/actions/klipper_dock.png");
                }
                subEstructuras.add(subTmp);
                vAtids.add(alumnoTarifa.getAlutarId());
            }
            tmpMantEstPag.setSubListaEstructura(subEstructuras);
            tmpMantEstPag.setbAlutarIds(vAtids);
            estructuras.add(tmpMantEstPag);
        }
        this.setListaEstructura(new ArrayList<>(estructuras));
        LOG.info("Total of Estructuras de Pago CL {}", estructuras.size());
    }

    public void openModalToEdit(ActionEvent event) {
        LOG.debug("[{}] editara!!!", event.getPhaseId());
        MbEdicionEstructPagoCL row = (MbEdicionEstructPagoCL) event.getComponent()
                .getAttributes().get("estructura_pago_cl");

        this.setModalMonto(row.getbSubMonto());
        this.setModalPago(row.getbSubFechaPago());
        this.setModalProrroga(row.getbSubFechaProrroga());
        this.setTmpAlutar(row.getbSubAlutarId());
    }

    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        daoAlumnoTarifa.actualizarDatosAlumnoTarifa(this.getTmpAlutar(), this.getModalMonto(),
                this.getModalPago(), this.getModalProrroga());

        realizarBusqueda(this.getbCodigo(), this.getbPaterno(),
                this.getbMaterno(), this.getbNombre(), this.getParametroModulo());

        this.setOncomplete("alert('SE ACTUALIZO CORRECTAMENTE LOS DATOS');"
                + "Richfaces.hideModalPanel('mpEditar')");
    }

    @SuppressWarnings("unchecked")
    public void askToDelete(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter estpagAlutarIds = (UIParameter) event.getComponent().findComponent("estpag_alutarIds");
        List<Integer> vAlutarIds = (List<Integer>) estpagAlutarIds.getValue();

        this.setbAlutarIds(vAlutarIds);
    }

    public void eliminarEstructura(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        HSPagoDAO daoPago = ServiceFinder.findBean("springHibernateDaoPago", HSPagoDAO.class);
        List<Integer> vAlutarIds = this.getbAlutarIds();
        //Suma los montos para un alutarId de AdPago
        float montoTotal = 0;
        for (Integer vAlutarId : vAlutarIds) {
            List<AdPago> ltmp = daoPago.listaPagosAlumno(vAlutarId, 1);

            for (AdPago adPago : ltmp) {
                montoTotal += adPago.getPagMonto();
            }
        }
        if (montoTotal == 0) {
            daoAlumnoTarifa.eliminarAlumnosTarifa(vAlutarIds);

            realizarBusqueda(this.getbCodigo(), this.getbPaterno(),
                    this.getbMaterno(), this.getbNombre(), this.getParametroModulo());

            this.setOncomplete("alert('Se eliminaron las estructuras, no habia pagos registrados');"
                    + "Richfaces.hideModalPanel('mpEliminar')");
        } else {
            this.setOncomplete("alert('No se puede eliminar, hay pagos realizados');"
                    + "Richfaces.hideModalPanel('mpEliminar')");
        }
    }

    public void mostrarDetalle(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.isMostrar()) {
            this.setMostrar(false);
            this.setImagenVer(ACTION_IMAGES_DOWN);
        } else {
            this.setMostrar(true);
            this.setImagenVer(ACTION_IMAGES_UP);
        }
    }

    public void askToOpenCloseLock(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        MbEdicionEstructPagoCL row = (MbEdicionEstructPagoCL) event.getComponent()
                .getAttributes().get("estructura_pago_cl");

        this.setTmpAlutar(row.getbSubAlutarId());
        this.setTmpEstado(Integer.parseInt(row.getbSubAlutarEstado()));
        this.setbSubMsj(row.getbSubMsj());
    }

    public void actualizarEstado(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");

        HSPagoDAO daoPago = ServiceFinder.findBean("springHibernateDaoPago", HSPagoDAO.class);

        // Monto de AdPago, lo que ha cancelado
        List<AdPago> ltmp = daoPago.listaPagosAlumno(this.getTmpAlutar(), 0);
        float montoTotal = 0;
        for (AdPago pag : ltmp) {
            montoTotal += pag.getPagMonto();
        }
        String mensaje;
        if (this.getTmpEstado() == 2) {
            if (montoTotal == 0) {
                daoPago.actualizarEstadoPago(this.getTmpAlutar(), "0");
            } else {
                daoPago.actualizarEstadoPago(this.getTmpAlutar(), "1");
            }
            mensaje = "Se abre el candado";
        } else {
            // Monto que debe pagar, el que se genera en la Estructura
            var tAlutar = daoPago.getClAlumnoTarifa(this.getTmpAlutar());
            float montoExiste = tAlutar.getAlutarMonto();
            if (montoTotal >= montoExiste) {
                mensaje = "se cerrara el candado, tiene los pagos al dia";
                daoPago.actualizarEstadoPago(this.getTmpAlutar(), "2");

                HSMatriculaCLDAO daoMatricula = ServiceFinder.findBean("springHibernateDaoCLMatricula", HSMatriculaCLDAO.class);
                daoMatricula.actualizarEstadoMatricula(CAT_TIPO_MATRICULA_REGULAR, tAlutar.getMatId());
            } else if (montoTotal > 0) {
                mensaje = "Aun presenta deudas";
                daoPago.actualizarEstadoPago(this.getTmpAlutar(), "1");
            } else {
                mensaje = "No se puede cerrar el candado, presenta deudas";
            }
        }
        realizarBusqueda(this.getbCodigo(), this.getbPaterno(),
                this.getbMaterno(), this.getbNombre(), this.getParametroModulo());

        this.setOncomplete("alert('" + mensaje + "');"
                + "Richfaces.hideModalPanel('mpCandado');");
    }

    public List<MbEdicionEstructPagoCL> autocomplete(Object suggest) {
        LOG.debug("Looking for payment structure '{}'", suggest);
        List<MbEdicionEstructPagoCL> l = new ArrayList<>();
        if (!"null".equals(suggest) && !ObjectUtils.isEmpty(suggest)) {
            var pref = String.valueOf(suggest);
            HSAlumnoCLDAO dao = ServiceFinder.findBean("springHibernateDaoCLAlumno", HSAlumnoCLDAO.class);

            List<ClAlumno> alumnos = dao.seleccionarAlumnosPorApellidos(pref);
            for (ClAlumno alu : alumnos) {
                var tmp = new MbEdicionEstructPagoCL();
                tmp.setbAluId(alu.getAluId());
                tmp.setbCodigo(alu.getAluCodigo());
                tmp.setbPaterno(alu.getAluAppaterno());
                tmp.setbMaterno(alu.getAluApmaterno());
                tmp.setbNombre(alu.getAluNombres());
                l.add(tmp);
            }
            LOG.info("Total autocomplete {}", l.size());
        }
        return l;
    }

    public void validarEstructuras(ActionEvent event) {
        this.setOncomplete("");
        int aluId = this.getModalId();
        int modId = this.getModalModulo();
        int estpagId = this.getModalPlantilla();
        int talId = this.getModalTaller();
        int matId = this.getModalMatricula();
        LOG.debug("[{}] Generating for {} | {} | {} | {} | {}",
                event.getPhaseId(), aluId, modId, estpagId, talId, matId);

        if (aluId == 0) {
            this.setOncomplete("alert('SELECCIONE UN ALUMNO');");
        } else if (modId == 0) {
            this.setOncomplete("alert('SELECCIONE UN MODULO');");
        } else if (estpagId == 0) {
            this.setOncomplete("alert('SELECCIONE UNA PLANTILLA');");
        } else if (talId == 0) {
            this.setOncomplete("alert('SELECCIONE UN TALLER');");
        } else if (matId == 0) {
            this.setOncomplete("alert('SELECCIONE UNA MATRICULA DEL ALUMNO');");
        } else {
            List<ClEstructuraPagosDetalle> lEstruc = daoEstPag.listaEstructurasExistentes(modId, estpagId, talId);
            List<ClAlumnoTarifa> lAlutar = daoEstPag.listaAlumnoTarifaExistentes(aluId, modId, estpagId);

            int iEstruc = lEstruc.size();
            int iAlutar = lAlutar.size();

            if (iEstruc == iAlutar) {
                this.setOncomplete("alert('ESTAN COMPLETAS LAS ESTRUCTURAS');"
                        + "Richfaces.hideModalPanel('mpEstructuraNuevo')");
            } else {
                this.generarEstructuras(lEstruc, lAlutar, matId);
            }
        }
    }

    private void generarEstructuras(List<ClEstructuraPagosDetalle> lEstpag, List<ClAlumnoTarifa> lAlutar, int matId) {
        this.setOncomplete("");
        if (lAlutar.isEmpty()) {
            for (ClEstructuraPagosDetalle estpagdet : lEstpag) {
                var alutar = new ClAlumnoTarifa();

                alutar.setAlutarMonto(estpagdet.getEstpagdetMonto());
                alutar.setAlutarFechaPago(estpagdet.getEstpagdetFechaPago());
                alutar.setAlutarFechaProrroga(estpagdet.getEstpagdetFechaPago());
                alutar.setAlutarActivo("1");
                alutar.setAlutarEstado("0");
                alutar.setAlutarAluTipo(CAT_TIPO_CLIENTE_ALUMNO_CL);
                alutar.setMatId(matId);
                alutar.setSecId(0);
                alutar.setConpagId(estpagdet.getAdConceptoPago().getId());
                alutar.setClEstructuraPagosDetalle(estpagdet);
                var alu = new ClAlumno();
                alu.setAluId(this.getModalId());
                alutar.setClAlumno(alu);

                daoAlumnoTarifa.generarAlumnoTarifa(alutar);
            }
        } else {
            for (ClEstructuraPagosDetalle estpagdet : lEstpag) {
                var encontrado = false;
                int j;
                for (j = 0; j < lAlutar.size() && !encontrado; j++) {
                    ClAlumnoTarifa alutar = lAlutar.get(j);

                    int iat = alutar.getClEstructuraPagosDetalle().getEstpagdetId();
                    int iep = estpagdet.getEstpagdetId();

                    if (iat == iep) {
                        encontrado = true;
                    }
                }
                if (!encontrado) {
                    var alutar = new ClAlumnoTarifa();

                    alutar.setAlutarMonto(estpagdet.getEstpagdetMonto());
                    alutar.setAlutarFechaPago(estpagdet.getEstpagdetFechaPago());
                    alutar.setAlutarFechaProrroga(estpagdet.getEstpagdetFechaPago());
                    alutar.setAlutarActivo("1");
                    alutar.setAlutarEstado("0");
                    alutar.setAlutarAluTipo(CAT_TIPO_CLIENTE_ALUMNO_CL);
                    alutar.setMatId(matId);
                    alutar.setSecId(0);
                    alutar.setConpagId(estpagdet.getAdConceptoPago().getId());
                    alutar.setClEstructuraPagosDetalle(estpagdet);
                    var alu = new ClAlumno();
                    alu.setAluId(this.getModalId());
                    alutar.setClAlumno(alu);

                    daoAlumnoTarifa.generarAlumnoTarifa(alutar);
                }
            }
        }

        this.setOncomplete("alert('SE GENERARON CORRECTAMENTE LAS ESTRUCTURAS');"
                + "Richfaces.hideModalPanel('mpEstructuraNuevo')");
    }
}
