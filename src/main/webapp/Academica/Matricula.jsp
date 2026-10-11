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
    <title>Matricula</title>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <table style="width:100%;">
                <tr>
                    <td colspan="3"><strong>MATRICULA</strong></td>
                    <td style="text-align: right">
                        <a4j:status>
                            <f:facet name="start">
                                <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                            </f:facet>
                        </a4j:status>
                        <rich:spacer width="8px"/>
                        <h:commandButton type="button" id="buscar" action="#{managerMatricula.seleccionar}"
                                         image="/Imagenes/actions/viewmag.png" title="Buscar"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="4"><rich:separator height="3" lineType="double"/></td>
                </tr>
                <tr>
                    <td style="width: 8%">C&oacute;digo:</td>
                    <td style="width: 20%">
                        <h:inputText value="#{managerMatricula.paramAluCodigo}" style="width: 180px;"/>
                    </td>
                    <td style="width: 8%">Facultad:</td>
                    <td>
                        <h:selectOneMenu id="FacuBuscar" value="#{managerMatricula.paramFacId}" style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todas ]"/>
                            <f:selectItems value="#{managerMatricula.comboFacultades}"/>
                            <a4j:support event="onchange" reRender="EspeBuscar"
                                         actionListener="#{managerMatricula.cambiarEspecialidadBuscar}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Ap. Paterno:</td>
                    <td>
                        <h:inputText value="#{managerMatricula.paramAluPaterno}" style="width: 180px;"/>
                    </td>
                    <td>Especialidad:</td>
                    <td>
                        <h:selectOneMenu id="EspeBuscar" value="#{managerMatricula.paramEspId}" style="width: 180px;">
                            <f:selectItem itemValue="0" itemLabel="[ Todas ]"/>
                            <f:selectItems value="#{managerMatricula.comboEspecialidades}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Ap. Materno:</td>
                    <td><h:inputText value="#{managerMatricula.paramAluMaterno}" style="width: 180px;"/></td>
                    <td>Semestre:</td>
                    <td>
                        <h:selectOneMenu id="SemBuscar" value="#{managerMatricula.paramSemId}"
                                         style="width: 180px;">
                            <f:selectItems value="#{managerMatricula.comboSemestres}"/>
                        </h:selectOneMenu>
                    </td>
                </tr>
                <tr>
                    <td>Nombres:</td>
                    <td><h:inputText value="#{managerMatricula.paramAluNombre}" style="width: 180px;"/></td>
                </tr>
                <tr>
                    <td colspan="4"><rich:separator height="3" lineType="double"/></td>
                </tr>
            </table>
        </h:form>
        <h:form style="width: 100%;">
            <rich:datascroller id="barra" align="right" for="tablaMaster" maxPages="8" style="width: 100%;"/>
            <rich:dataTable id="tablaMaster" rows="10" cellpadding="0" cellspacing="0" border="0" width="100%"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                            value="#{managerMatricula.listaAlumno}" var="alu">

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="C&oacute;digo" escape="false"/>
                    </f:facet>
                    <h:outputText value="#{alu.rowAluCodigo}"/>
                    <f:param value="#{alu.flagVer}" id="flag"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Apellidos y Nombres"/>
                    </f:facet>
                    <h:outputText value="#{alu.rowAluPaterno} #{alu.rowAluMaterno} #{alu.rowAluNombre}"
                    />
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Esp. Actual"/>
                    </f:facet>
                    <h:outputText value="#{alu.rowEspecialidad}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Tipo"/>
                    </f:facet>
                    <h:outputText value="#{alu.rowTipoMatriculaValue}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Registra"/>
                    </f:facet>
                    <h:outputText value="#{alu.rowSemestreCode}"/>
                </rich:column>
                <rich:column style="text-align: center;">
                    <h:graphicImage value="#{alu.imagen}" style="cursor: pointer;" title="Ver cursos">
                        <a4j:support event="onclick" actionListener="#{alu.verCursos}" reRender="iId,tablaMaster"/>
                    </h:graphicImage>
                </rich:column>
                <rich:column style="text-align: center;">
                    <a4j:commandButton rendered="#{alu.verDeuda}" image="/Imagenes/actions/warning.png"
                                       title="Ver Detalles"
                                       actionListener="#{managerMatricula.verDetalleDeuda}"
                                       oncomplete="#{managerMatricula.oncomplete}"
                                       reRender="d_codigo_alu,d_nombre_alu,tablaDeuda,tablaDeudaBar"/>
                    <a4j:commandButton rendered="#{alu.verMatricular}" image="/Imagenes/actions/matricular.gif"
                                       title="Matricular" action="#{managerMatricula.cursosAbiertos}"
                                       actionListener="#{managerMatricula.editarFila}"
                                       oncomplete="Richfaces.showModalPanel('mp',{top:50})"
                                       reRender="iId,iSem_,iCodigo,iPaterno,iMaterno,iNombre,semestre_vigente,tablaCursos,foto"/>

                    <f:param id="p_id" value="#{alu.studentId}"/>
                    <f:param id="p_codigo" value="#{alu.rowAluCodigo}"/>
                    <f:param id="p_paterno" value="#{alu.rowAluPaterno}"/>
                    <f:param id="p_materno" value="#{alu.rowAluMaterno}"/>
                    <f:param id="p_nombre" value="#{alu.rowAluNombre}"/>
                    <f:param id="p_id_fac_act" value="#{alu.paramFacId}"/>
                    <f:param id="p_id_esp_act" value="#{alu.paramEspId}"/>
                    <f:param id="p_id_semestre" value="#{alu.rowSemestreId}"/>
                    <f:param id="p_id_matricula" value="#{alu.rowMatriculaId}"/>
                    <f:param id="p_mat_tipo" value="#{alu.rowTipoMatriculaId}"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <a4j:commandButton rendered="#{alu.verDeuda}" image="/Imagenes/actions/warning.png"
                                       title="Ver Detalles"
                                       actionListener="#{managerMatricula.verDetalleDeuda}"
                                       oncomplete="#{managerMatricula.oncomplete}"
                                       reRender="d_codigo_alu,d_nombre_alu,tablaDeuda,tablaDeudaBar"/>
                    <a4j:commandButton rendered="#{alu.verRectificar}" image="/Imagenes/actions/rectificacion.gif"
                                       title="Rectificar Matricula"
                                       action="#{managerMatricula.rectificarMatricula}"
                                       actionListener="#{managerMatricula.editarFila}"
                                       oncomplete="#{managerMatricula.oncomplete}"
                                       reRender="iId1,iSem_1,iCodigo1,iPaterno1,iMaterno1,iNombre1,semestre_vigente1,tablaCursos1,foto1"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <h:commandButton image="/Imagenes/actions/anular.gif" rendered="#{alu.verEliminar}"
                                     title="Eliminar Matricula"
                                     actionListener="#{managerMatricula.eliminarMatricula}"
                                     action="#{managerMatricula.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Eliminar?'));"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <h:outputLink value="#{request.contextPath}/report" target="_blank"
                                  rendered="#{alu.verImprimirFicha}" title="Imprimir Ficha">
                        <f:param name="type" value="matricula-ficha"/>
                        <f:param name="matId" value="#{alu.rowMatriculaId}"/>
                        <h:graphicImage value="/Imagenes/actions/fileprint.png"
                                        style="border-width: 0; cursor: pointer"/>
                    </h:outputLink>
                </rich:column>

                <rich:column style="text-align: center;">
                    <h:outputLink value="#{request.contextPath}/report" target="_blank"
                                  rendered="#{alu.verImprimirConsolidado}" title="Imprimir Consolidado">
                        <f:param name="type" value="matricula-consolidado"/>
                        <f:param name="matId" value="#{alu.rowMatriculaId}"/>
                        <h:graphicImage value="/Imagenes/actions/fileprint.png"
                                        style="border-width: 0; cursor: pointer"/>
                    </h:outputLink>
                </rich:column>

                <rich:column style="text-align: center;">
                    <a4j:commandButton rendered="#{alu.verDeuda}" image="/Imagenes/actions/warning.png"
                                       title="Ver Detalles"
                                       actionListener="#{managerMatricula.verDetalleDeuda}"
                                       oncomplete="#{managerMatricula.oncomplete}"
                                       reRender="d_codigo_alu,d_nombre_alu,tablaDeuda,tablaDeudaBar"/>
                    <h:commandButton rendered="#{alu.verReservar}" image="/Imagenes/actions/reservar.gif"
                                     title="Reservar Matricula"
                                     actionListener="#{managerMatricula.reservarMatricula}"
                                     action="#{managerMatricula.seleccionar}"
                                     onclick="return (confirm('¿Esta realmente seguro de Reservar la Matricula?'));"/>
                </rich:column>

                <rich:column style="text-align: center;">
                    <a4j:commandButton rendered="#{alu.verReservar}" image="/Imagenes/actions/curso.png"
                                       title="Ver Ampliaciones"
                                       actionListener="#{managerMatricula.openModalforAmpliaciones}"
                                       oncomplete="Richfaces.showModalPanel('mpAmpliaciones', {top:120})"
                                       reRender="formAmpli">
                        <f:attribute name="student" value="#{alu}"/>
                    </a4j:commandButton>
                </rich:column>

                <rich:subTable id="subtable" rendered="#{alu.renderedSubTable}"
                               onRowMouseOver="this.style.backgroundColor='#F8F8F8'"
                               onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                               value="#{alu.listaCursosMatriculado}" var="det">

                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Cod. Curso"/>
                        </f:facet>
                        <h:outputText value="#{det.detCursoCodigo}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Nom. Curso"/>
                        </f:facet>
                        <h:outputText value="#{det.detCursoNombre}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Cr&eacute;ditos" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{det.detCursoCreditos}"/>
                    </rich:column>
                    <rich:column>
                        <f:facet name="header">
                            <h:outputText value="Nom. Secci&oacute;n" escape="false"/>
                        </f:facet>
                        <h:outputText value="#{det.detSeccionCodigo}"/>
                    </rich:column>
                </rich:subTable>
            </rich:dataTable>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mp" autosized="true" minWidth="720" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro de Matricula"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <table style="width: 100%" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td align="right" colspan="3">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerMatricula.matricular}"
                                           action="#{managerMatricula.seleccionar}"
                                           reRender="tablaMaster, barra, dupHorario"
                                           oncomplete="#{managerMatricula.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td>Semestre:</td>
                    <td>
                        <h:outputText id="semestre_vigente" style="width: 180px;"
                                      value="#{managerMatricula.semestreActual}"/>
                    </td>
                    <td align="right" rowspan="4">
                        <h:graphicImage id="foto" style="width: 100px;"
                                        value="/foto?id=#{managerMatricula.studentId}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">C&oacute;digo:</td>
                    <td width="70%">
                        <h:inputHidden id="iId" value="#{managerMatricula.studentId}"/>
                        <h:inputHidden id="iSem_" value="#{managerMatricula.rowSemestreId}"/>
                        <h:outputText id="iCodigo" style="width: 180px;"
                                      value="#{managerMatricula.rowAluCodigo}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Apellido Paterno:</td>
                    <td width="70%">
                        <h:outputText id="iPaterno" style="width: 180px;"
                                      value="#{managerMatricula.rowAluPaterno}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Apellido Materno:</td>
                    <td width="70%">
                        <h:outputText id="iMaterno" style="width: 180px;"
                                      value="#{managerMatricula.rowAluMaterno}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Nombres:</td>
                    <td width="70%">
                        <h:outputText id="iNombre" style="width: 180px;"
                                      value="#{managerMatricula.rowAluNombre}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" width="100%">
                        <rich:dataTable id="tablaCursos" cellpadding="0" cellspacing="0" width="100%" border="0"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        value="#{managerMatricula.listaCursos}" var="mat">

                            <h:column id="cCod">
                                <f:facet name="header">
                                    <h:outputText value="Cod."/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoCode}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Nom."/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoName}"/>
                            </h:column>
                            <rich:column style="text-align: center">
                                <f:facet name="header">
                                    <h:outputText value="Ciclo."/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoCiclo}"/>
                            </rich:column>
                            <rich:column style="text-align: center">
                                <f:facet name="header">
                                    <h:outputText value="Cr&eacute;ditos" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoCreditos}"/>
                            </rich:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Tipo"/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoTipo}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Seccion"/>
                                </f:facet>
                                <h:selectOneMenu value="#{mat.seccionId}" id="seccion">
                                    <f:selectItems value="#{mat.comboSecciones}"/>
                                </h:selectOneMenu>
                            </h:column>
                        </rich:dataTable>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:separator height="1"/>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mp6" autosized="true" minWidth="640" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Detalles de la Deuda"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp6')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <table style="width: 100%">
                <tr>
                    <td colspan="2"><rich:separator height="3" lineType="double"/></td>
                </tr>
                <tr>
                    <td colspan="2" style="text-align: center; font-weight: bold; height: 24px">
                        Detalles de la Deuda
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><rich:separator height="3" lineType="double"/></td>
                </tr>
                <tr>
                    <td style="width: 20%">C&oacute;digo de Alumno:</td>
                    <td><h:outputText id="d_codigo_alu" value="#{managerMatricula.rowAluCodigo}"/></td>
                </tr>
                <tr>
                    <td>Nombre del Alumno:</td>
                    <td><h:outputText id="d_nombre_alu" value="#{managerMatricula.rowAluNombre}"/></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:datascroller id="tablaDeudaBar" align="right" for="tablaDeuda" maxPages="8"
                                           style="width: 100%;"/>
                        <rich:dataTable id="tablaDeuda" rows="10" cellpadding="0" cellspacing="0"
                                        onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        width="100%" border="0" value="#{managerMatricula.detalleDeuda}" var="ddeu">
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Semestre"/>
                                </f:facet>
                                <h:outputText value="#{ddeu.mpRowDeudaSemestre}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Concepto"/>
                                </f:facet>
                                <h:outputText value="#{ddeu.mpRowDeudaConceptoPago}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="F. de Pago"/>
                                </f:facet>
                                <h:outputText value="#{ddeu.mpRowDeudaFechaPago}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="F. de Prorroga"/>
                                </f:facet>
                                <h:outputText value="#{ddeu.mpRowDeudaFechaProrroga}"/>
                            </h:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpRectificar" autosized="true" minWidth="720" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Rectificaci&oacute;n de Matricula" escape="false"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpRectificar')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <table style="width: 100%;" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td align="right" colspan="3">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerMatricula.matricular}"
                                           action="#{managerMatricula.seleccionar}"
                                           reRender="tablaMaster, barra, dupHorario"
                                           oncomplete="#{managerMatricula.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:separator height="3" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td>Semestre:</td>
                    <td>
                        <h:outputText id="semestre_vigente1" style=" width : 180px;"
                                      value="#{managerMatricula.semestreActual}"/>
                    </td>
                    <td align="right" width="20%" rowspan="5" colspan="4">
                        <h:graphicImage id="foto1" style="width : 100px;"
                                        value="/foto?id=#{managerMatricula.studentId}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">C&oacute;digo:</td>
                    <td width="70%">
                        <h:inputHidden id="iId1" value="#{managerMatricula.studentId}"/>
                        <h:inputHidden id="iSem_1" value="#{managerMatricula.rowSemestreId}"/>
                        <h:outputText id="iCodigo1" style=" width : 180px;"
                                      value="#{managerMatricula.rowAluCodigo}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Apellido Paterno:</td>
                    <td width="70%">
                        <h:outputText id="iPaterno1" style=" width : 180px;"
                                      value="#{managerMatricula.rowAluPaterno}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Apellido Materno:</td>
                    <td width="70%">
                        <h:outputText id="iMaterno1" style=" width : 180px;"
                                      value="#{managerMatricula.rowAluMaterno}"/>
                    </td>
                </tr>
                <tr>
                    <td width="30%">Nombres:</td>
                    <td width="70%">
                        <h:outputText id="iNombre1" style=" width : 180px;"
                                      value="#{managerMatricula.rowAluNombre}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" width="100%">
                        <rich:spacer height="10px"/>
                        <rich:dataTable id="tablaCursos1" onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                        onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                        cellpadding="0" cellspacing="0" width="100%" border="0"
                                        value="#{managerMatricula.listaCursos}" var="mat">
                            <h:column id="cId">
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoId}"/>
                            </h:column>
                            <h:column id="cCod">
                                <f:facet name="header">
                                    <h:outputText value="Cod."/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoCode}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Nom."/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoName}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Ciclo."/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoCiclo}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Creditos"/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoCreditos}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Tipo"/>
                                </f:facet>
                                <h:outputText value="#{mat.mpRowCursoTipo}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Secci&oacute;n" escape="false"/>
                                </f:facet>
                                <h:selectOneMenu value="#{mat.seccionId}" id="seccion">
                                    <f:selectItems value="#{mat.comboSecciones}"/>
                                </h:selectOneMenu>
                            </h:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="dupHorario" autosized="true" minWidth="700" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Cruce de Horario"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('dupHorario')" title="Cerrar"/>
        </f:facet>
        <h:form>
            <rich:dataTable value="#{managerMatricula.listaDuplicadoHor}" var="dup" align="center">
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="Nro"/>
                    </f:facet>
                    <h:outputText value="#{dup.dupId}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="curso"/>
                    </f:facet>
                    <h:outputText value="#{dup.dupNombreCurso}"/>
                </rich:column>

                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="cruce horario"/>
                    </f:facet>
                    <h:outputText value="#{dup.dupNombreCursos}" escape="false"/>
                </rich:column>
            </rich:dataTable>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpAmpliaciones" autosized="true" minWidth="300" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Ampliaciones"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpAmpliaciones')" title="Cerrar"/>
        </f:facet>
        <a4j:form id="formAmpli">
            <div class="message">
                Cr&eacute;ditos ampliados: <h:outputText value="#{managerMatricula.detCursoCreditos}"/>
            </div>
        </a4j:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
