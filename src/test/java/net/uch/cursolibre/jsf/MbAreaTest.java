package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 21/01/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbAreaTest extends SpringConfigurationTest {

    private MbArea manager;

    @Before
    public void setUp() {
        manager = new MbArea();
    }

    @Test
    public void seleccionarTest() {
        manager.setParamDescripcion("otro");

        manager.seleccionar();

        assertEquals(1, manager.getAreas().size());
    }

    @Test
    public void openModalForNewAreaTest() {
        manager.nuevo(actionEvent);

        assertTrue(manager.getOncomplete().contains("mp1"));

        manager.setMpAreCodigo("FAKE_CODE_AREA");
        manager.setMpAreDescripcion("FAKE_DESCRIPTION_AREA");

        manager.guardarArea(actionEvent);
        assertTrue(manager.getOncomplete().contains("Registro Satisfactorio"));

        manager.setRowAreId(manager.getMpAreId());

        UIParameter uipId = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_update")).thenReturn(uipId);
        when(uipId.getValue()).thenReturn(manager.getRowAreId());

        manager.actualizarArea(actionEvent);

        assertNotEquals(0, manager.getMpAreId());

        assertTrue(manager.getOncomplete().contains("mp1"));

        manager.setMpAreCodigo("FAKE_EDIT_AREA_CODE");
        manager.setMpAreDescripcion("FAKE_EDIT_DESCRIPTION_AREA");

        manager.guardarArea(actionEvent);
        assertTrue(manager.getOncomplete().contains("Actualización Satisfactoria"));

        manager.setRowAreId(manager.getMpAreId());

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_delete")).thenReturn(uipId);
        when(uipId.getValue()).thenReturn(manager.getRowAreId());

        manager.eliminarArea(actionEvent);
    }
}
