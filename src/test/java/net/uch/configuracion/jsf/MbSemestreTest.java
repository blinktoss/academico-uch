package net.uch.configuracion.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.common.audit.CurrentUserContext;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.AcSemestre;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import java.sql.Date;
import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 14/01/2023.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbSemestreTest extends SpringConfigurationTest {

    private MbSemestre manager;

    @Autowired
    @Qualifier("springHibernateDaoSemestre")
    private HSSemestreDAO semestreDAO;

    @Before
    public void setUp() {
        manager = new MbSemestre();

        // Simulate what AuditUserFilter does on every request in production: expose the
        // logged-in user's id to AuditInterceptor via CurrentUserContext, so created_by /
        // updated_by get stamped. The base class stubs the session to return loggerUser.
        loggerUser.setRowUsuId(Stubs.USUARIO_ADMIN_ID);
        CurrentUserContext.set(loggerUser.getRowUsuId());
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void seleccionarTest() {
        manager.seleccionar();
        assertFalse(manager.getListaSemestres().isEmpty());

        MbSemestre row = manager.getListaSemestres().get(0);
        assertNotEquals(0, row.getRowId());
        assertFalse(row.getRowVigente().isEmpty());
        assertFalse(row.getRowActual().isEmpty());
    }

    @Test
    public void saveNewEditDeleteSemestreTest() {
        // New
        manager.setEdiVigente(true);
        manager.setEdiActual(false);

        manager.openModalForSemestre(actionEvent);
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setMpCodigo("SEM-FAKE");
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setMpNombre("Semestre " + RandomStringUtils.randomNumeric(4));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        LocalDate now = LocalDate.now();
        manager.setRowInicio(Date.valueOf(now));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        LocalDate trimester = now.plusWeeks(16);
        manager.setRowFin(Date.valueOf(trimester));
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setMpNroSemanas(16);
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setMpNroPensiones("1");
        manager.grabar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Solo puede existir un semestre actual"));

        // ------------------------------------------------------------------
        manager.setEdiVigente(false);
        manager.grabar(actionEvent);

        // Find recent insert semester
        manager.setParamCodigo("SEM-FAKE");
        manager.setParamNombre("");
        manager.seleccionar();
        assertEquals(1, manager.getListaSemestres().size());

        // On insert AuditInterceptor.onSave fills only created_*; updated_* stay null
        // until the row is first modified.
        AcSemestre created = semestreDAO.getSemestre(manager.getRowId());
        assertNotNull("created_at must be stamped on insert", created.getCreatedAt());
        assertNotNull("created_by must be stamped on insert", created.getCreatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, created.getCreatedBy().intValue());
        assertNull("updated_at must stay null until first update", created.getUpdatedAt());
        assertNull("updated_by must stay null until first update", created.getUpdatedBy());

        // Edit
        editSemestretest();

        // Find recent deleted semester
        manager.setParamCodigo("");
        manager.setParamNombre("FAKE");
        manager.seleccionar();
        assertTrue(manager.getListaSemestres().isEmpty());
    }

    private void editSemestretest() {
        UIParameter uiParameterVigente = mock(UIParameter.class);
        UIParameter uiParameterActual = mock(UIParameter.class);
        UIParameter uiParameterId = mock(UIParameter.class);

        UIParameter uiParameterCodigo = mock(UIParameter.class);
        UIParameter uiParameterNombre = mock(UIParameter.class);
        UIParameter uiParameterSemanas = mock(UIParameter.class);
        UIParameter uiParameterInicio = mock(UIParameter.class);
        UIParameter uiParameterFin = mock(UIParameter.class);
        UIParameter uiParameterPensiones = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("vigente")).thenReturn(uiParameterVigente);
        when(uiParameterVigente.getValue()).thenReturn("false");
        when(uiComponent.findComponent("actual")).thenReturn(uiParameterActual);
        when(uiParameterActual.getValue()).thenReturn("false");
        when(uiComponent.findComponent("id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(String.valueOf(manager.getRowId()));

        LocalDate newDate = LocalDate.of(2022, 1, 20);
        LocalDate nextDate = newDate.plusWeeks(20);

        when(uiComponent.findComponent("codigo")).thenReturn(uiParameterCodigo);
        when(uiParameterCodigo.getValue()).thenReturn("SEM-FAKE");
        when(uiComponent.findComponent("nombre")).thenReturn(uiParameterNombre);
        when(uiParameterNombre.getValue()).thenReturn("SEMESTRE FAKE UPDATED");
        when(uiComponent.findComponent("semanas")).thenReturn(uiParameterSemanas);
        when(uiParameterSemanas.getValue()).thenReturn("20");
        when(uiComponent.findComponent("inicio")).thenReturn(uiParameterInicio);
        when(uiParameterInicio.getValue()).thenReturn(Date.valueOf(newDate));
        when(uiComponent.findComponent("fin")).thenReturn(uiParameterFin);
        when(uiParameterFin.getValue()).thenReturn(Date.valueOf(nextDate));
        when(uiComponent.findComponent("pensiones")).thenReturn(uiParameterPensiones);
        when(uiParameterPensiones.getValue()).thenReturn("2");

        manager.editar(actionEvent);

        assertTrue(manager.isView());
        assertFalse(manager.isEditable());
        assertFalse(manager.isEditable2());
        assertFalse(manager.isVisible());
        assertTrue(manager.getOncomplete().contains("Actualizaron"));

        // created_* must survive the update (load-then-merge in actualizarSemestre),
        // while updated_* are now stamped for the first time by AuditInterceptor.onFlushDirty.
        AcSemestre edited = semestreDAO.getSemestre(manager.getRowId());
        assertNotNull("created_at must be preserved on update", edited.getCreatedAt());
        assertNotNull("created_by must be preserved on update", edited.getCreatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, edited.getCreatedBy().intValue());
        assertNotNull("updated_at must be stamped on first update", edited.getUpdatedAt());
        assertNotNull("updated_by must be stamped on first update", edited.getUpdatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, edited.getUpdatedBy().intValue());

        // Delete
        when(uiComponent.findComponent("delete")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getRowId());

        manager.eliminarFila(actionEvent);
    }

    @Test
    public void showRowForEditAndCancelTest() {
        // Show row for edition
        manager.editarFila(actionEvent);

        assertEquals(manager.getRowInicio(), manager.getInicioAux());
        assertEquals(manager.getRowFin(), manager.getFinAux());
        assertEquals(manager.getParamCodigo(), manager.getCodigoAux());
        assertEquals(manager.getParamNombre(), manager.getNombreAux());
        assertEquals(manager.getRowNroSemanas(), manager.getNroSemanaAux());
        assertEquals(manager.isEdiVigente(), manager.isVigenteAux());
        assertEquals(manager.isEdiActual(), manager.isActualAux());
        assertEquals(manager.getRowNroPensiones(), manager.getNroPensionesAux());
        assertFalse(manager.isView());
        assertTrue(manager.isEditable());
        assertTrue(manager.isEditable2());
        assertTrue(manager.isVisible());

        // Cancel edition
        manager.cancelar(actionEvent);

        assertEquals(manager.getInicioAux(), manager.getRowInicio());
        assertEquals(manager.getFinAux(), manager.getRowFin());
        assertEquals(manager.getCodigoAux(), manager.getParamCodigo());
        assertEquals(manager.getNombreAux(), manager.getParamNombre());
        assertEquals(manager.isVigenteAux(), manager.isEdiVigente());
        assertEquals(manager.getNroPensionesAux(), manager.getRowNroPensiones());
        assertEquals(manager.getNroSemanaAux(), manager.getRowNroSemanas());
        assertEquals(manager.isActualAux(), manager.isEdiActual());
        assertTrue(manager.isView());
        assertFalse(manager.isEditable());
        assertFalse(manager.isEditable2());
        assertFalse(manager.isVisible());
    }
}
