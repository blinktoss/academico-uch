package net.uch.administrativa.web;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.administrativa.dao.HSClienteDAO;
import net.uch.administrativa.dao.HSPagoDAO;
import net.uch.common.SystemHelper;
import net.uch.common.security.Modules;
import net.uch.common.security.ResourceAuthorizer;
import net.uch.mapping.AcAlumno;
import net.uch.mapping.AdAlumnoTarifa;
import net.uch.mapping.AdComprobantePago;
import net.uch.mapping.AdPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static net.uch.common.Constants.CAT_TIPO_CLIENTE_ALUMNO_CP;

/**
 * Streams the printable payment-receipt (comprobante) page so the browser opens it directly in a
 * new tab and triggers the print dialog via the page's own {@code onload} script.
 *
 * <p>Replaces the legacy {@code MbPago.mandarComprobante}, which built this same HTML server-side
 * inside a JSF managed bean and shipped it to the client as a {@code window.open(...) +
 * document.write(...)} JavaScript blob. That mechanism is gone; the bean now just opens this URL.
 */
public class ComprobanteServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ComprobanteServlet.class);

    private static final String MARK_START_PRE = "<pre>";
    private static final String MARK_END_PRE = "</pre>";
    private static final String MARK_END_DIV = "</div>";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.debug("Building comprobante for id '{}'", req.getParameter("id"));

        if (!ResourceAuthorizer.enforce(req, resp, Modules.PAGOS)) {
            return;
        }

        int comprobanteId;
        try {
            comprobanteId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        var context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        var daoPago = context.getBean("springHibernateDaoPago", HSPagoDAO.class);

        AdComprobantePago compag = daoPago.seleccionarUnComprobantePago(comprobanteId);
        if (compag == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        var daoAlumno = context.getBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
        var daoCliente = context.getBean("springHibernateDaoCliente", HSClienteDAO.class);
        var daoAlumnoTarifa = context.getBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class);
        var helper = context.getBean("helper", SystemHelper.class);

        String html = buildHtml(compag, daoAlumno, daoCliente, daoAlumnoTarifa, helper);

        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write(html);
    }

    private String buildHtml(AdComprobantePago compag, HSAlumnoDAO daoAlumno, HSClienteDAO daoCliente,
                             HSAlumnoTarifaDAO daoAlumnoTarifa, SystemHelper helper) {
        var decimalFormat = new DecimalFormat("#,#00.00#;(-#,#00.00#)");

        String fecha = helper.format(compag.getCompagFecha());
        float total = 0;
        String nombre;
        var anho = fecha.substring(0, fecha.indexOf('-'));
        var fecha2 = fecha.substring((fecha.indexOf('-') + 1));
        var mes = fecha2.substring(0, fecha2.indexOf('-'));
        var dia = fecha2.substring((fecha2.indexOf('-') + 1));

        Set<AdPago> pagos = compag.getAdPagos();
        List<AdPago> listPagos = Collections.synchronizedList(new LinkedList<>(pagos));

        if (compag.getCompagClienteTipo().equals(CAT_TIPO_CLIENTE_ALUMNO_CP)) {
            AcAlumno alu = daoAlumno.seleccionarUnAlumno(compag.getCompagCliente());
            nombre = alu.getAluCodigo() + " - "
                    + alu.getAluAppaterno() + " "
                    + alu.getAluApmaterno() + "  "
                    + alu.getAluNombres();
        } else {
            nombre = daoCliente.seleccionarUnCliente(compag.getCompagCliente()).getCliRazonSocial();
        }

        var html = new StringBuilder("<html>"
                + "<head>"
                + "<title></title>"
                + "<style type=\"text/css\" >"
                + ".fuente"
                + "{"
                + "font-family:sans-serif;"
                + "font-size:13px;"
                + "font-weight:bold;"
                + "}"
                + ".estilo_matcod"
                + "{"
                + "font-family: sans-serif;"
                + "font-size:13px;"
                + "font-weight:bold;"
                + "}"
                + "</style>"
                + "<script type=\"text/javascript\">"
                + "function hacerPrint()"
                + "{"
                + "print();"
                + "}"
                + "</script>"
                + "</head>"
                + "<body onload=\"hacerPrint();\" leftmargin=\"0\" topmargin=\"3\">"
                + "<div class=\"fuente\" style=\" position:absolute; left:10px; top:67px;\" >"
                + MARK_START_PRE + dia + MARK_END_PRE
                + MARK_END_DIV
                + "<div class=\"fuente\" style=\" position:absolute; left:80px; top:67px;\" >"
                + MARK_START_PRE + mes + MARK_END_PRE
                + MARK_END_DIV
                + "<div class=\"fuente\" style=\" position:absolute; left:168px; top:67px;\" >"
                + MARK_START_PRE + anho + MARK_END_PRE
                + MARK_END_DIV
                + "<div class=\"fuente\" style=\" position:absolute; left:75px; top:85px; \">"
                + MARK_START_PRE
                + "" + nombre + ""
                + MARK_END_PRE
                + MARK_END_DIV
                + "<div class=\"fuente\" style=\" position:absolute; left:320px; top:100px; text-transform: lowercase\">"
                + MARK_START_PRE
                + "" + compag.getCompagNro() + ""
                + MARK_END_PRE
                + MARK_END_DIV
                + "<div class=\"fuente\" style=\" position:absolute; left:10px; top:150px;\">");
        for (AdPago pago : listPagos) {
            if (pago.getConpag().getConpagTipo().equals("007001")) {
                AdAlumnoTarifa tarifa = daoAlumnoTarifa.seleccionarUnAlumnoTarifa(pago.getAlutarId());
                if (tarifa.getAlutarEstado().equals("2")) {
                    html.append(MARK_START_PRE).append(tarifa.getEstpagdet().getEstpagdetNombre()).append(" (CANCELADO)").append(MARK_END_PRE);
                } else {
                    html.append(MARK_START_PRE).append(tarifa.getEstpagdet().getEstpagdetNombre()).append(" (A CUENTA)").append(MARK_END_PRE);
                }
            } else {
                html.append(MARK_START_PRE).append(pago.getConpag().getConpagDescripcion()).append(MARK_END_PRE);
            }
        }
        html.append(MARK_END_DIV).append("<div class=\"fuente\" style=\" position:absolute; left:428px; top:154px;\">");
        for (AdPago pago : listPagos) {
            total = total + pago.getPagMonto();
            html.append(MARK_START_PRE).append(decimalFormat.format(pago.getPagMonto())).append(MARK_END_PRE);
        }
        html.append(MARK_END_DIV).append("<div class=\"fuente\" style=\"position:absolute; left:428px; top:260px\">").append(MARK_START_PRE).append(decimalFormat.format(total)).append(MARK_END_PRE).append(MARK_END_DIV).append("</body>").append("</html>");

        return html.toString();
    }
}
