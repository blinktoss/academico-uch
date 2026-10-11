package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.common.audit.CurrentUserContext;
import net.uch.mapping.AcAlumno;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.component.UIParameter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.uch.Stubs.*;
import static net.uch.common.Constants.CAT_DEFAULT;
import static net.uch.common.Constants.PARAM_CODIGO;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created for sistema_academico on 06/02/2021.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbAlumnoTest extends SpringConfigurationTest {

    private MbAlumno manager;

    @Autowired
    @Qualifier("springHibernateDaoAlumno")
    private HSAlumnoDAO alumnoDAO;

    @Before
    public void setUp() {
        manager = new MbAlumno();
        manager.init();

        // Simulate what AuditUserFilter does on every request in production: expose the
        // logged-in user's id to AuditInterceptor via CurrentUserContext.
        loggerUser.setRowUsuId(Stubs.USUARIO_ADMIN_ID);
        CurrentUserContext.set(loggerUser.getRowUsuId());
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void getComboFacultadBuscarTest() {
        SelectItem[] result = manager.getComboFacultadBuscar();
        List<SelectItem> paramEsps = manager.getComboEspecialidadBuscar();

        assertEquals(5, result.length);
        assertTrue(paramEsps.isEmpty());

        // ------------------------------------------------------------------
        manager.setParamIdFacAct(Facultad.CONTABILIDAD);
        manager.cambiarEspecialidadBuscar(actionEvent);
        paramEsps = manager.getComboEspecialidadBuscar();
        assertEquals(1, paramEsps.size());

        // ------------------------------------------------------------------
        manager.setParamIdFacAct(0);
        manager.cambiarEspecialidadBuscar(actionEvent);
        paramEsps = manager.getComboEspecialidadBuscar();
        assertTrue(paramEsps.isEmpty());

        // ------------------------------------------------------------------
        assertFalse(manager.getSemestreActual().isEmpty());
    }

    @Test
    public void seleccionarTest() {
        manager.setParamCodigo("");
        manager.setParamIdFacAct(Facultad.CONTABILIDAD);
        manager.setParamMaterno("");
        manager.setParamNombre("");
        manager.setParamPaterno("DI");
        manager.setParamIdEspAct(Especialidad.CONTABILIDAD);

        manager.seleccionar(actionEvent);

        assertFalse(manager.getListaAlumno().isEmpty());

        MbAlumno row = manager.getListaAlumno().get(0);
        assertNotEquals(0, row.getAlumnoId());
        assertTrue(row.getAlumnoDni().isEmpty());
        assertFalse(row.getEspActual().isEmpty());
        assertTrue(row.getAlumnoMail().isEmpty());
        assertNull(row.getAlumnoProcedenciaDesc());
        assertNotEquals("0", row.getAlumnoEstadoId());
        assertNotEquals("0", row.getAlumnoModalidadId());
        assertNotEquals("0", row.getAlumnoTipoId());
    }

    @Test
    public void formForNewStudent() {
        manager.nuevo(actionEvent);

        assertTrue(manager.getModalPaternoAlumno().isEmpty());
        assertTrue(manager.getModalMaternoAlumno().isEmpty());
        assertTrue(manager.getModalNombreAlumno().isEmpty());
        assertTrue(manager.getModalDniAlumno().isEmpty());
        assertTrue(manager.getModalMailAlumno().isEmpty());

        SelectItem[] result = manager.getComboCatalogoTipo();
        assertEquals(5, result.length);

        result = manager.getComboCatalogoModa();
        assertEquals(6, result.length);

        result = manager.getComboCatalogoEstado();
        assertEquals(3, result.length);

        result = manager.getComboFacultadesIngreso();
        assertEquals(5, result.length);

        // ------------------------------------------------------------------
        manager.setModalIdFacIngAlumno(Facultad.CONTABILIDAD);
        manager.cambiarEspecialidadIngreso(actionEvent);
        List<SelectItem> lResult = manager.getComboEspecialidadesIngreso();
        assertEquals(1, lResult.size());
        lResult = manager.getComboPlanIngreso();
        assertTrue(lResult.isEmpty());

        manager.setModalIdFacIngAlumno(0);
        manager.cambiarEspecialidadIngreso(actionEvent);
        lResult = manager.getComboEspecialidadesIngreso();
        assertTrue(lResult.isEmpty());

        // ------------------------------------------------------------------
        manager.setModalIdEspIngAlumno(Especialidad.CONTABILIDAD);
        manager.cambiarPlanIngreso(actionEvent);
        lResult = manager.getComboPlanIngreso();
        assertEquals(1, lResult.size());

        result = manager.getComboSemestre();
        assertNotEquals(0, result.length);

        result = manager.getComboFacultadesActual();
        assertEquals(5, result.length);

        // ------------------------------------------------------------------
        manager.setModalIdFacActAlumno(Facultad.EDUCACION_PRIMARIA);
        manager.cambiarEspecialidadActual(actionEvent);
        lResult = manager.getComboEspecialidadesActual();
        assertEquals(1, lResult.size());
        lResult = manager.getComboPlanActual();
        assertTrue(lResult.isEmpty());

        manager.setModalIdEspActAlumno(Especialidad.EDUCACION_PRIMARIA);
        manager.cambiarPlanActual(actionEvent);
        lResult = manager.getComboPlanActual();
        assertEquals(1, lResult.size());

        // ------------------------------------------------------------------
        result = manager.getComboProcedencia();
        assertEquals(14, result.length);

        result = manager.getComboTipoFamiliar();
        assertEquals(12, result.length);

        result = manager.getComboFormaPago();
        assertEquals(2, result.length);

        result = manager.getComboUnidad();
        assertEquals(10, result.length);
    }

    @Test
    public void insertTest() throws IOException, SQLException {
        manager.nuevo(actionEvent);

        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalPaternoAlumno("FAKE_" + RandomStringUtils.randomAlphanumeric(10));
        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalMaternoAlumno("FAKE_" + RandomStringUtils.randomAlphanumeric(10));
        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalNombreAlumno("FAKE_" + RandomStringUtils.randomAlphanumeric(5));
        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalDniAlumno(RandomStringUtils.randomNumeric(8));
        manager.setModalMailAlumno(RandomStringUtils.randomAlphanumeric(10) + "@fake.xyz");
        manager.setModalIdTipoAlumno("009001");
        manager.setModalIdModalidadAlumno(ModalidadDeIngreso.random());
        manager.setModalIdEstadoAlumno("011001");
        manager.setModalIdEspIngAlumno(Especialidad.CONTABILIDAD);
        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalIdPlanIngAlumno(1);
        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalIdSemestreAlumno(SEMESTRE_2010_2_ID);
        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalIdPlanActAlumno(1);
        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalIdEspActAlumno(Especialidad.CONTABILIDAD);
        manager.setAlumnoProcedenciaId("023009");
        manager.setModalFamiliarAlumno("");
        manager.setModalIdTipoFamiliarAlumno("040001");
        manager.setModalIdFormaPagoAlumno("041001");
        manager.setModalUnidadAlumno("042001");
        manager.setModalMontoPagoAlumno(0);

        URL resource = getClass().getClassLoader().getResource("img/user.png");
        assertNotNull(resource);
        when(externalContext.getResourceAsStream(anyString())).thenReturn(resource.openStream());

        // Insert new student
        manager.insertar(actionEvent);

        assertTrue(manager.getOncomplete().contains("'mp'"));
        assertNotEquals(0, manager.getModalIdAlumno());

        // On insert AuditInterceptor.onSave fills only created_*; updated_* stay null
        // until the row is first modified.
        AcAlumno insertedStudent = alumnoDAO.seleccionarUnAlumno(manager.getModalIdAlumno());
        assertNotNull("created_at must be stamped on insert", insertedStudent.getCreatedAt());
        assertNotNull("created_by must be stamped on insert", insertedStudent.getCreatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, insertedStudent.getCreatedBy().intValue());
        assertNull("updated_at must stay null until first update", insertedStudent.getUpdatedAt());
        assertNull("updated_by must stay null until first update", insertedStudent.getUpdatedBy());

        // ------------------------------------------------------------------
        MbAlumno alu = new MbAlumno();
        alu.setAlumnoDni(manager.getModalDniAlumno());
        alu.setAlumnoMail(manager.getModalMailAlumno());
        alu.setAlumnoId(manager.getModalIdAlumno());
        alu.setParamCodigo(manager.getModalCodigoAlumno());
        alu.setParamPaterno(manager.getModalPaternoAlumno() + "_EDIT");
        alu.setParamMaterno(manager.getModalMaternoAlumno() + "_EDIT");
        alu.setParamNombre(manager.getModalNombreAlumno());
        alu.setAlumnoTipoId(manager.getModalIdTipoAlumno());
        alu.setAlumnoModalidadId(manager.getModalIdModalidadAlumno());
        alu.setAlumnoEstadoId(manager.getModalIdEstadoAlumno());

        var facIng = Stubs.Facultad.random();
        alu.setAlumnoFacIngId(facIng.getFacultadId());
        alu.setAlumnoEspIngId(facIng.getEspecialidadId());
        alu.setAlumnoPlanIngId(facIng.getPlanId());

        var facAct = Stubs.Facultad.random();
        alu.setParamIdFacAct(facAct.getFacultadId());
        alu.setParamIdEspAct(facAct.getEspecialidadId());
        alu.setAlumnoPlanActId(facAct.getPlanId());
        alu.setAlumnoSemestreId(SEMESTRE_2010_2_ID);
        alu.setAlumnoProcedenciaId("023014");
        alu.setModalFamiliarAlumno("FAKE_EDIT_" + RandomStringUtils.randomNumeric(10));
        alu.setModalIdTipoFamiliarAlumno("040008");
        alu.setModalIdFormaPagoAlumno("041002");
        alu.setModalUnidadAlumno("042006");
        alu.setModalMontoPagoAlumno(100);

        // Updating new student
        updateStudentTest(alu);

        // Guardar datos de alumno la primera vez
        saveStudentDataTest(alu);
    }

    private void updateStudentTest(MbAlumno alu) throws IOException, SQLException {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("p_alu", alu));

        manager.editarFila(actionEvent);

        assertEquals(alu.getAlumnoId(), manager.getModalIdAlumno());
        assertEquals(alu.getAlumnoFacIngId(), manager.getModalIdFacIngAlumno());
        assertEquals(alu.getAlumnoEspIngId(), manager.getModalIdEspIngAlumno());
        assertEquals(alu.getAlumnoPlanIngId(), manager.getModalIdPlanIngAlumno());
        assertEquals(alu.getParamIdFacAct(), manager.getModalIdFacActAlumno());
        assertEquals(alu.getParamIdEspAct(), manager.getModalIdEspActAlumno());
        assertEquals(alu.getAlumnoPlanActId(), manager.getModalIdPlanActAlumno());
        assertEquals(alu.getAlumnoSemestreId(), manager.getModalIdSemestreAlumno());
        assertEquals(alu.getAlumnoProcedenciaId(), manager.getAlumnoProcedenciaId());
        assertEquals(alu.getModalFamiliarAlumno(), manager.getModalFamiliarAlumno());
        assertEquals(alu.getModalIdTipoFamiliarAlumno(), manager.getModalIdTipoFamiliarAlumno());
        assertEquals(alu.getModalIdFormaPagoAlumno(), manager.getModalIdFormaPagoAlumno());
        assertEquals(alu.getModalUnidadAlumno(), manager.getModalUnidadAlumno());
        assertEquals(alu.getModalMontoPagoAlumno(), manager.getModalMontoPagoAlumno(), 0);

        manager.setModalNombreAlumno("");
        manager.insertar(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese todos los datos Requeridos"));

        // ------------------------------------------------------------------
        manager.setModalNombreAlumno(alu.getParamNombre() + "_EDIT");
        manager.insertar(actionEvent);

        assertTrue(manager.getOncomplete().contains("'mp'"));

        // created_* must survive the update (load-then-merge in insertar),
        // while updated_* are now stamped for the first time by AuditInterceptor.onFlushDirty.
        AcAlumno updatedStudent = alumnoDAO.seleccionarUnAlumno(alu.getAlumnoId());
        assertNotNull("created_at must be preserved on update", updatedStudent.getCreatedAt());
        assertNotNull("created_by must be preserved on update", updatedStudent.getCreatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, updatedStudent.getCreatedBy().intValue());
        assertNotNull("updated_at must be stamped on first update", updatedStudent.getUpdatedAt());
        assertNotNull("updated_by must be stamped on first update", updatedStudent.getUpdatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, updatedStudent.getUpdatedBy().intValue());
    }

    private void saveStudentDataTest(MbAlumno alu) {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("p_alu", alu));

        manager.editarFila2(actionEvent);

        // ------------------------------------------------------------------
        manager.setDocumentos(Arrays.asList("031001", "031002", "031003"));
        manager.setColegio("FAKE_SCHOOL_" + RandomStringUtils.randomAlphanumeric(13));
        manager.setCorreo(alu.getAlumnoMail());
        manager.setNumeroDocumento(alu.getAlumnoDni());
        String[] schoolTypes = new String[]{"030001", "030002", "030003", "030004", "030005", "030006", "030007"};
        manager.setTipoColegio(schoolTypes[Stubs.randomNumeric(0, schoolTypes.length)]);

        manager.guardar();

        // ------------------------------------------------------------------
        manager.editarFila2(actionEvent);

        // Actualizar datos de alumno
        manager.setTelefonoFijo(RandomStringUtils.randomNumeric(7));
        manager.setDireccionLima("FAKE_AVENUE_" + RandomStringUtils.randomAlphanumeric(10));

        manager.guardar();
    }

    @Test
    public void getComboDocumentosTest() {
        SelectItem[] result = manager.getComboDocumentos();

        assertEquals(3, result.length);
    }

    @Test
    public void getComboTipoColegioTest() {
        SelectItem[] result = manager.getComboTipoColegio();

        assertEquals(7, result.length);
    }

    @Test
    public void getCombosexoTest() {
        SelectItem[] result = manager.getCombosexo();

        assertEquals(2, result.length);
    }

    @Test
    public void getComboDepartamentosProvinciasDistrictosColTest() {
        SelectItem[] result = manager.getComboDepartamentos1();
        List<SelectItem> provinces = manager.getComboProvincias1();
        List<SelectItem> districts = manager.getComboDistrito1();

        assertEquals(26, result.length);
        assertTrue(provinces.isEmpty());
        assertTrue(districts.isEmpty());

        // ------------------------------------------------------------------
        manager.setDepartamentos1("060000"); // CAJAMARCA
        manager.cambiaDepartamentoCol(actionEvent);
        provinces = manager.getComboProvincias1();
        districts = manager.getComboDistrito1();

        assertEquals(13, provinces.size());
        assertTrue(districts.isEmpty());

        // ------------------------------------------------------------------
        manager.setProvincias1("060400"); // CHOTA
        manager.cambiaProvinciaCol(actionEvent);
        provinces = manager.getComboProvincias1();
        districts = manager.getComboDistrito1();

        assertEquals(13, provinces.size());
        assertEquals(19, districts.size());

        // ------------------------------------------------------------------
        manager.setProvincias1(CAT_DEFAULT);
        manager.cambiaProvinciaCol(actionEvent);
        provinces = manager.getComboProvincias1();
        districts = manager.getComboDistrito1();

        assertEquals(13, provinces.size());
        assertTrue(districts.isEmpty());

        // ------------------------------------------------------------------
        manager.setDepartamentos1(CAT_DEFAULT);
        manager.cambiaDepartamentoCol(actionEvent);
        provinces = manager.getComboProvincias1();
        districts = manager.getComboDistrito1();

        assertTrue(provinces.isEmpty());
        assertTrue(districts.isEmpty());
    }

    @Test
    public void getComboPreparacionTest() {
        SelectItem[] result = manager.getComboPreparacion();

        assertEquals(3, result.length);
    }

    @Test
    public void getComboTipoDocumentoTest() {
        SelectItem[] result = manager.getComboTipoDocumento();

        assertEquals(5, result.length);
    }

    @Test
    public void getComboAcademiasTest() {
        SelectItem[] result = manager.getComboAcademias();

        assertEquals(3, result.length);
    }

    @Test
    public void getComboAniosPreparacionTest() {
        SelectItem[] result = manager.getComboAniosPreparacion();

        assertEquals(5, result.length);
    }

    @Test
    public void getComboMesesPreparacionTest() {
        SelectItem[] result = manager.getComboMesesPreparacion();

        assertEquals(12, result.length);
    }

    @Test
    public void getComboDepartamentosTest() {
        SelectItem[] result = manager.getComboDepartamentos();

        assertEquals(26, result.length);
    }

    @Test
    public void getComboDepartamentosProvinciasDistrictosNacTest() {
        SelectItem[] result = manager.getComboDepartamentos();
        List<SelectItem> provinces = manager.getComboProvincias();
        List<SelectItem> districts = manager.getComboDistrito();

        assertEquals(26, result.length);
        assertTrue(provinces.isEmpty());
        assertTrue(districts.isEmpty());

        // ------------------------------------------------------------------
        manager.setDepartamentos("060000"); // CAJAMARCA
        manager.cambiaDepartamentoNac(actionEvent);
        provinces = manager.getComboProvincias();
        districts = manager.getComboDistrito();

        assertEquals(13, provinces.size());
        assertTrue(districts.isEmpty());

        // ------------------------------------------------------------------
        manager.setProvincias("060400"); // CHOTA
        manager.cambiaProvinciaNac(actionEvent);
        provinces = manager.getComboProvincias();
        districts = manager.getComboDistrito();

        assertEquals(13, provinces.size());
        assertEquals(19, districts.size());

        // ------------------------------------------------------------------
        manager.setProvincias(CAT_DEFAULT);
        manager.cambiaProvinciaNac(actionEvent);
        provinces = manager.getComboProvincias();
        districts = manager.getComboDistrito();

        assertEquals(13, provinces.size());
        assertTrue(districts.isEmpty());

        // ------------------------------------------------------------------
        manager.setDepartamentos(CAT_DEFAULT);
        manager.cambiaDepartamentoNac(actionEvent);
        provinces = manager.getComboProvincias();
        districts = manager.getComboDistrito();

        assertTrue(provinces.isEmpty());
        assertTrue(districts.isEmpty());
    }

    @Test
    public void getComboDistritoLimaTest() {
        List<SelectItem> result = manager.getComboDistritoLima();

        assertEquals(44, result.size());
    }


    @Test
    public void mostrarAsistenciaTest() {
        UIParameter uiParameterP_id = mock(UIParameter.class);
        UIParameter uiParameterP_codigo = mock(UIParameter.class);
        UIParameter uiParameterP_paterno = mock(UIParameter.class);
        UIParameter uiParameterP_materno = mock(UIParameter.class);
        UIParameter uiParameterP_nombre = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameterP_id);
        when(uiParameterP_id.getValue()).thenReturn("2923");
        when(uiComponent.findComponent(PARAM_CODIGO)).thenReturn(uiParameterP_codigo);
        when(uiParameterP_codigo.getValue()).thenReturn("08205040");
        when(uiComponent.findComponent("p_paterno")).thenReturn(uiParameterP_paterno);
        when(uiParameterP_paterno.getValue()).thenReturn("");
        when(uiComponent.findComponent("p_materno")).thenReturn(uiParameterP_materno);
        when(uiParameterP_materno.getValue()).thenReturn("");
        when(uiComponent.findComponent("p_nombre")).thenReturn(uiParameterP_nombre);
        when(uiParameterP_nombre.getValue()).thenReturn("");

        manager.mostrarAsistencia(actionEvent);

        LocalDate currentDate = LocalDate.now();

        assertEquals(currentDate.getMonth(), manager.getMes());
        assertEquals(currentDate.getYear(), manager.getAnio());
        assertEquals(6, manager.getListaAsistencia().size());
    }

    @Test
    public void evaluaAsistenciaFromTbAsistenciaTest() {
        manager.setModalCodigoAlumno("09204196");
        manager.setMes(Month.SEPTEMBER);
        manager.setAnio(2009);

        manager.evaluaAsistencia();

        assertNotNull(manager.getListaAsistencia());
        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.SEPTEMBER, manager.getMes());
        assertEquals(2009, manager.getAnio());

        // Adding one year
        manager.aumentaAnio(actionEvent);
        manager.evaluaAsistencia();

        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.SEPTEMBER, manager.getMes());
        assertEquals(2010, manager.getAnio());

        // Adding one month
        manager.aumentaMes(actionEvent);
        manager.evaluaAsistencia();

        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.OCTOBER, manager.getMes());
        assertEquals(2010, manager.getAnio());

        // Adding one month
        manager.aumentaMes(actionEvent);
        manager.evaluaAsistencia();

        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.NOVEMBER, manager.getMes());
        assertEquals(2010, manager.getAnio());

        // Adding one month
        manager.aumentaMes(actionEvent);
        manager.evaluaAsistencia();

        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.DECEMBER, manager.getMes());
        assertEquals(2010, manager.getAnio());

        // Adding one month
        manager.aumentaMes(actionEvent);
        manager.evaluaAsistencia();

        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.JANUARY, manager.getMes());
        assertEquals(2011, manager.getAnio());

        // Subtract one month
        manager.disminuyeMes(actionEvent);
        manager.evaluaAsistencia();

        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.DECEMBER, manager.getMes());
        assertEquals(2010, manager.getAnio());

        // Subtract one year
        manager.disminuyeAnio(actionEvent);
        manager.evaluaAsistencia();

        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.DECEMBER, manager.getMes());
        assertEquals(2009, manager.getAnio());
    }

    @Test
    public void evaluaAsistenciaFromAcSesionEfectivaAsistencia() {
        manager.setModalIdAlumno(4284);
        manager.setModalCodigoAlumno("10104026");
        manager.setMes(Month.SEPTEMBER);
        manager.setAnio(2010);

        manager.evaluaAsistencia();

        assertFalse(manager.getLabelMonth().isEmpty());
        assertFalse(manager.getLabelYear().isEmpty());
        assertNotNull(manager.getListaAsistencia());
        assertFalse(manager.getListaAsistencia().isEmpty());
        assertEquals(Month.SEPTEMBER, manager.getMes());
        assertEquals(2010, manager.getAnio());

        MbAlumno row = manager.getListaAsistencia().get(0);
        assertNull(row.getNroLunes());
        assertNull(row.getLabelLunes());
        assertNull(row.getStyleLunes());

        assertNull(row.getNroMartes());
        assertNull(row.getLabelMartes());
        assertNull(row.getStyleMartes());

        assertFalse(row.getNroMiercoles().isEmpty());
        assertFalse(row.getLabelMiercoles().isEmpty());
        assertFalse(row.getStyleMiercoles().isEmpty());

        assertFalse(row.getNroJueves().isEmpty());
        assertFalse(row.getLabelJueves().isEmpty());
        assertFalse(row.getStyleJueves().isEmpty());

        assertFalse(row.getNroViernes().isEmpty());
        assertFalse(row.getLabelViernes().isEmpty());
        assertFalse(row.getStyleViernes().isEmpty());

        assertFalse(row.getNroSabado().isEmpty());
        assertNull(row.getLabelSabado());
        assertNull(row.getStyleSabado());

        assertFalse(row.getNroDomingo().isEmpty());
        assertNull(row.getLabelDomingo());
        assertFalse(row.getStyleDomingo().isEmpty());
    }

    @Test
    public void editarFila2AdicionarDatosTest() {
        MbAlumno alu = new MbAlumno();
        alu.setAlumnoId(2080);
        alu.setParamPaterno("AGUILA");
        alu.setParamMaterno("OSORIO");
        alu.setParamNombre("JOHAN YAID");

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.getAttributes()).thenReturn(Collections.singletonMap("p_alu", alu));

        manager.editarFila2(actionEvent);

        assertEquals(alu.getAlumnoId(), manager.getModalIdAlumno());
        assertFalse(manager.getModalPaternoAlumno().isEmpty());
        assertFalse(manager.getModalMaternoAlumno().isEmpty());
        assertNull(manager.getPreparacion());
        assertFalse(manager.getDocumentos().isEmpty());

        // ------------------------------------------------------------------
        manager.setAcademia("028003");
        manager.cambiaAcabemia(actionEvent);

        assertFalse(manager.isDisableAcabemiaOtros());
        assertTrue(manager.getStyleAcabemiaOtros().isEmpty());
    }
}
