<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML>
<html lang="es">
<head>
    <title>Seguimiento de notas</title>
    <style>
        .uch-table-headercell {
            white-space: pre-wrap;
            max-width: 80px;
        }

        .uch-table-title {
            text-align: center;
            margin-bottom: 5px;
            font-size: 14px;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript">
        // Grade policy (frontend guard): only digits and a single decimal point may be typed.
        function gradeKeyFilter(event) {
            var e = event || window.event;
            var code = e.which || e.keyCode;
            // allow control keys (backspace, tab, enter, arrows, delete)
            if (code === 8 || code === 9 || code === 13 || code === 46 || (code >= 35 && code <= 40)) {
                return true;
            }
            var ch = String.fromCharCode(code);
            if (ch === '.') {
                return this.value.indexOf('.') === -1; // at most one decimal point
            }
            return ch >= '0' && ch <= '9';
        }

        // Range 0..20 and 0.5 increments only; reset and warn otherwise (empty means 0, allowed).
        function gradeValidate(input) {
            var value = input.value.trim();
            if (value === '') {
                return;
            }
            var grade = parseFloat(value);
            if (isNaN(grade) || grade < 0 || grade > 20 || (grade * 2) % 1 !== 0) {
                alert('Nota invalida. Ingrese un valor entre 0 y 20 en incrementos de 0.5.');
                input.value = '';
                input.focus();
            }
        }
    </script>
</head>
<body>
<f:view>
    <jsp:include page="../TablaSistema/Menu.jsp"/>
    <rich:panel>
        <h:form id="form1">
            <div class="row">
                <div class="col title-label">LISTADO DE NOTAS</div>
                <div class="col" style="text-align: right">
                    <a4j:status>
                        <f:facet name="start">
                            <h:graphicImage value="/Imagenes/actions/loading.gif"/>
                        </f:facet>
                    </a4j:status>
                    <rich:spacer width="8px"/>
                    <h:commandButton type="button" id="buscar" action="#{managerNotas.cargarArbol}"
                                     image="/Imagenes/actions/reload.png" title="Refrescar"/>
                </div>
            </div>
            <rich:separator height="3" lineType="double" style="margin-bottom: 10px"/>
            <div class="row">
                <div class="col-3">
                    <rich:tree value="#{managerNotas.arbol}" ajaxSubmitSelection="true" switchType="ajax"
                               nodeSelectListener="#{managerNotas.seleccion}" var="item"
                               reRender="o_curso,tablaMaster"/>
                </div>
                <div class="col" style="vertical-align:top;">
                    <div class="row" style="height: 25px;">
                        <div class="col">
                            <h:outputText id="o_curso" value="#{managerNotas.nodeCurapeDesc}"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col">
                            <rich:dataTable id="tablaMaster" cellpadding="0" cellspacing="0" border="0" width="100%"
                                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
                                            value="#{managerNotas.listaNotas}" var="notas1" rows="20">

                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="C&oacute;digo de Secci&oacute;n" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{notas1.secCodigo}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Nombre de Secci&oacute;n" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{notas1.secNombre}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="Matriculados"/>
                                    </f:facet>
                                    <h:outputText value="#{notas1.secVacantes}"/>
                                </rich:column>
                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Imprimir"/>
                                    </f:facet>
                                    <h:outputLink value="#{request.contextPath}/report" target="_blank"
                                                  title="Imprimir Todos los cursos">
                                        <f:param name="type" value="notas-seccion"/>
                                        <f:param name="seccion" value="#{notas1.secNombre}"/>
                                        <h:graphicImage value="/Imagenes/actions/fileprint.png"
                                                        style="cursor:pointer;"/>
                                    </h:outputLink>
                                </rich:column>
                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Ingresar Notas"/>
                                    </f:facet>
                                    <f:param value="#{notas1.secId}" id="id_seccion"/>
                                    <f:param value="#{notas1.secNombre}" id="id_nombre_sec"/>
                                    <f:param value="#{notas1.nodeCurapeId}" id="id_cur_ape"/>
                                    <a4j:commandButton image="/Imagenes/actions/notas.png"
                                                       actionListener="#{managerNotas.asignarNotas}"
                                                       oncomplete="#{managerNotas.oncomplete}"
                                                       reRender="formNotas">
                                        <f:attribute name="seccion" value="#{notas1}"/>
                                    </a4j:commandButton>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Acta Princ."/>
                                    </f:facet>
                                    <h:graphicImage value="/Imagenes/actions/calc.gif"
                                                    style="cursor:pointer;" title="Acta Princ.">
                                        <a4j:support event="onclick"
                                                     actionListener="#{managerNotas.asignarNotasActas}"
                                                     oncomplete="#{managerNotas.oncomplete}"
                                                     reRender="formNotasActa">
                                            <f:attribute name="seccion" value="#{notas1}"/>
                                        </a4j:support>
                                    </h:graphicImage>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Acta Sust."/>
                                    </f:facet>
                                    <h:graphicImage value="/Imagenes/actions/academico.png"
                                                    style="cursor:pointer;" title="Act.Susti">
                                        <a4j:support event="onclick"
                                                     actionListener="#{managerNotas.llenarActaSustitutoria}"
                                                     oncomplete="#{managerNotas.oncomplete}"
                                                     reRender="curso_nombre,nombre_acta_final,codigo_acta_final,numero_acta_final,substituteGrid">
                                            <f:attribute name="id_seccion" value="#{notas1.secId}"/>
                                        </a4j:support>
                                    </h:graphicImage>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Bloqueo Web"/>
                                    </f:facet>
                                    <a4j:commandButton actionListener="#{managerNotas.openModalForBloqueoWeb}"
                                                       image="/Imagenes/actions/bloqueo.png"
                                                       reRender="dtimporte"
                                                       onclick="Richfaces.showModalPanel('mpbloquea',{top:100})">
                                        <f:attribute name="seccion" value="#{notas1}"/>
                                    </a4j:commandButton>
                                </rich:column>

                                <rich:column style="text-align: center;">
                                    <f:facet name="header">
                                        <h:outputText value="Imp. Nota"/>
                                    </f:facet>
                                    <a4j:commandButton actionListener="#{managerNotas.openModalForImport}"
                                                       image="/Imagenes/actions/importar18.png"
                                                       reRender="radio"
                                                       onclick="Richfaces.showModalPanel('mpimporta',{top:100})">
                                        <f:attribute name="seccion" value="#{notas1}"/>
                                    </a4j:commandButton>
                                </rich:column>
                            </rich:dataTable>
                        </div>
                    </div>
                </div>
            </div>
        </h:form>
    </rich:panel>

    <rich:modalPanel id="mp" autosized="true" minWidth="750" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro de Notas"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp')" title="Cerrar"/>
        </f:facet>
        <h:form id="formNotas">
            <table width="100%">
                <tr>
                    <td>
                        <h:outputText id="cur_nombre" style="font-weight: bold" value="#{managerNotas.nodeCurapeDesc}"/>
                        <br/>
                        <h:outputText id="docente_nombre" style="font-size: 11px; color: #555"
                                      value="Docente: #{managerNotas.teacherName}"
                                      rendered="#{not empty managerNotas.teacherName}"/>
                    </td>
                    <td style="text-align: right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png" title="Guardar"
                                           actionListener="#{managerNotas.insertar}"
                                           oncomplete="#{managerNotas.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <rich:separator height="2" lineType="double"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <div style="height:390px; overflow-x: hidden; overflow-y: auto;">
                            <table class="rich-table" style="width: 100%" aria-label="grades">
                                <thead class="rich-table-thead">
                                <tr class="rich-table-subheader">
                                    <th class="rich-table-subheadercell" scope="col">APELLIDOS Y NOMBRES</th>
                                    <a4j:repeat value="#{managerNotas.cabecerasNotas}" var="h">
                                        <th class="rich-table-subheadercell uch-table-headercell" scope="col">
                                            <h:outputText value="#{h}"/>
                                        </th>
                                    </a4j:repeat>
                                </tr>
                                </thead>
                                <tbody>
                                <a4j:repeat id="PanelNotas" value="#{managerNotas.registroNotas}" var="row">
                                    <tr class="rich-table-row"
                                        onmouseover="this.style.backgroundColor='#F1F1F1'"
                                        onmouseout="this.style.backgroundColor='#FFFFFF'">
                                        <td class="rich-table-cell"><h:outputText value="#{row.student}"/></td>
                                        <a4j:repeat value="#{row.evaluations}" var="column">
                                            <td class="rich-table-cell" style="text-align: center;">
                                                <h:inputText value="#{column.gradeValue}" style="#{column.style}"
                                                             autocomplete="off"
                                                             onkeypress="return gradeKeyFilter.call(this, event)"
                                                             onblur="gradeValidate(this)"/>
                                            </td>
                                        </a4j:repeat>
                                    </tr>
                                </a4j:repeat>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mp2" autosized="true" minWidth="750" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Registro de Acta"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp2')" title="Cerrar"/>
        </f:facet>
        <h:form id="formNotasActa">
            <table style="width: 100%">
                <tr>
                    <td colspan="2">
                        <h:outputText id="cur_nombre1" style="font-weight: bold"
                                      value="#{managerNotas.nodeCurapeDesc}"/>
                    </td>
                    <td style="text-align: right">
                        <h:graphicImage value="/Imagenes/actions/filesave.png" style="cursor:pointer"
                                        title="Publicar"
                                        onclick="Richfaces.showModalPanel('mpConfirmarActaPrincipal')"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%">C&oacute;digo Acta</td>
                    <td><h:outputText value="#{managerNotas.codigoActa}"/></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Nombre Acta</td>
                    <td><h:outputText value="#{managerNotas.nombreActa}"/></td>
                    <td></td>
                </tr>
                <tr>
                    <td>N&uacute;mero de Acta</td>
                    <td><h:outputText value="#{managerNotas.numeroActa}"/></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="3">
                        <rich:separator height="1"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <div style="height:390px; overflow-x: hidden; overflow-y: auto;">
                            <table class="rich-table" style="width: 100%" aria-label="grades">
                                <thead class="rich-table-thead">
                                <tr class="rich-table-subheader">
                                    <th class="rich-table-subheadercell" scope="col">APELLIDOS Y NOMBRES</th>
                                    <a4j:repeat value="#{managerNotas.cabecerasNotas}" var="h">
                                        <th class="rich-table-subheadercell" scope="col">
                                            <h:outputText value="#{h}"/>
                                        </th>
                                    </a4j:repeat>
                                </tr>
                                </thead>
                                <tbody>
                                <a4j:repeat value="#{managerNotas.registroNotas}" var="row">
                                    <tr class="rich-table-row"
                                        onmouseover="this.style.backgroundColor='#F1F1F1'"
                                        onmouseout="this.style.backgroundColor='#FFFFFF'">
                                        <td class="rich-table-cell"><h:outputText value="#{row.student}"/></td>
                                        <a4j:repeat value="#{row.evaluations}" var="column">
                                            <td class="rich-table-cell" style="text-align: center;">
                                                <h:outputText value="#{column.gradeValue}" style="#{column.style}"/>
                                            </td>
                                        </a4j:repeat>
                                    </tr>
                                </a4j:repeat>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mp3" autosized="true" minWidth="660" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Listado de Acta Sustitutoria"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mp3')" title="Cerrar"/>
        </f:facet>
        <h:form id="form4">
            <table width="100%">
                <tr>
                    <td colspan="2">
                        <h:outputText id="curso_nombre" style="font-weight: bold"
                                      value="#{managerNotas.nodeCurapeDesc}"/>
                    </td>
                    <td style="text-align: right">
                        <h:graphicImage value="/Imagenes/actions/filesave.png" style="cursor:pointer"
                                        title="Publicar"
                                        onclick="Richfaces.showModalPanel('mpConfirmarActaSustitutoria')"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td>C&oacute;digo Acta</td>
                    <td>
                        <h:outputText id="codigo_acta_final" value="#{managerNotas.codigoActa}"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td style="width: 15%">Nombre Acta</td>
                    <td>
                        <h:outputText id="nombre_acta_final" value="#{managerNotas.nombreActa}"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td>N&uacute;mero de Acta</td>
                    <td>
                        <h:outputText id="numero_acta_final" value="#{managerNotas.numeroActa}"/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="3"><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td colspan="3">
                        <a4j:outputPanel id="substituteGrid" layout="block"
                                         style="max-height:390px; overflow-x: hidden; overflow-y: auto;">
                            <table class="rich-table" style="width: 100%" aria-label="substitute-grades">
                                <thead class="rich-table-thead">
                                <tr class="rich-table-subheader">
                                    <th class="rich-table-subheadercell" scope="col">Cod.</th>
                                    <th class="rich-table-subheadercell" scope="col">ALUMNO</th>
                                        <%-- Dynamic component columns, driven by the section evaluation system --%>
                                    <a4j:repeat value="#{managerNotas.substituteHeaders}" var="h">
                                        <th class="rich-table-subheadercell" scope="col">
                                            <h:outputText value="#{h}"/>
                                        </th>
                                    </a4j:repeat>
                                    <th class="rich-table-subheadercell" scope="col">PFinal</th>
                                    <th class="rich-table-subheadercell" scope="col">Susti.</th>
                                    <th class="rich-table-subheadercell" scope="col">Editar</th>
                                </tr>
                                </thead>
                                <tbody>
                                <a4j:repeat value="#{managerNotas.substituteActRows}" var="acta">
                                    <tr class="rich-table-row"
                                        onmouseover="this.style.backgroundColor='#F1F1F1'"
                                        onmouseout="this.style.backgroundColor='#FFFFFF'">
                                        <td class="rich-table-cell"><h:outputText value="#{acta.studentCode}"/></td>
                                        <td class="rich-table-cell"><h:outputText value="#{acta.studentName}"/></td>
                                        <a4j:repeat value="#{acta.components}" var="column">
                                            <td class="rich-table-cell" style="text-align: center;">
                                                <h:outputText value="#{column.value}" style="#{column.style}"/>
                                            </td>
                                        </a4j:repeat>
                                        <td class="rich-table-cell" style="text-align: center;">
                                            <h:outputText value="#{acta.originalFinal}"/>
                                        </td>
                                        <td class="rich-table-cell" style="text-align: center;">
                                            <h:outputText value="#{acta.substitute}" rendered="#{!acta.editable}"/>
                                            <h:inputText value="#{acta.substitute}" rendered="#{acta.editable}"
                                                         style="width:30px" disabled="#{acta.convalidated}"
                                                         onkeypress="return gradeKeyFilter.call(this, event)"
                                                         onblur="gradeValidate(this)"/>
                                        </td>
                                        <td class="rich-table-cell" style="text-align: center;">
                                            <a4j:commandButton actionListener="#{acta.editarFila}" title="Editar"
                                                               image="/Imagenes/actions/klipper_dock.png"
                                                               rendered="#{!acta.editable and !acta.convalidated}"
                                                               reRender="substituteGrid"/>
                                            <a4j:commandButton actionListener="#{acta.editar}" title="Guardar"
                                                               image="/Imagenes/actions/filesave.png"
                                                               rendered="#{acta.editable}" reRender="substituteGrid"
                                                               oncomplete="#{acta.oncomplete}"/>
                                            <a4j:commandButton actionListener="#{acta.cancelar}" title="Cancelar"
                                                               image="/Imagenes/actions/fileclose.png"
                                                               rendered="#{acta.editable}" reRender="substituteGrid"/>
                                        </td>
                                    </tr>
                                </a4j:repeat>
                                </tbody>
                            </table>
                        </a4j:outputPanel>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpConfirmarActaPrincipal" autosized="true" minWidth="360" zindex="2100">
        <f:facet name="header">
            <h:outputText value="Confirmar publicacion"/>
        </f:facet>
        <h:form id="formConfirmarActaPrincipal">
            <h:outputText
                    value="Esta accion publicara el Acta de forma definitiva. Una vez publicada, solo podra corregirse mediante el Acta Sustitutoria. Desea continuar?"/>
            <div style="text-align: right; margin-top: 15px;">
                <a4j:commandButton value="Publicar" actionListener="#{managerNotas.insertarActa}"
                                   oncomplete="Richfaces.hideModalPanel('mpConfirmarActaPrincipal');#{managerNotas.oncomplete}"/>
                <input type="button" value="Cancelar"
                       onclick="Richfaces.hideModalPanel('mpConfirmarActaPrincipal')"/>
            </div>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpConfirmarActaSustitutoria" autosized="true" minWidth="360" zindex="2100">
        <f:facet name="header">
            <h:outputText value="Confirmar publicacion"/>
        </f:facet>
        <h:form id="formConfirmarActaSustitutoria">
            <h:outputText
                    value="Esta accion publicara el Acta Sustitutoria de forma definitiva y no podra modificarse nuevamente. Desea continuar?"/>
            <div style="text-align: right; margin-top: 15px;">
                <a4j:commandButton value="Publicar" actionListener="#{managerNotas.insertarSustitutorios}"
                                   oncomplete="Richfaces.hideModalPanel('mpConfirmarActaSustitutoria');#{managerNotas.oncomplete}"/>
                <input type="button" value="Cancelar"
                       onclick="Richfaces.hideModalPanel('mpConfirmarActaSustitutoria')"/>
            </div>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpbloquea" autosized="true" minWidth="300" zindex="2000" onshow="showDiv();">
        <rich:effect name="hideDiv" for="contentDiv" type="Fade"/>
        <rich:effect name="showDiv" for="contentDiv" type="Appear"/>
        <f:facet name="header">
            <h:outputText value="Bloquear Nota"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpbloquea')" title="Cerrar"/>
        </f:facet>
        <h:form id="formblo">
            <table style="width: 100%">
                <tr>
                    <td style="text-align: right">
                        <a4j:commandButton image="/Imagenes/actions/filesave.png"
                                           actionListener="#{managerNotas.bloquearNotas}"
                                           oncomplete="#{managerNotas.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <rich:dataTable id="dtimporte" width="100%"
                                        value="#{managerNotas.mpSistemaEvaluacion}" var="importe">
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="C&oacute;digo" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{importe.mpSisevaPerCodigo}"/>
                            </rich:column>
                            <rich:column>
                                <f:facet name="header">
                                    <h:outputText value="Descripci&oacute;n" escape="false"/>
                                </f:facet>
                                <h:outputText value="#{importe.mpSisevaPerDescripcion}"/>
                            </rich:column>
                            <rich:column style="text-align: center;">
                                <f:facet name="header">
                                    <h:outputText value="Marcar"/>
                                </f:facet>
                                <h:selectBooleanCheckbox value="#{importe.mpMarcar}"/>

                                <f:param id="w_siseva_per_id" value="#{importe.mpSisevaPerId}"/>
                            </rich:column>
                        </rich:dataTable>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpimporta" autosized="true" minWidth="400" zindex="2000" onshow="showDiv();">
        <rich:effect name="hideDiv" for="contentDiv" type="Fade"/>
        <rich:effect name="showDiv" for="contentDiv" type="Appear"/>
        <f:facet name="header">
            <h:outputText value="Importar Notas"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpimporta')" title="Cerrar"/>
        </f:facet>
        <h:form id="formimportar">
            <table align="center" style="width: 100%">
                <tr>
                    <td style="text-align: right">
                        <a4j:commandButton title="Importar Notas" image="/Imagenes/actions/filesave.png"
                                           actionListener="#{managerNotas.guardarImporte}"
                                           oncomplete="#{managerNotas.oncomplete}"/>
                    </td>
                </tr>
                <tr>
                    <td><rich:separator height="1"/></td>
                </tr>
                <tr>
                    <td>
                        <h:selectOneRadio id="radio" layout="pageDirection" value="#{managerNotas.mpSisevaPerId}">
                            <f:selectItems value="#{managerNotas.cboRadioSisEva}"/>
                        </h:selectOneRadio>
                        <br/>
                        <rich:separator height="1"/>
                        <div style="text-align: right">
                            Vista previa
                            <a4j:commandButton title="Vista Previa" image="/Imagenes/actions/viewmag.png"
                                               actionListener="#{managerNotas.vistaPrevia}"
                                               oncomplete="#{managerNotas.oncomplete}"
                                               reRender="aluNota, aluNota2"/>
                        </div>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>

    <rich:modalPanel id="mpvista" autosized="true" zindex="2000">
        <f:facet name="header">
            <h:outputText value="Vista Previa"/>
        </f:facet>
        <f:facet name="controls">
            <h:graphicImage value="/Imagenes/actions/stop.png" style="cursor:pointer"
                            onclick="Richfaces.hideModalPanel('mpvista')" title="Cerrar"/>
        </f:facet>
        <h:form id="formvista">
            <table>
                <tr>
                    <td style="padding: 0 10px">
                        <div class="uch-table-title">
                            <h:outputText value="Nota Principal"/>
                        </div>
                        <div style="overflow-x: hidden; overflow-y: auto">
                            <rich:dataTable id="aluNota" value="#{managerNotas.listarAlumnoNota}"
                                            width="390px" var="listaAluNota">
                                <rich:column style="text-align:center">
                                    <f:facet name="header"><h:outputText value="Nro"/></f:facet>
                                    <h:outputText value="#{listaAluNota.nroOrden}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="C&oacute;digo" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{listaAluNota.alumnoCodigoFinal}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header"><h:outputText value="Apellidos y nombres"/></f:facet>
                                    <h:outputText value="#{listaAluNota.alumnoNombreFinal}"/>
                                </rich:column>
                                <rich:column style="text-align:right">
                                    <f:facet name="header"><h:outputText value="Nota"/></f:facet>
                                    <h:outputText value="#{listaAluNota.finales}"/>
                                </rich:column>
                            </rich:dataTable>
                        </div>
                    </td>
                    <td style="padding: 0 10px">
                        <div class="uch-table-title">
                            <h:outputText value="Nota Temporal"/>
                        </div>
                        <div style="overflow-x: hidden; overflow-y: auto">
                            <rich:dataTable value="#{managerNotas.listarAlumnoNota}" var="listaAluNota2" id="aluNota2"
                                            width="390px">
                                <rich:column style="text-align:center">
                                    <f:facet name="header"><h:outputText value="Nro"/></f:facet>
                                    <h:outputText value="#{listaAluNota2.nroOrden}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header">
                                        <h:outputText value="C&oacute;digo" escape="false"/>
                                    </f:facet>
                                    <h:outputText value="#{listaAluNota2.alumnoCodigoFinal}"/>
                                </rich:column>
                                <rich:column>
                                    <f:facet name="header"><h:outputText value="Apellidos y nombres"/></f:facet>
                                    <h:outputText value="#{listaAluNota2.alumnoNombreFinal}"/>
                                </rich:column>
                                <rich:column style="text-align:right">
                                    <f:facet name="header"><h:outputText value="Nota"/></f:facet>
                                    <h:outputText value="#{listaAluNota2.finalFinales}"/>
                                </rich:column>
                            </rich:dataTable>
                        </div>
                    </td>
                </tr>
            </table>
        </h:form>
    </rich:modalPanel>
</f:view>
</body>
</html>
