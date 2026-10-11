package net.uch.configuracion.jsf;

import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.common.ServiceFinder;
import net.uch.configuracion.dao.HSActividadDAO;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.mapping.AcActividadAlcance;
import net.uch.mapping.AcCalendario;
import net.uch.mapping.AcCalendarioActividades;
import net.uch.mapping.UserNotFoundException;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.uch.common.Constants.ACTION_IMAGES_ACTIVAR;
import static net.uch.common.Constants.ACTION_IMAGES_DESACTIVAR;

/**
 * @author cesardl
 */
public class MbActividad {

    private static final Logger LOG = LoggerFactory.getLogger(MbActividad.class);

    private final SimpleDateFormat formatTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final HSActividadDAO daoActividad = ServiceFinder.findBean("springHibernateDaoActividad", HSActividadDAO.class);

    private Timestamp tfNow;
    private int rowId;
    private Date rowFecha;
    private String rowTitulo;
    private Date rowFechaIni;
    private Date rowFechaFin;
    private boolean rowPublicado;
    private String rowEstadoPublicado;
    private String rowAlcance;
    private String[] mpAlcances;
    private Map<String, Integer> mapAlcancesId;
    private String mpTipo;
    private String mpDescripcion;
    private Blob mpImg;
    private String mpMime;
    //para listar las actividades
    private String imagenPublicado;
    private String title;
    private Date paramDate;
    private List<MbActividad> listaActividades;
    //para el js
    private String oncomplete;
    //para los check
    private SelectItem[] itemCheckAlcance;
    private SelectItem[] itemComboTipo;
    //para editar y publicar
    private int editar;
    private String publicado;

    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        tfNow = Timestamp.valueOf(formatTimeStamp.format(new Date()));
        paramDate = new Date();

        HSCatalogoDAO catalogoDAO = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        setItemComboTipo(catalogoDAO.seleccionarCatalogo("053").stream()
                .map(c -> new SelectItem("053" + c.getCatCodigoElemento(), c.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));

        setItemCheckAlcance(catalogoDAO.seleccionarCatalogo("052").stream()
                .map(c -> new SelectItem("052" + c.getCatCodigoElemento(), c.getCatDescripcionElemento()))
                .toArray(SelectItem[]::new));
    }

    public String[] getMpAlcances() {
        return mpAlcances;
    }

    public void setMpAlcances(String[] mpAlcances) {
        this.mpAlcances = mpAlcances;
    }

    public String getMpTipo() {
        return mpTipo;
    }

    public void setMpTipo(String mpTipo) {
        this.mpTipo = mpTipo;
    }

    public Date getRowFecha() {
        return rowFecha;
    }

    public void setRowFecha(Date rowFecha) {
        this.rowFecha = rowFecha;
    }

    public Date getRowFechaFin() {
        return rowFechaFin;
    }

    public void setRowFechaFin(Date rowFechaFin) {
        this.rowFechaFin = rowFechaFin;
    }

    public Date getRowFechaIni() {
        return rowFechaIni;
    }

