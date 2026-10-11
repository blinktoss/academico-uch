package net.uch.academica.jsf;

import net.uch.academica.dao.HSHorarioAreaDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.mapping.AcArea;
import net.uch.mapping.AcHorarioArea;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created for sistema_academico on 02/09/2024.
 *
 * @author Cesardl
 */
public class MbHorarioArea {

    private static final Logger LOG = LoggerFactory.getLogger(MbHorarioArea.class);

    private HSHorarioAreaDAO dao;

    private int paramAreaId;
    private SelectItem[] comboAreas;
    private List<MbHorarioArea> horariosAreaList;
    ///////////////////////////
    private String oncomplete;
    private List<SelectItem> days;
    ///////////////////////////
    private int rowId;
    private int rowOrder;
    private String rowAreaCode;
    private String rowDayCode;
    private String rowDayLabel;
    private Date rowHour;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        dao = ServiceFinder.findBean("springHibernateDaoHorarioArea", HSHorarioAreaDAO.class);

        List<AcArea> areas = dao.findActiveAreas();
        this.setComboAreas(areas.stream()
                .map(a -> new SelectItem(a.getAreId(), a.getAreNombre()))
                .toArray(SelectItem[]::new));

        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        days = daoCatalogo.seleccionarCatalogo("016").stream()
                .map(d -> new SelectItem(d.getCatCodigoGrupo() + d.getCatCodigoElemento(), d.getCatDescripcionElemento()))
                .collect(Collectors.toList());
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for by area: {}", event.getPhaseId(), paramAreaId);
        if (paramAreaId == 0) {
            setOncomplete("alert('Seleccione un area');");
            return;
        }

        setOncomplete("");
        List<AcHorarioArea> results = dao.listHorarioAreaByArea(paramAreaId);
        setHorariosAreaList(results.stream().map(r -> {
            var bean = new MbHorarioArea();
            bean.setRowId(r.getHorareId());
            bean.setRowOrder(r.getHorareOrden());
            bean.setRowAreaCode(r.getAcArea().getAreCodigo());
            bean.setRowDayCode(r.getHorareDia());
            bean.setRowDayLabel(ServiceFinder.getLabel(days, r.getHorareDia()));
            bean.setRowHour(r.getAcTurnoDetalle().getTurdetHora());
            return bean;
        }).collect(Collectors.toList()));

        LOG.info("Total of schedules by area: {}", horariosAreaList.size());
    }

    public int getParamAreaId() {
        return paramAreaId;
    }

    public void setParamAreaId(int paramAreaId) {
        this.paramAreaId = paramAreaId;
    }

    public SelectItem[] getComboAreas() {
        return comboAreas;
    }

    public void setComboAreas(SelectItem[] comboAreas) {
        this.comboAreas = comboAreas;
    }

    public List<MbHorarioArea> getHorariosAreaList() {
        return horariosAreaList;
    }

    public void setHorariosAreaList(List<MbHorarioArea> horariosAreaList) {
        this.horariosAreaList = horariosAreaList;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getRowOrder() {
        return rowOrder;
    }

    public void setRowOrder(int rowOrder) {
        this.rowOrder = rowOrder;
    }

    public String getRowAreaCode() {
        return rowAreaCode;
    }

    public void setRowAreaCode(String rowAreaCode) {
        this.rowAreaCode = rowAreaCode;
    }

    public String getRowDayCode() {
        return rowDayCode;
    }

    public void setRowDayCode(String rowDayCode) {
        this.rowDayCode = rowDayCode;
    }

    public String getRowDayLabel() {
        return rowDayLabel;
    }

    public void setRowDayLabel(String rowDayLabel) {
        this.rowDayLabel = rowDayLabel;
    }

    public Date getRowHour() {
        return rowHour;
    }

    public void setRowHour(Date rowHour) {
        this.rowHour = rowHour;
    }
}
