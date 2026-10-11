package net.uch.academica.jsf;

import net.uch.academica.dao.HSPlanCurricularDAO;
import net.uch.academica.dao.HSPreRequisitosDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.mapping.AcPlanCurso;
import net.uch.mapping.AcPreRequisitos;
import net.uch.mapping.TbCatalogo;
import net.uch.mapping.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.ACTION_IMAGES_DOWN;
import static net.uch.common.Constants.ACTION_IMAGES_UP;

public class MbPreRequisito {

    private static final Logger LOG = LoggerFactory.getLogger(MbPreRequisito.class);

    private final HSPreRequisitosDAO daoPreRequisitos = ServiceFinder.findBean("springHibernateDaoPreRequisitos", HSPreRequisitosDAO.class);

    private String imagen = ACTION_IMAGES_DOWN;
    private SelectItem[] comboFacultad;
    private List<SelectItem> comboEspecialidad;
    private List<SelectItem> comboPlan;
    private List<SelectItem> comboCiclo;
    private List<SelectItem> comboCurso;
    private List<MbPreRequisito> listaCursos;
    private int rowId;
    private String rowCodigo;
    private String rowNombre;
    private String rowCicloValue;
    private String rowCicloId;
    private String rowCreditos;
    private String rowTipo;
    private String rowFacu;
    private int paramIdFacu;
    private String rowEspe;
    private int paramIdEspe;
    private String rowPlan;
    private int paramIdPlan;
    private int rowCicloValor = 1;
    private int xtId;
    private int xtIdCurso;
    private String xtCurso;
    private int xtIdReq;
    private String xtReq;
    private String xtCiclo;
    private int detId;
    private String detReq;
    private String detCiclo;
    private String mpIdCiclo;
    private int mpIdCurso;
    private List<MbPreRequisito> listaPreRequisitos;
    private boolean ver = false;
    private List<MbPreRequisito> detallePreReq;
    private String oncomplete;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        HSFacultadDAO dao = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        setComboFacultad(dao.seleccionarFacultad("", "").stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .toArray(SelectItem[]::new));

        setComboEspecialidad(new ArrayList<>());
        setComboPlan(new ArrayList<>());

        setComboCiclo(new ArrayList<>());
        setComboCurso(new ArrayList<>());
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getRowCodigo() {
        return rowCodigo;
    }

    public void setRowCodigo(String rowCodigo) {
        this.rowCodigo = rowCodigo;
    }

    public String getRowNombre() {
        return rowNombre;
    }

    public void setRowNombre(String rowNombre) {
        this.rowNombre = rowNombre;
    }

    public String getRowCicloValue() {
        return rowCicloValue;
    }

    public void setRowCicloValue(String rowCicloValue) {
        this.rowCicloValue = rowCicloValue;
    }

    public String getRowCicloId() {
        return rowCicloId;
    }

    public void setRowCicloId(String rowCicloId) {
        this.rowCicloId = rowCicloId;
    }

    public String getRowCreditos() {
        return rowCreditos;
    }

    public void setRowCreditos(String rowCreditos) {
        this.rowCreditos = rowCreditos;
    }

    public String getRowTipo() {
        return rowTipo;
    }

    public void setRowTipo(String rowTipo) {
        this.rowTipo = rowTipo;
    }

    public String getRowFacu() {
        return rowFacu;
    }

    public void setRowFacu(String rowFacu) {
        this.rowFacu = rowFacu;
    }

    public int getParamIdFacu() {
        return paramIdFacu;
    }

    public void setParamIdFacu(int paramIdFacu) {
        this.paramIdFacu = paramIdFacu;
    }

    public String getRowEspe() {
        return rowEspe;
    }

    public void setRowEspe(String rowEspe) {
        this.rowEspe = rowEspe;
    }

    public int getParamIdEspe() {
        return paramIdEspe;
    }

    public void setParamIdEspe(int paramIdEspe) {
        this.paramIdEspe = paramIdEspe;
    }

    public String getRowPlan() {
        return rowPlan;
    }

    public void setRowPlan(String rowPlan) {
        this.rowPlan = rowPlan;
    }

    public int getParamIdPlan() {
        return paramIdPlan;
    }

    public void setParamIdPlan(int paramIdPlan) {
        this.paramIdPlan = paramIdPlan;
    }

    public int getRowCicloValor() {
        return rowCicloValor;
    }

    public void setRowCicloValor(int rowCicloValor) {
        this.rowCicloValor = rowCicloValor;
    }

    public String getMpIdCiclo() {
        return mpIdCiclo;
    }

    public void setMpIdCiclo(String mpIdCiclo) {
        this.mpIdCiclo = mpIdCiclo;
    }

