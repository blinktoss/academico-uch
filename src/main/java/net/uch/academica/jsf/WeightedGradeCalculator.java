package net.uch.academica.jsf;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Weighted course-grade calculation shared by the principal Acta and the substitute Acta.
 *
 * <p>The course grade is a two-level weighted average: within each evaluation group
 * ({@code ac_sis_eva_detalle}) the components ({@code ac_sis_eva_personalizado}) are averaged by
 * their own weight, and the group averages are then combined by the group weight. Per the Grade
 * Policy Rules, all intermediate steps keep full {@code double} precision — only the caller reduces
 * the {@link #rawFinal raw} result to the official grade via {@link GradePolicy#truncate}.
 */
public final class WeightedGradeCalculator {

    private WeightedGradeCalculator() {
    }

    /**
     * @return each group's weighted average of its components, keyed by group id, in insertion
     * order. Full precision, no rounding.
     */
    public static Map<Integer, Double> groupAverages(List<GradeEntry> entries) {
        Map<Integer, double[]> accumulator = new LinkedHashMap<>();
        for (GradeEntry entry : entries) {
            double[] acc = accumulator.computeIfAbsent(entry.groupId, k -> new double[2]);
            acc[0] += (double) entry.grade * entry.componentWeight;
            acc[1] += entry.componentWeight;
        }
        Map<Integer, Double> averages = new LinkedHashMap<>();
        for (Map.Entry<Integer, double[]> group : accumulator.entrySet()) {
            double[] acc = group.getValue();
            averages.put(group.getKey(), acc[1] == 0 ? 0d : acc[0] / acc[1]);
        }
        return averages;
    }

    /**
     * @return the raw (decimal) weighted course grade, preserving full precision. {@code 0} when
     * there are no entries or all weights are zero.
     */
    public static double rawFinal(List<GradeEntry> entries) {
        Map<Integer, Float> groupWeights = new LinkedHashMap<>();
        for (GradeEntry entry : entries) {
            groupWeights.put(entry.groupId, entry.groupWeight);
        }
        double weightedSum = 0;
        double weightTotal = 0;
        for (Map.Entry<Integer, Double> group : groupAverages(entries).entrySet()) {
            float groupWeight = groupWeights.get(group.getKey());
            weightedSum += group.getValue() * groupWeight;
            weightTotal += groupWeight;
        }
        return weightTotal == 0 ? 0d : weightedSum / weightTotal;
    }

    /**
     * One graded evaluation component: its grade, its weight inside its group, and the group it
     * belongs to (group id + the group's own weight).
     */
    public static final class GradeEntry {
        private final float grade;
        private final float componentWeight;
        private final int groupId;
        private final float groupWeight;

        public GradeEntry(float grade, float componentWeight, int groupId, float groupWeight) {
            this.grade = grade;
            this.componentWeight = componentWeight;
            this.groupId = groupId;
            this.groupWeight = groupWeight;
        }

        public float getGrade() {
            return grade;
        }

        public int getGroupId() {
            return groupId;
        }

        /**
         * @return a copy of this entry with a different grade, keeping the same weights and group.
         */
        public GradeEntry withGrade(float newGrade) {
            return new GradeEntry(newGrade, componentWeight, groupId, groupWeight);
        }
    }
}
