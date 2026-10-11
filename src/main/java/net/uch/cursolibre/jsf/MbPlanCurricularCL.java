package net.uch.cursolibre.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.HSAreaDAO;
import net.uch.cursolibre.dao.HSModuloDAO;
import net.uch.cursolibre.dao.HSPlanCurricularDAO;
import net.uch.mapping.ClModulo;
import net.uch.mapping.ClPlancurricular;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static net.uch.common.Constants.ACTION_IMAGES_ACTIVAR;
import static net.uch.common.Constants.ACTION_IMAGES_DESACTIVAR;

public class MbPlanCurricularCL {

    private static final Logger LOG = LoggerFactory.getLogger(MbPlanCurricularCL.class);

    private final HSPlanCurricularDAO dao = ServiceFinder.findBean("springHibernateDaoCLPlanCurricular", HSPlanCurricularDAO.class);

    //CAMPOS
    private int rowPlaId;
    private String rowPlaCodigo;
    private String rowPlaDescripcion;
    private String rowModDescripcion;
    private String rowImagenPlaActual;
    private String rowImagenPlaVigente;
    private String paramDescripcion;
    private int paramAreaId = 0;
    private SelectItem[] areas;
    private int paramModId;
    private List<SelectItem> paramModulos;
    private List<MbPlanCurricularCL> planes = new ArrayList<>();
    private String oncomplete;
    private int mpPlaId;
    private String mpPlaCodigo;
    private String mpPlaDescripcion;
    private boolean mpEditDisable;
    private int mpAreaId;
    private int mpModId;
    private List<SelectItem> mpModulos;
    private String mpPlaVigente;
    private SelectItem[] mpVigentes;
    private String mpPlaActual;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSAreaDAO daoCLArea = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);
        setAreas(daoCLArea.seleccionarArea("").stream()
                .map(a -> new SelectItem(a.getAreId(), a.getAreDescripcion()))
                .toArray(SelectItem[]::new));

        setParamModulos(new ArrayList<>());

        setMpModulos(new ArrayList<>());

        mpVigentes = new SelectItem[2];
        mpVigentes[0] = new SelectItem("1", "VIGENTE");
        mpVigentes[1] = new SelectItem("0", "NO VIGENTE");
    }

    //METODOS
    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (this.paramModId == 0) {
            this.setOncomplete("alert('Seleccione algun parametro para buscar.');");
        } else {
            this.setPlanes(new ArrayList<>());

            List<ClPlancurricular> lista = dao.seleccionarPlanCurricular(this.getParamModId(), this.getParamDescripcion().trim());
            for (ClPlancurricular clPlancurricular : lista) {
                var plan = new MbPlanCurricularCL();
                plan.setRowPlaId(clPlancurricular.getPlaId());
                plan.setRowPlaCodigo(clPlancurricular.getPlaCodigo());
                plan.setRowPlaDescripcion(clPlancurricular.getPlaDescripcion());
                plan.setRowModDescripcion(clPlancurricular.getClModulo().getModDescripcion());
                if (clPlancurricular.getPlaActual().equals("1")) {
                    plan.setRowImagenPlaActual(ACTION_IMAGES_ACTIVAR);
                } else {
                    plan.setRowImagenPlaActual(ACTION_IMAGES_DESACTIVAR);
                }
                if (clPlancurricular.getPlaVigente().equals("1")) {
                    plan.setRowImagenPlaVigente(ACTION_IMAGES_ACTIVAR);
                } else {
                    plan.setRowImagenPlaVigente(ACTION_IMAGES_DESACTIVAR);
                }
                this.getPlanes().add(plan);
            }
        }
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        this.setMpPlaId(0);
        this.setMpPlaCodigo("");
        this.setMpPlaDescripcion("");
        this.setMpAreaId(0);
        this.setMpModId(0);
        this.mpModulos.clear();
        this.setMpPlaVigente("1");
        this.setMpPlaActual("1");
        this.setMpEditDisable(false);
        this.setOncomplete("Richfaces.showModalPanel('mpUpdate', {top:80})");
    }

    public void actualizarPlanCurricular(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        var idUpdate = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_update")).getValue().toString());
        ClPlancurricular plan = dao.buscarPlanCurricular(idUpdate);
        this.setMpPlaId(plan.getPlaId());
        this.setMpPlaCodigo(plan.getPlaCodigo());
        this.setMpPlaDescripcion(plan.getPlaDescripcion());
        this.setMpAreaId(plan.getClModulo().getClArea().getAreId());
        this.setMpModId(plan.getClModulo().getModId());
        this.setMpVigentes(getMpVigentes());
        this.setMpPlaVigente(plan.getPlaVigente());
        this.setMpPlaActual(plan.getPlaActual());
        onChangeModulos(event);
        this.setMpEditDisable(true);
        this.setOncomplete("Richfaces.showModalPanel('mpUpdate', {top:80})");
    }

    public void eliminarPlanCurricular(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var idDelete = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_delete")).getValue().toString());
        dao.eliminarPlanCurricular(idDelete);

        seleccionar(event);
    }

    public void guardarPlanCurricular(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (this.getMpPlaCodigo().trim().length() > 0 &&
                this.getMpPlaDescripcion().trim().length() > 0
                && this.getMpModId() > 0) {
            var modulo = new ClModulo();
            modulo.setModId(this.getMpModId());
            var plan = new ClPlancurricular();
            if (this.getMpPlaId() > 0) {
                plan.setPlaId(this.getMpPlaId());
                plan.setPlaCodigo(this.getMpPlaCodigo().trim().toUpperCase());
                plan.setPlaDescripcion(this.getMpPlaDescripcion().trim().toUpperCase());
                plan.setClModulo(modulo);
                plan.setPlaActivo("1");
                plan.setPlaActual(this.getMpPlaActual());
                plan.setPlaVigente(this.getMpPlaVigente());

                dao.actualizarPlanCurricular(plan);
                LOG.info("CL Plan Curricular updated {}", plan.getPlaId());
                this.setOncomplete("alert('Actualización Satisfactoria.');Richfaces.hideModalPanel('mpUpdate')");
            } else {
                plan.setPlaCodigo(this.getMpPlaCodigo().trim().toUpperCase());
                plan.setPlaDescripcion(this.getMpPlaDescripcion().trim().toUpperCase());
                plan.setClModulo(modulo);
                plan.setPlaActivo("1");
                plan.setPlaActual(this.getMpPlaActual());
                plan.setPlaVigente(this.getMpPlaVigente());
                plan.setClTallers(new LinkedHashSet<>());

                dao.desactualizarPlanes(this.getMpModId());
                dao.insertarPlanCurricular(plan);
                this.setMpPlaId(plan.getPlaId());
                LOG.info("CL Plan Curricular saved {}", this.getMpPlaId());
                this.setOncomplete("alert('Registro Satisfactorio.');Richfaces.hideModalPanel('mpUpdate')");
            }
        } else {
            this.setOncomplete("alert('Todos los campos son obligatorios.')");
        }
    }

    //GETTERS AND SETTERS
    public int getRowPlaId() {
        return rowPlaId;
    }

    public void setRowPlaId(int rowPlaId) {
        this.rowPlaId = rowPlaId;
    }

    public String getRowPlaCodigo() {
        return rowPlaCodigo;
    }

    public void setRowPlaCodigo(String rowPlaCodigo) {
        this.rowPlaCodigo = rowPlaCodigo;
    }

    public String getRowPlaDescripcion() {
        return rowPlaDescripcion;
    }

    public void setRowPlaDescripcion(String rowPlaDescripcion) {
        this.rowPlaDescripcion = rowPlaDescripcion;
    }

    public String getRowModDescripcion() {
        return rowModDescripcion;
    }

    public void setRowModDescripcion(String rowModDescripcion) {
        this.rowModDescripcion = rowModDescripcion;
    }

    public String getParamDescripcion() {
        return paramDescripcion;
    }

    public void setParamDescripcion(String paramDescripcion) {
        this.paramDescripcion = paramDescripcion;
    }

    public int getParamAreaId() {
        return paramAreaId;
    }

    public void setParamAreaId(int paramAreaId) {
        this.paramAreaId = paramAreaId;
    }

    public SelectItem[] getAreas() {
        return areas;
    }

    public void setAreas(SelectItem[] areas) {
        this.areas = areas;
    }

    public void verModulos(ActionEvent event) {
        LOG.debug("[{}] for area {}", event.getPhaseId(), this.paramAreaId);
        paramModulos.clear();
        if (this.paramAreaId != 0) {
            HSModuloDAO daoCLModulo = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
            paramModulos.addAll(daoCLModulo.seleccionarModulos(this.paramAreaId, "").stream()
                    .map(m -> new SelectItem(m.getModId(), m.getModDescripcion()))
                    .collect(Collectors.toList()));
        }
    }

    public int getParamModId() {
        return paramModId;
    }

    public void setParamModId(int paramModId) {
        this.paramModId = paramModId;
    }

    public List<SelectItem> getParamModulos() {
        return paramModulos;
    }

    public void setParamModulos(List<SelectItem> paramModulos) {
        this.paramModulos = paramModulos;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getRowImagenPlaActual() {
        return rowImagenPlaActual;
    }

    public void setRowImagenPlaActual(String rowImagenPlaActual) {
        this.rowImagenPlaActual = rowImagenPlaActual;
    }

    public String getRowImagenPlaVigente() {
        return rowImagenPlaVigente;
    }

    public void setRowImagenPlaVigente(String rowImagenPlaVigente) {
        this.rowImagenPlaVigente = rowImagenPlaVigente;
    }

    public List<MbPlanCurricularCL> getPlanes() {
        return planes;
    }

    public void setPlanes(List<MbPlanCurricularCL> planes) {
        this.planes = planes;
    }

    public int getMpPlaId() {
        return mpPlaId;
    }

    public void setMpPlaId(int mpPlaId) {
        this.mpPlaId = mpPlaId;
    }

    public String getMpPlaCodigo() {
        return mpPlaCodigo;
    }

    public void setMpPlaCodigo(String mpPlaCodigo) {
        this.mpPlaCodigo = mpPlaCodigo;
    }

    public String getMpPlaDescripcion() {
        return mpPlaDescripcion;
    }

    public void setMpPlaDescripcion(String mpPlaDescripcion) {
        this.mpPlaDescripcion = mpPlaDescripcion;
    }

    public boolean isMpEditDisable() {
        return mpEditDisable;
    }

    public void setMpEditDisable(boolean mpEditDisable) {
        this.mpEditDisable = mpEditDisable;
    }

    public int getMpAreaId() {
        return mpAreaId;
    }

    public void setMpAreaId(int mpAreaId) {
        this.mpAreaId = mpAreaId;
    }

    public int getMpModId() {
        return mpModId;
    }

    public void setMpModId(int mpModId) {
        this.mpModId = mpModId;
    }

    public List<SelectItem> getMpModulos() {
        return mpModulos;
    }

    public void setMpModulos(List<SelectItem> mpModulos) {
        this.mpModulos = mpModulos;
    }

    public void onChangeModulos(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        mpModulos.clear();
        if (this.mpAreaId != 0) {
            HSModuloDAO daoCLModulo = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
            mpModulos.addAll(daoCLModulo.seleccionarModulos(this.mpAreaId, "").stream()
                    .map(m -> new SelectItem(m.getModId(), m.getModDescripcion()))
                    .collect(Collectors.toList()));
        }
    }

    public String getMpPlaVigente() {
        return mpPlaVigente;
    }

    public void setMpPlaVigente(String mpPlaVigente) {
        this.mpPlaVigente = mpPlaVigente;
    }

    public SelectItem[] getMpVigentes() {
        return mpVigentes;
    }

    public void setMpVigentes(SelectItem[] mpVigentes) {
        this.mpVigentes = mpVigentes;
    }

    public String getMpPlaActual() {
        return mpPlaActual;
    }

    public void setMpPlaActual(String mpPlaActual) {
        this.mpPlaActual = mpPlaActual;
    }
}
