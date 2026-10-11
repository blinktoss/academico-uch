package net.uch.banco.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 06/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbProcesoBancoTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private MbProcesoBanco manager;

    @AfterClass
    public static void cleanUpDatabase() {
        DAO.cleanTbBancoEntrada();
    }

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbProcesoBanco();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadMainPageTest() {
        manager.setNroInicio("");

        List<SelectItem> series = manager.getSeries();

        assertTrue(manager.isGeneraComprobante());
        assertTrue(manager.getSerie().isEmpty());
        assertEquals(4, series.size());
        assertTrue(manager.getNroInicio().isEmpty());
    }

    @Test
    public void limpiarTodoTest() {
        manager.setRespuestas(null);
        manager.setErrores(null);

        String result = manager.limpiarTodo();

        assertTrue(manager.isDisableProceso());
        assertFalse(manager.isRendered());
        assertTrue(manager.isGeneraComprobante());
        assertNull(manager.getFile());
        assertTrue(manager.getMensaje().isEmpty());
        assertTrue(manager.getBodyClass().isEmpty());
        assertTrue(manager.getFileNombre().isEmpty());
        assertEquals(0, manager.getFileDim());
        assertTrue(manager.getEfecto().isEmpty());
        assertTrue(manager.getNroInicio().isEmpty());
        assertTrue(manager.getRespuestas().isEmpty());
        assertEquals("ProcesoBanco", result);
    }

    @Test
    public void cargarArchivoTest() {
        UploadEvent uploadEvent = mock(UploadEvent.class);
        UploadItem uploadItem = mock(UploadItem.class);
        File fakeFile = mock(File.class);

        when(uploadEvent.getUploadItem()).thenReturn(uploadItem);
        when(uploadItem.getFile()).thenReturn(fakeFile);
        when(uploadItem.getFileName()).thenReturn("FAKE_NAME");
        when(uploadItem.getFileSize()).thenReturn(1000);

        manager.cargarArchivo(uploadEvent);

        assertEquals(fakeFile, manager.getFile());
        assertEquals("FAKE_NAME", manager.getFileNombre());
        assertEquals(1000, manager.getFileDim());
        assertFalse(manager.isDisableProceso());
    }

    @Test
    public void procesarArchivoTest() throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource("proceso_banco.txt");
        assertNotNull(resource);
        manager.setFile(new File(resource.toURI()));

        manager.procesarArchivo(actionEvent);

        assertEquals("success", manager.getBodyClass());
        assertNull(manager.getFile());
        assertTrue(manager.isDisableProceso());
        assertTrue(manager.isRendered());
        assertTrue(manager.isGeneraComprobante());
        assertTrue(manager.getErrores().isEmpty());
        assertEquals(6, manager.getTotalDataBanco());
        assertEquals(0, manager.getTotalErrores());

        salvarInformacionConComprobantesTest();
    }

    private void salvarInformacionConComprobantesTest() {
        manager.setGeneraComprobante(true);
        manager.setSerie("004");
        manager.cambiaSerie(actionEvent);

        manager.validaComprobante(actionEvent);
        assertTrue(manager.getOncomplete().contains("mpSalvar"));

        manager.setFechaComprobantes(new Date());

        String result = manager.guardarInformacion();
        assertEquals("ProcesoBanco", result);
    }

    @Test
    public void procesarArchivoMultipleTest() throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource("proceso_banco_multiple.txt");
        assertNotNull(resource);
        manager.setFile(new File(resource.toURI()));

        manager.procesarArchivo(actionEvent);

        assertEquals("success", manager.getBodyClass());
        assertNull(manager.getFile());
        assertTrue(manager.isDisableProceso());
        assertTrue(manager.isRendered());
        assertTrue(manager.isGeneraComprobante());
        assertTrue(manager.getErrores().isEmpty());
        assertEquals(22, manager.getTotalDataBanco());
        assertEquals(0, manager.getTotalErrores());

        salvarInformacionSinComprobantesTest();
    }

    private void salvarInformacionSinComprobantesTest() {
        manager.setNroInicio("");
        manager.setSerie("");
        manager.setGeneraComprobante(true);

        manager.validaComprobante(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese un numero de inicio correcto"));

        // ------------------------------------------------------------------
        manager.setSerie("0");
        manager.validaComprobante(actionEvent);
        assertTrue(manager.getOncomplete().contains("Seleccione una serie para los comprobantes"));

        // ------------------------------------------------------------------
        manager.setSerie("001");
        manager.setNroInicio("0");
        manager.validaComprobante(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese un numero de inicio correcto"));

        // ------------------------------------------------------------------

        manager.setGeneraComprobante(false);
        manager.validaComprobante(actionEvent);
        assertTrue(manager.getOncomplete().contains("mpSalvar"));
    }

    @Test
    public void procesarArchivoConErrorTest() throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource("proceso_banco_error.txt");
        assertNotNull(resource);
        manager.setFile(new File(resource.toURI()));

        manager.procesarArchivo(actionEvent);

        assertEquals("error", manager.getBodyClass());
        assertNull(manager.getFile());
        assertTrue(manager.isDisableProceso());
        assertTrue(manager.isRendered());
        assertTrue(manager.isGeneraComprobante());
        assertEquals(5, manager.getErrores().size());
        assertEquals(0, manager.getTotalDataBanco());
        assertEquals(5, manager.getTotalErrores());
    }

    @Test
    public void generarArchivoDescargableTest() throws IOException {
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        when(externalContext.getResponse()).thenReturn(httpServletResponse);
        when(httpServletResponse.getOutputStream()).thenReturn(outputStream);

        manager.generarArchivoDescargable(actionEvent);

        assertTrue(manager.getOncomplete().contains("Archivo generado"));
    }

    @Test
    public void seleccionarEfectoTest() {
        manager.setGeneraComprobante(true);

        manager.seleccionarEfecto(actionEvent);

        assertTrue(manager.getEfecto().contains("showDiv"));
        assertTrue(manager.getSerie().isEmpty());
        assertTrue(manager.getNroInicio().isEmpty());

        // ------------------------------------------------------------------
        manager.setSerie("001");
        manager.cambiaSerie(actionEvent);
        assertFalse(manager.getNroInicio().isEmpty());

        manager.setSerie("0");
        manager.cambiaSerie(actionEvent);
        assertTrue(manager.getNroInicio().isEmpty());

        // uncheck
        manager.setGeneraComprobante(false);

        manager.seleccionarEfecto(actionEvent);

        assertTrue(manager.getEfecto().contains("hideDiv"));
    }
}
