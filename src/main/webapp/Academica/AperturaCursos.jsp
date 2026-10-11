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
    <title>Mantenimiento de Curso Aperturado</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%;">
                <tr>
                    <td width="20%" colspan="2"><strong>APERTURA DE CURSOS</strong>
                    </td>
                    <td width="30%"></td>
                    <td width="30%">
                    </td>
                    <td align=right width="50%">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td>
                        <h:commandButton type="button" id="refrescar"
                                         action="#{managerCursoAperturado.cargarArbol}"
                                         image="/Imagenes/actions/reload.png"
                                         title="Buscar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="8">
                        <rich:separator height="1"/>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form>
            <table style="width: 100%;">
                <tr>
                    <td style="width: 250px; vertical-align:top;">
                        <rich:tree value="#{managerCursoAperturado.arbol}" ajaxSubmitSelection="true"
                                   switchType="ajax" nodeSelectListener="#{managerCursoAperturado.seleccionNodo}"
                                   var="item" reRender="facultad,semestre_f,tablaMaster"/>
                    </td>
                    <td style="vertical-align:top;" colspan="3">
                        <table style="width: 100%; height: 20px;">
                            <tr>
                                <td>
                                    <h:selectOneMenu id="semestre_f" value="#{managerCursoAperturado.comboSemestreId}">
                                        <f:selectItems value="#{managerCursoAperturado.comboSemestres}"/>
                                        <a4j:support event="onchange" reRender="tablaMaster"
                                                     actionListener="#{managerCursoAperturado.seleccionPorSemestre}"/>
                                    </h:selectOneMenu>
                                </td>
                                <td style="text-align: right">
                                    <h:outputText
                                            value="#{managerCursoAperturado.selectedNode.arbolEspecialidad} - #{managerCursoAperturado.selectedNode.arbolPlan}"
                                            id="facultad"/>
                                </td>
                            </tr>
                        </table>
                        <rich:separator height="1"/>
                        <div style="height: 20px; display: inherit; vertical-align: bottom;">
                            Cursos del Ciclo Seleccionado:
                        </div>
                        <rich:dataTable id="tablaMaster" rows="30" cellpadding="0" cellspacing="0" width="100%"
                                        border="0"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        value="#{managerCursoAperturado.listaCursos}" var="cursos">

                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Ciclo"/>
                                </f:facet>
                                <h:outputText value="#{cursos.cursoCicloLabel}"/>
                            </rich:column>

                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{cursos.cursoCodigo}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Nombre"/>
                                </f:facet>
                                <h:outputText value="#{cursos.cursoNombre}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Semestre"/>
                                </f:facet>
                                <h:outputText value="#{cursos.cursoSemestre}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Aperturado"/>
                                </f:facet>
                                <h:outputText value="#{cursos.cursoEstaAperturado}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Sis. Evaluaci&oacute;n" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{cursos.curapeSisEvaluacion}"/>
                            </h:column>

                            <rich:column style="text-align: center;">
                                <h:graphicImage value="#{cursos.curapeImgCandado}" style="cursor:pointer;">
                                    <a4j:support event="onclick"
                                                 actionListener="#{managerCursoAperturado.detallar}"
                                                 oncomplete="Richfaces.showModalPanel('mp',{top:50});"
                                                 reRender="id,id_curape,codigo,nombre,ciclo,sisevaluacion,promedios,aperturado,idplancur,tablaSecundaria,personal,pnlPersonalizacion">
                                        <f:attribute name="curso_aperturado" value="#{cursos}"/>
                                    </a4j:support>
                                </h:graphicImage>
                            </rich:column>

                            <rich:column style="text-align: center;">
                                <h:graphicImage value="/Imagenes/actions/secciona.png" style="cursor:pointer;">
                                    <a4j:support event="onclick"
                                                 actionListener="#{managerCursoAperturado.detallar}"
                                                 oncomplete="Richfaces.showModalPanel('mp2',{top:50});"
                                                 reRender="id1,id_curape1,codigo1,nombre1,ciclo1,idplancur1,sisevaluacion1,aperturado1,tablaSecundaria1">
                                        <f:attribute name="curso_aperturado" value="#{cursos}"/>
                                    </a4j:support>
                                </h:graphicImage>
                            </rich:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mp" autosized="true" minWidth="640" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro de Apertura"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <table style="width: 100%;">
                <tr>
                    <td align="right" colspan="2">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerCursoAperturado.insertar}"
                                           action="#{managerCursoAperturado.seleccion}"
                                           reRender="tablaMaster"
                                           oncomplete="alert('Se guardo el registro correctamente');Richfaces.hideModalPanel('mp')"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                        <h:inputHidden value="#{managerCursoAperturado.modalCurapeId}" id="id_curape"/>
                        <h:inputHidden value="#{managerCursoAperturado.modalPlancurId}" id="idplancur"/>
                        <h:inputHidden value="#{managerCursoAperturado.modalCursoId}" id="id"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 19%;">Ciclo:</td>
                    <td>
                        <h:outputText value="#{managerCursoAperturado.modalCiclo}" id="ciclo"/>
                    </td>
                </tr>
                <tr>
                    <td>C&oacute;digo:</td>
                    <td>
                        <h:outputText value="#{managerCursoAperturado.modalCodigo}" id="codigo"/>
                    </td>
                </tr>
                <tr>
                    <td>Nombre:</td>
                    <td>
                        <h:outputText value="#{managerCursoAperturado.modalNombre}" id="nombre"/>
                    </td>
                </tr>
                <tr>
                    <td>Aperturado:</td>
                    <td>
                        <h:selectBooleanCheckbox id="aperturado"
                                                 value="#{managerCursoAperturado.modalEstaAperturado}"/>
                    </td>
                </tr>
                <tr>
                    <td>Sist. de Evaluaci&oacute;n:</td>
                    <td>
                        <h:selectOneMenu value="#{managerCursoAperturado.modalSistemaEvaluacionId}"
                                         id="sisevaluacion">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerCursoAperturado.comboSistemaEvaluacion}"/>
                            <a4j:support actionListener="#{managerCursoAperturado.loadPromediosExamenes}"
                                         event="onchange" reRender="promedios"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Personalizar:</td>
                    <td>
                        <h:selectBooleanCheckbox value="#{managerCursoAperturado.personalizado}" id="personal">
                            <a4j:support event="onclick" actionListener="#{managerCursoAperturado.cambiarEfecto}"
                                         oncomplete="#{managerCursoAperturado.efecto}"
                                         reRender="pnlPersonalizacion"/>
                        </h:selectBooleanCheckbox>
                    </td>
                </tr>
            </table>
            <rich:effect name="hideDiv" for="pnlPersonalizacion" type="Fade"/>
            <rich:effect name="showDiv" for="pnlPersonalizacion" type="Appear"/>
            <rich:panel id="pnlPersonalizacion" style="width: 100%;">
                <div>
                    <table>
                        <tr>
                            <td>Promedio:</td>
                            <td>C&oacute;digo:</td>
                            <td>Nombre:</td>
                            <td>Peso:</td>
                            <td>Semana:</td>
                            <td>Orden:</td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>
                                <h:selectOneMenu value="#{managerCursoAperturado.sisevaperPromedioId}"
                                                 id="promedios" style="width: 150px;">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerCursoAperturado.comboPromediosExamenes}"/>
                                </h:selectOneMenu>
                            </td>
                            <td>
                                <h:inputText value="#{managerCursoAperturado.sisevaperCodigo}"/>
                            </td>
                            <td>
                                <h:inputText value="#{managerCursoAperturado.sisevaperNombre}"/>
                            </td>
                            <td>
                                <rich:inputNumberSpinner value="#{managerCursoAperturado.sisevaperPeso}"
                                                         inputStyle="width: 20px;"/>
                            </td>
                            <td>
                                <h:inputText value="#{managerCursoAperturado.sisevaperSemana}"
                                             style="width: 50px;"/>
                            </td>
                            <td>
                                <rich:inputNumberSpinner value="#{managerCursoAperturado.sisevaperOrden}"
                                                         inputStyle="width: 20px;"/>
                            </td>
                            <td>
                                <a4j:commandButton image="/Imagenes/actions/edit_add.png" title="Agregar"
                                                   actionListener="#{managerCursoAperturado.agregar}"
                                                   reRender="tablaSecundaria"
                                                   oncomplete="#{managerCursoAperturado.alert}"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <div style="height: 15px;">
                    <rich:separator height="1"/>
                </div>
                <div>
                    <rich:dataTable id="tablaSecundaria" cellpadding="0" cellspacing="0" border="0" width="100%"
                                    onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                    onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                    value="#{managerCursoAperturado.tablaDetallePromedios}" var="detalle" rows="10">
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Id"/>
                            </f:facet>
                            <h:outputText value="#{detalle.sisevaperId}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Promedio"/>
                            </f:facet>
                            <h:outputText value="#{detalle.sisevaperPromedio}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="C&oacute;digo" escape="false"/>
                            </f:facet>
                            <h:outputText value="#{detalle.sisevaperCodigo}" rendered="#{!detalle.editable}"/>
                            <h:inputText value="#{detalle.sisevaperCodigo}" style="width: 40px;"
                                         rendered="#{detalle.editable}" autocomplete="off"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Nombre"/>
                            </f:facet>
                            <h:outputText value="#{detalle.sisevaperNombre}" rendered="#{!detalle.editable}"/>
                            <h:inputText value="#{detalle.sisevaperNombre}"
                                         rendered="#{detalle.editable}" autocomplete="off"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Peso"/>
                            </f:facet>
                            <h:outputText value="#{detalle.sisevaperPeso}" rendered="#{!detalle.editable}"/>
                            <h:inputText value="#{detalle.sisevaperPeso}" style="width: 20px;"
                                         rendered="#{detalle.editable}" autocomplete="off"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Sem"/>
                            </f:facet>
                            <h:outputText value="#{detalle.sisevaperSemana}" style="width: 20px;"
                                          rendered="#{!detalle.editable}"/>
                            <h:inputText value="#{detalle.sisevaperSemana}" style="width: 20px;"
                                         rendered="#{detalle.editable}" autocomplete="off"/>
                        </h:column>

                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Ord"/>
                            </f:facet>
                            <h:outputText value="#{detalle.sisevaperOrden}" style="width: 20px;"
                                          rendered="#{!detalle.editable}"/>
                            <h:inputText value="#{detalle.sisevaperOrden}" style="width: 20px;"
                                         rendered="#{detalle.editable}" autocomplete="off"/>
                        </h:column>

                        <h:column>
                            <a4j:commandButton actionListener="#{detalle.edit}"
                                               image="/Imagenes/actions/klipper_dock.png"
                                               title="Editar" rendered="#{!detalle.editable}"
                                               reRender="tablaSecundaria"/>
                            <a4j:commandButton actionListener="#{detalle.aceptar}"
                                               image="/Imagenes/actions/fileexport.png"
                                               title="Aceptar" rendered="#{detalle.editable}"
                                               reRender="tablaSecundaria"/>
                            <a4j:commandButton actionListener="#{detalle.cancelar}"
                                               image="/Imagenes/actions/fileclose.png"
                                               title="Cancelar" rendered="#{detalle.editable}"
                                               reRender="tablaSecundaria"/>
                        </h:column>
                        <h:column>
                            <a4j:commandButton image="/Imagenes/actions/edit_remove.png"
                                               actionListener="#{managerCursoAperturado.quitar}"
                                               title="Quitar" reRender="tablaSecundaria">
                                <f:attribute name="id_det" value="#{detalle.sisevaperId}"/>
                            </a4j:commandButton>
                        </h:column>
                    </rich:dataTable>
                </div>
            </rich:panel>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mp2" autosized="true" minWidth="600" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro de Secciones"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp2')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <table width="100%">
                <tr>
                    <td align="right" colspan="2">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerCursoAperturado.insertar}"
                                           action="#{managerCursoAperturado.seleccion}"
                                           reRender="tablaMaster"
                                           oncomplete="alert('Se guardo el registro correctamente');Richfaces.hideModalPanel('mp2')"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                        <h:inputHidden value="#{managerCursoAperturado.modalCursoId}" id="id1"/>
                        <h:inputHidden value="#{managerCursoAperturado.modalPlancurId}" id="idplancur1"/>
                        <h:inputHidden value="#{managerCursoAperturado.modalCurapeId}" id="id_curape1"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%">Ciclo:</td>
                    <td>
                        <h:outputText value="#{managerCursoAperturado.modalCiclo}" id="ciclo1"/>
                    </td>
                </tr>
                <tr>
                    <td>C&oacute;digo de Curso:</td>
                    <td>
                        <h:outputText value="#{managerCursoAperturado.modalCodigo}" id="codigo1"/>
                    </td>
                </tr>
                <tr>
                    <td>Nombre de Curso:</td>
                    <td>
                        <h:outputText value="#{managerCursoAperturado.modalNombre}" id="nombre1"/>
                    </td>
                </tr>
                <tr>
                    <td>Aperturado:</td>
                    <td>
                        <h:selectBooleanCheckbox id="aperturado1"
                                                 value="#{managerCursoAperturado.modalEstaAperturado}"/>
                    </td>
                </tr>
                <tr>
                    <td>Sist. de Evaluaci&oacute;n:</td>
                    <td>
                        <h:outputText value="#{managerCursoAperturado.modalSistemaEvaluacion}" id="sisevaluacion1"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <table width="100%">
                            <tr>
                                <td width="25%">C&oacute;digo de Secci&oacute;n:</td>
                                <td width="30%">Nombre de Secci&oacute;n</td>
                                <td width="25%">N&uacute;mero de Vacantes</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td>
                                    <h:inputText value="#{managerCursoAperturado.secCodigo}"/>
                                </td>
                                <td>
                                    <h:inputText value="#{managerCursoAperturado.secNombre}" style="width: 155px;"/>
                                </td>
                                <td>
                                    <rich:inputNumberSpinner value="#{managerCursoAperturado.secVacantes}"
                                                             style="width: 20px;"/>
                                </td>
                                <td>
                                    <a4j:commandButton image="/Imagenes/actions/edit_add.png" title="Agregar"
                                                       actionListener="#{managerCursoAperturado.agregarSec}"
                                                       reRender="tablaSecundaria1"/>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:dataTable id="tablaSecundaria1" border="0" cellpadding="0" cellspacing="0" width="100%"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        value="#{managerCursoAperturado.tablaSecciones}" var="detalle" rows="10">
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{detalle.sisevaperId}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{detalle.secCodigo}" rendered="#{!detalle.editable}"/>
                                <h:inputText value="#{detalle.secCodigo}" style="width: 40px;"
                                             rendered="#{detalle.editable}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Nombre"/>
                                </f:facet>
                                <h:outputText value="#{detalle.secNombre}" rendered="#{!detalle.editable}"/>
                                <h:inputText value="#{detalle.secNombre}" rendered="#{detalle.editable}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Vacantes"/>
                                </f:facet>
                                <h:outputText value="#{detalle.secVacantes}" rendered="#{!detalle.editable}"/>
                                <h:inputText value="#{detalle.secVacantes}" style="width: 20px;"
                                             rendered="#{detalle.editable}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Activo"/>
                                </f:facet>
                                <h:outputText value="#{detalle.secActivo}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Editar"/>
                                </f:facet>
                                <a4j:commandButton actionListener="#{detalle.editSec}"
                                                   image="/Imagenes/actions/klipper_dock.png"
                                                   title="Editar" rendered="#{!detalle.editable}"
                                                   reRender="tablaSecundaria1"/>
                                <a4j:commandButton actionListener="#{detalle.aceptar}"
                                                   image="/Imagenes/actions/fileexport.png"
                                                   title="Aceptar" rendered="#{detalle.editable}"
                                                   reRender="tablaSecundaria1"/>
                                <a4j:commandButton actionListener="#{detalle.cancelarSec}"
                                                   image="/Imagenes/actions/fileclose.png"
                                                   title="Cancelar" rendered="#{detalle.editable}"
                                                   reRender="tablaSecundaria1"/>
                            </h:column>
                            <h:column>
                                <a4j:commandButton image="/Imagenes/actions/edit_remove.png"
                                                   actionListener="#{managerCursoAperturado.quitarSec}"
                                                   reRender="tablaSecundaria1">
                                    <f:attribute value="#{detalle.sisevaperId}" name="id_det1"/>
                                </a4j:commandButton>
                            </h:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
