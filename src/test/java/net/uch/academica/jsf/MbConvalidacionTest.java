package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.academica.jsf.beans.BeanConvalidacion;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Map;

import static net.uch.Stubs.Especialidad;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 06/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbConvalidacionTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private MbConvalidacion manager;

    @AfterClass
    public static void cleanUpDatabase() {
        DAO.cleanAcMatriculaCursoConv();
        DAO.cleanAcActaConvalidacionByAlumno(3015); // capturarAlumnoAndUpdateGradesTest
    }

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        when(externalContext.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(loggerUser);

        manager = new MbConvalidacion();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void buscarCursosTest() {
        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("");
        manager.setParamAluApMaterno("");
        manager.setParamAluNombre("");

        manager.buscarCursos(actionEvent);

        assertTrue(manager.getOncomplete().contains("Ingrese algun parametro para la busqueda"));

        // ------------------------------------------------------------------
        manager.setParamAluCodigo("09105155");
        manager.setParamAluApPaterno("");
        manager.setParamAluApMaterno("");
        manager.setParamAluNombre("");

        manager.buscarCursos(actionEvent);

        assertEquals(1, manager.getAlumnos().size());

        // ------------------------------------------------------------------
        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("DIAZ");
        manager.setParamAluApMaterno("");
        manager.setParamAluNombre("");

        manager.buscarCursos(actionEvent);

        assertEquals(17, manager.getAlumnos().size());

        // ------------------------------------------------------------------
        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("");
        manager.setParamAluApMaterno("MONTOYA");
        manager.setParamAluNombre("");

        manager.buscarCursos(actionEvent);

        assertEquals(6, manager.getAlumnos().size());

        // ------------------------------------------------------------------
        manager.setParamAluCodigo("");
        manager.setParamAluApPaterno("");
        manager.setParamAluApMaterno("");
        manager.setParamAluNombre("CYNTHIA");

        manager.buscarCursos(actionEvent);

        assertEquals(6, manager.getAlumnos().size());
        assertFalse(manager.getAlumnos().get(0).getParamAluCodigo().isEmpty());
        assertFalse(manager.getAlumnos().get(0).getParamAluApPaterno().isEmpty());
        assertFalse(manager.getAlumnos().get(0).getParamAluApMaterno().isEmpty());
    }

    @Test
    public void capturarAlumnoTest() {
        manager.setAlumnoId(4538);
        manager.setParamAluCodigo("10201075");
        manager.setEspecialidadId(Especialidad.ING_SISTEMAS);

        Map<String, Object> attributes = Collections.singletonMap("p_alu", manager);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.capturarAlumno(actionEvent);

        assertFalse(manager.getAluCodigoNombre().isEmpty());
        assertEquals(39, manager.getConvalidaciones().size());

        BeanConvalidacion bc = manager.getConvalidaciones().get(0);

        bc.cambiarEstado(actionEvent);
        assertFalse(bc.isActivar());
        assertTrue(bc.isDisable());
        assertFalse(bc.getStyle().isEmpty());

        bc.setActivar(true);
        bc.cambiarEstado(actionEvent);
        assertTrue(bc.isActivar());
        assertFalse(bc.isDisable());
        assertTrue(bc.getStyle().isEmpty());
    }

    @Test
    public void capturarAlumnoAndUpdateGradesTest() {
        manager.setAlumnoId(3015);
        manager.setParamAluCodigo("09102023");
        manager.setParamAluApPaterno("ANDRADE");
        manager.setParamAluApMaterno("TENORIO");
        manager.setParamAluNombre("EDITH");
        manager.setEspecialidadId(Especialidad.ENFERMERIA);
        manager.setEspecialidadNombre("NOMBRE DE LA ESPECIALIDAD");
        manager.setSemestreIngreso("NOMBRE DEL SEMESTRE");

        Map<String, Object> attributes = Collections.singletonMap("p_alu", manager);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.capturarAlumno(actionEvent);

        assertEquals(28, manager.getConvalidaciones().size());

        saveValidationTest();
    }

    private void saveValidationTest() {
        manager.getConvalidaciones().forEach(c -> {
            c.setActivar(true);
            c.setRegNota("");
        });
        manager.guardarConvalidacion(actionEvent);
        assertTrue(manager.getOncomplete().contains("Error en la notas, revise por favor"));

        // ------------------------------------------------------------------
        manager.getConvalidaciones().get(0).setRegNota("10");
        manager.guardarConvalidacion(actionEvent);
        assertTrue(manager.getOncomplete().contains("Error en la notas, revise por favor"));

        // ------------------------------------------------------------------
        manager.getConvalidaciones().get(0).setRegNota("21");
        manager.guardarConvalidacion(actionEvent);
        assertTrue(manager.getOncomplete().contains("Error en la notas, revise por favor"));

        // ------------------------------------------------------------------
        manager.getConvalidaciones().forEach(c -> c.setRegNota(String.valueOf(Stubs.randomNumeric(11, 21))));
        manager.guardarConvalidacion(actionEvent);
        assertTrue(manager.getOncomplete().contains("Convalidacion terminada"));
    }

}
