package net.uch.academica.service;

import net.uch.mapping.AcMatricula;

import java.util.List;
import java.util.Map;

/**
 * Use-case service for student enrollment (matricula). Replaces the bean-level pattern of opening
 * one {@code Session} by hand and threading it through several DAO calls (no {@code try}/{@code
 * finally}, leaked on exception) with proper {@code @Transactional} boundaries managed by Spring.
 * Also makes the create-then-update-type writes (regular registration, reservation) atomic - they
 * were two independent DAO-level transactions before this service existed.
 */
public interface EnrollmentService {

    /**
     * Reads a student's regular, reserved, and pre-enrollment records for {@code semesterId} in a
     * single transaction.
     */
    EnrollmentSnapshot findEnrollmentSnapshot(int studentId, int semesterId);

    /**
     * Batched form of {@link #findEnrollmentSnapshot}: reads every {@code studentIds} student's
     * regular/reserved/pre-enrollment records for {@code semesterId} in 3 queries total (not 3 per
     * student), grouped by student id. Every id in {@code studentIds} is present in the result,
     * mapped to an empty-lists snapshot if the student has no enrollment records.
     */
    Map<Integer, EnrollmentSnapshot> findEnrollmentSnapshots(List<Integer> studentIds, int semesterId);

    /**
     * Reads a student's pre-enrollment records for {@code semesterId}.
     */
    List<AcMatricula> findPreEnrollment(int studentId, int semesterId);

    /**
     * Reads a student's regular enrollment records for {@code semesterId}.
     */
    List<AcMatricula> findRegularEnrollment(int studentId, int semesterId);

    /**
     * Saves {@code matricula} and, if {@code previousMatriculaType} identifies a regular or
     * pre-enrollment record being rectified, stamps the newly-saved record's type accordingly - in
     * one transaction. Returns the saved record's id.
     */
    int registerEnrollment(AcMatricula matricula, String previousMatriculaType);

    /**
     * Saves {@code matricula} as a reservation and, if {@code matriculaIdToCancel} identifies an
     * existing record, marks it as cancelled-by-reservation - in one transaction. Returns the saved
     * record's id.
     */
    int reserveEnrollment(AcMatricula matricula, int matriculaIdToCancel);
}
