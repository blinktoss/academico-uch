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
    <title>Matricula de Cursos Libres</title>
    <script type="text/javascript">
        function printObjectsSelected_al(sgcomponent, idAL, nombreAL, codigoAL) {
            idAL.value = sgcomponent.getSelectedItems().pluck('studentId');
            nombreAL.innerHTML = sgcomponent.getSelectedItems().pluck('studentNames');
            codigoAL.innerHTML = sgcomponent.getSelectedItems().pluck('studentCode');
        }
    </script>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="font-size:13px; width: 100%">
                <tr>
                    <td colspan="2">
                        <h:outputText value="MATRICULA DE ALUMNOS" style="font-weight: bold;"/>
                    </td>
                    <td style="text-align: right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>
                        <h:graphicImage value="/Imagenes/actions/matricula.png" title="Matricular"
                                        style="cursor:pointer">
                            <a4j:support event="onclick"
                                         actionListener="#{managerCLMatricula.abrirMatricula}"
                                         oncomplete="Richfaces.showModalPanel('mpMatricula', {top:80})"
                                         reRender="n_alumno,suggestionAlumno,codigoAL,nombreAL,idAL,iArea,iModulo,iEstPago,resultados"/>
                        </h:graphicImage>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton image="/Imagenes/actions/viewmag.png"
                                           actionListener="#{managerCLMatricula.seleccionar}" title="Buscar"
                                           oncomplete="#{managerCLMatricula.oncomplete}"
                                           reRender="form3"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td style="width: 10%">C&oacute;digo</td>
                    <td>
                        <h:inputText id="b_codigo" value="#{managerCLMatricula.paramAluCodigo}"
                                     style="width: 200px;"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td>Ap. Paterno:</td>
                    <td>
                        <h:inputText id="b_appaterno" value="#{managerCLMatricula.paramAluAppaterno}"
                                     style="width: 200px;"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td>Ap. Materno:</td>
                    <td>
                        <h:inputText id="b_apmaterno" value="#{managerCLMatricula.paramAluApmaterno}"
                                     style="width: 200px;"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td>Nombres:</td>
                    <td>
                        <h:inputText id="b_nombres" value="#{managerCLMatricula.paramAluNombres}"
                                     style="width: 200px;"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="3" height="10px"></td>
                </tr>
            </table>
        </h:form>
        <h:form id="form3">
            <div style="width:100%">
                <rich:datascroller id="paginacion" for="tablaMaster" align="right" maxPages="10"/>
                <rich:dataTable id="tablaMaster" cellpadding="0" cellspacing="0" width="100%" rows="10"
                                onRowMouseOver="this.style.backgroundColor='#D7D7D7'"
                                onRowMouseOut="this.style.backgroundColor='#FFFFFF'"
                                value="#{managerCLMatricula.matriculas}" var="mat">

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="C&oacute;digo" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{mat.paramAluCodigo}"/>
                    </rich:column>

                    <rich:column colspan="4">
                        <f:facet name="header">
                            <h:outputText value="Alumno"/>
                        </f:facet>
                        <h:outputText
                                value="#{mat.paramAluAppaterno} #{mat.paramAluApmaterno} #{mat.paramAluNombres}"/>
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Tipo Alumno"/>
                        </f:facet>
                        <h:outputText value="#{mat.rowAluTipo}"/>
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Ver"/>
                        </f:facet>
                        <a4j:commandButton image="#{mat.rowImagenMostrar}" title="#{mat.rowTextoMostrar}"
                                           actionListener="#{mat.mostrarDetalle}"
                                           reRender="tablaMaster, paginacion"/>
                    </rich:column>

                    <rich:subTable id="subtable" value="#{mat.matriculas}" var="det"
                                   onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                                   onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                   rendered="#{mat.verDetalle}">
                        <rich:column>
                            <f:facet name="header">
                                <h:outputText value="Fecha de Matr&iacute;cula" escape="false"/>
                            </f:facet>
                            <h:outputText value="#{det.detMatFecha}">
                                <f:convertDateTime pattern="dd-MM-yyyy hh:mm a"/>
                            </h:outputText>
                        </rich:column>

                        <rich:column>
                            <f:facet name="header">
                                <h:outputText value="Tipo"/>
                            </f:facet>
                            <h:outputText value="#{det.detMatTipoValue}"/>
                        </rich:column>

                        <rich:column>
                            <f:facet name="header">
                                <h:outputText value="Taller"/>
                            </f:facet>
                            <h:outputText value="#{det.detTaller}" escape="false"/>
                        </rich:column>

                        <rich:column>
                            <f:facet name="header">
                                <h:outputText value="Secci&oacute;n" escape="false"/>
                            </f:facet>
                            <h:outputText value="#{det.detSeccion}" escape="false"/>
                        </rich:column>

                        <rich:column>
                            <f:facet name="header">
                                <h:outputText value="Fecha de Inicio"/>
                            </f:facet>
                            <h:outputText value="#{det.detFechaInicio}" escape="false"/>
                        </rich:column>

                        <rich:column style="text-align: center;">
                            <f:facet name="header">
                                <h:outputText value="Anular"/>
                            </f:facet>
                            <a4j:commandButton image="/Imagenes/actions/delete.gif"
                                               title="Anular Matricula"
                                               actionListener="#{managerCLMatricula.verificarPagosMatricula}"
                                               oncomplete="#{managerCLMatricula.oncomplete}"
                                               reRender="amMensaje">
                                <f:attribute name="p_mat_id_delete" value="#{det.detMatriculaId}"/>
                                <f:attribute name="p_alu_id_delete" value="#{mat.studentId}"/>
                            </a4j:commandButton>
                        </rich:column>
                    </rich:subTable>
                </rich:dataTable>
            </div>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpMatricula" autosized="true" width="900" zindex="4000">
        <f:facet name="header">
            <h:outputText value="Matricular"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpMatricula')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form2">
            <table style="width:100%">
                <tr>
                    <td colspan="2" style="text-align: right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Matricular"
                                           actionListener="#{managerCLMatricula.matricularSeleccionados}"
                                           oncomplete="#{managerCLMatricula.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr style="height: 25px;">
                    <td style="width: 100px;" class="tdLabel">Alumno Libre</td>
                    <td>
                        <h:inputText id="n_alumno" value="#{managerCLMatricula.suggestion}" style="width:280px;"/>
                        <rich:suggestionbox id="suggestionAlumno"
                                            for="n_alumno" nothingLabel="Alumno No Encontrado"
                                            suggestionAction="#{managerCLMatricula.autocomplete}" var="al"
                                            height="150" width="350" cellpadding="2"
                                            cellspacing="2" shadowOpacity="4" shadowDepth="4"
                                            minChars="3" rules="none" zindex="4500"
                                            usingSuggestObjects="true"
                                            onobjectchange="printObjectsSelected_al(#{rich:component('suggestionAlumno')},#{rich:element('idAL')},#{rich:element('nombreAL')},#{rich:element('codigoAL')});">
                            <h:column>
                                <h:outputText value="#{al.studentNames}" style="font-style:bold"/>
                            </h:column>
                            <h:column>
                                <h:outputText value="#{al.studentCode}" style="font-style:bold"/>
                            </h:column>
                        </rich:suggestionbox>
                        <br>
                        <h:outputText id="codigoAL" value="#{managerCLMatricula.studentCode}"/> -
                        <h:outputText id="nombreAL" value="#{managerCLMatricula.studentNames}"/>
                        <h:inputHidden id="idAL" value="#{managerCLMatricula.studentId}"/>
                    </td>
                </tr>
                <tr style="height: 25px">
                    <td class="tdLabel">&Aacute;rea</td>
                    <td>
                        <h:selectOneMenu id="iArea" value="#{managerCLMatricula.mpAreaId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLMatricula.mpAreas}"/>
                            <a4j:support event="onchange" reRender="iModulo,iEstPago,resultados"
                                         actionListener="#{managerCLMatricula.verModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 25px">
                    <td class="tdLabel">M&oacute;dulo</td>
                    <td>
                        <h:selectOneMenu id="iModulo" value="#{managerCLMatricula.mpModId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLMatricula.mpModulos}"/>
                            <a4j:support event="onchange" reRender="iEstPago,resultados"
                                         actionListener="#{managerCLMatricula.verEstructurasPago}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 25px">
                    <td class="tdLabel">Tipo de Pago</td>
                    <td>
                        <h:selectOneMenu id="iEstPago" value="#{managerCLMatricula.mpEstpagId}" style="width: 235px">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLMatricula.mpEstructurasPago}"/>
                            <a4j:support event="onchange" actionListener="#{managerCLMatricula.mostrarCursos}"
                                         reRender="resultados"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 20px">
                    <td colspan="2"></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <h:panelGroup id="resultados">
                            <h:outputText value="#{managerCLMatricula.mensajeError}"
                                          style="color: #FF0000; font-weight: bold;"
                                          rendered="#{!managerCLMatricula.mensajeError.isEmpty()}"/>
                            <h:outputText value="#{managerCLMatricula.mpModulo}" style="font-weight: bold;"
                                          rendered="#{managerCLMatricula.verCursos}"/>
                            <br/>
                            <rich:dataTable id="tablaCursos" cellpadding="0" cellspacing="0" border="0" width="100%"
                                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                            value="#{managerCLMatricula.secciones}"
                                            var="sec" rendered="#{managerCLMatricula.verCursos}">
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Curso"/>
                                    </f:facet>
                                    <h:outputText value="#{sec.mpRowCurso}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Secci&oacute;n" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{sec.mpRowSeccion}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Inicio"/>
                                    </f:facet>
                                    <h:outputText value="#{sec.mpRowFecInicio}">
                                        <f:convertDateTime pattern="dd-MM-yyyy"/>
                                    </h:outputText>
                                </rich:column>
                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Matriculados"/>
                                    </f:facet>
                                    <h:outputText id="nroMat" value="#{sec.mpRowNroMat}"
                                                  style="#{sec.mpRowNroMatStyle}"/>
                                    <rich:toolTip for="nroMat" value="#{sec.mpRowTtMessage}"
                                                  rendered="#{sec.mpRowVerMessage}"/>
                                </rich:column>
                                <rich:column style="text-align: center">
                                    <f:facet name="header">
                                        <h:outputText value="Monto Base"/>
                                    </f:facet>
                                    <h:outputText value="#{sec.mpRowMontoBase}"/>
                                </rich:column>
                                <rich:column style="text-align: center">
                                    <f:facet name="header">
                                        <h:outputText value="Monto a Pagar"/>
                                    </f:facet>
                                    <h:outputText value="#{sec.mpRowMontoPagar}" rendered="#{!sec.mpRowEditar}"/>
                                    <h:inputText value="#{sec.mpRowMontoEditar}"
                                                 style="font-size: 10px; width: 40px;" autocomplete="off"
                                                 rendered="#{sec.mpRowEditar}"/>
                                    <a4j:commandButton image="/Imagenes/actions/klipper_dock.png"
                                                       title="Editar"
                                                       actionListener="#{sec.editarMonto}"
                                                       reRender="resultados"
                                                       rendered="#{!sec.mpRowEditar}"/>
                                    <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                                       title="Grabar"
                                                       actionListener="#{sec.guardarMonto}"
                                                       reRender="resultados"
                                                       rendered="#{sec.mpRowEditar}"/>
                                    <a4j:commandButton image="/Imagenes/actions/fileclose.png"
                                                       title="Cancelar"
                                                       actionListener="#{sec.cancelarMonto}"
                                                       reRender="resultados"
                                                       rendered="#{sec.mpRowEditar}"/>
                                </rich:column>
                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Seleccionar"/>
                                    </f:facet>
                                    <h:selectBooleanCheckbox value="#{sec.mpRowAgregar}"/>
                                </rich:column>
                            </rich:dataTable>
                            <div style="text-align: right;">
                                <a4j:commandLink value="Deseleccionar Todos"
                                                 actionListener="#{managerCLMatricula.deseleccionarTodos}"
                                                 reRender="resultados"
                                                 rendered="#{managerCLMatricula.verCursos}"
                                                 style="font-size: 11px;"/>
                                <rich:spacer width="10px" rendered="#{managerCLMatricula.verCursos}"/>
                                <a4j:commandLink value="Seleccionar Todos"
                                                 actionListener="#{managerCLMatricula.seleccionarTodos}"
                                                 reRender="resultados"
                                                 rendered="#{managerCLMatricula.verCursos}"
                                                 style="font-size: 11px;"/>
                                <rich:spacer width="10px" rendered="#{managerCLMatricula.verCursos}"/>
                            </div>
                        </h:panelGroup>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mp2" width="350" height="100" autosized="true" zindex="4000">
        <f:facet name="header">
            <h:outputText value="Anular Matricula"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp2')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form4">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td align="center" valign="middle" colspan="2">
                        <h:outputText id="amMensaje" value="#{managerCLMatricula.mensajeEliminar}"
                                      style="font-size: 12px; font-weight: bold;"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" height="20px"></td>
                </tr>
                <tr>
                    <td align="center" valign="middle">
                        <a4j:commandButton image="/Imagenes/actions/button_accept.png"
                                           title="Aceptar"
                                           actionListener="#{managerCLMatricula.anularMatricula}"
                                           oncomplete="#{managerCLMatricula.oncomplete}"
                                           reRender="tablaMaster,paginacion"/>
                    </td>
                    <td align="center" valign="middle">
                        <a4j:commandButton image="/Imagenes/actions/button_cancel.png"
                                           title="Cancelar"
                                           oncomplete="Richfaces.hideModalPanel('mp2')"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
