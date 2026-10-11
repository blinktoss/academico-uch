package net.uch.cursolibre.jsf;

import net.uch.academica.jsf.beans.ObjUpdate;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.AbstractBaseMbEdicionEstructuraPagoBloque;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.HSAlumnoTarifaCLDAO;
import net.uch.cursolibre.dao.HSSeccionCLDAO;
import net.uch.cursolibre.dao.HSTallerAperturadoDAO;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cesardl
 */
public class MbEdicionEstructPagoBloqueCL extends AbstractBaseMbEdicionEstructuraPagoBloque {

    private static final Logger LOG = LoggerFactory.getLogger(MbEdicionEstructPagoBloqueCL.class);

    private int paramSecId;
    private int paramTalapeId;
    private List<SelectItem> paramSecciones;
    private SelectItem[] paramTalapes;

    @Override
    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSTallerAperturadoDAO dao = ServiceFinder.findBean("springHibernateDaoCLTallerAperturado", HSTallerAperturadoDAO.class);
        setParamTalapes(dao.seleccionarTalleresAperturados().stream()
                .map(talape -> new SelectItem(talape.getTalapeId(), talape.getTalapeDescripcion()))
                .toArray(SelectItem[]::new));

        setParamSecciones(new ArrayList<>());

        super.init();
    }

    public int getParamTalapeId() {
        return paramTalapeId;
    }

    public void setParamTalapeId(int paramTalapeId) {
        this.paramTalapeId = paramTalapeId;
    }

    public SelectItem[] getParamTalapes() {
        return paramTalapes;
    }

    public void setParamTalapes(SelectItem[] paramTalapes) {
        this.paramTalapes = paramTalapes;
    }

    public void parametroSeccion(ActionEvent event) {
        LOG.debug("[{}] load seccions for talape {}", event.getPhaseId(), this.getParamTalapeId());
        paramSecciones.clear();
        if (this.getParamTalapeId() != 0) {
            HSSeccionCLDAO dao = ServiceFinder.findBean("springHibernateDaoCLSeccion", HSSeccionCLDAO.class);
            this.paramSecciones.addAll(dao.seleccionarSecciones(this.getParamTalapeId()).stream()
                    .map(sec -> new SelectItem(sec.getSecId(), sec.getSecNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public int getParamSecId() {
        return paramSecId;
    }

    public void setParamSecId(int paramSecId) {
        this.paramSecId = paramSecId;
    }

    public List<SelectItem> getParamSecciones() {
        return paramSecciones;
    }

    public void setParamSecciones(List<SelectItem> paramSecciones) {
        this.paramSecciones = paramSecciones;
    }

    public String getSeccion() {
        for (SelectItem si : paramSecciones) {
            if (Integer.parseInt(String.valueOf(si.getValue())) == this.getParamSecId()) {
                return si.getLabel();
            }
        }
        return "";
    }

    public String getTallerAperturado() {
        for (SelectItem si : paramTalapes) {
            if (Integer.parseInt(String.valueOf(si.getValue())) == this.getParamTalapeId()) {
                return si.getLabel();
            }
        }
        return "";
    }

    public void buscar(ActionEvent event) {
        LOG.debug("[{}] Looking for EP on block, opened workshop {} | section {}", event.getPhaseId(), paramTalapeId, paramSecId);
        if (paramTalapeId == 0) {
            this.setOncomplete("alert('Debe seleccionar un taller aperturado');");
        } else if (paramSecId == 0) {
            this.setOncomplete("alert('Debe seleccionar una seccion');");
        } else if (getParamFiltro().length == 0) {
            this.setOncomplete("alert('Debe seleccionar algun campo para buscar');");
        } else if (getParamModif().length == 0) {
            this.setOncomplete("alert('Debe seleccionar algun campo para actualizar');");
        } else {
            performFillChecbox();
        }
    }

    public void actualizarEstructuras() {
        List<ObjUpdate> pRestricciones = new ArrayList<>();
        if (this.getMpBusMonto() != null && this.getMpBusMonto().trim().length() != 0) {
            pRestricciones.add(new ObjUpdate("cl_alumno_tarifa.alutar_monto", helper.aFloat(this.getMpBusMonto())));
        }
        if (this.getMpBusFechaPago() != null) {
            pRestricciones.add(new ObjUpdate("cl_alumno_tarifa.alutar_fecha_pago", helper.aDate(this.getMpBusFechaPago())));
        }
        if (this.getMpBusFechaProrroga() != null) {
            pRestricciones.add(new ObjUpdate("cl_alumno_tarifa.alutar_fecha_prorroga", helper.aDate(this.getMpBusFechaProrroga())));
        }

        List<ObjUpdate> pValores = new ArrayList<>();
        if (this.getMpModMonto() != null && this.getMpModMonto().trim().length() != 0) {
            pValores.add(new ObjUpdate("cl_alumno_tarifa.alutar_monto", helper.aFloat(this.getMpModMonto())));
        }
        if (this.getMpModFechaPago() != null) {
            pValores.add(new ObjUpdate("cl_alumno_tarifa.alutar_fecha_pago", helper.aDate(this.getMpModFechaPago())));
        }
        if (this.getMpModFechaProrroga() != null) {
            pValores.add(new ObjUpdate("cl_alumno_tarifa.alutar_fecha_prorroga", helper.aDate(this.getMpModFechaProrroga())));
        }

        HSAlumnoTarifaCLDAO dao = ServiceFinder.findBean("springHibernateDaoCLAlumnoTarifa", HSAlumnoTarifaCLDAO.class);
        int registros = dao.actualizarEstructurasPagoBloque(this.getParamSecId(), pRestricciones, pValores);

        LOG.debug("hay en total {}", registros);
        if (registros == 0) {
            this.setResultado("No se han actualizado registros.");
        } else {
            this.setResultado("Se han actualizado " + registros + " registros de estudiantes"
                    + "<br>Taller aperturado: " + getTallerAperturado()
                    + "<br>Seccion: " + getSeccion());
        }
        this.setOncomplete("Richfaces.hideModalPanel('mpBloque')");
    }
}
