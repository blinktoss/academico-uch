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
            <table style="font-size:13px; font-family:Verdana,sans-serif; width: 100%">
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
                                           style="border-width: 0"
                                           actionListener="#{managerEdicionEstructPagoBloque.buscar}"
                                           reRender="fFechaPag, fFechaPro,fMon, mFechaPag, mFechaPro,mMon, lEsp, lSem, lSeming"
                                           oncomplete="#{managerEdicionEstructPagoBloque.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <hr width="100%" size="1"/>
                </tr>
                <tr>
                    <td width="26%" colspan="2"></td>
                    <td width="14%" colspan="2"></td>
                    <td width="60%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Semestre Destino:</td>
                    <td width="27%">
                        <h:outputLabel value="#{managerEdicionEstructPagoBloque.paramSemDestino}"
                                       style="width : 180px;"/>
                    </td>
                    <td width="13%">Especialidad:</td>
                    <td width="27%">
                        <h:selectOneMenu value="#{managerEdicionEstructPagoBloque.paramEsp}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerEdicionEstructPagoBloque.paramEspecialidades}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Semestre Ingreso:</td>
                    <td width="27%">
                        <h:selectOneMenu value="#{managerEdicionEstructPagoBloque.paramSemIngId}"
                                         style="width : 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerEdicionEstructPagoBloque.paramSemIngreso}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%" style="vertical-align: middle;">Filtros:</td>
                    <td width="27%">
                        <rich:panel>
                            <h:selectManyCheckbox value="#{managerEdicionEstructPagoBloque.paramFiltro}">
                                <f:selectItems value="#{managerEdicionEstructPagoBloque.paramFiltros}"/>
                            </h:selectManyCheckbox>
                        </rich:panel>
                    </td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%" style="vertical-align: middle;">Modificar:</td>
                    <td width="27%">
                        <rich:panel>
                            <h:selectManyCheckbox value="#{managerEdicionEstructPagoBloque.paramModif}">
                                <f:selectItems value="#{managerEdicionEstructPagoBloque.paramToModify}"/>
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
                <tr align="center">
                    <td colspan="4">
                        <h:outputText id="rstl" escape="false"
                                      style="width: 100%; height: 50px; font-size: 12px; font-weight: bold;"
                                      value="#{managerEdicionEstructPagoBloque.resultado}"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpBloque" minWidth="700"
                     autosized="true"
                     zindex="2000" onshow="showDiv();">
        <rich:effect name="hideDiv" for="contentDivBloque" type="Fade"/>
        <rich:effect name="showDiv" for="contentDivBloque" type="Appear"/>
        <div id="contentDivBloque">
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
                                    actionListener="#{managerEdicionEstructPagoBloque.validarActualizacion}"
                                    image="/Imagenes/actions/filesave.png"
                                    title="Guardar"
                                    oncomplete="#{managerEdicionEstructPagoBloque.oncomplete}"
                                    reRender="rstl"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <rich:separator align="center" height="3px"/>
                        </td>
                    </tr>
                    <tr style="height: 20px;">
                        <td colspan="2"><strong>Especialidad</strong> -
                            <h:outputLabel id="lEsp"
                                           value="#{managerEdicionEstructPagoBloque.especialidad}"/>
                        </td>
                    </tr>
                    <tr style="height: 20px;">
                        <td><strong>Semestre destino</strong> -
                            <h:outputLabel id="lSem"
                                           value="#{managerEdicionEstructPagoBloque.paramSemDestino}"/>
                        </td>
                    </tr>
                    <tr style="height: 20px;">
                        <td><strong>Semestre ingreso</strong> -
                            <h:outputLabel id="lSeming"
                                           value="#{managerEdicionEstructPagoBloque.semestreIngreso}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <rich:panel header="Datos para buscar">
                                <h:panelGrid columns="2">
                                    <h:outputLabel value="Fecha de pago"/>
                                    <rich:calendar id="fFechaPag" datePattern="dd-MM-yyyy"
                                                   disabled="#{managerEdicionEstructPagoBloque.mpBusDisables[0]}"
                                                   value="#{managerEdicionEstructPagoBloque.mpBusFechaPago}"/>

                                    <h:outputLabel value="Fecha de prorroga"/>
                                    <rich:calendar id="fFechaPro" datePattern="dd-MM-yyyy"
                                                   disabled="#{managerEdicionEstructPagoBloque.mpBusDisables[1]}"
                                                   value="#{managerEdicionEstructPagoBloque.mpBusFechaProrroga}"/>

                                    <h:outputLabel value="Monto"/>
                                    <h:inputText id="fMon"
                                                 disabled="#{managerEdicionEstructPagoBloque.mpBusDisables[2]}"
                                                 value="#{managerEdicionEstructPagoBloque.mpBusMonto}"/>
                                </h:panelGrid>
                            </rich:panel>
                        </td>
                        <td>
                            <rich:panel header="Datos a modificar">
                                <h:panelGrid columns="2">
                                    <h:outputLabel value="Fecha de pago"/>
                                    <rich:calendar id="mFechaPag" datePattern="dd-MM-yyyy"
                                                   disabled="#{managerEdicionEstructPagoBloque.mpModDisable[0]}"
                                                   value="#{managerEdicionEstructPagoBloque.mpModFechaPago}"/>

                                    <h:outputLabel value="Fecha de prorroga"/>
                                    <rich:calendar id="mFechaPro" datePattern="dd-MM-yyyy"
                                                   disabled="#{managerEdicionEstructPagoBloque.mpModDisable[1]}"
                                                   value="#{managerEdicionEstructPagoBloque.mpModFechaProrroga}"/>

                                    <h:outputLabel value="Monto"/>
                                    <h:inputText id="mMon" disabled="#{managerEdicionEstructPagoBloque.mpModDisable[2]}"
                                                 value="#{managerEdicionEstructPagoBloque.mpModMonto}"/>
                                </h:panelGrid>
                            </rich:panel>
                        </td>
                    </tr>
                </table>
            </h:form>
        </div>
    </rich:modalPanel>
</f:view>
</body>
</html>
