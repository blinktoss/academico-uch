package net.uch.administrativa.service;

/**
 * Raised when a payment-extension (prórroga) commitment date breaks the business rule: it must be
 * strictly after today (minimum today + 1 day). Checked on purpose so the caller surfaces the
 * message to the operator instead of silently swallowing an invalid commitment.
 *
 * @author cesardl
 */
public class ProrrogaValidationException extends Exception {

    public ProrrogaValidationException(String message) {
        super(message);
    }
}
