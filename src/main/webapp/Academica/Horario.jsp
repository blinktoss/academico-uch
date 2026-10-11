<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de horarios</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table width="100%">
                <tr>
                    <td width="20%" colspan="2"><strong>LISTADO DE HORARIOS</strong></td>
                    <td width="30%"></td>
                    <td align=right width="50%">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                            </f:facet>
                        </a4j:status>
                        <h:commandButton type="button" id="buscar"
                                         action="#{managerHorario.cargarArbol}"
                                         image="/Imagenes/actions/reload.png"
                                         title="Refrescar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="5">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:top;">
                        <rich:tree value="#{managerHorario.arbol}" ajaxSubmitSelection="true"
                                   switchType="ajax" nodeSelectListener="#{managerHorario.seleccion}"
                                   var="item" reRender="tablaMaster, o_curso"/>
                    </td>
                    <td style="vertical-align:top;" colspan="5">
                        <div class="row">
                            <div class="col" style="text-align: right;">
                                <h:outputText value="#{managerHorario.lblCursoAperturado}" id="o_curso"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col">
                                <rich:dataTable id="tablaMaster" rows="30"
                                                onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                                onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                                cellpadding="0" cellspacing="0" border="0"
                                                width="100%" value="#{managerHorario.listaSecciones}"
                                                var="secciones">
                                    <h:column>
                                        <f:facet name="header">
                                            <h:outputText value="Id de la seccion"/>
                                        </f:facet>
                                        <h:outputText value="#{secciones.rowIdSec}"/>
                                    </h:column>
                                    <h:column>
                                        <f:facet name="header">
                                            <h:outputText value="Id del Semestre"/>
                                        </f:facet>
                                        <h:outputText value="#{secciones.rowIdSem}"/>
                                    </h:column>
                                    <h:column>
                                        <f:facet name="header">
                                            <h:outputText value="Codigo de la secci&oacute;n" escape="false"/>
                                        </f:facet>
                                        <h:outputText value="#{secciones.rowCodigoSec}"/>
                                    </h:column>
                                    <h:column>
                                        <f:facet name="header">
                                            <h:outputText value="Nombre de la secci&oacute;n" escape="false"/>
                                        </f:facet>
                                        <h:outputText value="#{secciones.rowNombreSec}"/>
                                    </h:column>
                                    <rich:column style="text-align: center">
                                        <f:facet name="header">
                                            <h:outputText value="Vacantes"/>
                                        </f:facet>
                                        <h:outputText value="#{secciones.rowVacantesSec}"/>
                                    </rich:column>
                                    <rich:column style="text-align: center">
                                        <f:facet name="header">
                                            <h:outputText value="Matriculados"/>
                                        </f:facet>
                                        <h:outputText value="#{secciones.rowVacantesOcupadas}"/>
                                    </rich:column>
                                    <rich:column style="text-align: center">
                                        <f:facet name="header">
                                            <h:outputText value="Asignar Horario"/>
                                        </f:facet>
                                        <f:param value="#{secciones.rowIdSec}" id="id_seccion"/>
                                        <f:param value="#{secciones.rowIdSem}" id="id_semestre"/>
                                        <a4j:commandButton image="/Imagenes/actions/horario.png"
                                                           actionListener="#{managerHorario.asignarHorario}"
                                                           oncomplete="Richfaces.showModalPanel('mpHorarios',{top:50});"
                                                           reRender="aulas,docentes,tipos,turnos,tablaHorario,nombreCurso"/>
                                    </rich:column>
                                </rich:dataTable>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpHorarios" zindex="2000" autosized="true" minWidth="800">
        <f:facet name="header">
            <h:outputText value="Registro de Horario Por Curso"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpHorarios')" title="Cerrar"/>
        </f:facet>
        <h:form id="formHorarios">
            <table width="100%">
                <tr>
                    <td colspan="2" style="text-align: right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerHorario.insertar}"
                                           oncomplete="alert('Se guardo el registro correctamente');Richfaces.hideModalPanel('mpHorarios')"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">Curso:</td>
                    <td><h:outputText value="#{managerHorario.lblCursoAperturado}" id="nombreCurso"/></td>
                </tr>
                <tr>
                    <td>Turno:</td>
                    <td>
                        <h:selectOneMenu value="#{managerHorario.mpTurnoId}" id="turnos">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerHorario.comboTurnos}"/>
                            <a4j:support event="onchange" actionListener="#{managerHorario.setearTurno}"
                                         reRender="tablaHorario,aulas,tipos,docentes"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Aula:</td>
                    <td>
                        <h:selectOneMenu value="#{managerHorario.mpAulaId}" id="aulas">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerHorario.comboAulas}"/>
                            <a4j:support event="onchange" reRender="tablaHorario"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Tipo:</td>
                    <td>
                        <h:selectOneMenu value="#{managerHorario.mpTipoId}" id="tipos">
                            <f:selectItems value="#{managerHorario.comboTipos}"/>
                            <a4j:support event="onchange" reRender="tablaHorario"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Docente:</td>
                    <td>
                        <h:selectOneMenu value="#{managerHorario.mpDocenteId}" id="docentes">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerHorario.comboDocentes}"/>
                            <a4j:support event="onchange" reRender="tablaHorario"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:dataTable id="tablaHorario" cellpadding="0" cellspacing="0" border="0" width="100%"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        value="#{managerHorario.tablaHorario}" var="horario">

                            <f:param value="#{managerHorario.mpTurnoId}" id="ho_turno"/>
                            <f:param value="#{managerHorario.mpAulaId}" id="ho_aula"/>
                            <f:param value="#{managerHorario.mpTipoId}" id="ho_tipo"/>
                            <f:param value="#{managerHorario.mpDocenteId}" id="ho_docente"/>
                            <f:param value="#{horario.mpRowIdHor}" id="param_id_turno"/>

                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Hora Inicio"/>
                                </f:facet>
                                <h:outputText value="#{horario.mpRowInicioHor}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Hora Fin"/>
                                </f:facet>
                                <h:outputText value="#{horario.mpRowFinHor}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Lunes"/>
                                </f:facet>
                                <f:param value="#{horario.mpRowIndexIdLun}" id="param_identificador_lunes"/>
                                <h:inputHidden value="#{horario.mpRowIndexIdLun}"/>
                                <rich:panel id="panelLunes">
                                    <h:outputText value="#{horario.mpRowHorLun}" id="lunes" escape="false"/>
                                    <a4j:support actionListener="#{horario.seleccionarLunes}"
                                                 immediate="true" event="onclick" reRender="tablaHorario"
                                                 oncomplete="#{horario.oncomplete}"/>
                                </rich:panel>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Martes"/>
                                </f:facet>
                                <f:param value="#{horario.mpRowIndexIdMar}" id="param_identificador_martes"/>
                                <h:inputHidden value="#{horario.mpRowIndexIdMar}"/>
                                <rich:panel id="panelMartes">
                                    <h:outputText value="#{horario.mpRowHorMar}" id="martes" escape="false"/>
                                    <a4j:support actionListener="#{horario.seleccionarMartes}"
                                                 immediate="true" event="onclick" reRender="tablaHorario"
                                                 oncomplete="#{horario.oncomplete}"/>
                                </rich:panel>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Miercoles"/>
                                </f:facet>
                                <f:param value="#{horario.mpRowIndexIdMie}" id="param_identificador_miercoles"/>
                                <h:inputHidden value="#{horario.mpRowIndexIdMie}"/>
                                <rich:panel id="panelMiercoles">
                                    <h:outputText value="#{horario.mpRowHorMie}" id="miercoles" escape="false"/>
                                    <a4j:support actionListener="#{horario.seleccionarMiercoles}"
                                                 immediate="true" event="onclick" reRender="tablaHorario"
                                                 oncomplete="#{horario.oncomplete}"/>
                                </rich:panel>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Jueves"/>
                                </f:facet>
                                <f:param value="#{horario.mpRowIndexIdJue}" id="param_identificador_jueves"/>
                                <h:inputHidden value="#{horario.mpRowIndexIdJue}"/>
                                <rich:panel id="panelJueves">
                                    <h:outputText value="#{horario.mpRowHorJue}" id="jueves" escape="false"/>
                                    <a4j:support actionListener="#{horario.seleccionarJueves}"
                                                 immediate="true" event="onclick" reRender="tablaHorario"
                                                 oncomplete="#{horario.oncomplete}"/>
                                </rich:panel>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Viernes"/>
                                </f:facet>
                                <f:param value="#{horario.mpRowIndexIdVie}" id="param_identificador_viernes"/>
                                <h:inputHidden value="#{horario.mpRowIndexIdVie}"/>
                                <rich:panel id="panelViernes">
                                    <h:outputText value="#{horario.mpRowHorVie}" id="viernes" escape="false"/>
                                    <a4j:support actionListener="#{horario.seleccionarViernes}"
                                                 immediate="true" event="onclick" reRender="tablaHorario"
                                                 oncomplete="#{horario.oncomplete}"/>
                                </rich:panel>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Sabado"/>
                                </f:facet>
                                <rich:panel id="panelSabado">
                                    <f:param value="#{horario.mpRowIndexIdSab}" id="param_identificador_sabado"/>
                                    <h:inputHidden value="#{horario.mpRowIndexIdSab}"/>
                                    <h:outputText value="#{horario.mpRowHorSab}" id="sabado" escape="false"/>
                                    <a4j:support actionListener="#{horario.seleccionarSabado}"
                                                 immediate="true" event="onclick" reRender="tablaHorario"
                                                 oncomplete="#{horario.oncomplete}"/>
                                </rich:panel>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Domingo"/>
                                </f:facet>
                                <rich:panel id="panelDomingo">
                                    <f:param value="#{horario.mpRowIndexIdDom}" id="param_identificador_domingo"/>
                                    <h:inputHidden value="#{horario.mpRowIndexIdDom}"/>
                                    <h:outputText value="#{horario.mpRowHorDom}" id="domingo" escape="false"/>
                                    <a4j:support actionListener="#{horario.seleccionarDomingo}"
                                                 immediate="true" event="onclick" reRender="tablaHorario"
                                                 oncomplete="#{horario.oncomplete}"/>
                                </rich:panel>
                            </h:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

</f:view>
</body>
</html>
