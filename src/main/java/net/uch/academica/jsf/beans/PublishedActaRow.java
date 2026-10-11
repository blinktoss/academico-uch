package net.uch.academica.jsf.beans;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Per-student computed result for a principal Acta: the component grades that feed the average,
 * plus the raw (decimal) course grade, the official truncated final, and the PASS/FAIL status.
 *
 * <p>Separates the persisted/auditable acta data from the display grid ({@code BeanNotas}). It is
 * built once while the grid is laid out and consumed by {@code insertarActa} to store one
 * PROMEDIO_PARCIAL detail per component plus a single PROMEDIO_FINAL detail carrying the raw grade
 * and status.
 */
public class PublishedActaRow {

    /**
     * component (siseva_personalizado) id -> grade.
     */
    private final Map<Integer, Float> componentGrades = new LinkedHashMap<>();
    private int studentId;
    private double rawFinal;
    private int finalGrade;
    private String status;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Map<Integer, Float> getComponentGrades() {
        return componentGrades;
    }

    public void addComponent(int componentId, float grade) {
        componentGrades.put(componentId, grade);
    }

    public double getRawFinal() {
        return rawFinal;
    }

    public void setRawFinal(double rawFinal) {
        this.rawFinal = rawFinal;
    }

    public int getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(int finalGrade) {
        this.finalGrade = finalGrade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
