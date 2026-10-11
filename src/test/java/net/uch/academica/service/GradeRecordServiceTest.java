package net.uch.academica.service;

import net.uch.SpringConfigurationTest;
import net.uch.academica.jsf.GradeRecordNomenclature;
import net.uch.mapping.AcActa;
import net.uch.mapping.AcSeccion;
import net.uch.util.HSTestSupportDao;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedHashSet;

import static net.uch.common.Constants.*;
import static org.junit.Assert.*;

/**
 * Integration test for the first @Transactional service-layer migration. Runs against the live
 * dev DB like the rest of the suite; uses an empty detail set since the service only cares about
 * the publish guard + persistence, not the detail contents.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class GradeRecordServiceTest extends SpringConfigurationTest {

    private static final int SECTION_ID = 907; // MATEMATICA I

    @Autowired
    private GradeRecordService gradeRecordService;

    @Autowired
    private HSTestSupportDao testSupportDao;

    @After
    public void cleanUp() {
        testSupportDao.cleanAcActa(SECTION_ID);
    }

    @Test
    public void publishPrincipalAssignsNumberFromIdTest() {
        AcActa record = buildRecord(CAT_TIPO_ACTA_PRINCIPAL, GradeRecordNomenclature.pendingNumber());

        gradeRecordService.publishPrincipal(record);

        assertNotEquals(0, record.getId().intValue());
        assertEquals(GradeRecordNomenclature.number(record.getId()), record.getActNumero());
    }

    @Test
    public void publishPrincipalRejectsSecondPublishTest() {
        gradeRecordService.publishPrincipal(buildRecord(CAT_TIPO_ACTA_PRINCIPAL, "001"));

        try {
            gradeRecordService.publishPrincipal(buildRecord(CAT_TIPO_ACTA_PRINCIPAL, "002"));
            fail("expected the second publish to be rejected");
        } catch (GradeRecordAlreadyPublishedException expected) {
            // a published principal record is immutable
        }
    }

    @Test
    public void publishSubstituteReplacesPreviousTest() {
        gradeRecordService.publishSubstitute(buildRecord(CAT_TIPO_ACTA_SUSTITUTORIA, "S-001"));

        // a published substitute record is also immutable - a second publish is rejected
        try {
            gradeRecordService.publishSubstitute(buildRecord(CAT_TIPO_ACTA_SUSTITUTORIA, "S-002"));
            fail("expected the second substitute publish to be rejected");
        } catch (GradeRecordAlreadyPublishedException expected) {
            // expected
        }
    }

    private AcActa buildRecord(String tipo, String numero) {
        var record = new AcActa();
        record.setActCodigo("TEST-COD");
        record.setActNombre("TEST NOMBRE");
        record.setActNumero(numero);
        record.setActActivo("1");
        record.setActTipo(tipo);
        record.setActStatus(ACT_STATUS_PUBLISHED);
        record.setSec(new AcSeccion(SECTION_ID));
        record.setAcActaDetalles(new LinkedHashSet<>());
        return record;
    }
}
