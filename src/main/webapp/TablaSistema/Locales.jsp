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
    <title>Mantenimiento de Locales</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel style="width: 100%;">
        <h:form id="form1" style="width:100%">
            <table width="100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE LOCALES</strong>
                    </td>
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
                                         actionListener="#{managerLocal.nuevo}" reRender="mpForm"/>
                        </h:graphicImage>
                    </td>
                    <td align=right width="50%">
                        <a4j:commandButton type="button" id="buscar" title="Buscar"
                                           actionListener="#{managerLocal.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png"
                                           reRender="tablaMaster, barra"/>
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
                        <h:inputText value="#{managerLocal.paramDescription}" style="width : 180px;"/></td>
                    <td width="50%"></td>
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
        <h:form>
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            width="100%" border="0" value="#{managerLocal.listaLocal}" var="local">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{local.localId}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{local.paramDescription}" rendered="#{local.view}"/>
                    <h:inputText value="#{local.paramDescription}" rendered="#{local.editable}"
                                 maxlength="45" style="width: 300px;"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton actionListener="#{local.editarFila}"
                                       image="/Imagenes/actions/klipper_dock.png" title="Editar"
                                       rendered="#{local.view}"
                                       reRender="tablaMaster, barra"/>

                    <a4j:commandButton actionListener="#{local.editar}"
                                       image="/Imagenes/actions/filesave.png" title="Guardar"
                                       rendered="#{local.editable}"
                                       reRender="tablaMaster, barra" oncomplete="#{local.oncomplete}"/>
                    <a4j:commandButton actionListener="#{local.cancelar}"
                                       image="/Imagenes/actions/fileclose.png" title="Cancelar"
                                       rendered="#{local.editable}"
                                       reRender="tablaMaster, barra"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                       actionListener="#{managerLocal.eliminarFila}"
                                       oncomplete="Richfaces.showModalPanel('mpEliminar', {top:150})"
                                       reRender="fromEliminar">
                        <f:attribute name="local" value="#{local}"/>
                    </a4j:commandButton>
                </rich:column>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpEliminar" width="300" autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Eliminar"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer" title="Cerrar"
                            onclick="Richfaces.hideModalPanel('mpEliminar')"/>
        </f:facet>
        <h:form id="fromEliminar" style="text-align: center">
            <h4>&iquest;Esta realmente seguro de eliminar el local
                <h:outputText value="#{managerLocal.modalDescription}" style="font-style: italic"/>?
            </h4>
            <a4j:commandButton value="Aceptar" reRender="tablaMaster, barra"
                               actionListener="#{managerLocal.eliminar}"
                               oncomplete="Richfaces.hideModalPanel('mpEliminar')"/>
            <rich:spacer width="20px"/>
            <a4j:commandButton value="Cancelar" onclick="Richfaces.hideModalPanel('mpEliminar')"/>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mp" autosized="true" minWidth="400" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro de Local"/>
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
                                           actionListener="#{managerLocal.grabar}"
                                           reRender="tablaMaster, barra"
                                           oncomplete="#{managerLocal.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="30%">Descripci&oacute;n:*</td>
                    <td width="70%">
                        <h:inputText id="codigo" style="width: 180px;"
                                     value="#{managerLocal.modalDescription}"/>
                    </td>
                    <td align="right" width="20%">
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

</f:view>
</body>
</html>

