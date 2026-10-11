package net.uch.configuracion.web;

import net.uch.common.security.Modules;
import net.uch.common.security.ResourceAuthorizer;
import net.uch.configuracion.jsf.MbActividad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Streams the activity image currently staged on the session-scoped {@code managerActividad} bean
 * ({@link MbActividad#getMpImg()}), so the "Registro de Actividades" preview opens it as a plain
 * {@code <img>} URL instead of an {@code a4j:mediaOutput createContent} callback.
 *
 * <p>Unlike the other photo servlets ({@code StudentPhotoServlet}, {@code CursoLibrePhotoServlet}),
 * this one cannot query a DAO by id: the previewed image may be a freshly picked file that hasn't
 * been saved yet (no row exists), so the only source of truth is whatever the bean currently holds
 * in this user's session — covering both the "new/unsaved upload" and "editing an existing
 * activity" cases, since {@code MbActividad} loads the existing image into the same field.
 */
public class ActivityImageServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityImageServlet.class);

    private static final String DEFAULT_MIME_TYPE = "image/png";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ResourceAuthorizer.enforce(req, resp, Modules.ACTIVIDADES)) {
            return;
        }

        var session = req.getSession(false);
        var bean = (MbActividad) session.getAttribute("managerActividad");
        Blob image = bean == null ? null : bean.getMpImg();
        if (image == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        LOG.debug("Streaming staged activity image [{}]", bean.getMpMime());

        // The MIME type is persisted in ac_calendario_actividades.calact_mime and loaded into the
        // bean both on fresh upload (cargaImagen) and when editing an existing activity, so it's
        // normally populated. It can still be blank for legacy rows created before that column
        // existed and never re-saved; PNG is a last-resort fallback for those.
        String mimeType = bean.getMpMime();
        resp.setContentType((mimeType == null || mimeType.isEmpty()) ? DEFAULT_MIME_TYPE : mimeType);
        resp.setHeader("Cache-Control", "no-store");

        try {
            int length = (int) image.length();
            resp.getOutputStream().write(image.getBytes(1, length));
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
