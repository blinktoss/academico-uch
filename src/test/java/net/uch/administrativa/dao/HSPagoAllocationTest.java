package net.uch.administrativa.dao;

import net.uch.SpringConfigurationTest;
import net.uch.common.ServiceFinder;
import net.uch.mapping.AdComprobantePago;
import net.uch.util.HSTestSupportDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Integration test for the atomic voucher-number allocation (comprobante_correlativo) that
 * replaces the racy form-open preview. Uses a throwaway serie ("099") and a sentinel cliente so
 * it never touches real data; cleans both up afterwards.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class HSPagoAllocationTest extends SpringConfigurationTest {

    private static final String SERIE = "099";
    private static final String TIP_DOC = "039001";
    private static final int SENTINEL_CLIENTE = 9_999_999;

    @Autowired
    private HSTestSupportDao testSupportDao;

    private HSPagoDAO daoPago() {
        return ServiceFinder.findBean("springHibernateDaoPago", HSPagoDAO.class);
    }

    @Before
    public void seedSequence() {
        // Real series are eager-seeded by the V12 migration, so allocation hits the deadlock-free
        // UPDATE path. Seed the throwaway serie here to mirror that production state.
        testSupportDao.deleteComprobantesByCliente(SENTINEL_CLIENTE);
        testSupportDao.seedCorrelativo(SERIE, TIP_DOC, 0);
    }

    @After
    public void cleanUp() {
        testSupportDao.deleteComprobantesByCliente(SENTINEL_CLIENTE);
        testSupportDao.deleteCorrelativo(SERIE);
    }

    @Test
    public void allocatesSequentialNumbersOverridingPreview() {
        // Both start from the same stale preview "099-0000000"; allocation must override it.
        int id1 = daoPago().insertarComprobantePago(voucher("099-0000000"), true);
        int id2 = daoPago().insertarComprobantePago(voucher("099-0000000"), true);

        String n1 = daoPago().seleccionarUnComprobantePago(id1).getCompagNro();
        String n2 = daoPago().seleccionarUnComprobantePago(id2).getCompagNro();

        assertEquals("099-0000001", n1);
        assertEquals("099-0000002", n2);
    }

    @Test
    public void concurrentAllocationsNeverCollide() throws InterruptedException {
        int threads = 8;
        // Resolve the (thread-safe) DAO proxy on the main thread: ServiceFinder.findBean reads the
        // JSF FacesContext, which is null in worker threads.
        final HSPagoDAO dao = daoPago();
        var startSignal = new CountDownLatch(1);
        var done = new CountDownLatch(threads);
        List<String> numbers = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    startSignal.await();
                    int id = dao.insertarComprobantePago(voucher("099-0000000"), true);
                    numbers.add(dao.seleccionarUnComprobantePago(id).getCompagNro());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        startSignal.countDown();
        assertTrue("allocation threads did not finish", done.await(30, TimeUnit.SECONDS));

        assertEquals(threads, numbers.size());
        // No duplicates: the whole point of the fix.
        assertEquals(threads, numbers.stream().distinct().count());
        // Contiguous 1..threads, in some order.
        for (int i = 1; i <= threads; i++) {
            assertTrue("missing 099-" + String.format("%07d", i),
                    numbers.contains("099-" + String.format("%07d", i)));
        }
    }

    @Test
    public void valeNumberIsNotOverwritten() {
        // VALE (015004) numbers are the manually-entered physical document number and must be
        // persisted verbatim, not replaced by a sequence number.
        AdComprobantePago vale = voucher("099-7654321");
        vale.setCompagProcedencia("015004");
        int id = daoPago().insertarComprobantePago(vale, true);
        assertEquals("099-7654321", daoPago().seleccionarUnComprobantePago(id).getCompagNro());
        assertNotEquals(0, id);
    }

    @Test
    public void previewMatchesNextAllocatedNumberAndDoesNotAdvance() {
        // Two consecutive previews see the same value (read-only), and the allocation then
        // takes exactly the previewed number — what the form shows is what the save stamps.
        assertEquals("0000001", daoPago().previewComprobanteNro(SERIE, TIP_DOC));
        assertEquals("0000001", daoPago().previewComprobanteNro(SERIE, TIP_DOC));

        int id = daoPago().insertarComprobantePago(voucher("099-0000000"), true);
        assertEquals("099-0000001", daoPago().seleccionarUnComprobantePago(id).getCompagNro());

        assertEquals("0000002", daoPago().previewComprobanteNro(SERIE, TIP_DOC));
    }

    @Test
    public void previewForUnseededSerieAgreesWithFirstAllocation() {
        // No counter row: the preview's fallback must mirror the allocator's first-allocation
        // seed, so the form and the first save still agree.
        testSupportDao.deleteCorrelativo(SERIE);
        assertEquals("0000001", daoPago().previewComprobanteNro(SERIE, TIP_DOC));

        int id = daoPago().insertarComprobantePago(voucher("099-0000000"), true);
        assertEquals("099-0000001", daoPago().seleccionarUnComprobantePago(id).getCompagNro());
    }

    private AdComprobantePago voucher(String previewNro) {
        var c = new AdComprobantePago();
        c.setCompagTipo("037001");
        c.setCompagDes("");
        c.setCompagAlu(0);
        c.setCompagCliente(SENTINEL_CLIENTE);
        c.setCompagClienteTipo("014001");
        c.setCompagUsado("1");
        c.setCompagProcedencia("015002");
        c.setCompagCreacion(new Date());
        c.setCompagActivo("1");
        c.setCompagMonto(0f);
        c.setCompagFecha(new Date());
        c.setCompagNro(previewNro);
        c.setCompagTipDoc(TIP_DOC);
        c.setCompagVoucherFecha(new Date());
        c.setCompagVoucherCodAgencia("");
        c.setCompagVoucherNroOperacion("");
        c.setAdPagos(Collections.emptySet());
        return c;
    }
}
