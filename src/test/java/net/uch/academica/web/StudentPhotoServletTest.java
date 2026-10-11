package net.uch.academica.web;

import net.uch.Stubs;
import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.common.Constants;
import net.uch.common.security.Modules;
import net.uch.mapping.AcAlumno;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.WebApplicationContext;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Cesardl
 */
public class StudentPhotoServletTest {

    private static final String DAO_BEAN_NAME = "springHibernateDaoAlumno";

    private StudentPhotoServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ServletContext servletContext;
    private WebApplicationContext applicationContext;
    private HSAlumnoDAO alumnoDAO;
    private TestServletOutputStream outputStream;

    private static byte[] aPngImage() throws Exception {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageIO.write(image, "png", buffer);
        return buffer.toByteArray();
    }

    @Before
    public void setUp() throws Exception {
        applicationContext = mock(WebApplicationContext.class);
        alumnoDAO = mock(HSAlumnoDAO.class);
        when(applicationContext.getBean(DAO_BEAN_NAME, HSAlumnoDAO.class)).thenReturn(alumnoDAO);

        servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
                .thenReturn(applicationContext);

        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        servlet = new StudentPhotoServlet();
        servlet.init(servletConfig);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        outputStream = new TestServletOutputStream();
    }

    @Test
    public void doGetWithoutSessionReturnsForbiddenTest() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void doGetWithoutLoggedUserReturnsForbiddenTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void doGetWithoutAlumnosModuleReturnsForbiddenTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.PAGOS));
        when(request.getParameter("id")).thenReturn("123");

        servlet.doGet(request, response);

        verify(response).setHeader("Referrer-Policy", "no-referrer");
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(alumnoDAO);
    }

    @Test
    public void doGetWithNonNumericIdReturnsBadRequestTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void doGetWithZeroIdServesDefaultPhotoTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
        when(request.getParameter("id")).thenReturn("0");
        when(response.getOutputStream()).thenReturn(outputStream);
        when(servletContext.getResourceAsStream(Constants.ACTION_IMAGES_USER_INFO))
                .thenReturn(new ByteArrayInputStream(aPngImage()));

        servlet.doGet(request, response);

        verify(response).setContentType("image/png");
        verify(response).setHeader("ETag", "\"alu-foto-0\"");
        assertTrue(outputStream.toByteArray().length > 0);
    }

    @Test
    public void doGetWithMatchingEtagReturnsNotModifiedTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
        when(request.getParameter("id")).thenReturn("123");
        when(request.getHeader("If-None-Match")).thenReturn("\"alu-foto-123\"");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        verifyNoInteractions(alumnoDAO);
    }

    @Test
    public void doGetWithUnknownStudentServesDefaultPhotoTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
        when(request.getParameter("id")).thenReturn("123");
        when(alumnoDAO.seleccionarUnAlumno(123)).thenReturn(null);
        when(response.getOutputStream()).thenReturn(outputStream);
        when(servletContext.getResourceAsStream(Constants.ACTION_IMAGES_USER_INFO))
                .thenReturn(new ByteArrayInputStream(aPngImage()));

        servlet.doGet(request, response);

        verify(response).setContentType("image/png");
        assertTrue(outputStream.toByteArray().length > 0);
    }

    @Test
    public void doGetWithoutPhotoServesDefaultPhotoTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
        when(request.getParameter("id")).thenReturn("123");

        AcAlumno alumno = new AcAlumno();
        alumno.setAluFoto(null);
        when(alumnoDAO.seleccionarUnAlumno(123)).thenReturn(alumno);
        when(response.getOutputStream()).thenReturn(outputStream);
        when(servletContext.getResourceAsStream(Constants.ACTION_IMAGES_USER_INFO))
                .thenReturn(new ByteArrayInputStream(aPngImage()));

        servlet.doGet(request, response);

        verify(response).setContentType("image/png");
        assertTrue(outputStream.toByteArray().length > 0);
    }

    @Test
    public void doGetWithPhotoWritesPngTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
        when(request.getParameter("id")).thenReturn("123");
        when(request.getHeader("If-None-Match")).thenReturn(null);
        when(response.getOutputStream()).thenReturn(outputStream);

        byte[] pngBytes = aPngImage();
        Blob foto = mock(Blob.class);
        when(foto.getBinaryStream()).thenReturn(new ByteArrayInputStream(pngBytes));

        AcAlumno alumno = new AcAlumno();
        alumno.setAluFoto(foto);
        when(alumnoDAO.seleccionarUnAlumno(123)).thenReturn(alumno);

        servlet.doGet(request, response);

        verify(response).setContentType("image/png");
        verify(response).setHeader("ETag", "\"alu-foto-123\"");
        verify(response).setHeader("Cache-Control", "private, max-age=86400");
        assertTrue(outputStream.toByteArray().length > 0);
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
