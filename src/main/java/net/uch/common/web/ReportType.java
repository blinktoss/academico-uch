package net.uch.common.web;

import net.uch.common.security.Modules;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Registry of PDF reports exposed through {@link ReportServlet}.
 *
 * <p>Each entry knows its compiled Jasper template (fixed, or resolved per request), the download
 * file name, whether its output is immutable (and therefore cacheable), and how to build its Jasper
 * parameters from the request parameters. Adding a report is a single enum constant — no servlet
 * change required.
 */
enum ReportType {

    MATRICULA_FICHA("matricula-ficha", "/WEB-INF/Reportes/ficha_matricula.jasper", "ficha_matricula", false, Modules.MATRICULA) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) throws IOException {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logo", logo(context));
            parameters.put("mat_id", requiredInt(request, "matId"));
            return parameters;
        }
    },

    MATRICULA_CONSOLIDADO("matricula-consolidado", "/WEB-INF/Reportes/consolidado_matricula.jasper", "consolidado_matricula", false, Modules.MATRICULA) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) throws IOException {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logo", logo(context));
            parameters.put("mat_id", requiredInt(request, "matId"));
            parameters.put("subreport", context.getRealPath("/") + "/WEB-INF/Reportes/sub_consolidado_matricula.jasper");
            return parameters;
        }
    },

    /**
     * Grade sheet for a whole section. One report key, but the Jasper template is chosen by the
     * section-code prefix (one template per faculty), so the path is resolved per request.
     */
    NOTAS_SECCION("notas-seccion", null, "notas_seccion", false, Modules.NOTAS) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) {
            // FIXME: the grades shown by notas-seccion are inconsistent with what's entered through
            // the grade-input flow (MbNota). Each per-faculty notas_de_todos_alumnos_*.jasper
            // carries its own embedded SQL / average calculation, which can diverge from how MbNota
            // registers and computes grades (e.g. evaluation weights, sustitutorio handling).
            // To fix later: decompile the relevant template with .ai/jasper-tool, compare its query
            // against the grade model, and reconcile (ideally move the calculation out of the
            // template and pass a datasource). Deferred - see the Jasper/report tooling notes.
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("SECCION", requiredString(request, "seccion"));
            return parameters;
        }

        @Override
        String resolveJasperPath(HttpServletRequest request) {
            String seccion = requiredString(request, "seccion");
            if (seccion.startsWith("IS")) {
                return "/WEB-INF/Reportes/notas_de_todos_alumnos_sistemas.jasper";
            } else if (seccion.startsWith("IE")) {
                return "/WEB-INF/Reportes/notas_de_todos_alumnos_electronica.jasper";
            } else if (seccion.startsWith("CF")) {
                return "/WEB-INF/Reportes/notas_de_todos_alumnos_contabilidad.jasper";
            } else if (seccion.startsWith("EP")) {
                return "/WEB-INF/Reportes/notas_de_todos_alumnos_educacion.jasper";
            } else if (seccion.startsWith("EN")) {
                return "/WEB-INF/Reportes/notas_de_todos_alumnos_enfermeria.jasper";
            }
            throw new IllegalArgumentException("Unsupported section prefix: " + seccion);
        }
    },

    ALUMNO_COMPROBANTE("alumno-comprobante", "/WEB-INF/Reportes/comprobante.jasper", "comprobante", false, Modules.ALUMNOS) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) {
            Map<String, Object> parameters = new HashMap<>();
            // FIXME: comprobante.jasper internally runs `SELECT * FROM ac_alumno LIMIT 0,1`, so it
            // ignores this aluId and always renders the first student — the report is effectively
            // broken. The Jasper template query must filter by aluId. Longer term we want to explore
            // an alternative PDF-generation strategy, since Jasper is too limited for current needs.
            // TODO: create a GitHub issue to fix the query and evaluate a Jasper replacement.
            parameters.put("aluId", requiredInt(request, "aluId"));
            return parameters;
        }
    },

    CONVALIDACION_BOLETA("convalidacion-boleta", "/WEB-INF/Reportes/boleta_notas_codigo.jasper", "boleta_notas", false, Modules.CONVALIDACION) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) throws IOException {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("logo", logo(context));
            parameters.put("alu_codigo", requiredString(request, "aluCodigo"));
            // FIXME: hardcoded placeholder semester. boleta_notas_codigo needs a SEMESTER_ID, and
            // "8" only happens to "work" for this scenario — it is not a real business value. The
            // correct semester is undecided: either (a) the student's admission semester (per row)
            // or (b) the current active semester (from app context). Tracked as a GitHub issue.
            parameters.put("sem_id", "8");
            return parameters;
        }
    },

    NOTASCL_SECCION("notascl-seccion", "/WEB-INF/Reportes/cl_notas_seccion.jasper", "notas_cl_seccion", false, Modules.NOTAS_CL) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("SEC_ID", requiredInt(request, "secId"));
            return parameters;
        }
    },

    HISTORIAL_PAGOS("historial-pagos", "/WEB-INF/Reportes/historial_pagos.jasper", "historial_pagos", false, Modules.EDICION_ESTRUCT_PAGO) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) throws IOException {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("LOGO", logo(context));
            parameters.put("ALU_CODIGO", requiredString(request, "aluCodigo"));
            parameters.put("SEM_ID", requiredInt(request, "semId"));
            return parameters;
        }
    },

    /**
     * Daily cash-register summary. Identity comes from a date range + the logged-in cashier, not a
     * row id, so the trigger builds this URL from form state (see {@code MbPago.imprimirListado}).
     */
    CUADRE_CAJA("cuadre-caja", "/WEB-INF/Reportes/cuadre_caja_diario.jasper", "cuadre_caja_diario", false, Modules.PAGOS) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fec_ini", requiredString(request, "fecIni"));
            parameters.put("fec_fin", requiredString(request, "fecFin"));
            parameters.put("usu_id", requiredInt(request, "usuId"));
            return parameters;
        }
    },

    /**
     * Bank/external-partner payment-import preview. The Jasper query takes no parameters — it
     * reports on whatever batch was just processed server-side — so this is the only report with
     * no required request parameters at all.
     */
    BANCO_RESPUESTA("banco-respuesta", "/WEB-INF/Reportes/data_respuesta_banco.jasper", "data_respuesta_banco", false, Modules.PROCESO_BANCO) {
        @Override
        Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) {
            return new HashMap<>();
        }
    };

    private static final String LOGO_PATH = "/Imagenes/actions/logo_p.jpg";

    private final String key;
    private final String jasperPath;
    private final String fileName;
    private final boolean cacheable;
    private final String requiredModule;

    ReportType(String key, String jasperPath, String fileName, boolean cacheable, String requiredModule) {
        this.key = key;
        this.jasperPath = jasperPath;
        this.fileName = fileName;
        this.cacheable = cacheable;
        this.requiredModule = requiredModule;
    }

    static Optional<ReportType> fromKey(String key) {
        return Stream.of(values())
                .filter(type -> type.key.equals(key))
                .findFirst();
    }

    static URL logo(ServletContext context) throws IOException {
        return context.getResource(LOGO_PATH);
    }

    static int requiredInt(HttpServletRequest request, String name) {
        return Integer.parseInt(requiredString(request, name));
    }

    static String requiredString(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameter: " + name);
        }
        return value;
    }

    /**
     * Builds the Jasper parameter map for this report, validating required request parameters.
     *
     * @throws IllegalArgumentException if a required parameter is missing or malformed (→ HTTP 400)
     */
    abstract Map<String, Object> buildParameters(HttpServletRequest request, ServletContext context) throws IOException;

    /**
     * Resolves the compiled Jasper template for this request. Defaults to the fixed template; reports
     * whose template depends on request parameters override this.
     *
     * @throws IllegalArgumentException if the request does not map to a known template (→ HTTP 400)
     */
    String resolveJasperPath(HttpServletRequest request) {
        return jasperPath;
    }

    String fileName() {
        return fileName;
    }

    boolean isCacheable() {
        return cacheable;
    }

    /**
     * @return the {@code men_accion} module that owns this report; a user may run it only if their
     * role has been granted this module (see {@link net.uch.common.security.Modules}).
     */
    String requiredModule() {
        return requiredModule;
    }
}
