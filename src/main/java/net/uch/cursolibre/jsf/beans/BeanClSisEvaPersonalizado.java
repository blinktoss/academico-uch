package net.uch.cursolibre.jsf.beans;

import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.HSSistemaEvaluacionCLDAO;
import net.uch.mapping.ClSisEvaDetalle;
import net.uch.mapping.ClSisEvaPersonalizado;
import net.uch.mapping.ClSisEvaluacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

import static net.uch.common.Constants.CAT_TIPO_CLIENTE_ALUMNO_CL;

/**
 * @author cesardl
 */
public class BeanClSisEvaPersonalizado {

    private static final Logger LOG = LoggerFactory.getLogger(BeanClSisEvaPersonalizado.class);

    private int id;
    private int idSiseva;
    private int pos;
    private String nombre;
    private int talapeId;
    private String codigo;
    private int peso;
    private String exaSemana;
    private int orden;
    private String aluTipo;
    private List<SelectItem> detalles;
    private String detallen;
    private int detalle;
    private boolean editable = false;
    private boolean view = true;
    private boolean visible = false;
    private boolean editActive = false;
    //Temporales
    private int auxDetalle;
    private String auxCodigo;
    private String auxNombre;
    private int auxPeso;
    private String auxExaSemana;
    private int auxOrden;

    public BeanClSisEvaPersonalizado() {
        this.aluTipo = CAT_TIPO_CLIENTE_ALUMNO_CL;
        this.initDetalles();
    }

    public BeanClSisEvaPersonalizado(int sisevaId) {
        this.setIdSiseva(sisevaId);
        this.aluTipo = CAT_TIPO_CLIENTE_ALUMNO_CL;
        this.initDetalles();
    }

    public BeanClSisEvaPersonalizado(ClSisEvaPersonalizado sisevaper, int pos) {
        this.setId(sisevaper.getSisevaPerId());
        this.setCodigo(sisevaper.getSisevaCodigo());
        this.setNombre(sisevaper.getSisevaPerNombre());
        this.setOrden(sisevaper.getSisevaPerOrden());
        this.setPeso(Integer.parseInt(sisevaper.getSisevaPerPeso()));
        this.setExaSemana(sisevaper.getSisevaPerExaSemana());
        this.setDetalle(sisevaper.getClSisEvaDetalle().getSisevaDetalleId());
        this.setDetallen(sisevaper.getClSisEvaDetalle().getSisevaDetalleNombre());
        this.setIdSiseva(sisevaper.getClSisEvaDetalle().getClSisEvaluacion().getSisevaId());
        this.initDetalles();
        this.setTalapeId(sisevaper.getClTallerAperturado().getTalapeId());
        this.setPos(pos);
    }

    public void limpiar() {
        this.setCodigo("");
        this.setNombre("");
        this.setPeso(0);
        this.setExaSemana("");
        this.setOrden(0);
        this.setDetalle(0);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getDetalle() {
        return detalle;
    }

    public void setDetalle(int detalle) {
        this.detalle = detalle;
    }

    public String getDetallen() {
        return detallen;
    }

    public void setDetallen(String detallen) {
        this.detallen = detallen;
    }

    public String getNombreDetalle(int detailId) {
        for (SelectItem se : detalles) {
            var value = Integer.parseInt(se.getValue().toString());
            if (value == detailId) {
                return se.getLabel();
            }
        }
        return "---";
    }

    private void initDetalles() {
        detalles = new ArrayList<>();
        if (this.getIdSiseva() != 0) {
            HSSistemaEvaluacionCLDAO dao =
                    ServiceFinder.findBean("springHibernateDaoCLSistemaEvaluacion", HSSistemaEvaluacionCLDAO.class);
            ClSisEvaluacion siseva = dao.seleccionarSistemaEvaluacion(this.getIdSiseva());

            for (ClSisEvaDetalle sisevaDet : siseva.getClSisEvaDetalles()) {
                detalles.add(new SelectItem(sisevaDet.getSisevaDetalleId(), sisevaDet.getSisevaDetalleNombre()));
            }
        }
    }

    public List<SelectItem> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<SelectItem> detalles) {
        this.detalles = detalles;
    }

    public int getIdSiseva() {
        return idSiseva;
    }

    public void setIdSiseva(int idSiseva) {
        this.idSiseva = idSiseva;
    }

    public String getAluTipo() {
        return aluTipo;
    }

    public void setAluTipo(String aluTipo) {
        this.aluTipo = aluTipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getExaSemana() {
        return exaSemana;
    }

    public void setExaSemana(String exaSemana) {
        this.exaSemana = exaSemana;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public int getTalapeId() {
        return talapeId;
    }

    public void setTalapeId(int talapeId) {
        this.talapeId = talapeId;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEditActive() {
        return editActive;
    }

    public void setEditActive(boolean editActive) {
        this.editActive = editActive;
    }

    public void edit(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.auxDetalle = this.detalle;
        this.auxCodigo = this.codigo;
        this.auxNombre = this.nombre;
        this.auxPeso = this.peso;
        this.auxExaSemana = this.exaSemana;
        this.auxOrden = this.orden;

        this.setView(false);
        this.setEditable(true);
        this.setVisible(true);
        this.setEditActive(true);
    }

    public void aceptar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.detalle != 0) {
            this.setDetallen(getNombreDetalle(this.detalle));

            this.setView(true);
            this.setEditable(false);
            this.setVisible(false);
            this.setEditActive(false);
        }
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.detalle = this.auxDetalle;
        this.codigo = this.auxCodigo;
        this.nombre = this.auxNombre;
        this.peso = this.auxPeso;
        this.exaSemana = this.auxExaSemana;
        this.orden = this.auxOrden;

        this.setView(true);
        this.setEditable(false);
        this.setVisible(false);
        this.setEditActive(false);
    }
}
