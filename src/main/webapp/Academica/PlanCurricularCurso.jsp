<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Asignar Cursos a Plan Curricular</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <a4j:form>
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2">
                        <strong>AGREGAR CURSOS A PLAN CURRICULAR</strong>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer">
                            <a4j:support oncomplete="Richfaces.showModalPanel('mp',{top:80})" event="onclick"
                                         actionListener="#{managerPlanCurricularCurso.nuevo}"
                                         reRender="iId,iFacultad,iEspecialidad,iCodigo,iDescripcion,iCreacion,iResolucion"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/fileprint.png" title="Imprimir Listado"/>
                    </td>
                    <td align=right width="50%">
                        <a4j:commandButton actionListener="#{managerPlanCurricularCurso.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png" title="Buscar"
                                           reRender="scrollerMaster,tablaMaster"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="3" lineType="double"/></td>
                </tr>
                <tr>
                    <td width="20%" colspan="2"></td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Facultad:</td>
                    <td width="70%">
                        <h:selectOneMenu id="Facultad" value="#{managerPlanCurricularCurso.paramFacultadId}">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerPlanCurricularCurso.paramFacultades}"/>
                            <a4j:support event="onchange" reRender="Especialidad"
                                         actionListener="#{managerPlanCurricularCurso.cargarParamEspecialidades}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td width="10%">Especialidad:</td>
                    <td width="70%">
                        <h:selectOneMenu id="Especialidad" value="#{managerPlanCurricularCurso.paramEspecialidadId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerPlanCurricularCurso.comboEspecialidadesS}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="10%">C&oacute;digo:</td>
                    <td width="30%">
                        <h:inputText id="txtCodigo" value="#{managerPlanCurricularCurso.paramCodigo}"
                                     style="width: 180px;"
                                     maxlength="10"/>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Nombre:</td>
                    <td width="30%">
                        <h:inputText value="#{managerPlanCurricularCurso.paramDescripcion}" style="width: 180px;"/>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="3" lineType="double"/></td>
                </tr>
                <tr>
                    <td></td>
                </tr>
            </table>
        </a4j:form>
        <a4j:form>
            <rich:datascroller id="scrollerMaster" align="right" for="tablaMaster" maxPages="8"
                               style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerPlanCurricularCurso.listaPlanCurricular}" var="planCurricular">

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{planCurricular.rowId}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{planCurricular.rowCodigo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{planCurricular.rowDescripcion}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Resoluci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{planCurricular.rowResolucion}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Fecha Creaci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{planCurricular.rowCreacion}">
                        <f:convertDateTime pattern="dd/MM/yyyy"/>
                    </h:outputText>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Facultad"/>
                    </f:facet>
                    <h:outputText value="#{planCurricular.rowFacultadDesc}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Especialidad"/>
                    </f:facet>
                    <h:outputText value="#{planCurricular.rowEspecialidadDesc}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <h:graphicImage value="/Imagenes/actions/editpaste.png" style="cursor: pointer">
                        <a4j:support oncomplete="Richfaces.showModalPanel('mp',{top:80})" event="onclick"
                                     actionListener="#{managerPlanCurricularCurso.editarFila}"
                                     reRender="iId,iCodigo,iCreacion,iDescripcion,iResolucion,iFacultad,iEspecialidad"/>
                    </h:graphicImage>
                    <f:param id="p_id" value="#{planCurricular.rowId}"/>
                    <f:param id="p_codigo" value="#{planCurricular.rowCodigo}"/>
                    <f:param id="p_descripcion" value="#{planCurricular.rowDescripcion}"/>
                    <f:param id="p_facultad" value="#{planCurricular.facultadId}"/>
                    <f:param id="p_facultad_d" value="#{planCurricular.rowFacultadDesc}"/>
                    <f:param id="p_especialidad" value="#{planCurricular.especialidadId}"/>
                    <f:param id="p_especialidad_d" value="#{planCurricular.rowEspecialidadDesc}"/>
                    <f:param id="p_resolucion" value="#{planCurricular.rowResolucion}"/>
                    <f:param id="p_creacion" value="#{planCurricular.rowCreacion}"/>

                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Cursos"/>
                    </f:facet>
                    <h:graphicImage title="Asignar Cursos" value="/Imagenes/actions/pencil.png" style="cursor: pointer">
                        <a4j:support oncomplete="Richfaces.showModalPanel('mp1',{top:80})" event="onclick"
                                     actionListener="#{managerPlanCurricularCurso.editarFila}"
                                     action="#{managerPlanCurricularCurso.openModalForInsertCourses}"
                                     reRender="oId,oCodigo,oCreacion,tablaGrupos,nombre1,oDescripcion,oResolucion,oFacultad,oEspecialidad,scroll1,nombre4,codigo1,nombre1,creditos1,tipo1"/>

                    </h:graphicImage>
                    <f:param id="cursos" value="#{planCurricular.rowId}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Equivalencia"/>
                    </f:facet>
                    <a4j:commandButton actionListener="#{managerPlanCurricularCurso.openModalEquivalencias}"
                                       image="/Imagenes/actions/plan.png"
                                       oncomplete="Richfaces.showModalPanel('mpEquiv', {top: 80})"
                                       reRender="tableEquiv">
                        <f:attribute name="plancur_id" value="#{planCurricular.rowId}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <a4j:commandButton actionListener="#{managerPlanCurricularCurso.eliminarFila}"
                                       image="/Imagenes/actions/no.png" title="Eliminar"
                                       onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"
                                       reRender="scrollerMaster,tablaMaster"/>
                    <f:param id="delete" value="#{planCurricular.rowId}"/>
                </rich:column>

            </rich:dataTable>
        </a4j:form>
    </rich:panel>

    <rich:modalPanel id="mp" autosized="true" minWidth="540" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro de Plan Curricular"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
        </f:facet>
        <a4j:form>
            <table width="100%">
                <tr>
                    <td align="right" colspan="4">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerPlanCurricularCurso.enviar}"
                                           oncomplete="#{managerPlanCurricularCurso.oncomplete}"
                                           reRender="scrollerMaster,tablaMaster"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="3" lineType="double"/></td>
                </tr>
                <tr>
                    <td width="30%">Facultad:</td>

                    <td width="70%">
                        <h:inputHidden id="iId" value="#{managerPlanCurricularCurso.mpId}"/>

                        <h:selectOneMenu id="iFacultad" value="#{managerPlanCurricularCurso.mpFacultadId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerPlanCurricularCurso.mpFacultades}"/>
                            <a4j:support event="onchange" reRender="iEspecialidad" ignoreDupResponses="true"
                                         actionListener="#{managerPlanCurricularCurso.cargarEspecialidades}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Especialidad:</td>
                    <td width="70%">
                        <h:selectOneMenu id="iEspecialidad" value="#{managerPlanCurricularCurso.mpEspecialidadId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerPlanCurricularCurso.mpEspecialidades}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">C&oacute;digo de Plan:</td>
                    <td width="70%">
                        <h:inputText id="iCodigo" style="width : 180px;"
                                     value="#{managerPlanCurricularCurso.mpCodigo}"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">Descripci&oacute;n:</td>
                    <td width="70%">
                        <h:inputTextarea id="iDescripcion" style="width : 180px;"
                                         value="#{managerPlanCurricularCurso.mpDescripcion}"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">Fecha de Creaci&oacute;n:</td>
                    <td width="70%">
                        <rich:calendar id="iCreacion" datePattern="dd/MM/yyyy"
                                       value="#{managerPlanCurricularCurso.mpCreacion}"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">Resoluci&oacute;n Rectoral:</td>
                    <td width="70%">
                        <h:inputText label="Resolucion" id="iResolucion" style="width : 180px;"
                                     value="#{managerPlanCurricularCurso.mpResolucion}"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td colspan="4"><rich:separator height="3" lineType="double"/></td>
                </tr>
            </table>
        </a4j:form>
    </rich:modalPanel>

    <rich:modalPanel id="mp1" autosized="true" minWidth="800" zindex="2000">
        <f:facet name="header">
            <h:outputText value="ASIGNACI&Oacute;N DE CURSOS" escape="false"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp1')" title="Cerrar"/>
        </f:facet>
        <a4j:form>
            <table style="width: 100%">
                <tr>
                    <td align="right" colspan="4">
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Facultad:</td>
                    <td width="70%">
                        <h:inputHidden id="oId" value="#{managerPlanCurricularCurso.mpId}"/>
                        <h:outputText id="oFacultad" value="#{managerPlanCurricularCurso.lblFacultadDescripcion}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Especialidad:</td>
                    <td width="70%">
                        <h:outputText id="oEspecialidad"
                                      value="#{managerPlanCurricularCurso.lblEspecialidadDescripcion}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">C&oacute;digo de Plan:</td>
                    <td width="70%">
                        <h:outputText id="oCodigo" value="#{managerPlanCurricularCurso.mpCodigo}"
                                      style="width : 180px;"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Descripci&oacute;n:</td>
                    <td width="70%">
                        <h:outputText id="oDescripcion" value="#{managerPlanCurricularCurso.mpDescripcion}"
                                      style="width : 180px;"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Fecha de Creaci&oacute;n:</td>
                    <td width="70%">
                        <h:outputText id="oCreacion" value="#{managerPlanCurricularCurso.lblCreacionDescripcion}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Resoluci&oacute;n Rectoral:</td>
                    <td width="70%">
                        <h:outputText id="oResolucion" value="#{managerPlanCurricularCurso.mpResolucion}"
                                      style="width : 180px;"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="4"><rich:separator height="2" lineType="double"/></td>
                </tr>
                <tr>
                    <td colspan="5">
                        <table style="width: 100%;">
                            <tr>
                                <td width="30%">Periodo:
                                </td>
                                <td width="30%">
                                    <h:selectOneMenu id="nombre4" value="#{managerPlanCurricularCurso.ciclo}">
                                        <f:selectItems value="#{managerPlanCurricularCurso.comboSemestre}"/>
                                    </h:selectOneMenu>
                                </td>
                                <td width="30%">C&oacute;digo:
                                </td>
                                <td width="30%">
                                    <h:inputText id="codigo1" style="width: 80px;"
                                                 value="#{managerPlanCurricularCurso.codigo1}" maxlength="10"/>
                                </td>
                                <td width="30%">Curso:
                                </td>
                                <td width="30%">
                                    <h:selectOneMenu id="nombre1" value="#{managerPlanCurricularCurso.nombre1}">
                                        <f:selectItems value="#{managerPlanCurricularCurso.comboCursos}"/>
                                    </h:selectOneMenu>
                                </td>
                                <td width="30%">Cr&eacute;ditos:
                                </td>
                                <td width="30%">
                                    <h:inputText id="creditos1" style="width: 30px;"
                                                 value="#{managerPlanCurricularCurso.creditos1}" maxlength="2"/>
                                </td>
                                <td width="30%">Tipo:
                                </td>
                                <td width="30%">
                                    <h:selectOneMenu id="tipo1" value="#{managerPlanCurricularCurso.tipo1}">
                                        <f:selectItems value="#{managerPlanCurricularCurso.comboTipos}"/>
                                    </h:selectOneMenu>
                                </td>
                                <td width="30%"></td>
                                <td width="10%">
                                    <a4j:commandButton image="/Imagenes/actions/edit_add.png" title="Agregar"
                                                       actionListener="#{managerPlanCurricularCurso.agregarFila}"
                                                       reRender="scroll1,tablaGrupos"
                                                       oncomplete="#{managerPlanCurricularCurso.oncomplete}"/>
                                </td>
                                <td>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
            <table>
                <tr>
                    <td colspan="8" width="100%">
                        <rich:datascroller id="scroll1" align="right" for="tablaGrupos" maxPages="10"
                                           style="width: 100%"/>
                        <rich:dataTable id="tablaGrupos" rows="10" cellpadding="0" cellspacing="0" width="100%"
                                        border="0"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        value="#{managerPlanCurricularCurso.listaCursos}" var="cursos">

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{cursos.rowId}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Ciclo"/>
                                </f:facet>
                                <h:outputText value="#{cursos.ciclo}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{cursos.rowDescripcion}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Nombre"/>
                                </f:facet>
                                <h:outputText value="#{cursos.rowCodigo}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Tipo"/>
                                </f:facet>
                                <h:outputText value="#{cursos.rowCreditos}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Cr&eacute;ditos" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{cursos.rowTipos}"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Quitar"/>
                                </f:facet>
                                <a4j:commandButton image="/Imagenes/actions/edit_remove.png" title="Eliminar"
                                                   actionListener="#{managerPlanCurricularCurso.quitarFila}"
                                                   reRender="scroll1,tablaGrupos"/>
                                <f:param id="p_id1" value="#{cursos.rowId}"/>
                            </rich:column>
                        </rich:dataTable>
                    </td>
                    <td>
                    </td>
                    <td>
                    </td>
                </tr>
            </table>
        </a4j:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpEquiv" autosized="true" minWidth="600" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Equivalencias"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpEquiv')" title="Cerrar"/>
        </f:facet>
        <a4j:form>
            <rich:dataTable id="tableEquiv" rows="10" cellpadding="0" cellspacing="0" border="0" width="100%"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerPlanCurricularCurso.equivalences}" var="equiv">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Curso"/>
                    </f:facet>
                    <h:outputText value="#{equiv.rowCodigo}" styleClass="uch-word-wrap"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{equiv.rowDescripcion}" styleClass="uch-word-wrap"/>
                </rich:column>
            </rich:dataTable>
        </a4j:form>
    </rich:modalPanel>
</f:view>
</body>
</html>

