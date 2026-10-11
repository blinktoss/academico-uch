package net.uch.administrativa.jsf;

import net.uch.administrativa.dao.HSSaldoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.mapping.AdSaldo;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created for sistema_academico on 14/10/2024.
 *
 * @author Cesardl
 */
public class MbSaldo {

    private static final Logger LOG = LoggerFactory.getLogger(MbSaldo.class);

    private final HSSaldoDAO daoSaldo = ServiceFinder.findBean("springHibernateDaoSaldo", HSSaldoDAO.class);

    private String paramStudentCode;
    private List<MbSaldo> saldoList;
    ///////////////////////////
    private String oncomplete;
    ///////////////////////////
    private String rowCodigo;
    private String rowLargeName;
    private Date rowRecordDate;
    private float rowMonto;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        setSaldoList(new ArrayList<>());
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] To find '{}'", event.getPhaseId(), paramStudentCode);
        saldoList.clear();

        List<AdSaldo> saldos = daoSaldo.findByStudentCode(paramStudentCode);
        saldoList.addAll(saldos.stream()
                .map(sal -> {
                    var row = new MbSaldo();
                    var alu = sal.getAlu();

                    row.setRowCodigo(alu.getAluCodigo());
                    row.setRowLargeName(StringUtils.collectionToDelimitedString(
                            Arrays.asList(alu.getAluAppaterno(), alu.getAluApmaterno(), alu.getAluNombres()), " "
                    ));
                    row.setRowRecordDate(sal.getSalFechaIngresoMonto());
                    row.setRowMonto(sal.getSalMonto());
                    return row;
                }).collect(Collectors.toList()));
    }

    public String getParamStudentCode() {
        return paramStudentCode;
    }

    public void setParamStudentCode(String paramStudentCode) {
        this.paramStudentCode = paramStudentCode;
    }

    public List<MbSaldo> getSaldoList() {
        return saldoList;
    }

    public void setSaldoList(List<MbSaldo> saldoList) {
        this.saldoList = saldoList;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getRowCodigo() {
        return rowCodigo;
    }

    public void setRowCodigo(String rowCodigo) {
        this.rowCodigo = rowCodigo;
    }

    public String getRowLargeName() {
        return rowLargeName;
    }

    public void setRowLargeName(String rowLargeName) {
        this.rowLargeName = rowLargeName;
    }

    public Date getRowRecordDate() {
        return rowRecordDate;
    }

    public void setRowRecordDate(Date rowRecordDate) {
        this.rowRecordDate = rowRecordDate;
    }

    public float getRowMonto() {
        return rowMonto;
    }

    public void setRowMonto(float rowMonto) {
        this.rowMonto = rowMonto;
    }
}
