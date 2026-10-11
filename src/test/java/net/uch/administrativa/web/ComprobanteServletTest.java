package net.uch.administrativa.web;

import net.uch.Stubs;
import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.administrativa.dao.HSClienteDAO;
import net.uch.administrativa.dao.HSPagoDAO;
import net.uch.common.SystemHelper;
import net.uch.common.security.Modules;
import net.uch.mapping.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ComprobanteServlet}, mirroring the harness used by
 * {@code net.uch.academica.web.StudentPhotoServletTest} and {@code ReportServletTest}.
 */
public class ComprobanteServletTest {

    private ComprobanteServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ServletContext servletContext;
    private HSPagoDAO daoPago;
    private HSAlumnoDAO daoAlumno;
    private HSClienteDAO daoCliente;
    private HSAlumnoTarifaDAO daoAlumnoTarifa;
    private SystemHelper helper;
    private ByteArrayOutputStream responseBody;
    private PrintWriter printWriter;

    @Before
    public void setUp() throws Exception {
        WebApplicationContext applicationContext = mock(WebApplicationContext.class);
        daoPago = mock(HSPagoDAO.class);
        daoAlumno = mock(HSAlumnoDAO.class);
        daoCliente = mock(HSClienteDAO.class);
        daoAlumnoTarifa = mock(HSAlumnoTarifaDAO.class);
        helper = mock(SystemHelper.class);
        when(applicationContext.getBean("springHibernateDaoPago", HSPagoDAO.class)).thenReturn(daoPago);
        when(applicationContext.getBean("springHibernateDaoAlumno", HSAlumnoDAO.class)).thenReturn(daoAlumno);
        when(applicationContext.getBean("springHibernateDaoCliente", HSClienteDAO.class)).thenReturn(daoCliente);
        when(applicationContext.getBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class)).thenReturn(daoAlumnoTarifa);
        when(applicationContext.getBean("helper", SystemHelper.class)).thenReturn(helper);

        servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
                .thenReturn(applicationContext);

        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        servlet = new ComprobanteServlet();
        servlet.init(servletConfig);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        responseBody = new ByteArrayOutputStream();
    }

    private void loggedIn() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
    }

    private void stubWriter() throws Exception {
        printWriter = new PrintWriter(responseBody);
        when(response.getWriter()).thenReturn(printWriter);
    }

    private AdPago paymentOf(float monto, String descripcion) {
        var conpag = new AdConceptoPago();
        conpag.setConpagTipo("000000");
        conpag.setConpagDescripcion(descripcion);

        var pago = new AdPago();
        pago.setConpag(conpag);
        pago.setPagMonto(monto);
        return pago;
    }

    @Test
    public void doGetWithoutSessionReturnsForbiddenTest() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(daoPago);
    }

    @Test
    public void doGetWithoutLoggedUserReturnsForbiddenTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(daoPago);
    }

    @Test
    public void doGetWithoutPagosModuleReturnsForbiddenTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.ALUMNOS));
        when(request.getParameter("id")).thenReturn("493");

        servlet.doGet(request, response);

        verify(response).setHeader("Referrer-Policy", "no-referrer");
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(daoPago);
    }

    @Test
    public void doGetWithNonNumericIdReturnsBadRequestTest() throws Exception {
        loggedIn();
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void doGetWithUnknownComprobanteReturnsNotFoundTest() throws Exception {
        loggedIn();
        when(request.getParameter("id")).thenReturn("999");
        when(daoPago.seleccionarUnComprobantePago(999)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void doGetForAlumnoWritesPrintableHtmlTest() throws Exception {
        loggedIn();
        when(request.getParameter("id")).thenReturn("493");
        stubWriter();

        var compag = new AdComprobantePago();
        compag.setCompagClienteTipo("014001");
        compag.setCompagCliente(123);
        compag.setCompagFecha(new Date());
        compag.setCompagNro("0001-000493");
        compag.setAdPagos(Collections.singleton(paymentOf(150.5f, "Matricula")));
        when(daoPago.seleccionarUnComprobantePago(493)).thenReturn(compag);
        when(helper.format(compag.getCompagFecha())).thenReturn("2026-06-24");

        var alumno = new AcAlumno();
        alumno.setAluCodigo("09105155");
        alumno.setAluAppaterno("Diaz");
        alumno.setAluApmaterno("Lopez");
        alumno.setAluNombres("Cesar");
        when(daoAlumno.seleccionarUnAlumno(123)).thenReturn(alumno);

        servlet.doGet(request, response);
        printWriter.flush();

        verify(response).setContentType("text/html;charset=UTF-8");
        String html = responseBody.toString();
        assertTrue(html.contains("09105155"));
        assertTrue(html.contains("Diaz"));
        assertTrue(html.contains("0001-000493"));
        assertTrue(html.contains("Matricula"));
        assertTrue(html.contains("onload=\"hacerPrint();\""));
        verifyNoInteractions(daoCliente);
    }

    @Test
    public void doGetForClienteWritesPrintableHtmlTest() throws Exception {
        loggedIn();
        when(request.getParameter("id")).thenReturn("30989");
        stubWriter();

        var compag = new AdComprobantePago();
        compag.setCompagClienteTipo("014002");
        compag.setCompagCliente(456);
        compag.setCompagFecha(new Date());
        compag.setCompagNro("0001-030989");
        compag.setAdPagos(Collections.singleton(paymentOf(80.0f, "Carnet")));
        when(daoPago.seleccionarUnComprobantePago(30989)).thenReturn(compag);
        when(helper.format(compag.getCompagFecha())).thenReturn("2026-06-24");

        var cliente = new AdClientes();
        cliente.setCliRazonSocial("Acme SAC");
        when(daoCliente.seleccionarUnCliente(456)).thenReturn(cliente);

        servlet.doGet(request, response);
        printWriter.flush();

        String html = responseBody.toString();
        assertTrue(html.contains("Acme SAC"));
        assertTrue(html.contains("0001-030989"));
        verifyNoInteractions(daoAlumno);
    }
}
