/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.banco.dao.impl;

import net.uch.banco.dao.HSBancoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

/**
 * @author richard
 */
public class HSBancoDAOImpl extends TypedHibernateDao implements HSBancoDAO {

    public List<TbBanco> listarBanco(int banId) {
        return findByCriteria(TbBanco.class, DetachedCriteria.forClass(TbBanco.class).add(Restrictions.eq("banId", banId)));
    }

    public List<SpDataBanco> listarDataBanco() {
        return findByNamedQuery(SpDataBanco.class, "sp_data_banco");
    }

    public List<SpConceptosBanco> listarConceptosBanco() {
        return findByNamedQuery(SpConceptosBanco.class, "sp_conceptos_banco");
    }

    public void guardarRespuestaBanco(List<TbBancoEntrada> entradas) {
        for (TbBancoEntrada entrada : entradas) {
            getHibernateTemplate().saveOrUpdate(entrada);
        }
    }

    public TbBancoEntrada existeNroOperacion(String nroOperacion) {
        List<TbBancoEntrada> results = findByCriteria(TbBancoEntrada.class, DetachedCriteria.forClass(TbBancoEntrada.class).
                add(Restrictions.eq("banentActivo", "1")).
                add(Restrictions.eq("banentNroOperacion", nroOperacion)));
        return DataAccessUtils.uniqueResult(results);
    }

    public List<TbPagoBanco> pagosBanco(Integer codigoGrupo) {
        return findByCriteria(TbPagoBanco.class, DetachedCriteria.forClass(TbPagoBanco.class).add(Restrictions.eq("pagbanActivo", "1")).
                add(Restrictions.eq("pagbanCodigoGrupo", codigoGrupo)));
    }
}
