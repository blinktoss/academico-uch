package net.uch.academica.dao;

import net.uch.SpringConfigurationTest;
import net.uch.academica.jsf.GradePolicy;
import net.uch.academica.jsf.beans.BeanCursos;
import net.uch.util.HSTestSupportDao;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static net.uch.common.Constants.ACT_STATUS_DRAFT;
import static net.uch.common.Constants.CAT_TIPO_ACTA_PRINCIPAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * CHARACTERIZATION TEST — Prerequisite approval semantics (critical flow #2: grades feeding
 * enrollment).
 *
 * <p>Pins the CURRENT behavior of {@code HSMatriculaDAOImpl.seleccionarCursosAprobados} — the
 * query that decides which courses a student has "approved", which in turn opens/blocks courses
 * during enrollment. Two institutional oddities are captured AS-IS (flagged, not fixed):
 *
 * <ol>
 *   <li><b>DRAFT actas count.</b> The query has no {@code ActStatus} filter, so a grade sitting
 *       in an unpublished (DRAFT) acta already counts toward prerequisites — before the record
 *       is official.</li>
 *   <li><b>The 10.5 vs 11 threshold split.</b> Prerequisite approval is {@code >= 10.5}
 *       (HSMatriculaDAOImpl:67) while {@code GradePolicy.PASSING_GRADE} is 11 with truncation —
 *       a decimal 10.5 final is "approved" for prerequisites yet FAIL for the acta status.</li>
 * </ol>
 *
 * <p>Fixture: student alu_id 2095 (active, ZERO acta history in the dev DB — verified) + section
 * 1829 (curape 844 → plancur 23). Each test inserts one acta+detail and deletes it afterwards.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PrerequisiteApprovalCharacterizationTest extends SpringConfigurationTest {

    private static final int STUDENT_NO_HISTORY = 2095;
    private static final int SECCION_ID = 1829;
    private static final int PLANCUR_ID = 23;

    private static HSTestSupportDao DAO;

    @Autowired
    @Qualifier("springHibernateDaoMatricula")
    private HSMatriculaDAO matriculaDAO;

    private int actaId;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @After
    public void cleanUp() {
        if (actaId != 0) {
            DAO.deleteActaCascade(actaId);
        }
    }

    @Test
    public void draftActaGradeOfTenPointFiveCountsAsApprovedTest() {
        // SUSPICIOUS BEHAVIOR (captured as-is): the acta is DRAFT — never published, no guard has
        // run — yet its PROMEDIO_FINAL detail already drives prerequisite approval. Documented as
        // "needs verification" in Student_Enrollment_State_Analysis §3.3; hereby pinned TRUE.
        actaId = DAO.insertActaConDetalleFinal(
                SECCION_ID, CAT_TIPO_ACTA_PRINCIPAL, ACT_STATUS_DRAFT, STUDENT_NO_HISTORY, 10.5f);

        BeanCursos cursos = matriculaDAO.seleccionarCursosAprobados(STUDENT_NO_HISTORY);

        assertEquals("the DRAFT-acta course must appear as approved",
                Collections.singletonList(PLANCUR_ID), cursos.getAprobados());
        assertTrue("nothing failed", cursos.getDesaprobadosId().isEmpty());

        // THRESHOLD INCONSISTENCY (flagged, not fixed): the same 10.5 that just counted as
        // "approved" for prerequisites is a FAIL by the official grade policy (truncate(10.5)=10,
        // PASSING_GRADE=11). SME question pending: is the institutional cutoff 10.5 or 11?
        assertEquals(GradePolicy.STATUS_FAIL, GradePolicy.status(GradePolicy.truncate(10.5)));
    }

    @Test
    public void gradeOfTenIsDesaprobadoTest() {
        // Boundary pin: 10.0 < 10.5 → the course lands in desaprobados and, having no approved
        // course at all, the marker value -1 fills aprobados (the "freshman" sentinel that
        // routes cursosAbiertos() to the cycle-1 course list).
        actaId = DAO.insertActaConDetalleFinal(
                SECCION_ID, CAT_TIPO_ACTA_PRINCIPAL, ACT_STATUS_DRAFT, STUDENT_NO_HISTORY, 10.0f);

        BeanCursos cursos = matriculaDAO.seleccionarCursosAprobados(STUDENT_NO_HISTORY);

        assertEquals(Collections.singletonList(-1), cursos.getAprobados());
        assertEquals(Collections.singletonList(PLANCUR_ID), cursos.getDesaprobadosId());
    }

    @Test
    public void studentWithNoHistoryGetsFreshmanSentinelTest() {
        // No acta rows at all → aprobados == [-1]: the sentinel consumed by
        // MbMatricula.cursosAbiertos() to offer cycle-1 (006001) courses of the current plan.
        BeanCursos cursos = matriculaDAO.seleccionarCursosAprobados(STUDENT_NO_HISTORY);

        assertEquals(Collections.singletonList(-1), cursos.getAprobados());
        assertTrue(cursos.getDesaprobadosId().isEmpty());
    }
}
