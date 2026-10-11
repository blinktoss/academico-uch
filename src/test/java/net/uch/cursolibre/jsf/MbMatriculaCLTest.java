package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.uch.common.Constants.CAT_TIPO_CLIENTE_ALUMNO_CL;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 08/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbMatriculaCLTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private static boolean CONFIGURED = true;
    private MbMatriculaCL manager;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbMatriculaCL();
        manager.init();

        if (CONFIGURED) {
            DAO.updateClSeccion(Arrays.asList(2206, 31, 41, 44));
            CONFIGURED = false;
        }
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void seleccionarTest() {
        // Empty
        manager.setParamAluCodigo("");
        manager.setParamAluAppaterno("");
        manager.setParamAluApmaterno("");
        manager.setParamAluNombres("");

        manager.seleccionar(actionEvent);

        assertTrue(manager.getOncomplete().contains("alert"));
        assertTrue(manager.getMatriculas().isEmpty());

        // By code
        manager.setParamAluCodigo("44");
        manager.setParamAluAppaterno("");
        manager.setParamAluApmaterno("");
        manager.setParamAluNombres("");

        manager.seleccionar(actionEvent);

        assertEquals(18, manager.getMatriculas().size());

        // By name
        manager.setParamAluCodigo("");
        manager.setParamAluAppaterno("di");
        manager.setParamAluApmaterno("r");
        manager.setParamAluNombres("d");

        manager.seleccionar(actionEvent);

        assertEquals(2, manager.getMatriculas().size());
        MbMatriculaCL row = manager.getMatriculas().get(0);
        assertNotEquals(0, row.getStudentId());
        assertFalse(row.getParamAluCodigo().isEmpty());
        assertFalse(row.getParamAluNombres().isEmpty());
        assertFalse(row.getParamAluAppaterno().isEmpty());
        assertFalse(row.getParamAluApmaterno().isEmpty());
        assertFalse(row.getRowAluTipo().isEmpty());

        assertFalse(row.getMatriculas().isEmpty());
        assertNotEquals(0, row.getMatriculas().get(0).getDetMatriculaId());
        assertNotNull(row.getMatriculas().get(0).getDetMatFecha());
        assertFalse(row.getMatriculas().get(0).getDetMatTipoCode().isEmpty());
        assertFalse(row.getMatriculas().get(0).getDetMatTipoValue().isEmpty());
        assertFalse(row.getMatriculas().get(0).getDetTaller().isEmpty());
        assertFalse(row.getMatriculas().get(0).getDetSeccion().isEmpty());
        assertNotNull(row.getMatriculas().get(0).getDetFechaInicio());

        row.mostrarDetalle(actionEvent);
        assertTrue(row.isVerDetalle());
        assertTrue(row.getRowImagenMostrar().contains("up.png"));
        assertFalse(row.getRowTextoMostrar().isEmpty());

        row.mostrarDetalle(actionEvent);
        assertFalse(row.isVerDetalle());
        assertTrue(row.getRowImagenMostrar().contains("down.png"));
        assertFalse(row.getRowTextoMostrar().isEmpty());
    }

    @Test
    public void eliminarMatriculaTest() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("p_mat_id_delete", 2321);
        attributes.put("p_alu_id_delete", 1813);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.verificarPagosMatricula(actionEvent);

        assertFalse(manager.getMensajeEliminar().isEmpty());
        assertTrue(manager.getOncomplete().contains("mp2"));
    }

    @Test
    public void autocompleteTest() {
        manager.setSuggestion("diaz");
        // By Name
        List<MbMatriculaCL> result = manager.autocomplete(manager.getSuggestion());

        assertNotNull(result);
        assertEquals(16, result.size());
        assertNotEquals(0, result.get(0).getStudentId());
        assertNotNull(result.get(0).getStudentCode());
        assertNotNull(result.get(0).getStudentNames());

        // By Code
        result = manager.autocomplete("10100");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotEquals(0, result.get(0).getStudentId());
        assertNotNull(result.get(0).getStudentCode());
        assertNotNull(result.get(0).getStudentNames());

        // ------------------------------------------------------------------
        result = manager.autocomplete("null");
        assertTrue(result.isEmpty());

        // ------------------------------------------------------------------
        result = manager.autocomplete("");
        assertTrue(result.isEmpty());
    }

    @Test
    public void openModalForMatricularAlumnoTest() {
        SelectItem[] areas = manager.getMpAreas();
        List<SelectItem> modulos = manager.getMpModulos();
        List<SelectItem> estructuras = manager.getMpEstructurasPago();

        manager.abrirMatricula(actionEvent);

        assertEquals(15, areas.length);
        assertTrue(modulos.isEmpty());
        assertTrue(estructuras.isEmpty());

        // On change area
        manager.setMpAreaId(1); // PROGRAMACIÓN
        manager.verModulos(actionEvent);
        modulos = manager.getMpModulos();

        assertEquals(1, manager.getMpAreaId());
        assertEquals(15, areas.length);
        assertEquals(10, modulos.size());
        assertTrue(estructuras.isEmpty());

        // On change modulo
        manager.setMpModId(7); // JAVA DEVELOPER
        manager.verEstructurasPago(actionEvent);
        estructuras = manager.getMpEstructurasPago();

        assertEquals(15, areas.length);
        assertEquals(10, modulos.size());
        assertEquals(1, estructuras.size());

        manager.setMpEstpagId(20); // JAVA DEVELOPER

        manager.mostrarCursos(actionEvent);

        assertTrue(manager.getMensajeError().contains("No hay"));
        assertFalse(manager.isVerCursos());

        // On change modulo
        manager.setMpModId(0); // JAVA DEVELOPER
        manager.verEstructurasPago(actionEvent);
        estructuras = manager.getMpEstructurasPago();

        assertEquals(15, areas.length);
        assertEquals(10, modulos.size());
        assertTrue(estructuras.isEmpty());

        // On change area
        manager.setMpAreaId(0); // PROGRAMACIÓN
        manager.verModulos(actionEvent);
        modulos = manager.getMpModulos();
        estructuras = manager.getMpEstructurasPago();

        assertEquals(15, areas.length);
        assertTrue(modulos.isEmpty());
        assertTrue(estructuras.isEmpty());
    }

    @Test
    public void mostrarCursosTest() {
        manager.setMpModId(2); // ESPECIALISTA EN OFIMÁTICA
        manager.setMpEstpagId(2); // ACCESS-L-Y-M8AM-12PM

        manager.mostrarCursos(actionEvent);

        assertTrue(manager.isVerCursos());
        MbMatriculaCL row = manager.getSecciones().get(0);
        assertNotEquals(0, row.getSeccionId());
        assertFalse(row.getMpModulo().isEmpty());
        assertFalse(row.getMpRowCurso().isEmpty());
        assertFalse(row.getMpRowSeccion().isEmpty());
        assertNotNull(row.getMpRowFecInicio());
        assertNotEquals(0, row.getMpRowNroMat());
        assertFalse(row.getMpRowTtMessage().isEmpty());
        assertFalse(row.getMpRowNroMatStyle().isEmpty());
        assertFalse(row.getMpRowTtMessage().isEmpty());
        assertTrue(row.isMpRowVerMessage());
        assertNotEquals(0, row.getSeccionVacMax());
        assertTrue(row.isMpRowAgregar());
        assertNotEquals(0, row.getMpRowEstpagdetId());
        assertNotEquals(0, row.getMpRowConpagId());
        assertNotNull(row.getMpRowFechaPago());
        assertNotNull(row.getMpRowFechaProrroga());
        assertEquals(40, row.getMpRowMontoBase(), 0);
        assertEquals(40, row.getMpRowMontoPagar(), 0);
        assertEquals(0, row.getMpRowMontoEditar(), 0);
        assertEquals(CAT_TIPO_CLIENTE_ALUMNO_CL, row.getMpRowAlutarTipo());

        manager.deseleccionarTodos(actionEvent);
        assertFalse(row.isMpRowAgregar());

        manager.matricularSeleccionados(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione un alumno"));

        // ------------------------------------------------------------------
        manager.setStudentId(1813); // DIAZ LURITA
        manager.matricularSeleccionados(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe seleccionar cursos para proceder a Matricular"));

        // ------------------------------------------------------------------
        manager.seleccionarTodos(actionEvent);
        assertTrue(row.isMpRowAgregar());

        // ------------------------------------------------------------------
        row.editarMonto(actionEvent);
        assertTrue(row.isMpRowEditar());

        row.guardarMonto(actionEvent);
        assertFalse(row.isMpRowEditar());

        row.cancelarMonto(actionEvent);
        assertFalse(row.isMpRowEditar());

        // ------------------------------------------------------------------
        manager.matricularSeleccionados(actionEvent);
        assertTrue(manager.getOncomplete().contains("Una de las secciones ya llegó al máximo de vacantes"));
    }

    @Test
    public void mostrarCursosToEnrollTest() {
        manager.setMpModId(79); // PROGRAMA DE INFORMATICA EDUCATIVA | MODULO I
        manager.setMpEstpagId(34); // ESTPAG-TEST1

        manager.mostrarCursos(actionEvent);

        assertTrue(manager.isVerCursos());

        // ------------------------------------------------------------------
        manager.setStudentId(345); // RONDINEL BUSTAMANTE
        manager.matricularSeleccionados(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ya es matriculado en la seccion"));
    }
}
