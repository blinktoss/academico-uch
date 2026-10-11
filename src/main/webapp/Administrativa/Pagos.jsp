<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Pagos</title>
    <script type="text/javascript">
        function printObjectsSelected(output, sgcomponent, output2, nMonto, nAdeuda) {
            output.innerHTML = sgcomponent.getSelectedItems().pluck('desc');
            output2.value = sgcomponent.getSelectedItems().pluck('id');
            nMonto.value = sgcomponent.getSelectedItems().pluck('monto');
            nAdeuda.innerHTML = sgcomponent.getSelectedItems().pluck('monto')
        }
    </script>
</head>
<body onload="document.getElementById('form1:codigoAlumno_cod').focus()">
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%;">
                <tr>
                    <td><strong>PAGOS</strong></td>
                    <td align="right" colspan="6">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading2.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td>
                        <h:graphicImage value="/Imagenes/actions/filenew.png"
                                        style="cursor: pointer;" title="Nuevo Pago"
                                        rendered="#{managerPago.mostrarAccionNuevo}">
                            <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mpNewPayments',{top:80})"
                                         actionListener="#{managerPago.nuevo}"
                                         reRender="objects,nConpag,nAdeuda,nTipoCliente,nClienteFac,nRazonSocial,nTiponj,nDniRuc,
                                                   nProcedencia,nNumComprobante0,nNumComprobante,nCalendar3,nMontoPago,nValorNuevo,nSaldo,nDes,nNomAlu
                                                   ,nCodAlu,nIdAlu,nTipoComprobante,nCalendar5,nNumVou,nCodAge"/>
                        </h:graphicImage>
                    </td>
                    <td>
                        <h:graphicImage value="/Imagenes/actions/fileprint.png"
                                        style="cursor: pointer;" title="Imprimir Listado">
                            <a4j:support event="onclick" oncomplete="#{managerPago.alertaCaja}"
                                         actionListener="#{managerPago.imprimirListado}"/>
                        </h:graphicImage>
                    </td>
                    <td>
                        <a4j:commandButton id="buscar" action="#{managerPago.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png" title="Buscar"
                                           reRender="tablaMaster,paginacion"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="10"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td>Tipo de pago:</td>
                    <td>
                        <h:selectOneMenu value="#{managerPago.paramListaPagoId}" style="width: 180px;">
                            <f:selectItems value="#{managerPago.comboCatalogoListaPagos}"/>
                            <a4j:support event="onchange" reRender="conceptoPagoBuscar"
                                         actionListener="#{managerPago.cambiarTipoPago}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="40%">
                    </td>
                    <td align="right"></td>
                    <td align="right"></td>
                </tr>
                <tr>
                    <td>Ap. Paterno:</td>
                    <td>
                        <h:inputText value="#{managerPago.paramAluApPaterno}" style="width: 180px;"/>
                    </td>
                    <td width="40%"></td>
                    <td align="right">Ap. Materno:</td>
                    <td>
                        <h:inputText value="#{managerPago.paramAluApMaterno}" style="width: 180px;"/>
                    </td>
                </tr>
                <tr>
                    <td>C&oacute;digo de Alumno:</td>
                    <td>
                        <h:inputText id="codigoAlumno_cod" value="#{managerPago.paramAluCodigo}" style="width: 180px;"/>
                    </td>
                    <td width="40%"></td>
                    <td align="right">Concepto de Pago:</td>
                    <td>
                        <h:selectOneMenu id="conceptoPagoBuscar" value="#{managerPago.paramConceptoPagoId}"
                                         style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerPago.comboConceptoPago}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Estado:</td>
                    <td>
                        <h:selectOneMenu id="iIdTipo" value="#{managerPago.paramEstadoId}" style="width: 180px;">
                            <f:selectItem itemValue="-1" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerPago.comboCatalogoEstado}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="40%">
                    </td>
                    <td align="right">Semestre:</td>
                    <td>
                        <h:selectOneMenu value="#{managerPago.paramSemestreId}" style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                            <f:selectItems value="#{managerPago.comboSemestre}"/>
                        </h:selectOneMenu>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td>Facultad:</td>
                    <td>
                        <h:selectOneMenu id="FacuBuscar" value="#{managerPago.paramFacId}" style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todos ]"/>
                            <f:selectItems value="#{managerPago.comboFacultadBuscar}"/>
                            <a4j:support event="onchange" reRender="EspeBuscar"
                                         actionListener="#{managerPago.cambiarEspecialidadBuscar}"/>
                        </h:selectOneMenu>
                    </td>
                    <td width="40%"></td>
                    <td align="right">Especialidad:</td>
                    <td>
                        <h:selectOneMenu id="EspeBuscar" value="#{managerPago.paramEspId}" style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                            <f:selectItems value="#{managerPago.comboEspecialidadBuscar}"/>
                        </h:selectOneMenu>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td>Desde:</td>
                    <td><rich:calendar inputStyle="width: 160px;" value="#{managerPago.paramFechaDesde}"
                                       datePattern="yyyy-MM-dd" showWeeksBar="false" enableManualInput="true"/>
                    </td>
                    <td width="40%"></td>
                    <td align="right">Hasta:</td>
                    <td><rich:calendar inputStyle="width: 160px;" value="#{managerPago.paramFechaHasta}"
                                       datePattern="yyyy-MM-dd" showWeeksBar="false" enableManualInput="true"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="10"><rich:separator height="1"/></td>
                </tr>
            </table>
        </h:form>
        <h:form style="width: 100%;">
            <%-- Server-side pager styled like rich:datascroller: it reuses the skin classes
                 (rich-datascr-*) that RichFaces emits on this page for the detail scroller
                 (barraSecundaria). If that datascroller is ever removed, these classes are no
                 longer generated and this pager loses its skin styling. Glyphs are HTML entities
                 rendered through h:outputText escape="false" (entities placed in a component's
                 value attribute get re-escaped by the renderer and show up as literal text). --%>
            <h:panelGroup id="paginacion" layout="block" style="text-align: right; padding: 0;">
                <h:outputText value="#{managerPago.totalCount}"
                              rendered="#{managerPago.totalCount gt 0}">
                    <f:convertNumber groupingUsed="true" integerOnly="true"/>
                </h:outputText>
                <h:outputText value="&nbsp;registros" escape="false" style="margin-right: 8px;"/>
                <%-- No cellpadding/cellspacing/border: the datascroller renderer omits them too,
                     so the browser defaults (2/1) reproduce the native scroller spacing. --%>
                <table class="rich-datascr rich-dtascroller-table" align="right" style="border-spacing: 1px;">
                    <tbody>
                    <tr>
                        <td class="rich-datascr-button">
                            <a4j:commandLink actionListener="#{managerPago.firstPage}" title="Primera"
                                             reRender="tablaMaster,paginacion"
                                             rendered="#{managerPago.currentPage gt 1}"
                                             style="display: block; text-decoration: none; color: #{a4jSkin.generalTextColor};">
                                <h:outputText value="&#171;&#171;" escape="false"/>
                            </a4j:commandLink>
                            <h:outputText value="&#171;&#171;" escape="false"
                                          styleClass="rich-datascr-button-dsbld"
                                          rendered="#{managerPago.currentPage le 1}"/>
                        </td>
                        <td class="rich-datascr-button">
                            <a4j:commandLink actionListener="#{managerPago.prevPage}" title="Anterior"
                                             reRender="tablaMaster,paginacion"
                                             rendered="#{managerPago.currentPage gt 1}"
                                             style="display: block; text-decoration: none; color: #{a4jSkin.generalTextColor};">
                                <h:outputText value="&#171;" escape="false"/>
                            </a4j:commandLink>
                            <h:outputText value="&#171;" escape="false"
                                          styleClass="rich-datascr-button-dsbld"
                                          rendered="#{managerPago.currentPage le 1}"/>
                        </td>
                        <td class="rich-datascr-button"></td>
                        <td style="padding: 0 4px;">
                            <h:outputText value="#{managerPago.currentPage} / #{managerPago.totalPages}"/>
                        </td>
                        <td class="rich-datascr-button"></td>
                        <td class="rich-datascr-button">
                            <a4j:commandLink actionListener="#{managerPago.nextPage}" title="Siguiente"
                                             reRender="tablaMaster,paginacion"
                                             rendered="#{managerPago.currentPage lt managerPago.totalPages}"
                                             style="display: block; text-decoration: none; color: #{a4jSkin.generalTextColor};">
                                <h:outputText value="&#187;" escape="false"/>
                            </a4j:commandLink>
                            <h:outputText value="&#187;" escape="false"
                                          styleClass="rich-datascr-button-dsbld"
                                          rendered="#{managerPago.currentPage ge managerPago.totalPages}"/>
                        </td>
                        <td class="rich-datascr-button">
                            <a4j:commandLink actionListener="#{managerPago.lastPage}" title="Final"
                                             reRender="tablaMaster,paginacion"
                                             rendered="#{managerPago.currentPage lt managerPago.totalPages}"
                                             style="display: block; text-decoration: none; color: #{a4jSkin.generalTextColor};">
                                <h:outputText value="&#187;&#187;" escape="false"/>
                            </a4j:commandLink>
                            <h:outputText value="&#187;&#187;" escape="false"
                                          styleClass="rich-datascr-button-dsbld"
                                          rendered="#{managerPago.currentPage ge managerPago.totalPages}"/>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </h:panelGroup>
            <rich:dataTable id="tablaMaster" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerPago.listaAlumno}" var="alu">
                <rich:column>
                    <f:facet name="header">
                        <!-- Semestre o Nro-Comprobante -->
                        <h:outputText value="#{managerPago.cabecera1}"/>
                    </f:facet>

                    <h:outputText value="#{alu.rowSemestreOrNroComp}"/>

                    <h:inputHidden value="#{alu.facultad}"/>
                    <h:inputHidden value="#{alu.alumnoId}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <!-- Especialidad o Tipo -->
                        <h:outputText value="#{managerPago.cabecera2}"/>
                    </f:facet>
                    <h:outputText value="#{alu.rowEspecialidadOrTipo}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Cod Alumno"/>
                    </f:facet>
                    <h:outputText value="#{alu.rowCodigoAlu}"/>
                    <h:inputHidden value="#{alu.alutarDniRuc}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Apellidos y Nombres"/>
                    </f:facet>

                    <h:outputText value="#{alu.rowNombres}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Concepto"/>
                    </f:facet>

                    <h:outputText value="#{alu.rowConceptoPago}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Monto"/>
                    </f:facet>

                    <h:outputText value="#{alu.rowMonto}" style="font-size:14px;"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="F. Registro"/>
                    </f:facet>

                    <h:outputText value="#{alu.alutarFecPago}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <!-- F. de Pro. o Fecha de Pago -->
                        <h:outputText value="#{managerPago.cabecera3}"/>
                    </f:facet>

                    <h:outputText value="#{alu.rowFecProOrFecComprPago}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <!-- Est. o Pago -->
                        <h:outputText value="#{managerPago.cabecera4}"/>
                    </f:facet>

                    <h:outputText value="#{alu.rowEstadoOrComprPagoMonto}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Adeuda"/>
                    </f:facet>

                    <h:outputText value="#{alu.rowAdeuda}" style="font-size:14px; font-family:verdana"/>
                </rich:column>

                <%-- Dual-purpose column: expander for INTERNOS, voucher type for EXTERNOS.
                     CURSOS LIBRES rows are tariffs without voucher context, so neither applies. --%>
                <rich:column style="text-align: center;"
                             rendered="#{managerPago.viewDetail || managerPago.mostrarAccionesComprobante}">
                    <f:facet name="header">
                        <h:outputText value="Tipo Comprobante" rendered="#{!managerPago.viewDetail}"/>
                    </f:facet>
                    <h:outputText value="#{alu.comprobanteTipo}" rendered="#{!managerPago.viewDetail}"/>

                    <h:graphicImage value="#{alu.imagen}" style="cursor: pointer" rendered="#{managerPago.viewDetail}">
                        <a4j:support event="onclick" actionListener="#{alu.verSubTabla}" reRender="tablaMaster">
                            <f:attribute value="#{alu.flag}" name="flag"/>
                            <f:attribute value="#{alu.alutarId}" name="id_x"/>
                        </a4j:support>
                    </h:graphicImage>
                </rich:column>

                <%-- Voucher actions (edit/void/delete/print) only make sense on EXTERNOS rows,
                     which ARE vouchers; CL rows are tariffs (collection deferred, UC-CSH-005). --%>
                <rich:column style="text-align: center;" rendered="#{managerPago.mostrarAccionesComprobante}">
                    <f:facet name="header">
                        <h:outputText value="Acciones"/>
                    </f:facet>
                    <f:param id="id_z" value="#{alu.zcComprobanteId}"/>
                    <h:panelGrid columns="4" style="width: 100%;">
                        <h:graphicImage value="/Imagenes/actions/pencil.png" style="cursor: pointer" title="Editar"
                                        rendered="#{managerPago.mostrarAccionPersonalizar}">
                            <a4j:support event="onclick"
                                         oncomplete="Richfaces.showModalPanel('mpx3',{width:100, top:100})"
                                         actionListener="#{managerPago.editarComprobante}"
                                         reRender="zNombre,zMonto,zNumCom,zFechaPag,zConPag,zConPagDet,zProcedencia">
                                <f:attribute name="zc_comprobante" value="#{alu}"/>
                            </a4j:support>
                        </h:graphicImage>

                        <h:commandButton image="/Imagenes/actions/anular_.gif" title="Anular"
                                         actionListener="#{managerPago.anularPago}"
                                         action="#{managerPago.seleccionar}"
                                         onclick="return (confirm('¿Esta realmente seguro de Anular el comprobante?'));"/>

                        <h:commandButton image="/Imagenes/actions/no.gif" title="Eliminar"
                                         actionListener="#{managerPago.eliminarPago}"
                                         action="#{managerPago.seleccionar}"
                                         onclick="return (confirm('¿Esta realmente seguro de Eliminar el comprobante?'));"/>

                        <a4j:commandButton image="/Imagenes/actions/fileprint.png" title="Imprimir"
                                           actionListener="#{managerPago.imprimirComprobante}"
                                           oncomplete="#{managerPago.jsImpresion}"/>
                    </h:panelGrid>
                </rich:column>

                <rich:column style="text-align: center;"
                             rendered="#{managerPago.mostrarFicha && managerPago.viewDetail}">
                    <%-- For Internal Payments and Users Lvl 0 --%>
                    <a4j:commandButton image="/Imagenes/actions/carnet.gif" title="Pago Carnet"
                                       actionListener="#{managerPago.carnet}" reRender="formPagoCarnet"
                                       onclick="Richfaces.showModalPanel('modalPagoCarnet',{top:80});">
                        <f:attribute name="p_alu_id" value="#{alu.alumnoId}"/>
                    </a4j:commandButton>

                    <rich:spacer width="8px"/>

                    <a4j:commandButton image="/Imagenes/actions/mora.png" title="Mora"
                                       actionListener="#{managerPago.mora}" reRender="formPagoCarnet"
                                       onclick="Richfaces.showModalPanel('modalPagoCarnet',{top:80});">
                        <f:attribute name="p_alu_id" value="#{alu.alumnoId}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:column rendered="#{managerPago.mostrarAccionPersonalizar}" style="text-align: center;">
                    <f:facet name="header">
                        <h:outputText value="Personalizar"/>
                    </f:facet>
                    <a4j:commandButton image="#{alu.imagenP2}" title="Personalizar de Pagos"
                                       disabled="#{alu.disabledP2}"
                                       oncomplete="Richfaces.showModalPanel('mpx2',{top:100})"
                                       actionListener="#{managerPago.personalizar}"
                                       reRender="xId,xIdNombre,xSem,xFac,xEsp,xCodigo,xNombre,xConPag,xConPagDet,xSaldo,xAdeuda,iTipoCliente,iNumComprobante,iDniRuc,nValorNuevo,PerMonto,PerFechaPag,PerFechaPro">
                        <f:attribute name="alu_s" value="#{alu}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:column style="text-align: center;" rendered="#{managerPago.viewDetail}">
                    <a4j:commandButton image="/Imagenes/actions/mat_extemp.gif"
                                       title="Cancelar Mat. Extemporanea"
                                       oncomplete="Richfaces.showModalPanel('modal2',{top:100})"
                                       actionListener="#{managerPago.matExtemporanea}"
                                       reRender="formMatExtemporanea">
                        <f:attribute name="p_alu_id" value="#{alu.alumnoId}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:column style="text-align: center;" rendered="#{managerPago.viewDetail}">
                    <a4j:commandButton image="#{alu.imagenP}" title="Registro de Pagos" disabled="#{alu.disabledP}"
                                       oncomplete="Richfaces.showModalPanel('mp',{top:50})"
                                       actionListener="#{managerPago.pagar}"
                                       reRender="iId,iIdNombre,iSem,iFac,iEsp,iCodigo,iNombre,iConPag,iConPagDet,
                                               iMonto,iFechaPag,iFechaPro,iSaldo,iAdeuda,iTipoCliente,iRazonSocial,iTiponj,
                                               iProcedencia,iNumComprobante0,iNumComprobante,iCalendar3,iMontoPago,iMatExpo,chkMatExpo,
                                               iMoraPago,chkMora,iClienteFac,iRazonSocial,iTipoCliente,iNumComprobante,iDniRuc,
                                               nValorNuevo,iTipoComprobante,iNumVou,iCalendar6,iCodAge">
                        <f:attribute name="alu_s" value="#{alu}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:subTable id="subtable" rendered="#{alu.ver}"
                               onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                               onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                               value="#{alu.detallesPagosInternos}" var="det">
                    <a4j:region rendered="false">
                        <rich:column>
                            <f:facet name="header">
                                <h:outputText value="Id Com"/>
                            </f:facet>
                            <h:outputText value="#{det.detCompagId}"/>
                        </rich:column>
                    </a4j:region>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Fecha de Pago"/>
                        </f:facet>
                        <h:outputText value="#{det.detCompagFechaPago}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Nombre"/>
                        </f:facet>
                        <h:outputText value="#{det.detNombrePagador}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Tipo de Comp."/>
                        </f:facet>
                        <h:outputText value="#{det.detCompagTipoCompValue}"/>
                        <h:inputHidden value="#{det.detCompagTipoCompId}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="N&deg; de Comp." escape="false"/>
                        </f:facet>
                        <h:outputText value="#{det.detCompagNro}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Monto de Pago"/>
                        </f:facet>
                        <h:outputText value="#{det.detCompagMonto}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Tipo Pago"/>
                        </f:facet>
                        <h:outputText value="#{det.detTipoPago}"/>
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Opciones"/>
                        </f:facet>
                        <f:param id="id_z" value="#{det.detCompagId}"/>
                        <f:param id="fecha_pag_z" value="#{det.detCompagFechaPago}"/>
                        <f:param id="nombre_z" value="#{det.detNombrePagador}"/>
                        <f:param id="num_com_z" value="#{det.detCompagNro}"/>
                        <f:param id="monto_z" value="#{det.detCompagMonto}"/>
                        <f:param id="con_pag_z" value="#{alu.conceptoPagoDesc}"/>
                        <f:param id="con_pag_det_z" value="#{alu.rowConceptoPago}"/>
                        <f:param id="id_tipo_com_z" value="#{det.detCompagTipoCompId}"/>

                        <f:param id="tip_com_z" value="#{det.detCompagTipoDoc}"/>
                        <f:param id="num_vou_z" value="#{det.detVoucherNroOper}"/>
                        <f:param id="fec_vou_z" value="#{det.detVoucherFecha}"/>
                        <f:param id="cod_age_z" value="#{det.detVoucherCodAgencia}"/>

                        <h:panelGrid columns="4" style="width: 100%;">
                            <h:graphicImage value="/Imagenes/actions/pencil.png" style="cursor: pointer" title="Editar"
                                            rendered="#{managerPago.mostrarAccionPersonalizar}">
                                <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mpx3',{top:100})"
                                             actionListener="#{managerPago.editarComprobante}"
                                             reRender="zNombre,zMonto,zNumCom,zFechaPag,zConPag,zConPagDet,zProcedencia,zTipoComprobante,zNumVou,zFecVou,zCodAge">>
                                    <f:attribute name="zc_comprobante" value="#{det}"/>
                                </a4j:support>
                            </h:graphicImage>

                            <h:commandButton image="/Imagenes/actions/anular_.gif" title="Anular"
                                             actionListener="#{managerPago.anularPago}"
                                             action="#{managerPago.seleccionar}"
                                             onclick="return (confirm('¿Esta realmente seguro de Anular el comprobante?'));"/>

                            <h:commandButton image="/Imagenes/actions/no.gif" title="Eliminar"
                                             actionListener="#{managerPago.eliminarPago}"
                                             action="#{managerPago.seleccionar}"
                                             onclick="return (confirm('¿Esta realmente seguro de Eliminar el comprobante?'));"/>

                            <a4j:commandButton image="/Imagenes/actions/fileprint.png" title="Imprimir"
                                               actionListener="#{managerPago.imprimirComprobante}"
                                               oncomplete="#{managerPago.jsImpresion}"/>
                        </h:panelGrid>
                    </rich:column>

                    <rich:column colspan="7"/>
                </rich:subTable>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="modalPagoCarnet" autosized="true" minWidth="350" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Pago Carnet"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('modalPagoCarnet')" title="Cerrar"/>
            </f:facet>
            <h:form id="formPagoCarnet">
                <div class="row">
                    <div class="col" style="text-align: right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerPago.pagarCarnet}" reRender="oculto_"
                                           oncomplete="#{managerPago.oncomplete}"/>
                    </div>
                </div>
                <rich:separator height="1" style="margin: 5px 0"/>
                <div class="row">
                    <div class="col uch-label">Ingrese N&deg; Comprobante</div>
                    <div class="col uch-input">
                        <h:inputText id="oculto_" value="#{managerPago.zcNumeroComprobante}"/>
                    </div>
                </div>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mpx2" autosized="true" minWidth="550" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Personalizaci&oacute;n de Pagos" escape="false"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpx2')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table style="width: 100%;" border="0" cellspacing="0">
                    <tr>
                        <td width="30%"></td>
                        <td width="50%"></td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td align="right" colspan="3">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerPago.savePersonalization}"
                                               action="#{managerPago.seleccionar}"
                                               reRender="tablaMaster"
                                               oncomplete="#{managerPago.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td>Semestre:</td>
                        <td>
                            <h:inputHidden id="xId" value="#{managerPago.alutarId}"/>
                            <h:inputHidden id="xIdNombre" value="#{managerPago.alumnoId}"/>
                            <h:outputText id="xSem" style="width:180px;" value="#{managerPago.rowSemestreOrNroComp}"/>
                        </td>
                        <td align="center" rowspan="5">
                            <rich:separator height="1"/>SALDO A FAVOR<br>
                            <h:outputText id="xSaldo" value="#{managerPago.saldo}"/>
                            <rich:separator height="1"/>ADEUDA<br>
                            <h:outputText id="xAdeuda" value="#{managerPago.rowAdeuda}"/>
                            <rich:separator height="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Facultad:</td>
                        <td>
                            <h:outputText id="xFac" value="#{managerPago.facultad}" style="width:180px;"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Especialidad:</td>
                        <td><h:outputText id="xEsp" value="#{managerPago.rowEspecialidadOrTipo}" style="width:180px;"/>
                        </td>
                    </tr>
                    <tr>
                        <td>C&oacute;digo:</td>
                        <td><h:outputText id="xCodigo" value="#{managerPago.rowCodigoAlu}" style="width:90px;"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Nombre:</td>
                        <td><h:outputText id="xNombre" value="#{managerPago.rowNombres}" style="width:180px;"/></td>
                    </tr>
                    <tr>
                        <td>Concepto de Pago:</td>
                        <td>
                            <h:outputText id="xConPag" value="#{managerPago.conceptoPagoDesc}" style="width:180px;"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Concepto de pago Detalle:</td>
                        <td>
                            <h:outputText id="xConPagDet" value="#{managerPago.rowConceptoPago}"
                                          style="width:180px;"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td>Monto:</td>
                        <td>
                            <h:inputText id="PerMonto" value="#{managerPago.rowMonto}" style="width:180px;"/>
                        </td>
                    </tr>
                    <tr>
                        <td>F. de Pago:</td>
                        <td>
                            <a4j:outputPanel id="PerFechaPag" layout="block">
                                <rich:calendar value="#{managerPago.alutarFecPago}" datePattern="yyyy-MM-dd"
                                               inputStyle="width: 160px;"/>
                            </a4j:outputPanel>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>F. de Prorroga:</td>
                        <td>
                            <a4j:outputPanel id="PerFechaPro" layout="block">
                                <rich:calendar value="#{managerPago.rowFecProOrFecComprPago}" datePattern="yyyy-MM-dd"
                                               inputStyle="width: 160px;"/>
                            </a4j:outputPanel>
                        </td>
                        <td></td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mpx3" autosized="true" minWidth="550" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Editar Comprobante de Pagos"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpx3')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table style="width: 100%;">
                    <tr>
                        <td width="30%"></td>
                        <td width="50%"></td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td align="right" colspan="3">
                            <a4j:commandButton image="/Imagenes/actions/llave.png" title="Guardar"
                                               oncomplete="Richfaces.showModalPanel('mpx4',{top:100})"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td>Concepto de Pago:</td>
                        <td>
                            <h:outputText id="zConPag" style="width:180px;" value="#{managerPago.zcConceptoPago}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Concepto de Pago - Det:</td>
                        <td>
                            <h:outputText id="zConPagDet" style="width:180px;"
                                          value="#{managerPago.zcConceptoPagoDetalle}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Nombre:</td>
                        <td>
                            <h:outputText id="zNombre" style="width:180px;" value="#{managerPago.zcNombre}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Monto de Pago:</td>
                        <td>
                            <h:outputText id="zMonto" style="width:180px;" value="#{managerPago.zcMonto}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td>N&uacute;mero de Comprobante:</td>
                        <td>
                            <h:inputText id="zNumCom" style="width:180px;" value="#{managerPago.zcNumeroComprobante}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>F. de Pago:</td>
                        <td>
                            <a4j:outputPanel id="zFechaPag" layout="block">
                                <rich:calendar value="#{managerPago.zcFechaPago}" datePattern="yyyy-MM-dd"
                                               inputStyle="width: 160px;"/>
                            </a4j:outputPanel>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Procedencia de Pago:</td>
                        <td>
                            <h:selectOneMenu id="zProcedencia" value="#{managerPago.zcProcedenciaPagoId}">
                                <f:selectItems value="#{managerPago.comboCatalogoProcedencia}"/>
                            </h:selectOneMenu>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Tipo de Comprobante:</td>
                        <td colspan="2">
                            <h:selectOneRadio id="zTipoComprobante" value="#{managerPago.zcTipoComprobanteId}">
                                <f:selectItems value="#{managerPago.comboTiposComprobantePago}"/>
                            </h:selectOneRadio>
                        </td>
                    </tr>
                    <tr>
                        <td>N&uacute;mero de Voucher:</td>
                        <td>
                            <h:inputText id="zNumVou" style="width:180px;" value="#{managerPago.zcNumVoucher}"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Fecha de Voucher:</td>
                        <td>
                            <a4j:outputPanel id="zFecVou" layout="block">
                                <rich:calendar value="#{managerPago.zcFecVoucher}" datePattern="yyyy-MM-dd"
                                               inputStyle="width: 160px;"/>
                            </a4j:outputPanel>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>C&oacute;digo de Agencia:</td>
                        <td>
                            <h:inputText id="zCodAge" style="width:180px;" value="#{managerPago.zcCodigoAgencia}"/>
                        </td>
                        <td></td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mpx4" autosized="true" minWidth="360" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Confirmaci&oacute;n" escape="false"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpx4')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table>
                    <tr>
                        <td style="text-align: right">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerPago.actualizarComprobantePago}"
                                               action="#{managerPago.seleccionar}"
                                               reRender="tablaMaster"
                                               oncomplete="#{managerPago.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td style="text-align: center; font-weight: bold;">IMPORTANTE</td>
                    </tr>
                    <tr>
                        <td style="background-color: #FFF000">
                            Ud esta a punto de modificar datos reservados,<br/>
                            esta Ud. realmente seguro que desea hacer estos cambios?<br/>
                            Si esta seguro proceda a guardar de lo contraria cierre las ventanas.<br/>
                            Por cuestiones de seguridad el sistema guardar&aacute; su nombre de Usuario.
                        </td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mp" autosized="true" minWidth="550" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Pagos"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table style="width: 100%;">
                    <tr>
                        <td width="30%"></td>
                        <td width="50%"></td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td align="right" colspan="3">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerPago.insertar}"
                                               reRender="tablaMaster"
                                               oncomplete="#{managerPago.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td>Semestre:</td>
                        <td>
                            <h:inputHidden id="iId" value="#{managerPago.alutarId}"/>
                            <h:inputHidden id="iIdNombre" value="#{managerPago.alumnoId}"/>
                            <h:outputText id="iSem" style="width:180px;" value="#{managerPago.rowSemestreOrNroComp}"/>
                        </td>
                        <td align="center" rowspan="8">
                            <rich:separator height="1"/>
                            SALDO A FAVOR<br>
                            <h:outputText id="iSaldo" value="#{managerPago.saldo}"/>
                            <rich:separator height="1"/>
                            ADEUDA<br>
                            <h:outputText id="iAdeuda" value="#{managerPago.rowAdeuda}"/>
                            <rich:separator height="1"/>
                            F. DE PAGO
                            <h:outputText id="iFechaPag" style="width:180px;" value="#{managerPago.alutarFecPago}">
                                <f:convertDateTime pattern="yyyy-MM-dd"/>
                            </h:outputText>
                            <rich:separator height="1"/>
                            F. DE PRO.
                            <h:outputText id="iFechaPro" style="width:180px;"
                                          value="#{managerPago.rowFecProOrFecComprPago}">
                                <f:convertDateTime pattern="yyyy-MM-dd"/>
                            </h:outputText>
                            <rich:separator height="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Facultad:</td>
                        <td>
                            <h:outputText id="iFac" style="width:180px;" value="#{managerPago.facultad}"/>

                        </td>
                    </tr>
                    <tr>
                        <td>Especialidad:</td>
                        <td>
                            <h:outputText id="iEsp" style="width:180px;" value="#{managerPago.rowEspecialidadOrTipo}"/>

                        </td>
                    </tr>
                    <tr>
                        <td>C&oacute;digo:</td>
                        <td>
                            <h:outputText id="iCodigo" style="width:90px;" value="#{managerPago.rowCodigoAlu}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Nombre:</td>
                        <td><h:outputText id="iNombre" style="width:180px;" value="#{managerPago.rowNombres}"/></td>
                    </tr>
                    <tr>
                        <td>Concepto de Pago:</td>
                        <td>
                            <h:outputText id="iConPag" style="width:180px;" value="#{managerPago.conceptoPagoDesc}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Concepto de pago Detalle:</td>
                        <td>
                            <h:outputText id="iConPagDet" style="width:180px;"
                                          value="#{managerPago.rowConceptoPago}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Monto:</td>
                        <td>
                            <h:outputText id="iMonto" style="width:180px;" value="#{managerPago.rowMonto}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td>Tipo de Cliente:</td>
                        <td>
                            <h:selectOneMenu id="iTipoCliente" value="#{managerPago.nMpIdTipoCliente}"
                                             style="width:180px;">
                                <a4j:support event="onchange" oncomplete="#{managerPago.selCli}"
                                             actionListener="#{managerPago.seleccionarClienteNuevo}"
                                             reRender="xunoy,xunox,tablaSecundaria,barraSecundaria,iRazonSocial,iTiponj,iClienteFac,iDniRuc"/>
                                <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                                <f:selectItems value="#{managerPago.comboCatalogoTipoCliente}"/>
                            </h:selectOneMenu>
                            <h:inputHidden id="iClienteFac" value="#{managerPago.tmpClientId}"/>
                            <a4j:region id="idEste">
                                <h:graphicImage value="/Imagenes/actions/user.png" style="cursor: pointer;"
                                                title="Alumno Actual">
                                    <a4j:support event="onclick" actionListener="#{managerPago.asignarCurrentStudent}"
                                                 reRender="iTiponj,iRazonSocial,iClienteFac,iTipoCliente,iDniRuc"/>
                                </h:graphicImage>
                            </a4j:region>
                            <h:graphicImage value="/Imagenes/actions/user_gris.png" style="cursor: pointer;"
                                            title="Nuevo Cliente Externo">
                                <a4j:support event="onclick" actionListener="#{managerPago.asignarNewExternalClient}"
                                             reRender="iTiponj,iRazonSocial,iClienteFac,iTipoCliente,iDniRuc"/>
                            </h:graphicImage>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Raz&oacute;n Social:</td>
                        <td>
                            <h:inputText id="iRazonSocial" style="width:180px;" value="#{managerPago.nMpRazonSocial}"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Tipo:</td>
                        <td>
                            <h:selectOneMenu id="iTiponj" value="#{managerPago.nMpTipoPersNatJur}" style="width:180px;">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                                <f:selectItems value="#{managerPago.comboCatalogoTipoPersNatJur}"/>
                            </h:selectOneMenu>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>DNI / RUC:</td>
                        <td>
                            <h:inputText id="iDniRuc" style="width:180px;" value="#{managerPago.nMpDniRuc}"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Procedencia de Pago:</td>
                        <td>
                            <h:selectOneMenu id="iProcedencia" value="#{managerPago.nMpProcedenciaId}"
                                             style="width:180px;">
                                <f:selectItems value="#{managerPago.comboCatalogoProcedencia}"/>
                            </h:selectOneMenu>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Tipo de Comprobante:</td>
                        <td colspan="2">
                            <h:selectOneRadio id="iTipoComprobante" value="#{managerPago.nMpTipoComprPago}">
                                <a4j:support event="onchange" actionListener="#{managerPago.cambiaSerieN}"
                                             reRender="iNumComprobante"/>
                                <f:selectItems value="#{managerPago.comboTiposComprobantePago}"/>
                            </h:selectOneRadio>
                        </td>
                    </tr>
                    <tr>
                        <td>N. de Comp.:</td>
                        <td>
                            <h:selectOneMenu id="iNumComprobante0" value="#{managerPago.nMpNumSerie}">
                                <f:selectItems value="#{managerPago.comboCatalogoReciboBloque}"/>
                                <a4j:support event="onchange" actionListener="#{managerPago.cambiaSerieN}"
                                             reRender="iNumComprobante"/>
                            </h:selectOneMenu>&nbsp;-&nbsp;
                            <h:inputText id="iNumComprobante" style="width:110px;"
                                         value="#{managerPago.nMpNumComprobante}"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>F. de Comp.:</td>
                        <td>
                            <a4j:outputPanel id="iCalendar3" layout="block">
                                <rich:calendar value="#{managerPago.nMpFechaComprobante}" datePattern="yyyy-MM-dd"
                                               direction="top-right" inputStyle="width: 160px;"/>
                            </a4j:outputPanel>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Monto a Pagar:</td>
                        <td>
                            <h:inputText id="iMontoPago" style="width:180px;" value="#{managerPago.nMpMontoPago}"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Mora:</td>
                        <td>
                            <h:inputText id="iMoraPago" style="width:180px;" value="#{managerPago.nMpMoraPago}"
                                         readonly="true"/>
                            <h:selectBooleanCheckbox id="chkMora" value="#{managerPago.nMpMoraSeleccion}"
                                                     style="width:20px;"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Matricula Extemporanea:</td>
                        <td>
                            <h:inputText id="iMatExpo" style="width:180px;" value="#{managerPago.nMpMatExtempo}"
                                         readonly="true"/>
                            <h:selectBooleanCheckbox id="chkMatExpo" value="#{managerPago.nMpMatExtempoSeleccion}"
                                                     style="width:20px;"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Nro. del Voucher:</td>
                        <td>
                            <h:inputText id="iNumVou" style="width:180px;" value="#{managerPago.nMpNumVou}"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>F. del Voucher:</td>
                        <td>
                            <a4j:outputPanel id="iCalendar6" layout="block">
                                <rich:calendar value="#{managerPago.nMpFecVou}" datePattern="yyyy-MM-dd"
                                               direction="top-right" inputStyle="width: 160px;"/>
                            </a4j:outputPanel>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Cod. de Agencia:</td>
                        <td>
                            <h:inputText id="iCodAge" style="width:180px;" value="#{managerPago.nMpCodAge}"/>
                        </td>
                        <td></td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mpCustomerSearch" autosized="true" minWidth="480" zindex="2000">
            <f:facet name="header">
                <h:outputText value="B&uacute;squeda de Clientes" escape="false"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpCustomerSearch')" title="Cerrar"/>
            </f:facet>
            <h:form style="width: 100%;">
                <table style="width: 100%;">
                    <tr>
                        <td><h:outputText id="xunoy" value="#{managerPago.xunoy}"/></td>
                        <td>
                            <h:inputText id="xunox" value="#{managerPago.xuno}" style="width: 180px;"/>
                        </td>
                        <td align="right">
                            <a4j:commandButton image="/Imagenes/actions/viewmag.png"
                                               actionListener="#{managerPago.funxuno}"
                                               oncomplete="#{managerPago.oncomplete2}"
                                               reRender="tablaSecundaria,barraSecundaria,xunox"/>
                        </td>
                    </tr>
                </table>
                <rich:datascroller id="barraSecundaria" align="right" for="tablaSecundaria" maxPages="5"
                                   style="width: 100%;"/>
                <rich:dataTable id="tablaSecundaria" rows="5" cellpadding="0" cellspacing="0" width="100%" border="0"
                                onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                value="#{managerPago.listaCliente}" var="cli">
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="DNI"/>
                        </f:facet>
                        <h:outputText value="#{cli.clienteDniRuc}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="C&oacute;digo" escape="false"/>
                        </f:facet>

                        <h:outputText value="#{cli.clienteCodigo}"/>
                    </rich:column>

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Nombres"/>
                        </f:facet>

                        <h:outputText value="#{cli.clienteNombre}"/>
                        <h:inputHidden value="#{cli.clienteRazon}"/>
                        <h:inputHidden value="#{cli.clienteSaldo}"/>
                    </rich:column>

                    <rich:column style="text-align: center;">
                        <f:facet name="header">
                            <h:outputText value="Asignar"/>
                        </f:facet>
                        <h:graphicImage value="/Imagenes/actions/button_ok.png" style="cursor: pointer;">
                            <a4j:support event="onclick" oncomplete="Richfaces.hideModalPanel('mpCustomerSearch')"
                                         actionListener="#{managerPago.asignar}"
                                         reRender="nNomAlu,nCodAlu,nIdAlu,xunox,iTiponj,iRazonSocial,iClienteFac,iDniRuc,nTiponj,nRazonSocial,nClienteFac,nDniRuc,nSaldo">
                                <f:attribute name="selected_cli" value="#{cli}"/>
                            </a4j:support>
                        </h:graphicImage>
                    </rich:column>
                </rich:dataTable>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mpNewPayments" autosized="true" minWidth="550" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Nuevos Pagos"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mpNewPayments')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table style="width: 100%">
                    <tr>
                        <td width="20%"></td>
                        <td width="50%"></td>
                        <td width="30%"></td>
                    </tr>
                    <tr>
                        <td align="right" colspan="3">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerPago.insertarNuevo}"
                                               oncomplete="#{managerPago.noncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3"><rich:separator height="1"/></td>
                    </tr>
                    <tr>
                        <td>Concepto de Pago:</td>
                        <td>
                            <h:inputHidden id="nValorNuevo" value="#{managerPago.nValorNuevo}"/>
                            <h:inputText id="cConpag" value="#{managerPago.suggestion}" style="width:180px"/>

                            <rich:suggestionbox id="suggestion" for="cConpag" nothingLabel="Concepto No Encontrado"
                                                suggestionAction="#{managerPago.autocomplete}" var="con"
                                                tokens="," height="150" width="260" cellpadding="2"
                                                cellspacing="2" shadowOpacity="4" shadowDepth="4"
                                                minChars="3" rules="none" zindex="3500" usingSuggestObjects="true"
                                                onobjectchange="printObjectsSelected(#{rich:element('objects')}, #{rich:component('suggestion')},#{rich:element('nConpag')},#{rich:element('nMontoPago')},#{rich:element('nAdeuda')});">
                                <rich:column>
                                    <h:outputText value="#{con.desc}" style="font-style:bold"/>
                                </rich:column>
                                <rich:column>
                                    <h:outputText value="#{con.monto}" style="font-style:bold"/>
                                </rich:column>
                            </rich:suggestionbox><br>
                            <h:outputText id="objects" style="font-weight:bold"/>
                            <h:inputHidden id="nConpag" value="#{managerPago.nConPagId}"/>
                        </td>
                        <td align="center" rowspan="3">
                            <rich:separator height="1"/>
                            SALDO A FAVOR<br>
                            <h:outputText id="nSaldo" value="#{managerPago.nMpSaldo}"/>
                            <rich:separator height="1"/>
                            MONTO A PAGAR<br>
                            <h:outputText id="nAdeuda" value="#{managerPago.nMpMonto}"
                                          style="font-size:14px; font-family:verdana"/>
                            <rich:separator height="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Tipo de Cliente:</td>
                        <td>
                            <h:selectOneMenu id="nTipoCliente" value="#{managerPago.nMpIdTipoCliente}"
                                             style="width:180px;">
                                <a4j:support event="onchange" oncomplete="#{managerPago.selCli}"
                                             actionListener="#{managerPago.seleccionarClienteNuevo}"
                                             reRender="xunoy,xunox,tablaSecundaria,barraSecundaria,nRazonSocial,nTiponj,nClienteFac,nDniRuc"/>
                                <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                                <f:selectItems value="#{managerPago.comboCatalogoTipoCliente}"/>
                            </h:selectOneMenu>
                            <h:graphicImage value="/Imagenes/actions/user_gris.png" style="cursor:pointer;"
                                            title="Nuevo Cliente Externo">
                                <a4j:support event="onclick" actionListener="#{managerPago.asignarNewExternalClient}"
                                             reRender="nTiponj,nRazonSocial,nClienteFac,nTipoCliente,nDniRuc,nSaldo"/>
                            </h:graphicImage>
                        </td>
                        <td><h:inputHidden id="nClienteFac" value="#{managerPago.tmpClientId}"/></td>
                    </tr>
                    <tr>
                        <td>Raz&oacute;n Social:</td>
                        <td>
                            <h:inputText id="nRazonSocial" style="width:180px;" value="#{managerPago.nMpRazonSocial}"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Tipo:</td>
                        <td>
                            <h:selectOneMenu id="nTiponj" value="#{managerPago.nMpTipoPersNatJur}" style="width:180px;">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccionar ]"/>
                                <f:selectItems value="#{managerPago.comboCatalogoTipoPersNatJur}"/>
                            </h:selectOneMenu>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <table cellspacing="0" border="0">
                                <tr>
                                    <td>DNI / RUC:</td>
                                    <td>
                                        <h:inputText id="nDniRuc" style="width:180px;"
                                                     value="#{managerPago.nMpDniRuc}"/>
                                    </td>
                                    <td></td>
                                    <td>Fecha del Voucher:</td>
                                    <td>
                                        <a4j:outputPanel id="nCalendar5" layout="block">
                                            <rich:calendar value="#{managerPago.nMpFecVou}" datePattern="yyyy-MM-dd"
                                                           direction="top-right" inputStyle="width: 120px;"/>
                                        </a4j:outputPanel>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="5"><rich:separator height="1"/></td>
                                </tr>
                                <tr>
                                    <td>Cod Alumno:</td>
                                    <td>
                                        <h:inputText id="nCodAlu" style="width: 180px;" readonly="true"
                                                     value="#{managerPago.nMpCodAlu}"/>
                                    </td>
                                    <td>
                                        <h:graphicImage value="/Imagenes/actions/viewmag.png"
                                                        style="cursor: pointer;" title="Buscar Alumno Regular">
                                            <a4j:support event="onclick" oncomplete="#{managerPago.selCli}"
                                                         actionListener="#{managerPago.nSeleccionarClienteNuevo}"
                                                         reRender="xunoy,xunox,nCodAlu,nNomAlu,nIdAlu,barraSecundaria,tablaSecundaria"/>
                                        </h:graphicImage>
                                        <h:inputHidden id="nIdAlu" value="#{managerPago.alumnoId}"/>
                                    </td>
                                    <td>Nro. Voucher:</td>
                                    <td>
                                        <h:inputText id="nNumVou" style="width:140px;"
                                                     value="#{managerPago.nMpNumVou}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Nombre Alumno:</td>
                                    <td>
                                        <h:inputText id="nNomAlu" style="width:180px;" readonly="true"
                                                     value="#{managerPago.rowNombres}"/>
                                    </td>
                                    <td></td>
                                    <td>Cod. Agencia:</td>
                                    <td>
                                        <h:inputText id="nCodAge" style="width:140px;"
                                                     value="#{managerPago.nMpCodAge}"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>Procedencia de Pago:</td>
                        <td>
                            <h:selectOneMenu id="nProcedencia" value="#{managerPago.nMpProcedenciaId}"
                                             style="width:180px;">
                                <f:selectItems value="#{managerPago.comboCatalogoProcedencia}"/>
                                <a4j:support event="onchange" actionListener="#{managerPago.loadSaldoOfStudent}"
                                             reRender="nSaldo"/>
                            </h:selectOneMenu>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Tipo de Comprobante:</td>
                        <td colspan="2">
                            <h:selectOneRadio id="nTipoComprobante" value="#{managerPago.nMpTipoComprPago}">
                                <a4j:support event="onchange" actionListener="#{managerPago.cambiaSerieN}"
                                             reRender="nNumComprobante"/>
                                <f:selectItems value="#{managerPago.comboTiposComprobantePago}"/>
                            </h:selectOneRadio>
                        </td>
                    </tr>
                    <tr>
                        <td>N. de Comp.:</td>
                        <td>
                            <h:selectOneMenu id="nNumComprobante0" value="#{managerPago.nMpNumSerie}">
                                <f:selectItems value="#{managerPago.comboCatalogoReciboBloque}"/>
                                <a4j:support event="onchange" actionListener="#{managerPago.cambiaSerieN}"
                                             reRender="nNumComprobante"/>
                            </h:selectOneMenu>&nbsp;-&nbsp;
                            <h:inputText id="nNumComprobante" style="width:110px;"
                                         value="#{managerPago.nMpNumComprobante}"/>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>F. de Comp.:</td>
                        <td><a4j:outputPanel id="nCalendar3" layout="block">
                            <rich:calendar value="#{managerPago.nMpFechaComprobante}" datePattern="yyyy-MM-dd"
                                           direction="top-right" inputStyle="width: 160px;"/>
                        </a4j:outputPanel></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Descripci&oacute;n:</td>
                        <td><h:inputTextarea id="nDes" style="width:180px;" value="#{managerPago.nMpDescription}"/></td>

                    </tr>
                    <tr>
                        <td>Monto a Pagar:</td>
                        <td><h:inputText id="nMontoPago" style="width:180px;" value="#{managerPago.nMpMontoPago}"/>
                        </td>
                        <td></td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="modal2" autosized="true" minWidth="450" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro de Matricula Extemporanea"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('modal2')" title="Cerrar"/>
            </f:facet>
            <h:form id="formMatExtemporanea">
                <table style="width: 100%;">
                    <tr>
                        <td style="text-align: right" colspan="2">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                               actionListener="#{managerPago.pagarMatExtemporanea}"
                                               oncomplete="#{managerPago.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2"><rich:separator height="1" style="margin: 5px 0"/></td>
                    </tr>
                    <tr>
                        <td>Ingrese N&deg; Comprobante:</td>
                        <td><h:inputText value="#{managerPago.zcNumeroComprobante}"/></td>
                    </tr>
                    <tr>
                        <td>Tipo de Comprobante:</td>
                        <td>
                            <h:selectOneRadio value="#{managerPago.zcTipoComprobanteId}" disabled="true">
                                <f:selectItems value="#{managerPago.comboTiposComprobantePago}"/>
                            </h:selectOneRadio>
                        </td>
                    </tr>
                    <tr>
                        <td>Fecha de Pago:</td>
                        <td>
                            <rich:calendar value="#{managerPago.zcFechaPago}" datePattern="yyyy-MM-dd"
                                           inputStyle="width: 160px;"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Monto:</td>
                        <td><h:inputText style="width:180px;" value="#{managerPago.zcMonto}"/></td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>

