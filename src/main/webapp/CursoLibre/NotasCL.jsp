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
    <title>Seguimiento de notas</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel style="width: 100%;">
        <h:form id="masterForm">
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2"><strong>LISTADO DE NOTAS - CURSOS LIBRES</strong>
                    </td>
                    <td width="30%"></td>
                    <td width="70%" align=right>
                        <h:outputText id="talNombre"
                                      value="#{managerCLNota.lblModNombre} - #{managerCLNota.lblTalapeNombre}"/>
                    </td>
                    <td align=right>
                        <table>
                            <tr>
                                <td>
                                    <a4j:status>
                                        <f:facet name="start">
                                            <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                                        </f:facet>
                                    </a4j:status>
                                </td>
                                <td>
                                    <h:commandButton type="button" title="Refrescar"
                                                     action="#{managerCLNota.cargarArbol}"
                                                     image="/Imagenes/actions/reload.png"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="10"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td style="vertical-align:top;">
                        <rich:tree value="#{managerCLNota.arbol}"
                                   ajaxSubmitSelection="true"
                                   switchType="ajax"
                                   style="font-size:10px; font-family:verdana"
                                   var="item"
                                   nodeSelectListener="#{managerCLNota.seleccion}"
                                   reRender="talNombre,masterScroll, masterTable"/>
                    </td>
                    <td style="vertical-align:top;" colspan="5">
                        <table width="100%" cellspacing="0" border="0" cellpadding="0">
                            <tr>
                                <td>
                                    <rich:datascroller id="masterScroll" for="masterTable"
                                                       align="right" maxPages="30"/>
                                    <rich:dataTable id="masterTable"
                                                    onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                                    cellpadding="0" cellspacing="0" width="100%" border="0" rows="10"
                                                    value="#{managerCLNota.listaTalleres}" var="notascl">

                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="Id de Secci&oacute;n" escape="false"/>
                                            </f:facet>
                                            <h:outputText value="#{notascl.rowSecId}"/>
                                        </rich:column>
                                        <rich:column sortable="true" sortExpression="#{notascl.rowSecCod}">
                                            <f:facet name="header">
                                                <h:outputText value="Codigo de Secci&oacute;n" escape="false"/>
                                            </f:facet>
                                            <h:outputText value="#{notascl.rowSecCod}"/>
                                        </rich:column>
                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="Nombre de Secci&oacute;n" escape="false"/>
                                            </f:facet>
                                            <h:outputText value="#{notascl.rowSecNom}"/>
                                        </rich:column>
                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="Fecha de Inicio"/>
                                            </f:facet>
                                            <h:outputText value="#{notascl.rowSecFini}">
                                                <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy"/>
                                            </h:outputText>
                                        </rich:column>
                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="Fecha de Fin"/>
                                            </f:facet>
                                            <h:outputText value="#{notascl.rowSecFfin}">
                                                <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy"/>
                                            </h:outputText>
                                        </rich:column>
                                        <rich:column style="text-align: center;">
                                            <f:facet name="header">
                                                <h:outputText value="Matriculados"/>
                                            </f:facet>
                                            <h:outputText value="#{notascl.rowMatNum}"/>
                                        </rich:column>
                                        <rich:column style="text-align: center;">
                                            <f:facet name="header">
                                                <h:outputText value="Imprimir"/>
                                            </f:facet>
                                            <h:outputLink value="#{request.contextPath}/report" target="_blank"
                                                          title="Imprimir notas de la seccion">
                                                <f:param name="type" value="notascl-seccion"/>
                                                <f:param name="secId" value="#{notascl.rowSecId}"/>
                                                <h:graphicImage value="/Imagenes/actions/fileprint.png"
                                                                style="border-width: 0; cursor: pointer;"/>
                                            </h:outputLink>
                                        </rich:column>
                                        <rich:column style="text-align: center;">
                                            <f:facet name="header">
                                                <h:outputText value="Ingresar Notas"/>
                                            </f:facet>
                                            <f:param id="p_talape_id" value="#{notascl.lblTalapeId}"/>
                                            <f:param id="p_sec_id" value="#{notascl.rowSecId}"/>
                                            <a4j:commandButton image="/Imagenes/actions/notas.png"
                                                               title="Muestra las notas del curso"
                                                               actionListener="#{managerCLNota.asignarNotas}"
                                                               reRender="panelNotas, mp_talape_id, mp_sec_id, mp_talape_nombre"
                                                               oncomplete="Richfaces.showModalPanel('mpNotasCL', {top:80});"/>
                                        </rich:column>
                                    </rich:dataTable>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpNotasCL" zindex="4000"
                     autosized="true" minWidth="450">
        <f:facet name="header">
            <h:outputText value="Actualizacion de notas"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpNotasCL')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="formNotasCL">
            <table width="100%">
                <tr>
                    <td align=right colspan="2">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           oncomplete="#{managerCLNota.oncomplete}"
                                           actionListener="#{managerCLNota.guardarNotas}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="4" align="right">
                        <h:inputHidden value="#{managerCLNota.mpTalapeId}" id="mp_talape_id"/>
                        <h:inputHidden value="#{managerCLNota.mpSecId}" id="mp_sec_id"/>
                        <h:outputText value="#{managerCLNota.lblTalapeNombre}" id="mp_talape_nombre"
                                      style="font-style: italic;font-weight: bold;"/>
                    </td>
                <tr>
                    <td>
                        <div style="overflow: auto; height: 350px;">
                            <h:panelGrid id="panelNotas" binding="#{managerCLNota.htmlPanel}"
                                         columns="#{managerCLNota.mpNroColumnas}"
                                         border="0" cellpadding="0" cellspacing="0" width="100%"/>
                        </div>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