    public int getMpIdCurso() {
        return mpIdCurso;
    }

    public void setMpIdCurso(int mpIdCurso) {
        this.mpIdCurso = mpIdCurso;
    }

    public int getXtId() {
        return xtId;
    }

    public void setXtId(int xtId) {
        this.xtId = xtId;
    }

    public int getXtIdCurso() {
        return xtIdCurso;
    }

    public void setXtIdCurso(int xtIdCurso) {
        this.xtIdCurso = xtIdCurso;
    }

    public String getXtCurso() {
        return xtCurso;
    }

    public void setXtCurso(String xtCurso) {
        this.xtCurso = xtCurso;
    }

    public int getXtIdReq() {
        return xtIdReq;
    }

    public void setXtIdReq(int xtIdReq) {
        this.xtIdReq = xtIdReq;
    }

    public String getXtReq() {
        return xtReq;
    }

    public void setXtReq(String xtReq) {
        this.xtReq = xtReq;
    }

    public String getXtCiclo() {
        return xtCiclo;
    }

    public void setXtCiclo(String xtCiclo) {
        this.xtCiclo = xtCiclo;
    }

    public List<MbPreRequisito> getListaPreRequisitos() {
        return listaPreRequisitos;
    }

    public void setListaPreRequisitos(List<MbPreRequisito> listaPreRequisitos) {
        this.listaPreRequisitos = listaPreRequisitos;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public boolean isVer() {
        return ver;
    }

    public void setVer(boolean ver) {
        this.ver = ver;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<MbPreRequisito> getDetallePreReq() {
        return detallePreReq;
    }

    public void setDetallePreReq(List<MbPreRequisito> detallePreReq) {
        this.detallePreReq = detallePreReq;
    }

    public int getDetId() {
        return detId;
    }

    public void setDetId(int detId) {
        this.detId = detId;
    }

    public String getDetReq() {
        return detReq;
    }

    public void setDetReq(String detReq) {
        this.detReq = detReq;
    }

    public String getDetCiclo() {
        return detCiclo;
    }

    public void setDetCiclo(String detCiclo) {
        this.detCiclo = detCiclo;
    }

    public SelectItem[] getComboFacultad() {
        return comboFacultad;
    }

    public void setComboFacultad(SelectItem[] comboFacultad) {
        this.comboFacultad = comboFacultad;
    }

    public void setearFacultadBuscar(ActionEvent event) {
        LOG.debug("[{}] Current facultad: {}", event.getPhaseId(), paramIdFacu);
        comboEspecialidad.clear();
        comboPlan.clear();
        if (paramIdFacu != 0) {
            HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            this.comboEspecialidad.addAll(dao.seleccionarEspecialidad(paramIdFacu).stream()
                    .map(e -> new SelectItem(e.getId(), e.getEspNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public void setearEspecialidadBuscar(ActionEvent event) {
        LOG.debug("[{}] Current especialidad: {}", event.getPhaseId(), paramIdEspe);
        comboPlan.clear();
        if (paramIdEspe != 0) {
            HSPlanCurricularDAO dao = ServiceFinder.findBean("springHibernateDaoPlanCurricular", HSPlanCurricularDAO.class);
            this.comboPlan.addAll(dao.seleccionarPlanActivo(paramIdEspe).stream()
                    .map(pc -> new SelectItem(pc.getId(), pc.getPlanDescripcion()))
                    .collect(Collectors.toList()));
            LOG.info("Total {} planes curriculares", comboPlan.size());
        }
    }

    public List<SelectItem> getComboEspecialidad() {
        return comboEspecialidad;
    }

    public void setComboEspecialidad(List<SelectItem> comboEspecialidad) {
        this.comboEspecialidad = comboEspecialidad;
    }

    public List<SelectItem> getComboPlan() {
        return comboPlan;
    }

    public void setComboPlan(List<SelectItem> comboPlan) {
        this.comboPlan = comboPlan;
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Looking for courses of plan curricular '{}'", event.getPhaseId(), this.paramIdPlan);
        if (paramIdPlan == 0) {
            this.setOncomplete("alert('Debe seleccionar un Plan Curricular')");
        } else {
            this.setOncomplete("");
            listaPreRequisitos = new ArrayList<>();
            List<AcPlanCurso> listax = daoPreRequisitos.cursoDet(this.getParamIdPlan());
            HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
            MbPreRequisito bA;
            for (AcPlanCurso acPlanCurso : listax) {
                bA = new MbPreRequisito();
                bA.setRowId(acPlanCurso.getId());
                bA.setRowCodigo(acPlanCurso.getCur().getCurCodigo());
                bA.setRowNombre(acPlanCurso.getCur().getCurNombre());
                bA.setRowCreditos(acPlanCurso.getPlancurCredito());
                bA.setRowCicloId(acPlanCurso.getPlancurCiclo());
                bA.setRowCicloValue(daoCatalogo.seleccionarDescripcion(acPlanCurso.getPlancurCiclo()));
                bA.setParamIdPlan(acPlanCurso.getPlan().getId());
                bA.setRowPlan(acPlanCurso.getPlan().getPlanDescripcion());
                bA.setParamIdEspe(acPlanCurso.getPlan().getEsp().getId());
                bA.setRowEspe(acPlanCurso.getPlan().getEsp().getEspNombre());
                bA.setParamIdFacu(acPlanCurso.getPlan().getEsp().getFac().getId());
                bA.setRowFacu(acPlanCurso.getPlan().getEsp().getFac().getFacNombre());
                bA.setRowTipo(daoCatalogo.seleccionarDescripcion(acPlanCurso.getPlancurTipo()));
                bA.setRowCicloValor(Integer.parseInt(daoCatalogo.seleccionarValor(acPlanCurso.getPlancurCiclo())));
                Set<AcPreRequisitos> preRequisitos = acPlanCurso.getAcPreRequisitosByPlancur();
                List<AcPreRequisitos> list = Collections.synchronizedList(new LinkedList<>(preRequisitos));
                List<MbPreRequisito> a = new ArrayList<>();
                if (!list.isEmpty()) {
                    MbPreRequisito preReq;
                    for (AcPreRequisitos acPreRequisitos : list) {
                        preReq = new MbPreRequisito();
                        preReq.setDetId(acPreRequisitos.getId());
                        preReq.setDetReq(acPreRequisitos.getPlancurIdRequisito().getCur().getCurNombre());
                        preReq.setDetCiclo(daoCatalogo.seleccionarDescripcion(acPreRequisitos.getPlancurIdRequisito().getPlancurCiclo()));

                        a.add(preReq);
                    }
                }
                LOG.trace("{} >> {} >> {}", bA.getRowCicloValue(), bA.getRowNombre(), a.size());
                bA.setDetallePreReq(a);
                listaPreRequisitos.add(bA);
            }
            this.setListaPreRequisitos(listaPreRequisitos);
            LOG.info("Total {} cursos asignados al plan", this.getListaPreRequisitos().size());
        }
    }

    public void editarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        mpIdCiclo = "0";
        oncomplete = "";
        UIParameter valor = (UIParameter) event.getComponent().findComponent("p_valor");
        UIParameter id = (UIParameter) event.getComponent().findComponent("p_id");
        UIParameter codigo = (UIParameter) event.getComponent().findComponent("p_codigo");
        UIParameter nombre = (UIParameter) event.getComponent().findComponent("p_nombre");
        UIParameter ciclo = (UIParameter) event.getComponent().findComponent("p_ciclo");
        UIParameter especialidad = (UIParameter) event.getComponent().findComponent("p_especialidad");
        UIParameter idEspecialidad = (UIParameter) event.getComponent().findComponent("p_id_especialidad");
        UIParameter facultad = (UIParameter) event.getComponent().findComponent("p_facultad");
        UIParameter plan = (UIParameter) event.getComponent().findComponent("p_plan");
        UIParameter idPlan = (UIParameter) event.getComponent().findComponent("p_id_plan");
        UIParameter idCiclo = (UIParameter) event.getComponent().findComponent("p_id_ciclo");
        rowCicloValor = Integer.parseInt(valor.getValue().toString());
        rowId = Integer.parseInt(id.getValue().toString());
        rowCodigo = codigo.getValue().toString();
        rowNombre = nombre.getValue().toString();
        rowCicloValue = ciclo.getValue().toString();
        rowEspe = especialidad.getValue().toString();
        paramIdEspe = Integer.parseInt(idEspecialidad.getValue().toString());
        rowFacu = facultad.getValue().toString();
        rowPlan = plan.getValue().toString();
        paramIdPlan = Integer.parseInt(idPlan.getValue().toString());
        rowCicloId = idCiclo.getValue().toString();

        fillComboCiclo();
        this.comboCurso.clear();
        fillListaCursos();
    }

    public void setearCiclo(ActionEvent event) {
        LOG.debug("[{}] Current ciclo: {}", event.getPhaseId(), mpIdCiclo);
        if ("0".equals(mpIdCiclo)) {
            this.comboCurso.clear();
        } else {
            fillComboCursos(paramIdPlan, mpIdCiclo);
        }
    }

    public void fillComboCiclo() {
        LOG.debug("Looking for ciclo: '{}'", this.rowCicloValor);
        HSCatalogoDAO daoz = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        List<TbCatalogo> lista = daoz.seleccionarCatalogo("006");

        comboCiclo.clear();
        comboCiclo.add(new SelectItem("0", "[  ]"));

        for (var i = 0; i < rowCicloValor - 1; i++) {
            comboCiclo.add(new SelectItem(lista.get(i).getCatCodigoGrupo() + lista.get(i).getCatCodigoElemento(), lista.get(i).getCatDescripcionElemento()));
        }
        LOG.info("Total {} ciclos", comboCiclo.size());
    }

    public void fillComboCursos(int planId, String ciclo) {
        LOG.debug("Looking for available cursos of plan '{}' for ciclo '{}'", planId, ciclo);
        List<AcPlanCurso> lista = daoPreRequisitos.seleccionarCurso(planId, ciclo);
        comboCurso = new ArrayList<>();
        for (AcPlanCurso acPlanCurso : lista) {
            comboCurso.add(new SelectItem(acPlanCurso.getId(), acPlanCurso.getCur().getCurNombre()));
        }
        this.setComboCurso(comboCurso);
        LOG.info("Total {} cursos disponibles", lista.size());
    }

    public void fillListaCursos() {
        LOG.debug("Looking for pre requisitos of curso '{}'", this.rowId);
        List<MbPreRequisito> grupos = new ArrayList<>();
        MbPreRequisito cursos;
        List<Object[]> lista = daoPreRequisitos.seleccionarPreReq(this.getRowId());
        for (Object[] bC : lista) {
            cursos = new MbPreRequisito();
            cursos.setXtId(Integer.parseInt(bC[0].toString()));
            cursos.setXtIdCurso(Integer.parseInt(bC[1].toString()));
            cursos.setXtCurso(bC[2].toString());
            cursos.setXtIdReq(Integer.parseInt(bC[3].toString()));
            cursos.setXtReq(bC[4].toString());
            cursos.setXtCiclo(bC[5].toString());
            grupos.add(cursos);
        }
        this.setListaCursos(grupos);
        LOG.info("Total {} pre requisitos", grupos.size());
    }

    public List<SelectItem> getComboCiclo() {
        return comboCiclo;
    }

    public void setComboCiclo(List<SelectItem> comboCiclo) {
        this.comboCiclo = comboCiclo;
    }

    public List<SelectItem> getComboCurso() {
        return comboCurso;
    }

    public void setComboCurso(List<SelectItem> comboCurso) {
        this.comboCurso = comboCurso;
    }

    public void agregarFila(ActionEvent event) {
        LOG.debug("[{}] Adding on course '{}' the pre requisito '{}' of ciclo '{}'",
                event.getPhaseId(), this.rowId, this.mpIdCurso, this.mpIdCiclo);

        if ("0".equals(this.getMpIdCiclo())) {
            this.setOncomplete("alert('Seleccione un ciclo')");
        } else if (this.getMpIdCurso() == 0) {
            this.setOncomplete("alert('Seleccione un curso')");
        } else {
            var preRequisito = new AcPreRequisitos();
            List<Object[]> lista = daoPreRequisitos.verificarRepitencia(this.getRowId(), this.getMpIdCurso());
            if (lista.isEmpty()) {
                preRequisito.setId(this.getXtId());
                var plancur = new AcPlanCurso();
                plancur.setId(this.getRowId());
                preRequisito.setPlancur(plancur);
                var plancurRequisito = new AcPlanCurso();
                plancurRequisito.setId(this.getMpIdCurso());
                preRequisito.setPlancurIdRequisito(plancurRequisito);
                preRequisito.setPreReqActivo("1");
                daoPreRequisitos.insertarPreRequisitos(preRequisito);
                LOG.info("Pre requisito saved {}", preRequisito.getId());
                this.setOncomplete("alert('Se creo una prerequisito correctamente')");

                fillListaCursos();
            } else {
                this.setOncomplete("alert('El curso ya fue asignado como Pre Requisito')");
            }
        }
    }

    public List<MbPreRequisito> getListaCursos() {
        return listaCursos;
    }

    public void setListaCursos(List<MbPreRequisito> listaCursos) {
        this.listaCursos = listaCursos;
    }

    public void quitarFila(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter delete = (UIParameter) event.getComponent().findComponent("p_id1");
        var id = Integer.valueOf(delete.getValue().toString());

        daoPreRequisitos.eliminarPreRequisitos(id);

        fillListaCursos();
    }

    public void openSubTable(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.isVer()) {
            this.setVer(false);
            this.setImagen(ACTION_IMAGES_DOWN);
        } else {
            this.setVer(true);
            this.setImagen(ACTION_IMAGES_UP);
        }
    }
}
