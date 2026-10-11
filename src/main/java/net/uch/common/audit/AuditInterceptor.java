package net.uch.common.audit;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Date;

/**
 * Stamps audit fields on every {@link Auditable} entity by name-matching the Hibernate
 * mapped property names rather than calling entity getters/setters. Any future core entity
 * can opt in by implementing {@link Auditable} and mapping those four property names in its
 * {@code .hbm.xml}.
 * <p>
 * On insert only {@code createdAt}/{@code createdBy} are set; {@code updatedAt}/{@code updatedBy}
 * stay null until the row is first modified, so a never-updated record is distinguishable from
 * an updated one. On update only the {@code updated*} fields change — {@code created*} are left
 * untouched.
 * <p>
 * Registered as {@code sessionFactory}'s {@code entityInterceptor} in
 * {@code applicationContext-hibernate.xml}.
 * <p>
 * Note: bulk HQL/SQL updates (e.g. {@code session.createQuery("update ...").executeUpdate()})
 * do not load entities into the session and therefore never reach this interceptor; those
 * paths must stamp the audit columns explicitly in the HQL/SQL itself.
 */
public class AuditInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (!(entity instanceof Auditable)) {
            return false;
        }
        var now = new Date();
        var usuId = CurrentUserContext.get();
        var auditable = (Auditable) entity;
        auditable.setCreatedAt(now);
        auditable.setCreatedBy(usuId);
        // updatedAt/updatedBy intentionally left null on insert; they are only stamped once
        // the row is first modified (see onFlushDirty).
        // Identity-generated ids (auto_increment, used throughout this schema) insert
        // directly from the entity's field values, bypassing this state array; mirroring
        // the stamp into it keeps non-identity generators consistent too.
        stamp(propertyNames, state, "createdAt", now);
        stamp(propertyNames, state, "createdBy", usuId);
        return true;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {
        if (!(entity instanceof Auditable)) {
            return false;
        }
        var now = new Date();
        var usuId = CurrentUserContext.get();
        var auditable = (Auditable) entity;
        auditable.setUpdatedAt(now);
        auditable.setUpdatedBy(usuId);
        stamp(propertyNames, currentState, "updatedAt", now);
        stamp(propertyNames, currentState, "updatedBy", usuId);
        return true;
    }

    private void stamp(String[] propertyNames, Object[] state, String propertyName, Object value) {
        for (var i = 0; i < propertyNames.length; i++) {
            if (propertyName.equals(propertyNames[i])) {
                state[i] = value;
                return;
            }
        }
    }
}
