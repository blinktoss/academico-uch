<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Edici&oacute;n de Estructura de Pagos</title>
    <script type="text/javascript">
        function printObjectsSelected_estpag(sgcomponent, idAL, nombreAL, codigoAL) {
            idAL.value = sgcomponent.getSelectedItems().pluck('bAluId');
            nombreAL.innerHTML = sgcomponent.getSelectedItems().pluck('bAlumno');
            codigoAL.innerHTML = sgcomponent.getSelectedItems().pluck('bCodigo');
        }
    </script>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="formInput">
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2">
                        <strong>EDICI&Oacute;N DE LAS ESTRUCTURAS DE PAGO DE LOS ALUMNOS DE CURSOS LIBRES</strong>
                    </td>
                    <td width="13%"></td>
                    <td width="47%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="../Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td width="10%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="border-width: 0;cursor: pointer;">
                            <a4j:support event="onclick"
                                         oncomplete="Richfaces.showModalPanel('mpEstructuraNuevo',{top:100})"
                                         actionListener="#{managerCLEditEstPag.nuevo}"
                                         reRender="iAlumno, iDestino, iPlantilla, modelInput"/>
                        </h:graphicImage>
                    </td>
                    <td align=right width="10%">
                        <a4j:commandButton type="button" id="buscar" value="" title="Buscar" reRender="tableData"
                                           actionListener="#{managerCLEditEstPag.validarBusqueda}"
                                           oncomplete="#{managerCLEditEstPag.oncomplete}"
                                           image="/Imagenes/actions/viewmag.png"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="20%" colspan="2"></td>
                    <td width="20%" colspan="2"></td>
                    <td width="60%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">C&oacute;digo:</td>
                    <td width="27%">
                        <h:inputText value="#{managerCLEditEstPag.bCodigo}" style="width : 180px;"/>
                    </td>
                    <td width="13%">&Aacute;rea:</td>
                    <td width="27%">
                        <h:selectOneMenu value="#{managerCLEditEstPag.parametroArea}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEditEstPag.parametroAreas}"/>
                            <a4j:support event="onchange" reRender="v_mod"
                                         actionListener="#{managerCLEditEstPag.verModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Apellido paterno:</td>
                    <td width="27%">
                        <h:inputText value="#{managerCLEditEstPag.bPaterno}" style="width : 180px;"/>
                    </td>
                    <td width="13%">M&oacute;dulo:</td>
                    <td width="27%">
                        <h:selectOneMenu id="v_mod" value="#{managerCLEditEstPag.parametroModulo}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEditEstPag.parametroModulos}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Apellido materno:</td>
                    <td width="27%">
                        <h:inputText value="#{managerCLEditEstPag.bMaterno}" style="width : 180px;"/>
                    </td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Nombres:</td>
                    <td width="27%">
                        <h:inputText value="#{managerCLEditEstPag.bNombre}" style="width : 180px;"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
            </table>
        </h:form>
        <h:form id="tableData" style="width: 100%;">
            <rich:datascroller id="scrollTableMaster" for="tablaMaster" align="right" maxPages="8"/>
            <rich:dataTable id="tablaMaster" rows="10"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            cellpadding="0" cellspacing="0"
                            width="100%" border="0" value="#{managerCLEditEstPag.listaEstructura}"
                            var="estructPag">

                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{estructPag.bCodigo}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Alumno"/>
                    </f:facet>
                    <h:outputText value="#{estructPag.bAlumno}"/>
                </rich:column>
                <rich:column colspan="2">
                    <f:facet name="header">
                        <h:outputText value="M&oacute;dulo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{estructPag.bModulo}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Ver"/>
                    </f:facet>
                    <h:graphicImage value="#{estructPag.imagenVer}"
                                    style="cursor: pointer;"
                                    title="Ver detalle de la Estructura">
                        <a4j:support event="onclick" reRender="tablaMaster"
                                     actionListener="#{estructPag.mostrarDetalle}"/>
                    </h:graphicImage>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <f:param id="estpag_alutarIds" value="#{estructPag.bAlutarIds}"/>
                    <a4j:commandButton image="/Imagenes/actions/delete.gif"
                                       title="Eliminar"
                                       actionListener="#{managerCLEditEstPag.askToDelete}"
                                       oncomplete="Richfaces.showModalPanel('mpEliminar',{width:300, top:200})"
                                       style="cursor: pointer;"/>
                </rich:column>
                <rich:subTable id="subTable" value="#{estructPag.subListaEstructura}"
                               onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                               onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                               var="subEstruct" rendered="#{estructPag.mostrar}">

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Concepto"/>
                        </f:facet>
                        <h:outputText value="#{subEstruct.bSubConcepto}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto a pagar"/>
                        </f:facet>
                        <h:outputText value="#{subEstruct.bSubMonto}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Fecha de pago"/>
                        </f:facet>
                        <h:outputText value="#{subEstruct.bSubFechaPago}">
                            <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy"/>
                        </h:outputText>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Fecha de prorroga"/>
                        </f:facet>
                        <h:outputText value="#{subEstruct.bSubFechaProrroga}">
                            <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy"/>
                        </h:outputText>
                    </rich:column>
                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Abrir/Cerrar pago"/>
                        </f:facet>
                        <a4j:commandButton image="#{subEstruct.bSubImgCandado}"
                                           title="#{subEstruct.bSubTitle}"
                                           reRender="mod_msj"
                                           actionListener="#{managerCLEditEstPag.askToOpenCloseLock}"
                                           oncomplete="Richfaces.showModalPanel('mpCandado',{top:200});">
                            <f:attribute name="estructura_pago_cl" value="#{subEstruct}"/>
                        </a4j:commandButton>
                    </rich:column>
                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Editar"/>
                        </f:facet>
                        <a4j:commandButton image="#{subEstruct.bSubImgKlipper}"
                                           title="Editar estructura"
                                           disabled="#{subEstruct.bSubEditDisabled}"
                                           reRender="mod_monto, mod_pago, mod_prorroga"
                                           actionListener="#{managerCLEditEstPag.openModalToEdit}"
                                           oncomplete="Richfaces.showModalPanel('mpEditar',{top:120})">
                            <f:attribute name="estructura_pago_cl" value="#{subEstruct}"/>
                        </a4j:commandButton>
                    </rich:column>
                </rich:subTable>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpEstructuraNuevo" minWidth="650" autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Nueva Estructura de Pago"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png"
                            style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpEstructuraNuevo')"
                            title="Cerrar"/>
        </f:facet>
        <h:form id="modelInput">
            <table style="width: 100%">
                <tr style=" height: 30px;">
                    <td width="50%">Alumno</td>
                    <td>
                        <h:inputText id="iAlumno" value="#{managerCLEditEstPag.sugAlumno}" style="width: 270px;"/>
                        <rich:suggestionbox id="suggestionEst" var="alum"
                                            for="iAlumno" nothingLabel="Alumno No Encontrado"
                                            suggestionAction="#{managerCLEditEstPag.autocomplete}"
                                            height="150" width="400" cellpadding="2"
                                            cellspacing="2" shadowOpacity="4" shadowDepth="4"
                                            minChars="3" rules="none" zindex="3500"
                                            usingSuggestObjects="true"
                                            reRender="iPlantilla, iMatricula" onsubmit=""
                                            onobjectchange="printObjectsSelected_estpag(#{rich:component('suggestionEst')},#{rich:element('idAL')},#{rich:element('nombreAL')},#{rich:element('codigoAL')});">

                            <h:column>
                                <h:outputText value="#{alum.bAlumno}" style="font-weight: bold;"/>
                            </h:column>
                            <h:column>
                                <h:outputText value="#{alum.bCodigo}" style="font-weight: bold;"/>
                            </h:column>
                        </rich:suggestionbox>
                    </td>
                </tr>
                <tr style="height : 10px; text-align: right;">
                    <td colspan="2">
                        <h:outputText id="codigoAL" value="#{managerCLEditEstPag.sugCodigo}"/> -
                        <h:outputText id="nombreAL" value="#{managerCLEditEstPag.sugAlumno}"/>
                        <h:inputHidden id="idAL" value="#{managerCLEditEstPag.modalId}"/>
                    </td>
                </tr>
                <tr style="height : 30px;">
                    <td>M&oacute;dulo destino</td>
                    <td>
                        <h:selectOneMenu id="iDestino" style="width: 450px;"
                                         value="#{managerCLEditEstPag.modalModulo}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEditEstPag.modalModulos}"/>
                            <a4j:support event="onchange" reRender="iPlantilla,iTaller,iMatricula"
                                         actionListener="#{managerCLEditEstPag.parametrosPlantilla}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height : 30px;">
                    <td>Plantilla de Estructura de Pago</td>
                    <td>
                        <h:selectOneMenu id="iPlantilla" style="width: 240px;"
                                         value="#{managerCLEditEstPag.modalPlantilla}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEditEstPag.modalPlantillas}"/>
                            <a4j:support event="onchange" reRender="iTaller,iMatricula"
                                         actionListener="#{managerCLEditEstPag.parametrosTaller}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height : 30px;">
                    <td>Taller Aperturado</td>
                    <td>
                        <h:selectOneMenu id="iTaller" style="width: 270px;"
                                         value="#{managerCLEditEstPag.modalTaller}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEditEstPag.modalTalleres}"/>
                            <a4j:support event="onchange" reRender="iMatricula"
                                         actionListener="#{managerCLEditEstPag.parametrosMatricula}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height : 30px;">
                    <td>Matriculas del alumno</td>
                    <td>
                        <h:selectOneMenu id="iMatricula" style="width: 270px;"
                                         value="#{managerCLEditEstPag.modalMatricula}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerCLEditEstPag.modalMatriculas}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="2" style="text-align: center">
                        <a4j:commandButton value="Generar estructuras" oncomplete="#{managerCLEditEstPag.oncomplete}"
                                           actionListener="#{managerCLEditEstPag.validarEstructuras}"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpCandado" height="150" minWidth="300" autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Abrir/cerar candado"/>
        </f:facet>
        <h:form id="fromCandado" style="text-align: center">
            <h:outputLabel>
                <h2>Esta seguro de <h:outputText id="mod_msj" value="#{managerCLEditEstPag.bSubMsj}"/></h2>
            </h:outputLabel>
            <a4j:commandButton value="Aceptar" reRender="tableData"
                               oncomplete="#{managerCLEditEstPag.oncomplete}"
                               actionListener="#{managerCLEditEstPag.actualizarEstado}"/>
            <rich:spacer width="20px"/>
            <a4j:commandButton value="Cancelar" onclick="Richfaces.hideModalPanel('mpCandado')"/>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpEliminar" autosized="true" height="130" width="300" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Eliminar"/>
        </f:facet>
        <h:form id="fromEliminar" style="text-align: center">
            <h:outputLabel>
                <h2>Esta seguro de eliminar las estructuras generadas?</h2>
            </h:outputLabel>
            <a4j:commandButton value="Aceptar" reRender="tableData"
                               oncomplete="#{managerCLEditEstPag.oncomplete}"
                               actionListener="#{managerCLEditEstPag.eliminarEstructura}"/>
            <rich:spacer width="20px"/>
            <h:commandButton value="Cancelar" onclick="Richfaces.hideModalPanel('mpEliminar')"/>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpEditar" minWidth="350"
                     autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Editar Datos del Alumno Tarifa"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpEditar')" title="Cerrar"/>
        </f:facet>
        <h:form id="fromEditar">
            <table width="100%">
                <tr>
                    <td width="40%"></td>
                    <td align="right">
                        <a4j:commandButton
                                image="/Imagenes/actions/filesave.png" title="Guardar"
                                actionListener="#{managerCLEditEstPag.editar}"
                                reRender="tablaMaster, scrollTableMaster, subTable"
                                oncomplete="#{managerCLEditEstPag.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="1"/></td>
                </tr>
                <tr style="height: 30px;">
                    <td><h:outputLabel styleClass="label-mpNuevoEstPagAlu" value="Monto:"/></td>
                    <td><h:inputText id="mod_monto" style="width : 180px;"
                                     value="#{managerCLEditEstPag.modalMonto}"/>
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td><h:outputLabel styleClass="label-mpNuevoEstPagAlu" value="Fecha de pago:"/></td>
                    <td><rich:calendar id="mod_pago" datePattern="dd-MM-yyyy" style="width : 200px;"
                                       value="#{managerCLEditEstPag.modalPago}"/>
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td><h:outputLabel styleClass="label-mpNuevoEstPagAlu" value="Fecha de Prorroga:"/></td>
                    <td><rich:calendar id="mod_prorroga" datePattern="dd-MM-yyyy" style="width : 200px;"
                                       value="#{managerCLEditEstPag.modalProrroga}"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
