package net.uch.configuracion.dao;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.common.ServiceFinder;
import net.uch.mapping.TbUsuario;
import net.uch.util.HSTestSupportDao;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * CHARACTERIZATION TEST — Login/lockout ordering and password storage (critical flow #5: auth).
 *
 * <p>Complements {@code HSUsuarioDAOTest} (threshold/reset/locked-reject) by pinning the CURRENT
 * ordering rules of {@code HSUsuarioDAOImpl.validateUser}:
 *
 * <ul>
 *   <li><b>Lock is checked before the password</b> — a locked account with the CORRECT password
 *       still gets {@code null}, and the correct attempt neither logs in nor resets the counter
 *       (only an admin/reset path clears the lock).</li>
 *   <li><b>Non-vigente accounts:</b> the correct password returns {@code null} (vigente gate)
 *       WITHOUT counting a failed attempt; a wrong password on that same non-vigente account DOES
 *       increment the counter — so a disabled account can still progress toward lockout.</li>
 * </ul>
 *
 * <p>SUSPICIOUS / SECURITY OBSERVATION (captured as-is, flagged NOT fixed — see delta item b28):
 * to install a known password this test simply instantiates jasypt's
 * {@code StandardPBEStringEncryptor} with the same hardcoded key the production DAO compiles in.
 * That this works from test code demonstrates the storage is <b>reversible encryption with a
 * hardcoded key</b>, not hashing. Accepted legacy debt for this training project (uch-env#20) —
 * do not "fix" as a side effect.
 *
 * <p>Fixture: cajero1 (usu_id 17), the dedicated lockout-test user. Password, vigente flag,
 * attempts and lock are snapshotted and restored after every test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LoginLockoutOrderCharacterizationTest extends SpringConfigurationTest {

    private static final String USERNAME = "cajero1";
    private static final String KNOWN_PASSWORD = "characterization-pw";
    private static final int THRESHOLD = 5; // mirrors HSUsuarioDAOImpl.MAX_FAILED_ATTEMPTS

    private static HSTestSupportDao DAO;

    private String originalEncryptedPassword;
    private String originalVigente;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    private HSUsuarioDAO dao() {
        return ServiceFinder.findBean("springHibernateDaoUsuario", HSUsuarioDAO.class);
    }

    @Before
    public void snapshotAndInstallKnownPassword() {
        TbUsuario original = DAO.getUsuario(Stubs.USUARIO_CAJERO1_ID);
        originalEncryptedPassword = original.getUsuPassword();
        originalVigente = original.getUsuVigente();

        // Same algorithm + same hardcoded key as HSUsuarioDAOImpl's constructor — reproducible
        // from outside the production code, which is precisely the b28 finding.
        var encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("jasypt");
        DAO.updateUsuarioPassword(Stubs.USUARIO_CAJERO1_ID, encryptor.encrypt(KNOWN_PASSWORD));

        dao().storeResetAttempts(Stubs.USUARIO_CAJERO1_ID);
    }

    @After
    public void restoreFixture() {
        DAO.updateUsuarioPassword(Stubs.USUARIO_CAJERO1_ID, originalEncryptedPassword);
        DAO.updateUsuarioVigente(Stubs.USUARIO_CAJERO1_ID, originalVigente);
        dao().storeResetAttempts(Stubs.USUARIO_CAJERO1_ID);
    }

    @Test
    public void correctPasswordSucceedsOnHealthyAccountTest() {
        // Sanity pin for the fixture: with the known password installed, login works and the
        // success path resets any prior attempts.
        dao().storeFailedAttempt(Stubs.USUARIO_CAJERO1_ID);
        TbUsuario result = dao().validateUser(USERNAME, KNOWN_PASSWORD);

        assertNotNull(result);
        assertEquals(Integer.valueOf(0),
                DAO.getUsuario(Stubs.USUARIO_CAJERO1_ID).getUsuFailedAttempts());
    }

    @Test
    public void lockIsCheckedBeforePasswordSoCorrectPasswordDoesNotUnlockTest() {
        for (int i = 0; i < THRESHOLD; i++) {
            dao().storeFailedAttempt(Stubs.USUARIO_CAJERO1_ID);
        }
        assertTrue(dao().isLocked(USERNAME));

        // CURRENT ORDER (HSUsuarioDAOImpl:48-51): the lock short-circuits before the password is
        // even compared — correct credentials neither authenticate nor reset the counter.
        assertNull("locked account must reject even the correct password",
                dao().validateUser(USERNAME, KNOWN_PASSWORD));
        assertTrue("correct password must NOT clear the lock", dao().isLocked(USERNAME));
        assertEquals(Integer.valueOf(THRESHOLD),
                DAO.getUsuario(Stubs.USUARIO_CAJERO1_ID).getUsuFailedAttempts());
    }

    @Test
    public void nonVigenteAccountRejectsCorrectPasswordWithoutCountingAnAttemptTest() {
        DAO.updateUsuarioVigente(Stubs.USUARIO_CAJERO1_ID, "0");

        // CURRENT RULE (HSUsuarioDAOImpl:55-64): passwordMatches==true but vigente=='0' →
        // null, and because the increment branch keys on !passwordMatches, NO attempt is counted.
        assertNull(dao().validateUser(USERNAME, KNOWN_PASSWORD));
        Integer attempts = DAO.getUsuario(Stubs.USUARIO_CAJERO1_ID).getUsuFailedAttempts();
        assertTrue("correct password on non-vigente account must not count an attempt",
                attempts == null || attempts == 0);

        // A WRONG password on the same non-vigente account DOES count — so an already-disabled
        // account can still accumulate attempts and end up locked. Odd but current behavior
        // (flagged, not fixed): the two disable mechanisms compound.
        assertNull(dao().validateUser(USERNAME, "definitely-wrong"));
        assertEquals(Integer.valueOf(1),
                DAO.getUsuario(Stubs.USUARIO_CAJERO1_ID).getUsuFailedAttempts());
    }
}
