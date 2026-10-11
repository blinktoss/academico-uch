package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSImportacionNotasDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcImportacionNotas;
import net.uch.mapping.AcSisEvaPersonalizado;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

/**
 * @author richard
 */
public class HSImportacionNotasDAOImpl extends TypedHibernateDao implements HSImportacionNotasDAO {

    @Override
    public List<AcSisEvaPersonalizado> listarSistemaEvaluacionPersonalizadoPorSeccion(int seccionId) {
        return findByCriteria(AcSisEvaPersonalizado.class, DetachedCriteria.forClass(AcSisEvaPersonalizado.class, "sisevaper").createCriteria("sisevaper.SisevaDetalle", "sisevadet").
                createCriteria("sisevadet.Siseva", "siseva").
                add(Restrictions.eq("sisevadet.SisevaDetalleActivo", "1")).
                add(Restrictions.eq("sisevaper.SisevaPerActivo", "1")).
                add(Restrictions.eq("siseva.SisevaActivo", "1")).
                add(Restrictions.eq("sisevaper.Curape.Id", seccionId)));
    }

    @Override
    public AcImportacionNotas listarImportacionNotaPorSeccion(int seccionId, int sisevaId) {
        List<AcImportacionNotas> results = findByCriteria(AcImportacionNotas.class, DetachedCriteria.forClass(AcImportacionNotas.class, "AcImportacionNotas").
                add(Restrictions.eq("AcImportacionNotas.acSeccion.Id", seccionId)).
                add(Restrictions.eq("AcImportacionNotas.acSisEvaPersonalizado.Id", sisevaId)));
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public void agregarImportacionNota(AcImportacionNotas notas) {
        this.getHibernateTemplate().save(notas);
    }

    @Override
    public void actualizarImportacionNota(AcImportacionNotas notas) {
        this.getHibernateTemplate().update(notas);
    }
}
