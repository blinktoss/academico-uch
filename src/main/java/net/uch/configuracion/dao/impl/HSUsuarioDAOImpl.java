package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSUsuarioDAO;
import net.uch.mapping.TbRoles;
import net.uch.mapping.TbUsuario;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.hibernate5.encryptor.HibernatePBEEncryptorRegistry;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;

import java.util.ArrayList;
import java.util.List;

import static net.uch.mapping.TbUsuario.*;

public class HSUsuarioDAOImpl extends TypedHibernateDao implements HSUsuarioDAO {

    /**
     * Consecutive failed login attempts that trigger a permanent lockout. Once reached, the account
     * stays locked until an administrator clears {@code usu_locked}.
     */
    static final int MAX_FAILED_ATTEMPTS = 5;

    private final StandardPBEStringEncryptor textEncryptor;

    public HSUsuarioDAOImpl() {
        textEncryptor = new StandardPBEStringEncryptor();
        textEncryptor.setPassword("jasypt");

        var registry1 = HibernatePBEEncryptorRegistry.getInstance();
        registry1.registerPBEStringEncryptor("hibernateStringEncryptor", textEncryptor);
    }

    @Override
    public TbUsuario validateUser(String strUserName, String password) {
        var criteria = DetachedCriteria.forClass(TbUsuario.class);
        criteria.add(Restrictions.eq(PROP_USU_USUARIO, strUserName));
        criteria.add(Restrictions.eq(PROP_USU_ACTIVO, "1"));
        try {
            List<TbUsuario> usuarios = findByCriteria(TbUsuario.class, criteria);
            TbUsuario usuario = DataAccessUtils.requiredUniqueResult(usuarios);

            if ("1".equals(usuario.getUsuLocked())) {
                logger.warn(String.format("Login refused: account '%s' is locked", strUserName));
                return null;
            }

            logger.trace(String.format("%s - %s", usuario.getUsuUsuario(), usuario.getUsuPassword()));
            boolean passwordMatches = password.equals(textEncryptor.decrypt(usuario.getUsuPassword()));
            if (passwordMatches && usuario.getUsuVigente().equals("1")) {
                if (usuario.getUsuFailedAttempts() != null && usuario.getUsuFailedAttempts() > 0) {
                    storeResetAttempts(usuario.getId());
                }
                storeLastLogin(usuario.getId());
                return usuario;
            }
            if (!passwordMatches) {
                storeFailedAttempt(usuario.getId());
            }
        } catch (EncryptionOperationNotPossibleException | EmptyResultDataAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isLocked(String strUserName) {
        var criteria = DetachedCriteria.forClass(TbUsuario.class);
        criteria.add(Restrictions.eq(PROP_USU_USUARIO, strUserName));
        criteria.add(Restrictions.eq(PROP_USU_ACTIVO, "1"));
        List<TbUsuario> usuarios = findByCriteria(TbUsuario.class, criteria);
        return !usuarios.isEmpty() && "1".equals(usuarios.get(0).getUsuLocked());
    }

    @Override
    public void storeFailedAttempt(int usuId) {
        var usuario = (TbUsuario) getHibernateTemplate().get(TbUsuario.class, usuId);
        if (usuario == null) {
            return;
        }
        int attempts = (usuario.getUsuFailedAttempts() == null ? 0 : usuario.getUsuFailedAttempts()) + 1;
        usuario.setUsuFailedAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            usuario.setUsuLocked("1");
            logger.warn(String.format("Account '%s' locked after %d failed login attempts", usuario.getUsuUsuario(), attempts));
        }
        getHibernateTemplate().update(usuario);
    }

    @Override
    public void storeResetAttempts(int usuId) {
        var usuario = (TbUsuario) getHibernateTemplate().get(TbUsuario.class, usuId);
        if (usuario == null) {
            return;
        }
        usuario.setUsuFailedAttempts(0);
        usuario.setUsuLocked("0");
        getHibernateTemplate().update(usuario);
    }

    @Override
    public void storeLastLogin(int usuId) {
        var usuario = (TbUsuario) getHibernateTemplate().get(TbUsuario.class, usuId);
        if (usuario == null) {
            return;
        }
        usuario.setUsuLastLogin(new java.util.Date());
        getHibernateTemplate().update(usuario);
    }

    @Override
    public void crearUsuario(TbUsuario usuario) {
        String encryptedPassword = textEncryptor.encrypt(usuario.getUsuPassword());

        usuario.setUsuPassword(encryptedPassword);

        this.getHibernateTemplate().save(usuario);
    }

    @Override
    public List<TbUsuario> getListUsuario(String strUserName, int rolId) {
        List<TbUsuario> ltbu = new ArrayList<>();

        var criteria = DetachedCriteria.forClass(TbUsuario.class);
        criteria.add(Restrictions.like(PROP_USU_USUARIO, strUserName, MatchMode.START));
        criteria.add(Restrictions.eq(PROP_USU_ACTIVO, "1"));
        if (rolId != 0) {
            criteria.add(Restrictions.eq(PROP_ROL + "." + TbRoles.PROP_ID, rolId));
        }
        List<TbUsuario> objs = findByCriteria(TbUsuario.class, criteria);
        for (TbUsuario tbUsuario : objs) {
            String decryptedPassword;
            try {
                decryptedPassword = textEncryptor.decrypt(tbUsuario.getUsuPassword());
            } catch (EncryptionOperationNotPossibleException e) {
                logger.warn(String.format("Skipping user '%s' in list: usu_password is not decryptable", tbUsuario.getUsuUsuario()));
                continue;
            }

            var obj = new TbUsuario();
            obj.setId(tbUsuario.getId());
            obj.setUsuUsuario(tbUsuario.getUsuUsuario());
            obj.setUsuPassword(decryptedPassword);
            obj.setRol(tbUsuario.getRol());
            obj.setUsuActivo(tbUsuario.getUsuActivo());
            obj.setUsuNivel(tbUsuario.getUsuNivel());
            obj.setUsuTipo(tbUsuario.getUsuTipo());
            obj.setUsuTipoId(tbUsuario.getUsuTipoId());
            obj.setUsuVigente(tbUsuario.getUsuVigente());
            obj.setUsuFailedAttempts(tbUsuario.getUsuFailedAttempts());
            obj.setUsuLocked(tbUsuario.getUsuLocked());

            ltbu.add(obj);
        }
        return ltbu;
    }

    @Override
    public List<TbRoles> getRoles() {
        String hql = "select rol.Id, rol.RolTipo "
                + "from TbRoles rol where rol.RolActivo = '1' "
                + "order by rol.RolTipo";

        List<Object[]> tmp = find(Object[].class, hql);
        List<TbRoles> roles = new ArrayList<>();
        for (Object[] objs : tmp) {
            var rol = new TbRoles();
            rol.setId(Integer.valueOf(objs[0].toString()));
            rol.setRolTipo(objs[1].toString().toUpperCase());

            roles.add(rol);
        }
        return roles;
    }
}

