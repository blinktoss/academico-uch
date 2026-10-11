package net.uch.academica.service;

/**
 * Thrown by the authoritative capacity gate when an enrollment reaches persistence and a
 * selected section has no seats left (BUG-02): the UI pre-check runs outside the enrollment
 * transaction, so two simultaneous submissions can both observe the last vacancy. Unchecked
 * so the service transaction rolls back and no partial enrollment is written.
 */
public class NoVacancyException extends RuntimeException {

    private final int sectionId;

    public NoVacancyException(int sectionId) {
        super("No vacancy left in section " + sectionId);
        this.sectionId = sectionId;
    }

    public int getSectionId() {
        return sectionId;
    }
}
