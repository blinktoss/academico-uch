package net.uch.academica.jsf;

/**
 * Central grade policy for the Grades module (see {@code docs} - Grade Policy Rules):
 * <ul>
 *   <li>Allowed range: {@value #MIN_GRADE} to {@value #MAX_GRADE}.</li>
 *   <li>Precision: 0.5 increments only (a grade is valid when {@code grade * 2} is an integer).</li>
 *   <li>Intermediate calculations keep full decimal precision; only the final grade is reduced.</li>
 *   <li>Final grade uses <b>truncation</b> (toward zero), never rounding.</li>
 *   <li>Passing grade: {@value #PASSING_GRADE} (a final grade below it is {@link #STATUS_FAIL}).</li>
 * </ul>
 *
 * <p>This is the single source of truth so the same rules apply to grade registration, the
 * principal Acta and the substitute Acta, on both the frontend and the backend.
 */
public final class GradePolicy {

    public static final float MIN_GRADE = 0f;
    public static final float MAX_GRADE = 20f;
    public static final int PASSING_GRADE = 11;

    public static final String STATUS_PASS = "PASS";
    public static final String STATUS_FAIL = "FAIL";

    private static final double EPSILON = 1e-6;

    private GradePolicy() {
    }

    /**
     * @return {@code true} when {@code grade} is within range and lands on a 0.5 increment.
     */
    public static boolean isValid(float grade) {
        if (grade < MIN_GRADE || grade > MAX_GRADE) {
            return false;
        }
        double doubled = grade * 2.0;
        return Math.abs(doubled - Math.rint(doubled)) < EPSILON;
    }

    /**
     * Parses and validates a raw text grade.
     *
     * @return {@code true} when {@code text} is a number that satisfies {@link #isValid(float)}.
     */
    public static boolean isValid(String text) {
        if (text == null) {
            return false;
        }
        try {
            return isValid(Float.parseFloat(text.trim()));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Reduces a raw (decimal) calculated grade to the official final grade by truncation only.
     */
    public static int truncate(double rawGrade) {
        return (int) rawGrade;
    }

    public static boolean isPassing(int finalGrade) {
        return finalGrade >= PASSING_GRADE;
    }

    public static String status(int finalGrade) {
        return isPassing(finalGrade) ? STATUS_PASS : STATUS_FAIL;
    }
}
