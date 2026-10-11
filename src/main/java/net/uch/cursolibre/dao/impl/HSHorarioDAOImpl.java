package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSHorarioDAO;
import net.uch.mapping.ClAlumno;
import net.uch.mapping.ClHoraria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HSHorarioDAOImpl extends TypedHibernateDao implements HSHorarioDAO {

    @Override
    public List<ClHoraria> seleccionarHorario(int seccionId) {
        return findByCriteria(ClHoraria.class, DetachedCriteria.forClass(ClHoraria.class).add(Restrictions.eq("horActivo", "1")).
                add(Restrictions.eq("clSeccion.secId", seccionId)));
    }

    @Override
    public void insertarActualizarHorarios(List<ClHoraria> horarias) {
        for (ClHoraria horaria : horarias) {
            this.getHibernateTemplate().saveOrUpdate(horaria);
        }
    }

    @Override
    public void eliminarHorarios(List<Integer> horarioIds) {
        String hqlUpdate = "update ClHoraria "
                + "set horActivo = '0' "
                + "where horId in (:v_id)";

        this.getSessionFactory().getCurrentSession().createQuery(hqlUpdate).
                setParameterList("v_id", horarioIds).executeUpdate();
    }

    @Override
    public List<ClAlumno> matriculadosSeccion(int seccionId) {
        return findByCriteria(ClAlumno.class, DetachedCriteria.forClass(ClAlumno.class, "ClAlumno").createCriteria("ClAlumno.clMatriculas", "ClMatricula").
                createCriteria("ClMatricula.clMatriculaTallers", "ClMatriculaTaller").
                add(Restrictions.eq("ClMatriculaTaller.mattalActivo", "1")).
                add(Restrictions.eq("ClMatricula.matActivo", "1")).
                add(Restrictions.eq("ClAlumno.aluActivo", "1")).
                add(Restrictions.eq("ClMatriculaTaller.clSeccion.secId", seccionId)).
                addOrder(Order.asc("ClAlumno.aluAppaterno")));
    }
}
