package net.uch.autenticacion.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSMenuDAO;
import net.uch.configuracion.dao.HSUsuarioDAO;
import net.uch.configuracion.jsf.MbMenu;
import net.uch.mapping.TbUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Created for sistema_academico on 15/11/2024.
 *
 * @author Cesardl
 */
public class MbLogin {

    private static final Logger LOG = LoggerFactory.getLogger(MbLogin.class);

    /**
     * The god-admin (usu_id = 1) bypasses module authorization.
     */
    private static final int SUPER_ADMIN_ID = 1;

    private String rowUsuario;
    private String rowPsw;
    private String mensajes;
    private MbMenu menu;

    public MbLogin() {
        mensajes = "";
    }

    public String checkUser() {
        LOG.info("check");
        HSUsuarioDAO daoUsuario = ServiceFinder.findBean("springHibernateDaoUsuario", HSUsuarioDAO.class);
        TbUsuario usua;
        if ((usua = daoUsuario.validateUser(getRowUsuario(), getRowPsw())) != null) {
            this.rowUsuario = usua.getUsuUsuario();
            this.rowPsw = usua.getUsuPassword();

            int rolId = usua.getRol().getId();
            HSMenuDAO daoMenu = ServiceFinder.findBean("springHibernateDaoMenu", HSMenuDAO.class);

            var user = new BeanUser();
            user.setRowUsuId(usua.getId());
            user.setNivel(usua.getUsuNivel());
            user.setRolId(rolId);
            user.setSuperAdmin(usua.getId() == SUPER_ADMIN_ID);
            user.setGrantedModules(daoMenu.findGrantedModules(rolId));

            var context = FacesContext.getCurrentInstance().getExternalContext();
            HttpSession session = (HttpSession) context.getSession(false);
            session.setAttribute("usuario", user);
            LOG.info("ok");
            var mbMenu = new MbMenu();
            mbMenu.setRolId(usua.getRol().getId());
            mbMenu.setUsuario(usua.getUsuUsuario());
            mbMenu.armarMenu();
            this.setMenu(mbMenu);

            return "ok";
        } else {
            if (daoUsuario.isLocked(getRowUsuario())) {
                this.setMensajes("Cuenta bloqueada por intentos fallidos. Contacte al administrador.");
            } else {
                this.setMensajes("Usuario o Password es incorrecto");
            }
            LOG.error("error");
            return "error";
        }
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

    public String getMensajes() {
        return mensajes;
    }

    public void setMensajes(String mensajes) {
        this.mensajes = mensajes;
    }

    public MbMenu getMenu() {
        return menu;
    }

    public void setMenu(MbMenu menu) {
        this.menu = menu;
    }
}
