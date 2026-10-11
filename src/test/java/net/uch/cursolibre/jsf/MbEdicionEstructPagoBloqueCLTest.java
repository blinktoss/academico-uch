package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 22/01/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbEdicionEstructPagoBloqueCLTest extends SpringConfigurationTest {

    private MbEdicionEstructPagoBloqueCL manager;

    @Before
    public void setUp() {
        manager = new MbEdicionEstructPagoBloqueCL();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] talapes = manager.getParamTalapes();
        List<SelectItem> secciones = manager.getParamSecciones();
        List<SelectItem> filtros = manager.getParamFiltros();
        List<SelectItem> aModificar = manager.getParamToModify();

        assertEquals(337, talapes.length);
        assertTrue(secciones.isEmpty());
        assertEquals(3, filtros.size());
        assertEquals(3, aModificar.size());

        // On change
        manager.setParamTalapeId(1);
        manager.parametroSeccion(actionEvent);

        talapes = manager.getParamTalapes();
        secciones = manager.getParamSecciones();

        assertEquals(337, talapes.length);
        assertEquals(4, secciones.size());
    }

    @Test
    public void buscarTest() {
        manager.buscar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe seleccionar un taller aperturado"));

        // ------------------------------------------------------------------
        manager.setParamTalapeId(10); // MS WINDOWS VISTA - INTERNET
        manager.buscar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe seleccionar una seccion"));

        // ------------------------------------------------------------------
        manager.setParamSecId(58); // SECCIÓN DEMOSTRACIÓN
        manager.setParamFiltro(new String[0]);
        manager.buscar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe seleccionar algun campo para buscar"));

        // ------------------------------------------------------------------
        String[] selectedFilters = manager.getParamFiltros().stream().map(si -> si.getValue().toString()).toArray(String[]::new);
        manager.setParamFiltro(selectedFilters);
        manager.setParamModif(new String[0]);
        manager.buscar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Debe seleccionar algun campo para actualizar"));

        // ------------------------------------------------------------------
        String[] selectedToModify = manager.getParamToModify().stream().map(si -> si.getValue().toString()).toArray(String[]::new);

        manager.setParamModif(selectedToModify);

        manager.buscar(actionEvent);

        assertEquals(3, manager.getParamFiltro().length);
        assertEquals(3, manager.getParamModif().length);
        assertTrue(manager.getOncomplete().contains("Richfaces"));
        assertFalse(manager.getTallerAperturado().isEmpty());
        assertTrue(manager.getSeccion().isEmpty());
        assertNull(manager.getMpBusFechaPago());
        assertNull(manager.getMpBusFechaProrroga());
        assertTrue(manager.getMpBusMonto().isEmpty());
        assertNull(manager.getMpModFechaPago());
        assertNull(manager.getMpModFechaProrroga());
        assertTrue(manager.getMpModMonto().isEmpty());
        assertEquals(3, manager.getMpBusDisables().length);
        assertEquals(3, manager.getMpModDisable().length);
    }


    @Test
    public void updatingStructuresInblockTest() {
        Date df = Date.valueOf(LocalDate.of(2010, Month.MARCH, 1));
        Date dm = Date.valueOf(LocalDate.of(2024, Month.APRIL, 15));

        manager.setMpBusFechaPago(df);
        manager.setMpBusFechaProrroga(df);
        manager.setMpBusMonto("100");
        manager.setMpModFechaPago(dm);
        manager.setMpModFechaProrroga(dm);
        manager.setMpModMonto("999");

        manager.validarActualizacion(actionEvent);

        assertTrue(manager.getResultado().contains("No"));
    }
}
