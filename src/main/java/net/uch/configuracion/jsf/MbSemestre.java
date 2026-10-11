package net.uch.configuracion.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.AcSemestre;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.uch.common.Constants.ACTION_IMAGES_ACTIVAR;
import static net.uch.common.Constants.ACTION_IMAGES_DESACTIVAR;

public class MbSemestre {

    private static final Logger LOG = LoggerFactory.getLogger(MbSemestre.class);

    private final HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);

    private boolean denegado = false;
    private int rowId;
    private String paramCodigo = "";
    private String mpCodigo;
    private String paramNombre = "";
    private String mpNombre;
    private Date rowInicio;
    private Date rowFin;
    private boolean ediVigente;
    private boolean ediActual;
    private String rowVigente;
    private String rowActual;
    private List<MbSemestre> listaSemestres;
    private boolean editable = false;
    private boolean view = true;
    private boolean visible = false;
    private String codigoAux;
    private String nroPensionesAux;
    private String nombreAux;
    private Date inicioAux;
    private Date finAux;
    private boolean vigenteAux;
    private boolean actualAux;
    private int rowNroSemanas;
    private int mpNroSemanas;
    private int nroSemanaAux;
    private String rowNroPensiones;
    private String mpNroPensiones;
    private String oncomplete;
    private boolean editable2 = false;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
    }

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

    public java.util.Date getRowInicio() {
        return rowInicio;
    }

    public void setRowInicio(java.util.Date rowInicio) {
        this.rowInicio = rowInicio;
    }

    public java.util.Date getRowFin() {
        return rowFin;
    }

    public void setRowFin(java.util.Date rowFin) {
        this.rowFin = rowFin;
    }

    public boolean isEdiVigente() {
        return ediVigente;
    }

    public void setEdiVigente(boolean ediVigente) {
        this.ediVigente = ediVigente;
    }

    public List<MbSemestre> getListaSemestres() {
        return listaSemestres;
    }

    public void setListaSemestres(List<MbSemestre> listaSemestres) {
        this.listaSemestres = listaSemestres;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEdiActual() {
        return ediActual;
    }

    public void setEdiActual(boolean ediActual) {
        this.ediActual = ediActual;
    }

    public String getMpCodigo() {
        return mpCodigo;
    }

    public void setMpCodigo(String mpCodigo) {
        this.mpCodigo = mpCodigo;
    }

    public String getMpNombre() {
        return mpNombre;
    }

    public void setMpNombre(String mpNombre) {
        this.mpNombre = mpNombre;
    }

    public Date getInicioAux() {
        return inicioAux;
    }

    public void setInicioAux(Date inicioAux) {
        this.inicioAux = inicioAux;
    }

    public Date getFinAux() {
        return finAux;
    }

    public void setFinAux(Date finAux) {
        this.finAux = finAux;
    }

    public String getCodigoAux() {
        return codigoAux;
    }

    public void setCodigoAux(String codigoAux) {
        this.codigoAux = codigoAux;
    }

    public String getNombreAux() {
        return nombreAux;
    }

    public void setNombreAux(String nombreAux) {
        this.nombreAux = nombreAux;
    }

    public boolean isActualAux() {
        return actualAux;
    }

    public void setActualAux(boolean actualAux) {
        this.actualAux = actualAux;
    }

    public boolean isVigenteAux() {
        return vigenteAux;
    }

    public void setVigenteAux(boolean vigenteAux) {
        this.vigenteAux = vigenteAux;
    }

    public String getNroPensionesAux() {
        return nroPensionesAux;
    }

    public void setNroPensionesAux(String nroPensionesAux) {
        this.nroPensionesAux = nroPensionesAux;
    }

    public int getRowNroSemanas() {
        return rowNroSemanas;
    }

    public void setRowNroSemanas(int rowNroSemanas) {
        this.rowNroSemanas = rowNroSemanas;
    }

    public int getMpNroSemanas() {
        return mpNroSemanas;
    }

    public void setMpNroSemanas(int mpNroSemanas) {
        this.mpNroSemanas = mpNroSemanas;
    }

    public int getNroSemanaAux() {
        return nroSemanaAux;
    }

    public void setNroSemanaAux(int nroSemanaAux) {
        this.nroSemanaAux = nroSemanaAux;
    }

    public String getRowNroPensiones() {
        return rowNroPensiones;
    }

    public void setRowNroPensiones(String rowNroPensiones) {
        this.rowNroPensiones = rowNroPensiones;
    }

    public String getMpNroPensiones() {
        return mpNroPensiones;
    }

    public void setMpNroPensiones(String mpNroPensiones) {
        this.mpNroPensiones = mpNroPensiones;
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

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setRowInicio(this.getInicioAux());
        this.setRowFin(this.getFinAux());
        this.setParamCodigo(this.getCodigoAux());
        this.setParamNombre(this.getNombreAux());
        this.setEdiVigente(this.isVigenteAux());
        this.setRowNroPensiones(this.getNroPensionesAux());
        this.setRowNroSemanas(this.getNroSemanaAux());
        this.setEdiActual(this.isActualAux());
        this.setView(true);
        this.setEditable(false);
        this.setEditable2(false);
        this.setVisible(false);
    }

    public void eliminarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var id = ((UIParameter) event.getComponent().findComponent("delete")).getValue().toString();

        daoSemestre.eliminarSemestre(id);
    }

    public void seleccionar() {
        LOG.debug("Looking for '{}' | '{}'", this.paramCodigo, this.paramNombre);
        listaSemestres = new ArrayList<>();
        List<AcSemestre> lista = daoSemestre.seleccionarSemestre(this.getParamCodigo(), this.getParamNombre());
        MbSemestre bS;
        for (AcSemestre acSemestre : lista) {
            bS = new MbSemestre();
            bS.setRowId(acSemestre.getId());
            bS.setParamCodigo(acSemestre.getSemCodigo());
            bS.setParamNombre(acSemestre.getSemNombre());
            bS.setRowInicio(acSemestre.getSemFechaInicio());
            bS.setRowFin(acSemestre.getSemFechaFin());
            bS.setRowNroSemanas(acSemestre.getSemNumSemanas());
            bS.setRowNroPensiones(acSemestre.getSemPensiones());
            if (acSemestre.getSemActual().equals("1")) {
                bS.setEdiVigente(true);
                bS.setRowVigente(ACTION_IMAGES_ACTIVAR);
            } else {
                bS.setEdiVigente(false);
                bS.setRowVigente(ACTION_IMAGES_DESACTIVAR);
            }
            if (acSemestre.getSemVigente().equals("1")) {
                bS.setEdiActual(true);
                bS.setRowActual(ACTION_IMAGES_ACTIVAR);
            } else {
                bS.setEdiActual(false);
                bS.setRowActual(ACTION_IMAGES_DESACTIVAR);
            }
            listaSemestres.add(bS);
        }
        this.setListaSemestres(listaSemestres);
    }

    public void openModalForSemestre(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.mpCodigo = "";
        this.mpNombre = "";
        this.rowInicio = null;
        this.rowFin = null;
        this.ediVigente = true;
        this.ediActual = true;
        this.mpNroSemanas = 0;
        this.mpNroPensiones = "";
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setInicioAux(this.getRowInicio());
        this.setFinAux(this.getRowFin());
        this.setCodigoAux(this.getParamCodigo());
        this.setNombreAux(this.getParamNombre());
        this.setNroSemanaAux(this.getRowNroSemanas());
        this.setVigenteAux(this.isEdiVigente());
        this.setActualAux(this.isEdiActual());
        this.setNroPensionesAux(this.getRowNroPensiones());
        this.setView(false);
        this.setEditable(true);
        this.setEditable2(true);
        this.setVisible(true);
    }

    public void editar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        denegado = false;
        List<AcSemestre> lista = daoSemestre.seleccionarSemestre("", "");
        UIParameter vigente = (UIParameter) event.getComponent().findComponent("vigente");
        UIParameter actual = (UIParameter) event.getComponent().findComponent("actual");
        UIParameter id = (UIParameter) event.getComponent().findComponent("id");
        if (Boolean.parseBoolean(vigente.getValue().toString())) {
            for (AcSemestre acSemestre : lista) {
                if (acSemestre.getSemActual().equals("1") && acSemestre.getId() != Integer.parseInt(id.getValue().toString())) {
                    denegado = true;
                    break;
                }
            }
        }
        if (denegado) {
            this.setOncomplete("alert('Solo puede existir un semestre actual')");
        } else {
            UIParameter codigo = (UIParameter) event.getComponent().findComponent("codigo");
            UIParameter nombre = (UIParameter) event.getComponent().findComponent("nombre");
            UIParameter semanas = (UIParameter) event.getComponent().findComponent("semanas");
            UIParameter inicio = (UIParameter) event.getComponent().findComponent("inicio");
            UIParameter fin = (UIParameter) event.getComponent().findComponent("fin");
            UIParameter pensiones = (UIParameter) event.getComponent().findComponent("pensiones");

            if (codigo.getValue().toString().trim().length() == 0 ||
                    nombre.getValue().toString().trim().length() == 0 ||
                    Integer.parseInt(semanas.getValue().toString()) <= 0 ||
                    inicio.getValue() == null ||
                    fin.getValue() == null ||
                    pensiones.getValue().toString().trim().isEmpty()) {
                this.setOncomplete("alert('Ingrese Datos.')");
            } else {
                var semestre = new AcSemestre();
                semestre.setId(Integer.parseInt(id.getValue().toString()));
                semestre.setSemCodigo(codigo.getValue().toString());
                semestre.setSemNombre(nombre.getValue().toString());
                if (Boolean.parseBoolean(vigente.getValue().toString())) {
                    semestre.setSemActual("1");
                    this.setRowVigente(ACTION_IMAGES_ACTIVAR);
                } else {
                    semestre.setSemActual("0");
                    this.setRowVigente(ACTION_IMAGES_DESACTIVAR);
                }
                semestre.setSemFechaInicio((Date) inicio.getValue());
                semestre.setSemFechaFin((Date) fin.getValue());
                if (Boolean.parseBoolean(actual.getValue().toString())) {
                    semestre.setSemVigente("1");
                    this.setRowActual(ACTION_IMAGES_ACTIVAR);
                } else {
                    semestre.setSemVigente("0");
                    this.setRowActual(ACTION_IMAGES_DESACTIVAR);
                }
                semestre.setSemNumSemanas(Integer.parseInt(semanas.getValue().toString()));
                semestre.setSemPensiones(pensiones.getValue().toString());
                semestre.setSemActivo("1");
                daoSemestre.actualizarSemestre(semestre);
                this.setView(true);
                this.setEditable(false);
                this.setEditable2(false);
                this.setVisible(false);
                this.setOncomplete("alert('Se Actualizaron los datos del Semestre correctamente.')");
            }
        }
    }

    public void grabar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        denegado = false;
        if (this.getMpCodigo().trim().length() == 0 ||
                this.getMpNombre().trim().length() == 0 ||
                this.getRowInicio() == null ||
                this.getRowFin() == null ||
                this.getMpNroSemanas() <= 0 ||
                this.getMpNroPensiones().trim().length() <= 0) {
            this.setOncomplete("alert('Ingrese todos los datos Requeridos.')");
        } else {
            List<AcSemestre> lista = daoSemestre.seleccionarSemestre("", "");
            if (this.isEdiVigente()) {
                for (AcSemestre acSemestre : lista) {
                    if (acSemestre.getSemActual().equals("1")) {
                        denegado = true;
                        break;
                    }
                }
            }
            if (denegado) {
                LOG.warn("There is already a current semester");
                this.setOncomplete("alert('Solo puede existir un semestre actual')");
            } else {
                var semestre = new AcSemestre();
                semestre.setSemCodigo(this.getMpCodigo());
                semestre.setSemNombre(this.getMpNombre());
                semestre.setSemFechaInicio(this.getRowInicio());
                semestre.setSemFechaFin(this.getRowFin());
                semestre.setSemNumSemanas(this.getMpNroSemanas());
                if (this.isEdiVigente()) {
                    semestre.setSemActual("1");
                } else {
                    semestre.setSemActual("0");
                }
                semestre.setSemPensiones(this.getMpNroPensiones());

                if (this.isEdiActual()) {
                    semestre.setSemVigente("1");
                } else {
                    semestre.setSemVigente("0");
                }
                semestre.setSemActivo("1");
                rowId = daoSemestre.insertarSemestre(semestre);
                LOG.info("Semestre saved {}", rowId);
                this.setOncomplete("alert('Se creo un Semestre correctamente.');Richfaces.hideModalPanel('mp')");
            }
        }
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

    public String getRowVigente() {
        return rowVigente;
    }

    public void setRowVigente(String rowVigente) {
        this.rowVigente = rowVigente;
    }

    public String getRowActual() {
        return rowActual;
    }

    public void setRowActual(String rowActual) {
        this.rowActual = rowActual;
    }
}
