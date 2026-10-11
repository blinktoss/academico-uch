package net.uch.common.security;

import net.uch.Stubs;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ScreenAccessFilter}: 403 for a logged-in user lacking the screen's module,
 * pass-through for granted users and anonymous requests (auth handled downstream).
 */
public class ScreenAccessFilterTest {

    private ScreenAccessFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private FilterChain chain;

    @Before
    public void setUp() {
        var guard = new ScreenAccessGuard(
                singletonMap("/Academica/Alumnos.jsp", Set.of(Modules.ALUMNOS)),
                Set.of(Modules.ALUMNOS));
        filter = new ScreenAccessFilter(guard);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        chain = mock(FilterChain.class);
    }

    @Test
    public void loggedInUserWithoutModuleIsForbidden() throws Exception {
        when(request.getServletPath()).thenReturn("/Academica/Alumnos.jsf");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.PAGOS));

        filter.doFilter(request, response, chain);

        verify(response).setHeader("Referrer-Policy", "no-referrer");
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(chain);
    }

    @Test
    public void loggedInUserWithModuleProceeds() throws Exception {
        when(request.getServletPath()).thenReturn("/Academica/Alumnos.jsf");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.ALUMNOS));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt());
    }

    @Test
    public void anonymousRequestProceeds() throws Exception {
        when(request.getServletPath()).thenReturn("/Academica/Alumnos.jsf");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt());
    }
}
