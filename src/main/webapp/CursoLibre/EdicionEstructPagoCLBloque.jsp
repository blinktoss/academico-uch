<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Edici&oacute;n de Estructura de Pagos por Bloques</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="formInput">
            <table style="width: 100%">
                <tr>
                    <td width="26%" colspan="2"><strong>EDICI&Oacute;N DE LAS ESTRUCTURAS DE PAGO POR BLOQUES</strong>
                    </td>
                    <td width="13%"></td>
                    <td width="51%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="../Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status></td>
                    <td align=right width="10%">
                        <a4j:commandButton type="button" id="buscar" value="" title="Buscar"
                                           image="/Imagenes/actions/viewmag.png"
                                           actionListener="#{managerCLEditEstPagBloq.buscar}"
                                           reRender="fFechaPag, fFechaPro,fMon, mFechaPag, mFechaPro,mMon, lTalape, lSec"
                                           oncomplete="#{managerCLEditEstPagBloq.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="26%" colspan="2"></td>
                    <td width="14%" colspan="2"></td>
                    <td width="60%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Taller Aperturado:</td>
                    <td width="27%">
                        <h:selectOneMenu value="#{managerCLEditEstPagBloq.paramTalapeId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLEditEstPagBloq.paramTalapes}"/>
                            <a4j:support event="onchange" reRender="i_seccion"
                                         actionListener="#{managerCLEditEstPagBloq.parametroSeccion}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Secci&oacute;n:</td>
                    <td width="27%">
                        <h:selectOneMenu id="i_seccion" value="#{managerCLEditEstPagBloq.paramSecId}"
                                         style="width : 220px;">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCLEditEstPagBloq.paramSecciones}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%" style="vertical-align: top;">Filtros:</td>
                    <td width="27%">
                        <rich:panel>
                            <h:selectManyCheckbox value="#{managerCLEditEstPagBloq.paramFiltro}">
                                <f:selectItems value="#{managerCLEditEstPagBloq.paramFiltros}"/>
                            </h:selectManyCheckbox>
                        </rich:panel>
                    </td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%" style="vertical-align: top;">Modificar:</td>
                    <td width="27%">
                        <rich:panel>
                            <h:selectManyCheckbox value="#{managerCLEditEstPagBloq.paramModif}">
                                <f:selectItems value="#{managerCLEditEstPagBloq.paramToModify}"/>
                            </h:selectManyCheckbox>
                        </rich:panel>
                    </td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr style="height: 20px; text-align: center;">
                    <td colspan="6">
                        <h:outputText id="rstl" style="width: 100%; height: 50px; font-size: 12px; font-weight: bold;"
                                      escape="false" value="#{managerCLEditEstPagBloq.resultado}"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpBloque" minWidth="700"
                     autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Ingreso de datos a actualizar"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png"
                            style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpBloque')"
                            title="Cerrar"/>
        </f:facet>
        <h:form>
            <table width="100%">
                <tr>
                    <td width="50%"></td>
                    <td align="right">
                        <a4j:commandButton
                                actionListener="#{managerCLEditEstPagBloq.validarActualizacion}"
                                image="/Imagenes/actions/filesave.png"
                                title="Guardar"
                                oncomplete="#{managerCLEditEstPagBloq.oncomplete}"
                                reRender="rstl"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="4" width="100%">
                        <hr width="100%" size="1"/>
                    </td>
                </tr>
                <tr style="height : 20px;">
                    <td><b>Taller aperturado</b> -
                        <h:outputLabel id="lTalape"
                                       value="#{managerCLEditEstPagBloq.tallerAperturado}"/>
                    </td>
                </tr>
                <tr style="height : 20px;">
                    <td><b>Secci&oacute;n</b> -
                        <h:outputLabel id="lSec"
                                       value="#{managerCLEditEstPagBloq.seccion}"/>
                    </td>
                </tr>
                <tr style="height : 10px;"></tr>
                <tr>
                    <td>
                        <rich:panel header="Datos para buscar">
                            <h:panelGrid columns="2">
                                <h:outputLabel value="Fecha de pago"/>
                                <rich:calendar id="fFechaPag" datePattern="dd-MM-yyyy"
                                               disabled="#{managerCLEditEstPagBloq.mpBusDisables[0]}"
                                               value="#{managerCLEditEstPagBloq.mpBusFechaPago}"/>

                                <h:outputLabel value="Fecha de prorroga"/>
                                <rich:calendar id="fFechaPro" datePattern="dd-MM-yyyy"
                                               disabled="#{managerCLEditEstPagBloq.mpBusDisables[1]}"
                                               value="#{managerCLEditEstPagBloq.mpBusFechaProrroga}"/>

                                <h:outputLabel value="Monto"/>
                                <h:inputText id="fMon" disabled="#{managerCLEditEstPagBloq.mpBusDisables[2]}"
                                             value="#{managerCLEditEstPagBloq.mpBusMonto}"/>
                            </h:panelGrid>
                        </rich:panel>
                    </td>
                    <td>
                        <rich:panel header="Datos a modificar">
                            <h:panelGrid columns="2">
                                <h:outputLabel value="Fecha de pago"/>
                                <rich:calendar id="mFechaPag" datePattern="dd-MM-yyyy"
                                               disabled="#{managerCLEditEstPagBloq.mpModDisable[0]}"
                                               value="#{managerCLEditEstPagBloq.mpModFechaPago}"/>

                                <h:outputLabel value="Fecha de prorroga"/>
                                <rich:calendar id="mFechaPro" datePattern="dd-MM-yyyy"
                                               disabled="#{managerCLEditEstPagBloq.mpModDisable[1]}"
                                               value="#{managerCLEditEstPagBloq.mpModFechaProrroga}"/>

                                <h:outputLabel value="Monto"/>
                                <h:inputText id="mMon" disabled="#{managerCLEditEstPagBloq.mpModDisable[2]}"
                                             value="#{managerCLEditEstPagBloq.mpModMonto}"/>
                            </h:panelGrid>
                        </rich:panel>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
