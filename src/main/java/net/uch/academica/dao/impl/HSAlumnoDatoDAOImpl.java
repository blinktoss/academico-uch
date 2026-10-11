package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSAlumnoDatoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcAlumnoDato;
import net.uch.mapping.AcAlumnoDocumento;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSAlumnoDatoDAOImpl extends TypedHibernateDao implements HSAlumnoDatoDAO {

    @Override
    public void insertarAlumnoDato(AcAlumnoDato aludat) {
        getHibernateTemplate().save(aludat);
    }

    @Override
    public AcAlumnoDato seleccionarAlumnoDato(int alumnoId) {
        List<AcAlumnoDato> results = findByCriteria(AcAlumnoDato.class, DetachedCriteria.forClass(AcAlumnoDato.class)
                .add(Restrictions.eq(AcAlumnoDato.PROP_ALU_ID, alumnoId)));
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public void actualizarAlumnoDato(AcAlumnoDato aludat) {
        getHibernateTemplate().update(aludat);
    }

    @Override
    public List<AcAlumnoDocumento> seleccionarDocumentos(int alumnoId) {
        return findByCriteria(AcAlumnoDocumento.class, DetachedCriteria.forClass(AcAlumnoDocumento.class)
                .add(Restrictions.eq("Alu.Id", alumnoId)));
    }

    @Override
    public void insertarDocumentos(AcAlumnoDocumento aluDoc) {
        this.getHibernateTemplate().save(aluDoc);
    }

    @Override
    public int eliminarDocumentos(int alumnoId) {
        return this.getHibernateTemplate().execute(session -> {
            String s = "delete from AcAlumnoDocumento where Alu.Id = :v_id";
            return session.createQuery(s).setParameter("v_id", alumnoId).executeUpdate();
        });
    }
}
