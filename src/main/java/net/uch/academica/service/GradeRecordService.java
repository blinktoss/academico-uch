package net.uch.academica.service;

import net.uch.mapping.AcActa;

/**
 * Use-case service for publishing official grade records (Actas). It owns the publish lifecycle
 * rule - a record can only be published while no published one exists for its section/type - and
 * runs the guard check together with the write in a single transaction, so the check and the
 * persist can't be interleaved with a concurrent publish (the check-then-write was two separate
 * transactions when this lived inline in the managed bean).
 */
public interface GradeRecordService {

    /**
     * Publishes the principal grade record for the section carried by {@code record}
     * ({@code record.getSec().getId()}). If the record's number is still the pending placeholder,
     * it's assigned from the generated id after the first save.
     *
     * @throws GradeRecordAlreadyPublishedException if a published principal record already exists.
     */
    void publishPrincipal(AcActa record);

    /**
     * Publishes the substitute grade record for the section carried by {@code record}, replacing
     * any existing (non-published) substitute record for that section in the same transaction.
     *
     * @throws GradeRecordAlreadyPublishedException if a published substitute record already exists.
     */
    void publishSubstitute(AcActa record);
}
