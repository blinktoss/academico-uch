package net.uch.configuracion.dao;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.common.ServiceFinder;
import net.uch.mapping.TbUsuario;
import net.uch.util.HSTestSupportDao;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test for {@code HSUsuarioDAOImpl} (login-lockout mechanics and the user list), run
 * against the development DB.
 *
 * <p>Exercised on {@code cajero1} (a real, test-unreferenced user). Because this writes to a shared
 * dev-DB row, {@link #restore()} always resets the user's attempt counter and lock flag afterwards.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class HSUsuarioDAOTest extends SpringConfigurationTest {

    /**
     * Mirrors {@code HSUsuarioDAOImpl.MAX_FAILED_ATTEMPTS}.
     */
    private static final int THRESHOLD = 5;
    private static final String USERNAME = "cajero1";

    @Autowired
    private HSTestSupportDao testSupportDao;

    private HSUsuarioDAO dao() {
        return ServiceFinder.findBean("springHibernateDaoUsuario", HSUsuarioDAO.class);
    }

    @After
    public void restore() {
        dao().storeResetAttempts(Stubs.USUARIO_CAJERO1_ID);
    }

    @Test
    public void reachingThresholdLocksAccount() {
        HSUsuarioDAO dao = dao();
        dao.storeResetAttempts(Stubs.USUARIO_CAJERO1_ID);
        assertFalse(dao.isLocked(USERNAME));

        for (int i = 0; i < THRESHOLD - 1; i++) {
            dao.storeFailedAttempt(Stubs.USUARIO_CAJERO1_ID);
        }
        assertFalse("should not lock before the threshold", dao.isLocked(USERNAME));

        dao.storeFailedAttempt(Stubs.USUARIO_CAJERO1_ID);
        assertTrue("should lock at the threshold", dao.isLocked(USERNAME));
    }

    @Test
    public void resetClearsLock() {
        HSUsuarioDAO dao = dao();
        for (int i = 0; i < THRESHOLD; i++) {
            dao.storeFailedAttempt(Stubs.USUARIO_CAJERO1_ID);
        }
        assertTrue(dao.isLocked(USERNAME));

        dao.storeResetAttempts(Stubs.USUARIO_CAJERO1_ID);
        assertFalse(dao.isLocked(USERNAME));
    }

    @Test
    public void validateUserRejectsLockedAccount() {
        HSUsuarioDAO dao = dao();
        for (int i = 0; i < THRESHOLD; i++) {
            dao.storeFailedAttempt(Stubs.USUARIO_CAJERO1_ID);
        }
        // Even with any password, a locked account cannot log in.
        assertNull(dao.validateUser(USERNAME, "any-password"));
    }

    @Test
    public void getListUsuarioSkipsRowWithUndecryptablePassword() {
        String originalPassword = testSupportDao.getUsuario(Stubs.USUARIO_CAJERO1_ID).getUsuPassword();
        // Not valid Base64 (hyphens aren't in the alphabet), so decrypt() is guaranteed to fail -
        // simulates the malformed usu_password rows that used to crash this method outright.
        testSupportDao.updateUsuarioPassword(Stubs.USUARIO_CAJERO1_ID, "not-a-valid-jasypt-ciphertext");

        try {
            List<TbUsuario> result = dao().getListUsuario(USERNAME, 0);
            assertTrue("row with an undecryptable password must be skipped instead of crashing the list",
                    result.stream().noneMatch(u -> USERNAME.equals(u.getUsuUsuario())));
        } finally {
            testSupportDao.updateUsuarioPassword(Stubs.USUARIO_CAJERO1_ID, originalPassword);
        }
    }
}
