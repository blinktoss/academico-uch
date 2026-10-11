package net.uch.administrativa.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.academica.jsf.beans.ObjUpdate;
import net.uch.administrativa.dao.HSEdicionEstructPagoDAO;
import net.uch.common.ServiceFinder;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static net.uch.Stubs.Especialidad;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 18/01/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbEdicionEstructPagoBloqueTest extends SpringConfigurationTest {

    private MbEdicionEstructPagoBloque manager;

    @Autowired
    private HSTestSupportDao support;

    @Before
    public void setUp() {
        manager = new MbEdicionEstructPagoBloque();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        SelectItem[] semestres = manager.getParamSemIngreso();
        SelectItem[] especialidades = manager.getParamEspecialidades();
        List<SelectItem> filtros = manager.getParamFiltros();
        List<SelectItem> aModificar = manager.getParamToModify();

        assertNotEquals(0, semestres.length);
        assertEquals(5, especialidades.length);
        assertEquals(3, filtros.size());
        assertEquals(3, aModificar.size());
        assertEquals(0, manager.getParamSemIngId());
    }

    @Test
    public void buscarTest() {
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
        assertTrue(manager.getOncomplete().contains("Debe seleccionar una especialidad"));

        // ------------------------------------------------------------------
        manager.setParamEsp(Especialidad.ING_SISTEMAS);
        manager.buscar(actionEvent);

        assertEquals(3, manager.getParamFiltro().length);
        assertEquals(3, manager.getParamModif().length);
        assertTrue(manager.getOncomplete().contains("Richfaces"));
        assertFalse(manager.getEspecialidad().isEmpty());
        assertFalse(manager.getParamSemDestino().isEmpty());
        assertTrue(manager.getSemestreIngreso().isEmpty());
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
        Date dm = Date.valueOf(LocalDate.now().plusYears(1)); // future: clears the F2 commitment-date rule

        manager.setMpBusFechaPago(df);
        manager.setMpBusFechaProrroga(df);
        manager.setMpBusMonto("100");
        manager.setMpModFechaPago(dm);
        manager.setMpModFechaProrroga(dm);
        manager.setMpModMonto("999");

        manager.validarActualizacion(actionEvent);

        assertTrue(manager.getResultado().contains("No"));
    }

    @Test
    public void massiveProrrogaUpdateIsAtomicTest() {
        HSEdicionEstructPagoDAO dao = ServiceFinder.findBean("springHibernateDaoEdicionEstructura", HSEdicionEstructPagoDAO.class);
        // sem 9 (current 2010-II) + esp 1 (ING SISTEMAS) + monto 560 matches exactly one active tariff.
        Integer alutarId = support.findAlutarIdByMontoSemEsp(9, Especialidad.ING_SISTEMAS, 560f);
        assertNotNull(alutarId);

        java.util.Date deadlineBefore = support.getAlumnoTarifa(alutarId).getAlutarFechaProrroga();
        int proIdBefore = support.maxProId(alutarId);
        String comentario = "From massive upload on TEST-33-F1";
        Date nueva = Date.valueOf(LocalDate.now().plusDays(15));

        List<ObjUpdate> filtro = Collections.singletonList(new ObjUpdate("ad_alumno_tarifa.alutar_monto", 560));
        List<ObjUpdate> valores = Collections.singletonList(
                new ObjUpdate("ad_alumno_tarifa.alutar_fecha_prorroga", "'" + nueva + "'"));

        try {
            int affected = dao.actualizarEstructurasPagoBloqueConHistorial(
                    9, Especialidad.ING_SISTEMAS, filtro, valores, nueva, comentario, 17);

            // both writes landed together in one transaction: the bulk column update AND the history row
            assertEquals(1, affected);
            assertEquals(nueva.toString(),
                    support.getAlumnoTarifa(alutarId).getAlutarFechaProrroga().toString().substring(0, 10));
            assertTrue("a history row was appended", support.maxProId(alutarId) > proIdBefore);
        } finally {
            support.deleteProrrogasByComentario(comentario);
            support.updateAlutarFechaProrroga(alutarId, deadlineBefore);
        }
    }

    @Test
    public void bulkRejectsPastProrrogaDateTest() {
        // F2: the commitment-date rule must be the same for the bulk path as for the single grant -
        // a past/today date is rejected before any row is touched (no history, no column update).
        manager.setParamEsp(Especialidad.ING_SISTEMAS);
        manager.setMpModFechaProrroga(Date.valueOf(LocalDate.now().minusDays(1)));

        manager.actualizarEstructuras();

        assertTrue(manager.getOncomplete().contains("posterior a hoy"));
        assertFalse(manager.getOncomplete().contains("hideModalPanel")); // modal stays open to correct
        assertNull(manager.getResultado());                              // nothing was updated
    }
}
