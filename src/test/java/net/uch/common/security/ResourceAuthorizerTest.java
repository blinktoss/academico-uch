package net.uch.common.security;

import net.uch.Stubs;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ResourceAuthorizer}: the single role-based access rule shared by the
 * resource servlets.
 */
public class ResourceAuthorizerTest {

    @Test
    public void nullUserIsNeverAllowed() {
        assertFalse(ResourceAuthorizer.isAllowed(null, Modules.ALUMNOS));
    }

    @Test
    public void superAdminIsAllowedAnyModule() {
        assertTrue(ResourceAuthorizer.isAllowed(Stubs.superAdmin(), Modules.PROCESO_BANCO));
    }

    @Test
    public void userIsAllowedOnlyGrantedModules() {
        var user = Stubs.userWithModules(Modules.PAGOS, Modules.ALUMNOS_CL);

        assertTrue(ResourceAuthorizer.isAllowed(user, Modules.PAGOS));
        assertFalse(ResourceAuthorizer.isAllowed(user, Modules.ALUMNOS));
    }

    @Test
    public void enforceAllowsGrantedUserWithoutTouchingResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.NOTAS));

        boolean allowed = ResourceAuthorizer.enforce(request, response, Modules.NOTAS);

        assertTrue(allowed);
        verify(response, never()).sendError(anyInt());
    }

    @Test
    public void enforceSendsForbiddenAndReferrerPolicyWhenDenied() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(Stubs.userWithModules(Modules.PAGOS));

        boolean allowed = ResourceAuthorizer.enforce(request, response, Modules.ALUMNOS);

        assertFalse(allowed);
        verify(response).setHeader("Referrer-Policy", "no-referrer");
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void enforceWithoutSessionSendsForbidden() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getSession(false)).thenReturn(null);

        boolean allowed = ResourceAuthorizer.enforce(request, response, Modules.ALUMNOS);

        assertFalse(allowed);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
