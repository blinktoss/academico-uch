package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.cursolibre.jsf.beans.BeanClAlumno;
import net.uch.cursolibre.jsf.beans.BeanClHoraria;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import static net.uch.common.Constants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 14/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbSeccionCLTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private static boolean CONFIGURED = true;
    private MbSeccionCL manager;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        loggerUser.setRowUsuId(63); // admincl
        loggerUser.setNivel("0");

        when(externalContext.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(loggerUser);

        manager = new MbSeccionCL();
        manager.init();

        if (CONFIGURED) {
            DAO.updateClSeccion(Arrays.asList(34, 58));
            CONFIGURED = false;
        }
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        TreeNode<String> result = manager.getArbol();

        assertNotNull(result);
        assertNotNull(manager.getNodos());

        // Selection
        String nodeId = "1:2:1:1"; // FUNDAMENTOS DE PROGRAMACIÓN CON JAVA - Con Horario
        NodeSelectedEvent nodeSelectedEvent = mock(NodeSelectedEvent.class);
        UITree uiTree = mock(UITree.class);

        when(nodeSelectedEvent.getComponent()).thenReturn(uiTree);
        when(uiTree.getRowKey()).thenReturn(nodeId);

        manager.seleccion(nodeSelectedEvent);

        assertEquals(1, manager.getListaSecciones().size());
        assertTrue(manager.getListaSecciones().get(0).getImagenHorario().contains("horario.png"));
        assertEquals(23, manager.getListaSecciones().get(0).getCantMatriculados());

        // Selection
        nodeId = "2:2:2:1"; // OFIMATICA | EXCEL XP - Sin Horario

        when(nodeSelectedEvent.getComponent()).thenReturn(uiTree);
        when(uiTree.getRowKey()).thenReturn(nodeId);

        manager.seleccion(nodeSelectedEvent);

        assertEquals(64, manager.getListaSecciones().size());
        assertTrue(manager.getListaSecciones().get(0).getImagenHorario().contains("horario_gris.png"));
        assertEquals(0, manager.getListaSecciones().get(0).getCantMatriculados());
    }

    @Test
    public void openModalForNewSeccionTest() {
        manager.setTalapeId(0);

        when(actionEvent.getComponent()).thenReturn(uiComponent);

        manager.seleccionarSeccion(actionEvent);

        assertTrue(manager.getOncomplete().contains("alert"));

        // For new
        manager.setTalapeId(28); // JAVA DEVELOPER | PROGRAMACIÓN ORIENTADA A OBJETOS CON JAVA

        when(actionEvent.getComponent()).thenReturn(uiComponent);

        manager.seleccionarSeccion(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpSeccion"));
        assertEquals(0, manager.getSeccion().getSecId().intValue());

        // Filling fields
        manager.getSeccion().setSecCodigo("FAKE_SECCION_CODE");
        manager.getSeccion().setSecNombre("FAKE_SECCION_NAME");
        manager.getSeccion().setSecVacMin(10);
        manager.getSeccion().setSecVacMax(40);
        manager.getSeccion().setSecFinicio(Date.valueOf(LocalDate.of(2024, Month.MARCH, 1)));
        manager.getSeccion().setSecFfin(Date.valueOf(LocalDate.of(2024, Month.MARCH, 30)));
        manager.getSeccion().setSecFinicioMatricula(Date.valueOf(LocalDate.of(2024, Month.FEBRUARY, 1)));
        manager.getSeccion().setSecFfinMatricula(Date.valueOf(LocalDate.of(2024, Month.FEBRUARY, 25)));
        manager.getSeccion().setSecTurno(manager.getTurnos()[0].getValue().toString());

        manager.insertarActualizarSeccion(actionEvent);

        assertTrue(manager.getOncomplete().contains("Registro"));

        // Open Modal for Edit
        when(uiComponent.findComponent(PARAM_SECCION_ID)).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getSeccion().getSecId());

        manager.seleccionarSeccion(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpSeccion"));
        assertNotEquals(0, manager.getSeccion().getSecId().intValue());

        // Editing fields
        manager.getSeccion().setSecCodigo("FAKE_EDIT_SEC_CODE");
        manager.getSeccion().setSecNombre("FAKE_EDIT_SECCION_NAME");
        manager.getSeccion().setSecVacMin(5);
        manager.getSeccion().setSecVacMax(50);
        manager.getSeccion().setSecTurno(manager.getTurnos()[1].getValue().toString());

        manager.insertarActualizarSeccion(actionEvent);

        assertTrue(manager.getOncomplete().contains("Actualizacion"));

        // Deleting field
        UIParameter uipDetail = mock(UIParameter.class);

        when(uiComponent.findComponent(PARAM_SECCION_ID)).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getSeccion().getSecId());
        when(uiComponent.findComponent(PARAM_SECCION_DETALLE)).thenReturn(uipDetail);
        when(uipDetail.getValue()).thenReturn(
                manager.getSeccion().getSecCodigo().concat(
                        manager.getSeccion().getSecNombre()));

        manager.verificarEliminacion(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpEliminar"));
        assertEquals(manager.getSeccion().getSecId().intValue(), manager.getMpSecId());

        manager.eliminarSeccion(actionEvent);

        assertEquals(0, manager.getMpSecId());
        assertTrue(manager.getMpSecDetalle().isEmpty());
    }

    @Test
    public void openModalHorarioTest() {
        UIParameter uipSecId = mock(UIParameter.class);
        UIParameter uipSecDetail = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent(PARAM_SECCION_ID)).thenReturn(uipSecId);
        when(uipSecId.getValue()).thenReturn(2206);
        when(uiComponent.findComponent(PARAM_SECCION_DETALLE)).thenReturn(uipSecDetail);
        when(uipSecDetail.getValue()).thenReturn("COMPUTACION BASICA ORIENTADA A LA EDUCACION");

        manager.cargarHorario(actionEvent);

        assertNull(manager.getModNombre());
        assertNull(manager.getCurNombre());
        assertNull(manager.getTalapeNombre());
        assertNotEquals(0, manager.getMpSecId());
        assertFalse(manager.getMpSecDetalle().isEmpty());
        assertNull(manager.getHoraria().getAcAula().getId());
        assertNotEquals(0, manager.getAulas().length);
        assertNull(manager.getHoraria().getAcDocente().getId());
        assertNotEquals(0, manager.getProfesores().length);
        assertFalse(manager.getHoraria().getHorTipoClase().isEmpty());
        assertNotEquals(0, manager.getTipoClase().length);
        assertEquals(CAT_DEFAULT, manager.getHoraria().getHorDia());
        assertNotEquals(0, manager.getDias().length);
        assertNotEquals(0, manager.getMeridianos().length);
        assertEquals(2, manager.getMpnListaHoraria().size());

        addAndRemoveHorariosTest();

        // Saving editing
        manager.guardarHorarios(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpHoraria"));

        // Opening again
        manager.cargarHorario(actionEvent);

        editingRowsHorariosTest();
    }

    private void addAndRemoveHorariosTest() {
        // Filling fields #1
        manager.getHoraria().getAcAula().setId(11); // 301
        manager.getHoraria().getAcDocente().setId(53); // ALZAMORA
        manager.getHoraria().setHorTipoClase("018001"); // TEORÍA
        manager.getHoraria().setHorDia("016006"); // SABADO
        manager.getHoraria().setHoraInicio("10");
        manager.getHoraria().setMinutoInicio("30");
        manager.getHoraria().setMeridianoInicio("AM");
        manager.getHoraria().setHoraFin("12");
        manager.getHoraria().setMinutoFin("30");
        manager.getHoraria().setMeridianoFin("PM");

        manager.agregarHorario(actionEvent);

        assertTrue(manager.getOncomplete().isEmpty());
        assertEquals(3, manager.getMpnListaHoraria().size());

        // Filling fields #2
        manager.getHoraria().getAcAula().setId(12); // 302
        manager.getHoraria().getAcDocente().setId(51); // ARROYO
        manager.getHoraria().setHorTipoClase("018003"); // LABORATORIO
        manager.getHoraria().setHorDia("016007"); // DOMINGO
        manager.getHoraria().setHoraInicio("10");
        manager.getHoraria().setMinutoInicio("30");
        manager.getHoraria().setMeridianoInicio("AM");
        manager.getHoraria().setHoraFin("12");
        manager.getHoraria().setMinutoFin("30");
        manager.getHoraria().setMeridianoFin("PM");

        manager.agregarHorario(actionEvent);

        assertTrue(manager.getOncomplete().isEmpty());
        assertEquals(4, manager.getMpnListaHoraria().size());

        // Removing position 0
        manager.getHoraria().setPosicion(0);
        UIParameter uipPHorPos = mock(UIParameter.class);
        when(uiComponent.findComponent("p_hor_pos")).thenReturn(uipPHorPos);
        when(uipPHorPos.getValue()).thenReturn(manager.getHoraria().getPosicion());

        manager.removerHorario(actionEvent);

        assertEquals(3, manager.getMpnListaHoraria().size());

        // Removing position 2
        manager.getHoraria().setPosicion(2);
        when(uipPHorPos.getValue()).thenReturn(manager.getHoraria().getPosicion());

        manager.removerHorario(actionEvent);

        assertEquals(2, manager.getMpnListaHoraria().size());
    }

    private void editingRowsHorariosTest() {
        BeanClHoraria row = manager.getMpnListaHoraria().get(0);

        row.edit(actionEvent);

        assertEquals(row.getAcAula().getId().intValue(), row.getAuxAula());
        assertEquals(row.getAcDocente().getId().intValue(), row.getAuxDocente());
        assertEquals(row.getHorDia(), row.getAuxDia());
        assertEquals(row.getHorTipoClase(), row.getAuxTipoClase());
        assertFalse(row.getHoraInicio().isEmpty());
        assertFalse(row.getMinutoInicio().isEmpty());
        assertFalse(row.getMeridianoInicio().isEmpty());
        assertFalse(row.getHoraFin().isEmpty());
        assertFalse(row.getMinutoFin().isEmpty());
        assertFalse(row.getMeridianoFin().isEmpty());

        row.aceptar(actionEvent);

        // ------------------------------------------------------------------
        row.edit(actionEvent);
        assertTrue(row.isEditable());

        manager.guardarHorarios(actionEvent);
        assertTrue(manager.getOncomplete().contains("Termine de editar los horarios"));

        // ------------------------------------------------------------------
        row.cancelar(actionEvent);
        assertFalse(row.isEditable());
    }

    @Test
    public void openModalMatriculadosAndCheckEnrolledTest() {
        manager.setModId(8); // INGLES - BÁSICO
        manager.setCurId(24); // BÁSICO I

        UIParameter uipSecId = mock(UIParameter.class);
        UIParameter uipSecDetail = mock(UIParameter.class);

        // Con alumnos matriculados
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent(PARAM_SECCION_ID)).thenReturn(uipSecId);
        when(uipSecId.getValue()).thenReturn(67);
        when(uiComponent.findComponent(PARAM_SECCION_DETALLE)).thenReturn(uipSecDetail);
        when(uipSecDetail.getValue()).thenReturn("SEC100608 | I-BASICOI-L-V4:30-6:00PM");

        manager.cargarMatriculados(actionEvent);

        assertEquals(0, manager.getMpTalDestId());
        assertEquals(7, manager.getMpTalDestDesc().size());
        assertTrue(manager.getMpSecDestDesc().isEmpty());
        assertTrue(manager.getMpEstpagDestDesc().isEmpty());
        assertNotNull(manager.getMpSecDetalle());
        assertNotEquals(0, manager.getTotalMatriculados());
        assertEquals(19, manager.getListaAlumnosMatriculados().size());
        BeanClAlumno row = manager.getListaAlumnosMatriculados().get(0);
        assertNotEquals(0, row.getAluContador());
        assertTrue(row.isAluMatriculado());

        // Sin alumnos matriculados
        when(uiComponent.findComponent(PARAM_SECCION_ID)).thenReturn(uipSecId);
        when(uipSecId.getValue()).thenReturn(2165);
        when(uiComponent.findComponent(PARAM_SECCION_DETALLE)).thenReturn(uipSecDetail);
        when(uipSecDetail.getValue()).thenReturn("SEC100916 | I-BASICOII-L-V-4:30-6PM");

        manager.cargarMatriculados(actionEvent);

        assertNotNull(manager.getMpSecDetalle());
        assertTrue(manager.getListaAlumnosMatriculados().isEmpty());
        assertTrue(manager.getStyle().contains("gray"));
    }

    @Test
    public void openModalMatriculadosAndEnrollTest() {
        manager.setModId(2); // ESPECIALISTA EN OFIMÁTICA
        manager.setCurId(2); // MS WORD 2007

        UIParameter uipSecId = mock(UIParameter.class);
        UIParameter uipSecDetail = mock(UIParameter.class);

        // Con alumnos matriculados
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent(PARAM_SECCION_ID)).thenReturn(uipSecId);
        when(uipSecId.getValue()).thenReturn(31);
        when(uiComponent.findComponent(PARAM_SECCION_DETALLE)).thenReturn(uipSecDetail);
        when(uipSecDetail.getValue()).thenReturn("A | EO-WINWORD-A-LYM-8-12");

        manager.cargarMatriculados(actionEvent);

        assertEquals(30, manager.getListaAlumnosMatriculados().size());

        // ------------------------------------------------------------------
        manager.guardarMatriculados(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione una seccion"));

        // ------------------------------------------------------------------
        manager.setMpTalDestId(3);
        manager.loadSeccionesFromTaller(actionEvent);
        assertFalse(manager.getMpSecDestDesc().isEmpty());
        assertEquals(1, manager.getMpEstpagDestDesc().size());

        // ------------------------------------------------------------------
        int secIdDest = 34;
        manager.setMpSecDestId(secIdDest);
        manager.guardarMatriculados(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione una estructura de pago"));

        // ------------------------------------------------------------------
        manager.setMpEstpagDestId(2);
        manager.guardarMatriculados(actionEvent);
        assertTrue(manager.getOncomplete().contains("Alumnos matriculados con exito en el taller"));

        // ------------------------------------------------------------------
        DAO.cleanClMatricula(secIdDest);
    }
}
