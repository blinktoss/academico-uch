package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.List;

import static net.uch.Stubs.SEMESTRE_2008_1_ID;
import static net.uch.Stubs.SEMESTRE_TRASLADOS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 20/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbTurnoTest extends SpringConfigurationTest {

    private MbTurno manager;

    @Before
    public void setUp() {
        manager = new MbTurno();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTurnosTest() {
        SelectItem[] mainComboSemestres = manager.getParamComboSemestres();
        SelectItem[] modalComboSemestres = manager.getMpComboSemestres();
        SelectItem[] modalComboMeridianos = manager.getMpComboMeridianos();

        assertNotEquals(0, mainComboSemestres.length);
        assertNotEquals(0, modalComboSemestres.length);
        assertEquals(2, modalComboMeridianos.length);
    }

    @Test
    public void seleccionarTest() {
        manager.setParamCodigo("");
        manager.setParamNombre("");
        manager.setParamSemestre(SEMESTRE_2008_1_ID);

        manager.seleccionar();

        assertEquals(6, manager.getTablaTurno().size());
        assertFalse(manager.getTablaTurno().get(0).getRowVigenteImg().isEmpty());
        assertFalse(manager.getTablaTurno().get(0).getRowSemestreDes().isEmpty());
    }

    @Test
    public void openModalForNewEditAndDeleteTest() {
        // Open modal
        manager.nuevo();

        assertEquals(0, manager.getMpId());
        assertTrue(manager.getMpNombre().isEmpty());
        assertTrue(manager.getMpCodigo().isEmpty());
        assertEquals(6, manager.getMpPeriodo());
        assertEquals(0, manager.getMpSemestre());
        assertFalse(manager.isMpVigente());
        assertEquals(0, manager.getMpTiempo());

        // Saving new turn
        manager.setMpCodigo("FAKE_" + RandomStringUtils.randomAlphanumeric(5));
        manager.setMpNombre("FAKE_" + RandomStringUtils.randomAlphanumeric(40));
        manager.setMpPeriodo(8);
        manager.setMpInicioHor("18");
        manager.setMpInicioMin("30");
        manager.setMpInicioMeridiano("PM");
        manager.setMpVigente(true);
        manager.setMpSemestre(SEMESTRE_TRASLADOS);
        manager.setMpTiempo(30);

        manager.insertar(actionEvent);

        assertNotEquals(0, manager.getMpId());

        List<MbTurno> turnoDetalle = manager.getTablaTurnoDetalle();
        assertEquals(manager.getMpPeriodo(), turnoDetalle.size());
        assertNotEquals(0, turnoDetalle.get(0).getMpDetId());
        assertNotNull(turnoDetalle.get(0).getMpDetInicio());
        assertNotNull(turnoDetalle.get(0).getMpDetFin());

        // Opening modal for editing
        manager.setRowId(manager.getMpId());
        manager.setRowCodigo(manager.getMpCodigo());
        manager.setRowNombre(manager.getMpNombre() + "_EDIT");
        manager.setRowPeriodo(4);
        manager.setRowInicio("10:00"); // Click on the row
        manager.setRowVigenteValue("0");
        manager.setRowSemestreValue(SEMESTRE_TRASLADOS);
        manager.setRowTiempo(manager.getMpTiempo());

        UIParameter uipIdS = mock(UIParameter.class);
        UIParameter uipCodigo_s = mock(UIParameter.class);
        UIParameter uipNombre_s = mock(UIParameter.class);
        UIParameter uipPeriodos_s = mock(UIParameter.class);
        UIParameter uipInicio_s = mock(UIParameter.class);
        UIParameter uipVigente_s = mock(UIParameter.class);
        UIParameter uipSemestre_s = mock(UIParameter.class);
        UIParameter uipTiempo_s = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("id_s")).thenReturn(uipIdS);
        when(uipIdS.getValue()).thenReturn(manager.getRowId());
        when(uiComponent.findComponent("codigo_s")).thenReturn(uipCodigo_s);
        when(uipCodigo_s.getValue()).thenReturn(manager.getRowCodigo());
        when(uiComponent.findComponent("nombre_s")).thenReturn(uipNombre_s);
        when(uipNombre_s.getValue()).thenReturn(manager.getRowNombre());
        when(uiComponent.findComponent("periodos_s")).thenReturn(uipPeriodos_s);
        when(uipPeriodos_s.getValue()).thenReturn(manager.getRowPeriodo());
        when(uiComponent.findComponent("inicio_s")).thenReturn(uipInicio_s);
        when(uipInicio_s.getValue()).thenReturn(manager.getRowInicio());
        when(uiComponent.findComponent("vigente_s")).thenReturn(uipVigente_s);
        when(uipVigente_s.getValue()).thenReturn(manager.getRowVigenteValue());
        when(uiComponent.findComponent("semestre_s")).thenReturn(uipSemestre_s);
        when(uipSemestre_s.getValue()).thenReturn(manager.getRowSemestreValue());
        when(uiComponent.findComponent("tiempo_s")).thenReturn(uipTiempo_s);
        when(uipTiempo_s.getValue()).thenReturn(manager.getRowTiempo());

        manager.editar(actionEvent);

        assertEquals("10", manager.getMpInicioHor());
        assertEquals("00", manager.getMpInicioMin());
        assertFalse(manager.isMpVigente());

        // Updating
        manager.setMpInicioMeridiano("AM");

        manager.insertar(actionEvent);

        assertNotEquals(0, manager.getMpId());
        turnoDetalle = manager.getTablaTurnoDetalle();
        assertEquals(manager.getMpPeriodo(), turnoDetalle.size());

        // Deleting turn
        manager.setRowId(manager.getMpId());

        manager.eliminar(actionEvent);

        manager.setParamCodigo("FAKE");
        manager.setParamNombre("FAKE");
        manager.setParamSemestre(5);

        manager.seleccionar();

        assertTrue(manager.getTablaTurno().isEmpty());
    }
}
