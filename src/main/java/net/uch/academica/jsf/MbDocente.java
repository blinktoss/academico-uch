package net.uch.academica.jsf;

import net.uch.academica.dao.HSDocenteDAO;
import net.uch.academica.dao.HSHorarioDAO;
import net.uch.academica.jsf.beans.BeanDocente;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSUbigeoDAO;
import net.uch.mapping.AcDocente;
import net.uch.mapping.TbCatalogo;
import net.uch.mapping.TbDistrito;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.uch.common.Constants.CAT_DEFAULT;

/**
 * @author richard
 */
public class MbDocente {

    private static final Logger LOG = LoggerFactory.getLogger(MbDocente.class);

    private final HSDocenteDAO daoDocente = ServiceFinder.findBean("springHibernateDaoDocente", HSDocenteDAO.class);
    private final HSUbigeoDAO daoUbigeo = ServiceFinder.findBean("springHibernateDaoUbigeo", HSUbigeoDAO.class);

    /**
     * Creates a new instance of mbDocente
     */
    private List<BeanDocente> listaDocente;
    private List<SelectItem> cboDepartamentosN;
    private List<SelectItem> cboProvinciaN;
    private List<SelectItem> cboDistritoN;
    private List<SelectItem> cboDepartamentosR;
    private List<SelectItem> cboProvinciaR;
    private List<SelectItem> cboDistritoR;
    private List<SelectItem> cboSituacion;
    private List<SelectItem> rdoSexo;
    private String docDniW;
    private String docNombreCompletoW;
    private BeanDocente docente;
    private String wDepaNaci = "0000";
    private String wProvNaci = "0000";
    private String wDepaResi = "0000";
    private String wProvResi = "0000";
    private String oncomplete;
    private String mensaje = "";

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        docente = new BeanDocente();

        HSCatalogoDAO dao = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        List<TbCatalogo> situations = dao.seleccionarCatalogo("002");
        this.setCboSituacion(situations.stream()
                .map(d -> new SelectItem(d.getCatCodigoGrupo() + d.getCatCodigoElemento(), d.getCatDescripcionElemento()))
                .collect(Collectors.toList()));

        List<TbCatalogo> genders = dao.seleccionarCatalogo("004");
        this.setRdoSexo(genders.stream()
                .map(g -> new SelectItem(g.getCatCodigoGrupo() + g.getCatCodigoElemento(), g.getCatDescripcionElemento()))
                .collect(Collectors.toList()));

        List<TbDistrito> tbDepartments = daoUbigeo.seleccionarDepartamento();
        List<SelectItem> siDepartments = tbDepartments.stream()
                .map(d -> new SelectItem(d.getId(), d.getDisDes()))
                .collect(Collectors.toList());

        this.setCboDepartamentosN(siDepartments);
        cboProvinciaN = new ArrayList<>();
        cboDistritoN = new ArrayList<>();

        this.setCboDepartamentosR(siDepartments);
        cboProvinciaR = new ArrayList<>();
        cboDistritoR = new ArrayList<>();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public List<SelectItem> getCboSituacion() {
        return cboSituacion;
    }

    public void setCboSituacion(List<SelectItem> cboSituacion) {
        this.cboSituacion = cboSituacion;
    }

    public List<SelectItem> getRdoSexo() {
        return rdoSexo;
    }

    public void setRdoSexo(List<SelectItem> rdoSexo) {
        this.rdoSexo = rdoSexo;
    }

    public String getwDepaNaci() {
        return wDepaNaci;
    }

    public void setwDepaNaci(String wDepaNaci) {
        this.wDepaNaci = wDepaNaci;
    }

    public String getwDepaResi() {
        return wDepaResi;
    }

    public void setwDepaResi(String wDepaResi) {
        this.wDepaResi = wDepaResi;
    }

    public String getwProvNaci() {
        return wProvNaci;
    }

    public void setwProvNaci(String wProvNaci) {
        this.wProvNaci = wProvNaci;
    }

    public String getwProvResi() {
        return wProvResi;
    }

    public void setwProvResi(String wProvResi) {
        this.wProvResi = wProvResi;
    }

    public BeanDocente getDocente() {
        return docente;
    }

    public void setDocente(BeanDocente docente) {
        this.docente = docente;
    }

    public String getDocDniW() {
        return docDniW;
    }

