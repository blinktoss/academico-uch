package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSHorarioDAO;
import net.uch.academica.jsf.beans.BeanDuplicadoHorario;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcHorario;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import java.util.ArrayList;
import java.util.List;

public class HSHorarioDAOImpl extends TypedHibernateDao implements HSHorarioDAO {

    @Override
    public void insertarHorarios(List<AcHorario> horarios) {
        for (AcHorario horario : horarios) {
            this.getHibernateTemplate().saveOrUpdate(horario);
        }
    }

    @Override
    public List<AcHorario> seleccionarHorario(int sec) {
        return findByCriteria(AcHorario.class, DetachedCriteria.forClass(AcHorario.class).setFetchMode("Doc", FetchMode.JOIN).
                setFetchMode("Aul", FetchMode.JOIN).
                setFetchMode("Sec", FetchMode.JOIN).
                setFetchMode("Turdet", FetchMode.JOIN).
                add(Restrictions.eq(AcHorario.PROP_HOR_ACTIVO, "1")).
                add(Restrictions.eq("Sec.Id", sec)));
    }

    @Override
    public AcHorario seleccionarUnHorario(int horarioId) {
        return (AcHorario) this.getSessionFactory().getCurrentSession().createCriteria(AcHorario.class)
                .add(Restrictions.eq(AcHorario.PROP_HOR_ACTIVO, "1"))
                .add(Restrictions.eq("Id", horarioId)).uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BeanDuplicadoHorario> duplicidadHoraria(List<Integer> seccionIds, int semestreId) {
        List<BeanDuplicadoHorario> lista = new ArrayList<>();

        String sql = "select master.hor_dia as horDia, "
                + "master.turdet_hora as turdetHora, "
                + "master.cur_nombre as curNombre, "
                + "master.cadena as candenaCurso, "
                + "master.cantidad as cantidadCurso "
                + "from( "
                + "select tabla.sec_id, tabla.hor_dia, tabla.turdet_hora, tabla.cur_nombre, group_concat(tabla.cur_nombre separator ' <br /> ' ) cadena  ,count(*) cantidad "
                + "from ( "
                + "select ac_seccion.sec_id, ac_seccion.sec_nombre, ac_horario.hor_dia, ac_turno_detalle.turdet_hora, ac_turno_detalle.turdet_id, ac_turno.tur_id, concat(ac_seccion.sec_nombre,' ',ac_curso.cur_nombre) cur_nombre, count(*) cantidad "
                + "from ac_seccion inner join ac_horario on ac_seccion.sec_id=ac_horario.sec_id "
                + "inner join ac_turno_detalle on ac_turno_detalle.turdet_id=ac_horario.turdet_id "
                + "inner join ac_turno on ac_turno.tur_id=ac_turno_detalle.tur_id "
                + "inner join ac_curso_aperturado on ac_curso_aperturado.curape_Id=ac_seccion.curape_id "
                + "inner join ac_plan_curso on ac_plan_curso.plancur_Id=ac_curso_aperturado.plancur_id "
                + "inner join ac_curso on ac_curso.cur_id=ac_plan_curso.cur_id "
                + "where ac_seccion.sec_activo='1' "
                + "and ac_turno_detalle.turdet_activo='1' "
                + "and ac_turno.tur_activo='1' "
                + "and ac_horario.hor_activo='1' "
                + "and ac_curso_aperturado.curape_activo='1' "
                + "and ac_curso_aperturado.curape_aperturado='1' "
                + "and ac_curso_aperturado.sem_id=:sem_id "
                + "and ac_plan_curso.plancur_activo='1' "
                + "and ac_curso.cur_activo='1' "
                + "and ac_seccion.sec_id in(:ids) "
                + "group by ac_seccion.sec_id, ac_horario.hor_dia, ac_turno_detalle.turdet_hora) tabla "
                + "group by tabla.hor_dia, tabla.turdet_hora) master where master.cantidad>1 "
                + "group by master.sec_id "
                + "order by master.hor_dia, master.turdet_hora; ";

        Session session = this.getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query q = session.createSQLQuery(sql).
                    addScalar("horDia", StringType.INSTANCE).
                    addScalar("turdetHora", StringType.INSTANCE).
                    addScalar("curNombre", StringType.INSTANCE).
                    addScalar("candenaCurso", StringType.INSTANCE).
                    setParameter("sem_id", semestreId).
                    setParameterList("ids", seccionIds);

            List<Object[]> l = q.list();
            for (var i = 0; i < l.size(); i++) {
                Object[] objs = l.get(i);

                var dup = new BeanDuplicadoHorario();
                dup.setDupDia(objs[0].toString());
                dup.setDupHora(objs[1].toString());
                dup.setDupNombreCurso(objs[2].toString());
                dup.setDupNombreCursos(objs[3].toString());
                dup.setDupId(i + 1);

                lista.add(dup);
            }
            return lista;
        } finally {
            session.close();
        }
    }

    @Override
    public int seleccionarHorarioPorDocente(Integer docenteId, Integer semestreId) {
        return this.getHibernateTemplate().execute(session -> {
            String sql = "select count(*) cantidad \n"
                    + "from ac_docente inner join ac_horario on ac_docente.doc_id=ac_horario.doc_id \n"
                    + "inner join ac_seccion on ac_seccion.sec_id=ac_horario.sec_id \n"
                    + "inner join ac_curso_aperturado on ac_curso_aperturado.curape_Id=ac_seccion.curape_id \n"
                    + "inner join ac_turno_detalle on ac_turno_detalle.turdet_id=ac_horario.turdet_id \n"
                    + "inner join ac_turno on ac_turno.tur_id=ac_turno_detalle.tur_id \n"
                    + "where ac_turno_detalle.turdet_activo='1' \n"
                    + "and ac_turno.tur_activo='1' \n"
                    + "and ac_seccion.sec_activo='1' \n"
                    + "and ac_curso_aperturado.curape_activo='1' \n"
                    + "and ac_curso_aperturado.curape_aperturado='1' \n"
                    + "and ac_curso_aperturado.sem_id = :sem_id "
                    + "and ac_docente.doc_id = :doc_id";

            Query q = session.createSQLQuery(sql).
                    addScalar("cantidad", IntegerType.INSTANCE).
                    setParameter("sem_id", semestreId).
                    setParameter("doc_id", docenteId);
            return Integer.parseInt(q.uniqueResult().toString());
        });
    }
}
