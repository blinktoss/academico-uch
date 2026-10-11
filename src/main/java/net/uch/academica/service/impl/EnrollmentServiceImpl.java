package net.uch.academica.service.impl;

import net.uch.academica.dao.HSMatriculaDAO;
import net.uch.academica.service.EnrollmentService;
import net.uch.academica.service.EnrollmentSnapshot;
import net.uch.academica.service.NoVacancyException;
import net.uch.mapping.AcMatricula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.uch.common.Constants.CAT_TIPO_MATRICULA_PRE_MATRICULA;
import static net.uch.common.Constants.CAT_TIPO_MATRICULA_REGULAR;

/**
 * Defined (and constructor-injected with the springHibernateDaoMatricula proxy) explicitly in
 * applicationContext-services.xml, same as GradeRecordServiceImpl - no component-scan on this
 * Spring 3.2 / Java 14 stack. DAO calls join this service's transaction via PROPAGATION_REQUIRED.
 */
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final HSMatriculaDAO matriculaDao;

    public EnrollmentServiceImpl(HSMatriculaDAO matriculaDao) {
        this.matriculaDao = matriculaDao;
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentSnapshot findEnrollmentSnapshot(int studentId, int semesterId) {
        return new EnrollmentSnapshot(
                matriculaDao.seleccionarMatricularRegular(studentId, semesterId),
                matriculaDao.seleccionarMatriculaReserva(studentId, semesterId),
                matriculaDao.seleccionarPrematricula(studentId, semesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, EnrollmentSnapshot> findEnrollmentSnapshots(List<Integer> studentIds, int semesterId) {
        if (studentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Integer, List<AcMatricula>> regularByStudent = groupByStudent(
                matriculaDao.seleccionarMatricularRegular(studentIds, semesterId));
        Map<Integer, List<AcMatricula>> reservedByStudent = groupByStudent(
                matriculaDao.seleccionarMatriculaReserva(studentIds, semesterId));
        Map<Integer, List<AcMatricula>> preEnrollmentByStudent = groupByStudent(
                matriculaDao.seleccionarPrematricula(studentIds, semesterId));

        Map<Integer, EnrollmentSnapshot> snapshots = new HashMap<>();
        for (Integer studentId : studentIds) {
            snapshots.put(studentId, new EnrollmentSnapshot(
                    regularByStudent.getOrDefault(studentId, Collections.emptyList()),
                    reservedByStudent.getOrDefault(studentId, Collections.emptyList()),
                    preEnrollmentByStudent.getOrDefault(studentId, Collections.emptyList())));
        }
        return snapshots;
    }

    private Map<Integer, List<AcMatricula>> groupByStudent(List<AcMatricula> matriculas) {
        return matriculas.stream().collect(Collectors.groupingBy(m -> m.getAlu().getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcMatricula> findPreEnrollment(int studentId, int semesterId) {
        return matriculaDao.seleccionarPrematricula(studentId, semesterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcMatricula> findRegularEnrollment(int studentId, int semesterId) {
        return matriculaDao.seleccionarMatricularRegular(studentId, semesterId);
    }

    @Override
    @Transactional
    public int registerEnrollment(AcMatricula matricula, String previousMatriculaType) {
        ensureVacancies(matricula);
        matriculaDao.registrarMatricula(matricula);
        int matriculaId = matricula.getId();
        if (CAT_TIPO_MATRICULA_REGULAR.equals(previousMatriculaType)) {
            matriculaDao.actualizarTipoMatricula(AcMatricula.Type.MATRICULA_RECTIFICADA, matriculaId);
        } else if (CAT_TIPO_MATRICULA_PRE_MATRICULA.equals(previousMatriculaType)) {
            matriculaDao.actualizarTipoMatricula(AcMatricula.Type.PRE_MATRICULA_RECTIFICADA, matriculaId);
        }
        LOG.info("Enrollment {} registered, previous type '{}'", matriculaId, previousMatriculaType);
        return matriculaId;
    }

    /**
     * Authoritative capacity gate (BUG-02). The UI pre-check counts seats outside this
     * transaction, so two simultaneous submissions can both observe the last vacancy.
     * Locking the section rows first serializes competing enrollments; the recount then
     * sees whatever the transaction that held the lock committed. Sections are locked in
     * ascending id order so concurrent multi-section enrollments cannot deadlock.
     */
    private void ensureVacancies(AcMatricula matricula) {
        List<Integer> sectionIds = matricula.getAcMatriculaCursos().stream()
                .map(mc -> mc.getSec().getId())
                .sorted()
                .collect(Collectors.toList());
        if (sectionIds.isEmpty()) {
            return;
        }
        Map<Integer, Integer> capacities = matriculaDao.lockSectionCapacities(sectionIds);
        int studentId = matricula.getAlu().getId();
        for (Integer sectionId : sectionIds) {
            int enrolled = matriculaDao.numeroMatriculadosporSeccionSinAlumno(sectionId, studentId);
            if (capacities.getOrDefault(sectionId, 0) - enrolled <= 0) {
                LOG.warn("Section {} is full ({} enrolled), enrollment for student {} rejected",
                        sectionId, enrolled, studentId);
                throw new NoVacancyException(sectionId);
            }
        }
    }

    @Override
    @Transactional
    public int reserveEnrollment(AcMatricula matricula, int matriculaIdToCancel) {
        matriculaDao.registrarMatricula(matricula);
        int matriculaId = matricula.getId();
        if (matriculaIdToCancel != 0) {
            matriculaDao.actualizarTipoMatricula(AcMatricula.Type.MATRICULA_ANULADA_RESERVA, matriculaIdToCancel);
        }
        LOG.info("Enrollment {} reserved, previous matricula {} cancelled", matriculaId, matriculaIdToCancel);
        return matriculaId;
    }
}
