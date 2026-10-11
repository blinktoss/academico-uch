package net.uch.configuracion.jsf;

import net.uch.SpringConfigurationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 13/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbAulaTest extends SpringConfigurationTest {

    private MbAula manager;

    @Before
    public void setUp() {
        manager = new MbAula();
        manager.init();
    }

    @Test
    public void loadMainPageAulasTest() {
        SelectItem[] resultLocales = manager.getParamLocales();
        List<SelectItem> resultPabellones = manager.getParamPabellones();

        assertEquals(1, resultLocales.length);
        assertTrue(resultPabellones.isEmpty());
        // On change
        manager.setParamLocalId(1);
        manager.setearParamLocal(actionEvent);

        resultPabellones = manager.getParamPabellones();

        assertEquals(1, resultLocales.length);
        assertEquals(1, resultPabellones.size());
    }

    @Test
    public void seleccionarTest() {
        manager.setParamDes("20");
        manager.setParamPabellonId(1);

        manager.seleccionar(actionEvent);

        assertFalse(manager.getListaAula().isEmpty());

        MbAula row = manager.getListaAula().get(0);
        assertFalse(row.getRowPabellonValue().isEmpty());
        assertFalse(row.getRowLocalValue().isEmpty());
    }

    @Test
    public void openModalForNewAulaTest() {
        manager.nuevo(actionEvent);

        assertTrue(manager.getAuxDes().isEmpty());
        assertTrue(manager.getMpDes().isEmpty());
        assertEquals(0, manager.getRowCapacidad());
        assertEquals(0, manager.getAuxCapacidad());
        assertEquals(0, manager.getRowPabellonId());
        assertEquals(0, manager.getRowLocalId());
        assertEquals(0, manager.getAuxLocalId());

        SelectItem[] resultLocales = manager.getComboLocales();
        List<SelectItem> resultPabellones = manager.getComboPabellones();

        assertEquals(1, resultLocales.length);
        assertTrue(resultPabellones.isEmpty());

        // On change
        manager.setRowLocalId(1);
        manager.setearLocal(actionEvent);

        resultPabellones = manager.getComboPabellones();

        assertEquals(1, resultLocales.length);
        assertEquals(1, resultPabellones.size());
    }

    @Test
    public void saveNewEditDeleteAulaTest() {
        // New
        manager.setMpDes("FAKE_AULA");
        manager.setRowCapacidad(40);
        manager.setRowPabellonId(1);

        manager.grabar(actionEvent);

        assertNotEquals(0, manager.getRowId());
        assertTrue(manager.getOncomplete().contains("mp"));

        // Find recent insert aula with pabellon
        manager.setParamDes("FAKE");
        manager.setParamPabellonId(1);
        manager.seleccionar(actionEvent);
        assertEquals(1, manager.getListaAula().size());

        // Edit
        UIParameter uiParameterId = mock(UIParameter.class);
        UIParameter uiParameterDescripcion = mock(UIParameter.class);
        UIParameter uiParameterCapacidad = mock(UIParameter.class);
        UIParameter uiParameterPabellon = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getRowId());
        when(uiComponent.findComponent("descripcion")).thenReturn(uiParameterDescripcion);
        when(uiParameterDescripcion.getValue()).thenReturn("FAKE_AULA_EDIT");
        when(uiComponent.findComponent("capacidad")).thenReturn(uiParameterCapacidad);
        when(uiParameterCapacidad.getValue()).thenReturn(80);
        when(uiComponent.findComponent("pabellon")).thenReturn(uiParameterPabellon);
        when(uiParameterPabellon.getValue()).thenReturn(1);

        manager.editar(actionEvent);

        assertTrue(manager.getView());
        assertFalse(manager.getEditable());
        assertFalse(manager.isEditable2());
        assertFalse(manager.isVisible());
        assertTrue(manager.getOncomplete().contains("Actualizaron"));

        // Delete
        when(uiComponent.findComponent("delete")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getRowId());

        manager.eliminarFila(actionEvent);

        // Find recent deleted aula
        manager.setParamDes("FAKE");
        manager.setParamPabellonId(0);
        manager.seleccionar(actionEvent);
        assertTrue(manager.getListaAula().isEmpty());
    }

    @Test
    public void showRowForEditAndCancelTest() {
        // Show row for edition
        manager.editarFila(actionEvent);

        assertEquals(manager.getParamDes(), manager.getAuxDes());
        assertEquals(manager.getRowCapacidad(), manager.getAuxCapacidad());
        assertEquals(manager.getRowLocalId(), manager.getAuxLocalId());
        assertFalse(manager.getView());
        assertTrue(manager.getEditable());
        assertTrue(manager.isEditable2());
        assertTrue(manager.isVisible());

        // Cancel edition
        manager.cancelar(actionEvent);

        assertEquals(manager.getAuxDes(), manager.getParamDes());
        assertEquals(manager.getAuxCapacidad(), manager.getRowCapacidad());
        assertEquals(manager.getAuxLocalId(), manager.getRowLocalId());
        assertTrue(manager.getView());
        assertFalse(manager.getEditable());
        assertFalse(manager.isEditable2());
        assertFalse(manager.isVisible());
    }
}