    public void setDocDniW(String docDniW) {
        this.docDniW = docDniW;
    }

    public String getDocNombreCompletoW() {
        return docNombreCompletoW;
    }

    public void setDocNombreCompletoW(String docNombreCompletoW) {
        this.docNombreCompletoW = docNombreCompletoW;
    }

    public List<SelectItem> getCboDepartamentosN() {
        return cboDepartamentosN;
    }

    public void setCboDepartamentosN(List<SelectItem> cboDepartamentosN) {
        this.cboDepartamentosN = cboDepartamentosN;
    }

    public List<SelectItem> getCboDepartamentosR() {
        return cboDepartamentosR;
    }

    public void setCboDepartamentosR(List<SelectItem> cboDepartamentosR) {
        this.cboDepartamentosR = cboDepartamentosR;
    }

    public void loadDistrictsN(ActionEvent event) {
        LOG.debug("[{}] from province '{}'", event.getPhaseId(), this.getwProvNaci());
        cboDistritoN.clear();
        if (!CAT_DEFAULT.equals(wProvNaci)) {
            List<TbDistrito> districts = daoUbigeo.seleccionarDistrito(this.getwDepaNaci(), this.getwProvNaci());
            this.setCboDistritoN(districts.stream()
                    .map(d -> new SelectItem(d.getId(), d.getDisDes())).collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getCboDistritoN() {
        return cboDistritoN;
    }

    public void setCboDistritoN(List<SelectItem> cboDistritoN) {
        this.cboDistritoN = cboDistritoN;
    }

    public void loadDistrictsR(ActionEvent event) {
        LOG.debug("[{}] from province '{}'", event.getPhaseId(), this.getwProvNaci());
        cboDistritoR.clear();
        if (!CAT_DEFAULT.equals(wProvResi)) {
            List<TbDistrito> districts = daoUbigeo.seleccionarDistrito(this.getwDepaResi(), this.getwProvResi());
            this.setCboDistritoR(districts.stream()
                    .map(d -> new SelectItem(d.getId(), d.getDisDes())).collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getCboDistritoR() {
        return cboDistritoR;
    }

    public void setCboDistritoR(List<SelectItem> cboDistritoR) {
        this.cboDistritoR = cboDistritoR;
    }

    public void loadProvincesN(ActionEvent event) {
        LOG.debug("[{}] from department '{}'", event.getPhaseId(), this.getwDepaNaci());
        cboProvinciaN.clear();
        cboDistritoN.clear();
        if (!CAT_DEFAULT.equals(wDepaNaci)) {
            this.setCboProvinciaN(daoUbigeo.seleccionarProvincia(wDepaNaci).stream()
                    .map(p -> new SelectItem(p.getId(), p.getDisDes())).collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getCboProvinciaN() {
        return cboProvinciaN;
    }

    public void setCboProvinciaN(List<SelectItem> cboProvinciaN) {
        this.cboProvinciaN = cboProvinciaN;
    }

    public void loadProvincesR(ActionEvent event) {
        LOG.debug("[{}] from department '{}'", event.getPhaseId(), this.getwDepaResi());
        cboProvinciaR.clear();
        cboDistritoR.clear();
        if (!CAT_DEFAULT.equals(wDepaResi)) {
            this.setCboProvinciaR(daoUbigeo.seleccionarProvincia(wDepaResi).stream()
                    .map(p -> new SelectItem(p.getId(), p.getDisDes())).collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getCboProvinciaR() {
        return cboProvinciaR;
    }

    public void setCboProvinciaR(List<SelectItem> cboProvinciaR) {
        this.cboProvinciaR = cboProvinciaR;
    }

    public List<BeanDocente> getListaDocente() {
        return listaDocente;
    }

    public void setListaDocente(List<BeanDocente> listaDocente) {
        this.listaDocente = listaDocente;
    }

    public void buscarDocente(ActionEvent event) {
        LOG.info("[{}] Buscando docente '{}' | '{}'", event.getPhaseId(), this.getDocDniW(), this.getDocNombreCompletoW());
        List<Object[]> lista = daoDocente.seleccionarDocente(this.getDocDniW(), this.getDocNombreCompletoW());
        List<BeanDocente> listaT = new ArrayList<>();

        for (Object[] doc : lista) {
            var bC = new BeanDocente();
            bC.setId(Integer.parseInt(doc[0].toString()));
            bC.setDocCodigo(Objects.toString(doc[1], null));
            bC.setDocNombre(doc[2].toString());
            bC.setDocCorreo(Objects.toString(doc[3], null));
            bC.setDocTelefono(Objects.toString(doc[4], null));
            bC.setDocNacimiento(Objects.toString(doc[5], CAT_DEFAULT));
            bC.setDocResidencia(Objects.toString(doc[6], CAT_DEFAULT));
            bC.setDocPeriodoInicio((Date) doc[7]);
            bC.setDocSituacion(Objects.toString(doc[8], null));
            bC.setDocSexo(Objects.toString(doc[9], null));
            bC.setDocDni(Objects.toString(doc[10], null));
            bC.setDocAppaterno(doc[11].toString());
            bC.setDocApmaterno(Objects.toString(doc[12], null));
            bC.setDocNombres(Objects.toString(doc[13], null));
            bC.setDocNacimientoDesc(Objects.toString(doc[14], ""));
            bC.setDocResidenciaDesc(Objects.toString(doc[15], ""));
            bC.setDocSituacionDesc(Objects.toString(doc[16], ""));
            bC.setDocSexoDesc(Objects.toString(doc[17], ""));
            listaT.add(bC);
        }
        this.setListaDocente(listaT);
        LOG.info("Total of teachers {}", listaT.size());
    }

    public void limpiarData(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        var doc = new BeanDocente();
        doc.setId(0);
        this.setMensaje("");
        this.setDocente(doc);
        this.setwDepaNaci(CAT_DEFAULT);
        this.setwDepaResi(CAT_DEFAULT);
        this.setwProvNaci("0000");
        this.cboProvinciaN.clear();
        this.cboDistritoN.clear();
        this.setwProvResi("0000");
        this.cboProvinciaR.clear();
        this.cboDistritoR.clear();
    }

    public void editarDocente(ActionEvent event) {
        var pId2 = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_id2")).getValue().toString());
        LOG.debug("[{}] Editing teacher '{}'", event.getPhaseId(), pId2);
        this.setOncomplete("");
        this.setMensaje("");
        AcDocente doc = daoDocente.seleccionarDocente(pId2);
        this.getDocente().setId(doc.getId());
        this.getDocente().setDocActivo(doc.getDocActivo());
        this.getDocente().setDocApmaterno(doc.getDocApmaterno());
        this.getDocente().setDocAppaterno(doc.getDocAppaterno());
        this.getDocente().setDocDni(doc.getDocDni());
        this.getDocente().setDocCorreo(doc.getDocCorreo());
        this.getDocente().setDocCodigo(doc.getDocCodigo());
        this.getDocente().setDocNacimiento(doc.getDocNacimiento());
        this.getDocente().setDocNombre(doc.getDocNombre());
        this.getDocente().setDocNombres(doc.getDocNombres());
        this.getDocente().setDocPassword(doc.getDocPassword());
        this.getDocente().setDocPeriodoInicio(doc.getDocPeriodoInicio());
        this.getDocente().setDocResidencia(doc.getDocResidencia());
        this.getDocente().setDocSexo(doc.getDocSexo());
        this.getDocente().setDocSituacion(doc.getDocSituacion());
        this.getDocente().setDocTelefono(doc.getDocTelefono());

        if (!StringUtils.hasText(doc.getDocNacimiento()) || doc.getDocNacimiento().equals(CAT_DEFAULT)) {
            this.setwDepaNaci(CAT_DEFAULT);
            this.setwProvNaci(CAT_DEFAULT);
        } else {
            this.setwDepaNaci(doc.getDocNacimiento().substring(0, 2) + "0000");
            this.setwProvNaci(doc.getDocNacimiento().substring(0, 4) + "00");
        }
        loadProvincesN(event);
        loadDistrictsN(event);
        if (!StringUtils.hasText(doc.getDocResidencia()) || doc.getDocResidencia().equals(CAT_DEFAULT)) {
            this.setwDepaResi(CAT_DEFAULT);
            this.setwProvResi(CAT_DEFAULT);
        } else {
            this.setwDepaResi(doc.getDocResidencia().substring(0, 2) + "0000");
            this.setwProvResi(doc.getDocResidencia().substring(0, 4) + "00");
        }
        loadProvincesR(event);
        loadDistrictsR(event);

        this.setOncomplete("Richfaces.showModalPanel('mpDocente');");
    }

    public void eliminarDocente(ActionEvent event) {
        var pId2 = Integer.valueOf(((UIParameter) event.getComponent().findComponent("p_id2")).getValue().toString());
        LOG.debug("[{}] Deleting teacher '{}'", event.getPhaseId(), pId2);
        setMensaje("");
        HSHorarioDAO dao = ServiceFinder.findBean("springHibernateDaoHorario", HSHorarioDAO.class);

        if (dao.seleccionarHorarioPorDocente(pId2, 9) == 0) {
            daoDocente.eliminarDocente(pId2);
            LOG.info("Teacher deleted {}", pId2);
        } else {
            setMensaje("El Docente tiene un horario asignado, no se puede eliminar");
        }
    }

    public void guardarDatos(ActionEvent event) {
        var pId = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_id")).getValue().toString());
        LOG.debug("[{}] registering teacher {}", event.getPhaseId(), pId);
        this.setOncomplete("");
        this.setMensaje("");
        this.getDocente().setDocActivo("1");
        this.getDocente().setDocCodigo(this.getDocente().getDocDni());
        this.getDocente().setDocNombre(this.getDocente().getDocAppaterno() + " " + this.getDocente().getDocApmaterno() + " " + this.getDocente().getDocNombres());
        this.getDocente().setDocPassword(this.getDocente().getDocDni());
        var doc = new AcDocente();
        doc.setId(pId);
        doc.setDocActivo("1");
        doc.setDocApmaterno(this.getDocente().getDocApmaterno());
        doc.setDocAppaterno(this.getDocente().getDocAppaterno());
        doc.setDocCodigo(this.getDocente().getDocDni());
        doc.setDocCorreo(this.getDocente().getDocCorreo());
        doc.setDocDni(this.getDocente().getDocDni());
        doc.setDocNacimiento(this.getDocente().getDocNacimiento());
        doc.setDocNombre(this.getDocente().getDocAppaterno() + " " + this.getDocente().getDocApmaterno() + " " + this.getDocente().getDocNombres());
        doc.setDocNombres(this.getDocente().getDocNombres());
        doc.setDocPeriodoInicio(this.getDocente().getDocPeriodoInicio());
        doc.setDocResidencia(this.getDocente().getDocResidencia());
        doc.setDocSexo(this.getDocente().getDocSexo());
        doc.setDocSituacion(this.getDocente().getDocSituacion());
        doc.setDocTelefono(this.getDocente().getDocTelefono());
        if (pId > 0) {
            doc.setDocPassword(this.getDocente().getDocPassword());
            if (validarCampos(doc)) {
                daoDocente.actualizarDocente(doc);
                LOG.info("Teacher updated {}", doc.getId());
                this.setOncomplete("alert('Registro modificado con exito');Richfaces.hideModalPanel('mpDocente');");
            }
        } else {
            doc.setDocPassword(this.getDocente().getDocDni());
            if (daoDocente.existeDocente(this.getDocente().getDocDni())) {
                this.setOncomplete("alert('El numero de DNI ya existe')");
            } else {
                if (validarCampos(doc)) {
                    daoDocente.insertarDocente(doc);
                    this.getDocente().setId(doc.getId());
                    LOG.info("Teacher saved {}", docente.getId());
                    this.setOncomplete("alert('Registro agregado con exito');Richfaces.hideModalPanel('mpDocente');");
                }
            }
        }
    }

    public boolean validarCampos(AcDocente doc) {
        if (doc.getDocAppaterno().length() < 3) {
            oncomplete = "";
            this.setOncomplete("alert('Ingrese Apellido Paterno')");
            return false;

        }
        if (doc.getDocApmaterno().length() < 3) {
            oncomplete = "";
            this.setOncomplete("alert('Ingrese Apellido Materno')");
            return false;
        }
        if (doc.getDocNombres().length() < 3) {
            oncomplete = "";
            this.setOncomplete("alert('Ingrese Nombres')");
            return false;
        }
        if (doc.getDocCorreo().length() < 10) {
            oncomplete = "";
            this.setOncomplete("alert('Ingrese Correo Electronico')");
            return false;
        }
        if (doc.getDocTelefono().length() < 5) {
            oncomplete = "";
            this.setOncomplete("alert('Ingrese nro telefono o celular')");
            return false;
        }

        return true;
    }
}
