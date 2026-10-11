package org.richfaces;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectOne;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * Source code recreated from a .class file by IntelliJ IDEA
 * (powered by Fernflower decompiler).
 * Created on 20/07/2019.
 *
 * @author Cesardl
 */
public class SkinBean {

    private static final String REF = "SkinBean";
    private static final String[] SKINS_ARRAY = new String[]{
            "blueSky", "classic", "deepMarine", "DEFAULT", "emeraldTown",
            "japanCherry", "ruby", "wine", "plain", "NULL"
    };
    private String skin;

    public SkinBean() {
        this.skin = "blueSky";
    }

    private UISelectOne createComponent() {
        UISelectOne selectOne = new HtmlSelectOneRadio();
        selectOne.setValue(this.skin);

        for (String skinName : SKINS_ARRAY) {
            var item = new UISelectItem();
            item.setItemLabel(skinName);
            item.setItemValue(skinName);
            item.setId("skinSelectionFor_" + skinName);
            selectOne.getChildren().add(item);
        }

        return selectOne;
    }

    public String getSkin() {
        return this.skin;
    }

    public UIComponent getComponent() {
        var facesContext = FacesContext.getCurrentInstance();
        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
        var object = requestMap.get(REF);
        if (object == null) {
            UISelectOne selectOne = this.createComponent();
            requestMap.put(REF, selectOne);
            return selectOne;
        } else {
            return (UISelectOne) object;
        }
    }

    public void setComponent(UIComponent component) {
        var facesContext = FacesContext.getCurrentInstance();
        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
        requestMap.put(REF, component);
    }

    public String change() {
        UISelectOne selectOne = (UISelectOne) this.getComponent();
        this.skin = (String) selectOne.getValue();
        return null;
    }
}

