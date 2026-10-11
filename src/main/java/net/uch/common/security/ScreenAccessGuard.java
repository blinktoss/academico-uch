package net.uch.common.security;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.configuracion.dao.HSMenuDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

/**
 * Decides whether a logged-in user may open a JSF screen directly by URL — the screen-level
 * counterpart to {@link ResourceAuthorizer} (which guards the resource servlets).
 *
 * <p>The screen owning a view is found from {@code faces-config.xml}: a navigation {@code from-outcome}
 * is a {@code men_accion} module and its {@code to-view-id} is the screen. A view is "gated" when its
 * outcome is one of the role-assignable modules ({@link HSMenuDAO#findAllModules()}); a gated view is
 * allowed only if the user's role grants it (or the user is the god-admin). Views with no navigation
 * mapping, or whose outcome is not a gated module (login, welcome, shared pages), are always allowed.
 */
public final class ScreenAccessGuard {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenAccessGuard.class);

    private final Map<String, Set<String>> viewToOutcomes;
    private final Set<String> gatedOutcomes;

    ScreenAccessGuard(Map<String, Set<String>> viewToOutcomes, Set<String> gatedOutcomes) {
        this.viewToOutcomes = viewToOutcomes;
        this.gatedOutcomes = gatedOutcomes;
    }

    /**
     * Builds a guard from the application's {@code faces-config.xml} navigation rules and the gated
     * module universe.
     */
    public static ScreenAccessGuard load(ServletContext context, HSMenuDAO menuDao) {
        return new ScreenAccessGuard(parseFacesConfig(context), new HashSet<>(menuDao.findAllModules()));
    }

    private static Map<String, Set<String>> parseFacesConfig(ServletContext context) {
        Map<String, Set<String>> map = new HashMap<>();
        try (InputStream in = context.getResourceAsStream("/WEB-INF/faces-config.xml")) {
            if (in == null) {
                LOG.error("faces-config.xml not found; screen access guard will allow all views");
                return map;
            }
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            var document = factory.newDocumentBuilder().parse(in);

            NodeList cases = document.getElementsByTagName("navigation-case");
            for (int i = 0; i < cases.getLength(); i++) {
                var navCase = (Element) cases.item(i);
                String outcome = text(navCase, "from-outcome");
                String viewId = text(navCase, "to-view-id");
                if (outcome != null && viewId != null) {
                    map.computeIfAbsent(viewId, k -> new HashSet<>()).add(outcome);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to parse faces-config.xml; screen access guard will allow all views", e);
        }
        return map;
    }

    private static String text(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0) {
            return null;
        }
        String value = nodes.item(0).getTextContent();
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    /**
     * @param user   the logged-in user (non-null; the filter handles the anonymous case)
     * @param viewId the requested view id, e.g. {@code /Academica/Alumnos.jsp}
     * @return {@code true} if the user may open the screen
     */
    public boolean isAllowed(BeanUser user, String viewId) {
        Set<String> outcomes = viewToOutcomes.get(viewId);
        if (outcomes == null) {
            return true; // view has no navigation mapping (e.g. login, welcome)
        }
        Set<String> required = new HashSet<>(outcomes);
        required.retainAll(gatedOutcomes);
        if (required.isEmpty()) {
            return true; // not a role-gated screen
        }
        if (user.isSuperAdmin()) {
            return true;
        }
        return !Collections.disjoint(user.getGrantedModules(), required);
    }
}
