package net.uch.academica.service.impl;

import net.uch.academica.dao.HSActaDAO;
import net.uch.academica.jsf.GradeRecordNomenclature;
import net.uch.academica.service.GradeRecordAlreadyPublishedException;
import net.uch.academica.service.GradeRecordService;
import net.uch.mapping.AcActa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static net.uch.common.Constants.*;

/**
 * First migration onto the @Transactional service layer. Defined (and constructor-injected with
 * the springHibernateDaoActa proxy) explicitly in applicationContext-services.xml rather than via
 * component-scan, since Spring 3.2's ASM can't read this project's Java 14 bytecode. Its DAO calls
 * join this service's transaction via PROPAGATION_REQUIRED, so the guard read and the write run as
 * one unit.
 */
public class GradeRecordServiceImpl implements GradeRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(GradeRecordServiceImpl.class);

    private final HSActaDAO actaDao;

    public GradeRecordServiceImpl(HSActaDAO actaDao) {
        this.actaDao = actaDao;
    }

    @Override
    @Transactional
    public void publishPrincipal(AcActa record) {
        int sectionId = record.getSec().getId();
        AcActa current = actaDao.seleccionarActa(sectionId, CAT_TIPO_ACTA_PRINCIPAL);
        rejectIfPublished(current, "Acta Principal", sectionId);
        if (current != null) {
            record.setId(current.getId());
        }
        actaDao.saveOrUpdateActa(record);
        if (GradeRecordNomenclature.pendingNumber().equals(record.getActNumero())) {
            record.setActNumero(GradeRecordNomenclature.number(record.getId()));
            actaDao.saveOrUpdateActa(record);
        }
        LOG.info("Acta Principal published {} for section {}", record.getId(), sectionId);
    }

    @Override
    @Transactional
    public void publishSubstitute(AcActa record) {
        int sectionId = record.getSec().getId();
        AcActa current = actaDao.seleccionarActa(sectionId, CAT_TIPO_ACTA_SUSTITUTORIA);
        rejectIfPublished(current, "Acta Sustitutoria", sectionId);
        AcActa toReplace = null;
        if (current != null) {
            toReplace = new AcActa();
            toReplace.setId(current.getId());
        }
        actaDao.reemplazarActaSustitutoria(toReplace, record);
        if (GradeRecordNomenclature.pendingNumber().equals(record.getActNumero())) {
            record.setActNumero(GradeRecordNomenclature.number(record.getId()));
            actaDao.saveOrUpdateActa(record);
        }
        LOG.info("Acta Sustitutoria published {} for section {}", record.getId(), sectionId);
    }

    private void rejectIfPublished(AcActa current, String label, int sectionId) {
        if (current != null && ACT_STATUS_PUBLISHED.equals(current.getActStatus())) {
            throw new GradeRecordAlreadyPublishedException(
                    label + " already published for section " + sectionId);
        }
    }
}
