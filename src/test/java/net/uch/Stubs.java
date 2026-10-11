package net.uch;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.cursolibre.jsf.MbModulo;

import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created for sistema_academico on 27/12/2022.
 *
 * @author Cesardl
 */
public class Stubs {

    public static final int USUARIO_ADMIN_ID = 1;
    // Roles (tb_roles) used in authorization tests.
    public static final int ROL_CAJA_ID = 2;          // grants Pagos, EdicionEstructPago, AlumnosCL, MatriculaCL (not Alumnos/Notas)
    public static final int ROL_ACADEMICA_ID = 3;     // grants Alumnos, Notas, Matricula, ... (not Pagos)
    public static final int ROL_OPERADOR_ID = 5;      // grants only Alumnos
    // A real, test-unreferenced cashier user used by the lockout integration test (rol = caja).
    public static final int USUARIO_CAJERO1_ID = 17;
    public static final int SEMESTRE_2008_1_ID = 1;
    public static final int SEMESTRE_2009_1_ID = 4;
    public static final int SEMESTRE_2009_2_ID = 6;
    public static final int SEMESTRE_2010_1_ID = 8;
    public static final int SEMESTRE_2010_2_ID = 9;
    public static final int SEMESTRE_TRASLADOS = 5;

    private Stubs() {
    }

    /**
     * @return the god-admin {@link BeanUser} (usu_id = 1), who bypasses module authorization.
     */
    public static BeanUser superAdmin() {
        var user = new BeanUser();
        user.setRowUsuId(USUARIO_ADMIN_ID);
        user.setSuperAdmin(true);
        return user;
    }

    /**
     * @param modules the granted {@code men_accion} module tokens
     * @return a non-admin {@link BeanUser} whose role grants exactly the given modules
     */
    public static BeanUser userWithModules(String... modules) {
        var user = new BeanUser();
        user.setRowUsuId(99);
        user.setGrantedModules(new HashSet<>(Arrays.asList(modules)));
        return user;
    }

    public static MbModulo beanClModule(String code, String name) {
        MbModulo stub = new MbModulo();
        stub.setMpCurCodigo(code);
        stub.setMpCurNombre(name);
        return stub;
    }

    public static int randomNumeric(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static int randomId(SelectItem[] selectItems) {
        int length = selectItems.length;
        int index = randomNumeric(0, length);
        String s = selectItems[index].getValue().toString();
        return Integer.parseInt(s);
    }

    public static final class Facultad {

        public static final int ING_SISTEMAS = 1;
        public static final int ENFERMERIA = 2;
        public static final int CONTABILIDAD = 3;
        public static final int EDUCACION_PRIMARIA = 4;
        public static final int ING_ELECTRONICA = 5;

        private static final FacultadEspecialidad[] VALUES = {
                new FacultadEspecialidad(ING_SISTEMAS, Especialidad.ING_SISTEMAS, 4),
                new FacultadEspecialidad(ENFERMERIA, Especialidad.ENFERMERIA, 2),
                new FacultadEspecialidad(CONTABILIDAD, Especialidad.CONTABILIDAD, 1),
                new FacultadEspecialidad(EDUCACION_PRIMARIA, Especialidad.EDUCACION_PRIMARIA, 3),
                new FacultadEspecialidad(ING_ELECTRONICA, Especialidad.ING_ELECTRONICA, 5)
        };

        public static FacultadEspecialidad random() {
            return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
        }
    }

    public static final class Especialidad {

        public static final int ING_SISTEMAS = 1;
        public static final int ENFERMERIA = 2;
        public static final int CONTABILIDAD = 3;
        public static final int EDUCACION_PRIMARIA = 4;
        public static final int ING_ELECTRONICA = 5;
    }

    public static class FacultadEspecialidad {

        private final int facultadId;
        private final int especialidadId;
        private final int planId;

        public FacultadEspecialidad(int facultadId, int especialidadId, int planId) {
            this.facultadId = facultadId;
            this.especialidadId = especialidadId;
            this.planId = planId;
        }

        public int getFacultadId() {
            return facultadId;
        }

        public int getEspecialidadId() {
            return especialidadId;
        }

        public int getPlanId() {
            return planId;
        }
    }

    public static final class ModalidadDeIngreso {

        public static final String EXAMEN_GENERAL = "010001";
        public static final String TRASLADO_INTERNO = "010002";
        public static final String TRASLADO_EXTERNO = "010003";
        public static final String PRIMEROS_PUESTOS = "010004";
        public static final String GRADUADOS_O_TITULADOS = "010005";
        public static final String INGRESO_PREFERENCIAL = "010006";

        private static final String[] VALUES = {EXAMEN_GENERAL, TRASLADO_INTERNO, TRASLADO_EXTERNO, PRIMEROS_PUESTOS, GRADUADOS_O_TITULADOS, INGRESO_PREFERENCIAL};

        public static String random() {
            return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
        }
    }
}
