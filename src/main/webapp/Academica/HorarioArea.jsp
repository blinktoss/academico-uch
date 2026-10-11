<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Horarios Area</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form>
            <div class="row">
                <div class="col title-label">MANTENIMIENTO DE HORARIOS &Aacute;REA</div>
                <div class="col" style="text-align: right">
                    <a4j:status>
                        <f:facet name="start">
                            <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                        </f:facet>
                    </a4j:status>
                    <rich:spacer width="8px"/>
                    <a4j:commandButton image="/Imagenes/actions/viewmag.png" type="button" title="Buscar"
                                       actionListener="#{managerHorarioArea.seleccionar}"
                                       oncomplete="#{managerHorarioArea.oncomplete}"
                                       reRender="barra,tablaMaster"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin-bottom: 10px"/>
            <div class="row">
                <div class="col uch-label">&Aacute;rea:</div>
                <div class="col uch-input">
                    <h:selectOneMenu value="#{managerHorarioArea.paramAreaId}">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                        <f:selectItems value="#{managerHorarioArea.comboAreas}"/>
                    </h:selectOneMenu>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin: 10px 0"/>
        </h:form>
        <h:form>
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="15" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerHorarioArea.horariosAreaList}" var="ha">

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Orden"/>
                    </f:facet>
                    <h:outputText value="#{ha.rowOrder}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="&Aacute;rea" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{ha.rowAreaCode}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="D&iacute;a" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{ha.rowDayLabel}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Hora"/>
                    </f:facet>
                    <h:outputText value="#{ha.rowHour}"/>
                </rich:column>

                <f:facet name="footer">
                    <h:outputText value="Total #{managerHorarioArea.horariosAreaList.size()}"/>
                </f:facet>
            </rich:dataTable>
        </h:form>
    </rich:panel>
</f:view>
</body>
</html>
