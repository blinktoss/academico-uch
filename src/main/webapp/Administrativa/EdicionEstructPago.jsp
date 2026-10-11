<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Mantenimiento de Estructura de Pagos de los Alumnos</title>
    <script type="text/javascript">
        function printObjectsSelected_estpag(sgcomponent, idAL, semingAL, espAL, nombreAL, codigoAL) {
            idAL.value = sgcomponent.getSelectedItems().pluck('alumnoId');
            semingAL.value = sgcomponent.getSelectedItems().pluck('modalSemIngreso');
            espAL.value = sgcomponent.getSelectedItems().pluck('modalEspecialidad');
            nombreAL.innerHTML = sgcomponent.getSelectedItems().pluck('parametroPaterno') + ' ' +
                sgcomponent.getSelectedItems().pluck('parametroMaterno') + ', ' +
                sgcomponent.getSelectedItems().pluck('parametroNombre');
            codigoAL.innerHTML = sgcomponent.getSelectedItems().pluck('parametroCodigo');
        }
    </script>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <f:facet name="ESTRUCTURA DE PAGO ALUMNO"/>
        <h:form id="formInput" style="width:100%">
            <table style="width: 100%">
                <tr>
                    <td width="20%" colspan="2"><strong>EDICI&Oacute;N DE LAS ESTRUCTURAS DE PAGO DE LOS
                        ALUMNOS</strong></td>
                    <td width="13%"></td>
                    <td width="47%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="../Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td width="10%" align="right">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" style="cursor: pointer;">
                            <a4j:support event="onclick"
                                         oncomplete="Richfaces.showModalPanel('mpEstructuraNuevo',{top:120})"
                                         actionListener="#{managerEdicionEstructPago.nuevo}"
                                         reRender="iAlumno, iDestino, iPlantilla, modelInput"/>
                        </h:graphicImage>
                    </td>
                    <td align=right width="10%">
                        <a4j:commandButton type="button" id="buscar" value="" title="Buscar" reRender="tableData"
                                           actionListener="#{managerEdicionEstructPago.validarBusqueda}"
                                           oncomplete="#{managerEdicionEstructPago.oncomplete}"
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
                        <h:inputText value="#{managerEdicionEstructPago.parametroCodigo}" style="width : 180px;"/>
                    </td>
                    <td width="13%">Semestre Estructura:</td>
                    <td width="27%">
                        <h:selectOneMenu value="#{managerEdicionEstructPago.parametroSemestre}" style="width : 180px;">
                            <f:selectItems value="#{managerEdicionEstructPago.parametroSemestres}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Apellido paterno:</td>
                    <td width="27%">
                        <h:inputText value="#{managerEdicionEstructPago.parametroPaterno}" style="width : 180px;"/>
                    </td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Apellido materno:</td>
                    <td width="27%">
                        <h:inputText value="#{managerEdicionEstructPago.parametroMaterno}" style="width : 180px;"/>
                    </td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr style="height : 20px;">
                    <td width="13%">Nombres:</td>
                    <td width="27%">
                        <h:inputText value="#{managerEdicionEstructPago.parametroNombre}" style="width : 180px;"/>
                    </td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
                <tr>
                    <td colspan="7" width="100%"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="13%"></td>
                    <td width="27%"></td>
                    <td width="20%"></td>
                </tr>
            </table>
        </h:form>
        <h:form id="tableData" style="width : 100%;">
            <rich:datascroller id="scrollTableMaster" for="tablaMaster" align="right" maxPages="8"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerEdicionEstructPago.listaEstructura}"
                            var="estructPag">
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Id"/>
                    </f:facet>
                    <h:outputText value="#{estructPag.alumnoId}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{estructPag.parametroCodigo}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Alumno"/>
                    </f:facet>
                    <h:outputText
                            value="#{estructPag.parametroPaterno} #{estructPag.parametroMaterno}, #{estructPag.parametroNombre}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Especialidad"/>
                    </f:facet>
                    <h:outputText value="#{estructPag.especialidad}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Semestre Ingreso"/>
                    </f:facet>
                    <h:outputText value="#{estructPag.semestreIngreso}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Semestre Estructura"/>
                    </f:facet>
                    <h:outputText value="#{estructPag.semestreEstructura}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Ver"/>
                    </f:facet>
                    <f:param id="p_ver" value="#{estructPag.estadoVer}"/>
                    <h:graphicImage value="#{estructPag.imagenVer}"
                                    style="cursor: pointer;"
                                    title="Ver detalle de la Estructura">
                        <a4j:support event="onclick" reRender="scrollTableMaster,tablaMaster"
                                     actionListener="#{estructPag.mostrarDetalle}"/>
                    </h:graphicImage>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Imprimir"/>
                    </f:facet>
                    <h:outputLink value="#{request.contextPath}/report" target="_blank"
                                  title="Imprimir historial de pago">
                        <f:param name="type" value="historial-pagos"/>
                        <f:param name="aluCodigo" value="#{estructPag.parametroCodigo}"/>
                        <f:param name="semId" value="#{estructPag.semestreEstructuraId}"/>
                        <h:graphicImage value="/Imagenes/actions/fileprint.png"
                                        style="border-width: 0; cursor: pointer;"/>
                    </h:outputLink>
                </rich:column>
                <rich:column style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Eliminar"/>
                    </f:facet>
                    <f:param id="estpag_alutarIds" value="#{estructPag.alutarIds}"/>
                    <a4j:commandButton image="/Imagenes/actions/no.png" title="Eliminar"
                                       actionListener="#{managerEdicionEstructPago.elimParam}"
                                       oncomplete="Richfaces.showModalPanel('mpEliminar',{width:300, top:200})"/>
                </rich:column>

                <rich:subTable id="subTable" value="#{estructPag.subListaEstructura}"
                               var="subEstruct" rendered="#{estructPag.mostrar}"
                               onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                               onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'">
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Concepto"/>
                        </f:facet>
                        <h:outputText value="#{subEstruct.subConcepto}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto a pagar"/>
                        </f:facet>
                        <h:outputText value="#{subEstruct.subMonto}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Fecha de pago"/>
                        </f:facet>
                        <h:outputText value="#{subEstruct.subFechaPago}">
                            <f:convertDateTime pattern="dd 'de' MMMMMM 'del' yyyy"/>
                        </h:outputText>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Fecha de prorroga"/>
                        </f:facet>
                        <h:outputText value="#{subEstruct.subFechaProrroga}">
                            <f:convertDateTime pattern="dd 'de' MMMMMM 'del' yyyy"/>
                        </h:outputText>
                    </rich:column>
                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Abrir/Cerrar pago"/>
                        </f:facet>
                        <f:param id="alutar_id" value="#{subEstruct.subAlutarId}"/>
                        <f:param id="alutar_estado" value="#{subEstruct.subAlutarEstado}"/>
                        <f:param id="mensaje" value="#{subEstruct.subMensaje}"/>
                        <a4j:commandButton image="#{subEstruct.subImgCandado}"
                                           title="#{subEstruct.subTitle}"
                                           reRender="mod_msj"
                                           actionListener="#{managerEdicionEstructPago.salvaParam}"
                                           oncomplete="Richfaces.showModalPanel('mpCandado',{width:300, top:200})"/>
                    </rich:column>
                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Editar"/>
                        </f:facet>
                        <a4j:commandButton image="#{subEstruct.subImgKlipper}"
                                           title="Editar estructura"
                                           disabled="#{subEstruct.subEditDisabled}"
                                           reRender="mod_monto, mod_pago, mod_prorroga"
                                           actionListener="#{managerEdicionEstructPago.salvaEditar}"
                                           oncomplete="Richfaces.showModalPanel('mpEditar',{top:200})"/>
                    </rich:column>
                    <rich:column colspan="4" style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Compromisos de pago"/>
                        </f:facet>
                        <f:param id="est_desc" value="#{subEstruct.subConcepto}"/>
                        <a4j:commandButton image="#{subEstruct.subImgCalendar}"
                                           title="Compromisos de pago"
                                           disabled="#{subEstruct.subEditDisabled}"
                                           reRender="fromProrroga, tablaProrroga, scrollProrroga, calProrroga, proconcep, propago, proprorroga"
                                           actionListener="#{managerEdicionEstructPago.salvaCompromiso}"
                                           oncomplete="Richfaces.showModalPanel('mpProrroga',{width:450, top:120})"/>
                    </rich:column>
                </rich:subTable>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mpEstructuraNuevo" minHeight="210" width="470" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Nueva Estructura de Pago"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor: pointer"
                            onclick="Richfaces.hideModalPanel('mpEstructuraNuevo')" title="Cerrar"/>
        </f:facet>
        <h:form id="modelInput">
            <table width="100%">
                <tr style="height: 30px;">
                    <td width="50%">
                        <h:outputLabel value="Alumno" style="width:200px"/>
                    </td>
                    <td>
                        <h:inputText id="iAlumno" value="#{managerEdicionEstructPago.sugAlumno}" style="width: 250px;"/>
                        <rich:suggestionbox id="suggestionEst" var="alum"
                                            for="iAlumno" nothingLabel="Alumno No Encontrado"
                                            suggestionAction="#{managerEdicionEstructPago.autocomplete}"
                                            height="150" width="400" cellpadding="2"
                                            cellspacing="2" shadowOpacity="4" shadowDepth="4"
                                            minChars="3" rules="none" zindex="3500"
                                            usingSuggestObjects="true"
                                            onobjectchange="printObjectsSelected_estpag(#{rich:component('suggestionEst')},#{rich:element('idAL')},#{rich:element('semingAL')},#{rich:element('espAL')},#{rich:element('nombreAL')},#{rich:element('codigoAL')});">

                            <h:column>
                                <h:outputText
                                        value="#{alum.parametroPaterno} #{alum.parametroMaterno}, #{alum.parametroNombre}"
                                        style="font-weight: bold;"/>
                            </h:column>
                            <h:column>
                                <h:outputText value="#{alum.parametroCodigo}" style="font-weight: bold;"/>
                            </h:column>
                            <a4j:support event="onselect" reRender="iPlantilla"
                                         actionListener="#{managerEdicionEstructPago.parametrosEstructura}"/>
                        </rich:suggestionbox>
                    </td>
                </tr>
                <tr style="height: 10px; text-align: right;">
                    <td colspan="3">
                        <h:outputText id="codigoAL" value="#{managerEdicionEstructPago.sugCodigo}"/> -
                        <h:outputText id="nombreAL" value="#{managerEdicionEstructPago.sugAlumno}"/>
                        <h:inputHidden id="idAL" value="#{managerEdicionEstructPago.modalId}"/>
                        <h:inputHidden id="semingAL" value="#{managerEdicionEstructPago.modalSemIngreso}"/>
                        <h:inputHidden id="espAL" value="#{managerEdicionEstructPago.modalEspecialidad}"/>
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td width="50%">
                        <h:outputLabel value="Semestre destino"/>
                    </td>
                    <td>
                        <h:selectOneMenu id="iDestino" style="width: 200px;"
                                         value="#{managerEdicionEstructPago.modalSemestre}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerEdicionEstructPago.modalSemestres}"/>
                            <a4j:support event="onchange" reRender="iPlantilla"
                                         actionListener="#{managerEdicionEstructPago.parametrosEstructura}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td width="50%">
                        <h:outputLabel value="Plantilla de Estructura de Pago"/>
                    </td>
                    <td>
                        <h:selectOneMenu id="iPlantilla" style="width: 200px;"
                                         value="#{managerEdicionEstructPago.modalPlantilla}">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerEdicionEstructPago.modalPlantillas}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr style="height: 30px"></tr>
                <tr style="height: 30px">
                    <td colspan="2">
                        <a4j:commandButton value="Generar estructuras"
                                           oncomplete="#{managerEdicionEstructPago.oncomplete}"
                                           actionListener="#{managerEdicionEstructPago.validarEstructuras}"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpCandado" autosized="true" minWidth="100" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Abrir/Cerar Candado"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpCandado')" title="Cerrar"/>
        </f:facet>
        <h:form id="fromCandado" style="text-align: center">
            <h:outputLabel>
                <h2>Esta seguro de <h:outputText id="mod_msj"
                                                 value="#{managerEdicionEstructPago.subMensaje}"/></h2>
            </h:outputLabel>
            <a4j:commandButton value="Aceptar" reRender="tableData"
                               oncomplete="#{managerEdicionEstructPago.oncomplete}"
                               actionListener="#{managerEdicionEstructPago.actualizarEstado}"/>
            <rich:spacer width="20px"/>
            <a4j:commandButton value="Cancelar" onclick="Richfaces.hideModalPanel('mpCandado')"/>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpEliminar" autosized="true" minWidth="100" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Eliminar"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png"
                            style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpEliminar')"
                            title="Cerrar"/>
        </f:facet>
        <h:form id="fromEliminar" style="text-align: center">
            <h:outputLabel>
                <h2>&iquest;Est&aacute; seguro de eliminar las estructuras generadas&quest;</h2>
            </h:outputLabel>
            <a4j:commandButton value="Aceptar" reRender="tableData"
                               oncomplete="#{managerEdicionEstructPago.oncomplete}"
                               actionListener="#{managerEdicionEstructPago.eliminarEstructura}"/>
            <rich:spacer width="20px"/>
            <a4j:commandButton value="Cancelar" onclick="Richfaces.hideModalPanel('mpEliminar')"/>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpEditar" minWidth="350" autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Editar Datos del Alumno Tarifa"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png"
                            style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpEditar')"
                            title="Cerrar"/>
        </f:facet>
        <h:form id="fromEditar">
            <table width="100%">
                <tr>
                    <td width="40%"></td>
                    <td align="right">
                        <a4j:commandButton
                                image="/Imagenes/actions/filesave.png" title="Guardar"
                                actionListener="#{managerEdicionEstructPago.editar}"
                                reRender="tablaMaster, scrollTableMaster, subTable"
                                oncomplete="#{managerEdicionEstructPago.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr style="height: 30px;">
                    <td><h:outputLabel styleClass="label-mpNuevoEstPagAlu" value="Monto:"/></td>
                    <td><h:inputText id="mod_monto" style="width : 180px;"
                                     value="#{managerEdicionEstructPago.modalMonto}"/>
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td><h:outputLabel styleClass="label-mpNuevoEstPagAlu" value="Fecha de pago:"/></td>
                    <td><rich:calendar id="mod_pago" datePattern="dd-MM-yyyy"
                                       style="width: 200px;" enableManualInput="true"
                                       value="#{managerEdicionEstructPago.modalPago}"/>
                    </td>
                </tr>
                <tr style="height: 30px;">
                    <td><h:outputLabel styleClass="label-mpNuevoEstPagAlu" value="Fecha de Prorroga:"/></td>
                    <td><h:outputText id="mod_prorroga" value="#{managerEdicionEstructPago.modalProrroga}">
                        <f:convertDateTime pattern="dd-MM-yyyy"/>
                    </h:outputText>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpProrroga" minWidth="450" autosized="true" zindex="2000">
        <div id="contentDivProrroga">
            <f:facet name="header">
                <h:outputText value="Editar compromisos de pago"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpProrroga')" title="Cerrar"/>
            </f:facet>
            <h:form id="fromProrrogaActions" style="width: 100%;">
                <table style="width: 100%">
                    <tr>
                        <td width="28%"></td>
                        <td></td>
                        <td align="right">
                            <a4j:commandButton
                                    actionListener="#{managerEdicionEstructPago.guardarCompromiso}"
                                    oncomplete="#{managerEdicionEstructPago.oncomplete}"
                                    image="/Imagenes/actions/filesave.png" title="Guardar"
                                    reRender="tablaMaster, scrollTableMaster, calProrroga, comProrroga"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="7" width="100%"><rich:separator height="1"/></td>
                    </tr>
                    <tr style="height : 20px;">
                        <td width="13%">Concepto de pago:</td>
                        <td>
                            <h:outputLabel id="proconcep" value="#{managerEdicionEstructPago.proInfoDescConcepto}"/>
                        </td>
                    </tr>
                    <tr style="height : 20px;">
                        <td width="13%">Fecha de pago:</td>
                        <td>
                            <h:outputLabel id="propago" value="#{managerEdicionEstructPago.proInfoFechaPago}">
                                <f:convertDateTime pattern="dd  'de'  MMMMMM  'del'  yyyy"/>
                            </h:outputLabel>
                        </td>
                    </tr>
                    <tr style="height : 20px;">
                        <td width="13%">Fecha de prorroga:</td>
                        <td>
                            <h:outputLabel id="proprorroga" value="#{managerEdicionEstructPago.proInfoFechaProrroga}">
                                <f:convertDateTime pattern="dd 'de' MMMMMM 'del' yyyy"/>
                            </h:outputLabel>
                        </td>
                    </tr>
                    <tr style="height : 20px;">
                        <td width="13%">Nueva Fecha de Compromiso:</td>
                        <td>
                            <rich:calendar id="calProrroga" datePattern="dd-MM-yyyy"
                                           value="#{managerEdicionEstructPago.proParamFecha}"/>
                        </td>
                    </tr>
                    <tr style="height : 20px;">
                        <td width="13%">Comentario:</td>
                        <td>
                            <h:inputTextarea id="comProrroga" rows="3" cols="20"
                                             value="#{managerEdicionEstructPago.proParamComentario}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="7" width="100%"><rich:separator height="1"/></td>
                    </tr>
                </table>
            </h:form>
            <h:form id="fromProrroga" style="width: 100%;">
                <rich:datascroller id="scrollProrroga" for="tablaProrroga" align="right" maxPages="3"/>
                <rich:dataTable id="tablaProrroga" rows="10" cellpadding="0" cellspacing="0"
                                onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                width="100%" border="0" value="#{managerEdicionEstructPago.proListaProrrogas}"
                                var="pro">
                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="N&uacute;mero" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{pro.proNumero}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Fecha"/>
                        </f:facet>
                        <h:outputText value="#{pro.proFecha}">
                            <f:convertDateTime pattern="dd 'de' MMMMMM 'del' yyyy"/>
                        </h:outputText>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Comentario"/>
                        </f:facet>
                        <h:outputText value="#{pro.proComentario}"/>
                    </rich:column>
                </rich:dataTable>
            </h:form>
        </div>
    </rich:modalPanel>
</f:view>
</body>
</html>
