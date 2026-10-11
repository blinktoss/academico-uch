<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Turnos</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width:100%" cellspacing="0" border="0" cellpadding="0">
                <tr>
                    <td width="20%" colspan="2"><strong>LISTADO TURNOS</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:region id="Region">
                            <h:graphicImage value="/Imagenes/actions/filenew.png"
                                            style="border-width: 0;cursor: pointer;">
                                <a4j:support oncomplete="Richfaces.showModalPanel('mp',{width:450, top:130})"
                                             event="onclick"
                                             action="#{managerTurno.nuevo}"
                                             reRender="iId,iCodigo,iNombre,iPeriodos,iInicio_h,iInicio_m,iInicio_meridiano,iVigente,iSemestre,iTiempo"/>
                            </h:graphicImage>
                        </a4j:region>
                    </td>
                    <td width="30%" align="right"><h:graphicImage value="/Imagenes/actions/fileprint.png"
                                                                  title="Imprimir Listado"/></td>
                    <td align=right width="50%">
                        <h:commandButton type="button" id="buscar" action="#{managerTurno.seleccionar}"
                                         image="/Imagenes/actions/viewmag.png" title="Buscar"/>
                    </td>
                </tr>
                <tr class="tr-label">
                    <td colspan="7" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr class="tr-label">
                    <td width="10%">C&oacute;digo:</td>
                    <td width="30%">
                        <h:inputText id="txtCodigo" value="#{managerTurno.paramCodigo}" style="width : 180px;"
                                     maxlength="10"/>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr></tr>
                <tr class="tr-label">
                    <td width="10%">Nombre:</td>
                    <td width="30%">
                        <h:inputText id="txtNombre" value="#{managerTurno.paramNombre}" style="width : 180px;"
                                     maxlength="50"/>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr></tr>
                <tr class="tr-label">
                    <td width="10%">Semestre:</td>
                    <td width="30%">
                        <h:selectOneMenu value="#{managerTurno.paramSemestre}" style="width : 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerTurno.paramComboSemestres}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr class="tr-label">
                    <td colspan="7" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form style="width: 100%;" id="form4">
            <rich:datascroller id="scroller" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerTurno.tablaTurno}" var="turno">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{turno.rowCodigo}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>
                    <h:outputText value="#{turno.rowNombre}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Periodo"/>
                    </f:facet>
                    <h:outputText value="#{turno.rowPeriodo}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Inicio"/>
                    </f:facet>
                    <h:outputText value="#{turno.rowInicio}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Vigente"/>
                    </f:facet>
                    <h:graphicImage value="#{turno.rowVigenteImg}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Semestre"/>
                    </f:facet>
                    <h:outputText value="#{turno.rowSemestreDes}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Tiempo"/>
                    </f:facet>
                    <h:outputText value="#{turno.rowTiempo}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:region id="ProvProc">
                        <h:graphicImage value="/Imagenes/actions/editpaste.png"
                                        style="border-width: 0; cursor: pointer">
                            <a4j:support oncomplete="Richfaces.showModalPanel('mp',{width:450, top:130})"
                                         event="onclick"
                                         actionListener="#{managerTurno.editar}"
                                         reRender="iId,iCodigo,iNombre,iPeriodos,iInicio_h,iInicio_m,iInicio_meridiano,iVigente,iSemestre,iTiempo"/>
                        </h:graphicImage>
                    </a4j:region>
                    <f:param id="id_s" value="#{turno.rowId}"/>
                    <f:param id="codigo_s" value="#{turno.rowCodigo}"/>
                    <f:param id="nombre_s" value="#{turno.rowNombre}"/>
                    <f:param id="periodos_s" value="#{turno.rowPeriodo}"/>
                    <f:param id="inicio_s" value="#{turno.rowInicio}"/>
                    <f:param id="vigente_s" value="#{turno.rowVigenteValue}"/>
                    <f:param id="semestre_s" value="#{turno.rowSemestreValue}"/>
                    <f:param id="tiempo_s" value="#{turno.rowTiempo}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Ver"/>
                    </f:facet>
                    <h:graphicImage value="/Imagenes/actions/color_line.png" style="border-width: 0; cursor: pointer">
                        <a4j:support oncomplete="Richfaces.showModalPanel('mp1',{width:550, top:130})" event="onclick"
                                     actionListener="#{managerTurno.editar}"
                                     reRender="iCodigo1,iNombre1,iPeriodos1,iInicio_h1,iInicio_m1,iInicio_meridiano1,iVigente1,iSemestre1,iTiempo1,tablaSecundaria,scroller2"/>
                    </h:graphicImage>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerTurno.eliminar}"
                                     action="#{managerTurno.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="mp" autosized="true" minWidth="450" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Cursos"/>
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
                                               actionListener="#{managerTurno.insertar}"
                                               action="#{managerTurno.seleccionar}"
                                               reRender="tablaMaster"
                                               oncomplete="alert('Se guardo el registro correctamente');Richfaces.hideModalPanel('mp')"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <rich:separator height="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">C&oacute;digo:</td>
                        <td width="70%">
                            <h:inputHidden id="iId" value="#{managerTurno.mpId}"/>
                            <h:inputText label="Codigo" id="iCodigo" style="width: 180px;"
                                         value="#{managerTurno.mpCodigo}" required="true" maxlength="10"/>
                            <rich:message for="iCodigo">
                                <f:facet name="passedMarker">
                                    <h:graphicImage value="/Imagenes/actions/ok.png"/>
                                </f:facet>
                                <f:facet name="errorMarker">
                                    <h:graphicImage value="/Imagenes/actions/no.png"/>
                                </f:facet>
                            </rich:message>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">Nombre:</td>
                        <td width="70%">
                            <h:inputText id="iNombre" label="Nombre" value="#{managerTurno.mpNombre}"
                                         style="width: 180px;" required="true"/>
                            <rich:message for="iNombre">
                                <f:facet name="passedMarker">
                                    <h:graphicImage value="/Imagenes/actions/ok.png"/>
                                </f:facet>
                                <f:facet name="errorMarker">
                                    <h:graphicImage value="/Imagenes/actions/no.png"/>
                                </f:facet>
                            </rich:message>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Nro de Periodos:</td>
                        <td width="70%">
                            <rich:inputNumberSpinner id="iPeriodos" value="#{managerTurno.mpPeriodo}"
                                                     style="width: 20px;">
                            </rich:inputNumberSpinner>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Inicio:</td>
                        <td width="70%">
                            <h:inputText id="iInicio_h" value="#{managerTurno.mpInicioHor}" style="width: 30px;"/>
                            <h:outputLabel value=":"/>
                            <h:inputText id="iInicio_m" value="#{managerTurno.mpInicioMin}" style="width: 30px;"/>
                            <h:selectOneMenu id="iInicio_meridiano" value="#{managerTurno.mpInicioMeridiano}">
                                <f:selectItems value="#{managerTurno.mpComboMeridianos}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Vigente:</td>
                        <td width="70%">
                            <h:selectBooleanCheckbox id="iVigente" value="#{managerTurno.mpVigente}"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Semestre:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iSemestre" value="#{managerTurno.mpSemestre}" style="width: 180px;">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerTurno.mpComboSemestres}"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">Tiempo:</td>
                        <td width="70%">
                            <h:inputText id="iTiempo" label="Tiempo" value="#{managerTurno.mpTiempo}"
                                         style="width: 100px;" required="true"/>
                            <h:outputLabel value=" min" for="iTiempo"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <rich:separator height="1"/>
                        </td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mp1" autosized="true" minWidth="400" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Detalle de Turno"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp1')" title="Cerrar"/>
            </f:facet>
            <h:form id="form2">
                <table style="width: 100%">
                    <tr>
                        <td width="30%">C&oacute;digo:</td>
                        <td width="70%">
                            <h:outputText id="iCodigo1" style="width: 180px;" value="#{managerTurno.mpCodigo}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">Nombre:</td>
                        <td width="70%">
                            <h:outputText id="iNombre1" value="#{managerTurno.mpNombre}" style="width: 180px;"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Nro de Periodos:</td>
                        <td width="70%">
                            <h:outputText id="iPeriodos1" value="#{managerTurno.mpPeriodo}" style="width: 20px;"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Inicio:</td>
                        <td width="70%">
                            <h:outputText id="iInicio_h1" value="#{managerTurno.mpInicioHor}"/>:
                            <h:outputText id="iInicio_m1" value="#{managerTurno.mpInicioMin}"/>
                            <h:outputText id="iInicio_meridiano1" value="#{managerTurno.mpInicioMeridiano}"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Vigente:</td>
                        <td width="70%">
                            <h:selectBooleanCheckbox id="iVigente1" value="#{managerTurno.mpVigente}" disabled="true"/>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Semestre:</td>
                        <td width="70%">
                            <h:outputText id="iSemestre1" value="#{managerTurno.rowSemestreDes}" style="width: 180px;"/>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">Tiempo:</td>
                        <td width="70%">
                            <h:outputText id="iTiempo1" value="#{managerTurno.mpTiempo}" style="width: 100px;"/>
                            <h:outputLabel value=" min"/>
                        </td>
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
                            <rich:datascroller id="scroller2" align="right" for="tablaSecundaria" maxPages="8"
                                               style=" width : 100%;"/>
                            <rich:dataTable id="tablaSecundaria" rows="10" width="100%" border="0" cellpadding="0"
                                            cellspacing="0"
                                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                            value="#{managerTurno.tablaTurnoDetalle}" var="turno_detalle">
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Hora Inicio"/>
                                    </f:facet>
                                    <h:outputText value="#{turno_detalle.mpDetInicio}">
                                        <f:convertDateTime pattern="HH:mm"/>
                                    </h:outputText>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Hora Fin"/>
                                    </f:facet>
                                    <h:outputText value="#{turno_detalle.mpDetFin}">
                                        <f:convertDateTime pattern="HH:mm"/>
                                    </h:outputText>
                                </rich:column>

                                <f:facet name="footer">
                                    <h:outputText value="Total #{managerTurno.tablaTurnoDetalle.size()}"/>
                                </f:facet>
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
