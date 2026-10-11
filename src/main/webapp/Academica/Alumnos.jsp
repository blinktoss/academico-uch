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
    <title>Mantenimiento de Alumnos</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width: 100%;">
                <tr>
                    <td width="20%" colspan="2"><strong>MANTENIMIENTO DE ALUMNOS</strong></td>
                    <td width="30%" align="right"></td>
                    <td width="30%" align="right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                            </f:facet>
                        </a4j:status>
                    </td>
                    <td width="30%">
                        <h:graphicImage value="/Imagenes/actions/filenew.png" width="16"
                                        style="border-width: 0;cursor: pointer;">
                            <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mp')"
                                         actionListener="#{managerAlumno.nuevo}"
                                         reRender="iId,iCodigo,iPaterno,iMaterno,iNombre,iDni,iMail,
                                                 iIdTipo,iIdModalidad,iIdEstado,iIdFacIng,iIdEspIng,
                                                 iIdFacAct,iIdEspAct,iSemestre,semestre_vigente,iIdPlanIng,
                                                 iIdPlanAct,iMonPag"/>
                        </h:graphicImage>
                    </td>
                    <td width="30%">
                        <h:graphicImage value="/Imagenes/actions/fileprint.png"
                                        style="cursor: pointer;"
                                        title="Imprimir Listado"/>
                    </td>
                    <td align=right width="50%">
                        <a4j:commandButton id="buscar" actionListener="#{managerAlumno.seleccionar}"
                                           image="/Imagenes/actions/viewmag.png" title="Buscar"
                                           reRender="tablaMaster,barra"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="7">
                        <rich:separator height="2" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td width="20%" colspan="6"></td>
                </tr>
                <tr>
                    <td width="10%"><h:outputText value="C&oacute;digo:" escape="false"/></td>
                    <td width="30%">
                        <h:inputText value="#{managerAlumno.paramCodigo}" style="width: 180px;"/>
                    </td>
                    <td width="10%" align="right">Nombres:</td>
                    <td><h:inputText value="#{managerAlumno.paramNombre}" style="width: 180px;"/></td>
                </tr>
                <tr>
                    <td width="10%">Ap. Paterno:</td>
                    <td width="30%">
                        <h:inputText value="#{managerAlumno.paramPaterno}" style="width: 180px;"/>
                    </td>
                    <td width="10%" align="right">Facultad Actual:</td>
                    <td>
                        <h:selectOneMenu id="FacuBuscar" value="#{managerAlumno.paramIdFacAct}" style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todas ]"/>
                            <f:selectItems value="#{managerAlumno.comboFacultadBuscar}"/>
                            <a4j:support event="onchange" reRender="FacuBuscar,EspeBuscar"
                                         actionListener="#{managerAlumno.cambiarEspecialidadBuscar}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td width="10%">Ap. Materno:</td>
                    <td><h:inputText value="#{managerAlumno.paramMaterno}" style="width: 180px;"/></td>
                    <td align="right">Especialidad Actual:</td>
                    <td>
                        <h:selectOneMenu id="EspeBuscar" value="#{managerAlumno.paramIdEspAct}" style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todas ]"/>
                            <f:selectItems value="#{managerAlumno.comboEspecialidadBuscar}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td colspan="7" width="100%">
                        <rich:separator height="2" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td>
                    </td>
                </tr>
            </table>
        </h:form>
        <h:form style="width: 100%;">
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"
                               pageIndexVar="pageIndex" pagesVar="pages">
                <f:facet name="pages">
                    <h:outputText value="#{pageIndex} / #{pages}" />
                </f:facet>
            </rich:datascroller>

            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" width="100%" border="0"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerAlumno.listaAlumno}" var="alu">
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Cod."/>
                    </f:facet>
                    <h:outputText value="#{alu.paramCodigo}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Ape y Nom"/>
                    </f:facet>
                    <h:outputText value="#{alu.paramPaterno} #{alu.paramMaterno} #{alu.paramNombre}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Especialidad"/>
                    </f:facet>
                    <h:outputText value="#{alu.espActual}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="DNI"/>
                    </f:facet>
                    <h:outputText value="#{alu.alumnoDni}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Correo"/>
                    </f:facet>
                    <h:outputText value="#{alu.alumnoMail}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Tipo"/>
                    </f:facet>
                    <h:outputText value="#{alu.alumnoTipoDescription}"/>
                </h:column>
                <h:column>
                    <f:facet name="header">
                        <h:outputText value="Procedencia"/>
                    </f:facet>
                    <h:outputText value="#{alu.alumnoProcedenciaDesc}"/>
                </h:column>
                <rich:column style="text-align: center;">
                    <h:outputLink value="#{request.contextPath}/report" target="_blank" title="Imprimir">
                        <f:param name="type" value="alumno-comprobante"/>
                        <f:param name="aluId" value="#{alu.alumnoId}"/>
                        <h:graphicImage value="/Imagenes/actions/fileprint.png" style="cursor: pointer;"/>
                    </h:outputLink>
                </rich:column>
                <rich:column style="text-align: center;">
                    <h:graphicImage value="/Imagenes/actions/calendar.gif" style="cursor: pointer;" title="Asistencia">
                        <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mp4', {top:80})"
                                     actionListener="#{managerAlumno.mostrarAsistencia}"
                                     reRender="aId,aCodigo,aCodigo1,aNombres,aMesAnio,tablaAsistencia"/>
                    </h:graphicImage>
                </rich:column>
                <rich:column style="text-align: center;">
                    <h:graphicImage value="/Imagenes/actions/editpaste.png" style="cursor: pointer;" title="Editar">
                        <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mp',{top:80})"
                                     actionListener="#{managerAlumno.editarFila}"
                                     reRender="iId,iCodigo,iPaterno,iMaterno,iNombre,iDni,iMail,iIdTipo,iIdModalidad,iIdEstado,iIdFacIng,iIdEspIng,iIdFacAct,iIdEspAct,iSemestre,semestre_vigente,iIdPlanIng,iIdPlanAct,imagen,iProcedencia,iFam,iTipoFamiliar,iFormaPago,iAluUnidad,iMonPag">
                            <f:attribute name="p_alu" value="#{alu}"/>
                        </a4j:support>
                    </h:graphicImage>
                </rich:column>
                <rich:column style="text-align: center;">
                    <h:graphicImage value="/Imagenes/actions/add_.gif" style="cursor: pointer;" title="Adicionar Datos">
                        <a4j:support event="onclick" oncomplete="Richfaces.showModalPanel('mp1',{top:100})"
                                     actionListener="#{managerAlumno.editarFila2}"
                                     reRender="iId2,datos,Colegio,iSexo,iCorreo,TelefonoCelular,iTipoColegio,iTipoDocumento,iNumeroDocumento,iCreacion,comboDistritoLima,iPreparacion,iAcademia,AcademiaOtros,iAniosPrep,iMesesPrep,iTelefono,direccionLima,nDepartamento,nProvincia,nDistrito,rDepartamento,rProvincia,rDistrito,Documentos">
                            <f:attribute name="p_alu" value="#{alu}"/>
                        </a4j:support>
                    </h:graphicImage>
                    <f:param id="p_id" value="#{alu.alumnoId}"/>
                    <f:param id="p_codigo" value="#{alu.paramCodigo}"/>
                    <f:param id="p_paterno" value="#{alu.paramPaterno}"/>
                    <f:param id="p_materno" value="#{alu.paramMaterno}"/>
                    <f:param id="p_nombre" value="#{alu.paramNombre}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <h:commandButton image="/Imagenes/actions/no.png" style="cursor: pointer;"
                                     title="Eliminar" actionListener="#{managerAlumno.eliminarFila}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                    <f:param id="delete" value="#{alu.alumnoId}"/>
                </rich:column>
            </rich:dataTable>
        </h:form>

        <rich:modalPanel id="mp" autosized="true" minWidth="550" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Registro Alumno"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table style="width: 100%;">
                    <tr>
                        <td>
                        </td>
                        <td align="right" colspan="4">
                            <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                               title="Guardar"
                                               actionListener="#{managerAlumno.insertar}"
                                               reRender="tablaMaster, barra"
                                               oncomplete="#{managerAlumno.oncomplete}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <rich:separator height="2" lineType="double"/>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">
                            <h:inputHidden id="iId" value="#{managerAlumno.modalIdAlumno}"/>
                            <h:inputHidden id="iCodigo" value="#{managerAlumno.modalCodigoAlumno}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Ap Paterno:</td>
                        <td width="70%">
                            <h:inputText id="iPaterno" style="width: 180px;"
                                         value="#{managerAlumno.modalPaternoAlumno}"/>
                        </td>
                        <td align="right" width="20%" rowspan="6">
                            <a4j:outputPanel id="imagen" ajaxRendered="true" layout="block">
                                <h:graphicImage style="width: 100px;"
                                                value="/foto?id=#{managerAlumno.modalIdAlumno}"/>
                            </a4j:outputPanel>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Ap Materno:</td>
                        <td width="70%">
                            <h:inputText id="iMaterno" style="width: 180px;"
                                         value="#{managerAlumno.modalMaternoAlumno}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Nombres:</td>
                        <td width="70%">
                            <h:inputText id="iNombre" style="width: 180px;" value="#{managerAlumno.modalNombreAlumno}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">DNI:</td>
                        <td width="70%">
                            <h:inputText id="iDni" style="width: 180px;" value="#{managerAlumno.modalDniAlumno}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">E-Mail:</td>
                        <td width="70%">
                            <h:inputText id="iMail" style="width: 180px;" value="#{managerAlumno.modalMailAlumno}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Tipo:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iIdTipo" value="#{managerAlumno.modalIdTipoAlumno}"
                                             style="width: 180px;">
                                <f:selectItems value="#{managerAlumno.comboCatalogoTipo}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Modalidad:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iIdModalidad" value="#{managerAlumno.modalIdModalidadAlumno}"
                                             style="width: 180px;">
                                <f:selectItems value="#{managerAlumno.comboCatalogoModa}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Estado:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iIdEstado" value="#{managerAlumno.modalIdEstadoAlumno}"
                                             style="width: 180px;">
                                <f:selectItems value="#{managerAlumno.comboCatalogoEstado}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Fac Ingreso:</td>
                        <td width="70%">
                            <a4j:region id="fac_ing1">
                                <h:selectOneMenu id="iIdFacIng" value="#{managerAlumno.modalIdFacIngAlumno}"
                                                 style="width: 180px;">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerAlumno.comboFacultadesIngreso}"/>
                                    <a4j:support event="onchange" reRender="iIdFacIng,iIdEspIng,iIdPlanIng"
                                                 actionListener="#{managerAlumno.cambiarEspecialidadIngreso}"/>
                                </h:selectOneMenu>
                            </a4j:region>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Esp Ingreso:</td>
                        <td width="70%">
                            <a4j:region id="fac_ing2">
                                <h:selectOneMenu id="iIdEspIng" value="#{managerAlumno.modalIdEspIngAlumno}"
                                                 style="width: 240px;">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerAlumno.comboEspecialidadesIngreso}"/>
                                    <a4j:support event="onchange" reRender="iIdEspIng,iIdPlanIng"
                                                 actionListener="#{managerAlumno.cambiarPlanIngreso}"/>
                                </h:selectOneMenu>
                            </a4j:region>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Plan Ingreso:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iIdPlanIng" value="#{managerAlumno.modalIdPlanIngAlumno}"
                                             style="width : 180px;">
                                <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerAlumno.comboPlanIngreso}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Sem Ingreso:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iSemestre" value="#{managerAlumno.modalIdSemestreAlumno}"
                                             style="width: 180px;">
                                <f:selectItems value="#{managerAlumno.comboSemestre}"/>
                            </h:selectOneMenu>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Sem Actual:</td>
                        <td width="70%"><h:outputText id="semestre_vigente"
                                                      style="width: 180px; color:#008000; font-weight: bold; "
                                                      value="#{managerAlumno.semestreActual}"/></td>
                    </tr>
                    <tr>
                        <td width="30%">*Fac Actual:</td>
                        <td width="70%">
                            <a4j:region id="fac_act1">
                                <h:selectOneMenu id="iIdFacAct" value="#{managerAlumno.modalIdFacActAlumno}"
                                                 style="width: 180px;">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerAlumno.comboFacultadesActual}"/>
                                    <a4j:support event="onchange" reRender="iIdFacAct,iIdEspAct,iIdPlanAct"
                                                 actionListener="#{managerAlumno.cambiarEspecialidadActual}"/>
                                </h:selectOneMenu>
                            </a4j:region>
                        </td>
                    </tr>
                    <tr>
                        <td width="30%">*Esp Actual:</td>
                        <td width="70%">
                            <a4j:region id="fac_act2">
                                <h:selectOneMenu id="iIdEspAct" value="#{managerAlumno.modalIdEspActAlumno}"
                                                 style="width: 240px;">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerAlumno.comboEspecialidadesActual}"/>
                                    <a4j:support event="onchange" reRender="iIdEspAct,iIdPlanAct"
                                                 actionListener="#{managerAlumno.cambiarPlanActual}"/>
                                </h:selectOneMenu>
                            </a4j:region>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Plan Actual:</td>
                        <td width="70%">
                            <a4j:region id="fac_act3">
                                <h:selectOneMenu id="iIdPlanAct" value="#{managerAlumno.modalIdPlanActAlumno}"
                                                 style="width: 180px;">
                                    <f:selectItem itemValue="0" itemLabel="[ Seleccione ]"/>
                                    <f:selectItems value="#{managerAlumno.comboPlanActual}"/>
                                </h:selectOneMenu>
                            </a4j:region>
                        </td>
                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">*Procedencia:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iProcedencia" value="#{managerAlumno.alumnoProcedenciaId}"
                                             style="width: 180px;">
                                <f:selectItems value="#{managerAlumno.comboProcedencia}"/>
                            </h:selectOneMenu>
                        </td>

                        <td width="20%"></td>
                    </tr>

                    <tr>
                        <td width="30%">Familiar:</td>
                        <td width="70%">
                            <h:inputText id="iFam" style="width: 180px;" value="#{managerAlumno.modalFamiliarAlumno}"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>

                    <tr>
                        <td width="30%">Tipo Familiar:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iTipoFamiliar" value="#{managerAlumno.modalIdTipoFamiliarAlumno}"
                                             style="width: 180px;">
                                <f:selectItems value="#{managerAlumno.comboTipoFamiliar}"/>
                            </h:selectOneMenu>
                        </td>

                        <td width="20%"></td>
                    </tr>

                    <tr>
                        <td width="30%">Forma de Pago:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iFormaPago" value="#{managerAlumno.modalIdFormaPagoAlumno}"
                                             style="width: 180px;">
                                <f:selectItems value="#{managerAlumno.comboFormaPago}"/>
                            </h:selectOneMenu>
                        </td>

                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Unidad:</td>
                        <td width="70%">
                            <h:selectOneMenu id="iAluUnidad" value="#{managerAlumno.modalUnidadAlumno}"
                                             style="width: 180px;">
                                <f:selectItems value="#{managerAlumno.comboUnidad}"/>
                            </h:selectOneMenu>
                        </td>


                        <td width="20%"></td>
                    </tr>
                    <tr>
                        <td width="30%">Monto de Pago:</td>
                        <td width="70%">
                            <h:inputText id="iMonPag" value="#{managerAlumno.modalMontoPagoAlumno}"
                                         style="width: 180px;"/>
                        </td>
                        <td align="right" width="20%">
                        </td>
                    </tr>

                </table>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mp1" autosized="true" width="840" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Datos de Alumno"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp1')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table>
                    <tr>
                        <td colspan="3">
                            <h:inputHidden id="iId2" value="#{managerAlumno.modalIdAlumno}"/>
                            <h:outputText id="datos"
                                          value="#{managerAlumno.modalPaternoAlumno} #{managerAlumno.modalMaternoAlumno} #{managerAlumno.modalNombreAlumno}"/>
                        </td>
                        <td style="text-align: right">
                            <a4j:commandButton action="#{managerAlumno.guardar}"
                                               image="/Imagenes/actions/filesave.png" title="Guardar"
                                               oncomplete="alert('Se guardo correctamente.');Richfaces.hideModalPanel('mp1')"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <rich:separator height="1"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Nom. Colegio:</td>
                        <td>
                            <h:inputText label="Colegio" value="#{managerAlumno.colegio}"
                                         style="width: 180px;" id="Colegio" rendered="true" tabindex="1"/>
                        </td>
                        <td style="width: 18%">Documentos Entregados:</td>
                        <td>
                            <h:selectManyCheckbox id="Documentos" value="#{managerAlumno.documentos}">
                                <f:selectItems value="#{managerAlumno.comboDocumentos}"/>
                            </h:selectManyCheckbox>
                        </td>
                    </tr>
                    <tr>
                        <td>Tipo Colegio:</td>
                        <td colspan="3">
                            <h:selectOneRadio id="iTipoColegio" value="#{managerAlumno.tipoColegio}" tabindex="2">
                                <f:selectItems value="#{managerAlumno.comboTipoColegio}"/>
                            </h:selectOneRadio>
                        </td>
                    </tr>
                    <tr>
                        <td>Sexo:</td>
                        <td>
                            <h:selectOneRadio id="iSexo" value="#{managerAlumno.sexo}" tabindex="3">
                                <f:selectItems value="#{managerAlumno.combosexo}"/>
                            </h:selectOneRadio>
                        </td>
                        <td>Col. Departamento:</td>
                        <td>
                            <h:selectOneMenu id="rDepartamento" value="#{managerAlumno.departamentos1}"
                                             style="width: 180px;" tabindex="13">
                                <f:selectItems value="#{managerAlumno.comboDepartamentos1}"/>
                                <a4j:support event="onchange" reRender="rProvincia,rDistrito"
                                             actionListener="#{managerAlumno.cambiaDepartamentoCol}"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                    <tr>
                        <td>E-Mail:</td>
                        <td>
                            <h:inputText id="iCorreo" value="#{managerAlumno.correo}" style="width: 180px;"
                                         rendered="true" tabindex="4"/>
                        </td>
                        <td>Col. Provincia:</td>
                        <td>
                            <h:selectOneMenu id="rProvincia" value="#{managerAlumno.provincias1}"
                                             style="width: 180px;" tabindex="14">
                                <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerAlumno.comboProvincias1}"/>
                                <a4j:support event="onchange" reRender="rDistrito"
                                             actionListener="#{managerAlumno.cambiaProvinciaCol}"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                    <tr>
                        <td>Celular</td>
                        <td>
                            <h:inputText id="TelefonoCelular" value="#{managerAlumno.telefonoCelular}"
                                         style="width: 180px;" tabindex="7"/>
                        </td>
                        <td>Col. Distrito:</td>
                        <td>
                            <h:selectOneMenu id="rDistrito" value="#{managerAlumno.distrito1}"
                                             style="width: 180px;" tabindex="15">
                                <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerAlumno.comboDistrito1}"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                    <tr>
                        <td>Tipo Doc.:</td>
                        <td>
                            <h:selectOneMenu id="iTipoDocumento" value="#{managerAlumno.tipoDocumento}"
                                             style="width: 180px;" tabindex="6">
                                <f:selectItems value="#{managerAlumno.comboTipoDocumento}"/>
                            </h:selectOneMenu>
                        </td>
                        <td>Tipo Preparaci&oacute;n:</td>
                        <td>
                            <h:selectOneMenu id="iPreparacion" value="#{managerAlumno.preparacion}"
                                             style="width: 180px;" tabindex="16">
                                <f:selectItems value="#{managerAlumno.comboPreparacion}"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                    <tr>
                        <td>Num. Doc:</td>
                        <td>
                            <h:inputText id="iNumeroDocumento" value="#{managerAlumno.numeroDocumento}"
                                         style="width: 180px;"/>
                        </td>
                        <td>Nom. Academia:</td>
                        <td>
                            <h:selectOneMenu id="iAcademia" value="#{managerAlumno.academia}"
                                             style="width: 180px;" tabindex="17">
                                <f:selectItems value="#{managerAlumno.comboAcademias}"/>
                                <a4j:support event="onchange" reRender="AcademiaOtros" focus="AcademiaOtros"
                                             actionListener="#{managerAlumno.cambiaAcabemia}"/>
                            </h:selectOneMenu>
                        </td>
                    </tr>
                    <tr>
                        <td>Fec. Nacimiento:</td>
                        <td>
                            <rich:calendar id="iCreacion" value="#{managerAlumno.fechaNacimiento}"
                                           datePattern="dd/MM/yyyy"/>
                        </td>
                        <td>Academia(Otros):</td>
                        <td>
                            <h:inputText id="AcademiaOtros" value="#{managerAlumno.academiaOtros}"
                                         disabled="#{managerAlumno.disableAcabemiaOtros}"
                                         style="width: 180px;#{managerAlumno.styleAcabemiaOtros}" tabindex="24"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Nac. Departamento:</td>
                        <td>
                            <h:selectOneMenu id="nDepartamento" value="#{managerAlumno.departamentos}"
                                             style="width: 180px;" tabindex="9">
                                <f:selectItems value="#{managerAlumno.comboDepartamentos}"/>
                                <a4j:support event="onchange" reRender="nProvincia,nDistrito"
                                             actionListener="#{managerAlumno.cambiaDepartamentoNac}"/>
                            </h:selectOneMenu>
                        </td>
                        <td colspan="2">
                            <table cellspacing="0" border="0">
                                <tr>
                                    <td>A&ntilde;os:</td>
                                    <td>
                                        <h:selectOneMenu id="iAniosPrep" tabindex="19" style="width: 40px;"
                                                         value="#{managerAlumno.aniosPreparacion}">
                                            <f:selectItems value="#{managerAlumno.comboAniosPreparacion}"/>
                                        </h:selectOneMenu>
                                    </td>
                                    <td>Meses:</td>
                                    <td>
                                        <h:selectOneMenu id="iMesesPrep" tabindex="20" style="width: 40px;"
                                                         value="#{managerAlumno.mesesPreparacion}">
                                            <f:selectItems value="#{managerAlumno.comboMesesPreparacion}"/>
                                        </h:selectOneMenu>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>Nac. Provincia:</td>
                        <td>
                            <h:selectOneMenu id="nProvincia" value="#{managerAlumno.provincias}"
                                             style="width: 180px;" tabindex="10">
                                <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerAlumno.comboProvincias}"/>
                                <a4j:support event="onchange" reRender="nDistrito"
                                             actionListener="#{managerAlumno.cambiaProvinciaNac}"/>
                            </h:selectOneMenu>
                        </td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Nac. Distrito:</td>
                        <td>
                            <h:selectOneMenu id="nDistrito" value="#{managerAlumno.distrito}"
                                             style="width: 180px;" tabindex="11">
                                <f:selectItem itemValue="000000" itemLabel="[ Seleccione ]"/>
                                <f:selectItems value="#{managerAlumno.comboDistrito}"/>
                            </h:selectOneMenu>
                        </td>
                        <td>Tel&eacute;f.:</td>
                        <td>
                            <h:inputText id="iTelefono" value="#{managerAlumno.telefonoFijo}"
                                         style="width: 180px;" tabindex="28"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Dist. Lima</td>
                        <td>
                            <h:selectOneMenu id="comboDistritoLima" value="#{managerAlumno.distritoLima}"
                                             style="width: 180px;" tabindex="22">
                                <f:selectItems value="#{managerAlumno.comboDistritoLima}"/>
                            </h:selectOneMenu>
                        </td>
                        <td>Dir. en Lima:</td>
                        <td>
                            <h:inputText id="direccionLima" value="#{managerAlumno.direccionLima}"
                                         style="width: 320px;" tabindex="30"/>
                        </td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>

        <rich:modalPanel id="mp4" autosized="true" minWidth="550" zindex="2000">
            <f:facet name="header">
                <h:outputText value="Asistencia de Alumno"/>
            </f:facet>
            <f:facet name="controls">
                <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                                onclick="Richfaces.hideModalPanel('mp4')" title="Cerrar"/>
            </f:facet>
            <h:form>
                <table style="width: 100%">
                    <tr>
                        <td style="width: 15%; vertical-align: bottom">
                            <a4j:commandButton value="<<" actionListener="#{managerAlumno.disminuyeAnio}"
                                               action="#{managerAlumno.evaluaAsistencia}"
                                               reRender="tablaAsistencia,aMesAnio"/>
                            <a4j:commandButton value="<" actionListener="#{managerAlumno.disminuyeMes}"
                                               action="#{managerAlumno.evaluaAsistencia}"
                                               reRender="tablaAsistencia,aMesAnio"/>
                            <a4j:commandButton value=">" actionListener="#{managerAlumno.aumentaMes}"
                                               action="#{managerAlumno.evaluaAsistencia}"
                                               reRender="tablaAsistencia,aMesAnio"/>
                            <a4j:commandButton value=">>" actionListener="#{managerAlumno.aumentaAnio}"
                                               action="#{managerAlumno.evaluaAsistencia}"
                                               reRender="tablaAsistencia,aMesAnio"/>
                        </td>
                        <td colspan="4" style="text-align: right">
                            <h:inputHidden id="aId" value="#{managerAlumno.modalIdAlumno}"/>
                            <h:inputHidden id="aCodigo" value="#{managerAlumno.modalCodigoAlumno}"/>
                            <h:outputText id="aCodigo1" value="#{managerAlumno.modalCodigoAlumno}"/>
                            <br>
                            <h:outputText id="aNombres"
                                          value="#{managerAlumno.modalPaternoAlumno} #{managerAlumno.modalMaternoAlumno} #{managerAlumno.modalNombreAlumno}"/>
                            <br>
                            <h:outputText id="aMesAnio"
                                          value="#{managerAlumno.labelMonth} - #{managerAlumno.labelYear}"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8">
                            <rich:dataTable id="tablaAsistencia" rows="10"
                                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                            cellpadding="0" cellspacing="0" width="100%" border="0"
                                            value="#{managerAlumno.listaAsistencia}" var="Asistencia">
                                <rich:column style="text-align: right">
                                    <f:facet name="header">
                                        <h:outputText value="Lunes"/>
                                    </f:facet>
                                    <h:outputText value="#{Asistencia.nroLunes}" style="#{Asistencia.styleLunes}"
                                                  title="#{Asistencia.labelLunes}"/>
                                </rich:column>

                                <rich:column style="text-align: right">
                                    <f:facet name="header">
                                        <h:outputText value="Martes"/>
                                    </f:facet>
                                    <h:outputText value="#{Asistencia.nroMartes}" style="#{Asistencia.styleMartes}"
                                                  title="#{Asistencia.labelMartes}"/>
                                </rich:column>

                                <rich:column style="text-align: right">
                                    <f:facet name="header">
                                        <h:outputText value="Mi&eacute;rcoles" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{Asistencia.nroMiercoles}"
                                                  style="#{Asistencia.styleMiercoles}"
                                                  title="#{Asistencia.labelMiercoles}"/>
                                </rich:column>

                                <rich:column style="text-align: right">
                                    <f:facet name="header">
                                        <h:outputText value="Jueves"/>
                                    </f:facet>
                                    <h:outputText value="#{Asistencia.nroJueves}" style="#{Asistencia.styleJueves}"
                                                  title="#{Asistencia.labelJueves}"/>
                                </rich:column>

                                <rich:column style="text-align: right">
                                    <f:facet name="header">
                                        <h:outputText value="Viernes"/>
                                    </f:facet>
                                    <h:outputText value="#{Asistencia.nroViernes}" style="#{Asistencia.styleViernes}"
                                                  title="#{Asistencia.labelViernes}"/>
                                </rich:column>

                                <rich:column style="text-align: right">
                                    <f:facet name="header">
                                        <h:outputText value="S&aacute;bado" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{Asistencia.nroSabado}" style="#{Asistencia.styleSabado}"
                                                  title="#{Asistencia.labelSabado}"/>
                                </rich:column>

                                <rich:column style="text-align: right">
                                    <f:facet name="header">
                                        <h:outputText value="Domingo"/>
                                    </f:facet>
                                    <h:outputText value="#{Asistencia.nroDomingo}" style="#{Asistencia.styleDomingo}"
                                                  title="#{Asistencia.labelDomingo}"/>
                                </rich:column>
                            </rich:dataTable>
                        </td>
                    </tr>
                </table>
            </h:form>
        </rich:modalPanel>
    </rich:panel>
</f:view>
</body>
</html>

