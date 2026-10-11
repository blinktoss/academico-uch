package net.uch.administrativa.jsf;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.administrativa.dao.*;
import net.uch.administrativa.jsf.beans.BeanConceptoPago;
import net.uch.administrativa.jsf.beans.NroSerie;
import net.uch.administrativa.service.FreeCourseCriteria;
import net.uch.administrativa.service.InternalPaymentCriteria;
import net.uch.administrativa.service.PaymentSearchService;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.common.SystemHelper;
import net.uch.common.service.PageResult;
import net.uch.common.state.LateFeePolicy;
import net.uch.common.state.PaymentStatusLifecycle;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.cursolibre.dao.HSAlumnoCLDAO;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

public class MbPago {

    private static final Logger LOG = LoggerFactory.getLogger(MbPago.class);

    private final SystemHelper helper = ServiceFinder.findBean("helper", SystemHelper.class);
    private final LateFeePolicy lateFeePolicy = new LateFeePolicy();
    private final PaymentStatusLifecycle paymentStatusLifecycle = new PaymentStatusLifecycle();

    private final HSAlumnoTarifaDAO daoAlumnoTarifa = ServiceFinder.findBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class);
    private final HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
    private final HSConceptoPagoDAO daoConceptoPago = ServiceFinder.findBean("springHibernateDaoConceptoPago", HSConceptoPagoDAO.class);
    private final HSPagoDAO daoPago = ServiceFinder.findBean("springHibernateDaoPago", HSPagoDAO.class);
    private final HSSaldoDAO daoSaldo = ServiceFinder.findBean("springHibernateDaoSaldo", HSSaldoDAO.class);
    private final HSAlumnoDAO daoAlumno = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
    private final HSClienteDAO daoCliente = ServiceFinder.findBean("springHibernateDaoCliente", HSClienteDAO.class);
    private final HSAlumnoCLDAO daoAlumnoLibre = ServiceFinder.findBean("springHibernateDaoCLAlumno", HSAlumnoCLDAO.class);
    private final PaymentSearchService paymentSearchService = ServiceFinder.findBean("paymentSearchService", PaymentSearchService.class);

    private String nMpNumSerie;
    private int estructPagoDetId;
    private int paramListaPagoId = 1;
    private int currentPage = 1;
    private int pageSize = 20;
    private long totalCount = 0;
    /**
     * True only for the EXTERNOS listing, whose rows are vouchers and support the voucher actions
     * (edit/void/delete/print). INTERNOS and CURSOS LIBRES rows are tariffs — rendering those
     * actions there operates on empty voucher state (and CL collection is deferred to UC-CSH-005).
     */
    private boolean mostrarAccionesComprobante;
    private int alutarId;
    private int alumnoId;
    private int conceptoPagoId;
    private String rowSemestreOrNroComp;
    private String facultad;
    private String rowEspecialidadOrTipo;
    private String rowCodigoAlu;
    private String rowNombres;
    private String conceptoPagoDesc;
    private String rowConceptoPago;
    private float rowMonto;
    private Date alutarFecPago;
    private Date rowFecProOrFecComprPago;
    private String rowEstadoOrComprPagoMonto;
    private float rowAdeuda = 0;
    private float saldo = 0;
    private String alutarDniRuc;
    private int zcComprobanteId;
    private String zcNombre;
    private float zcMonto;
    private String zcNumeroComprobante;
    private Date zcFechaPago;
    private String zcConceptoPago;
    private String zcConceptoPagoDetalle;
    private String zcProcedenciaPagoId;
    ///////////////////////////
    private int clienteId;
    private String clienteNombre;
    private String clienteRazon;
    private String clienteDniRuc;
    private String clienteCodigo;
    private float clienteSaldo;
    private String selCli = "";
    ///////////////////////////
    private List<MbPago> detallesPagosInternos;
    private String detTipoPago;
    private Date detCompagFechaPago;
    private float detCompagMonto;
    private int detCompagId;
    private String detCompagNro;
    private String detNombrePagador;
    private String detCompagTipoCompValue;
    private String detCompagTipoCompId;
    private String detCompagTipoDoc;
    private String detVoucherNroOper;
    private Date detVoucherFecha;
    private String detVoucherCodAgencia;
    ///////////////////////////
    private int nValorNuevo = 0;
    private int nConPagId;
    private float nMpMonto;
    private double nMpSaldo;
    private String nMpIdTipoCliente;
    private int tmpClientId;
    private String nMpRazonSocial;
    private String nMpTipoPersNatJur;
    private String nMpDniRuc;
    private String nMpProcedenciaId;
    private String nMpNumComprobante;
    private Date nMpFechaComprobante;
    private float nMpMontoPago;
    private String nMpDescription;
    private String nMpCodAlu;
    private String paramAluCodigo;
    private int paramSemestreId;
    private int paramConceptoPagoId;
    private int paramFacId;
    private int paramEspId;
    private String paramEstadoId;
    private List<MbPago> listaAlumno;
    private List<MbPago> listaCliente;
    private String paramAluApPaterno;
    private String paramAluApMaterno;
    private String xunoy;
    private String xuno;
    private List<SelectItem> comboCatalogoReciboBloque;
    private String imagenP2 = ACTION_IMAGES_EDIT_PASTE;
    private boolean disabledP2;
    private SelectItem[] comboFacultadBuscar;
    private List<SelectItem> comboEspecialidadBuscar;
    private SelectItem[] comboSemestre;
    private SelectItem[] comboConceptoPago;
    private SelectItem[] comboCatalogoEstado;
    private SelectItem[] comboCatalogoListaPagos;
    private SelectItem[] comboCatalogoTipoCliente;
    private SelectItem[] comboCatalogoTipoPersNatJur;
    private SelectItem[] comboCatalogoProcedencia;
    private String imagen = ACTION_IMAGES_DOWN;
    private String flag = "0";
    private Date paramFechaDesde = new Date();
    private Date paramFechaHasta = new Date();
    private String imagenP = "/Imagenes/actions/tesoreria.png";
    private boolean disabledP;
    private List<TbCatalogo> procedencias;
    private List<TbCatalogo> estadosBoleta;
    private String suggestion;
    private boolean ver = false;
    private String oncomplete;
    private String oncomplete2;
    private String noncomplete;
    private boolean mostrarAccionNuevo;
    private boolean mostrarAccionPersonalizar;
    private boolean viewDetail;
    private String cabecera1;
    private String cabecera2;
    private String cabecera3;
    private String cabecera4;
    private float nMpMoraPago;
    private boolean nMpMoraSeleccion;
    private float nMpMatExtempo;
    private boolean nMpMatExtempoSeleccion;
    private int comprobanteOculto;
    private String nroComprobanteOculto;
    private Date comprobanteFechaOculto;
    private String comprobanteNombreOculto;
    private float comprobanteMontoOculto;
    private String comprobanteConceptoOculto;
    private String comprobanteConceptoDetOculto;
    private String comprobanteTipo;
    private boolean mostrarFicha;
    private CustomerSearchType customerSearchType;
    ///////////////////////////
    private List<SelectItem> comboTiposComprobantePago;
    private String zcTipoComprobanteId;
    private Date zcFecVoucher;
    private String zcNumVoucher;
    private String zcCodigoAgencia;
    ///////////////////////////
    private String nMpTipoComprPago;
    private Date nMpFecVou;
    private String nMpNumVou;
    private String nMpCodAge;
    ///////////////////////////
    private String alertaCaja = "";
    private Integer pUsuID;
    private String jsImpresion;
    /////////////////////
    private List<AdConceptoPago> conceptosPago;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        LOG.trace("Loading info for comboboxes | Level User: {}", usu.getNivel());
        if (usu.getNivel().equals("0")) {
            setMostrarAccionNuevo(true);
            setMostrarFicha(true);
            setMostrarAccionPersonalizar(true);
        }
        this.pUsuID = usu.getRowUsuId();

        this.setComboCatalogoListaPagos(daoCatalogo.seleccionarCatalogo("024").stream()
                .map(c -> new SelectItem(c.getCatValor(), c.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        this.setComboCatalogoEstado(daoCatalogo.seleccionarCatalogo("013").stream()
                .map(e -> new SelectItem(e.getCatValor(), e.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        HSFacultadDAO dao4 = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        this.setComboFacultadBuscar(dao4.seleccionarFacultad("", "").stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .toArray(SelectItem[]::new));
        this.setComboEspecialidadBuscar(new ArrayList<>());

        conceptosPago = daoConceptoPago.seleccionarConceptoPago();
        buildConceptCombo();

        HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        this.setComboSemestre(daoSemestre.seleccionarSemestreActivo().stream()
                .map(sem -> new SelectItem(sem.getId(), sem.getSemNombre()))
                .toArray(SelectItem[]::new));

        procedencias = daoCatalogo.seleccionarCatalogo("015");
        estadosBoleta = daoCatalogo.seleccionarCatalogo("037");

        this.setComboCatalogoTipoCliente(daoCatalogo.seleccionarCatalogo("014").stream()
                .map(tc -> new SelectItem(tc.getCatCodigoGrupo() + tc.getCatCodigoElemento(), tc.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        this.setComboCatalogoTipoPersNatJur(daoCatalogo.seleccionarCatalogo("017").stream()
                .map(t -> new SelectItem(t.getCatCodigoGrupo() + t.getCatCodigoElemento(), t.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        this.setComboCatalogoProcedencia(procedencias.stream()
                .map(c -> new SelectItem(c.getCatCodigoGrupo() + c.getCatCodigoElemento(), c.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        List<TbCatalogo> lista15 = daoCatalogo.seleccionarCatalogo("032"); // RECIBOS-BLOQUE
        this.setComboCatalogoReciboBloque(lista15.stream()
                .map(c -> new SelectItem(c.getCatDescripcionElemento(), c.getCatDescripcionElemento()))
                .collect(Collectors.toList()));

        this.setComboTiposComprobantePago(daoCatalogo.seleccionarCatalogo("039").stream()
                .map(c -> new SelectItem(c.getCatCodigoGrupo() + c.getCatCodigoElemento(), c.getCatDescripcionElemento()))
                .collect(Collectors.toList()));

        cabecera1 = "Semestre";
        cabecera2 = "Especialidad";
        cabecera3 = "F. de Pro.";
        cabecera4 = "Est.";
        viewDetail = true;
    }

    public List<TbCatalogo> getProcedencias() {
        return procedencias;
    }

    public void setProcedencias(List<TbCatalogo> procedencias) {
        this.procedencias = procedencias;
    }

    public List<TbCatalogo> getEstadosBoleta() {
        return estadosBoleta;
    }

    public void setEstadosBoleta(List<TbCatalogo> estadosBoleta) {
        this.estadosBoleta = estadosBoleta;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public List<BeanConceptoPago> autocomplete(Object suggest) {
        if ("null".equals(suggest) || ObjectUtils.isEmpty(suggest)) {
            return Collections.emptyList();
        }
        String pref = (String) suggest;
        ArrayList<BeanConceptoPago> results = new ArrayList<>();
        LOG.debug("Looking for payment concept '{}'", pref);

        for (AdConceptoPago acp : conceptosPago) {
            if (acp.getConpagDescripcion().toLowerCase().contains(pref.toLowerCase()) &&
                    !"007001".equals(acp.getConpagTipo())) {
                var pp = new BeanConceptoPago();
                pp.setId(acp.getId());
                pp.setDesc(acp.getConpagDescripcion());
                pp.setMonto(acp.getConpagMonto());
                results.add(pp);
            }
        }
        LOG.info("Total autocomplete {}", results.size());
        return results;
    }

    public String getnMpNumSerie() {
        return nMpNumSerie;
    }

    public void setnMpNumSerie(String nMpNumSerie) {
        this.nMpNumSerie = nMpNumSerie;
    }

    public int getEstructPagoDetId() {
        return estructPagoDetId;
    }

    public void setEstructPagoDetId(int estructPagoDetId) {
        this.estructPagoDetId = estructPagoDetId;
    }

    public String getImagenP2() {
        return imagenP2;
    }

    public void setImagenP2(String imagenP2) {
        this.imagenP2 = imagenP2;
    }

    public boolean isDisabledP2() {
        return disabledP2;
    }

    public void setDisabledP2(boolean disabledP2) {
        this.disabledP2 = disabledP2;
    }

    public int getZcComprobanteId() {
        return zcComprobanteId;
    }

    public void setZcComprobanteId(int zcComprobanteId) {
        this.zcComprobanteId = zcComprobanteId;
    }

    public String getZcNombre() {
        return zcNombre;
    }

    public void setZcNombre(String zcNombre) {
        this.zcNombre = zcNombre;
    }

    public float getZcMonto() {
        return zcMonto;
    }

    public void setZcMonto(float zcMonto) {
        this.zcMonto = zcMonto;
    }

    public String getZcNumeroComprobante() {
        return zcNumeroComprobante;
    }

    public void setZcNumeroComprobante(String zcNumeroComprobante) {
        this.zcNumeroComprobante = zcNumeroComprobante;
    }

    public Date getZcFechaPago() {
        return zcFechaPago;
    }

    public void setZcFechaPago(Date zcFechaPago) {
        this.zcFechaPago = zcFechaPago;
    }

    public String getZcConceptoPago() {
        return zcConceptoPago;
    }

    public void setZcConceptoPago(String zcConceptoPago) {
        this.zcConceptoPago = zcConceptoPago;
    }

    public String getZcConceptoPagoDetalle() {
        return zcConceptoPagoDetalle;
    }

    public void setZcConceptoPagoDetalle(String zcConceptoPagoDetalle) {
        this.zcConceptoPagoDetalle = zcConceptoPagoDetalle;
    }

    public String getZcProcedenciaPagoId() {
        return zcProcedenciaPagoId;
    }

    public void setZcProcedenciaPagoId(String zcProcedenciaPagoId) {
        this.zcProcedenciaPagoId = zcProcedenciaPagoId;
    }

    public String getNoncomplete() {
        return noncomplete;
    }

    public void setNoncomplete(String noncomplete) {
        this.noncomplete = noncomplete;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getOncomplete2() {
        return oncomplete2;
    }

    public void setOncomplete2(String oncomplete2) {
        this.oncomplete2 = oncomplete2;
    }

    public String getImagenP() {
        return imagenP;
    }

    public void setImagenP(String imagenP) {
        this.imagenP = imagenP;
    }

    public boolean isDisabledP() {
        return disabledP;
    }

    public void setDisabledP(boolean disabledP) {
        this.disabledP = disabledP;
    }

    public int getParamListaPagoId() {
        return paramListaPagoId;
    }

    public void setParamListaPagoId(int paramListaPagoId) {
        this.paramListaPagoId = paramListaPagoId;
    }

    // ---- Server-side pagination of the main search results (1-based currentPage) ----

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        if (pageSize <= 0 || totalCount <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    public boolean isMostrarAccionesComprobante() {
        return mostrarAccionesComprobante;
    }

    public void setMostrarAccionesComprobante(boolean mostrarAccionesComprobante) {
        this.mostrarAccionesComprobante = mostrarAccionesComprobante;
    }

    public void firstPage(ActionEvent event) {
        this.currentPage = 1;
        runSearch();
    }

    public void prevPage(ActionEvent event) {
        if (currentPage > 1) {
            this.currentPage--;
        }
        runSearch();
    }

    public void nextPage(ActionEvent event) {
        if (currentPage < getTotalPages()) {
            this.currentPage++;
        }
        runSearch();
    }

    public void lastPage(ActionEvent event) {
        this.currentPage = getTotalPages();
        runSearch();
    }

    /**
     * Rebuilds the concept-filter combo for the selected payment type: Curso Libre (007003) concepts
     * for FREE COURSES, every other concept otherwise (issue #4, Decision A). Called from init() and
     * whenever the payment type changes.
     */
    private void buildConceptCombo() {
        this.setComboConceptoPago(paymentSearchService.findSearchableConcepts(paramListaPagoId).stream()
                .map(cp -> new SelectItem(cp.getId(), cp.getConpagDescripcion()))
                .toArray(SelectItem[]::new));
    }

    public void cambiarTipoPago(ActionEvent event) {
        LOG.debug("[{}] Payment type changed to {}", event.getPhaseId(), paramListaPagoId);
        this.paramConceptoPagoId = 0;
        buildConceptCombo();
    }

    public float getnMpMonto() {
        return nMpMonto;
    }

    public void setnMpMonto(float nMpMonto) {
        this.nMpMonto = nMpMonto;
    }

    public String getnMpIdTipoCliente() {
        return nMpIdTipoCliente;
    }

    public void setnMpIdTipoCliente(String nMpIdTipoCliente) {
        this.nMpIdTipoCliente = nMpIdTipoCliente;
    }

    public int getTmpClientId() {
        return tmpClientId;
    }

    public void setTmpClientId(int tmpClientId) {
        this.tmpClientId = tmpClientId;
    }

    public String getnMpRazonSocial() {
        return nMpRazonSocial;
    }

    public void setnMpRazonSocial(String nMpRazonSocial) {
        this.nMpRazonSocial = nMpRazonSocial;
    }

    public String getnMpTipoPersNatJur() {
        return nMpTipoPersNatJur;
    }

    public void setnMpTipoPersNatJur(String nMpTipoPersNatJur) {
        this.nMpTipoPersNatJur = nMpTipoPersNatJur;
    }

    public String getnMpDniRuc() {
        return nMpDniRuc;
    }

    public void setnMpDniRuc(String nMpDniRuc) {
        this.nMpDniRuc = nMpDniRuc;
    }

    public String getnMpProcedenciaId() {
        return nMpProcedenciaId;
    }

    public void setnMpProcedenciaId(String nMpProcedenciaId) {
        this.nMpProcedenciaId = nMpProcedenciaId;
    }

    public String getnMpNumComprobante() {
        return nMpNumComprobante;
    }

    public void setnMpNumComprobante(String nMpNumComprobante) {
        this.nMpNumComprobante = nMpNumComprobante;
    }

    public Date getnMpFechaComprobante() {
        return nMpFechaComprobante;
    }

    public void setnMpFechaComprobante(Date nMpFechaComprobante) {
        this.nMpFechaComprobante = nMpFechaComprobante;
    }

    public float getnMpMontoPago() {
        return nMpMontoPago;
    }

    public void setnMpMontoPago(float nMpMontoPago) {
        this.nMpMontoPago = nMpMontoPago;
    }

    public int getnValorNuevo() {
        return nValorNuevo;
    }

    public void setnValorNuevo(int nValorNuevo) {
        this.nValorNuevo = nValorNuevo;
    }

    public double getnMpSaldo() {
        return nMpSaldo;
    }

    public void setnMpSaldo(double nMpSaldo) {
        this.nMpSaldo = nMpSaldo;
    }

    public String getnMpDescription() {
        return nMpDescription;
    }

    public void setnMpDescription(String nMpDescription) {
        this.nMpDescription = nMpDescription;
    }

    public String getnMpCodAlu() {
        return nMpCodAlu;
    }

    public void setnMpCodAlu(String nMpCodAlu) {
        this.nMpCodAlu = nMpCodAlu;
    }

    public float getClienteSaldo() {
        return clienteSaldo;
    }

    public void setClienteSaldo(float clienteSaldo) {
        this.clienteSaldo = clienteSaldo;
    }

    public String getClienteCodigo() {
        return clienteCodigo;
    }

    public void setClienteCodigo(String clienteCodigo) {
        this.clienteCodigo = clienteCodigo;
    }

    public String getParamAluCodigo() {
        return paramAluCodigo;
    }

    public void setParamAluCodigo(String paramAluCodigo) {
        this.paramAluCodigo = paramAluCodigo;
    }

    public int getParamSemestreId() {
        return paramSemestreId;
    }

    public void setParamSemestreId(int paramSemestreId) {
        this.paramSemestreId = paramSemestreId;
    }

    public int getParamConceptoPagoId() {
        return paramConceptoPagoId;
    }

    public void setParamConceptoPagoId(int paramConceptoPagoId) {
        this.paramConceptoPagoId = paramConceptoPagoId;
    }

    public int getParamFacId() {
        return paramFacId;
    }

    public void setParamFacId(int paramFacId) {
        this.paramFacId = paramFacId;
    }

    public int getParamEspId() {
        return paramEspId;
    }

    public void setParamEspId(int paramEspId) {
        this.paramEspId = paramEspId;
    }

    public String getParamEstadoId() {
        return paramEstadoId;
    }

    public void setParamEstadoId(String paramEstadoId) {
        this.paramEstadoId = paramEstadoId;
    }

    public String getAlutarDniRuc() {
        return alutarDniRuc;
    }

    public void setAlutarDniRuc(String alutarDniRuc) {
        this.alutarDniRuc = alutarDniRuc;
    }

    public List<MbPago> getListaAlumno() {
        return listaAlumno;
    }

    public void setListaAlumno(List<MbPago> listaAlumno) {
        this.listaAlumno = listaAlumno;
    }

    public List<MbPago> getListaCliente() {
        return listaCliente;
    }

    public void setListaCliente(List<MbPago> listaCliente) {
        this.listaCliente = listaCliente;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
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

    public Date getParamFechaDesde() {
        return paramFechaDesde;
    }

    public void setParamFechaDesde(Date paramFechaDesde) {
        this.paramFechaDesde = paramFechaDesde;
    }

    public Date getParamFechaHasta() {
        return paramFechaHasta;
    }

    public void setParamFechaHasta(Date paramFechaHasta) {
        this.paramFechaHasta = paramFechaHasta;
    }

    public int getAlutarId() {
        return alutarId;
    }

    public void setAlutarId(int alutarId) {
        this.alutarId = alutarId;
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public int getConceptoPagoId() {
        return conceptoPagoId;
    }

    public void setConceptoPagoId(int conceptoPagoId) {
        this.conceptoPagoId = conceptoPagoId;
    }

    public String getRowSemestreOrNroComp() {
        return rowSemestreOrNroComp;
    }

    public void setRowSemestreOrNroComp(String rowSemestreOrNroComp) {
        this.rowSemestreOrNroComp = rowSemestreOrNroComp;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getRowEspecialidadOrTipo() {
        return rowEspecialidadOrTipo;
    }

    public void setRowEspecialidadOrTipo(String rowEspecialidadOrTipo) {
        this.rowEspecialidadOrTipo = rowEspecialidadOrTipo;
    }

    public String getRowCodigoAlu() {
        return rowCodigoAlu;
    }

    public void setRowCodigoAlu(String rowCodigoAlu) {
        this.rowCodigoAlu = rowCodigoAlu;
    }

    public String getRowNombres() {
        return rowNombres;
    }

    public void setRowNombres(String rowNombres) {
        this.rowNombres = rowNombres;
    }

    public String getConceptoPagoDesc() {
        return conceptoPagoDesc;
    }

    public void setConceptoPagoDesc(String conceptoPagoDesc) {
        this.conceptoPagoDesc = conceptoPagoDesc;
    }

    public String getRowConceptoPago() {
        return rowConceptoPago;
    }

    public void setRowConceptoPago(String rowConceptoPago) {
        this.rowConceptoPago = rowConceptoPago;
    }

    public float getRowMonto() {
        return rowMonto;
    }

    public void setRowMonto(float rowMonto) {
        this.rowMonto = rowMonto;
    }

    public Date getAlutarFecPago() {
        return alutarFecPago;
    }

    public void setAlutarFecPago(Date alutarFecPago) {
        this.alutarFecPago = alutarFecPago;
    }

    public Date getRowFecProOrFecComprPago() {
        return rowFecProOrFecComprPago;
    }

    public void setRowFecProOrFecComprPago(Date rowFecProOrFecComprPago) {
        this.rowFecProOrFecComprPago = rowFecProOrFecComprPago;
    }

    public String getRowEstadoOrComprPagoMonto() {
        return rowEstadoOrComprPagoMonto;
    }

    public void setRowEstadoOrComprPagoMonto(String rowEstadoOrComprPagoMonto) {
        this.rowEstadoOrComprPagoMonto = rowEstadoOrComprPagoMonto;
    }

    public float getRowAdeuda() {
        return rowAdeuda;
    }

    public void setRowAdeuda(float rowAdeuda) {
        this.rowAdeuda = rowAdeuda;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteRazon() {
        return clienteRazon;
    }

    public void setClienteRazon(String clienteRazon) {
        this.clienteRazon = clienteRazon;
    }

    public String getClienteDniRuc() {
        return clienteDniRuc;
    }

    public void setClienteDniRuc(String clienteDniRuc) {
        this.clienteDniRuc = clienteDniRuc;
    }

    public String getSelCli() {
        return selCli;
    }

    public void setSelCli(String selCli) {
        this.selCli = selCli;
    }

    public List<MbPago> getDetallesPagosInternos() {
        return detallesPagosInternos;
    }

    public void setDetallesPagosInternos(List<MbPago> detallesPagosInternos) {
        this.detallesPagosInternos = detallesPagosInternos;
    }

    public Date getDetCompagFechaPago() {
        return detCompagFechaPago;
    }

    public void setDetCompagFechaPago(Date detCompagFechaPago) {
        this.detCompagFechaPago = detCompagFechaPago;
    }

    public float getDetCompagMonto() {
        return detCompagMonto;
    }

    public void setDetCompagMonto(float detCompagMonto) {
        this.detCompagMonto = detCompagMonto;
    }

    public int getDetCompagId() {
        return detCompagId;
    }

    public void setDetCompagId(int detCompagId) {
        this.detCompagId = detCompagId;
    }

    public int getnConPagId() {
        return nConPagId;
    }

    public void setnConPagId(int nConPagId) {
        this.nConPagId = nConPagId;
    }

    public String getDetCompagNro() {
        return detCompagNro;
    }

    public void setDetCompagNro(String detCompagNro) {
        this.detCompagNro = detCompagNro;
    }

    public String getDetNombrePagador() {
        return detNombrePagador;
    }

    public void setDetNombrePagador(String detNombrePagador) {
        this.detNombrePagador = detNombrePagador;
    }

    public String getDetCompagTipoCompValue() {
        return detCompagTipoCompValue;
    }

    public void setDetCompagTipoCompValue(String detCompagTipoCompValue) {
        this.detCompagTipoCompValue = detCompagTipoCompValue;
    }

    public String getDetCompagTipoCompId() {
        return detCompagTipoCompId;
    }

    public void setDetCompagTipoCompId(String detCompagTipoCompId) {
        this.detCompagTipoCompId = detCompagTipoCompId;
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

    public SelectItem[] getComboConceptoPago() {
        return comboConceptoPago;
    }

    public void setComboConceptoPago(SelectItem[] comboConceptoPago) {
        this.comboConceptoPago = comboConceptoPago;
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
        LOG.debug("[{}] Loading Modal specialities of faculty {}", event.getPhaseId(), paramFacId);

        comboEspecialidadBuscar.clear();
        if (paramFacId != 0) {
            HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            List<AcEspecialidad> lista = dao.seleccionarEspecialidad(paramFacId);
            this.comboEspecialidadBuscar.addAll(lista.stream()
                    .map(esp -> new SelectItem(esp.getId(), esp.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public SelectItem[] getComboCatalogoListaPagos() {
        return comboCatalogoListaPagos;
    }

    public void setComboCatalogoListaPagos(SelectItem[] comboCatalogoListaPagos) {
        this.comboCatalogoListaPagos = comboCatalogoListaPagos;
    }

    public void cambiaSerieN(ActionEvent event) {
        LOG.debug("[{}] From serie '{}'", event.getPhaseId(), nMpNumSerie);
        setnMpNumComprobante(helper.serie(nMpNumSerie, getnMpTipoComprPago()).getSerie());
    }

    public void seleccionar() {
        this.currentPage = 1;
        runSearch();
    }

    /**
     * Runs the current search for {@link #currentPage} without resetting the page — shared by the
     * search button (via {@link #seleccionar()}, which resets to page 1) and the pager actions.
     */
    private void runSearch() {
        LOG.debug("Searching payments by type '{}' (page {})", paramListaPagoId, currentPage);
        listaAlumno = new ArrayList<>();
        totalCount = 0;
        mostrarAccionesComprobante = false;
        if (this.getParamListaPagoId() == 1) {
            performInternalSearch();
        } else if (this.getParamListaPagoId() == 2) {
            performExternalSearch();
        } else if (this.getParamListaPagoId() == 3) {
            performFreeCoursesSearch();
        }
        this.setListaAlumno(listaAlumno);
        LOG.info("Total {} pagos (page {} of {}, {} matching)", listaAlumno.size(), currentPage, getTotalPages(), totalCount);
    }

    private void performInternalSearch() {
        this.setCabecera1("Semestre");
        this.setCabecera2("Especialidad");
        this.setCabecera3("F. de Pro.");
        this.setCabecera4("Est.");
        this.setViewDetail(true);
        LOG.debug("Searching Internal Codigo: '{}' | Estado: '{}' | Facultad: {} | Especialidad: {} | Concepto Pago: {} | Paterno: '{}' | Materno: '{}' | Semestre: {}",
                this.paramAluCodigo, this.paramEstadoId, this.paramFacId, this.paramEspId, this.paramConceptoPagoId, this.paramAluApPaterno, paramAluApMaterno, this.paramSemestreId);
        var criteria = new InternalPaymentCriteria(this.paramAluCodigo, this.paramEstadoId, this.paramEspId,
                this.paramConceptoPagoId, this.paramAluApPaterno, this.paramAluApMaterno, this.paramSemestreId);
        PageResult<AdAlumnoTarifa> page = paymentSearchService.searchInternal(criteria, currentPage, pageSize);
        this.totalCount = page.getTotalCount();
        List<AdAlumnoTarifa> lista = page.getItems();

        // One aggregate query for the page's paid amounts, replacing the per-row N+1.
        List<Integer> tarifaIds = lista.stream().map(AdAlumnoTarifa::getId).collect(Collectors.toList());
        Map<Integer, Float> paidByTarifa = paymentSearchService.sumValidPaymentsByTarifa(tarifaIds);

        for (AdAlumnoTarifa adAlumnoTarifa : lista) {
            var pag = new MbPago();
            pag.setAlutarId(adAlumnoTarifa.getId());
            pag.setAlumnoId(adAlumnoTarifa.getAlu().getId());
            pag.setConceptoPagoId(adAlumnoTarifa.getConpag().getId());
            pag.setComprobanteOculto(0);

            // The tariff's own Estpagdet already carries structure/semester/faculty/specialty (all
            // eager to-one), so read it directly instead of scanning the whole estructura list once
            // per row — the old O(rows x allDetalles) pass that also loaded the full table each search.
            AdEstructuraPagosDetalle estpagdet = adAlumnoTarifa.getEstpagdet();
            pag.setRowSemestreOrNroComp(estpagdet.getEstpag().getSem().getSemCodigo());
            pag.setFacultad(estpagdet.getEstpag().getEsp().getFac().getFacNombre());
            pag.setRowEspecialidadOrTipo(estpagdet.getEstpag().getEsp().getEspNombre());
            pag.setRowConceptoPago(estpagdet.getEstpagdetNombre());
            pag.setEstructPagoDetId(estpagdet.getId());

            float pagy = adAlumnoTarifa.getAlutarMonto();
            pag.setAlutarDniRuc(adAlumnoTarifa.getAlu().getAluDni());
            pag.setRowCodigoAlu(adAlumnoTarifa.getAlu().getAluCodigo());
            pag.setRowNombres(adAlumnoTarifa.getAlu().getAluAppaterno() + " " + adAlumnoTarifa.getAlu().getAluApmaterno() + " " + adAlumnoTarifa.getAlu().getAluNombres());
            pag.setConceptoPagoDesc(adAlumnoTarifa.getConpag().getConpagDescripcion());
            pag.setRowMonto(adAlumnoTarifa.getAlutarMonto());
            pag.setAlutarFecPago(adAlumnoTarifa.getAlutarFechaPago());
            pag.setRowFecProOrFecComprPago(adAlumnoTarifa.getAlutarFechaProrroga());
            switch (adAlumnoTarifa.getAlutarEstado()) {
                case "2":
                    pag.setRowEstadoOrComprPagoMonto("PT");
                    pag.setImagenP(ACTION_IMAGES_CERRADO);
                    pag.setDisabledP(true);
                    pag.setImagenP2(ACTION_IMAGES_EDIT_PASTE_GRIS);
                    pag.setDisabledP2(true);
                    break;
                case "1":
                    pag.setRowEstadoOrComprPagoMonto("PP");
                    break;
                case "0":
                    pag.setRowEstadoOrComprPagoMonto("D");
                    break;
                default:
                    LOG.warn("Unregistered '{}'", adAlumnoTarifa.getAlutarEstado());
                    break;
            }
            float pagx = paidByTarifa.getOrDefault(adAlumnoTarifa.getId(), 0f);
            pag.setRowAdeuda(pagy - pagx);
            pag.setSaldo(0);
            pag.setDetallesPagosInternos(new ArrayList<>());
            pag.setProcedencias(procedencias);
            pag.setEstadosBoleta(estadosBoleta);
            listaAlumno.add(pag);
        }
    }

    private void performFreeCoursesSearch() {
        this.setCabecera1("Módulo");
        this.setCabecera2("Taller");
        this.setCabecera3("F. de Pro.");
        this.setCabecera4("Est.");
        this.setViewDetail(false);
        LOG.debug("Searching Free Courses Codigo: '{}' | Estado: '{}' | Concepto Pago: {} | Paterno: '{}' | Materno: '{}'",
                this.paramAluCodigo, this.paramEstadoId, this.paramConceptoPagoId, this.paramAluApPaterno, this.paramAluApMaterno);
        var criteria = new FreeCourseCriteria(this.paramAluCodigo, this.paramEstadoId, this.paramConceptoPagoId,
                this.paramAluApPaterno, this.paramAluApMaterno);
        PageResult<ClAlumnoTarifa> page = paymentSearchService.searchFreeCourse(criteria, currentPage, pageSize);
        this.totalCount = page.getTotalCount();
        List<ClAlumnoTarifa> lista = page.getItems();

        List<Integer> tarifaIds = lista.stream().map(ClAlumnoTarifa::getAlutarId).collect(Collectors.toList());
        Map<Integer, Float> paidByTarifa = paymentSearchService.sumValidPaymentsByTarifa(tarifaIds);

        for (ClAlumnoTarifa alumnoTarifa : lista) {
            var pag = new MbPago();
            pag.setAlutarId(alumnoTarifa.getAlutarId());
            pag.setAlumnoId(alumnoTarifa.getClAlumno().getAluId());
            pag.setConceptoPagoId(alumnoTarifa.getConpagId());
            pag.setComprobanteOculto(0);

            var estpagdet = alumnoTarifa.getClEstructuraPagosDetalle();
            pag.setRowSemestreOrNroComp(estpagdet.getClEstructuraPagos().getClModulo().getModDescripcion());
            pag.setRowEspecialidadOrTipo(estpagdet.getEstpagdetNombre());
            pag.setRowConceptoPago(estpagdet.getEstpagdetNombre());
            pag.setFacultad("-");

            float pagy = alumnoTarifa.getAlutarMonto();
            pag.setAlutarDniRuc(alumnoTarifa.getClAlumno().getAluDni());
            pag.setRowCodigoAlu(alumnoTarifa.getClAlumno().getAluCodigo());
            pag.setRowNombres(alumnoTarifa.getClAlumno().getAluAppaterno() + " " + alumnoTarifa.getClAlumno().getAluApmaterno() + " " + alumnoTarifa.getClAlumno().getAluNombres());
            pag.setConceptoPagoDesc(estpagdet.getEstpagdetNombre());
            pag.setRowMonto(alumnoTarifa.getAlutarMonto());
            pag.setAlutarFecPago(alumnoTarifa.getAlutarFechaPago());
            pag.setRowFecProOrFecComprPago(alumnoTarifa.getAlutarFechaProrroga());
            switch (alumnoTarifa.getAlutarEstado()) {
                case "2":
                    pag.setRowEstadoOrComprPagoMonto("PT");
                    pag.setImagenP(ACTION_IMAGES_CERRADO);
                    pag.setDisabledP(true);
                    pag.setImagenP2(ACTION_IMAGES_EDIT_PASTE_GRIS);
                    pag.setDisabledP2(true);
                    break;
                case "1":
                    pag.setRowEstadoOrComprPagoMonto("PP");
                    break;
                case "0":
                    pag.setRowEstadoOrComprPagoMonto("D");
                    break;
                default:
                    LOG.warn("Unregistered '{}'", alumnoTarifa.getAlutarEstado());
                    break;
            }
            float pagx = paidByTarifa.getOrDefault(alumnoTarifa.getAlutarId(), 0f);
            pag.setRowAdeuda(pagy - pagx);
            pag.setSaldo(0);
            pag.setDetallesPagosInternos(new ArrayList<>());
            pag.setProcedencias(procedencias);
            pag.setEstadosBoleta(estadosBoleta);
            listaAlumno.add(pag);
        }
    }

    private void performExternalSearch() {
        this.mostrarAccionesComprobante = true;
        this.setCabecera1("Nro-Comprobante");
        this.setCabecera2("Tipo");
        this.setCabecera3("F. de Pago");
        this.setCabecera4("Pago");
        this.setViewDetail(false);
        LOG.debug("Searching External Desde: {} | Hasta: {} | Concepto Pago: {}",
                this.paramFechaDesde, this.paramFechaHasta, this.paramConceptoPagoId);
        PageResult<AdComprobantePago> page = paymentSearchService.searchExternal(this.getParamFechaDesde(), this.getParamFechaHasta(), this.getParamConceptoPagoId(), currentPage, pageSize);
        this.totalCount = page.getTotalCount();
        for (AdComprobantePago comprobantePago : page.getItems()) {
            var pag = new MbPago();
            pag.setAlutarId(0);
            pag.setAlumnoId(0);
            Set<AdPago> pagos = comprobantePago.getAdPagos();
            List<AdPago> lPagos = Collections.synchronizedList(new LinkedList<>(pagos));
            AdPago p = lPagos.get(0);
            pag.setConceptoPagoId(p.getConpag().getId());
            pag.setComprobanteOculto(comprobantePago.getId());
            pag.setNroComprobanteOculto(comprobantePago.getCompagNro());
            pag.setComprobanteFechaOculto(comprobantePago.getCompagFecha());
            pag.setComprobanteMontoOculto(comprobantePago.getCompagMonto());

            pag.setComprobanteConceptoOculto(p.getConpag().getConpagDescripcion());
            pag.setComprobanteConceptoDetOculto(p.getConpag().getConpagDescripcion());
            pag.setConceptoPagoDesc(p.getConpag().getConpagDescripcion());
            pag.setRowConceptoPago(p.getConpag().getConpagDescripcion());//para que la descripcion sea uniforme

            pag.setRowMonto(p.getConpag().getConpagMonto());
            pag.setAlutarFecPago(p.getPagFecha());
            pag.setRowAdeuda(p.getConpag().getConpagMonto() - Float.parseFloat(comprobantePago.getCompagMonto().toString()));///monto que adeuda actulamente

            pag.setFacultad("-");
            pag.setZcComprobanteId(comprobantePago.getId());
            pag.setRowFecProOrFecComprPago(comprobantePago.getCompagFecha());
            for (TbCatalogo catalogo : estadosBoleta) {
                if (comprobantePago.getCompagTipo().equals(catalogo.getCatCodigoGrupo() + catalogo.getCatCodigoElemento())) {
                    pag.setComprobanteTipo(catalogo.getCatDescripcionElemento());
                }
            }
            pag.setRowSemestreOrNroComp(comprobantePago.getCompagNro());
            pag.setRowEstadoOrComprPagoMonto(comprobantePago.getCompagMonto().toString());
            for (TbCatalogo catalogo : procedencias) {
                if (comprobantePago.getCompagProcedencia().equals(catalogo.getCatCodigoGrupo() + catalogo.getCatCodigoElemento())) {
                    pag.setRowEspecialidadOrTipo(catalogo.getCatDescripcionElemento());
                }
            }
            LOG.trace("\t{} >> {} ::: {}",
                    comprobantePago.getCompagClienteTipo(), comprobantePago.getCompagAlu(), comprobantePago.getCompagCliente());
            AcAlumno lAlumno = null;
            // compag_alu carries no compag_alu_tipo from this app (issue #49): for a Curso Libre
            // payer (014003) it holds a cl_alumno id, so resolving it against ac_alumno renders an
            // unrelated student whenever the numeric id collides. Skip the AC lookup for CL vouchers
            // and let the payer switch below resolve the CL student (self-pay: same id, right table).
            if (comprobantePago.getCompagAlu() != 0
                    && !CAT_TIPO_CLIENTE_ALUMNO_CL.equals(comprobantePago.getCompagClienteTipo())) {
                lAlumno = daoAlumno.seleccionarUnAlumno(comprobantePago.getCompagAlu());
            }
            if (lAlumno != null) {
                pag.setAlutarDniRuc(lAlumno.getAluDni());
                pag.setRowCodigoAlu(lAlumno.getAluCodigo());
                pag.setRowNombres(lAlumno.getAluAppaterno() + " " + lAlumno.getAluApmaterno() + " " + lAlumno.getAluNombres());
                pag.setComprobanteNombreOculto(lAlumno.getAluAppaterno() + " " + lAlumno.getAluApmaterno() + " " + lAlumno.getAluNombres());
            } else {
                pag.setRowCodigoAlu("EXTERNO");
                switch (comprobantePago.getCompagClienteTipo()) {
                    case CAT_TIPO_CLIENTE_ALUMNO_CP:
                        AcAlumno alu = daoAlumno.seleccionarUnAlumno(comprobantePago.getCompagCliente());
                        if (alu == null) {
                            pag.setRowNombres("");
                        } else {
                            String a = alu.getAluAppaterno() + " " + alu.getAluApmaterno() + " " + alu.getAluNombres();
                            pag.setRowNombres(a);
                            pag.setComprobanteNombreOculto(a);
                            pag.setRowCodigoAlu(alu.getAluCodigo());
                        }
                        break;
                    case CAT_TIPO_CLIENTE_EXTERNO:
                        AdClientes cli = daoCliente.seleccionarUnCliente(comprobantePago.getCompagCliente());
                        if (cli == null) {
                            pag.setRowNombres("");
                        } else {
                            pag.setRowNombres(cli.getCliRazonSocial());
                            pag.setComprobanteNombreOculto(cli.getCliRazonSocial());
                        }
                        break;
                    default:
                        ClAlumno clAlu = daoAlumnoLibre.buscarAlumnoPorAluId(comprobantePago.getCompagCliente());
                        if (clAlu == null) {
                            pag.setRowNombres("");
                        } else {
                            String s = clAlu.getAluAppaterno() + " " + clAlu.getAluApmaterno() + " " + clAlu.getAluNombres();
                            pag.setRowNombres(s);
                            pag.setComprobanteNombreOculto(s);
                            pag.setRowCodigoAlu(clAlu.getAluCodigo());
                        }
                        break;
                }
            }
            pag.setImagenP(ACTION_IMAGES_CERRADO);
            pag.setDisabledP(true);
            pag.setImagenP2(ACTION_IMAGES_EDIT_PASTE_GRIS);
            pag.setDisabledP2(true);
            pag.setSaldo(0);
            pag.setDetallesPagosInternos(new ArrayList<>());
            pag.setProcedencias(procedencias);
            pag.setEstadosBoleta(estadosBoleta);
            listaAlumno.add(pag);
        }
    }

    public void verSubTabla(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        Map<String, Object> attr = event.getComponent().getAttributes();
        var flagVer = attr.get("flag").toString();
        if (flagVer.equals("0")) {
            var idAlutar = Integer.parseInt(attr.get("id_x").toString());
            var mora = Integer.parseInt(daoCatalogo.seleccionarValor(CAT_CONFIGURACION_MORA_ID));
            var matexp = Integer.parseInt(daoCatalogo.seleccionarValor(CAT_CONFIGURACION_MATRICULA_EXTEMPORANEA));
            var carneBib = Integer.parseInt(daoCatalogo.seleccionarValor("021006"));
            listaAlumno = new ArrayList<>();
            List<AdPago> listaPago = daoPago.seleccionarMostrarPagosMora(idAlutar);
            List<MbPago> compagos = new ArrayList<>();
            for (AdPago pago : listaPago) {
                var compag = new MbPago();
                compag.setDetCompagNro(pago.getCompag().getCompagNro());
                if (pago.getCompag().getCompagClienteTipo().equals(CAT_TIPO_CLIENTE_ALUMNO_CP)) {
                    AcAlumno alu = daoAlumno.seleccionarUnAlumno(pago.getCompag().getCompagCliente());
                    if (alu == null) {
                        compag.setDetNombrePagador("");
                    } else {
                        compag.setDetNombrePagador(alu.getAluAppaterno() + " " + alu.getAluApmaterno() + " " + alu.getAluNombres());
                    }
                }
                if (pago.getCompag().getCompagClienteTipo().equals(CAT_TIPO_CLIENTE_EXTERNO)) {
                    AdClientes listCli2 = daoCliente.seleccionarUnCliente(pago.getCompag().getCompagCliente());
                    if (listCli2 == null) {
                        compag.setDetNombrePagador("");
                    } else {
                        compag.setDetNombrePagador(listCli2.getCliRazonSocial());
                    }
                }
                compag.setDetCompagMonto(pago.getPagMonto());
                for (TbCatalogo catalogo : estadosBoleta) {
                    if ((catalogo.getCatCodigoGrupo() + catalogo.getCatCodigoElemento()).equals(pago.getCompag().getCompagTipo())) {
                        compag.setDetTipoPago(catalogo.getCatDescripcionElemento());
                    }
                }
                compag.setDetCompagFechaPago(pago.getCompag().getCompagFecha());
                compag.setDetCompagId(pago.getCompag().getId());
                compag.setDetCompagTipoCompId(pago.getCompag().getCompagProcedencia());
                for (TbCatalogo procedencia : procedencias) {
                    if ((procedencia.getCatCodigoGrupo() + procedencia.getCatCodigoElemento()).equals(pago.getCompag().getCompagProcedencia())) {
                        compag.setDetCompagTipoCompValue(procedencia.getCatDescripcionElemento());
                        if (mora == pago.getConpag().getId()) {
                            compag.setDetCompagTipoCompValue(procedencia.getCatDescripcionElemento() + "-Mora");
                        }
                        if (matexp == pago.getConpag().getId()) {
                            compag.setDetCompagTipoCompValue(procedencia.getCatDescripcionElemento() + "- Mat Extempo.");
                        }
                        if (carneBib == pago.getConpag().getId()) {
                            compag.setDetCompagTipoCompValue(procedencia.getCatDescripcionElemento() + "- CARNE BIBLIO.");
                        }
                    }
                }
                compag.setDetCompagTipoDoc(pago.getCompag().getCompagTipDoc());
                compag.setDetVoucherNroOper(pago.getCompag().getCompagVoucherNroOperacion());
                compag.setDetVoucherFecha(pago.getCompag().getCompagVoucherFecha());
                compag.setDetVoucherCodAgencia(pago.getCompag().getCompagVoucherCodAgencia());
                compagos.add(compag);
            }
            this.setDetallesPagosInternos(compagos);
            this.setVer(true);
            this.setFlag("1");
            this.setImagen(ACTION_IMAGES_UP);
        } else {
            this.setVer(false);
            this.setFlag("0");
            this.setImagen(ACTION_IMAGES_DOWN);
        }
    }

    public void carnet(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        alumnoId = Integer.parseInt(event.getComponent().getAttributes().get(PARAM_ALUMNO_ID).toString());
        flag = "1";
        this.setOncomplete("");
        this.setZcNumeroComprobante("");
    }

    public void mora(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        alumnoId = Integer.parseInt(event.getComponent().getAttributes().get(PARAM_ALUMNO_ID).toString());
        flag = "2";
        this.setOncomplete("");
        this.setZcNumeroComprobante("");
    }

    public void pagarCarnet(ActionEvent event) {
        LOG.debug("[{}] '{}'", event.getPhaseId(), this.getZcNumeroComprobante());
        if (this.getZcNumeroComprobante().trim().isEmpty()) {
            this.setOncomplete("alert('Ingrese nro de comprobante')");
        } else {
            float monto = 0;
            var concepto = 0;
            if (flag.equals("1")) {
                monto = 20;
                concepto = 20;
            } else if (flag.equals("2")) {
                monto = 30;
                concepto = 23;
            }
            if (daoPago.seleccionarPagosExternos(alumnoId, concepto)) {
                this.setOncomplete("alert('Ya registra pago por el concepto seleccionado')");
            } else {
                var pag = new AdPago();

                pag.setPagMonto(monto);
                pag.setPagFecha(new Date());
                pag.setAlutarId(0);
                pag.setPagActivo("1");
                pag.setUsuId(this.pUsuID);
                var con = new AdConceptoPago();
                con.setId(concepto);
                pag.setConpag(con);
                var compag = new AdComprobantePago();
                compag.setCompagTipo(CAT_ESTADO_BOLETA_VALIDO);
                compag.setCompagDes("");
                compag.setCompagAlu(0);
                compag.setCompagCliente(alumnoId);
                compag.setCompagClienteTipo(CAT_TIPO_CLIENTE_ALUMNO_CP);
                compag.setCompagUsado("1");
                compag.setCompagProcedencia(CAT_PROCEDENCIA_PAGO_EFECTIVO);
                compag.setCompagCreacion(new Date());
                compag.setCompagActivo("1");
                compag.setCompagMonto(monto);
                compag.setCompagFecha(new Date());
                compag.setCompagNro(this.getZcNumeroComprobante());
                pag.setCompag(compag);
                compag.setAdPagos(Collections.singleton(pag));
                int pagoId = daoPago.insertarComprobantePago(compag);
                LOG.info("Payment Receipt for carnet/mora saved {}", pagoId);
                this.setOncomplete("alert('El Pago se efectuo Correctamente'),Richfaces.hideModalPanel('modal'),abrir('boleta','" + pagoId + "',0,0)");
            }
        }
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        // Modal opens with tipo de comprobante defaulted to BOLETA (set below).
        NroSerie ns = helper.serie("001", CAT_TIPO_COMPROBANTE_PAGO_BOLETA);

        this.setnValorNuevo(1);
        this.setnConPagId(0);
        this.setnMpMonto(0);
        this.setnMpSaldo(0);
        this.setnMpIdTipoCliente("0");
        this.setTmpClientId(0);
        this.setnMpRazonSocial("");
        this.setnMpTipoPersNatJur("0");
        this.setnMpDniRuc("");
        this.setnMpProcedenciaId(CAT_PROCEDENCIA_PAGO_EFECTIVO);
        this.setnMpNumSerie(ns.getType());
        this.setnMpNumComprobante(ns.getSerie());
        this.setnMpFechaComprobante(new Date());
        this.setnMpMontoPago(0);
        this.setnMpDescription("");
        this.setnMpCodAlu("");
        this.setAlumnoId(0);
        this.setRowNombres("");

        this.comboTiposComprobantePago = getComboTiposComprobantePago();
        this.setnMpTipoComprPago(CAT_TIPO_COMPROBANTE_PAGO_BOLETA);
        this.setnMpNumVou("");
        this.setnMpFecVou(new Date());
        this.setnMpCodAge("");
    }

    public void pagar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        nMpMoraSeleccion = false;
        // Modal opens with tipo de comprobante defaulted to BOLETA (set below).
        NroSerie ns = helper.serie("001", CAT_TIPO_COMPROBANTE_PAGO_BOLETA);
        setnMpProcedenciaId(CAT_PROCEDENCIA_PAGO_EFECTIVO);
        setnMpNumSerie(ns.getType());
        setnMpNumComprobante(ns.getSerie());
        setnMpFechaComprobante(new Date());

        setnValorNuevo(0);
        setTmpClientId(0);
        setnMpTipoPersNatJur("0");
        setnMpRazonSocial("");
        setnMpDniRuc("");
        setnMpIdTipoCliente("0");
        setnMpMontoPago(0);
        setnMpMoraPago(0);
        MbPago row = (MbPago) event.getComponent().getAttributes().get("alu_s");
        alutarId = row.getAlutarId();
        alumnoId = row.getAlumnoId();
        conceptoPagoId = row.getConceptoPagoId();
        rowSemestreOrNroComp = row.getRowSemestreOrNroComp();
        facultad = row.getFacultad();
        rowEspecialidadOrTipo = row.getRowEspecialidadOrTipo();
        rowCodigoAlu = row.getRowCodigoAlu();
        rowNombres = row.getRowNombres();
        conceptoPagoDesc = row.getConceptoPagoDesc();
        rowConceptoPago = row.getRowConceptoPago();
        rowMonto = row.getRowMonto();
        saldo = row.getSaldo();
        rowAdeuda = row.getRowAdeuda();
        alutarDniRuc = row.getAlutarDniRuc();

        alutarFecPago = row.getAlutarFecPago();
        rowFecProOrFecComprPago = row.getRowFecProOrFecComprPago();

        this.setnMpMontoPago(rowAdeuda);
        // Displayed mora accrues up to today. Shared LateFeePolicy (calendar-day count)
        // replaces the former Calendar.DAY_OF_YEAR-1 arithmetic, which over-counted for
        // early-morning payments and reset after a year; caja is the rule of record.
        // via epoch millis: rowFecProOrFecComprPago is a java.sql.Date, whose toInstant() throws
        var deadline = Instant.ofEpochMilli(rowFecProOrFecComprPago.getTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();
        float moraDiaria = daoConceptoPago.seleccionarUnaConceptoPago(Integer.parseInt(daoCatalogo.seleccionarValor(CAT_CONFIGURACION_MORA_ID))).getConpagMonto();
        var mora = lateFeePolicy.calculate(deadline, LocalDate.now(), BigDecimal.valueOf(moraDiaria));
        this.setnMpMoraPago(mora.floatValue());
        this.setnMpMoraSeleccion(this.getnMpMoraPago() != 0);
        float matExpo = daoConceptoPago.seleccionarUnaConceptoPago(Integer.parseInt(daoCatalogo.seleccionarValor(CAT_CONFIGURACION_MATRICULA_EXTEMPORANEA))).getConpagMonto();
        LOG.trace("XPO: {}", rowConceptoPago);
        this.setnMpMatExtempo(matExpo);
        this.setnMpMatExtempoSeleccion(matExpo != 0);

        ///////////////////////////
        this.setnMpTipoComprPago(CAT_TIPO_COMPROBANTE_PAGO_BOLETA);
        this.setnMpNumVou("");
        this.setnMpFecVou(new Date());
        this.setnMpCodAge("");
    }

    public SelectItem[] getComboCatalogoTipoCliente() {
        return comboCatalogoTipoCliente;
    }

    public void setComboCatalogoTipoCliente(SelectItem[] comboCatalogoTipoCliente) {
        this.comboCatalogoTipoCliente = comboCatalogoTipoCliente;
    }

    public SelectItem[] getComboCatalogoTipoPersNatJur() {
        return comboCatalogoTipoPersNatJur;
    }

    public void setComboCatalogoTipoPersNatJur(SelectItem[] comboCatalogoTipoPersNatJur) {
        this.comboCatalogoTipoPersNatJur = comboCatalogoTipoPersNatJur;
    }

    public SelectItem[] getComboCatalogoProcedencia() {
        return comboCatalogoProcedencia;
    }

    public void setComboCatalogoProcedencia(SelectItem[] comboCatalogoProcedencia) {
        this.comboCatalogoProcedencia = comboCatalogoProcedencia;
    }

    public void loadSaldoOfStudent(ActionEvent event) {
        LOG.debug("[{}] Student {}", event.getPhaseId(), this.alumnoId);
        this.nMpSaldo = 0;
        if (CAT_PROCEDENCIA_PAGO_SALDO.equals(nMpProcedenciaId) &&
                this.alumnoId != 0) {
            this.nMpSaldo = daoSaldo.getTotalSaldoByStudentId(this.alumnoId);
        }
    }

    public void insertar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var extra = 0;
        var varG = 1;
        int verId;
        var fecha = new Date();
        Float pagMonto = null;
        var verComPago = "0";
        var sal = new AdSaldo();
        if (this.getnMpIdTipoCliente().equals("0") ||
                this.getnMpRazonSocial().trim().isEmpty() ||
                this.getnMpTipoPersNatJur().equals("0") ||
                this.getnMpProcedenciaId().equals("0") ||
                this.getnMpMontoPago() <= 0 ||
                this.getnMpFechaComprobante() == null ||
                this.getnMpNumComprobante().trim().isEmpty()) {
            this.setOncomplete(ALERT_REQUIRED_FIELDS);
        } else {
            if (this.getTmpClientId() == 0) {
                verId = daoCliente.seleccionarUnCliente(this.getnMpRazonSocial());
                if (verId == 0) {
                    var nueCli = new AdClientes();
                    nueCli.setId(0);
                    nueCli.setCliRuc(this.getnMpDniRuc());
                    nueCli.setCliRazonSocial(this.getnMpRazonSocial());
                    nueCli.setCliDireccion("");
                    nueCli.setCliTelefono("");
                    nueCli.setCliMail("");
                    nueCli.setCliTipo(this.getnMpTipoPersNatJur());
                    nueCli.setCliActivo("1");
                    daoCliente.insertarCliente(nueCli);
                    verId = nueCli.getId();
                    LOG.info("Client saved {}", verId);
                }
            } else {
                verId = this.getTmpClientId();
            }
            String numero = this.getnMpNumSerie() + "-" + this.getnMpNumComprobante();
            if (CAT_PROCEDENCIA_PAGO_VOUCHER.equals(this.getnMpProcedenciaId())) {
                verComPago = daoPago.seleccionarUnComprobantePago(numero);
                if (!verComPago.equalsIgnoreCase("0")) {
                    this.setOncomplete("alert('El Comprobante con Número " + verComPago + " ya fue usado')");
                } else {
                    fecha = this.getnMpFechaComprobante();
                    if (this.getnMpMontoPago() <= this.getRowAdeuda()) {
                        pagMonto = this.getnMpMontoPago();
                    }
                    if (this.getnMpMontoPago() > this.getRowAdeuda()) {
                        pagMonto = this.getRowAdeuda();
                        sal = new AdSaldo();
                        float resta = this.getnMpMontoPago() - this.getRowAdeuda();//getC_adeuda_u

                        sal.setSalMonto(resta);
                        sal.setSalFechaIngresoMonto(new Date());
                        sal.setSalCreacion(new Date());
                        var alu = new AcAlumno();
                        alu.setId(this.getAlumnoId());
                        sal.setAlu(alu);
                        extra = 1;
                    }
                    if (this.getnMpFecVou() == null || this.getnMpNumVou().trim().length() == 0 || this.getnMpCodAge().trim().length() == 0) {
                        this.setOncomplete("alert('Ingrese los campos faltantes.')");
                        varG = 0;
                    }
                }
            }
            if (verId != 0 && verComPago.equals("0")) {
                // BOLETA-VALE
                switch (this.getnMpProcedenciaId()) {
                    case CAT_PROCEDENCIA_PAGO_EFECTIVO:
                        fecha = this.getnMpFechaComprobante();
                        if (this.getnMpMontoPago() > this.getRowAdeuda()) {
                            this.setOncomplete("alert('Ingrese correctamete el monto a pagar1')");
                            varG = 0;
                        }
                        if (this.getnMpMontoPago() <= this.getRowAdeuda()) {
                            pagMonto = this.getnMpMontoPago();
                        }
                        break;
                    case CAT_PROCEDENCIA_PAGO_VALE:
                        fecha = this.getnMpFechaComprobante();
                        if (this.getnMpMontoPago() > this.getRowAdeuda()) {
                            this.setOncomplete("alert('Ingrese correctamete el monto a pagar2')");
                            varG = 0;
                        }
                        if (this.getnMpMontoPago() <= this.getRowAdeuda()) {
                            pagMonto = this.getnMpMontoPago();
                        }
                        break;
                    case CAT_PROCEDENCIA_PAGO_BOLETA_VALE:
                        fecha = this.getnMpFechaComprobante();
                        if (this.getnMpMontoPago() > this.getRowAdeuda()) {
                            this.setOncomplete("alert('Ingrese correctamete el monto a pagar21')");
                            varG = 0;
                        }
                        if (this.getnMpMontoPago() <= this.getRowAdeuda()) {
                            pagMonto = this.getnMpMontoPago();
                        }
                        break;
                    case CAT_PROCEDENCIA_PAGO_SALDO:
                        fecha = new Date();
                        numero = "SAL0000000000";
                        if (this.getnMpMontoPago() > this.getSaldo()) {
                            this.setOncomplete("alert('Ud No cuenta con saldo suficiente para Concretar este Pago')");
                            varG = 0;
                        } else {
                            if (this.getnMpMontoPago() > this.getRowAdeuda()) {
                                this.setOncomplete("alert('Ingrese correctamente el monto a pagar3')");
                                varG = 0;
                            }
                            if (this.getnMpMontoPago() <= this.getRowAdeuda()) {
                                pagMonto = this.getnMpMontoPago();
                                sal = new AdSaldo();
                                float resta = -1 * this.getnMpMontoPago();
                                sal.setSalMonto(resta);
                                sal.setSalFechaIngresoMonto(new Date());
                                sal.setSalCreacion(new Date());
                                var alu = new AcAlumno();
                                alu.setId(this.getAlumnoId());
                                sal.setAlu(alu);
                                extra = 1;
                            }
                        }
                        break;
                    default:
                        pagMonto = this.getnMpMontoPago();
                        break;
                }
                if (varG == 1) { // Se puede proceder a grabar
                    var pag = new AdPago();
                    pag.setPagMonto(pagMonto);
                    pag.setPagFecha(new Date());
                    pag.setAlutarId(this.getAlutarId());
                    pag.setPagActivo("1");
                    pag.setUsuId(this.pUsuID);//AQUI SE INGRESARA EL ID DEL USUARIO DE SESSION

                    var con = new AdConceptoPago();
                    con.setId(this.getConceptoPagoId());
                    pag.setConpag(con);
                    var compag = new AdComprobantePago();
                    compag.setCompagTipo(CAT_ESTADO_BOLETA_VALIDO);
                    compag.setCompagDes("");
                    compag.setCompagAlu(0);
                    compag.setCompagCliente(verId);
                    compag.setCompagClienteTipo(this.getnMpIdTipoCliente());
                    compag.setCompagUsado("1");
                    compag.setCompagProcedencia(this.getnMpProcedenciaId());
                    compag.setCompagCreacion(new Date());
                    compag.setCompagActivo("1");
                    compag.setCompagMonto(this.getnMpMontoPago());
                    compag.setCompagFecha(fecha);
                    compag.setCompagNro(numero);
                    ///////////////////////////
                    compag.setCompagTipDoc(this.getnMpTipoComprPago());
                    compag.setCompagVoucherNroOperacion(this.getnMpNumVou());
                    compag.setCompagVoucherFecha(this.getnMpFecVou());
                    compag.setCompagVoucherCodAgencia(this.getnMpCodAge());
                    ///////////////////////////
                    pag.setCompag(compag);
                    Set<AdPago> l = new LinkedHashSet<>();
                    l.add(pag);
                    if (this.isnMpMoraSeleccion() && this.getnMpMoraPago() != 0) {
                        compag.setCompagMonto(this.getnMpMontoPago() + this.getnMpMoraPago());
                        var pag1 = new AdPago();
                        pag1.setPagFecha(new Date());
                        pag1.setAlutarId(this.getAlutarId());
                        pag1.setPagActivo("1");
                        pag1.setUsuId(this.pUsuID);//AQUI SE INGRESARA EL ID DEL USUARIO DE SESSION

                        var con1 = new AdConceptoPago();
                        con1.setId(Integer.parseInt(daoCatalogo.seleccionarValor(CAT_CONFIGURACION_MORA_ID)));
                        pag1.setConpag(con1);
                        pag1.setCompag(compag);
                        pag1.setPagMonto(this.getnMpMoraPago());
                        l.add(pag1);
                    }
                    if (this.isnMpMatExtempoSeleccion() && this.getnMpMatExtempo() != 0) {
                        /* para la captura de monto de un concepto de pagos*/
                        compag.setCompagMonto(compag.getCompagMonto() + this.getnMpMatExtempo());
                        var pag1 = new AdPago();
                        pag1.setPagFecha(new Date());
                        pag1.setAlutarId(this.getAlutarId());
                        pag1.setPagActivo("1");
                        pag1.setUsuId(this.pUsuID);
                        var con1 = new AdConceptoPago();
                        con1.setId(Integer.parseInt(daoCatalogo.seleccionarValor(CAT_CONFIGURACION_MATRICULA_EXTEMPORANEA)));
                        pag1.setConpag(con1);
                        pag1.setCompag(compag);
                        pag1.setPagMonto(this.getnMpMatExtempo());
                        l.add(pag1);
                        /* Recuperar Monto y de Pagos de Carne*/
                    }
                    compag.setAdPagos(l);
                    int pagoId = daoPago.insertarComprobantePago(compag, true);
                    LOG.info("Payment Receipt saved {}", pagoId);
                    if (extra == 1) {
                        daoSaldo.insertarSaldo(sal);
                    }
                    // WP3 mora consistency (BUG-05/06; sigu_caja is the rule of record): mora is
                    // part of the student's debt, so covering the base amount while mora stays
                    // unpaid is a PARTIAL payment, never TOTAL. Paid = base payments + mora
                    // payments (both re-read post-insert); matrícula extemporánea remains a
                    // separate charge, excluded from both sides as before.
                    float basePaid = 0;
                    for (AdPago pago : daoPago.seleccionarMostrarPagos(this.getAlutarId())) {
                        if (pago.getCompag().getCompagTipo().equals(CAT_ESTADO_BOLETA_VALIDO)) {
                            basePaid += pago.getPagMonto();
                        }
                    }
                    float moraPaid = 0;
                    int moraConceptId = Integer.parseInt(daoCatalogo.seleccionarValor(CAT_CONFIGURACION_MORA_ID));
                    for (AdPago pago : daoPago.seleccionarPagosPorConcepto(this.getAlutarId(), moraConceptId)) {
                        moraPaid += pago.getPagMonto();
                    }
                    daoAlumnoTarifa.cambiarEstado(this.getAlutarId(), deriveEstadoTarifa(basePaid, moraPaid));
                    String alerta = mandarComprobante(pagoId);
                    this.setOncomplete("alert('El Pago se efectuo Correctamente');Richfaces.hideModalPanel('mp');" + alerta);
                }
            }
        }
    }

    /**
     * Payment status for the tariff being settled, via the shared PaymentStatusLifecycle rule:
     * total debt (tariff + mora accrued for this operation, nMpMoraPago) vs. what has been paid
     * against it (base + mora payments). Package-visible for tests.
     */
    String deriveEstadoTarifa(float basePaid, float moraPaid) {
        var totalDebt = BigDecimal.valueOf(this.getRowMonto()).add(BigDecimal.valueOf(this.getnMpMoraPago()));
        var totalPaid = BigDecimal.valueOf(basePaid).add(BigDecimal.valueOf(moraPaid));
        String estado = paymentStatusLifecycle.deriveStatus(totalDebt, totalPaid);
        LOG.debug("Tariff {} -> estado '{}': debt {} (incl. mora {}), paid {}",
                this.getAlutarId(), estado, totalDebt, this.getnMpMoraPago(), totalPaid);
        return estado;
    }

    public void asignarCurrentStudent(ActionEvent event) { // Alumno Actual
        LOG.debug("[{}]", event.getPhaseId());
        setTmpClientId(this.getAlumnoId());
        setnMpIdTipoCliente(CAT_TIPO_CLIENTE_ALUMNO_CP);
        setnMpRazonSocial(this.getRowNombres());
        setnMpTipoPersNatJur(CAT_TIPO_RAZON_SOCIAL_NATURAL);
        setnMpDniRuc(this.getAlutarDniRuc());
    }

    public void asignarNewExternalClient(ActionEvent event) { // Nuevo Cliente Externo
        LOG.debug("[{}][{}]", event.getPhaseId(), nValorNuevo);
        setTmpClientId(0);
        setnMpIdTipoCliente(CAT_TIPO_CLIENTE_EXTERNO);
        setnMpTipoPersNatJur("0");
        setnMpRazonSocial("");
        setnMpDniRuc("");
        if (nValorNuevo == 1) {
            setnMpCodAlu("");
            setRowNombres("");
            setnMpSaldo(0);
        }
    }

    public void seleccionarClienteNuevo(ActionEvent event) {
        LOG.debug("[{}] By customer type '{}'", event.getPhaseId(), this.getnMpIdTipoCliente());
        this.setCustomerSearchType(CustomerSearchType.REGISTRO_NUEVO_PAGO_COMBO);
        this.setXuno("");
        this.setSelCli("");
        setTmpClientId(0);
        setnMpTipoPersNatJur("0");
        setnMpRazonSocial("");
        setnMpDniRuc("");
        listaCliente = new ArrayList<>();
        if ("0".equals(this.getnMpIdTipoCliente())) {
            this.setSelCli("");
        } else {
            this.setXunoy(CAT_TIPO_CLIENTE_EXTERNO.equals(this.getnMpIdTipoCliente()) ?
                    "Razón Social:" : "Ap. Paterno:");
            this.setSelCli("Richfaces.showModalPanel('mpCustomerSearch',{top:200})");
        }
    }

    public void funxuno(ActionEvent event) {
        LOG.debug("[{}] Looking for customer '{}' from '{}'", event.getPhaseId(), this.getXuno(), this.getCustomerSearchType());
        this.setOncomplete2("");
        if (this.getXuno().trim().isEmpty()) {
            this.setOncomplete2("alert('Ingrese valor de busqueda');");
        } else {
            String type;
            if (CustomerSearchType.REGISTRO_NUEVO_PAGO_COMBO == this.getCustomerSearchType()) {
                type = this.getnMpIdTipoCliente();
            } else {
                type = CAT_TIPO_CLIENTE_ALUMNO_CP;
            }
            listaCliente.clear();
            if (CAT_TIPO_CLIENTE_ALUMNO_CP.equals(type)) {
                LOG.trace("Buscar por Alumno Regular");
                List<AcAlumno> lista = daoAlumno.seleccionarAlumnoCliente(this.getXuno());
                for (AcAlumno acAlumno : lista) {
                    var aluCli = new MbPago();
                    aluCli.setAlumnoId(acAlumno.getId());
                    aluCli.setClienteId(acAlumno.getId());
                    aluCli.setClienteCodigo(acAlumno.getAluCodigo());
                    aluCli.setClienteDniRuc(acAlumno.getAluDni());
                    aluCli.setClienteNombre(acAlumno.getAluAppaterno() + " " + acAlumno.getAluApmaterno() + " " + acAlumno.getAluNombres());
                    aluCli.setClienteRazon(CAT_TIPO_RAZON_SOCIAL_NATURAL);
                    aluCli.setClienteSaldo(0);
                    listaCliente.add(aluCli);
                }
                this.setListaCliente(listaCliente);
            } else if (CAT_TIPO_CLIENTE_EXTERNO.equals(type)) {
                LOG.trace("Buscar por Cliente Externo");
                List<AdClientes> lista = daoCliente.seleccionarCliente(this.getXuno());
                for (AdClientes adClientes : lista) {
                    var aluCli = new MbPago();
                    aluCli.setClienteId(adClientes.getId());
                    aluCli.setClienteCodigo("0000000");
                    aluCli.setClienteDniRuc(adClientes.getCliRuc());
                    aluCli.setClienteNombre(adClientes.getCliRazonSocial());
                    aluCli.setClienteRazon(adClientes.getCliTipo());
                    aluCli.setClienteSaldo(0);
                    listaCliente.add(aluCli);
                }
                this.setListaCliente(listaCliente);
            } else {
                LOG.trace("Buscar por Alumno Libre");
                List<ClAlumno> lista = daoAlumnoLibre.seleccionarAlumnosPorApellidos(this.getXuno());
                listaCliente.addAll(lista.stream().map(alu -> {
                    var b = new MbPago();
                    b.setAlumnoId(alu.getAluId());
                    b.setClienteId(alu.getAluId());
                    b.setClienteCodigo(alu.getAluCodigo());
                    b.setClienteDniRuc(alu.getAluDni());
                    b.setClienteNombre(alu.getAluAppaterno() + " " + alu.getAluApmaterno() + " " + alu.getAluNombres());
                    b.setClienteRazon(CAT_TIPO_RAZON_SOCIAL_NATURAL);
                    b.setClienteSaldo(0);
                    return b;
                }).collect(Collectors.toList()));
            }
            LOG.info("Total of students as customers {}", listaCliente.size());
        }
    }

    public void asignar(ActionEvent event) {
        MbPago row = (MbPago) event.getComponent().getAttributes().get("selected_cli");
        clienteId = row.getClienteId();
        clienteNombre = row.getClienteNombre();
        clienteRazon = row.getClienteRazon();
        clienteDniRuc = row.getClienteDniRuc();
        clienteSaldo = row.getClienteSaldo();
        clienteCodigo = row.getClienteCodigo();
        LOG.debug("[{}][{}] Client {} | Name '{}' | Razon Social '{}'", event.getPhaseId(), customerSearchType,
                clienteId, clienteNombre, clienteRazon);
        if (CustomerSearchType.REGISTRO_NUEVO_PAGO_COMBO == this.getCustomerSearchType()) {
            this.setTmpClientId(clienteId);
            this.setnMpRazonSocial(clienteNombre);
            this.setnMpTipoPersNatJur(clienteRazon);
            this.setnMpDniRuc(clienteDniRuc);
            if (this.getnValorNuevo() == 1) { // Nuevo Pago Alumno CP/Externo
                this.setnMpSaldo(clienteSaldo);
                if (!CAT_TIPO_CLIENTE_EXTERNO.equals(nMpIdTipoCliente)) {
                    this.setnMpCodAlu(clienteCodigo);
                    this.setRowNombres(clienteNombre);
                }
                this.setAlumnoId(row.getAlumnoId());
            }
        } else {
            this.setnMpCodAlu(clienteCodigo);
            this.setRowNombres(clienteNombre);
            this.setAlumnoId(row.getAlumnoId());
        }
        loadSaldoOfStudent(event);
        this.setXuno("");
    }

    public void insertarNuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var extra = 0;
        var varG = 1;
        int verId;
        var fecha = new Date();
        Float pagMonto = null;
        var verComPago = "0";
        var sal = new AdSaldo();
        if (this.getnMpIdTipoCliente().equals("0") ||
                this.getnMpRazonSocial().trim().isEmpty() ||
                this.getnMpTipoPersNatJur().equals("0") ||
                this.getnMpProcedenciaId().equals("0") ||
                this.getnMpMontoPago() < 0 ||
                this.getnMpFechaComprobante() == null ||
                this.getnMpNumComprobante().trim().isEmpty()) {
            this.setNoncomplete(ALERT_REQUIRED_FIELDS);
        } else {
            if (this.getTmpClientId() == 0) {
                verId = daoCliente.seleccionarUnCliente(this.getnMpRazonSocial());
                if (verId == 0) {
                    var nueCli = new AdClientes();
                    nueCli.setId(0);
                    nueCli.setCliRuc(this.getnMpDniRuc());
                    nueCli.setCliRazonSocial(this.getnMpRazonSocial());
                    nueCli.setCliDireccion("");
                    nueCli.setCliTelefono("");
                    nueCli.setCliMail("");
                    nueCli.setCliTipo(this.getnMpTipoPersNatJur());
                    nueCli.setCliActivo("1");
                    daoCliente.insertarCliente(nueCli);
                    verId = nueCli.getId();
                    LOG.info("Client saved {}", verId);
                }
            } else {
                verId = this.getTmpClientId();
            }
            String numero = this.getnMpNumSerie() + "-" + this.getnMpNumComprobante();
            LOG.trace("Nro comprobante to use: {}", numero);
            if (CAT_PROCEDENCIA_PAGO_VOUCHER.equals(this.getnMpProcedenciaId())) {
                verComPago = daoPago.seleccionarUnComprobantePago(numero);
                if (!verComPago.equalsIgnoreCase("0")) {
                    this.setNoncomplete("alert('El Comprobante con Numero " + verComPago + " ya fue usado')");
                } else {
                    fecha = this.getnMpFechaComprobante();
                    pagMonto = this.getnMpMontoPago();
                    if (this.getnMpFecVou() == null ||
                            this.getnMpNumVou().trim().length() == 0 ||
                            this.getnMpCodAge().trim().length() == 0) {
                        this.setNoncomplete("alert('Ingrese los campos faltantes')");
                        varG = 0;
                    }
                }
            }
            if (verId != 0 && verComPago.equals("0")) {
                switch (this.getnMpProcedenciaId()) {
                    case CAT_PROCEDENCIA_PAGO_EFECTIVO:
                        fecha = this.getnMpFechaComprobante();
                        if (this.getnMpMontoPago() == 0) {
                            varG = 0;
                            this.setNoncomplete("alert('Ingrese correctamete el monto a pagar.')");
                        }
                        pagMonto = this.getnMpMontoPago();
                        break;
                    case CAT_PROCEDENCIA_PAGO_VALE:
                        fecha = this.getnMpFechaComprobante();
                        pagMonto = this.getnMpMontoPago();
                        break;
                    case CAT_PROCEDENCIA_PAGO_SALDO:
                        fecha = new Date();
                        numero = "SAL0000000000";
                        if (this.getnMpMontoPago() > this.getnMpSaldo()) {
                            this.setNoncomplete("alert('Ud No cuenta con saldo suficiente para Concretar este Pago')");
                            varG = 0;
                        } else {
                            pagMonto = this.getnMpMontoPago();
                            sal = new AdSaldo();
                            float resta = -1 * this.getnMpMontoPago();
                            sal.setSalMonto(resta);
                            sal.setSalFechaIngresoMonto(new Date());
                            sal.setSalCreacion(new Date());
                            var alu = new AcAlumno();
                            alu.setId(alumnoId);
                            sal.setAlu(alu);
                            extra = 1;
                        }
                        break;
                    default:
                        pagMonto = this.getnMpMontoPago();
                        break;
                }

                //-----------
                if (varG == 1) {
                    var pag = new AdPago();
                    pag.setPagMonto(pagMonto);
                    pag.setPagFecha(new Date());
                    pag.setAlutarId(0); // PAGO EXTERNO
                    pag.setPagActivo("1");
                    pag.setUsuId(this.pUsuID);//AQUI SE INGRESARA EL ID DEL USUARIO DE SESSION

                    var con = new AdConceptoPago();
                    con.setId(this.getnConPagId());
                    pag.setConpag(con);
                    var compag = new AdComprobantePago();
                    compag.setCompagTipo(CAT_ESTADO_BOLETA_VALIDO);
                    compag.setCompagDes(this.getnMpDescription());
                    compag.setCompagAlu(this.getAlumnoId());
                    compag.setCompagCliente(verId);
                    compag.setCompagClienteTipo(this.getnMpIdTipoCliente());
                    compag.setCompagUsado("1");
                    compag.setCompagProcedencia(this.getnMpProcedenciaId());
                    compag.setCompagCreacion(new Date());
                    compag.setCompagActivo("1");
                    compag.setCompagMonto(this.getnMpMontoPago());
                    compag.setCompagFecha(fecha);
                    compag.setCompagNro(numero);

                    compag.setCompagTipDoc(this.getnMpTipoComprPago());
                    compag.setCompagVoucherFecha(this.getnMpFecVou());
                    compag.setCompagVoucherCodAgencia(this.getnMpCodAge());
                    compag.setCompagVoucherNroOperacion(this.getnMpNumVou());

                    pag.setCompag(compag);
                    compag.setAdPagos(Collections.singleton(pag));
                    this.zcComprobanteId = daoPago.insertarComprobantePago(compag, true);
                    LOG.info("Payment Receipt saved {}", zcComprobanteId);
                    if (extra == 1) {
                        daoSaldo.insertarSaldo(sal);
                    }
                    String alerta = mandarComprobante(zcComprobanteId);
                    this.setNoncomplete("alert('El Pago se efectuo Correctamente');Richfaces.hideModalPanel('mpNewPayments');" + alerta);
                }
            }
        }
    }

    public void nSeleccionarClienteNuevo(ActionEvent event) {
        LOG.debug("[{}][{}][{}]", event.getPhaseId(), customerSearchType, nValorNuevo);
        this.setCustomerSearchType(CustomerSearchType.ESTUDIANTE);
        this.setXunoy("Ap. Paterno:");
        this.setXuno("");
        this.setSelCli("");
        setnMpCodAlu("");
        setRowNombres("");
        setAlumnoId(0);
        listaCliente = new ArrayList<>();
        this.setSelCli("Richfaces.showModalPanel('mpCustomerSearch',{top:200})");
    }

    public String getParamAluApMaterno() {
        return paramAluApMaterno;
    }

    public void setParamAluApMaterno(String materno) {
        paramAluApMaterno = materno;
    }

    public String getParamAluApPaterno() {
        return paramAluApPaterno;
    }

    public void setParamAluApPaterno(String paterno) {
        paramAluApPaterno = paterno;
    }

    public void personalizar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        setnValorNuevo(0);
        setTmpClientId(0);
        setnMpTipoPersNatJur("0");
        setnMpRazonSocial("");
        setnMpDniRuc("");
        setnMpIdTipoCliente("0");
        setnMpProcedenciaId("0");
        setnMpNumComprobante("");
        setnMpFechaComprobante(null);
        setnMpMontoPago(0);
        setnMpMoraPago(0);
        MbPago row = (MbPago) event.getComponent().getAttributes().get("alu_s");
        alutarId = row.getAlutarId();
        alumnoId = row.getAlumnoId();
        conceptoPagoId = row.getConceptoPagoId();
        estructPagoDetId = row.getEstructPagoDetId();
        rowSemestreOrNroComp = row.getRowSemestreOrNroComp();
        facultad = row.getFacultad();
        rowEspecialidadOrTipo = row.getRowEspecialidadOrTipo();
        rowCodigoAlu = row.getRowCodigoAlu();
        rowNombres = row.getRowNombres();
        conceptoPagoDesc = row.getConceptoPagoDesc();
        rowConceptoPago = row.getRowConceptoPago();
        rowMonto = row.getRowMonto();
        saldo = row.getSaldo();
        rowAdeuda = row.getRowAdeuda();
        alutarDniRuc = row.getAlutarDniRuc();

        alutarFecPago = row.getAlutarFecPago();
        rowFecProOrFecComprPago = row.getRowFecProOrFecComprPago();

        this.setnMpMontoPago(rowAdeuda);
    }

    public void savePersonalization(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getAlutarFecPago() == null ||
                this.getRowFecProOrFecComprPago() == null ||
                this.getRowMonto() < 0f) {
            this.setOncomplete(ALERT_REQUIRED_FIELDS);
        } else {
            var alutar = new AdAlumnoTarifa();
            var alu = new AcAlumno();
            alu.setId(this.getAlumnoId());

            var conP = new AdConceptoPago();
            conP.setId(this.getConceptoPagoId());

            alutar.setId(alutarId);
            var estpagDet = new AdEstructuraPagosDetalle();
            estpagDet.setId(this.getEstructPagoDetId());
            alutar.setEstpagdet(estpagDet);
            alutar.setAlu(alu);
            alutar.setConpag(conP);
            alutar.setAlutarMonto(this.getRowMonto());
            alutar.setAlutarFechaPago(this.getAlutarFecPago());
            alutar.setAlutarFechaProrroga(this.getRowFecProOrFecComprPago());
            alutar.setAlutarEstado("0");
            alutar.setAlutarActivo("1");
            daoAlumnoTarifa.actualizarAlumnoTarifa(alutar);
            this.setOncomplete("alert('La Actualizacion se efectuo Corectamente'),Richfaces.hideModalPanel('mpx2')");
        }
    }

    public void editarComprobante(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        limpiarCampos();
        MbPago row = (MbPago) event.getComponent().getAttributes().get("zc_comprobante");
        if (row.getComprobanteOculto() == 0) {
            LOG.debug("Como interno");
            this.setZcComprobanteId(Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_z")).getValue().toString()));
            Object objNombreZ = ((UIParameter) event.getComponent().findComponent("nombre_z")).getValue();
            this.setZcNombre(Objects.toString(objNombreZ, ""));
            this.setZcNumeroComprobante(((UIParameter) event.getComponent().findComponent("num_com_z")).getValue().toString());
            this.setZcMonto(Float.parseFloat(((UIParameter) event.getComponent().findComponent("monto_z")).getValue().toString()));
            this.setZcConceptoPago(((UIParameter) event.getComponent().findComponent("con_pag_z")).getValue().toString());
            this.setZcConceptoPagoDetalle(((UIParameter) event.getComponent().findComponent("con_pag_det_z")).getValue().toString());
            this.setZcProcedenciaPagoId(((UIParameter) event.getComponent().findComponent("id_tipo_com_z")).getValue().toString());

            Object objTipComZ = ((UIParameter) event.getComponent().findComponent("tip_com_z")).getValue();
            this.setZcTipoComprobanteId(Objects.toString(objTipComZ, ""));
            this.setZcNumVoucher(((UIParameter) event.getComponent().findComponent("num_vou_z")).getValue().toString());
            this.setZcCodigoAgencia(((UIParameter) event.getComponent().findComponent("cod_age_z")).getValue().toString());

            Date fecha1 = helper.parse(((UIParameter) event.getComponent().findComponent("fecha_pag_z")).getValue().toString());
            Object objFecVouZ = ((UIParameter) event.getComponent().findComponent("fec_vou_z")).getValue();
            Date fecha2 = objFecVouZ == null ? null : helper.parse(objFecVouZ.toString());
            zcFechaPago = fecha1;
            zcFecVoucher = fecha2;
            this.setZcFechaPago(zcFechaPago);
            this.setZcFecVoucher(zcFecVoucher);
        } else {
            LOG.debug("Como externo");
            this.setZcComprobanteId(row.getComprobanteOculto());
            this.setZcNumeroComprobante(row.getNroComprobanteOculto());
            this.setZcProcedenciaPagoId(CAT_PROCEDENCIA_PAGO_EFECTIVO);
            this.setZcFechaPago(row.getComprobanteFechaOculto());

            this.setZcNombre(row.getComprobanteNombreOculto());
            this.setZcMonto(row.getComprobanteMontoOculto());
            this.setZcConceptoPago(row.getComprobanteConceptoOculto());
            this.setZcConceptoPagoDetalle(row.getComprobanteConceptoDetOculto());
        }
    }

    public void actualizarComprobantePago(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        int permiso;
        if (this.getZcNumeroComprobante().trim().length() == 0 ||
                this.getZcFechaPago() == null ||
                this.getZcTipoComprobanteId().trim().length() == 0) {
            permiso = 0;
            this.setOncomplete(ALERT_REQUIRED_FIELDS);
        } else {
            if (this.getZcProcedenciaPagoId().equals(CAT_PROCEDENCIA_PAGO_SALDO)) {
                permiso = 0;
                this.setOncomplete("alert('No esta permitido el Cambio de Procedencia a Saldos')");
            } else if (this.getZcProcedenciaPagoId().equals(CAT_PROCEDENCIA_PAGO_VOUCHER)) {
                if (this.getZcNumVoucher().trim().length() == 0 ||
                        this.getZcFecVoucher() == null ||
                        this.getZcCodigoAgencia().trim().length() == 0) {
                    permiso = 0;
                    this.setOncomplete(ALERT_REQUIRED_FIELDS);
                } else {
                    permiso = 1;
                }
            } else {
                permiso = 1;
            }
        }
        if (permiso == 1) {
            var compag = new AdComprobantePago();
            compag.setId(this.zcComprobanteId);
            compag.setCompagNro(this.zcNumeroComprobante);
            compag.setCompagFecha(this.zcFechaPago);
            compag.setCompagProcedencia(this.zcProcedenciaPagoId);
            compag.setCompagTipDoc(this.zcTipoComprobanteId);
            compag.setCompagVoucherFecha(this.zcFecVoucher);
            compag.setCompagVoucherCodAgencia(this.zcCodigoAgencia);
            compag.setCompagVoucherNroOperacion(this.zcNumVoucher);

            daoPago.actualizarComprobantePago(compag);
            LOG.info("Payment Receipt updated {}", zcComprobanteId);
            this.setOncomplete("alert('La Actualizacion se efectuo Correctamente'),Richfaces.hideModalPanel('mpx4'),Richfaces.hideModalPanel('mpx3')");
        }
    }

    public void limpiarCampos() {
        this.zcTipoComprobanteId = CAT_TIPO_COMPROBANTE_PAGO_BOLETA;
        this.zcNumVoucher = "";
        this.zcFecVoucher = null;
        this.zcCodigoAgencia = "";
    }

    public String getXunoy() {
        return xunoy;
    }

    public void setXunoy(String xunoy) {
        this.xunoy = xunoy;
    }

    public String getXuno() {
        return xuno;
    }

    public void setXuno(String xuno) {
        this.xuno = xuno;
    }

    public List<SelectItem> getComboCatalogoReciboBloque() {
        return comboCatalogoReciboBloque;
    }

    public void setComboCatalogoReciboBloque(List<SelectItem> comboCatalogoReciboBloque) {
        this.comboCatalogoReciboBloque = comboCatalogoReciboBloque;
    }

    public boolean isMostrarAccionNuevo() {
        return mostrarAccionNuevo;
    }

    public void setMostrarAccionNuevo(boolean mostrarAccionNuevo) {
        this.mostrarAccionNuevo = mostrarAccionNuevo;
    }

    public String getCabecera1() {
        return cabecera1;
    }

    public void setCabecera1(String cabecera1) {
        this.cabecera1 = cabecera1;
    }

    public String getCabecera2() {
        return cabecera2;
    }

    public void setCabecera2(String cabecera2) {
        this.cabecera2 = cabecera2;
    }

    public String getCabecera3() {
        return cabecera3;
    }

    public void setCabecera3(String cabecera3) {
        this.cabecera3 = cabecera3;
    }

    public String getCabecera4() {
        return cabecera4;
    }

    public void setCabecera4(String cabecera4) {
        this.cabecera4 = cabecera4;
    }

    public boolean isViewDetail() {
        return viewDetail;
    }

    public void setViewDetail(boolean viewDetail) {
        this.viewDetail = viewDetail;
    }

    public float getnMpMoraPago() {
        return nMpMoraPago;
    }

    public void setnMpMoraPago(float nMpMoraPago) {
        this.nMpMoraPago = nMpMoraPago;
    }

    public boolean isnMpMoraSeleccion() {
        return nMpMoraSeleccion;
    }

    public void setnMpMoraSeleccion(boolean nMpMoraSeleccion) {
        this.nMpMoraSeleccion = nMpMoraSeleccion;
    }

    public boolean isMostrarAccionPersonalizar() {
        return mostrarAccionPersonalizar;
    }

    public void setMostrarAccionPersonalizar(boolean mostrarAccionPersonalizar) {
        this.mostrarAccionPersonalizar = mostrarAccionPersonalizar;
    }

    public int getComprobanteOculto() {
        return comprobanteOculto;
    }

    public void setComprobanteOculto(int comprobanteOculto) {
        this.comprobanteOculto = comprobanteOculto;
    }

    public String getNroComprobanteOculto() {
        return nroComprobanteOculto;
    }

    public void setNroComprobanteOculto(String nroComprobanteOculto) {
        this.nroComprobanteOculto = nroComprobanteOculto;
    }

    public Date getComprobanteFechaOculto() {
        return comprobanteFechaOculto;
    }

    public void setComprobanteFechaOculto(Date comprobanteFechaOculto) {
        this.comprobanteFechaOculto = comprobanteFechaOculto;
    }

    public String getComprobanteNombreOculto() {
        return comprobanteNombreOculto;
    }

    public void setComprobanteNombreOculto(String comprobanteNombreOculto) {
        this.comprobanteNombreOculto = comprobanteNombreOculto;
    }

    public float getComprobanteMontoOculto() {
        return comprobanteMontoOculto;
    }

    public void setComprobanteMontoOculto(float comprobanteMontoOculto) {
        this.comprobanteMontoOculto = comprobanteMontoOculto;
    }

    public String getComprobanteConceptoOculto() {
        return comprobanteConceptoOculto;
    }

    public void setComprobanteConceptoOculto(String comprobanteConceptoOculto) {
        this.comprobanteConceptoOculto = comprobanteConceptoOculto;
    }

    public String getComprobanteConceptoDetOculto() {
        return comprobanteConceptoDetOculto;
    }

    public void setComprobanteConceptoDetOculto(String comprobanteConceptoDetOculto) {
        this.comprobanteConceptoDetOculto = comprobanteConceptoDetOculto;
    }

    public void anularPago(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        int compagId;
        if (event.getComponent().findComponent("o1") == null) {
            compagId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_z")).getValue().toString());
        } else {
            compagId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("o1")).getValue().toString());
        }
        daoPago.anularComprobantePago(compagId);
        cambiarEstados(compagId);
    }

    public void eliminarPago(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        int compagId;
        if (event.getComponent().findComponent("o1") == null) {
            compagId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_z")).getValue().toString());
        } else {
            compagId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("o1")).getValue().toString());
        }
        cambiarEstados(compagId);
        daoPago.eliminarComprobantePago(compagId);
    }

    public void cambiarEstados(int compagId) {
        Set<AdPago> adPagos = daoPago.seleccionarUnComprobantePago(compagId).getAdPagos();
        List<AdPago> list = Collections.synchronizedList(new LinkedList<>(adPagos));
        for (AdPago adPago : list) {
            daoAlumnoTarifa.cambiarEstado(adPago.getAlutarId(), "0");
        }
    }

    public String getDetTipoPago() {
        return detTipoPago;
    }

    public void setDetTipoPago(String detTipoPago) {
        this.detTipoPago = detTipoPago;
    }

    public String getComprobanteTipo() {
        return comprobanteTipo;
    }

    public void setComprobanteTipo(String comprobanteTipo) {
        this.comprobanteTipo = comprobanteTipo;
    }

    public boolean isMostrarFicha() {
        return mostrarFicha;
    }

    public void setMostrarFicha(boolean mostrarFicha) {
        this.mostrarFicha = mostrarFicha;
    }

    public CustomerSearchType getCustomerSearchType() {
        return customerSearchType;
    }

    public void setCustomerSearchType(CustomerSearchType customerSearchType) {
        this.customerSearchType = customerSearchType;
    }

    public float getnMpMatExtempo() {
        return nMpMatExtempo;
    }

    public void setnMpMatExtempo(float nMpMatExtempo) {
        this.nMpMatExtempo = nMpMatExtempo;
    }

    public boolean isnMpMatExtempoSeleccion() {
        return nMpMatExtempoSeleccion;
    }

    public void setnMpMatExtempoSeleccion(boolean nMpMatExtempoSeleccion) {
        this.nMpMatExtempoSeleccion = nMpMatExtempoSeleccion;
    }

    public List<SelectItem> getComboTiposComprobantePago() {
        return comboTiposComprobantePago;
    }

    public void setComboTiposComprobantePago(List<SelectItem> comboTiposComprobantePago) {
        this.comboTiposComprobantePago = comboTiposComprobantePago;
    }

    public Date getZcFecVoucher() {
        return zcFecVoucher;
    }

    public void setZcFecVoucher(Date zcFecVoucher) {
        this.zcFecVoucher = zcFecVoucher;
    }

    public String getZcNumVoucher() {
        return zcNumVoucher;
    }

    public void setZcNumVoucher(String zcNumVoucher) {
        this.zcNumVoucher = zcNumVoucher;
    }

    public String getZcCodigoAgencia() {
        return zcCodigoAgencia;
    }

    public void setZcCodigoAgencia(String zcCodigoAgencia) {
        this.zcCodigoAgencia = zcCodigoAgencia;
    }

    public String getZcTipoComprobanteId() {
        return zcTipoComprobanteId;
    }

    public void setZcTipoComprobanteId(String zcTipoComprobanteId) {
        this.zcTipoComprobanteId = zcTipoComprobanteId;
    }

    public String getDetCompagTipoDoc() {
        return detCompagTipoDoc;
    }

    public void setDetCompagTipoDoc(String detCompagTipoDoc) {
        this.detCompagTipoDoc = detCompagTipoDoc;
    }

    public String getDetVoucherNroOper() {
        return detVoucherNroOper;
    }

    public void setDetVoucherNroOper(String detVoucherNroOper) {
        this.detVoucherNroOper = detVoucherNroOper;
    }

    public Date getDetVoucherFecha() {
        return detVoucherFecha;
    }

    public void setDetVoucherFecha(Date detVoucherFecha) {
        this.detVoucherFecha = detVoucherFecha;
    }

    public String getDetVoucherCodAgencia() {
        return detVoucherCodAgencia;
    }

    public void setDetVoucherCodAgencia(String detVoucherCodAgencia) {
        this.detVoucherCodAgencia = detVoucherCodAgencia;
    }

    public String getnMpTipoComprPago() {
        return nMpTipoComprPago;
    }

    public void setnMpTipoComprPago(String nMpTipoComprPago) {
        this.nMpTipoComprPago = nMpTipoComprPago;
    }

    public Date getnMpFecVou() {
        return nMpFecVou;
    }

    public void setnMpFecVou(Date nMpFecVou) {
        this.nMpFecVou = nMpFecVou;
    }

    public String getnMpNumVou() {
        return nMpNumVou;
    }

    public void setnMpNumVou(String nMpNumVou) {
        this.nMpNumVou = nMpNumVou;
    }

    public String getnMpCodAge() {
        return nMpCodAge;
    }

    public void setnMpCodAge(String nMpCodAge) {
        this.nMpCodAge = nMpCodAge;
    }

    public void matExtemporanea(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        alumnoId = Integer.parseInt(event.getComponent().getAttributes().get(PARAM_ALUMNO_ID).toString());

        this.setZcNumeroComprobante("");

        this.setZcTipoComprobanteId(CAT_TIPO_COMPROBANTE_PAGO_BOLETA);

        this.setZcFechaPago(new Date());

        this.setZcMonto(daoConceptoPago.seleccionarUnaConceptoPago(154).getConpagMonto());
    }

    public void pagarMatExtemporanea(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getZcNumeroComprobante().trim().isEmpty()) {
            this.setOncomplete("alert('Ingrese Nro de Comprobante');");
        } else {
            float pagMonto = this.getZcMonto();

            var pag = new AdPago();

            pag.setPagMonto(pagMonto);
            pag.setPagFecha(new Date());
            pag.setAlutarId(0);
            pag.setPagActivo("1");

            pag.setUsuId(this.pUsuID);

            var con = new AdConceptoPago();
            con.setId(154);
            pag.setConpag(con);

            var compag = new AdComprobantePago();
            compag.setCompagTipo(CAT_ESTADO_BOLETA_VALIDO);
            compag.setCompagDes("");
            compag.setCompagAlu(0);
            compag.setCompagCliente(alumnoId);
            compag.setCompagClienteTipo(CAT_TIPO_CLIENTE_ALUMNO_CP);
            compag.setCompagUsado("1");
            compag.setCompagProcedencia(CAT_PROCEDENCIA_PAGO_EFECTIVO);
            compag.setCompagCreacion(new Date());
            compag.setCompagActivo("1");
            compag.setCompagMonto(pagMonto);
            compag.setCompagFecha(this.getZcFechaPago());
            compag.setCompagNro(this.getZcNumeroComprobante());
            compag.setCompagTipDoc(this.getZcTipoComprobanteId());
            pag.setCompag(compag);

            compag.setAdPagos(Collections.singleton(pag));

            int x = daoPago.insertarComprobantePago(compag);
            String alerta = mandarComprobante(x);

            this.setOncomplete("alert('El Pago se efectuo Correctamente.');Richfaces.hideModalPanel('modal2');" + alerta);
        }
    }

    public void imprimirListado(ActionEvent event) {
        LOG.debug("[{}] Filters for report {} | {}", event.getPhaseId(), this.paramFechaDesde, this.paramFechaHasta);
        if (this.getParamFechaDesde() == null || this.getParamFechaHasta() == null) {
            this.setAlertaCaja("alert('Debe ingresar las fechas para imprimir el reporte.')");
        } else {
            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            String fecIni = helper.format(this.getParamFechaDesde());
            String fecFin = helper.format(this.getParamFechaHasta());
            String url = contextPath + "/report?type=cuadre-caja&fecIni=" + fecIni + "&fecFin=" + fecFin + "&usuId=" + pUsuID;
            this.setAlertaCaja("window.open('" + url + "', '_blank');");
        }
    }

    public void imprimirComprobante(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_z");
        var idComprobante = Integer.parseInt(id.getValue().toString());
        String script = mandarComprobante(idComprobante);
        this.setJsImpresion(script);
    }

    /**
     * Builds the trigger script for the printable comprobante. The HTML receipt itself is rendered
     * by {@code ComprobanteServlet} so the browser opens it directly in a new tab and prints it from
     * there, instead of the legacy {@code document.write(...)} blob shipped inline as JavaScript.
     */
    public String mandarComprobante(int comprobanteId) {
        LOG.debug("To print {}", comprobanteId);
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return "window.open('" + contextPath + "/comprobante?id=" + comprobanteId + "', '_blank');";
    }

    public String getAlertaCaja() {
        return alertaCaja;
    }

    public void setAlertaCaja(String alertaCaja) {
        this.alertaCaja = alertaCaja;
    }

    public String getJsImpresion() {
        return jsImpresion;
    }

    public void setJsImpresion(String jsImpresion) {
        this.jsImpresion = jsImpresion;
    }

    private enum CustomerSearchType {
        REGISTRO_NUEVO_PAGO_COMBO,
        ESTUDIANTE
    }
}
