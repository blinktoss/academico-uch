package net.uch.cursolibre.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.HSAreaDAO;
import net.uch.cursolibre.dao.HSCursoCLDAO;
import net.uch.cursolibre.dao.HSModuloDAO;
import net.uch.mapping.ClArea;
import net.uch.mapping.ClCurso;
import net.uch.mapping.ClModulo;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.*;

import static net.uch.common.Constants.ACTION_IMAGES_DOWN;
import static net.uch.common.Constants.ACTION_IMAGES_UP;

public class MbModulo {

    private static final Logger LOG = LoggerFactory.getLogger(MbModulo.class);

    private final HSAreaDAO daoArea = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);
    private final HSModuloDAO daoModulo = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
    private final HSCursoCLDAO daoCurso = ServiceFinder.findBean("springHibernateDaoCLCurso", HSCursoCLDAO.class);

    int cont1 = 0;
    private int rowModId;
    private String rowModCodigo;
    private String rowModDescripcion;
    private String rowAreDescripcion;
    private SelectItem[] comboAreas;
    private int paramAreaId;
    private String paramDescripcion = "";
    private List<MbModulo> modulos = new ArrayList<>();
    private List<MbModulo> cursos = new ArrayList<>();
    private String oncomplete;
    private int mpModId;
    private String mpModCodigo;
    private String mpModDescripcion;
    private Date mpModCreacion;
    private int mpAreaId;
    //curso
    private int rsbCurId;
    private String rsbCurCodigo;
    private String rsbCurNombre;
    private boolean verDetalle;
    private String imagenDetalle;
    private String tituloDetalle;
    private int mpCurId;
    private String mpCurCodigo;
    private String mpCurNombre;
    private List<MbModulo> mpCursos = new ArrayList<>();
    private String message;
    private int edCurId;
    private String edCurCodigo;
    private String edCurNombre;
    private boolean cursoVer;
    private boolean cursoEditar;
    private List<Integer> idsCurDelete = new ArrayList<>();

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        List<ClArea> areas = daoArea.seleccionarArea("");
        setComboAreas(areas.stream()
                .map(a -> new SelectItem(a.getAreId(), a.getAreDescripcion()))
                .toArray(SelectItem[]::new));
    }

    //METODOS
    public String seleccionar() {
        LOG.debug("Looking for modules of area '{}' | '{}'", this.getParamAreaId(), this.getParamDescripcion());
        this.setModulos(new ArrayList<>());
        List<ClModulo> lista = daoModulo.seleccionarModulos(this.getParamAreaId(), this.getParamDescripcion());
        for (ClModulo clModulo : lista) {
            var mod = new MbModulo();
            mod.setRowModId(clModulo.getModId());
            mod.setRowModCodigo(clModulo.getModCodigo());
            mod.setRowModDescripcion(clModulo.getModDescripcion());

            Integer areaId = clModulo.getClArea().getAreId();
            ClArea area = daoArea.buscarArea(areaId);
            mod.setRowAreDescripcion(area.getAreDescripcion());

            mod.setCursos(new ArrayList<>());
            List<ClCurso> curs = daoCurso.seleccionarCursos(clModulo.getModId());
            for (ClCurso clCurso : curs) {
                var cur = new MbModulo();
                cur.setRsbCurId(clCurso.getCurId());
                cur.setRsbCurCodigo(clCurso.getCurCodigo());
                cur.setRsbCurNombre(clCurso.getCurNombre());

                cur.setCursoVer(true);
                cur.setCursoEditar(false);

                mod.getCursos().add(cur);
            }

            mod.setVerDetalle(false);
            mod.setImagenDetalle(ACTION_IMAGES_DOWN);
            mod.setTituloDetalle("Ver Cursos");

            this.getModulos().add(mod);
        }
        return "";
    }

    public void mostrarCursos(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.isVerDetalle()) {
            this.setVerDetalle(false);
            this.setImagenDetalle(ACTION_IMAGES_DOWN);
            this.setTituloDetalle("Ver Cursos");
        } else {
            this.setVerDetalle(true);
            this.setImagenDetalle(ACTION_IMAGES_UP);
            this.setTituloDetalle("Ocultar Cursos");
        }
    }

    public void agregarCurso(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMessage("");
        if (this.getMpCurCodigo().trim().length() > 0 &&
                this.getMpCurNombre().trim().length() > 0) {
            var curso = new MbModulo();
            curso.setMpCurId(cont1);
            curso.setMpCurCodigo(this.getMpCurCodigo().trim().toUpperCase());
            curso.setMpCurNombre(this.getMpCurNombre().trim().toUpperCase());

            curso.setEdCurId(0);
            curso.setEdCurCodigo("");
            curso.setEdCurNombre("");
            curso.setCursoVer(true);
            curso.setCursoEditar(false);

            this.getMpCursos().add(curso);
            cont1--;

            this.setMpCurCodigo("");
            this.setMpCurNombre("");
        } else {
            this.setMessage("alert('Debe ingresar un codigo y un nombre para el curso.')");
        }
    }

    public void editarCurso(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setEdCurId(this.getMpCurId());
        this.setEdCurCodigo(this.getMpCurCodigo());
        this.setEdCurNombre(this.getMpCurNombre());

        this.setCursoVer(false);
        this.setCursoEditar(true);
    }

    public void cancelarCurso(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setCursoVer(true);
        this.setCursoEditar(false);
    }

    public void guardarCurso(ActionEvent event) {
        this.setMessage("");
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getEdCurCodigo().trim().length() > 0 &&
                this.getEdCurNombre().trim().length() > 0) {
            this.setMpCurId(this.getEdCurId());
            this.setMpCurCodigo(this.getEdCurCodigo().trim().toUpperCase());
            this.setMpCurNombre(this.getEdCurNombre().trim().toUpperCase());

            this.setCursoVer(true);
            this.setCursoEditar(false);
        } else {
            this.setMessage("alert('Debe ingresar un codigo y un nombre para el curso.')");
        }
    }

    public void quitarCurso(ActionEvent event) {
        this.setMessage("");
        var idCurDelete = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_cur_delete")).getValue().toString());
        LOG.debug("[{}] course to remove '{}'", event.getPhaseId(), idCurDelete);

        if (daoCurso.verificarEliminarCurso(idCurDelete)) {
            for (var i = 0; i < this.getMpCursos().size(); i++) {
                if (this.getMpCursos().get(i).getMpCurId() == idCurDelete) {
                    if (this.getMpCursos().get(i).getMpCurId() > 0) {
                        this.getIdsCurDelete().add(this.getMpCursos().get(i).getMpCurId());
                    }

                    this.getMpCursos().remove(i);
                    break;
                }
            }
        } else {
            this.setMessage("alert('El curso no se puede Eliminar debido a que posee Talleres.')");
        }
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMpModId(0);
        this.setMpModCodigo("");
        this.setMpModDescripcion("");
        this.setMpModCreacion(new Date());
        this.setMpAreaId(0);
        this.setMpCursos(new ArrayList<>());
        this.setIdsCurDelete(new ArrayList<>());
        this.cont1 = 0;
        this.setOncomplete("Richfaces.showModalPanel('mp1', {top:80})");
    }

    public void actualizarModulo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        var idUpdate = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_update")).getValue().toString());

        ClModulo modulo = daoModulo.buscarModulo(idUpdate);
        this.setMpModId(modulo.getModId());
        this.setMpModCodigo(modulo.getModCodigo());
        this.setMpModDescripcion(modulo.getModDescripcion());
        this.setMpModCreacion(modulo.getModCreacion());
        this.setMpAreaId(modulo.getClArea().getAreId());

        this.setMpCurId(0);
        this.setMpCurCodigo("");
        this.setMpCurNombre("");
        this.setMpCursos(new ArrayList<>());
        this.cont1 = 0;
        List<ClCurso> lcursos = daoCurso.seleccionarCursos(modulo.getModId());
        this.setIdsCurDelete(new ArrayList<>());
        for (ClCurso lcurso : lcursos) {
            var cur = new MbModulo();
            cur.setMpCurId(lcurso.getCurId());
            cur.setMpCurCodigo(lcurso.getCurCodigo());
            cur.setMpCurNombre(lcurso.getCurNombre());

            cur.setEdCurId(0);
            cur.setEdCurCodigo("");
            cur.setEdCurNombre("");
            cur.setCursoVer(true);
            cur.setCursoEditar(false);

            this.getMpCursos().add(cur);
        }
        this.setOncomplete("Richfaces.showModalPanel('mp1', {top:80})");
    }

    public void guardarModulo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");

        if (this.getMpAreaId() > 0 &&
                this.getMpModCodigo().trim().length() > 0 &&
                this.getMpModDescripcion().trim().length() > 0) {
            var area = new ClArea();
            area.setAreId(this.getMpAreaId());

            var modulo = new ClModulo();
            if (this.getMpModId() > 0) {
                modulo.setModId(this.getMpModId());
                modulo.setModCodigo(this.getMpModCodigo().trim().toUpperCase());
                modulo.setModDescripcion(this.getMpModDescripcion().trim().toUpperCase());
                modulo.setModCreacion(this.getMpModCreacion());
                modulo.setModActivo("1");
                modulo.setClArea(area);

                for (var i = 0; i < this.getMpCursos().size(); i++) {
                    var cur = new ClCurso();
                    cur.setCurCodigo(this.getMpCursos().get(i).getMpCurCodigo().trim().toUpperCase());
                    cur.setCurNombre(this.getMpCursos().get(i).getMpCurNombre().trim().toUpperCase());
                    cur.setCurActivo("1");
                    if (this.getMpCursos().get(i).getMpCurId() > 0) {
                        cur.setCurId(this.getMpCursos().get(i).getMpCurId());
                        cur.setClModulo(modulo);

                        daoCurso.actualizarCurso(cur);
                        LOG.info("CL Course updated {}", cur.getCurId());
                    } else {
                        cur.setClModulo(modulo);
                        cur.setClTallers(new LinkedHashSet<>());

                        daoCurso.insertarCurso(cur);
                        LOG.info("CL Course saved {}", cur.getCurId());
                    }
                }
                for (var i = 0; i < this.getIdsCurDelete().size(); i++) {
                    int curIdToDelete = this.getIdsCurDelete().get(i);
                    daoCurso.eliminarCurso(curIdToDelete);
                    LOG.info("CL Course deleted {}", curIdToDelete);
                }

                daoModulo.actualizarModulo(modulo);
                LOG.info("CL Module updated {}", modulo.getModId());
                this.setOncomplete("alert('Actualización Satisfactoria.');Richfaces.hideModalPanel('mp1')");
            } else {
                modulo.setModCodigo(this.getMpModCodigo().trim().toUpperCase());
                modulo.setModDescripcion(this.getMpModDescripcion().trim().toUpperCase());
                modulo.setModCreacion(this.getMpModCreacion());
                modulo.setModActivo("1");
                modulo.setClArea(area);
                modulo.setClEstructuraPagoses(new LinkedHashSet<>());
                modulo.setClPlancurriculars(new LinkedHashSet<>());

                if (this.getMpCursos().isEmpty()) {
                    modulo.setClCursoses(new LinkedHashSet<>());
                } else {
                    Set<ClCurso> sCursos = new LinkedHashSet<>();
                    for (var i = 0; i < this.getMpCursos().size(); i++) {
                        var cur = new ClCurso();
                        cur.setCurCodigo(this.getMpCursos().get(i).getMpCurCodigo().trim().toUpperCase());
                        cur.setCurNombre(this.getMpCursos().get(i).getMpCurNombre().trim().toUpperCase());
                        cur.setCurActivo("1");
                        cur.setClModulo(modulo);
                        cur.setClTallers(new LinkedHashSet<>());

                        sCursos.add(cur);
                    }
                    modulo.setClCursoses(sCursos);
                }

                daoModulo.insertarModulo(modulo);
                LOG.info("CL Module saved {}", modulo.getModId());
                this.setMpModId(modulo.getModId());
                this.setOncomplete("alert('Registro Satisfactorio.');Richfaces.hideModalPanel('mp1')");
            }
        } else {
            this.setOncomplete("alert('Todos los campos son obligatorios.')");
        }
    }

    public void eliminarModulo(ActionEvent event) {
        var idDelete = Integer.parseInt(((UIParameter) event.getComponent().findComponent("id_delete")).getValue().toString());
        LOG.debug("[{}] Deleting {}", event.getPhaseId(), idDelete);
        daoModulo.eliminarModulo(idDelete);
    }

    //GETTERS AND SETTERS
    public int getRowModId() {
        return rowModId;
    }

    public void setRowModId(int rowModId) {
        this.rowModId = rowModId;
    }

    public String getRowModCodigo() {
        return rowModCodigo;
    }

    public void setRowModCodigo(String rowModCodigo) {
        this.rowModCodigo = rowModCodigo;
    }

    public String getRowModDescripcion() {
        return rowModDescripcion;
    }

    public void setRowModDescripcion(String rowModDescripcion) {
        this.rowModDescripcion = rowModDescripcion;
    }

    public SelectItem[] getComboAreas() {
        return comboAreas;
    }

    public void setComboAreas(SelectItem[] comboAreas) {
        this.comboAreas = comboAreas;
    }

    public int getParamAreaId() {
        return paramAreaId;
    }

    public void setParamAreaId(int paramAreaId) {
        this.paramAreaId = paramAreaId;
    }

    public String getParamDescripcion() {
        return paramDescripcion;
    }

    public void setParamDescripcion(String paramDescripcion) {
        this.paramDescripcion = paramDescripcion;
    }

    public List<MbModulo> getModulos() {
        return modulos;
    }

    public void setModulos(List<MbModulo> modulos) {
        this.modulos = modulos;
    }

    public String getRowAreDescripcion() {
        return rowAreDescripcion;
    }

    public void setRowAreDescripcion(String rowAreDescripcion) {
        this.rowAreDescripcion = rowAreDescripcion;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public int getMpModId() {
        return mpModId;
    }

    public void setMpModId(int mpModId) {
        this.mpModId = mpModId;
    }

    public String getMpModCodigo() {
        return mpModCodigo;
    }

    public void setMpModCodigo(String mpModCodigo) {
        this.mpModCodigo = mpModCodigo;
    }

    public String getMpModDescripcion() {
        return mpModDescripcion;
    }

    public void setMpModDescripcion(String mpModDescripcion) {
        this.mpModDescripcion = mpModDescripcion;
    }

    public Date getMpModCreacion() {
        return mpModCreacion;
    }

    public void setMpModCreacion(Date mpModCreacion) {
        this.mpModCreacion = mpModCreacion;
    }

    public int getMpAreaId() {
        return mpAreaId;
    }

    public void setMpAreaId(int mpAreaId) {
        this.mpAreaId = mpAreaId;
    }

    public List<MbModulo> getCursos() {
        return cursos;
    }

    public void setCursos(List<MbModulo> cursos) {
        this.cursos = cursos;
    }

    public int getRsbCurId() {
        return rsbCurId;
    }

    public void setRsbCurId(int rsbCurId) {
        this.rsbCurId = rsbCurId;
    }

    public String getRsbCurCodigo() {
        return rsbCurCodigo;
    }

    public void setRsbCurCodigo(String rsbCurCodigo) {
        this.rsbCurCodigo = rsbCurCodigo;
    }

    public String getRsbCurNombre() {
        return rsbCurNombre;
    }

    public void setRsbCurNombre(String rsbCurNombre) {
        this.rsbCurNombre = rsbCurNombre;
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

    public int getMpCurId() {
        return mpCurId;
    }

    public void setMpCurId(int mpCurId) {
        this.mpCurId = mpCurId;
    }

    public String getMpCurCodigo() {
        return mpCurCodigo;
    }

    public void setMpCurCodigo(String mpCurCodigo) {
        this.mpCurCodigo = mpCurCodigo;
    }

    public String getMpCurNombre() {
        return mpCurNombre;
    }

    public void setMpCurNombre(String mpCurNombre) {
        this.mpCurNombre = mpCurNombre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MbModulo> getMpCursos() {
        return mpCursos;
    }

    public void setMpCursos(List<MbModulo> mpCursos) {
        this.mpCursos = mpCursos;
    }

    public boolean isCursoVer() {
        return cursoVer;
    }

    public void setCursoVer(boolean cursoVer) {
        this.cursoVer = cursoVer;
    }

    public boolean isCursoEditar() {
        return cursoEditar;
    }

    public void setCursoEditar(boolean cursoEditar) {
        this.cursoEditar = cursoEditar;
    }

    public int getEdCurId() {
        return edCurId;
    }

    public void setEdCurId(int edCurId) {
        this.edCurId = edCurId;
    }

    public String getEdCurCodigo() {
        return edCurCodigo;
    }

    public void setEdCurCodigo(String edCurCodigo) {
        this.edCurCodigo = edCurCodigo;
    }

    public String getEdCurNombre() {
        return edCurNombre;
    }

    public void setEdCurNombre(String edCurNombre) {
        this.edCurNombre = edCurNombre;
    }

    public List<Integer> getIdsCurDelete() {
        return idsCurDelete;
    }

    public void setIdsCurDelete(List<Integer> idsCurDelete) {
        this.idsCurDelete = idsCurDelete;
    }
}
