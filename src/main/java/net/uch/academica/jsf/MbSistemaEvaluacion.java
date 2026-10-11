package net.uch.academica.jsf;

import net.uch.academica.dao.HSSistemaEvaluacionDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.mapping.AcSisEvaDetalle;
import net.uch.mapping.AcSisEvaluacion;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import java.util.*;

import static net.uch.common.Constants.*;

public class MbSistemaEvaluacion {

    private static final Logger LOG = LoggerFactory.getLogger(MbSistemaEvaluacion.class);

    private final HSSistemaEvaluacionDAO daoSistemaEvaluacion = ServiceFinder.findBean("springHibernateDaoSistemaEvaluacion", HSSistemaEvaluacionDAO.class);

    private String flagVer = "0";
    private String imagen = ACTION_IMAGES_DOWN;
    private int idS;
    private int idI;
    private String codigoS;
    private String codigoI;
    private String nombreS;
    private String nombreI;
    private Date creacionS;
    private String vigenteImg;
    private boolean vigenteI;
    private String nombreF;
    private List<MbSistemaEvaluacion> tablaSisEva;
    private List<MbSistemaEvaluacion> detalle;
    private String codDetAux;
    private String nomDetAux;
    private int pesDetAux;
    private List<MbSistemaEvaluacion> tablaSisEvaDetalle;
    private int idDet = 0;
    private String codigoDet;
    private String nombreDet;
    private int pesoDet;
    private String activoDet;
    private boolean editable = false;
    private boolean view = true;
    private boolean visible = false;
    private boolean ver = false;
    private String oncomplete;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
    }

    public int getIdS() {
        return idS;
    }

    public void setIdS(int idS) {
        this.idS = idS;
    }

    public int getIdI() {
        return idI;
    }

    public void setIdI(int idI) {
        this.idI = idI;
    }

    public String getCodigoS() {
        return codigoS;
    }

    public void setCodigoS(String codigoS) {
        this.codigoS = codigoS;
    }

    public String getCodigoI() {
        return codigoI;
    }

    public void setCodigoI(String codigoI) {
        this.codigoI = codigoI;
    }

    public String getNombreS() {
        return nombreS;
    }

    public void setNombreS(String nombreS) {
        this.nombreS = nombreS;
    }

    public String getNombreI() {
        return nombreI;
    }

    public void setNombreI(String nombreI) {
        this.nombreI = nombreI;
    }

    public Date getCreacionS() {
        return creacionS;
    }

    public void setCreacionS(Date creacionS) {
        this.creacionS = creacionS;
    }

    public String getVigenteImg() {
        return vigenteImg;
    }

    public void setVigenteImg(String vigenteImg) {
        this.vigenteImg = vigenteImg;
    }

    public boolean isVigenteI() {
        return vigenteI;
    }

    public void setVigenteI(boolean vigenteI) {
        this.vigenteI = vigenteI;
    }

    public String getNombreF() {
        return nombreF;
    }

    public void setNombreF(String nombreF) {
        this.nombreF = nombreF;
    }

    public List<MbSistemaEvaluacion> getTablaSisEva() {
        return tablaSisEva;
    }

    public void setTablaSisEva(List<MbSistemaEvaluacion> tablaSisEva) {
        this.tablaSisEva = tablaSisEva;
    }

    public List<MbSistemaEvaluacion> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<MbSistemaEvaluacion> detalle) {
        this.detalle = detalle;
    }

    public int getIdDet() {
        return idDet;
    }

    public void setIdDet(int idDet) {
        this.idDet = idDet;
    }

    public String getNombreDet() {
        return nombreDet;
    }

    public void setNombreDet(String nombreDet) {
        this.nombreDet = nombreDet;
    }

    public int getPesoDet() {
        return pesoDet;
    }

    public void setPesoDet(int pesoDet) {
        this.pesoDet = pesoDet;
    }

    public List<MbSistemaEvaluacion> getTablaSisEvaDetalle() {
        return tablaSisEvaDetalle;
    }

    public void setTablaSisEvaDetalle(List<MbSistemaEvaluacion> tablaSisEvaDetalle) {
        this.tablaSisEvaDetalle = tablaSisEvaDetalle;
    }

    public String getCodigoDet() {
        return codigoDet;
    }

    public void setCodigoDet(String codigoDet) {
        this.codigoDet = codigoDet;
    }

    public String getActivoDet() {
        return activoDet;
    }

    public void setActivoDet(String activoDet) {
        this.activoDet = activoDet;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVer() {
        return ver;
    }

    public void setVer(boolean ver) {
        this.ver = ver;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public String getFlagVer() {
        return flagVer;
    }

    public void setFlagVer(String flagVer) {
        this.flagVer = flagVer;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setIdI(0);
        this.setCodigoI("");
        this.setNombreI("");
        this.setVigenteI(false);
        setTablaSisEvaDetalle(new ArrayList<>());
        idDet = 0;
        this.setCodigoDet("");
        this.setNombreDet("");
        this.setPesoDet(0);
        this.setOncomplete("");
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for '{}'", event.getPhaseId(), this.getNombreF());

        loadData(this.getNombreF());
    }

    private void loadData(String qName) {
        tablaSisEva = new ArrayList<>();
        List<AcSisEvaluacion> lista = daoSistemaEvaluacion.obtenerSistemaEvaluacion(qName);
        for (AcSisEvaluacion acSisEvaluacion : lista) {
            var evaluacion = new MbSistemaEvaluacion();
            evaluacion.setIdS(acSisEvaluacion.getId());
            evaluacion.setCodigoS(acSisEvaluacion.getSisevaCodigo());
            evaluacion.setCreacionS(acSisEvaluacion.getSisevaCreacion());
            evaluacion.setNombreS(acSisEvaluacion.getSisevaNombre());
            if ("1".equals(acSisEvaluacion.getSisevaVigente())) {
                evaluacion.setVigenteImg(ACTION_IMAGES_ACTIVAR);
            } else {
                evaluacion.setVigenteImg(ACTION_IMAGES_DESACTIVAR);
            }
            Set<AcSisEvaDetalle> sisevadet = acSisEvaluacion.getAcSisEvaDetalles();
            List<AcSisEvaDetalle> list = Collections.synchronizedList(new LinkedList<>(sisevadet));
            List<MbSistemaEvaluacion> a = new ArrayList<>();
            for (AcSisEvaDetalle acSisEvaDetalle : list) {
                var siseva = new MbSistemaEvaluacion();
                siseva.setIdDet(acSisEvaDetalle.getId());
                siseva.setCodigoDet(acSisEvaDetalle.getSisevaDetalleCodigo());
                siseva.setNombreDet(acSisEvaDetalle.getSisevaDetalleNombre());
                siseva.setPesoDet(acSisEvaDetalle.getSisevaDetallePeso());
                siseva.setActivoDet(acSisEvaDetalle.getSisevaDetalleActivo());
                a.add(siseva);
            }
            evaluacion.setDetalle(a);
            tablaSisEva.add(evaluacion);
        }
        this.setTablaSisEva(tablaSisEva);
        LOG.info("Total of evaluations systems {}", tablaSisEva.size());
    }

    public void guardar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        if (this.codigoI.trim().isEmpty() ||
                this.nombreI.trim().isEmpty()) {
            this.setOncomplete("alert('Complete todos los campos')");
            return;
        }

        var siseva = new AcSisEvaluacion();
        siseva.setId(this.getIdI());
        siseva.setSisevaCodigo(this.getCodigoI());
        siseva.setSisevaNombre(this.getNombreI());
        siseva.setSisevaCreacion(new Date());
        siseva.setSisevaVigente(this.isVigenteI() ? "1" : "0");
        siseva.setSisevaActivo("1");
        Set<AcSisEvaDetalle> sisEvaDetalles = new LinkedHashSet<>();
        for (MbSistemaEvaluacion bean : tablaSisEvaDetalle) {
            var sisevadet = new AcSisEvaDetalle();
            if (bean.getIdDet() > 0) {
                sisevadet.setId(bean.getIdDet());
            }
            sisevadet.setSisevaDetalleCodigo(bean.getCodigoDet());
            sisevadet.setSisevaDetallePeso(bean.getPesoDet());
            sisevadet.setSisevaDetalleNombre(bean.getNombreDet());
            sisevadet.setSisevaDetalleActivo(bean.getActivoDet());
            sisevadet.setSiseva(siseva);
            sisEvaDetalles.add(sisevadet);
        }
        siseva.setAcSisEvaDetalles(sisEvaDetalles);
        String mensaje;
        if (this.getIdI() == 0) {
            daoSistemaEvaluacion.insertarSistemaEvaluacion(siseva);
            this.setIdI(siseva.getId());
            LOG.info("Evaluation system saved {}", idI);
            mensaje = "alert('Se guardo el registro correctamente');";
        } else {
            daoSistemaEvaluacion.actualizarSistemaEvaluacion(siseva);
            LOG.info("Evaluation system updated {}", idI);
            mensaje = "alert('Se actualizo el registro correctamente');";
        }
        this.setOncomplete(mensaje + "Richfaces.hideModalPanel('mp')");
        loadData(this.getNombreF());
    }

    public void editar(ActionEvent event) {
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_s");
        var idAux = Integer.parseInt(id.getValue().toString());
        LOG.debug("[{}] Evaluation system to edit '{}'", event.getPhaseId(), idAux);

        AcSisEvaluacion se = daoSistemaEvaluacion.obtenerSistemaEvaluacion(idAux);

        idI = se.getId();
        codigoI = se.getSisevaCodigo();
        nombreI = se.getSisevaNombre();
        vigenteI = "1".equals(se.getSisevaVigente());

        Set<AcSisEvaDetalle> sisevadet = se.getAcSisEvaDetalles();

        tablaSisEvaDetalle = new ArrayList<>();
        for (AcSisEvaDetalle acSisEvaDetalle : sisevadet) {
            var siseva = new MbSistemaEvaluacion();
            siseva.setIdDet(acSisEvaDetalle.getId());
            siseva.setCodigoDet(acSisEvaDetalle.getSisevaDetalleCodigo());
            siseva.setNombreDet(acSisEvaDetalle.getSisevaDetalleNombre());
            siseva.setPesoDet(acSisEvaDetalle.getSisevaDetallePeso());
            siseva.setActivoDet(acSisEvaDetalle.getSisevaDetalleActivo());
            tablaSisEvaDetalle.add(siseva);
        }
        idDet = 0;
        this.setCodigoDet("");
        this.setNombreDet("");
        this.setPesoDet(0);
        this.setOncomplete("");
    }

    public void eliminar(ActionEvent event) {
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_s");
        var idAux = Integer.valueOf(id.getValue().toString());
        LOG.debug("[{}] Evaluation system to delete '{}'", event.getPhaseId(), idAux);
        daoSistemaEvaluacion.eliminarSistemaEvaluacion(idAux);
        loadData(this.getNombreF());
    }

    public void agregar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.codigoDet.trim().isEmpty() ||
                this.nombreDet.trim().isEmpty() ||
                this.pesoDet <= 0) {
            this.setOncomplete("alert('Ingrese datos para el detalle');");
            return;
        }

        var evaluacion = new MbSistemaEvaluacion();
        evaluacion.setIdDet(idDet);
        evaluacion.setCodigoDet(this.getCodigoDet());
        evaluacion.setNombreDet(this.getNombreDet());
        evaluacion.setPesoDet(this.getPesoDet());
        evaluacion.setActivoDet("1");
        tablaSisEvaDetalle.add(evaluacion);
        idDet--;
    }

    public void quitar(ActionEvent event) {
        UIParameter id = (UIParameter) event.getComponent().findComponent("id_det");
        var idAux = Integer.parseInt(id.getValue().toString());
        LOG.debug("[{}] detail to remove {}", event.getPhaseId(), idAux);
        MbSistemaEvaluacion x = tablaSisEvaDetalle.stream()
                .filter(sed -> idAux == sed.getIdDet())
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        if (idAux > 0) {
            x.setActivoDet("0");
        } else {
            tablaSisEvaDetalle.remove(x);
        }
    }

    public void edit(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        codDetAux = this.getCodigoDet();
        nomDetAux = this.getNombreDet();
        pesDetAux = this.getPesoDet();
        this.setView(false);
        this.setEditable(true);
        this.setVisible(true);
    }

    public void aceptar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setView(true);
        this.setEditable(false);
        this.setVisible(false);
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setCodigoDet(codDetAux);
        this.setNombreDet(nomDetAux);
        this.setPesoDet(pesDetAux);
        this.setView(true);
        this.setEditable(false);
        this.setVisible(false);
    }

    public void openDetail(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter flag = (UIParameter) event.getComponent().findComponent("flag");
        if (flag.getValue().toString().equals("0")) {
            this.setVer(true);
            this.setFlagVer("1");
            this.setImagen(ACTION_IMAGES_UP);
        } else {
            this.setVer(false);
            this.setFlagVer("0");
            this.setImagen(ACTION_IMAGES_DOWN);
        }
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }
}
