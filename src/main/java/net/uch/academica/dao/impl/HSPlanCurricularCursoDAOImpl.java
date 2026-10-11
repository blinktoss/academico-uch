package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSPlanCurricularCursoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcEquivalencia;
import net.uch.mapping.AcPlanCurso;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSPlanCurricularCursoDAOImpl extends TypedHibernateDao implements HSPlanCurricularCursoDAO {

    @Override
    public Integer insertarPlanCurricularCurso(AcPlanCurso pc) {
        getHibernateTemplate().save(pc);
        return pc.getId();
    }

    @Override
    public List<Object[]> seleccionarPlanCurricularCurso(int plan) {
        String hql = "select pcc.Id,pcc.Plan.Id,cur.CurNombre, pcc.PlancurCiclo, cat.CatDescripcionElemento,pcc.PlancurCredito, "
                + "(select CatDescripcionElemento from TbCatalogo "
                + " where concat(CatCodigoGrupo,CatCodigoElemento)=pcc.PlancurTipo), pcc.PlancurCodigo "
                + "from AcPlanCurso pcc, AcCurso cur, TbCatalogo cat "
                + "where cur.Id = pcc.Cur.Id "
                + "and pcc.Plan.Id = :planId "
                + "and pcc.PlancurCiclo = concat(cat.CatCodigoGrupo,cat.CatCodigoElemento) "
                + "and pcc.PlancurActivo = '1' order by pcc.Id asc";
        return find(Object[].class, hql, "planId", plan);
    }

    @Override
    public List<Object[]> seleccionarPlanCurricularCurso(int plan, String cicloId) {
        String hql = "select pcc.Id,pcc.Plan.Id,cur.CurNombre, pcc.PlancurCiclo, cat.CatDescripcionElemento,pcc.PlancurCredito, "
                + "(select CatDescripcionElemento from TbCatalogo "
                + " where concat(CatCodigoGrupo,CatCodigoElemento)=pcc.PlancurTipo) "
                + "from AcPlanCurso pcc, AcCurso cur, TbCatalogo cat "
                + "where cur.Id = pcc.Cur.Id "
                + "and pcc.Plan.Id = :planId "
                + "and pcc.PlancurCiclo = :cicloId "
                + "and pcc.PlancurCiclo = concat(cat.CatCodigoGrupo,cat.CatCodigoElemento) "
                + "and pcc.PlancurActivo= '1' order by pcc.Id asc";
        return find(Object[].class,
                hql, new String[]{"planId", "cicloId"}, new Object[]{plan, cicloId});
    }

    @Override
    public boolean verificarRepitencia(int plan, int curso, String ciclo) {
        String hql = "select count(pcc.Id) "
                + "from AcPlanCurso pcc "
                + "where pcc.Plan.Id = :planId "
                + "and pcc.Cur.Id = :cursoId "
                + "and pcc.PlancurCiclo = :ciclo "
                + "and pcc.PlancurActivo = '1'";

        List<Long> l = find(Long.class,
                hql, new String[]{"planId", "cursoId", "ciclo"}, new Object[]{plan, curso, ciclo});
        long x = DataAccessUtils.requiredUniqueResult(l);
        logger.trace("Curso registrados: " + x);
        return x != 0;
    }

    @Override
    public void eliminarPlanCurricularCurso(String id) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AcPlanCurso set PlancurActivo = :activo where Id = :id";
            return session.createQuery(hqlUpdate).setParameter("activo", "0").setParameter("id", id).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public List<AcPlanCurso> seleccionarCursos(int plan, String ciclo) {
        return findByCriteria(AcPlanCurso.class, DetachedCriteria.forClass(AcPlanCurso.class).add(Restrictions.eq("Plan.Id", plan))
                .add(Restrictions.eq("PlancurCiclo", ciclo))
                .add(Restrictions.eq("PlancurActivo", "1")));
    }

    @Override
    public List<AcEquivalencia> findEquivalences(int plancurId) {
        var hql = "FROM AcEquivalencia WHERE Plancur.Id = :plancurId AND EquActivo = '1'";
        return find(AcEquivalencia.class, hql, "plancurId", plancurId);
    }
}
