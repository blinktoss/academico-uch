package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.academica.dao.HSHorarioDAO;
import net.uch.academica.jsf.beans.BeanDuplicadoHorario;
import net.uch.common.audit.CurrentUserContext;
import net.uch.mapping.AcAmpliaciones;
import net.uch.mapping.AcMatricula;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.uch.Stubs.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 14/12/2022.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbMatriculaTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private MbMatricula manager;

    @Autowired
    @Qualifier("springHibernateDaoHorario")
    private HSHorarioDAO horarioDAO;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbMatricula();
        manager.init();

        // Simulate what AuditUserFilter does on every request in production: expose the
        // logged-in user's id to AuditInterceptor via CurrentUserContext, so created_by /
        // updated_by get stamped. The base class stubs the session to return loggerUser.
        loggerUser.setRowUsuId(Stubs.USUARIO_ADMIN_ID);
        CurrentUserContext.set(loggerUser.getRowUsuId());
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void initializationOnInitTest() {
        assertNotNull(manager.getComboFacultades());
        assertNotNull(manager.getComboSemestres());
        assertFalse(manager.getSemestreActual().isEmpty());

        assertEquals(0, manager.getParamFacId());
        assertEquals(5, manager.getComboFacultades().length);
        assertNotEquals(0, manager.getComboSemestres().length);
    }

    @Test
    public void getComboEspecialidad_buscarTest() {
        manager.setParamFacId(Facultad.EDUCACION_PRIMARIA);
        manager.cambiarEspecialidadBuscar(actionEvent);

        List<SelectItem> result = manager.getComboEspecialidades();

        assertEquals(1, result.size());

        // ------------------------------------------------------------------
        manager.setParamFacId(0);
        manager.cambiarEspecialidadBuscar(actionEvent);
        result = manager.getComboEspecialidades();
        assertTrue(result.isEmpty());
    }

    @Test
    public void seleccionarTest() {
        manager.setParamAluCodigo("");
        manager.setParamAluPaterno("DI");
        manager.setParamAluMaterno("");
        manager.setParamAluNombre("");
        manager.setParamFacId(Facultad.CONTABILIDAD);
        manager.setParamEspId(Especialidad.CONTABILIDAD);
        manager.setParamSemId(SEMESTRE_2010_2_ID);

        manager.seleccionar();

        assertNotNull(manager.getListaAlumno());
        assertFalse(manager.getListaAlumno().isEmpty());
    }

    @Test
    public void seleccionarExcludesRetiradoStudentTest() {
        // Student 08030020 (alu_id 2098) is AluTipo=009003 (RETIRADO) and AluActivo='1' in the
        // dev DB - confirmed live during the AS-IS investigation that withdrawn students are not
        // soft-deleted, so without this filter they'd be searchable and enrollable.
        manager.setParamAluCodigo("08030020");
        manager.setParamAluPaterno("");
        manager.setParamAluMaterno("");
        manager.setParamAluNombre("");
        manager.setParamSemId(SEMESTRE_2010_2_ID);

        manager.seleccionar();

        assertNotNull(manager.getListaAlumno());
        assertTrue("RETIRADO student must not appear in the enrollment search", manager.getListaAlumno().isEmpty());
    }

    @Test
    public void seleccionarExcludesNonRegularAluEstadoTest() {
        // Defensive check, not yet exercised by live data: every student in the dev DB is
        // currently AluEstado=REGULAR (011001). Temporarily flips a known ESTUDIANTE fixture
        // (08201066, alu_id 2644) to OBSERVADO (011002) to prove the filter actually excludes a
        // non-REGULAR status, then restores it so other tests relying on this fixture (e.g.
        // seleccionarPrematriculaTest) are unaffected.
        int alumnoId = 2644;
        DAO.updateAluEstado(alumnoId, "011002");
        try {
            manager.setParamAluCodigo("08201066");
            manager.setParamAluPaterno("");
            manager.setParamAluMaterno("");
            manager.setParamAluNombre("");
            manager.setParamSemId(SEMESTRE_2009_1_ID);

            manager.seleccionar();

            assertNotNull(manager.getListaAlumno());
            assertTrue("OBSERVADO student must not appear in the enrollment search", manager.getListaAlumno().isEmpty());
        } finally {
            DAO.updateAluEstado(alumnoId, "011001");
        }
    }

    @Test
    public void matricularBlocksIneligibleStudentTest() {
        // Same RETIRADO fixture as seleccionarExcludesRetiradoStudentTest (alu_id 2098). Defends
        // against a stale/forged submission for a student who was eligible when the search ran but
        // no longer is by the time matricular() is invoked - seleccionar() only filters the search
        // results, it doesn't gate the write.
        manager.setStudentId(2098);
        manager.setRowSemestreId(SEMESTRE_2010_2_ID);

        manager.matricular(actionEvent);

        assertTrue(manager.getOncomplete().contains("no es elegible"));
        assertEquals(0, manager.getRowMatriculaId());
    }

    @Test
    public void matricularBlocksStudentWithOutstandingDebtTest() {
        // Student alu_id 3482 is ESTUDIANTE/REGULAR/active (passes eligibility) but has a known
        // unpaid AdAlumnoTarifa (41049, estado='0', fechaProrroga=2010-09-30, well in the past) -
        // same TOCTOU shape as eligibility: seleccionar() only disables the UI button for a
        // debtor, matricular() never re-checked it before this fix.
        manager.setStudentId(3482);
        manager.setRowSemestreId(SEMESTRE_2010_2_ID);

        manager.matricular(actionEvent);

        assertTrue(manager.getOncomplete().contains("deuda pendiente"));
        assertEquals(0, manager.getRowMatriculaId());
    }

    @Test
    public void reservarMatriculaBlocksIneligibleStudentTest() {
        UIParameter uipPId = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uipPId);
        when(uipPId.getValue()).thenReturn(2098);

        manager.setRowMatriculaId(0);
        manager.reservarMatricula(actionEvent);

        assertTrue(manager.getOncomplete().contains("no es elegible"));
        assertEquals(0, manager.getRowMatriculaId());
    }

    @Test
    public void seleccionarPrematriculaTest() {
        manager.setParamAluCodigo("08201066");
        manager.setParamAluPaterno("");
        manager.setParamAluMaterno("");
        manager.setParamAluNombre("");
        manager.setParamSemId(SEMESTRE_2009_1_ID);

        manager.seleccionar();

        assertNotNull(manager.getListaAlumno());
        assertEquals(1, manager.getListaAlumno().size());
        assertFalse(manager.getListaAlumno().get(0).getRowAluCodigo().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getRowAluPaterno().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getRowAluMaterno().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getRowAluNombre().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getRowEspecialidad().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getRowTipoMatriculaId().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getRowTipoMatriculaValue().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getRowSemestreCode().isEmpty());
        assertFalse(manager.getListaAlumno().get(0).getImagen().isEmpty());
    }

    @Test
    public void seleccionarStudentWithAlertTest() {
        manager.setParamAluCodigo("09101486");
        manager.setParamAluPaterno("");
        manager.setParamAluMaterno("");
        manager.setParamAluNombre("");
        manager.setParamSemId(SEMESTRE_2009_2_ID);

        manager.seleccionar();

        assertNotNull(manager.getListaAlumno());
        assertEquals(1, manager.getListaAlumno().size());
        assertNotEquals(0, manager.getListaAlumno().get(0).getStudentId());
        assertTrue(manager.getListaAlumno().get(0).isVerAlerta());
        assertTrue(manager.getListaAlumno().get(0).isVerDeuda());
        assertFalse(manager.getListaAlumno().get(0).isVerMatricular());
        assertFalse(manager.getListaAlumno().get(0).isVerRectificar());
        assertTrue(manager.getListaAlumno().get(0).isVerImprimirFicha());
        assertTrue(manager.getListaAlumno().get(0).isVerImprimirConsolidado());
        assertFalse(manager.getListaAlumno().get(0).isVerReservar());
        assertTrue(manager.getListaAlumno().get(0).isVerEliminar());
    }

    @Test
    public void openModalRegistroMatriculaTest() {
        // Student code 08201085, alu_id 2663 - real dev-DB AluTipo is 009004 (ANULACION DE
        // INGRESO), so matricular()'s defensive eligibility re-check would otherwise reject this
        // fixture. Temporarily flip it to ESTUDIANTE to keep exercising the happy path here, and
        // restore it so other sessions/tests see the real data.
        DAO.updateAluTipo(2663, "009001");
        try {
            UIParameter uipPIdMatricula = mock(UIParameter.class);
            UIParameter uipPMatTipo = mock(UIParameter.class);
            UIParameter uipPId = mock(UIParameter.class);
            UIParameter uipp_id_semestre = mock(UIParameter.class);
            UIParameter uipp_codigo = mock(UIParameter.class);
            UIParameter uipp_paterno = mock(UIParameter.class);
            UIParameter uipp_materno = mock(UIParameter.class);
            UIParameter uipp_nombre = mock(UIParameter.class);
            UIParameter uipp_id_esp_act = mock(UIParameter.class);

            manager.setRowMatriculaId(0);
            manager.setRowTipoMatriculaId("0");
            manager.setStudentId(2663);
            manager.setRowSemestreId(SEMESTRE_2010_2_ID);
            manager.setRowAluCodigo("08201085");
            manager.setRowAluPaterno("VALENZUELA");
            manager.setRowAluMaterno("GARCIA");
            manager.setRowAluNombre("JHON LUIS");
            manager.setParamEspId(Especialidad.ING_SISTEMAS);

            when(actionEvent.getComponent()).thenReturn(uiComponent);
            when(uiComponent.findComponent("p_id_matricula")).thenReturn(uipPIdMatricula);
            when(uipPIdMatricula.getValue()).thenReturn(manager.getRowMatriculaId());
            when(uiComponent.findComponent("p_mat_tipo")).thenReturn(uipPMatTipo);
            when(uipPMatTipo.getValue()).thenReturn(manager.getRowTipoMatriculaId());
            when(uiComponent.findComponent("p_id")).thenReturn(uipPId);
            when(uipPId.getValue()).thenReturn(manager.getStudentId());
            when(uiComponent.findComponent("p_id_semestre")).thenReturn(uipp_id_semestre);
            when(uipp_id_semestre.getValue()).thenReturn(manager.getRowSemestreId());
            when(uiComponent.findComponent("p_codigo")).thenReturn(uipp_codigo);
            when(uipp_codigo.getValue()).thenReturn(manager.getRowAluCodigo());
            when(uiComponent.findComponent("p_paterno")).thenReturn(uipp_paterno);
            when(uipp_paterno.getValue()).thenReturn(manager.getRowAluPaterno());
            when(uiComponent.findComponent("p_materno")).thenReturn(uipp_materno);
            when(uipp_materno.getValue()).thenReturn(manager.getRowAluMaterno());
            when(uiComponent.findComponent("p_nombre")).thenReturn(uipp_nombre);
            when(uipp_nombre.getValue()).thenReturn(manager.getRowAluNombre());
            when(uiComponent.findComponent("p_id_esp_act")).thenReturn(uipp_id_esp_act);
            when(uipp_id_esp_act.getValue()).thenReturn(manager.getParamEspId());

            // Opening modal
            manager.editarFila(actionEvent);
            manager.cursosAbiertos();

            assertNotNull(manager.getListaCursos());
            assertFalse(manager.getListaCursos().isEmpty());
            assertEquals(6, manager.getListaCursos().size());

            // Editing
            manager.getListaCursos().get(0).setSeccionId(1829); // ISI01MA-6
            manager.getListaCursos().get(1).setSeccionId(1832); // ISI01MA-12

            manager.matricular(actionEvent);

            assertTrue(manager.getOncomplete().contains("Richfaces.hideModalPanel('mp')"));
            assertNotEquals(0, manager.getRowMatriculaId());

            // Reservar matricula
            when(uipPIdMatricula.getValue()).thenReturn(manager.getRowMatriculaId());
            manager.reservarMatricula(actionEvent);

            // Eliminar matricula
            when(uipPIdMatricula.getValue()).thenReturn(manager.getRowMatriculaId());
            manager.eliminarMatricula(actionEvent);
        } finally {
            DAO.updateAluTipo(2663, "009004");
        }
    }

    @Test
    public void auditFieldsOnEnrollmentLifecycleTest() {
        // Same fixture-eligibility caveat as openModalRegistroMatriculaTest: alu_id 2663 is really
        // AluTipo=009004 (ANULACION DE INGRESO), flip/restore around the test.
        DAO.updateAluTipo(2663, "009001");
        try {
            UIParameter uipPIdMatricula = mock(UIParameter.class);
            UIParameter uipPMatTipo = mock(UIParameter.class);
            UIParameter uipPId = mock(UIParameter.class);
            UIParameter uipp_id_semestre = mock(UIParameter.class);
            UIParameter uipp_codigo = mock(UIParameter.class);
            UIParameter uipp_paterno = mock(UIParameter.class);
            UIParameter uipp_materno = mock(UIParameter.class);
            UIParameter uipp_nombre = mock(UIParameter.class);
            UIParameter uipp_id_esp_act = mock(UIParameter.class);

            manager.setRowMatriculaId(0);
            manager.setRowTipoMatriculaId("0");
            manager.setStudentId(2663);
            manager.setRowSemestreId(SEMESTRE_2010_2_ID);
            manager.setRowAluCodigo("08201085");
            manager.setRowAluPaterno("VALENZUELA");
            manager.setRowAluMaterno("GARCIA");
            manager.setRowAluNombre("JHON LUIS");
            manager.setParamEspId(Especialidad.ING_SISTEMAS);

            when(actionEvent.getComponent()).thenReturn(uiComponent);
            when(uiComponent.findComponent("p_id_matricula")).thenReturn(uipPIdMatricula);
            when(uipPIdMatricula.getValue()).thenReturn(manager.getRowMatriculaId());
            when(uiComponent.findComponent("p_mat_tipo")).thenReturn(uipPMatTipo);
            when(uipPMatTipo.getValue()).thenReturn(manager.getRowTipoMatriculaId());
            when(uiComponent.findComponent("p_id")).thenReturn(uipPId);
            when(uipPId.getValue()).thenReturn(manager.getStudentId());
            when(uiComponent.findComponent("p_id_semestre")).thenReturn(uipp_id_semestre);
            when(uipp_id_semestre.getValue()).thenReturn(manager.getRowSemestreId());
            when(uiComponent.findComponent("p_codigo")).thenReturn(uipp_codigo);
            when(uipp_codigo.getValue()).thenReturn(manager.getRowAluCodigo());
            when(uiComponent.findComponent("p_paterno")).thenReturn(uipp_paterno);
            when(uipp_paterno.getValue()).thenReturn(manager.getRowAluPaterno());
            when(uiComponent.findComponent("p_materno")).thenReturn(uipp_materno);
            when(uipp_materno.getValue()).thenReturn(manager.getRowAluMaterno());
            when(uiComponent.findComponent("p_nombre")).thenReturn(uipp_nombre);
            when(uipp_nombre.getValue()).thenReturn(manager.getRowAluNombre());
            when(uiComponent.findComponent("p_id_esp_act")).thenReturn(uipp_id_esp_act);
            when(uipp_id_esp_act.getValue()).thenReturn(manager.getParamEspId());

            manager.editarFila(actionEvent);
            manager.cursosAbiertos();
            manager.getListaCursos().get(0).setSeccionId(1829); // ISI01MA-6
            manager.getListaCursos().get(1).setSeccionId(1832); // ISI01MA-12

            manager.matricular(actionEvent);
            int matriculaId = manager.getRowMatriculaId();
            assertNotEquals(0, matriculaId);

            // On insert AuditInterceptor.onSave fills only created_*; updated_* stay null
            // until the row is first modified.
            AcMatricula created = DAO.getAcMatricula(matriculaId);
            assertNotNull("created_at must be stamped on insert", created.getCreatedAt());
            assertNotNull("created_by must be stamped on insert", created.getCreatedBy());
            assertEquals(Stubs.USUARIO_ADMIN_ID, created.getCreatedBy().intValue());
            assertNull("updated_at must stay null until first update", created.getUpdatedAt());
            assertNull("updated_by must stay null until first update", created.getUpdatedBy());

            // Reservar matricula cancels the just-created row via actualizarTipoMatricula, now
            // load-then-update instead of bulk HQL, so AuditInterceptor.onFlushDirty fires.
            when(uipPIdMatricula.getValue()).thenReturn(matriculaId);
            manager.reservarMatricula(actionEvent);

            AcMatricula cancelled = DAO.getAcMatricula(matriculaId);
            assertNotNull("created_at must be preserved on update", cancelled.getCreatedAt());
            assertNotNull("updated_at must be stamped on first update", cancelled.getUpdatedAt());
            assertNotNull("updated_by must be stamped on first update", cancelled.getUpdatedBy());
            assertEquals(Stubs.USUARIO_ADMIN_ID, cancelled.getUpdatedBy().intValue());

            int reservedId = manager.getRowMatriculaId();
            assertNotEquals(0, reservedId);
            AcMatricula reserved = DAO.getAcMatricula(reservedId);
            assertNotNull("created_at must be stamped on the new reservation row", reserved.getCreatedAt());
            assertNull("updated_at must stay null on the new reservation row", reserved.getUpdatedAt());

            // eliminarMatricula deactivates a row - also load-then-update, must stamp updated_*.
            when(uipPIdMatricula.getValue()).thenReturn(reservedId);
            manager.eliminarMatricula(actionEvent);

            AcMatricula deactivated = DAO.getAcMatricula(reservedId);
            assertNotNull("updated_at must be stamped when eliminarMatricula deactivates the row", deactivated.getUpdatedAt());
            assertNotNull("updated_by must be stamped when eliminarMatricula deactivates the row", deactivated.getUpdatedBy());
        } finally {
            DAO.updateAluTipo(2663, "009004");
        }
    }

    @Test
    public void verCursosSubTableTest() {
        UIParameter uipFlag = mock(UIParameter.class);
        UIParameter uipMatriculaId = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("flag")).thenReturn(uipFlag);

        // opening subtable
        manager.setFlagVer("0");
        manager.setRowMatriculaId(10250);
        when(uipFlag.getValue()).thenReturn(manager.getFlagVer());

        when(uiComponent.findComponent("p_id_matricula")).thenReturn(uipMatriculaId);
        when(uipMatriculaId.getValue()).thenReturn(manager.getRowMatriculaId());

        manager.verCursos(actionEvent);

        assertTrue(manager.isRenderedSubTable());
        assertEquals("1", manager.getFlagVer());
        assertTrue(manager.getImagen().contains("up.png"));
        assertEquals(7, manager.getListaCursosMatriculado().size());

        MbMatricula child = manager.getListaCursosMatriculado().get(0);
        assertNotEquals(0, child.getDetCursoId());
        assertFalse(child.getDetCursoNombre().isEmpty());
        assertFalse(child.getDetCursoCodigo().isEmpty());
        assertFalse(child.getDetCursoCreditos().isEmpty());
        assertFalse(child.getDetSeccionCodigo().isEmpty());

        // closing subtable
        manager.setFlagVer("1");
        when(uipFlag.getValue()).thenReturn(manager.getFlagVer());

        manager.verCursos(actionEvent);

        assertFalse(manager.isRenderedSubTable());
        assertEquals("0", manager.getFlagVer());
        assertTrue(manager.getImagen().contains("down.png"));
        assertTrue(manager.getListaCursosMatriculado().isEmpty());
    }

    @Test
    public void openModalForRectificacionDeMatriculaTest() {
        // Student code 08050002
        manager.setStudentId(2080);
        manager.setRowSemestreId(SEMESTRE_2010_2_ID);
        manager.setParamEspId(Especialidad.ING_ELECTRONICA);

        String result = manager.rectificarMatricula();

        assertTrue(result.isEmpty());
        assertNotNull(manager.getListaCursos());
        assertTrue(manager.getOncomplete().contains("mpRectificar"));

        MbMatricula row = manager.getListaCursos().get(0);
        assertNotEquals(0, row.getMpRowCursoId());
        assertFalse(row.getMpRowCursoCode().isEmpty());
        assertFalse(row.getMpRowCursoName().isEmpty());
        assertFalse(row.getMpRowCursoCiclo().isEmpty());
        assertFalse(row.getMpRowCursoCreditos().isEmpty());
        assertFalse(row.getMpRowCursoTipo().isEmpty());
        assertNotEquals(0, row.getComboSecciones().length);

        // Forcing duplication: swap the student's own plancur 313 from its currently-selected
        // section (2248) to its alternate section (2247) - same course, so the Sprint 2
        // prerequisite re-check (which only re-validates NEWLY selected courses, not ones already
        // part of the student's current enrollment) doesn't block it. Section 2247 shares active
        // hor_dia=016003 + turdet_hora 11:45/12:30 with the student's already-selected section 2261
        // (plancur 321), producing a genuine schedule conflict without introducing a new course.
        boolean swapped = false;
        for (MbMatricula curso : manager.getListaCursos()) {
            if (curso.getSeccionId() == 2248) {
                curso.setSeccionId(2247);
                swapped = true;
            }
        }
        assertTrue("expected to find the student's section 2248 (plancur 313) to swap", swapped);

        manager.matricular(actionEvent);

        assertTrue(manager.getOncomplete().contains("dupHorario"));
        // FIXME sometime return 1 or 2 horarios duplicados
        // error caused by the structure of the subquery
        // aggretors in select using the group of sec_id but if selected randomly
        // assertEquals(2, manager.getListaDuplicadoHor().size());

        assertNotEquals(0, manager.getListaDuplicadoHor().get(0).getDupId());
        assertFalse(manager.getListaDuplicadoHor().get(0).getDupHora().isEmpty());
        assertFalse(manager.getListaDuplicadoHor().get(0).getDupDia().isEmpty());
        assertFalse(manager.getListaDuplicadoHor().get(0).getDupNombreCurso().isEmpty());
        assertFalse(manager.getListaDuplicadoHor().get(0).getDupNombreCursos().isEmpty());
    }

    @Test
    public void matricularBlocksRectificationWithDisallowedNewCourseTest() {
        // Same student as openModalForRectificacionDeMatriculaTest (alu_id 2080) - an "ingresante"
        // with no Acta history yet, so cursosAbiertos() only offers their existing cycle-1 course
        // set. Forcing one selection to section 2021 (plancur 44, FISICA II, cycle 006002) swaps in
        // a course this student has no prerequisite standing for - the Sprint 2 Academic Validation
        // step must block this before it ever reaches capacity/schedule checks.
        manager.setStudentId(2080);
        manager.setRowSemestreId(SEMESTRE_2010_2_ID);
        manager.setParamEspId(Especialidad.ING_ELECTRONICA);

        manager.rectificarMatricula();
        manager.getListaCursos().get(0).setSeccionId(2021);

        manager.matricular(actionEvent);

        assertTrue(manager.getOncomplete().contains("prerrequisitos"));
        assertEquals(0, manager.getRowMatriculaId());
    }

    @Test
    public void matricularAllowsUnchangedPreMatriculaCoursesTest() {
        // Confirming an existing Pre-Matricula into a real (Regular) enrollment also writes
        // through matricular() (it always persists MatTipo=REGULAR regardless of what preceded
        // it). The prerequisite re-check must recognize courses already locked into that
        // Pre-Matricula as "not new" - otherwise every Pre-Matricula confirmation that lands in
        // cursosAbiertos()'s "show what's already selected" branch would be wrongly blocked, since
        // the exclusion set only checked findRegularEnrollment before this fix. alu_id 2827 has a
        // real active Pre-Matricula (mat_id 2645, SEMESTRE_2009_1_ID) with these exact sections.
        int alumnoId = 2827;
        List<Integer> blockingTarifaIds = Arrays.asList(59131, 59132, 59133, 59134, 59135);
        DAO.updateAlumnoTarifaEstado(blockingTarifaIds, "2");
        try {
            manager.setStudentId(alumnoId);
            manager.setRowSemestreId(SEMESTRE_2009_1_ID);
            manager.setParamEspId(3);
            manager.setRowTipoMatriculaId("022005"); // Pre-Matricula being confirmed

            List<MbMatricula> cursos = new ArrayList<>();
            for (int seccionId : new int[]{464, 456, 462, 454, 460, 458}) {
                var curso = new MbMatricula();
                curso.setSeccionId(seccionId);
                cursos.add(curso);
            }
            manager.setListaCursos(cursos);

            manager.matricular(actionEvent);

            assertFalse("unchanged Pre-Matricula courses must not be re-blocked by the prerequisite check",
                    manager.getOncomplete().contains("prerrequisitos"));
            assertNotEquals(0, manager.getRowMatriculaId());
        } finally {
            DAO.updateAlumnoTarifaEstado(blockingTarifaIds, "0");
        }
    }

    @Test
    public void duplicidadHorariaIsScopedBySemesterTest() {
        // Sections 2021 (ISI02MA FISICA II) and 1872 (ISI04MA METODOLOGIA DE LA INVESTIGACION
        // CIENTIFICA) belong to SEMESTRE_2010_2_ID and are the known overlapping pair forced in
        // openModalForRectificacionDeMatriculaTest. Before the fix, sem_id was hardcoded to 9
        // (SEMESTRE_2010_2_ID's literal value) inside the query, so any semestreId argument was
        // ignored and the conflict was always detected/missed regardless of what was passed in.
        List<Integer> conflictingSections = Arrays.asList(2021, 1872);

        List<BeanDuplicadoHorario> withCorrectSemester = horarioDAO.duplicidadHoraria(conflictingSections, SEMESTRE_2010_2_ID);
        assertFalse("conflict must be detected when querying the semester these sections actually belong to",
                withCorrectSemester.isEmpty());

        List<BeanDuplicadoHorario> withDifferentSemester = horarioDAO.duplicidadHoraria(conflictingSections, SEMESTRE_2009_2_ID);
        assertTrue("conflict must NOT be reported when querying a different semester (proves sem_id is no longer hardcoded)",
                withDifferentSemester.isEmpty());
    }

    @Test
    public void verDetalleDeudaTest() {
        // Student code 08010236
        String studentId = "2314";

        UIParameter uiParameterP_id = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameterP_id);
        when(uiParameterP_id.getValue()).thenReturn(studentId);

        manager.verDetalleDeuda(actionEvent);

        assertEquals(5, manager.getDetalleDeuda().size());
        MbMatricula deuda = manager.getDetalleDeuda().get(0);
        assertFalse(deuda.getMpRowDeudaSemestre().isEmpty());
        assertFalse(deuda.getMpRowDeudaConceptoPago().isEmpty());
        assertFalse(deuda.getMpRowDeudaFechaPago().isEmpty());
        assertFalse(deuda.getMpRowDeudaFechaProrroga().isEmpty());
    }

    @Test
    public void openModalforAmpliacionesTest() {
        MbMatricula row = new MbMatricula();
        row.setStudentId(4879);
        row.setRowSemestreId(SEMESTRE_2010_2_ID);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("student", row));

        manager.openModalforAmpliaciones(actionEvent);

        assertEquals("0", manager.getDetCursoCreditos());

        // ------------------------------------------------------------------
        AcAmpliaciones ampl = DAO.insertAcAmpliacion(row.getStudentId(), row.getRowSemestreId());

        manager.openModalforAmpliaciones(actionEvent);

        assertEquals(ampl.getAmpCreditos(), manager.getDetCursoCreditos());

        DAO.deleteAcAmpliacion(ampl);
    }
}
