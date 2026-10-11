package org.richfaces;

import net.uch.util.ContextMocker;
import org.junit.Before;
import org.junit.Test;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 17/02/2024.
 *
 * @author Cesardl
 */
public class SkinBeanTest {

    private SkinBean bean;
    private FacesContext facesContext;
    private ExternalContext externalContext;

    @Before
    public void setUp() {
        bean = new SkinBean();
        facesContext = ContextMocker.mockFacesContext();
        externalContext = mock(ExternalContext.class);
    }

    @Test
    public void bindingAndChangeSkinTest() {
        Map<String, Object> requestMap = new HashMap<>();
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRequestMap()).thenReturn(requestMap);

        UIComponent result = bean.getComponent();

        assertTrue(result instanceof UISelectOne);

        String currentSkin = "blueSky";

        assertEquals(currentSkin, bean.getSkin());

        currentSkin = "plain";
        UISelectOne uic = mock(UISelectOne.class);
        when(uic.getValue()).thenReturn(currentSkin);
        bean.setComponent(uic);

        assertEquals(bean.getComponent(), uic);

        bean.change();

        assertEquals(currentSkin, bean.getSkin());
    }
}
