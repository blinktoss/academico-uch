package net.uch.administrativa.dao;

import net.uch.SpringConfigurationTest;
import net.uch.common.ServiceFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Integration test for {@link HSPagoDAO#previewComprobanteNro(String, String)} (the form-open
 * preview), run against the development DB. Guards the BUG-03 property on the legacy max-query's
 * successor: the serie value — reachable from the user-bound field {@code MbPago.nMpNumSerie} —
 * is bound as a CHAR(3) procedure parameter, never concatenated into SQL, so a payload cannot
 * alter the query structure.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class HSPagoDAOTest extends SpringConfigurationTest {

    private HSPagoDAO daoPago() {
        return ServiceFinder.findBean("springHibernateDaoPago", HSPagoDAO.class);
    }

    @Test
    public void previewForUnknownSerieFallsBackToOne() {
        // No counter row and no voucher history: the read-only fallback mirrors the
        // allocator's seed formula, 0 + 1. Repeatable because previewing writes nothing.
        assertEquals("0000001", daoPago().previewComprobanteNro("ZZZ", "039001"));
    }

    @Test
    public void previewBindsQuoteBearingSerieAsData() {
        // Pre-BUG-03-fix, a quote in the serie altered the legacy max-query's structure. On the
        // procedure path it is bound as a CHAR(3) parameter: it matches no serie and simply
        // falls back to 1 — no broken SQL, no altered query.
        assertEquals("0000001", daoPago().previewComprobanteNro("ZZ'", "039001"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void previewRejectsOverlongPayloadAtTypeBoundary() {
        // Stronger than the legacy behavior: an injection-sized payload cannot even reach SQL —
        // the CHAR(3) procedure parameter rejects it before the procedure executes.
        daoPago().previewComprobanteNro("ZZ' or '1'='1", "039001");
    }
}
