<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>En construcci&oacute;n</title>
</head>
<body>
<f:view>
    <h:form>
        <jsp:include page="../TablaSistema/Menu.jsp"/>
        <table align="center" style="vertical-align:middle">
            <tr>
                <td height="600px"></td>
                <td align="center">
                    <h:graphicImage value="/Imagenes/actions/EnConstruccion.gif"/>
                </td>
            </tr>
        </table>
    </h:form>
</f:view>
</body>
</html>
