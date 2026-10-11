package net.uch.common.web;

import net.uch.common.security.Modules;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Guards the report → owning-module mapping that {@link ReportServlet} relies on to authorize each
 * report. A missing or wrong module would silently mis-gate a report.
 */
public class ReportTypeTest {

    @Test
    public void everyReportDeclaresARequiredModule() {
        for (ReportType type : ReportType.values()) {
            assertNotNull("Report " + type + " has no required module", type.requiredModule());
        }
    }

    @Test
    public void reportsMapToTheirOwningScreenModule() {
        assertEquals(Modules.MATRICULA, ReportType.MATRICULA_FICHA.requiredModule());
        assertEquals(Modules.MATRICULA, ReportType.MATRICULA_CONSOLIDADO.requiredModule());
        assertEquals(Modules.NOTAS, ReportType.NOTAS_SECCION.requiredModule());
        assertEquals(Modules.ALUMNOS, ReportType.ALUMNO_COMPROBANTE.requiredModule());
        assertEquals(Modules.CONVALIDACION, ReportType.CONVALIDACION_BOLETA.requiredModule());
        assertEquals(Modules.NOTAS_CL, ReportType.NOTASCL_SECCION.requiredModule());
        assertEquals(Modules.EDICION_ESTRUCT_PAGO, ReportType.HISTORIAL_PAGOS.requiredModule());
        assertEquals(Modules.PAGOS, ReportType.CUADRE_CAJA.requiredModule());
        assertEquals(Modules.PROCESO_BANCO, ReportType.BANCO_RESPUESTA.requiredModule());
    }
}
