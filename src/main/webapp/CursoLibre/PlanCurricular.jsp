<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Configuraci&oacute;n de Planes Curriculares</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%">
                <tr>
                    <td colspan="2">
                        <h:outputText value="MANTENIMIENTO DE PLANES CURRICULARES" style="font-weight: bold;"/>
                    </td>
                    <td align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>

                        <h:graphicImage value="/Imagenes/actions/filenew.png" title="Nuevo"
                                        style="cursor: pointer;">
                            <a4j:support event="onclick"
                                         actionListener="#{managerCLPlancurricular.nuevo}"
                                         oncomplete="#{managerCLPlancurricular.oncomplete}"
                                         reRender="iCodigo,iDescripcion,iArea,iModulo,iVigente"/>
                        </h:graphicImage>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton image="/Imagenes/actions/viewmag.png"
                                           title="Buscar" oncomplete="#{managerCLPlancurricular.oncomplete}"
                                           actionListener="#{managerCLPlancurricular.seleccionar}"
                                           reRender="tablaMaster, paginacion"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="8%">Descripci&oacute;n:</td>
                    <td>
                        <h:inputText id="b_descripcion" value="#{managerCLPlancurricular.paramDescripcion}"
                                     style="width: 300px;"/>
                    </td>
                </tr>
                <tr>
                    <td>&Aacute;rea:</td>
                    <td>
                        <h:selectOneMenu value="#{managerCLPlancurricular.paramAreaId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLPlancurricular.areas}"/>
                            <a4j:support event="onchange" reRender="b_modulo"
                                         actionListener="#{managerCLPlancurricular.verModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>M&oacute;dulo:</td>
                    <td>
                        <h:selectOneMenu id="b_modulo" value="#{managerCLPlancurricular.paramModId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLPlancurricular.paramModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
            </table>
        </h:form>
        <table style="width: 100%;">
            <tr>
                <td align="center">
                    <h:form id="form3">
                        <rich:datascroller id="paginacion" align="right" for="tablaMaster"
                                           maxPages="10" style="width: 80%;"/>
                        <rich:dataTable id="tablaMaster" cellpadding="0" cellspacing="0" width="80%" rows="10"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        value="#{managerCLPlancurricular.planes}" var="plan">

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{plan.rowPlaCodigo}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Descripci&oacute;n" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{plan.rowPlaDescripcion}"
                                              styleClass="uch-word-wrap" style="width: 400px"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="M&oacute;dulo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{plan.rowModDescripcion}"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Vigente"/>
                                </f:facet>
                                <h:graphicImage value="#{plan.rowImagenPlaVigente}"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Actual"/>
                                </f:facet>
                                <h:graphicImage value="#{plan.rowImagenPlaActual}"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Editar"/>
                                </f:facet>
                                <f:param id="id_update" value="#{plan.rowPlaId}"/>
                                <a4j:commandButton image="/Imagenes/actions/icon_edit.png"
                                                   title="Modificar"
                                                   actionListener="#{managerCLPlancurricular.actualizarPlanCurricular}"
                                                   oncomplete="#{managerCLPlancurricular.oncomplete}"
                                                   reRender="iCodigo,iDescripcion,iArea,iModulo,iVigente"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Eliminar"/>
                                </f:facet>
                                <f:param id="id_delete" value="#{plan.rowPlaId}"/>
                                <h:commandButton image="/Imagenes/actions/delete.gif"
                                                 title="Eliminar"
                                                 actionListener="#{managerCLPlancurricular.eliminarPlanCurricular}"
                                                 onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                            </rich:column>
                        </rich:dataTable>
                    </h:form>
                </td>
            </tr>
        </table>
    </rich:panel>

    <rich:modalPanel id="mpUpdate" autosized="true" width="500" zindex="3000">
        <f:facet name="header">
            <h:outputText value="Plan Curricular"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpUpdate')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form2">
            <table style="width: 100%;">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerCLPlancurricular.guardarPlanCurricular}"
                                           oncomplete="#{managerCLPlancurricular.oncomplete}"
                                           reRender="tablaMaster,paginacion"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td style="width: 80px;">C&oacute;digo</td>
                    <td>
                        <h:inputText id="iCodigo" value="#{managerCLPlancurricular.mpPlaCodigo}"/>
                    </td>
                </tr>
                <tr>
                    <td>Descripci&oacute;n</td>
                    <td>
                        <h:inputText id="iDescripcion" value="#{managerCLPlancurricular.mpPlaDescripcion}"
                                     style="width: 480px"/>
                    </td>
                </tr>
                <tr>
                    <td>&Aacute;rea</td>
                    <td>
                        <h:selectOneMenu id="iArea" value="#{managerCLPlancurricular.mpAreaId}"
                                         disabled="#{managerCLPlancurricular.mpEditDisable}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLPlancurricular.areas}"/>
                            <a4j:support event="onchange" reRender="iModulo"
                                         actionListener="#{managerCLPlancurricular.onChangeModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>M&oacute;dulo</td>
                    <td>
                        <h:selectOneMenu id="iModulo" value="#{managerCLPlancurricular.mpModId}"
                                         disabled="#{managerCLPlancurricular.mpEditDisable}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLPlancurricular.mpModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Estado</td>
                    <td>
                        <h:selectOneMenu id="iVigente" value="#{managerCLPlancurricular.mpPlaVigente}">
                            <f:selectItems value="#{managerCLPlancurricular.mpVigentes}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
