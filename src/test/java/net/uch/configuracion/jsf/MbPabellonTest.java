package net.uch.configuracion.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 13/09/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbPabellonTest extends SpringConfigurationTest {

    private MbPabellon manager;

    @Before
    public void setUp() {
        manager = new MbPabellon();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPagePabellonTest() {
        SelectItem[] result = manager.getParamComboLocales();

        assertEquals(1, result.length);
    }

    @Test
    public void seleccionarTest() {
        manager.setParamNombre("");
        manager.setParamIdLocal(0);

        manager.seleccionar(actionEvent);

        assertEquals(1, manager.getListaPabellon().size());

        MbPabellon row = manager.getListaPabellon().get(0);
        assertFalse(row.getRowLocalName().isEmpty());
    }

    @Test
    public void openModalForNewLocalTest() {
        manager.nuevo(actionEvent);
        assertTrue(manager.getModalNombre().isEmpty());
        assertTrue(manager.getModalDescription().isEmpty());

        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalNombre(RandomStringUtils.randomAlphanumeric(50));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalIdLocal(1); // OLIVOS
        manager.setModalDescription("FAKE_" + RandomStringUtils.randomAlphanumeric(100));
        manager.grabar(actionEvent);

        assertTrue(manager.getOncomplete().contains("Se creo un Pabellon correctamente"));

        // ------------------------------------------------------------------
        editarPabellonTest();

        // ------------------------------------------------------------------
        manager.setData(manager);
        assertNotEquals(0, manager.getData().getPabellonId());
        assertNotEquals(0, manager.getData().getParamIdLocal());
        manager.eliminar(actionEvent);
    }

    private void editarPabellonTest() {
        manager.editarFila(actionEvent);
        assertEquals(manager.getParamNombre(), manager.getModalNombre());
        assertEquals(manager.getPabellonDescription(), manager.getModalDescription());
        assertFalse(manager.isView());
        assertTrue(manager.isEditable());

        // ------------------------------------------------------------------
        manager.cancelar(actionEvent);
        assertEquals(manager.getModalNombre(), manager.getParamNombre());
        assertEquals(manager.getModalDescription(), manager.getPabellonDescription());
        assertTrue(manager.isView());
        assertFalse(manager.isEditable());

        // ------------------------------------------------------------------
        manager.setParamNombre("");
        manager.setParamIdLocal(0);
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        // ------------------------------------------------------------------
        manager.setParamNombre("FAKE_EDIT_" + RandomStringUtils.randomAlphanumeric(40));
        manager.setPabellonDescription(RandomStringUtils.randomAlphanumeric(100));
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        // ------------------------------------------------------------------
        manager.setParamIdLocal(1);
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se Actualizaron"));
    }
}
