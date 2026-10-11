<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Bienvenido</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <div class="row">
            <div class="col title-label">TABLERO DE CONTROL</div>
        </div>
        <rich:separator height="3" lineType="double" style="margin-bottom: 10px"/>
        <div class="kpi-grid">
            <div class="kpi-card">
                <div class="kpi-label">Semestre actual</div>
                <div class="kpi-value"><h:outputText value="#{managerDashboard.currentSemesterName}"/></div>
            </div>
            <div class="kpi-card">
                <div class="kpi-label">Alumnos registrados</div>
                <div class="kpi-value"><h:outputText value="#{managerDashboard.registeredStudentsLabel}"/></div>
            </div>
            <div class="kpi-card">
                <div class="kpi-label">Matriculados (semestre actual)</div>
                <div class="kpi-value"><h:outputText value="#{managerDashboard.enrolledStudentsLabel}"/></div>
            </div>
            <div class="kpi-card">
                <div class="kpi-label">Tasa de matr&iacute;cula</div>
                <div class="kpi-value"><h:outputText value="#{managerDashboard.enrollmentRate}"/>%</div>
            </div>
        </div>
    </rich:panel>
</f:view>
</body>
</html>
