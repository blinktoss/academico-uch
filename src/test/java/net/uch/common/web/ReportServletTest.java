package net.uch.common.web;

import net.uch.Stubs;
import net.uch.common.Print;
import net.uch.common.security.Modules;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReportServlet}, mirroring the harness used by
 * {@code net.uch.academica.web.StudentPhotoServletTest}.
 */
public class ReportServletTest {

    private static final String PRINTER_BEAN = "printer";
    private static final String LOGO_PATH = "/Imagenes/actions/logo_p.jpg";

    private ReportServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ServletContext servletContext;
    private Print printer;
    private TestServletOutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        WebApplicationContext applicationContext = mock(WebApplicationContext.class);
        printer = mock(Print.class);
        when(applicationContext.getBean(PRINTER_BEAN, Print.class)).thenReturn(printer);

        servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
                .thenReturn(applicationContext);

        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        servlet = new ReportServlet();
        servlet.init(servletConfig);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        outputStream = new TestServletOutputStream();
    }

    private void loggedIn() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
    }

    @Test
    public void doGetWithoutSessionReturnsForbiddenTest() throws Exception {
        when(request.getParameter("type")).thenReturn("matricula-ficha");
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetWithoutLoggedUserReturnsForbiddenTest() throws Exception {
        when(request.getParameter("type")).thenReturn("matricula-ficha");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetWithUnknownTypeReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("does-not-exist");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown report type");
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetWithoutRequiredModuleReturnsForbiddenTest() throws Exception {
        // A cashier (Pagos) has no Matricula module, so the enrollment report is forbidden.
        when(request.getParameter("type")).thenReturn("matricula-ficha");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.PAGOS));

        servlet.doGet(request, response);

        verify(response).setHeader("Referrer-Policy", "no-referrer");
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetWithGrantedModuleStreamsPdfTest() throws Exception {
        // A non-admin user whose role grants Matricula can run the enrollment report.
        when(request.getParameter("type")).thenReturn("matricula-ficha");
        when(request.getParameter("matId")).thenReturn("10593");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.MATRICULA));
        when(servletContext.getResource(LOGO_PATH)).thenReturn(new URL("file:/logo.jpg"));
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/ficha_matricula.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        verify(printer).generateReport(eq(outputStream), any(), any());
    }

    @Test
    public void doGetWithMissingMatIdReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("matricula-ficha");
        when(request.getParameter("matId")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetWithNonNumericMatIdReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("matricula-ficha");
        when(request.getParameter("matId")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetWithMissingTemplateReturnsServerErrorTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("matricula-ficha");
        when(request.getParameter("matId")).thenReturn("10593");
        when(servletContext.getResource(LOGO_PATH)).thenReturn(new URL("file:/logo.jpg"));
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/ficha_matricula.jasper")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetFichaStreamsPdfTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("matricula-ficha");
        when(request.getParameter("matId")).thenReturn("10593");
        when(servletContext.getResource(LOGO_PATH)).thenReturn(new URL("file:/logo.jpg"));
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/ficha_matricula.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader("Content-Disposition", "inline; filename=\"ficha_matricula.pdf\"");
        verify(response).setHeader("Cache-Control", "no-store");

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        assertEquals(Integer.valueOf(10593), captor.getValue().get("mat_id"));
    }

    @Test
    public void doGetConsolidadoIncludesSubreportTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("matricula-consolidado");
        when(request.getParameter("matId")).thenReturn("10593");
        when(servletContext.getResource(LOGO_PATH)).thenReturn(new URL("file:/logo.jpg"));
        when(servletContext.getRealPath("/")).thenReturn("/app/");
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/consolidado_matricula.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        Map<String, Object> parameters = captor.getValue();
        assertEquals(Integer.valueOf(10593), parameters.get("mat_id"));
        assertEquals("/app//WEB-INF/Reportes/sub_consolidado_matricula.jasper", parameters.get("subreport"));
    }

    @Test
    public void doGetNotasSeccionStreamsPdfTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("notas-seccion");
        when(request.getParameter("seccion")).thenReturn("ISI01MA");
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/notas_de_todos_alumnos_sistemas.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader("Content-Disposition", "inline; filename=\"notas_seccion.pdf\"");
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        assertEquals("ISI01MA", captor.getValue().get("SECCION"));
    }

    @Test
    public void doGetNotasWithUnsupportedSectionReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("notas-seccion");
        when(request.getParameter("seccion")).thenReturn("UNICA");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetNotasWithMissingSectionReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("notas-seccion");
        when(request.getParameter("seccion")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetAlumnoComprobanteStreamsPdfTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("alumno-comprobante");
        when(request.getParameter("aluId")).thenReturn("2080");
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/comprobante.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader("Content-Disposition", "inline; filename=\"comprobante.pdf\"");
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        assertEquals(Integer.valueOf(2080), captor.getValue().get("aluId"));
    }

    @Test
    public void doGetAlumnoComprobanteWithMissingIdReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("alumno-comprobante");
        when(request.getParameter("aluId")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetConvalidacionBoletaStreamsPdfTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("convalidacion-boleta");
        when(request.getParameter("aluCodigo")).thenReturn("09105155");
        when(servletContext.getResource(LOGO_PATH)).thenReturn(new URL("file:/logo.jpg"));
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/boleta_notas_codigo.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        Map<String, Object> parameters = captor.getValue();
        assertEquals("09105155", parameters.get("alu_codigo"));
        assertEquals("8", parameters.get("sem_id"));
    }

    @Test
    public void doGetConvalidacionBoletaWithMissingCodeReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("convalidacion-boleta");
        when(request.getParameter("aluCodigo")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetNotasClSeccionStreamsPdfTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("notascl-seccion");
        when(request.getParameter("secId")).thenReturn("345");
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/cl_notas_seccion.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader("Content-Disposition", "inline; filename=\"notas_cl_seccion.pdf\"");
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        assertEquals(Integer.valueOf(345), captor.getValue().get("SEC_ID"));
    }

    @Test
    public void doGetNotasClSeccionWithMissingIdReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("notascl-seccion");
        when(request.getParameter("secId")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetHistorialPagosStreamsPdfTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("historial-pagos");
        when(request.getParameter("aluCodigo")).thenReturn("09104009");
        when(request.getParameter("semId")).thenReturn("8");
        when(servletContext.getResource(LOGO_PATH)).thenReturn(new URL("file:/logo.jpg"));
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/historial_pagos.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        Map<String, Object> parameters = captor.getValue();
        assertEquals("09104009", parameters.get("ALU_CODIGO"));
        assertEquals(Integer.valueOf(8), parameters.get("SEM_ID"));
    }

    @Test
    public void doGetHistorialPagosWithMissingSemIdReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("historial-pagos");
        when(request.getParameter("aluCodigo")).thenReturn("09104009");
        when(request.getParameter("semId")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetCuadreCajaStreamsPdfTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("cuadre-caja");
        when(request.getParameter("fecIni")).thenReturn("2010-03-08");
        when(request.getParameter("fecFin")).thenReturn("2010-03-10");
        when(request.getParameter("usuId")).thenReturn("1");
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/cuadre_caja_diario.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        Map<String, Object> parameters = captor.getValue();
        assertEquals("2010-03-08", parameters.get("fec_ini"));
        assertEquals("2010-03-10", parameters.get("fec_fin"));
        assertEquals(Integer.valueOf(1), parameters.get("usu_id"));
    }

    @Test
    public void doGetCuadreCajaWithMissingUsuIdReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("cuadre-caja");
        when(request.getParameter("fecIni")).thenReturn("2010-03-08");
        when(request.getParameter("fecFin")).thenReturn("2010-03-10");
        when(request.getParameter("usuId")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(printer);
    }

    @Test
    public void doGetBancoRespuestaStreamsPdfTest() throws Exception {
        loggedIn();
        when(request.getParameter("type")).thenReturn("banco-respuesta");
        when(servletContext.getResourceAsStream("/WEB-INF/Reportes/data_respuesta_banco.jasper"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1}));
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader("Content-Disposition", "inline; filename=\"data_respuesta_banco.pdf\"");
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(printer).generateReport(eq(outputStream), any(), captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    private static class TestServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        @Override
        public void write(int b) {
            buffer.write(b);
        }

        byte[] toByteArray() {
            return buffer.toByteArray();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(javax.servlet.WriteListener writeListener) {
        }
    }
}
