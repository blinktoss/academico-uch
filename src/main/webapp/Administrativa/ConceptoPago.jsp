<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Conceptos de Pagos</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE CONCEPTO DE PAGOS</strong>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="border-width: 0; cursor: pointer;"
                                        width="16">
                            <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mp',{top:130})"
                                         action="#{managerConceptoPago.nuevo}"
                                         reRender="iId,iCodigo,iDescripcion,iMonto,iTipo"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/fileprint.png" title="Imprimir Listado"/>
                    </td>
                    <td align=right width="50%">
                        <h:commandButton type="button" id="buscar" action="#{managerConceptoPago.seleccionar}"
                                         image="/Imagenes/actions/viewmag.png" title="Buscar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td width="20%" colspan="2"></td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                </tr>
                <tr>
                    <td width="10%">Descripci&oacute;n:
                    </td>
                    <td width="30%">
                        <h:inputText value="#{managerConceptoPago.paramDescripcion}" style="width: 180px;"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">C&oacute;digo:
                    </td>
                    <td width="30%">
                        <h:inputText value="#{managerConceptoPago.paramCodigo}" style="width: 180px;"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Tipo:
                    </td>
                    <td width="30%">
                        <h:selectOneMenu id="bTipo" value="#{managerConceptoPago.paramTipoId}">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerConceptoPago.paramTipos}"/>
                        </h:selectOneMenu>
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
                    <td></td>
                </tr>
            </table>
        </h:form>
        <h:form style="width: 100%;">
            <rich:datascroller id="paginacion" align="right" for="tablaMaster" maxPages="8"
                               style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerConceptoPago.listaConceptoPago}" var="conPago">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{conPago.rowId}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{conPago.paramCodigo}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{conPago.paramDescripcion}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Monto"/>
                    </f:facet>
                    <h:outputText value="#{conPago.rowMonto}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Tipo"/>
                    </f:facet>
                    <h:outputText value="#{conPago.paramTipoId}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <h:graphicImage value="/Imagenes/actions/editpaste.png" style="border-width: 0; cursor: pointer;">
                        <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mp',{top:130})"
                                     actionListener="#{managerConceptoPago.editarFila}"
                                     reRender="iId,iCodigo,iDescripcion,iMonto,iTipo"/>
                    </h:graphicImage>
                    <f:param id="p_id" value="#{conPago.rowId}"/>
                    <f:param id="p_codigo" value="#{conPago.paramCodigo}"/>
                    <f:param id="p_descripcion" value="#{conPago.paramDescripcion}"/>
                    <f:param id="p_monto" value="#{conPago.rowMonto}"/>
                    <f:param id="p_tipo" value="#{conPago.rowIdTipo}"/>
                    <f:param id="p_creacion" value="#{conPago.rowCreacion}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerConceptoPago.eliminarFila}"
                                     action="#{managerConceptoPago.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="mp" autosized="true" width="360" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de conceptos de pago"/>
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
                                               actionListener="#{managerConceptoPago.insertar}"
                                               action="#{managerConceptoPago.seleccionar}"
                                               reRender="tablaMaster"
                                               oncomplete="alert('Se guardo el registro correctamente');Richfaces.hideModalPanel('mp')"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <rich:separator height="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">C&oacute;digo:</td>
                        <td width="70%">
                            <h:inputHidden id="iId" value="#{managerConceptoPago.mpId}"/>

                            <h:inputText label="Codigo" id="iCodigo" style="width: 180px;"
                                         value="#{managerConceptoPago.mpCodigo}" required="true" maxlength="10"/>
                            <rich:message for="iCodigo">
                                <f:facet name="passedMarker">
                                    <h:graphicImage value="/Imagenes/actions/ok.png"/>
                                </f:facet>
                                <f:facet name="errorMarker">
                                    <h:graphicImage value="/Imagenes/actions/no.png"/>
                                </f:facet>
                            </rich:message>
                        </td>
                        <td align="right" width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Descripci&oacute;n:</td>
                        <td width="70%">
                            <h:inputTextarea id="iDescripcion" label="Descripcion"
                                             value="#{managerConceptoPago.mpDescripcion}" style="width: 180px;"
                                             required="true"/>

                            <rich:message for="iDescripcion">
                                <f:facet name="passedMarker">
                                    <h:graphicImage value="/Imagenes/actions/ok.png"/>
                                </f:facet>
                                <f:facet name="errorMarker">
                                    <h:graphicImage value="/Imagenes/actions/no.png"/>
                                </f:facet>
                            </rich:message>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Monto:</td>
                        <td width="70%">
                            <h:inputText id="iMonto" label="Monto" value="#{managerConceptoPago.mpMonto}"
                                         style="width: 180px;" required="true"/>
                            <rich:message for="iMonto">
                                <f:facet name="passedMarker">
                                    <h:graphicImage value="/Imagenes/actions/ok.png"/>
                                </f:facet>
                                <f:facet name="errorMarker">
                                    <h:graphicImage value="/Imagenes/actions/no.png"/>
                                </f:facet>
                            </rich:message>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Tipo:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iTipo" value="#{managerConceptoPago.mpTipoId}">
                                <f:selectItems value="#{managerConceptoPago.comboCatalogo}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <rich:separator height="1"/>
                        </td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>

