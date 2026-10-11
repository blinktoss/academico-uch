package net.uch;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.audit.CurrentUserContext;
import net.uch.util.ContextMocker;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 07/06/2020.
 *
 * @author Cesardl
 */
@ContextConfiguration({
        "file:src/main/webapp/WEB-INF/applicationContext-hibernate.xml",
        "classpath:hibernateContextForTest.xml"
})
@WebAppConfiguration
public class SpringConfigurationTest {

    protected final FacesContext facesContext = ContextMocker.mockFacesContext();

    protected ExternalContext externalContext;
    protected ServletContext servletContext;
    protected Application app;
    protected HttpSession session;
    protected ActionEvent actionEvent;
    protected UIComponent uiComponent;
    protected UIParameter uiParameter;
    protected BeanUser loggerUser;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() {
        externalContext = mock(ExternalContext.class);
        app = mock(Application.class);
        session = mock(HttpSession.class);
        actionEvent = mock(ActionEvent.class);
        uiComponent = mock(UIComponent.class);
        uiParameter = mock(UIParameter.class);
        servletContext = mock(ServletContext.class);

        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(facesContext.getApplication()).thenReturn(app);
        when(externalContext.getContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(wac);

        loggerUser = new BeanUser();
        when(externalContext.getSession(false)).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(loggerUser);
    }

    @After
    public void setDown() {
        // In production AuditUserFilter clears this per request; mirror that here so a
        // user id stamped by one test never leaks into the next on the same thread.
        CurrentUserContext.clear();
        facesContext.release();
    }
}
