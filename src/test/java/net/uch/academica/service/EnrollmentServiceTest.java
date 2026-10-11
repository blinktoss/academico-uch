package net.uch.academica.service;

import net.uch.SpringConfigurationTest;
import net.uch.academica.dao.HSMatriculaDAO;
import net.uch.common.ServiceFinder;
import net.uch.mapping.AcAlumno;
import net.uch.mapping.AcMatricula;
import net.uch.mapping.AcMatriculaCurso;
import net.uch.mapping.AcSeccion;
import net.uch.util.HSTestSupportDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static net.uch.Stubs.SEMESTRE_2010_2_ID;
import static net.uch.common.Constants.CAT_TIPO_MATRICULA_REGULAR;
import static org.junit.Assert.*;

/**
 * Integration tests for the authoritative capacity gate in registerEnrollment (BUG-02).
 * Runs against the live dev DB: the section's capacity is shrunk around each test so the
 * last-seat scenarios are deterministic, and restored afterwards.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class EnrollmentServiceTest extends SpringConfigurationTest {

    private static final int SECTION_ID = 1829; // ISI01MA-6
    private static final int STUDENT_A = 2663;
    private static final int STUDENT_B = 2098;
    private final List<Integer> createdMatriculaIds = new ArrayList<>();
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private HSTestSupportDao testSupportDao;
    private int originalCapacity;
    private int alreadyEnrolled;

    @Before
    public void captureSectionState() {
        originalCapacity = seccionDao().seleccionarSeccion(SECTION_ID).getSecVacantes();
        alreadyEnrolled = matriculaDao().numeroMatriculadosporSeccionSinAlumno(SECTION_ID, 0);
    }

    @After
    public void restoreSectionState() {
        for (Integer matriculaId : createdMatriculaIds) {
            testSupportDao.deleteAcMatricula(matriculaId);
        }
        createdMatriculaIds.clear();
        testSupportDao.updateSecVacantes(SECTION_ID, originalCapacity);
    }

    @Test
    public void registerEnrollmentRejectsFullSectionTest() {
        testSupportDao.updateSecVacantes(SECTION_ID, alreadyEnrolled); // zero seats left

        try {
            int id = enrollmentService.registerEnrollment(buildEnrollment(STUDENT_A), "0");
            createdMatriculaIds.add(id);
            fail("expected NoVacancyException for a full section");
        } catch (NoVacancyException e) {
            assertEquals(SECTION_ID, e.getSectionId());
        }
    }

    @Test
    public void concurrentEnrollmentsClaimLastSeatExactlyOnceTest() throws InterruptedException {
        testSupportDao.updateSecVacantes(SECTION_ID, alreadyEnrolled + 1); // exactly one seat

        var startSignal = new CountDownLatch(1);
        var done = new CountDownLatch(2);
        var successes = new AtomicInteger();
        var rejections = new AtomicInteger();

        for (int studentId : new int[]{STUDENT_A, STUDENT_B}) {
            new Thread(() -> {
                try {
                    startSignal.await();
                    int id = enrollmentService.registerEnrollment(buildEnrollment(studentId), "0");
                    synchronized (createdMatriculaIds) {
                        createdMatriculaIds.add(id);
                    }
                    successes.incrementAndGet();
                } catch (NoVacancyException e) {
                    rejections.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        startSignal.countDown();
        assertTrue("enrollment threads did not finish", done.await(30, TimeUnit.SECONDS));

        assertEquals("exactly one enrollment must claim the last seat", 1, successes.get());
        assertEquals("the loser must be rejected with NoVacancyException", 1, rejections.get());
        assertEquals(alreadyEnrolled + 1, matriculaDao().numeroMatriculadosporSeccionSinAlumno(SECTION_ID, 0));
    }

    private AcMatricula buildEnrollment(int studentId) {
        var matricula = new AcMatricula();
        matricula.setMatActivo("1");
        matricula.setMatFecha(new Date());
        matricula.setUsuId(17);
        matricula.setSemId(SEMESTRE_2010_2_ID);
        matricula.setMatTipo(CAT_TIPO_MATRICULA_REGULAR);

        var alumno = new AcAlumno();
        alumno.setId(studentId);
        matricula.setAlu(alumno);

        var matriculaCurso = new AcMatriculaCurso();
        matriculaCurso.setMat(matricula);
        matriculaCurso.setMatcurActivo("1");
        var seccion = new AcSeccion();
        seccion.setId(SECTION_ID);
        matriculaCurso.setSec(seccion);

        Set<AcMatriculaCurso> cursos = new LinkedHashSet<>();
        cursos.add(matriculaCurso);
        matricula.setAcMatriculaCursos(cursos);
        return matricula;
    }

    private HSMatriculaDAO matriculaDao() {
        return ServiceFinder.findBean("springHibernateDaoMatricula", HSMatriculaDAO.class);
    }

    private net.uch.academica.dao.HSSeccionDAO seccionDao() {
        return ServiceFinder.findBean("springHibernateDaoSeccion", net.uch.academica.dao.HSSeccionDAO.class);
    }
}
