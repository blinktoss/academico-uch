<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Pre-Requisitos</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <div class="row">
                <div class="col title-label">MANTENIMIENTO PRE REQUISITOS</div>
                <div class="col" style="text-align: right">
                    <a4j:status>
                        <f:facet name="start">
                            <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                        </f:facet>
                    </a4j:status>
                    <rich:spacer width="8px"/>
                    <a4j:commandButton type="button" id="buscar" actionListener="#{managerPreRequisito.seleccionar}"
                                       image="/Imagenes/actions/viewmag.png" title="Buscar"
                                       oncomplete="#{managerPreRequisito.oncomplete}"
                                       reRender="paginacion,tablaMaster"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin-bottom: 10px"/>
            <div class="row">
                <div class="col uch-label">Facultad:</div>
                <div class="col uch-input">
                    <h:selectOneMenu id="FacuBuscar" value="#{managerPreRequisito.paramIdFacu}" style="width: 180px;">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                        <f:selectItems value="#{managerPreRequisito.comboFacultad}"/>
                        <a4j:support event="onchange" actionListener="#{managerPreRequisito.setearFacultadBuscar}"
                                     reRender="EspeBuscar,PlanBuscar"/>
                    </h:selectOneMenu>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Especialidad:</div>
                <div class="col uch-input">
                    <h:selectOneMenu id="EspeBuscar" value="#{managerPreRequisito.paramIdEspe}" style="width: 390px;">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                        <f:selectItems value="#{managerPreRequisito.comboEspecialidad}"/>
                        <a4j:support event="onchange"
                                     actionListener="#{managerPreRequisito.setearEspecialidadBuscar}"
                                     reRender="PlanBuscar"/>
                    </h:selectOneMenu>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Plan Curricular:</div>
                <div class="col uch-input">
                    <h:selectOneMenu id="PlanBuscar" value="#{managerPreRequisito.paramIdPlan}" style="width: 180px;">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                        <f:selectItems value="#{managerPreRequisito.comboPlan}"/>
                    </h:selectOneMenu>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin: 10px 0 10px"/>
        </h:form>
        <h:form>
            <rich:datascroller id="paginacion" align="right" for="tablaMaster" maxPages="8"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerPreRequisito.listaPreRequisitos}" var="pre">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowId}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Ciclo"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowCicloValue}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo de Curso" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowCodigo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowNombre}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Tipo de Curso"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowTipo}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="# de Cr&eacute;ditos" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowCreditos}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Facultad"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowFacu}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Especialidad"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowEspe}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Plan Curricular"/>
                    </f:facet>
                    <h:outputText value="#{pre.rowPlan}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Ver"/>
                    </f:facet>
                    <h:graphicImage value="#{pre.imagen}" style="cursor: pointer">
                        <a4j:support event="onclick" actionListener="#{pre.openSubTable}" reRender="tablaMaster"/>
                    </h:graphicImage>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value=""/>
                    </f:facet>
                    <!-- aqui se ingresa el edit -->
                    <h:graphicImage value="/Imagenes/actions/pencil.png" style="border-width: 0; cursor: pointer;"
                                    title="Asignar Requisitos">
                        <a4j:support event="onclick" actionListener="#{managerPreRequisito.editarFila}"
                                     oncomplete="Richfaces.showModalPanel('mp',{top:130})"
                                     reRender="iValor,iId,iCodigo,iNombre,iCiclo,iIdCiclo,iFacultad,iIdEspecialidad,iEspecialidad,iIdplan,iPlan,bCiclo,bCurso,tablaGrupos"/>
                    </h:graphicImage>

                    <f:param id="p_valor" value="#{pre.rowCicloValor}"/>
                    <f:param id="p_id" value="#{pre.rowId}"/>
                    <f:param id="p_codigo" value="#{pre.rowCodigo}"/>
                    <f:param id="p_nombre" value="#{pre.rowNombre}"/>
                    <f:param id="p_ciclo" value="#{pre.rowCicloValue}"/>
                    <f:param id="p_id_ciclo" value="#{pre.rowCicloId}"/>
                    <f:param id="p_especialidad" value="#{pre.rowEspe}"/>
                    <f:param id="p_id_especialidad" value="#{pre.paramIdEspe}"/>
                    <f:param id="p_facultad" value="#{pre.rowFacu}"/>
                    <f:param id="p_plan" value="#{pre.rowPlan}"/>
                    <f:param id="p_id_plan" value="#{pre.paramIdPlan}"/>
                    <!-- fin de ingresa el edit -->

                    <rich:subTable value="#{pre.detallePreReq}" var="det" rendered="#{pre.ver}" id="subtable"
                                   onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                                   onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                        <rich:column colspan="2">
                            <f:facet name="header">
                                <h:outputText value="Id Detalle"/>
                            </f:facet>
                            <h:outputText value="#{det.detId}"/>
                        </rich:column>
                        <rich:column colspan="2">
                            <f:facet name="header">
                                <h:outputText value="Nombre Pre"/>
                            </f:facet>
                            <h:outputText value="#{det.detReq}"/>
                        </rich:column>
                        <rich:column style="text-align: center;" colspan="2">
                            <f:facet name="header">
                                <h:outputText value="Ciclo Pre"/>
                            </f:facet>
                            <h:outputText value="#{det.detCiclo}"/>
                        </rich:column>
                    </rich:subTable>
                </rich:column>
            </rich:dataTable>
        </h:form>
        <!-- fin nuevo panel grid -->
    </rich:panel>

    <rich:modalPanel id="mp" autosized="true" minWidth="550" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro Pre - Requisitos"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <rich:separator height="1" style="margin: 10px 0"/>
            <div class="row">
                <div class="col uch-label">
                    <h:inputHidden id="iValor" value="#{managerPreRequisito.rowCicloValor}"/>
                    <h:inputHidden id="iId" value="#{managerPreRequisito.rowId}"/>
                    <h:inputHidden id="iIdEspecialidad" value="#{managerPreRequisito.paramIdEspe}"/>
                    <h:inputHidden id="iIdplan" value="#{managerPreRequisito.paramIdPlan}"/>
                    <h:inputHidden id="iIdCiclo" value="#{managerPreRequisito.rowCicloId}"/>
                    C&oacute;digo:
                </div>
                <div class="col uch-description">
                    <h:outputText id="iCodigo" style="width: 180px;" value="#{managerPreRequisito.rowCodigo}"/>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Curso:</div>
                <div class="col uch-description">
                    <h:outputText id="iNombre" style="width: 180px;" value="#{managerPreRequisito.rowNombre}"/>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Ciclo:</div>
                <div class="col uch-description">
                    <h:outputText id="iCiclo" style="width: 180px;" value="#{managerPreRequisito.rowCicloValue}"/>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Facultad:</div>
                <div class="col uch-description">
                    <h:outputText id="iFacultad" style="width: 180px;" value="#{managerPreRequisito.rowFacu}"/>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Especialidad:</div>
                <div class="col uch-description">
                    <h:outputText id="iEspecialidad" style="width: 180px;" value="#{managerPreRequisito.rowEspe}"/>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Plan:</div>
                <div class="col uch-description">
                    <h:outputText id="iPlan" style="width: 180px;" value="#{managerPreRequisito.rowPlan}"/>
                </div>
            </div>
            <rich:separator height="1" style="margin: 10px 0"/>
            <table style="width: 100%">
                <tr>
                    <td style="width: 15%">
                        Ciclo:
                        <h:selectOneMenu id="bCiclo" value="#{managerPreRequisito.mpIdCiclo}">
                            <f:selectItems value="#{managerPreRequisito.comboCiclo}"/>
                            <a4j:support event="onchange" actionListener="#{managerPreRequisito.setearCiclo}"
                                         reRender="bCurso"/>
                        </h:selectOneMenu>
                    </td>
                    <td style="width: 60%">
                        Curso:
                        <h:selectOneMenu id="bCurso" value="#{managerPreRequisito.mpIdCurso}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerPreRequisito.comboCurso}"/>
                        </h:selectOneMenu>
                    </td>
                    <td>
                        <a4j:commandButton image="/Imagenes/actions/edit_add.png" title="Agregar"
                                           actionListener="#{managerPreRequisito.agregarFila}"
                                           reRender="tablaGrupos,tablaMaster,subtable"
                                           oncomplete="#{managerPreRequisito.oncomplete}"/>
                    </td>
                    <td align="right" width="20%"></td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:dataTable id="tablaGrupos" rows="10" cellpadding="0" cellspacing="0"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        width="100%" border="0"
                                        value="#{managerPreRequisito.listaCursos}" var="cursos">
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{cursos.xtId}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id Curso / Plan"/>
                                </f:facet>
                                <h:outputText value="#{cursos.xtIdCurso}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Nombre del Curso"/>
                                </f:facet>
                                <h:outputText value="#{cursos.xtCurso}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id Curso / Requisito"/>
                                </f:facet>
                                <h:outputText value="#{cursos.xtIdReq}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Nombre del Curso"/>
                                </f:facet>
                                <h:outputText value="#{cursos.xtReq}"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Ciclo del Curso"/>
                                </f:facet>
                                <h:outputText value="#{cursos.xtCiclo}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Quitar"/>
                                </f:facet>
                                <f:param id="p_id1" value="#{cursos.xtId}"/>
                                <a4j:commandButton image="/Imagenes/actions/edit_remove.png"
                                                   title="Eliminar"
                                                   actionListener="#{managerPreRequisito.quitarFila}"
                                                   reRender="tablaGrupos,tablaMaster,subtable"/>
                            </rich:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>

