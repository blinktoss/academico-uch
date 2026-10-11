package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.academica.jsf.beans.ObjNodeGnral;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIOutput;
import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.Collections;

import static net.uch.Stubs.SEMESTRE_2010_2_ID;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 07/06/2020.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbHorarioTest extends SpringConfigurationTest {

    private static final String PARAM_ID_TURNO = "param_id_turno";
    private static final String HO_AULA = "ho_aula";
    private static final String HO_TIPO = "ho_tipo";
    private static final String HO_DOCENTE = "ho_docente";

    private MbHorario manager;

    @Before
    public void setUp() {
        manager = new MbHorario();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadTreeTest() {
        TreeNode<String> result = manager.getArbol();

        assertNotNull(result);
        assertNotNull(manager.getNodos());
        assertNull(manager.getListaSecciones());
    }

    @Test
    public void seleccionNodoArbolTest() {
        String rowKey = "1:1:2:1:2";
        int openedCourseId = 848;
        String openedCourseName = "ALGORITMICA I";

        ObjNodeGnral node = new ObjNodeGnral();
        node.setId(rowKey);
        node.setCurapeId(openedCourseId);
        node.setCurNom(openedCourseName);
        node.setSemId(SEMESTRE_2010_2_ID);
        manager.setNodos(Collections.singletonList(node));

        NodeSelectedEvent nodeSelectedEvent = mock(NodeSelectedEvent.class);
        UITree uiTree = mock(UITree.class);

        when(nodeSelectedEvent.getComponent()).thenReturn(uiTree);
        when(uiTree.getRowKey()).thenReturn(rowKey);

        manager.seleccion(nodeSelectedEvent);

        assertNotNull(manager.getLblCursoAperturado());
        assertFalse(manager.getListaSecciones().isEmpty());

        assertEquals(openedCourseName, manager.getLblCursoAperturado());

        var row = manager.getListaSecciones().get(0);
        assertNotEquals(0, row.getRowIdSec());
        assertNotEquals(0, row.getRowIdSem());
        assertFalse(row.getRowCodigoSec().isEmpty());
        assertFalse(row.getRowNombreSec().isEmpty());
        assertNotEquals(0, row.getRowVacantesSec());
        assertFalse(row.getRowVacantesOcupadas().isEmpty());
    }

    @Test
    public void openAndShowModalRegistroHorarioPorCursoTest() {
        UIParameter uiParameterId = mock(UIParameter.class);
        UIParameter uiParameterIdSemestre = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_seccion")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn("1838");
        when(uiComponent.findComponent("id_semestre")).thenReturn(uiParameterIdSemestre);
        when(uiParameterIdSemestre.getValue()).thenReturn(SEMESTRE_2010_2_ID);

        // preparing for open modal first time
        manager.asignarHorario(actionEvent);

        assertEquals(0, manager.getMpAulaId());
        assertEquals(0, manager.getMpDocenteId());
        assertEquals(0, manager.getMpTurnoId());
        assertTrue(manager.getListaHorario().isEmpty());

        // Loading modal
        SelectItem[] resultTurnos = manager.getComboTurnos();
        assertEquals(4, resultTurnos.length);

        // Selection turno
        manager.setMpTurnoId((Integer) resultTurnos[0].getValue());

        // Refresh variable for reRender from turnos combo box
        manager.setearTurno(actionEvent);

        SelectItem[] resultAulas = manager.getComboAulas();
        assertEquals(43, resultAulas.length);

        SelectItem[] resultTipos = manager.getComboTipos();
        assertEquals(3, resultTipos.length);

        SelectItem[] resultDocentes = manager.getComboDocentes();
        assertEquals(271, resultDocentes.length);

        assertNotNull(manager.getTablaHorario());
        assertEquals(8, manager.getTablaHorario().size());
    }

    @Test
    public void seleccionarDiasTest() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);

        UIOutput uiOutput = mock(UIOutput.class);

        UIParameter uiParamTurnoId = mock(UIParameter.class);
        when(uiComponent.findComponent(PARAM_ID_TURNO)).thenReturn(uiParamTurnoId);
        when(uiParamTurnoId.getValue()).thenReturn("230");

        UIParameter uiParamAulaId = mock(UIParameter.class);
        when(uiComponent.findComponent(HO_AULA)).thenReturn(uiParamAulaId);
        when(uiParamAulaId.getValue()).thenReturn("45");

        UIParameter uiParamTipoId = mock(UIParameter.class);
        when(uiComponent.findComponent(HO_TIPO)).thenReturn(uiParamTipoId);
        when(uiParamTipoId.getValue()).thenReturn("018001");

        UIParameter uiParamDocenteId = mock(UIParameter.class);
        when(uiComponent.findComponent(HO_DOCENTE)).thenReturn(uiParamDocenteId);
        when(uiParamDocenteId.getValue()).thenReturn("53");

        // Lunes
        when(uiComponent.findComponent("lunes")).thenReturn(uiOutput);
        when(uiOutput.getValue()).thenReturn("");

        manager.seleccionarLunes(actionEvent);

        assertEquals(-1, manager.getIde());
        assertEquals(0, manager.getMpRowIndexIdLun());
        assertFalse(manager.getMpRowHorLun().isEmpty());

        // Martes
        when(uiComponent.findComponent("martes")).thenReturn(uiOutput);
        when(uiOutput.getValue()).thenReturn("");

        manager.seleccionarMartes(actionEvent);

        assertEquals(-2, manager.getIde());
        assertEquals(-1, manager.getMpRowIndexIdMar());
        assertFalse(manager.getMpRowHorMar().isEmpty());

        // Miercoles
        when(uiComponent.findComponent("miercoles")).thenReturn(uiOutput);
        when(uiOutput.getValue()).thenReturn("");

        manager.seleccionarMiercoles(actionEvent);

        assertEquals(-3, manager.getIde());
        assertEquals(-2, manager.getMpRowIndexIdMie());
        assertFalse(manager.getMpRowHorMie().isEmpty());

        // Jueves
        when(uiComponent.findComponent("jueves")).thenReturn(uiOutput);
        when(uiOutput.getValue()).thenReturn("");

        manager.seleccionarJueves(actionEvent);

        assertEquals(-4, manager.getIde());
        assertEquals(-3, manager.getMpRowIndexIdJue());
        assertFalse(manager.getMpRowHorJue().isEmpty());

        // Viernes
        when(uiComponent.findComponent("viernes")).thenReturn(uiOutput);
        when(uiOutput.getValue()).thenReturn("");

        manager.seleccionarViernes(actionEvent);

        assertEquals(-5, manager.getIde());
        assertEquals(-4, manager.getMpRowIndexIdVie());
        assertFalse(manager.getMpRowHorVie().isEmpty());

        // Sabado
        when(uiComponent.findComponent("sabado")).thenReturn(uiOutput);
        when(uiOutput.getValue()).thenReturn("");

        manager.seleccionarSabado(actionEvent);

        assertEquals(-6, manager.getIde());
        assertEquals(-5, manager.getMpRowIndexIdSab());
        assertFalse(manager.getMpRowHorSab().isEmpty());

        // Domingo
        when(uiComponent.findComponent("domingo")).thenReturn(uiOutput);
        when(uiOutput.getValue()).thenReturn("");

        manager.seleccionarDomingo(actionEvent);

        assertEquals(-7, manager.getIde());
        assertEquals(-6, manager.getMpRowIndexIdDom());
        assertFalse(manager.getMpRowHorDom().isEmpty());
    }
}
