package net.uch.academica.jsf;

import net.uch.academica.dao.HSActaDAO;
import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.academica.dao.HSMatriculaDAO;
import net.uch.academica.dao.HSSeccionDAO;
import net.uch.academica.jsf.beans.BeanConvalidacion;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSSemestreDAO;
import net.uch.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

/**
 * @author cesardl
 */
public class MbConvalidacion {

    private static final Logger LOG = LoggerFactory.getLogger(MbConvalidacion.class);

    private int alumnoId;
    private String paramAluCodigo;
    private String paramAluApPaterno;
    private String paramAluApMaterno;
    private String paramAluNombre;
    private String aluCodigoNombre;
    private int especialidadId;
    private String especialidadNombre;
    private String semestreIngreso;
    private String oncomplete;
    private List<MbConvalidacion> alumnos;
    private List<BeanConvalidacion> convalidaciones;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getParamAluCodigo() {
        return paramAluCodigo;
    }

    public void setParamAluCodigo(String paramAluCodigo) {
        this.paramAluCodigo = paramAluCodigo;
    }

    public String getParamAluApMaterno() {
        return paramAluApMaterno;
    }

    public void setParamAluApMaterno(String paramAluApMaterno) {
        this.paramAluApMaterno = paramAluApMaterno;
    }

    public String getParamAluNombre() {
        return paramAluNombre;
    }

    public void setParamAluNombre(String paramAluNombre) {
        this.paramAluNombre = paramAluNombre;
    }

    public String getParamAluApPaterno() {
        return paramAluApPaterno;
    }

    public void setParamAluApPaterno(String paramAluApPaterno) {
        this.paramAluApPaterno = paramAluApPaterno;
    }

    public String getAluCodigoNombre() {
        return aluCodigoNombre;
    }

    public void setAluCodigoNombre(String aluCodigoNombre) {
        this.aluCodigoNombre = aluCodigoNombre;
    }

    public int getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(int especialidadId) {
        this.especialidadId = especialidadId;
    }

    public String getEspecialidadNombre() {
        return especialidadNombre;
    }

    public void setEspecialidadNombre(String especialidadNombre) {
        this.especialidadNombre = especialidadNombre;
    }

    public String getSemestreIngreso() {
        return semestreIngreso;
    }

    public void setSemestreIngreso(String semestreIngreso) {
        this.semestreIngreso = semestreIngreso;
    }

    public List<MbConvalidacion> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(List<MbConvalidacion> alumnos) {
        this.alumnos = alumnos;
    }

    public List<BeanConvalidacion> getConvalidaciones() {
        return convalidaciones;
    }

