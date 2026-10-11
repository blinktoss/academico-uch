<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%--
  Generic unexpected-error page. Reached via the <error-page> mapping in web.xml for any unhandled
  java.lang.Exception (e.g. the ComprobanteServlet NPE on legacy 007001-without-tariff vouchers,
  issue #4). Deliberately a plain JSP with no JSF/RichFaces tags: the container dispatches it outside
  the Faces lifecycle, and it must render even when the error happened before/around JSF.
--%>
<%!
    private static final Logger LOG = LoggerFactory.getLogger("net.uch.web.error");
%>
<%
    Object error = request.getAttribute("javax.servlet.error.exception");
    Object uri = request.getAttribute("javax.servlet.error.request_uri");
    Object status = request.getAttribute("javax.servlet.error.status_code");
    if (error instanceof Throwable) {
        LOG.error("Unhandled application error at {} (status {})", uri, status, (Throwable) error);
    } else {
        LOG.error("Error dispatch at {} (status {}): {}", uri, status,
                request.getAttribute("javax.servlet.error.message"));
    }
    String ctx = request.getContextPath();
%>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>.:: Sistema Academico UCH ::.</title>
    <link rel="icon" type="image/x_icon" href="<%= ctx %>/Imagenes/actions/pinguino.gif"/>
    <link rel="stylesheet" type="text/css" href="<%= ctx %>/css/formulario.css"/>
</head>
<body>
<div align="center" style="margin-top:120px;">
    <fieldset style="width:460px; padding:24px 24px 28px;">
        <legend>Error</legend>
        <p><img src="<%= ctx %>/Imagenes/actions/warning_y.gif" alt="Error"/></p>
        <p style="font-size:14px; line-height:1.5;">
            Ha ocurrido un error inesperado.<br/>
            Por favor, comuníquese con el Departamento de Sistemas.
        </p>
        <p style="margin-top:22px;">
            <button type="button" onclick="history.back();">Volver</button>
            &nbsp;&nbsp;
            <a href="<%= ctx %>/">Ir al inicio</a>
        </p>
    </fieldset>
</div>
</body>
</html>
