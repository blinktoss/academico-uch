package net.uch.academica.jsf;

import net.uch.academica.jsf.WeightedGradeCalculator.GradeEntry;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WeightedGradeCalculatorTest {

    private static final double DELTA = 1e-9;

    @Test
    public void emptyEntriesGiveZeroTest() {
        assertEquals(0d, WeightedGradeCalculator.rawFinal(Arrays.asList()), DELTA);
    }

    @Test
    public void singleGroupEqualWeightsIsPlainAverageTest() {
        // one group, two components weight 1 each: (12 + 14) / 2 = 13
        List<GradeEntry> entries = Arrays.asList(
                new GradeEntry(12f, 1f, 1, 1f),
                new GradeEntry(14f, 1f, 1, 1f));
        assertEquals(13d, WeightedGradeCalculator.rawFinal(entries), DELTA);
    }

    @Test
    public void preservesPrecisionNoIntermediateRoundingTest() {
        // (10 + 11 + 13) / 3 = 11.333333... must NOT be rounded mid-calc
        List<GradeEntry> entries = Arrays.asList(
                new GradeEntry(10f, 1f, 1, 1f),
                new GradeEntry(11f, 1f, 1, 1f),
                new GradeEntry(13f, 1f, 1, 1f));
        assertEquals(34d / 3d, WeightedGradeCalculator.rawFinal(entries), DELTA);
    }

    @Test
    public void twoGroupsWeightedByGroupWeightTest() {
        // group A (weight 1): avg 10 ; group B (weight 3): avg 14
        // final = (10*1 + 14*3) / (1+3) = 52/4 = 13
        List<GradeEntry> entries = Arrays.asList(
                new GradeEntry(10f, 1f, 1, 1f),
                new GradeEntry(14f, 1f, 2, 3f));
        assertEquals(13d, WeightedGradeCalculator.rawFinal(entries), DELTA);
    }

    @Test
    public void componentWeightsWithinGroupTest() {
        // one group: (8*1 + 16*3) / (1+3) = 56/4 = 14
        List<GradeEntry> entries = Arrays.asList(
                new GradeEntry(8f, 1f, 1, 1f),
                new GradeEntry(16f, 3f, 1, 1f));
        assertEquals(14d, WeightedGradeCalculator.rawFinal(entries), DELTA);
    }
}
