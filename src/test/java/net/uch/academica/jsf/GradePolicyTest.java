package net.uch.academica.jsf;

import org.junit.Test;

import static org.junit.Assert.*;

public class GradePolicyTest {

    @Test
    public void validGradesWithinRangeAndHalfIncrementsTest() {
        for (float grade : new float[]{0f, 0.5f, 10f, 12.5f, 19.5f, 20f}) {
            assertTrue("expected valid: " + grade, GradePolicy.isValid(grade));
        }
    }

    @Test
    public void invalidGradesOutOfRangeTest() {
        assertFalse(GradePolicy.isValid(-1f));
        assertFalse(GradePolicy.isValid(20.5f));
        assertFalse(GradePolicy.isValid(21f));
    }

    @Test
    public void invalidGradesWrongPrecisionTest() {
        assertFalse(GradePolicy.isValid(10.25f));
        assertFalse(GradePolicy.isValid(12.7f));
        assertFalse(GradePolicy.isValid(18.75f));
    }

    @Test
    public void textParsingTest() {
        assertTrue(GradePolicy.isValid("0"));
        assertTrue(GradePolicy.isValid("12.5"));
        assertTrue(GradePolicy.isValid(" 20 "));
        assertFalse(GradePolicy.isValid("A"));
        assertFalse(GradePolicy.isValid("12A"));
        assertFalse(GradePolicy.isValid("10.25"));
        assertFalse(GradePolicy.isValid("21"));
        assertFalse(GradePolicy.isValid(""));
        assertFalse(GradePolicy.isValid(null));
    }

    @Test
    public void finalGradeTruncatesNeverRoundsTest() {
        assertEquals(12, GradePolicy.truncate(12.99));
        assertEquals(12, GradePolicy.truncate(12.57));
        assertEquals(13, GradePolicy.truncate(13.99));
        assertEquals(12, GradePolicy.truncate(12.857142));
    }

    @Test
    public void passingStatusUsesElevenTest() {
        assertFalse(GradePolicy.isPassing(10));
        assertTrue(GradePolicy.isPassing(11));
        assertEquals(GradePolicy.STATUS_FAIL, GradePolicy.status(10));
        assertEquals(GradePolicy.STATUS_PASS, GradePolicy.status(11));
        assertEquals(GradePolicy.STATUS_PASS, GradePolicy.status(20));
    }
}
