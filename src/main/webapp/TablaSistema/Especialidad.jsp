<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Especialidades</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1" style="width:100%">
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE ESPECIALIDADES</strong>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filenew.png"
                                           actionListener="#{managerEspecialidad.newSpeciality}"
                                           reRender="txtCodigo,txtNombre,txtCreacion,txtResolucion,txtTiposPeriodo,txtPeriodos,txtFacultad"
                                           oncomplete="Richfaces.showModalPanel('mp',{width:450, top:130})"/>
                    </td>
                    <td width="30%"></td>
                    <td align=right width="50%">
                        <h:commandButton type="button" id="buscar" action="#{managerEspecialidad.seleccionar}"
                                         image="/Imagenes/actions/viewmag.png" title="Buscar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="20%" colspan="2"></td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td>Facultad:</td>
                    <td>
                        <h:selectOneMenu id="oFacultad" value="#{managerEspecialidad.paramFacultadId}"
                                         style="width : 180px;">
                            <f:selectItem itemValue="0" itemLabel="TODOS"/>
                            <f:selectItems value="#{managerEspecialidad.comboFacultades}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height : 24px;">
                    <td width="10%">C&oacute;digo:</td>
                    <td width="30%"><h:inputText value="#{managerEspecialidad.paramCodigo}" style="width : 180px;"
                                                 maxlength="10"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Nombre:</td>
                    <td width="30%"><h:inputText value="#{managerEspecialidad.paramNombre}"
                                                 style="width : 180px;"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td></td>
                </tr>
            </table>
        </h:form>
        <h:form style="width : 100%;">
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width : 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            width="100%" border="0" value="#{managerEspecialidad.listaEspecialidad}" var="especialidad">

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{especialidad.paramCodigo}" rendered="#{especialidad.view}"/>
                    <h:inputText value="#{especialidad.paramCodigo}" rendered="#{especialidad.editable}"
                                 maxlength="10"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>

                    <h:outputText value="#{especialidad.paramNombre}" rendered="#{especialidad.view}"/>
                    <h:inputText value="#{especialidad.paramNombre}" rendered="#{especialidad.editable}"
                                 style="width: 250px;"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Resoluci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{especialidad.rowResolrectoral}" rendered="#{especialidad.view}"/>
                    <h:inputText value="#{especialidad.rowResolrectoral}" rendered="#{especialidad.editable}"
                                 style="width: 140px;"/>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Fecha de Creaci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{especialidad.rowFechacreacion}" rendered="#{especialidad.view}">
                        <f:convertDateTime pattern="dd/MM/yyyy"/>
                    </h:outputText>
                    <rich:calendar value="#{especialidad.rowFechacreacion}" datePattern="dd/MM/yyyy"
                                   rendered="#{especialidad.visible}"/>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Nro Periodos"/>
                    </f:facet>

                    <h:outputText value="#{especialidad.rowNroPeriodos}" rendered="#{especialidad.view}"/>
                    <h:inputText value="#{especialidad.rowNroPeriodos}" rendered="#{especialidad.editable}"
                                 style=" width : 31px;"/>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Tipo de Periodo"/>
                    </f:facet>

                    <h:outputText value="#{especialidad.rowTipoPeriodoDesc}" rendered="#{especialidad.view}"/>
                    <h:selectOneMenu value="#{especialidad.rowTipoPeriodoId}" rendered="#{especialidad.editable}">
                        <f:selectItems value="#{especialidad.comboTipos}"/>
                    </h:selectOneMenu>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Facultad"/>
                    </f:facet>

                    <h:outputText value="#{especialidad.rowFacultadValue}" rendered="#{especialidad.view}"/>
                    <h:selectOneMenu value="#{especialidad.rowFacultadId}" rendered="#{especialidad.editable}">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                        <f:selectItems value="#{especialidad.comboFacultades}"/>
                    </h:selectOneMenu>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton actionListener="#{especialidad.editarFila}" title="Editar"
                                       image="/Imagenes/actions/klipper_dock.png" reRender="tablaMaster, barra"
                                       rendered="#{especialidad.view}"/>

                    <f:param id="id" value="#{especialidad.rowId}"/>
                    <f:param id="codigo" value="#{especialidad.paramCodigo}"/>
                    <f:param id="nombre" value="#{especialidad.paramNombre}"/>
                    <f:param id="facultad" value="#{especialidad.rowFacultadId}"/>
                    <f:param id="resolucion" value="#{especialidad.rowResolrectoral}"/>
                    <f:param id="creacion" value="#{especialidad.rowFechacreacion}"/>
                    <f:param id="numero_periodos" value="#{especialidad.rowNroPeriodos}"/>
                    <f:param id="tipo_periodos" value="#{especialidad.rowTipoPeriodoId}"/>

                    <a4j:commandButton actionListener="#{especialidad.editar}"
                                       image="/Imagenes/actions/filesave.png" title="Guardar"
                                       rendered="#{especialidad.editable2}" reRender="tablaMaster, barra"
                                       oncomplete="#{especialidad.oncomplete}"/>
                    <a4j:commandButton actionListener="#{especialidad.cancelar}"
                                       image="/Imagenes/actions/fileclose.png" title="Cancelar"
                                       rendered="#{especialidad.editable}" reRender="tablaMaster, barra"/>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <f:param id="delete" value="#{especialidad.rowId}"/>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerEspecialidad.eliminarFila}"
                                     action="#{managerEspecialidad.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>

            </rich:dataTable>

        </h:form>

        <rich:modalPanel id="mp" minWidth="450" autosized="true" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Especialidades"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table style="width: 100%;">
                    <tr>
                        <td align="right" colspan="4">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerEspecialidad.grabar}"
                                               action="#{managerEspecialidad.seleccionar}"
                                               reRender="tablaMaster, barra"
                                               oncomplete="#{managerEspecialidad.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td width="30%">*C&oacute;digo:</td>
                        <td width="70%">
                            <h:inputText id="txtCodigo" style="width: 180px"
                                         value="#{managerEspecialidad.mpCodigo}" maxlength="10"/>
                        </td>
                        <td align="right" width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Nombre:</td>
                        <td width="70%">
                            <h:inputText id="txtNombre" style="width: 180px"
                                         value="#{managerEspecialidad.mpNombre}"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Fecha de Creaci&oacute;n:</td>
                        <td width="70%">
                            <a4j:outputPanel id="txtCalendar" layout="block">
                                <rich:calendar id="txtCreacion" datePattern="dd/MM/yyyy"
                                               value="#{managerEspecialidad.rowFechacreacion}"/>
                            </a4j:outputPanel>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Resoluci&oacute;n Rectoral:</td>
                        <td width="70%">
                            <h:inputText id="txtResolucion" style="width : 180px"
                                         value="#{managerEspecialidad.rowResolrectoral}"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Tipo de Periodo:</td>
                        <td width="70%">
                            <h:selectOneMenu id="txtTiposPeriodo" value="#{managerEspecialidad.rowTipoPeriodoId}">
                                <f:selectItems value="#{managerEspecialidad.comboTipos}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*N&uacute;mero de Periodos:</td>
                        <td width="70%">
                            <h:inputText id="txtPeriodos" style="width : 180px"
                                         converterMessage="Please enter digits only."
                                         value="#{managerEspecialidad.rowNroPeriodos}"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Facultad:</td>
                        <td width="70%">
                            <h:selectOneMenu id="txtFacultad" value="#{managerEspecialidad.rowFacultadId}">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                                <f:selectItems value="#{managerEspecialidad.comboFacultades}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td colspan="4"><rich:separator height="1"/></td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>
