package net.uch.administrativa.jsf;

import net.uch.academica.jsf.beans.ObjUpdate;
import net.uch.administrativa.dao.HSEdicionEstructPagoDAO;
import net.uch.administrativa.service.ProrrogaService;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.AbstractBaseMbEdicionEstructuraPagoBloque;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.AcSemestre;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cesardl
 */
public class MbEdicionEstructPagoBloque extends AbstractBaseMbEdicionEstructuraPagoBloque {

    private static final Logger LOG = LoggerFactory.getLogger(MbEdicionEstructPagoBloque.class);

    private final ProrrogaService prorrogaService = ServiceFinder.findBean("prorrogaService", ProrrogaService.class);

    private int paramSemActual;
    private int paramSemIngId;
    private int paramEsp;
    private String paramSemDestino;
    private SelectItem[] paramSemIngreso;
    private SelectItem[] paramEspecialidades;

    @Override
    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSSemestreDAO daoS = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);
        setParamSemIngreso(daoS.seleccionarSemestreActivo().stream()
                .map(sem -> new SelectItem(sem.getId(), sem.getSemNombre()))
                .toArray(SelectItem[]::new));

        List<AcSemestre> lsa = daoS.seleccionarSemestreActivo();
        for (AcSemestre sem : lsa) {
            if (sem.getSemActual().equalsIgnoreCase("1")) {
                setParamSemDestino(sem.getSemNombre());
                paramSemActual = sem.getId();
            }
        }

        HSEspecialidadDAO daoE = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
        setParamEspecialidades(daoE.seleccionarEspecialidades().stream()
                .map(esp -> new SelectItem(esp.getId(), esp.getEspNombre()))
                .toArray(SelectItem[]::new));

        super.init();
    }

    public int getParamEsp() {
        return paramEsp;
    }

    public void setParamEsp(int paramEsp) {
        this.paramEsp = paramEsp;
    }

    public SelectItem[] getParamEspecialidades() {
        return paramEspecialidades;
    }

    public void setParamEspecialidades(SelectItem[] paramEspecialidades) {
        this.paramEspecialidades = paramEspecialidades;
    }

    public int getParamSemIngId() {
        return paramSemIngId;
    }

    public void setParamSemIngId(int paramSemIngId) {
        this.paramSemIngId = paramSemIngId;
    }

    public SelectItem[] getParamSemIngreso() {
        return paramSemIngreso;
    }

    public void setParamSemIngreso(SelectItem[] paramSemIngreso) {
        this.paramSemIngreso = paramSemIngreso;
    }

    public String getParamSemDestino() {
        return paramSemDestino;
    }

    public void setParamSemDestino(String paramSemDestino) {
        this.paramSemDestino = paramSemDestino;
    }

    public String getSemestreIngreso() {
        for (SelectItem si : paramSemIngreso) {
            if (Integer.parseInt(String.valueOf(si.getValue())) == this.getParamSemIngId()) {
                return si.getLabel();
            }
        }
        return "";
    }

    public String getEspecialidad() {
        for (SelectItem si : paramEspecialidades) {
            if (Integer.parseInt(String.valueOf(si.getValue())) == this.getParamEsp()) {
                return si.getLabel();
            }
        }
        return "";
    }

    public void buscar(ActionEvent event) {
        LOG.debug("[{}] Looking for EP on block, speciality {}", event.getPhaseId(), paramEsp);
        if (getParamFiltro().length == 0) {
            this.setOncomplete("alert('Debe seleccionar algun campo para buscar');");
        } else if (getParamModif().length == 0) {
            this.setOncomplete("alert('Debe seleccionar algun campo para actualizar');");
        } else if (paramEsp == 0) {
            this.setOncomplete("alert('Debe seleccionar una especialidad');");
        } else {
            performFillChecbox();
        }
    }

    public void actualizarEstructuras() {
        // Issue #33 review (F2): the commitment-date rule is the same for one row or many - only the
        // volume differs. Reject a past/today prorroga date here just like the single-grant path
        // (ProrrogaService), before any history or column is written.
        if (this.getMpModFechaProrroga() != null
                && !prorrogaService.isValidCommitmentDate(this.getMpModFechaProrroga())) {
            this.setOncomplete("alert('" + ProrrogaService.INVALID_COMMITMENT_DATE_MESSAGE + "');");
            return;
        }

        List<ObjUpdate> pRestricciones = new ArrayList<>();
        if (this.getMpBusMonto() != null && this.getMpBusMonto().trim().length() != 0) {
            pRestricciones.add(new ObjUpdate("ad_alumno_tarifa.alutar_monto", helper.aFloat(this.getMpBusMonto())));
        }
        if (this.getMpBusFechaPago() != null) {
            pRestricciones.add(new ObjUpdate("ad_alumno_tarifa.alutar_fecha_pago", helper.aDate(this.getMpBusFechaPago())));
        }
        if (this.getMpBusFechaProrroga() != null) {
            pRestricciones.add(new ObjUpdate("ad_alumno_tarifa.alutar_fecha_prorroga", helper.aDate(this.getMpBusFechaProrroga())));
        }
        if (this.getParamSemIngId() != 0) {
            pRestricciones.add(new ObjUpdate("ac_alumno.sem_id", this.getParamSemIngId()));
        }

        List<ObjUpdate> pValores = new ArrayList<>();
        if (this.getMpModMonto() != null && this.getMpModMonto().trim().length() != 0) {
            pValores.add(new ObjUpdate("ad_alumno_tarifa.alutar_monto", helper.aFloat(this.getMpModMonto())));
        }
        if (this.getMpModFechaPago() != null) {
            pValores.add(new ObjUpdate("ad_alumno_tarifa.alutar_fecha_pago", helper.aDate(this.getMpModFechaPago())));
        }
        if (this.getMpModFechaProrroga() != null) {
            pValores.add(new ObjUpdate("ad_alumno_tarifa.alutar_fecha_prorroga", helper.aDate(this.getMpModFechaProrroga())));
        }

        HSEdicionEstructPagoDAO dao = ServiceFinder.findBean("springHibernateDaoEdicionEstructura", HSEdicionEstructPagoDAO.class);

        int i;
        if (this.getMpModFechaProrroga() != null) {
            // Issue #33 (F1): a massive prorroga change must leave an audit trail AND move the deadline
            // atomically - one DAO call, one transaction, so the ad_prorroga history and the moved
            // column commit together (or neither does).
            String comentario = "From massive upload on "
                    + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            i = dao.actualizarEstructurasPagoBloqueConHistorial(this.paramSemActual, this.getParamEsp(),
                    pRestricciones, pValores, this.getMpModFechaProrroga(), comentario,
                    ServiceFinder.getLoggedUser().getRowUsuId());
        } else {
            i = dao.actualizarEstructurasPagoBloque(this.paramSemActual, this.getParamEsp(), pRestricciones, pValores);
        }
        LOG.debug("hay en total {}", i);
        if (i == 0) {
            this.setResultado("No se han actualizado registros.");
        } else {
            this.setResultado("Se han actualizado " + i + " registros de estudiantes"
                    + "<br>Especialidad: " + getEspecialidad()
                    + "<br>Semestre de ingreso: " + getSemestreIngreso());
        }
        this.setOncomplete("Richfaces.hideModalPanel('mpBloque')");
    }
}
