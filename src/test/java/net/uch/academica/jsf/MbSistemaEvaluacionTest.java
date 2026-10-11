package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 20/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbSistemaEvaluacionTest extends SpringConfigurationTest {

    private MbSistemaEvaluacion manager;

    @Before
    public void setUp() {
        manager = new MbSistemaEvaluacion();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void seleccionarTest() {
        manager.setNombreF("SISTEMA");

        manager.seleccionar(actionEvent);

        assertEquals(7, manager.getTablaSisEva().size());

        MbSistemaEvaluacion r = manager.getTablaSisEva().get(0);

        assertNotEquals(0, r.getIdS());
        assertFalse(r.getCodigoS().isEmpty());
        assertFalse(r.getNombreS().isEmpty());
        assertNotNull(r.getCreacionS());
        assertFalse(r.getVigenteImg().isEmpty());
        assertFalse(r.getDetalle().isEmpty());
    }

    @Test
    public void openModalForNuevoTest() {
        manager.setNombreF("fake");

        manager.nuevo(actionEvent);

        assertEquals(0, manager.getIdI());
        assertTrue(manager.getCodigoI().isEmpty());
        assertTrue(manager.getNombreI().isEmpty());
        assertFalse(manager.isVigenteI());
        assertTrue(manager.getTablaSisEvaDetalle().isEmpty());
        assertTrue(manager.getCodigoDet().isEmpty());
        assertTrue(manager.getNombreDet().isEmpty());
        assertEquals(0, manager.getPesoDet());

        newEvaluationSystem();
        editEvaluationSystem();
        deleteEvaluationSystem();

        // ------------------------------------------------------------------
        manager.nuevo(actionEvent);

        newEvaluationSystemWithDetails();
        editEvaluationSystemWithDetails();
        deleteEvaluationSystem();
    }

    private void newEvaluationSystem() {
        manager.guardar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Complete todos los campos"));

        String r = RandomStringUtils.randomAlphanumeric(10);
        manager.setCodigoI(r);
        manager.guardar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Complete todos los campos"));

        manager.setNombreI("FAKE_" + r);
        manager.guardar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se guardo el registro correctamente"));
    }

    private void editEvaluationSystem() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_s")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getIdI());

        manager.editar(actionEvent);

        assertTrue(manager.getOncomplete().isEmpty());

        // ------------------------------------------------------------------
        manager.setNombreI(manager.getNombreI() + "_EDIT");
        manager.setVigenteI(true);
        manager.guardar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se actualizo el registro correctamente"));
    }

    private void deleteEvaluationSystem() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_s")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getIdI());

        manager.eliminar(actionEvent);
    }

    private void newEvaluationSystemWithDetails() {
        String r = RandomStringUtils.randomAlphanumeric(10);
        manager.setCodigoI(r);
        manager.setNombreI("FAKE_" + r);

        // ------------------------------------------------------------------
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese datos para el detalle"));

        // ------------------------------------------------------------------
        String rd = RandomStringUtils.randomAlphanumeric(5);
        manager.setCodigoDet(rd);
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese datos para el detalle"));

        // ------------------------------------------------------------------
        manager.setNombreDet("FAKE_DETAIL_" + rd);
        manager.agregar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese datos para el detalle"));

        // ------------------------------------------------------------------
        manager.setPesoDet(4);
        manager.agregar(actionEvent);
        assertEquals(1, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        rd = RandomStringUtils.randomAlphanumeric(5);
        manager.setCodigoDet(rd);
        manager.setNombreDet("FAKE_DETAIL_" + rd);
        manager.setPesoDet(8);
        manager.agregar(actionEvent);
        assertEquals(2, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        rd = RandomStringUtils.randomAlphanumeric(5);
        manager.setCodigoDet(rd);
        manager.setNombreDet("FAKE_DETAIL_" + rd);
        manager.setPesoDet(12);
        manager.agregar(actionEvent);
        assertEquals(3, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        rd = RandomStringUtils.randomAlphanumeric(5);
        manager.setCodigoDet(rd);
        manager.setNombreDet("FAKE_DETAIL_" + rd);
        manager.setPesoDet(16);
        manager.agregar(actionEvent);
        assertEquals(4, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        manager.guardar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se guardo el registro correctamente"));
    }

    private void editEvaluationSystemWithDetails() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_s")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getIdI());

        manager.editar(actionEvent);

        // ------------------------------------------------------------------
        MbSistemaEvaluacion b = manager.getTablaSisEvaDetalle().get(2);
        b.edit(actionEvent);
        assertFalse(b.isView());
        assertTrue(b.isEditable());
        assertTrue(b.isVisible());

        b.aceptar(actionEvent);
        assertTrue(b.isView());
        assertFalse(b.isEditable());
        assertFalse(b.isVisible());

        b.cancelar(actionEvent);
        assertTrue(b.isView());
        assertFalse(b.isEditable());
        assertFalse(b.isVisible());

        // ------------------------------------------------------------------
        UIParameter uip = mock(UIParameter.class);
        when(uiComponent.findComponent("id_det")).thenReturn(uip);
        when(uip.getValue()).thenReturn(b.getIdDet());

        manager.quitar(actionEvent);
        assertEquals(4, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        String rd = RandomStringUtils.randomAlphanumeric(5);
        manager.setCodigoDet(rd);
        manager.setNombreDet("FAKE_EDIT_DETAIL_" + rd);
        manager.setPesoDet(30);
        manager.agregar(actionEvent);
        assertEquals(5, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        when(uip.getValue()).thenReturn(manager.getTablaSisEvaDetalle().get(4).getIdDet());
        manager.quitar(actionEvent);
        assertEquals(4, manager.getTablaSisEvaDetalle().size());

        // ------------------------------------------------------------------
        manager.setNombreI("EDIT_DETAILS_" + manager.getNombreI());
        manager.guardar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se actualizo el registro correctamente"));
    }

    @Test
    public void openAndCloseSubTable() {
        UIParameter uipFlag = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("flag")).thenReturn(uipFlag);

        manager.setFlagVer("0");

        when(uipFlag.getValue()).thenReturn(manager.getFlagVer());
        manager.openDetail(actionEvent);

        assertTrue(manager.isVer());
        assertEquals("1", manager.getFlagVer());
        assertTrue(manager.getImagen().contains("up.png"));

        // Closing
        manager.setFlagVer("1");

        when(uipFlag.getValue()).thenReturn(manager.getFlagVer());
        manager.openDetail(actionEvent);

        assertFalse(manager.isVer());
        assertEquals("0", manager.getFlagVer());
        assertTrue(manager.getImagen().contains("down.png"));
    }
}
