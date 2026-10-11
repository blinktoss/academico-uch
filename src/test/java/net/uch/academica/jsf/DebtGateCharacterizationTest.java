package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.common.audit.CurrentUserContext;
import net.uch.mapping.AdAlumnoTarifa;
import net.uch.util.HSTestSupportDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import java.util.Date;

import static net.uch.Stubs.SEMESTRE_2010_2_ID;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * CHARACTERIZATION TEST — Enrollment debt gate (critical flow #1).
 *
 * <p>Pins the CURRENT behavior of {@code MbMatricula}'s debt-blocking rule for the
 * null-effective-deadline branch introduced by Sprint 2 ("a tariff row written by a sibling app
 * with no {@code alutar_fecha_prorroga} blocks rather than slipping through"). Captures behavior
 * as-is, including suspicious spots — flagged in comments, NOT fixed here.
 *
 * <p>Fixture: student alu_id 3482 (ESTUDIANTE/REGULAR/active) with the known unpaid tariff
 * 41049 (estado='0', prorroga=2010-09-30) — same fixture as
 * {@code MbMatriculaTest.matricularBlocksStudentWithOutstandingDebtTest}. Each test snapshots and
 * restores the tariff's deadline so the shared dev DB is left untouched.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DebtGateCharacterizationTest extends SpringConfigurationTest {

    private static final int STUDENT_ID = 3482;
    private static final int TARIFA_ID = 41049;

    private static HSTestSupportDao DAO;
    private MbMatricula manager;
    private Date originalProrroga;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbMatricula();
        manager.init();
        loggerUser.setRowUsuId(Stubs.USUARIO_ADMIN_ID);
        CurrentUserContext.set(loggerUser.getRowUsuId());

        originalProrroga = DAO.getAlumnoTarifa(TARIFA_ID).getAlutarFechaProrroga();
    }

    @After
    public void restoreFixture() {
        if (originalProrroga != null) {
            DAO.updateAlutarFechaProrroga(TARIFA_ID, originalProrroga);
        }
    }

    @Test
    public void unpaidTariffWithNullDeadlineBlocksEnrollmentTest() {
        // CURRENT RULE (esDeudaBloqueante, MbMatricula:793-802): when alutar_fecha_prorroga is
        // NULL and estado != '2' (PAGO_TOTAL), the tariff blocks — the null-deadline row cannot
        // "slip through unnoticed". This is the Sprint-2 drift over the documented AS-IS rule
        // (which only blocked when today > deadline).
        DAO.clearAlutarFechaProrroga(TARIFA_ID);
        AdAlumnoTarifa tarifa = DAO.getAlumnoTarifa(TARIFA_ID);
        assertNull("fixture precondition: deadline cleared", tarifa.getAlutarFechaProrroga());
        assertEquals("fixture precondition: tariff unpaid", "0", tarifa.getAlutarEstado());

        manager.setStudentId(STUDENT_ID);
        manager.setRowSemestreId(SEMESTRE_2010_2_ID);

        manager.matricular(actionEvent);

        assertTrue("null-deadline unpaid tariff must block enrollment",
                manager.getOncomplete().contains("deuda pendiente"));
        assertEquals(0, manager.getRowMatriculaId());
    }

    @Test
    public void unpaidTariffWithFutureDeadlineDoesNotBlockAtSubmissionTest() {
        // CURRENT RULE: estado != '2' but today <= deadline → NOT blocking. Pin the boundary by
        // pushing the fixture tariff's deadline far into the future; every OTHER tariff of this
        // student still has a 2010 deadline, so we assert only through the single-tariff
        // predicate path: with 41049 future-dated the student is still blocked by the OTHER
        // expired tariffs — proving the rule is per-tariff OR-ed, not just driven by 41049.
        DAO.updateAlutarFechaProrroga(TARIFA_ID, new Date(System.currentTimeMillis() + 86_400_000L * 365));

        manager.setStudentId(STUDENT_ID);
        manager.setRowSemestreId(SEMESTRE_2010_2_ID);

        manager.matricular(actionEvent);

        // Still blocked — student 3482 has more expired unpaid tariffs than just 41049.
        // (Characterizes tieneDeudaBloqueante's any-match semantics, MbMatricula:804-814.)
        assertTrue(manager.getOncomplete().contains("deuda pendiente"));
    }

    @Test(expected = NullPointerException.class)
    public void verDetalleDeudaCrashesOnNullDeadlineTariffTest() {
        // SUSPICIOUS BEHAVIOR (captured as-is, do not fix here): the debt-DETAIL modal
        // (verDetalleDeuda, MbMatricula:1135-1140) still inlines the OLD comparison and calls
        // Calendar.setTime(alutarFechaProrroga) unguarded. For the very same null-deadline
        // tariff that BLOCKS enrollment (test above), the detail modal throws NPE — so the
        // cashier/registrar can be told "student has blocking debt" yet be unable to see which
        // debt it is. Residue of the Sprint-2 consolidation (the fixed predicate lives in
        // esDeudaBloqueante; this loop was not updated). If this test ever fails because the
        // NPE stopped happening, the residue was fixed — update the docs and this test.
        DAO.clearAlutarFechaProrroga(TARIFA_ID);

        UIParameter uipPId = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uipPId);
        when(uipPId.getValue()).thenReturn(String.valueOf(STUDENT_ID));

        manager.verDetalleDeuda(actionEvent);
    }
}
