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
    <title>Mantenimiento de actividades</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table width="100%" style="font-size:13px;">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE ACTIVIDADES</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                            <a4j:support event="onclick" onsubmit="hideDiv({duration:0.1});"
                                         oncomplete="Richfaces.showModalPanel('mpActividadNuevo',{width:600, top:50})"
                                         actionListener="#{managerActividad.nuevo}"
                                         reRender="formActividad,iTitulo,imgLoaded,iFecha,iFechaIni,iFechaFin,iImagen,iPublicado,iAlcance,iDescripcion"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%"></td>
                    <td align=right width="50%">
                        <a4j:commandButton type="button" id="buscar" value=""
                                           title="Buscar" reRender="tableData"
                                           actionListener="#{managerActividad.mostrarPorFecha}"
                                           oncomplete="#{managerActividad.oncomplete}"
                                           image="/Imagenes/actions/viewmag.png"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="3" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td width="20%" colspan="2"></td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr style="height: 24px;">
                    <td width="10%">Fecha:</td>
                    <td width="30%">
                        <rich:calendar datePattern="dd-MM-yyyy" showWeeksBar="false"
                                       value="#{managerActividad.paramDate}"/>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="3" lineType="double"/></td>
                </tr>
            </table>
        </h:form>
        <h:form id="tableData" style=" width : 100%;">
            <rich:datascroller id="scrollTableMaster" for="tablaMaster" align="right" maxPages="8"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" border="0" width="100%"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerActividad.listaActividades}" var="actividades">

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Fecha"/>
                    </f:facet>
                    <h:outputText value="#{actividades.rowFecha}">
                        <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy"/>
                    </h:outputText>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="T&iacute;tulo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{actividades.rowTitulo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Fecha inicio"/>
                    </f:facet>
                    <h:outputText value="#{actividades.rowFechaIni}">
                        <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy  'a' 'las'  hh:mm:ss a" locale="es"/>
                    </h:outputText>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Fecha fin"/>
                    </f:facet>
                    <h:outputText value="#{actividades.rowFechaFin}">
                        <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy  'a' 'las'  hh:mm:ss a" locale="es"/>
                    </h:outputText>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Publicado"/>
                    </f:facet>
                    <f:param id="p_id" value="#{actividades.rowId}"/>
                    <f:param id="p_est_pub" value="#{actividades.rowEstadoPublicado}"/>
                    <f:param id="p_fecha" value="#{actividades.rowFecha}"/>
                    <a4j:commandButton image="#{actividades.imagenPublicado}" title="#{actividades.title}"
                                       actionListener="#{managerActividad.setearParametros}"
                                       oncomplete="Richfaces.showModalPanel('mpPublicar',{top:200})"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Alcance"/>
                    </f:facet>
                    <h:outputText value="#{actividades.rowAlcance}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/editpaste.png"
                                       actionListener="#{managerActividad.seleccionarActividad}"
                                       oncomplete="Richfaces.showModalPanel('mpActividadNuevo',{top:80})"
                                       reRender="formActividad,iTitulo,imgLoaded,iFecha,iFechaIni,iFechaFin,iImagen,iPublicado,iAlcance,iDescripcion"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerActividad.eliminarActividad}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="mpActividadNuevo" autosized="true" zindex="2000" minWidth="550">
            <f:facet name="header">
                <h:outputText value="Registro de Actividades"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer" title="Cerrar"
                                onclick="Richfaces.hideModalPanel('mpActividadNuevo')"/>
            </f:facet>
            <a4j:form id="formActividad">
                <table width="100%">
                    <tr>
                        <td colspan="3" width="100%" align="right">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerActividad.manipularDatos}"
                                               reRender="tablaMaster, scrollTableMaster"
                                               oncomplete="#{managerActividad.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <rich:separator height="3" lineType="double"/>
                        </td>
                    </tr>
                    <tr>
                        <td>T&iacute;tulo</td>
                        <td width="65%">
                            <h:inputText id="iTitulo" value="#{managerActividad.rowTitulo}"/>
                        </td>
                        <td rowspan="5">
                            <rich:panel id="imgLoaded">
                                <div align="center">
                                    <h:outputText value="No hay imagen cargada"
                                                  rendered="#{managerActividad.mpImg==null}"/>
                                    <h:graphicImage value="/foto-actividad?time=#{managerActividad.timeStamp}"
                                                    style="width:100px; height:100px;"
                                                    rendered="#{managerActividad.mpImg!=null}"/>
                                    <br/><rich:spacer height="5px"/>
                                    <a4j:commandButton actionListener="#{managerActividad.limpiarImagenActividad}"
                                                       reRender="imgLoaded, formActividad"
                                                       value="Borrar imagen"
                                                       rendered="#{managerActividad.mpImg!=null}"/>
                                </div>
                            </rich:panel>
                        </td>
                    </tr>
                    <tr>
                        <td><h:outputLabel value="Fecha"/></td>
                        <td><rich:calendar id="iFecha" datePattern="dd-MM-yyyy"
                                           value="#{managerActividad.rowFecha}"/></td>
                    </tr>
                    <tr>
                        <td><h:outputLabel value="Fecha inicio"/></td>
                        <td><rich:calendar id="iFechaIni" datePattern="dd-MM-yyyy hh:mm a"
                                           showApplyButton="true"
                                           value="#{managerActividad.rowFechaIni}"/></td>
                    </tr>
                    <tr>
                        <td><h:outputLabel value="Fecha fin"/></td>
                        <td><rich:calendar id="iFechaFin" datePattern="dd-MM-yyyy hh:mm a"
                                           showApplyButton="true"
                                           value="#{managerActividad.rowFechaFin}"/></td>
                    </tr>
                    <tr>
                        <td>Tipo</td>
                        <td>
                            <h:selectOneMenu id="iTipo" value="#{managerActividad.mpTipo}">
                                <f:selectItems value="#{managerActividad.itemComboTipo}"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                    <tr>
                        <td>Im&aacute;gen</td>
                        <td colspan="2">
                            <rich:fileUpload id="iImagen" maxFilesQuantity="1"
                                             fileUploadListener="#{managerActividad.cargaImagen}"
                                             immediateUpload="false"
                                             acceptedTypes="png, jpg, jpeg, gif">
                                <a4j:support event="onuploadcomplete" reRender="imgLoaded"/>
                            </rich:fileUpload>
                        </td>
                    </tr>
                    <tr>
                        <td>Publicado</td>
                        <td>
                            <h:selectBooleanCheckbox id="iPublicado"
                                                     value="#{managerActividad.rowPublicado}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Alcance</td>
                        <td colspan="2">
                            <h:selectManyCheckbox id="iAlcance"
                                                  value="#{managerActividad.mpAlcances}">
                                <f:selectItems value="#{managerActividad.itemCheckAlcance}"/>
                            </h:selectManyCheckbox>
                        </td>
                    </tr>
                    <tr>
                        <td>Descripci&oacute;n</td>
                        <td colspan="2">
                            <h:inputTextarea id="iDescripcion" cols="60" rows="5"
                                             value="#{managerActividad.mpDescripcion}"/>
                        </td>
                    </tr>
                </table>
            </a4j:form>
        </rich:modalPanel>

        <rich:modalPanel id="mpPublicar" autosized="true" width="300"
                         zindex="2000" onshow="showDiv();">
            <rich:effect name="hideDiv" for="contentDivPublicar" type="Fade"/>
            <rich:effect name="showDiv" for="contentDivPublicar" type="Appear"/>
            <div id="contentDivPublicar">
                <f:facet name="header">
                    <h:outputText value="Publicar la actividad"/>
                </f:facet>
                <f:facet name="controls">
                    <h:graphicImage value="/Imagenes/actions/stop.png"
                                    style="cursor:pointer"
                                    onclick="Richfaces.hideModalPanel('mpPublicar')"
                                    title="Cerrar"/>
                </f:facet>
                <h:form id="formPublicar" style="text-align: center">
                    <h:outputLabel>
                        <h2>&iquest;Esta seguro(a) de realizar esta acci&oacute;n?</h2><br>
                    </h:outputLabel>
                    <h:commandButton value="Aceptar" actionListener="#{managerActividad.publicarActividad}"/>
                    <rich:spacer width="20px"/>
                    <h:commandButton value="Cancelar" onclick="Richfaces.hideModalPanel('mpPublicar')"/>
                </h:form>
            </div>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>
