package net.uch.administrativa.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.util.HSTestSupportDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * CHARACTERIZATION TEST — Voucher annulment vs. tariff status (critical flow #4: payments).
 *
 * <p>Pins the CURRENT behavior of {@code MbPago.cambiarEstados(compagId)} — the status handler
 * shared by {@code anularPago} and {@code eliminarPago}: every tariff referenced by the voucher's
 * payments is reset to <b>"0" (DEBE) unconditionally</b>.
 *
 * <p>SUSPICIOUS BEHAVIOR (captured as-is, flagged NOT fixed): this reset does not re-derive the
 * status from the remaining valid payments the way the create/settle path does
 * ({@code deriveEstadoTarifa}, mora-aware paid-vs-owed — and sigu_caja, the suite's declared rule
 * of record, re-derives). A tariff partially covered by a SECOND, still-valid voucher is
 * nevertheless flipped back to DEBE when THIS voucher is annulled. Whether reset-to-DEBE is
 * intended or a divergence is an open SME question from the 2026-07-18 rules-extraction delta
 * (item b14).
 *
 * <p>Fixture discipline: uses the first real voucher in the dev DB whose payments reference
 * student tariffs; snapshots every affected tariff's estado and restores it afterwards.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AnnulVoucherEstadoCharacterizationTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private final Map<Integer, String> estadoSnapshot = new HashMap<>();
    private MbPago manager;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        loggerUser.setRowUsuId(17);
        loggerUser.setNivel("0");
        when(externalContext.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(loggerUser);

        manager = new MbPago();
        manager.init();
    }

    @After
    public void restoreEstados() {
        estadoSnapshot.forEach((alutarId, estado) ->
                DAO.updateAlumnoTarifaEstado(List.of(alutarId), estado == null ? "0" : estado));
    }

    @Test
    public void cambiarEstadosResetsEveryTariffOnTheVoucherToDebeUnconditionallyTest() {
        int compagId = DAO.findFirstComprobanteIdWithTariffPagos();
        List<Integer> alutarIds = DAO.findAlutarIdsForComprobante(compagId);
        assertFalse("fixture: the voucher must reference at least one tariff", alutarIds.isEmpty());

        // Snapshot, then force every referenced tariff to PAGO_TOTAL ("2") — the strongest
        // possible pre-state, as if fully covered (possibly by OTHER, still-valid vouchers).
        for (int alutarId : alutarIds) {
            estadoSnapshot.put(alutarId, DAO.getAlumnoTarifa(alutarId).getAlutarEstado());
        }
        DAO.updateAlumnoTarifaEstado(alutarIds, "2");

        manager.cambiarEstados(compagId);

        // CURRENT BEHAVIOR: blanket reset to DEBE — no re-derivation from remaining payments.
        for (int alutarId : alutarIds) {
            assertEquals("tariff " + alutarId + " must be reset to DEBE regardless of other payments",
                    "0", DAO.getAlumnoTarifa(alutarId).getAlutarEstado());
        }
    }
}
