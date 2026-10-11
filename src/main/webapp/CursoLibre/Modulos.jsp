<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Configuraci&oacute;n de M&oacute;dulos y Cursos</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%">
                <tr>
                    <td width="100%" colspan="10">
                        <table style="width: 100%">
                            <tr>
                                <td>
                                    <h:outputText value="MANTENIMIENTO DE M&Oacute;DULOS Y CURSOS"
                                                  style="font-weight: bold" escape="false"/>
                                </td>
                                <td align="right">
                                    <a4j:status>
                                        <f:facet name="start">
                                            <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                                        </f:facet>
                                    </a4j:status>
                                    <h:graphicImage value="/Imagenes/actions/filenew.png" title="Nuevo"
                                                    style="cursor: pointer">
                                        <a4j:support event="onclick"
                                                     actionListener="#{managerCLModulo.nuevo}"
                                                     oncomplete="#{managerCLModulo.oncomplete}"
                                                     reRender="iArea,iCodigo,iDescripcion,pnlCursos"/>
                                    </h:graphicImage>
                                    <h:commandButton image="/Imagenes/actions/viewmag.png"
                                                     title="Buscar"
                                                     action="#{managerCLModulo.seleccionar}"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="10">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td width="10%"><h:outputText value="Descripci&oacute;n :" escape="false"/></td>
                    <td colspan="9"><h:inputText id="b_descripcion"
                                                 value="#{managerCLModulo.paramDescripcion}"
                                                 style="width: 300px"/></td>
                </tr>
                <tr>
                    <td width="10%"><h:outputText value="&Aacute;rea :" escape="false"/></td>
                    <td>
                        <h:selectOneMenu value="#{managerCLModulo.paramAreaId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLModulo.comboAreas}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form id="form3">
            <rich:datascroller id="paginacion" align="right" for="tablaMaster" maxPages="10" style="width: 100%"/>
            <rich:dataTable id="tablaMaster" cellpadding="0" cellspacing="0" width="100%" rows="10"
                            onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerCLModulo.modulos}" var="modulo">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{modulo.rowModId}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{modulo.rowModCodigo}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{modulo.rowModDescripcion}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="&Aacute;rea" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{modulo.rowAreDescripcion}"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Cursos"/>
                    </f:facet>
                    <a4j:commandButton image="#{modulo.imagenDetalle}"
                                       title="#{modulo.tituloDetalle}"
                                       actionListener="#{modulo.mostrarCursos}"
                                       reRender="tablaMaster"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <f:param id="id_update" value="#{modulo.rowModId}"/>
                    <a4j:commandButton image="/Imagenes/actions/icon_edit.png"
                                       title="Modificar"
                                       actionListener="#{managerCLModulo.actualizarModulo}"
                                       oncomplete="#{managerCLModulo.oncomplete}"
                                       reRender="iArea,iCodigo,iDescripcion,pnlCursos"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <f:param id="id_delete" value="#{modulo.rowModId}"/>
                    <h:commandButton image="/Imagenes/actions/no.png"
                                     title="Eliminar"
                                     actionListener="#{managerCLModulo.eliminarModulo}"
                                     action="#{managerCLModulo.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>
                <rich:subTable id="detalleMaster" value="#{modulo.cursos}" var="curso"
                               onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                               onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                               rendered="#{modulo.verDetalle}">

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Id"/>
                        </f:facet>
                        <h:outputText value="#{curso.rsbCurId}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="C&oacute;digo" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{curso.rsbCurCodigo}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Nombre"/>
                        </f:facet>
                        <h:outputText value="#{curso.rsbCurNombre}"/>
                    </rich:column>
                </rich:subTable>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mp1" autosized="true" zindex="3000" width="600">
        <f:facet name="header">
            <h:outputText value="M&oacute;dulo y Curso" escape="false"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp1')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form2">
            <table style="width: 100%">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           title="Guardar"
                                           actionListener="#{managerCLModulo.guardarModulo}"
                                           action="#{managerCLModulo.seleccionar}"
                                           oncomplete="#{managerCLModulo.oncomplete}"
                                           reRender="tablaMaster,paginacion"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <h:outputText value="M&Oacute;DULO" style="font-weight: bold" escape="false"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="height: 5px"></td>
                </tr>
                <tr>
                    <td style="width: 70px"><h:outputText value="&Aacute;rea" escape="false"/>
                    </td>
                    <td>
                        <h:selectOneMenu id="iArea" value="#{managerCLModulo.mpAreaId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLModulo.comboAreas}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td><h:outputText value="C&oacute;digo" escape="false"/>
                    </td>
                    <td>
                        <h:inputText id="iCodigo" value="#{managerCLModulo.mpModCodigo}"/>
                    </td>
                </tr>
                <tr>
                    <td><h:outputText value="Descripci&oacute;n" escape="false"/>
                    </td>
                    <td>
                        <h:inputText id="iDescripcion" value="#{managerCLModulo.mpModDescripcion}"
                                     style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <h:panelGroup id="pnlCursos">
                            <table style="width: 100%" cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                    <td colspan="2">
                                        <h:outputText value="CURSO" style="font-weight: bold"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" style="height: 5px"></td>
                                </tr>
                                <tr>
                                    <td><h:outputText value="C&oacute;digo" escape="false"/>
                                    </td>
                                    <td>
                                        <h:inputText id="iCurCodigo" value="#{managerCLModulo.mpCurCodigo}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><h:outputText value="Nombre"/>
                                    </td>
                                    <td>
                                        <h:inputText id="iCurNombre" value="#{managerCLModulo.mpCurNombre}"
                                                     style="width: 300px"/>
                                        <rich:spacer width="20px"/>
                                        <a4j:commandButton actionListener="#{managerCLModulo.agregarCurso}"
                                                           reRender="iCurCodigo,iCurNombre,barra,tablaDetalle"
                                                           oncomplete="#{managerCLModulo.message}"
                                                           image="/Imagenes/actions/edit_add.png" title="Agregar"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" style="height: 10px"></td>
                                </tr>
                                <tr>
                                    <td colspan="2" valign="top">
                                        <rich:datascroller id="barra" align="right" for="tablaDetalle"/>
                                        <rich:dataTable id="tablaDetalle" rows="15" width="100%"
                                                        value="#{managerCLModulo.mpCursos}"
                                                        onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                                        var="detalle">
                                            <rich:column>
                                                <f:facet name="header">
                                                    <h:outputText value="Id"/>
                                                </f:facet>
                                                <h:outputText value="#{detalle.mpCurId}"/>
                                            </rich:column>
                                            <rich:column>
                                                <f:facet name="header">
                                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                                </f:facet>
                                                <h:outputText value="#{detalle.mpCurCodigo}"
                                                              rendered="#{detalle.cursoVer}"/>
                                                <h:inputText value="#{detalle.edCurCodigo}"
                                                             rendered="#{detalle.cursoEditar}" style="width: 150px"/>
                                            </rich:column>
                                            <rich:column>
                                                <f:facet name="header">
                                                    <h:outputText value="Nombre"/>
                                                </f:facet>
                                                <h:outputText value="#{detalle.mpCurNombre}"
                                                              rendered="#{detalle.cursoVer}"/>
                                                <h:inputText value="#{detalle.edCurNombre}"
                                                             rendered="#{detalle.cursoEditar}" style="width: 180px"/>
                                            </rich:column>
                                            <rich:column style="text-align: center">
                                                <f:facet name="header">
                                                    <h:outputText value="Editar"/>
                                                </f:facet>
                                                <f:param id="id_cur_update" value="#{detalle.mpCurId}"/>
                                                <a4j:commandButton actionListener="#{detalle.editarCurso}"
                                                                   reRender="tablaDetalle"
                                                                   image="/Imagenes/actions/klipper_dock.png"
                                                                   title="Editar"
                                                                   rendered="#{detalle.cursoVer}"/>
                                                <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                                                   title="Grabar"
                                                                   actionListener="#{detalle.guardarCurso}"
                                                                   reRender="tablaDetalle"
                                                                   oncomplete="#{detalle.message}"
                                                                   rendered="#{detalle.cursoEditar}"/>
                                                <a4j:commandButton image="/Imagenes/actions/fileclose.png"
                                                                   title="Cancelar"
                                                                   actionListener="#{detalle.cancelarCurso}"
                                                                   reRender="tablaDetalle"
                                                                   rendered="#{detalle.cursoEditar}"/>
                                            </rich:column>
                                            <rich:column style="text-align: center">
                                                <f:facet name="header">
                                                    <h:outputText value="Eliminar"/>
                                                </f:facet>
                                                <f:param id="id_cur_delete" value="#{detalle.mpCurId}"/>
                                                <a4j:commandButton actionListener="#{managerCLModulo.quitarCurso}"
                                                                   reRender="barra,tablaDetalle"
                                                                   oncomplete="#{managerCLModulo.message}"
                                                                   image="/Imagenes/actions/delete.gif"
                                                                   title="Eliminar"/>
                                            </rich:column>
                                        </rich:dataTable>
                                    </td>
                                </tr>
                            </table>
                        </h:panelGroup>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
