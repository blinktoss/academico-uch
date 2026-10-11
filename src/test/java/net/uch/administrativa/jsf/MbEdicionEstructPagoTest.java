package net.uch.administrativa.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.common.ServiceFinder;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static net.uch.Stubs.*;
import static net.uch.common.Constants.PARAM_ALUTAR_ID;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 18/01/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbEdicionEstructPagoTest extends SpringConfigurationTest {

    private MbEdicionEstructPago manager;

    @Autowired
    private HSTestSupportDao support;

    @Before
    public void setUp() {
        loggerUser.setRowUsuId(17);
        when(session.getAttribute("usuario")).thenReturn(loggerUser);

        manager = new MbEdicionEstructPago();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void validarBusquedaTest() {
        // Validations
        manager.setParametroCodigo("");
        manager.setParametroPaterno("");
        manager.setParametroMaterno("");
        manager.setParametroNombre("");
        manager.setParametroSemestre(SEMESTRE_2010_2_ID);

        manager.validarBusqueda(actionEvent);

        assertNotEquals(0, manager.getParametroSemestres().length);
        assertTrue(manager.getOncomplete().contains("Ingrese algun parametro para la busqueda"));

        // Code length validation
        manager.setParametroCodigo("080402");
        manager.setParametroPaterno("");
        manager.setParametroMaterno("");
        manager.setParametroNombre("");
        manager.setParametroSemestre(SEMESTRE_2010_2_ID);

        manager.validarBusqueda(actionEvent);
        assertTrue(manager.getOncomplete().contains("Codigo muy corto para realizar busqueda"));

        // Searching complete
        manager.setParametroCodigo("08010005");
        manager.setParametroPaterno("");
        manager.setParametroMaterno("");
        manager.setParametroNombre("");
        manager.setParametroSemestre(SEMESTRE_2010_2_ID);

        manager.validarBusqueda(actionEvent);
        assertEquals(1, manager.getListaEstructura().size());

        MbEdicionEstructPago r = manager.getListaEstructura().get(0);
        assertNotEquals(0, r.getAlumnoId());
        assertFalse(r.getParametroPaterno().isEmpty());
        assertFalse(r.getParametroMaterno().isEmpty());
        assertFalse(r.getParametroNombre().isEmpty());
        assertFalse(r.getEspecialidad().isEmpty());
        assertFalse(r.getSemestreIngreso().isEmpty());
        assertFalse(r.getSemestreEstructura().isEmpty());

        MbEdicionEstructPago s = r.getSubListaEstructura().get(0);
        assertNotEquals(0, s.getSubAlutarId());
        assertNotEquals(0, s.getSubAlutarEstado());
        assertNotNull(s.getSubFechaPago());
        assertNotNull(s.getSubFechaProrroga());
        assertEquals(0, s.getSubMonto(), 0);
        assertFalse(s.getSubConcepto().isEmpty());
        assertFalse(s.getSubImgCandado().isEmpty());
        assertFalse(s.getSubTitle().isEmpty());
        assertFalse(s.getSubMensaje().isEmpty());
        assertTrue(s.isSubEditDisabled());
        assertFalse(s.getSubImgKlipper().isEmpty());
        assertFalse(s.getSubImgCalendar().isEmpty());

        // ------------------------------------------------------------------
        manager.setParametroCodigo("");
        manager.setParametroPaterno("AGUILAR");
        manager.setParametroMaterno("AYRAS");
        manager.setParametroNombre("LUZ");
        manager.setParametroSemestre(SEMESTRE_2010_2_ID);

        manager.validarBusqueda(actionEvent);
        assertEquals(2, manager.getListaEstructura().size());

        // ------------------------------------------------------------------
        manager.setParametroCodigo("");
        manager.setParametroPaterno("");
        manager.setParametroMaterno("AYRAS");
        manager.setParametroNombre("");
        manager.setParametroSemestre(SEMESTRE_2010_2_ID);

        manager.validarBusqueda(actionEvent);
        assertEquals(2, manager.getListaEstructura().size());

        // ------------------------------------------------------------------
        manager.setParametroCodigo("");
        manager.setParametroPaterno("");
        manager.setParametroMaterno("");
        manager.setParametroNombre("LUZ");
        manager.setParametroSemestre(SEMESTRE_2010_2_ID);

        manager.validarBusqueda(actionEvent);
        assertEquals(30, manager.getListaEstructura().size());
    }

    @Test
    public void openAndCloseSubTable() {
        UIParameter uipFlag = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_ver")).thenReturn(uipFlag);

        manager.setEstadoVer("0");

        when(uipFlag.getValue()).thenReturn(manager.getEstadoVer());
        manager.mostrarDetalle(actionEvent);

        assertTrue(manager.isMostrar());
        assertEquals("1", manager.getEstadoVer());
        assertTrue(manager.getImagenVer().contains("up.png"));

        // Closing
        manager.setEstadoVer("1");

        when(uipFlag.getValue()).thenReturn(manager.getEstadoVer());
        manager.mostrarDetalle(actionEvent);

        assertFalse(manager.isMostrar());
        assertEquals("0", manager.getEstadoVer());
        assertTrue(manager.getImagenVer().contains("down.png"));
    }

    @Test
    public void openModalForNewEstructuraPagoAlumnoTest() {
        // Opening modal
        manager.nuevo(actionEvent);

        assertEquals(0, manager.getModalEspecialidad());
        assertEquals(0, manager.getModalSemestre());
        assertEquals(0, manager.getModalSemIngreso());
        assertEquals(0, manager.getModalPlantilla());
        assertTrue(manager.getOncomplete().isEmpty());
        assertTrue(manager.getSugCodigo().isEmpty());
        assertTrue(manager.getSugAlumno().isEmpty());
        assertEquals(0, manager.getTmpAlutar());
        assertEquals(0, manager.getTmpEstado());

        // Interacting
        List<MbEdicionEstructPago> results = manager.autocomplete("null");
        assertTrue(results.isEmpty());

        // ------------------------------------------------------------------
        results = manager.autocomplete("");
        assertTrue(results.isEmpty());

        // ------------------------------------------------------------------
        manager.setSugAlumno("mend");

        results = manager.autocomplete(manager.getSugAlumno());
        SelectItem[] semestres = manager.getModalSemestres();
        List<SelectItem> estructurasDePago = manager.getModalPlantillas();

        assertEquals(51, results.size());
        assertNotEquals(0, semestres.length);
        assertTrue(estructurasDePago.isEmpty());

        // On Object Change
        manager.setSugAlumno("MENDOZA CALLE, RAQUEL MARLE");
        manager.setModalId(2304);
        manager.setModalSemIngreso(SEMESTRE_2008_1_ID);
        manager.setModalEspecialidad(Especialidad.EDUCACION_PRIMARIA);

        manager.setModalSemestre(SEMESTRE_2010_2_ID);

        manager.parametrosEstructura(actionEvent);

        semestres = manager.getModalSemestres();
        estructurasDePago = manager.getModalPlantillas();

        assertEquals(2304, manager.getModalId());
        assertEquals(SEMESTRE_2008_1_ID, manager.getModalSemIngreso());
        assertEquals(Especialidad.EDUCACION_PRIMARIA, manager.getModalEspecialidad());
        assertEquals(51, results.size());
        assertNotEquals(0, semestres.length);
        assertEquals(1, estructurasDePago.size());

        // On Object Change
        manager.setModalSemestre(0);

        manager.parametrosEstructura(actionEvent);

        semestres = manager.getModalSemestres();
        estructurasDePago = manager.getModalPlantillas();

        assertEquals(51, results.size());
        assertNotEquals(0, semestres.length);
        assertTrue(estructurasDePago.isEmpty());

        // Creating structures
        manager.setModalSemestre(SEMESTRE_2010_2_ID);

        manager.validarEstructuras(actionEvent);

        assertTrue(manager.getOncomplete().contains("ESTAN COMPLETAS LAS ESTRUCTURAS"));
    }

    @Test
    public void deleteAndCreatePaymentStructuresTest() {
        // 09104009 - AGUILAR AYRAS, KARITO LUZ
        manager.setParametroCodigo("09104009");
        manager.setParametroPaterno("");
        manager.setParametroMaterno("");
        manager.setParametroNombre("");
        manager.setParametroSemestre(SEMESTRE_2010_2_ID);

        manager.realizarBusqueda(manager.getParametroCodigo(), manager.getParametroPaterno(), manager.getParametroMaterno(),
                manager.getParametroNombre(), manager.getParametroSemestre());

        MbEdicionEstructPago bean = manager.getListaEstructura().get(0);
        List<Integer> vAlutarIds = bean.getAlutarIds();

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("estpag_alutarIds")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(vAlutarIds);

        manager.elimParam(actionEvent);

        assertEquals(bean.getAlutarIds(), manager.getAlutarIds());

        // Opening modal for delete
        manager.eliminarEstructura(actionEvent);

        assertTrue(manager.getOncomplete().contains("Se eliminaron las estructuras, no habia pagos registrados"));

        // Generating payment structures again
        manager.setModalId(3001);
        manager.setModalSemIngreso(SEMESTRE_2009_1_ID);
        manager.setModalEspecialidad(Especialidad.EDUCACION_PRIMARIA);
        manager.setModalSemestre(SEMESTRE_2010_2_ID);
        manager.setModalPlantilla(112); // Pago 2010-II Educacion 2009-I

        manager.validarEstructuras(actionEvent);

        assertTrue(manager.getOncomplete().contains("SE GENERARON CORRECTAMENTE LAS ESTRUCTURAS"));
    }

    @Test
    public void openModalPadlockTest() {
        manager.setParametroCodigo("");
        manager.setParametroPaterno("");
        manager.setParametroMaterno("");
        manager.setParametroNombre("");

        manager.setSubMensaje("cerrar el candado? la siguiente accion se realizara bajo su responsabilidad?");
        manager.setSubAlutarId(61695); // 2DA CUOTA DE 5 | 200.0
        manager.setSubAlutarEstado(0);

        UIParameter uipMensaje = mock(UIParameter.class);
        UIParameter uipAlutarId = mock(UIParameter.class);
        UIParameter uipAlutarEstado = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("mensaje")).thenReturn(uipMensaje);
        when(uipMensaje.getValue()).thenReturn(manager.getSubMensaje());

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("alutar_id")).thenReturn(uipAlutarId);
        when(uipAlutarId.getValue()).thenReturn(manager.getSubAlutarId());

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("alutar_estado")).thenReturn(uipAlutarEstado);
        when(uipAlutarEstado.getValue()).thenReturn(manager.getSubAlutarEstado());

        manager.salvaParam(actionEvent);

        assertEquals(manager.getSubMensaje(), manager.getSubMensaje());
        assertEquals(manager.getSubAlutarId(), manager.getTmpAlutar());
        assertEquals(manager.getSubAlutarEstado(), manager.getTmpEstado());

        // Updating
        manager.actualizarEstado(actionEvent);

        assertTrue(manager.getOncomplete().contains("No se puede cerrar el candado, presenta deudas"));
    }

    @Test
    public void openModalEditAlumnoTarifaTest() {
        manager.setParametroCodigo("10104076");
        manager.setParametroPaterno("");
        manager.setParametroMaterno("");
        manager.setParametroNombre("");

        manager.setSubAlutarId(63665);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent(PARAM_ALUTAR_ID)).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getSubAlutarId());

        manager.salvaEditar(actionEvent);

        assertNotEquals(0, manager.getModalMonto(), 0);
        assertNotNull(manager.getModalPago());
        assertNotNull(manager.getModalProrroga());
        assertEquals(manager.getSubAlutarId(), manager.getTmpAlutar());

        // ------------------------------------------------------------------
        // Prorroga is read-only from this modal now: setting it must NOT persist.
        java.util.Date deadlineBefore = fechaProrrogaEnBd(63665);
        manager.setModalProrroga(Date.valueOf(LocalDate.now().plusYears(1)));

        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("SE ACTUALIZO CORRECTAMENTE LOS DATOS"));
        assertEquals(deadlineBefore, fechaProrrogaEnBd(63665));
    }

    @Test
    public void openModalCompromisoLoadsReadOnlyHistoryTest() {
        openCompromiso(37361);

        assertEquals(manager.getSubAlutarId(), manager.getTmpAlutar());
        assertNotNull(manager.getProInfoFechaPago());
        assertNotNull(manager.getProInfoFechaProrroga());
        assertNull(manager.getProParamFecha());
        assertTrue(manager.getProParamComentario().isEmpty());
        assertFalse(manager.getProInfoDescConcepto().isEmpty());

        // Read-only history: the 3 backup prorrogas (#1..#3), display fields only.
        assertEquals(3, manager.getProListaProrrogas().size());
        MbEdicionEstructPago row = manager.getProListaProrrogas().get(0);
        assertFalse(row.getProNumero().isEmpty());
        assertNotNull(row.getProFecha());
        assertNotNull(row.getProComentario());
    }

    @Test
    public void registerCompromisoSyncsDeadlineTest() {
        int lastProId = support.maxProId(37361);
        try {
            openCompromiso(37361);
            int before = manager.getProListaProrrogas().size();

            // Search state behind the modal (F6): the grid was populated by a search - reproduce it
            // (student 08050255, sem 8) so the post-save refresh has params to re-run.
            manager.setParametroCodigo("08050255");
            manager.setParametroPaterno("");
            manager.setParametroMaterno("");
            manager.setParametroNombre("");
            manager.setParametroSemestre(8);

            manager.setProParamFecha(Date.valueOf(LocalDate.now().plusDays(20)));
            manager.setProParamComentario("commitment via UI");
            manager.guardarCompromiso(actionEvent);

            assertTrue(manager.getOncomplete().contains("mpProrroga")); // modal closes on success
            assertNull(manager.getProParamFecha());                     // fields cleared after save
            assertTrue(manager.getProParamComentario().isEmpty());
            assertEquals(Date.valueOf(LocalDate.now().plusDays(20)).toString(),
                    fechaProrrogaEnBd(37361).toString().substring(0, 10));

            // F6: the master grid was rebuilt, so the row already shows the new deadline (no re-search).
            java.util.Date shownDeadline = manager.getListaEstructura().stream()
                    .flatMap(e -> e.getSubListaEstructura().stream())
                    .filter(s -> s.getSubAlutarId() == 37361)
                    .findFirst().orElseThrow(AssertionError::new)
                    .getSubFechaProrroga();
            assertEquals(Date.valueOf(LocalDate.now().plusDays(20)).toString(),
                    shownDeadline.toString().substring(0, 10));

            openCompromiso(37361);
            assertEquals(before + 1, manager.getProListaProrrogas().size());
        } finally {
            support.cleanProrrogasCreatedDuringTest(37361, lastProId);
        }
    }

    @Test
    public void rejectsPastCompromisoTest() {
        int lastProId = support.maxProId(37361);
        try {
            openCompromiso(37361);
            String before = fechaProrrogaEnBd(37361).toString().substring(0, 10);

            manager.setProParamFecha(Date.valueOf(LocalDate.now())); // today - invalid
            manager.setProParamComentario("must fail");
            manager.guardarCompromiso(actionEvent);

            assertTrue(manager.getOncomplete().contains("alert"));
            assertFalse(manager.getOncomplete().contains("mpProrroga")); // modal stays open
            assertEquals(before, fechaProrrogaEnBd(37361).toString().substring(0, 10));
        } finally {
            support.cleanProrrogasCreatedDuringTest(37361, lastProId);
        }
    }

    private void openCompromiso(int alutarId) {
        manager.setSubAlutarId(alutarId);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent(PARAM_ALUTAR_ID)).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getSubAlutarId());
        manager.salvaCompromiso(actionEvent);
    }

    private java.util.Date fechaProrrogaEnBd(int alutarId) {
        return ServiceFinder.findBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class)
                .seleccionarUnAlumnoTarifa(alutarId).getAlutarFechaProrroga();
    }
}
