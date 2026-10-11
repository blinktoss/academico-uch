package net.uch.common.audit;

import net.uch.autenticacion.jsf.beans.BeanUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Populates {@link CurrentUserContext} from the session-bound {@code BeanUser} for the
 * duration of the request, so {@link AuditInterceptor} can stamp created/updated-by
 * fields regardless of whether the request came through the JSF lifecycle or a plain
 * servlet.
 */
public class AuditUserFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var session = ((HttpServletRequest) request).getSession(false);
        if (session != null) {
            var usuario = (BeanUser) session.getAttribute("usuario");
            if (usuario != null) {
                CurrentUserContext.set(usuario.getRowUsuId());
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            CurrentUserContext.clear();
        }
    }

    @Override
    public void destroy() {
    }
}
