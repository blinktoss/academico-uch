<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Mantenimiento de Estructura de Pagos</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%;">
                <tr>
                    <td width="20%" colspan="2"><strong>ESTRUCTURA DE PAGOS</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;" title="Nuevo">
                            <a4j:support oncomplete="Richfaces.showModalPanel('mp', {top:80})" event="onclick"
                                         actionListener="#{managerEstructuraPago.nuevo}"
                                         reRender="iId1,iSem2,SemeD,iDes1,iDes2,FacuI,EspeI,SemeI,monto_det,sem_det,fecha_det,desc_det,tablaSecundaria"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%"></td>
                    <td width="30%"></td>
                    <td align=right width="50%">
                        <h:commandButton type="button" id="buscar" action="#{managerEstructuraPago.seleccionar}"
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
                </tr>
                <tr>
                    <td width="10%">Facultad:</td>
                    <td>
                        <h:selectOneMenu id="FacuBuscar" value="#{managerEstructuraPago.paramFacuId}"
                                         style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerEstructuraPago.comboFacultad}"/>
                            <a4j:support event="onchange"
                                         actionListener="#{managerEstructuraPago.setearFacultadBuscar}"
                                         reRender="EspeBuscar"/>
                        </h:selectOneMenu>
                    </td>
                    <td colspan="4"></td>
                </tr>
                <tr>
                    <td>Especialidad:</td>
                    <td>
                        <h:selectOneMenu id="EspeBuscar" value="#{managerEstructuraPago.paramEspeId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerEstructuraPago.comboEspecialidadBuscar}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%" colspan="4"></td>
                </tr>
                <tr>
                    <td>Semestre:</td>
                    <td>
                        <h:selectOneMenu id="Semestre" value="#{managerEstructuraPago.paramSemeId}"
                                         style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerEstructuraPago.comboSemestre}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%" colspan="4"></td>
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
            </table>
        </h:form>
        <h:form style="width: 100%;">
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8"/>
            <rich:dataTable id="tablaMaster" cellpadding="0" cellspacing="0" border="0" rows="10" width="100%"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerEstructuraPago.tablaSisEva}" var="siseva">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{siseva.rowId}"/>
                    <f:param value="#{siseva.flag}" id="flag"/>
                    <h:inputHidden value="#{siseva.flag}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{siseva.rowDescription}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Facultad"/>
                    </f:facet>
                    <h:outputText value="#{siseva.rowFacultad}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Especialidad"/>
                    </f:facet>
                    <h:outputText value="#{siseva.rowEspecialidad}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Semestre Ejecuci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{siseva.rowSemEjecucionValue}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Semestre Destino"/>
                    </f:facet>
                    <h:outputText value="#{siseva.rowSemDestinoValue}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Publicado"/>
                    </f:facet>
                    <h:outputText value="#{siseva.rowPublicado}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:region id="ProvProc">
                        <h:graphicImage value="#{siseva.rowImgEdit}" style="border-width: 0;cursor: pointer;"
                                        height="16">
                            <a4j:support oncomplete="Richfaces.showModalPanel('mp',{top:80})" event="onclick"
                                         actionListener="#{managerEstructuraPago.editar}"
                                         reRender="iId1,iSem2,iDes1,iDes2,FacuI,EspeI,SemeI,SemeD,monto_det,sem_det,fecha_det,desc_det,tablaSecundaria"/>
                        </h:graphicImage>
                    </a4j:region>

                    <f:param id="id_s" value="#{siseva.rowId}"/>
                    <f:param id="fac_s" value="#{siseva.rowFacultad}"/>
                    <f:param id="esp_s" value="#{siseva.rowEspecialidad}"/>
                    <f:param id="fac_id_s" value="#{siseva.rowFacultadId}"/>
                    <f:param id="esp_id_s" value="#{siseva.rowEspecialidadId}"/>
                    <f:param id="sem_s" value="#{siseva.rowSemEjecucionValue}"/>
                    <f:param id="sem_id_s" value="#{siseva.rowSemEjecucionId}"/>
                    <f:param id="des_s" value="#{siseva.rowDescription}"/>
                    <f:param id="sem_d" value="#{siseva.rowSemDestinoValue}"/>
                    <f:param id="sem_id_d" value="#{siseva.rowSemDestinoId}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Ver"/>
                    </f:facet>
                    <h:graphicImage value="#{siseva.imagen}" style="border-width: 0;cursor: pointer;">
                        <a4j:support event="onclick" actionListener="#{siseva.subTable}" reRender="tablaMaster"/>
                    </h:graphicImage>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Publicar"/>
                    </f:facet>
                    <h:commandButton image="#{siseva.rowImgPublish}" title="Publicar"
                                     disabled="#{siseva.disabledPublish}"
                                     actionListener="#{managerEstructuraPago.publicar}"
                                     action="#{managerEstructuraPago.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de publicar esta Estructura de Pago?'));"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="#{siseva.rowImgDelete}" title="Eliminar"
                                     disabled="#{siseva.disabledDelete}"
                                     actionListener="#{managerEstructuraPago.eliminar}"
                                     action="#{managerEstructuraPago.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>

                <rich:subTable value="#{siseva.subTablaDetalle}" var="det" rendered="#{siseva.ver}" id="subtable"
                               onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                               onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Descripci&oacute;n" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{det.detNombre}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Codigo de Con. Pago"/>
                        </f:facet>
                        <h:outputText value="#{det.detCodigoRef}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Con. Pago Id"/>
                        </f:facet>
                        <h:outputText value="#{det.detConPagValue}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto de Referencia"/>
                        </f:facet>
                        <h:outputText value="#{det.detMontoRef}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Fecha Pago"/>
                        </f:facet>
                        <h:outputText value="#{det.detFecha}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto a Pagar"/>
                        </f:facet>
                        <h:outputText value="#{det.detMonto}"/>
                    </rich:column>
                    <rich:column colspan="5"/>
                </rich:subTable>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="mp" autosized="true" minWidth="1000" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Estructura de Pagos"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table style="width: 100%; font-size:10px; font-family:Verdana,sans-serif">
                    <tr>
                        <td align="right" colspan="3">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerEstructuraPago.grabar}"
                                               action="#{managerEstructuraPago.seleccionar}"
                                               reRender="tablaMaster, barra"
                                               oncomplete="#{managerEstructuraPago.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td width="15%">Facultad:</td>
                        <td width="70%">
                            <h:inputHidden id="iId1" value="#{managerEstructuraPago.rowId}"/>
                            <a4j:region id="FACU_I">
                                <h:selectOneMenu id="FacuI" value="#{managerEstructuraPago.rowFacultadId}">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerEstructuraPago.comboFacultad}"/>
                                    <a4j:support event="onchange"
                                                 actionListener="#{managerEstructuraPago.setearFacultadMp}"
                                                 reRender="EspeI"/>
                                </h:selectOneMenu>
                            </a4j:region>
                        </td>
                        <td align="right" width="20%"></td>
                    </tr>
                    <tr>
                        <td width="15%">Especialidad:</td>
                        <td width="70%">
                            <h:selectOneMenu id="EspeI" value="#{managerEstructuraPago.rowFacultadId}">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerEstructuraPago.mpComboEspecialidad}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="15%">Semestre Ejecuci&oacute;n:</td>
                        <td width="70%">
                            <h:selectOneMenu id="SemeI" value="#{managerEstructuraPago.rowSemEjecucionId}">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerEstructuraPago.comboSemestre}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="15%">Semestre Destino:</td>
                        <td width="70%">
                            <h:inputHidden id="iSem2" value="#{managerEstructuraPago.rowSemDestinoValue}"/>
                            <h:selectOneMenu id="SemeD" value="#{managerEstructuraPago.rowSemDestinoId}">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerEstructuraPago.comboSemestre}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="15%">Descripci&oacute;n:</td>
                        <td width="70%">
                            <h:inputHidden id="iDes1" value="#{managerEstructuraPago.mpDescription}"/>
                            <h:inputTextarea id="iDes2" value="#{managerEstructuraPago.mpDescription}"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <table>
                                <tr>
                                    <td>Concepto de Pago</td>
                                    <td>Monto a Pagar</td>
                                    <td>Fecha de Pago</td>
                                    <td>Descripci&oacute;n</td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td>
                                        <h:selectOneMenu id="sem_det" value="#{managerEstructuraPago.detConPagId}">
                                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                                            <f:selectItems value="#{managerEstructuraPago.comboConceptoPago}"/>
                                        </h:selectOneMenu>
                                    </td>
                                    <td>
                                        <h:inputText id="monto_det" value="#{managerEstructuraPago.detMonto}"/>
                                    </td>
                                    <td>
                                        <rich:calendar id="fecha_det" value="#{managerEstructuraPago.detFecha}"
                                                       datePattern="yyyy-MM-dd"/>
                                    </td>
                                    <td>
                                        <h:inputText id="desc_det" value="#{managerEstructuraPago.detNombre}"/>
                                    </td>
                                    <td>
                                        <a4j:commandButton image="/Imagenes/actions/edit_add.png"
                                                           actionListener="#{managerEstructuraPago.agregar}"
                                                           oncomplete="#{managerEstructuraPago.oncomplete}"
                                                           reRender="tablaSecundaria,ScrollerSecundaria"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" style="width: 100%">
                            <rich:datascroller id="ScrollerSecundaria" for="tablaSecundaria" maxPages="8"
                                               align="right"/>
                            <rich:dataTable id="tablaSecundaria" rows="10" cellpadding="0" cellspacing="0" width="100%"
                                            border="0"
                                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                            value="#{managerEstructuraPago.tablaSisEvaDetalle}" var="siseva_detalle">
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Id"/>
                                    </f:facet>
                                    <h:outputText value="#{siseva_detalle.detId}"/><br>
                                    <h:inputHidden value="#{siseva_detalle.detConPagId}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{siseva_detalle.detNombre}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="C&oacute;digo Con. Pag." escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{siseva_detalle.detCodigoRef}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Con Pago"/>
                                    </f:facet>
                                    <h:outputText value="#{siseva_detalle.detConPagValue}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Monto de Referencia"/>
                                    </f:facet>
                                    <h:outputText value="#{siseva_detalle.detMontoRef}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Fecha de Pago"/>
                                    </f:facet>
                                    <h:outputText value="#{siseva_detalle.detFecha}">
                                        <f:convertDateTime pattern="yyyy-MM-dd"/>
                                    </h:outputText>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Monto a Pagar"/>
                                    </f:facet>
                                    <h:outputText value="#{siseva_detalle.detMonto}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Eliminar"/>
                                    </f:facet>
                                    <f:param id="id_det" value="#{siseva_detalle.detId}"/>
                                    <f:param id="nombre_det" value="#{siseva_detalle.detNombre}"/>
                                    <a4j:commandButton image="/Imagenes/actions/edit_remove.png"
                                                       actionListener="#{managerEstructuraPago.quitar}"
                                                       reRender="tablaSecundaria"/>
                                </rich:column>
                            </rich:dataTable>
                        </td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>

    </rich:panel>

</f:view>
</body>
</html>
