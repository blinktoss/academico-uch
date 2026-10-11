<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:subview id="svAlumnoUpdate">
    <a4j:loadStyle src="resource:///css/formulario.css"/>
    <a4j:loadStyle src="resource:///css/richStyle.xcss"/>

    <rich:modalPanel id="mpUpdate" autosized="true" minWidth="550" zindex="4000">
        <f:facet name="header">
            <h:outputText value="Registro Alumno"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpUpdate')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <table style="width:100%">
                <tr>
                    <td colspan="3" align="right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           title="Guardar" reRender="iCodigo"
                                           actionListener="#{managerCLAlumno.guardarDatos}"
                                           oncomplete="#{managerCLAlumno.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="3" align="left">
                        <h:outputText value="(*) Campos Obligatorios." style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>C&oacute;digo</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluCodigo}" id="iCodigo"/>
                    </td>
                    <td rowspan="6">
                        <h:panelGroup id="nImagen">
                            <rich:panel rendered="#{managerCLAlumno.aluId != 0}" style="text-align: center;">
                                <h:graphicImage style="width: 100px;"
                                                value="/foto-cl?id=#{managerCLAlumno.aluId}"/>
                            </rich:panel>
                        </h:panelGroup>
                    </td>
                </tr>
                <tr>
                    <td>Ap. Paterno</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluAppaterno}" id="iPaterno"/>
                        <rich:spacer width="5px"/>
                        <h:outputText value="(*)" style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>Ap. Materno</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluApmaterno}" id="iMaterno"/>
                        <rich:spacer width="5px"/>
                        <h:outputText value="(*)" style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>Nombres</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluNombres}" id="iNombres"/>
                        <rich:spacer width="5px"/>
                        <h:outputText value="(*)" style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>E-mail</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluMail}" id="iEmail" style="width: 250px;"/>
                        <rich:spacer width="5px"/>
                        <h:outputText value="(*)" style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>Tel&eacute;fono</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluTelefono}" id="iTelefono"/>
                        <rich:spacer width="5px"/>
                        <h:outputText value="(*)" style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>Celular</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluCelular}" id="iCelular"/>
                        <rich:spacer width="5px"/>
                        <h:outputText value="(*)" style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>Direcci&oacute;n</td>
                    <td colspan="2">
                        <h:inputText value="#{managerCLAlumno.aluDireccion}" id="iDireccion" style="width: 400px;"/>
                        <rich:spacer width="5px"/>
                        <h:outputText value="(*)" style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>Forma pago</td>
                    <td>
                        <h:selectOneMenu id="nFormaPago" value="#{managerCLAlumno.aluFormaPago}" style="width : 180px;"
                                         tabindex="11">
                            <f:selectItems value="#{managerCLAlumno.cboFormaPago}"/>
                        </h:selectOneMenu>
                        <rich:spacer width="5px"/>
                        <h:outputText value="(*)" style="color: red;"/>
                    </td>
                </tr>
                <tr>
                    <td>Fecha Nac.</td>
                    <td>
                        <rich:calendar id="iFecha" value="#{managerCLAlumno.aluFechaNacimiento}"
                                       datePattern="dd/MM/yyyy"/>
                    </td>
                </tr>
                <tr>
                    <td>DNI</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluDni}" id="iDni"/>
                    </td>
                </tr>
                <tr>
                    <td>Alumno tipo</td>
                    <td>
                        <h:selectOneMenu id="nTipo" value="#{managerCLAlumno.aluTipo}" style="width : 180px;"
                                         tabindex="9">
                            <f:selectItems value="#{managerCLAlumno.cboTipo}"/>
                            <a4j:support event="onchange"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Departamento</td>
                    <td>
                        <h:selectOneMenu id="nDepartamento" value="#{managerCLAlumno.departamento}"
                                         style="width : 180px;" tabindex="9">
                            <f:selectItems value="#{managerCLAlumno.cboDepartamento}"/>
                            <a4j:support event="onchange" reRender="nProvincia,nDistrito"
                                         actionListener="#{managerCLAlumno.onChangeDepartamento}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Provincia</td>
                    <td>
                        <h:selectOneMenu id="nProvincia" value="#{managerCLAlumno.provincia}" style="width : 180px;"
                                         tabindex="10">
                            <f:selectItem itemValue="0000" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLAlumno.cboProvincia}"/>
                            <a4j:support event="onchange" reRender="nDistrito"
                                         actionListener="#{managerCLAlumno.onChangeProvincia}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Distrito</td>
                    <td>
                        <h:selectOneMenu id="nDistrito" value="#{managerCLAlumno.distrito}" style="width : 180px;"
                                         tabindex="11">
                            <f:selectItem itemValue="000000" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLAlumno.cboDistrito}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Sexo</td>
                    <td>
                        <h:selectOneMenu id="nSexo" value="#{managerCLAlumno.aluSexo}" style="width : 180px;"
                                         tabindex="11">
                            <f:selectItems value="#{managerCLAlumno.cbosexo}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Unidad</td>
                    <td>
                        <h:selectOneMenu id="nUnidad" value="#{managerCLAlumno.aluUnidad}" style="width : 180px;"
                                         tabindex="11">
                            <f:selectItems value="#{managerCLAlumno.cboUnidad}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Procedencia</td>
                    <td>
                        <h:selectOneMenu id="nProcedencia" value="#{managerCLAlumno.aluProcedencia}"
                                         style="width : 180px;" tabindex="11">
                            <f:selectItems value="#{managerCLAlumno.cboProcedencia}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Parentesco</td>
                    <td>
                        <h:selectOneMenu id="nParentesco" value="#{managerCLAlumno.aluParentesco}"
                                         style="width : 180px;" tabindex="11">
                            <f:selectItems value="#{managerCLAlumno.cboParentesco}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Familiar</td>
                    <td>
                        <h:inputText value="#{managerCLAlumno.aluNombreFamiliar}" id="nFamiliar"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:subview>
