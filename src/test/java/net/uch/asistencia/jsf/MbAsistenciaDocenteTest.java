package net.uch.asistencia.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.asistencia.jsf.beans.NuevaAsistenciaBean;
import net.uch.common.SystemHelper;
import net.uch.mapping.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static net.uch.Stubs.Especialidad;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 08/06/2020.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbAsistenciaDocenteTest extends SpringConfigurationTest {

    @Autowired
    private SystemHelper helper;
    private MbAsistenciaDocente manager;

    @Before
    public void setUp() {
        manager = new MbAsistenciaDocente();
        manager.init();
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void onLoadPageTest() {
        SelectItem[] result = manager.getTiposAsistencia();
        assertNotNull(result);

        result = manager.getCboDocente();
        assertNotNull(result);
    }

    @Test
    public void autocompleteTest() {
        List<MbAsistenciaDocente> result = manager.autocomplete("diaz");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0).getSugDocCodigo());
        assertNotEquals(0, result.get(0).getSugDocId());
        assertNotNull(result.get(0).getSugDocNombre());
    }

    @Test
    public void mostrarSessionAsistenciaTipoAsistenciaSeccionTest() {
        manager.setParamDocIdBk(127); // GALARRETA DIAZ JOSE HERMENEGILDO
        manager.setParamSessionFechaIni(Date.valueOf(LocalDate.of(2008, 1, 7)));
        manager.setParamSessionFechaFin(Date.valueOf(LocalDate.of(2020, 6, 1)));
        manager.setParamTipasisCode("050001");

        manager.mostrarSessionAsistencia(actionEvent);

        assertNotNull(manager.getListarSessiones());
    }

    @Test
    public void mostrarSessionAsistenciaTipoAsistenciaAreaTest() {
        manager.setParamDocIdBk(144); // ALVINES FERNANDEZ DORIS
        manager.setParamSessionFechaIni(Date.valueOf(LocalDate.of(2010, 1, 1)));
        manager.setParamSessionFechaFin(Date.valueOf(LocalDate.of(2010, 12, 31)));
        manager.setParamTipasisCode("050002");

        manager.mostrarSessionAsistencia(actionEvent);

        assertNotNull(manager.getListarSessiones());
        assertEquals(72, manager.getListarSessiones().size());
    }

    @Test
    public void mostrarSessionAsistenciaTipoAsistenciaCursoLibreTest() {
        manager.setParamDocIdBk(241); // ACOSTA SOLORZANO WILLIAMS FERNANDO
        manager.setParamSessionFechaIni(Date.valueOf(LocalDate.of(2010, 1, 1)));
        manager.setParamSessionFechaFin(Date.valueOf(LocalDate.of(2010, 12, 31)));
        manager.setParamTipasisCode("050003");

        manager.mostrarSessionAsistencia(actionEvent);

        assertNotNull(manager.getListarSessiones());
        assertEquals(15, manager.getListarSessiones().size());
    }

    @Test
    public void openingModalForSaveEditAndRegisterAssistanceTest() {
        int docId = 284; // MEDINA TASAYCO GRACIANO
        String result = manager.limpiar();

        assertTrue(result.isEmpty());
        assertNotNull(manager.getNuevAsisDocente());

        LocalDate ld = LocalDate.now().plusDays(3);
        LocalDateTime ldt = ld.atTime(LocalTime.now());

        manager.getNuevAsisDocente().setDocId(docId);
        manager.getNuevAsisDocente().setEspecialidadCode(Especialidad.ING_SISTEMAS);
        manager.getNuevAsisDocente().setSeccionCode("ISI01MA");
        manager.getNuevAsisDocente().setCursoSeccion(1834);
        manager.getNuevAsisDocente().setFecSesion(helper.convertToDate(ld));
        manager.getNuevAsisDocente().setRegHora(ldt.getHour());
        manager.getNuevAsisDocente().setRegMin(ldt.getMinute());
        manager.getNuevAsisDocente().setSalHora(ldt.plusHours(1).getHour());
        manager.getNuevAsisDocente().setSalMin(ldt.plusMinutes(30).getMinute());
        manager.getNuevAsisDocente().setTipasisCode("050001");
        manager.getNuevAsisDocente().setTipsesCode("047001");

        result = manager.validarNuevaSesion();

        assertTrue(result.isEmpty());
        assertNotEquals(0, manager.getRowSesId());
        assertTrue(manager.getOncomplete().contains("mpNuevaSesion"));

        // Opening modal to Edit
        UIParameter uiParameterId = mock(UIParameter.class);
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("w_ses_id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getRowSesId());

        manager.seleccionarSesion(actionEvent);

        assertNotEquals(0, manager.getMpDocId());
        assertFalse(manager.getMpEspNombre().isEmpty());
        assertNotNull(manager.getMpHoraIni2());
        assertNotNull(manager.getMpHoraFin2());
        assertTrue(manager.getOncomplete().contains("mpSessionAsis"));

        // Updating
        UIParameter uiParameterTeacherId = mock(UIParameter.class);
        UIParameter uiParameterChangeId = mock(UIParameter.class);
        UIParameter uiParameterStart = mock(UIParameter.class);
        UIParameter uiParameterEnd = mock(UIParameter.class);

        when(uiComponent.findComponent("p_ses_id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getRowSesId());
        when(uiComponent.findComponent("p_cambio")).thenReturn(uiParameterChangeId);
        when(uiParameterChangeId.getValue()).thenReturn(38); // JORGE DEL CARPIO SALINAS
        when(uiComponent.findComponent("p_doc_ant")).thenReturn(uiParameterTeacherId);
        when(uiParameterTeacherId.getValue()).thenReturn(docId);
        when(uiComponent.findComponent("p_inicio")).thenReturn(uiParameterStart);
        when(uiParameterStart.getValue()).thenReturn(helper.convertToDate(ldt));
        when(uiComponent.findComponent("p_fin")).thenReturn(uiParameterEnd);
        when(uiParameterEnd.getValue()).thenReturn(helper.convertToDate(ldt.plusHours(2)));

        manager.actualizarSesion(actionEvent);

        assertTrue(manager.getOncomplete().contains("Richfaces.hideModalPanel('mpSessionAsis')"));

        // Opening modal to Mark 1st time
        UIParameter uiParameterCourse = mock(UIParameter.class);
        UIParameter uiParameterSection = mock(UIParameter.class);

        manager.setSugDocId(250); // For the change made

        when(uiComponent.findComponent("w_ses_id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getRowSesId());
        when(uiComponent.findComponent("p_cur_nombre")).thenReturn(uiParameterCourse);
        when(uiParameterCourse.getValue()).thenReturn(manager.getRowCurNombre());
        when(uiComponent.findComponent("p_sec_nombre")).thenReturn(uiParameterSection);
        when(uiParameterSection.getValue()).thenReturn(manager.getRowSecNombre());
        when(uiComponent.findComponent("w_doc_id")).thenReturn(uiParameterTeacherId);
        when(uiParameterTeacherId.getValue()).thenReturn(manager.getSugDocId());

        manager.seleccionarMarcaDocente(actionEvent);

        assertEquals(0, manager.getAsisDocente().getSesefeasisdocId());
        assertNotNull(manager.getAsisDocente().getRegistro());
        assertNotNull(manager.getAsisDocente().getSalida());
        assertFalse(manager.getAsisDocente().getDocNombre().isEmpty());
        assertFalse(manager.getAsisDocente().getSecNombre().isEmpty());
        assertFalse(manager.getAsisDocente().getCurNombre().isEmpty());
        assertTrue(manager.getOncomplete().contains("Richfaces.showModalPanel('mpAsisDocente')"));

        // Mark start hour
        UIParameter uiParameterEffectiveSession = mock(UIParameter.class);

        when(uiComponent.findComponent("p_ses_id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getAsisDocente().getSesId());
        when(uiComponent.findComponent("p_doc_id")).thenReturn(uiParameterTeacherId);
        when(uiParameterTeacherId.getValue()).thenReturn(manager.getAsisDocente().getDocId());
        when(uiComponent.findComponent("p_sesefec_id")).thenReturn(uiParameterEffectiveSession);
        when(uiParameterEffectiveSession.getValue()).thenReturn(manager.getAsisDocente().getSesefeasisdocId());

        manager.getAsisDocente().setEstasisCode("046001");
        manager.getAsisDocente().setRegHora(ldt.getHour());
        manager.getAsisDocente().setRegMin(ldt.getMinute());

        manager.guardarMarcaAsistencia(actionEvent);

        assertTrue(manager.getOncomplete().contains("Richfaces.hideModalPanel('mpAsisDocente')"));

        // Opening modal to Mark 2nd time
        when(uiComponent.findComponent("w_ses_id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getRowSesId());
        when(uiComponent.findComponent("p_cur_nombre")).thenReturn(uiParameterCourse);
        when(uiParameterCourse.getValue()).thenReturn(manager.getRowCurNombre());
        when(uiComponent.findComponent("p_sec_nombre")).thenReturn(uiParameterSection);
        when(uiParameterSection.getValue()).thenReturn(manager.getRowSecNombre());
        when(uiComponent.findComponent("w_doc_id")).thenReturn(uiParameterTeacherId);
        when(uiParameterTeacherId.getValue()).thenReturn(manager.getSugDocId());

        manager.seleccionarMarcaDocente(actionEvent);

        assertNotEquals(0, manager.getAsisDocente().getSesefeasisdocId());
        assertTrue(manager.getOncomplete().contains("Richfaces.showModalPanel('mpAsisDocente')"));

        // Mark End hour
        when(uiComponent.findComponent("p_ses_id")).thenReturn(uiParameterId);
        when(uiParameterId.getValue()).thenReturn(manager.getAsisDocente().getSesId());
        when(uiComponent.findComponent("p_doc_id")).thenReturn(uiParameterTeacherId);
        when(uiParameterTeacherId.getValue()).thenReturn(manager.getAsisDocente().getDocId());
        when(uiComponent.findComponent("p_sesefec_id")).thenReturn(uiParameterEffectiveSession);
        when(uiParameterEffectiveSession.getValue()).thenReturn(manager.getAsisDocente().getSesefeasisdocId());

        manager.getAsisDocente().setSalHora(ldt.plusHours(2).getHour());
        manager.getAsisDocente().setSalMin(ldt.minusMinutes(1).getMinute());
        manager.getAsisDocente().setSesefeasisdocObs("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        manager.guardarMarcaAsistencia(actionEvent);

        assertTrue(manager.getOncomplete().contains("Richfaces.hideModalPanel('mpAsisDocente')"));
    }

    @Test
    public void nuevaSesionCruzandoMedianocheTest() {
        // BUG-01: a session 23:00 -> 01:00 crosses midnight; the checkout belongs to the
        // next calendar day and must be accepted, not rejected as inicio > fin.
        manager.limpiar();
        LocalDate ld = LocalDate.now().plusDays(3);

        manager.getNuevAsisDocente().setDocId(284); // MEDINA TASAYCO GRACIANO
        manager.getNuevAsisDocente().setEspecialidadCode(Especialidad.ING_SISTEMAS);
        manager.getNuevAsisDocente().setSeccionCode("ISI01MA");
        manager.getNuevAsisDocente().setCursoSeccion(1834);
        manager.getNuevAsisDocente().setFecSesion(helper.convertToDate(ld));
        manager.getNuevAsisDocente().setRegHora(23);
        manager.getNuevAsisDocente().setRegMin(0);
        manager.getNuevAsisDocente().setSalHora(1);
        manager.getNuevAsisDocente().setSalMin(0);
        manager.getNuevAsisDocente().setTipasisCode("050001");
        manager.getNuevAsisDocente().setTipsesCode("047001");

        manager.validarNuevaSesion();

        assertTrue(manager.getOncomplete().contains("SE INGRESO LA NUEVA SESSION"));
        LocalDateTime ini = helper.convertToLocalDateTime(manager.getNuevAsisDocente().getFecSesionIni());
        LocalDateTime fin = helper.convertToLocalDateTime(manager.getNuevAsisDocente().getFecSesionFin());
        assertTrue(fin.isAfter(ini));
        assertEquals(ld.plusDays(1), fin.toLocalDate());
    }

    @Test
    public void nuevaSesionHorasIgualesSigueRechazadaTest() {
        // Equal check-in and checkout remain invalid even with midnight rollover in place.
        manager.limpiar();
        LocalDate ld = LocalDate.now().plusDays(3);

        manager.getNuevAsisDocente().setDocId(284);
        manager.getNuevAsisDocente().setEspecialidadCode(Especialidad.ING_SISTEMAS);
        manager.getNuevAsisDocente().setSeccionCode("ISI01MA");
        manager.getNuevAsisDocente().setCursoSeccion(1834);
        manager.getNuevAsisDocente().setFecSesion(helper.convertToDate(ld));
        manager.getNuevAsisDocente().setRegHora(8);
        manager.getNuevAsisDocente().setRegMin(30);
        manager.getNuevAsisDocente().setSalHora(8);
        manager.getNuevAsisDocente().setSalMin(30);
        manager.getNuevAsisDocente().setTipasisCode("050001");
        manager.getNuevAsisDocente().setTipsesCode("047001");

        manager.validarNuevaSesion();

        assertTrue(manager.getOncomplete().contains("LA HORA DE INICIO NO PUEDE SER IGUAL A LA DE FIN"));
    }

    @Test
    public void openingModalNuevaSesionTest() {
        NuevaAsistenciaBean nab = new NuevaAsistenciaBean();
        manager.setNuevAsisDocente(nab);

        SelectItem[] teachers = manager.getCboDocente();
        SelectItem[] specialities = manager.getEspecialidades();
        List<SelectItem> sections = manager.getSecciones();
        List<SelectItem> courses = manager.getCursos();
        SelectItem[] assistanceType = manager.getTiposAsistencia();
        SelectItem[] sessionType = manager.getTiposSesiones();

        assertNotNull(teachers);
        assertNotNull(specialities);
        assertEquals(5, specialities.length);
        assertNotNull(sections);
        assertTrue(sections.isEmpty());
        assertNotNull(courses);
        assertTrue(courses.isEmpty());
        assertNotNull(assistanceType);
        assertEquals(3, assistanceType.length);
        assertNotNull(sessionType);
        assertEquals(3, sessionType.length);

        // On change speciality
        manager.getNuevAsisDocente().setEspecialidadCode(Especialidad.CONTABILIDAD);
        manager.onChangeEspecialidades(actionEvent);
        sections = manager.getSecciones();

        assertEquals(Especialidad.CONTABILIDAD, manager.getNuevAsisDocente().getEspecialidadCode());
        assertEquals(13, sections.size());

        // On change section
        manager.getNuevAsisDocente().setSeccionCode("CF01MA");
        manager.onChangeSecciones(actionEvent);
        courses = manager.getCursos();

        assertEquals("CF01MA", manager.getNuevAsisDocente().getSeccionCode());
        assertEquals(7, courses.size());
    }
}
