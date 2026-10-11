package net.uch.cursolibre.jsf;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSUbigeoDAO;
import net.uch.cursolibre.dao.HSAlumnoCLDAO;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.uch.common.Constants.CAT_DEFAULT;
import static net.uch.common.Constants.PARAM_ALUMNO_ID;

/**
 * @author richard, cesardl
 */
public class MbAlumnoCL {

    private static final Logger LOG = LoggerFactory.getLogger(MbAlumnoCL.class);

    private final HSAlumnoDAO daoAlumno = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
    private final HSAlumnoCLDAO daoAlumnoCL = ServiceFinder.findBean("springHibernateDaoCLAlumno", HSAlumnoCLDAO.class);
    private final HSUbigeoDAO daoUbigeo = ServiceFinder.findBean("springHibernateDaoUbigeo", HSUbigeoDAO.class);
    private final HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);

    private String paramCodigo;
    private String paramPaterno;
    private String paramMaterno;
    private String paramNombre;

    private String aluCodigo;
    private String aluAppaterno;
    private String aluApmaterno;
    private String aluNombres;
    private int aluId;
    private String aluDni;
    private String aluMail;
    private String aluTipo;
    private Blob aluFoto;
    private String aluDuplicado;
    private String aluProcedencia;
    private String aluTemporal;
    private String aluSexo;
    private String aluDireccion;
    private String aluTelefono;
    private String aluCelular;
    private Date aluFechaNacimiento = new Date();
    private String aluUnidad;
    private String aluFormaPago;
    private String aluNombreFamiliar;
    private String aluParentesco;

    private String provincia = "0000";
    private String departamento = "00";
    private String distrito;
    private SelectItem[] cboDepartamento;
    private List<SelectItem> cboProvincia;
    private List<SelectItem> cboDistrito;
    //*******************************
    private SelectItem[] cbosexo;
    private SelectItem[] cboUnidad;
    private SelectItem[] cboFormaPago;
    private SelectItem[] cboParentesco;
    private SelectItem[] cboProcedencia;
    private SelectItem[] cboTipo;
    private String oncomplete;
    private List<MbAlumnoCL> listaAlumno;
    //*******************************
    private String univCodigo;
    private AcAlumno univAlumno;
    private int findImage;
    //*******************************
    private List<MbAlumnoCL> listaEnlace;
    private ClAlumno enlaceAlumno;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        this.setCboDepartamento(daoUbigeo.seleccionarDepartamento().stream()
                .map(d -> new SelectItem(d.getId(), d.getDisDes()))
                .toArray(SelectItem[]::new));
        this.setCboProvincia(new ArrayList<>());
        this.setCboDistrito(new ArrayList<>());
        /* fin departamento*/

        /*unidad*/
        List<TbCatalogo> listauni = daoCatalogo.seleccionarCatalogo("042");
        cboUnidad = new SelectItem[listauni.size()];
        for (var i = 0; i < cboUnidad.length; i++) {
            cboUnidad[i] = new SelectItem(listauni.get(i).getCatCodigoGrupo() + listauni.get(i).getCatCodigoElemento(), listauni.get(i).getCatDescripcionElemento());
        }
        /* fin unidad*/

        /*FORMA DE PAGO*/
        List<TbCatalogo> listafp = daoCatalogo.seleccionarCatalogo("041");
        cboFormaPago = new SelectItem[listafp.size()];
        for (var i = 0; i < cboFormaPago.length; i++) {
            cboFormaPago[i] = new SelectItem(listafp.get(i).getCatCodigoGrupo() + listafp.get(i).getCatCodigoElemento(), listafp.get(i).getCatDescripcionElemento());
        }

        /*tipo FAMILIAR*/
        List<TbCatalogo> listatf = daoCatalogo.seleccionarCatalogo("040");
        cboParentesco = new SelectItem[listatf.size()];
        for (var i = 0; i < cboParentesco.length; i++) {
            cboParentesco[i] = new SelectItem(listatf.get(i).getCatCodigoGrupo() + listatf.get(i).getCatCodigoElemento(), listatf.get(i).getCatDescripcionElemento());
        }

        /*PROCEDENCIA*/
        List<TbCatalogo> listaPro = daoCatalogo.seleccionarCatalogo("023");
        cboProcedencia = new SelectItem[listaPro.size()];
        for (var i = 0; i < cboProcedencia.length; i++) {
            cboProcedencia[i] = new SelectItem(listaPro.get(i).getCatCodigoGrupo() + listaPro.get(i).getCatCodigoElemento(), listaPro.get(i).getCatDescripcionElemento());
        }

        /*TIPO*/
        List<TbCatalogo> listaTipo = daoCatalogo.seleccionarCatalogo("057");
        cboTipo = new SelectItem[listaTipo.size()];
        for (var i = 0; i < cboTipo.length; i++) {
            cboTipo[i] = new SelectItem(listaTipo.get(i).getCatCodigoGrupo() + listaTipo.get(i).getCatCodigoElemento(), listaTipo.get(i).getCatDescripcionElemento());
        }

        List<TbCatalogo> lista = daoCatalogo.seleccionarCatalogo("004");
        cbosexo = new SelectItem[lista.size()];
        for (var i = 0; i < cbosexo.length; i++) {
            cbosexo[i] = new SelectItem(lista.get(i).getCatCodigoGrupo() + lista.get(i).getCatCodigoElemento(), lista.get(i).getCatDescripcionElemento());
        }

        this.setFindImage(0);
        this.setEnlaceAlumno(null);
    }

    public String getParamCodigo() {
        return paramCodigo;
    }

    public void setParamCodigo(String paramCodigo) {
        this.paramCodigo = paramCodigo;
    }

    public String getParamPaterno() {
        return paramPaterno;
    }

    public void setParamPaterno(String paramPaterno) {
        this.paramPaterno = paramPaterno;
    }

    public String getParamMaterno() {
        return paramMaterno;
    }

    public void setParamMaterno(String paramMaterno) {
        this.paramMaterno = paramMaterno;
    }

    public String getParamNombre() {
        return paramNombre;
    }

    public void setParamNombre(String paramNombre) {
        this.paramNombre = paramNombre;
    }

    public List<MbAlumnoCL> getListaAlumno() {
        return listaAlumno;
    }

    public void setListaAlumno(List<MbAlumnoCL> listaAlumno) {
        this.listaAlumno = listaAlumno;
    }

    public int getAluId() {
        return aluId;
    }

    public void setAluId(int aluId) {
        this.aluId = aluId;
    }

    public String getAluDni() {
        return aluDni;
    }

    public void setAluDni(String aluDni) {
        this.aluDni = aluDni;
    }

    public String getAluMail() {
        return aluMail;
    }

    public void setAluMail(String aluMail) {
        this.aluMail = aluMail;
    }

    public String getAluTipo() {
        return aluTipo;
    }

    public void setAluTipo(String aluTipo) {
        this.aluTipo = aluTipo;
    }

    public Blob getAluFoto() {
        return aluFoto;
    }

    public void setAluFoto(Blob aluFoto) {
        this.aluFoto = aluFoto;
    }

    public String getAluDuplicado() {
        return aluDuplicado;
    }

    public void setAluDuplicado(String aluDuplicado) {
        this.aluDuplicado = aluDuplicado;
    }

    public String getAluProcedencia() {
        return aluProcedencia;
    }

    public void setAluProcedencia(String aluProcedencia) {
        this.aluProcedencia = aluProcedencia;
    }

    public String getAluTemporal() {
        return aluTemporal;
    }

    public void setAluTemporal(String aluTemporal) {
        this.aluTemporal = aluTemporal;
    }

    public String getAluSexo() {
        return aluSexo;
    }

    public void setAluSexo(String aluSexo) {
        this.aluSexo = aluSexo;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getAluDireccion() {
        return aluDireccion;
    }

    public void setAluDireccion(String aluDireccion) {
        this.aluDireccion = aluDireccion;
    }

    public String getAluTelefono() {
        return aluTelefono;
    }

    public void setAluTelefono(String aluTelefono) {
        this.aluTelefono = aluTelefono;
    }

    public String getAluCelular() {
        return aluCelular;
    }

    public void setAluCelular(String aluCelular) {
        this.aluCelular = aluCelular;
    }

    public Date getAluFechaNacimiento() {
        return aluFechaNacimiento;
    }

    public void setAluFechaNacimiento(Date aluFechaNacimiento) {
        this.aluFechaNacimiento = aluFechaNacimiento;
    }

    public String getAluUnidad() {
        return aluUnidad;
    }

    public void setAluUnidad(String aluUnidad) {
        this.aluUnidad = aluUnidad;
    }

    public String getAluFormaPago() {
        return aluFormaPago;
    }

    public void setAluFormaPago(String aluFormaPago) {
        this.aluFormaPago = aluFormaPago;
    }

    public String getAluNombreFamiliar() {
        return aluNombreFamiliar;
    }

    public void setAluNombreFamiliar(String aluNombreFamiliar) {
        this.aluNombreFamiliar = aluNombreFamiliar;
    }

    public String getAluParentesco() {
        return aluParentesco;
    }

    public void setAluParentesco(String aluParentesco) {
        this.aluParentesco = aluParentesco;
    }

    public String getAluCodigo() {
        return aluCodigo;
    }

    public void setAluCodigo(String aluCodigo) {
        this.aluCodigo = aluCodigo;
    }

    public String getAluAppaterno() {
        return aluAppaterno;
    }

    public void setAluAppaterno(String aluAppaterno) {
        this.aluAppaterno = aluAppaterno;
    }

    public String getAluApmaterno() {
        return aluApmaterno;
    }

    public void setAluApmaterno(String aluApmaterno) {
        this.aluApmaterno = aluApmaterno;
    }

    public String getAluNombres() {
        return aluNombres;
    }

    public void setAluNombres(String aluNombres) {
        this.aluNombres = aluNombres;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public SelectItem[] getCboDepartamento() {
        return cboDepartamento;
    }

    public void setCboDepartamento(SelectItem[] cboDepartamento) {
        this.cboDepartamento = cboDepartamento;
    }

    public void onChangeDepartamento(ActionEvent event) {
        LOG.debug("[{}] On change Dep {}", event.getPhaseId(), this.departamento);
        cboProvincia.clear();
        cboDistrito.clear();
        if (!"0000".equals(this.departamento)) {
            List<TbDistrito> lista = daoUbigeo.seleccionarProvincia(this.departamento);
            cboProvincia.addAll(lista.stream()
                    .map(p -> new SelectItem(p.getId(), p.getDisDes()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getCboProvincia() {
        return cboProvincia;
    }

    public void setCboProvincia(List<SelectItem> cboProvincia) {
        this.cboProvincia = cboProvincia;
    }

    public void onChangeProvincia(ActionEvent event) {
        LOG.debug("[{}] On change Dep {}, Prov {}", event.getPhaseId(), this.departamento, this.provincia);
        cboDistrito.clear();
        if (!CAT_DEFAULT.equals(this.provincia)) {
            List<TbDistrito> lista = daoUbigeo.seleccionarDistrito(this.getDepartamento(), this.getProvincia());
            cboDistrito.addAll(lista.stream()
                    .map(d -> new SelectItem(d.getId(), d.getDisDes()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getCboDistrito() {
        return cboDistrito;
    }

    public void setCboDistrito(List<SelectItem> cboDistrito) {
        this.cboDistrito = cboDistrito;
    }

    public SelectItem[] getCbosexo() {
        return cbosexo;
    }

    public void setCbosexo(SelectItem[] cbosexo) {
        this.cbosexo = cbosexo;
    }

    public SelectItem[] getCboUnidad() {
        return cboUnidad;
    }

    public void setCboUnidad(SelectItem[] cboUnidad) {
        this.cboUnidad = cboUnidad;
    }

    public SelectItem[] getCboFormaPago() {
        return cboFormaPago;
    }

    public void setCboFormaPago(SelectItem[] cboFormaPago) {
        this.cboFormaPago = cboFormaPago;
    }

    public SelectItem[] getCboParentesco() {
        return cboParentesco;
    }

    public void setCboParentesco(SelectItem[] cboParentesco) {
        this.cboParentesco = cboParentesco;
    }

    public SelectItem[] getCboProcedencia() {
        return cboProcedencia;
    }

    public void setCboProcedencia(SelectItem[] cboProcedencia) {
        this.cboProcedencia = cboProcedencia;
    }

    public SelectItem[] getCboTipo() {
        return cboTipo;
    }

    public void setCboTipo(SelectItem[] cboTipo) {
        this.cboTipo = cboTipo;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for students '{}' | '{}' | '{}' | '{}'", event.getPhaseId(),
                this.getParamCodigo(), this.getParamPaterno(), this.getParamMaterno(), this.getParamNombre());
        this.setOncomplete("");
        if (this.paramPaterno.trim().length() == 0
                && this.paramMaterno.trim().length() == 0
                && this.paramNombre.trim().length() == 0
                && this.paramCodigo.trim().length() == 0) {
            this.setOncomplete("alert('Ingrese algun parametro para buscar');");
        } else {
            setListaAlumno(new ArrayList<>());
            List<ClAlumno> lista = daoAlumnoCL.seleccionarAlumnosLibres(this.getParamCodigo(), this.getParamPaterno(), this.getParamMaterno(), this.getParamNombre());
            for (ClAlumno alumno : lista) {
                var ba = new MbAlumnoCL();

                ba.setAluCelular(alumno.getAluCelular());
                ba.setAluDireccion(alumno.getAluDireccion());
                ba.setDistrito(alumno.getAluDistrito());
                ba.setAluDni(alumno.getAluDni());
                ba.setAluDuplicado(alumno.getAluDuplicado());
                ba.setAluFechaNacimiento(alumno.getAluFechaNacimiento());
                ba.setAluFormaPago(alumno.getAluFormaPago());
                ba.setAluFoto(alumno.getAluFoto());
                ba.setAluId(alumno.getAluId());
                ba.setAluMail(alumno.getAluMail());
                ba.setAluNombreFamiliar(alumno.getAluNombreFamiliar());
                ba.setAluParentesco(alumno.getAluParentesco());
                ba.setAluProcedencia(alumno.getAluProcedencia());
                ba.setAluSexo(alumno.getAluSexo());
                ba.setAluTelefono(alumno.getAluTelefono());
                ba.setAluTemporal(alumno.getAluTemporal());
                ba.setAluTipo(alumno.getAluTipo());
                ba.setAluUnidad(alumno.getAluUnidad());
                ba.setAluApmaterno(alumno.getAluApmaterno());
                ba.setAluAppaterno(alumno.getAluAppaterno());
                ba.setAluNombres(alumno.getAluNombres());
                ba.setAluCodigo(alumno.getAluCodigo());
                ba.setParamMaterno(alumno.getAluApmaterno());
                ba.setParamPaterno(alumno.getAluAppaterno());
                ba.setParamNombre(alumno.getAluNombres());
                ba.setParamCodigo(alumno.getAluCodigo());

                if (StringUtils.hasText(alumno.getAluFormaPago())) {
                    ba.setAluFormaPago(daoCatalogo.seleccionarDescripcion(alumno.getAluFormaPago()));
                }
                if (StringUtils.hasText(alumno.getAluUnidad())) {
                    ba.setAluUnidad(daoCatalogo.seleccionarDescripcion(alumno.getAluUnidad()));
                }
                if (StringUtils.hasText(alumno.getAluProcedencia())) {
                    ba.setAluProcedencia(daoCatalogo.seleccionarDescripcion(alumno.getAluProcedencia()));
                }
                if (StringUtils.hasText(alumno.getAluParentesco())) {
                    ba.setAluParentesco(daoCatalogo.seleccionarDescripcion(alumno.getAluParentesco()));
                }

                listaAlumno.add(ba);
            }
            LOG.info("Total of students {}", listaAlumno.size());
        }
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}] Opening modal", event.getPhaseId());
        limpiar();
        this.setFindImage(0);
        this.setAluCodigo(daoAlumnoCL.maximoCodigo());
        LOG.trace("entro al nuevo {}", this.getParamCodigo());
        LOG.trace("el codigo -> {}", this.getAluCodigo());
    }

    public void limpiar() {
        aluCodigo = "";
        aluAppaterno = "";
        aluApmaterno = "";
        aluNombres = "";
        aluId = 0;
        aluDni = "";
        aluMail = "";
        aluTipo = "";
        aluFoto = null;
        aluDuplicado = "";
        aluProcedencia = "023009";
        aluTemporal = "";
        aluSexo = "";
        distrito = "";
        aluDireccion = "";
        aluTelefono = "";
        aluCelular = "";
        aluFechaNacimiento = new Date();
        aluUnidad = "042006";
        aluFormaPago = "";
        aluNombreFamiliar = "";
        aluParentesco = "040001";
    }

    public void guardarDatos(ActionEvent event) {
        this.setOncomplete("");
        LOG.debug("[{}] Saving data for student {}", event.getPhaseId(), this.aluCodigo);
        if (this.getAluAppaterno().trim().isEmpty()
                || this.getAluApmaterno().trim().isEmpty()
                || this.getAluNombres().trim().isEmpty()
                || this.getAluMail().trim().isEmpty()
                || this.getAluTelefono().trim().isEmpty()
                || this.getAluCelular().trim().isEmpty()
                || this.getAluDireccion().trim().isEmpty()) {
            this.setOncomplete("alert('Debe ingresar los campos obligatorios.')");
        } else {
            var alu = new ClAlumno();

            alu.setAluActivo("1");
            alu.setAluApmaterno(this.getAluApmaterno().trim().toUpperCase());
            alu.setAluAppaterno(this.getAluAppaterno().trim().toUpperCase());
            alu.setAluCelular(this.getAluCelular());
            alu.setAluCodigo(this.getAluCodigo());
            alu.setAluDireccion(this.getAluDireccion().trim().toUpperCase());
            alu.setAluDistrito(this.getDistrito());
            alu.setAluDni(this.getAluDni());
            alu.setAluDuplicado(this.getAluDuplicado());
            alu.setAluFechaNacimiento(this.getAluFechaNacimiento());
            alu.setAluFormaPago(this.getAluFormaPago());
            alu.setAluFoto(this.getAluFoto());
            alu.setAluMail(this.getAluMail().trim().toUpperCase());
            alu.setAluNombreFamiliar(this.getAluNombreFamiliar().trim().toUpperCase());
            alu.setAluNombres(this.getAluNombres().trim().toUpperCase());
            alu.setAluParentesco(this.getAluParentesco());
            alu.setAluProcedencia(this.getAluProcedencia());
            alu.setAluSexo(this.getAluSexo());
            alu.setAluTelefono(this.getAluTelefono());
            alu.setAluTemporal(this.getAluTemporal());
            alu.setAluTipo(this.getAluTipo());
            alu.setAluUnidad(this.getAluUnidad());
            alu.setAluId(this.getAluId());

            if (this.getAluId() == 0) {
                alu.setAluPassword(this.getAluCodigo());
                daoAlumnoCL.insertarAlumnoCl(alu);
                LOG.info("Student saved {}", alu.getAluId());
                this.setOncomplete("alert('Se creo una Alumno correctamente.');Richfaces.hideModalPanel('mpUpdate')");
            } else {
                daoAlumnoCL.modificarAlumnoCl(alu);
                LOG.info("Student updated {}", alu.getAluId());
                this.setOncomplete("alert('Se modifico Alumno correctamente.');Richfaces.hideModalPanel('mpUpdate')");
            }
            limpiar();
            aluId = alu.getAluId();
        }
    }

    public void editarAlumno(ActionEvent event) {
        var vAluId = Integer.parseInt(event.getComponent().getAttributes().get(PARAM_ALUMNO_ID).toString());
        LOG.debug("[{}] To edit {}", event.getPhaseId(), vAluId);
        limpiar();
        this.setFindImage(2);
        ClAlumno alu = daoAlumnoCL.buscarAlumnoPorAluId(vAluId);
        this.setAluApmaterno(alu.getAluApmaterno());
        this.setAluAppaterno(alu.getAluAppaterno());
        this.setAluCelular(alu.getAluCelular());
        if (alu.getAluCodigo() == null || alu.getAluCodigo().trim().length() == 0) {
            String code = daoAlumnoCL.maximoCodigo();
            this.setAluCodigo(code);
            LOG.warn("Student without code, generating new one '{}'", code);
        } else {
            this.setAluCodigo(alu.getAluCodigo());
        }
        this.setAluDireccion(alu.getAluDireccion());
        this.setAluDni(alu.getAluDni());
        this.setAluDuplicado(alu.getAluDuplicado());
        this.setAluFechaNacimiento(alu.getAluFechaNacimiento());
        this.setAluFormaPago(alu.getAluFormaPago());
        this.setAluFoto(alu.getAluFoto());
        this.setAluId(alu.getAluId());
        this.setAluMail(alu.getAluMail());
        this.setAluNombreFamiliar(alu.getAluNombreFamiliar());
        this.setAluNombres(alu.getAluNombres());
        this.setAluParentesco(alu.getAluParentesco());
        this.setAluProcedencia(alu.getAluProcedencia());
        this.setAluSexo(alu.getAluSexo());
        this.setAluTelefono(alu.getAluTelefono());
        this.setAluTemporal(alu.getAluTemporal());
        this.setAluTipo(alu.getAluTipo());
        this.setAluUnidad(alu.getAluUnidad());
        if (!StringUtils.hasText(alu.getAluDistrito())) {
            LOG.warn("Student without distritct");
            this.setDepartamento("00");
            this.setProvincia("0000");
            this.setDistrito(CAT_DEFAULT);
        } else {
            this.setDepartamento(alu.getAluDistrito().substring(0, 2) + "0000");
            this.setProvincia(alu.getAluDistrito().substring(0, 4) + "00");
            this.setDistrito(alu.getAluDistrito());
        }
        onChangeDepartamento(event);
        onChangeProvincia(event);

        this.setParamPaterno(paramPaterno);
        this.setParamMaterno(paramMaterno);
        this.setParamNombre(paramNombre);
        this.setParamCodigo(paramCodigo);
    }

    public void eliminarAlumno(ActionEvent event) {
        var vAluId = ((UIParameter) event.getComponent().findComponent(PARAM_ALUMNO_ID)).getValue().toString();
        daoAlumnoCL.eliminarAlumnoCl(vAluId);
    }

    /**
     * 1 AcAlumno | 2 ClAlumno
     *
     * @return image searching type
     */
    public int getFindImage() {
        return findImage;
    }

    /**
     * 1 AcAlumno | 2 ClAlumno
     *
     * @param findImage image searching type
     */
    public void setFindImage(int findImage) {
        this.findImage = findImage;
    }

    public AcAlumno getUnivAlumno() {
        return univAlumno;
    }

    public void setUnivAlumno(AcAlumno univAlumno) {
        this.univAlumno = univAlumno;
    }

    public String getUnivCodigo() {
        return univCodigo;
    }

    public void setUnivCodigo(String univCodigo) {
        this.univCodigo = univCodigo;
    }

    public void nuevoUniv(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setUnivCodigo("");
        this.setFindImage(1);
        this.setUnivAlumno(new AcAlumno());
    }

    public void buscarUniversidad(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        AcAlumno alu = daoAlumno.buscarAlumnoCodigo(this.getUnivCodigo());
        if (alu == null) {
            this.setOncomplete("showErrorDiv();hideOkDiv();");
        } else {
            this.setUnivAlumno(alu);
            this.setOncomplete("showOkDiv();hideErrorDiv();");
        }
    }

    public void salvarAlumnoUniversidad(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (!daoAlumnoCL.existeAlumnoUnivRegistrado(this.getUnivAlumno().getId())) {
            var alu = new ClAlumno();

            alu.setAluCodigo(this.getUnivAlumno().getAluCodigo());
            alu.setAluCp(this.getUnivAlumno().getId());
            alu.setAluTemporal(this.getUnivAlumno().getId().toString());
            alu.setAluAppaterno(this.getUnivAlumno().getAluAppaterno());
            alu.setAluApmaterno(this.getUnivAlumno().getAluApmaterno());
            alu.setAluNombres(this.getUnivAlumno().getAluNombres());
            alu.setAluActivo("1");

            alu.setAluDni(this.getUnivAlumno().getAluDni());
            alu.setAluProcedencia(this.getUnivAlumno().getAluProcedencia());
            alu.setAluDireccion("");
            alu.setAluDistrito("");

            alu.setAluDuplicado("00");
            alu.setAluFechaNacimiento(new Date());
            alu.setAluFormaPago("041001");
            alu.setAluFoto(this.getUnivAlumno().getAluFoto());
            alu.setAluMail(this.getUnivAlumno().getAluMail());
            alu.setAluNombreFamiliar(this.getUnivAlumno().getAluFamiliar());

            alu.setAluParentesco("040001");
            alu.setAluPassword(this.getUnivAlumno().getAluCodigo());
            alu.setAluSexo("");
            alu.setAluTelefono("");
            alu.setAluCelular("");

            alu.setAluTipo("057002");
            alu.setAluUnidad("042002");

            daoAlumnoCL.insertarAlumnoCl(alu);
            this.setOncomplete("alert('Se guardo correctamente la informacion');"
                    + "Richfaces.hideModalPanel('mpUniv');");
        } else {
            this.setOncomplete("alert('Ya esta registrado el alumno');");
        }
    }

    public List<MbAlumnoCL> getListaEnlace() {
        return listaEnlace;
    }

    public void setListaEnlace(List<MbAlumnoCL> listaEnlace) {
        this.listaEnlace = listaEnlace;
    }

    public ClAlumno getEnlaceAlumno() {
        return enlaceAlumno;
    }

    public void setEnlaceAlumno(ClAlumno enlaceAlumno) {
        this.enlaceAlumno = enlaceAlumno;
    }

    public void enlaceUniversidad(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        MbAlumnoCL vAlu = (MbAlumnoCL) event.getComponent().getAttributes().get("p_alu");
        int vAluId = vAlu.getAluId();
        String vAluPaterno = vAlu.getAluAppaterno();
        String vAluMaterno = vAlu.getAluApmaterno();
        String vAluNombres = vAlu.getAluNombres();

        List<AcAlumno> lAlumnos = daoAlumno.seleccionarAlumnos("", vAluPaterno, vAluMaterno, vAluNombres, 0);

        this.setEnlaceAlumno(daoAlumnoCL.buscarAlumnoPorAluId(vAluId));
        this.setFindImage(1);
        this.setListaEnlace(lAlumnos.stream()
                .map(a -> {
                    var bean = new MbAlumnoCL();
                    bean.setAluId(a.getId());
                    bean.setAluCodigo(a.getAluCodigo());
                    bean.setAluAppaterno(a.getAluAppaterno());
                    bean.setAluApmaterno(a.getAluApmaterno());
                    bean.setAluNombres(a.getAluNombres());
                    bean.setUnivCodigo(a.getEsp().getEspNombre());
                    return bean;
                })
                .collect(Collectors.toList()));
    }

    public void relacionarUniv(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var enlAluId = Integer.parseInt((((UIParameter) event.getComponent().findComponent("enl_alu_id")).getValue().toString()));
        var acAlumno = daoAlumno.seleccionarUnAlumno(enlAluId);

        var alu = new ClAlumno();
        alu.setAluId(this.getEnlaceAlumno().getAluId());
        if (this.getEnlaceAlumno().getAluCodigo() == null
                || this.getEnlaceAlumno().getAluCodigo().trim().length() == 0) {
            alu.setAluCodigo(acAlumno.getAluCodigo());
        } else {
            alu.setAluCodigo(this.getEnlaceAlumno().getAluCodigo());
        }
        alu.setAluAppaterno(this.getEnlaceAlumno().getAluAppaterno());
        alu.setAluApmaterno(this.getEnlaceAlumno().getAluApmaterno());

        if (numeroCaracteres(this.getEnlaceAlumno().getAluNombres()) < numeroCaracteres(acAlumno.getAluNombres())) {
            alu.setAluNombres(acAlumno.getAluNombres());
        } else {
            alu.setAluNombres(this.getEnlaceAlumno().getAluNombres());
        }

        if (numeroCaracteres(this.getEnlaceAlumno().getAluDni()) < numeroCaracteres(acAlumno.getAluDni())) {
            alu.setAluDni(acAlumno.getAluDni());
        } else {
            alu.setAluDni(this.getEnlaceAlumno().getAluDni());
        }

        if (numeroCaracteres(this.getEnlaceAlumno().getAluMail()) < numeroCaracteres(acAlumno.getAluMail())) {
            alu.setAluMail(acAlumno.getAluMail());
        } else {
            alu.setAluMail(this.getEnlaceAlumno().getAluMail());
        }
        alu.setAluTipo(this.getEnlaceAlumno().getAluTipo());

        alu.setAluActivo("1");
        alu.setAluFoto(acAlumno.getAluFoto());
        alu.setAluDuplicado(this.getEnlaceAlumno().getAluDuplicado());

        if (numeroCaracteres(this.getEnlaceAlumno().getAluProcedencia()) < numeroCaracteres(acAlumno.getAluProcedencia())) {
            alu.setAluProcedencia(acAlumno.getAluProcedencia());
        } else {
            alu.setAluProcedencia(this.getEnlaceAlumno().getAluProcedencia());
        }
        alu.setAluPassword(this.getEnlaceAlumno().getAluPassword());

        alu.setAluCp(acAlumno.getId());
        alu.setAluTemporal(acAlumno.getId().toString());
        alu.setAluSexo(this.getEnlaceAlumno().getAluSexo());

        alu.setAluDistrito(this.getEnlaceAlumno().getAluDistrito());
        alu.setAluDireccion(this.getEnlaceAlumno().getAluDireccion());
        alu.setAluTelefono(this.getEnlaceAlumno().getAluTelefono());
        alu.setAluCelular(this.getEnlaceAlumno().getAluCelular());

        alu.setAluFechaNacimiento(this.getEnlaceAlumno().getAluFechaNacimiento());
        alu.setAluUnidad(this.getEnlaceAlumno().getAluUnidad());
        alu.setAluFormaPago(this.getEnlaceAlumno().getAluFormaPago());
        alu.setAluNombreFamiliar(this.getEnlaceAlumno().getAluNombreFamiliar());
        alu.setAluParentesco(this.getEnlaceAlumno().getAluParentesco());

        daoAlumnoCL.modificarAlumnoCl(alu);
    }

    private int numeroCaracteres(String cadena) {
        if (cadena == null) {
            return 0;
        } else {
            return cadena.trim().length();
        }
    }
}
