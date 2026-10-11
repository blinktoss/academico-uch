<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Configuraci&oacute;n de Estructuras de Pago</title>
    <script type="text/javascript">
        function printObjectsSelected(sgcomponent, iIdConcepto, iConcepto) {
            iIdConcepto.value = sgcomponent.getSelectedItems().pluck('auConpagId');
            iConcepto.innerHTML = sgcomponent.getSelectedItems().pluck('auConcepto');
        }
    </script>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%">
                <tr>
                    <td colspan="2">
                        <h:outputText value="MANTENIMIENTO DE ESTRUCTURAS DE PAGOS" style="font-weight: bold;"/>
                    </td>
                    <td align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton image="/Imagenes/actions/filenew.png" title="Nuevo"
                                           actionListener="#{managerCLEstructuraPago.nuevo}"
                                           oncomplete="Richfaces.showModalPanel('mpUpdate')"
                                           reRender="iArea, iModulo,iNombre,iAbreviatura,iSugConcepto,suggestionConcepto,iIdConcepto,iConcepto,iTaller,iDesc,iMonto,tablaDetalle"/>
                        <rich:spacer width="8px"/>

                        <h:commandButton image="/Imagenes/actions/viewmag.png"
                                         title="Buscar"
                                         action="#{managerCLEstructuraPago.seleccionar}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="10"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="10%">Descripci&oacute;n</td>
                    <td colspan="9" align="left"><h:inputText id="b_nombre"
                                                              value="#{managerCLEstructuraPago.paramNombre}"
                                                              style="width: 300px;"/></td>
                </tr>
                <tr>
                    <td width="10%">&Aacute;rea</td>
                    <td>
                        <h:selectOneMenu value="#{managerCLEstructuraPago.paramAreaId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEstructuraPago.areas}"/>
                            <a4j:support event="onchange" reRender="v_mod"
                                         actionListener="#{managerCLEstructuraPago.verModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td width="10%">M&oacute;dulo</td>
                    <td>
                        <h:selectOneMenu id="v_mod" value="#{managerCLEstructuraPago.paramModId}">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerCLEstructuraPago.modulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form id="form3">
            <table style="width:100%">

                <tr>
                    <td colspan="10" valign="top" align="center">
                        <rich:datascroller id="paginacion" align="right" for="tablaMaster" maxPages="10"
                                           style="width: 100%;"/>
                        <rich:dataTable id="tablaMaster"
                                        onRowMouseOver="this.style.backgroundColor='#D7D7D7'"
                                        onRowMouseOut="this.style.backgroundColor='#FFFFFF'"
                                        cellpadding="0"
                                        cellspacing="0"
                                        width="100%"
                                        rows="10"
                                        value="#{managerCLEstructuraPago.estructuras}"
                                        var="est">
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{est.rowEstpagId}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="M&oacute;dulo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{est.modulo}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Descripci&oacute;n" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{est.rowEstpagNombre}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Abreviatura"/>
                                </f:facet>
                                <h:outputText value="#{est.rowEstpagAbrev}"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Ver"/>
                                </f:facet>
                                <f:param id="id_ver" value="#{est.verDetalle}"/>
                                <a4j:commandButton image="#{est.verDetImagen}" title="#{est.verDetTitulo}"
                                                   actionListener="#{est.mostrarDetalle}"
                                                   reRender="tablaMaster"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Editar"/>
                                </f:facet>
                                <f:param id="id_update" value="#{est.rowEstpagId}"/>
                                <a4j:commandButton image="/Imagenes/actions/icon_edit.png"
                                                   title="Modificar"
                                                   actionListener="#{managerCLEstructuraPago.actualizarEstructura}"
                                                   oncomplete="#{managerCLEstructuraPago.oncomplete}"
                                                   reRender="iArea, iModulo,iNombre,iAbreviatura,iSugConcepto,suggestionConcepto,iIdConcepto,iConcepto,iTaller,iDesc,iMonto,tablaDetalle"/>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Eliminar"/>
                                </f:facet>
                                <f:param id="id_delete" value="#{est.rowEstpagId}"/>
                                <a4j:commandButton image="/Imagenes/actions/no.png"
                                                   title="Eliminar"
                                                   actionListener="#{managerCLEstructuraPago.eliminarEstructura}"
                                                   action="#{managerCLEstructuraPago.seleccionar}"
                                                   oncomplete="#{managerCLEstructuraPago.oncomplete}"
                                                   reRender="tablaMaster,paginacion"/>
                            </rich:column>

                            <rich:subTable id="detalleMaster" value="#{est.detalle}" var="det"
                                           onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                           onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                           rendered="#{est.verDetalle}">
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Id"/>
                                    </f:facet>
                                    <h:outputText value="#{det.rsbEstpagdetId}"/>
                                </rich:column>

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Taller"/>
                                    </f:facet>
                                    <h:outputText value="#{det.taller}"/>
                                </rich:column>

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{det.rsbEstpagdetNombre}"/>
                                </rich:column>

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Concepto"/>
                                    </f:facet>
                                    <h:outputText value="#{det.concepto}"/>
                                </rich:column>

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Monto"/>
                                    </f:facet>
                                    <h:outputText value="#{det.rsbEstpagdetMonto}"/>
                                </rich:column>

                                <rich:column colspan="2"/>
                            </rich:subTable>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpUpdate" autosized="true" zindex="3000" width="550">
        <f:facet name="header">
            <h:outputText value="Estructura de Pagos"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpUpdate')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form2">
            <table style="font-size:12px; width: 100%">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           title="Guardar"
                                           actionListener="#{managerCLEstructuraPago.guardarEstructura}"
                                           oncomplete="#{managerCLEstructuraPago.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="2" align="left">
                        <h:outputText value="ESTRUCTURA DE PAGOS" style="font-weight: bold;"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="height: 5px;"></td>
                </tr>
                <tr>
                    <td>&Aacute;rea</td>
                    <td>
                        <h:selectOneMenu id="iArea" value="#{managerCLEstructuraPago.mpAreaId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEstructuraPago.mpAreas}"/>
                            <a4j:support event="onchange" reRender="iModulo,iTaller,iDesc"
                                         actionListener="#{managerCLEstructuraPago.loadModalModules}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>M&oacute;dulo</td>
                    <td>
                        <h:selectOneMenu id="iModulo" value="#{managerCLEstructuraPago.mpModId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEstructuraPago.mpModulos}"/>
                            <a4j:support event="onchange" reRender="iTaller,iDesc"
                                         actionListener="#{managerCLEstructuraPago.loadWorkshops}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Nombre</td>
                    <td>
                        <h:inputText id="iNombre" value="#{managerCLEstructuraPago.mpEstpagNombre}"
                                     style="width: 360px;"/>
                    </td>
                </tr>
                <tr>
                    <td>Abreviatura</td>
                    <td>
                        <h:inputText id="iAbreviatura" value="#{managerCLEstructuraPago.mpEstpagAbrev}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="2" align="left">
                        <h:outputText value="DETALLE" style="font-weight: bold;"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="height: 5px;"></td>
                </tr>
                <tr>
                    <td>Concepto</td>
                    <td colspan="3" align="left">
                        <h:inputText id="iSugConcepto" value="#{managerCLEstructuraPago.sugConcepto}"
                                     style="width:200px;"/>
                        <rich:suggestionbox id="suggestionConcepto"
                                            for="iSugConcepto" nothingLabel="Concepto No Encontrado"
                                            suggestionAction="#{managerCLEstructuraPago.autocomplete}" var="cp"
                                            tokens=","
                                            height="150" width="350" cellpadding="2"
                                            cellspacing="2" shadowOpacity="4" shadowDepth="4"
                                            minChars="2" rules="none" zindex="3500"
                                            usingSuggestObjects="true"
                                            onobjectchange="printObjectsSelected(#{rich:component('suggestionConcepto')},#{rich:element('iIdConcepto')},#{rich:element('iConcepto')});">
                            <h:column>
                                <h:outputText value="#{cp.auConcepto}" style="font-style:bold"/>
                            </h:column>
                        </rich:suggestionbox>
                        <h:inputHidden id="iIdConcepto" value="#{managerCLEstructuraPago.sugConpagId}"/>
                        <rich:spacer width="10px"/>
                        <h:outputText id="iConcepto" value="#{managerCLEstructuraPago.sugConpagDes}"
                                      style="font-weight: bold;"/>
                    </td>
                </tr>
                <tr>
                    <td>Taller</td>
                    <td>
                        <h:selectOneMenu id="iTaller" value="#{managerCLEstructuraPago.mpTalId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEstructuraPago.mpTalleres}"/>
                            <a4j:support event="onchange" reRender="iDesc"
                                         actionListener="#{managerCLEstructuraPago.loadWorkshopDescription}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Descripci&oacute;n</td>
                    <td>
                        <h:inputText id="iDesc" value="#{managerCLEstructuraPago.mpEstpagdetDescription}"
                                     style="width: 360px;"/>
                    </td>
                </tr>
                <tr>
                    <td>Monto</td>
                    <td>
                        <h:inputText id="iMonto" value="#{managerCLEstructuraPago.mpEstpagdetMonto}"
                                     style="width: 300px;"/>
                        <rich:spacer width="50px"/>
                        <a4j:commandButton actionListener="#{managerCLEstructuraPago.agregarDetalle}"
                                           reRender="iIdConcepto,iConcepto,iTaller,iDesc,iMonto,tablaDetalle"
                                           oncomplete="#{managerCLEstructuraPago.oncomplete}"
                                           image="/Imagenes/actions/edit_add.png" title="Agregar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="height: 10px;"></td>
                </tr>
                <tr>
                    <td colspan="2" valign="top">
                        <rich:dataTable id="tablaDetalle"
                                        width="100%"
                                        value="#{managerCLEstructuraPago.detDetalle}"
                                        var="det">
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{det.detEstpagdetId}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Concepto"/>
                                </f:facet>
                                <h:outputText value="#{det.sugConpagDes}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Taller"/>
                                </f:facet>
                                <h:outputText value="#{det.detDetTaller}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Descripci&oacute;n" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{det.mpEstpagdetDescription}" rendered="#{det.detalleVer}"/>
                                <h:inputText value="#{det.ediEstpagdetDes}" rendered="#{det.detalleEditar}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Monto"/>
                                </f:facet>
                                <h:outputText value="#{det.mpEstpagdetMonto}" rendered="#{det.detalleVer}"/>
                                <h:inputText value="#{det.ediEstpagdetMonto}" rendered="#{det.detalleEditar}"/>
                            </rich:column>
                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Editar"/>
                                </f:facet>
                                <a4j:commandButton actionListener="#{det.editarDetalle}"
                                                   reRender="tablaDetalle"
                                                   image="/Imagenes/actions/klipper_dock.png"
                                                   title="Editar"
                                                   rendered="#{det.detalleVer}"/>
                                <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                                   title="Grabar"
                                                   actionListener="#{det.guardarDetalle}"
                                                   reRender="tablaDetalle"
                                                   oncomplete="#{det.oncomplete}"
                                                   rendered="#{det.detalleEditar}"/>
                                <a4j:commandButton image="/Imagenes/actions/fileclose.png"
                                                   title="Cancelar"
                                                   actionListener="#{det.cancelarDetalle}"
                                                   reRender="tablaDetalle"
                                                   rendered="#{det.detalleEditar}"/>
                            </rich:column>
                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Eliminar"/>
                                </f:facet>
                                <f:param id="id_det_delete" value="#{det.detEstpagdetId}"/>
                                <a4j:commandButton actionListener="#{managerCLEstructuraPago.eliminarDetalle}"
                                                   reRender="tablaDetalle"
                                                   oncomplete="#{managerCLEstructuraPago.oncomplete}"
                                                   image="/Imagenes/actions/delete.gif"
                                                   title="Eliminar"/>
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
