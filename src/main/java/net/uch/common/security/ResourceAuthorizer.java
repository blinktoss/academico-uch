package net.uch.common.security;

import net.uch.autenticacion.jsf.beans.BeanUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Centralizes role-based access control for the resource servlets ({@code /report}, {@code /foto},
 * {@code /comprobante}, …), which previously only checked that <em>some</em> user was logged in.
 *
 * <p>A request is allowed when the logged-in {@link BeanUser} is the god-admin, or its role has been
 * granted the module ({@code men_accion}) that owns the requested resource (see {@link Modules}).
 * Otherwise the response is {@code 403 Forbidden}. This collapses the per-servlet
 * {@code session == null || usuario == null} checks into one rule so the policy lives in a single
 * place.
 */
public final class ResourceAuthorizer {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceAuthorizer.class);

    private ResourceAuthorizer() {
    }

    /**
     * @param user           the logged-in user, or {@code null} if there is no session/user
     * @param requiredModule the {@code men_accion} module that owns the requested resource
     * @return {@code true} if the user may access a resource owned by {@code requiredModule}
     */
    public static boolean isAllowed(BeanUser user, String requiredModule) {
        if (user == null) {
            return false;
        }
        if (user.isSuperAdmin()) {
            return true;
        }
        return user.getGrantedModules().contains(requiredModule);
    }

    /**
     * Authorizes the request for {@code requiredModule}. On failure, sets {@code Referrer-Policy:
     * no-referrer} (so the resource URL is not leaked onward) and sends {@code 403}.
     *
     * @return {@code true} if the caller may proceed; {@code false} if a {@code 403} was already sent
     */
    public static boolean enforce(HttpServletRequest req, HttpServletResponse resp, String requiredModule)
            throws IOException {
        var session = req.getSession(false);
        BeanUser user = session == null ? null : (BeanUser) session.getAttribute("usuario");
        if (isAllowed(user, requiredModule)) {
            return true;
        }
        LOG.warn("Forbidden: user '{}' lacks module '{}'", user == null ? "<none>" : user.getRowUsuId(), requiredModule);
        resp.setHeader("Referrer-Policy", "no-referrer");
        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
