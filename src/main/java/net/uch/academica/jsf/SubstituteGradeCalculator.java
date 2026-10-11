package net.uch.academica.jsf;

/**
 * Substitute exam ("Sustitutorio") replacement rule (common practice in Peru, confirmed by the
 * maintainer): the substitute score unconditionally replaces the single lowest of the student's
 * original grades, regardless of that component's weight, and even if the new score is itself
 * lower than what it replaces. Ties are broken by first occurrence.
 */
public final class SubstituteGradeCalculator {

    private SubstituteGradeCalculator() {
    }

    public static int lowestGradeIndex(float[] grades) {
        if (grades.length == 0) {
            return -1;
        }
        var lowestIndex = 0;
        for (var i = 1; i < grades.length; i++) {
            if (grades[i] < grades[lowestIndex]) {
                lowestIndex = i;
            }
        }
        return lowestIndex;
    }

    public static float[] replaceLowestGrade(float[] grades, float substituteGrade) {
        if (grades.length == 0) {
            return grades.clone();
        }
        var result = grades.clone();
        result[lowestGradeIndex(grades)] = substituteGrade;
        return result;
    }

    public static float average(float[] grades) {
        if (grades.length == 0) {
            return 0f;
        }
        var sum = 0f;
        for (float grade : grades) {
            sum += grade;
        }
        return sum / grades.length;
    }
}
