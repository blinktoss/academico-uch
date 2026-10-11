package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.Collections;

import static net.uch.common.Constants.ACTION_IMAGES_DOWN;
import static net.uch.common.Constants.ACTION_IMAGES_UP;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 27/12/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbModuloTest extends SpringConfigurationTest {

    private MbModulo manager;

    @Before
    public void setUp() {
        manager = new MbModulo();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] result = manager.getComboAreas();

        assertEquals(0, manager.getParamAreaId());
        assertEquals(15, result.length);
    }

    @Test
    public void seleccionarTest() {
        manager.setParamAreaId(2);
        manager.setParamDescripcion("");

        String result = manager.seleccionar();

        assertEquals(12, manager.getModulos().size());
        assertTrue(result.isEmpty());

        MbModulo row = manager.getModulos().get(0);
        assertFalse(row.getRowModDescripcion().isEmpty());
        assertFalse(row.getRowModCodigo().isEmpty());
        assertFalse(row.getRowAreDescripcion().isEmpty());

        MbModulo subRow = row.getCursos().get(0);
        assertNotEquals(0, subRow.getRsbCurId());
        assertFalse(subRow.getRsbCurCodigo().isEmpty());
        assertFalse(subRow.getRsbCurNombre().isEmpty());
    }

    @Test
    public void mostrarCursosTest() {
        // opening
        manager.setVerDetalle(true);
        manager.mostrarCursos(actionEvent);

        assertFalse(manager.isVerDetalle());
        assertEquals(ACTION_IMAGES_DOWN, manager.getImagenDetalle());
        assertEquals("Ver Cursos", manager.getTituloDetalle());

        // closing
        manager.setVerDetalle(false);
        manager.mostrarCursos(actionEvent);

        assertTrue(manager.isVerDetalle());
        assertEquals(ACTION_IMAGES_UP, manager.getImagenDetalle());
        assertEquals("Ocultar Cursos", manager.getTituloDetalle());
    }

    @Test
    public void loadModalForNewModuleTest() {
        manager.nuevo(actionEvent);

        SelectItem[] areas = manager.getComboAreas();

        assertEquals(0, manager.getMpModId());
        assertTrue(manager.getMpModCodigo().isEmpty());
        assertTrue(manager.getMpModDescripcion().isEmpty());
        assertNotNull(manager.getMpModCreacion());
        assertEquals(0, manager.getMpAreaId());
        assertTrue(manager.getOncomplete().contains("Richfaces.showModalPanel('mp1'"));
        assertEquals(15, areas.length);

        saveNewAndEditModuleWithCourses();

        manager.nuevo(actionEvent);

        assertEquals(0, manager.getMpModId());
        assertEquals(0, manager.getMpAreaId());

        saveNewAndEditModuleWithoutCourses();
    }

    private void saveNewAndEditModuleWithCourses() {
        manager.setMpAreaId(20); // PROGRAMA DE ESPECIALIZACION
        manager.setMpModCodigo("FAKE_MODULE_CODE_1");
        manager.setMpModDescripcion("FAKE_MODULO_DESCRIPTION_1");
        manager.setMpCursos(Arrays.asList(
                Stubs.beanClModule("FAKE_COURSE_CODE_1_1", "FAKE_COURSE_NAME_1_1"),
                Stubs.beanClModule("FAKE_COURSE_CODE_1_2", "FAKE_COURSE_NAME_1_2"),
                Stubs.beanClModule("FAKE_COURSE_CODE_1_3", "FAKE_COURSE_NAME_1_3")));

        manager.guardarModulo(actionEvent);

        assertTrue(manager.getOncomplete().contains("Richfaces.hideModalPanel('mp1'"));

        // Opening modal to edit
        manager.setRowModId(manager.getMpModId());

        UIParameter uipModuleId = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_update")).thenReturn(uipModuleId);
        when(uipModuleId.getValue()).thenReturn(manager.getRowModId());

        manager.actualizarModulo(actionEvent);

        assertTrue(manager.getOncomplete().contains("Richfaces.showModalPanel('mp1'"));
        assertEquals(3, manager.getMpCursos().size());

        // ------------------------------------------------------------------
        // Editing
        MbModulo bean = manager.getMpCursos().get(1);
        bean.setMpModCodigo("FAKE_EDIT_MOD_CODE_1");
        bean.setMpModDescripcion("FAKE_EDIT_MODULO_DESCRIPTION_1");

        // Editing to save course
        manager.setMpCurId(bean.getMpCurId());

        UIParameter uipCourseId = mock(UIParameter.class);
        when(uiComponent.findComponent("id_cur_update")).thenReturn(uipCourseId);
        when(uipCourseId.getValue()).thenReturn(manager.getMpCurId());

        bean.editarCurso(actionEvent);

        assertEquals(bean.getEdCurId(), bean.getMpCurId());
        assertEquals(bean.getEdCurCodigo(), bean.getMpCurCodigo());
        assertEquals(bean.getEdCurNombre(), bean.getMpCurNombre());
        assertFalse(bean.isCursoVer());
        assertTrue(bean.isCursoEditar());

        // ------------------------------------------------------------------
        bean.setEdCurCodigo("");
        bean.setEdCurNombre("");
        bean.guardarCurso(actionEvent);
        assertTrue(bean.getMessage().contains("Debe ingresar un codigo y un nombre para el curso"));

        bean.setEdCurCodigo("FAKE_EDIT_CO_COD_1_2");
        bean.guardarCurso(actionEvent);
        assertTrue(bean.getMessage().contains("Debe ingresar un codigo y un nombre para el curso"));

        bean.setEdCurNombre("FAKE_EDIT_COURSE_NAME_1_2");
        bean.guardarCurso(actionEvent);

        assertEquals(bean.getEdCurId(), bean.getMpCurId());
        assertEquals(bean.getEdCurCodigo(), bean.getMpCurCodigo());
        assertEquals(bean.getEdCurNombre(), bean.getMpCurNombre());
        assertEquals("FAKE_EDIT_CO_COD_1_2", bean.getMpCurCodigo());
        assertEquals("FAKE_EDIT_COURSE_NAME_1_2", bean.getMpCurNombre());
        assertTrue(bean.isCursoVer());
        assertFalse(bean.isCursoEditar());

        // Adding course
        manager.agregarCurso(actionEvent);
        assertTrue(manager.getMessage().contains("Debe ingresar un codigo y un nombre para el curso"));

        manager.setMpCurCodigo("FAKE_COURSE_CODE_1_4");
        manager.agregarCurso(actionEvent);
        assertTrue(manager.getMessage().contains("Debe ingresar un codigo y un nombre para el curso"));

        manager.setMpCurNombre("FAKE_COURSE_NAME_1_4");
        manager.agregarCurso(actionEvent);
        assertTrue(manager.getMessage().isEmpty());
        assertEquals(4, manager.getMpCursos().size());

        // Adding another course
        manager.setMpCurCodigo("FAKE_COURSE_CODE_1_5");
        manager.setMpCurNombre("FAKE_COURSE_NAME_1_5");
        manager.agregarCurso(actionEvent);
        assertTrue(manager.getMessage().isEmpty());
        assertEquals(5, manager.getMpCursos().size());

        // Editing to save cancel
        bean = manager.getMpCursos().get(4);
        bean.setMpCurId(bean.getMpCurId());
        bean.editarCurso(actionEvent);
        assertFalse(bean.isCursoVer());
        assertTrue(bean.isCursoEditar());

        bean.cancelarCurso(actionEvent);
        assertTrue(bean.isCursoVer());
        assertFalse(bean.isCursoEditar());

        // Adding course
        manager.setMpCurCodigo("FAKE_COURSE_CODE_1_6");
        manager.setMpCurNombre("FAKE_COURSE_NAME_1_6");
        manager.agregarCurso(actionEvent);
        assertTrue(manager.getMessage().isEmpty());
        assertEquals(6, manager.getMpCursos().size());

        // Removing already saved - FAKE_COURSE_CODE_1_3
        manager.setMpCurId(manager.getMpCursos().get(2).getMpCurId());
        UIParameter uipToDelete = mock(UIParameter.class);
        when(uiComponent.findComponent("id_cur_delete")).thenReturn(uipToDelete);
        when(uipToDelete.getValue()).thenReturn(manager.getMpCurId());

        manager.quitarCurso(actionEvent);
        assertEquals(1, manager.getIdsCurDelete().size());
        assertEquals(5, manager.getMpCursos().size());

        // Removing new added - FAKE_COURSE_CODE_1_5
        manager.setMpCurId(manager.getMpCursos().get(3).getMpCurId());
        when(uiComponent.findComponent("id_cur_delete")).thenReturn(uipToDelete);
        when(uipToDelete.getValue()).thenReturn(manager.getMpCurId());

        manager.quitarCurso(actionEvent);
        assertEquals(1, manager.getIdsCurDelete().size());
        assertEquals(4, manager.getMpCursos().size());

        // Saving
        manager.guardarModulo(actionEvent);

        assertTrue(manager.getOncomplete().contains("Richfaces.hideModalPanel('mp1'"));

        // Deleting
        when(uiComponent.findComponent("id_delete")).thenReturn(uipToDelete);
        when(uipToDelete.getValue()).thenReturn(manager.getRowModId());

        manager.eliminarModulo(actionEvent);
    }

    private void saveNewAndEditModuleWithoutCourses() {
        manager.guardarModulo(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpAreaId(20); // PROGRAMA DE ESPECIALIZACION
        manager.guardarModulo(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpModCodigo("FAKE_MODULE_CODE_2");
        manager.guardarModulo(actionEvent);
        assertTrue(manager.getOncomplete().contains("Todos los campos son obligatorios"));

        // ------------------------------------------------------------------
        manager.setMpModDescripcion("FAKE_MODULO_DESCRIPTION_2");
        manager.setMpCursos(Collections.emptyList());

        manager.guardarModulo(actionEvent);

        assertTrue(manager.getOncomplete().contains("Richfaces.hideModalPanel('mp1'"));

        manager.setRowModId(manager.getMpModId());

        // Deleting
        when(uiComponent.findComponent("id_delete")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getRowModId());

        manager.eliminarModulo(actionEvent);
    }

    @Test
    public void quitarCursoTest() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_cur_delete")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(242); // CEV-COMPPKIDS

        manager.quitarCurso(actionEvent);
        assertTrue(manager.getMessage().contains("El curso no se puede Eliminar debido a que posee Talleres"));
    }
}
