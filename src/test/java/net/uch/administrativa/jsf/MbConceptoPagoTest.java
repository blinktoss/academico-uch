package net.uch.administrativa.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 15/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbConceptoPagoTest extends SpringConfigurationTest {

    private MbConceptoPago manager;

    @Before
    public void setUp() {
        manager = new MbConceptoPago();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageConceptoDePagoTest() {
        SelectItem[] catalogoBuscar = manager.getParamTipos();
        SelectItem[] catalogo = manager.getComboCatalogo();

        assertEquals(3, catalogoBuscar.length);
        assertEquals(3, catalogo.length);
    }

    @Test
    public void seleccionarTest() {
        manager.setParamDescripcion("");
        manager.setParamCodigo("");
        manager.setParamTipoId("007003"); // Curso Libre

        manager.seleccionar();

        assertEquals(15, manager.getListaConceptoPago().size());
    }

    @Test
    public void openModalForNewEditAndDeleteConceptoDePagoTest() {
        // Open modal
        manager.nuevo();

        assertEquals(0, manager.getMpId());
        assertTrue(manager.getMpCodigo().isEmpty());
        assertTrue(manager.getMpDescripcion().isEmpty());
        assertEquals(0, manager.getMpMonto(), 0);
        assertEquals("0", manager.getMpTipoId());

        // Saving new concept
        manager.setMpCodigo("FAKE_CODE");
        manager.setMpDescripcion("FAKE_DESCRIPTION");
        manager.setMpMonto(150);
        manager.setMpTipoId("007001");

        manager.insertar(actionEvent);

        assertNotEquals(0, manager.getMpId());

        // Opening modal for editing
        manager.setRowId(manager.getMpId());
        manager.setParamCodigo("FAKE_EDIT_");
        manager.setParamDescripcion("FAKE_EDIT_DESCRIPTION");
        manager.setRowMonto(100);
        manager.setRowIdTipo(manager.getMpTipoId());
        manager.setRowCreacion(new Date());

        UIParameter uipId = mock(UIParameter.class);
        UIParameter uipCodigo = mock(UIParameter.class);
        UIParameter uipDescripcion = mock(UIParameter.class);
        UIParameter uipMonto = mock(UIParameter.class);
        UIParameter uipTipo = mock(UIParameter.class);
        UIParameter uipCreacion = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uipId);
        when(uipId.getValue()).thenReturn(manager.getRowId());
        when(uiComponent.findComponent("p_codigo")).thenReturn(uipCodigo);
        when(uipCodigo.getValue()).thenReturn(manager.getParamCodigo());
        when(uiComponent.findComponent("p_descripcion")).thenReturn(uipDescripcion);
        when(uipDescripcion.getValue()).thenReturn(manager.getParamDescripcion());
        when(uiComponent.findComponent("p_monto")).thenReturn(uipMonto);
        when(uipMonto.getValue()).thenReturn(manager.getRowMonto());
        when(uiComponent.findComponent("p_tipo")).thenReturn(uipTipo);
        when(uipTipo.getValue()).thenReturn(manager.getRowIdTipo());
        when(uiComponent.findComponent("p_creacion")).thenReturn(uipCreacion);
        when(uipCreacion.getValue()).thenReturn(manager.getRowCreacion());

        manager.editarFila(actionEvent);

        // Updating
        manager.insertar(actionEvent);

        assertNotEquals(0, manager.getMpId());

        // Deleting
        manager.eliminarFila(actionEvent);

        manager.setParamDescripcion("FAKE");
        manager.setParamCodigo("FAKE");
        manager.setParamTipoId("0"); // Interno

        manager.seleccionar();

        assertTrue(manager.getListaConceptoPago().isEmpty());
    }
}
