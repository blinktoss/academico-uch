package net.uch.common.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Base DAO adding type-safe query helpers on top of {@link HibernateDaoSupport}.
 * <p>
 * {@code HibernateTemplate} predates generics and returns raw {@code List}, forcing every
 * caller into an unchecked cast. The HQL helpers here delegate to Hibernate's typed
 * {@code Query<T>} (result type validated by Hibernate itself), while the Criteria and
 * named-query helpers centralize the one unavoidable unchecked cast of the legacy APIs,
 * keeping the concrete DAOs free of casts and {@code @SuppressWarnings}.
 */
public abstract class TypedHibernateDao extends HibernateDaoSupport {

    // Mirrors HibernateTemplate.applyNamedParameterToQuery so "in (:param)" call sites
    // keep working when the value is a Collection or array.
    private static void applyParameter(Query<?> query, String name, Object value) {
        if (value instanceof Collection) {
            query.setParameterList(name, (Collection<?>) value);
        } else if (value instanceof Object[]) {
            query.setParameterList(name, (Object[]) value);
        } else {
            query.setParameter(name, value);
        }
    }

    protected <T> List<T> find(Class<T> resultType, String hql) {
        return getHibernateTemplate().execute(session -> session.createQuery(hql, resultType).list());
    }

    protected <T> List<T> find(Class<T> resultType, String hql, String paramName, Object value) {
        return getHibernateTemplate().execute(session -> {
            Query<T> query = session.createQuery(hql, resultType);
            applyParameter(query, paramName, value);
            return query.list();
        });
    }

    protected <T> List<T> find(Class<T> resultType, String hql, String[] paramNames, Object[] values) {
        if (paramNames.length != values.length) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }
        return getHibernateTemplate().execute(session -> {
            Query<T> query = session.createQuery(hql, resultType);
            for (int i = 0; i < paramNames.length; i++) {
                applyParameter(query, paramNames[i], values[i]);
            }
            return query.list();
        });
    }

    protected <T> List<T> find(Class<T> resultType, String hql, Map<String, ?> params) {
        return getHibernateTemplate().execute(session -> {
            Query<T> query = session.createQuery(hql, resultType);
            params.forEach((name, value) -> applyParameter(query, name, value));
            return query.list();
        });
    }

    /**
     * @param resultType witness for the element type of the legacy (untyped) Criteria result;
     *                   states the contract the {@code DetachedCriteria} root class must honor
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> findByCriteria(Class<T> resultType, DetachedCriteria criteria) {
        return (List<T>) getHibernateTemplate().findByCriteria(criteria);
    }

    /**
     * @param resultType witness for the element type declared by the named query mapping
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> findByNamedQuery(Class<T> resultType, String queryName, Object... values) {
        return (List<T>) getHibernateTemplate().findByNamedQuery(queryName, values);
    }

    /**
     * @param resultType witness for the element type declared by the named query mapping
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> findByNamedQueryAndNamedParam(Class<T> resultType, String queryName,
                                                        String[] paramNames, Object[] values) {
        return (List<T>) getHibernateTemplate().findByNamedQueryAndNamedParam(queryName, paramNames, values);
    }
}
