package net.uch.configuracion.jsf;

import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSLocalDAO;
import net.uch.mapping.AcLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MbLocal {

    private static final Logger LOG = LoggerFactory.getLogger(MbLocal.class);

    private final HSLocalDAO daoLocal = ServiceFinder.findBean("springHibernateDaoLocal", HSLocalDAO.class);

    private int localId;
    private String paramDescription;
    private String modalDescription;
    private boolean editable = false;
    private boolean view = true;
    private List<MbLocal> listaLocal;
    private String oncomplete;

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
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

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for '{}'", event.getPhaseId(), this.paramDescription);
        listaLocal = new ArrayList<>();
        List<AcLocal> lista = daoLocal.seleccionarLocal(this.getParamDescription());
        this.setListaLocal(lista.stream().map(acLocal -> {
            var bL = new MbLocal();
            bL.setLocalId(acLocal.getId());
            bL.setParamDescription(acLocal.getLocDes());
            return bL;
        }).collect(Collectors.toList()));
        LOG.info("Total of locals {}", listaLocal.size());
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.modalDescription = "";
    }

    public void grabar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getModalDescription().trim().isEmpty()) {
            this.setOncomplete("alert('Ingrese todos los datos Requeridos.')");
        } else {
            var local = new AcLocal();
            local.setLocDes(this.getModalDescription());
            local.setLocActivo("1");
            daoLocal.insertarLocal(local);
            this.setLocalId(local.getId());
            LOG.info("Local saved {}", localId);
            this.setOncomplete("alert('Se creo un Local correctamente.');Richfaces.hideModalPanel('mp')");
            seleccionar(event);
        }
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setModalDescription(this.getParamDescription());
        this.setView(false);
        this.setEditable(true);
    }

    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        if (this.paramDescription.isEmpty()) {
            this.setOncomplete("alert('Ingrese Descripcion.')");
        } else {
            var local = new AcLocal();
            local.setId(this.localId);
            local.setLocDes(this.paramDescription);
            local.setLocActivo("1");
            daoLocal.actualizarLocal(local);
            LOG.info("Local updated {}", localId);
            this.setView(true);
            this.setEditable(false);
            this.setOncomplete("alert('Se Actualizo la Descripcion del Lacal correctamente.')");
        }
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setParamDescription(this.getModalDescription());
        this.setView(true);
        this.setEditable(false);
    }

    public void eliminarFila(ActionEvent event) {
        MbLocal row = (MbLocal) event.getComponent().getAttributes().get("local");
        localId = row.getLocalId();
        modalDescription = row.getParamDescription();
        LOG.debug("[{}] To delete {}", event.getPhaseId(), localId);
    }

    public void eliminar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var local = new AcLocal();
        local.setId(this.localId);
        local.setLocActivo("1");
        daoLocal.eliminarLocal(local);
        LOG.info("Local deleted {}", localId);

        seleccionar(event);
    }

    public String getModalDescription() {
        return modalDescription;
    }

    public void setModalDescription(String modalDescription) {
        this.modalDescription = modalDescription;
    }

    public List<MbLocal> getListaLocal() {
        return listaLocal;
    }

    public void setListaLocal(List<MbLocal> listaLocal) {
        this.listaLocal = listaLocal;
    }
}
