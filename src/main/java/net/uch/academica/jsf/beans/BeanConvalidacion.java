package net.uch.academica.jsf.beans;

import net.uch.mapping.SpConvalidacionCursos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ActionEvent;

/**
 * @author cesardl
 */
public class BeanConvalidacion extends SpConvalidacionCursos {

    private static final Logger LOG = LoggerFactory.getLogger(BeanConvalidacion.class);

    private boolean activar;
    private boolean disable;
    private String style;

    public BeanConvalidacion(SpConvalidacionCursos convCur) {
        this.setRegActdetId(convCur.getRegActdetId());
        this.setRegSecId(convCur.getRegSecId());
        this.setRegSecNombre(convCur.getRegSecNombre());
        this.setRegPlancurCodigo(convCur.getRegPlancurCodigo());
        this.setRegCurNombre(convCur.getRegCurNombre());
        this.setRegNota(convCur.getRegNota());
    }

    public boolean isActivar() {
        return activar;
    }

    public void setActivar(boolean activar) {
        this.activar = activar;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void cambiarEstado(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.activar) {
            this.disable = false;
            this.style = "";
        } else {
            this.disable = true;
            this.style = "background: gray;";
        }
    }
}
