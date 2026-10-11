<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Saldos</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form>
            <div class="row">
                <div class="col title-label">MANTENIMIENTO DE SALDOS</div>
                <div class="col" style="text-align: right">
                    <a4j:status>
                        <f:facet name="start">
                            <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                        </f:facet>
                    </a4j:status>
                    <rich:spacer width="8px"/>
                    <a4j:commandButton image="/Imagenes/actions/viewmag.png" type="button" title="Buscar"
                                       actionListener="#{managerSaldo.seleccionar}"
                                       oncomplete="#{managerSaldo.oncomplete}"
                                       reRender="barra,tablaMaster"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin-bottom: 10px"/>
            <div class="row">
                <div class="col uch-label">C&oacute;digo:</div>
                <div class="col uch-input">
                    <h:inputText value="#{managerSaldo.paramStudentCode}"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin: 10px 0"/>
        </h:form>
        <a4j:form>
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 90%"/>
            <rich:dataTable id="tablaMaster" cellpadding="0" cellspacing="0" border="0" width="80%" align="center"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerSaldo.saldoList}" var="s" rows="15">

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{s.rowCodigo}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombres"/>
                    </f:facet>
                    <h:outputText value="#{s.rowLargeName}"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Fecha de Registro"/>
                    </f:facet>
                    <h:outputText value="#{s.rowRecordDate}"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Monto"/>
                    </f:facet>
                    <h:outputText value="#{s.rowMonto}">
                        <f:convertNumber pattern="0.00"/>
                    </h:outputText>
                </rich:column>
            </rich:dataTable>
        </a4j:form>
    </rich:panel>
</f:view>
</body>
