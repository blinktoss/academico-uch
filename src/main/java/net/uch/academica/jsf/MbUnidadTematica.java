package net.uch.academica.jsf;

import net.uch.academica.dao.HSContenidoTematicoDAO;
import net.uch.academica.dao.HSPlanCurricularCursoDAO;
import net.uch.academica.dao.HSPlanCurricularDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.configuracion.dao.HSEspecialidadDAO;
import net.uch.configuracion.dao.HSFacultadDAO;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MbUnidadTematica {

    private static final Logger LOG = LoggerFactory.getLogger(MbUnidadTematica.class);

    private static final String COMBO_BOX_ITEM_SELECT_LABEL = "[ Seleccione ]";

    private final HSContenidoTematicoDAO daoContenidoTematico = ServiceFinder.findBean("springHibernateDaoContenidoTematico", HSContenidoTematicoDAO.class);

    private int bFacultad;
    private int bEspecialidad;
    private int bPlanCurricular;
    private String bCiclo;
    private String cicloDescripcion;
    private List<SelectItem> comboCicloS;
    private List<SelectItem> comboPlanCurricularS;
    private String planDescripcion;
    private List<SelectItem> comboFacultadesS;
    private List<SelectItem> comboEspecialidadesS;
    private String especialidadDescripcion;
    private String cursoDescripcion = "";
    private int planCursoId;
    private String ciclo;
    private String nombre;
    private int creditos;
    private String tipo;
    private List<MbUnidadTematica> listaDatos;
    private Integer contentId;
    private String ctTituloU;
    private String ctDescripcionU;
    private int ctSemanasU;
    private boolean ctVigenteU;
    private int detId;
    private String detContenido;
    private String detContenidoAux;
    private int detNroSemana;
    private int nroSemana = 1;
    private List<MbUnidadTematica> listaDatosDetalle;
    private List<MbUnidadTematica> detalleEliminacion;
    private boolean editableb1 = true;
    private boolean editableb2 = false;
    private String mensajes = "";
    private int contadorSemana;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }

        HSFacultadDAO dao = ServiceFinder.findBean("springHibernateDaoFacultad", HSFacultadDAO.class);
        List<AcFacultad> facultades = dao.seleccionarFacultad("", "");
        this.setComboFacultadesS(facultades.stream()
                .map(f -> new SelectItem(f.getId(), f.getFacNombre()))
                .collect(Collectors.toList()));

        this.comboEspecialidadesS = new ArrayList<>();
        this.comboPlanCurricularS = new ArrayList<>();
        this.comboCicloS = new ArrayList<>();
        this.comboCicloS.add(new SelectItem(0, COMBO_BOX_ITEM_SELECT_LABEL));
    }

    public String getMensajes() {
        return mensajes;
    }

    public void setMensajes(String mensajes) {
        this.mensajes = mensajes;
    }

    public List<MbUnidadTematica> getListaDatos() {
        return listaDatos;
    }

    public void setListaDatos(List<MbUnidadTematica> listaDatos) {
        this.listaDatos = listaDatos;
    }

    public void setearFacultadS(ActionEvent event) {
        LOG.debug("[{}] On change Facultad {}", event.getPhaseId(), this.bFacultad);
        if (this.bFacultad == 0) {
            this.comboEspecialidadesS.clear();
        } else {
            HSEspecialidadDAO dao = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            this.setComboEspecialidadesS(dao.seleccionarEspecialidad(this.bFacultad).stream()
                    .map(e -> new SelectItem(e.getId(), e.getEspNombre()))
                    .collect(Collectors.toList()));
        }
        this.comboPlanCurricularS.clear();
        this.comboCicloS.clear();
        this.comboCicloS.add(new SelectItem(0, COMBO_BOX_ITEM_SELECT_LABEL));
    }

    public void setearEspecialidadS(ActionEvent event) {
        LOG.debug("[{}] On change Especialidad {}", event.getPhaseId(), this.bEspecialidad);
        if (this.bEspecialidad == 0) {
            this.comboPlanCurricularS.clear();
            this.comboCicloS.clear();
            this.comboCicloS.add(new SelectItem(0, COMBO_BOX_ITEM_SELECT_LABEL));
        } else {
            HSPlanCurricularDAO dao = ServiceFinder.findBean("springHibernateDaoPlanCurricular", HSPlanCurricularDAO.class);
            this.setComboPlanCurricularS(dao.seleccionarPlanActivo(this.bEspecialidad).stream()
                    .map(pc -> new SelectItem(pc.getId(), pc.getPlanDescripcion()))
                    .collect(Collectors.toList()));
        }
    }

    public void setearPCurricularS(ActionEvent event) {
        LOG.debug("[{}] On change Plan Curricular {}", event.getPhaseId(), this.bPlanCurricular);
        this.comboCicloS.clear();
        if (this.bPlanCurricular == 0) {
            this.comboCicloS.add(new SelectItem(0, COMBO_BOX_ITEM_SELECT_LABEL));
        } else {
            HSCatalogoDAO dao = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
            List<TbCatalogo> lista = dao.seleccionarCatalogo("006");
            HSEspecialidadDAO dao2 = ServiceFinder.findBean("springHibernateDaoEspecialidad", HSEspecialidadDAO.class);
            AcEspecialidad esp = dao2.seleccionarUnaEspecialidad(this.bEspecialidad);
            var nroPeriodos = Integer.parseInt(esp.getEspNroperiodos());
            LOG.trace("{} periodos in speciality {}", nroPeriodos, this.bEspecialidad);
            comboCicloS.add(new SelectItem(99, "[ Todos ]"));
            for (var k = 0; k < nroPeriodos; k++) {
                TbCatalogo c = lista.get(k);
                comboCicloS.add(new SelectItem(c.getCatCodigoGrupo() + c.getCatCodigoElemento(), c.getCatDescripcionElemento()));
            }
        }
    }

    public void seleccionar(ActionEvent event) {
        LOG.debug("[{}] Plan Curricular {} | Ciclo '{}'", event.getPhaseId(), this.bPlanCurricular, this.bCiclo);
        listaDatos = new ArrayList<>();
        List<Object[]> lista;
        HSPlanCurricularCursoDAO dao = ServiceFinder.findBean("springHibernateDaoPlanCurricularCurso", HSPlanCurricularCursoDAO.class);
        if (this.getbPlanCurricular() != 0) {
            if ("99".equals(this.getbCiclo())) {
                lista = dao.seleccionarPlanCurricularCurso(this.getbPlanCurricular());
            } else {
                lista = dao.seleccionarPlanCurricularCurso(this.getbPlanCurricular(), this.getbCiclo());
            }
            for (Object[] obj : lista) {
                var bUT = new MbUnidadTematica();
                bUT.setPlanCursoId(Integer.parseInt(obj[0].toString()));
                bUT.setCiclo(obj[4].toString());
                bUT.setNombre(obj[2].toString());
                bUT.setCreditos(Integer.parseInt(obj[5].toString()));
                bUT.setTipo(obj[6].toString());
                listaDatos.add(bUT);
            }
            this.setListaDatos(listaDatos);
        }
        LOG.info("Total of curricular plans {}", listaDatos.size());
    }

    public void asignar(ActionEvent event) {
        UIParameter id = (UIParameter) event.getComponent().findComponent("p_id");
        var pcId = Integer.parseInt(id.getValue().toString());
        LOG.debug("[{}] Of Curricular Plan Course {}", event.getPhaseId(), pcId);

        this.contadorSemana = 0;
        this.setDetContenido("");
        this.listaDatosDetalle = new ArrayList<>();
        this.detalleEliminacion = new ArrayList<>();
        this.setPlanCursoId(pcId);

        Object[] obj = daoContenidoTematico.seleccionarDatosCT(pcId);
        this.setPlanDescripcion(obj[3].toString());//Plan Curricular
        this.setEspecialidadDescripcion(obj[4].toString());//Especialidad
        this.setCicloDescripcion(obj[5].toString());//Ciclo
        this.setCursoDescripcion(obj[6].toString());//Curso

        AcContenidoTematico ct = daoContenidoTematico.getContenidoTematico(pcId);
        if (ct == null) {
            contentId = null;
            this.setNroSemana(1);
            this.setCtTituloU("");
            this.setCtDescripcionU("");
            this.setCtSemanasU(0);
            this.setCtVigenteU(false);
        } else {
            contentId = ct.getId();
            String titulo = ct.getContemTitulo();
            String descripcion = ct.getContemDescripcion();
            var nroSemanas = Integer.parseInt(ct.getContemNroSemanas());
            String vig = ct.getContemVigente();

            this.setNroSemana(nroSemanas + 1);
            this.setCtTituloU(titulo);
            this.setCtDescripcionU(descripcion);
            this.setCtSemanasU(nroSemanas);
            this.setCtVigenteU(vig.equals("1"));

            List<AcContenidoDetalle> details = daoContenidoTematico.listarDetalle(contentId);
            this.setListaDatosDetalle(details.stream()
                    .map(acContenidoDetalle -> {
                        var bUT = new MbUnidadTematica();
                        bUT.setDetId(acContenidoDetalle.getId());
                        bUT.setDetContenido(acContenidoDetalle.getCondetContenido());
                        bUT.setDetNroSemana(Integer.parseInt(acContenidoDetalle.getCondetSemana()));
                        return bUT;
                    })
                    .collect(Collectors.toList()));
            LOG.info("Total content details {}", listaDatosDetalle.size());
        }
        LOG.trace("Updating Thematic Content '{}'", contentId);
    }

    public void guardar(ActionEvent event) {
        LOG.debug("[{}] registering thematic content '{}'", event.getPhaseId(), this.contentId);
        if (this.getCtTituloU().trim().isEmpty()) {
            this.setMensajes("alert('El Titulo del Contenido Tematico no puede ir vacio')");
        } else if (this.getCtDescripcionU().trim().isEmpty()) {
            this.setMensajes("alert('La Descripcion del Contenido Tematico no puede ir vacio')");
        } else {
            if (!detalleEliminacion.isEmpty()) {
                List<AcContenidoDetalle> listaContenidoDetalleEliminacion = new ArrayList<>();
                for (MbUnidadTematica mbUnidadTematica : detalleEliminacion) {
                    var ct1 = new AcContenidoTematico();
                    ct1.setId(contentId);
                    var contemdet = new AcContenidoDetalle();
                    contemdet.setId(mbUnidadTematica.getDetId());
                    contemdet.setContem(ct1);
                    listaContenidoDetalleEliminacion.add(contemdet);
                    LOG.trace("Thematic Content Detail {} registered to delete", contemdet.getId());
                }
                daoContenidoTematico.eliminarContenidoTematicoDetalle(listaContenidoDetalleEliminacion);
                LOG.info("Thematic Content Details deleted {} of Thematic Content '{}'", listaContenidoDetalleEliminacion.size(), contentId);
            }
            var pc = new AcPlanCurso();
            pc.setId(this.getPlanCursoId());

            var ct = new AcContenidoTematico();
            ct.setId(contentId);
            ct.setContemTitulo(this.getCtTituloU());
            ct.setContemDescripcion(this.getCtDescripcionU());
            ct.setContemVigente(this.isCtVigenteU() ? "1" : "0");
            ct.setContemActivo("1");
            ct.setContemNroSemanas(String.valueOf(listaDatosDetalle.size()));
            ct.setPlancur(pc);

            Set<AcContenidoDetalle> listaContenidoDetalle = new LinkedHashSet<>();
            for (MbUnidadTematica mbUnidadTematica : listaDatosDetalle) {
                var contemdet = new AcContenidoDetalle();
                if (mbUnidadTematica.getDetId() > 0) {
                    contemdet.setId(mbUnidadTematica.getDetId());
                }
                contemdet.setCondetContenido(mbUnidadTematica.getDetContenido());
                contemdet.setCondetSemana(String.valueOf(mbUnidadTematica.getDetNroSemana()));
                contemdet.setContem(ct);
                listaContenidoDetalle.add(contemdet);
            }
            ct.setAcContenidoDetalles(listaContenidoDetalle);
            daoContenidoTematico.insertarContenidoTematico(ct);
            LOG.info("Thematic Content saved/updated {} and {} details", ct.getId(), listaContenidoDetalle.size());
            this.setMensajes("alert('Se guardo Correctamente');Richfaces.hideModalPanel('nuevo')");
        }
    }

    public void editarFilaDetalle(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setEditableb1(false);
        this.setEditableb2(true);
        this.setDetContenidoAux(this.getDetContenido());
    }

    public void grabar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setEditableb1(true);
        this.setEditableb2(false);
    }

    public void cancelar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setEditableb1(true);
        this.setEditableb2(false);
        this.setDetContenido(this.getDetContenidoAux());
    }

    public void agregar(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        int weekCounter = listaDatosDetalle.size() + 1;

        var utematica = new MbUnidadTematica();
        utematica.setDetId(this.contadorSemana);
        utematica.setDetNroSemana(weekCounter);
        utematica.setDetContenido(this.getDetContenido());
        listaDatosDetalle.add(utematica);

        this.setNroSemana(weekCounter + 1);

        this.setDetContenido("");

        this.contadorSemana--;
    }

    public void quitar(ActionEvent event) {
        UIParameter uipTcId = (UIParameter) event.getComponent().findComponent("tc_id");
        var tcId = Integer.parseInt(uipTcId.getValue().toString());
        LOG.debug("[{}] to remove of detail list {}", event.getPhaseId(), tcId);

        int indexToRemove = -1;
        var week = 1;
        for (var i = 0; i < listaDatosDetalle.size(); i++) {
            MbUnidadTematica mbUT = listaDatosDetalle.get(i);
            if (tcId == mbUT.getDetId()) {
                indexToRemove = i;
            } else {
                mbUT.setDetNroSemana(week);
                week++;
            }
        }
        MbUnidadTematica r = listaDatosDetalle.remove(indexToRemove);
        if (r.getDetId() > 0) {
            detalleEliminacion.add(r);
        }
        this.setNroSemana(this.getNroSemana() - 1);
    }

    public int getbFacultad() {
        return bFacultad;
    }

    public void setbFacultad(int bFacultad) {
        this.bFacultad = bFacultad;
    }

    public List<SelectItem> getComboFacultadesS() {
        return comboFacultadesS;
    }

    public void setComboFacultadesS(List<SelectItem> comboFacultadesS) {
        this.comboFacultadesS = comboFacultadesS;
    }

    public int getbEspecialidad() {
        return bEspecialidad;
    }

    public void setbEspecialidad(int bEspecialidad) {
        this.bEspecialidad = bEspecialidad;
    }

    public List<SelectItem> getComboEspecialidadesS() {
        return comboEspecialidadesS;
    }

    public void setComboEspecialidadesS(List<SelectItem> comboEspecialidadesS) {
        this.comboEspecialidadesS = comboEspecialidadesS;
    }

    public int getbPlanCurricular() {
        return bPlanCurricular;
    }

    public void setbPlanCurricular(int bPlanCurricular) {
        this.bPlanCurricular = bPlanCurricular;
    }

    public List<SelectItem> getComboPlanCurricularS() {
        return comboPlanCurricularS;
    }

    public void setComboPlanCurricularS(List<SelectItem> comboPlanCurricularS) {
        this.comboPlanCurricularS = comboPlanCurricularS;
    }

    public String getbCiclo() {
        return bCiclo;
    }

    public void setbCiclo(String bCiclo) {
        this.bCiclo = bCiclo;
    }

    public List<SelectItem> getComboCicloS() {
        return comboCicloS;
    }

    public void setComboCicloS(List<SelectItem> comboCicloS) {
        this.comboCicloS = comboCicloS;
    }

    public int getPlanCursoId() {
        return planCursoId;
    }

    public void setPlanCursoId(int planCursoId) {
        this.planCursoId = planCursoId;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getDetId() {
        return detId;
    }

    public void setDetId(int detId) {
        this.detId = detId;
    }

    public String getDetContenido() {
        return detContenido;
    }

    public void setDetContenido(String detContenido) {
        this.detContenido = detContenido;
    }

    public int getDetNroSemana() {
        return detNroSemana;
    }

    public void setDetNroSemana(int detNroSemana) {
        this.detNroSemana = detNroSemana;
    }

    public List<MbUnidadTematica> getListaDatosDetalle() {
        return listaDatosDetalle;
    }

    public void setListaDatosDetalle(List<MbUnidadTematica> listaDatosDetalle) {
        this.listaDatosDetalle = listaDatosDetalle;
    }

    public String getCtTituloU() {
        return ctTituloU;
    }

    public void setCtTituloU(String ctTituloU) {
        this.ctTituloU = ctTituloU;
    }

    public String getCtDescripcionU() {
        return ctDescripcionU;
    }

    public void setCtDescripcionU(String ctDescripcionU) {
        this.ctDescripcionU = ctDescripcionU;
    }

    public int getCtSemanasU() {
        return ctSemanasU;
    }

    public void setCtSemanasU(int ctSemanasU) {
        this.ctSemanasU = ctSemanasU;
    }

    public boolean isCtVigenteU() {
        return ctVigenteU;
    }

    public void setCtVigenteU(boolean ctVigenteU) {
        this.ctVigenteU = ctVigenteU;
    }

    public boolean isEditableb1() {
        return editableb1;
    }

    public void setEditableb1(boolean editableb1) {
        this.editableb1 = editableb1;
    }

    public boolean isEditableb2() {
        return editableb2;
    }

    public void setEditableb2(boolean editableb2) {
        this.editableb2 = editableb2;
    }

    public String getEspecialidadDescripcion() {
        return especialidadDescripcion;
    }

    public void setEspecialidadDescripcion(String especialidadDescripcion) {
        this.especialidadDescripcion = especialidadDescripcion;
    }

    public String getPlanDescripcion() {
        return planDescripcion;
    }

    public void setPlanDescripcion(String planDescripcion) {
        this.planDescripcion = planDescripcion;
    }

    public String getCicloDescripcion() {
        return cicloDescripcion;
    }

    public void setCicloDescripcion(String cicloDescripcion) {
        this.cicloDescripcion = cicloDescripcion;
    }

    public String getCursoDescripcion() {
        return cursoDescripcion;
    }

    public void setCursoDescripcion(String cursoDescripcion) {
        this.cursoDescripcion = cursoDescripcion;
    }

    public int getNroSemana() {
        return nroSemana;
    }

    public void setNroSemana(int nroSemana) {
        this.nroSemana = nroSemana;
    }

    public String getDetContenidoAux() {
        return detContenidoAux;
    }

    public void setDetContenidoAux(String detContenidoAux) {
        this.detContenidoAux = detContenidoAux;
    }
}
