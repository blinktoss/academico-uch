<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Aulas</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <a4j:form id="form1">
            <table width="100%">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE AULAS</strong></td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                            <a4j:support oncomplete="Richfaces.showModalPanel('mp',{top:130})"
                                         event="onclick" actionListener="#{managerAula.nuevo}"
                                         reRender="descripcion_i,capacidad_i,local_i,pabellon_i"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%"></td>
                    <td align=right width="50%">
                        <a4j:commandButton type="button" id="buscar" title="Buscar"
                                           actionListener="#{managerAula.seleccionar}"
                                           reRender="tablaMaster, barra"
                                           image="/Imagenes/actions/viewmag.png"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="3" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td width="20%" colspan="2"></td>
                    <td width="30%"></td>
                    <td width="50%"></td>
                </tr>
                <tr>
                </tr>
                <tr>
                    <td width="10%">Nombre de Aula:</td>
                    <td width="30%">
                        <h:inputText value="#{managerAula.paramDes}" style="width: 180px;"/>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Local:</td>
                    <td width="30%">
                        <h:selectOneMenu value="#{managerAula.paramLocalId}">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerAula.paramLocales}"/>
                            <a4j:support actionListener="#{managerAula.setearParamLocal}" event="onchange"
                                         reRender="pabellon_f"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Pabell&oacute;n:</td>
                    <td width="30%">
                        <h:selectOneMenu id="pabellon_f" value="#{managerAula.paramPabellonId}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerAula.paramPabellones}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="3" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                </tr>
            </table>
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            width="100%" border="0" value="#{managerAula.listaAula}" var="aula">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Descripci&oacute;n" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{aula.paramDes}" rendered="#{aula.view}"/>
                    <h:inputText value="#{aula.paramDes}" rendered="#{aula.editable}" maxlength="45"/>
                </h:column>

                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Capacidad"/>
                    </f:facet>

                    <h:outputText value="#{aula.rowCapacidad}" rendered="#{aula.view}"/>
                    <h:inputText value="#{aula.rowCapacidad}" rendered="#{aula.editable}"/>
                </h:column>

                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Local"/>
                    </f:facet>

                    <h:outputText value="#{aula.rowLocalValue}" rendered="#{aula.view}">

                    </h:outputText>
                    <h:selectOneMenu value="#{aula.rowLocalId}" rendered="#{aula.editable}">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                        <f:selectItems value="#{aula.comboLocales}"/>
                        <a4j:support actionListener="#{aula.setearLocal}" event="onchange"
                                     reRender="pabellon_s"/>
                    </h:selectOneMenu>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Pabell&oacute;n" escape="false"/>
                    </f:facet>

                    <h:outputText value="#{aula.rowPabellonValue}" rendered="#{aula.view}">

                    </h:outputText>
                    <h:selectOneMenu id="pabellon_s" value="#{aula.rowPabellonId}" rendered="#{aula.editable}">
                        <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                        <f:selectItems value="#{aula.comboPabellones}"/>
                    </h:selectOneMenu>
                </h:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <h:commandButton actionListener="#{aula.editarFila}" value=""
                                     image="/Imagenes/actions/klipper_dock.png" title="Editar"
                                     rendered="#{aula.view}"/>

                    <f:param id="id" value="#{aula.rowId}"/>
                    <f:param id="descripcion" value="#{aula.paramDes}"/>
                    <f:param id="capacidad" value="#{aula.rowCapacidad}"/>
                    <f:param id="pabellon" value="#{aula.rowPabellonId}"/>

                    <a4j:commandButton actionListener="#{aula.editar}" image="/Imagenes/actions/filesave.png"
                                       value="" title="Guardar" rendered="#{aula.editable2}"
                                       reRender="tablaMaster, barra" oncomplete="#{aula.oncomplete}"/>
                    <h:commandButton actionListener="#{aula.cancelar}" image="/Imagenes/actions/fileclose.png"
                                     value="" title="Cancelar" rendered="#{aula.editable}"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerAula.eliminarFila}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                    <f:param id="delete" value="#{aula.rowId}"/>
                </rich:column>

            </rich:dataTable>
        </a4j:form>

        <rich:modalPanel id="mp" minWidth="500" autosized="true" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Aulas"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table width="100%">
                    <tr>
                        <td align="right" colspan="4">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerAula.grabar}"
                                               action="#{managerAula.seleccionar}" reRender="tablaMaster, barra"
                                               oncomplete="#{managerAula.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <rich:separator height="3" lineType="double"/>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Descripci&oacute;n:</td>
                        <td width="70%">
                            <h:inputText label="Descripcion" id="descripcion_i" style=" width : 180px;"
                                         value="#{managerAula.mpDes}" maxlength="45"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Capacidad:</td>
                        <td width="70%">
                            <h:inputText id="capacidad_i" label="Capacidad" value="#{managerAula.rowCapacidad}"
                                         style="width : 180px;"/>
                        </td>
                        <td width="20%"></td>
                    </tr>

                    <tr>
                        <td width="30%">Local:</td>
                        <td width="70%">
                            <h:selectOneMenu id="local_i" value="#{managerAula.rowLocalId}">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerAula.comboLocales}"/>
                                <a4j:support event="onchange" actionListener="#{managerAula.setearLocal}"
                                             reRender="pabellon_i"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Pabell&oacute;n:</td>
                        <td width="70%">
                            <h:selectOneMenu id="pabellon_i" value="#{managerAula.rowPabellonId}">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerAula.comboPabellones}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <rich:separator height="3" lineType="double"/>
                        </td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>
