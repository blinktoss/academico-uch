package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSAsistenciaDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcSesionEfectivaAsisDet;
import net.uch.mapping.AcSesionEfectivaAsistencia;
import net.uch.mapping.TbAsistencia;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HSAsistenciaDAOImpl extends TypedHibernateDao implements HSAsistenciaDAO {

    @Override
    public List<TbAsistencia> seleccionarAsistencia(int aluId, String codigo, Date desde, Date hasta) {
        List<TbAsistencia> results = findByCriteria(TbAsistencia.class, DetachedCriteria.forClass(TbAsistencia.class)
                .add(Restrictions.eq(TbAsistencia.PROP_ASIS_CODIGO, codigo))
                .add(Restrictions.between(TbAsistencia.PROP_ASIS_DIA, desde, hasta)));

        if (results.isEmpty()) {
            String hql = "from AcSesionEfectivaAsistencia sesefeasis " +
                    "inner join fetch sesefeasis.acSesionEfectivaAsisDets sesefeasisdet " +
                    "inner join fetch sesefeasisdet.acAlumno alu " +
                    "where sesefeasis.sesefeasisActivo = '1' " +
                    "and sesefeasisdet.sesefeasisDetActivo = '1' " +
                    "and alu.Id = :aluId " +
                    "and sesefeasis.sesefeasisRegistro between :desde and :hasta";
            List<AcSesionEfectivaAsistencia> l = find(AcSesionEfectivaAsistencia.class,
                    hql, new String[]{"aluId", "desde", "hasta"}, new Object[]{aluId, desde, hasta});
            results = l.stream().map(x -> {
                AcSesionEfectivaAsisDet sesefeasisdet = DataAccessUtils.requiredUniqueResult(x.getAcSesionEfectivaAsisDets());

                var asis = new TbAsistencia();
                asis.setAsisDia(x.getSesefeasisRegistro());
                asis.setAsisCodigo(sesefeasisdet.getAcAlumno().getAluCodigo());
                return asis;
            }).collect(Collectors.toList());
        }
        return results;
    }
}
