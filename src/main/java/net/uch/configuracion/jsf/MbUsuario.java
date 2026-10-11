package net.uch.configuracion.jsf;

import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSUsuarioDAO;
import net.uch.mapping.TbRoles;
import net.uch.mapping.TbUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static net.uch.common.Constants.*;

public class MbUsuario {

    private static final Logger LOG = LoggerFactory.getLogger(MbUsuario.class);

    private HSUsuarioDAO daoUsuario;

    private int rowUsuId;
    private String rowUsuario;
    private String rowPsw;
    private boolean mpVigente;
    private String rowVigente;
    private String rowRolTipo;
    private Integer mpRolId;
    private SelectItem[] mpRoles;
    private List<MbUsuario> listUsuarios;
    private String paramUsuario;
    private int paramRolId;
    private String oncomplete;
    private boolean rowLocked;
    private String rowImgCandado;
    private int rowFailedAttempts;
    private boolean superAdmin;
    private int modalUsuId;
    private String modalUsuario;
    private int modalFailedAttempts;

    @PostConstruct
    public void init() {
        daoUsuario = ServiceFinder.findBean("springHibernateDaoUsuario", HSUsuarioDAO.class);

        setMpRoles(daoUsuario.getRoles().stream()
                .map(rol -> new SelectItem(rol.getId(), rol.getRolTipo()))
                .toArray(SelectItem[]::new));
        setSuperAdmin(ServiceFinder.getLoggedUser().isSuperAdmin());
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public List<MbUsuario> getListUsuarios() {
        return listUsuarios;
    }

    public void setListUsuarios(List<MbUsuario> listUsuarios) {
        this.listUsuarios = listUsuarios;
    }

    public String getParamUsuario() {
        return paramUsuario;
    }

    public void setParamUsuario(String paramUsuario) {
        this.paramUsuario = paramUsuario;
    }

    public int getParamRolId() {
        return paramRolId;
    }

    public void setParamRolId(int paramRolId) {
        this.paramRolId = paramRolId;
    }

    public int getRowUsuId() {
        return rowUsuId;
    }

    public void setRowUsuId(int rowUsuId) {
        this.rowUsuId = rowUsuId;
    }

    public String getRowUsuario() {
        return rowUsuario;
    }

    public void setRowUsuario(String rowUsuario) {
        this.rowUsuario = rowUsuario;
    }

    public String getRowPsw() {
        return rowPsw;
    }

    public void setRowPsw(String rowPsw) {
        this.rowPsw = rowPsw;
    }

    public boolean isMpVigente() {
        return mpVigente;
    }

    public void setMpVigente(boolean mpVigente) {
        this.mpVigente = mpVigente;
    }

    public String getRowVigente() {
        return rowVigente;
    }

    public void setRowVigente(String rowVigente) {
        this.rowVigente = rowVigente;
    }

    public String getRowRolTipo() {
        return rowRolTipo;
    }

    public void setRowRolTipo(String rowRolTipo) {
        this.rowRolTipo = rowRolTipo;
    }

    public SelectItem[] getMpRoles() {
        return mpRoles;
    }

    public void setMpRoles(SelectItem[] mpRoles) {
        this.mpRoles = mpRoles;
    }

    public Integer getMpRolId() {
        return mpRolId;
    }

    public void setMpRolId(Integer mpRolId) {
        this.mpRolId = mpRolId;
    }

    public boolean isRowLocked() {
        return rowLocked;
    }

    public void setRowLocked(boolean rowLocked) {
        this.rowLocked = rowLocked;
    }

    public String getRowImgCandado() {
        return rowImgCandado;
    }

    public void setRowImgCandado(String rowImgCandado) {
        this.rowImgCandado = rowImgCandado;
    }

    public int getRowFailedAttempts() {
        return rowFailedAttempts;
    }

    public void setRowFailedAttempts(int rowFailedAttempts) {
        this.rowFailedAttempts = rowFailedAttempts;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public int getModalUsuId() {
        return modalUsuId;
    }

    public void setModalUsuId(int modalUsuId) {
        this.modalUsuId = modalUsuId;
    }

    public String getModalUsuario() {
        return modalUsuario;
    }

    public void setModalUsuario(String modalUsuario) {
        this.modalUsuario = modalUsuario;
    }

    public int getModalFailedAttempts() {
        return modalFailedAttempts;
    }

    public void setModalFailedAttempts(int modalFailedAttempts) {
        this.modalFailedAttempts = modalFailedAttempts;
    }

    public void seleccionar(ActionEvent event) {
        LOG.info("[{}] Looking for user with name starting in '{}' and role {}", event.getPhaseId(), this.paramUsuario, this.paramRolId);
        this.setListUsuarios(new ArrayList<>());
        if (!this.paramUsuario.isEmpty() || this.paramRolId != 0) {
            List<TbUsuario> ltbu = daoUsuario.getListUsuario(this.paramUsuario, this.paramRolId);

            for (TbUsuario usua : ltbu) {
                var bU = new MbUsuario();
                bU.rowUsuario = usua.getUsuUsuario();
                bU.rowPsw = usua.getUsuPassword();
                bU.setRowUsuId(usua.getId());
                bU.setMpRolId(usua.getRol().getId());
                bU.setRowRolTipo(usua.getRol().getRolTipo());
                if (usua.getUsuVigente().equals("1")) {
                    bU.setMpVigente(true);
                    bU.setRowVigente(ACTION_IMAGES_ACTIVAR);
                } else {
                    bU.setMpVigente(false);
                    bU.setRowVigente(ACTION_IMAGES_DESACTIVAR);
                }
                bU.setRowLocked("1".equals(usua.getUsuLocked()));
                bU.setRowFailedAttempts(usua.getUsuFailedAttempts() == null ? 0 : usua.getUsuFailedAttempts());
                bU.setRowImgCandado(bU.isRowLocked() ? ACTION_IMAGES_CERRADO : ACTION_IMAGES_ABIERTO);
                this.listUsuarios.add(bU);
            }
            LOG.info("Total of users {}", listUsuarios.size());
        }
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        rowUsuario = "";
        rowPsw = "";
        mpVigente = false;
        mpRolId = 0;
    }

    public boolean validar() {
        if (this.rowUsuario.trim().length() == 0) {
            return false;
        } else if (this.rowPsw.trim().length() == 0) {
            return false;
        } else return this.mpRolId != 0;
    }

    public void crearUsuario(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (validar()) {
            var usuario = new TbUsuario();
            usuario.setUsuActivo("1");
            usuario.setUsuUsuario(this.getRowUsuario());
            usuario.setUsuPassword(this.getRowPsw());
            usuario.setUsuVigente(this.isMpVigente() ? "1" : "0");
            usuario.setUsuTipo("1");
            usuario.setUsuTipoId(1);
            usuario.setUsuNivel("1");
            usuario.setUsuPsw(this.getRowPsw());
            usuario.setUsuFailedAttempts(0);
            usuario.setUsuLocked("0");
            usuario.setRol(new TbRoles(this.getMpRolId()));
            daoUsuario.crearUsuario(usuario);
            this.setOncomplete("alert('Usuario creado correctamente');"
                    + "Richfaces.hideModalPanel('mp')");
        } else {
            this.setOncomplete("alert('Ingrese todos los campos');");
        }
    }

    public void prepararDesbloqueo(ActionEvent event) {
        var row = (MbUsuario) event.getComponent().getAttributes().get("usuarioRow");
        this.setModalUsuId(row.getRowUsuId());
        this.setModalUsuario(row.getRowUsuario());
        this.setModalFailedAttempts(row.getRowFailedAttempts());
    }

    public void desbloquear(ActionEvent event) {
        LOG.info("[{}] Unlocking user '{}' (id={})", event.getPhaseId(), modalUsuario, modalUsuId);
        daoUsuario.storeResetAttempts(modalUsuId);
        for (MbUsuario row : listUsuarios) {
            if (row.getRowUsuId() == modalUsuId) {
                row.setRowLocked(false);
                row.setRowFailedAttempts(0);
                row.setRowImgCandado(ACTION_IMAGES_ABIERTO);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public String cerrar() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        Enumeration<String> atributos = session.getAttributeNames();
        while (atributos.hasMoreElements()) {
            session.removeAttribute(atributos.nextElement());
        }
        session.invalidate();

        return "index";
    }
}
