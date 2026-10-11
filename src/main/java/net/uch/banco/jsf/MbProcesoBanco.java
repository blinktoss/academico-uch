package net.uch.banco.jsf;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.administrativa.dao.HSEdicionEstructPagoDAO;
import net.uch.administrativa.dao.HSPagoDAO;
import net.uch.autenticacion.jsf.beans.BeanUser;
import net.uch.banco.dao.HSBancoDAO;
import net.uch.banco.jsf.beans.*;
import net.uch.common.ServiceFinder;
import net.uch.common.SystemHelper;
import net.uch.configuracion.dao.HSCatalogoDAO;
import net.uch.mapping.*;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static net.uch.common.Constants.*;

/**
 * @author cesardl
 */
public class MbProcesoBanco {

    private static final Logger LOG = LoggerFactory.getLogger(MbProcesoBanco.class);

    private static final String TOKEN_COMPLETE_AMOUNT_LINE = "0000000";

    private final HSBancoDAO daoBanco = ServiceFinder.findBean("springHibernateDaoBanco", HSBancoDAO.class);

    private final SystemHelper helper = ServiceFinder.findBean("helper", SystemHelper.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String mensaje;
    private boolean disableProceso;
    private boolean rendered;
    private String oncomplete;
    private String bodyClass;
    private File file;
    private String fileNombre;
    private long fileDim;
    private boolean generaComprobante;
    private String efecto;
    private String serie;
    private List<SelectItem> series;
    private String nroInicio;
    private Date fechaComprobantes;
    private List<RespuestaBanco> respuestas;
    private List<RespuestaBanco> errores;

    private List<InfoBanco> transacciones;
    private List<TbBancoEntrada> entradas;

    /**
     * Creates a new instance of bProcesoBanco
     */
    @PostConstruct
    public void init() {
        BeanUser usu = ServiceFinder.getLoggedUser();
        if (usu == null) {
            throw new UserNotFoundException();
        }
        this.disableProceso = true;
        this.rendered = false;
        this.generaComprobante = true;
        this.serie = "";

        HSCatalogoDAO catalogoDAO = ServiceFinder.findBean("springHibernateDaoCatalogo", HSCatalogoDAO.class);
        List<TbCatalogo> catalogos = catalogoDAO.seleccionarCatalogo("032");
        this.setSeries(catalogos.stream()
                .map(cat -> new SelectItem(cat.getCatDescripcionElemento(), cat.getCatDescripcionElemento()))
                .collect(Collectors.toList()));
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isDisableProceso() {
        return disableProceso;
    }

    public void setDisableProceso(boolean disableProceso) {
        this.disableProceso = disableProceso;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getBodyClass() {
        return bodyClass;
    }

    public void setBodyClass(String bodyClass) {
        this.bodyClass = bodyClass;
    }

    public long getFileDim() {
        return fileDim;
    }

    public void setFileDim(long fileDim) {
        this.fileDim = fileDim;
    }

    public boolean isGeneraComprobante() {
        return generaComprobante;
    }

    public void setGeneraComprobante(boolean generaComprobante) {
        this.generaComprobante = generaComprobante;
    }

    public String getEfecto() {
        return efecto;
    }

    public void setEfecto(String efecto) {
        this.efecto = efecto;
    }

    public String getNroInicio() {
        return nroInicio;
    }

    public void setNroInicio(String nroInicio) {
        this.nroInicio = nroInicio;
    }

    public Date getFechaComprobantes() {
        return fechaComprobantes;
    }

    public void setFechaComprobantes(Date fechaComprobantes) {
        this.fechaComprobantes = fechaComprobantes;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public List<SelectItem> getSeries() {
        return series;
    }

    public void setSeries(List<SelectItem> series) {
        this.series = series;
    }

    public String getFileNombre() {
        return fileNombre;
    }

    public void setFileNombre(String fileNombre) {
        this.fileNombre = fileNombre;
    }

    public List<RespuestaBanco> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<RespuestaBanco> respuestas) {
        this.respuestas = respuestas;
    }

    public int getTotalDataBanco() {
        return this.respuestas.size();
    }

    public List<RespuestaBanco> getErrores() {
        return errores;
    }

    public void setErrores(List<RespuestaBanco> errores) {
        this.errores = errores;
    }

    public int getTotalErrores() {
        return this.errores.size();
    }

    public void seleccionarEfecto(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if (this.isGeneraComprobante()) {
            this.setEfecto("showDiv();");
        } else {
            this.setEfecto("hideDiv({duration:0.7});");
        }
        this.setSerie("");
        this.setNroInicio("");
    }

    public String limpiarTodo() {
        LOG.debug("Cleaning all inputs");
        this.disableProceso = true;
        this.rendered = false;
        this.generaComprobante = true;
        this.file = null;
        this.mensaje = "";
        this.bodyClass = "";
        this.fileNombre = "";
        this.fileDim = 0;
        this.efecto = "";
        this.serie = "0";
        this.nroInicio = "";
        this.transacciones = new ArrayList<>();
        this.entradas = new ArrayList<>();
        this.respuestas = new ArrayList<>();

        return "ProcesoBanco";
    }

    public void generarArchivoDescargable(ActionEvent event) {
        LOG.debug("[{}] Starting file download", event.getPhaseId());
        List<TbBanco> banco = daoBanco.listarBanco(1);
        List<SpDataBanco> lista = daoBanco.listarDataBanco();

        double iMontoTotal = 0;
        String fechaEnvio = lista.get(0).getRegFechaIni();

        for (SpDataBanco spDataBanco : lista) {
            iMontoTotal += Double.parseDouble(spDataBanco.getRegTotalCobrar());
        }
        var valor = String.valueOf(iMontoTotal);
        String[] monto = valor.split("\\.");

        String montoTotal = "000000000000000" + monto[0] + "00";

        var fileName = "Data_entrada_cuota_variable.txt";
        String filePath = System.getProperty("user.dir") + File.separator + fileName;
        LOG.debug("{}", filePath);
        try (var fw = new FileWriter(filePath);
             var bw = new BufferedWriter(fw);
             var writer = new PrintWriter(bw)) {
            var cantReg = String.valueOf(lista.size()); //CANTIDAD DE REGISTROS

            if (cantReg.length() < 7) {
                cantReg = TOKEN_COMPLETE_AMOUNT_LINE.concat(cantReg);
            }

            cantReg = cantReg.substring(cantReg.length() - 7);
            montoTotal = montoTotal.substring(montoTotal.length() - 17);
            String cuenta = banco.get(0).getBanCuentaEmpresa().concat("     ");
            var cuentaEmpe = cuenta.substring(0, 14);
            writer.print("H");
            writer.print(cuentaEmpe);
            writer.print(banco.get(0).getBanSecServicio());
            writer.print(cantReg); //CANTIDAD DE REGISTROS
            writer.print(montoTotal); //TOTAL SOLES
            writer.print("00000000000000000"); //TOTAL DOLARES
            writer.print(banco.get(0).getBanRucEmpresa());
            writer.print(fechaEnvio);
            writer.print(fechaEnvio);
            writer.print("   "); //filler 3
            writer.print(banco.get(0).getBanDiaMora());
            writer.print(banco.get(0).getBanTipoMora());
            writer.print(banco.get(0).getBanMoraFlat());
            writer.print(banco.get(0).getBanPorcenMora());
            writer.print("000000000"); // MONTO FIJO
            writer.print(banco.get(0).getBanTipoDescuento());
            writer.print("000000000"); // MONTO A DESCONTAR
            writer.print(banco.get(0).getBanPorcenDescuento());
            writer.print(banco.get(0).getBanDiasDescuento());

            var filler = "";
            for (var i = 1; i <= 111; i++) {
                filler = filler.concat(" ");
            }
            writer.print(filler); //filler 111
            writer.print("*");
            writer.print("\r\n");

            lista = daoBanco.listarDataBanco();

            for (SpDataBanco spDataBanco : lista) {
                String aluCod = spDataBanco.getRegCodigoAlu().concat("             ");
                var aluCodigo = aluCod.substring(0, 15);
                String nroReci = spDataBanco.getRegNumeroRec() + "               ";
                var nroRecibo = nroReci.substring(0, 15);
                String aluNom = spDataBanco.getRegNombreUsu() + "                    ";
                var aluNombre = aluNom.substring(0, 20);
                String refReci = spDataBanco.getRegReferenciaRec() + "                              ";
                var refRecibo = refReci.substring(0, 30);
                String importeCo = TOKEN_COMPLETE_AMOUNT_LINE + spDataBanco.getRegImporteCob1().replace(".", "");
                var importeCobrar = importeCo.substring(importeCo.length() - 9);
                String diasPro = "000" + spDataBanco.getRegDiasProrroga();
                var diasProrroga = diasPro.substring(diasPro.length() - 3);

                String importeCo2 = TOKEN_COMPLETE_AMOUNT_LINE + spDataBanco.getRegImporteCob2().replace(".", "");
                var importeCobrar2 = importeCo2.substring(importeCo2.length() - 9);

                String importeCo3 = TOKEN_COMPLETE_AMOUNT_LINE + spDataBanco.getRegImporteCob3().replace(".", "");
                var importeCobrar3 = importeCo3.substring(importeCo3.length() - 9);

                String importeCo4 = TOKEN_COMPLETE_AMOUNT_LINE + spDataBanco.getRegImporteCob4().replace(".", "");
                var importeCobrar4 = importeCo4.substring(importeCo4.length() - 9);

                String importeCo5 = TOKEN_COMPLETE_AMOUNT_LINE + spDataBanco.getRegImporteCob5().replace(".", "");
                var importeCobrar5 = importeCo5.substring(importeCo5.length() - 9);

                String importeCo6 = TOKEN_COMPLETE_AMOUNT_LINE + spDataBanco.getRegImporteCob6().replace(".", "");
                var importeCobrar6 = importeCo6.substring(importeCo6.length() - 9);

                String totalCo = "0000000000000" + spDataBanco.getRegTotalCobrar().replace(".", "");
                var totalCobrar = totalCo.substring(totalCo.length() - 15);

                writer.print("D");
                writer.print(cuentaEmpe);
                writer.print(banco.get(0).getBanSecServicio());
                writer.print(aluCodigo); // CODIGO USUARIO -> ALU_COD
                writer.print(nroRecibo); // NUMERO RECIBO -> ALUTAR_ID
                writer.print(banco.get(0).getBanRucEmpresa()); //RUC EMPRESA
                writer.print(spDataBanco.getRegSituacion());
                writer.print(spDataBanco.getRegMoneda()); // SOLES -> 0000
                writer.print(replaces(aluNombre));// nombre de usuario modificado
                writer.print(replaces(refRecibo)); //referencia recibo modificado
                writer.print(spDataBanco.getRegConceptoCob1()); // CONCEPTO A COBRAR 1
                writer.print(importeCobrar); // IMPORTE A COBRAR 1
                writer.print(spDataBanco.getRegConceptoCob2()); // CONCEPTO A COBRAR 2
                writer.print(importeCobrar2); // IMPORTE A COBRAR 2
                writer.print(spDataBanco.getRegConceptoCob3()); // CONCEPTO A COBRAR 3
                writer.print(importeCobrar3); // IMPORTE A COBRAR 3
                writer.print(spDataBanco.getRegConceptoCob4()); // CONCEPTO A COBRAR 4
                writer.print(importeCobrar4); // IMPORTE A COBRAR 4
                writer.print(spDataBanco.getRegConceptoCob5()); // CONCEPTO A COBRAR 5
                writer.print(importeCobrar5); // IMPORTE A COBRAR 5
                writer.print(spDataBanco.getRegConceptoCob6()); // CONCEPTO A COBRAR 6
                writer.print(importeCobrar6); // IMPORTE A COBRAR 6 O MORA
                writer.print(totalCobrar); //TOTAL A COBRAR
                writer.print(totalCobrar); //SALDO A DEUDA
                writer.print(spDataBanco.getRegPorcentajeMin());
                writer.print(spDataBanco.getRegOrdenCrono());
                writer.print(spDataBanco.getRegFechaIni()); //FECHA DE EMISION
                writer.print(spDataBanco.getRegFechaVenci()); //FECHA DE VENCIMIENTO
                writer.print(diasProrroga); //DIAS DE PRORROGA
                writer.print("               ");
                writer.print("*");
                writer.print("\r\n");
            }

            List<SpConceptosBanco> conceptos = daoBanco.listarConceptosBanco();
            for (var i = 0; i < conceptos.size(); i++) {
                SpConceptosBanco concep = conceptos.get(i);

                writer.print("C");
                writer.print(cuentaEmpe);
                writer.print(banco.get(0).getBanSecServicio());

                //CODIGO CONCEPTO
                if (i <= 9) {
                    writer.print("0" + concep.getRegConcepto());
                } else {
                    writer.print(concep.getRegConcepto());
                }

                String conceptDescription = concep.getRegDescripcion();
                for (var j = conceptDescription.length(); j < 30; j++) {
                    conceptDescription = conceptDescription.concat(" ");
                }
                writer.print(this.replaces(conceptDescription));//DESCRIPCION CONCEPTO  30
                writer.print("1");//AFECTO A PAGO PARCIAL  1
                writer.print(cuentaEmpe);//CUENTA DE ABONO

                filler = "";
                for (var k = 0; k < 188; k++) {
                    filler = filler.concat(" ");
                }
                writer.print(filler);
                writer.print("*");
                writer.print("\r\n");
            }

            fileName = fileName.replaceFirst(".txt", "_" + dateFormat.format(new Date()) + ".txt");

            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Content-Type", "application/force-download");
            try (var archivo = new FileInputStream(filePath)) {
                var datos = new byte[archivo.available()];
                int read = archivo.read(datos);
                LOG.debug("Bytes read {}", read);
                ServletOutputStream ouputStream = response.getOutputStream();
                ouputStream.write(datos);
                ouputStream.flush();
                ouputStream.close();
                FacesContext.getCurrentInstance().responseComplete();
            }
            LOG.info("Archivo generado para descarga: {}", fileName);
            this.setOncomplete("alert('Archivo generado')");
        } catch (IOException ioe) {
            LOG.error("se presento el error: {}", ioe.getMessage(), ioe);
            this.setOncomplete("alert('Error generando archivo')");
        } finally {
            try {
                Files.delete(Paths.get(filePath));
                LOG.info("se borro el archivo");
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public void cargarArchivo(UploadEvent event) {
        LOG.debug("[{}] Uploading file", event.getPhaseId());
        var item = event.getUploadItem();

        this.setFile(item.getFile());
        this.setFileNombre(item.getFileName());
        this.setFileDim(item.getFileSize());
        this.setDisableProceso(false);
    }

    public void procesarArchivo(ActionEvent event) throws IOException {
        LOG.debug("[{}]", event.getPhaseId());
        var banco = new InfoBanco();
        List<Detalle> detalles = new ArrayList<>();
        List<Concepto> conceptos = new ArrayList<>();

        var numeroLinea = 1;
        transacciones = new ArrayList<>();
        errores = new ArrayList<>();
        try (var fr = new FileReader(this.getFile());
             var br = new BufferedReader(fr)) {

            String cadena = br.readLine();
            while (cadena != null) {
                if (!cadena.endsWith("*")) {
                    errores.add(new RespuestaBanco(numeroLinea, null, "No termina con el caracter '*'"));
                    cadena = br.readLine();
                } else if (cadena.length() != 180) {
                    errores.add(new RespuestaBanco(numeroLinea, null, "No tiene el n&uacute;mero de caracteres establecidos"));
                    cadena = br.readLine();
                } else {
                    try {
                        InfoBanco.RowIdentifiers ri = InfoBanco.RowIdentifiers.valueOf(String.valueOf(cadena.charAt(0)));
                        switch (ri) {
                            case H:
                                LOG.trace("> Cabecera: {}", cadena);
                                banco.setCabecera(new Cabecera(cadena));
                                LOG.debug("\n{}", banco.getCabecera());
                                break;
                            case D:
                                LOG.trace(">> Detalle: {}", cadena);
                                var d = new Detalle(cadena);
                                if (daoBanco.pagosBanco(convertirInt(d.getNumeroRecibo())).isEmpty()) {
                                    errores.add(new RespuestaBanco(numeroLinea, d.getNumeroOperacion(),
                                            "Presenta datos incorrectos, no se puede procesar"));
                                } else if (daoBanco.existeNroOperacion(d.getNumeroOperacion()) != null) {
                                    errores.add(new RespuestaBanco(numeroLinea, d.getNumeroOperacion(),
                                            "El registro ya ha sido procesado"));
                                } else {
                                    detalles.add(d);
                                }
                                LOG.debug("\n{}", d);
                                break;
                            case T:
                                LOG.trace(">>> Conceptos: {}", cadena);
                                var c = new Concepto(cadena);
                                conceptos.add(c);
                                LOG.debug("\n{}", c);
                                break;
                            default:
                                LOG.warn("Unregistered token '{}'", ri);
                                break;
                        }
                    } catch (IllegalArgumentException e) {
                        LOG.warn(e.getMessage(), e);
                        errores.add(new RespuestaBanco(numeroLinea, "[]", e.getMessage()));
                    }
                    cadena = br.readLine();
                    if (cadena == null) {
                        LOG.info("Last line, adding details and concepts");

                        banco.setDetalles(detalles);
                        banco.setConceptos(conceptos);

                        transacciones.add(banco);
                    } else {
                        if (cadena.charAt(0) == 'H') {
                            banco.setDetalles(detalles);
                            banco.setConceptos(conceptos);

                            transacciones.add(banco);
                            LOG.info("From Cabecera, adding details and concepts");
                        }
                    }
                }// FIN DE IF-ELSE
                numeroLinea++;
            }
            LOG.info("Total {} lines processed", numeroLinea - 1);
        }

        if (this.errores.isEmpty()) {
            this.setBodyClass("success");
            this.setMensaje("<h3>Archivo procesado con &eacute;xito - Todos los datos correctos</h3>");
        } else {
            this.setBodyClass("error");
            this.setMensaje("<h3>Archivo procesado con errores - Revise las l&iacute;neas mostradas</h3>");
            LOG.warn("Total errors in file {} read", this.errores.size());
        }

        this.llenarTablaEntradas();
        this.setFile(null);
        this.setDisableProceso(true);
        this.setRendered(true);
        this.setGeneraComprobante(true);
    }

    public void llenarTablaEntradas() {
        LOG.debug("Processing {} transactions", transacciones.size());
        entradas = new ArrayList<>();
        respuestas = new ArrayList<>();

        for (InfoBanco infoBanco : transacciones) {
            this.setFechaComprobantes(convertirFecha(infoBanco.getCabecera().getFechaMovimiento()));

            List<Detalle> detalles = infoBanco.getDetalles();
            LOG.debug("Completing information for {} details", detalles.size());
            for (Detalle d : detalles) {
                var numeroRecibo = convertirInt(d.getNumeroRecibo());

                var bancoEntrada = new TbBancoEntrada();
                bancoEntrada.setBanentMonto1(convertirFloat(d.getImporte1()));
                bancoEntrada.setBanentMonto2(convertirFloat(d.getImporte2()));
                bancoEntrada.setBanentMonto3(convertirFloat(d.getImporte3()));
                bancoEntrada.setBanentMonto4(convertirFloat(d.getImporte4()));
                bancoEntrada.setBanentMonto5(convertirFloat(d.getImporte5()));
                bancoEntrada.setBanentMora(convertirFloat(d.getImporte6Mora()));
                bancoEntrada.setBanentFechaMovimiento(convertirFecha(infoBanco.getCabecera().getFechaMovimiento()));
                bancoEntrada.setBanentFechaPago(convertirFecha(d.getFechaPago()));
                bancoEntrada.setBanentFechaCreacion(Timestamp.valueOf(timeStampFormat.format(new Date())));
                bancoEntrada.setBanentNroOperacion(d.getNumeroOperacion());
                bancoEntrada.setBanentProcesado("0");
                bancoEntrada.setBanentActivo("1");
                bancoEntrada.setBanentNroRecibo(numeroRecibo);

                entradas.add(bancoEntrada);

                var conceptos = new StringBuilder();
                var aluId = 0;
                List<TbPagoBanco> pb = daoBanco.pagosBanco(numeroRecibo);
                LOG.debug("Total de pagos banco {} para el nro de recibo '{}'", pb.size(), numeroRecibo);
                for (TbPagoBanco pagoBanco : pb) {
                    AdAlumnoTarifa alutar = getAlumnoTarifa(pagoBanco.getAlutarId());
                    String nuevoConcepto = alutar.getEstpagdet().getEstpagdetNombre();
                    if (pagoBanco.getPagbanConcepto().equalsIgnoreCase("06")) {
                        conceptos.insert(0, "- " + nuevoConcepto + "<br />");
                    } else {
                        conceptos = new StringBuilder(conceptos + "- " + nuevoConcepto + "<br />");
                    }
                    aluId = pagoBanco.getAluId();
                    LOG.trace("id del alumno_tarifa: {}", alutar.getId());
                    LOG.trace("id de alumno: {}", aluId);
                    LOG.trace("conceptos : {}", conceptos);
                }
                conceptos = new StringBuilder(conceptos.substring(0, conceptos.length() - 6));
                var rb = new RespuestaBanco(bancoEntrada);
                rb.setBanentNomAlumno(getNombreAlumno(aluId));
                rb.setBanentConceptoPago(conceptos.toString());
                respuestas.add(rb);
            }
        }
        LOG.info("Total bank responses {} to process", respuestas.size());
    }

    public void cambiaSerie(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        if ("0".equals(serie)) {
            nroInicio = "";
        } else {
            // Preview only — insertarComprobantesPago stamps each voucher from the counter.
            nroInicio = helper.serie(serie, CAT_TIPO_COMPROBANTE_PAGO_BOLETA).getSerie();
        }
    }

    public void validaComprobante(ActionEvent event) {
        LOG.debug("[{}]", event.getPhaseId());
        this.setOncomplete("");
        if (this.isGeneraComprobante()) {
            try {
                if ("0".equals(this.getSerie())) {
                    this.setOncomplete("alert('Seleccione una serie para los comprobantes');");
                } else {
                    var iNroInicio = convertirInt(this.getNroInicio());
                    if (iNroInicio == 0) {
                        this.setOncomplete("alert('Ingrese un numero de inicio correcto');");
                    } else {
                        this.setOncomplete("Richfaces.showModalPanel('mpSalvar',{top:150})");
                    }
                }
            } catch (NumberFormatException nfe) {
                this.setOncomplete("alert('Ingrese un numero de inicio correcto');");
            }
        } else {
            this.setOncomplete("Richfaces.showModalPanel('mpSalvar',{top:150})");
        }
    }

    public String guardarInformacion() {
        if (this.isGeneraComprobante()) {
            generarComprobantes();
            for (TbBancoEntrada tbBancoEntrada : entradas) {
                tbBancoEntrada.setBanentProcesado("1");
            }
        }
        daoBanco.guardarRespuestaBanco(entradas);

        LOG.debug("INFORMACION SALVADA");
        this.entradas = null;
        this.respuestas = null;
        this.disableProceso = true;
        this.rendered = false;
        this.fileNombre = "";
        this.fileDim = 0;
        this.serie = "";
        this.nroInicio = "";
        this.generaComprobante = true;
        this.fechaComprobantes = new Date();
        return "ProcesoBanco";
    }

    public void generarComprobantes() {
        LOG.debug("proceso para generar AdComprobantePago");

        List<AdComprobantePago> compags = new ArrayList<>();

        var iCompagnro = convertirInt(this.getNroInicio());
        int usuId = ServiceFinder.getLoggedUser().getRowUsuId();

        HSEdicionEstructPagoDAO daoAlumnoTarifa = ServiceFinder.findBean("springHibernateDaoEdicionEstructura", HSEdicionEstructPagoDAO.class);

        for (InfoBanco banco : transacciones) {
            for (var j = 0; j < banco.getDetalles().size(); j++) {
                var detalle = banco.getDetalles().get(j);

                var comprobante = new AdComprobantePago();

                List<TbPagoBanco> pbs = daoBanco.pagosBanco(convertirInt(detalle.getNumeroRecibo()));
                LOG.trace("NUMERO DE RECIBO {}\tTOTAL PAGOS BANCO {}", detalle.getNumeroRecibo(), pbs.size());

                String[] importes = detalle.getImportes();
                var tmp = String.valueOf(iCompagnro + j);
                for (int i = tmp.length(); i < 7; i++) {
                    tmp = "0".concat(tmp);
                }
                String compagnro = this.getSerie().concat("-").concat(tmp);
                comprobante.setCompagNro(compagnro);// compag_nro
                comprobante.setCompagMonto(sumatoriaImportes(importes));// compag_monto
                comprobante.setCompagFecha(this.getFechaComprobantes());// compag_fecha
                comprobante.setCompagCliente(pbs.get(0).getAluId());// compag_cliente - ALU_ID
                comprobante.setCompagClienteTipo(CAT_TIPO_CLIENTE_ALUMNO_CP);// compag_cliente_tipo - ALUMNO CP
                comprobante.setCompagUsado("1");// compag_usado
                comprobante.setCompagProcedencia(CAT_PROCEDENCIA_PAGO_BANCO);// compag_procedencia - PAGO BANCO
                comprobante.setCompagCreacion(new Date());// compag_creacion
                comprobante.setCompagActivo("1");// compag_activo
                comprobante.setCompagDes("");// compag_des
                comprobante.setCompagAlu(0);// compag_alu
                comprobante.setCompagTipo(CAT_ESTADO_BOLETA_VALIDO);// compag_tipo - ESTADO DE BOLETA
                comprobante.setCompagTipDoc(CAT_TIPO_COMPROBANTE_PAGO_BOLETA);// compag_tip_doc - BOLETA
                comprobante.setCompagAluTipo("");// compag_alu_tipo
                comprobante.setCompagVoucherFecha(new Date());// compag_voucher_fecha
                comprobante.setCompagVoucherCodAgencia("");// compag_voucher_cod_agencia
                comprobante.setCompagVoucherNroOperacion("");// compag_voucher_nro_operacion

                Set<AdPago> pagos = new HashSet<>();
                Integer alutarId = 0;
                for (TbPagoBanco pagban : pbs) {
                    AdAlumnoTarifa alutar = getAlumnoTarifa(pagban.getAlutarId());
                    alutarId = alutar.getId();

                    var pago = new AdPago();
                    pago.setPagMonto(pagban.getPagbanMonto());// pag_monto
                    pago.setPagFecha(convertirFecha(detalle.getFechaPago()));// pag_fecha
                    pago.setPagActivo("1");// pag_activo
                    pago.setUsuId(usuId);// usuId
                    pago.setCompag(comprobante);// compag_id
                    pago.setConpag(alutar.getConpag());// conpag_id
                    pago.setAlutarId(alutarId);// alutarId

                    pagos.add(pago);
                    LOG.trace("\tPAGO monto {} alutarId {}", pago.getPagMonto(), pago.getAlutarId());

                    daoAlumnoTarifa.actualizarEstadoPago(alutar.getId(), "2");
                }
                var monto = convertirFloat(detalle.getImporte6Mora());
                if (monto != 0 && alutarId != 0) {
                    var pago = new AdPago();
                    pago.setPagMonto(monto);// pag_monto
                    pago.setPagFecha(convertirFecha(detalle.getFechaPago()));// pag_fecha
                    pago.setPagActivo("1");// pag_activo
                    pago.setUsuId(usuId);// usuId
                    pago.setCompag(comprobante);// compag_id
                    pago.setConpag(new AdConceptoPago(49));// conpag_id
                    pago.setAlutarId(alutarId);// alutarId

                    pagos.add(pago);
                    LOG.trace("\tPAGO monto {} alutarId {}", pago.getPagMonto(), pago.getAlutarId());
                }
                comprobante.setAdPagos(pagos);

                compags.add(comprobante);
                LOG.trace("\tCOMPROBANTE total_pagos {} monto_total {} alu_id {}", comprobante.getAdPagos().size(), comprobante.getCompagMonto(), comprobante.getCompagCliente());
            }
        }// FIN FOR - LLENAR LISTA DE COMPROBANTES

        HSPagoDAO daoPago = ServiceFinder.findBean("springHibernateDaoPago", HSPagoDAO.class);
        daoPago.insertarComprobantesPago(compags);
    }

    private String replaces(String cadena) {
        cadena = cadena.toUpperCase();
        cadena = cadena.replace('Ñ', 'N').
                replace('Á', 'A').replace('Ä', 'A').replace('À', 'A').
                replace('É', 'E').replace('Ë', 'E').replace('È', 'E').
                replace('Í', 'I').replace('Ï', 'I').replace('Ì', 'I').
                replace('Ó', 'O').replace('Ö', 'O').replace('Ò', 'O').
                replace('Ú', 'U').replace('Ü', 'U').replace('Ù', 'U');
        return cadena;
    }

    private AdAlumnoTarifa getAlumnoTarifa(int alutarId) {
        HSAlumnoTarifaDAO dao = ServiceFinder.findBean("springHibernateDaoAlumnoTarifa", HSAlumnoTarifaDAO.class);
        return dao.seleccionarUnAlumnoTarifa(alutarId);
    }

    private String getNombreAlumno(int aluId) {
        HSAlumnoDAO daoAc = ServiceFinder.findBean("springHibernateDaoAlumno", HSAlumnoDAO.class);
        AcAlumno alumno = daoAc.seleccionarUnAlumno(aluId);
        String nombre = alumno.getAluAppaterno().trim() + " "
                + alumno.getAluApmaterno().trim() + " "
                + alumno.getAluNombres().trim();
        return nombre.toUpperCase();
    }

    private int convertirInt(String cadena) {
        return Integer.parseInt(cadena.trim());
    }

    private Date convertirFecha(String cadena) {
        var year = Integer.parseInt(cadena.substring(0, 4).trim());
        var month = Integer.parseInt(cadena.substring(4, 6).trim());
        var dayOfMonth = Integer.parseInt(cadena.substring(6, 8).trim());

        var time = LocalDate.of(year, month, dayOfMonth);
        return helper.convertToDate(time);
    }

    private Float convertirFloat(String cadena) {
        var integer = Float.parseFloat(cadena.substring(0, 7).trim());
        float decimal = Float.parseFloat(cadena.substring(7, 9).trim()) / 100;

        return integer + decimal;
    }

    private Float sumatoriaImportes(String... importes) {
        float sumatoria = 0;
        for (String str : importes) {
            sumatoria += convertirFloat(str);
        }
        return sumatoria;
    }
}
