<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Interfaz con el banco</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="formUpload">
            <table style="width:100%; font-size:13px;">
                <tr>
                    <td><strong>PROCESAR INFORMACI&Oacute;N DEL BANCO</strong></td>
                    <td align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>

                        <h:commandButton action="#{managerBanco.limpiarTodo}"
                                         image="/Imagenes/actions/limpiar.png"
                                         title="Limpiar todo el contenido"/>
                        <rich:spacer width="8px"/>

                        <h:outputLink value="#{request.contextPath}/report" target="_blank" title="Vista previa">
                            <f:param name="type" value="banco-respuesta"/>
                            <h:graphicImage value="/Imagenes/actions/viewmag.png"/>
                        </h:outputLink>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton actionListener="#{managerBanco.generarArchivoDescargable}"
                                           image="/Imagenes/actions/download.png"
                                           title="Descargar archivo"
                                           oncomplete="#{managerBanco.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <h:panelGrid columns="3" style="margin: auto">
                            <rich:fileUpload id="banUpload" acceptedTypes="txt"
                                             maxFilesQuantity="1" listHeight="58px"
                                             fileUploadListener="#{managerBanco.cargarArchivo}">
                                <a4j:support event="onuploadcomplete" reRender="info, banProceso"/>
                            </rich:fileUpload>

                            <h:panelGroup id="info" layout="block">
                                <f:facet name="header">
                                    <h:outputText value="Archivo subido"/>
                                </f:facet>
                                <h:panelGrid columns="1">
                                    <h:outputLabel value="Archivo: " style="font-weight: bold;"/>
                                    <h:outputText value="#{managerBanco.fileNombre}"/>
                                </h:panelGrid>

                                <a4j:commandButton id="banProceso" value="Procesar archivo"
                                                   disabled="#{managerBanco.disableProceso}"
                                                   actionListener="#{managerBanco.procesarArchivo}"
                                                   reRender="bancoOutput, info"/>
                            </h:panelGroup>
                            <h:panelGroup>
                                <h:panelGrid columns="2">
                                    <h:selectBooleanCheckbox value="#{managerBanco.generaComprobante}">
                                        <a4j:support event="onclick" actionListener="#{managerBanco.seleccionarEfecto}"
                                                     oncomplete="#{managerBanco.efecto}" reRender="contentPanel"/>
                                    </h:selectBooleanCheckbox>
                                    <h:outputText value="Generar comprobantes"/>
                                </h:panelGrid>
                                <rich:effect name="hideDiv" for="contentDiv" type="BlindUp"/>
                                <rich:effect name="showDiv" for="contentDiv" type="Appear"/>
                                <div id="contentDiv">
                                    <rich:panel id="contentPanel" header="Comprobantes - Boleta">
                                        <h:panelGrid columns="2">
                                            <h:outputText value="Serie"/>
                                            <h:selectOneMenu value="#{managerBanco.serie}">
                                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                                <f:selectItems value="#{managerBanco.series}"/>
                                                <a4j:support event="onchange"
                                                             actionListener="#{managerBanco.cambiaSerie}"
                                                             reRender="nroinicio"/>
                                            </h:selectOneMenu>

                                            <h:outputText value="N&uacute;mero de Inicio" escape="false"/>
                                            <h:inputText id="nroinicio" value="#{managerBanco.nroInicio}"
                                                         maxlength="7"/>
                                        </h:panelGrid>
                                    </rich:panel>
                                </div>
                            </h:panelGroup>
                        </h:panelGrid>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <h:panelGroup id="bancoOutput">
                        <rich:panel rendered="#{managerBanco.rendered}">
                        <rich:panel bodyClass="message #{managerBanco.bodyClass}">
                            <h:outputText value="#{managerBanco.mensaje}" escape="false"/>
                        <h:panelGroup rendered="#{managerBanco.totalErrores != 0}">
                        <rich:dataTable id="tablaError" rows="10" onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        cellpadding="0" cellspacing="0" width="98%" align="center"
                                        border="0" style="font-weight: normal;"
                                        value="#{managerBanco.errores}" var="error">

                        <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Nro de l&iacute;nea" escape="false"/>
                        </f:facet>
                            <h:outputText value="#{error.banentNumeroLinea}"/>
                        </rich:column>
                        <rich:column style="text-align: left;">
                        <f:facet name="header">
                            <h:outputText value="Comentario"/>
                        </f:facet>
                            <h:outputText value="#{error.banentComentario}" escape="false"/>
                        </rich:column>
                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="N&uacute;mero de Operaci&oacute;n" escape="false"/>
                        </f:facet>
                            <h:outputText value="#{error.banentNroOperacion}"/>
                        </rich:column>

                        <f:facet name="footer">
                            <rich:datascroller id="scrollError" for="tablaError" maxPages="10" align="center"/>
                        </f:facet>
                        </rich:dataTable><br/>
                        </h:panelGroup>
                        </rich:panel>
                        <h:panelGroup rendered="#{managerBanco.totalDataBanco != 0}">
                        <div style="text-align: right">
                            <a4j:commandButton value="Salvar informacion procesada"
                                               title="Guardar" reRender="formSalvar"
                                               disabled="#{managerBanco.totalDataBanco == 0}"
                                               actionListener="#{managerBanco.validaComprobante}"
                                               oncomplete="#{managerBanco.oncomplete}"/>
                            <rich:separator height="2" lineType="double"/>
                            <rich:spacer width="10px"/>
                        </div>
                        <rich:dataTable id="tablaBanco" rows="20" onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        cellpadding="0" cellspacing="0" width="100%" border="0"
                                        value="#{managerBanco.respuestas}" var="respuesta">

                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Alumno"/>
                        </f:facet>
                            <h:outputText value="#{respuesta.banentNomAlumno}"/>
                        </rich:column>

                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Concepto de pago"/>
                        </f:facet>
                            <h:outputText value="#{respuesta.banentConceptoPago}"
                                          escape="false"/>
                        </rich:column>

                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto 1"/>
                        </f:facet>
                        <h:outputText value="#{respuesta.banentMonto1}">
                            <f:convertNumber pattern="0.00"/>
                        </h:outputText>
                        </rich:column>

                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto 2"/>
                        </f:facet>
                        <h:outputText value="#{respuesta.banentMonto2}">
                            <f:convertNumber pattern="0.00"/>
                        </h:outputText>
                        </rich:column>

                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto 3"/>
                        </f:facet>
                        <h:outputText value="#{respuesta.banentMonto3}">
                            <f:convertNumber pattern="0.00"/>
                        </h:outputText>
                        </rich:column>

                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto 4"/>
                        </f:facet>
                        <h:outputText value="#{respuesta.banentMonto4}">
                            <f:convertNumber pattern="0.00"/>
                        </h:outputText>
                        </rich:column>

                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto 5"/>
                        </f:facet>
                        <h:outputText value="#{respuesta.banentMonto5}">
                            <f:convertNumber pattern="0.00"/>
                        </h:outputText>
                        </rich:column>

                        <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Mora"/>
                        </f:facet>
                        <h:outputText value="#{respuesta.banentMora}">
                            <f:convertNumber pattern="0.00"/>
                        </h:outputText>
                        </rich:column>

                        <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Fecha de movimiento"/>
                        </f:facet>
                        <h:outputText value="#{respuesta.banentFechaMovimiento}">
                            <f:convertDateTime pattern="dd / MM / yyyy"/>
                        </h:outputText>
                        </rich:column>

                        <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Fecha de pago"/>
                        </f:facet>
                        <h:outputText value="#{respuesta.banentFechaPago}">
                            <f:convertDateTime pattern="dd / MM / yyyy"/>
                        </h:outputText>
                        </rich:column>

                        <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Numero de Operaci&oacute;n" escape="false"/>
                        </f:facet>
                            <h:outputText value="#{respuesta.banentNroOperacion}"/>
                        </rich:column>

                        <f:facet name="footer">
                            <rich:datascroller id="scrollBanco" for="tablaBanco" maxPages="10" align="center"/>
                        </f:facet>
                        </rich:dataTable>
                        </h:panelGroup>
                        </rich:panel>
                        </h:panelGroup>
                    <td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpSalvar" autosized="true" width="300" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Salvar Informaci&oacute;n" escape="false"/>
        </f:facet>
        <h:form id="formSalvar">
            <table style="width: 100%">
                <tr>
                    <td colspan="2">
                        <h:outputText value="<center>Se salvaran todos los cambios<br>
                                              Se borraran todos los datos temporales</center>"
                                      escape="false" styleClass="message"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%"><h:outputText value="Seleccione la fecha para los comprobantes"/></td>
                    <td><rich:calendar datePattern="dd-MM-yyyy"
                                       value="#{managerBanco.fechaComprobantes}"/></td>
                </tr>
                <tr>
                    <td align="center" colspan="2">
                        <h:commandButton value="Aceptar"
                                         action="#{managerBanco.guardarInformacion}"/>
                        <rich:spacer width="20"/>
                        <a4j:commandButton value="Cancelar"
                                           onclick="Richfaces.hideModalPanel('mpSalvar')"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
