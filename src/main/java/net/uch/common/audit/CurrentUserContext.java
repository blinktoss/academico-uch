package net.uch.common.audit;

/**
 * Holds the id of the logged-in user for the duration of the current request thread,
 * so that code outside the JSF lifecycle (e.g. {@link AuditInterceptor}, running inside
 * Hibernate's flush) can know who is acting without depending on {@code FacesContext}.
 * Populated by {@link AuditUserFilter} on every request.
 */
public final class CurrentUserContext {

    private static final ThreadLocal<Integer> CURRENT_USER_ID = new ThreadLocal<>();

    private CurrentUserContext() {
    }

    public static void set(Integer usuId) {
        CURRENT_USER_ID.set(usuId);
    }

    public static Integer get() {
        return CURRENT_USER_ID.get();
    }

    public static void clear() {
        CURRENT_USER_ID.remove();
    }
}
