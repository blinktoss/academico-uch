/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uch.cursolibre.jsf;

import net.uch.academica.dao.HSDocenteDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSAulaDAO;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.cursolibre.dao.*;
import net.uch.cursolibre.jsf.beans.BeanClAlumno;
import net.uch.cursolibre.jsf.beans.BeanClHoraria;
import net.uch.cursolibre.jsf.beans.BeanClSeccion;
import net.uch.cursolibre.jsf.beans.ObjNodeCLGnral;
import net.uch.mapping.*;
import net.uch.mapping.base.BaseClHoraria;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

/**
 * @author cesardl
 */
public class MbSeccionCL extends BaseMbViewTreeCL {

    private static final Logger LOG = LoggerFactory.getLogger(MbSeccionCL.class);

    //Constantes
    private static final int EXCEDE_CANTIDAD_ALUMNOS = 1;
    private static final int PERTENECE_AL_MISMO_CURSO = 2;

    private final HSSeccionCLDAO daoCLSeccion = ServiceFinder.findBean("springHibernateDaoCLSeccion", HSSeccionCLDAO.class);
    private final HSHorarioDAO daoCLHorario = ServiceFinder.findBean("springHibernateDaoCLHorario", HSHorarioDAO.class);

    //General
    private int talapeId;
    private String talapeNombre;
    private int curId;
    private String curNombre;
    private int modId;
    private String modNombre;
    private List<BeanClSeccion> listaSecciones;
    private String oncomplete;
    private String style;
    private int mpTalDestId;
    private int mpSecDestId;
    private int mpEstpagDestId;
    private List<SelectItem> mpTalDestDesc;
    private List<SelectItem> mpSecDestDesc;
    private List<SelectItem> mpEstpagDestDesc;
    //Detalle de horaria
    private BeanClHoraria horaria;
    private SelectItem[] aulas;
    private SelectItem[] profesores;
    private SelectItem[] tipoClase;
    private SelectItem[] dias;
    private SelectItem[] meridianos;
    private List<BeanClHoraria> mpnListaHoraria;
    private List<BeanClHoraria> mpqListaHoraria;
    //Detalles de seccion
    private int mpSecId;
    private String mpSecDetalle;
    private BeanClSeccion seccion;
    private SelectItem[] turnos;
    private List<BeanClAlumno> listaAlumnosMatriculados;

    /**
     * Creates a new instance of bSeccionCL
     */
    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        this.talapeId = 0;
        this.seccion = new BeanClSeccion(0);
        this.horaria = new BeanClHoraria(0);
        this.setListaSecciones(new ArrayList<>());
        this.setMpnListaHoraria(new ArrayList<>());
        this.mpqListaHoraria = new ArrayList<>();

        HSAulaDAO daoAula = ServiceFinder.findBean("springHibernateDaoAula", HSAulaDAO.class);
        setAulas(daoAula.seleccionarAula().stream()
                .map(a -> new SelectItem(a.getId(), a.getAulDes()))
                .toArray(SelectItem[]::new));

        HSDocenteDAO daoDocente = ServiceFinder.findBean("springHibernateDaoDocente", HSDocenteDAO.class);
        setProfesores(daoDocente.seleccionarDocente().stream()
                .map(d -> new SelectItem(d.getId(), d.getDocNombre()))
                .toArray(SelectItem[]::new));

