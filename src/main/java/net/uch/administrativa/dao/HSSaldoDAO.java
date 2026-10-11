package net.uch.administrativa.dao;

import net.uch.mapping.AdSaldo;

import java.util.List;

/**
 * Created for sistema_academico on 13/10/2024.
 *
 * @author Cesardl
 */
public interface HSSaldoDAO {

    void insertarSaldo(AdSaldo sal);

    Double getTotalSaldoByStudentId(int studentId);

    List<AdSaldo> findByStudentCode(String code);
}
