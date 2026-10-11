<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Plan Curricular</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2"><strong>PLAN CURRICULAR</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:commandButton title="Nuevo Plan" image="/Imagenes/actions/filenew.png"
                                           oncomplete="Richfaces.showModalPanel('mp',{width:450, top:130})"
                                           actionListener="#{managerPlanCurricular.nuevo}"
                                           reRender="iId,iCodigo,iDescripcion,iResolucion,iCreacion,iEspecialidad,iFacultad"/>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right"><h:graphicImage value="/Imagenes/actions/fileprint.png"
                                                                  title="Imprimir Listado"/>
                    </td>
                    <td align=right width="50%">
                        <h:commandButton type="button" id="buscar" title="Buscar"
                                         action="#{managerPlanCurricular.seleccionar}"
                                         image="/Imagenes/actions/viewmag.png"/>
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
                </tr>
                <tr>
                    <td width="10%">Facultad:</td>
                    <td width="70%">
                        <h:selectOneMenu id="Facultad" value="#{managerPlanCurricular.paramFacultadId}"
                                         style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerPlanCurricular.paramComboFacultades}"/>
                            <a4j:support event="onchange" reRender="Especialidad"
                                         actionListener="#{managerPlanCurricular.onChangeParamFacultad}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>

                <tr>
                    <td width="10%">Especialidad:</td>
                    <td width="70%">
                        <h:selectOneMenu id="Especialidad" value="#{managerPlanCurricular.paramEspecialidadId}"
                                         style="width: 300px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerPlanCurricular.paramComboEspecialidades}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="10%">C&oacute;digo:</td>
                    <td width="30%">
                        <h:inputText id="txtCodigo" value="#{managerPlanCurricular.paramCodigo}" style="width : 180px;"
                                     maxlength="10"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Descripci&oacute;n:</td>
                    <td width="30%">
                        <h:inputText value="#{managerPlanCurricular.paramDescripcion}" style="width : 180px;"/></td>
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
                        <rich:separator height="3" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                </tr>
            </table>
        </h:form>
        <h:form style="width: 100%;">
            <rich:datascroller align="right" for="tablaMaster" maxPages="8" style=" width : 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            width="100%" border="0" value="#{managerPlanCurricular.listaPlanCurricular}"
                            var="planCurricular">

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{planCurricular.pcId}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{planCurricular.pcCodigo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{planCurricular.pcDescripcion}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Resoluci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{planCurricular.pcResolucion}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Fecha Creaci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{planCurricular.pcCreacion}">
                        <f:convertDateTime pattern="dd/MM/yyyy"/>
                    </h:outputText>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Facultad"/>
                    </f:facet>

                    <h:outputText value="#{planCurricular.pcFacultadDescripcion}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Especialidad"/>
                    </f:facet>

                    <h:outputText value="#{planCurricular.pcEspecialidadDescripcion}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/editpaste.png"
                                       actionListener="#{managerPlanCurricular.editarFila}"
                                       oncomplete="Richfaces.showModalPanel('mp')"
                                       reRender="iId,iCodigo,iCreacion,iDescripcion,iResolucion,iFacultad,iEspecialidad"/>

                    <f:param id="p_id" value="#{planCurricular.pcId}"/>
                    <f:param id="p_codigo" value="#{planCurricular.pcCodigo}"/>
                    <f:param id="p_descripcion" value="#{planCurricular.pcDescripcion}"/>
                    <f:param id="p_facultad" value="#{planCurricular.pcFacultadId}"/>
                    <f:param id="p_especialidad" value="#{planCurricular.pcEspecialidadId}"/>
                    <f:param id="p_resolucion" value="#{planCurricular.pcResolucion}"/>
                    <f:param id="p_creacion" value="#{planCurricular.pcCreacion}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Delete"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerPlanCurricular.eliminarFila}"
                                     action="#{managerPlanCurricular.seleccionar}"
                                     onclick="return confirm('¿Esta realmente seguro de Eliminar?');"/>
                    <f:param id="delete" value="#{planCurricular.pcId}"/>
                </rich:column>

            </rich:dataTable>

        </h:form>

        <rich:modalPanel id="mp" autosized="true" width="600" zindex="3000">
            <f:facet name="header">
                <h:outputText value="Registro de Plan Curricular"/>
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
                                               actionListener="#{managerPlanCurricular.saveOrUpdate}"
                                               action="#{managerPlanCurricular.seleccionar}"
                                               reRender="tablaMaster" oncomplete="#{managerPlanCurricular.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td width="30%">Facultad:</td>
                        <td width="70%">
                            <h:inputHidden id="iId" value="#{managerPlanCurricular.pcId}"/>

                            <h:selectOneMenu id="iFacultad" value="#{managerPlanCurricular.pcFacultadId}"
                                             style="width: 180px;">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerPlanCurricular.paramComboFacultades}"/>
                                <a4j:support event="onchange"
                                             actionListener="#{managerPlanCurricular.onChangeMpFacultad}"
                                             reRender="iEspecialidad"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">Especialidad:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iEspecialidad" value="#{managerPlanCurricular.pcEspecialidadId}"
                                             style="width: 300px;">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerPlanCurricular.comboEspecialidades}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">C&oacute;digo de Plan:</td>
                        <td width="70%">
                            <h:inputText label="Codigo" id="iCodigo" value="#{managerPlanCurricular.pcCodigo}"
                                         style="width : 180px;"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Descripci&oacute;n:</td>
                        <td width="70%">
                            <h:inputTextarea label="Resolucion" id="iDescripcion"
                                             value="#{managerPlanCurricular.pcDescripcion}" style="width : 180px;"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Fecha de Creaci&oacute;n:</td>
                        <td width="70%">
                            <a4j:outputPanel id="calendar" layout="block">
                                <rich:calendar id="iCreacion" value="#{managerPlanCurricular.pcCreacion}"
                                               datePattern="dd/MM/yyyy"/>
                            </a4j:outputPanel>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Resoluci&oacute;n Rectoral:</td>
                        <td width="70%">
                            <h:inputText label="Resolucion" id="iResolucion"
                                         value="#{managerPlanCurricular.pcResolucion}" style="width : 180px;"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>

