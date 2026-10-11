package net.uch.common.security;

/**
 * Module access tokens used for authorization.
 *
 * <p>Each constant is a {@code men_accion} navigation outcome — the same string stored in
 * {@code tb_menu.men_accion} for a leaf menu item and declared as a {@code from-outcome} in
 * {@code faces-config.xml}, mapping 1:1 to a screen. A role's granted modules (see
 * {@code HSMenuDAO.findGrantedModules}) are a set of these tokens; a report or resource servlet
 * declares the token of the screen that owns it, and access is granted only when the user's role
 * holds that token (or the user is the god-admin).
 */
public final class Modules {

    /**
     * Student maintenance — {@code /Academica/Alumnos.jsp}.
     */
    public static final String ALUMNOS = "Alumnos";
    /**
     * Enrollment — {@code /Academica/Matricula.jsp}.
     */
    public static final String MATRICULA = "Matricula";
    /**
     * Grades — {@code /Academica/Notas.jsp}.
     */
    public static final String NOTAS = "Notas";
    /**
     * Credit transfer / boletas — {@code /Academica/Convalidacion.jsp}.
     */
    public static final String CONVALIDACION = "Convalidacion";
    /**
     * Payments / cashier — {@code /Administrativa/Pagos.jsp}.
     */
    public static final String PAGOS = "Pagos";
    /**
     * Per-student payment structure ("Por Alumno") — {@code /Administrativa/EdicionEstructPago.jsp}.
     */
    public static final String EDICION_ESTRUCT_PAGO = "EdicionEstructPago";
    /**
     * Bank payment-file processing — {@code /Banco/ProcesoBanco.jsp}.
     */
    public static final String PROCESO_BANCO = "ProcesoBanco";
    /**
     * Free-courses students — {@code /CursoLibre/AlumnosCl.jsp}.
     */
    public static final String ALUMNOS_CL = "AlumnosCL";
    /**
     * Free-courses grades — {@code /CursoLibre/NotasCL.jsp}.
     */
    public static final String NOTAS_CL = "NotasCL";
    /**
     * Activities catalog — {@code /TablaSistema/Actividades.jsp}.
     */
    public static final String ACTIVIDADES = "Actividades";

    private Modules() {
    }
}
