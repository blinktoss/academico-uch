package net.uch.configuracion.jsf;

import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.mapping.AcFacultad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MbFacultad {

    private static final Logger LOG = LoggerFactory.getLogger(MbFacultad.class);

    private final HSFacultadDAO daoFacultad = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);

    private int facultadId;
    private String paramCodigo;
    private String rowCodigo;
    private String paramNombre;
    private String rowNombre;
    private java.util.Date rowCreacion;
    private String rowResolucion;
    private List<MbFacultad> listaFacultad;
    private boolean editable;
    private boolean view = true;
    private String codigoAuxiliar;
    private String nombreAuxiliar;
    private String resolucionAuxiliar;
    private Date fechaAuxiliar;
    private String oncomplete;

    public String getParamCodigo() {
        return paramCodigo;
    }

    public void setParamCodigo(String paramCodigo) {
        this.paramCodigo = paramCodigo;
    }

    public String getParamNombre() {
        return paramNombre;
    }

    public void setParamNombre(String paramNombre) {
        this.paramNombre = paramNombre;
    }

    public java.util.Date getRowCreacion() {
        return rowCreacion;
    }

    public void setRowCreacion(java.util.Date rowCreacion) {
        this.rowCreacion = rowCreacion;
    }

    public String getRowResolucion() {
        return rowResolucion;
    }

    public void setRowResolucion(String rowResolucion) {
        this.rowResolucion = rowResolucion;
    }

    public List<MbFacultad> getListaFacultad() {
        return listaFacultad;
    }

    public void setListaFacultad(List<MbFacultad> listaFacultad) {
        this.listaFacultad = listaFacultad;
    }

    public int getFacultadId() {
        return facultadId;
    }

    public void setFacultadId(int facultadId) {
        this.facultadId = facultadId;
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

    public String getRowCodigo() {
        return rowCodigo;
    }

    public void setRowCodigo(String rowCodigo) {
        this.rowCodigo = rowCodigo;
    }

    public String getRowNombre() {
        return rowNombre;
    }

    public void setRowNombre(String rowNombre) {
        this.rowNombre = rowNombre;
    }

    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }

    public String getNombreAuxiliar() {
        return nombreAuxiliar;
    }

    public void setNombreAuxiliar(String nombreAuxiliar) {
        this.nombreAuxiliar = nombreAuxiliar;
    }

    public String getResolucionAuxiliar() {
        return resolucionAuxiliar;
    }

    public void setResolucionAuxiliar(String resolucionAuxiliar) {
        this.resolucionAuxiliar = resolucionAuxiliar;
    }

    public Date getFechaAuxiliar() {
        return fechaAuxiliar;
    }

    public void setFechaAuxiliar(Date fechaAuxiliar) {
        this.fechaAuxiliar = fechaAuxiliar;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for '{}' | {}", event.getPhaseId(), this.paramCodigo, this.paramNombre);
        List<AcFacultad> lista = daoFacultad.seleccionarFacultad(this.getParamCodigo(), this.getParamNombre());
        this.setListaFacultad(lista.stream().map(acFacultad -> {
            var bF = new MbFacultad();
            bF.setFacultadId(acFacultad.getId());
            bF.setRowCodigo(acFacultad.getFacCodigo());
            bF.setRowNombre(acFacultad.getFacNombre());
            bF.setRowCreacion(acFacultad.getFacFechacreacion());
            bF.setRowResolucion(acFacultad.getFacResolrectoral());
            return bF;
        }).collect(Collectors.toList()));
        LOG.info("Total of faculties {}", listaFacultad.size());
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.rowCodigo = "";
        this.rowNombre = "";
        this.rowCreacion = null;
        this.rowResolucion = "";
    }

    private boolean validarDatos(String codigo, String nombre, String resolucion, Date creacion) {
        return codigo.isEmpty() ||
                nombre.isEmpty() ||
                resolucion.isEmpty() ||
                creacion == null;
    }

    public void grabar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (validarDatos(this.getRowCodigo().trim(),
                this.getRowNombre().trim(),
                this.getRowResolucion().trim(),
                this.getRowCreacion())) {
            this.setOncomplete("alert('Ingrese todos los datos Requeridos.')");
        } else {
            var facultad = new AcFacultad();
            facultad.setFacCodigo(this.getRowCodigo());
            facultad.setFacNombre(this.getRowNombre());
            facultad.setFacFechacreacion(this.getRowCreacion());
            facultad.setFacResolrectoral(this.getRowResolucion());
            facultad.setFacActivo("1");
            daoFacultad.insertarFacultad(facultad);
            this.setFacultadId(facultad.getId());
            LOG.info("Faculty saved {}", this.facultadId);
            this.setOncomplete("alert('Se creo una Facultad correctamente.');Richfaces.hideModalPanel('mp')");
        }
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setCodigoAuxiliar(this.getRowCodigo());
        this.setNombreAuxiliar(this.getRowNombre());
        this.setResolucionAuxiliar(this.getRowResolucion());
        this.setFechaAuxiliar(this.getRowCreacion());
        this.setView(false);
        this.setEditable(true);
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setRowCodigo(this.getCodigoAuxiliar());
        this.setRowNombre(this.getNombreAuxiliar());
        this.setRowResolucion(this.getResolucionAuxiliar());
        this.setRowCreacion(this.getFechaAuxiliar());
        this.setView(true);
        this.setEditable(false);
    }

    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        String codigo = this.rowCodigo.trim();
        String nombre = this.rowNombre.trim();
        String resolucion = this.rowResolucion.trim();

        if (validarDatos(codigo, nombre, resolucion, this.rowCreacion)) {
            this.setOncomplete("alert('Ingrese Datos.')");
        } else {
            var facultad = new AcFacultad();
            facultad.setId(this.facultadId);
            facultad.setFacCodigo(codigo);
            facultad.setFacNombre(nombre);
            facultad.setFacResolrectoral(resolucion);
            facultad.setFacFechacreacion(this.rowCreacion);
            facultad.setFacActivo("1");
            daoFacultad.actualizarFacultad(facultad);
            LOG.info("Faculty updated {}", this.facultadId);
            this.setView(true);
            this.setEditable(false);
            this.setOncomplete("alert('Se Actualizaron los datos de la Facultad correctamente.')");
        }
    }

    public void eliminarFila(ActionEvent event) {
        UIParameter component = (UIParameter) event.getComponent().findComponent("delete");
        var id = Integer.parseInt(component.getValue().toString());
        LOG.debug("[{}] To delete {}", event.getPhaseId(), id);
        var facultad = new AcFacultad();
        facultad.setId(id);
        facultad.setFacActivo("0");
        daoFacultad.eliminarFacultad(facultad);
    }
}
