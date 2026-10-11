package net.uch.cursolibre.web;

import net.uch.common.Constants;
import net.uch.common.security.Modules;
import net.uch.common.security.ResourceAuthorizer;
import net.uch.cursolibre.dao.HSAlumnoCLDAO;
import net.uch.mapping.ClAlumno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Streams a free-course student's photo ({@code ClAlumno.aluFoto}) as a cacheable PNG.
 * Serves the default avatar for id=0 (no student selected yet).
 */
public class CursoLibrePhotoServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CursoLibrePhotoServlet.class);

    private final AtomicLong invocationCount = new AtomicLong();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("Invocation #{} for CL student id '{}'", invocationCount.incrementAndGet(), req.getParameter("id"));

        if (!ResourceAuthorizer.enforce(req, resp, Modules.ALUMNOS_CL)) {
            return;
        }

        int aluId;
        try {
            aluId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        var etag = "\"cl-foto-" + aluId + "\"";
        if (etag.equals(req.getHeader("If-None-Match"))) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        Blob foto = null;
        if (aluId != 0) {
            var dao = WebApplicationContextUtils.getWebApplicationContext(getServletContext())
                    .getBean("springHibernateDaoCLAlumno", HSAlumnoCLDAO.class);
            ClAlumno alumno = dao.buscarAlumnoPorAluId(aluId);
            foto = alumno == null ? null : alumno.getAluFoto();
        }

        resp.setContentType("image/png");
        resp.setHeader("ETag", etag);
        resp.setHeader("Cache-Control", "private, max-age=86400");

        try {
            InputStream imageStream = foto != null
                    ? foto.getBinaryStream()
                    : getServletContext().getResourceAsStream(Constants.ACTION_IMAGES_USER_INFO);
            ImageIO.write(ImageIO.read(imageStream), "png", resp.getOutputStream());
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
