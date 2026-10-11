package net.uch.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created for sistema_academico on 03/09/2024.
 *
 * @author Cesardl
 */
public abstract class AbstractBaseMbEdicionEstructuraPagoBloque {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseMbEdicionEstructuraPagoBloque.class);

    protected final SystemHelper helper = ServiceFinder.findBean("helper", SystemHelper.class);

    private List<SelectItem> paramFiltros;
    private List<SelectItem> paramToModify;
    private String[] paramFiltro;
    private String[] paramModif;
    private Date mpBusFechaPago;
    private Date mpBusFechaProrroga;
    private String mpBusMonto;
    private boolean[] mpBusDisables = new boolean[3];
    private Date mpModFechaPago;
    private Date mpModFechaProrroga;
    private String mpModMonto;
    private boolean[] mpModDisable = new boolean[3];
    private String resultado;
    private String oncomplete;

    protected void init() {
        paramFiltros = new ArrayList<>();
        paramFiltros.add(new SelectItem("AlutarFechaPago", "Fecha de Pago"));
        paramFiltros.add(new SelectItem("AlutarFechaProrroga", "Fecha de Prorroga"));
        paramFiltros.add(new SelectItem("AlutarMonto", "Monto"));

        paramToModify = new ArrayList<>();
        paramToModify.add(new SelectItem("AlutarFechaPago", "Fecha de Pago"));
        paramToModify.add(new SelectItem("AlutarFechaProrroga", "Fecha de Prorroga"));
        paramToModify.add(new SelectItem("AlutarMonto", "Monto"));
    }

    public void validarActualizacion(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        //0 pago - 1 prorroga - 2 mont
        var error = false;
        for (var i = 0; i < mpBusDisables.length; i++) {
            if (!mpBusDisables[i]) {
                switch (i) {
                    case 0:
                        if (this.getMpBusFechaPago() == null) {
                            error = true;
                        }
                        break;
                    case 1:
                        if (this.getMpBusFechaProrroga() == null) {
                            error = true;
                        }
                        break;
                    case 2:
                        if (helper.aFloat(this.getMpBusMonto()) == -1) {
                            error = true;
                        }
                        break;
                    default:
                        LOG.warn("Unregistered index '{}'", i);
                }
            }
        }
        for (var i = 0; i < mpModDisable.length; i++) {
            if (!mpModDisable[i]) {
                switch (i) {
                    case 0:
                        if (this.getMpModFechaPago() == null) {
                            error = true;
                        }
                        break;
                    case 1:
                        if (this.getMpModFechaProrroga() == null) {
                            error = true;
                        }
                        break;
                    case 2:
                        if (helper.aFloat(this.getMpModMonto()) == -1) {
                            error = true;
                        }
                        break;
                    default:
                        LOG.warn("Unregistered index '{}'", i);
                }
            }
        }
        if (!error) {
            actualizarEstructuras();
        }
    }

    public abstract void buscar(ActionEvent event);

    protected void performFillChecbox() {
        LOG.debug("To filter {} | To edit {}", paramFiltro, paramModif);
        this.setMpBusFechaPago(null);
        this.setMpBusFechaProrroga(null);
        this.setMpBusMonto("");
        this.setMpModFechaPago(null);
        this.setMpModFechaProrroga(null);
        this.setMpModMonto("");
        this.setOncomplete("Richfaces.showModalPanel('mpBloque',{width:600, top:100})");

        fill(mpBusDisables, paramFiltros, paramFiltro);

        fill(mpModDisable, paramToModify, paramModif);
    }

    private void fill(boolean[] ds, List<SelectItem> sis, String[] ss) {
        Arrays.fill(ds, true);
        for (var i = 0; i < sis.size(); i++) {
            var si = String.valueOf(sis.get(i).getValue());
            var existe = false;
            var j = 0;
            var pos = 0;
            while (j < ss.length && !existe) {
                String str = ss[j];
                if (si.equalsIgnoreCase(str)) {
                    existe = true;
                    j = ss.length;
                    pos = i;
                }
                j++;
            }
            if (existe) {
                ds[pos] = false;
            }
        }
    }

    public abstract void actualizarEstructuras();

    public List<SelectItem> getParamFiltros() {
        return paramFiltros;
    }

    public void setParamFiltros(List<SelectItem> paramFiltros) {
        this.paramFiltros = paramFiltros;
    }

    public List<SelectItem> getParamToModify() {
        return paramToModify;
    }

    public void setParamToModify(List<SelectItem> paramToModify) {
        this.paramToModify = paramToModify;
    }

    public String[] getParamFiltro() {
        return paramFiltro;
    }

    public void setParamFiltro(String[] paramFiltro) {
        this.paramFiltro = paramFiltro;
    }

    public String[] getParamModif() {
        return paramModif;
    }

    public void setParamModif(String[] paramModif) {
        this.paramModif = paramModif;
    }

    public Date getMpBusFechaPago() {
        return mpBusFechaPago;
    }

    public void setMpBusFechaPago(Date mpBusFechaPago) {
        this.mpBusFechaPago = mpBusFechaPago;
    }

    public Date getMpBusFechaProrroga() {
        return mpBusFechaProrroga;
    }

    public void setMpBusFechaProrroga(Date mpBusFechaProrroga) {
        this.mpBusFechaProrroga = mpBusFechaProrroga;
    }

    public String getMpBusMonto() {
        return mpBusMonto;
    }

    public void setMpBusMonto(String mpBusMonto) {
        this.mpBusMonto = mpBusMonto;
    }

    public boolean[] getMpBusDisables() {
        return mpBusDisables;
    }

    public void setMpBusDisables(boolean[] mpBusDisables) {
        this.mpBusDisables = mpBusDisables;
    }

    public Date getMpModFechaPago() {
        return mpModFechaPago;
    }

    public void setMpModFechaPago(Date mpModFechaPago) {
        this.mpModFechaPago = mpModFechaPago;
    }

    public Date getMpModFechaProrroga() {
        return mpModFechaProrroga;
    }

    public void setMpModFechaProrroga(Date mpModFechaProrroga) {
        this.mpModFechaProrroga = mpModFechaProrroga;
    }

    public String getMpModMonto() {
        return mpModMonto;
    }

    public void setMpModMonto(String mpModMonto) {
        this.mpModMonto = mpModMonto;
    }

    public boolean[] getMpModDisable() {
        return mpModDisable;
    }

    public void setMpModDisable(boolean[] mpModDisable) {
        this.mpModDisable = mpModDisable;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }
}
