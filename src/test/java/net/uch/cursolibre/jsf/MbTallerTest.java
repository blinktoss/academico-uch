package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 08/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbTallerTest extends SpringConfigurationTest {

    private MbTaller manager;

    @Before
    public void setUp() {
        manager = new MbTaller();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] areas = manager.getComboAreas();
        List<SelectItem> modulos = manager.getComboModulos();

        assertEquals(0, manager.getParamAreaId());
        assertEquals(15, areas.length);
        assertEquals(0, manager.getParamModId());
        assertTrue(modulos.isEmpty());

        // On change
        manager.setParamAreaId(1);
        manager.verModulos(actionEvent);

        areas = manager.getComboAreas();
        modulos = manager.getComboModulos();

        assertEquals(15, areas.length);
        assertEquals(10, modulos.size());

        // On change
        manager.setParamAreaId(0);
        manager.verModulos(actionEvent);

        areas = manager.getComboAreas();
        modulos = manager.getComboModulos();

        assertEquals(15, areas.length);
        assertTrue(modulos.isEmpty());
    }

    @Test
    public void seleccionarTest() {
        manager.setParamAreaId(1); // PROGRAMACIÓN
        manager.setParamModId(0);

        String result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertTrue(manager.getCursos().isEmpty());

        // Other test
        manager.setParamModId(7); // JAVA DEVELOPER

        result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertEquals(4, manager.getCursos().size());

        MbTaller cur = manager.getCursos().get(0);
        assertNotEquals(0, cur.getRowCurId());
        assertFalse(cur.getRowCurModulo().isEmpty());
        assertFalse(cur.getRowCurCodigo().isEmpty());
        assertFalse(cur.getRowCurNombre().isEmpty());
        assertFalse(cur.isVerDetalle());
        assertFalse(cur.getImagenDetalle().isEmpty());
        assertFalse(cur.getTituloDetalle().isEmpty());

        MbTaller tal = cur.getTalleres().get(0);

        assertNotEquals(0, tal.getSubTalId());
        assertFalse(tal.getSubTalCodigo().isEmpty());
        assertFalse(tal.getSubTalNumero().isEmpty());
        assertFalse(tal.getSubTalDescripcion().isEmpty());
        assertNotEquals(0, tal.getRowCurId());
        assertNotEquals(0, tal.getSubPlaId());
        assertFalse(tal.getSubPlaDescripcion().isEmpty());
    }

    @Test
    public void mostrarTalleresTest() {
        UIParameter uipFlag = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("flag")).thenReturn(uipFlag);

        // opening subtable
        manager.setVerDetalle(true);
        when(uipFlag.getValue()).thenReturn(manager.isVerDetalle());

        manager.mostrarTalleres(actionEvent);

        assertFalse(manager.isVerDetalle());
        assertTrue(manager.getImagenDetalle().contains("down.png"));
        assertTrue(manager.getTituloDetalle().contains("Ver"));

        // closing subtable
        manager.setVerDetalle(false);
        when(uipFlag.getValue()).thenReturn(manager.isVerDetalle());

        manager.mostrarTalleres(actionEvent);

        assertTrue(manager.isVerDetalle());
        assertTrue(manager.getImagenDetalle().contains("up.png"));
        assertTrue(manager.getTituloDetalle().contains("Ocultar"));
    }

    @Test
    public void interactionsWithTalleresModalTest() {
        manager.setRowCurId(12); // ESPECIALISTA EN DISEÑO WEB

        UIParameter uipCourseId = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_talleres")).thenReturn(uipCourseId);
        when(uipCourseId.getValue()).thenReturn(manager.getRowCurId());

        manager.cargarTalleres(actionEvent);

        assertFalse(manager.getRowCurModulo().isEmpty());
        assertNotEquals(0, manager.getRowCurId());
        assertFalse(manager.getRowCurCodigo().isEmpty());
        assertFalse(manager.getRowCurNombre().isEmpty());
        assertNotEquals(0, manager.getSubPlaId());
        assertFalse(manager.getSubPlaDescripcion().isEmpty());
        assertTrue(manager.getIdsTalDelete().isEmpty());
        assertTrue(manager.getOncomplete().contains("Richfaces.showModalPanel('mpTaller')"));

        // Empty taller
        manager.setSubTalCodigo("");
        manager.setSubTalNumero("");
        manager.setSubTalDescripcion("");

        manager.agregarTaller(actionEvent);

        assertTrue(manager.getMessage().contains("Todos los campos son obligatorios"));

        // Filling fields 1
        manager.setSubTalCodigo("FAKE_COD_0_1");
        manager.setSubTalNumero("3");
        manager.setSubTalDescripcion("FAKE_DESC_0_1");

        manager.agregarTaller(actionEvent);

        assertEquals(3, manager.getMpTableTalleres().size());

        // Filling fields 2
        manager.setSubTalCodigo("FAKE_COD_0_2");
        manager.setSubTalNumero("4");
        manager.setSubTalDescripcion("FAKE_DESC_0_2");

        manager.agregarTaller(actionEvent);

        assertEquals(4, manager.getMpTableTalleres().size());

        // Filling fields 3
        manager.setSubTalCodigo("FAKE_COD_0_3");
        manager.setSubTalNumero("5");
        manager.setSubTalDescripcion("FAKE_DESC_0_3");

        manager.agregarTaller(actionEvent);

        assertEquals(5, manager.getMpTableTalleres().size());

        // Saving
        manager.guardarTalleres(actionEvent);

        assertTrue(manager.getOncomplete().contains("Cambios guardados."));

        // Loading after save 3 new workshops
        manager.cargarTalleres(actionEvent);

        assertEquals(5, manager.getMpTableTalleres().size());
        assertTrue(manager.getIdsTalDelete().isEmpty());
        assertTrue(manager.getOncomplete().contains("Richfaces.showModalPanel('mpTaller')"));

        // Edit workshop in row table
        MbTaller w = manager.getMpTableTalleres().get(3);
        w.editarTaller(actionEvent);

        assertEquals(w.getSubTalId(), w.getEditTalId());
        assertEquals(w.getSubTalCodigo(), w.getEditTalCodigo());
        assertEquals(w.getSubTalNumero(), w.getEditTalNumero());
        assertEquals(w.getSubTalDescripcion(), w.getEditTalDescripcion());
        assertTrue(w.isEditable());

        // Cancel workshop edit in row table
        w.cancelarTaller(actionEvent);

        assertFalse(w.isEditable());

        w.setEditTalCodigo("FAKE_COD_0_2E");

        w.guardarTaller(actionEvent);

        assertEquals(w.getEditTalId(), w.getSubTalId());
        assertEquals(w.getEditTalCodigo(), w.getSubTalCodigo());
        assertEquals(w.getEditTalNumero(), w.getSubTalNumero());
        assertEquals(w.getEditTalDescripcion(), w.getSubTalDescripcion());
        assertFalse(w.isEditable());

        // Saving
        manager.guardarTalleres(actionEvent);

        // Loading after save 3 new workshops
        manager.cargarTalleres(actionEvent);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_tal_delete")).thenReturn(uipCourseId);
        when(uipCourseId.getValue()).thenReturn(13); // EDW01 | ADOBE DREAMWEAVER CS4 - DESIGNER

        manager.eliminarTaller(actionEvent);

        assertTrue(manager.getMessage().contains("Este Taller posee talleres Aperturados"));

        List<Integer> workshopsIds = Arrays.asList(
                manager.getMpTableTalleres().get(2).getSubTalId(),
                manager.getMpTableTalleres().get(3).getSubTalId(),
                manager.getMpTableTalleres().get(4).getSubTalId());
        for (Integer wId : workshopsIds) {
            when(actionEvent.getComponent()).thenReturn(uiComponent);
            when(uiComponent.findComponent("id_tal_delete")).thenReturn(uipCourseId);
            when(uipCourseId.getValue()).thenReturn(wId);

            manager.eliminarTaller(actionEvent);

            assertTrue(manager.getMessage().isEmpty());
        }

        // Saving
        manager.guardarTalleres(actionEvent);

        assertEquals(2, manager.getMpTableTalleres().size());
        assertEquals(3, manager.getIdsTalDelete().size());
        assertTrue(manager.getOncomplete().contains("Cambios guardados."));
    }
}
