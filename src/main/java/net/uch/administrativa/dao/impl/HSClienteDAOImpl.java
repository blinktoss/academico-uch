package net.uch.administrativa.dao.impl;

import net.uch.administrativa.dao.HSClienteDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AdClientes;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSClienteDAOImpl extends TypedHibernateDao implements HSClienteDAO {

    @Override
    public void insertarCliente(AdClientes cli) {
        getHibernateTemplate().save(cli);
    }

    @Override
    public List<AdClientes> seleccionarCliente(String buscar) {
        return findByCriteria(AdClientes.class, DetachedCriteria.forClass(AdClientes.class).add(Restrictions.eq(AdClientes.PROP_CLI_ACTIVO, "1")).
                add(Restrictions.like(AdClientes.PROP_CLI_RAZON_SOCIAL, buscar, MatchMode.ANYWHERE)).
                addOrder(Order.asc(AdClientes.PROP_CLI_RAZON_SOCIAL)));
    }

    @Override
    public int seleccionarUnCliente(String ruc) {
        var x = 0;
        var criteria = DetachedCriteria.forClass(AdClientes.class);
        criteria.add(Restrictions.eq(AdClientes.PROP_CLI_ACTIVO, "1"));
        criteria.add(Restrictions.eq(AdClientes.PROP_CLI_RAZON_SOCIAL, ruc));
        criteria.addOrder(Order.asc(AdClientes.PROP_ID));
        List<AdClientes> lista = findByCriteria(AdClientes.class, criteria);
        if (!lista.isEmpty()) {
            x = lista.get(0).getId();
        }
        return x;
    }

    @Override
    public AdClientes seleccionarUnCliente(int id) {
        List<AdClientes> results = findByCriteria(AdClientes.class, DetachedCriteria.forClass(AdClientes.class).add(Restrictions.eq(AdClientes.PROP_CLI_ACTIVO, "1")).
                add(Restrictions.eq(AdClientes.PROP_ID, id)).
                addOrder(Order.asc(AdClientes.PROP_ID)));

        return DataAccessUtils.uniqueResult(results);
    }
}
