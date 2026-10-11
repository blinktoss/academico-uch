package net.uch.administrativa.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.administrativa.jsf.beans.BeanConceptoPago;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.uch.Stubs.*;
import static net.uch.common.Constants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 31/12/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbPagoTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private static boolean CONFIGURED = true;
    private MbPago manager;

    @AfterClass
    public static void cleanUpDatabase() {
        DAO.cleanAdSaldo();
    }

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        loggerUser.setRowUsuId(17);
        loggerUser.setNivel("0");

        when(externalContext.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(loggerUser);

        manager = new MbPago();
        manager.init();

        if (CONFIGURED) {
            DAO.insertAdSaldos();
            CONFIGURED = false;
        }
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] result = manager.getComboCatalogoListaPagos();
        assertEquals(3, result.length);

        // Default payment type (INTERNOS) excludes the 15 Curso Libre (007003) concepts: 268 - 15.
        result = manager.getComboConceptoPago();
        assertEquals(253, result.length);

        result = manager.getComboCatalogoEstado();
        assertEquals(3, result.length);
        assertNull(manager.getParamEstadoId());

        result = manager.getComboSemestre();
        assertNotEquals(0, result.length);
        assertEquals(0, manager.getParamSemestreId());

        result = manager.getComboFacultadBuscar();
        assertEquals(5, result.length);

        List<SelectItem> especialities = manager.getComboEspecialidadBuscar();
        assertTrue(especialities.isEmpty());

        manager.setParamFacId(Facultad.ENFERMERIA);
        manager.cambiarEspecialidadBuscar(actionEvent);
        especialities = manager.getComboEspecialidadBuscar();
        assertEquals(Facultad.ENFERMERIA, manager.getParamFacId());
        assertEquals(1, especialities.size());

        manager.setParamEspId(Integer.parseInt(especialities.get(0).getValue().toString()));
        assertEquals(Especialidad.ENFERMERIA, manager.getParamEspId());

        manager.setParamFacId(0);
        manager.cambiarEspecialidadBuscar(actionEvent);
        especialities = manager.getComboEspecialidadBuscar();
        assertTrue(especialities.isEmpty());
    }

    @Test
    public void initializationTest() {
        SelectItem[] result = manager.getComboCatalogoTipoCliente();
        assertEquals(3, result.length);

        result = manager.getComboCatalogoTipoPersNatJur();
        assertEquals(2, result.length);

        result = manager.getComboCatalogoProcedencia();
        assertEquals(7, result.length);

        List<SelectItem> resultL = manager.getComboCatalogoReciboBloque();
        assertEquals(4, resultL.size());

        assertFalse(manager.getProcedencias().isEmpty());
        assertFalse(manager.getEstadosBoleta().isEmpty());

        assertEquals("Semestre", manager.getCabecera1());
        assertEquals("Especialidad", manager.getCabecera2());
        assertEquals("F. de Pro.", manager.getCabecera3());
        assertEquals("Est.", manager.getCabecera4());
        assertTrue(manager.isViewDetail());

        assertTrue(manager.isMostrarAccionNuevo());
        assertTrue(manager.isMostrarFicha());
        assertTrue(manager.isMostrarAccionPersonalizar());
    }

    @Test
    public void seleccionarPagosInternosTest() {
        manager.setParamListaPagoId(1);

        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("");
        manager.setParamAluApMaterno("");
        manager.setParamEstadoId("-1");

        manager.seleccionar();
        assertEquals(manager.getPageSize(), manager.getListaAlumno().size()); // Default page size

        // ------------------------------------------------------------------
        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("QUISP");
        manager.setParamAluApMaterno("ME");
        manager.setParamConceptoPagoId(1); // Matricula
        manager.setParamSemestreId(0);
        manager.setParamFacId(0);
        manager.setParamEspId(0);

        manager.seleccionar();
        assertEquals(12, manager.getTotalCount());
        assertEquals(12, manager.getListaAlumno().size());

        // ------------------------------------------------------------------
        manager.setParamAluCodigo("09105155");
        manager.setParamAluApPaterno("");
        manager.setParamAluApMaterno("");
        manager.setParamConceptoPagoId(0);
        manager.setParamEstadoId("-1");
        manager.setParamSemestreId(SEMESTRE_2010_2_ID);
        manager.setParamFacId(0);
        manager.setParamEspId(0);

        manager.seleccionar();
        assertEquals(6, manager.getListaAlumno().size());

        // ------------------------------------------------------------------
        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("DIAZ");
        manager.setParamAluApMaterno("");
        manager.setParamConceptoPagoId(0);
        manager.setParamEstadoId("0"); // DEBE
        manager.setParamSemestreId(SEMESTRE_2010_1_ID);
        manager.setParamFacId(Facultad.EDUCACION_PRIMARIA);
        manager.setParamEspId(Especialidad.EDUCACION_PRIMARIA);

        manager.seleccionar();
        assertEquals(3, manager.getListaAlumno().size());
        assertFalse(manager.getListaAlumno().get(0).getRowEstadoOrComprPagoMonto().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getImagenP().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).isDisabledP());
        assertFalse(manager.getListaAlumno().get(0).getImagenP2().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).isDisabledP2());
    }

    @Test
    public void seleccionarPagosExternosTest() {
        manager.setParamListaPagoId(2);
        manager.setParamFechaDesde(Date.valueOf(LocalDate.of(2010, Month.MARCH, 1)));
        manager.setParamFechaHasta(Date.valueOf(LocalDate.of(2010, Month.MARCH, 1)));
        manager.setParamConceptoPagoId(0);

        manager.seleccionar();

        assertEquals(42, manager.getTotalCount());
        assertEquals(20, manager.getListaAlumno().size()); // first page bounded by pageSize (20)

        // ------------------------------------------------------------------
        manager.setParamFechaDesde(Date.valueOf(LocalDate.of(2008, Month.JUNE, 4)));
        manager.setParamFechaHasta(Date.valueOf(LocalDate.of(2008, Month.JUNE, 4)));
        manager.setParamConceptoPagoId(6); // Carnet de Biblioteca

        manager.seleccionar();

        assertEquals(6, manager.getListaAlumno().size());

        // ------------------------------------------------------------------
        manager.setParamFechaDesde(Date.valueOf(LocalDate.of(2010, Month.FEBRUARY, 2)));
        manager.setParamFechaHasta(Date.valueOf(LocalDate.of(2010, Month.FEBRUARY, 2)));
        manager.setParamConceptoPagoId(0); // Carnet de Biblioteca

        manager.seleccionar();

        assertEquals(34, manager.getTotalCount());
        assertEquals(20, manager.getListaAlumno().size()); // first page bounded by pageSize (20)
        assertFalse(manager.getListaAlumno().get(0).getComprobanteTipo().isEmpty());
        assertTrue(manager.isMostrarAccionesComprobante()); // voucher actions only on EXTERNOS rows
    }

    @Test
    public void seleccionarPagosExternosResolvesFreeCourseBeneficiaryTest() {
        // Voucher 10812 (2009-02-03) is a Curso Libre self-payment: compag_cliente_tipo = 014003 and
        // compag_alu = compag_cliente = 2573 with NO compag_alu_tipo (issue #49). cl_alumno 2573 is
        // FLORES VILCHES (the payer); ac_alumno 2573 is REMIGIO — an unrelated student the pre-guard
        // reader used to display because it resolved compag_alu blindly against ac_alumno.
        manager.setParamListaPagoId(2);
        manager.setParamFechaDesde(Date.valueOf(LocalDate.of(2009, Month.FEBRUARY, 3)));
        manager.setParamFechaHasta(Date.valueOf(LocalDate.of(2009, Month.FEBRUARY, 3)));
        manager.setParamConceptoPagoId(0);

        manager.seleccionar();

        MbPago row = manager.getListaAlumno().stream()
                .filter(r -> r.getComprobanteOculto() == 10812)
                .findFirst().orElseThrow(() -> new AssertionError("voucher 10812 not in the listing"));
        assertTrue("expected the CL student, got: " + row.getRowNombres(),
                row.getRowNombres().startsWith("FLORES VILCHES"));
        assertEquals("08152810", row.getRowCodigoAlu()); // cl_alumno code, not REMIGIO's 08030493
    }

    @Test
    public void seleccionarPagosCursosLibresTest() {
        manager.setParamListaPagoId(3);
        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("RAMIREZ");
        manager.setParamAluApMaterno("");
        manager.setParamConceptoPagoId(0);
        manager.setParamEstadoId("-1");

        manager.seleccionar();

        assertTrue(manager.getTotalCount() > 20);
        assertEquals(20, manager.getListaAlumno().size()); // first page bounded by pageSize (20)

        MbPago row = manager.getListaAlumno().get(0);
        assertFalse(row.getRowSemestreOrNroComp().isEmpty()); // Módulo
        assertFalse(row.getRowNombres().isEmpty());
        assertFalse(row.getRowCodigoAlu().isEmpty());
        assertFalse(row.getRowEstadoOrComprPagoMonto().isEmpty());

        // CL rows are tariffs: no expander sub-table and no voucher actions (collection is
        // deferred to UC-CSH-005) — both flags must keep those columns unrendered.
        assertFalse(manager.isViewDetail());
        assertFalse(manager.isMostrarAccionesComprobante());
    }

    @Test
    public void openSubTableTest() {
        manager.setFlag("0");
        manager.setAlutarId(2173);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("flag", manager.getFlag());
        attributes.put("id_x", manager.getAlutarId());

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        // opening subtable
        manager.verSubTabla(actionEvent);

        assertFalse(manager.getDetallesPagosInternos().get(1).getDetTipoPago().isEmpty());
        assertFalse(manager.getDetallesPagosInternos().get(1).getDetCompagTipoDoc().isEmpty());
        assertFalse(manager.getDetallesPagosInternos().get(1).getDetCompagTipoCompValue().isEmpty());
        assertFalse(manager.getDetallesPagosInternos().get(1).getDetVoucherNroOper().isEmpty());
        assertNotNull(manager.getDetallesPagosInternos().get(1).getDetVoucherFecha());
        assertFalse(manager.getDetallesPagosInternos().get(1).getDetVoucherCodAgencia().isEmpty());

        assertTrue(manager.isVer());
        assertEquals("1", manager.getFlag());
        assertTrue(manager.getImagen().contains("up.png"));
        assertEquals(2, manager.getDetallesPagosInternos().size());

        // closing subtable
        manager.setFlag("1");
        attributes.put("flag", manager.getFlag());

        manager.verSubTabla(actionEvent);

        assertFalse(manager.isVer());
        assertEquals("0", manager.getFlag());
        assertTrue(manager.getImagen().contains("down.png"));
    }

    @Test
    public void openingModalForPagoTest() {
        manager.setParamListaPagoId(1);
        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("ARENAS");
        manager.setParamAluApMaterno("");
        manager.setParamEspId(Especialidad.ING_SISTEMAS);
        manager.setParamConceptoPagoId(3); // Pensión Bloque A
        manager.setParamEstadoId("-1");
        manager.setParamSemestreId(SEMESTRE_2010_2_ID);
        manager.seleccionar();
        MbPago bean = manager.getListaAlumno().get(1);

        Map<String, Object> attributes = Collections.singletonMap("alu_s", bean);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.pagar(actionEvent);

        assertEquals(4, manager.getComboTiposComprobantePago().size());
        assertEquals(CAT_TIPO_COMPROBANTE_PAGO_BOLETA, manager.getnMpTipoComprPago());
        assertTrue(manager.getnMpNumVou().isEmpty());
        assertNotNull(manager.getnMpFecVou());
        assertTrue(manager.getnMpCodAge().isEmpty());
        assertNotEquals(0, manager.getnMpMoraPago(), 0);
        assertTrue(manager.isnMpMoraSeleccion());
        assertNotEquals(0, manager.getnMpMatExtempo(), 0);
        assertTrue(manager.isnMpMatExtempoSeleccion());
    }

    @Test
    public void editarComprobanteInternoTest() {
        manager.setDetCompagId(21818);
        manager.setDetNombrePagador("DIAZ SOTO SEGUNDO GABRIEL");
        manager.setDetCompagNro("002-0011418");
        manager.setDetCompagMonto(360);
        manager.setConceptoPagoDesc("PENSION");
        manager.setRowConceptoPago("1RA CUOTA DE 5");
        manager.setDetCompagTipoCompId(CAT_PROCEDENCIA_PAGO_EFECTIVO);
        manager.setDetCompagTipoDoc(CAT_PROCEDENCIA_PAGO_EFECTIVO);
        manager.setDetVoucherNroOper("002-0011418");
        manager.setDetVoucherCodAgencia("");
        manager.setDetCompagFechaPago(Date.valueOf(LocalDate.of(2009, Month.AUGUST, 31)));
        manager.setDetVoucherFecha(Date.valueOf(LocalDate.of(2009, Month.AUGUST, 31)));

        UIParameter uipId_z = mock(UIParameter.class);
        UIParameter uipNombre_z = mock(UIParameter.class);
        UIParameter uipNum_com_z = mock(UIParameter.class);
        UIParameter uipMonto_z = mock(UIParameter.class);
        UIParameter uipCon_pag_z = mock(UIParameter.class);
        UIParameter uipCon_pag_det_z = mock(UIParameter.class);
        UIParameter uipId_tipo_com_z = mock(UIParameter.class);
        UIParameter uipTip_com_z = mock(UIParameter.class);
        UIParameter uipNum_vou_z = mock(UIParameter.class);
        UIParameter uipCod_age_z = mock(UIParameter.class);
        UIParameter uipFecha_pag_z = mock(UIParameter.class);
        UIParameter uipFec_vou_z = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_z")).thenReturn(uipId_z);
        when(uipId_z.getValue()).thenReturn(manager.getDetCompagId());

        when(uiComponent.findComponent("nombre_z")).thenReturn(uipNombre_z);
        when(uipNombre_z.getValue()).thenReturn(manager.getDetNombrePagador());

        when(uiComponent.findComponent("num_com_z")).thenReturn(uipNum_com_z);
        when(uipNum_com_z.getValue()).thenReturn(manager.getDetCompagNro());

        when(uiComponent.findComponent("monto_z")).thenReturn(uipMonto_z);
        when(uipMonto_z.getValue()).thenReturn(manager.getDetCompagMonto());

        when(uiComponent.findComponent("con_pag_z")).thenReturn(uipCon_pag_z);
        when(uipCon_pag_z.getValue()).thenReturn(manager.getConceptoPagoDesc());

        when(uiComponent.findComponent("con_pag_det_z")).thenReturn(uipCon_pag_det_z);
        when(uipCon_pag_det_z.getValue()).thenReturn(manager.getRowConceptoPago());

        when(uiComponent.findComponent("id_tipo_com_z")).thenReturn(uipId_tipo_com_z);
        when(uipId_tipo_com_z.getValue()).thenReturn(manager.getDetCompagTipoCompId());

        when(uiComponent.findComponent("tip_com_z")).thenReturn(uipTip_com_z);
        when(uipTip_com_z.getValue()).thenReturn(manager.getDetCompagTipoDoc());

        when(uiComponent.findComponent("num_vou_z")).thenReturn(uipNum_vou_z);
        when(uipNum_vou_z.getValue()).thenReturn(manager.getDetVoucherNroOper());

        when(uiComponent.findComponent("cod_age_z")).thenReturn(uipCod_age_z);
        when(uipCod_age_z.getValue()).thenReturn(manager.getDetVoucherCodAgencia());

        when(uiComponent.findComponent("fecha_pag_z")).thenReturn(uipFecha_pag_z);
        when(uipFecha_pag_z.getValue()).thenReturn(manager.getDetCompagFechaPago());

        when(uiComponent.findComponent("fec_vou_z")).thenReturn(uipFec_vou_z);
        when(uipFec_vou_z.getValue()).thenReturn(manager.getDetVoucherFecha());

        MbPago row = new MbPago();
        Map<String, Object> attributes = Collections.singletonMap("zc_comprobante", row);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.editarComprobante(actionEvent);

        assertEquals(manager.getDetCompagId(), manager.getZcComprobanteId());
        assertEquals(manager.getDetNombrePagador(), manager.getZcNombre());
        assertEquals(manager.getDetCompagNro(), manager.getZcNumeroComprobante());
        assertEquals(manager.getDetCompagMonto(), manager.getZcMonto(), 0);
        assertEquals(manager.getConceptoPagoDesc(), manager.getZcConceptoPago());
        assertEquals(manager.getRowConceptoPago(), manager.getZcConceptoPagoDetalle());
        assertEquals(manager.getDetCompagTipoCompId(), manager.getZcProcedenciaPagoId());

        assertEquals(manager.getDetCompagTipoDoc(), manager.getZcTipoComprobanteId());
        assertEquals(manager.getDetVoucherNroOper(), manager.getZcNumVoucher());
        assertEquals(manager.getDetVoucherCodAgencia(), manager.getZcCodigoAgencia());

        assertEquals(manager.getDetCompagFechaPago(), manager.getZcFechaPago());
        assertEquals(manager.getDetVoucherFecha(), manager.getZcFecVoucher());
    }

    @Test
    public void openingModalForNewPagoRegularStudentTest() {
        manager.nuevo(actionEvent);

        assertEquals(1, manager.getnValorNuevo());
        assertEquals(0, manager.getnConPagId());
        assertEquals(0, manager.getnMpMonto(), 0);
        assertEquals(0, manager.getnMpSaldo(), 0);
        assertEquals("0", manager.getnMpIdTipoCliente());
        assertEquals(0, manager.getTmpClientId());
        assertTrue(manager.getnMpRazonSocial().isEmpty());
        assertEquals("0", manager.getnMpTipoPersNatJur());
        assertTrue(manager.getnMpDniRuc().isEmpty());
        assertEquals(CAT_PROCEDENCIA_PAGO_EFECTIVO, manager.getnMpProcedenciaId());
        assertNotNull(manager.getnMpFechaComprobante());
        assertEquals(0, manager.getnMpMontoPago(), 0);
        assertTrue(manager.getnMpDescription().isEmpty());
        assertTrue(manager.getnMpCodAlu().isEmpty());
        assertEquals(0, manager.getAlumnoId());
        assertTrue(manager.getRowNombres().isEmpty());

        assertEquals(4, manager.getComboTiposComprobantePago().size());
        assertEquals(CAT_TIPO_COMPROBANTE_PAGO_BOLETA, manager.getnMpTipoComprPago());
        assertTrue(manager.getnMpNumVou().isEmpty());
        assertNotNull(manager.getnMpFecVou());
        assertTrue(manager.getnMpCodAge().isEmpty());

        interactingWithModalNewPaymentRegularStudent();
    }

    private void interactingWithModalNewPaymentRegularStudent() {
        manager.setnMpIdTipoCliente("0");
        manager.seleccionarClienteNuevo(actionEvent);
        assertTrue(manager.getSelCli().isEmpty());
        assertEquals(0, manager.getTmpClientId());
        assertEquals("0", manager.getnMpTipoPersNatJur());
        assertTrue(manager.getnMpRazonSocial().isEmpty());
        assertTrue(manager.getnMpDniRuc().isEmpty());

        // Category Customer Type ALUMNO_CP
        manager.setnMpIdTipoCliente(CAT_TIPO_CLIENTE_ALUMNO_CP);
        manager.seleccionarClienteNuevo(actionEvent);
        assertFalse(manager.getXunoy().isEmpty());
        assertTrue(manager.getSelCli().contains("mpCustomerSearch"));

        // Looking for customer on modal 'mpCustomerSearch'
        manager.setXuno("");
        manager.funxuno(actionEvent);
        assertTrue(manager.getOncomplete2().contains("Ingrese valor de busqueda"));

        manager.setXuno("SILVA");
        manager.funxuno(actionEvent);
        assertEquals(8, manager.getListaCliente().size());

        // ------------------------------------------------------------------
        MbPago row = manager.getListaCliente().get(Stubs.randomNumeric(0, manager.getListaCliente().size()));
        Map<String, Object> attributes = Collections.singletonMap("selected_cli", row);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.asignar(actionEvent);

        saveNewPaymentAlumnoCP();

        editarComprobanteExternoTest();
    }

    private void saveNewPaymentAlumnoCP() {
        manager.setnConPagId(274); // SEMINARIO UCH
        manager.setnMpMontoPago(0); // PAGO EFECTIVO
        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("Ingrese correctamete el monto a pagar"));

        // ------------------------------------------------------------------
        manager.setnMpMontoPago(60);
        manager.setnMpDescription("FROM_TEST_" + RandomStringUtils.randomAlphanumeric(40));
        manager.setnMpNumSerie("004");
        manager.cambiaSerieN(actionEvent);

        manager.insertarNuevo(actionEvent);

        assertTrue(manager.getNoncomplete().contains("El Pago se efectuo Correctamente"));
    }

    private void editarComprobanteExternoTest() {
        // Looking for the last value
        manager.setParamListaPagoId(2);
        manager.setParamFechaDesde(Date.valueOf(LocalDate.now()));
        manager.setParamFechaHasta(Date.valueOf(LocalDate.now()));
        manager.setParamConceptoPagoId(274);

        manager.seleccionar();

        MbPago row = manager.getListaAlumno().get(manager.getListaAlumno().size() - 1);
        Map<String, Object> attributes = Collections.singletonMap("zc_comprobante", row);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.editarComprobante(actionEvent);

        assertEquals(manager.getZcComprobanteId(), row.getComprobanteOculto());
        assertEquals(manager.getZcNumeroComprobante(), row.getNroComprobanteOculto());
        assertEquals(CAT_PROCEDENCIA_PAGO_EFECTIVO, manager.getZcProcedenciaPagoId());
        assertEquals(manager.getZcFechaPago(), row.getComprobanteFechaOculto());
        assertEquals(manager.getZcNombre(), row.getComprobanteNombreOculto());
        assertEquals(manager.getZcMonto(), row.getComprobanteMontoOculto(), 0);
        assertEquals(manager.getZcConceptoPago(), row.getComprobanteConceptoOculto());
        assertEquals(manager.getZcConceptoPagoDetalle(), row.getComprobanteConceptoDetOculto());

        actualizarComprobantePagoTest();
    }

    private void actualizarComprobantePagoTest() {
        String tmpNumCom = manager.getZcNumeroComprobante();
        java.util.Date tempFePag = manager.getZcFechaPago();

        manager.setZcNumeroComprobante("");
        manager.setZcFechaPago(null);
        manager.setZcTipoComprobanteId("");

        manager.actualizarComprobantePago(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setZcNumeroComprobante(tmpNumCom);
        manager.actualizarComprobantePago(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setZcFechaPago(tempFePag);
        manager.actualizarComprobantePago(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setZcProcedenciaPagoId(CAT_PROCEDENCIA_PAGO_SALDO);
        manager.setZcTipoComprobanteId("039004");
        manager.actualizarComprobantePago(actionEvent);
        assertTrue(manager.getOncomplete().contains("No esta permitido el Cambio de Procedencia a Saldos"));

        // ------------------------------------------------------------------
        manager.setZcProcedenciaPagoId(CAT_PROCEDENCIA_PAGO_VOUCHER);
        manager.actualizarComprobantePago(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setZcNumVoucher(RandomStringUtils.randomNumeric(20));
        manager.actualizarComprobantePago(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setZcFecVoucher(new java.util.Date());
        manager.actualizarComprobantePago(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setZcCodigoAgencia(RandomStringUtils.randomAscii(3));

        manager.actualizarComprobantePago(actionEvent);
        assertTrue(manager.getOncomplete().contains("La Actualizacion se efectuo Correctamente"));
    }

    @Test
    public void openingModalForNewPagoClienteExternoTest() {
        manager.nuevo(actionEvent);
        assertEquals(1, manager.getnValorNuevo());

        // ------------------------------------------------------------------
        manager.nSeleccionarClienteNuevo(actionEvent);
        assertTrue(manager.getSelCli().contains("mpCustomerSearch"));

        // ------------------------------------------------------------------
        manager.setnMpIdTipoCliente(CAT_TIPO_CLIENTE_EXTERNO);
        manager.seleccionarClienteNuevo(actionEvent);
        assertTrue(manager.getSelCli().contains("mpCustomerSearch"));

        // Looking for customer on modal 'mpCustomerSearch'
        manager.setXuno("CYNTHIA");

        manager.funxuno(actionEvent);

        assertEquals(3, manager.getListaCliente().size());

        // ------------------------------------------------------------------
        MbPago row = manager.getListaCliente().get(Stubs.randomNumeric(0, manager.getListaCliente().size()));
        Map<String, Object> attributes = Collections.singletonMap("selected_cli", row);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.asignar(actionEvent);

        // ------------------------------------------------------------------
        saveNewPaymentExternoTransferencia();
    }

    private void saveNewPaymentExternoTransferencia() {
        manager.setnMpProcedenciaId(CAT_PROCEDENCIA_PAGO_TRANSFERENCIA);
        manager.setnConPagId(83); // LIBRO NUEVA GUIA
        manager.setnMpMontoPago(10);
        manager.setnMpDescription("FROM_TEST_" + RandomStringUtils.randomAlphanumeric(40));

        manager.setnMpNumSerie("004");
        manager.cambiaSerieN(actionEvent);

        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("El Pago se efectuo Correctamente"));
    }

    @Test
    public void openingModalForNewPagoAlumnoCursoLibreTest() {
        manager.nuevo(actionEvent);
        assertEquals(1, manager.getnValorNuevo());

        manager.setnMpIdTipoCliente(CAT_TIPO_CLIENTE_ALUMNO_CL);
        manager.seleccionarClienteNuevo(actionEvent);
        assertTrue(manager.getSelCli().contains("mpCustomerSearch"));

        manager.setXuno("MENDOZA");

        manager.funxuno(actionEvent);

        assertEquals(27, manager.getListaCliente().size());

        // ------------------------------------------------------------------
        MbPago row = manager.getListaCliente().get(Stubs.randomNumeric(0, manager.getListaCliente().size()));
        Map<String, Object> attributes = Collections.singletonMap("selected_cli", row);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.asignar(actionEvent);

        // ------------------------------------------------------------------
        manager.setnConPagId(Stubs.randomId(manager.getComboConceptoPago()));
        manager.setnMpMontoPago(5);
        manager.setnMpDescription("FROM_TEST_" + RandomStringUtils.randomAlphanumeric(40));

        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("El Pago se efectuo Correctamente"));

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("o1")).thenReturn(null);
        when(uiComponent.findComponent("id_z")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getZcComprobanteId());
        manager.eliminarPago(actionEvent);
    }

    @Test
    public void openingModalForNewPagoExternoTest() {
        manager.nuevo(actionEvent);
        manager.insertarNuevo(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getNoncomplete());

        // ------------------------------------------------------------------
        manager.asignarNewExternalClient(actionEvent);
        assertEquals(CAT_TIPO_CLIENTE_EXTERNO, manager.getnMpIdTipoCliente());
        assertEquals("0", manager.getnMpTipoPersNatJur());
        assertEquals(0, manager.getnMpSaldo(), 0);

        manager.setnMpProcedenciaId("0");
        manager.setnMpMontoPago(-100);
        manager.setnMpFechaComprobante(null);
        manager.setnMpNumComprobante("");
        manager.insertarNuevo(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getNoncomplete());

        // ------------------------------------------------------------------
        manager.setnMpRazonSocial("FAKE_" + RandomStringUtils.randomAlphanumeric(25));
        manager.insertarNuevo(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getNoncomplete());

        // ------------------------------------------------------------------
        manager.setnMpTipoPersNatJur("017002");
        manager.insertarNuevo(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getNoncomplete());

        // ------------------------------------------------------------------
        manager.setnMpProcedenciaId(CAT_PROCEDENCIA_PAGO_BANCO); // PAGO BANCO
        manager.insertarNuevo(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getNoncomplete());

        // ------------------------------------------------------------------
        manager.setnMpMontoPago(Stubs.randomNumeric(0, 100)); // PAGO BANCO
        manager.insertarNuevo(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getNoncomplete());

        // ------------------------------------------------------------------
        manager.setnMpFechaComprobante(new java.util.Date());
        manager.insertarNuevo(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getNoncomplete());

        // ------------------------------------------------------------------
        manager.setnConPagId(20); // CARNET UNIVERSITARIO
        manager.setnMpNumSerie("004");
        manager.cambiaSerieN(actionEvent);
        manager.setnMpDniRuc("20" + RandomStringUtils.randomNumeric(9));
        manager.setnMpDescription("FROM_TEST_" + RandomStringUtils.randomAlphanumeric(40));

        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("El Pago se efectuo Correctamente"));
    }

    @Test
    public void openingModalForNewPagoExternoVoucherTest() {
        java.util.Date currentDate = new java.util.Date();

        manager.nuevo(actionEvent);
        assertEquals(1, manager.getnValorNuevo());

        manager.setnMpIdTipoCliente(CAT_TIPO_CLIENTE_EXTERNO);
        manager.setnMpRazonSocial("CCOA GARAY MICHAEL AMES");
        manager.setnMpTipoPersNatJur(CAT_TIPO_RAZON_SOCIAL_NATURAL);
        manager.setnMpProcedenciaId(CAT_PROCEDENCIA_PAGO_VOUCHER);
        manager.setnMpMontoPago(100);
        manager.setnMpFechaComprobante(currentDate);
        manager.setnMpNumComprobante("0000354");
        manager.setnMpFecVou(null);

        manager.setnConPagId(125);
        manager.setnMpNumSerie("004");
        manager.setAlumnoId(3102);
        manager.setnMpCodAlu("09105110");
        manager.setTmpClientId(332);
        manager.setnMpTipoComprPago(CAT_TIPO_COMPROBANTE_PAGO_BOLETA);
        manager.setnMpDescription("FROM_TEST_" + RandomStringUtils.randomAlphanumeric(40));

        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("ya fue usado"));

        // ------------------------------------------------------------------
        manager.cambiaSerieN(actionEvent); // reset nro compr.
        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("Ingrese los campos faltantes"));

        // ------------------------------------------------------------------
        manager.setnMpFecVou(currentDate);
        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("Ingrese los campos faltantes"));

        // ------------------------------------------------------------------
        manager.setnMpNumVou(RandomStringUtils.randomNumeric(10));
        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("Ingrese los campos faltantes"));

        // ------------------------------------------------------------------
        manager.setnMpCodAge(RandomStringUtils.randomNumeric(3));
        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("El Pago se efectuo Correctamente"));
    }

    @Test
    public void openingModalForNewPagoExternoValeTest() {
        manager.nuevo(actionEvent);
        assertEquals(1, manager.getnValorNuevo());

        manager.setnMpIdTipoCliente(CAT_TIPO_CLIENTE_EXTERNO);
        manager.setnMpRazonSocial("ROCHA DURAND JUAN PABLO");
        manager.setnMpTipoPersNatJur(CAT_TIPO_RAZON_SOCIAL_NATURAL);
        manager.setnMpProcedenciaId(CAT_PROCEDENCIA_PAGO_VALE);
        manager.setnMpMontoPago(100);
        manager.setnMpFechaComprobante(new java.util.Date());

        manager.setnMpNumSerie("004");
        manager.cambiaSerieN(actionEvent);

        manager.setnConPagId(132);
        manager.setAlumnoId(3633);
        manager.setnMpCodAlu("09202107");
        manager.setnMpTipoComprPago("039003"); // RECIBO INTERNO
        manager.setnMpDescription("FROM_TEST_" + RandomStringUtils.randomAlphanumeric(40));

        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("El Pago se efectuo Correctamente"));

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("o1")).thenReturn(null);
        when(uiComponent.findComponent("id_z")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getZcComprobanteId());
        manager.anularPago(actionEvent);
    }

    @Test
    public void openingModalForNewPagoExternoSaldoTest() {
        manager.nuevo(actionEvent);
        assertEquals(1, manager.getnValorNuevo());

        manager.setnMpIdTipoCliente(CAT_TIPO_CLIENTE_EXTERNO);
        manager.setnMpRazonSocial("GOMEZ PARODI MANUEL ELIAS");
        manager.setnMpTipoPersNatJur(CAT_TIPO_RAZON_SOCIAL_NATURAL);
        manager.setnMpProcedenciaId(CAT_PROCEDENCIA_PAGO_SALDO);
        manager.setnMpMontoPago(100);
        manager.setnMpFechaComprobante(new java.util.Date());

        manager.setnMpNumSerie("004");
        manager.cambiaSerieN(actionEvent);

        manager.setnConPagId(131); // INVESTIGACION MISERIA DEL METODO  EN CIENCIAS SOCIALES
        manager.setAlumnoId(2791);
        manager.loadSaldoOfStudent(actionEvent);
        manager.setnMpDniRuc("45047061");
        manager.setnMpCodAlu("08203034");
        manager.setnMpTipoComprPago("039004"); // RECIBO CAJA
        manager.setnMpDescription("FROM_TEST_" + RandomStringUtils.randomAlphanumeric(40));

        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("Ud No cuenta con saldo suficiente para Concretar este Pago"));

        // ------------------------------------------------------------------
        manager.setAlumnoId(3102);
        manager.loadSaldoOfStudent(actionEvent);

        manager.insertarNuevo(actionEvent);
        assertTrue(manager.getNoncomplete().contains("El Pago se efectuo Correctamente"));
    }

    @Test
    public void autocompleteConceptoPagoModalNuevoPagoTest() {
        manager.setSuggestion("matri");
        List<BeanConceptoPago> results = manager.autocomplete(manager.getSuggestion());

        assertEquals(9, results.size());

        BeanConceptoPago r = results.get(0);

        assertNotEquals(0, r.getId());
        assertFalse(r.getDesc().isEmpty());
        assertEquals(0, r.getMonto(), 0);
        assertEquals(0, manager.getnConPagId());

        // ------------------------------------------------------------------
        results = manager.autocomplete("null");
        assertTrue(results.isEmpty());

        // ------------------------------------------------------------------
        results = manager.autocomplete("");
        assertTrue(results.isEmpty());
    }

    @Test
    public void openModalPersonalizarTest() {
        java.util.Date currentDate = new java.util.Date();

        MbPago bean = new MbPago();
        bean.setAlutarId(53870);
        bean.setAlumnoId(3309); // 09101317 - PARIONA MALPICA RICHARD RONALD
        bean.setConceptoPagoId(3);
        bean.setEstructPagoDetId(595);
        bean.setRowAdeuda(450);

        Map<String, Object> attributes = Collections.singletonMap("alu_s", bean);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.personalizar(actionEvent);

        assertEquals(0, manager.getnValorNuevo());
        assertEquals(0, manager.getTmpClientId());
        assertEquals(bean.getRowAdeuda(), manager.getRowAdeuda(), 0);

        // ------------------------------------------------------------------
        manager.setRowMonto(-1);
        manager.savePersonalization(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setAlutarFecPago(currentDate);
        manager.savePersonalization(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setRowFecProOrFecComprPago(currentDate);
        manager.savePersonalization(actionEvent);
        assertEquals(ALERT_REQUIRED_FIELDS, manager.getOncomplete());

        // ------------------------------------------------------------------
        manager.setRowMonto(500);
        manager.savePersonalization(actionEvent);
        assertTrue(manager.getOncomplete().contains("La Actualizacion se efectuo Corectamente"));
    }

    @Test
    public void openModalPagoCarnetAndMoraTest() {
        Map<String, Object> attributes = Collections.singletonMap("p_alu_id", 2580);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.carnet(actionEvent);

        assertEquals(attributes.get("p_alu_id"), manager.getAlumnoId());

        // ------------------------------------------------------------------
        manager.pagarCarnet(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese nro de comprobante"));

        // ------------------------------------------------------------------
        manager.setZcNumeroComprobante("001-0003368");
        manager.pagarCarnet(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ya registra pago por el concepto seleccionado"));

        // ------------------------------------------------------------------
        attributes = Collections.singletonMap("p_alu_id", 2378);
        when(uiComponent.getAttributes()).thenReturn(attributes);
        manager.mora(actionEvent);

        assertEquals(attributes.get("p_alu_id"), manager.getAlumnoId());

        // ------------------------------------------------------------------
        manager.setZcNumeroComprobante("001-0003365");
        manager.pagarCarnet(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ya registra pago por el concepto seleccionado"));

        // ------------------------------------------------------------------
        attributes = Collections.singletonMap("p_alu_id", 3937);
        when(uiComponent.getAttributes()).thenReturn(attributes);
        manager.mora(actionEvent);

        assertEquals(attributes.get("p_alu_id"), manager.getAlumnoId());

        manager.setnMpNumSerie("004");
        manager.cambiaSerieN(actionEvent); // Generating nro serie
        manager.setZcNumeroComprobante("004-" + manager.getnMpNumComprobante());

        manager.pagarCarnet(actionEvent);
        assertTrue(manager.getOncomplete().contains("El Pago se efectuo Correctamente"));

        // ------------------------------------------------------------------
        DAO.deleteAdComprobantePago(3937, manager.getZcNumeroComprobante());
    }

    @Test
    public void openModalPagoInternoMatriculaExtemporaneaTest() {
        Map<String, Object> attributes = Collections.singletonMap("p_alu_id", 3247);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.matExtemporanea(actionEvent);

        assertEquals(attributes.get("p_alu_id"), manager.getAlumnoId());

        // ------------------------------------------------------------------
        manager.pagarMatExtemporanea(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Nro de Comprobante"));

        // ------------------------------------------------------------------
        manager.setnMpNumSerie("004");
        manager.cambiaSerieN(actionEvent); // Generating nro serie
        manager.setZcNumeroComprobante(manager.getnMpNumComprobante());

        manager.pagarMatExtemporanea(actionEvent);
        assertTrue(manager.getOncomplete().contains("El Pago se efectuo Correctamente"));
    }

    @Test
    public void moraOutstandingKeepsTariffPartialTest() {
        // WP3 mora-consistency rule (BUG-05/06): mora is part of the student's debt.
        manager.setRowMonto(360f);
        manager.setnMpMoraPago(20f);
        // Base fully covered but mora unpaid (checkbox unchecked) -> PARTIAL ("1"), never TOTAL.
        assertEquals("1", manager.deriveEstadoTarifa(360f, 0f));
        // Mora settled too -> TOTAL ("2").
        assertEquals("2", manager.deriveEstadoTarifa(360f, 20f));
        // Nothing paid -> DEBE ("0").
        assertEquals("0", manager.deriveEstadoTarifa(0f, 0f));
        // No mora due: the base alone settles the tariff.
        manager.setnMpMoraPago(0f);
        assertEquals("2", manager.deriveEstadoTarifa(360f, 0f));
    }

    @Test
    public void imprimirComprobanteAlumnoRegularTest() {
        manager.setZcComprobanteId(493);

        UIParameter uipIdZ = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_z")).thenReturn(uipIdZ);
        when(uipIdZ.getValue()).thenReturn(manager.getZcComprobanteId());

        manager.imprimirComprobante(actionEvent);

        assertTrue(manager.getJsImpresion().contains("window.open("));
        assertTrue(manager.getJsImpresion().contains("/comprobante?id=493"));
    }

    @Test
    public void imprimirComprobanteClienteTest() {
        manager.setZcComprobanteId(30989);

        UIParameter uipIdZ = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_z")).thenReturn(uipIdZ);
        when(uipIdZ.getValue()).thenReturn(manager.getZcComprobanteId());

        manager.imprimirComprobante(actionEvent);

        assertTrue(manager.getJsImpresion().contains("window.open("));
        assertTrue(manager.getJsImpresion().contains("/comprobante?id=30989"));
    }

    @Test
    public void imprimirListadoWithoutDatesShowsAlertTest() {
        manager.setParamFechaDesde(null);
        manager.setParamFechaHasta(null);

        manager.imprimirListado(actionEvent);

        assertTrue(manager.getAlertaCaja().contains("alert("));
    }

    @Test
    public void imprimirListadoOpensReportInNewTabTest() {
        manager.setParamFechaDesde(Date.valueOf(LocalDate.of(2010, Month.MARCH, 8)));
        manager.setParamFechaHasta(Date.valueOf(LocalDate.of(2010, Month.MARCH, 10)));

        manager.imprimirListado(actionEvent);

        String alertaCaja = manager.getAlertaCaja();
        assertTrue(alertaCaja.contains("window.open("));
        assertTrue(alertaCaja.contains("/report?type=cuadre-caja"));
        assertTrue(alertaCaja.contains("fecIni=2010-03-08"));
        assertTrue(alertaCaja.contains("fecFin=2010-03-10"));
    }
}
