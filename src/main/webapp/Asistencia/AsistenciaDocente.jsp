<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Asistencia docente</title>
    <script type="text/javascript">
        function printObjectsSelected_u(sgcomponent, idAL, nombreAL, codigoAL) {
            idAL.value = sgcomponent.getSelectedItems().pluck('sugDocId');
            nombreAL.innerHTML = sgcomponent.getSelectedItems().pluck('sugDocNombre');
            codigoAL.innerHTML = sgcomponent.getSelectedItems().pluck('sugDocCodigo');
        }
    </script>
</head>
<f:view>
    <body>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1" style="width: 100%">
            <table style="width: 100%">
                <tr>
                    <td width="100%" colspan="2"><strong>MANTENIMIENTO ASISTENCIA</strong>
                    </td>
                    <td align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td>
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor:pointer;">
                            <a4j:support event="onclick"
                                         oncomplete="Richfaces.showModalPanel('mpNuevaSesion')"
                                         action="#{managerSEAsistenciaDocente.limpiar}"
                                         reRender="form4, n_doc, n_fecha_ses,n_ho_reg, n_mi_reg, n_ho_sal,
                                                  n_mi_sal, n_tipasis, n_tipses, n_especialidad, n_seccion, n_curso"/>
                        </h:graphicImage>
                    </td>
                    <td>
                        <a4j:commandButton image="/Imagenes/actions/viewmag.png"
                                           actionListener="#{managerSEAsistenciaDocente.mostrarSessionAsistencia}"
                                           reRender="fromTableAsisDoc, scrollTableAsisDoc, tableAsisDoc"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="6" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td width="10%">Docente:</td>
                    <td>
                        <h:inputText id="txt_dato" value="#{managerSEAsistenciaDocente.suggestion}"
                                     style="width: 300px;" maxlength="10"/>
                        <rich:suggestionbox id="suggestion" width="400"
                                            for="txt_dato" nothingLabel="Docente No Encontrado"
                                            suggestionAction="#{managerSEAsistenciaDocente.autocomplete}" var="asis"
                                            cellpadding="2" cellspacing="2" shadowOpacity="4" shadowDepth="4"
                                            minChars="3" rules="none" zindex="3500" usingSuggestObjects="true"
                                            onobjectchange="printObjectsSelected_u(#{rich:component('suggestion')},#{rich:element('idAL')},#{rich:element('codigoAL')},#{rich:element('nombreAL')});">
                            <h:column>
                                <h:outputText value="#{asis.sugDocNombre}" style="font-weight: bold;"/>
                            </h:column>
                            <h:column>
                                <h:outputText value="#{asis.sugDocCodigo}" style="font-weight: bold;"/>
                            </h:column>
                        </rich:suggestionbox>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td width="10%">Fecha de inicio:</td>
                    <td>
                        <rich:calendar id="b_fecha_ini"
                                       value="#{managerSEAsistenciaDocente.paramSessionFechaIni}"
                                       inputStyle="width: 120px;"
                                       showWeeksBar="false"
                                       datePattern="yyyy-MM-dd"/>
                    </td>
                    <td></td>
                </tr>
                <tr style="height: 24px;">
                    <td width="10%">Fecha fin:</td>
                    <td>
                        <rich:calendar id="b_fecha_fin"
                                       value="#{managerSEAsistenciaDocente.paramSessionFechaFin}"
                                       inputStyle="width: 120px;"
                                       showWeeksBar="false"
                                       datePattern="yyyy-MM-dd"/></td>
                </tr>
                <tr style="height: 24px;">
                    <td width="10%">Tipo de asistencia:</td>
                    <td>
                        <h:selectOneMenu value="#{managerSEAsistenciaDocente.paramTipasisCode}" style="width : 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSEAsistenciaDocente.tiposAsistencia}"/>
                        </h:selectOneMenu>
                    </td>
                    <td></td>
                </tr>
                <tr style="height: 24px;">
                    <td colspan="6">
                        <i>
                            <h:outputText id="codigoAL" value="#{managerSEAsistenciaDocente.selectedDocCodigo}"/> -
                            <h:outputText id="nombreAL" value="#{managerSEAsistenciaDocente.selectedDocNombre}"/>
                        </i>
                        <h:inputHidden id="idAL" value="#{managerSEAsistenciaDocente.paramDocIdBk}"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="6" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form id="fromTableAsisDoc" style="width: 100%">
            <rich:datascroller id="scrollTableAsisDoc" for="tableAsisDoc" align="right" maxPages="20"/>
            <rich:dataTable id="tableAsisDoc" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerSEAsistenciaDocente.listarSessiones}" var="ses">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{ses.rowSesId}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Docente"/>
                    </f:facet>
                    <h:outputText value="#{ses.rowDocNombre}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Fecha"/>
                    </f:facet>
                    <h:outputText value="#{ses.rowFechaRegistro}">
                        <f:convertDateTime pattern="EEEEE dd  'de'  MMMMMM  'del'  yyyy"/>
                    </h:outputText>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Secci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{ses.rowSecNombre}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Curso"/>
                    </f:facet>
                    <h:outputText value="#{ses.rowCurNombre}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Hra. Inicio"/>
                    </f:facet>
                    <h:outputText value="#{ses.rowSesHoraIni}">
                        <f:convertDateTime pattern="hh:mm:ss a"/>
                    </h:outputText>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Hra. Fin"/>
                    </f:facet>
                    <h:outputText value="#{ses.rowSesHoraFin}">
                        <f:convertDateTime pattern="hh:mm:ss a"/>
                    </h:outputText>
                    <f:param id="w_ses_id" value="#{ses.rowSesId}"/>
                    <f:param id="w_doc_id" value="#{ses.sugDocId}"/>
                    <f:param id="p_hora_inicio" value="#{ses.rowSesHoraIni}"/>
                    <f:param id="p_hora_fin" value="#{ses.rowSesHoraFin}"/>
                    <f:param id="p_cur_nombre" value="#{ses.rowCurNombre}"/>
                    <f:param id="p_sec_nombre" value="#{ses.rowSecNombre}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/editpaste.png"
                                       title="Editar sesion del docente"
                                       actionListener="#{managerSEAsistenciaDocente.seleccionarSesion}"
                                       oncomplete="#{managerSEAsistenciaDocente.oncomplete}"
                                       reRender="s_docente, s_cambio, s_curso, s_especialidad, s_seccion, s_inicio, s_fin"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Marcar sesi&oacute;n" escape="false"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/matricula.png"
                                       title="Marcar asistencia del docente"
                                       actionListener="#{managerSEAsistenciaDocente.seleccionarMarcaDocente}"
                                       oncomplete="#{managerSEAsistenciaDocente.oncomplete}"
                                       reRender="a_doc_nombre, a_sec_nombre, a_estasis, a_cur_nombre,a_fecha_reg,a_ho_reg, a_reg_lbl, a_sal_lbl, a_mi_reg, a_ho_sal, a_mi_sal, a_obs"/>
                </rich:column>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpAsisDocente" zindex="4000" autosized="true" minWidth="400">
        <f:facet name="header">
            <h:outputText value="Asistencia Docente"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpAsisDocente')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form2">
            <table style="width: 100%">
                <tr>
                    <td align=right colspan="2">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           oncomplete="#{managerSEAsistenciaDocente.oncomplete}"
                                           actionListener="#{managerSEAsistenciaDocente.guardarMarcaAsistencia}"
                                           reRender="fromTableAsisDoc, scrollTableAsisDoc, tableAsisDoc"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>Docente</td>
                    <td>
                        <h:outputText value="#{managerSEAsistenciaDocente.asisDocente.docNombre}" id="a_doc_nombre"/>
                        <f:param id="p_ses_id" value="#{managerSEAsistenciaDocente.asisDocente.sesId}"/>
                        <f:param id="p_doc_id" value="#{managerSEAsistenciaDocente.asisDocente.docId}"/>
                        <f:param id="p_sesefec_id" value="#{managerSEAsistenciaDocente.asisDocente.sesefeasisdocId}"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>Estado de asistencia docente</td>
                    <td>
                        <h:selectOneMenu value="#{managerSEAsistenciaDocente.asisDocente.estasisCode}"
                                         style="width: 220px;" id="a_estasis">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSEAsistenciaDocente.estasis}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td><h:outputText value="Secci&oacute;n" escape="false"/></td>
                    <td>
                        <h:outputText value="#{managerSEAsistenciaDocente.asisDocente.secNombre}" id="a_sec_nombre"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td><h:outputText value="Curso"/></td>
                    <td>
                        <h:outputText value="#{managerSEAsistenciaDocente.asisDocente.curNombre}" id="a_cur_nombre"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td><h:outputText value="Fecha Registro"/></td>
                    <td>
                        <h:outputText value="#{managerSEAsistenciaDocente.asisDocente.sesefeasisdocFecha}"
                                      id="a_fecha_reg">
                            <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy"/>
                        </h:outputText>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td><h:outputText value="Hora Inicio"/></td>
                    <td>
                        <table>
                            <tr>
                                <td colspan="3" style="font-size: 12px; font-weight: bold;">
                                    <h:outputLabel id="a_reg_lbl"
                                                   value="#{managerSEAsistenciaDocente.asisDocente.registro}">
                                        <f:convertDateTime pattern="HH:mm"/>
                                    </h:outputLabel>
                                </td>
                            </tr>
                            <tr>
                                <td><rich:inputNumberSpinner id="a_ho_reg"
                                                             value="#{managerSEAsistenciaDocente.asisDocente.regHora}"
                                                             minValue="0" maxValue="23" inputStyle="width: 20px;"/></td>
                                <td> :</td>
                                <td><rich:inputNumberSpinner id="a_mi_reg"
                                                             value="#{managerSEAsistenciaDocente.asisDocente.regMin}"
                                                             minValue="0" maxValue="59" inputStyle="width: 20px;"/></td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td><h:outputText value="Hora Fin"/></td>
                    <td>
                        <table>
                            <tr>
                                <td colspan="3" style="font-size: 12px; font-weight: bold;">
                                    <h:outputLabel id="a_sal_lbl"
                                                   value="#{managerSEAsistenciaDocente.asisDocente.salida}">
                                        <f:convertDateTime pattern="HH:mm"/>
                                    </h:outputLabel>
                                </td>
                            </tr>
                            <tr>
                                <td><rich:inputNumberSpinner id="a_ho_sal"
                                                             value="#{managerSEAsistenciaDocente.asisDocente.salHora}"
                                                             minValue="0" maxValue="23" inputStyle="width: 20px;"/></td>
                                <td> :</td>
                                <td><rich:inputNumberSpinner id="a_mi_sal"
                                                             value="#{managerSEAsistenciaDocente.asisDocente.salMin}"
                                                             minValue="0" maxValue="59" inputStyle="width: 20px;"/></td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td><h:outputText value="Observaci&oacute;n" escape="false"/></td>
                    <td colspan="2">
                        <h:inputTextarea id="a_obs" cols="20" rows="2"
                                         value="#{managerSEAsistenciaDocente.asisDocente.sesefeasisdocObs}"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpSessionAsis" autosized="true" minWidth="400" zindex="4000">
        <f:facet name="header">
            <h:outputText value="Actualizar sesi&oacute;n" escape="false"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png"
                                style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpSessionAsis')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form3">
            <table style="width: 100%">
                <tr>
                    <td align=right colspan="2">
                        <f:param id="p_ses_id" value="#{managerSEAsistenciaDocente.rowSesId}"/>
                        <f:param id="p_cambio" value="#{managerSEAsistenciaDocente.mpDocId}"/>
                        <f:param id="p_doc_ant" value="#{managerSEAsistenciaDocente.paramDocIdBk}"/>
                        <f:param id="p_inicio" value="#{managerSEAsistenciaDocente.mpHoraIni2}"/>
                        <f:param id="p_fin" value="#{managerSEAsistenciaDocente.mpHoraFin2}"/>

                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerSEAsistenciaDocente.actualizarSesion}"
                                           oncomplete="#{managerSEAsistenciaDocente.oncomplete}"
                                           reRender="fromTableAsisDoc, scrollTableAsisDoc, tableAsisDoc"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="Docente"/>
                    </td>
                    <td>
                        <h:outputText value="#{managerSEAsistenciaDocente.sugDocNombre}" id="s_docente"/>
                        <h:outputText value="#{managerSEAsistenciaDocente.sugDocId}" id="s_doc_ant" rendered="false"/>
                        <h:outputText value="#{managerSEAsistenciaDocente.rowSesId}" id="s_ses_id" rendered="false"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="Docente Reemplazo"/>
                    </td>
                    <td>
                        <h:selectOneMenu id="s_cambio" value="#{managerSEAsistenciaDocente.mpDocId}"
                                         style="width: 280px;" tabindex="11">
                            <f:selectItems value="#{managerSEAsistenciaDocente.cboDocente}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="Especialidad"/>
                    </td>
                    <td>
                        <h:outputText id="s_especialidad" value="#{managerSEAsistenciaDocente.mpEspNombre}"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="Secci&oacute;n" escape="false"/>
                    </td>
                    <td>
                        <h:outputText id="s_seccion" value="#{managerSEAsistenciaDocente.rowSecNombre}"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="Curso"/>
                    </td>
                    <td>
                        <h:outputText id="s_curso" value="#{managerSEAsistenciaDocente.rowCurNombre}"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="F. Inicio"/>
                    </td>
                    <td>
                        <rich:calendar id="s_inicio" datePattern="dd-MM-yyyy hh:mm a" showApplyButton="true"
                                       value="#{managerSEAsistenciaDocente.mpHoraIni2}"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="F. Fin"/>
                    </td>
                    <td>
                        <rich:calendar id="s_fin" datePattern="dd-MM-yyyy hh:mm a" showApplyButton="true"
                                       value="#{managerSEAsistenciaDocente.mpHoraFin2}"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpNuevaSesion" zindex="4000" autosized="true" minWidth="500">
        <f:facet name="header">
            <h:outputText value="Nueva sesi&oacute;n" escape="false"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png"
                                style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpNuevaSesion')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form4">
            <table style="width: 100%">
                <tr>
                    <td align=right colspan="2">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           action="#{managerSEAsistenciaDocente.validarNuevaSesion}"
                                           oncomplete="#{managerSEAsistenciaDocente.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td width="50%">Docente:</td>
                    <td>
                        <h:selectOneMenu id="n_doc" value="#{managerSEAsistenciaDocente.nuevAsisDocente.docId}"
                                         style="width : 270px;">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSEAsistenciaDocente.cboDocente}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="Especialidad:"/>
                    </td>
                    <td>
                        <h:selectOneMenu id="n_especialidad"
                                         value="#{managerSEAsistenciaDocente.nuevAsisDocente.especialidadCode}"
                                         style="width : 380px;" tabindex="11">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSEAsistenciaDocente.especialidades}"/>
                            <a4j:support event="onchange" reRender="n_seccion, n_curso"
                                         actionListener="#{managerSEAsistenciaDocente.onChangeEspecialidades}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>
                        <h:outputText value="Secci&oacute;n:" escape="false"/>
                    </td>
                    <td>
                        <h:selectOneMenu id="n_seccion"
                                         value="#{managerSEAsistenciaDocente.nuevAsisDocente.seccionCode}"
                                         style="width : 180px;" tabindex="11">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSEAsistenciaDocente.secciones}"/>
                            <a4j:support event="onchange" reRender="n_curso"
                                         actionListener="#{managerSEAsistenciaDocente.onChangeSecciones}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>Curso:</td>
                    <td colspan="2">
                        <h:selectOneMenu id="n_curso"
                                         value="#{managerSEAsistenciaDocente.nuevAsisDocente.cursoSeccion}"
                                         style="width : 310px;" tabindex="11">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSEAsistenciaDocente.cursos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>Fecha de la sesi&oacute;n:</td>
                    <td>
                        <rich:calendar id="n_fecha_ses"
                                       value="#{managerSEAsistenciaDocente.nuevAsisDocente.fecSesion}"
                                       showWeeksBar="false"
                                       datePattern="yyyy-MM-dd"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>Hora Inicio:</td>
                    <td>
                        <table>
                            <tr>
                                <td><rich:inputNumberSpinner id="n_ho_reg" minValue="0" maxValue="23"
                                                             inputStyle="width: 20px;"
                                                             value="#{managerSEAsistenciaDocente.nuevAsisDocente.regHora}"/></td>
                                <td> :</td>
                                <td><rich:inputNumberSpinner id="n_mi_reg" minValue="0" maxValue="59"
                                                             inputStyle="width: 20px;"
                                                             value="#{managerSEAsistenciaDocente.nuevAsisDocente.regMin}"/></td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>Hora Fin:</td>
                    <td>
                        <table>
                            <tr>
                                <td><rich:inputNumberSpinner id="n_ho_sal"
                                                             value="#{managerSEAsistenciaDocente.nuevAsisDocente.salHora}"
                                                             minValue="0" maxValue="23" inputStyle="width: 20px;"/></td>
                                <td> :</td>
                                <td><rich:inputNumberSpinner id="n_mi_sal"
                                                             value="#{managerSEAsistenciaDocente.nuevAsisDocente.salMin}"
                                                             minValue="0" maxValue="59" inputStyle="width: 20px;"/></td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>Tipo de asistencia:</td>
                    <td>
                        <h:selectOneMenu id="n_tipasis"
                                         value="#{managerSEAsistenciaDocente.nuevAsisDocente.tipasisCode}"
                                         style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSEAsistenciaDocente.tiposAsistencia}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td>Tipo de sesion:</td>
                    <td>
                        <h:selectOneMenu id="n_tipses"
                                         value="#{managerSEAsistenciaDocente.nuevAsisDocente.tipsesCode}"
                                         style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerSEAsistenciaDocente.tiposSesiones}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
    </body>
</f:view>
</html>
