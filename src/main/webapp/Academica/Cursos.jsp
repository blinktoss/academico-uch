<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Cursos</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <div class="row">
                <div class="col title-label">LISTADO CURSOS</div>
                <div class="col" style="text-align: right">
                    <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                        <a4j:support oncomplete="Richfaces.showModalPanel('mp', {top:130})"
                                     event="onclick" actionListener="#{managerCurso.nuevo}"
                                     reRender="iId,iCodigo,iFacultad,iEspecialidad,iNombre"/>
                    </h:graphicImage>
                    <rich:spacer width="8px"/>
                    <h:commandButton type="button" id="buscar" action="#{managerCurso.seleccionar}"
                                     image="/Imagenes/actions/viewmag.png" title="Buscar"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin-bottom: 10px"/>
            <div class="row">
                <div class="col uch-label">Facultad:</div>
                <div class="col uch-input">
                    <h:selectOneMenu id="Facultad" value="#{managerCurso.paramFacultadId}" style="width:180px">
                        <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                        <f:selectItems value="#{managerCurso.comboFacultades}"/>
                        <a4j:support event="onchange" reRender="Especialidad"
                                     actionListener="#{managerCurso.onChangeParamFacultades}"/>
                    </h:selectOneMenu>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Especialidad:</div>
                <div class="col uch-input">
                    <h:selectOneMenu id="Especialidad" value="#{managerCurso.paramEspecialidadId}">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                        <f:selectItems value="#{managerCurso.paramEspecialidades}"/>
                    </h:selectOneMenu>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">C&oacute;digo:</div>
                <div class="col uch-input">
                    <h:inputText id="txtCodigo" value="#{managerCurso.paramCodigo}" style="width: 180px;"
                                 maxlength="20"/>
                </div>
            </div>
            <div class="row">
                <div class="col uch-label">Nombre:</div>
                <div class="col uch-input">
                    <h:inputText id="txtNombre" value="#{managerCurso.paramNombre}" style="width: 180px;"
                                 maxlength="50"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin: 10px 0"/>
        </h:form>
        <h:form>
            <rich:datascroller id="barra" align="right" for="cursos" maxPages="8"/>
            <rich:dataTable id="cursos" var="cursos" rows="10"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            cellpadding="0" cellspacing="0"
                            width="100%" border="0" value="#{managerCurso.listaCursos}">

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{cursos.paramCodigo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>

                    <h:outputText value="#{cursos.paramNombre}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Facultad"/>
                    </f:facet>
                    <h:outputText value="#{cursos.rowFacultadDescripcion}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Especialidad"/>
                    </f:facet>
                    <h:outputText value="#{cursos.rowEspecialidadDescripcion}"/>
                </rich:column>

                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <h:graphicImage value="/Imagenes/actions/editpaste.png" style="cursor: pointer;">
                        <a4j:support oncomplete="Richfaces.showModalPanel('mp', {top:130})"
                                     event="onclick" actionListener="#{managerCurso.editarFila}"
                                     reRender="iId,iCodigo,iFacultad,iEspecialidad,iNombre"/>
                    </h:graphicImage>
                    <f:param id="p_id" value="#{cursos.rowId}"/>
                    <f:param id="p_codigo" value="#{cursos.paramCodigo}"/>
                    <f:param id="p_nombre" value="#{cursos.paramNombre}"/>
                    <f:param id="p_facultad" value="#{cursos.paramFacultadId}"/>
                    <f:param id="p_especialidad" value="#{cursos.paramEspecialidadId}"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerCurso.eliminarFila}"
                                     action="#{managerCurso.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                    <f:param id="delete" value="#{cursos.rowId}"/>
                </rich:column>
            </rich:dataTable>
        </h:form>
    </rich:panel>

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
                                           actionListener="#{managerCurso.enviar}"
                                           action="#{managerCurso.seleccionar}" reRender="cursos"
                                           oncomplete="#{managerCurso.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:separator height="3" lineType="double" style="margin-bottom: 10px"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">*C&oacute;digo:</td>
                    <td width="70%">
                        <h:inputHidden id="iId" value="#{managerCurso.mpId}"/>
                        <h:inputText id="iCodigo" style="width: 180px;" value="#{managerCurso.mpCodigo}"
                                     maxlength="20"/>
                    </td>
                    <td align="right" width="20%">
                    </td>
                </tr>
                <tr>
                    <td width="30%">*Nombre:</td>
                    <td width="70%">
                        <h:inputText id="iNombre" value="#{managerCurso.mpNombre}" style="width: 180px;"
                                     maxlength="50"/>
                    </td>
                    <td width="20%"></td>
                </tr>

                <tr>
                    <td width="30%">*Facultad:</td>
                    <td width="70%">
                        <h:selectOneMenu id="iFacultad" value="#{managerCurso.mpFacultadId}" style="width: 180px">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCurso.comboFacultades}"/>
                            <a4j:support event="onchange" reRender="iEspecialidad"
                                         actionListener="#{managerCurso.onChangeModalFacultades}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>

                <tr>
                    <td width="30%">*Especialidad:</td>
                    <td width="70%">
                        <h:selectOneMenu id="iEspecialidad" value="#{managerCurso.mpEspecialidadId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCurso.mpEspecialidades}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
