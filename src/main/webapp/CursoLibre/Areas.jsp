<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Configuraci&oacute;n de Areas</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
                <tr>
                    <td width="100%" colspan="10">
                        <table cellpadding="0" cellspacing="0" border="0" style="width: 100%;">
                            <tr>
                                <td align="left"><h:outputText value="MANTENIMIENTO DE &Aacute;REAS" escape="false"
                                                               style="font-weight: bold;"/></td>
                                <td align="right">
                                    <a4j:status>
                                        <f:facet name="start">
                                            <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                                        </f:facet>
                                    </a4j:status>
                                    <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;"
                                                    title="Nuevo">
                                        <a4j:support event="onclick" actionListener="#{managerCLArea.nuevo}"
                                                     oncomplete="#{managerCLArea.oncomplete}"
                                                     reRender="iCodArea,iCodDescripcion"/>
                                    </h:graphicImage>
                                    <h:commandButton image="/Imagenes/actions/viewmag.png"
                                                     action="#{managerCLArea.seleccionar}" title="Buscar"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr style="height: 20px;">
                    <td colspan="10">
                        <hr size="1">
                    </td>
                </tr>
                <tr>
                    <td width="10%">Descripci&oacute;n :</td>
                    <td colspan="9" align="left"><h:inputText id="b_descripcion"
                                                              value="#{managerCLArea.paramDescripcion}"
                                                              style="width: 300px;"/></td>
                </tr>
                <tr style="height: 20px;">
                    <td colspan="10">
                        <hr size="1">
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form id="form3">
            <table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
                <tr>
                    <td colspan="10" valign="top" align="center">
                        <rich:datascroller id="paginacion" align="right" for="tablaMaster" maxPages="10"
                                           style="width: 90%"/>
                        <rich:dataTable id="tablaMaster" width="90%" rows="10"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#FFFFFF'"
                                        cellpadding="0" cellspacing="0"
                                        value="#{managerCLArea.areas}" var="area">
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{area.rowAreId}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{area.rowAreCodigo}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Descripci&oacute;n" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{area.rowAreDescripcion}"/>
                            </rich:column>
                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Editar"/>
                                </f:facet>
                                <f:param id="id_update" value="#{area.rowAreId}"/>
                                <a4j:commandButton image="/Imagenes/actions/editpaste.png"
                                                   title="Modificar"
                                                   actionListener="#{managerCLArea.actualizarArea}"
                                                   oncomplete="#{managerCLArea.oncomplete}"
                                                   reRender="iCodArea,iCodDescripcion"/>
                            </rich:column>
                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Eliminar"/>
                                </f:facet>
                                <f:param id="id_delete" value="#{area.rowAreId}"/>
                                <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                                 actionListener="#{managerCLArea.eliminarArea}"
                                                 action="#{managerCLArea.seleccionar}"
                                                 onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                            </rich:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mp1" autosized="true" zindex="3000" width="450" height="120">
        <f:facet name="header">
            <h:outputText value="&Aacute;rea" escape="false"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp1')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form2">
            <table style="width: 100%">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           title="Guardar"
                                           actionListener="#{managerCLArea.guardarArea}"
                                           action="#{managerCLArea.seleccionar}"
                                           oncomplete="#{managerCLArea.oncomplete}"
                                           reRender="tablaMaster,paginacion"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <hr size="1">
                    </td>
                </tr>
                <tr>
                    <td style="width: 70px;">C&oacute;digo</td>
                    <td>
                        <h:inputText id="iCodArea" value="#{managerCLArea.mpAreCodigo}"/>
                    </td>
                </tr>
                <tr>
                    <td>Descripci&oacute;n</td>
                    <td>
                        <h:inputText id="iCodDescripcion" value="#{managerCLArea.mpAreDescripcion}"
                                     style="width: 100%;"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
