package net.uch.common.security;

import net.uch.Stubs;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ScreenAccessGuard}: the screen-level authorization decision and the
 * faces-config parsing that backs it.
 */
public class ScreenAccessGuardTest {

    private ScreenAccessGuard guard(Map<String, Set<String>> viewToOutcomes, Set<String> gated) {
        return new ScreenAccessGuard(viewToOutcomes, gated);
    }

    @Test
    public void unmappedViewIsAllowed() {
        var guard = guard(Map.of(), Set.of(Modules.ALUMNOS));
        assertTrue(guard.isAllowed(Stubs.userWithModules(), "/Autenticacion/Bienvenida.jsp"));
    }

    @Test
    public void ungatedViewIsAllowed() {
        // view maps to an outcome that is not a role-assignable module
        var guard = guard(singletonMap("/Autenticacion/Login.jsp", Set.of("index")), Set.of(Modules.ALUMNOS));
        assertTrue(guard.isAllowed(Stubs.userWithModules(), "/Autenticacion/Login.jsp"));
    }

    @Test
    public void gatedViewDeniedWithoutGrant() {
        var guard = guard(singletonMap("/Academica/Alumnos.jsp", Set.of(Modules.ALUMNOS)), Set.of(Modules.ALUMNOS, Modules.PAGOS));
        assertFalse(guard.isAllowed(Stubs.userWithModules(Modules.PAGOS), "/Academica/Alumnos.jsp"));
    }

    @Test
    public void gatedViewAllowedWithGrant() {
        var guard = guard(singletonMap("/Academica/Alumnos.jsp", Set.of(Modules.ALUMNOS)), Set.of(Modules.ALUMNOS));
        assertTrue(guard.isAllowed(Stubs.userWithModules(Modules.ALUMNOS), "/Academica/Alumnos.jsp"));
    }

    @Test
    public void superAdminAllowedOnGatedView() {
        var guard = guard(singletonMap("/Academica/Alumnos.jsp", Set.of(Modules.ALUMNOS)), Set.of(Modules.ALUMNOS));
        assertTrue(guard.isAllowed(Stubs.superAdmin(), "/Academica/Alumnos.jsp"));
    }

    @Test
    public void loadParsesFacesConfigNavigationCases() throws Exception {
        String facesConfig = "<?xml version='1.0'?><faces-config><navigation-rule>"
                + "<navigation-case><from-outcome>Alumnos</from-outcome><to-view-id>/Academica/Alumnos.jsp</to-view-id></navigation-case>"
                + "<navigation-case><from-outcome>index</from-outcome><to-view-id>/Autenticacion/Login.jsp</to-view-id></navigation-case>"
                + "</navigation-rule></faces-config>";
        ServletContext context = mock(ServletContext.class);
        when(context.getResourceAsStream("/WEB-INF/faces-config.xml"))
                .thenReturn(new ByteArrayInputStream(facesConfig.getBytes(StandardCharsets.UTF_8)));

        var menuDao = mock(net.uch.configuracion.dao.HSMenuDAO.class);
        when(menuDao.findAllModules()).thenReturn(Set.of(Modules.ALUMNOS));

        ScreenAccessGuard guard = ScreenAccessGuard.load(context, menuDao);

        // Alumnos is gated -> a user without it is denied; Login maps to a non-gated outcome -> allowed.
        assertFalse(guard.isAllowed(Stubs.userWithModules(), "/Academica/Alumnos.jsp"));
        assertTrue(guard.isAllowed(Stubs.userWithModules(), "/Autenticacion/Login.jsp"));
    }
}
