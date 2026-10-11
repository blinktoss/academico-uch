package net.uch.configuracion.jsf;

import net.uch.SpringConfigurationTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 15/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbFacultadTest extends SpringConfigurationTest {

    private MbFacultad manager;

    @Before
    public void setUp() {
        manager = new MbFacultad();
    }

    @Test
    public void seleccionarTest() {
        manager.setParamCodigo("");
        manager.setParamNombre("");

        manager.seleccionar(actionEvent);

        assertEquals(5, manager.getListaFacultad().size());

        // ------------------------------------------------------------------
        manager.setParamCodigo("001");
        manager.setParamNombre("");
        manager.seleccionar(actionEvent);
        assertEquals(1, manager.getListaFacultad().size());

        MbFacultad f = manager.getListaFacultad().get(0);
        assertNotEquals(0, f.getFacultadId());
        assertFalse(f.getRowCodigo().isEmpty());
        assertFalse(f.getRowNombre().isEmpty());
        assertFalse(f.getRowResolucion().isEmpty());
        assertNotNull(f.getRowCreacion());

        // ------------------------------------------------------------------
        manager.setParamCodigo("");
        manager.setParamNombre("INGENIERIA");
        manager.seleccionar(actionEvent);
        assertEquals(2, manager.getListaFacultad().size());
    }

    @Test
    public void openModalForNewFacultyTest() {
        manager.nuevo(actionEvent);
        assertTrue(manager.getRowCodigo().isEmpty());
        assertTrue(manager.getRowNombre().isEmpty());
        assertNull(manager.getRowCreacion());
        assertTrue(manager.getRowResolucion().isEmpty());

        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setRowCodigo("00" + RandomStringUtils.randomNumeric(8));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setRowNombre(RandomStringUtils.randomAlphanumeric(40));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setRowResolucion("RS-" + RandomStringUtils.randomAlphanumeric(17));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setRowCreacion(new Date());
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se creo una Facultad correctamente"));

        // ------------------------------------------------------------------
        editarFacultyTest();

        // ------------------------------------------------------------------
        eliminarFacultyTest();
    }

    private void editarFacultyTest() {
        manager.editarFila(actionEvent);

        assertEquals(manager.getRowCodigo(), manager.getCodigoAuxiliar());
        assertEquals(manager.getRowNombre(), manager.getNombreAuxiliar());
        assertEquals(manager.getRowResolucion(), manager.getResolucionAuxiliar());
        assertEquals(manager.getRowCreacion(), manager.getFechaAuxiliar());
        assertFalse(manager.isView());
        assertTrue(manager.isEditable());

        // ------------------------------------------------------------------
        manager.cancelar(actionEvent);

        assertEquals(manager.getCodigoAuxiliar(), manager.getRowCodigo());
        assertEquals(manager.getNombreAuxiliar(), manager.getRowNombre());
        assertEquals(manager.getResolucionAuxiliar(), manager.getRowResolucion());
        assertEquals(manager.getFechaAuxiliar(), manager.getRowCreacion());
        assertTrue(manager.isView());
        assertFalse(manager.isEditable());

        // ------------------------------------------------------------------
        manager.setRowCreacion(null);
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Datos."));

        manager.setRowCreacion(new Date());
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se Actualizaron los datos de la Facultad correctamente"));
    }

    private void eliminarFacultyTest() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("delete")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getFacultadId());

        manager.eliminarFila(actionEvent);
    }
}
