package net.uch.common;

import net.uch.administrativa.dao.HSPagoDAO;
import net.uch.administrativa.jsf.beans.NroSerie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created for sistema_academico on 08/01/2024.
 *
 * @author Cesardl
 */
public class SystemHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SystemHelper.class);

    private final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

    public Date parse(String source) {
        try {
            return sdf.parse(source);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato incorrecto de Fechaxx", e);
        }
    }

    public String format(Date date) {
        return sdf.format(date);
    }

    public String aDate(Date date) {
        return "'" + sdf.format(date) + "'";
    }

    public float aFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public long parseHourMinutes(String source) {
        try {
            var df = new SimpleDateFormat("hh:mm a");
            LOG.debug("Parsing {}", source);
            var date = df.parse(source);
            return date.getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato incorrecto", e);
        }
    }

    public LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Date convertToDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public Date convertToDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    /**
     * Non-binding preview of the next voucher number for (serie, tip doc): the value the
     * commit-time allocation (sp_allocate_voucher_number) will stamp, read from the shared
     * counter so sistema_academico and sigu_caja always display the same number. A concurrent
     * save may take it — the stamp at commit remains authoritative.
     */
    public NroSerie serie(String serie, String tipDoc) {
        HSPagoDAO daoPago = ServiceFinder.findBean("springHibernateDaoPago", HSPagoDAO.class);
        var n = new NroSerie();
        n.setType(serie);
        n.setSerie(daoPago.previewComprobanteNro(serie, tipDoc));
        LOG.trace("Voucher preview: {}-{}", n.getType(), n.getSerie());
        return n;
    }
}
