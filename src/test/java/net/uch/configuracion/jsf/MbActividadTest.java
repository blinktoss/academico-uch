package net.uch.configuracion.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 06/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbActividadTest extends SpringConfigurationTest {

    private MbActividad manager;

    @Before
    public void setUp() {
        loggerUser.setRowUsuId(17);
        when(session.getAttribute("usuario")).thenReturn(loggerUser);

        manager = new MbActividad();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void initializationOnInitTest() {
        SelectItem[] result = manager.getItemComboTipo();

        assertNotNull(result);
        assertEquals(1, result.length);

        result = manager.getItemCheckAlcance();

        assertNotNull(result);
        assertEquals(5, result.length);
    }

    @Test
    public void mostrarPorFechaTest() {
        LocalDate of = LocalDate.of(2010, 3, 29);
        manager.setParamDate(Date.valueOf(of));

        manager.mostrarPorFecha(actionEvent);

        assertEquals(1, manager.getListaActividades().size());
        assertFalse(manager.getListaActividades().get(0).getRowAlcance().isEmpty());
        assertFalse(manager.getListaActividades().get(0).getImagenPublicado().isEmpty());
        assertFalse(manager.getListaActividades().get(0).getTitle().isEmpty());
        assertFalse(manager.getListaActividades().get(0).getRowEstadoPublicado().isEmpty());
    }

    @Test
    public void mostrarPorFechaSinResutadoTest() {
        manager.setParamDate(null);

        manager.mostrarPorFecha(actionEvent);

        assertNull(manager.getListaActividades());
    }

    @Test
    public void openModalForNewActivityTest() throws SQLException, URISyntaxException {
        manager.nuevo(actionEvent);

        assertTrue(manager.getRowTitulo().isEmpty());
        assertNull(manager.getRowFecha());
        assertNull(manager.getRowFechaIni());
        assertNull(manager.getRowFechaFin());
        assertNull(manager.getMpAlcances());
        assertFalse(manager.isRowPublicado());
        assertTrue(manager.getMpDescripcion().isEmpty());
        assertTrue(manager.getMpTipo().isEmpty());
        assertNull(manager.getMpImg());
        assertEquals(0, manager.getEditar());

        creatingNewActivityTest();

        editingActivityTest();

        publishAndDeleteActivityTest();
    }

    private void creatingNewActivityTest() throws SQLException, URISyntaxException {
        manager.setMpAlcances(new String[0]); // JSF set as empty array

        manager.manipularDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese un titulo"));

        // ------------------------------------------------------------------
        manager.setRowTitulo(RandomStringUtils.randomAlphanumeric(20));
        manager.manipularDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Faltan llenar los campos de las fechas"));

        // ------------------------------------------------------------------
        LocalDate now = LocalDate.now();

        manager.setRowFecha(Date.valueOf(now));
        manager.manipularDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Faltan llenar los campos de las fechas"));

        manager.setRowFechaIni(Date.valueOf(now.plusWeeks(2)));
        manager.manipularDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Faltan llenar los campos de las fechas"));

        manager.setRowFechaFin(Date.valueOf(now.plusWeeks(1)));
        manager.manipularDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("La fecha de fin no puede ser antes de la de inicio"));

        // ------------------------------------------------------------------
        manager.setRowFechaFin(Date.valueOf(now.plusWeeks(6)));
        manager.manipularDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione al menos un alcance"));

        // ------------------------------------------------------------------
        manager.setMpAlcances(Stream.of(manager.getItemCheckAlcance()).map(x -> x.getValue().toString()).toArray(String[]::new));

        UploadEvent uploadEvent = mock(UploadEvent.class);
        UploadItem uploadItem = mock(UploadItem.class);
        URL resource = getClass().getClassLoader().getResource("img/167523_494694684132_1292964_n.jpg");
        assertNotNull(resource);

        when(uploadEvent.getUploadItem()).thenReturn(uploadItem);
        when(uploadItem.getContentType()).thenReturn("image/jpeg");
        when(uploadItem.getFile()).thenReturn(new File(resource.toURI()));

        manager.cargaImagen(uploadEvent);
        assertFalse(manager.getMpMime().isEmpty());

        manager.manipularDatos(actionEvent);
        assertNotEquals(0, manager.getTimeStamp());
        assertTrue(manager.getOncomplete().contains("Imagen demasiado pesada"));

        // ------------------------------------------------------------------
        resource = getClass().getClassLoader().getResource("img/poker_face_2_by_rober_raik-d4cxlfn.png");
        assertNotNull(resource);

        when(uploadItem.getContentType()).thenReturn("image/png");
        when(uploadItem.getFile()).thenReturn(new File(resource.toURI()));
        manager.cargaImagen(uploadEvent);

        manager.setMpTipo("053001");
        manager.setMpDescripcion(RandomStringUtils.randomAlphanumeric(50));
        manager.manipularDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se creo una Actividad correctamente"));
    }

    private void editingActivityTest() throws SQLException {
        int id = manager.getRowId();
        UIParameter uiParameter = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(id);

        manager.seleccionarActividad(actionEvent);

        assertEquals(id, manager.getRowId());
        assertNotEquals(0, manager.getEditar());
        // MIME type round-trips from the persisted calact_mime column - editing an existing
        // activity now knows the image type without a fresh upload (the bug this fixes).
        assertEquals("image/png", manager.getMpMime());

        // ------------------------------------------------------------------
        manager.setMpAlcances(new String[]{"052001", "052003", "052005"});
        manager.setRowPublicado(true);
        manager.limpiarImagenActividad(actionEvent);

        manager.manipularDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Se actualizo una Actividad correctamente"));
    }

    private void publishAndDeleteActivityTest() {
        UIParameter uipId = mock(UIParameter.class);
        UIParameter uipEstPub = mock(UIParameter.class);
        UIParameter uipFecha = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);

        when(uiComponent.findComponent("p_id")).thenReturn(uipId);
        when(uiComponent.findComponent("p_est_pub")).thenReturn(uipEstPub);
        when(uiComponent.findComponent("p_fecha")).thenReturn(uipFecha);

        when(uipId.getValue()).thenReturn(manager.getRowId());
        when(uipEstPub.getValue()).thenReturn(manager.getRowEstadoPublicado());
        when(uipFecha.getValue()).thenReturn(manager.getRowFecha());

        manager.setearParametros(actionEvent);

        assertEquals(manager.getRowId(), manager.getEditar());
        assertEquals(manager.getRowEstadoPublicado(), manager.getPublicado());

        // ------------------------------------------------------------------
        manager.publicarActividad(actionEvent);
        assertEquals("1", manager.getPublicado());

        manager.publicarActividad(actionEvent);
        assertEquals("0", manager.getPublicado());

        // ------------------------------------------------------------------
        manager.eliminarActividad(actionEvent);
    }

    @Test
    public void limpiarImagenActividadTest() throws IOException, SQLException, URISyntaxException {
        UploadEvent uploadEvent = mock(UploadEvent.class);
        UploadItem uploadItem = mock(UploadItem.class);
        URL resource = getClass().getClassLoader().getResource("img/user.png");
        assertNotNull(resource);

        when(uploadEvent.getUploadItem()).thenReturn(uploadItem);
        when(uploadItem.getContentType()).thenReturn("image/jpeg");
        when(uploadItem.getFile()).thenReturn(new File(resource.toURI()));

        manager.cargaImagen(uploadEvent);

        assertNotNull(manager.getMpImg());
        assertEquals("image/jpeg", manager.getMpMime());

        manager.limpiarImagenActividad(actionEvent);

        assertNull(manager.getMpImg());
    }

    @Test
    public void seleccionarActividadTest() {
        int id = 2;

        UIParameter uiParameter = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(String.valueOf(id));

        manager.seleccionarActividad(actionEvent);

        assertEquals(id, manager.getRowId());
        assertNotEquals(0, manager.getEditar());
        assertNotNull(manager.getMpTipo());
        assertNotEquals(0, manager.getMpAlcances().length);
        assertNotNull(manager.getRowTitulo());
        assertNotNull(manager.getRowFechaIni());
        assertNotNull(manager.getRowFechaFin());
        assertNotNull(manager.getMpDescripcion());
        assertNull(manager.getMpImg());
    }
}
