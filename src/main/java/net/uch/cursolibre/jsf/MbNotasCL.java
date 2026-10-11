/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.*;
import net.uch.cursolibre.jsf.beans.ObjNodeCLGnral;
import net.uch.mapping.*;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlSeparator;
import org.richfaces.component.html.HtmlSpacer;
import org.richfaces.event.NodeSelectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.uch.common.Constants.PARAM_SECCION_ID;

/**
 * @author cesardl
 */
public class MbNotasCL extends BaseMbViewTreeCL {

    private static final Logger LOG = LoggerFactory.getLogger(MbNotasCL.class);

    private int lblTalapeId;
    private String lblTalapeNombre;
    private String lblCurNombre;
    private String lblModNombre;
    private String oncomplete;
    //#################################De la tabla
    private int rowSecId;
    private String rowSecCod;
    private String rowSecNom;
    private Date rowSecFini;
    private Date rowSecFfin;
    private int rowMatNum;
    //#################################De la modal
    private HtmlPanelGrid htmlPanel;
    private int mpNroColumnas;
    private List<HtmlInputHidden> hidden;
    private int mpSecId;
    private int mpTalapeId;
    private List<ClSisEvaPersonalizado> columns;
    private List<ClMatricula> rows;
    //#################################De la pagina
    private List<MbNotasCL> listaTalleres;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
    }

    public List<MbNotasCL> getListaTalleres() {
        return listaTalleres;
    }

    public void setListaTalleres(List<MbNotasCL> listaTalleres) {
        this.listaTalleres = listaTalleres;
    }

    public int getLblTalapeId() {
        return lblTalapeId;
    }

    public void setLblTalapeId(int lblTalapeId) {
        this.lblTalapeId = lblTalapeId;
    }

    public String getLblTalapeNombre() {
        return lblTalapeNombre;
    }

    public void setLblTalapeNombre(String lblTalapeNombre) {
        this.lblTalapeNombre = lblTalapeNombre;
    }

    public String getLblCurNombre() {
        return lblCurNombre;
    }

    public void setLblCurNombre(String lblCurNombre) {
        this.lblCurNombre = lblCurNombre;
    }

    public String getLblModNombre() {
        return lblModNombre;
    }

    public void setLblModNombre(String lblModNombre) {
        this.lblModNombre = lblModNombre;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public Date getRowSecFini() {
        return rowSecFini;
    }

    public void setRowSecFini(Date rowSecFini) {
        this.rowSecFini = rowSecFini;
    }

    public Date getRowSecFfin() {
        return rowSecFfin;
    }

    public void setRowSecFfin(Date rowSecFfin) {
        this.rowSecFfin = rowSecFfin;
    }

    public int getRowMatNum() {
        return rowMatNum;
    }

    public void setRowMatNum(int rowMatNum) {
        this.rowMatNum = rowMatNum;
    }

    public int getRowSecId() {
        return rowSecId;
    }

    public void setRowSecId(int rowSecId) {
        this.rowSecId = rowSecId;
    }

    public String getRowSecCod() {
        return rowSecCod;
    }

    public void setRowSecCod(String rowSecCod) {
        this.rowSecCod = rowSecCod;
    }

    public String getRowSecNom() {
        return rowSecNom;
    }

    public void setRowSecNom(String rowSecNom) {
        this.rowSecNom = rowSecNom;
    }

    public List<ClSisEvaPersonalizado> getColumns() {
        return columns;
    }

    public void setColumns(List<ClSisEvaPersonalizado> columns) {
        this.columns = columns;
    }

    public List<ClMatricula> getRows() {
        return rows;
    }

    public void setRows(List<ClMatricula> rows) {
        this.rows = rows;
    }

    public void seleccion(NodeSelectedEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UITree tree = (UITree) event.getComponent();
        var id = tree.getRowKey().toString();
        int length = id.length();

        if (length >= 7) {
            ObjNodeCLGnral nodo = buscarNodo(id);

            this.setLblTalapeId(nodo.getTalapeId());
            this.setLblModNombre(nodo.getModNombre());
            this.setLblCurNombre(nodo.getCurNombre());
            this.setLblTalapeNombre(nodo.getTalapeNombre());

            seleccionarTaller(this.getLblTalapeId());
        }
    }

    public void seleccionarTaller(int talapeId) {
        HSSeccionCLDAO daoSec = ServiceFinder.findBean("springHibernateDaoCLSeccion", HSSeccionCLDAO.class);
        HSMatriculaCLDAO daoMat = ServiceFinder.findBean("springHibernateDaoCLMatricula", HSMatriculaCLDAO.class);

        List<ClSeccion> lsec = daoSec.seleccionarSecciones(talapeId);
        List<MbNotasCL> l = new ArrayList<>();

        for (var i = 0; i < lsec.size(); i++) {
            ClSeccion sec = lsec.get(i);

            var not = new MbNotasCL();
            not.setLblTalapeId(talapeId);
            not.setRowSecId(sec.getSecId());
            not.setRowSecCod(sec.getSecCodigo());
            not.setRowSecNom(sec.getSecNombre());
            not.setRowSecFini(sec.getSecFinicio());
            not.setRowSecFfin(sec.getSecFfin());

            int tm = daoMat.totalMatriculadosSeccion(sec.getSecId(), talapeId, 0).size();
            not.setRowMatNum(tm);

            l.add(i, not);
        }
        this.setListaTalleres(l);
    }

    public HtmlPanelGrid getHtmlPanel() {
        return htmlPanel;
    }

    public void setHtmlPanel(HtmlPanelGrid htmlPanel) {
        this.htmlPanel = htmlPanel;
    }

    public int getMpNroColumnas() {
        return mpNroColumnas;
    }

    public void setMpNroColumnas(int mpNroColumnas) {
        this.mpNroColumnas = mpNroColumnas;
    }

    public int getMpSecId() {
        return mpSecId;
    }

    public void setMpSecId(int mpSecId) {
        this.mpSecId = mpSecId;
    }

    public int getMpTalapeId() {
        return mpTalapeId;
    }

    public void setMpTalapeId(int mpTalapeId) {
        this.mpTalapeId = mpTalapeId;
    }

    public void asignarNotas(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter uipSec = (UIParameter) event.getComponent().findComponent(PARAM_SECCION_ID);
        UIParameter uipTalape = (UIParameter) event.getComponent().findComponent("p_talape_id");

        this.setMpSecId(Integer.parseInt(String.valueOf(uipSec.getValue())));
        this.setMpTalapeId(Integer.parseInt(String.valueOf(uipTalape.getValue())));

        this.armarRegistro(this.getMpSecId(), this.getMpTalapeId());
    }

    private void armarRegistro(int secId, int talapeId) {
        HSSistemaEvaluacionCLDAO daoSiseva = ServiceFinder.findBean("springHibernateDaoCLSistemaEvaluacion", HSSistemaEvaluacionCLDAO.class);

        HSTallerAperturadoDAO dao = ServiceFinder.findBean("springHibernateDaoCLTallerAperturado", HSTallerAperturadoDAO.class);

        List<ClSisEvaPersonalizado> lc = daoSiseva.seleccionarSisEvaPerClTalape(dao.seleccionarTallerAperturado(talapeId));

        HSMatriculaCLDAO daoMat = ServiceFinder.findBean("springHibernateDaoCLMatricula", HSMatriculaCLDAO.class);
        List<ClMatricula> lr = daoMat.totalMatriculadosSeccion(secId, talapeId, 1);

        this.setRows(lr);
        this.setColumns(lc);

        this.armarTablaNotas(lr, lc, secId);
    }

    private void armarTablaNotas(List<ClMatricula> rows, List<ClSisEvaPersonalizado> columns, int secId) {
        hidden = new ArrayList<>();

        List<UIComponent> childrenList = getHtmlPanel().getChildren();
        childrenList.clear();

        var app = FacesContext.getCurrentInstance().getApplication();

        HtmlOutputText hot = (HtmlOutputText) app.createComponent(HtmlOutputText.COMPONENT_TYPE);
        HtmlInputText hit;
        HtmlInputHidden hih;

        hot.setValue("APELLIDOS Y NOMBRES");
        hot.setStyle("font-weight: bold;");
        childrenList.add(hot);

        this.setMpNroColumnas(columns.size() + 1);

        for (ClSisEvaPersonalizado column : columns) {
            hot = (HtmlOutputText) app.createComponent(HtmlOutputText.COMPONENT_TYPE);
            hot.setValue(column.getSisevaCodigo());
            hot.setStyle("font-weight: bold;");
            childrenList.add(hot);
        }
        HtmlSeparator hse;
        for (var i = 0; i < columns.size() + 1; i++) {
            hse = new HtmlSeparator();
            hse.setAlign("center");
            hse.setWidth("100%");
            hse.setHeight("3px");
            childrenList.add(hse);
        }

        HtmlSpacer hsp;
        for (var i = 0; i < columns.size() + 1; i++) {
            hsp = new HtmlSpacer();
            hsp.setWidth("100%");
            hsp.setHeight("5px");
            childrenList.add(hsp);
        }

        HSNotasCLDAO dao = ServiceFinder.findBean("springHibernateDaoCLNotas", HSNotasCLDAO.class);
        List<ClNota> notas = dao.seleccionarNotas(secId);

        int valorId;
        String valorNota;
        for (var i = 0; i < rows.size(); i++) {
            ClMatricula matricula = rows.get(i);

            var alu = matricula.getClAlumno();
            String alunombres = alu.getAluAppaterno() + " " + alu.getAluApmaterno() + ", " + alu.getAluNombres();

            hot = (HtmlOutputText) app.createComponent(HtmlOutputText.COMPONENT_TYPE);
            hot.setValue((i + 1) + ".- " + alunombres);

            childrenList.add(hot);
            for (var j = 0; j < columns.size(); j++) {
                hit = (HtmlInputText) app.createComponent(HtmlInputText.COMPONENT_TYPE);
                hih = (HtmlInputHidden) app.createComponent(HtmlInputHidden.COMPONENT_TYPE);
                valorNota = "0";
                valorId = 0;
                for (ClNota clnota : notas) {
                    if (clnota.getClAlumno().getAluId().compareTo(rows.get(i).getClAlumno().getAluId()) == 0
                            && clnota.getClSisEvaPersonalizado().getSisevaPerId().compareTo(columns.get(j).getSisevaPerId()) == 0) {

                        valorNota = String.valueOf(clnota.getNotNota());
                        valorId = clnota.getNotId();
                    }
                }

                hih.setId("h" + rows.get(i).getClAlumno().getAluId() + "-" + columns.get(j).getSisevaPerId());
                hih.setValue(valorId);
                if (j % 2 == 0) {
                    hit.setStyle("width:35px;background-color: #ffffff");
                } else {
                    hit.setStyle("width:35px;background-color: #bbbbbb");
                }
                hit.setId("id" + rows.get(i).getClAlumno().getAluId() + "-" + columns.get(j).getSisevaPerId());
                var nota = Float.parseFloat(valorNota);
                if (nota < 10.5) {
                    hit.setStyle("color: #cc0000;" + hit.getStyle());
                } else {
                    hit.setStyle("color: #000099;" + hit.getStyle());
                }
                hit.setValue(valorNota);
                childrenList.add(hit);
                hidden.add(hih);
            }
        }
    }

    public void guardarNotas(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        List<ClNota> notas = new ArrayList<>();
        List<ClMatricula> lrow = this.getRows();
        List<ClSisEvaPersonalizado> lcol = this.getColumns();

        this.setOncomplete("");

        for (ClMatricula clMatricula : lrow) {
            for (ClSisEvaPersonalizado clSisEvaPersonalizado : lcol) {
                String a = "id" + clMatricula.getClAlumno().getAluId() + "-" + clSisEvaPersonalizado.getSisevaPerId();
                String h = "h" + clMatricula.getClAlumno().getAluId() + "-" + clSisEvaPersonalizado.getSisevaPerId();

                var valorNota = ((UIInput) event.getComponent().findComponent(a)).getValue().toString();

                if (Float.parseFloat(valorNota) > 20) {
                    throw new IllegalArgumentException("Error! notas mayores a 20");
                } else {
                    if (valorNota.trim().length() <= 0) {
                        valorNota = "0";
                    }
                    var idNota = 0;
                    for (UIInput hid : hidden) {
                        if (hid.getId().equals(h)) {
                            idNota = Integer.parseInt(hid.getValue().toString());
                        }
                    }

                    var nota = new ClNota();
                    if (idNota != 0) {
                        nota.setNotId(idNota);
                    }
                    nota.setNotActivo("1");
                    nota.setNotNota(Float.parseFloat(valorNota));
                    nota.setClAlumno(clMatricula.getClAlumno());
                    nota.setClSisEvaPersonalizado(clSisEvaPersonalizado);
                    nota.setNotCreacion(new Date());
                    nota.setSecId(this.getMpSecId());

                    notas.add(nota);
                }
            }
        }
        HSNotasCLDAO dao = ServiceFinder.findBean("springHibernateDaoCLNotas", HSNotasCLDAO.class);
        dao.insertarActualizarNotas(notas);
        this.setOncomplete("javascript:alert('Actualizacion satisfactoria');"
                + "Richfaces.hideModalPanel('mpNotasCL');");
    }

}
