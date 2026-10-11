package net.uch.academica.service;

import net.uch.common.service.ServiceException;

/**
 * Raised when an attempt is made to publish a grade record (Acta) for a section/type that already
 * has a published one. Published records are immutable; corrections go through the substitute
 * record flow, not by overwriting.
 */
public class GradeRecordAlreadyPublishedException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public GradeRecordAlreadyPublishedException(String message) {
        super(message);
    }
}
