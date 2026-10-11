package net.uch.configuracion.jsf;

import net.uch.SpringConfigurationTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static net.uch.common.Constants.ACTION_IMAGES_ACTIVAR;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created on 20/07/2019.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbUsuarioTest extends SpringConfigurationTest {

    private MbUsuario manager;

    @Before
    public void setUp() {
        manager = new MbUsuario();
        manager.init();
    }

    @Test
    public void getUsuRolDescripcionTest() {
        SelectItem[] result = manager.getMpRoles();

        assertEquals(23, result.length);
    }

    @Test
    public void seleccionarTest() {
        manager.setParamUsuario("ado");
        manager.setParamRolId(0);

        manager.seleccionar(actionEvent);

        assertFalse(manager.getListUsuarios().isEmpty());
        assertEquals(8, manager.getListUsuarios().size());

        MbUsuario row = manager.getListUsuarios().get(0);
        assertNotEquals(0, row.getRowUsuId());
        assertFalse(row.getRowUsuario().isEmpty());
        assertFalse(row.getRowPsw().isEmpty());
        assertFalse(row.getRowRolTipo().isEmpty());
        assertEquals(ACTION_IMAGES_ACTIVAR, row.getRowVigente());

        // ------------------------------------------------------------------

        manager.setParamUsuario("");
        manager.setParamRolId(2);

        manager.seleccionar(actionEvent);

        assertFalse(manager.getListUsuarios().isEmpty());
        assertEquals(32, manager.getListUsuarios().size());
    }

    @Test
    public void seleccionarEmptyTest() {
        manager.setParamUsuario("");
        manager.setParamRolId(0);

        manager.seleccionar(actionEvent);

        assertTrue(manager.getParamUsuario().isEmpty());
        assertTrue(manager.getListUsuarios().isEmpty());
    }

    @Test
    public void crearNuevoUsuario() {
        manager.nuevo(actionEvent);

        assertEquals(0, manager.getMpRolId().intValue());

        // ------------------------------------------------------------------
        manager.crearUsuario(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los campos"));

        // ------------------------------------------------------------------
        manager.setRowUsuario("FAKE_USER_".concat(RandomStringUtils.randomNumeric(4)));
        manager.crearUsuario(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los campos"));

        // ------------------------------------------------------------------
        manager.setRowPsw("F4K3_P4SSWD");
        manager.crearUsuario(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los campos"));

        // ------------------------------------------------------------------
        manager.setMpRolId(6);

        manager.crearUsuario(actionEvent);

        assertTrue(manager.getOncomplete().contains("mp"));

        // ------------------------------------------------------------------

        manager.nuevo(actionEvent);

        manager.setRowUsuario("FAKE_USER_".concat(RandomStringUtils.randomNumeric(4)));
        manager.setRowPsw("F4K3_P4SSWD");
        manager.setMpRolId(6);
        manager.setMpVigente(true);
        manager.crearUsuario(actionEvent);

        assertTrue(manager.getOncomplete().contains("mp"));

        manager.setParamUsuario("FAKE_USER_");
        manager.seleccionar(actionEvent);
        assertFalse(manager.getListUsuarios().isEmpty());
    }

    @Test
    public void cerrarSesionTest() {
        when(externalContext.getSession(true)).thenReturn(session);
        when(session.getAttributeNames()).thenReturn(Collections.enumeration(
                Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString())));

        String result = manager.cerrar();

        assertEquals("index", result);
    }
}
