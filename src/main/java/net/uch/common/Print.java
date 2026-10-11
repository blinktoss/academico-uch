package net.uch.common;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;

public class Print {

    private static final Logger LOG = LoggerFactory.getLogger(Print.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Fills a Jasper report from an already-resolved template stream and writes the PDF to {@code out}.
     * Carries no dependency on the JSF {@code FacesContext}, so it can be invoked from a plain servlet
     * (see {@code net.uch.common.web.ReportServlet}) as well as from JSF managed beans.
     *
     * @param out          stream the generated PDF is written to
     * @param reportStream compiled {@code .jasper} template stream
     * @param parameters   report parameters
     */
    public void generateReport(OutputStream out, InputStream reportStream, Map<String, Object> parameters) {
        try (var connection = dataSource.getConnection()) {
            var jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);

            var buffer = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, buffer);

            out.write(buffer.toByteArray());
            LOG.debug("Bytes written {}", buffer.size());

            buffer.close();
            out.flush();
        } catch (SQLException | JRException | IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Genera el buffer del reporte en PDF resolviendo la plantilla a través del contexto JSF.
     * <p>Legacy entry point still used by managed beans during the report migration; new callers
     * should resolve the template themselves and use
     * {@link #generateReport(OutputStream, InputStream, Map)}.
     *
     * @param out        buffer to write
     * @param jasper     report to be printed
     * @param parameters list of parameters
     */
    public void cargarReporte(OutputStream out, String jasper, Map<String, Object> parameters) {
        InputStream reportStream = FacesContext.getCurrentInstance()
                .getExternalContext().getResourceAsStream(jasper);
        generateReport(out, reportStream, parameters);
    }
}
