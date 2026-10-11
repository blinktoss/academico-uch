package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import java.util.Collections;
import java.util.List;

import static net.uch.Stubs.SEMESTRE_TRASLADOS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 12/06/2020.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbAperturaCursosTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private MbAperturaCursos manager;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbAperturaCursos();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void getComboSemestresTest() {
        SelectItem[] result = manager.getComboSemestres();

        assertNotEquals(0, result.length);
    }

    @Test
    public void loadTreeTest() {
        TreeNode<String> result = manager.getArbol();

        assertNotNull(result);
        assertNotNull(manager.getNodos());
        assertNull(manager.getListaCursos());

        // Selection
        NodeSelectedEvent nodeSelectedEvent = mock(NodeSelectedEvent.class);
        UITree uiTree = mock(UITree.class);

        when(nodeSelectedEvent.getComponent()).thenReturn(uiTree);
        when(uiTree.getRowKey()).thenReturn("4:1:1:5"); // EDUCACIÓN PRIMARIA E INTERCULTURALIDAD
        when(uiTree.getRowData()).thenReturn("V");

        manager.setComboSemestreId(Stubs.SEMESTRE_2010_2_ID);

        manager.seleccionNodo(nodeSelectedEvent);

        assertFalse(manager.getSelectedNode().getArbolFacultad().isEmpty());
        assertFalse(manager.getSelectedNode().getArbolEspecialidad().isEmpty());
        assertFalse(manager.getSelectedNode().getArbolCiclo().isEmpty());
        assertNotEquals(0, manager.getSelectedNode().getArbolPlan());
        assertEquals(7, manager.getListaCursos().size());

        MbAperturaCursos n = manager.getListaCursos().get(0);
        assertNotEquals(0, n.getCursoId());
        assertFalse(n.getCursoCicloLabel().isEmpty());
        assertNotEquals(0, n.getPlanCursoId());
        assertFalse(n.getCursoCodigo().isEmpty());
        assertFalse(n.getCursoNombre().isEmpty());
        assertFalse(n.getCursoEstaAperturado().isEmpty());
        assertFalse(n.getCurapeSisEvaluacion().isEmpty());
        assertFalse(n.getCurapeImgCandado().isEmpty());
        assertNotEquals(0, n.getCurapeSisEvaluacionId());
        assertNotEquals(0, n.getCurapeId());
        assertFalse(n.getCursoSemestre().isEmpty());

        // ------------------------------------------------------------------
        manager.setComboSemestreId(SEMESTRE_TRASLADOS); // SEMESTRE TRASLADOS
        manager.seleccionPorSemestre(actionEvent);
        assertEquals(7, manager.getListaCursos().size());
        assertEquals(SEMESTRE_TRASLADOS, manager.getComboSemestreId());
    }

    @Test
    public void detallarForAperturaTest() {
        manager.setComboSemestreId(SEMESTRE_TRASLADOS);
        //  EDUCACIÓN PRIMARIA E INTERCULTURALIDAD - Ciclo VII
        MbAperturaCursos row = new MbAperturaCursos();
        row.setCursoId(156);
        row.setCursoCicloLabel("VII");
        row.setCursoCodigo("IDENADIC");
        row.setCursoNombre("IDENTIDAD NACIONAL Y DIVERSIDAD CULTURAL");
        row.setPlanCursoId(152);
        row.setCurapeId(-1);
        row.setCurapeSisEvaluacionId(0);
        row.setCursoEstaAperturado("No");

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("curso_aperturado", row));

        manager.detallar(actionEvent);

        assertEquals(row.getCurapeId(), manager.getModalCurapeId());
        assertEquals(row.getPlanCursoId(), manager.getModalPlancurId());
        assertEquals(row.getCursoId(), manager.getModalCursoId());
        assertFalse(manager.getModalCiclo().isEmpty());
        assertFalse(manager.getModalCodigo().isEmpty());
        assertFalse(manager.getModalNombre().isEmpty());
        assertFalse(manager.isModalEstaAperturado());
        assertEquals(7, manager.getComboSistemaEvaluacion().length);

        assertTrue(manager.getTablaDetallePromedios().isEmpty());
        assertTrue(manager.getTablaSecciones().isEmpty());

        openModalRegistroAperturaTest();

        // ------------------------------------------------------------------
        row.setCurapeId(manager.getCurapeId());
        row.setCurapeSisEvaluacionId(manager.getModalSistemaEvaluacionId());
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("curso_aperturado", row));
        manager.detallar(actionEvent);

        assertFalse(manager.isModalEstaAperturado());

        openModalRegistroAperturaToEditTest();
        // ------------------------------------------------------------------
        DAO.deleteOpenedCourse(manager.getCurapeId());
    }

    private void openModalRegistroAperturaTest() {
        manager.setModalSistemaEvaluacionId(7); // SISTEMA D (siseva)
        manager.loadPromediosExamenes(actionEvent);

        List<SelectItem> sisevas = manager.getComboPromediosExamenes();
        int x = 1;
        for (SelectItem se : sisevas) {
            int sisevadetId = Integer.parseInt(se.getValue().toString());
            manager.setSisevaperPromedioId(sisevadetId);
            manager.setSisevaperCodigo(RandomStringUtils.randomAlphabetic(10));
            manager.setSisevaperNombre(RandomStringUtils.randomAlphabetic(20));
            manager.setSisevaperPeso(String.valueOf(x * 3));
            manager.setSisevaperSemana(String.valueOf(x * 4));
            manager.setSisevaperOrden(x);
            manager.agregar(actionEvent);
            x++;
        }
        assertEquals(sisevas.size(), manager.getTablaDetallePromedios().size());
        assertEquals(5, manager.getTablaDetallePromedios().size());

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap(
                "id_det", manager.getTablaDetallePromedios().get(x / 2).getSisevaperId()));
        manager.quitar(actionEvent);
        assertEquals(4, manager.getTablaDetallePromedios().size());

        manager.insertar(actionEvent);
    }

    private void openModalRegistroAperturaToEditTest() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap(
                "id_det", manager.getTablaDetallePromedios().get(0).getSisevaperId()));
        manager.quitar(actionEvent);
        assertEquals(3, manager.getTablaDetallePromedios().size());

        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap(
                "id_det", manager.getTablaDetallePromedios().get(0).getSisevaperId()));
        manager.quitar(actionEvent);
        assertEquals(2, manager.getTablaDetallePromedios().size());

        // ------------------------------------------------------------------
        manager.setSisevaperPromedioId(17); // EXAMEN PARCIAL
        manager.setSisevaperCodigo(RandomStringUtils.randomAlphabetic(5) + "_EDIT");
        manager.setSisevaperNombre(RandomStringUtils.randomAlphabetic(15) + "_EDIT");
        manager.setSisevaperPeso(RandomStringUtils.randomNumeric(1));
        manager.setSisevaperSemana(RandomStringUtils.randomNumeric(1));
        manager.setSisevaperOrden(1);
        manager.agregar(actionEvent);
        assertEquals(3, manager.getTablaDetallePromedios().size());

        // ------------------------------------------------------------------
        manager.setSisevaperPromedioId(14); // PROMEDIO DE ACTIVIDADES
        manager.setSisevaperCodigo(RandomStringUtils.randomAlphabetic(5) + "_EDIT");
        manager.setSisevaperNombre(RandomStringUtils.randomAlphabetic(15) + "_EDIT");
        manager.setSisevaperPeso(RandomStringUtils.randomNumeric(1));
        manager.setSisevaperSemana(RandomStringUtils.randomNumeric(1));
        manager.setSisevaperOrden(2);
        manager.agregar(actionEvent);
        assertEquals(4, manager.getTablaDetallePromedios().size());

        // ------------------------------------------------------------------
        manager.insertar(actionEvent);
    }

    @Test
    public void detallarForSeccionesTest() {
        manager.setComboSemestreId(SEMESTRE_TRASLADOS);
        //  EDUCACIÓN PRIMARIA E INTERCULTURALIDAD - Ciclo VII
        MbAperturaCursos row = new MbAperturaCursos();
        row.setCursoId(166);
        row.setCursoCicloLabel("VII");
        row.setCursoCodigo("ADGED");
        row.setCursoNombre("ADMINISTRACION Y GESTION EDUCATIVA");
        row.setPlanCursoId(154);
        row.setCurapeId(-1);
        row.setCurapeSisEvaluacionId(0);
        row.setCursoEstaAperturado("No");

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("curso_aperturado", row));

        manager.detallar(actionEvent);

        assertEquals(row.getCurapeId(), manager.getModalCurapeId());
        assertEquals(row.getPlanCursoId(), manager.getModalPlancurId());
        assertEquals(row.getCursoId(), manager.getModalCursoId());
        assertFalse(manager.getModalCiclo().isEmpty());
        assertFalse(manager.getModalCodigo().isEmpty());
        assertFalse(manager.getModalNombre().isEmpty());
        assertFalse(manager.isModalEstaAperturado());
        assertNull(manager.getModalSistemaEvaluacion());

        openModalRegistroSeccionesTest();

        // ------------------------------------------------------------------
        DAO.deleteOpenedCourse(manager.getCurapeId());
    }

    private void openModalRegistroSeccionesTest() {
        int x = 1;
        for (; x <= 6; x++) {
            manager.setSecCodigo(RandomStringUtils.randomAlphabetic(10));
            manager.setSecNombre(RandomStringUtils.randomAlphabetic(20));
            manager.setSecVacantes(String.valueOf(x * 5));
            manager.agregarSec(actionEvent);
        }

        assertEquals(6, manager.getTablaSecciones().size());

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap(
                "id_det1", manager.getTablaSecciones().get(x / 2).getSisevaperId()));
        manager.quitarSec(actionEvent);
        assertEquals(5, manager.getTablaSecciones().size());

        manager.insertar(actionEvent);
    }
}
