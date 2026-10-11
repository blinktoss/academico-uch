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
    <title>Asignar Contenido Tematico al Curso</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form>
            <table style="width: 100%">
                <tr>
                    <td colspan="2"><strong>MANTENIMIENTO DE TEM&Aacute;TICAS</strong></td>
                    <td width="4%">&nbsp;</td>
                    <td width="11%">&nbsp;</td>
                    <td width="16%">&nbsp;</td>
                    <td width="10%"></td>
                    <td width="7%"></td>
                    <td width="22%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>
                        <a4j:commandButton type="button" id="buscar" style=" width : 16px;"
                                           actionListener="#{managerUnidadTematica.seleccionar}"
                                           reRender="barra,tablaMaster"
                                           image="/Imagenes/actions/viewmag.png" title="Buscar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="8" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="13%" height="25" align="left">Facultad</td>
                    <td width="17%" align="left">
                        <h:selectOneMenu id="Facultad" style="width: 180px;"
                                         value="#{managerUnidadTematica.bFacultad}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerUnidadTematica.comboFacultadesS}"/>
                            <a4j:support event="onchange" reRender="Especialidad,PCurricular,Ciclo"
                                         actionListener="#{managerUnidadTematica.setearFacultadS}"/>
                        </h:selectOneMenu>
                    </td>
                    <td>&nbsp;</td>
                    <td align="left">Especialidad</td>
                    <td align="left">
                        <h:selectOneMenu id="Especialidad" style="width: 180px;"
                                         value="#{managerUnidadTematica.bEspecialidad}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerUnidadTematica.comboEspecialidadesS}"/>
                            <a4j:support event="onchange" reRender="PCurricular,Ciclo"
                                         actionListener="#{managerUnidadTematica.setearEspecialidadS}"/>
                        </h:selectOneMenu>
                    </td>
                    <td colspan="3">&nbsp;</td>
                </tr>
                <tr>
                    <td align="left">Plan Curricular</td>
                    <td align="left">
                        <h:selectOneMenu id="PCurricular" style="width: 180px;"
                                         value="#{managerUnidadTematica.bPlanCurricular}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerUnidadTematica.comboPlanCurricularS}"/>
                            <a4j:support event="onchange" reRender="Ciclo"
                                         actionListener="#{managerUnidadTematica.setearPCurricularS}"/>
                        </h:selectOneMenu>
                    </td>
                    <td></td>
                    <td align="left">Ciclo</td>
                    <td align="left">
                        <h:selectOneMenu id="Ciclo" value="#{managerUnidadTematica.bCiclo}">
                            <f:selectItems value="#{managerUnidadTematica.comboCicloS}"/>
                        </h:selectOneMenu>
                    </td>
                    <td colspan="3"></td>
                </tr>
                <tr>
                    <td colspan="8" width="100%"><rich:separator height="1"/></td>
                </tr>
            </table>
        </h:form>
        <h:form>
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" border="0" width="100%"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerUnidadTematica.listaDatos}" var="cursos">
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{cursos.planCursoId}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Ciclo"/>
                    </f:facet>
                    <h:outputText value="#{cursos.ciclo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>
                    <h:outputText value="#{cursos.nombre}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Cr&eacute;ditos" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{cursos.creditos}"/>
                </rich:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Tipo"/>
                    </f:facet>
                    <h:outputText value="#{cursos.tipo}"/>
                </h:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Asignar Temas"/>
                    </f:facet>
                    <h:graphicImage value="/Imagenes/actions/pencil.png" style="cursor: pointer">
                        <a4j:support event="onclick" actionListener="#{managerUnidadTematica.asignar}"
                                     reRender="tablaDetalle,scrolldetalle,pc_Titulo,pc_Des,pc_Vigente,pc_Esp,pc_Des1,pc_Ciclo,pc_Curso,sema1,areacontenido"
                                     oncomplete="Richfaces.showModalPanel('nuevo',{top:8})"/>
                    </h:graphicImage>
                    <f:param id="p_id" value="#{cursos.planCursoId}"/>
                </rich:column>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="nuevo" autosized="true" minWidth="800" zindex="2000">
            <f:facet name="header">
                <h:outputText value="ASIGNACI&Oacute;N DE CONTENIDO TEM&Aacute;TICO" escape="false"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('nuevo')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table width="100%">
                    <tr>
                        <td align="right" colspan="4">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerUnidadTematica.guardar}"
                                               reRender="tablaDetalle,scrolldetalle,pc_Titulo,pc_Des,pc_Vigente,pc_Esp,pc_Des1,pc_Ciclo,pc_Curso"
                                               oncomplete="#{managerUnidadTematica.mensajes}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td width="12%">Especialidad:</td>
                        <td width="48%"><h:outputText id="pc_Esp"
                                                      value="#{managerUnidadTematica.especialidadDescripcion}"/></td>
                        <td width="6%">Ciclo:</td>
                        <td><h:outputText id="pc_Ciclo" value="#{managerUnidadTematica.cicloDescripcion}"/></td>
                    </tr>
                    <tr>
                        <td>Plan Curricular:</td>
                        <td><h:outputText id="pc_Des1" value="#{managerUnidadTematica.planDescripcion}"/></td>
                        <td>Curso:</td>
                        <td><h:outputText id="pc_Curso" value="#{managerUnidadTematica.cursoDescripcion}"/></td>
                    </tr>
                </table>

                <table width="100%">
                    <tr>
                        <td width="12%">T&iacute;tulo:</td>
                        <td>
                            <h:inputText id="pc_Titulo" value="#{managerUnidadTematica.ctTituloU}" style="width: 100%;"
                                         maxlength="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Descripci&oacute;n:</td>
                        <td>
                            <h:inputTextarea id="pc_Des" style="width: 100%;"
                                             value="#{managerUnidadTematica.ctDescripcionU}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Vigente:</td>
                        <td>
                            <h:selectBooleanCheckbox id="pc_Vigente" value="#{managerUnidadTematica.ctVigenteU}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td>Contenido de la Semana&nbsp;<h:outputText id="sema1"
                                                                      value="#{managerUnidadTematica.nroSemana}"
                                                                      style="font-weight: bold"/></td>
                        <td>
                            <h:inputTextarea id="areacontenido" value="#{managerUnidadTematica.detContenido}"
                                             style="width: 100%"/>
                        </td>
                        <td style="text-align: center">
                            <a4j:commandButton image="/Imagenes/actions/edit_add.png"
                                               actionListener="#{managerUnidadTematica.agregar}"
                                               reRender="tablaDetalle,scrolldetalle,areacontenido,sema1"
                                               title="Adicionar Contenido de Semana"/>
                        </td>
                    </tr>
                </table>
            </h:form>
            <h:form>
                <rich:datascroller id="scrolldetalle" align="right" for="tablaDetalle" maxPages="10"
                                   style="width: 100%;"/>
                <rich:dataTable id="tablaDetalle" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                                onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                value="#{managerUnidadTematica.listaDatosDetalle}" var="detalle">

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Sem."/>
                        </f:facet>
                        <h:outputText value="#{detalle.detNroSemana}" style="width: 40px"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Contenido"/>
                        </f:facet>
                        <h:inputTextarea value="#{detalle.detContenido}" rendered="#{detalle.editableb2}"
                                         style="width: 99%"/>

                        <h:outputText id="txtCont" styleClass="uch-word-wrap" style="width: 600px"
                                      value="#{detalle.detContenido}" rendered="#{detalle.editableb1}"/>
                    </rich:column>
                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Editar"/>
                        </f:facet>
                        <f:param id="tc_id" value="#{detalle.detId}"/>
                        <a4j:commandButton actionListener="#{detalle.editarFilaDetalle}"
                                           image="/Imagenes/actions/klipper_dock.png" title="Editar"
                                           rendered="#{detalle.editableb1}"
                                           reRender="tablaDetalle,scrolldetalle"/>
                        <a4j:commandButton actionListener="#{detalle.grabar}"
                                           image="/Imagenes/actions/filesave.png" title="Guardar"
                                           rendered="#{detalle.editableb2}"
                                           reRender="tablaDetalle,scrolldetalle"/>
                        <a4j:commandButton actionListener="#{detalle.cancelar}"
                                           image="/Imagenes/actions/fileclose.png" title="Cancelar"
                                           rendered="#{detalle.editableb2}"
                                           reRender="tablaDetalle,scrolldetalle,txtCont"/>
                    </rich:column>
                    <rich:column>
                        <a4j:commandButton actionListener="#{managerUnidadTematica.quitar}"
                                           image="/Imagenes/actions/edit_remove.png" title="Eliminar Semana"
                                           reRender="tablaDetalle,scrolldetalle,areacontenido,sema1"/>
                    </rich:column>
                </rich:dataTable>
            </h:form>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>