    public void setConvalidaciones(List<BeanConvalidacion> convalidaciones) {
        this.convalidaciones = convalidaciones;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    private boolean camposVacios() {
        return this.paramAluCodigo.trim().length() == 0
                && this.paramAluApPaterno.trim().length() == 0
                && this.paramAluApMaterno.trim().length() == 0
                && this.paramAluNombre.trim().length() == 0;
    }

    public void buscarCursos(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (camposVacios()) {
            LOG.warn("Without params for search");
            this.setOncomplete("alert('Ingrese algun parametro para la busqueda');");
        } else {
            HSAlumnoDAO daoAlumno = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
            HSSemestreDAO daoSemestre = ServiceFinder.findBean("springHibernateDaoSemestre", HSSemestreDAO.class);

            LOG.debug("Looking for '{}' | '{}' | '{}' | '{}'", this.paramAluCodigo, this.paramAluApPaterno, this.paramAluApMaterno, this.paramAluNombre);

            List<AcAlumno> lAlumnos = daoAlumno.seleccionarAlumnos(
                    this.paramAluCodigo, this.paramAluApPaterno, this.paramAluApMaterno, this.paramAluNombre, 0);

            this.setAlumnos(lAlumnos.stream().map(alu -> {
                var conv = new MbConvalidacion();
                conv.setAlumnoId(alu.getId());
                conv.setParamAluCodigo(alu.getAluCodigo());
                conv.setParamAluApPaterno(alu.getAluAppaterno());
                conv.setParamAluApMaterno(alu.getAluApmaterno());
                conv.setParamAluNombre(alu.getAluNombres());
                conv.setEspecialidadId(alu.getEsp().getId());
                conv.setEspecialidadNombre(alu.getEsp().getEspNombre());
                conv.setSemestreIngreso(daoSemestre.getSemestre(alu.getSemId()).getSemNombre());
                return conv;
            }).collect(Collectors.toList()));
            LOG.info("Total of convalidations {}", alumnos.size());
        }
    }

    public void capturarAlumno(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        MbConvalidacion tmpAlu = (MbConvalidacion) event.getComponent().getAttributes().get("p_alu");

        int pAluId = tmpAlu.getAlumnoId();
        String pAluNom = tmpAlu.getParamAluCodigo() + " - " +
                tmpAlu.getParamAluApPaterno() + " " + tmpAlu.getParamAluApMaterno() + " " + tmpAlu.getParamAluNombre();
        int pEspId = tmpAlu.getEspecialidadId();
        String pEspNom = tmpAlu.getEspecialidadNombre();
        String pSemNom = tmpAlu.getSemestreIngreso();

        LOG.debug("Retrieving data of {} | {} | '{}' | '{}' | '{}'",
                pAluId, pEspId, pAluNom, pEspNom, pSemNom);

        this.setEspecialidadId(pEspId);
        this.setEspecialidadNombre(pEspNom);
        this.setAlumnoId(pAluId);
        this.setAluCodigoNombre(pAluNom);
        this.setSemestreIngreso(pSemNom);

        HSSeccionDAO dao = ServiceFinder.findBean("springHibernateDaoSeccion", HSSeccionDAO.class);
        List<SpConvalidacionCursos> tmp = dao.listaConvalidacion(pEspId, pAluId);

        List<BeanConvalidacion> convs = new ArrayList<>();
        for (SpConvalidacionCursos spConvCur : tmp) {
            var conv = new BeanConvalidacion(spConvCur);

            if (conv.getRegNota().equalsIgnoreCase("")) {
                conv.setDisable(true);
                conv.setActivar(false);
                conv.setStyle("background: gray;");
            } else {
                conv.setActivar(true);
            }
            convs.add(conv);
        }
        this.setConvalidaciones(convs);
        LOG.info("Total courses to convalidate {}", convalidaciones.size());
    }

    public void guardarConvalidacion(ActionEvent event) {
        LOG.debug("[{}] For student {}", event.getPhaseId(), this.alumnoId);
        this.setOncomplete("");
        if (validarNotas()) {
            HSMatriculaDAO daoMatricula = ServiceFinder.findBean("springHibernateDaoMatricula", HSMatriculaDAO.class);
            AcMatricula mat = daoMatricula.seleccionarMatriculaAlumnoConvalidacion(this.alumnoId);
            Set<AcMatriculaCurso> matcurs;
            if (mat == null) {
                LOG.debug("Matricula not found");
                mat = new AcMatricula();
                mat.setAlu(new AcAlumno(this.getAlumnoId()));
                mat.setMatActivo("1");
                mat.setMatCodigo("MAT-CONV");
                mat.setMatFecha(new Date());
                mat.setMatTipo(CAT_TIPO_MATRICULA_REGULAR);
                mat.setSemId(5); // SEM-TRAS
                mat.setUsuId(ServiceFinder.getLoggedUser().getRowUsuId());
                matcurs = new LinkedHashSet<>();
            } else {
                matcurs = new LinkedHashSet<>(mat.getAcMatriculaCursos());
                LOG.debug("Matricula {} with mat_curs {}", mat.getId(), matcurs.size());
            }

            HSActaDAO daoActa = ServiceFinder.findBean("springHibernateDaoActa", HSActaDAO.class);

            List<AcActaDetalle> tmpActaDetalles = new ArrayList<>();
            for (BeanConvalidacion bean : convalidaciones) {
                if (bean.isActivar()) {
                    AcMatriculaCurso matcur = matcurs.stream()
                            .filter(mc -> mc.getSec().getId() == bean.getRegSecId())
                            .findFirst().orElse(new AcMatriculaCurso());
                    matcur.setMat(mat);
                    matcur.setMatcurActivo("1");
                    matcur.setMatcurEstado(null);
                    matcur.setMatcurObs("MAT-CUR-CONV");
                    matcur.setSec(new AcSeccion(bean.getRegSecId()));

                    matcurs.add(matcur);

                    AcActa acta = daoActa.seleccionarActa(bean.getRegSecId(), CAT_TIPO_ACTA_CONVALIDACION);
                    if (acta == null) {
                        acta = new AcActa();
                        acta.setActActivo("1");
                        acta.setActCodigo(null);
                        acta.setActNombre(null);
                        acta.setActNumero(null);
                        acta.setActTipo(CAT_TIPO_ACTA_CONVALIDACION);
                        acta.setActStatus(ACT_STATUS_PUBLISHED);
                        acta.setSec(new AcSeccion(bean.getRegSecId()));

                        daoActa.saveOrUpdateActa(acta);
                        LOG.debug("Acta created");
                    }
                    LOG.trace("Acta to use {}", acta.getId());
                    var actaDetalle = new AcActaDetalle();
                    actaDetalle.setId(bean.getRegActdetId());
                    actaDetalle.setAct(acta);
                    actaDetalle.setActdetActivo("1");
                    actaDetalle.setActdetNota(Float.parseFloat(bean.getRegNota()));
                    actaDetalle.setActdetTipoNota(CAT_TIPO_NOTA_CONVALIDACION);
                    actaDetalle.setAluId(this.alumnoId);
                    actaDetalle.setSisevaDetalleId(0);
                    tmpActaDetalles.add(actaDetalle);
                }
            }
            mat.setAcMatriculaCursos(matcurs);

            daoMatricula.registrarMatricula(mat);
            LOG.info("Matricula saved/updated {}", mat.getId());
            daoActa.insertarActaDetalles(tmpActaDetalles);
            LOG.info("Acta Detalle saved/updated {}", tmpActaDetalles.size());

            this.setParamAluApPaterno("");
            this.setParamAluApMaterno("");
            this.setParamAluNombre("");
            this.setParamAluCodigo("");
            this.setAlumnoId(0);
            this.setEspecialidadId(0);
            this.setConvalidaciones(new ArrayList<>());
            this.setOncomplete("alert('Convalidacion terminada.');"
                    + "Richfaces.hideModalPanel('mpConvalidacion')");
        } else {
            this.setOncomplete("alert('Error en la notas, revise por favor.');");
        }
    }

    private boolean validarNotas() {
        for (BeanConvalidacion bean : convalidaciones) {
            LOG.trace("{} :::: '{}'", bean.isActivar(), bean.getRegNota());
            if (bean.isActivar()) {
                if (!StringUtils.hasText(bean.getRegNota())) {
                    return false;
                } else {
                    var f = Float.parseFloat(bean.getRegNota());
                    if (f <= 10 || f > 20) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
