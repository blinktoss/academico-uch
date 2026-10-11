package net.uch.common;

import net.uch.autenticacion.jsf.beans.BeanUser;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author chano
 */
public class ServiceFinder {

    private ServiceFinder() {
    }

    /**
     * Busca un bean en el applicationContext-hibernate de Spring
     *
     * @param beanName  a name of bean
     * @param className a class of bean
     * @return a bean from context
     */
    public static <T> T findBean(String beanName, Class<T> className) {
        var servletContext = (ServletContext) FacesContext.getCurrentInstance().
                getExternalContext().getContext();
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        return appContext.getBean(beanName, className);
    }

    public static BeanUser getLoggedUser() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().
                getExternalContext().getSession(false);
        return (BeanUser) session.getAttribute("usuario");
    }

    public static URL getLogo() throws IOException {
        var externalContext = FacesContext.getCurrentInstance().
                getExternalContext();
        return externalContext.getResource("/Imagenes/actions/logo_p.jpg");
    }

    public static String getLabel(List<SelectItem> selectItems, Object id) {
        return selectItems.stream()
                .filter(si -> si.getValue().equals(id))
                .map(SelectItem::getLabel)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