    public void setRowFechaIni(Date rowFechaIni) {
        this.rowFechaIni = rowFechaIni;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public boolean isRowPublicado() {
        return rowPublicado;
    }

    public void setRowPublicado(boolean rowPublicado) {
        this.rowPublicado = rowPublicado;
    }

    public String getRowAlcance() {
        return rowAlcance;
    }

    public void setRowAlcance(String rowAlcance) {
        this.rowAlcance = rowAlcance;
    }

    public String getRowTitulo() {
        return rowTitulo;
    }

    public void setRowTitulo(String rowTitulo) {
        this.rowTitulo = rowTitulo;
    }

    public String getMpDescripcion() {
        return mpDescripcion;
    }

    public void setMpDescripcion(String mpDescripcion) {
        this.mpDescripcion = mpDescripcion;
    }

    public Blob getMpImg() {
        return mpImg;
    }

    public void setMpImg(Blob mpImg) {
        this.mpImg = mpImg;
    }

    public String getImagenPublicado() {
        return imagenPublicado;
    }

    public void setImagenPublicado(String imagenPublicado) {
        this.imagenPublicado = imagenPublicado;
    }

    public String getMpMime() {
        return mpMime;
    }

    public void setMpMime(String mpMime) {
        this.mpMime = mpMime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getParamDate() {
        return paramDate;
    }

    public void setParamDate(Date paramDate) {
        this.paramDate = paramDate;
    }

    public List<MbActividad> getListaActividades() {
        return listaActividades;
    }

    public void setListaActividades(List<MbActividad> listaActividades) {
        this.listaActividades = listaActividades;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getRowEstadoPublicado() {
        return rowEstadoPublicado;
    }

    public void setRowEstadoPublicado(String rowEstadoPublicado) {
        this.rowEstadoPublicado = rowEstadoPublicado;
    }

    public SelectItem[] getItemCheckAlcance() {
        return itemCheckAlcance;
    }

    public void setItemCheckAlcance(SelectItem[] itemCheckAlcance) {
        this.itemCheckAlcance = itemCheckAlcance;
    }

    public SelectItem[] getItemComboTipo() {
        return itemComboTipo;
    }

    public void setItemComboTipo(SelectItem[] itemComboTipo) {
        this.itemComboTipo = itemComboTipo;
    }

    public int getEditar() {
        return editar;
    }

    public void setEditar(int editar) {
        this.editar = editar;
    }

    public String getPublicado() {
        return publicado;
    }

    public void setPublicado(String publicado) {
        this.publicado = publicado;
    }

    public void nuevo(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setRowFecha(null);
        this.setRowFechaIni(null);
        this.setRowFechaFin(null);
        this.setMpTipo("");
        this.setMpAlcances(null);
        this.setRowPublicado(false);
        this.setRowTitulo("");
        this.setMpDescripcion("");
        this.setMpImg(null);
        this.setEditar(0);
        LOG.trace("hago nuevo los datos");
    }

    public void cargaImagen(UploadEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        try {
            var item = event.getUploadItem();
            this.setMpMime(item.getContentType());

            var file = item.getFile();
            if (file != null) {
                this.setMpImg(new SerialBlob(fromFiletoByteArray(file)));
                LOG.debug("{}", file.length());
            }
        } catch (IOException | SQLException e) {
            LOG.error("FALLA AL CARGAR LA IMAGEN", e);
        }
    }

    public void limpiarImagenActividad(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setMpImg(null);
    }

    /**
     * Para Mostrar en la tabla los datos
     */
    public void mostrarPorFecha(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getParamDate() == null) {
            this.setOncomplete("alert('Seleccione una fecha');");
        } else {
            this.setListaActividades(null);
            this.setOncomplete("");
            performSelect(paramDate);
        }
    }

    private void performSelect(Date tfParam) {
        List<AcCalendario> calendarios = daoActividad.seleccionarActividadDetalle(tfParam);

        List<MbActividad> lTmp = new ArrayList<>();
        for (AcCalendario cal : calendarios) {
            var tmp = new MbActividad();
            tmp.setRowFecha(cal.getCalFecha());

            var calact = cal.getAcCalendarioActividades();

            tmp.setRowId(cal.getCalId());
            tmp.setRowTitulo(calact.getCalactTitulo());
            tmp.setRowFechaIni(calact.getCalactFechaInicio());
            tmp.setRowFechaFin(calact.getCalactFechaFin());
            tmp.setMpDescripcion(calact.getCalactDesc());
            tmp.setMpImg(calact.getCalactImg());
            tmp.setMpMime(calact.getCalactMime());
            if (calact.getCalactPublicado().equals("1")) {
                tmp.setImagenPublicado(ACTION_IMAGES_ACTIVAR);
                tmp.setTitle("desea desactivar la actividad? dame click!");
                tmp.setRowEstadoPublicado("1");
                tmp.setRowPublicado(true);
            } else if (calact.getCalactPublicado().equals("0")) {
                tmp.setImagenPublicado(ACTION_IMAGES_DESACTIVAR);
                tmp.setTitle("desea activar la actividad? dame click!");
                tmp.setRowEstadoPublicado("0");
                tmp.setRowPublicado(false);
            }
            Set<AcActividadAlcance> objActAlc = calact.getAcActividadAlcances();
            var label = new String[objActAlc.size()];
            var j = 0;
            for (AcActividadAlcance actalc : objActAlc) {
                label[j] = obtenerAlcanceDescripcion(actalc.getActalcAlcance());
                j++;
            }
            tmp.setRowAlcance(concatenar(label));
            lTmp.add(tmp);
        }
        this.setListaActividades(lTmp);
    }

    private String obtenerAlcanceDescripcion(String str) {
        for (SelectItem item : itemCheckAlcance) {
            if (item.getValue().equals(str)) {
                return item.getLabel();
            }
        }
        throw new IllegalArgumentException("Label not found for " + str);
    }

    /**
     * Proceso de validacion de datos y seleccion si va a
     * editar o insertar una nueva actividad
     *
     * @param event face event
     * @throws SQLException from blob image
     */
    public void manipularDatos(ActionEvent event) throws SQLException {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (this.getRowTitulo().trim().length() == 0) {
            this.setOncomplete("alert('Ingrese un titulo.');");
        } else if (this.getRowFecha() == null
                || this.getRowFechaIni() == null
                || this.getRowFechaFin() == null) {
            this.setOncomplete("alert('Faltan llenar los campos de las fechas')");
        } else if (this.getRowFechaIni().getTime() >= this.getRowFechaFin().getTime()) {
            this.setOncomplete("alert('La fecha de fin no puede ser antes de la de inicio');");
        } else if (this.getMpAlcances().length == 0) {
            this.setOncomplete("alert('Seleccione al menos un alcance');");
        } else if (this.getMpImg() != null &&
                this.getMpImg().length() >= 65000) {
            this.setOncomplete("alert('Imagen demasiado pesada');");
        } else {
            nuevaActividad();
        }
    }

    private void nuevaActividad() {
        int userId = ServiceFinder.getLoggedUser().getRowUsuId();

        var cal = new AcCalendario();

        cal.setCalFecha(this.getRowFecha());
        cal.setCalActivo("1");
        cal.setCalFechaCreacion(tfNow);

        //Siempre va a ser un AcCalendario par aun AcCalendarioActividad
        var calAct = new AcCalendarioActividades();
        if (this.getEditar() != 0) {
            cal.setCalId(this.getRowId());
            calAct.setCalactId(this.getEditar());
        }
        calAct.setCalactDesc(this.getMpDescripcion());
        calAct.setCalactActivo("1");
        calAct.setCalactImg(this.getMpImg());//RUTA DE LA IMAGEN

        calAct.setCalactCreacion(tfNow);
        calAct.setCalactTipo(this.getMpTipo());
        calAct.setUsuId(userId);
        calAct.setCalactTitulo(this.getRowTitulo());
        calAct.setCalactPublicado(this.obtenerEstadoPublicado(this.isRowPublicado()));
        var tfIni = Timestamp.valueOf(formatTimeStamp.format(this.getRowFechaIni()));
        var tfFin = Timestamp.valueOf(formatTimeStamp.format(this.getRowFechaFin()));
        calAct.setCalactFechaInicio(tfIni);
        calAct.setCalactFechaFin(tfFin);
        calAct.setCalactImg(this.getMpImg());
        calAct.setCalactMime(this.getMpMime());

        //AcCalendarioActividad tiene varios AcActividadAlcance - ver MER
        Set<AcActividadAlcance> setActAcl = new LinkedHashSet<>();
        //Puede ser varios alcances
        Map<String, Integer> idsToPersist = new HashMap<>();
        if (this.getEditar() != 0) {
            String[] selectedAlcances = this.getMpAlcances();

            for (String alcance : selectedAlcances) {
                Integer actalcId = mapAlcancesId.remove(alcance);
                // no estaba inicialmente
                // sí estaba inicialmente
                idsToPersist.put(alcance, Objects.requireNonNullElse(actalcId, 0));
            }

            if (!mapAlcancesId.isEmpty()) {
                daoActividad.eliminarAlcances(new ArrayList<>(mapAlcancesId.values()));
            }
        }
        for (String alcance : this.mpAlcances) {
            var actalc = new AcActividadAlcance();
            Integer id = idsToPersist.getOrDefault(alcance, 0);
            if (this.getEditar() != 0 && id != 0) {
                actalc.setActalcId(id);
            }
            actalc.setActalcActivo("1");
            actalc.setActalcAlcance(alcance);//modificar el alcance por numero
            actalc.setAcCalendarioActividades(calAct);
            //Aniado cada AcActividadAlcance al Set
            setActAcl.add(actalc);
        }
        //El CalendarioActividad tieen su set de ActividadAlcance, lo seteo ya lleno
        calAct.setAcActividadAlcances(setActAcl);

        //El AcCalendario tiene su setCalAct, ahora lleno lo seteo
        cal.setAcCalendarioActividades(calAct);
        //Como AcCalendarioActividades tiene un AcCalendario.cal_id lo linkeo
        calAct.setAcCalendario(cal);

        if (this.getEditar() == 0) {
            daoActividad.insertarActividad(cal);
            rowId = cal.getCalId();
            this.setOncomplete("alert('Se creo una Actividad correctamente.');"
                    + "Richfaces.hideModalPanel('mpActividadNuevo')");
        } else {
            daoActividad.actualizarActividad(cal);
            this.setOncomplete("alert('Se actualizo una Actividad correctamente.');"
                    + "Richfaces.hideModalPanel('mpActividadNuevo')");
        }
        performSelect(this.rowFecha);
    }

    public void seleccionarActividad(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter uip = (UIParameter) event.getComponent().findComponent("p_id");
        var id = Integer.parseInt(uip.getValue().toString());

        AcCalendario cal = daoActividad.seleccionarActividadId(id);
        this.setRowId(cal.getCalId());
        this.setRowFecha(cal.getCalFecha());

        var calact = cal.getAcCalendarioActividades();

        this.setEditar(calact.getCalactId());
        this.setRowTitulo(calact.getCalactTitulo());
        this.setRowFechaIni(calact.getCalactFechaInicio());
        this.setRowFechaFin(calact.getCalactFechaFin());
        this.setMpDescripcion(calact.getCalactDesc());
        this.setMpImg(calact.getCalactImg());
        this.setMpMime(calact.getCalactMime());
        this.setMpTipo(calact.getCalactTipo());

        if (calact.getCalactPublicado().equals("1")) {
            this.setRowEstadoPublicado("1");
            this.setRowPublicado(true);
        } else if (calact.getCalactPublicado().equals("0")) {
            this.setRowEstadoPublicado("0");
            this.setRowPublicado(false);
        }
        int size = calact.getAcActividadAlcances().size();
        var checkedAlcances = new String[size];
        mapAlcancesId = new HashMap<>();

        var j = 0;
        for (AcActividadAlcance actalc : calact.getAcActividadAlcances()) {
            checkedAlcances[j] = actalc.getActalcAlcance();

            mapAlcancesId.put(actalc.getActalcAlcance(), actalc.getActalcId());
            j++;
        }
        this.setMpAlcances(checkedAlcances);
    }

    /**
     * Captura los parametros para cambiar el estado publicado
     *
     * @param event an event
     */
    public void setearParametros(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter c1 = (UIParameter) event.getComponent().findComponent("p_id");
        UIParameter c2 = (UIParameter) event.getComponent().findComponent("p_est_pub");
        UIParameter c3 = (UIParameter) event.getComponent().findComponent("p_fecha");
        this.setEditar(Integer.parseInt(String.valueOf(c1.getValue())));
        this.setPublicado(String.valueOf(c2.getValue()));
        this.setRowFecha((Date) c3.getValue());
    }

    /**
     * Metodo que cambia el estado de la actividad - publicado/no publicar
     */
    public void publicarActividad(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.getPublicado().equals("1")) {
            this.setPublicado("0");
        } else if (this.getPublicado().equals("0")) {
            this.setPublicado("1");
        }
        daoActividad.actualizarEstadoPublicado(this.getEditar(), this.getPublicado());
        performSelect(this.rowFecha);
    }

    public void eliminarActividad(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        UIParameter uip = (UIParameter) event.getComponent().findComponent("p_id");
        UIParameter uipFecha = (UIParameter) event.getComponent().findComponent("p_fecha");
        var id = Integer.parseInt(uip.getValue().toString());
        Date fecha = (Date) uipFecha.getValue();
        daoActividad.eliminarActividad(id);
        performSelect(fecha);
    }

    public long getTimeStamp() {
        return System.currentTimeMillis();
    }

    private String obtenerEstadoPublicado(boolean band) {
        if (band) {
            return "1";
        } else {
            return "0";
        }
    }

    private String concatenar(Object[] valores) {
        if (valores == null) {
            return "";
        }
        var sb = new StringBuilder();
        for (Object valore : valores) {
            if (!valore.equals("")) {
                sb.append(valore.toString()).append(", ");
            } else {
                sb.append(valore.toString());
            }
        }
        String str = new String(sb).trim();
        if (str.endsWith(",")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private byte[] fromFiletoByteArray(File file) throws IOException {
        try (var fin = new FileInputStream(file)) {
            var fileContent = new byte[(int) file.length()];
            int read = fin.read(fileContent);

            if (read == -1) {
                return new byte[0];
            } else {
                return fileContent;
            }
        }
    }
}
