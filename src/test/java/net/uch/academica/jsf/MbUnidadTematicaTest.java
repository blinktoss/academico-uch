package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.List;

import static net.uch.Stubs.Especialidad;
import static net.uch.Stubs.Facultad;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 19/12/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbUnidadTematicaTest extends SpringConfigurationTest {

    private MbUnidadTematica manager;

    @Before
    public void setUp() {
        manager = new MbUnidadTematica();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        List<SelectItem> faculties = manager.getComboFacultadesS();
        List<SelectItem> specialities = manager.getComboEspecialidadesS();
        List<SelectItem> curricularPlans = manager.getComboPlanCurricularS();
        List<SelectItem> cycles = manager.getComboCicloS();

        assertEquals(5, faculties.size());
        assertTrue(specialities.isEmpty());
        assertTrue(curricularPlans.isEmpty());
        assertEquals(1, cycles.size());

        // ------------------------------------------------------------------
        manager.setbFacultad(Facultad.ING_SISTEMAS);
        manager.setearFacultadS(actionEvent);

        faculties = manager.getComboFacultadesS();
        specialities = manager.getComboEspecialidadesS();
        curricularPlans = manager.getComboPlanCurricularS();
        cycles = manager.getComboCicloS();

        assertEquals(5, faculties.size());
        assertEquals(1, specialities.size());
        assertTrue(curricularPlans.isEmpty());
        assertEquals(1, cycles.size());

        // ------------------------------------------------------------------
        manager.setbEspecialidad(Especialidad.ING_SISTEMAS);
        manager.setearEspecialidadS(actionEvent);

        specialities = manager.getComboEspecialidadesS();
        curricularPlans = manager.getComboPlanCurricularS();
        cycles = manager.getComboCicloS();

        assertEquals(1, specialities.size());
        assertEquals(1, curricularPlans.size());
        assertEquals(1, cycles.size());

        // ------------------------------------------------------------------
        manager.setbPlanCurricular(4); // PLAN CURRICULAR 2008
        manager.setearPCurricularS(actionEvent);

        specialities = manager.getComboEspecialidadesS();
        curricularPlans = manager.getComboPlanCurricularS();
        cycles = manager.getComboCicloS();

        assertEquals(1, specialities.size());
        assertEquals(1, curricularPlans.size());
        assertEquals(11, cycles.size());

        // ------------------------------------------------------------------
        manager.setbPlanCurricular(0);
        manager.setearPCurricularS(actionEvent);

        specialities = manager.getComboEspecialidadesS();
        curricularPlans = manager.getComboPlanCurricularS();
        cycles = manager.getComboCicloS();

        assertEquals(1, specialities.size());
        assertEquals(1, curricularPlans.size());
        assertEquals(1, cycles.size());

        // ------------------------------------------------------------------
        manager.setbEspecialidad(0);
        manager.setearEspecialidadS(actionEvent);

        faculties = manager.getComboFacultadesS();
        specialities = manager.getComboEspecialidadesS();
        curricularPlans = manager.getComboPlanCurricularS();
        cycles = manager.getComboCicloS();

        assertEquals(5, faculties.size());
        assertEquals(1, specialities.size());
        assertTrue(curricularPlans.isEmpty());
        assertEquals(1, cycles.size());

        // ------------------------------------------------------------------
        manager.setbFacultad(0);
        manager.setearFacultadS(actionEvent);

        faculties = manager.getComboFacultadesS();
        specialities = manager.getComboEspecialidadesS();
        curricularPlans = manager.getComboPlanCurricularS();
        cycles = manager.getComboCicloS();

        assertEquals(5, faculties.size());
        assertTrue(specialities.isEmpty());
        assertTrue(curricularPlans.isEmpty());
        assertEquals(1, cycles.size());
    }

    @Test
    public void seleccionarTest() {
        manager.setbPlanCurricular(0);
        manager.setbCiclo("0");
        manager.seleccionar(actionEvent);
        assertEquals(0, manager.getbFacultad());
        assertEquals(0, manager.getbEspecialidad());
        assertTrue(manager.getListaDatos().isEmpty());

        // ------------------------------------------------------------------
        manager.setbPlanCurricular(4); // ING SISTEMAS
        manager.setbCiclo("99"); // TODOS
        manager.seleccionar(actionEvent);
        assertEquals(67, manager.getListaDatos().size());

        assertNotEquals(0, manager.getListaDatos().get(0).getPlanCursoId());
        assertNotNull(manager.getListaDatos().get(0).getCiclo());
        assertNotNull(manager.getListaDatos().get(0).getNombre());
        assertNotEquals(0, manager.getListaDatos().get(0).getCreditos());
        assertNotNull(manager.getListaDatos().get(0).getTipo());

        // ------------------------------------------------------------------
        manager.setbPlanCurricular(4); // ING SISTEMAS
        manager.setbCiclo("006001"); // I Ciclo
        manager.seleccionar(actionEvent);
        assertEquals(7, manager.getListaDatos().size());
    }

    @Test
    public void asignarWithContentTest() {
        UIParameter uiParameter = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn("27");

        manager.asignar(actionEvent);

        assertFalse(manager.getEspecialidadDescripcion().isEmpty());
        assertFalse(manager.getCicloDescripcion().isEmpty());
        assertFalse(manager.getPlanDescripcion().isEmpty());
        assertFalse(manager.getCursoDescripcion().isEmpty());
        assertTrue(manager.getDetContenido().isEmpty());
        assertFalse(manager.getCtTituloU().isEmpty());
        assertFalse(manager.getCtDescripcionU().isEmpty());
        assertEquals(17, manager.getCtSemanasU());
        assertTrue(manager.isCtVigenteU());
        assertEquals(18, manager.getNroSemana());
        assertFalse(manager.getListaDatosDetalle().isEmpty());
    }

    @Test
    public void asignarWithoutContentTest() {
        UIParameter uiParameter = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn("87");

        manager.asignar(actionEvent);

        assertTrue(manager.getDetContenido().isEmpty());
        assertTrue(manager.getCtTituloU().isEmpty());
        assertTrue(manager.getCtDescripcionU().isEmpty());
        assertEquals(0, manager.getCtSemanasU());
        assertFalse(manager.isCtVigenteU());
        assertEquals(1, manager.getNroSemana());
        assertTrue(manager.getListaDatosDetalle().isEmpty());
    }

    @Test
    public void asignarThematicContentDetailsTest() {
        String curricularPlanId = "92";
        UIParameter uiParameter = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(curricularPlanId);

        manager.asignar(actionEvent);

        assertTrue(manager.getDetContenido().isEmpty());
        assertFalse(manager.isCtVigenteU());
        assertEquals(1, manager.getNroSemana());
        assertTrue(manager.getListaDatosDetalle().isEmpty());

        addDetailsTest();

        manager.setCtTituloU("");
        manager.setCtDescripcionU("");
        manager.setCtVigenteU(true);
        manager.guardar(actionEvent);
        assertTrue(manager.getMensajes().contains("El Titulo del Contenido Tematico no puede ir vacio"));

        // ------------------------------------------------------------------
        manager.setCtTituloU(RandomStringUtils.randomAlphanumeric(50));
        manager.guardar(actionEvent);
        assertTrue(manager.getMensajes().contains("La Descripcion del Contenido Tematico no puede ir vacio"));

        // ------------------------------------------------------------------
        manager.setCtDescripcionU(RandomStringUtils.randomAlphanumeric(150));
        manager.guardar(actionEvent);
        assertTrue(manager.getMensajes().contains("Se guardo Correctamente"));

        // ------------------------------------------------------------------
        // Adding new detail more on a CT with details
        when(uiParameter.getValue()).thenReturn(curricularPlanId);
        manager.asignar(actionEvent);

        manager.setDetContenido(RandomStringUtils.randomAlphanumeric(255));
        manager.agregar(actionEvent);
        assertEquals(6, manager.getListaDatosDetalle().size());
        assertEquals(7, manager.getNroSemana());

        manager.guardar(actionEvent);
        assertTrue(manager.getMensajes().contains("Se guardo Correctamente"));

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(curricularPlanId);
        manager.asignar(actionEvent);

        deleteAllDetailsTest();

        manager.setCtVigenteU(false);
        manager.guardar(actionEvent);
        assertTrue(manager.getMensajes().contains("Se guardo Correctamente"));
    }

    private void addDetailsTest() {
        manager.setDetContenido(RandomStringUtils.randomAlphanumeric(255));
        manager.agregar(actionEvent);
        assertEquals(1, manager.getListaDatosDetalle().size());
        assertEquals(2, manager.getNroSemana());

        // ------------------------------------------------------------------
        manager.setDetContenido(RandomStringUtils.randomAlphanumeric(255));
        manager.agregar(actionEvent);
        assertEquals(2, manager.getListaDatosDetalle().size());
        assertEquals(3, manager.getNroSemana());

        // ------------------------------------------------------------------
        manager.setDetContenido(RandomStringUtils.randomAlphanumeric(255));
        manager.agregar(actionEvent);
        assertEquals(3, manager.getListaDatosDetalle().size());
        assertEquals(4, manager.getNroSemana());

        // ------------------------------------------------------------------
        manager.setDetContenido(RandomStringUtils.randomAlphanumeric(255));
        manager.agregar(actionEvent);
        assertEquals(4, manager.getListaDatosDetalle().size());
        assertEquals(5, manager.getNroSemana());

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("tc_id")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getListaDatosDetalle().get(2).getDetId());
        manager.quitar(actionEvent);
        assertEquals(3, manager.getListaDatosDetalle().size());
        assertEquals(4, manager.getNroSemana());

        // ------------------------------------------------------------------
        manager.setDetContenido(RandomStringUtils.randomAlphanumeric(255));
        manager.agregar(actionEvent);
        assertEquals(4, manager.getListaDatosDetalle().size());
        assertEquals(5, manager.getNroSemana());

        // ------------------------------------------------------------------
        manager.setDetContenido(RandomStringUtils.randomAlphanumeric(255));
        manager.agregar(actionEvent);
        assertEquals(5, manager.getListaDatosDetalle().size());
        assertEquals(6, manager.getNroSemana());

        editingRow();
    }

    private void editingRow() {
        MbUnidadTematica b = manager.getListaDatosDetalle().get(2);
        b.editarFilaDetalle(actionEvent);
        assertFalse(b.isEditableb1());
        assertTrue(b.isEditableb2());
        assertEquals(b.getDetContenido(), b.getDetContenidoAux());

        // ------------------------------------------------------------------
        b.cancelar(actionEvent);
        assertTrue(b.isEditableb1());
        assertFalse(b.isEditableb2());
        assertEquals(b.getDetContenidoAux(), b.getDetContenido());

        // ------------------------------------------------------------------
        b.setDetContenido(RandomStringUtils.randomAlphanumeric(400));
        b.grabar(actionEvent);
        assertTrue(b.isEditableb1());
        assertFalse(b.isEditableb2());
    }

    private void deleteAllDetailsTest() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("tc_id")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getListaDatosDetalle().get(0).getDetId());
        manager.quitar(actionEvent);
        assertEquals(5, manager.getListaDatosDetalle().size());
        assertEquals(6, manager.getNroSemana());

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(manager.getListaDatosDetalle().get(0).getDetId());
        manager.quitar(actionEvent);
        assertEquals(4, manager.getListaDatosDetalle().size());
        assertEquals(5, manager.getNroSemana());

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(manager.getListaDatosDetalle().get(0).getDetId());
        manager.quitar(actionEvent);
        assertEquals(3, manager.getListaDatosDetalle().size());
        assertEquals(4, manager.getNroSemana());

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(manager.getListaDatosDetalle().get(0).getDetId());
        manager.quitar(actionEvent);
        assertEquals(2, manager.getListaDatosDetalle().size());
        assertEquals(3, manager.getNroSemana());

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(manager.getListaDatosDetalle().get(0).getDetId());
        manager.quitar(actionEvent);
        assertEquals(1, manager.getListaDatosDetalle().size());
        assertEquals(2, manager.getNroSemana());

        // ------------------------------------------------------------------
        when(uiParameter.getValue()).thenReturn(manager.getListaDatosDetalle().get(0).getDetId());
        manager.quitar(actionEvent);
        assertTrue(manager.getListaDatosDetalle().isEmpty());
        assertEquals(1, manager.getNroSemana());
    }
}
