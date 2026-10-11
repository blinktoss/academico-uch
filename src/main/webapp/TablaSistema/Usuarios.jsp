<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Usuarios</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1" style="width:100%">
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE USUARIOS</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="../Imagenes/actions/loading.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer">
                            <a4j:support oncomplete="Richfaces.showModalPanel('mp',{top:120})"
                                         event="onclick" actionListener="#{managerUsuario.nuevo}"
                                         reRender="password_u,usuario_u,vigente_u,rol_u"/>
                        </h:graphicImage>
                    </td>
                    <td align=right width="50%">
                        <a4j:commandButton type="button" id="buscar"
                                           actionListener="#{managerUsuario.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png"
                                           reRender="formUsuario"
                                           title="Buscar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="20%" colspan="2"></td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                </tr>
                <tr>
                    <td width="10%">Usuario:</td>
                    <td width="30%">
                            <h:inputText value="#{managerUsuario.paramUsuario}"/>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Rol:</td>
                    <td width="30%">
                        <h:selectOneMenu value="#{managerUsuario.paramRolId}">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerUsuario.mpRoles}"/>
                        </h:selectOneMenu>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
            </table>
        </h:form>
        <h:form id="formUsuario">
            <rich:dataTable value="#{managerUsuario.listUsuarios}" var="usu" width="40%"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{usu.rowUsuId}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Usuario"/>
                    </f:facet>
                    <h:outputText value="#{usu.rowUsuario}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Password"/>
                    </f:facet>
                    <h:outputText value="#{usu.rowPsw}"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Vigente"/>
                    </f:facet>
                    <h:graphicImage value="#{usu.rowVigente}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Rol"/>
                    </f:facet>
                    <h:outputText value="#{usu.rowRolTipo.toUpperCase()}"/>
                </rich:column>
                <rich:column style="text-align: center" rendered="#{managerUsuario.superAdmin}">
                    <f:facet name="header">
                        <h:outputText value="Estado"/>
                    </f:facet>
                    <h:graphicImage value="#{usu.rowImgCandado}" rendered="#{!usu.rowLocked}"
                                    style="cursor: default" title="Usuario desbloqueado"/>
                    <h:graphicImage value="#{usu.rowImgCandado}" rendered="#{usu.rowLocked}"
                                    style="cursor: pointer" title="Usuario bloqueado - clic para desbloquear">
                        <a4j:support event="onclick" actionListener="#{managerUsuario.prepararDesbloqueo}"
                                     oncomplete="Richfaces.showModalPanel('mpDesbloquear',{top:150})"
                                     reRender="fromDesbloquear">
                            <f:attribute name="usuarioRow" value="#{usu}"/>
                        </a4j:support>
                    </h:graphicImage>
                </rich:column>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpDesbloquear" width="350" autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Desbloquear Usuario"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer" title="Cerrar"
                            onclick="Richfaces.hideModalPanel('mpDesbloquear')"/>
        </f:facet>
        <h:form id="fromDesbloquear" style="text-align: center">
            <h4>&iquest;Esta seguro de desbloquear al usuario
                <h:outputText value="#{managerUsuario.modalUsuario}" style="font-style: italic"/>?
            </h4>
            <h:outputText value="Intentos fallidos registrados: #{managerUsuario.modalFailedAttempts}"/>
            <br/><br/>
            <a4j:commandButton value="Aceptar" reRender="formUsuario"
                               actionListener="#{managerUsuario.desbloquear}"
                               oncomplete="Richfaces.hideModalPanel('mpDesbloquear')"/>
            <rich:spacer width="20px"/>
            <a4j:commandButton value="Cancelar" onclick="Richfaces.hideModalPanel('mpDesbloquear')"/>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mp" autosized="true" zindex="2000"
                     width="500" height="100">
        <f:facet name="header">
            <h:outputText value="Registro de Usuarios"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <table style="width: 100%">
                <tr>
                    <td align="right" colspan="4">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerUsuario.crearUsuario}"
                                           oncomplete="#{managerUsuario.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="35%" class="tdLabel">Usuario</td>
                    <td width="65%">
                        <h:inputText label="Usuario" id="usuario_u" style=" width : 180px;"
                                     value="#{managerUsuario.rowUsuario}" maxlength="45"/>
                    </td>
                    <td align="right" width="20%">
                    </td>
                </tr>
                <tr>
                    <td width="35%" class="tdLabel">Password</td>
                    <td width="65%">
                        <h:inputText id="password_u" label="Password" value="#{managerUsuario.rowPsw}"
                                     style="width : 180px;"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="35%" class="tdLabel">Vigente</td>
                    <td width="65%">
                        <h:selectBooleanCheckbox id="vigente_u" value="#{managerUsuario.mpVigente}"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="35%" class="tdLabel">Rol</td>
                    <td width="65%">
                        <h:selectOneMenu id="rol_u" value="#{managerUsuario.mpRolId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerUsuario.mpRoles}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
