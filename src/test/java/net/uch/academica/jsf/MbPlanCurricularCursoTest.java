package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.academica.jsf.beans.BeanPlanCurricularCurso;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static net.uch.Stubs.Especialidad;
import static net.uch.Stubs.Facultad;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 06/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbPlanCurricularCursoTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private MbPlanCurricularCurso manager;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbPlanCurricularCurso();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] resultFacultades = manager.getParamFacultades();
        List<SelectItem> resultEspecialidades = manager.getComboEspecialidadesS();
        SelectItem[] resultTipoCurso = manager.getComboTipos();

        assertEquals(5, resultFacultades.length);
        assertTrue(resultEspecialidades.isEmpty());
        assertEquals(2, resultTipoCurso.length);

        // On change
        manager.setParamFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.cargarParamEspecialidades(actionEvent);

        resultEspecialidades = manager.getComboEspecialidadesS();

        assertEquals(5, resultFacultades.length);
        assertEquals(1, resultEspecialidades.size());
    }

    @Test
    public void seleccionarTest() {
        manager.setParamCodigo("");
        manager.setParamDescripcion("");
        manager.setParamFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.setParamEspecialidadId(Especialidad.EDUCACION_PRIMARIA);

        manager.seleccionar(actionEvent);

        List<BeanPlanCurricularCurso> result = manager.getListaPlanCurricular();

        assertEquals(1, result.size());

        BeanPlanCurricularCurso row = result.get(0);
        assertNotEquals(0, row.getRowId());
        assertFalse(row.getRowCodigo().isEmpty());
        assertFalse(row.getRowDescripcion().isEmpty());
        assertNotNull(row.getRowCreacion());
        assertFalse(row.getRowResolucion().isEmpty());
        assertNotEquals(0, row.getFacultadId());
        assertFalse(row.getRowFacultadDesc().isEmpty());
        assertNotEquals(0, row.getEspecialidadId());
        assertFalse(row.getRowEspecialidadDesc().isEmpty());
    }

    @Test
    public void openModalForNewPlanCurricularTest() {
        manager.nuevo(actionEvent);

        assertEquals(0, manager.getMpFacultadId());
        assertEquals(0, manager.getMpEspecialidadId());
        assertTrue(manager.getMpCodigo().isEmpty());
        assertTrue(manager.getMpDescripcion().isEmpty());
        assertNull(manager.getMpCreacion());
        assertTrue(manager.getMpResolucion().isEmpty());
        assertTrue(manager.getComboEspecialidadesS().isEmpty());
    }

    @Test
    public void saveNewEditDeletePlanCurricularTest() {
        manager.setMpCodigo("FAKE_CODE");
        manager.setMpDescripcion("FAKE_DESCRIPTION");
        manager.setMpCreacion(new Date());
        manager.setMpResolucion("FAKE_RESOLUTION");
        manager.setMpFacultadId(Facultad.ING_ELECTRONICA);
        manager.setMpEspecialidadId(Especialidad.ING_ELECTRONICA);

        manager.setbModificacionAux("0");

        // Insert new Curricular Plan
        manager.enviar(actionEvent);

        assertTrue(manager.getOncomplete().contains("'mp'"));
        assertNotEquals(0, manager.getMpId());

        // Find recent insert Curricular Plan
        manager.setParamCodigo("FAKE");
        manager.setParamDescripcion("FAKE");
        manager.seleccionar(actionEvent);

        assertEquals(1, manager.getListaPlanCurricular().size());

        // Opening Modal to Edit
        UIParameter uiParameterId = mock(UIParameter.class);
        UIParameter uiParameterCode = mock(UIParameter.class);
        UIParameter uiParameterDescription = mock(UIParameter.class);
        UIParameter uiParameterFaculty = mock(UIParameter.class);
        UIParameter uiParameterFacultyDescription = mock(UIParameter.class);
        UIParameter uiParameterSpecialty = mock(UIParameter.class);
        UIParameter uiParameterSpecialtyDescription = mock(UIParameter.class);
        UIParameter uiParameterResolution = mock(UIParameter.class);
        UIParameter uiParameterCreation = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getMpId());
        when(uiComponent.findComponent("p_codigo")).thenReturn(uiParameterCode);
        when(uiParameterCode.getValue()).thenReturn("FAKE_EDIT_CODE");
        when(uiComponent.findComponent("p_descripcion")).thenReturn(uiParameterDescription);
        when(uiParameterDescription.getValue()).thenReturn("FAKE_EDIT_DESCRIPTION");
        when(uiComponent.findComponent("p_facultad")).thenReturn(uiParameterFaculty);
        when(uiParameterFaculty.getValue()).thenReturn(Facultad.ING_SISTEMAS);
        when(uiComponent.findComponent("p_facultad_d")).thenReturn(uiParameterFacultyDescription);
        when(uiParameterFacultyDescription.getValue()).thenReturn("SOME FACULTY DESCRIPTION");
        when(uiComponent.findComponent("p_especialidad")).thenReturn(uiParameterSpecialty);
        when(uiParameterSpecialty.getValue()).thenReturn(Especialidad.ING_SISTEMAS);
        when(uiComponent.findComponent("p_especialidad_d")).thenReturn(uiParameterSpecialtyDescription);
        when(uiParameterSpecialtyDescription.getValue()).thenReturn("SOME SPECIALITY DESCRIPTION");
        when(uiComponent.findComponent("p_resolucion")).thenReturn(uiParameterResolution);
        when(uiParameterResolution.getValue()).thenReturn("FAKE_EDIT_RESOLUTION");
        when(uiComponent.findComponent("p_creacion")).thenReturn(uiParameterCreation);
        when(uiParameterCreation.getValue()).thenReturn("2024-01-05");

        manager.editarFila(actionEvent);

        assertFalse(manager.getLblFacultadDescripcion().isEmpty());
        assertFalse(manager.getLblEspecialidadDescripcion().isEmpty());
        assertFalse(manager.getMpCodigo().isEmpty());
        assertFalse(manager.getMpDescripcion().isEmpty());
        assertFalse(manager.getLblCreacionDescripcion().isEmpty());
        assertNotNull(manager.getMpCreacion());
        assertFalse(manager.getMpResolucion().isEmpty());

        // Update the Curricular Plan
        manager.enviar(actionEvent);

        assertTrue(manager.getOncomplete().contains("'mp'"));
        assertNotEquals(0, manager.getMpId());

        // Find recent insert Curricular Plan
        manager.setParamCodigo("FAKE_EDIT");
        manager.setParamDescripcion("FAKE_EDIT");
        manager.seleccionar(actionEvent);
        assertEquals(1, manager.getListaPlanCurricular().size());

        // Delete
        when(uiComponent.findComponent("delete")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getMpId());

        manager.eliminarFila(actionEvent);

        // Find recent deleted Plan Curricular
        manager.setParamCodigo("FAKE_EDIT");
        manager.setParamDescripcion("FAKE_EDIT");
        manager.seleccionar(actionEvent);
        assertEquals(0, manager.getListaPlanCurricular().size());
    }

    @Test
    public void openModalForInsertCoursesTest() {
        manager.setMpEspecialidadId(Especialidad.ING_ELECTRONICA);
        manager.setMpId(5);

        List<SelectItem> resultSemestres = manager.getComboSemestre();
        List<SelectItem> resultCursos = manager.getComboCursos();

        manager.openModalForInsertCourses();

        assertEquals(Especialidad.ING_ELECTRONICA, manager.getMpEspecialidadId());
        assertEquals(10, resultSemestres.size());
        assertTrue(manager.getCodigo1().isEmpty());
        assertEquals(0, manager.getNombre1());
        assertTrue(manager.getCreditos1().isEmpty());
        assertNull(manager.getTipo1());
        assertEquals(79, resultCursos.size());
        assertTrue(manager.getCreditos1().isEmpty());

        List<BeanPlanCurricularCurso> courses = manager.getListaCursos();
        assertEquals(79, courses.size());
        BeanPlanCurricularCurso row = courses.get(0);
        assertNotEquals(0, row.getRowId());
        assertFalse(row.getCiclo().isEmpty());
        assertFalse(row.getRowDescripcion().isEmpty());
        assertFalse(row.getRowCodigo().isEmpty());
        assertFalse(row.getRowCreditos().isEmpty());
        assertFalse(row.getRowTipos().isEmpty());

        // ------------------------------------------------------------------
        manager.setMpId(5);
        manager.setNombre1(29);
        manager.setCiclo("006001");
        manager.agregarFila(actionEvent);

        assertTrue(manager.getOncomplete().contains("El curso ya fue agregado"));
    }

    @Test
    public void openModalEquivalenciasTest() {
        int plancurId = 1;

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("plancur_id", plancurId));

        manager.openModalEquivalencias(actionEvent);

        assertTrue(manager.getEquivalences().isEmpty());

        // ------------------------------------------------------------------
        int size = 5;
        DAO.insertAcEquivalencia(plancurId, size);

        manager.openModalEquivalencias(actionEvent);

        assertEquals(size, manager.getEquivalences().size());

        DAO.deleteAcEquivalencia();
    }
}
