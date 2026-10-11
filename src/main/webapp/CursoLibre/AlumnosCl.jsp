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
    <title>Inscripcion de Alumnos - CL</title>
</head>
<f:view>
    <body>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1" style="width:100%">
            <table style="width:100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE ALUMNOS</strong></td>
                    <td width="30%" align="right"></td>
                    <td width="30%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>

                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;"
                                        title="Nuevo alumno" width="16">
                            <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mpUpdate',{top:100})"
                                         actionListener="#{managerCLAlumno.nuevo}"
                                         reRender="iPaterno, iMaterno,iNombres, iDni, iEmail,
                                                 iFecha, iTelefono, iCelular, iDireccion, nTipo, nDepartamento, nProvincia, nDistrito,
                                                 nSexo, nUnidad, nFormaPago, nProcedencia,iCodigo, nParentesco, nFamiliar, nImagen"/>
                        </h:graphicImage>
                        <rich:spacer width="8px"/>

                        <h:graphicImage value="/Imagenes/actions/pencil.png" style="cursor: pointer;"
                                        title="Alumno de la universidad" width="16">
                            <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mpUniv',{top:150})"
                                         actionListener="#{managerCLAlumno.nuevoUniv}"
                                         reRender="univCodigo, univ_pate, univ_mate, univ_nomb, univ_esp, univOk, univError, univ_imagen"/>
                        </h:graphicImage>
                        <rich:spacer width="8px"/>

                        <a4j:commandButton id="buscar" actionListener="#{managerCLAlumno.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png" title="Buscar"
                                           oncomplete="#{managerCLAlumno.oncomplete}"
                                           reRender="tablaMaster,barra"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7"><rich:separator height="1"/></td>
                </tr>
                <tr>
                </tr>
                <tr style="height: 20px">
                    <td width="10%">C&oacute;digo:</td>
                    <td width="30%">
                        <h:inputText styleClass="dr-spnr-i rich-spinner-input" value="#{managerCLAlumno.paramCodigo}"
                                     style="width : 180px;"/>
                    </td>
                    <td width="10%" align="right"></td>
                    <td></td>
                </tr>
                <tr style="height: 20px">
                    <td width="10%">Ap. Paterno:</td>
                    <td width="30%">
                        <h:inputText value="#{managerCLAlumno.paramPaterno}" style="width : 180px;"/>
                    </td>
                    <td width="10%" align="right"></td>
                    <td>

                    </td>
                </tr>
                <tr style="height: 20px">
                    <td width="10%">Ap. Materno:</td>
                    <td><h:inputText value="#{managerCLAlumno.paramMaterno}" style="width : 180px;"/></td>
                    <td align="right"></td>
                    <td>

                    </td>
                </tr>
                <tr style="height: 20px">
                    <td width="10%">Nombres:</td>
                    <td><h:inputText value="#{managerCLAlumno.paramNombre}" style="width : 180px;"/></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form style="width: 100%;">
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style=" width : 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            cellpadding="0" cellspacing="0" width="100%" border="0"
                            value="#{managerCLAlumno.listaAlumno}" var="alu">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{alu.aluId}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{alu.aluCodigo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Apellidos y Nombres"/>
                    </f:facet>
                    <h:outputText value="#{alu.aluAppaterno} #{alu.aluApmaterno} #{alu.aluNombres}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Unidad"/>
                    </f:facet>
                    <h:outputText value="#{alu.aluUnidad}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Forma Pago"/>
                    </f:facet>
                    <h:outputText value="#{alu.aluFormaPago}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Procedencia"/>
                    </f:facet>
                    <h:outputText value="#{alu.aluProcedencia}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Parentesco"/>
                    </f:facet>
                    <h:outputText value="#{alu.aluParentesco}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/editpaste.png" title="Editar"
                                       oncomplete="Richfaces.showModalPanel('mpUpdate',{top:100})"
                                       actionListener="#{managerCLAlumno.editarAlumno}"
                                       reRender="iPaterno, iMaterno,iNombres, iDni, iEmail, iFecha, iTelefono,
                                               iCelular, iDireccion, nTipo, nDepartamento, nProvincia, nDistrito, nSexo,
                                               nUnidad, nFormaPago, nProcedencia,iCodigo, nParentesco, nFamiliar, nImagen">
                        <f:attribute name="p_alu_id" value="#{alu.aluId}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Enlazar"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/enlazar.png" title="Enlazar con la universidad"
                                       oncomplete="Richfaces.showModalPanel('mpEnlace',{top:100})"
                                       actionListener="#{managerCLAlumno.enlaceUniversidad}"
                                       reRender="enlForm, tablaEnlace">
                        <f:attribute name="p_alu" value="#{alu}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/delete.gif" title="Eliminar"
                                     actionListener="#{managerCLAlumno.eliminarAlumno}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <jsp:include page="modal/ModalAlumno.jsp"/>

    <rich:modalPanel id="mpUniv" autosized="true" minWidth="500" zindex="2000"
                     onshow="hideOkDiv();hideErrorDiv();">
        <f:facet name="header">
            <h:outputText value="Registro Alumno"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png"
                            style="cursor:pointer" title="Cerrar"
                            onclick="Richfaces.hideModalPanel('mpUniv')"/>
        </f:facet>
        <h:form id="univFind">
            <table style="width:100%">
                <tr style="height: 45px">
                    <td>Ingrese c&oacute;digo de la universidad</td>
                    <td><h:inputText id="univCodigo" value="#{managerCLAlumno.univCodigo}" maxlength="8"/></td>
                    <td><a4j:commandButton value="Buscar alumno"
                                           actionListener="#{managerCLAlumno.buscarUniversidad}"
                                           oncomplete="#{managerCLAlumno.oncomplete}"
                                           reRender="univ_pate, univ_mate, univ_nomb, univ_esp, univ_imagen, univOk, univError"/>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form id="univForm">
            <rich:effect name="hideOkDiv" for="divOk" type="BlindUp"/>
            <rich:effect name="showOkDiv" for="divOk" type="Appear"/>
            <div id="divOk">
                <rich:panel id="univOk">
                    <table width="100%" cellspacing="0" border="0">
                        <tr>
                            <td colspan="3" align="right">
                                <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                                   oncomplete="#{managerCLAlumno.oncomplete}"
                                                   actionListener="#{managerCLAlumno.salvarAlumnoUniversidad}"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3"><rich:separator height="1"/></td>
                        </tr>
                        <tr>
                            <td class="tdLabel" style="width: 20%">Ap. Paterno:</td>
                            <td><h:outputLabel id="univ_pate" value="#{managerCLAlumno.univAlumno.aluAppaterno}"/></td>
                            <td rowspan="5">
                                <a4j:outputPanel id="univ_imagen" ajaxRendered="true" layout="block">
                                    <h:graphicImage style="width: 100px;"
                                                    value="/foto?id=#{managerCLAlumno.univAlumno.id}"/>
                                </a4j:outputPanel>
                            </td>
                        </tr>
                        <tr>
                            <td class="tdLabel">Ap. Materno:</td>
                            <td><h:outputLabel id="univ_mate" value="#{managerCLAlumno.univAlumno.aluApmaterno}"/></td>
                        </tr>
                        <tr>
                            <td class="tdLabel">Nombre:</td>
                            <td><h:outputLabel id="univ_nomb" value="#{managerCLAlumno.univAlumno.aluNombres}"/></td>
                        </tr>
                        <tr>
                            <td class="tdLabel" style="vertical-align: top">Especialidad:</td>
                            <td><h:outputLabel id="univ_esp" value="#{managerCLAlumno.univAlumno.esp.espNombre}"/></td>
                        </tr>
                        <tr>
                            <td style="height: 40px"></td>
                        </tr>
                    </table>
                </rich:panel>
            </div>
            <rich:effect name="hideErrorDiv" for="divError" type="BlindUp"/>
            <rich:effect name="showErrorDiv" for="divError" type="Appear"/>
            <div id="divError">
                <rich:panel id="univError">
                    <div class="error" style="font-weight: bold;font-size: 15px;">
                        No se encuentra alg&uacute;n alumno con dicho c&oacute;digo.
                    </div>
                </rich:panel>
            </div>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpEnlace" autosized="true" minWidth="500" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Relacionar Alumno con Universidad"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png"
                            style="cursor:pointer" title="Cerrar"
                            onclick="Richfaces.hideModalPanel('mpEnlace')"/>
        </f:facet>
        <h:form id="enlForm">
            <table width="100%" cellspacing="0" border="0">
                <tr>
                    <td class="tdLabel" style="width: 12%;">C&oacute;digo:</td>
                    <td><h:outputText value="#{managerCLAlumno.enlaceAlumno.aluCodigo}"/></td>
                </tr>
                <tr>
                    <td class="tdLabel">Nombre:</td>
                    <td><h:outputText
                            value="#{managerCLAlumno.enlaceAlumno.aluAppaterno} #{managerCLAlumno.enlaceAlumno.aluApmaterno}, #{managerCLAlumno.enlaceAlumno.aluNombres}"/></td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:dataTable id="tablaEnlace" cellpadding="0" cellspacing="0" width="100%" border="0"
                                        onRowMouseOver="this.style.backgroundColor='#999999'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        value="#{managerCLAlumno.listaEnlace}" var="alu" rows="10">
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{alu.aluId}"/>
                                <f:param id="enl_alu_id" value="#{alu.aluId}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{alu.aluCodigo}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Ap. paterno"/>
                                </f:facet>
                                <h:outputText value="#{alu.aluAppaterno}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Ap. Materno"/>
                                </f:facet>
                                <h:outputText value="#{alu.aluApmaterno}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Nombre"/>
                                </f:facet>
                                <h:outputText value="#{alu.aluNombres}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Especialidad"/>
                                </f:facet>
                                <h:outputText value="#{alu.univCodigo}"/>
                            </rich:column>

                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Foto"/>
                                </f:facet>
                                <a4j:outputPanel id="pan_imagen" ajaxRendered="true" layout="block">
                                    <h:graphicImage style="width: 100px;"
                                                    value="/foto?id=#{alu.aluId}"/>
                                </a4j:outputPanel>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Aceptar"/>
                                </f:facet>
                                <h:commandButton image="/Imagenes/actions/button_ok.png" title="Guardar"
                                                 actionListener="#{managerCLAlumno.relacionarUniv}"
                                                 onclick="return (confirm('¿Es correcto el alumno seleccionado?'));"/>
                            </rich:column>
                        </rich:dataTable>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
    </body>
</f:view>
</html>
