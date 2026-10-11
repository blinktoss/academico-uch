package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSPreRequisitosDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcPlanCurso;
import net.uch.mapping.AcPreRequisitos;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.*;

public class HSPreRequisitosDAOImpl extends TypedHibernateDao implements HSPreRequisitosDAO {

    @Override
    public void insertarPreRequisitos(AcPreRequisitos alu) {
        getHibernateTemplate().save(alu);
    }

    @Override
    public List<AcPlanCurso> cursoDet(int plan) {
        return findByCriteria(AcPlanCurso.class, DetachedCriteria.forClass(AcPlanCurso.class)
                .add(Restrictions.eq(AcPlanCurso.PROP_PLANCUR_ACTIVO, "1"))
                .add(Restrictions.eq("Plan.Id", plan))
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.asc(AcPlanCurso.PROP_PLANCUR_CICLO))
                .addOrder(Order.asc(AcPlanCurso.PROP_ID)));
    }

    @Override
    public List<AcPlanCurso> seleccionarCurso(int plan, String ciclo) {
        return findByCriteria(AcPlanCurso.class, DetachedCriteria.forClass(AcPlanCurso.class)
                .add(Restrictions.eq("Plan.Id", plan))
                .add(Restrictions.eq(AcPlanCurso.PROP_PLANCUR_ACTIVO, "1"))
                .add(Restrictions.eq(AcPlanCurso.PROP_PLANCUR_CICLO, ciclo)));
    }

    @Override
    public void eliminarPreRequisitos(Integer id) {
        var hqlUpdate = "update AcPreRequisitos set PreReqActivo = :v_activo where Id = :v_id";
        this.getSessionFactory().getCurrentSession().createQuery(hqlUpdate).setParameter("v_activo", "0").setParameter("v_id", id).executeUpdate();
    }

    @Override
    public List<Object[]> seleccionarPreReq(int curso) {
        String hqlUpdate = "select p.Id, p.Plancur.Id, p.Plancur.Cur.CurNombre, p.PlancurIdRequisito.Id, "
                + " p.PlancurIdRequisito.Cur.CurNombre, "
                + " (  "
                + "     select c.CatDescripcionElemento from TbCatalogo c, AcPlanCurso x "
                + "     where  x.Id=p.PlancurIdRequisito.Id "
                + "     and concat(c.CatCodigoGrupo,c.CatCodigoElemento)=x.PlancurCiclo "
                + " )  "
                + " from AcPreRequisitos p where p.PreReqActivo = '1' "
                + " and p.Plancur.Id=:cursoId";
        return find(Object[].class, hqlUpdate, "cursoId", curso);
    }

    @Override
    public List<Object[]> verificarRepitencia(Integer curso, Integer req) {
        String hql = "select p.Id, p.Plancur.Id ,p.PlancurIdRequisito.Id from AcPreRequisitos p " +
                "where p.PreReqActivo= '1' and p.Plancur.Id = :cursoId " +
                "and p.PlancurIdRequisito.Id = :reqId";

        return find(Object[].class,
                hql, new String[]{"cursoId", "reqId"}, new Object[]{curso, req});
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AcPlanCurso> retonarCursosAbiertos(List<Integer> aprobados, List<Integer> desaprobados) {
        List<AcPlanCurso> lista;
        lista = this.getSessionFactory().getCurrentSession().createCriteria(AcPlanCurso.class)
                .add(Restrictions.eq(AcPlanCurso.PROP_PLANCUR_ACTIVO, "1")).list();
        List<AcPlanCurso> cursosAbiertos = new ArrayList<>();
        List<AcPlanCurso> cursosFinales = new ArrayList<>();
        for (AcPlanCurso acPlanCurso : lista) {
            Set<AcPreRequisitos> prereq = acPlanCurso.getAcPreRequisitosByPlancurIdRequisito();
            List<AcPreRequisitos> listP = Collections.synchronizedList(new LinkedList<>(prereq));

            var contador = 0;
            for (var j = 0; j < listP.size(); j++) {
                for (Integer aprobado : aprobados) {
                    if (("" + aprobado).equals("" + (listP.get(j).getPlancurIdRequisito().getId()))) {
                        contador++;
                    }
                }
                if (contador == listP.size()) {
                    cursosAbiertos.add(listP.get(j).getPlancur());
                }
            }
        }

        //CURSOS ABIERTOS
        for (AcPlanCurso cursos_abierto : cursosAbiertos) {
            var flag = 0;
            //CURSOS APROBADOS
            for (Integer aprobado : aprobados) {
                if (("" + cursos_abierto.getId()).equals("" + aprobado)) {
                    flag++;
                    break;
                }
            }
            //CURSOS DESAPROBADOS
            for (Integer desaprobado : desaprobados) {
                if (cursos_abierto.getId().equals(desaprobado)) {
                    flag++;
                    break;
                }
            }
            if (flag < 1) {
                cursosFinales.add(cursos_abierto);
            }
        }

        return cursosFinales;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AcPlanCurso> retonarCursosLibres(List<Integer> list, int esp) {
        List<AcPlanCurso> cursosLibres = new ArrayList<>();

        //se obtiene los plancursos de la especialidad que llega como parametro
        List<AcPlanCurso> lista = this.getSessionFactory().getCurrentSession().createCriteria(AcPlanCurso.class)
                .add(Restrictions.eq(AcPlanCurso.PROP_PLANCUR_ACTIVO, "1"))
                .createCriteria("Plan")
                .add(Restrictions.eq("Esp.Id", esp)).list();

        //se recorre los plancursos A B C D E F G
        for (AcPlanCurso acPlanCurso : lista) {
            var flag = 0;
            if (acPlanCurso.getAcPreRequisitosByPlancurIdRequisito().isEmpty()) { //A C E F
                //A D E F G
                for (Integer integer : list) {
                    if (acPlanCurso.getId().equals(integer)) {
                        flag = 1;
                        break;
                    }
                }

                if (flag == 0) {
                    cursosLibres.add(acPlanCurso);
                }
            }
        }
        return cursosLibres;
    }
}
