package net.uch.administrativa.service;

import net.uch.SpringConfigurationTest;
import net.uch.common.service.PageResult;
import net.uch.mapping.AdAlumnoTarifa;
import net.uch.mapping.AdComprobantePago;
import net.uch.mapping.AdConceptoPago;
import net.uch.mapping.ClAlumnoTarifa;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link PaymentSearchService} against the live dev DB, using the same known
 * fixtures as {@code MbPagoTest}: a single-day external window with 42 comprobantes, and the
 * QUISP/ME internal filter with 12 tariffs. Pins the counts and the page windows.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PaymentSearchServiceTest extends SpringConfigurationTest {

    @Autowired
    private PaymentSearchService paymentSearchService;

    @Test
    public void externalSearchPagesCoverAllDistinctComprobantes() {
        Date day = Date.valueOf(LocalDate.of(2010, Month.MARCH, 1));
        int pageSize = 20;

        PageResult<AdComprobantePago> page1 = paymentSearchService.searchExternal(day, day, 0, 1, pageSize);
        assertEquals(42, page1.getTotalCount());
        assertEquals(3, page1.getTotalPages());
        assertEquals(20, page1.getItems().size());

        PageResult<AdComprobantePago> page2 = paymentSearchService.searchExternal(day, day, 0, 2, pageSize);
        assertEquals(20, page2.getItems().size());

        PageResult<AdComprobantePago> page3 = paymentSearchService.searchExternal(day, day, 0, 3, pageSize);
        assertEquals(2, page3.getItems().size());

        // The three pages together cover exactly the 42 distinct comprobantes, with no overlap.
        List<AdComprobantePago> all = new ArrayList<>();
        all.addAll(page1.getItems());
        all.addAll(page2.getItems());
        all.addAll(page3.getItems());
        Set<Integer> ids = new HashSet<>();
        for (AdComprobantePago c : all) {
            assertTrue("duplicate comprobante across pages: " + c.getId(), ids.add(c.getId()));
        }
        assertEquals(42, ids.size());
    }

    @Test
    public void internalSearchReturnsCountAndFirstPage() {
        InternalPaymentCriteria criteria = new InternalPaymentCriteria("", "-1", 0, 1, "QUISP", "ME", 0);
        PageResult<AdAlumnoTarifa> page = paymentSearchService.searchInternal(criteria, 1, 20);
        assertEquals(12, page.getTotalCount());
        assertEquals(1, page.getTotalPages());
        assertEquals(12, page.getItems().size());
    }

    @Test
    public void freeCourseSearchPagesAreConsistent() {
        // Surname filter is LIKE-ANYWHERE. Count is not hardcoded: CL workshop-enrollment tests in the
        // suite create RAMIREZ tariffs against this shared dev DB, so assert pagination *consistency*.
        FreeCourseCriteria criteria = new FreeCourseCriteria("", "-1", 0, "RAMIREZ", "");
        int pageSize = 20;

        PageResult<ClAlumnoTarifa> page1 = paymentSearchService.searchFreeCourse(criteria, 1, pageSize);
        long total = page1.getTotalCount();
        assertTrue("expected several pages of RAMIREZ CL tariffs", total > pageSize);
        assertEquals((int) Math.ceil((double) total / pageSize), page1.getTotalPages());
        assertEquals(pageSize, page1.getItems().size());

        // View paths must be fetched so the bean can read them after the tx closes.
        ClAlumnoTarifa first = page1.getItems().get(0);
        assertFalse(first.getClAlumno().getAluAppaterno().isEmpty());
        assertNotNull(first.getClEstructuraPagosDetalle().getClEstructuraPagos().getClModulo().getModDescripcion());

        // Walk every page: the sizes sum to the total and no tariff id repeats across pages.
        Set<Integer> ids = new HashSet<>();
        int seen = 0;
        for (int p = 1; p <= page1.getTotalPages(); p++) {
            List<ClAlumnoTarifa> items = paymentSearchService.searchFreeCourse(criteria, p, pageSize).getItems();
            seen += items.size();
            for (ClAlumnoTarifa t : items) {
                assertTrue("duplicate tariff across pages: " + t.getAlutarId(), ids.add(t.getAlutarId()));
            }
        }
        assertEquals(total, seen);
    }

    @Test
    public void searchableConceptsDependOnPaymentType() {
        // FREE COURSES (type 3) -> only the 15 Curso Libre (007003) concepts.
        List<AdConceptoPago> freeCourse = paymentSearchService.findSearchableConcepts(3);
        assertEquals(15, freeCourse.size());
        for (AdConceptoPago c : freeCourse) {
            assertEquals("007003", c.getConpagTipo());
        }

        // INTERNOS / EXTERNOS -> everything except Curso Libre (253 = 268 - 15), and the same list.
        List<AdConceptoPago> internal = paymentSearchService.findSearchableConcepts(1);
        assertEquals(253, internal.size());
        for (AdConceptoPago c : internal) {
            assertNotEquals("007003", c.getConpagTipo());
        }
        assertEquals(internal.size(), paymentSearchService.findSearchableConcepts(2).size());
    }

    @Test
    public void sumValidPaymentsByTarifaKeyedBySuppliedIds() {
        assertTrue(paymentSearchService.sumValidPaymentsByTarifa(Collections.emptyList()).isEmpty());

        InternalPaymentCriteria criteria = new InternalPaymentCriteria("", "-1", 0, 1, "QUISP", "ME", 0);
        List<Integer> tarifaIds = paymentSearchService.searchInternal(criteria, 1, 20).getItems()
                .stream().map(AdAlumnoTarifa::getId).collect(Collectors.toList());

        Map<Integer, Float> sums = paymentSearchService.sumValidPaymentsByTarifa(tarifaIds);
        for (Map.Entry<Integer, Float> entry : sums.entrySet()) {
            assertTrue("sum keyed by an un-requested tariff id", tarifaIds.contains(entry.getKey()));
            assertTrue(entry.getValue() >= 0f);
        }
    }
}
