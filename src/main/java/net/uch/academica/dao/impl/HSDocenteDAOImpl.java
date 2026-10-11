package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSDocenteDAO;
import net.uch.academica.jsf.MbDocenteDisponibilidad;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcDocente;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.springframework.dao.support.DataAccessUtils;

import java.util.ArrayList;
import java.util.List;

import static net.uch.mapping.AcDocente.PROP_DOC_ACTIVO;
import static net.uch.mapping.AcDocente.PROP_DOC_NOMBRE;

public class HSDocenteDAOImpl extends TypedHibernateDao implements HSDocenteDAO {

    @Override
    public void insertarDocente(AcDocente doc) {
        getHibernateTemplate().save(doc);
    }

    @Override
    public List<Object[]> seleccionarDocente(String codigo, String nombre) {
        List<String> names = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        names.add("activo");
        values.add("1");
        var queryString = "select doc.Id, doc.DocCodigo, doc.DocNombre, doc.DocCorreo, doc.DocTelefono, doc.DocNacimiento, doc.DocResidencia, "
                + "doc.DocPeriodoInicio, doc.DocSituacion, doc.DocSexo, doc.DocDni, doc.DocAppaterno, doc.DocApmaterno, doc.DocNombres, "
                + "(select dis.DisDes from TbDistrito dis where dis.Id = doc.DocNacimiento), "
                + "(select dis.DisDes from TbDistrito dis where dis.Id = doc.DocResidencia), "
                + "(select cat.CatDescripcionElemento from TbCatalogo cat where concat(cat.CatCodigoGrupo, cat.CatCodigoElemento) = doc.DocSituacion), "
                + "(select cat.CatDescripcionElemento from TbCatalogo cat where concat(cat.CatCodigoGrupo, cat.CatCodigoElemento) = doc.DocSexo) "
                + "from AcDocente doc "
                + "where doc.DocActivo= :activo ";
        if (!codigo.trim().isEmpty()) {
            queryString += "and doc.DocCodigo like :codigo ";
            names.add("codigo");
            values.add(codigo + "%");
        }
        if (!nombre.trim().isEmpty()) {
            queryString += "and doc.DocNombre like :nombre ";
            names.add("nombre");
            values.add("%" + nombre + "%");
        }
        queryString += "order by doc.Id";
        return find(Object[].class,
                queryString, names.toArray(new String[0]), values.toArray(new Object[0]));
    }

    @Override
    public void eliminarDocente(Integer id) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update AcDocente set DocActivo = :v_activo where Id = :v_id";
            return session.createQuery(hqlUpdate)
                    .setParameter("v_activo", "0")
                    .setParameter("v_id", id).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public void actualizarDocente(AcDocente doc) {
        AcDocente persistido = getHibernateTemplate().get(AcDocente.class, doc.getId());
        persistido.setDocActivo(doc.getDocActivo());
        persistido.setDocApmaterno(doc.getDocApmaterno());
        persistido.setDocAppaterno(doc.getDocAppaterno());
        persistido.setDocCodigo(doc.getDocCodigo());
        persistido.setDocCorreo(doc.getDocCorreo());
        persistido.setDocDni(doc.getDocDni());
        persistido.setDocNacimiento(doc.getDocNacimiento());
        persistido.setDocNombre(doc.getDocNombre());
        persistido.setDocNombres(doc.getDocNombres());
        persistido.setDocPassword(doc.getDocPassword());
        persistido.setDocPeriodoInicio(doc.getDocPeriodoInicio());
        persistido.setDocResidencia(doc.getDocResidencia());
        persistido.setDocSexo(doc.getDocSexo());
        persistido.setDocSituacion(doc.getDocSituacion());
        persistido.setDocTelefono(doc.getDocTelefono());
        getHibernateTemplate().update(persistido);
    }

    @Override
    public List<AcDocente> seleccionarDocente() {
        return findByCriteria(AcDocente.class, DetachedCriteria.forClass(AcDocente.class).add(Restrictions.eq(PROP_DOC_ACTIVO, "1"))
                .addOrder(Order.asc(PROP_DOC_NOMBRE)));
    }

    @Override
    public AcDocente seleccionarDocente(int id) {
        List<AcDocente> results = findByCriteria(AcDocente.class, DetachedCriteria.forClass(AcDocente.class).add(Restrictions.eq(PROP_DOC_ACTIVO, "1"))
                .add(Restrictions.eq("Id", id)));
        return DataAccessUtils.requiredUniqueResult(results);
    }

    @Override
    public boolean existeDocente(String dni) {
        List<Long> results = findByCriteria(Long.class, DetachedCriteria.forClass(AcDocente.class).add(Restrictions.eq(PROP_DOC_ACTIVO, "1")).
                add(Restrictions.eq(AcDocente.PROP_DOC_DNI, dni)).
                setProjection(Projections.rowCount()));
        long x = DataAccessUtils.requiredUniqueResult(results);
        logger.trace("Teacher registered: " + x);
        return x != 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MbDocenteDisponibilidad> findByCodeAndGetAvailability(String code, String name) {
        return this.getHibernateTemplate().execute(session -> {
            String sql = "select doc.doc_id as rowTeacherId, " +
                    "doc.doc_codigo as rowTeacherCode, " +
                    "doc.doc_nombre as rowTeacherNames, " +
                    "(select count(*) from ac_disponibilidad disp " +
                    "where disp.dis_activo = '1' and disp.doc_id = doc.doc_id) as rowTeacherAvailability " +
                    "from ac_docente doc " +
                    "where doc.doc_activo = '1' ";
            if (!code.isEmpty()) {
                sql += "and doc.doc_codigo = :teacher_code";
            }
            if (!name.isEmpty()) {
                sql += "and doc.doc_nombre like :teacher_name";
            }
            var sqlQuery = session.createSQLQuery(sql)
                    .addScalar("rowTeacherId")
                    .addScalar("rowTeacherCode")
                    .addScalar("rowTeacherNames")
                    .addScalar("rowTeacherAvailability", IntegerType.INSTANCE);

            if (!code.isEmpty()) {
                sqlQuery.setParameter("teacher_code", code);
            }
            if (!name.isEmpty()) {
                sqlQuery.setParameter("teacher_name", "%" + name + "%");
            }
            sqlQuery.setResultTransformer(Transformers.aliasToBean(MbDocenteDisponibilidad.class));
            return sqlQuery.list();
        });
    }

    @Override
    public List<Object[]> findAvailabilitiesByTeacher(int teacherId) {
        String hql = "select dis.DisObservacion, " +
                "(select turdet.TurdetHora from AcTurnoDetalle turdet where turdet.Id = dis.TurdetId and turdet.TurdetActivo = '1'), " +
                "(select cat.CatDescripcionElemento from TbCatalogo cat where concat(cat.CatCodigoGrupo, cat.CatCodigoElemento) = dis.DisDia) " +
                "from AcDisponibilidad  dis " +
                "where dis.DisActivo = '1' " +
                "and dis.Doc.Id = :teacherId";

        return find(Object[].class, hql, "teacherId", teacherId);
    }
}
