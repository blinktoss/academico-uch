package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static net.uch.common.Constants.ACTION_IMAGES_DOWN;
import static net.uch.common.Constants.ACTION_IMAGES_UP;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 14/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbEdicionEstructPagoCLTest extends SpringConfigurationTest {

    private MbEdicionEstructPagoCL manager;

    @Before
    public void setUp() {
        manager = new MbEdicionEstructPagoCL();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        List<SelectItem> areas = manager.getParametroAreas();
        List<SelectItem> modulos = manager.getParametroModulos();

        assertEquals(15, areas.size());
        assertTrue(modulos.isEmpty());

        // por area
        manager.setParametroArea(5);
        manager.verModulos(actionEvent);

        modulos = manager.getParametroModulos();

        assertEquals(5, modulos.size());

        // ------------------------------------------------------------------
        manager.setParametroArea(0);
        manager.verModulos(actionEvent);

        modulos = manager.getParametroModulos();

        assertTrue(modulos.isEmpty());
    }

    @Test
    public void seleccionarTest() {
        manager.setbCodigo("");
        manager.setbPaterno("agu");
        manager.setbMaterno("");
        manager.setbNombre("");
        manager.setParametroModulo(15);

        manager.validarBusqueda(actionEvent);

        assertEquals(4, manager.getListaEstructura().size());
        MbEdicionEstructPagoCL root = manager.getListaEstructura().get(0);
        assertFalse(root.getbAlumno().isEmpty());
        assertFalse(root.isMostrar());
        assertNotEquals(0, root.getbAluId());
        assertFalse(root.getbModulo().isEmpty());
        assertFalse(root.getbAlutarIds().isEmpty());
        assertEquals(ACTION_IMAGES_DOWN, root.getImagenVer());
        assertFalse(root.isbSubEditDisabled());
        MbEdicionEstructPagoCL child = root.getSubListaEstructura().get(0);
        assertFalse(child.getbSubConcepto().isEmpty());
        assertNotEquals(0, child.getbSubMonto(), 0);
        assertNotNull(child.getbSubFechaPago());
        assertNotNull(child.getbSubFechaProrroga());
        assertNotEquals(0, child.getbSubAlutarId());
        assertFalse(child.getbSubAlutarEstado().isEmpty());
        assertFalse(child.getbSubMsj().isEmpty());
        assertFalse(child.getbSubImgCandado().isEmpty());
        assertFalse(child.getbSubTitle().isEmpty());
        assertFalse(child.getbSubImgKlipper().isEmpty());

        root.mostrarDetalle(actionEvent);
        assertTrue(root.isMostrar());
        assertEquals(ACTION_IMAGES_UP, root.getImagenVer());

        // ------------------------------------------------------------------
        root.mostrarDetalle(actionEvent);
        assertFalse(root.isMostrar());
        assertEquals(ACTION_IMAGES_DOWN, root.getImagenVer());
    }

    @Test
    public void seleccionarValidationsTest() {
        manager.setbCodigo("");
        manager.setbPaterno("");
        manager.setbMaterno("");
        manager.setbNombre("");
        manager.setParametroModulo(0);

        manager.validarBusqueda(actionEvent);
        assertEquals(0, manager.getParametroArea());
        assertTrue(manager.getOncomplete().contains("Ingrese algun parametro para la busqueda"));

        // ------------------------------------------------------------------
        manager.setbCodigo("x");
        manager.setbPaterno("");
        manager.setbMaterno("");
        manager.setbNombre("");
        manager.setParametroModulo(0);

        manager.validarBusqueda(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione algun modulo"));

        // ------------------------------------------------------------------
        manager.setbCodigo("");
        manager.setbPaterno("x");
        manager.setbMaterno("");
        manager.setbNombre("");
        manager.setParametroModulo(0);

        manager.validarBusqueda(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione algun modulo"));

        // ------------------------------------------------------------------
        manager.setbCodigo("");
        manager.setbPaterno("");
        manager.setbMaterno("x");
        manager.setbNombre("");
        manager.setParametroModulo(0);

        manager.validarBusqueda(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione algun modulo"));

        // ------------------------------------------------------------------
        manager.setbCodigo("");
        manager.setbPaterno("");
        manager.setbMaterno("");
        manager.setbNombre("x");
        manager.setParametroModulo(0);

        manager.validarBusqueda(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione algun modulo"));
    }

    @Test
    public void autocompleteTest() {
        List<MbEdicionEstructPagoCL> result = manager.autocomplete("diaz");

        assertNotNull(result);
        assertEquals(16, result.size());
        assertNotEquals(0, result.get(0).getbAluId());
        assertFalse(result.get(0).getbCodigo().isEmpty());
        assertFalse(result.get(0).getbPaterno().isEmpty());
        assertFalse(result.get(0).getbMaterno().isEmpty());
        assertFalse(result.get(0).getbNombre().isEmpty());

        // ------------------------------------------------------------------
        result = manager.autocomplete("null");
        assertTrue(result.isEmpty());

        // ------------------------------------------------------------------
        result = manager.autocomplete("");
        assertTrue(result.isEmpty());
    }

    @Test
    public void loadModalForNewEstructuraPagoAlumnoTest() {
        manager.nuevo(actionEvent);

        assertEquals(0, manager.getModalModulo());
        assertEquals(0, manager.getModalPlantilla());
        assertEquals(0, manager.getModalId());
        assertTrue(manager.getModalAlumno().isEmpty());
        assertTrue(manager.getOncomplete().isEmpty());
        assertTrue(manager.getSugCodigo().isEmpty());
        assertTrue(manager.getSugAlumno().isEmpty());
        assertEquals(0, manager.getTmpAlutar());
        assertEquals(0, manager.getTmpEstado());

        interactingWithCombosOfModal();

        // ------------------------------------------------------------------
        tryingToGeneratePaymentStructure();
    }

    private void interactingWithCombosOfModal() {
        List<SelectItem> modulos = manager.getModalModulos();
        List<SelectItem> estructurasDePago = manager.getModalPlantillas();
        List<SelectItem> talleres = manager.getModalTalleres();
        List<SelectItem> matriculas = manager.getModalMatriculas();

        assertEquals(74, modulos.size());
        assertTrue(estructurasDePago.isEmpty());
        assertTrue(talleres.isEmpty());
        assertTrue(matriculas.isEmpty());

        // On Change
        manager.setModalModulo(8); // INGLES - BÁSICO
        manager.parametrosPlantilla(actionEvent);

        modulos = manager.getModalModulos();
        estructurasDePago = manager.getModalPlantillas();
        talleres = manager.getModalTalleres();
        matriculas = manager.getModalMatriculas();

        assertEquals(74, modulos.size());
        assertEquals(8, estructurasDePago.size());
        assertTrue(talleres.isEmpty());
        assertTrue(matriculas.isEmpty());

        // On Change
        manager.setModalPlantilla(13); // BASICO I
        manager.parametrosTaller(actionEvent);

        modulos = manager.getModalModulos();
        estructurasDePago = manager.getModalPlantillas();
        talleres = manager.getModalTalleres();
        matriculas = manager.getModalMatriculas();

        assertEquals(74, modulos.size());
        assertEquals(8, estructurasDePago.size());
        assertEquals(2, talleres.size());
        assertTrue(matriculas.isEmpty());

        // On Change
        manager.setModalPlantilla(0); // SELECT
        manager.parametrosTaller(actionEvent);

        modulos = manager.getModalModulos();
        estructurasDePago = manager.getModalPlantillas();
        talleres = manager.getModalTalleres();
        matriculas = manager.getModalMatriculas();

        assertEquals(74, modulos.size());
        assertEquals(8, estructurasDePago.size());
        assertTrue(talleres.isEmpty());
        assertTrue(matriculas.isEmpty());

        // On Change
        manager.setModalModulo(0); // SELECT
        manager.parametrosPlantilla(actionEvent);

        modulos = manager.getModalModulos();
        estructurasDePago = manager.getModalPlantillas();
        talleres = manager.getModalTalleres();
        matriculas = manager.getModalMatriculas();

        assertEquals(74, modulos.size());
        assertTrue(estructurasDePago.isEmpty());
        assertTrue(talleres.isEmpty());
        assertTrue(matriculas.isEmpty());
    }

    private void tryingToGeneratePaymentStructure() {
        manager.validarEstructuras(actionEvent);
        assertTrue(manager.getOncomplete().contains("SELECCIONE UN ALUMNO"));

        // 10410148 - MUJICA GAMIO, MELINA
        manager.setModalId(16);
        manager.validarEstructuras(actionEvent);
        assertTrue(manager.getOncomplete().contains("SELECCIONE UN MODULO"));

        // ------------------------------------------------------------------
        manager.setModalModulo(22);
        manager.validarEstructuras(actionEvent);
        assertTrue(manager.getOncomplete().contains("SELECCIONE UNA PLANTILLA"));

        // ------------------------------------------------------------------
        manager.setModalPlantilla(22);
        manager.validarEstructuras(actionEvent);
        assertTrue(manager.getOncomplete().contains("SELECCIONE UN TALLER"));

        // ------------------------------------------------------------------
        manager.setModalTaller(83);
        manager.validarEstructuras(actionEvent);
        assertTrue(manager.getOncomplete().contains("SELECCIONE UNA MATRICULA DEL ALUMNO"));

        // ------------------------------------------------------------------
        manager.setModalMatricula(2813);
        manager.validarEstructuras(actionEvent);
        assertTrue(manager.getOncomplete().contains("ESTAN COMPLETAS LAS ESTRUCTURAS"));
    }

    @Test
    public void openModalCandadoTest() {
        manager.setbCodigo("10020541");
        manager.setbPaterno("");
        manager.setbMaterno("");
        manager.setbNombre("");
        manager.setParametroModulo(15);

        MbEdicionEstructPagoCL bean = new MbEdicionEstructPagoCL();
        bean.setbSubAlutarId(605);
        bean.setbSubAlutarEstado("0");
        bean.setbSubMsj(RandomStringUtils.randomAlphabetic(76));

        Map<String, Object> attributes = Collections.singletonMap("estructura_pago_cl", bean);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.askToOpenCloseLock(actionEvent);

        assertEquals(bean.getbSubAlutarId(), manager.getTmpAlutar());
        assertEquals(bean.getbSubAlutarEstado(), String.valueOf(manager.getTmpEstado()));
        assertEquals(bean.getbSubMsj(), manager.getbSubMsj());

        // ------------------------------------------------------------------
        manager.actualizarEstado(actionEvent);
        assertTrue(manager.getOncomplete().contains("No se puede cerrar el candado, presenta deudas"));
    }

    @Test
    public void openModalToEditTest() {
        Date currentDate = new Date();
        manager.setbCodigo("10020541");
        manager.setbPaterno("");
        manager.setbMaterno("");
        manager.setbNombre("");
        manager.setParametroModulo(15);

        MbEdicionEstructPagoCL bean = new MbEdicionEstructPagoCL();
        bean.setbSubMonto(Stubs.randomNumeric(0, 51));
        bean.setbSubFechaPago(currentDate);
        bean.setbSubFechaProrroga(currentDate);
        bean.setbSubAlutarId(605);

        Map<String, Object> attributes = Collections.singletonMap("estructura_pago_cl", bean);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.openModalToEdit(actionEvent);

        assertEquals(bean.getbSubMonto(), manager.getModalMonto(), 0);
        assertEquals(bean.getbSubFechaPago(), manager.getModalPago());
        assertEquals(bean.getbSubFechaProrroga(), manager.getModalProrroga());
        assertEquals(bean.getbSubAlutarId(), manager.getTmpAlutar());

        // ------------------------------------------------------------------
        manager.editar(actionEvent);
        assertTrue(manager.getOncomplete().contains("SE ACTUALIZO CORRECTAMENTE LOS DATOS"));
    }

    @Test
    public void deletingPaymentStructureWithPayments() {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("estpag_alutarIds")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(Collections.singletonList(172));

        manager.askToDelete(actionEvent);

        assertEquals(1, manager.getbAlutarIds().size());

        // ------------------------------------------------------------------
        manager.eliminarEstructura(actionEvent);

        assertTrue(manager.getOncomplete().contains("No se puede eliminar, hay pagos realizados"));
    }
}
