package net.uch.academica.jsf;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.academica.dao.HSDocenteDAO;
import net.uch.academica.jsf.beans.BeanDocente;
import net.uch.common.audit.CurrentUserContext;
import net.uch.mapping.AcDocente;
import net.uch.mapping.UserNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.faces.model.SelectItem;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static net.uch.common.Constants.CAT_DEFAULT;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for sistema_academico on 23/05/2020.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MbDocenteTest extends SpringConfigurationTest {

    private MbDocente manager;

    @Autowired
    @Qualifier("springHibernateDaoDocente")
    private HSDocenteDAO docenteDAO;

    @Before
    public void setUp() {
        manager = new MbDocente();
        manager.init();

        // Simulate what AuditUserFilter does on every request in production: expose the
        // logged-in user's id to AuditInterceptor via CurrentUserContext, so created_by /
        // updated_by get stamped. The base class stubs the session to return loggerUser.
        loggerUser.setRowUsuId(Stubs.USUARIO_ADMIN_ID);
        CurrentUserContext.set(loggerUser.getRowUsuId());
    }

    @Test(expected = UserNotFoundException.class)
    public void loadBeanWithoutSessionTest() {
        when(session.getAttribute("usuario")).thenReturn(null);
        manager.init();
    }

    @Test
    public void loadModalNewTeacherCboNacimientoTest() {
        List<SelectItem> cboSituacion = manager.getCboSituacion();
        List<SelectItem> rdoSexo = manager.getRdoSexo();

        assertEquals(2, cboSituacion.size());
        assertEquals(2, rdoSexo.size());

        // Cbo birthday
        List<SelectItem> cboDepartamentosN = manager.getCboDepartamentosN();
        List<SelectItem> cboProvinciaN = manager.getCboProvinciaN();
        List<SelectItem> cboDistritoN = manager.getCboDistritoN();

        assertEquals(26, cboDepartamentosN.size());
        assertTrue(cboProvinciaN.isEmpty());
        assertTrue(cboDistritoN.isEmpty());

        // On change province
        manager.setwDepaNaci("060000"); // CAJAMARCA
        manager.loadProvincesN(actionEvent);

        cboDepartamentosN = manager.getCboDepartamentosN();
        cboProvinciaN = manager.getCboProvinciaN();
        cboDistritoN = manager.getCboDistritoN();

        assertEquals(26, cboDepartamentosN.size());
        assertEquals(13, cboProvinciaN.size());
        assertTrue(cboDistritoN.isEmpty());

        // On change district
        manager.setwProvNaci("060400"); // CHOTA
        manager.loadDistrictsN(actionEvent);

        cboDepartamentosN = manager.getCboDepartamentosN();
        cboProvinciaN = manager.getCboProvinciaN();
        cboDistritoN = manager.getCboDistritoN();

        assertEquals(26, cboDepartamentosN.size());
        assertEquals(13, cboProvinciaN.size());
        assertEquals(19, cboDistritoN.size());

        // On change province
        manager.setwProvNaci(CAT_DEFAULT); // CHOTA
        manager.loadDistrictsN(actionEvent);

        cboDepartamentosN = manager.getCboDepartamentosN();
        cboProvinciaN = manager.getCboProvinciaN();
        cboDistritoN = manager.getCboDistritoN();

        assertEquals(26, cboDepartamentosN.size());
        assertEquals(13, cboProvinciaN.size());
        assertTrue(cboDistritoN.isEmpty());

        // On change
        manager.setwDepaNaci(CAT_DEFAULT);
        manager.loadProvincesN(actionEvent);

        cboDepartamentosN = manager.getCboDepartamentosN();
        cboProvinciaN = manager.getCboProvinciaN();
        cboDistritoN = manager.getCboDistritoN();

        assertEquals(26, cboDepartamentosN.size());
        assertTrue(cboProvinciaN.isEmpty());
        assertTrue(cboDistritoN.isEmpty());

        // ------------------------------------------------------------------
    }

    @Test
    public void loadModalNewTeacherCboResidenceTest() {
        // Cbo residence
        List<SelectItem> cboDepartamentosR = manager.getCboDepartamentosR();
        List<SelectItem> cboProvinciaR = manager.getCboProvinciaR();
        List<SelectItem> cboDistritoR = manager.getCboDistritoR();

        assertEquals(26, cboDepartamentosR.size());
        assertTrue(cboProvinciaR.isEmpty());
        assertTrue(cboDistritoR.isEmpty());

        // On change province
        manager.setwDepaResi("150000"); // LIMA
        manager.loadProvincesR(actionEvent);

        cboDepartamentosR = manager.getCboDepartamentosR();
        cboProvinciaR = manager.getCboProvinciaR();
        cboDistritoR = manager.getCboDistritoR();

        assertEquals(26, cboDepartamentosR.size());
        assertEquals(10, cboProvinciaR.size());
        assertTrue(cboDistritoR.isEmpty());

        // On change district
        manager.setwProvResi("150100"); // LIMA
        manager.loadDistrictsR(actionEvent);

        cboDepartamentosR = manager.getCboDepartamentosR();
        cboProvinciaR = manager.getCboProvinciaR();
        cboDistritoR = manager.getCboDistritoR();

        assertEquals(26, cboDepartamentosR.size());
        assertEquals(10, cboProvinciaR.size());
        assertEquals(43, cboDistritoR.size());

        // On change province
        manager.setwProvResi(CAT_DEFAULT); // LIMA
        manager.loadDistrictsR(actionEvent);

        cboDepartamentosR = manager.getCboDepartamentosR();
        cboProvinciaR = manager.getCboProvinciaR();
        cboDistritoR = manager.getCboDistritoR();

        assertEquals(26, cboDepartamentosR.size());
        assertEquals(10, cboProvinciaR.size());
        assertTrue(cboDistritoR.isEmpty());

        // On change
        manager.setwDepaResi(CAT_DEFAULT); // LIMA
        manager.loadProvincesR(actionEvent);

        cboDepartamentosR = manager.getCboDepartamentosR();
        cboProvinciaR = manager.getCboProvinciaR();
        cboDistritoR = manager.getCboDistritoR();

        assertEquals(26, cboDepartamentosR.size());
        assertTrue(cboProvinciaR.isEmpty());
        assertTrue(cboDistritoR.isEmpty());
    }

    @Test
    public void buscarDocenteTest() {
        manager.setDocDniW("");
        manager.setDocNombreCompletoW("");

        manager.buscarDocente(actionEvent);
        assertFalse(manager.getListaDocente().isEmpty());

        // ------------------------------------------------------------------
        manager.setDocNombreCompletoW("fel");

        manager.buscarDocente(actionEvent);
        assertEquals(6, manager.getListaDocente().size());

        BeanDocente bD = manager.getListaDocente().get(1);
        assertFalse(bD.getDocNacimientoDesc().isEmpty());
        assertFalse(bD.getDocResidenciaDesc().isEmpty());
        assertFalse(bD.getDocSituacionDesc().isEmpty());
        assertFalse(bD.getDocSexoDesc().isEmpty());

        // ------------------------------------------------------------------
        manager.setDocDniW("100");
        manager.setDocNombreCompletoW("");

        manager.buscarDocente(actionEvent);
        assertEquals(3, manager.getListaDocente().size());
    }

    @Test
    public void openModalForNewTeacherTest() {
        manager.limpiarData(actionEvent);

        assertTrue(manager.getOncomplete().isEmpty());
        assertTrue(manager.getMensaje().isEmpty());

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(manager.getDocente().getId());

        manager.getDocente().setDocDni("41003490");
        manager.getDocente().setDocAppaterno("");
        manager.getDocente().setDocApmaterno("");
        manager.getDocente().setDocNombres("");
        manager.getDocente().setDocCorreo("");
        manager.getDocente().setDocTelefono("");
        manager.getDocente().setDocNacimiento(CAT_DEFAULT);
        manager.getDocente().setDocResidencia(CAT_DEFAULT);
        manager.getDocente().setDocSituacion("002001");

        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("El numero de DNI ya existe"));

        manager.getDocente().setDocDni(RandomStringUtils.randomNumeric(8));
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Apellido Paterno"));

        manager.getDocente().setDocAppaterno("FAKE_FATHERLM");
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Apellido Materno"));

        manager.getDocente().setDocApmaterno("FAKE_MOTHERLM");
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Nombres"));

        manager.getDocente().setDocNombres("FAKE_TEACHER_NAME");
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Correo Electronico"));

        manager.getDocente().setDocCorreo("joe.fake@domain.com");
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese nro telefono o celular"));

        manager.getDocente().setDocTelefono(RandomStringUtils.randomNumeric(9));
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Registro agregado con exito"));

        // On insert AuditInterceptor.onSave fills only created_*; updated_* stay null
        // until the row is first modified.
        AcDocente created = docenteDAO.seleccionarDocente(manager.getDocente().getId());
        assertNotNull("created_at must be stamped on insert", created.getCreatedAt());
        assertNotNull("created_by must be stamped on insert", created.getCreatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, created.getCreatedBy().intValue());
        assertNull("updated_at must stay null until first update", created.getUpdatedAt());
        assertNull("updated_by must stay null until first update", created.getUpdatedBy());

        // ------------------------------------------------------------------
        editarTeacher(manager.getDocente().getId());

        // ------------------------------------------------------------------
        deleteTeacher(81); // Teacher with schedule assigned
        assertTrue(manager.getMensaje().contains("El Docente tiene un horario asignado, no se puede eliminar"));

        deleteTeacher(manager.getDocente().getId());
        assertTrue(manager.getMensaje().isEmpty());

        // ------------------------------------------------------------------
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id2")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(95); // Teacher with null fields

        manager.editarDocente(actionEvent);
        assertTrue(manager.getOncomplete().contains("mpDocente"));
    }

    private void editarTeacher(Integer teacherId) {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id2")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(teacherId);

        manager.editarDocente(actionEvent);
        assertTrue(manager.getOncomplete().contains("mpDocente"));
        assertTrue(manager.getMensaje().isEmpty());
        assertEquals(teacherId, manager.getDocente().getId());

        manager.getDocente().setDocCorreo("");
        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Ingrese Correo Electronico"));

        manager.getDocente().setDocDni(RandomStringUtils.randomNumeric(8));
        manager.getDocente().setDocNombres("FAKE_TEACHER_EDIT_NAME");
        manager.getDocente().setDocPeriodoInicio(Date.valueOf(LocalDate.now()));
        manager.getDocente().setDocSexo("004002");
        manager.getDocente().setDocNacimiento("210203"); // ARAPA
        manager.getDocente().setDocResidencia("250202"); // SEPAHUA
        manager.getDocente().setDocSituacion("002002");
        manager.getDocente().setDocCorreo("joe.fake2@domain.com");

        manager.guardarDatos(actionEvent);
        assertTrue(manager.getOncomplete().contains("Registro modificado con exito"));

        // created_* must survive the update (load-then-merge in actualizarDocente),
        // while updated_* are now stamped for the first time by AuditInterceptor.onFlushDirty.
        AcDocente edited = docenteDAO.seleccionarDocente(teacherId);
        assertNotNull("created_at must be preserved on update", edited.getCreatedAt());
        assertNotNull("created_by must be preserved on update", edited.getCreatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, edited.getCreatedBy().intValue());
        assertNotNull("updated_at must be stamped on first update", edited.getUpdatedAt());
        assertNotNull("updated_by must be stamped on first update", edited.getUpdatedBy());
        assertEquals(Stubs.USUARIO_ADMIN_ID, edited.getUpdatedBy().intValue());

        // ------------------------------------------------------------------
        manager.editarDocente(actionEvent);
        assertTrue(manager.getOncomplete().contains("mpDocente"));
        assertEquals(teacherId, manager.getDocente().getId());
    }

    private void deleteTeacher(Integer teacherId) {
        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("p_id2")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(teacherId);

        manager.eliminarDocente(actionEvent);
    }
}
