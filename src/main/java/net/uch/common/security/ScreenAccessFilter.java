package net.uch.common.security;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.configuracion.dao.HSMenuDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Enforces role-based authorization on direct JSF screen requests ({@code *.jsf}) — closing the same
 * gap the resource servlets close, but for screens. A logged-in user who opens a screen URL their
 * role does not grant gets {@code 403}, rather than merely having the menu item hidden.
 *
 * <p>Authentication is left to the existing per-page session guard (the shared menu forwards anonymous
 * users to the login page); this filter only adds the authorization check for logged-in users.
 */
public class ScreenAccessFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenAccessFilter.class);

    private ServletContext servletContext;
    private volatile ScreenAccessGuard guard;

    public ScreenAccessFilter() {
    }

    /**
     * Test seam: inject a pre-built guard, bypassing lazy Spring/faces-config loading.
     */
    ScreenAccessFilter(ScreenAccessGuard guard) {
        this.guard = guard;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        var resp = (HttpServletResponse) response;

        var session = req.getSession(false);
        BeanUser user = session == null ? null : (BeanUser) session.getAttribute("usuario");

        // Anonymous access is handled downstream (the page's menu redirects to login); only authorize
        // logged-in users here.
        if (user != null && !guard().isAllowed(user, viewId(req))) {
            LOG.warn("Forbidden screen: user '{}' for view '{}'", user.getRowUsuId(), viewId(req));
            resp.setHeader("Referrer-Policy", "no-referrer");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * Maps the requested {@code *.jsf} path to its JSF view id ({@code *.jsp}).
     */
    private String viewId(HttpServletRequest req) {
        String path = req.getServletPath();
        return path.endsWith(".jsf") ? path.substring(0, path.length() - 4) + ".jsp" : path;
    }

    private ScreenAccessGuard guard() {
        ScreenAccessGuard local = guard;
        if (local == null) {
            synchronized (this) {
                local = guard;
                if (local == null) {
                    var menuDao = WebApplicationContextUtils.getWebApplicationContext(servletContext)
                            .getBean("springHibernateDaoMenu", HSMenuDAO.class);
                    local = ScreenAccessGuard.load(servletContext, menuDao);
                    guard = local;
                }
            }
        }
        return local;
    }

    @Override
    public void destroy() {
    }
}
