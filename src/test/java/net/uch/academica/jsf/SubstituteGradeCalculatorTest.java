package net.uch.academica.jsf;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SubstituteGradeCalculatorTest {

    private static final float DELTA = 0.0001f;

    @Test
    public void n0_emptyArrayTest() {
        float[] grades = {};

        assertEquals(-1, SubstituteGradeCalculator.lowestGradeIndex(grades));
        assertArrayEquals(new float[]{}, SubstituteGradeCalculator.replaceLowestGrade(grades, 15f), DELTA);
        assertEquals(0f, SubstituteGradeCalculator.average(grades), DELTA);
    }

    @Test
    public void n1_singleGradeTest() {
        float[] grades = {12f};

        assertEquals(0, SubstituteGradeCalculator.lowestGradeIndex(grades));
        assertArrayEquals(new float[]{18f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 18f), DELTA);
    }

    @Test
    public void n2_twoGradesTest() {
        float[] grades = {14f, 9f};

        assertEquals(1, SubstituteGradeCalculator.lowestGradeIndex(grades));
        assertArrayEquals(new float[]{14f, 17f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 17f), DELTA);
    }

    @Test
    public void nX_generalCaseWithFourGradesTest() {
        // PA-1, PA-2, EP, EF style spread - lowest is EP at index 2
        float[] grades = {15f, 14f, 8f, 16f};

        assertEquals(2, SubstituteGradeCalculator.lowestGradeIndex(grades));
        assertArrayEquals(new float[]{15f, 14f, 19f, 16f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 19f), DELTA);
    }

    @Test
    public void allGradesEqualTest() {
        // Tie across every slot - first occurrence wins
        float[] grades = {13f, 13f, 13f, 13f};

        assertEquals(0, SubstituteGradeCalculator.lowestGradeIndex(grades));
        assertArrayEquals(new float[]{20f, 13f, 13f, 13f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 20f), DELTA);
    }

    @Test
    public void allGradesAtTheHighestTest() {
        float[] grades = {20f, 20f, 20f};

        assertEquals(0, SubstituteGradeCalculator.lowestGradeIndex(grades));
        assertArrayEquals(new float[]{11f, 20f, 20f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 11f), DELTA);
    }

    @Test
    public void allGradesAtTheLowestTest() {
        float[] grades = {0f, 0f, 0f};

        assertEquals(0, SubstituteGradeCalculator.lowestGradeIndex(grades));
        assertArrayEquals(new float[]{15f, 0f, 0f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 15f), DELTA);
    }

    @Test
    public void mixedGradesTest() {
        // Cesar's worked example: 10,11,12,13 with a substitute grade of 9 -> 9,11,12,13
        float[] grades = {10f, 11f, 12f, 13f};

        assertArrayEquals(new float[]{9f, 11f, 12f, 13f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 9f), DELTA);
    }

    @Test
    public void substituteWorseThanOriginalLowestStillReplacesItTest() {
        // Per the agreed rule: replacement is unconditional, even if the new score is itself lower
        float[] grades = {10f, 11f, 12f, 13f};

        assertArrayEquals(new float[]{5f, 11f, 12f, 13f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 5f), DELTA);
    }

    @Test
    public void lowestGradeAtLastPositionTest() {
        float[] grades = {15f, 12f, 8f};

        assertEquals(2, SubstituteGradeCalculator.lowestGradeIndex(grades));
        assertArrayEquals(new float[]{15f, 12f, 20f}, SubstituteGradeCalculator.replaceLowestGrade(grades, 20f), DELTA);
    }

    @Test
    public void averageOfAdjustedGradesTest() {
        float[] grades = {10f, 11f, 12f, 13f};
        float[] adjusted = SubstituteGradeCalculator.replaceLowestGrade(grades, 9f);

        assertEquals((9f + 11f + 12f + 13f) / 4, SubstituteGradeCalculator.average(adjusted), DELTA);
    }
}
