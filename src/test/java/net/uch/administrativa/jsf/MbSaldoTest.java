package net.uch.administrativa.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 14/10/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbSaldoTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private static boolean CONFIGURED = true;
    private MbSaldo manager;

    @AfterClass
    public static void cleanUpDatabase() {
        DAO.cleanAdSaldo();
    }

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbSaldo();
        manager.init();

        if (CONFIGURED) {
            DAO.insertAdSaldos();
            CONFIGURED = false;
        }
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void seleccionarTest() {
        manager.setParamStudentCode("10201003");

        manager.seleccionar(actionEvent);

        assertFalse(manager.getSaldoList().isEmpty());

        MbSaldo row = manager.getSaldoList().get(0);

        assertFalse(row.getRowCodigo().isEmpty());
        assertFalse(row.getRowLargeName().isEmpty());
        assertNotNull(row.getRowRecordDate());
        assertNotEquals(0, row.getRowMonto(), 0);
    }
}
