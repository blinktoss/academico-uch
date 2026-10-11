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
    <title>Apertura de Talleres</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table width="100%">
                <tr>
                    <td width="20%" colspan="2"><strong>APERTURA DE TALLERES</strong>
                    </td>
                    <td width="30%"></td>
                    <td width="70%" align=right>
                        <h:outputText id="cabecera"
                                      value="#{managerCLTallerAperturado.lblModulo} - #{managerCLTallerAperturado.lblCurso}"/>
                    </td>
                    <td align=right>
                        <table>
                            <tr>
                                <td>
                                    <a4j:status>
                                        <f:facet name="start">
                                            <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                                        </f:facet>
                                    </a4j:status>
                                </td>
                                <td>
                                    <h:commandButton type="button" id="buscar"
                                                     action="#{managerCLTallerAperturado.cargarArbol}"
                                                     image="/Imagenes/actions/reload.png" title="Refrescar"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="10" height="10px">
                        <hr width="100%"/>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form id="form2">
            <table style="width: 100%">
                <tr>
                    <td style="width: 250px;vertical-align: top;">
                        <rich:tree value="#{managerCLTallerAperturado.arbol}"
                                   ajaxSubmitSelection="true"
                                   switchType="ajax"
                                   style="font-size:10px; font-family:verdana"
                                   var="item"
                                   nodeSelectListener="#{managerCLTallerAperturado.seleccion}"
                                   reRender="cabecera,tablaMaster"/>
                    </td>
                    <td style="vertical-align:top;" colspan="3">
                        <h:outputText escape="false" value="Talleres del Curso Seleccionado:"/>
                        <rich:spacer height="5px"/>
                        <rich:dataTable id="tablaMaster"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        cellpadding="0" cellspacing="0"
                                        width="100%" border="0" value="#{managerCLTallerAperturado.talleres}" var="tal">

                            <f:param id="id_taller_tmp" value="#{tal.rowId}"/>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{tal.rowId}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{tal.rowCodigo}"/>
                            </rich:column>

                            <rich:column colspan="2">
                                <f:facet name="header">
                                    <h:outputText value="Descripci&oacute;n" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{tal.rowDescripcion}"/>
                            </rich:column>

                            <rich:column colspan="2" style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Aperturar"/>
                                </f:facet>
                                <a4j:commandButton image="/Imagenes/actions/filenew.png"
                                                   title="Nuevo taller aperturado"
                                                   actionListener="#{managerCLTallerAperturado.aperturarTaller}"
                                                   oncomplete="#{managerCLTallerAperturado.oncomplete}"
                                                   reRender="form3"/>
                            </rich:column>

                            <rich:column colspan="2" style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Ver"/>
                                </f:facet>
                                <a4j:commandButton image="#{tal.rowImagenMostrar}" title="#{tal.rowTextoMostrar}"
                                                   actionListener="#{tal.mostrarDetalle}"
                                                   reRender="tablaMaster"/>
                            </rich:column>

                            <rich:subTable id="subtable" value="#{tal.subTalleresAperturados}" var="talape"
                                           onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                                           onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                           rendered="#{tal.verDetalle}">

                                <f:param id="id_taller_aper_tmp" value="#{talape.subTalapeId}"/>
                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Id"/>
                                    </f:facet>
                                    <h:outputText value="#{talape.subTalapeId}"/>
                                </rich:column>

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Descripcion"/>
                                    </f:facet>
                                    <h:outputText value="#{talape.subTalapeDesc}"/>
                                </rich:column>

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Sis. Evaluaci&oacute;n" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{talape.subSisEvaluacion}"/>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Num. horas"/>
                                    </f:facet>
                                    <h:outputText value="#{talape.subTalapeNumHoras}"/>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Aperturado"/>
                                    </f:facet>
                                    <a4j:commandButton image="#{talape.subImagenAperturar}"
                                                       title="Aperturar"
                                                       actionListener="#{managerCLTallerAperturado.aperturarTaller}"
                                                       oncomplete="#{managerCLTallerAperturado.oncomplete}"
                                                       reRender="form3"/>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Vigente"/>
                                    </f:facet>
                                    <h:graphicImage value="#{talape.subImagenVigente}"/>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Actual"/>
                                    </f:facet>
                                    <h:graphicImage value="#{talape.subImagenActual}"/>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Eliminar"/>
                                    </f:facet>
                                    <a4j:commandButton image="/Imagenes/actions/delete.gif" title="Eliminar"
                                                       actionListener="#{managerCLTallerAperturado.prepararEliminacionTalape}"
                                                       oncomplete="Richfaces.showModalPanel('mpEliminar');"
                                                       reRender="formElim"/>
                                </rich:column>
                            </rich:subTable>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpEliminar" width="300" height="100"
                     autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Apertura de Taller"/>
        </f:facet>
        <h:form id="formElim">
            <h:panelGroup layout="block" style="text-align: center;font-size: 12px;">
                <h:outputText escape="false"
                              value="#{managerCLTallerAperturado.mpElimMensaje}
                                       <br/><b>#{managerCLTallerAperturado.mpElimTalapeDesc}</b><br/><br/>"/>

                <a4j:commandButton title="Aceptar"
                                   image="/Imagenes/actions/button_accept.png"
                                   rendered="#{managerCLTallerAperturado.mpElimBand}"
                                   actionListener="#{managerCLTallerAperturado.eliminarTallerAperturado}"
                                   oncomplete="Richfaces.hideModalPanel('mpEliminar')"
                                   reRender="tablaMaster"/>
                <rich:spacer width="50px" rendered="#{managerCLTallerAperturado.mpElimBand}"/>

                <a4j:commandButton title="Cancelar"
                                   image="/Imagenes/actions/button_cancel.png"
                                   oncomplete="Richfaces.hideModalPanel('mpEliminar')"/>
            </h:panelGroup>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpApertura" autosized="true"
                     minWidth="650"
                     zindex="3000">
        <f:facet name="header">
            <h:outputText value="Apertura de Taller"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpApertura')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <h:form id="form3">
            <table style="width: 100%; font-size:13px;">
                <tr>
                    <td colspan="2" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           title="Guardar"
                                           actionListener="#{managerCLTallerAperturado.guardarTaller}"
                                           oncomplete="#{managerCLTallerAperturado.oncomplete}"
                                           reRender="tablaMaster"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <hr size="1">
                    </td>
                </tr>
                <tr>
                    <td width="100px;" class="tdLabel">M&oacute;dulo</td>
                    <td><h:outputText id="apeModulo" value="#{managerCLTallerAperturado.mpModulo}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Curso</td>
                    <td><h:outputText id="apeCurso" value="#{managerCLTallerAperturado.mpCurso}"/></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <hr size="1">
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel">Descripci&oacute;n</td>
                    <td><h:inputText id="apeDescripcion" style="width: 300px;"
                                     value="#{managerCLTallerAperturado.subTalapeDesc}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Nro. de Horas</td>
                    <td><h:inputText id="apeNumHoras" style="width: 40px;"
                                     value="#{managerCLTallerAperturado.subTalapeNumHoras}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Aperturado</td>
                    <td><h:selectBooleanCheckbox id="apeAperturado"
                                                 value="#{managerCLTallerAperturado.mpTalapeAperturado}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Vigente</td>
                    <td><h:selectBooleanCheckbox id="apeVigente"
                                                 value="#{managerCLTallerAperturado.mpTalapeVigente}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Sis. Evaluaci&oacute;n</td>
                    <td>
                        <h:selectOneMenu id="apeSisEvas" value="#{managerCLTallerAperturado.mpSisevaId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLTallerAperturado.mpSisEvaluaciones}"/>
                            <a4j:support event="onchange" reRender="c_detalles"
                                         actionListener="#{managerCLTallerAperturado.cambioSisEvaluacion}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td class="tdLabel">Personalizar</td>
                    <td>
                        <h:selectBooleanCheckbox value="#{managerCLTallerAperturado.personalizado}" id="personal">
                            <a4j:support event="onclick" actionListener="#{managerCLTallerAperturado.cambiarEfecto}"
                                         oncomplete="#{managerCLTallerAperturado.efecto}"
                                         reRender="sisevaluacion, c_detalles, c_codigo, c_nombre, c_peso, c_semana, c_orden"/>
                        </h:selectBooleanCheckbox>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:effect name="hideDiv" for="contentDiv" type="Fade"/>
                        <rich:effect name="showDiv" for="contentDiv" type="Appear"/>
                        <div id="contentDiv">
                            <rich:panel id="sisevaluacion">
                                <table width="100%" cellpadding="0" cellspacing="0" border="0">
                                    <tr>
                                        <td>
                                            <table>
                                                <tr class="tdLabel">
                                                    <td width="20%">Promedio</td>
                                                    <td width="20%">C&oacute;digo</td>
                                                    <td width="20%">Nombre</td>
                                                    <td width="20%">Peso</td>
                                                    <td width="20%">Semana</td>
                                                    <td width="20%">Orden</td>
                                                    <td width="20%"></td>
                                                </tr>
                                                <tr>
                                                    <td width="20%">
                                                        <h:selectOneMenu
                                                                value="#{managerCLTallerAperturado.sep.detalle}"
                                                                id="c_detalles" style="width: 150px;">
                                                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                                            <f:selectItems
                                                                    value="#{managerCLTallerAperturado.sep.detalles}"/>
                                                        </h:selectOneMenu>
                                                    </td>
                                                    <td width="20%">
                                                        <h:inputText id="c_codigo"
                                                                     value="#{managerCLTallerAperturado.sep.codigo}"/>
                                                    </td>
                                                    <td width="20%">
                                                        <h:inputText id="c_nombre"
                                                                     value="#{managerCLTallerAperturado.sep.nombre}"/>
                                                    </td>
                                                    <td width="20%">
                                                        <rich:inputNumberSpinner id="c_peso"
                                                                                 value="#{managerCLTallerAperturado.sep.peso}"
                                                                                 inputStyle="width: 20px;"/>
                                                    </td>
                                                    <td width="10%">
                                                        <h:inputText id="c_semana"
                                                                     value="#{managerCLTallerAperturado.sep.exaSemana}"
                                                                     style="width: 50px;"/>
                                                    </td>
                                                    <td width="10%">
                                                        <rich:inputNumberSpinner id="c_orden"
                                                                                 value="#{managerCLTallerAperturado.sep.orden}"
                                                                                 inputStyle="width: 20px;"/>
                                                    </td>
                                                    <td width="20%">
                                                        <f:param id="p_talape_id"
                                                                 value="#{managerCLTallerAperturado.subTalapeId}"/>
                                                        <a4j:commandButton image="/Imagenes/actions/edit_add.png"
                                                                           title="Agregar"
                                                                           reRender="tablaSecundaria, s_prom,c_detalles, c_codigo, c_nombre, c_peso, c_semana, c_orden"
                                                                           oncomplete="#{managerCLTallerAperturado.oncomplete}"
                                                                           actionListener="#{managerCLTallerAperturado.insertarSisEvaPer}"/>
                                                    </td>

                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="7">
                                            <hr size="1" width="100%">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="7" width="100%">
                                            <rich:dataTable id="tablaSecundaria" rows="6"
                                                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                                            cellpadding="0" cellspacing="0"
                                                            value="#{managerCLTallerAperturado.mpnSisEvaPer}"
                                                            width="100%" border="0" var="sisevaper">

                                                <rich:column style="text-align: center;">
                                                    <f:facet name="header">
                                                        <h:outputText value="Id"/>
                                                    </f:facet>
                                                    <h:outputText value="#{sisevaper.id}"/>
                                                </rich:column>

                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="Promedio"/>
                                                    </f:facet>
                                                    <h:outputText value="#{sisevaper.detallen}"
                                                                  rendered="#{sisevaper.view}"/>
                                                    <h:selectOneMenu value="#{sisevaper.detalle}" id="s_prom"
                                                                     rendered="#{sisevaper.editable}"
                                                                     style="width: 150px;">
                                                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                                        <f:selectItems value="#{sisevaper.detalles}"/>
                                                    </h:selectOneMenu>
                                                </rich:column>

                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="C&oacute;digo" escape="false"/>
                                                    </f:facet>
                                                    <h:outputText value="#{sisevaper.codigo}"
                                                                  rendered="#{sisevaper.view}"/>
                                                    <h:inputText value="#{sisevaper.codigo}"
                                                                 rendered="#{sisevaper.editable}" style="width: 40px;"/>
                                                </rich:column>

                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="Nombre"/>
                                                    </f:facet>
                                                    <h:outputText value="#{sisevaper.nombre}"
                                                                  rendered="#{sisevaper.view}"/>
                                                    <h:inputText value="#{sisevaper.nombre}"
                                                                 rendered="#{sisevaper.editable}"/>
                                                </rich:column>

                                                <rich:column style="text-align: center;">
                                                    <f:facet name="header">
                                                        <h:outputText value="Peso"/>
                                                    </f:facet>
                                                    <h:outputText value="#{sisevaper.peso}" style="width: 20px;"
                                                                  rendered="#{sisevaper.view}"/>
                                                    <h:inputText value="#{sisevaper.peso}" style="width: 20px;"
                                                                 rendered="#{sisevaper.editable}"/>
                                                </rich:column>

                                                <rich:column style="text-align: center;">
                                                    <f:facet name="header">
                                                        <h:outputText value="Semana"/>
                                                    </f:facet>
                                                    <h:outputText value="#{sisevaper.exaSemana}" style="width: 20px;"
                                                                  rendered="#{sisevaper.view}"/>
                                                    <h:inputText value="#{sisevaper.exaSemana}" style="width: 20px;"
                                                                 rendered="#{sisevaper.editable}"/>
                                                </rich:column>

                                                <rich:column style="text-align: center;">
                                                    <f:facet name="header">
                                                        <h:outputText value="Orden"/>
                                                    </f:facet>
                                                    <h:outputText value="#{sisevaper.orden}" style="width: 20px;"
                                                                  rendered="#{sisevaper.view}"/>
                                                    <h:inputText value="#{sisevaper.orden}" style="width: 20px;"
                                                                 rendered="#{sisevaper.editable}"/>
                                                </rich:column>

                                                <rich:column style="text-align: center;">
                                                    <f:facet name="header">
                                                        <h:outputText value="Editar"/>
                                                    </f:facet>
                                                    <a4j:commandButton actionListener="#{sisevaper.edit}"
                                                                       image="/Imagenes/actions/klipper_dock.png"
                                                                       title="Editar"
                                                                       rendered="#{sisevaper.view}"
                                                                       reRender="tablaSecundaria,s_prom"/>
                                                    <a4j:commandButton actionListener="#{sisevaper.aceptar}"
                                                                       image="/Imagenes/actions/fileexport.png"
                                                                       title="Aceptar"
                                                                       rendered="#{sisevaper.editable}"
                                                                       reRender="tablaSecundaria"/>
                                                    <a4j:commandButton actionListener="#{sisevaper.cancelar}"
                                                                       image="/Imagenes/actions/fileclose.png"
                                                                       title="Cancelar"
                                                                       rendered="#{sisevaper.editable}"
                                                                       reRender="tablaSecundaria"/>
                                                </rich:column>

                                                <rich:column style="text-align: center;">
                                                    <f:facet name="header">
                                                        <h:outputText value="Quitar"/>
                                                    </f:facet>
                                                    <f:param value="#{sisevaper.id}" id="id_det"/>
                                                    <f:param value="#{sisevaper.pos}" id="pos_det"/>
                                                    <a4j:commandButton image="/Imagenes/actions/edit_remove.png"
                                                                       actionListener="#{managerCLTallerAperturado.quitarSisEvaPer}"
                                                                       title="Quitar" reRender="tablaSecundaria"/>
                                                </rich:column>
                                            </rich:dataTable>
                                        </td>
                                    </tr>
                                </table>
                            </rich:panel>
                        </div>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
