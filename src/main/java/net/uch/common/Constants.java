package net.uch.common;

/**
 * Created for sistema_academico on 13/02/2021.
 *
 * @author Cesardl
 */
public final class Constants {

    // DICTIONARY
    public static final String LABEL_INGRESO = "Ingreso:";
    // PARAMETERS
    public static final String PARAM_ALUMNO_ID = "p_alu_id";
    public static final String PARAM_CODIGO = "p_codigo";
    public static final String PARAM_PATERNO = "p_paterno";
    public static final String PARAM_MATERNO = "p_materno";
    public static final String PARAM_NOMBRE = "p_nombre";
    public static final String PARAM_ALUTAR_ID = "alutar_id";
    public static final String PARAM_SECCION_ID = "p_sec_id";
    public static final String PARAM_SECCION_DETALLE = "p_sec_detalle";

    public static final String ACTION_IMAGES_UP = "/Imagenes/actions/up.png";
    public static final String ACTION_IMAGES_DOWN = "/Imagenes/actions/down.png";
    public static final String ACTION_IMAGES_EDIT_REMOVE = "/Imagenes/actions/edit_remove.png";
    public static final String ACTION_IMAGES_EDIT_REMOVE_GRIS = "/Imagenes/actions/edit_remove_gris.png";
    public static final String ACTION_IMAGES_USER_INFO = "/Imagenes/actions/user-info.png";
    public static final String ACTION_IMAGES_ACTIVAR = "/Imagenes/actions/activar.png";
    public static final String ACTION_IMAGES_DESACTIVAR = "/Imagenes/actions/desactivar.png";
    public static final String ACTION_IMAGES_CERRADO = "/Imagenes/actions/cerrado.png";
    public static final String ACTION_IMAGES_ABIERTO = "/Imagenes/actions/abierto.png";
    public static final String ACTION_IMAGES_EDIT_PASTE = "/Imagenes/actions/editpaste.png";
    public static final String ACTION_IMAGES_EDIT_PASTE_GRIS = "/Imagenes/actions/editpaste_gris.png";

    // TB_CATALOGO
    public static final String CAT_DEFAULT = "000000";

    public static final String CAT_TIPO_ALUMNO_ESTUDIANTE = "009001";

    public static final String CAT_ESTADO_ALUMNO_REGULAR = "011001";

    public static final String CAT_TIPO_CLIENTE_ALUMNO_CP = "014001";
    public static final String CAT_TIPO_CLIENTE_EXTERNO = "014002";
    public static final String CAT_TIPO_CLIENTE_ALUMNO_CL = "014003";

    public static final String CAT_PROCEDENCIA_PAGO_VOUCHER = "015001";
    public static final String CAT_PROCEDENCIA_PAGO_EFECTIVO = "015002";
    public static final String CAT_PROCEDENCIA_PAGO_SALDO = "015003";
    public static final String CAT_PROCEDENCIA_PAGO_VALE = "015004";
    public static final String CAT_PROCEDENCIA_PAGO_BOLETA_VALE = "015005";
    public static final String CAT_PROCEDENCIA_PAGO_TRANSFERENCIA = "015006";
    public static final String CAT_PROCEDENCIA_PAGO_BANCO = "015007";

    public static final String CAT_TIPO_RAZON_SOCIAL_NATURAL = "017001";

    public static final String CAT_CONFIGURACION_MORA_ID = "021004";
    public static final String CAT_CONFIGURACION_MATRICULA_EXTEMPORANEA = "021005";

    public static final String CAT_TIPO_MATRICULA_REGULAR = "022001";
    public static final String CAT_TIPO_MATRICULA_RESERVA = "022002";
    public static final String CAT_TIPO_MATRICULA_RECTIFICADA = "022004";
    public static final String CAT_TIPO_MATRICULA_PRE_MATRICULA = "022005";
    public static final String CAT_TIPO_PRE_MATRICULA_RECTIFICADA = "022006";
    public static final String CAT_TIPO_MATRICULA_ANULADA = "022007";

    public static final String CAT_TIPO_NOTA_PROMEDIO_PARCIAL = "034001";
    public static final String CAT_TIPO_NOTA_PROMEDIO_FINAL = "034002";
    public static final String CAT_TIPO_NOTA_CONVALIDACION = "034003";
    public static final String CAT_TIPO_NOTA_SUSTITUTORIO = "034004";

    public static final String CAT_TIPO_ACTA_PRINCIPAL = "036001";
    public static final String CAT_TIPO_ACTA_SUSTITUTORIA = "036002";
    public static final String CAT_TIPO_ACTA_CONVALIDACION = "036003";

    // act_status values (AcActa lifecycle) - plain status flags, not tb_catalogo codes
    public static final String ACT_STATUS_DRAFT = "DRAFT";
    public static final String ACT_STATUS_PUBLISHED = "PUBLISHED";

    public static final String CAT_ESTADO_BOLETA_VALIDO = "037001";

    public static final String CAT_TIPO_COMPROBANTE_PAGO_BOLETA = "039001";

    public static final String CAT_TIPO_IMPORTACION_NO_IMPORTADO = "051001";
    public static final String CAT_TIPO_IMPORTACION_IMPORTADO = "051002";

    public static final String LABEL_EMPTY = "--";
    public static final String ALERT_REQUIRED_FIELDS = "alert('Ingrese todos los datos requeridos.')";

    private Constants() {
    }
}
