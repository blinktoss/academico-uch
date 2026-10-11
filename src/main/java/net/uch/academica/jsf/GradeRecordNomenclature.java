package net.uch.academica.jsf;

import net.uch.mapping.AcSeccion;
import net.uch.mapping.AcSemestre;

import static net.uch.common.Constants.CAT_TIPO_ACTA_PRINCIPAL;

/**
 * Deterministic grade record (Acta) code/name/number, derived from existing section/semester
 * data instead of free text. Code/name can be computed before the record is ever saved; number
 * depends on the generated act_id, so it's only available once the record has been persisted.
 * <p>
 * Deliberately built only from AcSeccion/AcSemestre (loaded directly through their own DAOs,
 * not through AcCursoAperturado's lazy associations, which are not safe to dereference outside
 * the session that loaded them). The course name is already shown separately as nodeCurapeDesc
 * wherever this is used, so it isn't repeated here.
 */
public final class GradeRecordNomenclature {

    private static final String PLACEHOLDER_NUMBER = "(se asignara al publicar)";

    private GradeRecordNomenclature() {
    }

    public static String code(AcSemestre semester, AcSeccion section, String recordType) {
        return semester.getSemCodigo() + "-" + section.getSecCodigo() + "-" + suffix(recordType);
    }

    public static String name(AcSemestre semester, AcSeccion section, String recordType) {
        var typeLabel = CAT_TIPO_ACTA_PRINCIPAL.equals(recordType) ? "Principal" : "Sustitutoria";
        return "Acta " + typeLabel + " de Notas - Seccion " + section.getSecNombre() +
                " - " + semester.getSemNombre();
    }

    public static String pendingNumber() {
        return PLACEHOLDER_NUMBER;
    }

    public static String number(int recordId) {
        return String.format("%06d", recordId);
    }

    private static String suffix(String recordType) {
        return CAT_TIPO_ACTA_PRINCIPAL.equals(recordType) ? "P" : "S";
    }
}
