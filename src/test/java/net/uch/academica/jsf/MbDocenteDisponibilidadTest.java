package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.util.HSTestSupportDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 15/09/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbDocenteDisponibilidadTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private static boolean CONFIGURED = true;
    private MbDocenteDisponibilidad manager;

    @AfterClass
    public static void cleanUpDatabase() {
        DAO.cleanAcDisponibilidades();
    }

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbDocenteDisponibilidad();

        if (CONFIGURED) {
            DAO.insertAcDisponibilidades();
            CONFIGURED = false;
        }
    }

    @Test
    public void seleccionarTest() {
        manager.seleccionar(actionEvent);

        assertNull(manager.getParamCode());
        assertNull(manager.getParamName());
        assertTrue(manager.getOncomplete().contains("Ingrese valor de busqueda"));

        // ------------------------------------------------------------------
        manager.setParamCode("");
        manager.setParamName("mau");
        manager.seleccionar(actionEvent);

        assertEquals(3, manager.getAvailabilities().size());
        MbDocenteDisponibilidad row = manager.getAvailabilities().get(0);
        assertNotEquals(0, row.getRowTeacherId());
        assertFalse(row.getRowTeacherCode().isEmpty());
        assertFalse(row.getRowTeacherNames().isEmpty());
        assertNotEquals(0, row.getRowTeacherAvailability());

        // ------------------------------------------------------------------
        manager.setParamCode("08871949");
        manager.setParamName("");
        manager.seleccionar(actionEvent);

        assertEquals(1, manager.getAvailabilities().size());
    }

    @Test
    public void openModalForAvailabilitiesTest() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("teacher_id", 39));

        manager.openModalForAvailabilities(actionEvent);

        assertFalse(manager.getDetails().isEmpty());
        MbDocenteDisponibilidad det = manager.getDetails().get(0);
        assertFalse(det.getMpAvailableDay().isEmpty());
        assertNotNull(det.getMpAvailableHour());
        assertFalse(det.getMpAvailableObservation().isEmpty());
    }
}
