package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.academica.jsf.beans.BeanNotas;
import net.uch.academica.jsf.beans.SubstituteActRow;
import net.uch.mapping.UserNotFoundException;
import net.uch.util.HSTestSupportDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 12/06/2020.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbNotaTest extends SpringConfigurationTest {

    private static HSTestSupportDao DAO;
    private MbNota manager;

    @Autowired
    public void setStaticName(HSTestSupportDao dao) {
        DAO = dao;
    }

    @Before
    public void setUp() {
        manager = new MbNota();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadTreeAndNodeSelectionTest() {
        // Loading tree
        TreeNode<String> result = manager.getArbol();

        assertNotNull(result);
        assertNotNull(manager.getNodos());
        assertTrue(manager.getListaNotas().isEmpty());

        // Selection
        NodeSelectedEvent nodeSelectedEvent = mock(NodeSelectedEvent.class);
        UITree uiTree = mock(UITree.class);

        when(nodeSelectedEvent.getComponent()).thenReturn(uiTree);
        when(uiTree.getRowKey()).thenReturn("1:1:1:2:2"); // 2010-II | ALGORITMICA II

        manager.seleccion(nodeSelectedEvent);

        assertNotEquals(0, manager.getNodeCurapeId());
        assertFalse(manager.getNodeCurapeDesc().isEmpty());
        assertNotNull(manager.getListaNotas());

        MbNota n = manager.getListaNotas().get(0);
        assertNotEquals(0, n.getSecId());
        assertFalse(n.getSecCodigo().isEmpty());
        assertFalse(n.getSecNombre().isEmpty());
        assertNotEquals(0, n.getSecVacantes());
        assertEquals(manager.getNodeCurapeId(), n.getNodeCurapeId());
    }

    @Test
    public void asignarNotasTest() {
        MbNota bean = new MbNota();
        bean.setNodeCurapeId(856); // ALGORITMICA II
        bean.setSecId(1848); // ISI02MA

        Map<String, Object> attributes = Collections.singletonMap("seccion", bean);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.asignarNotas(actionEvent);

        assertEquals(23, manager.getRegistroNotas().size());
        assertEquals(2, manager.getRegistroNotas().get(0).getEvaluations().size());

        for (BeanNotas rn : manager.getRegistroNotas()) {
            for (BeanNotas e : rn.getEvaluations()) {
                e.setGradeValue(String.valueOf(Stubs.randomNumeric(5, 21)));
            }
        }
        manager.getRegistroNotas().get(0).getEvaluations().get(0).setGradeValue("");

        manager.insertar(actionEvent);
        assertEquals(23, manager.getRegistroNotas().size());

        // ------------------------------------------------------------------
        manager.asignarNotas(actionEvent);

        manager.insertar(actionEvent);

        assertEquals(23, manager.getRegistroNotas().size());

        DAO.cleanAcNotas(bean.getSecId());
    }

    @Test
    public void asignarNotasActasTest() {
        manager.setCodigoActa("");
        manager.setNombreActa("");
        manager.setNumeroActa("");

        MbNota bean = new MbNota();
        bean.setNodeCurapeId(1051); // INGENIERIA DE CONTROL I
        bean.setSecId(2255); // IET06MA

        Map<String, Object> attributes = Collections.singletonMap("seccion", bean);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.asignarNotasActas(actionEvent);

        assertEquals(5, manager.getCabecerasNotas().size());
        assertEquals(18, manager.getRegistroNotas().size());
        assertEquals(5, manager.getRegistroNotas().get(0).getEvaluations().size());
        // Codigo/nombre are now auto-generated, not left blank for manual entry
        assertFalse(manager.getCodigoActa().isEmpty());
        assertFalse(manager.getNombreActa().isEmpty());

        // ------------------------------------------------------------------
        manager.setCodigoActa("");
        manager.setNombreActa("");
        manager.setNumeroActa("");

        manager.insertarActa(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese el codigo del acta"));

        // ------------------------------------------------------------------
        manager.setCodigoActa(RandomStringUtils.randomAlphanumeric(10));
        manager.insertarActa(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese el nombre del acta"));

        // ------------------------------------------------------------------
        manager.setNombreActa(RandomStringUtils.randomAlphanumeric(20));
        manager.insertarActa(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese el numero del acta"));

        // ------------------------------------------------------------------
        manager.setNumeroActa(RandomStringUtils.randomNumeric(10));
        manager.insertarActa(actionEvent);
        assertEquals(18, manager.getRegistroNotas().size());

        DAO.cleanAcActa(bean.getSecId());
    }

    @Test
    public void openModalForActaSustitutoriaTest() {
        int secId = 2257; // SEMESTRE VI - TELECOMUNICACIONES II

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("id_seccion", secId));

        manager.llenarActaSustitutoria(actionEvent);

        assertTrue(manager.getOncomplete().contains("Esta seccion no posee un acta registrada"));

        // ------------------------------------------------------------------
        secId = 511; // SEMESTRE TRASLADO - MATEMATICA I
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("id_seccion", secId));

        manager.llenarActaSustitutoria(actionEvent);

        // The Substitute Act shows its own identity (code "...-S", "Acta Sustitutoria" name, number).
        assertTrue(manager.getCodigoActa().endsWith("-S"));
        assertTrue(manager.getNombreActa().startsWith("Acta Sustitutoria"));
        assertFalse(manager.getNumeroActa().isEmpty());

        // Columns are driven by the section's evaluation system (EP/EF/PA/PI), not hardcoded.
        assertEquals(4, manager.getSubstituteHeaders().size());

        List<SubstituteActRow> rows = manager.getSubstituteActRows();
        // One row per student: no X*Y duplication from the multiple acta-detail rows.
        assertEquals(23, rows.size());
        assertEquals(rows.size(), (int) rows.stream().map(SubstituteActRow::getStudentId).distinct().count());

        SubstituteActRow row = rows.get(0);
        assertNotEquals(0, row.getStudentId());
        assertFalse(row.getStudentCode().isEmpty());
        assertFalse(row.getStudentName().isEmpty());
        assertEquals("--", row.getSubstitute());
        assertEquals(manager.getSubstituteHeaders().size(), row.getComponents().size());
        // Section 511 students are all convalidación: every component shows the "C" marker.
        assertTrue(row.isConvalidated());
        row.getComponents().forEach(component -> assertEquals("C", component.getValue()));
        assertFalse(row.getOriginalFinal().isEmpty());
    }

    @Test
    public void seleccionarSecParaBloqueoWeb() {
        MbNota bean = new MbNota();
        bean.setNodeCurapeId(479); // MATEMATICA II - SEM TRASLADOS
        bean.setSecId(909); // IET02MA

        Map<String, Object> attributes = Collections.singletonMap("seccion", bean);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.openModalForBloqueoWeb(actionEvent);

        int size = manager.getMpSistemaEvaluacion().size();
        assertEquals(4, size);
        assertEquals(bean.getSecId(), manager.getSecId());

        MbNota row = manager.getMpSistemaEvaluacion().get(0);
        assertNotEquals(0, row.getMpSisevaPerId());
        assertFalse(row.getMpSisevaPerCodigo().isEmpty());
        assertFalse(row.getMpSisevaPerDescripcion().isEmpty());
        assertFalse(row.isMpMarcar());

        // ------------------------------------------------------------------
        for (int i = 0; i < size; i++) {
            manager.getMpSistemaEvaluacion().get(i).setMpMarcar(i % 2 == 0);
        }

        manager.bloquearNotas(actionEvent);
        assertTrue(manager.getOncomplete().contains("mpbloquea"));

        // ------------------------------------------------------------------
        manager.openModalForBloqueoWeb(actionEvent);

        size = manager.getMpSistemaEvaluacion().size();

        for (int i = 0; i < size; i++) {
            manager.getMpSistemaEvaluacion().get(i).setMpMarcar(i % 2 != 0);
        }

        manager.bloquearNotas(actionEvent);
        assertTrue(manager.getOncomplete().contains("mpbloquea"));

        DAO.cleanAcImportacionNotas(bean.getSecId());
    }

    @Test
    public void openModalImportarNotasTest() {
        int sisevaperId = 6770; // 1ER PROM. ACTIVIDADES
        // EDUCACIÓN PRIMARIA E INTERCULTURALIDAD
        MbNota bean = new MbNota();
        bean.setNodeCurapeId(998); // TEORIA DE LA INTERCULTURALIDAD
        bean.setSecId(2171); // EPI05TA

        DAO.prepareAcNotasTemporales(bean.getSecId(), sisevaperId);

        Map<String, Object> attributes = Collections.singletonMap("seccion", bean);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.openModalForImport(actionEvent);

        assertEquals(0, manager.getMpSisevaPerId());
        assertEquals(2, manager.getCboRadioSisEva().length);

        // ------------------------------------------------------------------
        manager.setMpSisevaPerId(sisevaperId);

        manager.vistaPrevia(actionEvent);

        assertTrue(manager.getOncomplete().contains("mpvista"));

        // Saving
        manager.guardarImporte(actionEvent);

        assertTrue(manager.getOncomplete().contains("hideModalPanel('mpimporta')"));

        // ------------------------------------------------------------------
        manager.openModalForImport(actionEvent);

        // Editing
        manager.setMpSisevaPerId(sisevaperId);

        manager.guardarImporte(actionEvent);

        DAO.cleanAcNotaAndAcNotasTemporalesAndAcImportacionNotas(bean.getSecId(), sisevaperId);
    }

    @Test
    public void openModalImportarNotasSinNotasTemporalesTest() {
        int sisevaperId = 6771; // 2DO PROM. ACTIVIDADES
        // EDUCACIÓN PRIMARIA E INTERCULTURALIDAD
        MbNota bean = new MbNota();
        bean.setNodeCurapeId(998); // TEORIA DE LA INTERCULTURALIDAD
        bean.setSecId(2172); // EPI05NA

        Map<String, Object> attributes = Collections.singletonMap("seccion", bean);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.openModalForImport(actionEvent);

        assertEquals(0, manager.getMpSisevaPerId());
        assertEquals(2, manager.getCboRadioSisEva().length);

        // Saving
        manager.setMpSisevaPerId(sisevaperId);

        manager.guardarImporte(actionEvent);

        assertTrue(manager.getOncomplete().contains("No hay notas temporales"));
    }

    @Test
    public void vistaPreviaAlumnosSinNotasTest() {
        manager.setSecId(2195);
        manager.setMpSisevaPerId(6712);

        manager.vistaPrevia(actionEvent);

        assertEquals(29, manager.getListarAlumnoNota().size());
        assertTrue(manager.getOncomplete().contains("mpvista"));
    }

    @Test
    public void fillingGradesAndSubstituteActTest() {
        int sectionId = 907; // MATEMATICA I

        // Start from a clean slate: a published Acta left by a previous run would (correctly) be
        // blocked by the publish guard in asignarNotas and make this test non-idempotent.
        DAO.cleanAcActa(sectionId);

        MbNota bean = new MbNota();
        bean.setNodeCurapeId(477);
        bean.setSecId(sectionId);

        Map<String, Object> attributes = Collections.singletonMap("seccion", bean);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(attributes);

        manager.asignarNotas(actionEvent);
        assertTrue(manager.getOncomplete().contains("mp"));
        List<BeanNotas> grades = manager.getRegistroNotas();
        for (BeanNotas nota : grades) {
            List<BeanNotas> evas = nota.getEvaluations();
            for (var j = 0; j < evas.size(); j++) {
                BeanNotas eva = evas.get(j);
                if (j % 2 == 0) {
                    eva.setGradeValue(String.valueOf(Stubs.randomNumeric(5, 11)));
                } else {
                    eva.setGradeValue(String.valueOf(Stubs.randomNumeric(13, 21)));
                }
            }
        }
        manager.getRegistroNotas().forEach(nota ->
                nota.getEvaluations().forEach(eva ->
                        eva.setGradeValue(String.valueOf(Stubs.randomNumeric(5, 21)))));

        manager.insertar(actionEvent);

        // ------------------------------------------------------------------
        manager.asignarNotasActas(actionEvent);
        assertTrue(manager.getOncomplete().contains("mp2"));

        // Grade policy: every student has a truncated final in range and a PASS/FAIL status.
        assertFalse(manager.getActaPublishRows().isEmpty());
        manager.getActaPublishRows().forEach(actaRow -> {
            assertTrue(actaRow.getFinalGrade() >= 0 && actaRow.getFinalGrade() <= 20);
            assertTrue(GradePolicy.STATUS_PASS.equals(actaRow.getStatus())
                    || GradePolicy.STATUS_FAIL.equals(actaRow.getStatus()));
        });

        manager.setCodigoActa(RandomStringUtils.randomAlphanumeric(10));
        manager.setNombreActa(RandomStringUtils.randomAlphanumeric(20));
        manager.setNumeroActa(RandomStringUtils.randomNumeric(10));
        manager.insertarActa(actionEvent);
        assertTrue(manager.getOncomplete().contains("mp2"));

        // ------------------------------------------------------------------
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("id_seccion", sectionId));
        manager.llenarActaSustitutoria(actionEvent);

        List<SubstituteActRow> substituteRows = manager.getSubstituteActRows();
        // Multi-component section (EP/EF/PA/PI): prove one row per student, not a grade matrix.
        assertEquals(substituteRows.size(),
                (int) substituteRows.stream().map(SubstituteActRow::getStudentId).distinct().count());
        substituteRows.forEach(ac -> {
            ac.editarFila(actionEvent);
            ac.cancelar(actionEvent);

            ac.editarFila(actionEvent);
            ac.setSubstitute(String.valueOf(Stubs.randomNumeric(13, 19)));
            ac.editar(actionEvent);
        });

        manager.insertarSustitutorios(actionEvent);
        assertTrue(manager.getOncomplete().contains("mp3"));

        // ------------------------------------------------------------------
        // Once published, neither Acta can be silently overwritten again
        when(uiComponent.getAttributes()).thenReturn(attributes);
        manager.asignarNotasActas(actionEvent);
        manager.insertarActa(actionEvent);
        assertTrue(manager.getOncomplete().contains("ya fue publicada"));

        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("id_seccion", sectionId));
        manager.llenarActaSustitutoria(actionEvent);
        manager.insertarSustitutorios(actionEvent);
        assertTrue(manager.getOncomplete().contains("ya fue publicada"));

        DAO.cleanAcActa(sectionId);
        DAO.cleanAcNotas(sectionId);
    }
}
