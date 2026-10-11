package net.uch.asistencia.dao.impl;

import net.uch.asistencia.dao.HSAsistenciaDocenteDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.*;
import org.hibernate.criterion.*;
import org.springframework.dao.support.DataAccessUtils;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * @author cesardl
 */
public class HSAsistenciaDocenteDAOImpl extends TypedHibernateDao implements HSAsistenciaDocenteDAO {

    @Override
    public List<AcDocente> buscarDocenteDato(String dato) {
        return findByCriteria(AcDocente.class, DetachedCriteria.forClass(AcDocente.class).add(Restrictions.like(AcDocente.PROP_DOC_NOMBRE, dato, MatchMode.ANYWHERE)).
                add(Restrictions.eq(AcDocente.PROP_DOC_ACTIVO, "1")));
    }

    @Override
    public List<AcSesionAsistencia> listarSessionAsistenciaDocenteFecha(int docId, Date fechaIni, Date fechaFin, String tipasis) {
        DetachedCriteria c = DetachedCriteria.forClass(AcSesionAsistencia.class).
                add(Restrictions.between("sesFechaRegistro", fechaIni, fechaFin)).
                add(Restrictions.eq("sesActivo", "1")).
                add(Restrictions.eq("sesTipoAsistencia", tipasis));

        if (docId != 0) {
            c.add(Restrictions.eq("docente.id", docId));
        }
        c.addOrder(Order.asc("sesFechaInicio")).
                createAlias("docente", "doc").
                add(Restrictions.eq("doc.DocActivo", "1"));

        return findByCriteria(AcSesionAsistencia.class, c);
    }

    @Override
    public Object[] seleccionarSeccion(int seccionId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AcSeccion.class, "sec")
                .createCriteria("sec.Curape", "curape")
                .createCriteria("curape.Plancur", "plancur")
                .createCriteria("plancur.Cur", "cur")
                .createCriteria("cur.Esp", "esp")
                .add(Restrictions.eq("sec.SecActivo", "1"))
                .add(Restrictions.eq("curape.CurapeActivo", "1"))
                .add(Restrictions.eq("plancur.PlancurActivo", "1"))
                .add(Restrictions.eq("cur.CurActivo", "1"))
                .add(Restrictions.eq("sec.Id", seccionId));

        ProjectionList projection = Projections.projectionList()
                .add(Projections.property("cur.CurNombre"))
                .add(Projections.property("sec.SecNombre"))
                .add(Projections.property("esp.EspNombre"));
        criteria.setProjection(projection);

        List<Object[]> results = findByCriteria(Object[].class, criteria);
        return DataAccessUtils.requiredUniqueResult(results);
    }

    @Override
    public AcArea seleccionarArea(int seccionId) {
        List<AcArea> results = findByCriteria(AcArea.class, DetachedCriteria.forClass(AcArea.class).add(Restrictions.and(
                Restrictions.eq("areActivo", "1"),
                Restrictions.eq("areId", seccionId))));

        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public AcSesionEfectivaAsisDoc listaMarcadoDeAsistenciaDocente(int sesionId) {
        List<AcSesionEfectivaAsisDoc> result = findByCriteria(AcSesionEfectivaAsisDoc.class, DetachedCriteria.forClass(AcSesionEfectivaAsisDoc.class).add(Restrictions.eq("sesefeasisdocActivo", "1")).
                add(Restrictions.eq("acSesionAsistencia.sesId", sesionId))
                .createCriteria("acDocente"));

        return DataAccessUtils.uniqueResult(result);
    }

    @Override
    public void ingresarAsisteanDocente(AcSesionEfectivaAsisDoc acSesionEfectivaAsisDoc) {
        this.getHibernateTemplate().saveOrUpdate(acSesionEfectivaAsisDoc);
    }

    @Override
    public AcSesionAsistencia getSessionAsistencia(int sesionId) {
        List<AcSesionAsistencia> l = findByCriteria(AcSesionAsistencia.class, DetachedCriteria.forClass(AcSesionAsistencia.class).add(Restrictions.eq("sesId", sesionId)));

        return DataAccessUtils.uniqueResult(l);
    }

    @Override
    public void actualizarSesionDocente(int sesionId, int docenteId, Date horaIni, Date horaFin, String sesionTipo) {
        int r = getHibernateTemplate().execute(session -> {
            String hqlUpdate = "UPDATE AcSesionAsistencia SET docente.Id = :v_doc_id, "
                    + " sesFechaInicio = :v_hora_ini, "
                    + " sesFechaFin = :v_hora_fin, "
                    + " sesTipo = :v_ses_tipo "
                    + " WHERE sesId = :v_id";

            return session.createQuery(hqlUpdate).
                    setParameter("v_doc_id", docenteId).
                    setParameter("v_hora_ini", horaIni, TemporalType.TIMESTAMP).
                    setParameter("v_hora_fin", horaFin, TemporalType.TIMESTAMP).
                    setParameter("v_ses_tipo", sesionTipo).
                    setParameter("v_id", sesionId).
                    executeUpdate();
        });
        logger.trace("Total rows affected: " + r);
    }

    @Override
    public void ingresarSesionAsistencia(AcSesionAsistencia sesasis) {
        this.getHibernateTemplate().save(sesasis);
    }
}
