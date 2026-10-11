package net.uch.common.web;

import net.uch.common.Print;
import net.uch.common.security.ResourceAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Streams server-generated PDF reports so the browser opens them in a new tab.
 *
 * <p>Replaces the per-screen {@code a4j:mediaOutput createContent} callbacks, which generated the
 * report inside the JSF resource lifecycle — coupling it to session-scoped mutable state, firing
 * twice on modal show/hide, and re-running Jasper plus a DB connection on every render.
 *
 * <p>URL contract: {@code GET /report?type=<key>&<report-specific params>}. See {@link ReportType}
 * for the available reports and their parameters.
 */
public class ReportServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ReportServlet.class);

    private static final long serialVersionUID = 981281077478492328L;

    private static final String PRINTER_BEAN = "printer";

    private final AtomicLong invocationCount = new AtomicLong();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String type = req.getParameter("type");
        LOG.debug("Invocation #{} for report '{}'", invocationCount.incrementAndGet(), type);

        Optional<ReportType> report = ReportType.fromKey(type);
        if (report.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown report type");
            return;
        }
        ReportType reportType = report.get();

        if (!ResourceAuthorizer.enforce(req, resp, reportType.requiredModule())) {
            return;
        }

        Map<String, Object> parameters;
        String jasperPath;
        try {
            parameters = reportType.buildParameters(req, getServletContext());
            jasperPath = reportType.resolveJasperPath(req);
        } catch (IllegalArgumentException e) {
            LOG.warn("Bad request for report '{}': {}", type, e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        InputStream template = getServletContext().getResourceAsStream(jasperPath);
        if (template == null) {
            LOG.error("Report template not found: {}", jasperPath);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=\"" + reportType.fileName() + ".pdf\"");
        resp.setHeader("Cache-Control", reportType.isCacheable() ? "private, max-age=86400" : "no-store");

        var printer = WebApplicationContextUtils.getWebApplicationContext(getServletContext())
                .getBean(PRINTER_BEAN, Print.class);
        printer.generateReport(resp.getOutputStream(), template, parameters);
    }
}
