package net.uch.configuracion.jsf;

import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSAulaDAO;
import net.uch.configuracion.dao.HSLocalDAO;
import net.uch.configuracion.dao.HSPabellonDAO;
import net.uch.mapping.AcAula;
import net.uch.mapping.AcPabellon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MbAula {

    private static final Logger LOG = LoggerFactory.getLogger(MbAula.class);

    private final HSAulaDAO daoAula = ServiceFinder.findBean("springHibernateDaoAula", HSAulaDAO.class);

    private int rowId;
    private String paramDes;
    private String mpDes;
    private String auxDes;
    private int rowCapacidad;
    private int auxCapacidad;
    private int rowLocalId;
    private String rowLocalValue;
    private int rowPabellonId = 0;
    private String rowPabellonValue;
    private int auxLocalId;
    private int paramLocalId;
    private int paramPabellonId;
    private List<MbAula> listaAula;
    private boolean editable = false;
    private boolean view = true;
    private boolean visible = false;
    private SelectItem[] comboLocales;
    private List<SelectItem> comboPabellones;
    private SelectItem[] paramLocales;
    private List<SelectItem> paramPabellones;
    private String oncomplete;
    private boolean editable2 = false;

    @PostConstruct
    public void init() {
        HSLocalDAO dao = ServiceFinder.findBean("springHibernateDaoLocal", HSLocalDAO.class);
        SelectItem[] locales = dao.seleccionarLocal("").stream()
                .map(l -> new SelectItem(l.getId(), l.getLocDes()))
                .toArray(SelectItem[]::new);
        setParamLocales(locales);
        setParamPabellones(new ArrayList<>());

        setComboLocales(locales);
        setComboPabellones(new ArrayList<>());
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setAuxDes("");
        this.setMpDes("");
        this.setRowCapacidad(0);
        this.setAuxCapacidad(0);
        this.setRowPabellonId(0);
        this.setRowLocalId(0);
        this.setAuxLocalId(0);
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean getView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<MbAula> getListaAula() {
        return listaAula;
    }

    public void setListaAula(List<MbAula> listaAula) {
        this.listaAula = listaAula;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getParamDes() {
        return paramDes;
    }

    public void setParamDes(String paramDes) {
        this.paramDes = paramDes;
    }

    public int getRowCapacidad() {
        return rowCapacidad;
    }

    public void setRowCapacidad(int rowCapacidad) {
        this.rowCapacidad = rowCapacidad;
    }

    public int getRowLocalId() {
        return rowLocalId;
    }

    public void setRowLocalId(int rowLocalId) {
        this.rowLocalId = rowLocalId;
    }

    public String getRowLocalValue() {
        return rowLocalValue;
    }

    public void setRowLocalValue(String rowLocalValue) {
        this.rowLocalValue = rowLocalValue;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public boolean isEditable2() {
        return editable2;
    }

    public void setEditable2(boolean editable2) {
        this.editable2 = editable2;
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for '{}' | {} | {}", event.getPhaseId(), this.paramDes, this.paramLocalId, this.paramPabellonId);

        loadData(event);
    }

    private void loadData(ActionEvent event) {
        listaAula = new ArrayList<>();
        List<AcAula> lista = daoAula.seleccionarAula(this.getParamDes(), this.getParamLocalId(), this.getParamPabellonId());
        for (AcAula a : lista) {
            var r = new MbAula();
            r.setRowId(a.getId());
            r.setParamDes(a.getAulDes());
            r.setRowCapacidad(a.getAulCap());
            r.setRowPabellonId(a.getPab().getId());
            r.setRowPabellonValue(a.getPab().getPabNombre());
            r.setRowLocalId(a.getPab().getLoc().getId());
            r.setRowLocalValue(a.getPab().getLoc().getLocDes());
            r.setComboLocales(this.comboLocales);
            r.setComboPabellones(new ArrayList<>());
            r.setearLocal(event);
            listaAula.add(r);
        }
        LOG.info("Total of classrooms {}", listaAula.size());
        this.setListaAula(listaAula);
    }

    public String getAuxDes() {
        return auxDes;
    }

    public void setAuxDes(String auxDes) {
        this.auxDes = auxDes;
    }

    public int getAuxCapacidad() {
        return auxCapacidad;
    }

    public void setAuxCapacidad(int auxCapacidad) {
        this.auxCapacidad = auxCapacidad;
    }

    public int getAuxLocalId() {
        return auxLocalId;
    }

    public void setAuxLocalId(int auxLocalId) {
        this.auxLocalId = auxLocalId;
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setParamDes(this.getAuxDes());
        this.setRowCapacidad(this.getAuxCapacidad());
        this.setRowLocalId(this.getAuxLocalId());
        this.setView(true);
        this.setEditable(false);
        this.setEditable2(false);
        this.setVisible(false);
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setAuxDes(this.getParamDes());
        this.setAuxCapacidad(this.getRowCapacidad());
        this.setAuxLocalId(this.getRowLocalId());
        this.setView(false);
        this.setEditable(true);
        this.setEditable2(true);
        this.setVisible(true);
    }

    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("id");
        UIParameter descripcion = (UIParameter) event.getComponent().findComponent("descripcion");
        UIParameter capacidad = (UIParameter) event.getComponent().findComponent("capacidad");
        UIParameter pabellon = (UIParameter) event.getComponent().findComponent("pabellon");

        if ((descripcion.getValue().toString().trim()).length() == 0 || Integer.parseInt(capacidad.getValue().toString()) <= 0 || Integer.parseInt(pabellon.getValue().toString()) == 0) {
            this.setOncomplete("javascript:alert('Ingrese Datos.')");
        } else {
            var aula = new AcAula();
            aula.setId(Integer.parseInt(id.getValue().toString()));
            aula.setAulDes(descripcion.getValue().toString());
            aula.setAulCap(Integer.parseInt(capacidad.getValue().toString()));
            var pab = new AcPabellon();
            pab.setId(Integer.parseInt(pabellon.getValue().toString()));
            aula.setPab(pab);
            aula.setAulActivo("1");
            daoAula.actualizarAula(aula);
            this.setView(true);
            this.setEditable(false);
            this.setEditable2(false);
            this.setVisible(false);
            this.setOncomplete("javascript:alert('Se Actualizaron los datos del Aula correctamente.')");
        }
    }

    public void eliminarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter delete = (UIParameter) event.getComponent().findComponent("delete");
        var id = Integer.valueOf(delete.getValue().toString());

        daoAula.eliminarAula(id);
        loadData(event);
    }

    public String getMpDes() {
        return mpDes;
    }

    public void setMpDes(String mpDes) {
        this.mpDes = mpDes;
    }

    public List<SelectItem> getComboPabellones() {
        return comboPabellones;
    }

    public void setComboPabellones(List<SelectItem> comboPabellones) {
        this.comboPabellones = comboPabellones;
    }

    public SelectItem[] getComboLocales() {
        return comboLocales;
    }

    public void setComboLocales(SelectItem[] comboLocales) {
        this.comboLocales = comboLocales;
    }

    public void grabar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());

        if (this.getMpDes().trim().length() == 0 ||
                this.getRowCapacidad() <= 0 ||
                this.getRowPabellonId() == 0) {
            this.setOncomplete("javascript:alert('Ingrese todos los datos Requeridos.')");
        } else {
            var aula = new AcAula();
            aula.setId(this.getRowId());
            aula.setAulDes(this.getMpDes());
            aula.setAulCap(this.getRowCapacidad());
            var pab = new AcPabellon();
            pab.setId(this.getRowPabellonId());
            aula.setPab(pab);
            aula.setAulActivo("1");
            this.setRowId(daoAula.insertarAula(aula));
            LOG.info("Aula saved {}", rowId);
            this.setOncomplete("javascript:alert('Se creo un Aula correctamente.');Richfaces.hideModalPanel('mp')");
        }
    }

    public int getRowPabellonId() {
        return rowPabellonId;
    }

    public void setRowPabellonId(int rowPabellonId) {
        this.rowPabellonId = rowPabellonId;
    }

    public String getRowPabellonValue() {
        return rowPabellonValue;
    }

    public void setRowPabellonValue(String rowPabellonValue) {
        this.rowPabellonValue = rowPabellonValue;
    }

    public void setearLocal(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.comboPabellones.clear();
        if (rowLocalId != 0) {
            HSPabellonDAO dao = ServiceFinder.findBean("springHibernateDaoPabellon", HSPabellonDAO.class);
            this.comboPabellones.addAll(dao.seleccionarPabellon("", this.getRowLocalId()).stream()
                    .map(p -> new SelectItem(p.getId(), p.getPabNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public void setearParamLocal(ActionEvent event) {
        LOG.debug("[{}] for local {}", event.getPhaseId(), paramLocalId);
        this.paramPabellones.clear();
        if (this.paramLocalId != 0) {
            HSPabellonDAO dao = ServiceFinder.findBean("springHibernateDaoPabellon", HSPabellonDAO.class);

            this.paramPabellones.addAll(dao.seleccionarPabellon("", this.getParamLocalId()).stream()
                    .map(p -> new SelectItem(p.getId(), p.getPabNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public int getParamLocalId() {
        return paramLocalId;
    }

    public void setParamLocalId(int paramLocalId) {
        this.paramLocalId = paramLocalId;
    }

    public int getParamPabellonId() {
        return paramPabellonId;
    }

    public void setParamPabellonId(int paramPabellonId) {
        this.paramPabellonId = paramPabellonId;
    }

    public SelectItem[] getParamLocales() {
        return paramLocales;
    }

    public void setParamLocales(SelectItem[] paramLocales) {
        this.paramLocales = paramLocales;
    }

    public List<SelectItem> getParamPabellones() {
        return paramPabellones;
    }

    public void setParamPabellones(List<SelectItem> paramPabellones) {
        this.paramPabellones = paramPabellones;
    }
}
