package net.uch.administrativa.service;

import java.util.Date;

/**
 * Payment extensions (prórrogas) for internal ({@code ad_alumno_tarifa}) tariffs.
 *
 * <p>Registering an extension is two writes that must stay together: it appends a row to the
 * {@code ad_prorroga} history and syncs the tariff's denormalized effective deadline
 * ({@code alutar_fecha_prorroga}) - the single value every reader consumes (the enrollment debt
 * gate here, {@code sigu_caja}'s mora, {@code sigu_intranet}'s debt view). The latest registered
 * extension is always the active one (highest {@code pro_numero}), even when its date is earlier
 * than a previous extension. See {@code docs/modules/academica.md}.
 *
 * @author cesardl
 */
public interface ProrrogaService {

    /**
     * Message surfaced when {@link #isValidCommitmentDate(Date)} fails. Shared by the single-grant
     * and the massive-block paths so both reject a bad commitment date with the identical text -
     * the rule must not diverge just because one path edits many rows (issue #33 review).
     */
    String INVALID_COMMITMENT_DATE_MESSAGE = "La nueva fecha de compromiso debe ser posterior a hoy.";

    /**
     * Registers a new payment extension for a tariff: validates the commitment date, appends an
     * auto-numbered {@code ad_prorroga} history row, and sets {@code alutar_fecha_prorroga} to
     * {@code newDate}.
     *
     * @param alutarId the {@code ad_alumno_tarifa} id
     * @param newDate  the new commitment date (must be strictly after today)
     * @param comment  operator note stored in the history
     * @param usuId    the granting user's id
     * @throws ProrrogaValidationException if {@code newDate} is null or not after today
     */
    void registerExtension(int alutarId, Date newDate, String comment, int usuId) throws ProrrogaValidationException;

    /**
     * The commitment-date rule: a new extension date must be strictly after today (minimum
     * today + 1 day); there is no maximum. Exposed so the UI can pre-validate before registering.
     */
    boolean isValidCommitmentDate(Date newDate);
}
