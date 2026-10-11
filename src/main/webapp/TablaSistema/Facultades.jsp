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
    <title>Mantenimiento de Facultades</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel style="width: 100%;">
        <h:form id="form1" style="width:100%">
            <table width="100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE FACULTADES</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="../Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td width="30%">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                            <a4j:support event="onclick" reRender="formNewFaculty"
                                         oncomplete="Richfaces.showModalPanel('mp',{top:130})"
                                         actionListener="#{managerFacultad.nuevo}"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%" align="right"></td>
                    <td align=right width="50%">
                        <a4j:commandButton type="button" id="buscar" actionListener="#{managerFacultad.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png" title="Buscar"
                                           reRender="barra,tablaMaster"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="20%" colspan="2">

                    </td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                </tr>
                <tr style=" height : 24px;">
                    <td width="10%">C&oacute;digo:</td>
                    <td width="30%"><h:inputText id="txtCodigo" value="#{managerFacultad.paramCodigo}"
                                                 style="width : 180px;" maxlength="10"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Nombre:</td>
                    <td width="30%"><h:inputText value="#{managerFacultad.paramNombre}" style="width : 180px;"/></td>
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
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form style="width: 100%;">
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerFacultad.listaFacultad}" var="facultad">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{facultad.facultadId}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{facultad.rowCodigo}" rendered="#{facultad.view}"/>
                    <h:inputText value="#{facultad.rowCodigo}" rendered="#{facultad.editable}" maxlength="10"/>
                </h:column>

                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>

                    <h:outputText value="#{facultad.rowNombre}" rendered="#{facultad.view}"/>
                    <h:inputText value="#{facultad.rowNombre}" rendered="#{facultad.editable}" style="width: 270px"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Resoluci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{facultad.rowResolucion}" rendered="#{facultad.view}"/>
                    <h:inputText value="#{facultad.rowResolucion}" rendered="#{facultad.editable}"
                                 style="width: 150px"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Fecha Creaci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{facultad.rowCreacion}" rendered="#{facultad.view}">
                        <f:convertDateTime pattern="dd/MM/yyyy"/>
                    </h:outputText>
                    <a4j:outputPanel id="calendar" layout="block" rendered="#{facultad.editable}">
                        <rich:calendar value="#{facultad.rowCreacion}" datePattern="dd/MM/yyyy" style="width: 180px;"/>
                    </a4j:outputPanel>
                </h:column>

                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton actionListener="#{facultad.editarFila}" title="Editar"
                                       image="/Imagenes/actions/klipper_dock.png"
                                       rendered="#{facultad.view}" reRender="tablaMaster,barra"/>
                    <a4j:commandButton actionListener="#{facultad.editar}"
                                       image="/Imagenes/actions/filesave.png" title="Guardar"
                                       rendered="#{facultad.editable}" reRender="tablaMaster,barra"/>
                    <a4j:commandButton actionListener="#{facultad.cancelar}"
                                       image="/Imagenes/actions/fileclose.png" title="Cancelar"
                                       rendered="#{facultad.editable}" reRender="tablaMaster,barra"/>
                </h:column>

                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerFacultad.eliminarFila}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                    <f:param id="delete" value="#{facultad.facultadId}"/>
                </h:column>

            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="mp" autosized="true" width="500" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Facultad"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
            </f:facet>
            <h:form id="formNewFaculty">
                <table width="100%">
                    <tr>
                        <td align="right" colspan="4">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerFacultad.grabar}"
                                               reRender="tablaMaster, barra"
                                               oncomplete="#{managerFacultad.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td width="30%">*C&oacute;digo:</td>
                        <td width="70%">
                            <h:inputText id="codigo" style="width: 180px;"
                                         value="#{managerFacultad.rowCodigo}" maxlength="10"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Nombre:</td>
                        <td width="70%">
                            <h:inputText id="txtNombre" style="width: 180px;"
                                         value="#{managerFacultad.rowNombre}"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Fecha de Creaci&oacute;n:</td>
                        <td width="70%">
                            <a4j:outputPanel id="calendar" layout="block">
                                <rich:calendar id="txtCreacion" value="#{managerFacultad.rowCreacion}"
                                               datePattern="dd/MM/yyyy"/>
                            </a4j:outputPanel>

                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Resoluci&oacute;n Rectoral:</td>
                        <td width="70%">
                            <h:inputText id="txtResolucion" style="width: 180px;"
                                         value="#{managerFacultad.rowResolucion}"/>
                        </td>
                        <td width="20%">
                        </td>
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
