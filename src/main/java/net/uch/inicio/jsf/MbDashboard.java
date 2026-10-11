package net.uch.inicio.jsf;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.academica.dao.HSMatriculaDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.AcSemestre;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * Backing bean for the post-login admin dashboard (Bienvenida.jsp).
 * Slice 1 exposes the current semester plus registered vs. enrolled student counts.
 * <p>
 * Created for sistema_academico on 23/06/2026.
 *
 * @author Cesardl
 */
public class MbDashboard {

    private static final Logger LOG = LoggerFactory.getLogger(MbDashboard.class);

    private static final String NO_SEMESTER = "N/D";

    private final HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
    private final HSAlumnoDAO daoAlumno = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
    private final HSMatriculaDAO daoMatricula = ServiceFinder.findBean("springHibernateDaoMatricula", HSMatriculaDAO.class);

    private String currentSemesterName;
    private long registeredStudents;
    private long enrolledStudents;

    @PostConstruct
    public void init() {
        BeanUser user = ServiceFinder.getLoggedUser();
        if (user == null) {
            throw new UserNotFoundException();
        }
        loadIndicators();
    }

    private void loadIndicators() {
        AcSemestre current = findCurrentSemester();
        currentSemesterName = current == null ? NO_SEMESTER : current.getSemNombre();
        registeredStudents = daoAlumno.getActiveStudentCount();
        enrolledStudents = current == null ? 0L : daoMatricula.getEnrolledStudentCount(current.getId());
        LOG.info("Dashboard indicators -> semester: '{}', registered: {}, enrolled: {}",
                currentSemesterName, registeredStudents, enrolledStudents);
    }

    private AcSemestre findCurrentSemester() {
        try {
            return daoSemestre.seleccionarSemestreActual();
        } catch (RuntimeException e) {
            LOG.warn("Could not resolve a single current semester (expected exactly one with sem_actual = '1')", e);
            return null;
        }
    }

    public String getCurrentSemesterName() {
        return currentSemesterName;
    }

    public long getRegisteredStudents() {
        return registeredStudents;
    }

    public long getEnrolledStudents() {
        return enrolledStudents;
    }

    public String getRegisteredStudentsLabel() {
        return String.format(Locale.US, "%,d", registeredStudents);
    }

    public String getEnrolledStudentsLabel() {
        return String.format(Locale.US, "%,d", enrolledStudents);
    }

    public int getEnrollmentRate() {
        if (registeredStudents == 0L) {
            return 0;
        }
        return Math.round((enrolledStudents * 100f) / registeredStudents);
    }
}
