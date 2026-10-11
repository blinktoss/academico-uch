package net.uch.configuracion.jsf;

import net.uch.SpringConfigurationTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 15/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbLocalTest extends SpringConfigurationTest {

    private MbLocal manager;

    @Before
    public void setUp() {
        manager = new MbLocal();
    }

    @Test
    public void seleccionarTest() {
        manager.setParamDescription("");

        manager.seleccionar(actionEvent);

        assertEquals(1, manager.getListaLocal().size());

        MbLocal l = manager.getListaLocal().get(0);
        assertFalse(l.getParamDescription().isEmpty());
    }

    @Test
    public void openModalForNewLocalTest() {
        manager.nuevo(actionEvent);
        assertTrue(manager.getModalDescription().isEmpty());

        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalDescription("FAKE_" + RandomStringUtils.randomAlphanumeric(40));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se creo un Local correctamente"));

        // ------------------------------------------------------------------
        editarLocalTest();

        // ------------------------------------------------------------------
        Map<String, Object> attributes = Collections.singletonMap("local", manager);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);
        manager.eliminarFila(actionEvent);
        manager.eliminar(actionEvent);
    }

    private void editarLocalTest() {
        manager.editarFila(actionEvent);
        assertNotEquals(0, manager.getLocalId());
        assertEquals(manager.getParamDescription(), manager.getModalDescription());
        assertFalse(manager.isView());
        assertTrue(manager.isEditable());

        // ------------------------------------------------------------------
        manager.cancelar(actionEvent);
        assertEquals(manager.getModalDescription(), manager.getParamDescription());
        assertTrue(manager.isView());
        assertFalse(manager.isEditable());

        // ------------------------------------------------------------------
        manager.setParamDescription("");
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Descripcion"));

        // ------------------------------------------------------------------
        manager.setParamDescription("FAKE_EDIT_" + RandomStringUtils.randomAlphanumeric(35));
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se Actualizo"));
    }
}
