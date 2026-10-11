<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Horarios y Secciones</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%">
                <tr>
                    <td width="20%"><strong>HORARIOS Y SECCIONES</strong></td>
                    <td align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton type="button" title="Refrescar"
                                           action="#{managerSeccion.cargarArbol}"
                                           reRender="clTree"
                                           image="/Imagenes/actions/reload.png"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td style="vertical-align:top;">
                        <rich:tree id="clTree" value="#{managerSeccion.arbol}"
                                   ajaxSubmitSelection="true" switchType="ajax"
                                   style="font-size:10px; font-family:verdana"
                                   var="item"
                                   nodeSelectListener="#{managerSeccion.seleccion}"
                                   reRender="masterDetalle, masterTable, masterScroll"/>
                    </td>
                    <td style="vertical-align:top;" colspan="5">
                        <table style="width:100%">
                            <tr>
                                <td>
                                    <h:panelGroup id="masterDetalle">
                                        <h:outputText value="Modulo: #{managerSeccion.modNombre}"/><br/>
                                        <h:outputText value="Curso: #{managerSeccion.curNombre}"/><br/>
                                        <h:outputText value="Taller: #{managerSeccion.talapeNombre}"/>
                                    </h:panelGroup>
                                </td>
                                <td align="right">
                                    <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;"
                                                    title="Nueva seccion" width="16">
                                        <a4j:support event="onclick"
                                                     actionListener="#{managerSeccion.seleccionarSeccion}"
                                                     oncomplete="#{managerSeccion.oncomplete}"
                                                     reRender="formSeccion"/>
                                    </h:graphicImage>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <rich:datascroller id="masterScroll" for="masterTable"
                                                       align="right" maxPages="30"/>
                                    <rich:dataTable id="masterTable" width="100%" border="0" rows="10"
                                                    onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                                    cellpadding="0" cellspacing="0"
                                                    value="#{managerSeccion.listaSecciones}" var="seccl">

                                        <f:param id="p_sec_id" value="#{seccl.secId}"/>
                                        <f:param id="p_sec_detalle" value="#{seccl.secCodigo} | #{seccl.secNombre}"/>
                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="Id"/>
                                            </f:facet>
                                            <h:outputText value="#{seccl.secId}"/>
                                        </rich:column>
                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="C&oacute;digo" escape="false"/>
                                            </f:facet>
                                            <h:outputText value="#{seccl.secCodigo}"/>
                                        </rich:column>
                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="Nombre"/>
                                            </f:facet>
                                            <h:outputText value="#{seccl.secNombre}"/>
                                        </rich:column>
                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="Fecha de Inicio"/>
                                            </f:facet>
                                            <h:outputText value="#{seccl.secFinicio}">
                                                <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy" locale="es"/>
                                            </h:outputText>
                                        </rich:column>
                                        <rich:column>
                                            <f:facet name="header">
                                                <h:outputText value="Fecha de Fin"/>
                                            </f:facet>
                                            <h:outputText value="#{seccl.secFfin}">
                                                <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy" locale="es"/>
                                            </h:outputText>
                                        </rich:column>

                                        <rich:column style="text-align: center;">
                                            <f:facet name="header">
                                                <h:outputText value="Editar"/>
                                            </f:facet>
                                            <a4j:commandButton image="/Imagenes/actions/editpaste.png"
                                                               actionListener="#{managerSeccion.seleccionarSeccion}"
                                                               oncomplete="Richfaces.showModalPanel('mpSeccion',{top:80})"
                                                               reRender="formSeccion"/>
                                        </rich:column>

                                        <rich:column style="text-align: center;">
                                            <f:facet name="header">
                                                <h:outputText value="Horario"/>
                                            </f:facet>
                                            <a4j:commandButton image="#{seccl.imagenHorario}"
                                                               title="Horario"
                                                               actionListener="#{managerSeccion.cargarHorario}"
                                                               oncomplete="Richfaces.showModalPanel('mpHoraria',{top:80})"
                                                               reRender="formHoraria"/>
                                        </rich:column>

                                        <rich:column style="text-align: center;">
                                            <f:facet name="header">
                                                <h:outputText value="Matriculados"/>
                                            </f:facet>
                                            <a4j:commandLink title="Matriculados"
                                                             value="#{seccl.cantMatriculados}"
                                                             actionListener="#{managerSeccion.cargarMatriculados}"
                                                             oncomplete="Richfaces.showModalPanel('mpMatriculados', {top: 80})"
                                                             reRender="formMatriculados"/>
                                        </rich:column>

                                        <rich:column style="text-align: center;">
                                            <f:facet name="header">
                                                <h:outputText value="Eliminar"/>
                                            </f:facet>
                                            <a4j:commandButton image="/Imagenes/actions/delete.gif"
                                                               actionListener="#{managerSeccion.verificarEliminacion}"
                                                               oncomplete="#{managerSeccion.oncomplete}"
                                                               reRender="formEliminar"/>
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

    <rich:modalPanel id="mpSeccion" minWidth="650" autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Nueva secci&oacute;n" escape="false"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png"
                            style="cursor:pointer" title="Cerrar"
                            onclick="Richfaces.hideModalPanel('mpSeccion')"/>
        </f:facet>
        <h:form id="formSeccion">
            <table style="width: 100%">
                <tr>
                    <td align="right" colspan="2">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           actionListener="#{managerSeccion.insertarActualizarSeccion}"
                                           oncomplete="#{managerSeccion.oncomplete}"
                                           reRender="masterDetalle, masterScroll, masterTable"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="110px" class="tdLabel">M&oacute;dulo</td>
                    <td><h:outputText value="#{managerSeccion.modNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Curso</td>
                    <td><h:outputText value="#{managerSeccion.curNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Taller aperturado</td>
                    <td><h:outputText value="#{managerSeccion.talapeNombre}"/></td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">C&oacute;digo</td>
                    <td><h:inputText id="secCodigo" value="#{managerSeccion.seccion.secCodigo}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Nombre</td>
                    <td><h:inputText id="secNombre" value="#{managerSeccion.seccion.secNombre}"
                                     style="width: 300px;"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Vac. M&iacute;nimas</td>
                    <td><h:inputText id="secVacMin" value="#{managerSeccion.seccion.secVacMin}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Vac. M&aacute;ximas</td>
                    <td><h:inputText id="secVacMax" value="#{managerSeccion.seccion.secVacMax}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Inicio de Clases</td>
                    <td><rich:calendar id="secIni" value="#{managerSeccion.seccion.secFinicio}"
                                       showWeeksBar="false" datePattern="dd-MM-yyyy"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Fin de Clases</td>
                    <td><rich:calendar id="secFin" value="#{managerSeccion.seccion.secFfin}"
                                       showWeeksBar="false" datePattern="dd-MM-yyyy"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Inicio de Matr&iacute;cula</td>
                    <td><rich:calendar id="secIniMat" value="#{managerSeccion.seccion.secFinicioMatricula}"
                                       showWeeksBar="false" datePattern="dd-MM-yyyy"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Fin de Matr&iacute;cula</td>
                    <td><rich:calendar id="secFinMat" value="#{managerSeccion.seccion.secFfinMatricula}"
                                       showWeeksBar="false" datePattern="dd-MM-yyyy"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Turno</td>
                    <td>
                        <h:selectOneMenu id="secTurnos" value="#{managerSeccion.seccion.secTurno}">
                            <f:selectItem itemValue="X" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSeccion.turnos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpEliminar" width="250" height="100"
                     autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Eliminar secci&oacute;n" escape="false"/>
        </f:facet>
        <h:form id="formEliminar">
            <h:panelGroup layout="block" style="text-align: center;font-size: 12px;">
                <h:outputText escape="false"
                              value="Se eliminara la seccion <br/><b>#{managerSeccion.mpSecDetalle}</b><br/><br/>"/>

                <a4j:commandButton title="Aceptar"
                                   image="/Imagenes/actions/button_accept.png"
                                   actionListener="#{managerSeccion.eliminarSeccion}"
                                   oncomplete="Richfaces.hideModalPanel('mpEliminar')"
                                   reRender="masterScroll, masterTable"/>
                <rich:spacer width="50px"/>

                <a4j:commandButton title="Cancelar"
                                   image="/Imagenes/actions/button_cancel.png"
                                   oncomplete="Richfaces.hideModalPanel('mpEliminar')"/>
            </h:panelGroup>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpHoraria" minWidth="1000" autosized="true" zindex="3000">
        <f:facet name="header">
            <h:outputText value="Horario"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png"
                                style="cursor:pointer" title="Cerrar"
                                onclick="Richfaces.hideModalPanel('mpHoraria')"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="formHoraria">
            <table style="width: 100%">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           title="Guardar"
                                           actionListener="#{managerSeccion.guardarHorarios}"
                                           oncomplete="#{managerSeccion.oncomplete}"
                                           reRender="masterTable, masterScroll"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td class="tdLabel" style="width: 100px;"><h:outputText value="M&oacute;dulo" escape="false"/></td>
                    <td><h:outputText value="#{managerSeccion.modNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Curso"/></td>
                    <td><h:outputText value="#{managerSeccion.curNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Taller"/></td>
                    <td><h:outputText value="#{managerSeccion.talapeNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Secci&oacute;n" escape="false"/></td>
                    <td><h:outputText value="#{managerSeccion.mpSecDetalle}"/></td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Aula"/></td>
                    <td>
                        <h:selectOneMenu id="horAula" value="#{managerSeccion.horaria.acAula.id}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSeccion.aulas}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Docente"/></td>
                    <td>
                        <h:selectOneMenu id="horDocente" value="#{managerSeccion.horaria.acDocente.id}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSeccion.profesores}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Tipo de Clase"/></td>
                    <td>
                        <h:selectOneMenu id="horTipoClase" value="#{managerSeccion.horaria.horTipoClase}">
                            <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSeccion.tipoClase}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="D&iacute;a" escape="false"/></td>
                    <td>
                        <h:selectOneMenu id="horDia" value="#{managerSeccion.horaria.horDia}">
                            <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSeccion.dias}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Inicio"/></td>
                    <td>
                        <h:panelGroup>
                            <h:inputText id="horIniH" value="#{managerSeccion.horaria.horaInicio}" maxlength="2"
                                         required="true" style="width: 20px;"/>
                            <h:outputLabel value=":"/>
                            <h:inputText id="horIniM" value="#{managerSeccion.horaria.minutoInicio}" maxlength="2"
                                         required="true" style="width: 20px;"/>
                            <h:selectOneMenu id="horIniMeridianos" value="#{managerSeccion.horaria.meridianoInicio}">
                                <f:selectItems value="#{managerSeccion.meridianos}"/>
                            </h:selectOneMenu>
                        </h:panelGroup>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel"><h:outputText value="Fin"/></td>
                    <td>
                        <h:panelGroup>
                            <h:inputText id="horFinH" value="#{managerSeccion.horaria.horaFin}" maxlength="2"
                                         required="true" style="width: 20px;"/>
                            <h:outputLabel value=":"/>
                            <h:inputText id="horFinM" value="#{managerSeccion.horaria.minutoFin}" maxlength="2"
                                         required="true" style="width: 20px;"/>
                            <h:selectOneMenu id="horFinMeridianos" value="#{managerSeccion.horaria.meridianoFin}">
                                <f:selectItems value="#{managerSeccion.meridianos}"/>
                            </h:selectOneMenu>
                        </h:panelGroup>
                        <rich:spacer width="20px"/>

                        <a4j:commandButton image="/Imagenes/actions/edit_add.png"
                                           title="Agregar"
                                           actionListener="#{managerSeccion.agregarHorario}"
                                           oncomplete="#{managerSeccion.oncomplete}"
                                           reRender="formHoraria"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="4" align="center" valign="top">
                        <rich:dataTable id="tablaDetalle" var="hor" width="100%"
                                        value="#{managerSeccion.mpnListaHoraria}"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="D&iacute;a" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{hor.horDiaValue}" rendered="#{!hor.editable}"/>

                                <h:selectOneMenu value="#{hor.horDia}" rendered="#{hor.editable}">
                                    <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerSeccion.dias}"/>
                                </h:selectOneMenu>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Inicio"/>
                                </f:facet>
                                <h:outputText value="#{hor.horHini}" rendered="#{!hor.editable}">
                                    <f:convertDateTime pattern="hh:mm a"/>
                                </h:outputText>

                                <h:panelGroup rendered="#{hor.editable}">
                                    <h:inputText value="#{hor.horaInicio}" maxlength="2" required="true"
                                                 style="width: 20px;"/>
                                    <h:outputLabel value=":"/>
                                    <h:inputText value="#{hor.minutoInicio}" maxlength="2" required="true"
                                                 style="width: 20px;"/>
                                    <h:selectOneMenu value="#{hor.meridianoInicio}">
                                        <f:selectItems value="#{managerSeccion.meridianos}"/>
                                    </h:selectOneMenu>
                                </h:panelGroup>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Fin"/>
                                </f:facet>
                                <h:outputText value="#{hor.horHfin}" rendered="#{!hor.editable}">
                                    <f:convertDateTime pattern="hh:mm a"/>
                                </h:outputText>

                                <h:panelGroup rendered="#{hor.editable}">
                                    <h:inputText value="#{hor.horaFin}" maxlength="2" required="true"
                                                 style="width: 20px;"/>
                                    <h:outputLabel value=":"/>
                                    <h:inputText value="#{hor.minutoFin}" maxlength="2" required="true"
                                                 style="width: 20px;"/>
                                    <h:selectOneMenu value="#{hor.meridianoFin}">
                                        <f:selectItems value="#{managerSeccion.meridianos}"/>
                                    </h:selectOneMenu>
                                </h:panelGroup>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Aula"/>
                                </f:facet>
                                <h:outputText value="#{hor.acAula.aulDes}" rendered="#{!hor.editable}"/>

                                <h:selectOneMenu value="#{hor.acAula.id}" rendered="#{hor.editable}">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerSeccion.aulas}"/>
                                </h:selectOneMenu>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Docente"/>
                                </f:facet>
                                <h:outputText value="#{hor.acDocente.docNombre}" rendered="#{!hor.editable}"/>

                                <h:selectOneMenu value="#{hor.acDocente.id}" rendered="#{hor.editable}">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerSeccion.profesores}"/>
                                </h:selectOneMenu>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Tipo de Clase"/>
                                </f:facet>
                                <h:outputText value="#{hor.horTipoClaseValue}" rendered="#{!hor.editable}"/>

                                <h:selectOneMenu value="#{hor.horTipoClase}" rendered="#{hor.editable}">
                                    <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerSeccion.tipoClase}"/>
                                </h:selectOneMenu>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Editar"/>
                                </f:facet>
                                <a4j:commandButton actionListener="#{hor.edit}"
                                                   image="/Imagenes/actions/klipper_dock.png" title="Editar"
                                                   rendered="#{!hor.editable}" reRender="tablaDetalle"/>
                                <a4j:commandButton actionListener="#{hor.aceptar}"
                                                   image="/Imagenes/actions/fileexport.png" title="Aceptar"
                                                   rendered="#{hor.editable}" reRender="tablaDetalle"/>
                                <a4j:commandButton actionListener="#{hor.cancelar}"
                                                   image="/Imagenes/actions/fileclose.png" title="Cancelar"
                                                   rendered="#{hor.editable}" reRender="tablaDetalle"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Eliminar"/>
                                </f:facet>
                                <f:param id="p_hor_pos" value="#{hor.posicion}"/>
                                <a4j:commandButton image="/Imagenes/actions/edit_remove.png"
                                                   title="Eliminar"
                                                   actionListener="#{managerSeccion.removerHorario}"
                                                   reRender="tablaDetalle"/>
                            </rich:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpMatriculados" width="550" autosized="true" zindex="3000">
        <f:facet name="header">
            <h:outputText value="Matriculados"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpMatriculados')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="formMatriculados">
            <table style="width: 100%">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerSeccion.guardarMatriculados}"
                                           oncomplete="#{managerSeccion.oncomplete}"
                                           reRender="masterTable, masterScroll"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td class="tdLabel" style="width: 80px;">M&oacute;dulo</td>
                    <td><h:outputText id="matModulo" value="#{managerSeccion.modNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Curso</td>
                    <td><h:outputText id="matCurso" value="#{managerSeccion.curNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Taller</td>
                    <td><h:outputText id="matTaller" value="#{managerSeccion.talapeNombre}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Secci&oacute;n</td>
                    <td><h:outputText id="matSeccion" value="#{managerSeccion.mpSecDetalle}"/></td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Taller destino</td>
                    <td>
                        <h:selectOneMenu value="#{managerSeccion.mpTalDestId}"
                                         style="#{managerSeccion.style}"
                                         disabled="#{managerSeccion.totalMatriculados == 0}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSeccion.mpTalDestDesc}"/>
                            <a4j:support event="onchange" reRender="cb_sec,cb_estpag"
                                         actionListener="#{managerSeccion.loadSeccionesFromTaller}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel">Secci&oacute;n destino</td>
                    <td>
                        <h:selectOneMenu id="cb_sec" value="#{managerSeccion.mpSecDestId}"
                                         style="#{managerSeccion.style}"
                                         disabled="#{managerSeccion.totalMatriculados == 0}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSeccion.mpSecDestDesc}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel">Tipo de pago</td>
                    <td>
                        <h:selectOneMenu id="cb_estpag" value="#{managerSeccion.mpEstpagDestId}"
                                         style="#{managerSeccion.style}"
                                         disabled="#{managerSeccion.totalMatriculados == 0}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSeccion.mpEstpagDestDesc}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="4" align="center" valign="top" width="100%">
                        <h:panelGroup rendered="#{managerSeccion.totalMatriculados == 0}" styleClass="message error">
                            <h:outputText value="No hay alumnos matriculados"/>
                        </h:panelGroup>
                        <h:panelGroup rendered="#{managerSeccion.totalMatriculados != 0}">
                            <h:outputText value="Relaci&oacute;n de Alumnos matriculados" escape="false"/>
                            <div style="overflow: auto; height: 350px;">
                                <rich:dataTable id="matriculados" width="508"
                                                onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                                onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                                value="#{managerSeccion.listaAlumnosMatriculados}" var="alu">
                                    <rich:column style="text-align: center;">
                                        <f:facet name="header">
                                            <h:outputText value="Nro."/>
                                        </f:facet>
                                        <h:outputText value="#{alu.aluContador}"/>
                                    </rich:column>

                                    <rich:column>
                                        <f:facet name="header">
                                            <h:outputText value="C&oacute;digo" escape="false"/>
                                        </f:facet>
                                        <h:outputText value="#{alu.aluCodigo}"/>
                                    </rich:column>

                                    <rich:column>
                                        <f:facet name="header">
                                            <h:outputText value="Apellidos"/>
                                        </f:facet>
                                        <h:outputText value="#{alu.aluAppaterno} #{alu.aluApmaterno}"/>
                                    </rich:column>

                                    <rich:column>
                                        <f:facet name="header">
                                            <h:outputText value="Nombres"/>
                                        </f:facet>
                                        <h:outputText value="#{alu.aluNombres}"/>
                                    </rich:column>

                                    <rich:column style="text-align: center;">
                                        <f:facet name="header">
                                            <h:outputText value="Matricular"/>
                                        </f:facet>
                                        <h:selectBooleanCheckbox value="#{alu.aluMatriculado}"/>
                                    </rich:column>
                                </rich:dataTable>
                            </div>
                        </h:panelGroup>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
