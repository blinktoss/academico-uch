package net.uch.administrativa.dao.impl;

import net.uch.administrativa.dao.HSConceptoPagoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AdConceptoPago;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSConceptoPagoDAOImpl extends TypedHibernateDao implements HSConceptoPagoDAO {

    @Override
    public void insertarConceptoPago(AdConceptoPago conPago) {
        getHibernateTemplate().save(conPago);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> seleccionarConceptoPago(String nombre, String codigo, String tipo) {
        if (tipo.equalsIgnoreCase("0")) {
            tipo = "";
        }
        String hqlUpdate = "select CP.Id,CP.ConpagCodigo,CP.ConpagDescripcion, "
                + " CP.ConpagMonto,CP.ConpagTipo,CP.ConpagCreacion, "
                + " CP.ConpagActivo,C.CatDescripcionElemento "
                + " from AdConceptoPago CP, TbCatalogo C "
                + " where CP.ConpagDescripcion like :nombre "
                + " and CP.ConpagCodigo like :codigo "
                + " and CP.ConpagTipo like :tipo "
                + " and concat(C.CatCodigoGrupo,C.CatCodigoElemento)=CP.ConpagTipo "
                + " and CP.ConpagActivo=:activo ";
        var session = this.getHibernateTemplate().getSessionFactory().openSession();
        List<Object[]> results = session.createQuery(hqlUpdate)
                .setParameter("nombre", "%" + nombre + "%")
                .setParameter("codigo", "%" + codigo + "%")
                .setParameter("tipo", "%" + tipo + "%")
                .setParameter("activo", "1")
                .list();
        session.close();
        return results;
    }

    @Override
    public AdConceptoPago seleccionarUnaConceptoPago(int conPagId) {
        var criteria = DetachedCriteria.forClass(AdConceptoPago.class);
        criteria.add(Restrictions.eq(AdConceptoPago.PROP_ID, conPagId));
        criteria.add(Restrictions.eq(AdConceptoPago.PROP_CONPAG_ACTIVO, "1"));
        return (AdConceptoPago) DataAccessUtils.requiredUniqueResult(getHibernateTemplate().findByCriteria(criteria));
    }

    @Override
    public List<AdConceptoPago> seleccionarConceptoPago() {
        return findByCriteria(AdConceptoPago.class, DetachedCriteria.forClass(AdConceptoPago.class)
                .add(Restrictions.eq(AdConceptoPago.PROP_CONPAG_ACTIVO, "1")));
    }

    @Override
    public void eliminarConceptoPago(int id) {
        getHibernateTemplate().delete(new AdConceptoPago(id));
    }

    @Override
    public void actualizarConceptoPago(AdConceptoPago conPago) {
        getHibernateTemplate().update(conPago);
    }

    @Override
    public List<AdConceptoPago> seleccionarConceptosParaBusqueda(boolean cursoLibre) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AdConceptoPago.class)
                .add(Restrictions.eq(AdConceptoPago.PROP_CONPAG_ACTIVO, "1"))
                .add(cursoLibre
                        ? Restrictions.eq(AdConceptoPago.PROP_CONPAG_TIPO, "007003")
                        : Restrictions.ne(AdConceptoPago.PROP_CONPAG_TIPO, "007003"))
                .addOrder(Order.asc(AdConceptoPago.PROP_CONPAG_DESCRIPCION));
        return findByCriteria(AdConceptoPago.class, criteria);
    }

    @Override
    public List<AdConceptoPago> seleccionarConceptoCursoLibre(String descripcion) {
        return findByCriteria(AdConceptoPago.class, DetachedCriteria.forClass(AdConceptoPago.class)
                .add(Restrictions.eq(AdConceptoPago.PROP_CONPAG_ACTIVO, "1"))
                .add(Restrictions.eq(AdConceptoPago.PROP_CONPAG_TIPO, "007003"))
                .add(Restrictions.like(AdConceptoPago.PROP_CONPAG_DESCRIPCION, "%" + descripcion + "%")));
    }
}
