package net.uch.administrativa.jsf;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.administrativa.dao.HSConceptoPagoDAO;
import net.uch.administrativa.dao.HSEstructuraPagoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
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

import static net.uch.common.Constants.*;

public class MbEstructuraPago {

    private static final Logger LOG = LoggerFactory.getLogger(MbEstructuraPago.class);

    private final HSConceptoPagoDAO daoConceptoPago = ServiceFinder.findBean("springHibernateDaoConceptoPago", HSConceptoPagoDAO.class);
    private final HSEspecialidadDAO daoEspecialidad = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
    private final HSEstructuraPagoDAO daoEstructuraPagos = ServiceFinder.findBean("springHibernateDaoEstructuraPagos", HSEstructuraPagoDAO.class);
    private final HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
    /**
     * para la busqueda
     * vriables de tipo global
     * numero maximo de registros que se presentaran en el comcom de semestres;
     */
    private int paramFacuId;
    private int paramEspeId;
    private int paramSemeId;
    private String flag = "0";
    private String imagen = ACTION_IMAGES_DOWN;
    private String rowImgPublish = "/Imagenes/actions/matricula.png";
    private String rowImgEdit = ACTION_IMAGES_EDIT_PASTE;
    private String rowImgDelete = "/Imagenes/actions/no.png";
    private boolean disabledPublish = false;
    private boolean disabledDelete = false;
    private List<SelectItem> comboFacultad;
    private List<SelectItem> comboEspecialidadBuscar;
    private List<SelectItem> comboSemestre;
    private List<SelectItem> mpComboEspecialidad;
    private List<SelectItem> comboConceptoPago;
    private int rowId;
    private String rowDescription;
    private int rowEspecialidadId;
    private String rowEspecialidad;
    private int rowFacultadId;
    private String rowFacultad;
    private String rowSemEjecucionValue;
    private int rowSemEjecucionId;
    private String rowSemDestinoValue;
    private int rowSemDestinoId;
    private String mpDescription;
    private String oncomplete;
    private String rowPublicado;
    private List<MbEstructuraPago> tablaSisEva;
    private List<MbEstructuraPago> subTablaDetalle;
    private int detId = 0;
    private String detNombre;
    private int detConPagId;
    private String detConPagValue;
    private Date detFecha;
    private float detMonto;
    private float detMontoRef;
    private String detCodigoRef;
    private String detActivo;
    private List<MbEstructuraPago> tablaSisEvaDetalle;
    private boolean ver = false;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        // Para cargar facultades
        HSFacultadDAO dao = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        this.setComboFacultad(dao.seleccionarFacultad("", "").stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre())).collect(Collectors.toList()));

        comboEspecialidadBuscar = new ArrayList<>();
        mpComboEspecialidad = new ArrayList<>();

        // Para cargar semestres
        List<AcSemestre> semestres = daoSemestre.seleccionarSemestreActivo();
        List<SelectItem> siSemestres = semestres.stream()
                .map(s -> new SelectItem(s.getId(), s.getSemNombre())).collect(Collectors.toList());
        this.setComboSemestre(siSemestres);

        // Para conceptos de pago
        List<AdConceptoPago> conceptosDePago = daoConceptoPago.seleccionarConceptoPago();
        List<SelectItem> siPaymentConcepts = conceptosDePago.stream()
                .map(cp -> new SelectItem(cp.getId(),
                        cp.getConpagDescripcion() + " ... S/. " + cp.getConpagMonto())).collect(Collectors.toList());
        this.setComboConceptoPago(siPaymentConcepts);

        tablaSisEvaDetalle = new ArrayList<>();
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getRowDescription() {
        return rowDescription;
    }

    public void setRowDescription(String rowDescription) {
        this.rowDescription = rowDescription;
    }

    public List<MbEstructuraPago> getTablaSisEva() {
        return tablaSisEva;
    }

    public void setTablaSisEva(List<MbEstructuraPago> tablaSisEva) {
        this.tablaSisEva = tablaSisEva;
    }

    public int getDetId() {
        return detId;
    }

    public void setDetId(int detId) {
        this.detId = detId;
    }

    public String getDetNombre() {
        return detNombre;
    }

    public void setDetNombre(String detNombre) {
        this.detNombre = detNombre;
    }

    public List<MbEstructuraPago> getTablaSisEvaDetalle() {
        return tablaSisEvaDetalle;
    }

    public void setTablaSisEvaDetalle(List<MbEstructuraPago> tablaSisEvaDetalle) {
        this.tablaSisEvaDetalle = tablaSisEvaDetalle;
    }

    public List<MbEstructuraPago> getSubTablaDetalle() {
        return subTablaDetalle;
    }

    public void setSubTablaDetalle(List<MbEstructuraPago> subTablaDetalle) {
        this.subTablaDetalle = subTablaDetalle;
    }

    public String getDetActivo() {
        return detActivo;
    }

    public void setDetActivo(String detActivo) {
        this.detActivo = detActivo;
    }

    public boolean isVer() {
        return ver;
    }

    public void setVer(boolean ver) {
        this.ver = ver;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getRowImgPublish() {
        return rowImgPublish;
    }

    public void setRowImgPublish(String rowImgPublish) {
        this.rowImgPublish = rowImgPublish;
    }

    public boolean isDisabledPublish() {
        return disabledPublish;
    }

    public void setDisabledPublish(boolean disabledPublish) {
        this.disabledPublish = disabledPublish;
    }

    public String getRowImgEdit() {
        return rowImgEdit;
    }

    public void setRowImgEdit(String rowImgEdit) {
        this.rowImgEdit = rowImgEdit;
    }

    public String getRowImgDelete() {
        return rowImgDelete;
    }

    public void setRowImgDelete(String rowImgDelete) {
        this.rowImgDelete = rowImgDelete;
    }

    public boolean isDisabledDelete() {
        return disabledDelete;
    }

    public void setDisabledDelete(boolean disabledDelete) {
        this.disabledDelete = disabledDelete;
    }

    public String getRowPublicado() {
        return rowPublicado;
    }

    public void setRowPublicado(String rowPublicado) {
        this.rowPublicado = rowPublicado;
    }

    public int getRowEspecialidadId() {
        return rowEspecialidadId;
    }

    public void setRowEspecialidadId(int rowEspecialidadId) {
        this.rowEspecialidadId = rowEspecialidadId;
    }

    public int getDetConPagId() {
        return detConPagId;
    }

    public void setDetConPagId(int detConPagId) {
        this.detConPagId = detConPagId;
    }

    public Date getDetFecha() {
        return detFecha;
    }

    public void setDetFecha(Date detFecha) {
        this.detFecha = detFecha;
    }

    public float getDetMonto() {
        return detMonto;
    }

    public void setDetMonto(float detMonto) {
        this.detMonto = detMonto;
    }

    public String getRowEspecialidad() {
        return rowEspecialidad;
    }

    public void setRowEspecialidad(String rowEspecialidad) {
        this.rowEspecialidad = rowEspecialidad;
    }

    public int getRowFacultadId() {
        return rowFacultadId;
    }

    public void setRowFacultadId(int rowFacultadId) {
        this.rowFacultadId = rowFacultadId;
    }

    public String getRowFacultad() {
        return rowFacultad;
    }

    public void setRowFacultad(String rowFacultad) {
        this.rowFacultad = rowFacultad;
    }

    public String getDetConPagValue() {
        return detConPagValue;
    }

    public void setDetConPagValue(String detConPagValue) {
        this.detConPagValue = detConPagValue;
    }

    public float getDetMontoRef() {
        return detMontoRef;
    }

    public void setDetMontoRef(float detMontoRef) {
        this.detMontoRef = detMontoRef;
    }

    public String getDetCodigoRef() {
        return detCodigoRef;
    }

    public void setDetCodigoRef(String detCodigoRef) {
        this.detCodigoRef = detCodigoRef;
    }

    public String getMpDescription() {
        return mpDescription;
    }

    public void setMpDescription(String mpDescription) {
        this.mpDescription = mpDescription;
    }

    public String getRowSemEjecucionValue() {
        return rowSemEjecucionValue;
    }

    public void setRowSemEjecucionValue(String rowSemEjecucionValue) {
        this.rowSemEjecucionValue = rowSemEjecucionValue;
    }

    public int getRowSemEjecucionId() {
        return rowSemEjecucionId;
    }

    public void setRowSemEjecucionId(int rowSemEjecucionId) {
        this.rowSemEjecucionId = rowSemEjecucionId;
    }

    ///////para la busqueda
    public int getParamFacuId() {
        return paramFacuId;
    }

    public void setParamFacuId(int paramFacuId) {
        this.paramFacuId = paramFacuId;
    }

    public int getParamEspeId() {
        return paramEspeId;
    }

    public void setParamEspeId(int paramEspeId) {
        this.paramEspeId = paramEspeId;
    }

    public int getParamSemeId() {
        return paramSemeId;
    }

    public void setParamSemeId(int paramSemeId) {
        this.paramSemeId = paramSemeId;
    }

    public List<SelectItem> getComboFacultad() {
        return comboFacultad;
    }

    public void setComboFacultad(List<SelectItem> comboFacultad) {
        this.comboFacultad = comboFacultad;
    }

    public void setearFacultadBuscar(ActionEvent event) {
        LOG.debug("[{}] Current facultad: {}", event.getPhaseId(), paramFacuId);
        if (paramFacuId == 0) {
            setearEmptyComboEspecialidad();
        } else {
            this.setComboEspecialidadBuscar(daoEspecialidad.seleccionarEspecialidad(paramFacuId).stream()
                    .map(esp -> new SelectItem(esp.getId(), esp.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public void setearEmptyComboEspecialidad() {
        comboEspecialidadBuscar.clear();
    }

    public List<SelectItem> getComboEspecialidadBuscar() {
        return comboEspecialidadBuscar;
    }

    public void setComboEspecialidadBuscar(List<SelectItem> comboEspecialidadBuscar) {
        this.comboEspecialidadBuscar = comboEspecialidadBuscar;
    }

    public List<SelectItem> getComboSemestre() {
        return comboSemestre;
    }

    public void setComboSemestre(List<SelectItem> comboSemestre) {
        this.comboSemestre = comboSemestre;
    }

    public List<SelectItem> getComboConceptoPago() {
        return comboConceptoPago;
    }

    public void setComboConceptoPago(List<SelectItem> comboConceptoPago) {
        this.comboConceptoPago = comboConceptoPago;
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}] Opening modal for new Estructura de Pago", event.getPhaseId());
        this.setRowId(0);
        this.setDetId(0);
        this.setMpDescription("");
        this.setRowFacultadId(0);
        this.setRowFacultadId(0);
        this.setRowSemEjecucionId(0);
        this.setRowSemDestinoId(0);
        this.setRowSemDestinoValue("0");
        this.setDetConPagId(0);
        this.setDetMonto(0);
        this.setDetFecha(null);
        this.setDetNombre("");
        this.getTablaSisEvaDetalle().clear();
        this.getMpComboEspecialidad().clear();
    }

    public void seleccionar() {
        LOG.debug("Especialidad: {} | Semestre: {}", this.getParamEspeId(), this.getParamSemeId());
        tablaSisEva = new ArrayList<>();
        List<AdEstructuraPagos> lista = daoEstructuraPagos.seleccionarEstructuraPagos(this.getParamEspeId(), this.getParamSemeId());
        MbEstructuraPago evaluacion;
        for (AdEstructuraPagos adEstructuraPagos : lista) {
            evaluacion = new MbEstructuraPago();
            evaluacion.setRowId(adEstructuraPagos.getId());
            evaluacion.setRowDescription(adEstructuraPagos.getEstpagoNombre());

            evaluacion.setRowEspecialidad(adEstructuraPagos.getEsp().getEspNombre());
            evaluacion.setRowEspecialidadId(adEstructuraPagos.getEsp().getId());
            evaluacion.setRowFacultadId(adEstructuraPagos.getEsp().getFac().getId());
            evaluacion.setRowFacultad(adEstructuraPagos.getEsp().getFac().getFacNombre());

            evaluacion.setRowSemEjecucionValue(adEstructuraPagos.getSem().getSemNombre());
            evaluacion.setRowSemEjecucionId(adEstructuraPagos.getSem().getId());
            evaluacion.setRowSemDestinoValue(adEstructuraPagos.getSemIn().getSemNombre());
            evaluacion.setRowSemDestinoId(adEstructuraPagos.getSemIn().getId());

            if (adEstructuraPagos.getEstpagoPublicado().equals("0")) {
                evaluacion.setRowPublicado("NO");
            } else {
                evaluacion.setRowPublicado("SI");
                evaluacion.setRowImgPublish("/Imagenes/actions/matricula_gris.png");
                evaluacion.setRowImgEdit(ACTION_IMAGES_EDIT_PASTE_GRIS);
                evaluacion.setRowImgDelete("/Imagenes/actions/no_gris.png");
                evaluacion.setDisabledPublish(true);
                evaluacion.setDisabledDelete(true);
            }
            Set<AdEstructuraPagosDetalle> sisevadet = adEstructuraPagos.getAdEstructuraPagosDetalles();
            List<AdEstructuraPagosDetalle> list = Collections.synchronizedList(new LinkedList<>(sisevadet));
            List<MbEstructuraPago> a = new ArrayList<>();
            for (AdEstructuraPagosDetalle adEstructuraPagosDetalle : list) {
                var siseva = new MbEstructuraPago();
                siseva.setDetId(adEstructuraPagosDetalle.getId());

                siseva.setDetConPagId(adEstructuraPagosDetalle.getConpagId());
                AdConceptoPago conPago = daoConceptoPago.seleccionarUnaConceptoPago(adEstructuraPagosDetalle.getConpagId());
                siseva.setDetConPagValue(conPago.getConpagDescripcion());
                siseva.setDetMontoRef(conPago.getConpagMonto());
                siseva.setDetCodigoRef(conPago.getConpagCodigo());

                siseva.setDetFecha(adEstructuraPagosDetalle.getEstpagdetFechaPago());
                siseva.setDetMonto(adEstructuraPagosDetalle.getEstpagdetMonto());
                siseva.setDetActivo(adEstructuraPagosDetalle.getEstpagdetActivo());
                siseva.setDetNombre(adEstructuraPagosDetalle.getEstpagdetNombre());
                a.add(siseva);
            }
            evaluacion.setSubTablaDetalle(a);
            tablaSisEva.add(evaluacion);
        }
        this.setTablaSisEva(tablaSisEva);
        LOG.info("Total {} de estructuras de pago", this.getTablaSisEva().size());
    }

    public void grabar(ActionEvent event) {
        LOG.debug("[{}] Save or update payment structure '{}'", event.getPhaseId(), rowId);
        if (this.getRowFacultadId() == 0 ||
                this.getRowSemEjecucionId() == 0 ||
                this.getRowSemDestinoId() == 0 ||
                this.getMpDescription().trim().isEmpty()) {
            this.setOncomplete("alert('Ingrese todos los datos requeridos.')");
        } else {
            var estpag = new AdEstructuraPagos();
            estpag.setId(this.getRowId());
            estpag.setEstpagoNombre(this.getMpDescription());
            var espe = new AcEspecialidad();
            espe.setId(this.getRowFacultadId());
            estpag.setEsp(espe);
            estpag.setEstpagoPublicado("0");
            estpag.setEstpagActivo("1");
            var sem = new AcSemestre(this.getRowSemEjecucionId());
            estpag.setSem(sem);
            var semI = new AcSemestre(this.getRowSemDestinoId());
            estpag.setSemIn(semI);
            Set<AdEstructuraPagosDetalle> listaDetalle = new LinkedHashSet<>();
            for (MbEstructuraPago bep : tablaSisEvaDetalle) {
                var estpagdet = new AdEstructuraPagosDetalle();
                if (bep.getDetId() > 0) {
                    estpagdet.setId(bep.getDetId());
                }
                estpagdet.setEstpagdetNombre(bep.getDetNombre());
                estpagdet.setConpagId(bep.getDetConPagId());
                estpagdet.setEstpagdetMonto(bep.getDetMonto());
                estpagdet.setEstpagdetFechaPago(bep.getDetFecha());
                estpagdet.setEstpagdetActivo(bep.getDetActivo());
                estpagdet.setEstpag(estpag);
                listaDetalle.add(estpagdet);
            }
            estpag.setAdEstructuraPagosDetalles(listaDetalle);
            if (rowId == 0) {
                daoEstructuraPagos.insertarEstructuraPagos(estpag);
                rowId = estpag.getId();
                LOG.info("Payment Structure saved {}", rowId);
                this.setOncomplete("alert('Se creo una Estructura de Pagos correctamente.');Richfaces.hideModalPanel('mp')");
            } else {
                daoEstructuraPagos.actualizarEstructuraPagos(estpag);
                LOG.info("Payment Structure updated {}", estpag.getId());
                this.setOncomplete("alert('Se Actualizaron los datos de la Estructura de Pagos correctamente.');Richfaces.hideModalPanel('mp')");
            }
        }
    }

    public void editar(ActionEvent event) {
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_s");
        rowId = Integer.parseInt(id.getValue().toString());
        LOG.debug("[{}] Opening modal for edit Estructura de Pago '{}'", event.getPhaseId(), rowId);
        this.setDetConPagId(0);
        this.setDetMonto(0);
        this.setDetFecha(null);
        this.setDetNombre("");

        /* Llenar variable para mostrar estrutura de pagos*/
        AdEstructuraPagos ep = daoEstructuraPagos.seleccionarUnaEstructuraPagos(rowId);
        this.setRowId(ep.getId());
        this.setRowFacultadId(ep.getEsp().getFac().getId());
        this.setearFacultadMp(event);
        this.setRowEspecialidadId(ep.getEsp().getId());
        this.setRowSemEjecucionId(ep.getSem().getId());
        this.setRowSemDestinoValue(ep.getSemIn().getId().toString());
        this.setRowSemDestinoId(ep.getSemIn().getId());
        this.setMpDescription(ep.getEstpagoNombre());

        /* Fin Llenar estrutura de pagos */
        Set<AdEstructuraPagosDetalle> sisevadet = ep.getAdEstructuraPagosDetalles();
        List<AdEstructuraPagosDetalle> list = Collections.synchronizedList(new LinkedList<>(sisevadet));
        List<MbEstructuraPago> a = new ArrayList<>();
        for (AdEstructuraPagosDetalle adEstructuraPagosDetalle : list) {
            var siseva = new MbEstructuraPago();
            siseva.setDetId(adEstructuraPagosDetalle.getId());

            siseva.setDetConPagId(adEstructuraPagosDetalle.getConpagId());
            AdConceptoPago conPago = daoConceptoPago.seleccionarUnaConceptoPago(adEstructuraPagosDetalle.getConpagId());
            siseva.setDetConPagValue(conPago.getConpagDescripcion());
            siseva.setDetMontoRef(conPago.getConpagMonto());
            siseva.setDetCodigoRef(conPago.getConpagCodigo());

            siseva.setDetFecha(adEstructuraPagosDetalle.getEstpagdetFechaPago());
            siseva.setDetMonto(adEstructuraPagosDetalle.getEstpagdetMonto());
            siseva.setDetActivo(adEstructuraPagosDetalle.getEstpagdetActivo());
            siseva.setDetNombre(adEstructuraPagosDetalle.getEstpagdetNombre());
            a.add(siseva);
        }
        setTablaSisEvaDetalle(a);
    }

    public void eliminar(ActionEvent event) {
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_s");
        var peId = Integer.parseInt(id.getValue().toString());
        LOG.debug("[{}] Deleting payment structure '{}'", event.getPhaseId(), peId);
        daoEstructuraPagos.eliminarEstructuraPagos(peId);
    }

    public void agregar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getDetConPagId() <= 0 ||
                this.getDetMonto() <= 0 ||
                this.getDetFecha() == null ||
                this.getDetNombre().trim().isEmpty()) {
            this.setOncomplete("alert('Ud tiene que Ingrese todos los datos, para Agregar un Concepto.')");
        } else {
            var estpag = new MbEstructuraPago();
            estpag.setDetId(detId);
            estpag.setDetNombre(this.getDetNombre());
            estpag.setDetConPagId(this.getDetConPagId());

            AdConceptoPago conceptoPago = daoConceptoPago.seleccionarUnaConceptoPago(this.getDetConPagId());
            estpag.setDetCodigoRef(conceptoPago.getConpagCodigo());
            estpag.setDetConPagValue(conceptoPago.getConpagDescripcion());
            estpag.setDetMontoRef(conceptoPago.getConpagMonto());

            estpag.setDetFecha(this.getDetFecha());

            estpag.setDetMonto(this.getDetMonto());
            estpag.setDetActivo("1");
            tablaSisEvaDetalle.add(estpag);
            detId--;
            this.setOncomplete("alert('Se Agrego un Concepto de Pago mas.')");
        }
    }

    public void quitar(ActionEvent event) {
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_det");
        var idDet = Integer.parseInt(id.getValue().toString());
        LOG.debug("[{}] Removing payment structure detail '{}'", event.getPhaseId(), idDet);
        var evaluacion = new MbEstructuraPago();
        evaluacion.setDetId(Integer.parseInt(id.getValue().toString()));
        for (var i = 0; i < tablaSisEvaDetalle.size(); i++) {
            int idEs = tablaSisEvaDetalle.get(i).getDetId();
            if (idDet == idEs) {
                LOG.trace("Payment structure detail index found {}", i);
                tablaSisEvaDetalle.get(i).setDetActivo("0");
                tablaSisEvaDetalle.remove(i);
                break;
            }
        }
    }

    public void subTable(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter uiFlag = (UIParameter) event.getComponent().findComponent("flag");
        if (uiFlag.getValue().toString().equals("0")) {
            this.setVer(true);
            this.setFlag("1");
            this.setImagen(ACTION_IMAGES_UP);
        } else {
            this.setVer(false);
            this.setFlag("0");
            this.setImagen(ACTION_IMAGES_DOWN);
        }
    }

    ////para una nueva estructura de pagos
    public void setearFacultadMp(ActionEvent event) {
        LOG.debug("[{}] Current facultad: {}", event.getPhaseId(), rowFacultadId);
        if (rowFacultadId == 0) {
            setearEmptyModalComboEspecialidad();
        } else {
            List<SelectItem> comboEspecialidadI = daoEspecialidad.seleccionarEspecialidad(rowFacultadId).stream()
                    .map(espI -> new SelectItem(espI.getId(), espI.getEspNombre()))
                    .collect(Collectors.toList());
            this.setMpComboEspecialidad(comboEspecialidadI);
        }
    }

    public void setearEmptyModalComboEspecialidad() {
        mpComboEspecialidad.clear();
    }

    public List<SelectItem> getMpComboEspecialidad() {
        return mpComboEspecialidad;
    }

    public void setMpComboEspecialidad(List<SelectItem> mpComboEspecialidad) {
        this.mpComboEspecialidad = mpComboEspecialidad;
    }

    ////publicar
    public void publicar(ActionEvent event) {
        var publicado = 0;
        UIParameter uipId = (UIParameter) event.getComponent().findComponent("id_s");
        var peId = Integer.parseInt(uipId.getValue().toString());
        UIParameter uipEspId = (UIParameter) event.getComponent().findComponent("esp_id_s");
        var espId = Integer.parseInt(uipEspId.getValue().toString());
        UIParameter uipSemDestinationId = (UIParameter) event.getComponent().findComponent("sem_id_d");
        var semDestId = Integer.parseInt(uipSemDestinationId.getValue().toString());

        LOG.debug("[{}] Publishing payment structure '{}' | especiality: '{}' | semester: '{}'", event.getPhaseId(), peId, espId, semDestId);

        HSAlumnoDAO dao = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
        List<AcAlumno> lista = dao.seleccionarAlumnoEstPago(espId, CAT_TIPO_ALUMNO_ESTUDIANTE, semDestId);
        LOG.info("Total of students to generate payment structure {}", lista.size());

        Set<AdEstructuraPagosDetalle> estdet = daoEstructuraPagos.seleccionarUnaEstructuraPagos(peId).getAdEstructuraPagosDetalles();
        AdAlumnoTarifa alTa;
        if (!lista.isEmpty()) {
            List<AdAlumnoTarifa> listaPubli = new ArrayList<>();
            for (AcAlumno alumno : lista) {
                for (AdEstructuraPagosDetalle estructuraPagosDetalle : estdet) {
                    alTa = new AdAlumnoTarifa();
                    var estpagDet = new AdEstructuraPagosDetalle();
                    estpagDet.setId(estructuraPagosDetalle.getId());
                    alTa.setEstpagdet(estpagDet);

                    var alu = new AcAlumno();
                    alu.setId(alumno.getId());
                    alTa.setAlu(alu);

                    alTa.setAlutarEstado("0");
                    alTa.setAlutarMonto(estructuraPagosDetalle.getEstpagdetMonto());
                    if (alumno.getAluProcedencia() != null
                            && (alumno.getAluProcedencia().equals("023001")
                            || alumno.getAluProcedencia().equals("023002")
                            || alumno.getAluProcedencia().equals("023003")
                            || alumno.getAluProcedencia().equals("023004")
                            || alumno.getAluProcedencia().equals("023010")) && alumno.getAluMontoPago() > 0) {
                        alTa.setAlutarMonto(alumno.getAluMontoPago());
                        if (estructuraPagosDetalle.getConpagId() == 1) {
                            alTa.setAlutarMonto(Float.parseFloat("0"));
                            alTa.setAlutarEstado("2");
                        }
                    }

                    var con = new AdConceptoPago();
                    con.setId(estructuraPagosDetalle.getConpagId());
                    alTa.setConpag(con);

                    alTa.setAlutarFechaPago(estructuraPagosDetalle.getEstpagdetFechaPago());
                    alTa.setAlutarFechaProrroga(estructuraPagosDetalle.getEstpagdetFechaPago());
                    alTa.setAlutarActivo("1");

                    listaPubli.add(alTa);
                }
            }
            HSAlumnoTarifaDAO daoTarifa = ServiceFinder.findBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class);
            daoTarifa.insertarTotal(listaPubli);
            publicado = 1;
        }
        if (publicado == 1) {
            LOG.debug("Processing payment estructure to publish");
            UIParameter uipDescription = (UIParameter) event.getComponent().findComponent("des_s");
            UIParameter uipSemExecutionId = (UIParameter) event.getComponent().findComponent("sem_id_s");

            var publi = new AdEstructuraPagos();
            publi.setId(peId);
            publi.setEstpagoNombre(uipDescription.getValue().toString());

            var espe = new AcEspecialidad();
            espe.setId(espId);
            publi.setEsp(espe);

            publi.setEstpagoPublicado("1");
            publi.setEstpagActivo("1");

            var sem = new AcSemestre(Integer.parseInt(uipSemExecutionId.getValue().toString()));
            publi.setSem(sem);
            var sem2 = new AcSemestre(semDestId);
            publi.setSemIn(sem2);

            daoEstructuraPagos.actualizarEstructuraPagos(publi);
            LOG.info("Payment strucure published {}", publi.getId());
        }
    }

    public String getRowSemDestinoValue() {
        return rowSemDestinoValue;
    }

    public void setRowSemDestinoValue(String rowSemDestinoValue) {
        this.rowSemDestinoValue = rowSemDestinoValue;
    }

    public int getRowSemDestinoId() {
        return rowSemDestinoId;
    }

    public void setRowSemDestinoId(int rowSemDestinoId) {
        this.rowSemDestinoId = rowSemDestinoId;
    }
}
