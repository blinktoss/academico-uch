package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Map;

import static net.uch.common.Constants.PARAM_ALUMNO_ID;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 25/12/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbAlumnoCLTest extends SpringConfigurationTest {

    private MbAlumnoCL manager;

    @Before
    public void setUp() {
        manager = new MbAlumnoCL();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void initializationOnConstructorTest() {
        assertNotNull(manager.getCboDepartamento());
        assertNotNull(manager.getCboUnidad());
        assertNotNull(manager.getCboFormaPago());
        assertNotNull(manager.getCboParentesco());
        assertNotNull(manager.getCboProcedencia());
        assertNotNull(manager.getCboTipo());

        assertEquals(26, manager.getCboDepartamento().length);
        assertEquals(10, manager.getCboUnidad().length);
        assertEquals(2, manager.getCboFormaPago().length);
        assertEquals(12, manager.getCboParentesco().length);
        assertEquals(14, manager.getCboProcedencia().length);
        assertEquals(4, manager.getCboTipo().length);

        assertEquals(0, manager.getFindImage());
        assertNull(manager.getEnlaceAlumno());
    }

    @Test
    public void seleccionarTest() {
        manager.setParamCodigo("");
        manager.setParamPaterno("");
        manager.setParamMaterno("");
        manager.setParamNombre("");

        manager.seleccionar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese algun parametro para buscar"));

        // ------------------------------------------------------------------
        manager.setParamCodigo("10104");

        manager.seleccionar(actionEvent);
        assertEquals(2, manager.getListaAlumno().size());

        // ------------------------------------------------------------------
        manager.setParamCodigo("");
        manager.setParamPaterno("AGUILAR");

        manager.seleccionar(actionEvent);
        assertEquals(5, manager.getListaAlumno().size());

        // ------------------------------------------------------------------
        manager.setParamPaterno("");
        manager.setParamMaterno("SOTO");

        manager.seleccionar(actionEvent);
        assertEquals(2, manager.getListaAlumno().size());

        // ------------------------------------------------------------------
        manager.setParamMaterno("");
        manager.setParamNombre("CYNTHIA");

        manager.seleccionar(actionEvent);
        assertEquals(4, manager.getListaAlumno().size());
    }

    @Test
    public void nuevoStudentCLFromUnivTest() {
        manager.nuevoUniv(actionEvent);
        assertEquals(1, manager.getFindImage());

        manager.setUnivCodigo("00000000");
        manager.buscarUniversidad(actionEvent);
        assertNull(manager.getUnivAlumno().getId());
        assertTrue(manager.getOncomplete().contains("showErrorDiv();hideOkDiv();"));

        // ------------------------------------------------------------------
        manager.setUnivCodigo("08010016");
        manager.buscarUniversidad(actionEvent);
        assertNotEquals(0, manager.getUnivAlumno().getId().intValue());
        assertTrue(manager.getOncomplete().contains("showOkDiv();hideErrorDiv();"));

        assertNotNull(manager.getUnivAlumno().getId());
        assertFalse(manager.getUnivAlumno().getAluCodigo().isEmpty());
        assertFalse(manager.getUnivAlumno().getAluAppaterno().isEmpty());
        assertFalse(manager.getUnivAlumno().getAluApmaterno().isEmpty());
        assertFalse(manager.getUnivAlumno().getAluNombres().isEmpty());
        assertFalse(manager.getUnivAlumno().getEsp().getEspNombre().isEmpty());
    }

    @Test
    public void openModalForNewStudentCLTest() {
        manager.nuevo(actionEvent);
        assertEquals(0, manager.getFindImage());
        assertFalse(manager.getAluCodigo().isEmpty());

        saveNewStudentTest();

        Map<String, Object> attributes = Collections.singletonMap(PARAM_ALUMNO_ID, manager.getAluId());
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.editarAlumno(actionEvent);
        assertEquals(2, manager.getFindImage());

        editNewStudentTest();
    }

    private void saveNewStudentTest() {
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar los campos obligatorios"));

        // ------------------------------------------------------------------
        manager.setAluAppaterno("FAKE_" + RandomStringUtils.randomAlphanumeric(15));
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar los campos obligatorios"));

        // ------------------------------------------------------------------
        manager.setAluApmaterno("FAKE_" + RandomStringUtils.randomAlphanumeric(15));
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar los campos obligatorios"));

        // ------------------------------------------------------------------
        manager.setAluNombres("FAKE_" + RandomStringUtils.randomAlphanumeric(5));
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar los campos obligatorios"));

        // ------------------------------------------------------------------
        manager.setAluMail(RandomStringUtils.randomAlphanumeric(10) + "@fake.xyz");
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar los campos obligatorios"));

        // ------------------------------------------------------------------
        manager.setAluTelefono(RandomStringUtils.randomNumeric(7));
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar los campos obligatorios"));

        // ------------------------------------------------------------------
        manager.setAluCelular(RandomStringUtils.randomNumeric(9));
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe ingresar los campos obligatorios"));

        // ------------------------------------------------------------------
        manager.setAluDireccion(RandomStringUtils.randomAlphanumeric(50));
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se creo una Alumno correctamente"));
    }

    private void editNewStudentTest() {
        manager.setAluFormaPago("041002"); // VALE
        manager.setAluFechaNacimiento(Date.valueOf(LocalDate.of(1999, Month.MARCH, 1)));
        manager.setAluDni(RandomStringUtils.randomNumeric(8));
        manager.setAluTipo("057004"); // ADMINISTRATIVO
        manager.setDepartamento("150000");
        manager.setProvincia("150100");
        manager.setDistrito("150101");

        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se modifico Alumno correctamente"));
    }

    @Test
    public void enlaceUniversidadTest() {
        manager.setAluId(147);
        manager.setAluAppaterno("AGUILAR");
        manager.setAluApmaterno("GALVEZ");
        manager.setAluNombres("GIANFRANCO ANDRES");

        Map<String, Object> attributes = Collections.singletonMap("p_alu", manager);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.enlaceUniversidad(actionEvent);
        assertEquals(1, manager.getFindImage());
        assertEquals(1, manager.getListaEnlace().size());
    }
}
