package net.uch.cursolibre.jsf;

import net.uch.administrativa.dao.HSConceptoPagoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.HSAreaDAO;
import net.uch.cursolibre.dao.HSEstructuraPagoDAO;
import net.uch.cursolibre.dao.HSModuloDAO;
import net.uch.cursolibre.dao.HSTallerDAO;
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

import static net.uch.common.Constants.ACTION_IMAGES_DOWN;
import static net.uch.common.Constants.ACTION_IMAGES_UP;

public class MbEstructuraPagoCL {

    private static final Logger LOG = LoggerFactory.getLogger(MbEstructuraPagoCL.class);

    private final HSEstructuraPagoDAO daoCLEstructuraPago = ServiceFinder.findBean("springHibernateDaoCLEstructuraPago", HSEstructuraPagoDAO.class);
    private final HSTallerDAO daoCLTaller = ServiceFinder.findBean("springHibernateDaoCLTaller", HSTallerDAO.class);

    int cont1 = 0;
    //CAMPOS
    private int rowEstpagId;
    private String rowEstpagNombre;
    private String rowEstpagAbrev;
    private String rowEstpagActivo;
    private int modId;
    private String modulo;
    private List<SelectItem> areas;
    private int paramAreaId = 0;
    private List<SelectItem> modulos;
    private int paramModId;
    private String paramNombre = "";
    private int rsbEstpagdetId;
    private String rsbEstpagdetNombre;
    private int rsbConpagId;
    private String concepto;
    private float rsbEstpagdetMonto;
    private int rsbEstpagId;
    private int rsbTalId;
    private String taller;
    private List<MbEstructuraPagoCL> estructuras = new ArrayList<>();
    private List<MbEstructuraPagoCL> detalle = new ArrayList<>();
    private boolean verDetalle;
    private String verDetImagen;
    private String verDetTitulo;
    private String oncomplete;
    /////////////////////////
    //modal//
    private int auConpagId;
    private String auConcepto;
    private int iEstpagId;
    private String mpEstpagNombre;
    private String mpEstpagAbrev;
    private int mpAreaId = 0;
    private List<SelectItem> mpAreas;
    private int mpModId = 0;
    private List<SelectItem> mpModulos;
    //detalle//
    private int detEstpagdetId;
    private String mpEstpagdetDescription;
    private String sugConcepto;
    private int sugConpagId;
    private String sugConpagDes;
    private float mpEstpagdetMonto;
    private List<SelectItem> mpTalleres;
    private int mpTalId = 0;
    private String detDetTaller;
    private boolean detalleVer;
    private boolean detalleEditar;
    private String ediEstpagdetDes;
    private float ediEstpagdetMonto;
    private List<MbEstructuraPagoCL> detDetalle = new ArrayList<>();
    private List<Integer> idsToDelete = new ArrayList<>();

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSAreaDAO dao = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);
        List<SelectItem> tmpAreas = dao.seleccionarArea("").stream()
                .map(a -> new SelectItem(a.getAreId(), a.getAreDescripcion()))
                .collect(Collectors.toList());

        this.setAreas(tmpAreas);
        this.setMpAreas(tmpAreas);

        modulos = new ArrayList<>();
        mpModulos = new ArrayList<>();
        mpTalleres = new ArrayList<>();
    }

    //METODOS
    public List<MbEstructuraPagoCL> autocomplete(Object suggest) {
        HSConceptoPagoDAO daoConcepto = ServiceFinder.findBean("springHibernateDaoConceptoPago", HSConceptoPagoDAO.class);
        String pref = (String) suggest;
        List<MbEstructuraPagoCL> conceptos = new ArrayList<>();
        LOG.debug("Looking for payment structure '{}'", pref);
        if (!ObjectUtils.isEmpty(pref)) {
            List<AdConceptoPago> lista = daoConcepto.seleccionarConceptoCursoLibre(pref);
            for (AdConceptoPago cp : lista) {
                var c = new MbEstructuraPagoCL();
                c.setAuConpagId(cp.getId());
                c.setAuConcepto(cp.getConpagDescripcion());
                conceptos.add(c);
            }
            LOG.info("Total autocomplete {}", conceptos.size());
        }
        return conceptos;
    }

    public String seleccionar() {
        LOG.debug("Looking for '{}' | '{}' | '{}'", this.paramNombre, this.paramAreaId, this.paramModId);
        this.setEstructuras(new ArrayList<>());

        List<ClEstructuraPagos> lista = daoCLEstructuraPago.seleccionarEstructuras(this.getParamNombre(), this.getParamAreaId(), this.getParamModId());
        for (ClEstructuraPagos est : lista) {
            var e = new MbEstructuraPagoCL();
            e.setRowEstpagId(est.getEstpagId());
            e.setRowEstpagNombre(est.getEstpagNombre());
            e.setRowEstpagAbrev(est.getEstpagAbrev());
            e.setRowEstpagActivo(est.getEstpagActivo());
            e.setModId(est.getClModulo().getModId());
            e.setModulo(est.getClModulo().getModDescripcion());

            e.setDetalle(new ArrayList<>());
            Set<ClEstructuraPagosDetalle> lDetalle = est.getClEstructuraPagosDetalles();
            for (ClEstructuraPagosDetalle det : lDetalle) {
                var d = new MbEstructuraPagoCL();

                d.setRsbEstpagdetId(det.getEstpagdetId());
                d.setRsbEstpagdetNombre(det.getEstpagdetNombre());
                d.setRsbConpagId(det.getAdConceptoPago().getId());
                d.setConcepto(det.getAdConceptoPago().getConpagDescripcion());
                d.setRsbEstpagdetMonto(det.getEstpagdetMonto());
                d.setRsbEstpagId(est.getEstpagId());
                d.setRsbTalId(det.getTalId());
                d.setTaller(daoCLTaller.buscarTaller(det.getTalId()).getTalDescripcion());
                e.getDetalle().add(d);
            }

            e.setVerDetalle(false);
            e.setVerDetImagen(ACTION_IMAGES_DOWN);
            e.setVerDetTitulo("Ver Detalle");

            this.getEstructuras().add(e);
        }
        LOG.info("Total of Estructuras de Pago {}", estructuras.size());
        return "";
    }

    public void mostrarDetalle(ActionEvent event) {
        var pIdVer = Boolean.parseBoolean(((UIParameter) event.getComponent().findComponent("id_ver")).getValue().toString());
        LOG.debug("[{}] Opening was '{}'", event.getPhaseId(), pIdVer);
        if (pIdVer) {
            this.setVerDetalle(false);
            this.setVerDetImagen(ACTION_IMAGES_DOWN);
            this.setVerDetTitulo("Mostrar Detalle");
        } else {
            this.setVerDetalle(true);
            this.setVerDetImagen(ACTION_IMAGES_UP);
            this.setVerDetTitulo("Ocultar Detalle");
        }
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        this.setiEstpagId(0);
        this.setMpEstpagNombre("");
        this.setMpEstpagAbrev("");
        this.setMpModulos(getMpModulos());
        this.setMpAreaId(0);
        this.setMpModId(0);

        cleanUpPaymentStructureDetails();
    }

    public void agregarDetalle(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        HSConceptoPagoDAO daoConcepto = ServiceFinder.findBean("springHibernateDaoConceptoPago", HSConceptoPagoDAO.class);

        if (this.getSugConpagId() > 0
                && this.getMpTalId() > 0
                && this.getMpEstpagdetDescription().trim().length() > 0
                && this.getMpEstpagdetMonto() > 0) {
            var det = new MbEstructuraPagoCL();
            det.setDetEstpagdetId(cont1);
            det.setSugConpagId(this.getSugConpagId());
            det.setSugConpagDes(daoConcepto.seleccionarUnaConceptoPago(this.getSugConpagId()).getConpagDescripcion());
            det.setMpTalId(this.getMpTalId());
            det.setDetDetTaller(daoCLTaller.buscarTaller(this.getMpTalId()).getTalDescripcion());
            det.setMpEstpagdetDescription(this.getMpEstpagdetDescription().trim().toUpperCase());
            det.setMpEstpagdetMonto(this.getMpEstpagdetMonto());

            det.setEdiEstpagdetDes("");
            det.setEdiEstpagdetMonto(0);
            det.setDetalleEditar(false);
            det.setDetalleVer(true);

            this.getDetDetalle().add(det);
            cont1--;

            // Clean up
            this.setSugConcepto("");
            this.setSugConpagId(0);
            this.setSugConpagDes("");
            this.setMpTalId(0);
            this.setMpEstpagdetDescription("");
            this.setMpEstpagdetMonto(0);
        } else {
            this.setOncomplete("alert('Todos los campos son Obligatorios.')");
        }
    }

    public void editarDetalle(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setEdiEstpagdetDes(this.getMpEstpagdetDescription());
        this.setEdiEstpagdetMonto(this.getMpEstpagdetMonto());

        this.setDetalleEditar(true);
        this.setDetalleVer(false);
    }

    public void guardarDetalle(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (this.getEdiEstpagdetDes().trim().isEmpty()
                || this.getEdiEstpagdetMonto() <= 0) {
            this.setOncomplete("alert('Ingrese ambos campos para Guardar.')");
        } else {
            this.setMpEstpagdetDescription(this.getEdiEstpagdetDes().trim().toUpperCase());
            this.setMpEstpagdetMonto(this.getEdiEstpagdetMonto());

            this.setDetalleEditar(false);
            this.setDetalleVer(true);
        }
    }

    public void cancelarDetalle(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setDetalleEditar(false);
        this.setDetalleVer(true);
    }

    public void eliminarDetalle(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        var idDetDelete = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_det_delete")).getValue().toString());

        if (daoCLEstructuraPago.verificarEliminarDetalle(idDetDelete)) {
            this.setOncomplete("alert('Este detalle ya fue Utilizado.')");
        } else {
            for (var i = 0; i < this.getDetDetalle().size(); i++) {
                MbEstructuraPagoCL epd = this.getDetDetalle().get(i);
                if (epd.getDetEstpagdetId() == idDetDelete) {
                    if (epd.getDetEstpagdetId() > 0) {
                        this.getIdsToDelete().add(epd.getDetEstpagdetId());
                    }
                    this.getDetDetalle().remove(i);

                    break;
                }
            }
        }
    }

    public void actualizarEstructura(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");

        var peId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_update")).getValue().toString());

        ClEstructuraPagos est = daoCLEstructuraPago.buscarEstructuraPagos(peId);
        this.setiEstpagId(est.getEstpagId());
        this.setMpEstpagNombre(est.getEstpagNombre());
        this.setMpEstpagAbrev(est.getEstpagAbrev());
        this.setMpAreaId(est.getClModulo().getClArea().getAreId());
        this.setMpModulos(getMpModulos());
        this.setMpModId(est.getClModulo().getModId());

        cleanUpPaymentStructureDetails();

        Set<ClEstructuraPagosDetalle> lista = est.getClEstructuraPagosDetalles();
        for (ClEstructuraPagosDetalle det : lista) {
            var e = new MbEstructuraPagoCL();
            e.setDetEstpagdetId(det.getEstpagdetId());
            e.setSugConpagId(det.getAdConceptoPago().getId());
            e.setSugConpagDes(det.getAdConceptoPago().getConpagDescripcion());
            e.setMpTalId(det.getTalId());
            e.setDetDetTaller(daoCLTaller.buscarTaller(det.getTalId()).getTalDescripcion());
            e.setMpEstpagdetDescription(det.getEstpagdetNombre());
            e.setMpEstpagdetMonto(det.getEstpagdetMonto());

            e.setEdiEstpagdetDes("");
            e.setEdiEstpagdetMonto(0);
            e.setDetalleEditar(false);
            e.setDetalleVer(true);

            this.getDetDetalle().add(e);
        }
        this.loadModalModules(event);
        this.loadWorkshops(event);
        this.setOncomplete("Richfaces.showModalPanel('mpUpdate')");
    }

    private void cleanUpPaymentStructureDetails() {
        this.setDetDetalle(new ArrayList<>());
        this.setSugConcepto("");
        this.setSugConpagId(0);
        this.setSugConpagDes("");
        this.setMpTalId(0);
        this.setMpEstpagdetDescription("");
        this.setMpEstpagdetMonto(0);
        cont1 = 0;
        this.setIdsToDelete(new ArrayList<>());
    }

    public void guardarEstructura(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (this.getMpModId() == 0
                || this.getMpEstpagNombre().trim().isEmpty()
                || this.getMpEstpagAbrev().trim().isEmpty()) {
            this.setOncomplete("alert('Los datos de Estructura de Pagos son obligatorios.')");
        } else {
            var mod = new ClModulo();
            mod.setModId(this.getMpModId());

            var estructura = new ClEstructuraPagos();
            estructura.setEstpagNombre(this.getMpEstpagNombre().trim().toUpperCase());
            estructura.setEstpagAbrev(this.getMpEstpagAbrev().trim().toUpperCase());

            if (this.getiEstpagId() > 0) {
                estructura.setEstpagId(this.getiEstpagId());

                estructura.setEstpagActivo("1");
                estructura.setClModulo(mod);

                if (!this.getDetDetalle().isEmpty()) {
                    for (var i = 0; i < this.getDetDetalle().size(); i++) {
                        var det = new ClEstructuraPagosDetalle();
                        det.setEstpagdetNombre(this.getDetDetalle().get(i).getMpEstpagdetDescription().trim().toUpperCase());
                        det.setEstpagdetMonto(this.getDetDetalle().get(i).getMpEstpagdetMonto());
                        det.setTalId(this.getDetDetalle().get(i).getMpTalId());
                        det.setEstpagdetActivo("1");

                        var conpag = new AdConceptoPago();
                        conpag.setId(this.getDetDetalle().get(i).getSugConpagId());
                        det.setAdConceptoPago(conpag);

                        if (this.getDetDetalle().get(i).getDetEstpagdetId() > 0) {
                            det.setEstpagdetId(this.getDetDetalle().get(i).getDetEstpagdetId());
                            det.setEstpagdetFechaPago(new Date());
                            det.setClEstructuraPagos(estructura);

                            daoCLEstructuraPago.actualizarEstructuraPagoDetalle(det);
                        } else {
                            det.setClEstructuraPagos(estructura);

                            daoCLEstructuraPago.insertarEstructuraPagoDetalle(det);
                        }
                    }
                }
                if (!this.getIdsToDelete().isEmpty()) {
                    daoCLEstructuraPago.eliminarEstructuraPagosDetalle(this.getIdsToDelete());
                }

                daoCLEstructuraPago.actualizarEstructuraPagos(estructura);
                this.setiEstpagId(estructura.getEstpagId());
                LOG.info("CL Payment Structure updated {}", this.getiEstpagId());
                this.setOncomplete("alert('Actualización Satisfactoria.');"
                        + "Richfaces.hideModalPanel('mpUpdate');");
            } else {
                estructura.setEstpagActivo("1");
                estructura.setClModulo(mod);

                Set<ClEstructuraPagosDetalle> sDetalle = new LinkedHashSet<>();
                if (!this.getDetDetalle().isEmpty()) {
                    for (var i = 0; i < this.getDetDetalle().size(); i++) {
                        var det = new ClEstructuraPagosDetalle();
                        det.setEstpagdetNombre(this.getDetDetalle().get(i).getMpEstpagdetDescription().trim().toUpperCase());

                        var conpag = new AdConceptoPago();
                        conpag.setId(this.getDetDetalle().get(i).getSugConpagId());
                        det.setAdConceptoPago(conpag);

                        det.setEstpagdetMonto(this.getDetDetalle().get(i).getMpEstpagdetMonto());
                        det.setClEstructuraPagos(estructura);
                        det.setEstpagdetActivo("1");
                        det.setTalId(this.getDetDetalle().get(i).getMpTalId());
                        sDetalle.add(det);
                    }
                }
                estructura.setClEstructuraPagosDetalles(sDetalle);

                daoCLEstructuraPago.insertarEstructuraPagos(estructura);
                this.setiEstpagId(estructura.getEstpagId());
                LOG.info("CL Payment Structure saved {}", this.getiEstpagId());
                this.setOncomplete("alert('Registro Satisfactorio.');Richfaces.hideModalPanel('mpUpdate')");
            }
        }
    }

    public void eliminarEstructura(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        var idDelete = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_delete")).getValue().toString());
        if (daoCLEstructuraPago.verificarEliminar(idDelete)) {
            this.setOncomplete("alert('Esta estructura ya ha sido utilizada y no puede eliminarse.')");
        } else {
            daoCLEstructuraPago.eliminarEstructuraPagos(idDelete);
            LOG.info("CL Payment Structure deleted {}", idDelete);
        }
    }

    public List<SelectItem> getAreas() {
        return areas;
    }

    public void setAreas(List<SelectItem> areas) {
        this.areas = areas;
    }

    public int getParamAreaId() {
        return paramAreaId;
    }

    public void setParamAreaId(int paramAreaId) {
        this.paramAreaId = paramAreaId;
    }

    //GETTERS AND SETTERS
    public int getParamModId() {
        return paramModId;
    }

    public void setParamModId(int paramModId) {
        this.paramModId = paramModId;
    }

    public void verModulos(ActionEvent event) {
        LOG.debug("[{}] Loading modules of area '{}'", event.getPhaseId(), paramAreaId);
        if (paramAreaId == 0) {
            modulos.clear();
        } else {
            HSModuloDAO dao = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
            List<SelectItem> tmpModulos = dao.seleccionarModulos(this.paramAreaId, "").stream()
                    .map(m -> new SelectItem(m.getModId(), m.getModDescripcion()))
                    .collect(Collectors.toList());
            this.setModulos(tmpModulos);
        }
    }

    public List<SelectItem> getModulos() {
        return modulos;
    }

    public void setModulos(List<SelectItem> modulos) {
        this.modulos = modulos;
    }

    public List<MbEstructuraPagoCL> getEstructuras() {
        return estructuras;
    }

    public void setEstructuras(List<MbEstructuraPagoCL> estructuras) {
        this.estructuras = estructuras;
    }

    public String getRowEstpagAbrev() {
        return rowEstpagAbrev;
    }

    public void setRowEstpagAbrev(String rowEstpagAbrev) {
        this.rowEstpagAbrev = rowEstpagAbrev;
    }

    public String getRowEstpagActivo() {
        return rowEstpagActivo;
    }

    public void setRowEstpagActivo(String rowEstpagActivo) {
        this.rowEstpagActivo = rowEstpagActivo;
    }

    public int getRowEstpagId() {
        return rowEstpagId;
    }

    public void setRowEstpagId(int rowEstpagId) {
        this.rowEstpagId = rowEstpagId;
    }

    public String getRowEstpagNombre() {
        return rowEstpagNombre;
    }

    public void setRowEstpagNombre(String rowEstpagNombre) {
        this.rowEstpagNombre = rowEstpagNombre;
    }

    public int getModId() {
        return modId;
    }

    public void setModId(int modId) {
        this.modId = modId;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getParamNombre() {
        return paramNombre;
    }

    public void setParamNombre(String paramNombre) {
        this.paramNombre = paramNombre;
    }

    public int getRsbEstpagdetId() {
        return rsbEstpagdetId;
    }

    public void setRsbEstpagdetId(int rsbEstpagdetId) {
        this.rsbEstpagdetId = rsbEstpagdetId;
    }

    public String getRsbEstpagdetNombre() {
        return rsbEstpagdetNombre;
    }

    public void setRsbEstpagdetNombre(String rsbEstpagdetNombre) {
        this.rsbEstpagdetNombre = rsbEstpagdetNombre;
    }

    public int getRsbConpagId() {
        return rsbConpagId;
    }

    public void setRsbConpagId(int rsbConpagId) {
        this.rsbConpagId = rsbConpagId;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public float getRsbEstpagdetMonto() {
        return rsbEstpagdetMonto;
    }

    public void setRsbEstpagdetMonto(float rsbEstpagdetMonto) {
        this.rsbEstpagdetMonto = rsbEstpagdetMonto;
    }

    public int getRsbEstpagId() {
        return rsbEstpagId;
    }

    public void setRsbEstpagId(int rsbEstpagId) {
        this.rsbEstpagId = rsbEstpagId;
    }

    public int getRsbTalId() {
        return rsbTalId;
    }

    public void setRsbTalId(int rsbTalId) {
        this.rsbTalId = rsbTalId;
    }

    public List<MbEstructuraPagoCL> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<MbEstructuraPagoCL> detalle) {
        this.detalle = detalle;
    }

    public boolean isVerDetalle() {
        return verDetalle;
    }

    public void setVerDetalle(boolean verDetalle) {
        this.verDetalle = verDetalle;
    }

    public String getVerDetImagen() {
        return verDetImagen;
    }

    public void setVerDetImagen(String verDetImagen) {
        this.verDetImagen = verDetImagen;
    }

    public String getVerDetTitulo() {
        return verDetTitulo;
    }

    public void setVerDetTitulo(String verDetTitulo) {
        this.verDetTitulo = verDetTitulo;
    }

    public String getTaller() {
        return taller;
    }

    public void setTaller(String taller) {
        this.taller = taller;
    }

    public int getiEstpagId() {
        return iEstpagId;
    }

    public void setiEstpagId(int iEstpagId) {
        this.iEstpagId = iEstpagId;
    }

    public String getMpEstpagNombre() {
        return mpEstpagNombre;
    }

    public void setMpEstpagNombre(String mpEstpagNombre) {
        this.mpEstpagNombre = mpEstpagNombre;
    }

    public String getMpEstpagAbrev() {
        return mpEstpagAbrev;
    }

    public void setMpEstpagAbrev(String mpEstpagAbrev) {
        this.mpEstpagAbrev = mpEstpagAbrev;
    }

    public int getMpAreaId() {
        return mpAreaId;
    }

    public void setMpAreaId(int mpAreaId) {
        this.mpAreaId = mpAreaId;
    }

    public List<SelectItem> getMpAreas() {
        return mpAreas;
    }

    public void setMpAreas(List<SelectItem> mpAreas) {
        this.mpAreas = mpAreas;
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

    public void loadModalModules(ActionEvent event) {
        LOG.debug("[{}] Loading modal modules of area '{}'", event.getPhaseId(), mpAreaId);
        if (mpAreaId == 0) {
            mpModulos.clear();
        } else {
            HSModuloDAO dao = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
            List<SelectItem> tmpModulos = dao.seleccionarModulos(this.mpAreaId, "").stream()
                    .map(m -> new SelectItem(m.getModId(), m.getModDescripcion()))
                    .collect(Collectors.toList());
            this.setMpModulos(tmpModulos);
        }
        mpTalleres.clear();
        mpEstpagdetDescription = "";
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public List<MbEstructuraPagoCL> getDetDetalle() {
        return detDetalle;
    }

    public void setDetDetalle(List<MbEstructuraPagoCL> detDetalle) {
        this.detDetalle = detDetalle;
    }

    public int getDetEstpagdetId() {
        return detEstpagdetId;
    }

    public void setDetEstpagdetId(int detEstpagdetId) {
        this.detEstpagdetId = detEstpagdetId;
    }

    public String getSugConcepto() {
        return sugConcepto;
    }

    public void setSugConcepto(String sugConcepto) {
        this.sugConcepto = sugConcepto;
    }

    public int getSugConpagId() {
        return sugConpagId;
    }

    public void setSugConpagId(int sugConpagId) {
        this.sugConpagId = sugConpagId;
    }

    public float getMpEstpagdetMonto() {
        return mpEstpagdetMonto;
    }

    public void setMpEstpagdetMonto(float mpEstpagdetMonto) {
        this.mpEstpagdetMonto = mpEstpagdetMonto;
    }

    public List<SelectItem> getMpTalleres() {
        return mpTalleres;
    }

    public void setMpTalleres(List<SelectItem> mpTalleres) {
        this.mpTalleres = mpTalleres;
    }

    public void loadWorkshops(ActionEvent event) {
        LOG.debug("[{}] Loading workshops of module '{}'", event.getPhaseId(), mpModId);
        if (mpModId == 0) {
            mpTalleres.clear();
        } else {
            List<SelectItem> lista = daoCLTaller.seleccionarTalleresXMod(this.getMpModId()).stream()
                    .map(t -> new SelectItem(t.getTalId(), t.getTalDescripcion()))
                    .collect(Collectors.toList());
            this.setMpTalleres(lista);
        }
        mpEstpagdetDescription = "";
    }

    public int getMpTalId() {
        return mpTalId;
    }

    public void setMpTalId(int mpTalId) {
        this.mpTalId = mpTalId;
    }

    public int getAuConpagId() {
        return auConpagId;
    }

    public void setAuConpagId(int auConpagId) {
        this.auConpagId = auConpagId;
    }

    public String getAuConcepto() {
        return auConcepto;
    }

    public void setAuConcepto(String auConcepto) {
        this.auConcepto = auConcepto;
    }

    public String getSugConpagDes() {
        return sugConpagDes;
    }

    public void setSugConpagDes(String sugConpagDes) {
        this.sugConpagDes = sugConpagDes;
    }

    public String getMpEstpagdetDescription() {
        return mpEstpagdetDescription;
    }

    public void setMpEstpagdetDescription(String mpEstpagdetDescription) {
        this.mpEstpagdetDescription = mpEstpagdetDescription;
    }

    public void loadWorkshopDescription(ActionEvent event) {
        LOG.debug("[{}] Load description for workshop '{}'", event.getPhaseId(), this.getMpTalId());
        if (this.getMpTalId() == 0) {
            this.setMpEstpagdetDescription("");
        } else {
            SelectItem tsi = this.getMpTalleres().stream()
                    .filter(si -> this.getMpTalId() == Integer.parseInt(String.valueOf(si.getValue())))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
            this.setMpEstpagdetDescription(tsi.getLabel());
        }
    }

    public String getDetDetTaller() {
        return detDetTaller;
    }

    public void setDetDetTaller(String detDetTaller) {
        this.detDetTaller = detDetTaller;
    }

    public String getEdiEstpagdetDes() {
        return ediEstpagdetDes;
    }

    public void setEdiEstpagdetDes(String ediEstpagdetDes) {
        this.ediEstpagdetDes = ediEstpagdetDes;
    }

    public float getEdiEstpagdetMonto() {
        return ediEstpagdetMonto;
    }

    public void setEdiEstpagdetMonto(float ediEstpagdetMonto) {
        this.ediEstpagdetMonto = ediEstpagdetMonto;
    }

    public boolean isDetalleVer() {
        return detalleVer;
    }

    public void setDetalleVer(boolean detalleVer) {
        this.detalleVer = detalleVer;
    }

    public boolean isDetalleEditar() {
        return detalleEditar;
    }

    public void setDetalleEditar(boolean detalleEditar) {
        this.detalleEditar = detalleEditar;
    }

    public List<Integer> getIdsToDelete() {
        return idsToDelete;
    }

    public void setIdsToDelete(List<Integer> idsToDelete) {
        this.idsToDelete = idsToDelete;
    }
}