        HSCatalogoDAO daoCatalogo = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        setTipoClase(daoCatalogo.seleccionarCatalogo("018").stream()
                .map(tc -> new SelectItem(tc.getCatCodigoGrupo() + tc.getCatCodigoElemento(), tc.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        setDias(daoCatalogo.seleccionarCatalogo("016").stream()
                .map(d -> new SelectItem(d.getCatCodigoGrupo() + d.getCatCodigoElemento(), d.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        var tmpMeridianos = new SelectItem[2];
        tmpMeridianos[0] = new SelectItem("AM", "A.M.");
        tmpMeridianos[1] = new SelectItem("PM", "P.M.");
        setMeridianos(tmpMeridianos);

        var tmpTurnos = new SelectItem[3];
        tmpTurnos[0] = new SelectItem("M", "MAÑANA");
        tmpTurnos[1] = new SelectItem("T", "TARDE");
        tmpTurnos[2] = new SelectItem("N", "NOCHE");
        setTurnos(tmpTurnos);

        setMpTalDestDesc(new ArrayList<>());
        setMpSecDestDesc(new ArrayList<>());
        setMpEstpagDestDesc(new ArrayList<>());
    }

    public void seleccion(NodeSelectedEvent event) {
        UITree tree = (UITree) event.getComponent();
        var id = tree.getRowKey().toString();
        int length = id.length();
        LOG.debug("[{}] Selected node '{}'", event.getPhaseId(), id);

        if (length >= 7) {
            ObjNodeCLGnral nodo = buscarNodo(id);

            this.setTalapeId(nodo.getTalapeId());
            this.setTalapeNombre(nodo.getTalapeNombre());
            this.setCurId(nodo.getCurId());
            this.setCurNombre(nodo.getCurNombre());
            this.setModId(nodo.getModId());
            this.setModNombre(nodo.getModNombre());

            seleccionarTaller(this.getTalapeId());
        }
    }

    public void seleccionarTaller(int pTalapeId) {
        List<ClSeccion> lsec = daoCLSeccion.seleccionarSecciones(pTalapeId);
        this.listaSecciones = new ArrayList<>();
        for (ClSeccion tmpSec : lsec) {
            var tmp = new BeanClSeccion(tmpSec);
            if (daoCLHorario.seleccionarHorario(tmpSec.getSecId()).isEmpty()) {
                tmp.setImagenHorario("/Imagenes/actions/horario_gris.png");
            } else {
                tmp.setImagenHorario("/Imagenes/actions/horario.png");
            }
            tmp.setCantMatriculados(daoCLHorario.matriculadosSeccion(tmpSec.getSecId()).size());
            this.addListaSecciones(tmp);
        }
    }

    public void seleccionarSeccion(ActionEvent event) {
        LOG.debug("[{}] for taller aperturado '{}'", event.getPhaseId(), talapeId);
        UIParameter component = (UIParameter) event.getComponent().findComponent(PARAM_SECCION_ID);

        if (talapeId == 0) {
            this.setOncomplete("alert('Seleccione un taller')");
        } else {
            if (component == null) {
                this.seccion = new BeanClSeccion(this.talapeId);
            } else {
                var pSecId = Integer.parseInt(component.getValue().toString());

                this.seccion = new BeanClSeccion(daoCLSeccion.seleccionarSeccion(pSecId));
            }
            this.setOncomplete("Richfaces.showModalPanel('mpSeccion',{top:80})");
        }
    }

    public void verificarEliminacion(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var uipSecId = Integer.parseInt(((UIParameter) event.getComponent().findComponent(PARAM_SECCION_ID)).getValue().toString());
        var uipSecDetalle = ((UIParameter) event.getComponent().findComponent(PARAM_SECCION_DETALLE)).getValue().toString();

        this.setOncomplete("");
        if (daoCLSeccion.existenMatriculasSeccion(uipSecId)) {
            this.setOncomplete("alert('Es imposible eliminar la seccion debido a que registra alumnos.');");
        } else {
            this.setMpSecId(uipSecId);
            this.setMpSecDetalle(uipSecDetalle);
            this.setOncomplete("Richfaces.showModalPanel('mpEliminar');");
        }
    }

    public void eliminarSeccion(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        daoCLSeccion.eliminarSeccion(this.getMpSecId());
        LOG.info("Seccion CL deleted {}", this.getMpSecId());

        seleccionarTaller(this.getTalapeId());
        this.setMpSecId(0);
        this.setMpSecDetalle("");
    }

    public void insertarActualizarSeccion(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var mensaje = "";
        this.setOncomplete(mensaje);
        if (this.seccion.esValido()) {
            var clSeccion = this.seccion.getSeccion();
            if (this.seccion.getSecId() == 0) {
                daoCLSeccion.insertarSeccion(clSeccion);
                LOG.info("Seccion CL saved {}", clSeccion.getSecId());
                mensaje = "alert('Registro exitoso.');";
            } else {
                daoCLSeccion.actualizarSeccion(clSeccion);
                LOG.info("Seccion CL updated {}", clSeccion.getSecId());
                mensaje = "alert('Actualizacion exitosa.');";
            }
            mensaje = mensaje.concat("Richfaces.hideModalPanel('mpSeccion');");
            this.seccion.setSecId(clSeccion.getSecId());
        } else {
            mensaje = "alert('Todos los campos son obligatorios.');";
        }
        this.setOncomplete(mensaje);
        seleccionarTaller(this.getTalapeId());
    }

    public void cargarHorario(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        HSCatalogoDAO daoC = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);

        var uipSecId = Integer.parseInt(((UIParameter) event.getComponent().findComponent(PARAM_SECCION_ID)).getValue().toString());
        var uipSecDetalle = ((UIParameter) event.getComponent().findComponent(PARAM_SECCION_DETALLE)).getValue().toString();

        this.setMpSecId(uipSecId);
        this.setMpSecDetalle(uipSecDetalle);
        this.horaria = new BeanClHoraria(uipSecId);
        this.mpnListaHoraria = new ArrayList<>();
        this.mpqListaHoraria = new ArrayList<>();
        List<ClHoraria> horarias = daoCLHorario.seleccionarHorario(uipSecId);
        for (var i = 0; i < horarias.size(); i++) {
            ClHoraria tmpHor = horarias.get(i);

            var tmp = new BeanClHoraria(tmpHor);
            tmp.setPosicion(i);
            tmp.setHorDiaValue(daoC.seleccionarDescripcion(tmpHor.getHorDia()));
            tmp.setHorTipoClaseValue(daoC.seleccionarDescripcion(tmpHor.getHorTipoClase()));
            tmp.setEditable(false);

            this.addnListaHoraria(tmp);
        }
    }

    public void agregarHorario(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");

        if (this.horaria.esValido()) {
            this.horaria.actualizarDatos(this.getMpSecId());
            this.horaria.setPosicion(this.mpnListaHoraria.size());
            this.addnListaHoraria(horaria);

            this.horaria = new BeanClHoraria(this.getMpSecId());
        } else {
            this.setOncomplete("alert('Todos los campos son obligatorios.')");
        }
    }

    public void removerHorario(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var pHorPos = Integer.parseInt(((UIParameter) event.getComponent().findComponent("p_hor_pos")).getValue().toString());

        BeanClHoraria r = this.mpnListaHoraria.remove(pHorPos);
        if (r.getHorId() != null) {
            this.addqListaHoraria(r);
        }
        // reorder
        for (var i = 0; i < this.mpnListaHoraria.size(); i++) {
            this.mpnListaHoraria.get(i).setPosicion(i);
        }
    }

    public boolean edicionActivada() {
        for (BeanClHoraria bean : mpnListaHoraria) {
            if (bean.isEditable()) {
                return true;
            }
        }
        return false;
    }

    public void guardarHorarios(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (this.mpnListaHoraria.isEmpty()) {
            this.setOncomplete("alert('Debe ingresar los horarios para Guardar.')");
        } else if (edicionActivada()) {
            this.setOncomplete("alert('Termine de editar los horarios.')");
        } else {
            List<ClHoraria> listaHorarios = new ArrayList<>();
            for (BeanClHoraria tmp_horaria : mpnListaHoraria) {
                listaHorarios.add(tmp_horaria.getClHoraria());
            }
            daoCLHorario.insertarActualizarHorarios(listaHorarios);
            if (!mpqListaHoraria.isEmpty()) {
                List<Integer> listaHorElim = mpqListaHoraria.stream().map(BaseClHoraria::getHorId).collect(Collectors.toList());
                daoCLHorario.eliminarHorarios(listaHorElim);
            }
            this.setOncomplete("alert('Generacion de horarios satisfactoria.');"
                    + "Richfaces.hideModalPanel('mpHoraria');");
            seleccionarTaller(this.getTalapeId());
        }
    }

    public void cargarMatriculados(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        var uipSecId = Integer.parseInt(((UIParameter) event.getComponent().findComponent(PARAM_SECCION_ID)).getValue().toString());
        var uipSecDetalle = ((UIParameter) event.getComponent().findComponent(PARAM_SECCION_DETALLE)).getValue().toString();

        this.setMpSecDetalle(uipSecDetalle);
        this.listaAlumnosMatriculados = new ArrayList<>();

        List<ClAlumno> lista = daoCLHorario.matriculadosSeccion(uipSecId);
        if (lista.isEmpty()) {
            this.setStyle("background-color: gray;");
        } else {
            this.setStyle("");
            this.mpSecId = uipSecId;
            this.mpTalDestId = 0;
            this.mpSecDestId = 0;
            this.mpEstpagDestId = 0;

            mpTalDestDesc.clear();
            HSTallerAperturadoDAO daoTA = ServiceFinder.findBean("springHibernateDaoCLTallerAperturado", HSTallerAperturadoDAO.class);
            mpTalDestDesc.addAll(daoTA.seleccionarTalleresAperturadosPorModulo(this.getModId(), this.getCurId()).stream()
                    .map(talape -> new SelectItem(talape.getClTaller().getTalId(), talape.getTalapeDescripcion()))
                    .collect(Collectors.toList()));

            for (var i = 0; i < lista.size(); i++) {
                var tmpAlu = lista.get(i);
                var alu = new BeanClAlumno(tmpAlu);
                alu.setAluContador(i + 1);

                this.addListaAlumnosMatriculados(alu);
            }
        }
    }

    public void guardarMatriculados(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");

        if (this.getMpSecDestId() == 0) {
            this.setOncomplete("alert('Seleccione una seccion.');");
        } else if (this.getMpEstpagDestId() == 0) {
            this.setOncomplete("alert('Seleccione una estructura de pago.');");
        } else {
            var tmpSec = daoCLSeccion.seleccionarSeccion(this.getMpSecDestId());
            if (validaciones(EXCEDE_CANTIDAD_ALUMNOS, tmpSec)) {
                this.setOncomplete("alert('Muchos alumnos para matricular.');");
            } else if (validaciones(PERTENECE_AL_MISMO_CURSO, tmpSec)) {
                this.setOncomplete("alert('La seccion pertenece al mismo curso.');");
            } else {
                perfomEnrollmentsToSection(tmpSec);
            }
        }
    }

    private void perfomEnrollmentsToSection(ClSeccion tmpSec) {
        List<ClMatricula> matriculas = new ArrayList<>();
        HSMatriculaCLDAO daoM = ServiceFinder.findBean("springHibernateDaoCLMatricula", HSMatriculaCLDAO.class);
        for (BeanClAlumno beanAlumno : listaAlumnosMatriculados) {
            if (beanAlumno.isAluMatriculado() && !daoM.estaMatriculado(beanAlumno.getAluId(), tmpSec.getSecId())) {
                var alu = new ClAlumno();
                alu.setAluId(beanAlumno.getAluId());

                var mat = new ClMatricula();
                mat.setClAlumno(alu);
                mat.setMatActivo("1");
                mat.setMatCodigo("MAT-BL");
                mat.setMatFecha(new Timestamp(new Date().getTime()));
                mat.setMatTipo(CAT_TIPO_MATRICULA_PRE_MATRICULA);
                mat.setUsuId(ServiceFinder.getLoggedUser().getRowUsuId());

                Set<ClMatriculaTaller> mattals = new LinkedHashSet<>(1);
                var mattal = new ClMatriculaTaller();
                mattal.setMattalActivo("1");
                mattal.setMattalEstado("1");
                mattal.setMattalObs("Alumno matriculado por bloque");
                mattal.setClMatricula(mat);
                mattal.setClSeccion(tmpSec);
                mattals.add(mattal);

                mat.setClMatriculaTallers(mattals);

                matriculas.add(mat);
            }
        }

        daoM.insertarMatriculas(matriculas);

        HSEstructuraPagoDAO daoEP = ServiceFinder.findBean("springHibernateDaoCLEstructuraPago", HSEstructuraPagoDAO.class);
        List<ClEstructuraPagosDetalle> detalles = daoEP.seleccionarDetalle(this.getMpEstpagDestId());
        List<ClAlumnoTarifa> alumnoTarifas = new ArrayList<>();
        int tmpTalId = tmpSec.getClTallerAperturado().getClTaller().getTalId();
        for (ClMatricula mat : matriculas) {
            for (ClEstructuraPagosDetalle estpagdet : detalles) {
                if (estpagdet.getTalId() == tmpTalId) {
                    var alutar = new ClAlumnoTarifa();
                    alutar.setAlutarActivo("1");
                    alutar.setAlutarAluTipo(CAT_TIPO_CLIENTE_ALUMNO_CL);
                    alutar.setAlutarEstado("0");
                    alutar.setAlutarFechaPago(tmpSec.getSecFinicio());
                    alutar.setAlutarFechaProrroga(tmpSec.getSecFinicio());
                    alutar.setAlutarMonto(estpagdet.getEstpagdetMonto());
                    alutar.setClAlumno(mat.getClAlumno());
                    alutar.setClEstructuraPagosDetalle(estpagdet);
                    alutar.setConpagId(estpagdet.getAdConceptoPago().getId());
                    alutar.setMatId(mat.getMatId());
                    alutar.setSecId(tmpSec.getSecId());

                    alumnoTarifas.add(alutar);
                    break;
                }
            }
        }
        HSAlumnoTarifaCLDAO daoAT = ServiceFinder.findBean("springHibernateDaoCLAlumnoTarifa", HSAlumnoTarifaCLDAO.class);
        daoAT.generarAlumnosTarifa(alumnoTarifas);
        this.setOncomplete("alert('Alumnos matriculados con exito en el taller " + tmpSec.getClTallerAperturado().getTalapeDescripcion() + ".');"
                + "Richfaces.hideModalPanel('mpMatriculados');");
    }

    private boolean validaciones(int caso, ClSeccion sec) {
        switch (caso) {
            case EXCEDE_CANTIDAD_ALUMNOS:
                int alumnosMatriculados = daoCLHorario.matriculadosSeccion(this.getMpSecDestId()).size();
                int totalAlumnos = getTotalParaMatricular() + alumnosMatriculados;
                return sec.getSecVacMax() < totalAlumnos;

            case PERTENECE_AL_MISMO_CURSO:
                int tmpCurId = sec.getClTallerAperturado().getClTaller().getClCurso().getCurId();
                return tmpCurId == this.getCurId();

            default:
                return false;
        }
    }

    public int getModId() {
        return modId;
    }

    public void setModId(int modId) {
        this.modId = modId;
    }

    public int getCurId() {
        return curId;
    }

    public void setCurId(int curId) {
        this.curId = curId;
    }

    public String getCurNombre() {
        return curNombre;
    }

    public void setCurNombre(String curNombre) {
        this.curNombre = curNombre;
    }

    public String getModNombre() {
        return modNombre;
    }

    public void setModNombre(String modNombre) {
        this.modNombre = modNombre;
    }

    public String getTalapeNombre() {
        return talapeNombre;
    }

    public void setTalapeNombre(String talapeNombre) {
        this.talapeNombre = talapeNombre;
    }

    public int getTalapeId() {
        return talapeId;
    }

    public void setTalapeId(int talapeId) {
        this.talapeId = talapeId;
    }

    public void addListaSecciones(BeanClSeccion tmp) {
        this.listaSecciones.add(tmp);
    }

    public List<BeanClSeccion> getListaSecciones() {
        return listaSecciones;
    }

    public void setListaSecciones(List<BeanClSeccion> listaSecciones) {
        this.listaSecciones = listaSecciones;
    }

    public void addnListaHoraria(BeanClHoraria tmp) {
        this.mpnListaHoraria.add(tmp);
    }

    public List<BeanClHoraria> getMpnListaHoraria() {
        return mpnListaHoraria;
    }

    public void setMpnListaHoraria(List<BeanClHoraria> mpnListaHoraria) {
        this.mpnListaHoraria = mpnListaHoraria;
    }

    public void addqListaHoraria(BeanClHoraria tmp) {
        this.mpqListaHoraria.add(tmp);
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public List<SelectItem> getMpTalDestDesc() {
        return mpTalDestDesc;
    }

    public void setMpTalDestDesc(List<SelectItem> mpTalDestDesc) {
        this.mpTalDestDesc = mpTalDestDesc;
    }

    public void loadSeccionesFromTaller(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.mpSecDestId = 0;
        this.mpEstpagDestId = 0;
        this.mpSecDestDesc.clear();
        this.mpEstpagDestDesc.clear();
        if (this.getMpTalDestId() != 0) {
            mpSecDestDesc.addAll(daoCLSeccion.listarSeccionXTaller(this.getMpTalDestId()).stream()
                    .map(sec -> new SelectItem(sec.getSecId(), sec.getSecCodigo() + " | " + sec.getSecNombre()))
                    .collect(Collectors.toList()));

            HSEstructuraPagoDAO daoEP = ServiceFinder.findBean("springHibernateDaoCLEstructuraPago", HSEstructuraPagoDAO.class);
            mpEstpagDestDesc.addAll(daoEP.seleccionarEstructurasXTaller(this.getMpTalDestId()).stream()
                    .map(estpag -> new SelectItem(estpag.getEstpagId(), estpag.getEstpagNombre()))
                    .collect(Collectors.toList()));
        }
    }

    public int getMpTalDestId() {
        return mpTalDestId;
    }

    public void setMpTalDestId(int mpTalDestId) {
        this.mpTalDestId = mpTalDestId;
    }

    public List<SelectItem> getMpSecDestDesc() {
        return mpSecDestDesc;
    }

    public void setMpSecDestDesc(List<SelectItem> mpSecDestDesc) {
        this.mpSecDestDesc = mpSecDestDesc;
    }

    public int getMpSecDestId() {
        return mpSecDestId;
    }

    public void setMpSecDestId(int mpSecDestId) {
        this.mpSecDestId = mpSecDestId;
    }

    public List<SelectItem> getMpEstpagDestDesc() {
        return mpEstpagDestDesc;
    }

    public void setMpEstpagDestDesc(List<SelectItem> mpEstpagDestDesc) {
        this.mpEstpagDestDesc = mpEstpagDestDesc;
    }

    public int getMpEstpagDestId() {
        return mpEstpagDestId;
    }

    public void setMpEstpagDestId(int mpEstpagDestId) {
        this.mpEstpagDestId = mpEstpagDestId;
    }

    public int getMpSecId() {
        return mpSecId;
    }

    public void setMpSecId(int mpSecId) {
        this.mpSecId = mpSecId;
    }

    public String getMpSecDetalle() {
        return mpSecDetalle;
    }

    public void setMpSecDetalle(String mpSecDetalle) {
        this.mpSecDetalle = mpSecDetalle;
    }

    public BeanClSeccion getSeccion() {
        return seccion;
    }

    public void setSeccion(BeanClSeccion seccion) {
        this.seccion = seccion;
    }

    public SelectItem[] getTurnos() {
        return turnos;
    }

    public void setTurnos(SelectItem[] turnos) {
        this.turnos = turnos;
    }

    public BeanClHoraria getHoraria() {
        return horaria;
    }

    public void setHoraria(BeanClHoraria horaria) {
        this.horaria = horaria;
    }

    public SelectItem[] getAulas() {
        return aulas;
    }

    public void setAulas(SelectItem[] aulas) {
        this.aulas = aulas;
    }

    public SelectItem[] getProfesores() {
        return profesores;
    }

    public void setProfesores(SelectItem[] profesores) {
        this.profesores = profesores;
    }

    public SelectItem[] getTipoClase() {
        return tipoClase;
    }

    public void setTipoClase(SelectItem[] tipoClase) {
        this.tipoClase = tipoClase;
    }

    public SelectItem[] getDias() {
        return dias;
    }

    public void setDias(SelectItem[] dias) {
        this.dias = dias;
    }

    public SelectItem[] getMeridianos() {
        return meridianos;
    }

    public void setMeridianos(SelectItem[] meridianos) {
        this.meridianos = meridianos;
    }

    public int getTotalMatriculados() {
        if (this.listaAlumnosMatriculados == null) {
            return 0;
        } else {
            return this.listaAlumnosMatriculados.size();
        }
    }

    public int getTotalParaMatricular() {
        var total = 0;
        for (BeanClAlumno tmp : listaAlumnosMatriculados) {
            if (tmp.isAluMatriculado()) {
                total++;
            }
        }
        return total;
    }

    public void addListaAlumnosMatriculados(BeanClAlumno tmp) {
        this.listaAlumnosMatriculados.add(tmp);
    }

    public List<BeanClAlumno> getListaAlumnosMatriculados() {
        return listaAlumnosMatriculados;
    }

    public void setListaAlumnosMatriculados(List<BeanClAlumno> listaAlumnosMatriculados) {
        this.listaAlumnosMatriculados = listaAlumnosMatriculados;
    }
}
