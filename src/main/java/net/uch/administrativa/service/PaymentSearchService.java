package net.uch.administrativa.service;

import net.uch.common.service.PageResult;
import net.uch.mapping.AdAlumnoTarifa;
import net.uch.mapping.AdComprobantePago;
import net.uch.mapping.AdConceptoPago;
import net.uch.mapping.ClAlumnoTarifa;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Read side of the payment-search screen ({@code MbPago}), pulled out of the managed bean so the
 * query + pagination logic lives in a transactional service (the Spring 5 foundation goal of shrinking
 * the god-bean). The bean keeps the JSF view-row mapping; this service owns "which rows, how many, and
 * the page window".
 */
public interface PaymentSearchService {

    /**
     * One page of internal (tariff-linked) payments plus the total count, for the given filters.
     */
    PageResult<AdAlumnoTarifa> searchInternal(InternalPaymentCriteria criteria, int page, int pageSize);

    /**
     * One page of external (unlinked) comprobantes plus the total count, for the date/concept filters.
     */
    PageResult<AdComprobantePago> searchExternal(Date from, Date to, int conceptId, int page, int pageSize);

    /**
     * One page of free-courses (Curso Libre) tariffs plus the total count, for the given filters.
     */
    PageResult<ClAlumnoTarifa> searchFreeCourse(FreeCourseCriteria criteria, int page, int pageSize);

    /**
     * Sum of valid-voucher payments per tariff id (one query), replacing the per-row N+1 the internal
     * listing used to run. Contains only the tariff ids that have at least one such payment.
     */
    Map<Integer, Float> sumValidPaymentsByTarifa(List<Integer> tarifaIds);

    /**
     * Payment concepts for the search screen's concept filter, dependent on the selected payment
     * type: Curso Libre (007003) concepts for FREE COURSES (type 3), everything else otherwise.
     */
    List<AdConceptoPago> findSearchableConcepts(int paymentTypeId);
}
