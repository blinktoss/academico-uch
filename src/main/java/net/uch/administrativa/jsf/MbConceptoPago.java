package net.uch.administrativa.jsf;

import net.uch.administrativa.dao.HSConceptoPagoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.mapping.AdConceptoPago;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MbConceptoPago {

    private static final Logger LOG = LoggerFactory.getLogger(MbConceptoPago.class);

    private final HSConceptoPagoDAO daoConceptoPago = ServiceFinder.findBean("springHibernateDaoConceptoPago", HSConceptoPagoDAO.class);

    private SelectItem[] paramTipos;
    private SelectItem[] comboCatalogo;
    private int rowId;
    private String paramCodigo;
    private String paramDescripcion;
    private float rowMonto;
    private String paramTipoId;
    private Date rowCreacion;
    private List<MbConceptoPago> listaConceptoPago;
    private String rowIdTipo;
    private int mpId;
    private String mpCodigo;
    private String mpDescripcion;
    private float mpMonto;
    private String mpTipoId;
    private Date mpCreacion;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        HSCatalogoDAO dao = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        SelectItem[] sis = dao.seleccionarCatalogo("007").stream()
                .map(t -> {
                    String value = t.getCatCodigoGrupo() + t.getCatCodigoElemento();
                    String label = t.getCatDescripcionElemento();
                    return new SelectItem(value, label);
                })
                .toArray(SelectItem[]::new);
        setParamTipos(sis);
        setComboCatalogo(sis);
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getParamCodigo() {
        return paramCodigo;
    }

    public void setParamCodigo(String paramCodigo) {
        this.paramCodigo = paramCodigo;
    }

    public String getParamDescripcion() {
        return paramDescripcion;
    }

    public void setParamDescripcion(String paramDescripcion) {
        this.paramDescripcion = paramDescripcion;
    }

    public float getRowMonto() {
        return rowMonto;
    }

    public void setRowMonto(float rowMonto) {
        this.rowMonto = rowMonto;
    }

    public String getParamTipoId() {
        return paramTipoId;
    }

    public void setParamTipoId(String paramTipoId) {
        this.paramTipoId = paramTipoId;
    }

    public Date getRowCreacion() {
        return rowCreacion;
    }

    public void setRowCreacion(Date rowCreacion) {
        this.rowCreacion = rowCreacion;
    }

    public List<MbConceptoPago> getListaConceptoPago() {
        return listaConceptoPago;
    }

    public void setListaConceptoPago(List<MbConceptoPago> listaConceptoPago) {
        this.listaConceptoPago = listaConceptoPago;
    }

    public String getRowIdTipo() {
        return rowIdTipo;
    }

    public void setRowIdTipo(String rowIdTipo) {
        this.rowIdTipo = rowIdTipo;
    }

    public int getMpId() {
        return mpId;
    }

    public void setMpId(int mpId) {
        this.mpId = mpId;
    }

    public String getMpCodigo() {
        return mpCodigo;
    }

    public void setMpCodigo(String mpCodigo) {
        this.mpCodigo = mpCodigo;
    }

    public String getMpDescripcion() {
        return mpDescripcion;
    }

    public void setMpDescripcion(String mpDescripcion) {
        this.mpDescripcion = mpDescripcion;
    }

    public float getMpMonto() {
        return mpMonto;
    }

    public void setMpMonto(float mpMonto) {
        this.mpMonto = mpMonto;
    }

    public String getMpTipoId() {
        return mpTipoId;
    }

    public void setMpTipoId(String mpTipoId) {
        this.mpTipoId = mpTipoId;
    }

    public SelectItem[] getComboCatalogo() {
        return comboCatalogo;
    }

    public void setComboCatalogo(SelectItem[] comboCatalogo) {
        this.comboCatalogo = comboCatalogo;
    }

    public SelectItem[] getParamTipos() {
        return paramTipos;
    }

    public void setParamTipos(SelectItem[] paramTipos) {
        this.paramTipos = paramTipos;
    }

    ///metodo para seleccionars
    public void seleccionar() {
        LOG.debug("Looking for '{}' | '{}' | '{}'", this.paramDescripcion, this.paramCodigo, this.paramTipoId);
        listaConceptoPago = new ArrayList<>();
        List<Object[]> lista = daoConceptoPago.seleccionarConceptoPago(this.getParamDescripcion(), this.getParamCodigo(), this.getParamTipoId());
        MbConceptoPago bE;
        for (Object[] objects : lista) {
            bE = new MbConceptoPago();
            bE.setRowId(Integer.parseInt(objects[0].toString()));
            bE.setParamCodigo(objects[1].toString());
            bE.setParamDescripcion(objects[2].toString());
            bE.setRowMonto(Float.parseFloat(objects[3].toString()));
            bE.setParamTipoId(objects[7].toString());
            if (objects[5] != null) {
                bE.setRowCreacion((Date) objects[5]);
            }
            bE.setRowIdTipo(objects[4].toString());
            listaConceptoPago.add(bE);
        }
        this.setListaConceptoPago(listaConceptoPago);
        LOG.info("Total of payment concepts {}", listaConceptoPago.size());
    }

    //---para eliminar fila
    public void eliminarFila(ActionEvent event) {
        UIParameter delete = (UIParameter) event.getComponent().findComponent("p_id");
        var id = Integer.parseInt(delete.getValue().toString());
        LOG.debug("[{}] To delete {}", event.getPhaseId(), id);
        daoConceptoPago.eliminarConceptoPago(id);
    }

    ///---
    public String nuevo() {
        LOG.debug("Opening modal");
        this.setMpId(0);
        this.setMpCodigo("");
        this.setMpDescripcion("");
        this.setMpMonto(0);
        this.setMpTipoId("0");
        return "";
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter id = (UIParameter) event.getComponent().findComponent("p_id");
        UIParameter codigo = (UIParameter) event.getComponent().findComponent("p_codigo");
        UIParameter descripcion = (UIParameter) event.getComponent().findComponent("p_descripcion");
        UIParameter monto = (UIParameter) event.getComponent().findComponent("p_monto");
        UIParameter tipo = (UIParameter) event.getComponent().findComponent("p_tipo");
        UIParameter creacion = (UIParameter) event.getComponent().findComponent("p_creacion");

        mpId = Integer.parseInt(id.getValue().toString());
        mpCodigo = codigo.getValue().toString();
        mpDescripcion = descripcion.getValue().toString();
        mpMonto = Float.parseFloat(monto.getValue().toString());
        mpTipoId = tipo.getValue().toString();
        mpCreacion = (Date) creacion.getValue();
    }

    public void insertar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var conPago = new AdConceptoPago();
        if (mpId == 0) {
            conPago.setConpagCodigo(mpCodigo);
            conPago.setConpagDescripcion(mpDescripcion);
            conPago.setConpagMonto(mpMonto);
            conPago.setConpagTipo(mpTipoId);
            conPago.setConpagCreacion(new Date());
            conPago.setConpagActivo("1");
            daoConceptoPago.insertarConceptoPago(conPago);
            this.setMpId(conPago.getId());
            LOG.info("Payment Concept saved {}", mpId);
        } else {
            conPago.setId(mpId);
            conPago.setConpagCodigo(mpCodigo);
            conPago.setConpagDescripcion(mpDescripcion);
            conPago.setConpagMonto(mpMonto);
            conPago.setConpagTipo(mpTipoId);
            conPago.setConpagCreacion(mpCreacion);
            conPago.setConpagActualizacion(new Date());
            conPago.setConpagActivo("1");
            daoConceptoPago.actualizarConceptoPago(conPago);
            LOG.info("Payment Concept updated {}", conPago.getId());
        }
    }
}
