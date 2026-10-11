package net.uch.inicio.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 23/06/2026.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbDashboardTest extends SpringConfigurationTest {

    private MbDashboard manager;

    @Before
    public void setUp() {
        manager = new MbDashboard();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadIndicatorsTest() {
        assertNotNull(manager.getCurrentSemesterName());
        assertTrue("registered students should not be negative", manager.getRegisteredStudents() >= 0);
        assertTrue("enrolled students should not be negative", manager.getEnrolledStudents() >= 0);
    }

    @Test
    public void enrollmentRateMatchesCountsTest() {
        long registered = manager.getRegisteredStudents();
        long enrolled = manager.getEnrolledStudents();
        int rate = manager.getEnrollmentRate();

        if (registered == 0L) {
            assertEquals(0, rate);
        } else {
            assertEquals(Math.round((enrolled * 100f) / registered), rate);
        }
    }

    @Test
    public void formattedCountLabelsTest() {
        assertEquals(String.valueOf(manager.getRegisteredStudents()),
                manager.getRegisteredStudentsLabel().replace(",", ""));
        assertEquals(String.valueOf(manager.getEnrolledStudents()),
                manager.getEnrolledStudentsLabel().replace(",", ""));
    }
}
