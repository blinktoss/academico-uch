package net.uch.administrativa.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static net.uch.Stubs.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 17/12/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbEstructuraPagoTest extends SpringConfigurationTest {

    private MbEstructuraPago manager;

    @Before
    public void setUp() {
        manager = new MbEstructuraPago();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        List<SelectItem> facultades = manager.getComboFacultad();
        List<SelectItem> especialidades = manager.getComboEspecialidadBuscar();
        List<SelectItem> semestres = manager.getComboSemestre();

        assertEquals(5, facultades.size());
        assertEquals(0, especialidades.size());
        assertNotEquals(0, semestres.size());
        assertEquals(0, manager.getParamSemeId());

        // On change facultad
        manager.setParamFacuId(Facultad.CONTABILIDAD);
        manager.setearFacultadBuscar(actionEvent);

        facultades = manager.getComboFacultad();
        especialidades = manager.getComboEspecialidadBuscar();

        assertEquals(Facultad.CONTABILIDAD, manager.getParamFacuId());
        assertEquals(5, facultades.size());
        assertEquals(1, especialidades.size());

        // On change facultad
        manager.setParamFacuId(0);
        manager.setearFacultadBuscar(actionEvent);

        facultades = manager.getComboFacultad();
        especialidades = manager.getComboEspecialidadBuscar();

        assertEquals(0, manager.getParamFacuId());
        assertEquals(5, facultades.size());
        assertTrue(especialidades.isEmpty());
    }

    @Test
    public void seleccionarBySpecialityTest() {
        manager.setParamEspeId(Especialidad.CONTABILIDAD);
        manager.setParamSemeId(SEMESTRE_2010_2_ID);

        manager.seleccionar();

        assertEquals(7, manager.getTablaSisEva().size());

        assertNotEquals(0, manager.getTablaSisEva().get(0).getRowId());
        assertFalse(manager.getTablaSisEva().get(0).getRowDescription().isEmpty());
        assertFalse(manager.getTablaSisEva().get(0).getRowEspecialidad().isEmpty());
        assertNotEquals(0, manager.getTablaSisEva().get(0).getRowEspecialidadId());
        assertNotEquals(0, manager.getTablaSisEva().get(0).getRowFacultadId());
        assertFalse(manager.getTablaSisEva().get(0).getRowFacultad().isEmpty());
        assertFalse(manager.getTablaSisEva().get(0).getRowSemEjecucionValue().isEmpty());
        assertNotEquals(0, manager.getTablaSisEva().get(0).getRowSemEjecucionId());
        assertFalse(manager.getTablaSisEva().get(0).getRowSemDestinoValue().isEmpty());
        assertFalse(manager.getTablaSisEva().get(0).getRowPublicado().isEmpty());

        assertFalse(manager.getTablaSisEva().get(0).getRowImgPublish().isEmpty());
        assertFalse(manager.getTablaSisEva().get(0).getRowImgEdit().isEmpty());
        assertFalse(manager.getTablaSisEva().get(0).getRowImgDelete().isEmpty());
        assertTrue(manager.getTablaSisEva().get(0).isDisabledPublish());
        assertTrue(manager.getTablaSisEva().get(0).isDisabledDelete());

        assertNotEquals(0, manager.getTablaSisEva().get(0).getSubTablaDetalle().get(0).getDetConPagId());
        assertFalse(manager.getTablaSisEva().get(0).getSubTablaDetalle().get(0).getDetConPagValue().isEmpty());
        assertNotEquals(0, manager.getTablaSisEva().get(0).getSubTablaDetalle().get(0).getDetMontoRef(), 0);
        assertFalse(manager.getTablaSisEva().get(0).getSubTablaDetalle().get(0).getDetCodigoRef().isEmpty());
        assertNotNull(manager.getTablaSisEva().get(0).getSubTablaDetalle().get(0).getDetFecha());
        assertNotEquals(0, manager.getTablaSisEva().get(0).getSubTablaDetalle().get(0).getDetMonto(), 0);
    }

    @Test
    public void openAndCloseSubTable() {
        UIParameter uipFlag = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("flag")).thenReturn(uipFlag);

        manager.setFlag("0");

        when(uipFlag.getValue()).thenReturn(manager.getFlag());
        manager.subTable(actionEvent);

        assertTrue(manager.isVer());
        assertEquals("1", manager.getFlag());
        assertTrue(manager.getImagen().contains("up.png"));

        // Closing
        manager.setFlag("1");

        when(uipFlag.getValue()).thenReturn(manager.getFlag());
        manager.subTable(actionEvent);

        assertFalse(manager.isVer());
        assertEquals("0", manager.getFlag());
        assertTrue(manager.getImagen().contains("down.png"));
    }

    @Test
    public void openModalModalEstructuraPagoTest() {
        manager.nuevo(actionEvent);

        assertTrue(manager.getMpDescription().isEmpty());
        assertEquals(0, manager.getRowId());
        assertEquals(0, manager.getRowFacultadId());
        assertEquals(0, manager.getRowFacultadId());
        assertEquals(0, manager.getRowSemEjecucionId());
        assertEquals(0, manager.getRowSemDestinoId());

        List<SelectItem> facultades = manager.getComboFacultad();
        List<SelectItem> especialidades = manager.getMpComboEspecialidad();
        List<SelectItem> semestresEjecucion = manager.getComboSemestre();
        List<SelectItem> semestresDestino = manager.getComboSemestre();
        List<SelectItem> conceptosDePago = manager.getComboConceptoPago();

        assertEquals(5, facultades.size());
        assertTrue(especialidades.isEmpty());
        assertNotEquals(0, semestresEjecucion.size());
        assertNotEquals(0, semestresDestino.size());
        assertEquals(268, conceptosDePago.size());

        // On change facultad
        manager.setRowFacultadId(Facultad.CONTABILIDAD);
        manager.setearFacultadMp(actionEvent);

        facultades = manager.getComboFacultad();
        especialidades = manager.getMpComboEspecialidad();

        assertEquals(Facultad.CONTABILIDAD, manager.getRowFacultadId());
        assertEquals(5, facultades.size());
        assertEquals(1, especialidades.size());

        // On change facultad
        manager.setRowFacultadId(0);
        manager.setearFacultadMp(actionEvent);

        facultades = manager.getComboFacultad();
        especialidades = manager.getMpComboEspecialidad();

        assertEquals(0, manager.getRowFacultadId());
        assertEquals(5, facultades.size());
        assertTrue(especialidades.isEmpty());

        saveNewPaymentStructureWithoutDetails();
        publishPaymentStructureTest(manager.getRowId());
        deletingPaymentStructure(manager.getRowId());
    }

    @Test
    public void openModalModalEstructuraPagoWithDetailsTest() {
        manager.nuevo(actionEvent);

        assertTrue(manager.getMpDescription().isEmpty());
        assertEquals(0, manager.getRowId());

        saveNewPaymentStructureWithDetails();
        editingPaymentStructureWithDetails(manager.getRowId());
        publishPaymentStructureWithDetails(manager.getRowId());
        deletingPaymentStructure(manager.getRowId());
    }

    private void saveNewPaymentStructureWithoutDetails() {
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos requeridos"));

        manager.setRowFacultadId(Facultad.ING_ELECTRONICA);
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos requeridos"));

        manager.setRowFacultadId(Especialidad.ING_ELECTRONICA);
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos requeridos"));

        manager.setRowSemEjecucionId(5); // SEMESTRE TRASLADOS
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos requeridos"));

        manager.setRowSemDestinoId(5); // SEMESTRE TRASLADOS
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos requeridos"));

        manager.setMpDescription("FAKE_PAYMENT_STRUCTURE_DESCRIPTION");
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se creo una Estructura de Pagos correctamente"));
        assertTrue(manager.getTablaSisEvaDetalle().isEmpty());
    }

    private void saveNewPaymentStructureWithDetails() {
        Date now = Date.valueOf(LocalDate.now());
        String randomStr = UUID.randomUUID().toString();

        manager.setRowFacultadId(Facultad.ING_ELECTRONICA);
        manager.setRowFacultadId(Especialidad.ING_ELECTRONICA);
        manager.setRowSemEjecucionId(5); // SEMESTRE TRASLADOS
        manager.setRowSemDestinoId(5); // SEMESTRE TRASLADOS
        manager.setMpDescription("FAKE_PAYMENT_STRUCTURE_DETAILS_DESCRIPTION " + randomStr);

        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ud tiene que Ingrese todos los datos"));

        manager.setDetConPagId(274); // SEMINARIO UCH ... S/. 60.0
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ud tiene que Ingrese todos los datos"));

        manager.setDetMonto(10);
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ud tiene que Ingrese todos los datos"));

        manager.setDetFecha(now);
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ud tiene que Ingrese todos los datos"));

        manager.setDetNombre("FAKE_DESC_DETAILS_001");
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se Agrego un Concepto de Pago mas"));
        assertEquals(1, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        manager.setDetConPagId(273); // LIBRO APRENDIENDO LA CONSTITUCION ... S/. 35.0
        manager.setDetMonto(20);
        manager.setDetFecha(now);
        manager.setDetNombre("FAKE_DESC_DETAILS_002");
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se Agrego un Concepto de Pago mas"));
        assertEquals(2, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        manager.setDetConPagId(272); // LIBRO EDUCACION Y TRABAJO EN EL NUEVO ESCENARIO ... S/. 35.0
        manager.setDetMonto(20);
        manager.setDetFecha(now);
        manager.setDetNombre("FAKE_DESC_DETAILS_003");
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se Agrego un Concepto de Pago mas"));
        assertEquals(3, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_det")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getTablaSisEvaDetalle().get(1).getDetId());
        manager.quitar(actionEvent);
        assertEquals(2, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        manager.setDetConPagId(14); // CI-MATERIALES1 ... S/. 85.0
        manager.setDetMonto(30);
        manager.setDetFecha(now);
        manager.setDetNombre("FAKE_DESC_DETAILS_004");
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se Agrego un Concepto de Pago mas"));
        assertEquals(3, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se creo una Estructura de Pagos correctamente"));
    }

    private void editingPaymentStructureWithDetails(int peId) {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_s")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(peId);

        manager.editar(actionEvent);

        assertEquals(0, manager.getDetConPagId());
        assertEquals(0, manager.getDetMonto(), 0);
        assertNull(manager.getDetFecha());
        assertTrue(manager.getDetNombre().isEmpty());

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_det")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getTablaSisEvaDetalle().get(1).getDetId());
        manager.quitar(actionEvent);
        assertEquals(2, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se Actualizaron los datos de la Estructura de Pagos correctamente"));
    }

    private void deletingPaymentStructure(int peId) {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_s")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(peId);

        manager.eliminar(actionEvent);
    }

    private void publishPaymentStructureTest(int peId) {
        manager.setRowId(peId);
        manager.setRowEspecialidadId(Especialidad.ING_ELECTRONICA);
        manager.setRowSemDestinoId(5);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_s")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getRowId());

        UIParameter uipEspId = mock(UIParameter.class);
        when(uiComponent.findComponent("esp_id_s")).thenReturn(uipEspId);
        when(uipEspId.getValue()).thenReturn(manager.getRowEspecialidadId());

        UIParameter uipSemDestinationId = mock(UIParameter.class);
        when(uiComponent.findComponent("sem_id_d")).thenReturn(uipSemDestinationId);
        when(uipSemDestinationId.getValue()).thenReturn(manager.getRowSemDestinoId());

        manager.publicar(actionEvent);
    }

    private void publishPaymentStructureWithDetails(int peId) {
        manager.setRowId(peId);
        manager.setRowEspecialidadId(Especialidad.CONTABILIDAD);
        manager.setRowSemDestinoId(SEMESTRE_2009_2_ID);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_s")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getRowId());

        UIParameter uipEspId = mock(UIParameter.class);
        when(uiComponent.findComponent("esp_id_s")).thenReturn(uipEspId);
        when(uipEspId.getValue()).thenReturn(manager.getRowEspecialidadId());

        UIParameter uipSemDestinationId = mock(UIParameter.class);
        when(uiComponent.findComponent("sem_id_d")).thenReturn(uipSemDestinationId);
        when(uipSemDestinationId.getValue()).thenReturn(manager.getRowSemDestinoId());

        UIParameter uipSemDescription = mock(UIParameter.class);
        UIParameter uipSemExecutionId = mock(UIParameter.class);

        when(uiComponent.findComponent("des_s")).thenReturn(uipSemDescription);
        when(uiComponent.findComponent("sem_id_s")).thenReturn(uipSemExecutionId);

        when(uipSemDescription.getValue()).thenReturn(RandomStringUtils.randomAlphabetic(15));
        when(uipSemExecutionId.getValue()).thenReturn(5);

        manager.publicar(actionEvent);
    }
}
