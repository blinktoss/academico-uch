package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 02/09/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbHorarioAreaTest extends SpringConfigurationTest {

    private MbHorarioArea manager;

    @Before
    public void setUp() {
        manager = new MbHorarioArea();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] areas = manager.getComboAreas();

        assertEquals(13, areas.length);
    }

    @Test
    public void seleccionarTest() {
        manager.seleccionar(actionEvent);
        assertEquals(0, manager.getParamAreaId());
        assertTrue(manager.getOncomplete().contains("alert"));

        // ------------------------------------------------------------------
        int areaId = Integer.parseInt(manager.getComboAreas()[0].getValue().toString());
        manager.setParamAreaId(areaId);

        manager.seleccionar(actionEvent);

        assertFalse(manager.getHorariosAreaList().isEmpty());
        MbHorarioArea row = manager.getHorariosAreaList().get(0);

        assertNotEquals(0, row.getRowId());
        assertNotEquals(0, row.getRowOrder());
        assertFalse(row.getRowAreaCode().isEmpty());
        assertFalse(row.getRowDayCode().isEmpty());
        assertFalse(row.getRowDayLabel().isEmpty());
        assertNotNull(row.getRowHour());
    }
}
