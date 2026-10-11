package net.uch.autenticacion.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSUsuarioDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.component.html.HtmlDropDownMenu;
import org.richfaces.component.html.HtmlMenuGroup;
import org.richfaces.component.html.HtmlMenuItem;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.el.ExpressionFactory;
import javax.faces.application.Application;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created for sistema_academico on 15/11/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbLoginTest extends SpringConfigurationTest {

    private MbLogin manager;

    @Before
    public void setUp() {
        manager = new MbLogin();
    }

    @Test
    public void checkUserSuccessfulTest() {
        Application app = mock(Application.class);
        HtmlMenuGroup hmg = mock(HtmlMenuGroup.class);
        HtmlDropDownMenu hddm = mock(HtmlDropDownMenu.class);
        HtmlPanelGroup hpg = mock(HtmlPanelGroup.class);
        HtmlGraphicImage hgi = mock(HtmlGraphicImage.class);
        HtmlOutputText hot = mock(HtmlOutputText.class);
        HtmlMenuItem hmi = mock(HtmlMenuItem.class);
        HtmlCommandButton hcb = mock(HtmlCommandButton.class);
        ExpressionFactory ef = mock(ExpressionFactory.class);

        when(externalContext.getSession(false)).thenReturn(session);
        when(facesContext.getApplication()).thenReturn(app);
        when(app.createComponent(HtmlCommandButton.COMPONENT_TYPE)).thenReturn(hcb);
        when(app.createComponent(HtmlMenuGroup.COMPONENT_TYPE)).thenReturn(hmg);
        when(app.createComponent(HtmlDropDownMenu.COMPONENT_TYPE)).thenReturn(hddm);
        when(app.createComponent(HtmlPanelGroup.COMPONENT_TYPE)).thenReturn(hpg);
        when(app.createComponent(HtmlGraphicImage.COMPONENT_TYPE)).thenReturn(hgi);
        when(app.createComponent(HtmlOutputText.COMPONENT_TYPE)).thenReturn(hot);
        when(app.createComponent(HtmlMenuItem.COMPONENT_TYPE)).thenReturn(hmi);
        when(app.getExpressionFactory()).thenReturn(ef);

        manager.setRowUsuario("admin");
        manager.setRowPsw("4dm1n");

        String result = manager.checkUser();

        verify(hmg, times(27)).getChildren();
        verify(hddm, times(8)).getChildren();
        verify(hpg, times(16)).getChildren();
        verify(hgi, times(8)).setValue(any());
        verify(hot, times(9)).setValue(any());
        verify(hmi, times(52)).setValue(any());

        assertEquals("ok", result);
        assertNotNull(manager.getRowUsuario());
        assertNotNull(manager.getRowPsw());
        assertNotNull(manager.getMenu());
        assertTrue(manager.getMensajes().isEmpty());

        // Rol: Caja
        checkUserRolCaja();

        // Rol: Academica
        checkUserRolAcademica();

        // Rol: Operador
        checkUserRolOperador();

        // Rol: Admin Cursos Libres
        checkUserRolCursosLibres();
    }

    private void checkUserRolCaja() {
        manager.setRowUsuario("contabilidad");
        manager.setRowPsw("3v3nt0s");

        String result = manager.checkUser();

        assertEquals("ok", result);
    }

    private void checkUserRolAcademica() {
        manager.setRowUsuario("publica");
        manager.setRowPsw("3v3nt0s");

        String result = manager.checkUser();

        assertEquals("ok", result);
    }

    private void checkUserRolOperador() {
        manager.setRowUsuario("operador");
        manager.setRowPsw("4manzana4");

        String result = manager.checkUser();

        assertEquals("ok", result);
    }

    private void checkUserRolCursosLibres() {
        manager.setRowUsuario("admincl");
        manager.setRowPsw("cl4dm1n");

        String result = manager.checkUser();

        assertEquals("ok", result);
    }

    @Test
    public void checkUserFailedTest() {
        manager.setRowUsuario("matricula1");
        manager.setRowPsw("3v3nt0s");

        String result = manager.checkUser();

        assertEquals("error", result);
        assertNotNull(manager.getRowUsuario());
        assertNotNull(manager.getRowPsw());
        assertFalse(manager.getMensajes().isEmpty());
    }

    @Test
    public void checkUserLockedAccountTest() {
        HSUsuarioDAO daoUsuario = ServiceFinder.findBean("springHibernateDaoUsuario", HSUsuarioDAO.class);
        try {
            for (int i = 0; i < 5; i++) { // HSUsuarioDAOImpl.MAX_FAILED_ATTEMPTS
                daoUsuario.storeFailedAttempt(Stubs.USUARIO_CAJERO1_ID);
            }

            manager.setRowUsuario("cajero1");
            manager.setRowPsw("any-wrong-password");

            String result = manager.checkUser();

            assertEquals("error", result);
            assertTrue(manager.getMensajes().toLowerCase().contains("bloqueada"));
        } finally {
            daoUsuario.storeResetAttempts(Stubs.USUARIO_CAJERO1_ID);
        }
    }
}
