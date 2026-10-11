package net.uch.cursolibre.jsf;

import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.HSAreaDAO;
import net.uch.mapping.ClArea;
import net.uch.mapping.ClModulo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MbArea {

    private static final Logger LOG = LoggerFactory.getLogger(MbArea.class);

    private final HSAreaDAO dao = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);

    private int rowAreId;
    private String rowAreCodigo;
    private String rowAreDescripcion;
    private String paramDescripcion = "";
    private List<MbArea> areas = new ArrayList<>();
    private int mpAreId;
    private String mpAreCodigo;
    private String mpAreDescripcion;
    private String oncomplete;

    // METODOS
    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        this.setMpAreId(0);
        this.setMpAreCodigo("");
        this.setMpAreDescripcion("");
        this.setOncomplete("Richfaces.showModalPanel('mp1')");
    }

    public String seleccionar() {
        List<ClArea> a = dao.seleccionarArea(this.getParamDescripcion().trim());
        this.areas = new ArrayList<>();
        for (ClArea clArea : a) {
            var area = new MbArea();
            area.setRowAreId(clArea.getAreId());
            area.setRowAreCodigo(clArea.getAreCod());
            area.setRowAreDescripcion(clArea.getAreDescripcion());
            areas.add(area);
        }
        return "";
    }

    public void actualizarArea(ActionEvent event) {
        this.setOncomplete("");
        var idUpdate = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_update")).getValue().toString());
        ClArea area = dao.buscarArea(idUpdate);
        this.setMpAreId(area.getAreId());
        this.setMpAreCodigo(area.getAreCod());
        this.setMpAreDescripcion(area.getAreDescripcion());
        this.setOncomplete("Richfaces.showModalPanel('mp1')");
    }

    public void eliminarArea(ActionEvent event) {
        var idDelete = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_delete")).getValue().toString());
        dao.eliminarArea(idDelete);
        LOG.info("Area deleted '{}'", idDelete);
    }

    public void guardarArea(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (!this.getMpAreCodigo().trim().isEmpty() && !this.getMpAreDescripcion().trim().isEmpty()) {
            var area = new ClArea();
            if (this.getMpAreId() == 0) {
                area.setAreCod(this.getMpAreCodigo().trim().toUpperCase());
                area.setAreDescripcion(this.getMpAreDescripcion().trim().toUpperCase());
                area.setAreActivo("1");
                Set<ClModulo> mods = new LinkedHashSet<>();
                area.setClModulos(mods);

                dao.insertarArea(area);
                LOG.info("Area saved '{}'", area.getAreId());
                this.setOncomplete("alert('Registro Satisfactorio.');Richfaces.hideModalPanel('mp1')");
            } else {
                area.setAreId(this.getMpAreId());
                area.setAreCod(this.getMpAreCodigo().trim().toUpperCase());
                area.setAreDescripcion(this.getMpAreDescripcion().trim().toUpperCase());
                area.setAreActivo("1");

                dao.actualizarArea(area);
                LOG.info("Area updated '{}'", area.getAreId());
                this.setOncomplete("alert('Actualización Satisfactoria.');Richfaces.hideModalPanel('mp1')");
            }
            this.setMpAreId(area.getAreId());
        } else {
            this.setOncomplete("alert('Debe ingresar un codigo y una descripcion.')");
        }
    }

    // GETTERS AND SETTERS
    public int getRowAreId() {
        return rowAreId;
    }

    public void setRowAreId(int rowAreId) {
        this.rowAreId = rowAreId;
    }

    public String getRowAreCodigo() {
        return rowAreCodigo;
    }

    public void setRowAreCodigo(String rowAreCodigo) {
        this.rowAreCodigo = rowAreCodigo;
    }

    public String getRowAreDescripcion() {
        return rowAreDescripcion;
    }

    public void setRowAreDescripcion(String rowAreDescripcion) {
        this.rowAreDescripcion = rowAreDescripcion;
    }

    public String getParamDescripcion() {
        return paramDescripcion;
    }

    public void setParamDescripcion(String paramDescripcion) {
        this.paramDescripcion = paramDescripcion;
    }

    public List<MbArea> getAreas() {
        return areas;
    }

    public void setAreas(List<MbArea> areas) {
        this.areas = areas;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public int getMpAreId() {
        return mpAreId;
    }

    public void setMpAreId(int mpAreId) {
        this.mpAreId = mpAreId;
    }

    public String getMpAreCodigo() {
        return mpAreCodigo;
    }

    public void setMpAreCodigo(String mpAreCodigo) {
        this.mpAreCodigo = mpAreCodigo;
    }

    public String getMpAreDescripcion() {
        return mpAreDescripcion;
    }

    public void setMpAreDescripcion(String mpAreDescripcion) {
        this.mpAreDescripcion = mpAreDescripcion;
    }
}
