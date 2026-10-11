package net.uch.academica.service;

import net.uch.mapping.AcMatricula;

import java.util.List;

/**
 * Bundles a student's regular/reserved/pre-enrollment records for a given semester, read together
 * in one transaction by {@link EnrollmentService#findEnrollmentSnapshot}.
 */
public class EnrollmentSnapshot {

    private final List<AcMatricula> regular;
    private final List<AcMatricula> reserved;
    private final List<AcMatricula> preEnrollment;

    public EnrollmentSnapshot(List<AcMatricula> regular, List<AcMatricula> reserved, List<AcMatricula> preEnrollment) {
        this.regular = regular;
        this.reserved = reserved;
        this.preEnrollment = preEnrollment;
    }

    public List<AcMatricula> getRegular() {
        return regular;
    }

    public List<AcMatricula> getReserved() {
        return reserved;
    }

    public List<AcMatricula> getPreEnrollment() {
        return preEnrollment;
    }
}
