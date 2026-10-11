package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSContenidoTematicoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcContenidoDetalle;
import net.uch.mapping.AcContenidoTematico;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSContenidoTematicoDAOImpl extends TypedHibernateDao implements HSContenidoTematicoDAO {

    @Override
    public void insertarContenidoTematico(AcContenidoTematico con) {
        this.getHibernateTemplate().saveOrUpdate(con);
    }

    @Override
    public void eliminarContenidoTematicoDetalle(List<AcContenidoDetalle> det) {
        this.getHibernateTemplate().deleteAll(det);
    }

    @Override
    public AcContenidoTematico getContenidoTematico(int plancurId) {
        List<AcContenidoTematico> results = findByCriteria(AcContenidoTematico.class, DetachedCriteria.forClass(AcContenidoTematico.class).add(Restrictions.eq("Plancur.Id", plancurId)).
                add(Restrictions.eq("ContemActivo", "1")));
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public List<AcContenidoDetalle> listarDetalle(int contemId) {
        return findByCriteria(AcContenidoDetalle.class, DetachedCriteria.forClass(AcContenidoDetalle.class).add(Restrictions.eq("Contem.Id", contemId)));
    }

    @Override
    public Object[] seleccionarDatosCT(int plancurId) {
        String hqlSelect = "select pc.Id,pc.PlancurCiclo,pcu.Id,pcu.PlanDescripcion"
                + " ,e.EspNombre, ca.CatDescripcionElemento,pc.Cur.CurNombre"
                + " from AcPlanCurso pc"
                + " ,AcPlancurricular pcu,AcEspecialidad e,"
                + " TbCatalogo ca"
                + " where pc.Plan.Id=pcu.Id"
                + " and pcu.Esp.Id=e.Id"
                + " and ca.CatCodigoGrupo=substring(pc.PlancurCiclo,1,3)"
                + " and ca.CatCodigoElemento=substring(pc.PlancurCiclo,4,6)"
                + " and pc.Id= :plancurId";
        List<Object[]> results = find(Object[].class, hqlSelect, "plancurId", plancurId);
        return DataAccessUtils.requiredUniqueResult(results);
    }
}
