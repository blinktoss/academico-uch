<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j" %>

<% HttpSession sesion = request.getSession();
    if (sesion.getAttribute("usuario") == null) {
%>
<jsp:forward page="../index1.jsp"/>
<% }
%>

<f:subview id="subview">
    <a4j:loadStyle src="resource:///css/formulario.css"/>
    <a4j:loadStyle src="resource:///css/richStyle.xcss"/>
    <link rel="icon" type="image/x_icon" href="/Sistema_Academico/Imagenes/actions/pinguino.gif"/>
    <h:form>
        <table style="width: 100%">
            <tr>
                <td>
                    <h:selectOneRadio binding="#{skinBean.component}" style="font-size:10px;"/>
                </td>
                <td align="right">
                    <h:commandLink action="#{skinBean.change}" value="Establecer" style="font-size:10px;"/>
                </td>
            </tr>
        </table>
        <rich:toolBar binding="#{managerLogin.menu.toolBar}" width="100%" style="padding: 1px 0 1px"/>
    </h:form>
</f:subview>
