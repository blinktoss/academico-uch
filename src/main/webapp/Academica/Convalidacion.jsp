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
    <title>Convalidaci&oacute;n de Cursos</title>
</head>
<f:view>
    <body>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="formSearch" style="width:100%">
            <table style="font-size:13px; width: 100%">
                <tr>
                    <td colspan="2"><strong>CONVALIDACI&Oacute;N DE CURSOS</strong></td>
                    <td align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>
                        <a4j:commandButton id="buscar" title="Buscar"
                                           actionListener="#{managerConvalidacion.buscarCursos}"
                                           image="/Imagenes/actions/viewmag.png"
                                           oncomplete="#{managerConvalidacion.oncomplete}"
                                           reRender="tableAlumno, scrollAlumno, formAlumno"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="70px" class="tdLabel"><h:outputText value="C&oacute;digo" escape="false"/></td>
                    <td>
                        <h:inputText value="#{managerConvalidacion.paramAluCodigo}"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Paterno"/></td>
                    <td>
                        <h:inputText value="#{managerConvalidacion.paramAluApPaterno}"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Materno"/></td>
                    <td>
                        <h:inputText value="#{managerConvalidacion.paramAluApMaterno}"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Nombres"/></td>
                    <td>
                        <h:inputText value="#{managerConvalidacion.paramAluNombre}"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
            </table>
        </h:form>
        <h:form id="formAlumno" style="width: 100%">
            <rich:datascroller id="scrollAlumno" for="tableAlumno" align="right" maxPages="20"/>
            <rich:dataTable id="tableAlumno" rows="10" cellpadding="0" cellspacing="0" border="0" width="100%"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerConvalidacion.alumnos}" var="alu">

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{alu.alumnoId}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{alu.paramAluCodigo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>
                    <h:outputText value="#{alu.paramAluApPaterno} #{alu.paramAluApMaterno} #{alu.paramAluNombre}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Especialidad"/>
                    </f:facet>
                    <h:outputText value="#{alu.especialidadNombre}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Semestre Ingreso"/>
                    </f:facet>
                    <h:outputText value="#{alu.semestreIngreso}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Convalidar"/>
                    </f:facet>
                    <a4j:commandButton title="Convalidar cursos"
                                       reRender="formConv, tableConv"
                                       image="/Imagenes/actions/convalidar.png"
                                       oncomplete="Richfaces.showModalPanel('mpConvalidacion')"
                                       actionListener="#{managerConvalidacion.capturarAlumno}">
                        <f:attribute name="p_alu" value="#{alu}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Imprimir"/>
                    </f:facet>
                    <h:outputLink value="#{request.contextPath}/report" target="_blank"
                                  title="Imprimir Boleta de Notas">
                        <f:param name="type" value="convalidacion-boleta"/>
                        <f:param name="aluCodigo" value="#{alu.paramAluCodigo}"/>
                        <h:graphicImage value="/Imagenes/actions/fileprint.png"
                                        style="border-width: 0; cursor: pointer;"/>
                    </h:outputLink>
                </rich:column>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpConvalidacion" minWidth="630" zindex="4000" autosized="true">
        <f:facet name="header">
            <h:outputText value="Convalidaci&oacute;n de Cursos" escape="false"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png"
                                style="cursor:pointer" title="Cerrar"
                                onclick="Richfaces.hideModalPanel('mpConvalidacion')"/>
            </h:panelGroup>
        </f:facet>
        <a4j:form id="formConv">
            <table width="100%">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           oncomplete="#{managerConvalidacion.oncomplete}"
                                           reRender="formSearch"
                                           actionListener="#{managerConvalidacion.guardarConvalidacion}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td class="tdLabel" style="width: 22%;"><h:outputText value="Alumno"/></td>
                    <td><h:outputText value="#{managerConvalidacion.aluCodigoNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Especialidad"/></td>
                    <td><h:outputText value="#{managerConvalidacion.especialidadNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Semestre Ingreso"/></td>
                    <td><h:outputText value="#{managerConvalidacion.semestreIngreso}"/></td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <div style="overflow: auto;height:400px;">
                            <rich:dataTable id="tableConv" var="conv"
                                            onRowMouseOver="this.style.backgroundColor='#D7D7D7'"
                                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                            cellpadding="0" cellspacing="0" width="100%" border="0"
                                            value="#{managerConvalidacion.convalidaciones}">

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Sec. Id"/>
                                    </f:facet>
                                    <h:outputText value="#{conv.regSecId}"/>
                                </rich:column>

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Secci&oacute;n" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{conv.regSecNombre}"/>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Plan Curso"/>
                                    </f:facet>
                                    <h:outputText value="#{conv.regPlancurCodigo}"/>
                                </rich:column>

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Curso"/>
                                    </f:facet>
                                    <h:outputText value="#{conv.regCurNombre}"/>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Activar"/>
                                    </f:facet>
                                    <h:selectBooleanCheckbox value="#{conv.activar}">
                                        <a4j:support event="onclick" reRender="panNota" focus="txtNota"
                                                     actionListener="#{conv.cambiarEstado}"/>
                                    </h:selectBooleanCheckbox>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Nota"/>
                                    </f:facet>
                                    <h:panelGroup id="panNota">
                                        <h:inputText id="txtNota" value="#{conv.regNota}" autocomplete="off"
                                                     disabled="#{conv.disable}" maxlength="2"
                                                     style="text-align: center;width: 30px; #{conv.style}"/>
                                    </h:panelGroup>
                                </rich:column>
                            </rich:dataTable>
                        </div>
                    </td>
                </tr>
            </table>
        </a4j:form>
    </rich:modalPanel>

    </body>
</f:view>
</html>
