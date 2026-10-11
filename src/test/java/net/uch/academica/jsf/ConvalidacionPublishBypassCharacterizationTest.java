package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.academica.dao.HSActaDAO;
import net.uch.academica.dao.HSMatriculaDAO;
import net.uch.academica.jsf.beans.BeanConvalidacion;
import net.uch.mapping.AcActa;
import net.uch.mapping.AcMatricula;
import net.uch.util.HSTestSupportDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Map;

import static net.uch.Stubs.Especialidad;
import static net.uch.common.Constants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * CHARACTERIZATION TEST — Convalidación acta lifecycle bypass (critical flow #3: grades/actas).
 *
 * <p>The suite's official-record rule is "actas become immutable once published; publication goes
 * through {@code GradeRecordService}'s guard". Convalidación does NOT follow that path — pinned
 * here AS-IS (flagged, not fixed):
 *
 * <ul>
 *   <li>The convalidación acta is <b>born PUBLISHED</b> (MbConvalidacion:272 writes
 *       {@code ACT_STATUS_PUBLISHED} directly via the DAO — no {@code rejectIfPublished} guard,
 *       no {@code GradeRecordNomenclature} number: code/name/number stay null).</li>
 *   <li>Re-running the flow <b>appends details to the already-PUBLISHED acta</b> without any
 *       {@code GradeRecordAlreadyPublishedException} — the immutability rule does not apply on
 *       this path (System_State_Management_Discovery §2's still-open gap).</li>
 *   <li>The backing enrollment is written in the <b>hardcoded semester id 5</b> ("SEM-TRAS")
 *       with code MAT-CONV and MatTipo=REGULAR, regardless of any current semester.</li>
 * </ul>
 *
 * <p>Fixture: same student as {@code MbConvalidacionTest} (alu_id 3015, ENFERMERÍA); cleanup
 * mirrors that test's {@code @AfterClass}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ConvalidacionPublishBypassCharacterizationTest extends SpringConfigurationTest {

    private static final int STUDENT_ID = 3015;

    private static HSTestSupportDao DAO;
    private MbConvalidacion manager;

    @Autowired
    @Qualifier("springHibernateDaoActa")
    private HSActaDAO actaDAO;

    @Autowired
    @Qualifier("springHibernateDaoMatricula")
    private HSMatriculaDAO matriculaDAO;

    @AfterClass
    public static void cleanUpDatabase() {
        DAO.cleanAcMatriculaCursoConv();
        DAO.cleanAcActaConvalidacionByAlumno(STUDENT_ID);
    }

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbConvalidacion();
        manager.init();
    }

    @Test
    public void convalidacionActaIsBornPublishedAndAcceptsRepeatedWritesTest() {
        // Load the student's convalidable courses exactly like the production screen does.
        manager.setAlumnoId(STUDENT_ID);
        manager.setParamAluCodigo("09102023");
        manager.setEspecialidadId(Especialidad.ENFERMERIA);

        Map<String, Object> attributes = Collections.singletonMap("p_alu", manager);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);
        manager.capturarAlumno(actionEvent);
        assertFalse(manager.getConvalidaciones().isEmpty());

        // Activate a single course with the decimal boundary grade 10.5 — validarNotas accepts
        // any grade in (10, 20], so 10.5 is a legal convalidación grade. NOTE (flagged, not
        // fixed): a stored 10.5 counts as "approved" for prerequisites (>= 10.5 rule) while
        // GradePolicy would call its truncated value (10) a FAIL — the b8 threshold tension.
        BeanConvalidacion elegido = manager.getConvalidaciones().get(0);
        int secId = elegido.getRegSecId();
        manager.getConvalidaciones().forEach(c -> c.setActivar(false));
        elegido.setActivar(true);
        elegido.setRegNota("10.5");

        manager.guardarConvalidacion(actionEvent);
        assertTrue(manager.getOncomplete().contains("Convalidacion terminada"));

        // 1) The acta was created directly in PUBLISHED state — no DRAFT phase, no publish
        //    guard, and none of the GradeRecordNomenclature identity (code/name/number null).
        AcActa acta = actaDAO.seleccionarActa(secId, CAT_TIPO_ACTA_CONVALIDACION);
        assertNotNull(acta);
        assertEquals("convalidación acta is born PUBLISHED (bypasses GradeRecordService)",
                ACT_STATUS_PUBLISHED, acta.getActStatus());
        assertNull("no acta number is ever assigned on this path", acta.getActNumero());
        assertNull(acta.getActCodigo());

        // 2) The backing enrollment: hardcoded semester 5 (SEM-TRAS), code MAT-CONV, REGULAR.
        AcMatricula mat = matriculaDAO.seleccionarMatriculaAlumnoConvalidacion(STUDENT_ID);
        assertNotNull(mat);
        assertEquals("convalidación always enrolls into hardcoded semester 5 (SEM-TRAS)",
                Integer.valueOf(5), mat.getSemId());
        assertEquals("MAT-CONV", mat.getMatCodigo());
        assertEquals(CAT_TIPO_MATRICULA_REGULAR, mat.getMatTipo());

        // 3) SUSPICIOUS BEHAVIOR (captured as-is): saving AGAIN against the now-PUBLISHED acta
        //    succeeds — no GradeRecordAlreadyPublishedException, no immutability. The guard only
        //    exists on the MbNota/GradeRecordService path; convalidación writes around it.
        //    guardarConvalidacion() cleared the form, so reload the student first.
        manager.setAlumnoId(STUDENT_ID);
        manager.setEspecialidadId(Especialidad.ENFERMERIA);
        manager.capturarAlumno(actionEvent);
        BeanConvalidacion again = manager.getConvalidaciones().stream()
                .filter(c -> c.getRegSecId() == secId).findFirst().orElseThrow(AssertionError::new);
        manager.getConvalidaciones().forEach(c -> c.setActivar(false));
        again.setActivar(true);
        again.setRegNota("15");

        manager.guardarConvalidacion(actionEvent);

        assertTrue("published convalidación acta accepts further writes — immutability bypass",
                manager.getOncomplete().contains("Convalidacion terminada"));
        assertEquals(ACT_STATUS_PUBLISHED,
                actaDAO.seleccionarActa(secId, CAT_TIPO_ACTA_CONVALIDACION).getActStatus());
    }
}
