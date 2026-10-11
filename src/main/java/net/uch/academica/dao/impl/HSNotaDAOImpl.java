package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSNotaDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcNota;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSNotaDAOImpl extends TypedHibernateDao implements HSNotaDAO {

    @Override
    public void insertarOrActualizarNotas(List<AcNota> notas) {
        int r = this.getHibernateTemplate().execute(session -> {
            int size = notas.size();
            for (int i = 0; i < size; i++) {
                session.saveOrUpdate(notas.get(i));
                if (i % 5 == 0) { //20, same as the JDBC batch size
                    //flush a batch of inserts and release memory:
                    session.flush();
                    session.clear();
                }
            }
            return size;
        });
        logger.trace("Total rows affected: " + r);
    }

    @Override
    public List<AcNota> seleccionarNotas(int seccion) {
        return findByCriteria(AcNota.class, DetachedCriteria.forClass(AcNota.class).add(Restrictions.eq(AcNota.PROP_SEC_ID, seccion)));
    }

    @Override
    public AcNota listarNotaPorAlumno(int alumnoId, int seccionId, int sisevaPerId) {
        List<AcNota> results = findByCriteria(AcNota.class, DetachedCriteria.forClass(AcNota.class).add(Restrictions.eq(AcNota.PROP_NOT_ACTIVO, "1"))
                .add(Restrictions.eq("Alu.Id", alumnoId))
                .add(Restrictions.eq("SisevaPer.Id", sisevaPerId))
                .add(Restrictions.eq(AcNota.PROP_SEC_ID, seccionId)));
        return DataAccessUtils.uniqueResult(results);
    }
}
