package net.uch.administrativa.service;

import net.uch.SpringConfigurationTest;
import net.uch.administrativa.dao.HSProrrogaDAO;
import net.uch.mapping.AdProrroga;
import net.uch.util.HSTestSupportDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link ProrrogaService} against the live dev DB. Uses tariff 37361 (three
 * historical active prorrogas, #1..#3, dated 2010) and cleans up every prorroga it registers so runs
 * do not accumulate ad_prorroga rows. Pins the issue-#33 rules: the commitment date must be after
 * today, the history number is auto-incremented, and the LATEST-registered extension becomes the
 * effective deadline (not the maximum date).
 *
 * @author cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ProrrogaServiceTest extends SpringConfigurationTest {

    private static final int ALUTAR_ID = 37361;
    private static final int USU_ID = 17;

    @Autowired
    private ProrrogaService service;

    @Autowired
    private HSTestSupportDao support;

    @Autowired
    @Qualifier("springHibernateDaoProrroga")
    private HSProrrogaDAO prorrogaDao;

    private int lastProIdBefore;
    private int activeCountBefore;

    private static Date sqlDate(LocalDate d) {
        return java.sql.Date.valueOf(d);
    }

    private static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Before
    public void setUp() {
        lastProIdBefore = support.maxProId(ALUTAR_ID);
        activeCountBefore = prorrogaDao.listaProrrogas(ALUTAR_ID).size();
    }

    @After
    public void tearDown() {
        support.cleanProrrogasCreatedDuringTest(ALUTAR_ID, lastProIdBefore);
    }

    @Test
    public void commitmentDateRuleTest() {
        assertFalse("null is invalid", service.isValidCommitmentDate(null));
        assertFalse("today is invalid (min is today+1)", service.isValidCommitmentDate(sqlDate(LocalDate.now())));
        assertFalse("a past date is invalid", service.isValidCommitmentDate(sqlDate(LocalDate.now().minusDays(1))));
        assertTrue("tomorrow is the minimum valid", service.isValidCommitmentDate(sqlDate(LocalDate.now().plusDays(1))));
        assertTrue("far future has no cap", service.isValidCommitmentDate(sqlDate(LocalDate.now().plusYears(5))));
    }

    @Test(expected = ProrrogaValidationException.class)
    public void rejectsTodayAsCommitmentTest() throws Exception {
        service.registerExtension(ALUTAR_ID, sqlDate(LocalDate.now()), "must fail", USU_ID);
    }

    @Test
    public void invalidDateWritesNothingTest() {
        Date deadlineBefore = support.getAlumnoTarifa(ALUTAR_ID).getAlutarFechaProrroga();
        try {
            service.registerExtension(ALUTAR_ID, sqlDate(LocalDate.now().minusDays(1)), "must fail", USU_ID);
            fail("expected ProrrogaValidationException");
        } catch (ProrrogaValidationException expected) {
            // expected: nothing must have been written
        }
        assertEquals(activeCountBefore, prorrogaDao.listaProrrogas(ALUTAR_ID).size());
        assertEquals(deadlineBefore, support.getAlumnoTarifa(ALUTAR_ID).getAlutarFechaProrroga());
    }

    @Test
    public void registersHistoryAndSyncsDeadlineTest() throws Exception {
        LocalDate future = LocalDate.now().plusDays(30);
        service.registerExtension(ALUTAR_ID, sqlDate(future), "commitment A", USU_ID);

        List<AdProrroga> history = prorrogaDao.listaProrrogas(ALUTAR_ID);
        assertEquals(activeCountBefore + 1, history.size());
        assertEquals(String.valueOf(activeCountBefore + 1), history.get(history.size() - 1).getProNumero());
        assertEquals(future, deadlineOf(ALUTAR_ID));
    }

    @Test
    public void latestRegisteredReplacesEarlierNotMaxTest() throws Exception {
        LocalDate later = LocalDate.now().plusDays(30);
        LocalDate earlier = LocalDate.now().plusDays(10); // still future, but earlier than 'later'

        service.registerExtension(ALUTAR_ID, sqlDate(later), "A", USU_ID);
        assertEquals(later, deadlineOf(ALUTAR_ID));

        service.registerExtension(ALUTAR_ID, sqlDate(earlier), "B replaces A", USU_ID);

        // Latest registered wins even though 'later' is the maximum date - the #33 rule that
        // supersedes #45's max().
        assertEquals(earlier, deadlineOf(ALUTAR_ID));
        assertEquals(activeCountBefore + 2, prorrogaDao.listaProrrogas(ALUTAR_ID).size());
    }

    private LocalDate deadlineOf(int alutarId) {
        return toLocalDate(support.getAlumnoTarifa(alutarId).getAlutarFechaProrroga());
    }
}
