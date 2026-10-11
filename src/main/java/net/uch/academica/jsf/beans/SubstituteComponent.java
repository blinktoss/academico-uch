package net.uch.academica.jsf.beans;

/**
 * One evaluation component shown as a column in the Substitute Act (Acta Sustitutoria) grid.
 *
 * <p>The columns are not hardcoded: each component is derived from the section's evaluation system
 * ({@code ac_sis_eva_personalizado}), so a new evaluation system configured in the database is
 * picked up without any code change. {@link #sisevaDetalleId} is the identifier stored back on the
 * substitute act detail, matching how the principal act persists its component grades.
 */
public class SubstituteComponent {

    /**
     * Placeholder shown when a student has no grade for this component.
     */
    public static final String EMPTY = "--";

    /**
     * Marker shown when the component is satisfied by convalidación instead of a numeric grade.
     */
    public static final String CONVALIDATED = "C";

    private String code;
    private String value;
    private int sisevaDetalleId;
    // Weights used to recompute the final grade after a substitution (Grade Policy item 6).
    private float componentWeight;
    private int groupId;
    private float groupWeight;
    // Inline color style (same criteria as the grade-registration screen: red below the passing grade).
    private String style = "";

    public SubstituteComponent() {
    }

    public SubstituteComponent(String code, String value, int sisevaDetalleId) {
        this.code = code;
        this.value = value;
        this.sisevaDetalleId = sisevaDetalleId;
    }

    /**
     * @return {@code true} when this component carries a real numeric grade that can be replaced by
     * a substitute score (i.e. it is neither empty nor a convalidación marker).
     */
    public boolean isReplaceable() {
        return value != null && !EMPTY.equals(value) && !CONVALIDATED.equals(value);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSisevaDetalleId() {
        return sisevaDetalleId;
    }

    public void setSisevaDetalleId(int sisevaDetalleId) {
        this.sisevaDetalleId = sisevaDetalleId;
    }

    public float getComponentWeight() {
        return componentWeight;
    }

    public void setComponentWeight(float componentWeight) {
        this.componentWeight = componentWeight;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public float getGroupWeight() {
        return groupWeight;
    }

    public void setGroupWeight(float groupWeight) {
        this.groupWeight = groupWeight;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
