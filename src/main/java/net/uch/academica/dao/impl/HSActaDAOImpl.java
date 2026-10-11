package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSActaDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcActa;
import net.uch.mapping.AcActaDetalle;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSActaDAOImpl extends TypedHibernateDao implements HSActaDAO {

    @Override
    public void saveOrUpdateActa(AcActa acta) {
        logger.trace("save or update");
        this.getHibernateTemplate().saveOrUpdate(acta);
    }

    @Override
    public void insertarActaDetalles(List<AcActaDetalle> actasDetalle) {
        for (AcActaDetalle actaDetalle : actasDetalle) {
            this.getHibernateTemplate().saveOrUpdate(actaDetalle);
        }
    }

    @Override
    public AcActa seleccionarActa(int secId, String actaTipo) {
        List<AcActa> results = findByCriteria(AcActa.class, DetachedCriteria.forClass(AcActa.class).add(Restrictions.eq("Sec.Id", secId)).
                add(Restrictions.eq("ActTipo", actaTipo)).
                add(Restrictions.eq("ActActivo", "1")).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public List<Object[]> seleccionarAlumnosActa(int secId) {
        String hql = "select alu, actdet " +
                "from AcAlumno alu, AcActaDetalle actdet " +
                "where actdet.ActdetActivo = '1' " +
                "and actdet.AluId = alu.Id " +
                "and actdet.Act.Sec.Id = :secId " +
                "order by alu.AluAppaterno, alu.AluApmaterno, alu.AluNombres";
        return find(Object[].class, hql, "secId", secId);
    }

    @Override
    public void eliminarActa(AcActa acta) {
        this.getHibernateTemplate().delete(acta);
    }

    @Override
    public void reemplazarActaSustitutoria(AcActa actaVieja, AcActa actaNueva) {
        if (actaVieja != null) {
            this.getHibernateTemplate().delete(actaVieja);
        }
        this.getHibernateTemplate().saveOrUpdate(actaNueva);
    }
}
