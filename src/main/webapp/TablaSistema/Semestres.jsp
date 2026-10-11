<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Mantenimiento de Semestres</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1" style="width:100%">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE SEMESTRES</strong>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                            <a4j:support actionListener="#{managerSemestre.openModalForSemestre}"
                                         event="onclick" reRender="formSemestre"
                                         oncomplete="Richfaces.showModalPanel('mp',{top:130})"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" align="right"></td>
                    <td align=right width="50%">
                        <a4j:commandButton type="button" id="buscar" title="Buscar" reRender="barra,tablaMaster"
                                           action="#{managerSemestre.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png"/>
                    </td>
                </tr>
                <tr style="width: 100%">
                    <td colspan="7" style="padding: 10px 0">
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
                <tr style=" height : 24px;">
                    <td width="10%">C&oacute;digo:
                    </td>
                    <td width="30%">
                        <h:inputText id="txtCodigo" value="#{managerSemestre.paramCodigo}" style="width : 180px;"
                                     maxlength="10"/>
                    </td>
                    <td width="50%"></td>
                </tr>
                <tr>
                    <td width="10%">Nombre:
                    </td>
                    <td width="30%">
                        <h:inputText value="#{managerSemestre.paramNombre}" style="width : 180px;"/></td>
                    <td width="50%">
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="7" style="padding: 10px 0">
                        <rich:separator height="3" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                </tr>
            </table>
        </h:form>
        <a4j:form>
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8"/>
            <rich:dataTable id="tablaMaster" rows="10"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            cellpadding="0" cellspacing="0" border="0" width="100%"
                            value="#{managerSemestre.listaSemestres}" var="semestre">

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{semestre.paramCodigo}" rendered="#{semestre.view}"/>
                    <h:inputText value="#{semestre.paramCodigo}" rendered="#{semestre.editable}" maxlength="10"
                                 style="width: 100px;"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nombre"/>
                    </f:facet>

                    <h:outputText value="#{semestre.paramNombre}" rendered="#{semestre.view}"/>
                    <h:inputText value="#{semestre.paramNombre}" rendered="#{semestre.editable}"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Vigente"/>
                    </f:facet>

                    <h:graphicImage value="#{semestre.rowActual}" rendered="#{semestre.view}"/>

                    <h:selectBooleanCheckbox value="#{semestre.ediActual}" rendered="#{semestre.editable}"/>
                </rich:column>
                <rich:column style="text-align: center">
                    <f:facet name="header">
                        <h:outputText value="Actual"/>
                    </f:facet>

                    <h:graphicImage value="#{semestre.rowVigente}" rendered="#{semestre.view}"/>

                    <h:selectBooleanCheckbox value="#{semestre.ediVigente}" rendered="#{semestre.editable}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Inicio Semestre"/>
                    </f:facet>

                    <h:outputText value="#{semestre.rowInicio}" rendered="#{semestre.view}">
                        <f:convertDateTime pattern="dd/MM/yyyy"/>
                    </h:outputText>
                    <rich:calendar value="#{semestre.rowInicio}" datePattern="dd/MM/yyyy"
                                   rendered="#{semestre.visible}"
                                   inputStyle="width : 80px;"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Fin Semestre"/>
                    </f:facet>

                    <h:outputText value="#{semestre.rowFin}" rendered="#{semestre.view}">
                        <f:convertDateTime pattern="dd/MM/yyyy"/>
                    </h:outputText>
                    <rich:calendar value="#{semestre.rowFin}" datePattern="dd/MM/yyyy"
                                   rendered="#{semestre.visible}"
                                   inputStyle="width: 80px;"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Semanas"/>
                    </f:facet>

                    <h:outputText value="#{semestre.rowNroSemanas}" rendered="#{semestre.view}"/>
                    <h:inputText value="#{semestre.rowNroSemanas}" rendered="#{semestre.editable}"
                                 style=" width: 40px;"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Pensiones"/>
                    </f:facet>

                    <h:outputText value="#{semestre.rowNroPensiones}" rendered="#{semestre.view}"/>
                    <h:inputText value="#{semestre.rowNroPensiones}" rendered="#{semestre.editable}"
                                 style=" width: 40px;"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Editar"/>
                    </f:facet>
                    <a4j:commandButton actionListener="#{semestre.editarFila}"
                                       image="/Imagenes/actions/klipper_dock.png" title="Editar"
                                       rendered="#{semestre.view}" reRender="tablaMaster, barra"/>

                    <f:param id="id" value="#{semestre.rowId}"/>
                    <f:param id="codigo" value="#{semestre.paramCodigo}"/>
                    <f:param id="nombre" value="#{semestre.paramNombre}"/>
                    <f:param id="vigente" value="#{semestre.ediVigente}"/>
                    <f:param id="inicio" value="#{semestre.rowInicio}"/>
                    <f:param id="fin" value="#{semestre.rowFin}"/>
                    <f:param id="semanas" value="#{semestre.rowNroSemanas}"/>
                    <f:param id="pensiones" value="#{semestre.rowNroPensiones}"/>
                    <f:param id="actual" value="#{semestre.ediActual}"/>

                    <a4j:commandButton actionListener="#{semestre.editar}"
                                       image="/Imagenes/actions/filesave.png" title="Guardar"
                                       rendered="#{semestre.editable2}" reRender="tablaMaster, barra"
                                       oncomplete="#{semestre.oncomplete}"/>
                    <a4j:commandButton actionListener="#{semestre.cancelar}"
                                       image="/Imagenes/actions/fileclose.png" title="Cancelar"
                                       rendered="#{semestre.editable}" reRender="tablaMaster, barra"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <h:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                     actionListener="#{managerSemestre.eliminarFila}"
                                     action="#{managerSemestre.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                    <f:param id="delete" value="#{semestre.rowId}"/>
                </rich:column>
            </rich:dataTable>
        </a4j:form>
    </rich:panel>

    <rich:modalPanel id="mp" autosized="true" minWidth="450" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro de Semestre"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
        </f:facet>
        <h:form id="formSemestre">
            <table style="width: 100%">
                <tr>
                    <td align="right" colspan="4">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerSemestre.grabar}"
                                           action="#{managerSemestre.seleccionar}" reRender="tablaMaster, barra"
                                           oncomplete="#{managerSemestre.oncomplete}"/>

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
                        <h:inputText id="codigo" style="width : 180px;" value="#{managerSemestre.mpCodigo}"
                                     maxlength="10"/>
                    </td>
                    <td align="right" width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">*Nombre:</td>
                    <td width="70%">
                        <h:inputText id="txtNombre" value="#{managerSemestre.mpNombre}" style="width : 180px;"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">*Inicio de Semestre:</td>
                    <td width="70%">
                        <a4j:outputPanel id="calendar" layout="block">
                            <rich:calendar id="txtInicio" value="#{managerSemestre.rowInicio}"
                                           datePattern="dd/MM/yyyy" inputStyle="width : 160px;"/>
                        </a4j:outputPanel>

                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">*Fin de Semestre:</td>
                    <td width="70%">
                        <a4j:outputPanel id="calendar3" layout="block">
                            <rich:calendar id="txtFin" value="#{managerSemestre.rowFin}" datePattern="dd/MM/yyyy"
                                           inputStyle="width : 160px;"/>
                        </a4j:outputPanel>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">Actual:</td>
                    <td width="70%" align="left">
                        <h:selectBooleanCheckbox id="txtResolucion" value="#{managerSemestre.ediVigente}"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">Vigente:</td>
                    <td width="70%">
                        <h:selectBooleanCheckbox id="txtActual" value="#{managerSemestre.ediActual}"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:separator height="3" lineType="double" style="margin: 5px 0"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">*Nro de Semanas:</td>
                    <td width="70%">
                        <h:inputText id="txtSemana" value="#{managerSemestre.mpNroSemanas}"
                                     style="width : 180px;"/>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td width="30%">*Nro. de Pensiones:</td>
                    <td width="70%">
                        <h:inputText id="txtPensiones" value="#{managerSemestre.mpNroPensiones}"
                                     style="width : 180px;"/>
                    </td>
                    <td width="20%"></td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
