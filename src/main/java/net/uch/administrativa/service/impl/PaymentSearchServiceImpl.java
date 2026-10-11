package net.uch.administrativa.service.impl;

import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.administrativa.dao.HSConceptoPagoDAO;
import net.uch.administrativa.dao.HSPagoDAO;
import net.uch.administrativa.service.FreeCourseCriteria;
import net.uch.administrativa.service.InternalPaymentCriteria;
import net.uch.administrativa.service.PaymentSearchService;
import net.uch.common.service.PageResult;
import net.uch.cursolibre.dao.HSAlumnoTarifaCLDAO;
import net.uch.mapping.AdAlumnoTarifa;
import net.uch.mapping.AdComprobantePago;
import net.uch.mapping.AdConceptoPago;
import net.uch.mapping.ClAlumnoTarifa;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Wired explicitly (constructor-injected with the DAO proxies) in applicationContext-services.xml,
 * like the enrollment/grade services — no component-scan on this stack. Each method's DAO calls join
 * its transaction via PROPAGATION_REQUIRED, so the page query and its count share one read-only tx.
 */
public class PaymentSearchServiceImpl implements PaymentSearchService {

    /**
     * Payment-type filter value for FREE COURSES (see MbPago; 1 = internal, 2 = external, 3 = CL).
     */
    private static final int PAYMENT_TYPE_FREE_COURSES = 3;

    private final HSAlumnoTarifaDAO alumnoTarifaDao;
    private final HSPagoDAO pagoDao;
    private final HSAlumnoTarifaCLDAO alumnoTarifaCLDao;
    private final HSConceptoPagoDAO conceptoPagoDao;

    public PaymentSearchServiceImpl(HSAlumnoTarifaDAO alumnoTarifaDao, HSPagoDAO pagoDao,
                                    HSAlumnoTarifaCLDAO alumnoTarifaCLDao, HSConceptoPagoDAO conceptoPagoDao) {
        this.alumnoTarifaDao = alumnoTarifaDao;
        this.pagoDao = pagoDao;
        this.alumnoTarifaCLDao = alumnoTarifaCLDao;
        this.conceptoPagoDao = conceptoPagoDao;
    }

    /**
     * 0-based offset of the first row of a 1-based page.
     */
    private static int firstResult(int page, int pageSize) {
        return Math.max(0, (page - 1) * pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AdAlumnoTarifa> searchInternal(InternalPaymentCriteria c, int page, int pageSize) {
        long total = alumnoTarifaDao.contarAlumnoTarifa(c.getCodAlu(), c.getEstadoId(), c.getEspecialidadId(),
                c.getConceptoPagoId(), c.getApPaterno(), c.getApMaterno(), c.getSemestreId());
        List<AdAlumnoTarifa> items = total == 0 ? Collections.emptyList()
                : alumnoTarifaDao.seleccionarAlumnoTarifa(c.getCodAlu(), c.getEstadoId(), c.getEspecialidadId(),
                c.getConceptoPagoId(), c.getApPaterno(), c.getApMaterno(), c.getSemestreId(),
                firstResult(page, pageSize), pageSize);
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AdComprobantePago> searchExternal(Date from, Date to, int conceptId, int page, int pageSize) {
        long total = pagoDao.contarPagosExternos(from, to, conceptId);
        List<AdComprobantePago> items = total == 0 ? Collections.emptyList()
                : pagoDao.seleccionarMostrarPagosExternos(from, to, conceptId, firstResult(page, pageSize), pageSize);
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ClAlumnoTarifa> searchFreeCourse(FreeCourseCriteria c, int page, int pageSize) {
        long total = alumnoTarifaCLDao.contarTarifas(c.getCodAlu(), c.getEstadoId(), c.getConceptoPagoId(),
                c.getApPaterno(), c.getApMaterno());
        List<ClAlumnoTarifa> items = total == 0 ? Collections.emptyList()
                : alumnoTarifaCLDao.buscarTarifas(c.getCodAlu(), c.getEstadoId(), c.getConceptoPagoId(),
                c.getApPaterno(), c.getApMaterno(), firstResult(page, pageSize), pageSize);
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Float> sumValidPaymentsByTarifa(List<Integer> tarifaIds) {
        return pagoDao.sumarPagosValidosPorTarifa(tarifaIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdConceptoPago> findSearchableConcepts(int paymentTypeId) {
        boolean freeCourse = paymentTypeId == PAYMENT_TYPE_FREE_COURSES;
        return conceptoPagoDao.seleccionarConceptosParaBusqueda(freeCourse);
    }
}
