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
 * Created for sistema_academico on 28/12/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbCursoTest extends SpringConfigurationTest {

    private MbCurso manager;

    @Before
    public void setUp() {
        manager = new MbCurso();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void getComboFacultadesAndEspecialidadesToSearchTest() {
        SelectItem[] facultades = manager.getComboFacultades();
        List<SelectItem> result = manager.getParamEspecialidades();

        assertEquals(5, facultades.length);
        assertTrue(result.isEmpty());

        manager.setParamFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.onChangeParamFacultades(actionEvent);

        result = manager.getParamEspecialidades();
        assertEquals(1, result.size());

        // ------------------------------------------------------------------
        manager.setParamFacultadId(0);
        manager.onChangeParamFacultades(actionEvent);

        result = manager.getParamEspecialidades();
        assertTrue(result.isEmpty());
    }

    @Test
    public void getComboFacultadesAndEspecialidadesOnModalTest() {
        SelectItem[] facultades = manager.getComboFacultades();
        List<SelectItem> result = manager.getMpEspecialidades();

        assertEquals(5, facultades.length);
        assertTrue(result.isEmpty());

        manager.setMpFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.onChangeModalFacultades(actionEvent);

        result = manager.getMpEspecialidades();
        assertEquals(1, result.size());

        // ------------------------------------------------------------------
        manager.setMpFacultadId(0);
        manager.onChangeModalFacultades(actionEvent);

        result = manager.getMpEspecialidades();
        assertTrue(result.isEmpty());
    }

    @Test
    public void openModalForNuevoCursoTest() {
        manager.nuevo(actionEvent);

        assertEquals(0, manager.getMpId());
        assertTrue(manager.getMpCodigo().isEmpty());
        assertTrue(manager.getMpNombre().isEmpty());
        assertEquals(0, manager.getMpEspecialidadId());
        assertEquals(0, manager.getMpFacultadId());
        assertTrue(manager.getMpEspecialidades().isEmpty());

        createCursoTest();

        editCursoTest();

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("delete")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getMpId());

        manager.eliminarFila(actionEvent);
    }

    private void createCursoTest() {
        manager.enviar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese los datos necesarios"));

        // ------------------------------------------------------------------
        manager.setMpCodigo("FAKE_" + RandomStringUtils.randomAlphanumeric(10));
        manager.enviar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese los datos necesarios"));

        // ------------------------------------------------------------------
        manager.setMpNombre("FAKE_" + RandomStringUtils.randomAlphanumeric(15));
        manager.enviar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese los datos necesarios"));

        // ------------------------------------------------------------------
        manager.setMpFacultadId(Facultad.ENFERMERIA);
        manager.onChangeModalFacultades(actionEvent);
        manager.enviar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese los datos necesarios"));

        // ------------------------------------------------------------------
        manager.setMpEspecialidadId(Especialidad.ENFERMERIA);
        manager.enviar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se creo un Curso correctamente"));
    }

    private void editCursoTest() {
        int b_id_u = manager.getMpId();
        String b_codigo_u = manager.getMpCodigo();
        String b_nombre_u = manager.getMpNombre();

        UIParameter uiParameterBIdU = mock(UIParameter.class);
        UIParameter uiParameterBCodigoU = mock(UIParameter.class);
        UIParameter uiParameterBNombreU = mock(UIParameter.class);
        UIParameter uiParameterBFacultadU = mock(UIParameter.class);
        UIParameter uiParameterBEspecialidadU = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);

        when(uiComponent.findComponent("p_id")).thenReturn(uiParameterBIdU);
        when(uiParameterBIdU.getValue()).thenReturn(b_id_u);

        when(uiComponent.findComponent("p_codigo")).thenReturn(uiParameterBCodigoU);
        when(uiParameterBCodigoU.getValue()).thenReturn(b_codigo_u);

        when(uiComponent.findComponent("p_nombre")).thenReturn(uiParameterBNombreU);
        when(uiParameterBNombreU.getValue()).thenReturn(b_nombre_u);

        when(uiComponent.findComponent("p_facultad")).thenReturn(uiParameterBFacultadU);
        when(uiParameterBFacultadU.getValue()).thenReturn(manager.getMpFacultadId());

        when(uiComponent.findComponent("p_especialidad")).thenReturn(uiParameterBEspecialidadU);
        when(uiParameterBEspecialidadU.getValue()).thenReturn(manager.getMpEspecialidadId());

        manager.editarFila(actionEvent);

        assertEquals(b_id_u, manager.getMpId());
        assertEquals(b_codigo_u, manager.getMpCodigo());
        assertEquals(b_nombre_u, manager.getMpNombre());
        assertEquals(Facultad.ENFERMERIA, manager.getMpFacultadId());
        assertEquals(Especialidad.ENFERMERIA, manager.getMpEspecialidadId());

        // ------------------------------------------------------------------
        manager.setMpCodigo("FAKE_EDIT_" + RandomStringUtils.randomAlphanumeric(5));
        manager.setMpNombre("FAKE_EDIT_" + RandomStringUtils.randomAlphanumeric(10));

        manager.enviar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se actualizo el Curso correctamente"));
    }

    @Test
    public void seleccionarTest() {
        manager.setParamCodigo("");
        manager.setParamNombre("");
        manager.setParamFacultadId(0);
        manager.setParamEspecialidadId(0);

        manager.seleccionar();

        assertFalse(manager.getListaCursos().isEmpty());

        MbCurso row = manager.getListaCursos().get(0);
        assertNotEquals(0, row.getRowId());
        assertFalse(row.getParamCodigo().isEmpty());
        assertFalse(row.getParamNombre().isEmpty());
        assertFalse(row.getRowFacultadDescripcion().isEmpty());
        assertFalse(row.getRowEspecialidadDescripcion().isEmpty());

        // ------------------------------------------------------------------
        manager.setParamFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.onChangeParamFacultades(actionEvent);
        manager.setParamEspecialidadId(Especialidad.EDUCACION_PRIMARIA);

        manager.seleccionar();

        assertEquals(67, manager.getListaCursos().size());
    }
}
