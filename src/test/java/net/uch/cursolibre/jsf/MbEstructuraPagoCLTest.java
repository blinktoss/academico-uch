package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 08/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbEstructuraPagoCLTest extends SpringConfigurationTest {

    private MbEstructuraPagoCL manager;

    @Before
    public void setUp() {
        manager = new MbEstructuraPagoCL();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        List<SelectItem> areas = manager.getAreas();
        List<SelectItem> modulos = manager.getModulos();

        assertEquals(0, manager.getParamAreaId());
        assertEquals(15, areas.size());
        assertEquals(0, manager.getParamModId());
        assertTrue(modulos.isEmpty());

        // On change
        manager.setParamAreaId(1);
        manager.verModulos(actionEvent);

        areas = manager.getAreas();
        modulos = manager.getModulos();

        assertEquals(15, areas.size());
        assertEquals(10, modulos.size());

        // On change
        manager.setParamAreaId(0);
        manager.verModulos(actionEvent);

        areas = manager.getAreas();
        modulos = manager.getModulos();

        assertEquals(15, areas.size());
        assertTrue(modulos.isEmpty());
    }

    @Test
    public void seleccionarTest() {
        manager.setParamNombre("");
        manager.setParamAreaId(1); // PROGRAMACIÓN
        manager.setParamModId(7); // JAVA DEVELOPER

        String result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertEquals(1, manager.getEstructuras().size());

        assertNotEquals(0, manager.getEstructuras().get(0).getRowEstpagId());
        assertFalse(manager.getEstructuras().get(0).getRowEstpagNombre().isEmpty());
        assertFalse(manager.getEstructuras().get(0).getRowEstpagAbrev().isEmpty());
        assertFalse(manager.getEstructuras().get(0).getRowEstpagActivo().isEmpty());
        assertNotEquals(0, manager.getEstructuras().get(0).getModId());
        assertFalse(manager.getEstructuras().get(0).getModulo().isEmpty());

        assertNotEquals(0, manager.getEstructuras().get(0).getDetalle().get(0).getRsbEstpagdetId());
        assertFalse(manager.getEstructuras().get(0).getDetalle().get(0).getRsbEstpagdetNombre().isEmpty());
        assertNotEquals(0, manager.getEstructuras().get(0).getDetalle().get(0).getRsbConpagId());
        assertFalse(manager.getEstructuras().get(0).getDetalle().get(0).getConcepto().isEmpty());
        assertNotEquals(0, manager.getEstructuras().get(0).getDetalle().get(0).getRsbEstpagdetMonto(), 0);
        assertNotEquals(0, manager.getEstructuras().get(0).getDetalle().get(0).getRsbEstpagId());
        assertNotEquals(0, manager.getEstructuras().get(0).getDetalle().get(0).getRsbTalId());
        assertFalse(manager.getEstructuras().get(0).getDetalle().get(0).getTaller().isEmpty());

        // Other search
        manager.setParamNombre("");
        manager.setParamAreaId(1); // PROGRAMACIÓN
        manager.setParamModId(0); // JAVA DEVELOPER

        result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertEquals(2, manager.getEstructuras().size());

        // Other search
        manager.setParamNombre("pago");
        manager.setParamAreaId(0); // PROGRAMACIÓN
        manager.setParamModId(0); // JAVA DEVELOPER

        result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertEquals(5, manager.getEstructuras().size());
    }

    @Test
    public void mostrarTalleresTest() {
        UIParameter uipFlag = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_ver")).thenReturn(uipFlag);

        // opening subtable
        manager.setVerDetalle(true);
        when(uipFlag.getValue()).thenReturn(manager.isVerDetalle());

        manager.mostrarDetalle(actionEvent);

        assertFalse(manager.isVerDetalle());
        assertTrue(manager.getVerDetImagen().contains("down.png"));
        assertTrue(manager.getVerDetTitulo().contains("Mostrar"));

        // closing subtable
        manager.setVerDetalle(false);
        when(uipFlag.getValue()).thenReturn(manager.isVerDetalle());

        manager.mostrarDetalle(actionEvent);

        assertTrue(manager.isVerDetalle());
        assertTrue(manager.getVerDetImagen().contains("up.png"));
        assertTrue(manager.getVerDetTitulo().contains("Ocultar"));
    }

    @Test
    public void autocompleteTest() {
        List<MbEstructuraPagoCL> results = manager.autocomplete("libre");
        assertEquals(3, results.size());
        assertNotEquals(0, results.get(0).getAuConpagId());
        assertFalse(results.get(0).getAuConcepto().isEmpty());

        results = manager.autocomplete("");
        assertTrue(results.isEmpty());
    }

    @Test
    public void openModalForNewEstructuraPagoTest() {
        manager.nuevo(actionEvent);

        assertEquals(0, manager.getiEstpagId());
        assertTrue(manager.getMpEstpagNombre().isEmpty());
        assertTrue(manager.getMpEstpagAbrev().isEmpty());
        assertTrue(manager.getMpModulos().isEmpty());
        assertEquals(0, manager.getMpAreaId());
        assertEquals(15, manager.getMpAreas().size());
        assertEquals(0, manager.getMpModId());
        assertTrue(manager.getDetDetalle().isEmpty());

        assertTrue(manager.getSugConcepto().isEmpty());
        assertEquals(0, manager.getSugConpagId());
        assertTrue(manager.getSugConpagDes().isEmpty());
        assertTrue(manager.getMpTalleres().isEmpty());
        assertEquals(0, manager.getMpTalId());
        assertTrue(manager.getMpEstpagdetDescription().isEmpty());
        assertEquals(0, manager.getMpEstpagdetMonto(), 0);
        assertTrue(manager.getIdsToDelete().isEmpty());

        interactingWithCombos();

        savePaymentStructureWithoutDetails();
        editPaymentStructure();
        deletePaymentStructure();

        // ------------------------------------------------------------------
        manager.nuevo(actionEvent);
        savePaymentStructureWithDetails();
        editPaymentStructureWithDetails();
        deletePaymentStructure();
    }

    private void interactingWithCombos() {
        // ------------------------------------------------------------------
        manager.setMpAreaId(1);
        manager.loadModalModules(actionEvent);
        assertEquals(0, manager.getMpModId());
        assertEquals(10, manager.getMpModulos().size());
        assertEquals(0, manager.getMpTalId());
        assertTrue(manager.getMpTalleres().isEmpty());
        assertTrue(manager.getMpEstpagdetDescription().isEmpty());

        // ------------------------------------------------------------------
        manager.setMpModId(7); // JAVA DEVELOPER
        manager.loadWorkshops(actionEvent);
        assertEquals(0, manager.getMpTalId());
        assertEquals(5, manager.getMpTalleres().size());
        assertTrue(manager.getMpEstpagdetDescription().isEmpty());

        // ------------------------------------------------------------------
        manager.setMpTalId(27);// DESARROLLO DE APLICACIONES WEB CON JAVA - AVANZADO
        manager.loadWorkshopDescription(actionEvent);
        assertFalse(manager.getMpEstpagdetDescription().isEmpty());

        // ------------------------------------------------------------------
        manager.setMpModId(31); // PROGRAMACION WEB
        manager.loadWorkshops(actionEvent);
        assertEquals(8, manager.getMpTalleres().size());
        assertTrue(manager.getMpEstpagdetDescription().isEmpty());

        // ------------------------------------------------------------------
        manager.setMpAreaId(0);
        manager.loadModalModules(actionEvent);
        assertTrue(manager.getMpModulos().isEmpty());
        assertTrue(manager.getMpTalleres().isEmpty());
        assertTrue(manager.getMpEstpagdetDescription().isEmpty());

        // ------------------------------------------------------------------
        manager.setMpModId(0);
        manager.loadWorkshops(actionEvent);
        assertTrue(manager.getMpTalleres().isEmpty());
        assertTrue(manager.getMpEstpagdetDescription().isEmpty());

        // ------------------------------------------------------------------
        manager.setMpTalId(0);
        manager.loadWorkshopDescription(actionEvent);
        assertTrue(manager.getMpEstpagdetDescription().isEmpty());
    }

    private void savePaymentStructureWithoutDetails() {
        manager.guardarEstructura(actionEvent);
        assertTrue(manager.getOncomplete().contains("Los datos de Estructura de Pagos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpModId(79); // PROGRAMA DE INFORMATICA EDUCATIVA - MODULO I
        manager.guardarEstructura(actionEvent);
        assertTrue(manager.getOncomplete().contains("Los datos de Estructura de Pagos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpEstpagNombre("EDP_FAKE_" + UUID.randomUUID().toString()); //
        manager.guardarEstructura(actionEvent);
        assertTrue(manager.getOncomplete().contains("Los datos de Estructura de Pagos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpEstpagAbrev("EDPF");
        manager.guardarEstructura(actionEvent);
        assertTrue(manager.getOncomplete().contains("Registro Satisfactorio"));
    }

    private void editPaymentStructure() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_update")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getiEstpagId());

        manager.actualizarEstructura(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpUpdate"));

        // ------------------------------------------------------------------
        manager.setMpEstpagNombre(manager.getMpEstpagNombre().concat("_EDIT"));
        manager.setMpEstpagAbrev(manager.getMpEstpagAbrev().concat("_0E"));

        // ------------------------------------------------------------------
        manager.setSugConpagId(88); // MODULO COMPLETO CURSO LIBRE
        manager.setMpTalId(376); // COMPUTACION BASICA ORIENTADA A LA EDUCACION
        manager.setMpEstpagdetDescription("DESC_1_EDIT");
        manager.setMpEstpagdetMonto(1000050);
        manager.agregarDetalle(actionEvent);
        assertEquals(1, manager.getDetDetalle().size());

        manager.guardarEstructura(actionEvent);
        assertTrue(manager.getOncomplete().contains("Actualización Satisfactoria"));
    }

    private void deletePaymentStructure() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_delete")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getiEstpagId());

        manager.eliminarEstructura(actionEvent);

        assertTrue(manager.getOncomplete().isEmpty());
    }

    private void savePaymentStructureWithDetails() {
        String uuid = UUID.randomUUID().toString();
        manager.setMpModId(79); // MODULO I :: PROGRAMA DE INFORMATICA EDUCATIVA
        manager.setMpEstpagNombre("EDP_FAKE_" + uuid);
        manager.setMpEstpagAbrev("EDPF");

        // ------------------------------------------------------------------
        manager.agregarDetalle(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son Obligatorios"));

        // ------------------------------------------------------------------
        manager.setSugConpagId(91);
        manager.agregarDetalle(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son Obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpTalId(376);
        manager.agregarDetalle(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son Obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpEstpagdetDescription("DESC_1_" + uuid);
        manager.agregarDetalle(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son Obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpEstpagdetMonto(1000);
        manager.agregarDetalle(actionEvent);
        assertEquals(1, manager.getDetDetalle().size());

        // ------------------------------------------------------------------
        manager.setSugConpagId(93);
        manager.setMpTalId(376);
        manager.setMpEstpagdetDescription("DESC_2_" + uuid);
        manager.setMpEstpagdetMonto(500);
        manager.agregarDetalle(actionEvent);
        assertEquals(2, manager.getDetDetalle().size());

        // ------------------------------------------------------------------
        manager.setSugConpagId(88); // MODULO COMPLETO CURSO LIBRE
        manager.setMpTalId(376);
        manager.setMpEstpagdetDescription("DESC_3_" + uuid);
        manager.setMpEstpagdetMonto(250);
        manager.agregarDetalle(actionEvent);
        assertEquals(3, manager.getDetDetalle().size());

        // ------------------------------------------------------------------
        manager.setSugConpagId(91);
        manager.setMpTalId(376);
        manager.setMpEstpagdetDescription("DESC_4_" + uuid);
        manager.setMpEstpagdetMonto(1000);
        manager.agregarDetalle(actionEvent);
        assertEquals(4, manager.getDetDetalle().size());

        // ------------------------------------------------------------------
        manager.setSugConpagId(91);
        manager.setMpTalId(376);
        manager.setMpEstpagdetDescription("DESC_5_" + uuid);
        manager.setMpEstpagdetMonto(786531);
        manager.agregarDetalle(actionEvent);
        assertEquals(5, manager.getDetDetalle().size());

        // ------------------------------------------------------------------
        manager.setSugConpagId(91);
        manager.setMpTalId(376);
        manager.setMpEstpagdetDescription("DESC_6_" + uuid);
        manager.setMpEstpagdetMonto(6346524);
        manager.agregarDetalle(actionEvent);
        assertEquals(6, manager.getDetDetalle().size());

        // ------------------------------------------------------------------
        manager.setSugConpagId(88); // MODULO COMPLETO CURSO LIBRE
        manager.setMpTalId(376);
        manager.setMpEstpagdetDescription("DESC_7_" + uuid);
        manager.setMpEstpagdetMonto(685632);
        manager.agregarDetalle(actionEvent);
        assertEquals(7, manager.getDetDetalle().size());

        // ------------------------------------------------------------------
        manager.guardarEstructura(actionEvent);
        assertTrue(manager.getOncomplete().contains("Registro Satisfactorio"));
    }

    private void editPaymentStructureWithDetails() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_update")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getiEstpagId());

        manager.actualizarEstructura(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpUpdate"));
        assertEquals(7, manager.getDetDetalle().size());
        // ------------------------------------------------------------------
        MbEstructuraPagoCL b = manager.getDetDetalle().get(1);
        b.editarDetalle(actionEvent);
        assertTrue(b.isDetalleEditar());
        assertFalse(b.isDetalleVer());

        b.setEdiEstpagdetDes("");
        b.setEdiEstpagdetMonto(0);
        b.guardarDetalle(actionEvent);
        assertTrue(b.getOncomplete().contains("Ingrese ambos campos para Guardar"));

        b.setEdiEstpagdetDes("FAKE_EDIT_DESC_002");
        b.guardarDetalle(actionEvent);
        assertTrue(b.getOncomplete().contains("Ingrese ambos campos para Guardar"));

        b.setEdiEstpagdetMonto(989);
        b.guardarDetalle(actionEvent);
        assertFalse(b.isDetalleEditar());
        assertTrue(b.isDetalleVer());

        b.cancelarDetalle(actionEvent);
        assertFalse(b.isDetalleEditar());
        assertTrue(b.isDetalleVer());

        // ------------------------------------------------------------------
        deletePaymentStructureDetail();

        // ------------------------------------------------------------------
        manager.setMpEstpagAbrev(manager.getMpEstpagAbrev().concat("_1E"));
        manager.guardarEstructura(actionEvent);
        assertTrue(manager.getOncomplete().contains("Actualización Satisfactoria"));
    }

    private void deletePaymentStructureDetail() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_det_delete")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getDetDetalle().get(2).getDetEstpagdetId());

        manager.eliminarDetalle(actionEvent);

        assertEquals(6, manager.getDetDetalle().size());
        assertEquals(1, manager.getIdsToDelete().size());

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(manager.getDetDetalle().get(3).getDetEstpagdetId());

        manager.eliminarDetalle(actionEvent);

        assertEquals(5, manager.getDetDetalle().size());
        assertEquals(2, manager.getIdsToDelete().size());

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(manager.getDetDetalle().get(2).getDetEstpagdetId());

        manager.eliminarDetalle(actionEvent);

        assertEquals(4, manager.getDetDetalle().size());
        assertEquals(3, manager.getIdsToDelete().size());

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(manager.getDetDetalle().get(2).getDetEstpagdetId());

        manager.eliminarDetalle(actionEvent);

        assertEquals(3, manager.getDetDetalle().size());
        assertEquals(4, manager.getIdsToDelete().size());
    }
}
