package net.uch.academica.jsf.beans;

import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * One student row in the Substitute Act (Acta Sustitutoria) grid.
 *
 * <p>Replaces the old "one {@code MbNota} per acta-detail row" model, which produced a duplicated
 * X*Y matrix (a row per grade component instead of per student) and relied on a hardcoded
 * {@code EP/EF/PA-1/PA-2} switch. Here each student is a single row carrying its evaluation
 * {@link SubstituteComponent components} as a list, so the columns are driven entirely by the
 * section's evaluation system.
 *
 * <p>The inline-edit handlers ({@link #editarFila}, {@link #editar}, {@link #cancelar}) live on the
 * row itself because RichFaces binds {@code #{row.editarFila}} to the row object during iteration.
 */
public class SubstituteActRow {

    private int studentId;
    private String studentCode;
    private String studentName;
    private List<SubstituteComponent> components = new ArrayList<>();
    private String originalFinal = SubstituteComponent.EMPTY;
    private boolean convalidated;

    private String substitute = SubstituteComponent.EMPTY;
    private String substituteAuxiliary;
    private boolean editable;
    private String oncomplete;

    /**
     * @return {@code true} when the teacher entered a substitute score for this student (the row is
     * skipped on save otherwise, and convalidación rows are never substitutable).
     */
    public boolean hasSubstitute() {
        return !convalidated
                && substitute != null
                && !SubstituteComponent.EMPTY.equals(substitute)
                && !substitute.trim().isEmpty();
    }

    public void editarFila(ActionEvent event) {
        this.substituteAuxiliary = this.substitute;
        this.editable = true;
    }

    public void editar(ActionEvent event) {
        this.editable = false;
    }

    public void cancelar(ActionEvent event) {
        this.substitute = this.substituteAuxiliary;
        this.editable = false;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<SubstituteComponent> getComponents() {
        return components;
    }

    public void setComponents(List<SubstituteComponent> components) {
        this.components = components;
    }

    public String getOriginalFinal() {
        return originalFinal;
    }

    public void setOriginalFinal(String originalFinal) {
        this.originalFinal = originalFinal;
    }

    public boolean isConvalidated() {
        return convalidated;
    }

    public void setConvalidated(boolean convalidated) {
        this.convalidated = convalidated;
    }

    public String getSubstitute() {
        return substitute;
    }

    public void setSubstitute(String substitute) {
        this.substitute = substitute;
    }

    public String getSubstituteAuxiliary() {
        return substituteAuxiliary;
    }

    public void setSubstituteAuxiliary(String substituteAuxiliary) {
        this.substituteAuxiliary = substituteAuxiliary;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }
}
