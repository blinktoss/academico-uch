package net.uch.configuracion.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSLocalDAO;
import net.uch.configuracion.dao.HSPabellonDAO;
import net.uch.mapping.AcLocal;
import net.uch.mapping.AcPabellon;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.List;
import java.util.stream.Collectors;

public class MbPabellon {

    private static final Logger LOG = LoggerFactory.getLogger(MbPabellon.class);

    private final HSPabellonDAO daoPabellon = ServiceFinder.findBean("springHibernateDaoPabellon", HSPabellonDAO.class);

    private int pabellonId;
    private String paramNombre;
    private String pabellonDescription;
    private int paramIdLocal;
    private SelectItem[] paramComboLocales;
    private List<MbPabellon> listaPabellon;
    private String rowLocalName;
    private boolean editable = false;
    private boolean view = true;
    private String modalNombre;
    private String modalDescription;
    private int modalIdLocal;
    private String oncomplete;
    private MbPabellon data;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSLocalDAO dao = ServiceFinder.findBean("springHibernateDaoLocal", HSLocalDAO.class);
        List<AcLocal> lista = dao.seleccionarLocal("");
        setParamComboLocales(lista.stream()
                .map(l -> new SelectItem(l.getId(), l.getLocDes()))
                .toArray(SelectItem[]::new));
    }

    public int getPabellonId() {
        return pabellonId;
    }

    public void setPabellonId(int pabellonId) {
        this.pabellonId = pabellonId;
    }

    public String getParamNombre() {
        return paramNombre;
    }

    public void setParamNombre(String paramNombre) {
        this.paramNombre = paramNombre;
    }

    public String getPabellonDescription() {
        return pabellonDescription;
    }

    public void setPabellonDescription(String pabellonDescription) {
        this.pabellonDescription = pabellonDescription;
    }

    public int getParamIdLocal() {
        return paramIdLocal;
    }

    public void setParamIdLocal(int paramIdLocal) {
        this.paramIdLocal = paramIdLocal;
    }

    public SelectItem[] getParamComboLocales() {
        return paramComboLocales;
    }

    public void setParamComboLocales(SelectItem[] paramComboLocales) {
        this.paramComboLocales = paramComboLocales;
    }

    public List<MbPabellon> getListaPabellon() {
        return listaPabellon;
    }

    public void setListaPabellon(List<MbPabellon> listaPabellon) {
        this.listaPabellon = listaPabellon;
    }

    public String getRowLocalName() {
        return rowLocalName;
    }

    public void setRowLocalName(String rowLocalName) {
        this.rowLocalName = rowLocalName;
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

    public String getModalNombre() {
        return modalNombre;
    }

    public void setModalNombre(String modalNombre) {
        this.modalNombre = modalNombre;
    }

    public String getModalDescription() {
        return modalDescription;
    }

    public void setModalDescription(String modalDescription) {
        this.modalDescription = modalDescription;
    }

    public int getModalIdLocal() {
        return modalIdLocal;
    }

    public void setModalIdLocal(int modalIdLocal) {
        this.modalIdLocal = modalIdLocal;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public MbPabellon getData() {
        return data;
    }

    public void setData(MbPabellon data) {
        this.data = data;
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for '{}' | {}", event.getPhaseId(), this.paramNombre, this.paramIdLocal);

        List<AcPabellon> lista = daoPabellon.seleccionarPabellon(this.getParamNombre(), this.getParamIdLocal());
        this.setListaPabellon(lista.stream().map(acPabellon -> {
            var bL = new MbPabellon();
            bL.setPabellonId(acPabellon.getId());
            bL.setParamNombre(acPabellon.getPabNombre());
            bL.setPabellonDescription(acPabellon.getPabDescripcion());

            AcLocal loc = acPabellon.getLoc();
            bL.setParamIdLocal(loc.getId());
            bL.setRowLocalName(loc.getLocDes());
            return bL;
        }).collect(Collectors.toList()));
        LOG.info("Total of pavilion {}", listaPabellon.size());
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.modalNombre = "";
        this.modalDescription = "";
        this.modalIdLocal = 0;
    }

    public void grabar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getModalNombre().trim().length() == 0 ||
                this.getModalIdLocal() == 0) {
            this.setOncomplete("alert('Ingrese todos los datos Requeridos.')");
        } else {
            var pabellon = new AcPabellon();
            pabellon.setPabNombre(this.getModalNombre());
            pabellon.setPabDescripcion(this.getModalDescription());
            var loc = new AcLocal();
            loc.setId(this.getModalIdLocal());
            pabellon.setLoc(loc);
            pabellon.setPabActivo("1");
            daoPabellon.insertarPabellon(pabellon);
            this.setPabellonId(pabellon.getId());
            LOG.info("Pavilion saved {}", pabellonId);
            this.setOncomplete("alert('Se creo un Pabellon correctamente.');Richfaces.hideModalPanel('mp')");
            seleccionar(event);
        }
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setModalNombre(this.getParamNombre());
        this.setModalDescription(this.getPabellonDescription());
        this.setModalIdLocal(this.getParamIdLocal());
        this.setView(false);
        this.setEditable(true);
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setParamNombre(this.getModalNombre());
        this.setPabellonDescription(this.getModalDescription());
        this.setParamIdLocal(this.getModalIdLocal());
        this.setView(true);
        this.setEditable(false);
    }

    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        if (paramNombre.trim().length() == 0 ||
                paramIdLocal == 0) {
            this.setOncomplete("alert('Ingrese Datos.')");
        } else {
            var pabellon = new AcPabellon();
            pabellon.setId(pabellonId);
            pabellon.setPabNombre(paramNombre);
            pabellon.setPabDescripcion(pabellonDescription);
            var loc = new AcLocal();
            loc.setId(paramIdLocal);
            pabellon.setLoc(loc);
            pabellon.setPabActivo("1");
            daoPabellon.actualizarPabellon(pabellon);
            LOG.info("Pavilion updated {}", pabellonId);
            this.setView(true);
            this.setEditable(false);
            this.setOncomplete("alert('Se Actualizaron los Datos del Pabellon correctamente.')");
        }
    }

    public void eliminar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var loc = new AcLocal();
        loc.setId(data.getParamIdLocal());

        var pabellon = new AcPabellon();
        pabellon.setId(data.getPabellonId());
        pabellon.setLoc(loc);
        pabellon.setPabActivo("1");
        daoPabellon.eliminarPabellon(pabellon);
        LOG.info("Pavilion deleted {}", data.getPabellonId());

        seleccionar(event);
    }
}
