package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSMenuDAO;
import net.uch.mapping.TbMenu;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cesardl
 */
public class HSMenuDAOImpl extends TypedHibernateDao implements HSMenuDAO {

    @Override
    public List<TbMenu> seleccionToolBar(int rolId) {
        return findByCriteria(TbMenu.class, DetachedCriteria.forClass(TbMenu.class, "men").createCriteria("men.tbMenuRols", "menrol").
                add(Restrictions.eq("men.menActivo", "1")).
                add(Restrictions.eq("menrol.menrolActivo", "1")).
                add(Restrictions.eq("men.menOpc", "0")).
                add(Restrictions.eq("menrol.tbRoles.Id", rolId)).
                addOrder(Order.asc("men.menCodigo")));
    }

    @Override
    public List<TbMenu> seleccionHijos(int menuId, int rolId) {
        return findByCriteria(TbMenu.class, DetachedCriteria.forClass(TbMenu.class, "men").createCriteria("men.tbMenuRols", "menrol").
                add(Restrictions.eq("men.menActivo", "1")).
                add(Restrictions.eq("menrol.menrolActivo", "1")).
                add(Restrictions.eq("menrol.tbRoles.Id", rolId)).
                add(Restrictions.eq("men.menIdPadre", menuId)).
                addOrder(Order.asc("men.menCodigo")));
    }

    @Override
    public Set<String> findGrantedModules(int rolId) {
        List<String> acciones = findByCriteria(String.class, DetachedCriteria.forClass(TbMenu.class, "men").createCriteria("men.tbMenuRols", "menrol").
                add(Restrictions.eq("men.menActivo", "1")).
                add(Restrictions.eq("menrol.menrolActivo", "1")).
                add(Restrictions.eq("men.menOpc", "2")).
                add(Restrictions.eq("menrol.tbRoles.Id", rolId)).
                add(Restrictions.isNotNull("men.menAccion")).
                setProjection(Projections.distinct(Projections.property("men.menAccion"))));
        return new HashSet<>(acciones);
    }

    @Override
    public Set<String> findAllModules() {
        List<String> acciones = findByCriteria(String.class, DetachedCriteria.forClass(TbMenu.class, "men").add(Restrictions.eq("men.menActivo", "1")).
                add(Restrictions.eq("men.menOpc", "2")).
                add(Restrictions.isNotNull("men.menAccion")).
                setProjection(Projections.distinct(Projections.property("men.menAccion"))));
        Set<String> modules = new HashSet<>();
        for (String accion : acciones) {
            if (!accion.startsWith("#")) { // skip EL action items like the "Salir" menu entry
                modules.add(accion);
            }
        }
        return modules;
    }
}
