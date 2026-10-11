package net.uch.academica.web;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.common.Constants;
import net.uch.common.security.Modules;
import net.uch.common.security.ResourceAuthorizer;
import net.uch.mapping.AcAlumno;
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
 * Streams a student's photo ({@code AcAlumno.aluFoto}) as a cacheable PNG.
 * Replaces the {@code a4j:mediaOutput createContent} callbacks, which re-ran
 * the DB lookup and BLOB-to-PNG conversion on every render.
 */
public class StudentPhotoServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(StudentPhotoServlet.class);

    private final AtomicLong invocationCount = new AtomicLong();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("Invocation #{} for student id '{}'", invocationCount.incrementAndGet(), req.getParameter("id"));

        if (!ResourceAuthorizer.enforce(req, resp, Modules.ALUMNOS)) {
            return;
        }

        int aluId;
        try {
            aluId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        var etag = "\"alu-foto-" + aluId + "\"";
        if (etag.equals(req.getHeader("If-None-Match"))) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        Blob foto = null;
        if (aluId != 0) {
            var dao = WebApplicationContextUtils.getWebApplicationContext(getServletContext())
                    .getBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
            AcAlumno alumno = dao.seleccionarUnAlumno(aluId);
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
