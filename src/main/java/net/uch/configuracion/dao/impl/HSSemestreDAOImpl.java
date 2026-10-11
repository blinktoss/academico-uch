package net.uch.configuracion.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.AcSemestre;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSSemestreDAOImpl extends TypedHibernateDao implements HSSemestreDAO {

    @Override
    public Integer insertarSemestre(AcSemestre semestre) {
        this.getHibernateTemplate().save(semestre);
        return semestre.getId();
    }

    @Override
    public List<AcSemestre> seleccionarSemestre(String codigo, String nombre) {
        var criteria = DetachedCriteria.forClass(AcSemestre.class);
        if (!codigo.isEmpty()) {
            criteria.add(Restrictions.eq(AcSemestre.PROP_SEM_CODIGO, codigo));
        }
        criteria.add(Restrictions.like(AcSemestre.PROP_SEM_NOMBRE, nombre, MatchMode.ANYWHERE))
                .add(Restrictions.eq(AcSemestre.PROP_SEM_ACTIVO, "1"))
                .addOrder(Order.desc(AcSemestre.PROP_SEM_FECHA_INICIO));
        return findByCriteria(AcSemestre.class, criteria);
    }

    @Override
    public void eliminarSemestre(String id) {
        AcSemestre semestre = getHibernateTemplate().get(AcSemestre.class, Integer.parseInt(id));
        if (semestre != null) {
            semestre.setSemActivo("0");
            getHibernateTemplate().update(semestre);
        }
    }

    @Override
    public void actualizarSemestre(AcSemestre semestre) {
        AcSemestre persistido = getHibernateTemplate().get(AcSemestre.class, semestre.getId());
        persistido.setSemCodigo(semestre.getSemCodigo());
        persistido.setSemNombre(semestre.getSemNombre());
        persistido.setSemFechaInicio(semestre.getSemFechaInicio());
        persistido.setSemFechaFin(semestre.getSemFechaFin());
        persistido.setSemVigente(semestre.getSemVigente());
        persistido.setSemNumSemanas(semestre.getSemNumSemanas());
        persistido.setSemActivo(semestre.getSemActivo());
        persistido.setSemPensiones(semestre.getSemPensiones());
        persistido.setSemActual(semestre.getSemActual());
        getHibernateTemplate().update(persistido);
    }

    @Override
    public List<AcSemestre> seleccionarSemestreActivo() {
        return findByCriteria(AcSemestre.class, DetachedCriteria.forClass(AcSemestre.class).add(Restrictions.eq(AcSemestre.PROP_SEM_ACTIVO, "1"))
                .addOrder(Order.desc(AcSemestre.PROP_SEM_FECHA_INICIO)));
    }

    @Override
    public List<AcSemestre> seleccionarSemestresVigentes() {
        return findByCriteria(AcSemestre.class, DetachedCriteria.forClass(AcSemestre.class).add(Restrictions.eq(AcSemestre.PROP_SEM_VIGENTE, "1"))
                .add(Restrictions.eq(AcSemestre.PROP_SEM_ACTIVO, "1"))
                .addOrder(Order.desc(AcSemestre.PROP_SEM_FECHA_INICIO)));
    }

    @Override
    public AcSemestre seleccionarSemestreActual() {
        List<AcSemestre> results = findByCriteria(AcSemestre.class, DetachedCriteria.forClass(AcSemestre.class).add(Restrictions.eq(AcSemestre.PROP_SEM_ACTIVO, "1"))
                .add(Restrictions.eq(AcSemestre.PROP_SEM_ACTUAL, "1")));
        return DataAccessUtils.requiredUniqueResult(results);
    }

    @Override
    public AcSemestre getSemestre(Integer semestreId) {
        return this.getHibernateTemplate().get(AcSemestre.class, semestreId);
    }
}

