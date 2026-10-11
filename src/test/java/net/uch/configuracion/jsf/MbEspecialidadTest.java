package net.uch.configuracion.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static net.uch.Stubs.Facultad;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 15/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbEspecialidadTest extends SpringConfigurationTest {

    private MbEspecialidad manager;

    @Before
    public void setUp() {
        manager = new MbEspecialidad();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageEspecialidadTest() {
        // Combo Facultades
        List<SelectItem> facultades = manager.getComboFacultades();

        assertEquals(5, facultades.size());

        // Combo Tipo
        SelectItem[] tipoPeriodos = manager.getComboTipos();

        assertEquals(2, tipoPeriodos.length);
    }

    @Test
    public void seleccionarTest() {
        manager.setParamCodigo("");
        manager.setParamNombre("");
        manager.setParamFacultadId(0);

        String result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertEquals(5, manager.getListaEspecialidad().size());

        // Other
        manager.setParamCodigo("");
        manager.setParamNombre("");
        manager.setParamFacultadId(Facultad.CONTABILIDAD);

        result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertEquals(1, manager.getListaEspecialidad().size());

        // Other
        manager.setParamCodigo("002");
        manager.setParamNombre("");
        manager.setParamFacultadId(0);

        result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertEquals(1, manager.getListaEspecialidad().size());

        // Other
        manager.setParamCodigo("");
        manager.setParamNombre("INGENIERIA");
        manager.setParamFacultadId(0);

        result = manager.seleccionar();

        assertTrue(result.isEmpty());
        assertEquals(2, manager.getListaEspecialidad().size());
    }

    @Test
    public void openModalForNewSpecialityTest() {
        manager.newSpeciality(actionEvent);

        assertTrue(manager.getMpCodigo().isEmpty());
        assertTrue(manager.getMpNombre().isEmpty());
        assertNull(manager.getRowFechacreacion());
        assertTrue(manager.getRowResolrectoral().isEmpty());
        assertEquals("005001", manager.getRowTipoPeriodoId());
        assertEquals(0, manager.getRowNroPeriodos());
        assertEquals(0, manager.getRowFacultadId());

        // Without filled required fields
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // With code
        manager.setMpCodigo("FAKE_CODE1");
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // With name
        manager.setMpNombre("FAKE_NAME_SPECIALITY_1");
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // With creation date
        manager.setRowFechacreacion(Date.valueOf(LocalDate.now().plusDays(2)));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // With rectoral resolution
        manager.setRowResolrectoral("FAKE_RESOLUC_RECTOR");
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // With periods number
        manager.setRowNroPeriodos(12);
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        manager.setRowTipoPeriodoId("005002");

        // With faculty
        manager.setRowFacultadId(Facultad.EDUCACION_PRIMARIA);
        manager.grabar(actionEvent);

        assertTrue(manager.getOncomplete().contains("Se creo una Especialidad correctamente"));

        // Update
        manager.editarFila(actionEvent);

        assertEquals(manager.getRowFechacreacion(), manager.getAuxFechacreacion());
        assertEquals(manager.getParamCodigo(), manager.getAuxCodigo());
        assertEquals(manager.getParamNombre(), manager.getAuxNombre());
        assertEquals(manager.getRowResolrectoral(), manager.getAuxResolrectoral());
        assertEquals(manager.getRowFacultadId(), manager.getAuxFacultadId());
        assertEquals(manager.getRowFacultadValue(), manager.getAuxFacultadValue());
        assertEquals(manager.getRowNroPeriodos(), manager.getAuxNroPeriodos());
        assertEquals(manager.getRowTipoPeriodoId(), manager.getAuxTipoPeriodosId());
        assertFalse(manager.isView());
        assertTrue(manager.isEditable());
        assertTrue(manager.isEditable2());
        assertTrue(manager.isVisible());

        manager.cancelar(actionEvent);

        assertEquals(manager.getAuxFechacreacion(), manager.getRowFechacreacion());
        assertEquals(manager.getAuxCodigo(), manager.getParamCodigo());
        assertEquals(manager.getAuxNombre(), manager.getParamNombre());
        assertEquals(manager.getAuxResolrectoral(), manager.getRowResolrectoral());
        assertEquals(manager.getAuxFacultadId(), manager.getRowFacultadId());
        assertEquals(manager.getAuxFacultadValue(), manager.getRowFacultadValue());
        assertEquals(manager.getAuxNroPeriodos(), manager.getRowNroPeriodos());
        assertEquals(manager.getAuxTipoPeriodosId(), manager.getRowTipoPeriodoId());
        assertTrue(manager.isView());
        assertFalse(manager.isEditable());
        assertFalse(manager.isEditable2());
        assertFalse(manager.isVisible());

        // Preparing for edition
        UIParameter uipId = mock(UIParameter.class);
        UIParameter uipCodigo = mock(UIParameter.class);
        UIParameter uipNombre = mock(UIParameter.class);
        UIParameter uipResolucion = mock(UIParameter.class);
        UIParameter uipFacultad = mock(UIParameter.class);
        UIParameter uipNumero = mock(UIParameter.class);
        UIParameter uipTipo = mock(UIParameter.class);
        UIParameter uipCreacion = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);

        when(uiComponent.findComponent("id")).thenReturn(uipId);
        when(uipId.getValue()).thenReturn(manager.getRowId());

        when(uiComponent.findComponent("codigo")).thenReturn(uipCodigo);
        when(uipCodigo.getValue()).thenReturn("");

        when(uiComponent.findComponent("nombre")).thenReturn(uipNombre);
        when(uipNombre.getValue()).thenReturn("");

        when(uiComponent.findComponent("resolucion")).thenReturn(uipResolucion);
        when(uipResolucion.getValue()).thenReturn("");

        when(uiComponent.findComponent("facultad")).thenReturn(uipFacultad);
        when(uipFacultad.getValue()).thenReturn(0);

        when(uiComponent.findComponent("numero_periodos")).thenReturn(uipNumero);
        when(uipNumero.getValue()).thenReturn("");

        when(uiComponent.findComponent("tipo_periodos")).thenReturn(uipTipo);
        when(uipTipo.getValue()).thenReturn("");

        when(uiComponent.findComponent("creacion")).thenReturn(uipCreacion);
        when(uipCreacion.getValue()).thenReturn(null);

        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        when(uipCodigo.getValue()).thenReturn("FAKE_CODE2");
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        when(uipNombre.getValue()).thenReturn("FAKE_EDIT_NAME_SPECIALITY_1");
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        when(uipResolucion.getValue()).thenReturn("FAKE_EDIT_RESOL_RECT");
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        when(uipFacultad.getValue()).thenReturn(Facultad.ING_ELECTRONICA);
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        when(uipNumero.getValue()).thenReturn("16");
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        when(uipTipo.getValue()).thenReturn("005001");
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos"));

        when(uipCreacion.getValue()).thenReturn(Date.valueOf(LocalDate.now().plusDays(4)));
        manager.editar(actionEvent);

        assertTrue(manager.isView());
        assertFalse(manager.isEditable());
        assertFalse(manager.isEditable2());
        assertFalse(manager.isVisible());
        assertFalse(manager.getRowFacultadValue().isEmpty());
        assertFalse(manager.getRowTipoPeriodoDesc().isEmpty());
        assertTrue(manager.getOncomplete().contains("Se actualizaron los datos"));

        // Delete
        when(uiComponent.findComponent("delete")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getRowId());

        manager.eliminarFila(actionEvent);
    }
}
