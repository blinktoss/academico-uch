package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 22/01/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbPlanCurricularCLTest extends SpringConfigurationTest {

    private MbPlanCurricularCL manager;

    @Before
    public void setUp() {
        manager = new MbPlanCurricularCL();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] areas = manager.getAreas();
        List<SelectItem> modulos = manager.getParamModulos();

        assertEquals(0, manager.getParamAreaId());
        assertEquals(15, areas.length);
        assertEquals(0, manager.getParamModId());
        assertTrue(modulos.isEmpty());

        // On Change
        manager.setParamAreaId(1);

        manager.verModulos(actionEvent);

        areas = manager.getAreas();
        modulos = manager.getParamModulos();

        assertEquals(15, areas.length);
        assertEquals(10, modulos.size());

        // On Change
        manager.setParamAreaId(0);

        manager.verModulos(actionEvent);

        areas = manager.getAreas();
        modulos = manager.getParamModulos();

        assertEquals(15, areas.length);
        assertTrue(modulos.isEmpty());
    }

    @Test
    public void seleccionarTest() {
        // Module Java Developer
        manager.setParamModId(7);
        manager.setParamDescripcion("");

        manager.seleccionar(actionEvent);

        assertEquals(1, manager.getPlanes().size());

        // ------------------------------------------------------------------
        manager.setParamDescripcion("PC");

        manager.seleccionar(actionEvent);

        assertEquals(1, manager.getPlanes().size());

        MbPlanCurricularCL row = manager.getPlanes().get(0);
        assertNotEquals(0, row.getRowPlaId());
        assertFalse(row.getRowPlaCodigo().isEmpty());
        assertFalse(row.getRowPlaDescripcion().isEmpty());
        assertFalse(row.getRowModDescripcion().isEmpty());
        assertFalse(row.getRowImagenPlaVigente().isEmpty());
        assertFalse(row.getRowImagenPlaActual().isEmpty());
    }

    @Test
    public void loadModalPlanCurricularMaintancePageTest() {
        SelectItem[] areas = manager.getAreas();
        List<SelectItem> modulos = manager.getMpModulos();

        assertEquals(0, manager.getMpAreaId());
        assertEquals(15, areas.length);
        assertEquals(0, manager.getMpModId());
        assertTrue(modulos.isEmpty());

        // On Change
        manager.setMpAreaId(1);

        manager.onChangeModulos(actionEvent);

        areas = manager.getAreas();
        modulos = manager.getMpModulos();

        assertEquals(15, areas.length);
        assertEquals(10, modulos.size());

        // On Change
        manager.setMpAreaId(0);

        manager.onChangeModulos(actionEvent);

        areas = manager.getAreas();
        modulos = manager.getMpModulos();

        assertEquals(15, areas.length);
        assertTrue(modulos.isEmpty());
    }

    @Test
    public void openModalForNewPlanCurricularTest() {
        manager.nuevo(actionEvent);

        assertEquals(0, manager.getMpPlaId());
        assertTrue(manager.getMpPlaCodigo().isEmpty());
        assertTrue(manager.getMpPlaDescripcion().isEmpty());
        assertEquals("1", manager.getMpPlaVigente());
        assertEquals(2, manager.getMpVigentes().length);
        assertEquals("1", manager.getMpPlaActual());
        assertTrue(manager.getMpModulos().isEmpty());
        assertFalse(manager.isMpEditDisable());
        assertTrue(manager.getOncomplete().contains("mpUpdate"));

        savingNewPlanCurricularTest();

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_update")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getMpPlaId());

        manager.actualizarPlanCurricular(actionEvent);
        assertTrue(manager.isMpEditDisable());
        assertTrue(manager.getOncomplete().contains("mpUpdate"));

        // ------------------------------------------------------------------
        manager.setMpPlaCodigo("FAKE_" + RandomStringUtils.randomAlphanumeric(15));
        manager.setMpPlaDescripcion("FAKE_" + RandomStringUtils.randomAlphanumeric(95));
        manager.guardarPlanCurricular(actionEvent);
        assertTrue(manager.getOncomplete().contains("Actualización Satisfactoria"));

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_delete")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getMpPlaId());

        manager.eliminarPlanCurricular(actionEvent);
    }

    private void savingNewPlanCurricularTest() {
        manager.setMpPlaCodigo(RandomStringUtils.randomAlphanumeric(20));
        manager.guardarPlanCurricular(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpPlaDescripcion(RandomStringUtils.randomAlphanumeric(100));
        manager.guardarPlanCurricular(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpAreaId(0);
        manager.onChangeModulos(actionEvent);
        assertTrue(manager.getMpModulos().isEmpty());

        manager.setMpAreaId(16); // OTROS
        manager.onChangeModulos(actionEvent);
        assertEquals(1, manager.getMpModulos().size());

        manager.guardarPlanCurricular(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpModId(66); // OTROS
        manager.setMpPlaVigente("0");
        manager.guardarPlanCurricular(actionEvent);
        assertTrue(manager.getOncomplete().contains("Registro Satisfactorio"));
    }
}
