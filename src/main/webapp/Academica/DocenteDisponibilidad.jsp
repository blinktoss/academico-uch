<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Disponibilidad Docente</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <a4j:form>
            <div class="row">
                <div class="col title-label">MANTENIMIENTO DE DISPONIBILIDAD DOCENTE</div>
                <div class="col" style="text-align: right">
                    <a4j:status>
                        <f:facet name="start">
                            <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                        </f:facet>
                    </a4j:status>
                    <rich:spacer width="8px"/>
                    <a4j:commandButton image="/Imagenes/actions/viewmag.png" type="button" title="Buscar"
                                       actionListener="#{managerDocenteDisponibilidad.seleccionar}"
                                       oncomplete="#{managerDocenteDisponibilidad.oncomplete}"
                                       reRender="barra,tablaMaster"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin-bottom: 10px"/>
            <div class="row">
                <div class="col uch-label">C&oacute;digo:</div>
                <div class="col uch-input">
                    <h:inputText value="#{managerDocenteDisponibilidad.paramCode}"/>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Nombres:</div>
                <div class="col uch-input">
                    <h:inputText value="#{managerDocenteDisponibilidad.paramName}"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin: 10px 0"/>
        </a4j:form>
        <a4j:form>
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="15" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerDocenteDisponibilidad.availabilities}" var="av">

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{av.rowTeacherCode}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombres"/>
                    </f:facet>
                    <h:outputText value="#{av.rowTeacherNames}"/>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Disponibilidades"/>
                    </f:facet>
                    <h:outputText value="#{av.rowTeacherAvailability}"/>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Detalle"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/curso.png" title="Ver Disponibilidad"
                                       actionListener="#{managerDocenteDisponibilidad.openModalForAvailabilities}"
                                       oncomplete="Richfaces.showModalPanel('mpAvailabilities', {top:120})"
                                       reRender="formAvail">
                        <f:attribute name="teacher_id" value="#{av.rowTeacherId}"/>
                    </a4j:commandButton>
                </rich:column>
            </rich:dataTable>
        </a4j:form>
    </rich:panel>

    <rich:modalPanel id="mpAvailabilities" autosized="true" minWidth="600" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Disponibilidades"/>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpAvailabilities')" title="Cerrar"/>
            </h:panelGroup>
        </f:facet>
        <a4j:form id="formAvail">
            <rich:dataTable cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerDocenteDisponibilidad.details}" var="det">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="D&iacute;a" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{det.mpAvailableDay}"/>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Hora"/>
                    </f:facet>
                    <h:outputText value="#{det.mpAvailableHour}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Observaci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{det.mpAvailableObservation}"/>
                </rich:column>
            </rich:dataTable>
        </a4j:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
