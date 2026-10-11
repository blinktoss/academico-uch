package net.uch.configuracion.web;

import net.uch.Stubs;
import net.uch.common.security.Modules;
import net.uch.configuracion.jsf.MbActividad;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ActivityImageServlet}, mirroring the harness used by
 * {@code net.uch.academica.web.StudentPhotoServletTest}.
 *
 * <p>Unlike the other photo servlets, this one has no DAO to mock: the image always comes from the
 * session-scoped {@code managerActividad} bean instance, covering both the "freshly uploaded, not
 * yet saved" and "loaded from an existing activity" cases.
 */
public class ActivityImageServletTest {

    private ActivityImageServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private TestServletOutputStream outputStream;

    @Before
    public void setUp() {
        servlet = new ActivityImageServlet();
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
    public void doGetWithoutActividadesModuleReturnsForbiddenTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.ALUMNOS));

        servlet.doGet(request, response);

        verify(response).setHeader("Referrer-Policy", "no-referrer");
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void doGetWithoutStagedBeanReturnsNotFoundTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
        when(session.getAttribute("managerActividad")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void doGetWithoutStagedImageReturnsNotFoundTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());
        when(session.getAttribute("managerActividad")).thenReturn(mock(MbActividad.class));

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void doGetWritesStagedImageWithItsMimeTypeTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());

        byte[] bytes = {1, 2, 3, 4};
        var bean = mock(MbActividad.class);
        when(bean.getMpImg()).thenReturn(new SerialBlob(bytes));
        when(bean.getMpMime()).thenReturn("image/jpeg");
        when(session.getAttribute("managerActividad")).thenReturn(bean);
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("image/jpeg");
        verify(response).setHeader("Cache-Control", "no-store");
        assertTrue(outputStream.toByteArray().length > 0);
    }

    @Test
    public void doGetFallsBackToDefaultMimeTypeWhenBlankTest() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.superAdmin());

        var bean = mock(MbActividad.class);
        when(bean.getMpImg()).thenReturn(new SerialBlob(new byte[]{1, 2, 3}));
        // mpMime left unset, as happens for a legacy activity whose row predates the calact_mime
        // column and has never been re-saved — the servlet falls back to PNG for those.
        when(session.getAttribute("managerActividad")).thenReturn(bean);
        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("image/png");
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
