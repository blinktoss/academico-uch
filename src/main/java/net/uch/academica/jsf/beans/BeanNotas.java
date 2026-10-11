package net.uch.academica.jsf.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for sistema_academico on 07/09/2024.
 *
 * @author Cesardl
 */
public class BeanNotas {

    private int studentId;
    private String student;
    private List<BeanNotas> evaluations;
    private int sisEvaluationId;
    private int gradeId;
    private String gradeValue;
    private String style;

    public BeanNotas() {
        evaluations = new ArrayList<>();
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public List<BeanNotas> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<BeanNotas> evaluations) {
        this.evaluations = evaluations;
    }

    public int getSisEvaluationId() {
        return sisEvaluationId;
    }

    public void setSisEvaluationId(int sisEvaluationId) {
        this.sisEvaluationId = sisEvaluationId;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(String gradeValue) {
        this.gradeValue = gradeValue;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
