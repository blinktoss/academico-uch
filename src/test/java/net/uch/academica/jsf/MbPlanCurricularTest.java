package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

import static net.uch.Stubs.Especialidad;
import static net.uch.Stubs.Facultad;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 20/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbPlanCurricularTest extends SpringConfigurationTest {

    private MbPlanCurricular manager;

    @Before
    public void setUp() {
        manager = new MbPlanCurricular();
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] resultFacultades = manager.getParamComboFacultades();
        List<SelectItem> resultEspecialidades = manager.getParamComboEspecialidades();

        assertEquals(5, resultFacultades.length);
        assertTrue(resultEspecialidades.isEmpty());

        // On change
        manager.setParamFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.onChangeParamFacultad(actionEvent);

        resultEspecialidades = manager.getParamComboEspecialidades();

        assertEquals(5, resultFacultades.length);
        assertEquals(1, resultEspecialidades.size());
    }

    @Test
    public void seleccionarAllTest() {
        manager.setParamCodigo("");
        manager.setParamDescripcion("");
        manager.setParamFacultadId(0);
        manager.setParamEspecialidadId(0);

        manager.seleccionar();

        assertFalse(manager.getListaPlanCurricular().isEmpty());
        assertEquals(5, manager.getListaPlanCurricular().size());
    }

    @Test
    public void seleccionarBySpecialityTest() {
        manager.setParamCodigo("");
        manager.setParamDescripcion("");
        manager.setParamFacultadId(Facultad.CONTABILIDAD);
        manager.setParamEspecialidadId(Especialidad.CONTABILIDAD);

        manager.seleccionar();

        assertFalse(manager.getListaPlanCurricular().isEmpty());
        assertEquals(1, manager.getListaPlanCurricular().size());
        assertNotNull(manager.getListaPlanCurricular().get(0).getPcFacultadDescripcion());
        assertNotNull(manager.getListaPlanCurricular().get(0).getPcEspecialidadDescripcion());
    }

    @Test
    public void openModalForNewPlanCurricularTest() {
        SelectItem[] resultFacultades = manager.getParamComboFacultades();
        List<SelectItem> resultEspecialidades = manager.getComboEspecialidades();
        manager.setPcEspecialidadId(0);

        manager.nuevo(actionEvent);

        assertEquals(0, manager.getPcId());
        assertEquals("", manager.getPcCodigo());
        assertNull(manager.getPcCreacion());
        assertEquals("", manager.getPcDescripcion());
        assertEquals("", manager.getPcResolucion());
        assertEquals(0, manager.getPcFacultadId());
        assertEquals(0, manager.getPcEspecialidadId());
        assertEquals(5, resultFacultades.length);
        assertTrue(resultEspecialidades.isEmpty());

        // On change
        manager.setPcFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.onChangeMpFacultad(actionEvent);

        resultEspecialidades = manager.getComboEspecialidades();

        assertEquals(5, resultFacultades.length);
        assertEquals(1, resultEspecialidades.size());
    }

    @Test
    public void saveNewEditDeletePlanCurricularTest() {
        manager.nuevo(actionEvent);

        manager.setPcCodigo("FAKE_CODE");
        manager.setPcDescripcion("FAKE_DESCRIPTION");
        manager.setPcCreacion(new Date());
        manager.setPcResolucion("FAKE_RESOLUTION");
        manager.setPcFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.setPcEspecialidadId(Especialidad.EDUCACION_PRIMARIA);

        // Insert new Curricular Plan
        manager.saveOrUpdate(actionEvent);

        assertTrue(manager.getOncomplete().contains("'mp'"));
        assertNotEquals(0, manager.getPcId());

        // Find recent insert Curricular Plan
        manager.setParamCodigo("FAKE");
        manager.setParamDescripcion("FAKE");
        manager.seleccionar();

        assertEquals(1, manager.getListaPlanCurricular().size());

        // Opening Modal to Edit
        UIParameter uiParameterId = mock(UIParameter.class);
        UIParameter uiParameterCode = mock(UIParameter.class);
        UIParameter uiParameterDescription = mock(UIParameter.class);
        UIParameter uiParameterFaculty = mock(UIParameter.class);
        UIParameter uiParameterSpecialty = mock(UIParameter.class);
        UIParameter uiParameterResolution = mock(UIParameter.class);
        UIParameter uiParameterCreation = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getPcId());
        when(uiComponent.findComponent("p_codigo")).thenReturn(uiParameterCode);
        when(uiParameterCode.getValue()).thenReturn("FAKE_EDIT_CODE");
        when(uiComponent.findComponent("p_descripcion")).thenReturn(uiParameterDescription);
        when(uiParameterDescription.getValue()).thenReturn("FAKE_EDIT_DESCRIPTION");
        when(uiComponent.findComponent("p_facultad")).thenReturn(uiParameterFaculty);
        when(uiParameterFaculty.getValue()).thenReturn(Facultad.CONTABILIDAD);
        when(uiComponent.findComponent("p_especialidad")).thenReturn(uiParameterSpecialty);
        when(uiParameterSpecialty.getValue()).thenReturn(Especialidad.CONTABILIDAD);
        when(uiComponent.findComponent("p_resolucion")).thenReturn(uiParameterResolution);
        when(uiParameterResolution.getValue()).thenReturn("FAKE_EDIT_RESOLUTION");
        when(uiComponent.findComponent("p_creacion")).thenReturn(uiParameterCreation);
        when(uiParameterCreation.getValue()).thenReturn(new Date());

        manager.editarFila(actionEvent);

        // Update the Curricular Plan
        manager.saveOrUpdate(actionEvent);

        assertTrue(manager.getOncomplete().contains("'mp'"));
        assertNotEquals(0, manager.getPcId());

        // Find recent insert Curricular Plan
        manager.setParamCodigo("FAKE_EDIT");
        manager.setParamDescripcion("FAKE_EDIT");
        manager.seleccionar();
        assertEquals(1, manager.getListaPlanCurricular().size());

        // Delete
        when(uiComponent.findComponent("delete")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getPcId());

        manager.eliminarFila(actionEvent);

        // Find recent deleted Plan Curricular
        manager.setParamCodigo("FAKE_EDIT");
        manager.setParamDescripcion("FAKE_EDIT");
        manager.seleccionar();
        assertEquals(0, manager.getListaPlanCurricular().size());
    }

    @Test
    public void failedGrabarActionTest() {
        manager.setPcCodigo("");
        manager.setPcDescripcion("");
        manager.setPcFacultadId(0);
        manager.setPcEspecialidadId(0);

        manager.grabar();

        assertTrue(manager.getOncomplete().contains("Ingrese los datos necesarios"));
    }

    @Test
    public void failedEditarActionTest() {
        manager.setPcCodigo("");
        manager.setPcDescripcion("");
        manager.setPcFacultadId(0);
        manager.setPcEspecialidadId(0);

        manager.editar();

        assertTrue(manager.getOncomplete().contains("Ingrese los datos necesarios"));
    }
}
