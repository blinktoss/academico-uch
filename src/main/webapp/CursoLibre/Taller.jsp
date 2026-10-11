<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Configuracion de Talleres</title>
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
                                <td align="left">
                                    <h:outputText value="MANTENIMIENTO DE TALLERES" style="font-weight: bold;"/>
                                </td>
                                <td align="right">
                                    <a4j:status>
                                        <f:facet name="start">
                                            <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                                        </f:facet>
                                    </a4j:status>
                                    <h:commandButton image="/Imagenes/actions/viewmag.png"
                                                     title="Buscar"
                                                     action="#{managerCLTaller.seleccionar}"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="10"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="10" height="15px"></td>
                </tr>
                <tr>
                    <td width="10%">&Aacute;rea:</td>
                    <td>
                        <h:selectOneMenu value="#{managerCLTaller.paramAreaId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLTaller.comboAreas}"/>
                            <a4j:support event="onchange" reRender="v_mod"
                                         actionListener="#{managerCLTaller.verModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td width="10%">M&oacute;dulo:</td>
                    <td>
                        <h:selectOneMenu id="v_mod" value="#{managerCLTaller.paramModId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLTaller.comboModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form id="form3">
            <table style="width: 100%">
                <tr>
                    <td colspan="10" valign="top" align="center">
                        <rich:datascroller id="paginacion" align="right" for="tablaMaster" maxPages="10"
                                           style="width: 90%;"/>
                        <rich:dataTable id="tablaMaster"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        cellpadding="0" cellspacing="0" width="90%" rows="10"
                                        value="#{managerCLTaller.cursos}" var="curso">
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{curso.rowCurId}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{curso.rowCurCodigo}"/>
                            </rich:column>
                            <rich:column colspan="2">
                                <f:facet name="header">
                                    <h:outputText value="M&oacute;dulo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{curso.rowCurModulo}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Nombre"/>
                                </f:facet>
                                <h:outputText value="#{curso.rowCurNombre}"/>
                            </rich:column>
                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Detalle"/>
                                </f:facet>
                                <f:param id="flag" value="#{curso.verDetalle}"/>
                                <a4j:commandButton image="#{curso.imagenDetalle}"
                                                   title="#{curso.tituloDetalle}"
                                                   actionListener="#{curso.mostrarTalleres}"
                                                   reRender="tablaMaster"/>
                            </rich:column>
                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Talleres"/>
                                </f:facet>
                                <f:param id="id_talleres" value="#{curso.rowCurId}"/>
                                <a4j:commandButton image="/Imagenes/actions/reservar.gif"
                                                   title="Cargar Talleres"
                                                   actionListener="#{managerCLTaller.cargarTalleres}"
                                                   oncomplete="#{managerCLTaller.oncomplete}"
                                                   reRender="iModulo,iCodigo,iCurso,iPlan,pnlTalleres"/>
                            </rich:column>
                            <rich:subTable id="detalleMaster" value="#{curso.talleres}" var="taller"
                                           onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                                           onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                           rendered="#{curso.verDetalle}">
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Id"/>
                                    </f:facet>
                                    <h:outputText value="#{taller.subTalId}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="C&oacute;digo" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{taller.subTalCodigo}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="N&uacute;mero" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{taller.subTalNumero}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{taller.subTalDescripcion}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Plan Curricular"/>
                                    </f:facet>
                                    <h:outputText value="#{taller.subPlaDescripcion}"/>
                                </rich:column>
                            </rich:subTable>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpTaller" autosized="true" minWidth="600" zindex="3000">
        <f:facet name="header">
            <h:outputText value="Talleres"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpTaller')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form2">
            <table style="width: 100%;">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           title="Guardar"
                                           actionListener="#{managerCLTaller.guardarTalleres}"
                                           action="#{managerCLTaller.seleccionar}"
                                           oncomplete="#{managerCLTaller.oncomplete}"
                                           reRender="tablaMaster,paginacion"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <h:outputText value="INFORMACI&Oacute;N" style="font-weight: bold;" escape="false"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="height: 5px;"></td>
                </tr>
                <tr>
                    <td style="width: 20%"><h:outputText value="M&oacute;dulo" escape="false"/></td>
                    <td>
                        <h:outputText id="iModulo" value="#{managerCLTaller.rowCurModulo}" style="width: 300px;"/>
                    </td>
                </tr>
                <tr>
                    <td><h:outputText value="C&oacute;digo" escape="false"/></td>
                    <td>
                        <h:outputText id="iCodigo" value="#{managerCLTaller.rowCurCodigo}" style="width: 300px;"/>
                    </td>
                </tr>
                <tr>
                    <td><h:outputText value="Curso"/></td>
                    <td>
                        <h:outputText id="iCurso" value="#{managerCLTaller.rowCurNombre}" style="width: 300px;"/>
                    </td>
                </tr>
                <tr>
                    <td><h:outputText value="Plan Curricular"/></td>
                    <td>
                        <h:outputText id="iPlan" value="#{managerCLTaller.subPlaDescripcion}" style="width: 300px;"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <h:panelGroup id="pnlTalleres">
                            <table style="width: 100%;">
                                <tr>
                                    <td colspan="2" align="left">
                                        <h:outputText value="TALLER" style="font-weight: bold;"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" style="height: 5px;"></td>
                                </tr>
                                <tr>
                                    <td><h:outputText value="C&oacute;digo" escape="false"/>
                                    </td>
                                    <td>
                                        <h:inputText id="iTalCodigo" value="#{managerCLTaller.subTalCodigo}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><h:outputText value="Descripci&oacute;n" escape="false"/>
                                    </td>
                                    <td>
                                        <h:inputText id="iTalDescripcion" value="#{managerCLTaller.subTalDescripcion}"
                                                     style="width: 400px;"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><h:outputText value="N&uacute;mero" escape="false"/>
                                    </td>
                                    <td>
                                        <h:inputText id="iTalNumero" value="#{managerCLTaller.subTalNumero}"
                                                     style="width: 100px;" maxlength="2"/>
                                        <rich:spacer width="40px"/>
                                        <a4j:commandButton actionListener="#{managerCLTaller.agregarTaller}"
                                                           reRender="iTalCodigo,iTalNumero,iTalDescripcion,tablaDetalle"
                                                           oncomplete="#{managerCLTaller.message}"
                                                           image="/Imagenes/actions/edit_add.png"
                                                           title="Agregar"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" style="height: 10px;"></td>
                                </tr>
                                <tr>
                                    <td colspan="2" valign="top">
                                        <rich:dataTable id="tablaDetalle"
                                                        width="100%"
                                                        value="#{managerCLTaller.mpTableTalleres}"
                                                        var="taller">
                                            <rich:column>
                                                <f:facet name="header">
                                                    <h:outputText value="Id"/>
                                                </f:facet>
                                                <h:outputText value="#{taller.subTalId}"/>
                                            </rich:column>

                                            <rich:column>
                                                <f:facet name="header">
                                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                                </f:facet>
                                                <h:outputText value="#{taller.subTalCodigo}"
                                                              rendered="#{!taller.editable}"/>
                                                <h:inputText value="#{taller.editTalCodigo}"
                                                             rendered="#{taller.editable}" style="width: 40px"/>
                                            </rich:column>

                                            <rich:column>
                                                <f:facet name="header">
                                                    <h:outputText value="Descripci&oacute;n" escape="false"/>
                                                </f:facet>
                                                <h:outputText value="#{taller.subTalDescripcion}"
                                                              rendered="#{!taller.editable}"/>
                                                <h:inputText value="#{taller.editTalDescripcion}"
                                                             rendered="#{taller.editable}"/>
                                            </rich:column>

                                            <rich:column>
                                                <f:facet name="header">
                                                    <h:outputText value="N&uacute;mero" escape="false"/>
                                                </f:facet>
                                                <h:outputText value="#{taller.subTalNumero}"
                                                              rendered="#{!taller.editable}"/>
                                                <h:inputText value="#{taller.editTalNumero}" maxlength="2"
                                                             rendered="#{taller.editable}" style="width: 30px"/>
                                            </rich:column>

                                            <rich:column style="text-align: center;">
                                                <f:facet name="header">
                                                    <h:outputText value="Editar"/>
                                                </f:facet>
                                                <f:param id="id_tal_update" value="#{taller.subTalId}"/>
                                                <a4j:commandButton actionListener="#{taller.editarTaller}"
                                                                   reRender="tablaDetalle"
                                                                   image="/Imagenes/actions/klipper_dock.png"
                                                                   title="Editar"
                                                                   rendered="#{!taller.editable}"/>
                                                <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                                                   title="Grabar"
                                                                   actionListener="#{taller.guardarTaller}"
                                                                   reRender="tablaDetalle"
                                                                   oncomplete="#{taller.message}"
                                                                   rendered="#{taller.editable}"/>
                                                <a4j:commandButton image="/Imagenes/actions/fileclose.png"
                                                                   title="Cancelar"
                                                                   actionListener="#{taller.cancelarTaller}"
                                                                   reRender="tablaDetalle"
                                                                   rendered="#{taller.editable}"/>
                                            </rich:column>

                                            <rich:column style="text-align: center;">
                                                <f:facet name="header">
                                                    <h:outputText value="Eliminar"/>
                                                </f:facet>
                                                <f:param id="id_tal_delete" value="#{taller.subTalId}"/>
                                                <a4j:commandButton actionListener="#{managerCLTaller.eliminarTaller}"
                                                                   reRender="tablaDetalle"
                                                                   oncomplete="#{managerCLTaller.message}"
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
