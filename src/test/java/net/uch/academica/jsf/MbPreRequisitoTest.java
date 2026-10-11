package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.List;

import static net.uch.Stubs.Facultad;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 13/12/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbPreRequisitoTest extends SpringConfigurationTest {

    private MbPreRequisito manager;

    @Before
    public void setUp() {
        manager = new MbPreRequisito();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] facultades = manager.getComboFacultad();
        List<SelectItem> especialidad = manager.getComboEspecialidad();
        List<SelectItem> planesCurriculares = manager.getComboPlan();

        assertEquals(5, facultades.length);
        assertTrue(especialidad.isEmpty());
        assertTrue(planesCurriculares.isEmpty());

        // On change facultad
        manager.setParamIdFacu(Facultad.ING_ELECTRONICA);
        manager.setearFacultadBuscar(actionEvent);

        facultades = manager.getComboFacultad();
        especialidad = manager.getComboEspecialidad();
        planesCurriculares = manager.getComboPlan();

        assertEquals(Facultad.ING_ELECTRONICA, manager.getParamIdFacu());
        assertEquals(5, facultades.length);
        assertEquals(1, especialidad.size());
        assertTrue(planesCurriculares.isEmpty());

        // On change especialidad
        manager.setParamIdEspe(Stubs.Especialidad.ING_ELECTRONICA);
        manager.setearEspecialidadBuscar(actionEvent);

        facultades = manager.getComboFacultad();
        especialidad = manager.getComboEspecialidad();
        planesCurriculares = manager.getComboPlan();

        assertEquals(5, facultades.length);
        assertEquals(1, especialidad.size());
        assertEquals(1, planesCurriculares.size());

        // On change especialidad to select
        manager.setParamIdEspe(0);
        manager.setearEspecialidadBuscar(actionEvent);

        facultades = manager.getComboFacultad();
        especialidad = manager.getComboEspecialidad();
        planesCurriculares = manager.getComboPlan();

        assertEquals(5, facultades.length);
        assertEquals(1, especialidad.size());
        assertTrue(planesCurriculares.isEmpty());

        // On change facultad to select
        manager.setParamIdFacu(0);
        manager.setearFacultadBuscar(actionEvent);

        facultades = manager.getComboFacultad();
        especialidad = manager.getComboEspecialidad();
        planesCurriculares = manager.getComboPlan();

        assertEquals(0, manager.getParamIdFacu());
        assertEquals(5, facultades.length);
        assertTrue(especialidad.isEmpty());
        assertTrue(planesCurriculares.isEmpty());
    }

    @Test
    public void seleccionarEmptyTest() {
        manager.setParamIdPlan(0);

        manager.seleccionar(actionEvent);

        assertTrue(manager.getOncomplete().contains("alert"));
        assertNull(manager.getListaPreRequisitos());
    }

    @Test
    public void seleccionarPreRequisitoFacultadSistemasTest() {
        manager.setParamIdPlan(4);

        manager.seleccionar(actionEvent);

        List<MbPreRequisito> results = manager.getListaPreRequisitos();

        assertEquals(67, results.size());
        MbPreRequisito row = results.get(8);
        assertNotEquals(0, row.getRowId());
        assertFalse(row.getRowCicloValue().isEmpty());
        assertFalse(row.getRowCodigo().isEmpty());
        assertFalse(row.getRowNombre().isEmpty());
        assertFalse(row.getRowTipo().isEmpty());
        assertFalse(row.getRowCreditos().isEmpty());
        assertFalse(row.getRowFacu().isEmpty());
        assertFalse(row.getRowEspe().isEmpty());
        assertFalse(row.getRowPlan().isEmpty());

        List<MbPreRequisito> subResults = row.getDetallePreReq();
        MbPreRequisito subRow = subResults.get(0);
        assertNotEquals(0, subRow.getDetId());
        assertFalse(subRow.getDetReq().isEmpty());
        assertFalse(subRow.getDetCiclo().isEmpty());
    }

    @Test
    public void openAndCloseSubTable() {
        assertFalse(manager.isVer());

        manager.openSubTable(actionEvent);

        assertTrue(manager.isVer());
        assertTrue(manager.getImagen().contains("up.png"));

        // Closing

        manager.openSubTable(actionEvent);

        assertFalse(manager.isVer());
        assertTrue(manager.getImagen().contains("down.png"));
    }

    @Test
    public void openModalModalPreRequisitosTest() {
        manager.setRowCicloValor(2);
        manager.setRowId(283);
        manager.setRowCodigo("MAT-II");
        manager.setRowNombre("MATEMATICA II");
        manager.setRowCicloValue("II");
        manager.setRowCicloId("006002");
        manager.setRowEspe("INGENIERÍA ELECTRÓNICA CON MENCION EN TELECOMUNICACIONES");
        manager.setParamIdEspe(Stubs.Especialidad.ING_ELECTRONICA);
        manager.setRowFacu("INGENIERÍA ELECTRÓNICA");
        manager.setRowPlan("PLAN CURRICULAR 2008");
        manager.setParamIdPlan(5);

        UIParameter uipValor = mock(UIParameter.class);
        UIParameter uipId = mock(UIParameter.class);
        UIParameter uipCodigo = mock(UIParameter.class);
        UIParameter uipNombre = mock(UIParameter.class);
        UIParameter uipCiclo = mock(UIParameter.class);
        UIParameter uipEspecialidad = mock(UIParameter.class);
        UIParameter uipId_especialidad = mock(UIParameter.class);
        UIParameter uipFacultad = mock(UIParameter.class);
        UIParameter uipPlan = mock(UIParameter.class);
        UIParameter uipId_plan = mock(UIParameter.class);
        UIParameter uipId_ciclo = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_valor")).thenReturn(uipValor);
        when(uipValor.getValue()).thenReturn(manager.getRowCicloValor());

        when(uiComponent.findComponent("p_id")).thenReturn(uipId);
        when(uipId.getValue()).thenReturn(manager.getRowId());

        when(uiComponent.findComponent("p_codigo")).thenReturn(uipCodigo);
        when(uipCodigo.getValue()).thenReturn(manager.getRowCodigo());

        when(uiComponent.findComponent("p_nombre")).thenReturn(uipNombre);
        when(uipNombre.getValue()).thenReturn(manager.getRowNombre());

        when(uiComponent.findComponent("p_ciclo")).thenReturn(uipCiclo);
        when(uipCiclo.getValue()).thenReturn(manager.getRowCicloValue());

        when(uiComponent.findComponent("p_id_ciclo")).thenReturn(uipId_ciclo);
        when(uipId_ciclo.getValue()).thenReturn(manager.getRowCicloId());

        when(uiComponent.findComponent("p_especialidad")).thenReturn(uipEspecialidad);
        when(uipEspecialidad.getValue()).thenReturn(manager.getRowEspe());

        when(uiComponent.findComponent("p_id_especialidad")).thenReturn(uipId_especialidad);
        when(uipId_especialidad.getValue()).thenReturn(manager.getParamIdEspe());

        when(uiComponent.findComponent("p_facultad")).thenReturn(uipFacultad);
        when(uipFacultad.getValue()).thenReturn(manager.getRowFacu());

        when(uiComponent.findComponent("p_plan")).thenReturn(uipPlan);
        when(uipPlan.getValue()).thenReturn(manager.getRowPlan());

        when(uiComponent.findComponent("p_id_plan")).thenReturn(uipId_plan);
        when(uipId_plan.getValue()).thenReturn(manager.getParamIdPlan());

        manager.editarFila(actionEvent);

        assertEquals("0", manager.getMpIdCiclo());
        assertTrue(manager.getOncomplete().isEmpty());
        assertEquals(2, manager.getComboCiclo().size());
        assertTrue(manager.getComboCurso().isEmpty());
        assertEquals(1, manager.getListaCursos().size());
    }

    @Test
    public void agregarFilaNewCourseSuccessAndRemoveTest() {
        manager.setRowId(40); // INGENIERIA DE SISTEMAS E INFORMATICA | MATEMATICA II
        manager.setMpIdCurso(28); // ACTIVIDAD A

        manager.fillListaCursos();
        List<MbPreRequisito> listaCursos = manager.getListaCursos();

        assertEquals(1, listaCursos.size());

        manager.agregarFila(actionEvent);

        listaCursos = manager.getListaCursos();
        MbPreRequisito preRequisitoAdded = listaCursos.get(0);

        assertEquals(2, listaCursos.size());
        assertNotEquals(0, preRequisitoAdded.getXtIdCurso());
        assertFalse(preRequisitoAdded.getXtCurso().isEmpty());
        assertNotEquals(0, preRequisitoAdded.getXtIdReq());
        assertFalse(preRequisitoAdded.getXtReq().isEmpty());
        assertFalse(preRequisitoAdded.getXtCiclo().isEmpty());
        assertEquals("alert('Se creo una prerequisito correctamente')", manager.getOncomplete());

        UIParameter uiParameterP_id1 = mock(UIParameter.class);

        int preRequirementCourseId = listaCursos.get(1).getXtId();

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id1")).thenReturn(uiParameterP_id1);
        when(uiParameterP_id1.getValue()).thenReturn(preRequirementCourseId);

        manager.quitarFila(actionEvent);

        listaCursos = manager.getListaCursos();

        assertEquals(1, listaCursos.size());
    }

    @Test
    public void agregarFilaCourseAlreadyAddedTest() {
        manager.setParamIdPlan(4); // PLAN ING SISTEMAS
        manager.setMpIdCiclo("0");

        manager.agregarFila(actionEvent);

        assertEquals("alert('Seleccione un ciclo')", manager.getOncomplete());

        // On change combo ciclo
        manager.setMpIdCiclo("006001");
        manager.setearCiclo(actionEvent);

        assertEquals(7, manager.getComboCurso().size());

        manager.setMpIdCurso(0);

        manager.agregarFila(actionEvent);

        assertEquals("alert('Seleccione un curso')", manager.getOncomplete());

        // Adding course already added
        manager.setRowId(40); // MATEMATICA II
        manager.setMpIdCurso(23); // MATEMATICA I

        manager.agregarFila(actionEvent);

        assertEquals("alert('El curso ya fue asignado como Pre Requisito')", manager.getOncomplete());

        // On change combo ciclo to select
        manager.setMpIdCiclo("0");
        manager.setearCiclo(actionEvent);

        assertTrue(manager.getComboCurso().isEmpty());
    }
}
