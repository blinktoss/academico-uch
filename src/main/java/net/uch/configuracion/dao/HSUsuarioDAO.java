package net.uch.configuracion.dao;

import net.uch.mapping.TbRoles;
import net.uch.mapping.TbUsuario;

import java.util.List;

public interface HSUsuarioDAO {

    TbUsuario validateUser(String strUserName, String password);

    /**
     * @param strUserName the username
     * @return {@code true} if the active user exists and is locked out after too many failed logins
     */
    boolean isLocked(String strUserName);

    /**
     * Records a failed login attempt, locking the account once the attempt threshold is reached.
     *
     * @param usuId the user id
     */
    void storeFailedAttempt(int usuId);

    /**
     * Clears the failed-attempt counter after a successful login.
     *
     * @param usuId the user id
     */
    void storeResetAttempts(int usuId);

    /**
     * Records the timestamp of a successful login.
     *
     * @param usuId the user id
     */
    void storeLastLogin(int usuId);

    /**
     * Crea un usuario encriptando su contraseña
     *
     * @param usuario the user
     */
    void crearUsuario(TbUsuario usuario);

    List<TbUsuario> getListUsuario(String strUserName, int rolId);

    /**
     * Devuelve los posibles roles para el usuario
     *
     * @return lista de roles
     */
    List<TbRoles> getRoles();
}
