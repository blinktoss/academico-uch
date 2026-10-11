<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Mantenimiento de Pabellones</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel style="width: 100%;">
        <h:form id="form1" style="width:100%">
            <table width="100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE PABELLONES</strong></td>
                    <td width="30%"></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="../Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td width="30%">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                            <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mp',{top:130})"
                                         actionListener="#{managerPabellon.nuevo}" reRender="mpForm"/>
                        </h:graphicImage>
                    </td>
                    <td align=right width="50%">
                        <a4j:commandButton type="button" id="buscar" title="Buscar"
                                           actionListener="#{managerPabellon.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png"
                                           reRender="barra, tablaMaster"/>
                    </td>

                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td width="20%" colspan="2">

                    </td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                </tr>
                <tr>
                    <td width="10%">Descripci&oacute;n:
                    </td>
                    <td width="30%">
                        <h:inputText value="#{managerPabellon.paramNombre}" style="width : 180px;"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Local:
                    </td>
                    <td width="30%">
                        <h:selectOneMenu value="#{managerPabellon.paramIdLocal}" style="width : 180px;">
                            <f:selectItem itemValue="0" itemLabel="TODOS"/>
                            <f:selectItems value="#{managerPabellon.paramComboLocales}"/>
                        </h:selectOneMenu></td>
                    <td width="50%">
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form style=" width : 100%;">
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            width="100%" border="0" value="#{managerPabellon.listaPabellon}" var="pabellon">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{pabellon.pabellonId}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>

                    <h:outputText value="#{pabellon.paramNombre}" rendered="#{pabellon.view}"/>
                    <h:inputText value="#{pabellon.paramNombre}" rendered="#{pabellon.editable}"
                                 maxlength="45" style="width: 300px;"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{pabellon.pabellonDescription}" rendered="#{pabellon.view}"/>
                    <h:inputText value="#{pabellon.pabellonDescription}" rendered="#{pabellon.editable}"
                                 maxlength="45"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Local"/>
                    </f:facet>

                    <h:outputText value="#{pabellon.rowLocalName}" rendered="#{pabellon.view}"/>
                    <h:selectOneMenu value="#{pabellon.paramIdLocal}" rendered="#{pabellon.editable}">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                        <f:selectItems value="#{managerPabellon.paramComboLocales}"/>
                    </h:selectOneMenu>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton actionListener="#{pabellon.editarFila}"
                                       image="/Imagenes/actions/klipper_dock.png" title="Editar"
                                       rendered="#{pabellon.view}"
                                       reRender="tablaMaster, barra"/>

                    <a4j:commandButton actionListener="#{pabellon.editar}"
                                       image="/Imagenes/actions/filesave.png" title="Guardar"
                                       rendered="#{pabellon.editable}"
                                       reRender="tablaMaster, barra"
                                       oncomplete="#{pabellon.oncomplete}"/>
                    <a4j:commandButton actionListener="#{pabellon.cancelar}"
                                       image="/Imagenes/actions/fileclose.png" title="Cancelar"
                                       rendered="#{pabellon.editable}"
                                       reRender="tablaMaster, barra"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                       oncomplete="Richfaces.showModalPanel('mpEliminar', {top:150})"
                                       reRender="fromEliminar">
                        <f:setPropertyActionListener target="#{managerPabellon.data}" value="#{pabellon}"/>
                    </a4j:commandButton>
                </rich:column>

            </rich:dataTable>

        </h:form>

        <rich:modalPanel id="mpEliminar" width="300" autosized="true" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Eliminar"/>
            </f:facet>
            <h:form id="fromEliminar" style="text-align: center">
                <h4>&iquest;Esta realmente seguro de eliminar el pabell&oacute;n
                    <h:outputText value="#{managerPabellon.data.paramNombre}" style="font-style: italic"/>?
                </h4>
                <a4j:commandButton value="Aceptar" reRender="tablaMaster, barra"
                                   actionListener="#{managerPabellon.eliminar}"
                                   oncomplete="Richfaces.hideModalPanel('mpEliminar')"/>
                <rich:spacer width="20px"/>
                <a4j:commandButton value="Cancelar" onclick="Richfaces.hideModalPanel('mpEliminar')"/>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mp" autosized="true" minWidth="400" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Pabell&oacute;n" escape="false"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
            </f:facet>
            <h:form id="mpForm">
                <table width="100%">
                    <tr>
                        <td align="right" colspan="4">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerPabellon.grabar}"
                                               reRender="tablaMaster, barra"
                                               oncomplete="#{managerPabellon.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td width="30%">*Nombre:</td>
                        <td width="70%">
                            <h:inputText id="nombre" style="width: 180px;" value="#{managerPabellon.modalNombre}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">Descripci&oacute;n:</td>
                        <td width="70%">
                            <h:inputTextarea label="Descripcion" id="codigo" style="width: 180px;"
                                             value="#{managerPabellon.modalDescription}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td>*Local:</td>
                        <td>
                            <h:selectOneMenu value="#{managerPabellon.modalIdLocal}">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerPabellon.paramComboLocales}"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>
    </rich:panel>

</f:view>
</body>
</html>

