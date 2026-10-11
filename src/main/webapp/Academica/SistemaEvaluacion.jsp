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
    <title>Mantenimiento de Sistemas de Evaluacion</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table width="100%">
                <tr>
                    <td width="20%" colspan="2"><strong>LISTADO SISTEMAS DE EVALUACI&Oacute;N</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td width="30%">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                            <a4j:support oncomplete="Richfaces.showModalPanel('mp',{top:80})" event="onclick"
                                         actionListener="#{managerSistemaEvaluacion.nuevo}"
                                         reRender="iId1,iCodigo1,iNombre1,iVigente1,codigo_det,nombre_det,peso_det,tablaSecundaria"/>
                        </h:graphicImage>
                    </td>
                    <td align=right width="50%">
                        <a4j:commandButton type="button" id="buscar" title="Buscar" reRender="scroller,tablaMaster"
                                           actionListener="#{managerSistemaEvaluacion.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="2" lineType="double"/></td>
                </tr>
                <tr>
                    <td width="20%" colspan="2"></td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                </tr>
                <tr style="height: 24px;">
                    <td width="8%">Descripci&oacute;n:</td>
                    <td width="30%">
                        <h:inputText id="txtCodigo" value="#{managerSistemaEvaluacion.nombreF}" style="width : 180px;"
                                     maxlength="10"/></td>
                    <td width="50%">
                    </td>
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
                    <td colspan="8"></td>
                </tr>
            </table>
        </h:form>
        <h:form>
            <rich:datascroller id="scroller" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            cellpadding="0" cellspacing="0"
                            width="100%" border="1" value="#{managerSistemaEvaluacion.tablaSisEva}" var="siseva">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{siseva.idS}"/>
                    <f:param value="#{siseva.flagVer}" id="flag"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{siseva.codigoS}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>
                    <h:outputText value="#{siseva.nombreS}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Creaci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{siseva.creacionS}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Vigente"/>
                    </f:facet>
                    <h:graphicImage value="#{siseva.vigenteImg}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Ver"/>
                    </f:facet>
                    <h:graphicImage value="#{siseva.imagen}" style="cursor: pointer;">
                        <a4j:support event="onclick" actionListener="#{siseva.openDetail}" reRender="tablaMaster"/>
                    </h:graphicImage>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:region id="ProvProc">
                        <h:graphicImage value="/Imagenes/actions/editpaste.png" style="cursor: pointer;">
                            <a4j:support oncomplete="Richfaces.showModalPanel('mp',{width:600, top:80})" event="onclick"
                                         actionListener="#{managerSistemaEvaluacion.editar}"
                                         reRender="iId1,iCodigo1,iNombre1,iVigente1,tablaSecundaria"/>
                        </h:graphicImage>
                    </a4j:region>
                    <f:param id="id_s" value="#{siseva.idS}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerSistemaEvaluacion.eliminar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>
                <rich:subTable value="#{siseva.detalle}" var="det" rendered="#{siseva.ver}" id="subtable"
                               onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                               onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">

                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Cod. Evaluaci&oacute;n" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{det.codigoDet}"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Nom. Evaluaci&oacute;n" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{det.nombreDet}"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Pes. Evaluaci&oacute;n" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{det.pesoDet}"/>
                    </h:column>
                </rich:subTable>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="mp" autosized="true" minWidth="650" zindex="2000">
            <div id="contentDiv">
                <f:facet name="header">
                    <h:outputText value="Registro de Sistemas de Evaluaci&oacute;n" escape="false"/>
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
                                                   actionListener="#{managerSistemaEvaluacion.guardar}"
                                                   reRender="tablaMaster"
                                                   oncomplete="#{managerSistemaEvaluacion.oncomplete}"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3"><rich:separator height="1"/></td>
                        </tr>
                        <tr>
                            <td width="30%">C&oacute;digo:</td>
                            <td width="70%">
                                <h:inputHidden id="iId1" value="#{managerSistemaEvaluacion.idI}"/>
                                <h:inputText id="iCodigo1" style="width: 180px;"
                                             value="#{managerSistemaEvaluacion.codigoI}"/>
                            </td>
                            <td align="right" width="20%">
                            </td>
                        </tr>
                        <tr>
                            <td width="30%">Nombre:</td>
                            <td width="70%">
                                <h:inputText id="iNombre1" value="#{managerSistemaEvaluacion.nombreI}"
                                             style="width: 180px;"/>
                            </td>
                            <td width="20%"></td>
                        </tr>
                        <tr>
                            <td width="30%">Vigente:</td>
                            <td width="70%">
                                <h:selectBooleanCheckbox id="iVigente1" value="#{managerSistemaEvaluacion.vigenteI}"/>
                            </td>
                            <td width="20%"></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td colspan="4"><rich:separator height="1"/></td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <table>
                                    <tr>
                                        <td>C&oacute;digo de Evaluaci&oacute;n
                                        </td>
                                        <td>Nombre de Evaluaci&oacute;n
                                        </td>
                                        <td>Peso de Evaluaci&oacute;n
                                        </td>
                                        <td>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><h:inputText id="codigo_det" value="#{managerSistemaEvaluacion.codigoDet}"/>
                                        </td>
                                        <td><h:inputText id="nombre_det" value="#{managerSistemaEvaluacion.nombreDet}"/>
                                        </td>
                                        <td><h:inputText id="peso_det" value="#{managerSistemaEvaluacion.pesoDet}"/>
                                        </td>
                                        <td><a4j:commandButton image="/Imagenes/actions/edit_add.png"
                                                               actionListener="#{managerSistemaEvaluacion.agregar}"
                                                               oncomplete="#{managerSistemaEvaluacion.oncomplete}"
                                                               reRender="tablaSecundaria"/>
                                        </td>
                                        <td>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <rich:dataTable id="tablaSecundaria" rows="10" cellpadding="0" cellspacing="0"
                                                width="100%" border="0"
                                                onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                                onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                                value="#{managerSistemaEvaluacion.tablaSisEvaDetalle}"
                                                var="siseva_detalle">
                                    <rich:column>
                                        <f:facet name="header">
                                            <h:outputText value="Id"/>
                                        </f:facet>
                                        <h:outputText value="#{siseva_detalle.idDet}"/>
                                    </rich:column>
                                    <rich:column>
                                        <f:facet name="header">
                                            <h:outputText value="C&oacute;digo de Sistema" escape="false"/>
                                        </f:facet>
                                        <h:outputText value="#{siseva_detalle.codigoDet}"
                                                      rendered="#{siseva_detalle.view}"/>
                                        <h:inputText value="#{siseva_detalle.codigoDet}"
                                                     rendered="#{siseva_detalle.editable}"/>
                                    </rich:column>
                                    <rich:column>
                                        <f:facet name="header">
                                            <h:outputText value="Nombre de Sistema"/>
                                        </f:facet>
                                        <h:outputText value="#{siseva_detalle.nombreDet}"
                                                      rendered="#{siseva_detalle.view}"/>
                                        <h:inputText value="#{siseva_detalle.nombreDet}"
                                                     rendered="#{siseva_detalle.editable}"/>
                                    </rich:column>
                                    <rich:column>
                                        <f:facet name="header">
                                            <h:outputText value="Peso"/>
                                        </f:facet>
                                        <h:outputText value="#{siseva_detalle.pesoDet}"
                                                      rendered="#{siseva_detalle.view}"/>
                                        <h:inputText value="#{siseva_detalle.pesoDet}"
                                                     rendered="#{siseva_detalle.editable}"/>
                                    </rich:column>
                                    <rich:column>
                                        <f:facet name="header">
                                            <h:outputText value="Activo"/>
                                        </f:facet>
                                        <h:outputText value="#{siseva_detalle.activoDet}"/>
                                    </rich:column>
                                    <rich:column style="text-align: center;">
                                        <f:facet name="header">
                                            <h:outputText value="Editar"/>
                                        </f:facet>
                                        <a4j:commandButton actionListener="#{siseva_detalle.edit}"
                                                           image="/Imagenes/actions/klipper_dock.png"
                                                           title="Editar" rendered="#{siseva_detalle.view}"
                                                           reRender="tablaSecundaria"/>
                                        <a4j:commandButton actionListener="#{siseva_detalle.aceptar}"
                                                           image="/Imagenes/actions/fileexport.png"
                                                           title="Aceptar" rendered="#{siseva_detalle.editable}"
                                                           reRender="tablaSecundaria"/>
                                        <a4j:commandButton actionListener="#{siseva_detalle.cancelar}"
                                                           image="/Imagenes/actions/fileclose.png"
                                                           title="Cancelar" rendered="#{siseva_detalle.editable}"
                                                           reRender="tablaSecundaria"/>
                                    </rich:column>
                                    <rich:column style="text-align: center;">
                                        <f:facet name="header">
                                            <h:outputText value="Eliminar"/>
                                        </f:facet>
                                        <f:param id="id_det" value="#{siseva_detalle.idDet}"/>
                                        <a4j:commandButton image="/Imagenes/actions/edit_remove.png"
                                                           actionListener="#{managerSistemaEvaluacion.quitar}"
                                                           reRender="tablaSecundaria"/>
                                    </rich:column>
                                </rich:dataTable>
                            </td>
                        </tr>
                    </table>
                </h:form>
            </div>
        </rich:modalPanel>

    </rich:panel>

</f:view>
</body>
</html>
