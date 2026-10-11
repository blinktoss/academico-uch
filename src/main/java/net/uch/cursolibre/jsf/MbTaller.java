package net.uch.cursolibre.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.*;
import net.uch.mapping.*;
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

import static net.uch.common.Constants.ACTION_IMAGES_DOWN;
import static net.uch.common.Constants.ACTION_IMAGES_UP;

public class MbTaller {

    private static final Logger LOG = LoggerFactory.getLogger(MbTaller.class);

    private final HSTallerDAO daoTaller = ServiceFinder.findBean("springHibernateDaoCLTaller", HSTallerDAO.class);

    int cont1 = 0;
    //CAMPOS
    private int subTalId;
    private String subTalCodigo;
    private String subTalDescripcion;
    private String subTalNumero;
    private int subPlaId;
    private String subPlaDescripcion;
    private boolean verDetalle;
    private String imagenDetalle;
    private String tituloDetalle;
    private int rowCurId;
    private String rowCurModulo;
    private String rowCurCodigo;
    private String rowCurNombre;
    private List<SelectItem> comboModulos;
    private SelectItem[] comboAreas;
    private int paramModId;
    private int paramAreaId;
    private List<MbTaller> cursos = new ArrayList<>();
    private List<MbTaller> talleres = new ArrayList<>();
    private String oncomplete;
    private String message;
    private List<MbTaller> mpTableTalleres = new ArrayList<>();
    private int editTalId;
    private String editTalCodigo;
    private String editTalNumero;
    private String editTalDescripcion;
    private boolean editable;
    private List<Integer> idsTalDelete = new ArrayList<>();

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        HSAreaDAO dao = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);
        List<ClArea> areas = dao.seleccionarArea("");
        setComboAreas(areas.stream()
                .map(a -> new SelectItem(a.getAreId(), a.getAreDescripcion()))
                .toArray(SelectItem[]::new));
        setComboModulos(new ArrayList<>());
    }

    //METODOS
    public String seleccionar() {
        LOG.debug("Looking for courses by module '{}'", this.getParamModId());
        if (this.getParamModId() == 0) {
            this.setCursos(new ArrayList<>());
        } else {
            HSModuloDAO daoModulo = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
            HSCursoCLDAO daoCurso = ServiceFinder.findBean("springHibernateDaoCLCurso", HSCursoCLDAO.class);
            this.setCursos(new ArrayList<>());
            List<ClCurso> listaCurso = daoCurso.seleccionarCursos(this.getParamModId());

            for (ClCurso clCurso : listaCurso) {
                var cur = new MbTaller();
                cur.setRowCurId(clCurso.getCurId());
                cur.setRowCurModulo(daoModulo.buscarModulo(clCurso.getClModulo().getModId()).getModDescripcion());
                cur.setRowCurCodigo(clCurso.getCurCodigo());
                cur.setRowCurNombre(clCurso.getCurNombre());

                cur.setTalleres(new ArrayList<>());
                List<ClTaller> listaTaller = daoTaller.seleccionarTalleres(clCurso.getCurId());
                for (ClTaller clTaller : listaTaller) {
                    var tal = new MbTaller();
                    tal.setSubTalId(clTaller.getTalId());
                    tal.setSubTalCodigo(clTaller.getTalCodigo());
                    tal.setSubTalNumero(clTaller.getTalNumero());
                    tal.setSubTalDescripcion(clTaller.getTalDescripcion());
                    tal.setRowCurId(clTaller.getClCurso().getCurId());
                    tal.setSubPlaId(clTaller.getClPlancurricular().getPlaId());
                    tal.setSubPlaDescripcion(clTaller.getClPlancurricular().getPlaDescripcion());

                    cur.getTalleres().add(tal);
                }

                cur.setVerDetalle(false);
                cur.setImagenDetalle(ACTION_IMAGES_DOWN);
                cur.setTituloDetalle("Ver Talleres");

                this.getCursos().add(cur);
            }
            LOG.info("Total of courses by area: {}", this.getCursos().size());
        }
        return "";
    }

    public void mostrarTalleres(ActionEvent event) {
        var flag = Boolean.parseBoolean(((UIParameter) event.getComponent().findComponent("flag")).getValue().toString());
        LOG.debug("[{}] Opening {}", event.getPhaseId(), flag);
        if (flag) {
            this.setVerDetalle(false);
            this.setImagenDetalle(ACTION_IMAGES_DOWN);
            this.setTituloDetalle("Ver Talleres");
        } else {
            this.setVerDetalle(true);
            this.setImagenDetalle(ACTION_IMAGES_UP);
            this.setTituloDetalle("Ocultar Talleres");
        }
    }

    public void cargarTalleres(ActionEvent event) {
        var idTaller = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_talleres")).getValue().toString());
        LOG.debug("[{}] Loading Talleres of course '{}'", event.getPhaseId(), idTaller);
        this.setOncomplete("");

        HSCursoCLDAO daoCurso = ServiceFinder.findBean("springHibernateDaoCLCurso", HSCursoCLDAO.class);
        HSPlanCurricularDAO daoPlancur = ServiceFinder.findBean("springHibernateDaoCLPlanCurricular", HSPlanCurricularDAO.class);

        ClCurso curso = daoCurso.buscarCurso(idTaller);

        this.setRowCurModulo(curso.getClModulo().getModDescripcion());
        this.setRowCurId(curso.getCurId());
        this.setRowCurCodigo(curso.getCurCodigo());
        this.setRowCurNombre(curso.getCurNombre());
        ClPlancurricular plancur = daoPlancur.buscarPlanActual(curso.getClModulo().getModId());
        this.setSubPlaId(plancur.getPlaId());
        this.setSubPlaDescripcion(plancur.getPlaDescripcion());

        this.setSubTalCodigo("");
        this.setSubTalNumero("");
        this.setSubTalDescripcion(curso.getCurNombre());
        this.setSubTalId(0);
        cont1 = 0;

        this.setIdsTalDelete(new ArrayList<>());

        this.setMpTableTalleres(new ArrayList<>());
        List<ClTaller> tmpTalleres = daoTaller.seleccionarTalleres(curso.getCurId());

        for (ClTaller taller : tmpTalleres) {
            var tal = new MbTaller();
            tal.setSubTalId(taller.getTalId());
            tal.setSubTalCodigo(taller.getTalCodigo());
            tal.setSubTalNumero(taller.getTalNumero());
            tal.setSubTalDescripcion(taller.getTalDescripcion());

            tal.setEditTalId(0);
            tal.setEditTalCodigo("");
            tal.setEditTalNumero("");
            tal.setEditTalDescripcion("");

            tal.setEditable(false);

            this.getMpTableTalleres().add(tal);
        }
        this.setOncomplete("Richfaces.showModalPanel('mpTaller')");
    }

    public void agregarTaller(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMessage("");
        if (this.getSubTalCodigo().trim().isEmpty() ||
                this.getSubTalNumero().trim().isEmpty() ||
                this.getSubTalDescripcion().trim().isEmpty()) {
            this.setMessage("alert('Todos los campos son obligatorios.')");
        } else {
            var tal = new MbTaller();
            tal.setSubTalId(cont1);
            tal.setSubTalCodigo(this.getSubTalCodigo().trim().toUpperCase());
            tal.setSubTalNumero(this.getSubTalNumero().trim().toUpperCase());
            tal.setSubTalDescripcion(this.getSubTalDescripcion().trim().toUpperCase());

            tal.setEditTalId(0);
            tal.setEditTalCodigo("");
            tal.setEditTalNumero("");
            tal.setEditTalDescripcion("");

            tal.setEditable(false);

            this.getMpTableTalleres().add(tal);
            cont1--;

            this.setSubTalCodigo("");
            this.setSubTalNumero("");
        }
    }

    public void guardarTaller(ActionEvent event) {
        LOG.debug("[{}] Saving in line", event.getPhaseId());
        this.setMessage("");

        if (this.getEditTalCodigo().trim().isEmpty() || this.getEditTalNumero().trim().isEmpty() || this.getEditTalDescripcion().trim().isEmpty()) {
            this.setMessage("alert('Debe ingresar un codigo y numero para Guardar.')");
        } else {
            this.setSubTalId(this.getEditTalId());
            this.setSubTalCodigo(this.getEditTalCodigo().trim().toUpperCase());
            this.setSubTalNumero(this.getEditTalNumero().trim().toUpperCase());
            this.setSubTalDescripcion(this.getEditTalDescripcion().trim().toUpperCase());

            this.setEditable(false);
        }
    }

    public void editarTaller(ActionEvent event) {
        LOG.debug("[{}] Editing in line", event.getPhaseId());
        this.setEditTalId(this.getSubTalId());
        this.setEditTalCodigo(this.getSubTalCodigo());
        this.setEditTalNumero(this.getSubTalNumero());
        this.setEditTalDescripcion(this.getSubTalDescripcion());

        this.setEditable(true);
    }

    public void cancelarTaller(ActionEvent event) {
        LOG.debug("[{}] Canceling in line", event.getPhaseId());
        this.setEditable(false);
    }

    public void eliminarTaller(ActionEvent event) {
        this.setMessage("");
        var idTalDelete = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_tal_delete")).getValue().toString());
        LOG.debug("[{}] Preparing workshop to delete '{}'", event.getPhaseId(), idTalDelete);
        if (daoTaller.tieneTalleresAperturados(idTalDelete)) {
            this.setMessage("alert('Este Taller posee talleres Aperturados.')");
        } else {
            List<MbTaller> workshops = this.getMpTableTalleres();
            for (var i = 0; i < workshops.size(); i++) {
                MbTaller w = workshops.get(i);
                if (w.getSubTalId() == idTalDelete) {
                    if (w.getSubTalId() > 0) {
                        this.getIdsTalDelete().add(w.getSubTalId());
                    }

                    workshops.remove(i);
                    break;
                }
            }
        }
    }

    public void guardarTalleres(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        List<MbTaller> workshops = this.getMpTableTalleres();
        if (!workshops.isEmpty()) {
            for (MbTaller workshop : workshops) {
                var tal = new ClTaller();
                tal.setTalCodigo(workshop.getSubTalCodigo().trim().toUpperCase());
                tal.setTalNumero(workshop.getSubTalNumero().trim().toUpperCase());
                tal.setTalDescripcion(workshop.getSubTalDescripcion().trim().toUpperCase());
                var plan = new ClPlancurricular();
                plan.setPlaId(this.getSubPlaId());
                tal.setClPlancurricular(plan);
                var cur = new ClCurso();
                cur.setCurId(this.getRowCurId());
                tal.setClCurso(cur);
                tal.setTalActivo("1");

                if (workshop.getSubTalId() > 0) {
                    tal.setTalId(workshop.getSubTalId());

                    daoTaller.actualizarTaller(tal);
                    LOG.info("Workshop updated {}", tal.getTalId());
                } else {
                    tal.setClTallerAperturados(new LinkedHashSet<>());

                    daoTaller.insertarTaller(tal);
                    LOG.info("Workshop saved {}", tal.getTalId());
                }
            }
            if (!this.getIdsTalDelete().isEmpty()) {
                int r = daoTaller.eliminarTalleres(this.getIdsTalDelete());
                LOG.info("Total workshops deleted: {}", r);
            }
            this.setOncomplete("alert('Cambios guardados.');Richfaces.hideModalPanel('mpTaller')");
        } else if (!this.getIdsTalDelete().isEmpty()) {
            int r = daoTaller.eliminarTalleres(this.getIdsTalDelete());
            LOG.info("Total workshops deleted: {}", r);
            this.setOncomplete("alert('Cambios guardados.');Richfaces.hideModalPanel('mpTaller')");
        } else {
            this.setOncomplete("alert('Ningun Cambio efectuado.');Richfaces.hideModalPanel('mpTaller')");
        }
    }

    //GETTERS AND SETTERS
    public int getSubTalId() {
        return subTalId;
    }

    public void setSubTalId(int subTalId) {
        this.subTalId = subTalId;
    }

    public String getSubTalCodigo() {
        return subTalCodigo;
    }

    public void setSubTalCodigo(String subTalCodigo) {
        this.subTalCodigo = subTalCodigo;
    }

    public String getSubTalNumero() {
        return subTalNumero;
    }

    public void setSubTalNumero(String subTalNumero) {
        this.subTalNumero = subTalNumero;
    }

    public int getSubPlaId() {
        return subPlaId;
    }

    public void setSubPlaId(int subPlaId) {
        this.subPlaId = subPlaId;
    }

    public String getSubPlaDescripcion() {
        return subPlaDescripcion;
    }

    public void setSubPlaDescripcion(String subPlaDescripcion) {
        this.subPlaDescripcion = subPlaDescripcion;
    }

    public SelectItem[] getComboAreas() {
        return comboAreas;
    }

    public void setComboAreas(SelectItem[] comboAreas) {
        this.comboAreas = comboAreas;
    }

    public void verModulos(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        comboModulos.clear();
        if (paramAreaId != 0) {
            HSModuloDAO dao = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
            List<ClModulo> modulos = dao.seleccionarModulos(this.paramAreaId, "");
            comboModulos.addAll(modulos.stream()
                    .map(m -> new SelectItem(m.getModId(), m.getModDescripcion()))
                    .collect(Collectors.toList()));
        }
    }

    public List<SelectItem> getComboModulos() {
        return comboModulos;
    }

    public void setComboModulos(List<SelectItem> comboModulos) {
        this.comboModulos = comboModulos;
    }

    public int getParamAreaId() {
        return paramAreaId;
    }

    public void setParamAreaId(int paramAreaId) {
        this.paramAreaId = paramAreaId;
    }

    public int getParamModId() {
        return paramModId;
    }

    public void setParamModId(int paramModId) {
        this.paramModId = paramModId;
    }

    public int getRowCurId() {
        return rowCurId;
    }

    public void setRowCurId(int rowCurId) {
        this.rowCurId = rowCurId;
    }

    public String getRowCurModulo() {
        return rowCurModulo;
    }

    public void setRowCurModulo(String rowCurModulo) {
        this.rowCurModulo = rowCurModulo;
    }

    public String getRowCurCodigo() {
        return rowCurCodigo;
    }

    public void setRowCurCodigo(String rowCurCodigo) {
        this.rowCurCodigo = rowCurCodigo;
    }

    public String getRowCurNombre() {
        return rowCurNombre;
    }

    public void setRowCurNombre(String rowCurNombre) {
        this.rowCurNombre = rowCurNombre;
    }

    public List<MbTaller> getCursos() {
        return cursos;
    }

    public void setCursos(List<MbTaller> cursos) {
        this.cursos = cursos;
    }

    public List<MbTaller> getTalleres() {
        return talleres;
    }

    public void setTalleres(List<MbTaller> talleres) {
        this.talleres = talleres;
    }

    public boolean isVerDetalle() {
        return verDetalle;
    }

    public void setVerDetalle(boolean verDetalle) {
        this.verDetalle = verDetalle;
    }

    public String getImagenDetalle() {
        return imagenDetalle;
    }

    public void setImagenDetalle(String imagenDetalle) {
        this.imagenDetalle = imagenDetalle;
    }

    public String getTituloDetalle() {
        return tituloDetalle;
    }

    public void setTituloDetalle(String tituloDetalle) {
        this.tituloDetalle = tituloDetalle;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MbTaller> getMpTableTalleres() {
        return mpTableTalleres;
    }

    public void setMpTableTalleres(List<MbTaller> mpTableTalleres) {
        this.mpTableTalleres = mpTableTalleres;
    }

    public int getEditTalId() {
        return editTalId;
    }

    public void setEditTalId(int editTalId) {
        this.editTalId = editTalId;
    }

    public String getEditTalCodigo() {
        return editTalCodigo;
    }

    public void setEditTalCodigo(String editTalCodigo) {
        this.editTalCodigo = editTalCodigo;
    }

    public String getEditTalNumero() {
        return editTalNumero;
    }

    public void setEditTalNumero(String editTalNumero) {
        this.editTalNumero = editTalNumero;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public List<Integer> getIdsTalDelete() {
        return idsTalDelete;
    }

    public void setIdsTalDelete(List<Integer> idsTalDelete) {
        this.idsTalDelete = idsTalDelete;
    }

    public String getSubTalDescripcion() {
        return subTalDescripcion;
    }

    public void setSubTalDescripcion(String subTalDescripcion) {
        this.subTalDescripcion = subTalDescripcion;
    }

    public String getEditTalDescripcion() {
        return editTalDescripcion;
    }

    public void setEditTalDescripcion(String editTalDescripcion) {
        this.editTalDescripcion = editTalDescripcion;
    }
}
