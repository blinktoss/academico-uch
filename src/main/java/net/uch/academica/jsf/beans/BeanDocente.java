package net.uch.academica.jsf.beans;

import net.uch.mapping.AcDocente;

/**
 * @author richard
 */
public class BeanDocente extends AcDocente {

    private static final long serialVersionUID = -2356302131658889218L;

    private String docNacimientoDesc;
    private String docResidenciaDesc;
    private String docSituacionDesc;
    private String docSexoDesc;

    public String getDocNacimientoDesc() {
        return docNacimientoDesc;
    }

    public void setDocNacimientoDesc(String docNacimientoDesc) {
        this.docNacimientoDesc = docNacimientoDesc;
    }

    public String getDocResidenciaDesc() {
        return docResidenciaDesc;
    }

    public void setDocResidenciaDesc(String docResidenciaDesc) {
        this.docResidenciaDesc = docResidenciaDesc;
    }

    public String getDocSituacionDesc() {
        return docSituacionDesc;
    }

    public void setDocSituacionDesc(String docSituacionDesc) {
        this.docSituacionDesc = docSituacionDesc;
    }

    public String getDocSexoDesc() {
        return docSexoDesc;
    }

    public void setDocSexoDesc(String docSexoDesc) {
        this.docSexoDesc = docSexoDesc;
    }
}
