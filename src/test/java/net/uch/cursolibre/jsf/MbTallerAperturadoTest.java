package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.cursolibre.jsf.beans.BeanClSisEvaPersonalizado;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 14/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbTallerAperturadoTest extends SpringConfigurationTest {

    private MbTallerAperturado manager;

    @Before
    public void setUp() {
        manager = new MbTallerAperturado();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadTreeAndSelectNodeTest() {
        TreeNode<String> result = manager.getArbol();

        assertNotNull(result);
        assertNotNull(manager.getNodos());

        String nodeId = "2:1:1";
        NodeSelectedEvent nodeSelectedEvent = mock(NodeSelectedEvent.class);
        UITree tree = mock(UITree.class);

        when(nodeSelectedEvent.getComponent()).thenReturn(tree);
        when(tree.getRowKey()).thenReturn(nodeId);

        manager.seleccion(nodeSelectedEvent);

        assertFalse(manager.getLblModulo().isEmpty());
        assertFalse(manager.getLblCurso().isEmpty());
        assertEquals(3, manager.getTalleres().size());

        MbTallerAperturado row = manager.getTalleres().get(0);
        assertNotEquals(0, row.getRowId());
        assertFalse(row.getRowCodigo().isEmpty());
        assertFalse(row.getRowCodigo().isEmpty());

        MbTallerAperturado sub = row.getSubTalleresAperturados().get(0);
        assertNotEquals(0, sub.getSubTalapeId());
        assertFalse(sub.getSubTalapeDesc().isEmpty());
        assertFalse(sub.getSubSisEvaluacion().isEmpty());
        assertNotEquals(0, sub.getSubTalapeNumHoras());
        assertFalse(sub.getSubImagenAperturar().isEmpty());
        assertFalse(sub.getSubImagenVigente().isEmpty());
        assertFalse(sub.getSubImagenActual().isEmpty());

        // ------------------------------------------------------------------
        nodeId = "2:1";

        when(nodeSelectedEvent.getComponent()).thenReturn(tree);
        when(tree.getRowKey()).thenReturn(nodeId);
        manager.seleccion(nodeSelectedEvent);
    }

    @Test
    public void openModalForNuevoTallerAperturadoTest() {
        manager.setRowId(77);
        manager.setLblModulo("LIB-CD");
        manager.setLblCurso("LIBRO + CD");

        saveOpenWorkshop();
        editOpenWorkshop();
        deleteOpenWorkshop();

        saveOpenWorkshopWithEvaluationSystems();
        editOpenWorkshopWithEvaluationSystems();
        deleteOpenWorkshop();
    }

    private void saveOpenWorkshop() {
        UIParameter uipIdTallerTmp = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_taller_tmp")).thenReturn(uipIdTallerTmp);
        when(uipIdTallerTmp.getValue()).thenReturn(manager.getRowId()); // MATERIAL INGLES | LIBRO + CD

        manager.aperturarTaller(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpApertura"));
        assertEquals(manager.getRowId(), manager.getMpTalId());
        assertEquals(0, manager.getSubTalapeId());
        assertFalse(manager.getSubTalapeDesc().isEmpty());
        assertEquals(0, manager.getSubTalapeNumHoras());
        assertTrue(manager.isMpTalapeAperturado());
        assertTrue(manager.isMpTalapeVigente());
        assertTrue(manager.isPersonalizado());
        assertEquals(3, manager.getMpSisEvaluaciones().length);
        assertTrue(manager.getSep().getDetalles().isEmpty());
        assertEquals(0, manager.getMpSisevaId());

        manager.guardarTaller(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar un número de horas y un sistema de evaluación para Guardar"));

        // ------------------------------------------------------------------
        manager.setSubTalapeNumHoras(4 * 2 * 8);
        manager.guardarTaller(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar un número de horas y un sistema de evaluación para Guardar"));

        // On Change Sist. Evalac.
        manager.setMpSisevaId(3);
        manager.cambioSisEvaluacion(actionEvent);

        assertEquals(2, manager.getSep().getDetalles().size());
        manager.guardarTaller(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar los Sistemas de Evaluacion personalizados para Guardar"));

        // ------------------------------------------------------------------
        manager.setPersonalizado(false);
        manager.setSubTalapeDesc(manager.getSubTalapeDesc().concat("_").concat(UUID.randomUUID().toString()));
        manager.setMpTalapeAperturado(true);

        manager.guardarTaller(actionEvent);
        assertTrue(manager.getOncomplete().contains("Apertura Satisfactoria"));
        assertNotEquals(0, manager.getSubTalapeId());
        assertTrue(manager.getMpnSisEvaPer().isEmpty());
    }

    private void editOpenWorkshop() {
        UIParameter uipIdTallerTmp = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_taller_tmp")).thenReturn(uipIdTallerTmp);
        when(uipIdTallerTmp.getValue()).thenReturn(manager.getRowId()); // MATERIAL INGLES | LIBRO + CD

        when(uiComponent.findComponent("id_taller_aper_tmp")).thenReturn(uipIdTallerTmp);
        when(uipIdTallerTmp.getValue()).thenReturn(manager.getSubTalapeId());

        manager.aperturarTaller(actionEvent);

        assertEquals(2, manager.getMpnSisEvaPer().size());

        // ------------------------------------------------------------------
        BeanClSisEvaPersonalizado b = manager.getMpnSisEvaPer().get(1);
        b.edit(actionEvent);
        assertFalse(b.isView());
        assertTrue(b.isEditable());
        assertTrue(b.isVisible());
        assertTrue(b.isEditActive());

        b.aceptar(actionEvent);
        assertTrue(b.isView());
        assertFalse(b.isEditable());
        assertFalse(b.isVisible());
        assertFalse(b.isEditActive());

        b.cancelar(actionEvent);
        assertTrue(b.isView());
        assertFalse(b.isEditable());
        assertFalse(b.isVisible());
        assertFalse(b.isEditActive());

        // ------------------------------------------------------------------
        manager.setMpTalapeAperturado(false);

        manager.guardarTaller(actionEvent);

        assertTrue(manager.getOncomplete().contains("Actualización Satisfactoria"));
    }

    private void saveOpenWorkshopWithEvaluationSystems() {
        UIParameter uipIdTallerTmp = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_taller_tmp")).thenReturn(uipIdTallerTmp);
        when(uipIdTallerTmp.getValue()).thenReturn(manager.getRowId()); // MATERIAL INGLES | LIBRO + CD
        when(uiComponent.findComponent("id_taller_aper_tmp")).thenReturn(null);

        manager.aperturarTaller(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpApertura"));

        // On Change Sist. Evalac.
        manager.setMpSisevaId(2);
        manager.cambioSisEvaluacion(actionEvent);

        assertEquals(2, manager.getSep().getDetalles().size());

        // ------------------------------------------------------------------
        UIParameter uipPTalapeId = mock(UIParameter.class);

        manager.getSep().setDetalle(3);
        manager.getSep().setCodigo("FAKESISEV1");
        manager.getSep().setNombre("FAKE_SISEVA_NAME_1");
        manager.getSep().setPeso(2);
        manager.getSep().setExaSemana("4");
        manager.getSep().setOrden(1);

        when(uiComponent.findComponent("p_talape_id")).thenReturn(uipPTalapeId);
        when(uipPTalapeId.getValue()).thenReturn(0);

        manager.insertarSisEvaPer(actionEvent);

        assertEquals(1, manager.getMpnSisEvaPer().size());

        // ------------------------------------------------------------------
        manager.getSep().setDetalle(4);
        manager.getSep().setCodigo("FAKESISEV2");
        manager.getSep().setNombre("FAKE_SISEVA_NAME_2");
        manager.getSep().setPeso(4);
        manager.getSep().setExaSemana("8");
        manager.getSep().setOrden(2);

        when(uiComponent.findComponent("p_talape_id")).thenReturn(uipPTalapeId);
        when(uipPTalapeId.getValue()).thenReturn(0);

        manager.insertarSisEvaPer(actionEvent);

        assertEquals(2, manager.getMpnSisEvaPer().size());

        // To save
        manager.setSubTalapeDesc(manager.getSubTalapeDesc().concat("_")
                .concat(UUID.randomUUID().toString()));
        manager.setSubTalapeNumHoras(4 * 3 * 8);
        manager.setMpTalapeAperturado(true);
        manager.setMpTalapeVigente(false);

        manager.guardarTaller(actionEvent);

        assertTrue(manager.getOncomplete().contains("Apertura Satisfactoria"));
    }

    private void editOpenWorkshopWithEvaluationSystems() {
        UIParameter uipIdTallerTmp = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_taller_tmp")).thenReturn(uipIdTallerTmp);
        when(uipIdTallerTmp.getValue()).thenReturn(manager.getRowId()); // MATERIAL INGLES | LIBRO + CD

        when(uiComponent.findComponent("id_taller_aper_tmp")).thenReturn(uipIdTallerTmp);
        when(uipIdTallerTmp.getValue()).thenReturn(manager.getSubTalapeId());

        manager.aperturarTaller(actionEvent);

        assertEquals(2, manager.getMpnSisEvaPer().size());

        // ------------------------------------------------------------------
        manager.setMpTalapeAperturado(false);

        manager.guardarTaller(actionEvent);

        assertTrue(manager.getOncomplete().contains("Actualización Satisfactoria"));
    }

    private void deleteOpenWorkshop() {
        when(uiComponent.findComponent("id_taller_aper_tmp")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getSubTalapeId());

        manager.prepararEliminacionTalape(actionEvent);

        assertTrue(manager.isMpElimBand());

        manager.eliminarTallerAperturado(actionEvent);

        assertEquals(0, manager.getMpElimTalapeId());
        assertTrue(manager.getMpElimTalapeDesc().isEmpty());
    }

    @Test
    public void mostrarDetalleTest() {
        manager.setVerDetalle(true);

        manager.mostrarDetalle(actionEvent);

        assertFalse(manager.isVerDetalle());
        assertTrue(manager.getRowImagenMostrar().contains("down.png"));
        assertTrue(manager.getRowTextoMostrar().contains("Mostrar"));

        // closing subtable
        manager.setVerDetalle(false);

        manager.mostrarDetalle(actionEvent);

        assertTrue(manager.isVerDetalle());
        assertTrue(manager.getRowImagenMostrar().contains("up.png"));
        assertTrue(manager.getRowTextoMostrar().contains("Ocultar"));
    }
}
