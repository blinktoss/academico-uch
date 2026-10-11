<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<%
    HttpSession sesion = request.getSession();
    if (sesion.getAttribute("usuario") != null) {
        out.println("Sesion Iniciada");
    }
%>
<f:view>
    <!DOCTYPE HTML>
    <html lang="es">
    <head>
        <title>.:: Sistema Academico UCH ::.</title>
        <meta http-equiv="Expires" content="0">
        <meta http-equiv="Last-Modified" content="0">
        <meta http-equiv="Cache-Control" content="no-cache, must-revalidate">
        <meta http-equiv="Pragma" content="no-cache">
        <meta charset="UTF-8"/>
        <link rel="icon" type="image/x_icon" href="/Sistema_Academico/Imagenes/actions/pinguino.gif"/>
    </head>
    <body onload="document.getElementById('formulario:j_username').focus()">
    <a4j:loadStyle src="resource:///css/formulario.css"/>
    <h:form id="formulario">
        <p style="height:150px;" align="center">
        <div align="center">
            <fieldset style="width:350px;">
                <legend>Login</legend>
                <table border="0">
                    <tr>
                        <td colspan="2">
                            <h:panelGroup rendered="#{!managerLogin.mensajes.isEmpty()}"
                                          styleClass="error" layout="block">
                                <h:graphicImage value="/Imagenes/actions/warning_y.gif"/>
                                <h:outputLabel value="#{managerLogin.mensajes}"
                                               escape="false" styleClass="error"/>
                            </h:panelGroup>
                        </td>
                    </tr>
                    <tr>
                        <td><h:outputLabel id="Usuario" value="#{lbl['label.user']}"/></td>
                        <td>
                            <h:inputText id="j_username" size="27" required="true" label="Usuario"
                                         value="#{managerLogin.rowUsuario}"/>
                        </td>
                    </tr>
                    <tr>
                        <td><h:outputLabel id="Contrasenha" value="#{lbl['label.password']}"/></td>
                        <td>
                            <h:inputSecret id="j_password" size="27" required="true" label="Contrasenha"
                                           value="#{managerLogin.rowPsw}"/>
                        </td>
                    </tr>
                    <tr>
                        <td align="center" colspan="2">
                            <div align="right">
                                <h:commandButton value="#{lbl['label.accept']}" action="#{managerLogin.checkUser}"
                                                 styleClass="button"/>
                            </div>
                        </td>
                        <td>
                            <div></div>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </div>
    </h:form>
    </body>
    </html>
</f:view>
