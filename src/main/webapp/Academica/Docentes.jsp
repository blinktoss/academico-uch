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
    <title>Mantenimiento de Docentes</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE DOCENTES</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                            <a4j:support actionListener="#{managedBeanDocente.limpiarData}"
                                         event="onclick" reRender="fDocente,iMensaje"
                                         oncomplete="Richfaces.showModalPanel('mpDocente')"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%"></td>
                    <td align=right width="50%">
                        <a4j:commandButton id="buscar" actionListener="#{managedBeanDocente.buscarDocente}"
                                           reRender="barra,tablaMaster"
                                           image="/Imagenes/actions/viewmag.png" title="Buscar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr style="height: 24px;">
                    <td width="10%">DNI:</td>
                    <td width="30%">
                        <h:inputText id="txtCodigo" value="#{managedBeanDocente.docDniW}" style="width: 180px;"
                                     maxlength="10"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Nombre:</td>
                    <td width="30%">
                        <h:inputText id="txtnombre" value="#{managedBeanDocente.docNombreCompletoW}"
                                     style="width: 180px;"/></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                </tr>
            </table>
        </h:form>
        <h:form>
            <h:outputText value="#{managedBeanDocente.mensaje}" id="iMensaje" styleClass="message error"/>
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" width="100%" border="0" cellpadding="0" cellspacing="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managedBeanDocente.listaDocente}" var="doc">

                <f:param value="#{doc.id}" id="p_id2"/>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="DNI" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{doc.docDni}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>
                    <h:outputText value="#{doc.docNombre}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Correo"/>
                    </f:facet>

                    <h:outputText value="#{doc.docCorreo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Tel&eacute;fono" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{doc.docTelefono}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Situaci&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{doc.docSituacionDesc}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Periodo de Inicio"/>
                    </f:facet>

                    <h:outputText value="#{doc.docPeriodoInicio}">
                        <f:convertDateTime pattern="dd/MM/yyyy"/>
                    </h:outputText>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Sexo"/>
                    </f:facet>

                    <h:outputText value="#{doc.docSexoDesc}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nacimiento"/>
                    </f:facet>

                    <h:outputText value="#{doc.docNacimientoDesc}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Residencia"/>
                    </f:facet>

                    <h:outputText value="#{doc.docResidenciaDesc}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton image="/Imagenes/actions/editpaste.png"
                                       actionListener="#{managedBeanDocente.editarDocente}"
                                       oncomplete="#{managedBeanDocente.oncomplete}"
                                       reRender="fDocente,p_id,rDistrito,rProvincia,rDepartamento,nDistrito,nProvincia,nDepartamento,iMensaje"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managedBeanDocente.eliminarDocente}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="mpDocente" autosized="true" width="600">
            <f:facet name="header">
                <h:outputText value="Registro de Docentes"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpDocente')" title="Cerrar"/>
            </f:facet>
            <h:form id="fDocente">
                <f:param id="p_id" value="#{managedBeanDocente.docente.id}"/>
                <div style="display: flex; justify-content: flex-end; padding: 10px 0;">
                    <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                       actionListener="#{managedBeanDocente.guardarDatos}"
                                       oncomplete="#{managedBeanDocente.oncomplete}"/>
                </div>
                <rich:separator height="2" lineType="double" style="margin-bottom: 10px"/>
                <div style="display: flex; gap: 10px;">
                    <div style="flex: 1;">
                        <div class="row">
                            <div class="uch-label">DNI:</div>
                            <div class="uch-input">
                                <h:inputText id="i_dni" value="#{managedBeanDocente.docente.docDni}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="uch-label">Paterno:</div>
                            <div class="uch-input">
                                <h:inputText id="i_paterno" value="#{managedBeanDocente.docente.docAppaterno}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="uch-label">Materno:</div>
                            <div class="uch-input">
                                <h:inputText id="i_materno" value="#{managedBeanDocente.docente.docApmaterno}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="uch-label">Nombres:</div>
                            <div class="uch-input">
                                <h:inputText id="i_nombres" value="#{managedBeanDocente.docente.docNombres}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="uch-label">Correo:</div>
                            <div class="uch-input">
                                <h:inputText id="iCorreo" value="#{managedBeanDocente.docente.docCorreo}" style="width: 250px;"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="uch-label">Tel&eacute;fono:</div>
                            <div class="uch-input">
                                <h:inputText id="iTelefono" value="#{managedBeanDocente.docente.docTelefono}" style="width: 180px;"/>
                            </div>
                        </div>
                    </div>
                    <div style="flex: 0 0 120px; text-align: center;">
                        <a4j:outputPanel id="imagen" ajaxRendered="true" layout="block">
                            <h:graphicImage style="width: 100px;"
                                            value="/foto-docente?id=#{managedBeanDocente.docente.id}"/>
                        </a4j:outputPanel>
                    </div>
                </div>
                <div class="row" style="margin-top: 10px;">
                    <div class="uch-label">Fecha Inicio:</div>
                    <div class="uch-input">
                        <a4j:outputPanel id="calendar" layout="block">
                            <rich:calendar id="iCreacion" value="#{managedBeanDocente.docente.docPeriodoInicio}" datePattern="dd/MM/yyyy"/>
                        </a4j:outputPanel>
                    </div>
                </div>
                <div class="row">
                    <div class="uch-label">Sexo:</div>
                    <div class="uch-input">
                        <h:selectOneRadio id="iSexo" value="#{managedBeanDocente.docente.docSexo}">
                            <f:selectItems value="#{managedBeanDocente.rdoSexo}"/>
                        </h:selectOneRadio>
                    </div>
                </div>
                <div class="row">
                    <div class="uch-label">Situaci&oacute;n:</div>
                    <div class="uch-input">
                        <h:selectOneMenu id="iSituacion" value="#{managedBeanDocente.docente.docSituacion}">
                            <f:selectItems value="#{managedBeanDocente.cboSituacion}"/>
                        </h:selectOneMenu>
                    </div>
                </div>
                <rich:separator height="2" lineType="double" style="margin: 10px 0"/>
                <div class="row">
                    <div class="uch-label">Nac. Departamento:</div>
                    <div class="uch-input">
                        <h:selectOneMenu id="nDepartamento" value="#{managedBeanDocente.wDepaNaci}">
                            <f:selectItems value="#{managedBeanDocente.cboDepartamentosN}"/>
                            <a4j:support event="onchange" reRender="nProvincia,nDistrito"
                                         actionListener="#{managedBeanDocente.loadProvincesN}"/>
                        </h:selectOneMenu>
                    </div>
                </div>
                <div class="row">
                    <div class="uch-label">Nac. Provincia:</div>
                    <div class="uch-input">
                        <h:selectOneMenu id="nProvincia" value="#{managedBeanDocente.wProvNaci}">
                            <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managedBeanDocente.cboProvinciaN}"/>
                            <a4j:support event="onchange" reRender="nDistrito"
                                         actionListener="#{managedBeanDocente.loadDistrictsN}"/>
                        </h:selectOneMenu>
                    </div>
                </div>
                <div class="row">
                    <div class="uch-label">Nac. Distrito:</div>
                    <div class="uch-input">
                        <h:selectOneMenu id="nDistrito" value="#{managedBeanDocente.docente.docNacimiento}">
                            <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managedBeanDocente.cboDistritoN}"/>
                        </h:selectOneMenu>
                    </div>
                </div>
                <div class="row">
                    <div class="uch-label">Res. Departamento:</div>
                    <div class="uch-input">
                        <h:selectOneMenu id="rDepartamento" value="#{managedBeanDocente.wDepaResi}">
                            <f:selectItems value="#{managedBeanDocente.cboDepartamentosR}"/>
                            <a4j:support event="onchange" reRender="rProvincia,rDistrito"
                                         actionListener="#{managedBeanDocente.loadProvincesR}"/>
                        </h:selectOneMenu>
                    </div>
                </div>
                <div class="row">
                    <div class="uch-label">Res. Provincia:</div>
                    <div class="uch-input">
                        <h:selectOneMenu id="rProvincia" value="#{managedBeanDocente.wProvResi}">
                            <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managedBeanDocente.cboProvinciaR}"/>
                            <a4j:support event="onchange" reRender="rDistrito"
                                         actionListener="#{managedBeanDocente.loadDistrictsR}"/>
                        </h:selectOneMenu>
                    </div>
                </div>
                <div class="row">
                    <div class="uch-label">Res. Distrito:</div>
                    <div class="uch-input">
                        <h:selectOneMenu id="rDistrito" value="#{managedBeanDocente.docente.docResidencia}">
                            <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managedBeanDocente.cboDistritoR}"/>
                        </h:selectOneMenu>
                    </div>
                </div>
            </h:form>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>


