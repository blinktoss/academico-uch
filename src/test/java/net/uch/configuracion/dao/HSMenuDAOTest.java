package net.uch.configuracion.dao;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.common.ServiceFinder;
import net.uch.common.security.Modules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for {@link HSMenuDAO#findGrantedModules(int)}, run against the development DB:
 * the role grant set is the source of truth that drives servlet/report authorization.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class HSMenuDAOTest extends SpringConfigurationTest {

    private HSMenuDAO daoMenu() {
        return ServiceFinder.findBean("springHibernateDaoMenu", HSMenuDAO.class);
    }

    @Test
    public void cajaRoleGrantsPaymentModulesButNotStudentScreens() {
        Set<String> modules = daoMenu().findGrantedModules(Stubs.ROL_CAJA_ID);

        assertTrue(modules.contains(Modules.PAGOS));
        assertTrue(modules.contains(Modules.EDICION_ESTRUCT_PAGO));
        assertTrue(modules.contains(Modules.ALUMNOS_CL));
        // The cashier has no academic screens — this is the cascade that forbids the student report.
        assertFalse(modules.contains(Modules.ALUMNOS));
        assertFalse(modules.contains(Modules.NOTAS));
        assertFalse(modules.contains(Modules.MATRICULA));
    }

    @Test
    public void operadorRoleGrantsOnlyStudents() {
        Set<String> modules = daoMenu().findGrantedModules(Stubs.ROL_OPERADOR_ID);

        assertTrue(modules.contains(Modules.ALUMNOS));
        assertFalse(modules.contains(Modules.PAGOS));
    }
}
