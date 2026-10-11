package net.uch.administrativa.dao.impl;

import net.uch.administrativa.dao.HSSaldoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AdSaldo;
import org.hibernate.Session;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

/**
 * Created for sistema_academico on 13/10/2024.
 *
 * @author Cesardl
 */
public class HSSaldoDAOImpl extends TypedHibernateDao implements HSSaldoDAO {

    @Override
    public void insertarSaldo(AdSaldo sal) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        var tx = session.beginTransaction();
        session.save(sal);
        tx.commit();
        session.close();
    }

    @Override
    public Double getTotalSaldoByStudentId(int studentId) {
        var hql = "select sum(s.SalMonto) from AdSaldo s " +
                "where s.Alu.Id = :studentId " +
                "group by s.Alu";
        List<Double> results = find(Double.class, hql, "studentId", studentId);
        if (results.isEmpty()) {
            return 0d;
        } else {
            return DataAccessUtils.requiredUniqueResult(results);
        }
    }

    @Override
    public List<AdSaldo> findByStudentCode(String code) {
        var hql = "select s from AdSaldo s " +
                "inner join fetch s.Alu a " +
                "where a.AluCodigo = :code " +
                "and a.AluActivo = '1' ";
        return find(AdSaldo.class, hql, "code", code);
    }
}
