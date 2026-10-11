package net.uch.cursolibre.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;

import static net.uch.common.Constants.PARAM_SECCION_ID;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 14/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbNotasCLTest extends SpringConfigurationTest {

    private MbNotasCL manager;

    @Before
    public void setUp() {
        manager = new MbNotasCL();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadTreeAndSelectNodeTest() {
        TreeNode<String> result = manager.getArbol();

        assertNotNull(result);
        assertNotNull(manager.getNodos());

        String nodeId = "2:1:1:1";
        NodeSelectedEvent nodeSelectedEvent = mock(NodeSelectedEvent.class);
        UITree tree = mock(UITree.class);

        when(nodeSelectedEvent.getComponent()).thenReturn(tree);
        when(tree.getRowKey()).thenReturn(nodeId);

        manager.seleccion(nodeSelectedEvent);

        assertNotEquals(0, manager.getLblTalapeId());
        assertFalse(manager.getLblModNombre().isEmpty());
        assertFalse(manager.getLblCurNombre().isEmpty());
        assertFalse(manager.getLblTalapeNombre().isEmpty());
        assertEquals(5, manager.getListaTalleres().size());
    }

    @Test
    public void asignarNotasTest() {
        UIParameter uiSec = mock(UIParameter.class);
        UIParameter uiTalape = mock(UIParameter.class);
        HtmlPanelGrid htmlPanel = mock(HtmlPanelGrid.class);
        HtmlOutputText htmlOutputText = mock(HtmlOutputText.class);
        HtmlInputText htmlInputText = mock(HtmlInputText.class);
        HtmlInputHidden htmlInputHidden = mock(HtmlInputHidden.class);

        manager.setHtmlPanel(htmlPanel);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent(PARAM_SECCION_ID)).thenReturn(uiSec);
        when(uiSec.getValue()).thenReturn("19");
        when(uiComponent.findComponent("p_talape_id")).thenReturn(uiTalape);
        when(uiTalape.getValue()).thenReturn("10");

        when(app.createComponent(HtmlOutputText.COMPONENT_TYPE)).thenReturn(htmlOutputText);
        when(app.createComponent(HtmlInputText.COMPONENT_TYPE)).thenReturn(htmlInputText);
        when(app.createComponent(HtmlInputHidden.COMPONENT_TYPE)).thenReturn(htmlInputHidden);

        manager.asignarNotas(actionEvent);

        assertEquals(19, manager.getMpSecId());
        assertEquals(10, manager.getMpTalapeId());
        assertEquals(30, manager.getRows().size());
        assertEquals(2, manager.getColumns().size());
    }
}
