package net.uch.configuracion.jsf;

import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSMenuDAO;
import net.uch.mapping.TbMenu;
import org.richfaces.component.html.HtmlDropDownMenu;
import org.richfaces.component.html.HtmlMenuGroup;
import org.richfaces.component.html.HtmlMenuItem;
import org.richfaces.component.html.HtmlToolBar;

import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import java.util.List;

/**
 * @author cesardl
 */
public class MbMenu {

    private HtmlToolBar toolBar = new HtmlToolBar();
    private int rolId;
    private String usuario;

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public HtmlToolBar getToolBar() {
        return toolBar;
    }

    public void setToolBar(HtmlToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public void armarMenu() {
        HSMenuDAO daoMenu = ServiceFinder.findBean("springHibernateDaoMenu", HSMenuDAO.class);
        List<TbMenu> menus = daoMenu.seleccionToolBar(this.getRolId());

        List<UIComponent> childrenToolBar = getToolBar().getChildren();
        childrenToolBar.clear();

        var app = FacesContext.getCurrentInstance().getApplication();

        HtmlCommandButton homeButton = (HtmlCommandButton) app.createComponent(HtmlCommandButton.COMPONENT_TYPE);
        homeButton.setActionExpression(crearMetodo(app, "Bienvenida"));
        homeButton.setImage("/Imagenes/actions/home.png");
        homeButton.setStyle("border: none; background: transparent; cursor: pointer; vertical-align: middle");
        childrenToolBar.add(homeButton);

        HtmlMenuGroup hmg = (HtmlMenuGroup) app.createComponent(HtmlMenuGroup.COMPONENT_TYPE);
        HtmlDropDownMenu hddm;

        List<UIComponent> childrenMenuGroup = hmg.getChildren();
        childrenMenuGroup.clear();

        HtmlGraphicImage hgi;
        HtmlOutputText hot;
        HtmlPanelGroup hpg;
        for (TbMenu menu : menus) {
            hddm = (HtmlDropDownMenu) app.createComponent(HtmlDropDownMenu.COMPONENT_TYPE);

            List<UIComponent> childrenDropDownMenu = hddm.getChildren();
            childrenDropDownMenu.clear();

            hpg = (HtmlPanelGroup) app.createComponent(HtmlPanelGroup.COMPONENT_TYPE);
            hpg.setStyle("display: flex; align-items: center;");

            hgi = (HtmlGraphicImage) app.createComponent(HtmlGraphicImage.COMPONENT_TYPE);
            hgi.setValue(menu.getMenAccion());
            hpg.getChildren().add(hgi);

            hot = (HtmlOutputText) app.createComponent(HtmlOutputText.COMPONENT_TYPE);
            hot.setValue(menu.getMenDescripcion());
            hot.setStyle("padding-left: 5px");
            hpg.getChildren().add(hot);

            hddm.getFacets().put("label", hpg);

            childrenDropDownMenu.addAll(generarHijos(app, menu.getMenId(), this.getRolId(), childrenMenuGroup));

            childrenToolBar.add(hddm);
        }
        hot = (HtmlOutputText) app.createComponent(HtmlOutputText.COMPONENT_TYPE);
        hot.setEscape(false);
        hot.setValue("&nbsp;Usuario: &nbsp;&nbsp;" + this.getUsuario());
        childrenToolBar.add(hot);
    }

    public List<UIComponent> generarHijos(Application app, int padreId, int rolId, List<UIComponent> children) {
        HSMenuDAO dao = ServiceFinder.findBean("springHibernateDaoMenu", HSMenuDAO.class);
        List<TbMenu> h = dao.seleccionHijos(padreId, rolId);

        for (TbMenu m : h) {
            if ("1".equalsIgnoreCase(m.getMenOpc())) {
                HtmlMenuGroup hmg = (HtmlMenuGroup) app.createComponent(HtmlMenuGroup.COMPONENT_TYPE);
                hmg.setValue(m.getMenDescripcion());

                List<UIComponent> l = generarHijos(app, m.getMenId(), rolId, hmg.getChildren());
                hmg.getChildren().addAll(l);

                children.add(hmg);
            } else if ("2".equalsIgnoreCase(m.getMenOpc())) {
                HtmlMenuItem hmi = (HtmlMenuItem) app.createComponent(HtmlMenuItem.COMPONENT_TYPE);
                hmi.setActionExpression(crearMetodo(app, m.getMenAccion()));
                hmi.setSubmitMode("server");
                hmi.setValue(m.getMenDescripcion());
                children.add(hmi);
            }
        }
        return children;
    }

    private MethodExpression crearMetodo(Application application, String action) {
        return application.getExpressionFactory().
                createMethodExpression(FacesContext.getCurrentInstance().getELContext(),
                        action, null, new Class<?>[0]);
    }
}
