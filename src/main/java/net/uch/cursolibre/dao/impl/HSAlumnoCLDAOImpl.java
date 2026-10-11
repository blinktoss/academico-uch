package net.uch.cursolibre.dao.impl;

import net.uch.common.dao.TypedHibernateDao;
import net.uch.cursolibre.dao.HSAlumnoCLDAO;
import net.uch.mapping.ClAlumno;
import net.uch.mapping.ClAlumnoTarifa;
import net.uch.mapping.SpGenerarCodigo;
import org.hibernate.criterion.*;
import org.hibernate.type.StringType;
import org.springframework.dao.support.DataAccessUtils;

import java.util.List;

public class HSAlumnoCLDAOImpl extends TypedHibernateDao implements HSAlumnoCLDAO {

    @Override
    public void insertarAlumnoCl(ClAlumno alu) {
        this.getHibernateTemplate().save(alu);
    }

    @Override
    public void modificarAlumnoCl(ClAlumno alu) {
        this.getHibernateTemplate().update(alu);
    }

    @Override
    public void eliminarAlumnoCl(String aluId) {
        int r = this.getHibernateTemplate().execute(session -> {
            var hqlUpdate = "update ClAlumno set aluActivo = :v_activo where aluId = :v_id";
            return session.createQuery(hqlUpdate).setParameter("v_activo", "0").setParameter("v_id", aluId).executeUpdate();
        });
        logger.trace("Rows affected: " + r);
    }

    @Override
    public List<ClAlumno> seleccionarAlumnosLibres(String codigo, String paterno, String materno, String nombre) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ClAlumno.class).
                add(Restrictions.eq(ClAlumno.PROP_ALU_ACTIVO, "1"));

        if (codigo.trim().length() != 0) {
            criteria.add(Restrictions.like(ClAlumno.PROP_ALU_CODIGO, codigo, MatchMode.START));
        }
        if (paterno.trim().length() != 0) {
            criteria.add(Restrictions.like(ClAlumno.PROP_ALU_AP_PATERNO, paterno, MatchMode.START));
        }
        if (materno.trim().length() != 0) {
            criteria.add(Restrictions.like(ClAlumno.PROP_ALU_AP_MATERNO, materno, MatchMode.START));
        }
        if (nombre.trim().length() != 0) {
            criteria.add(Restrictions.like(ClAlumno.PROP_ALU_NOMBRES, nombre, MatchMode.ANYWHERE));
        }

        criteria.addOrder(Order.asc(ClAlumno.PROP_ALU_AP_PATERNO)).
                addOrder(Order.asc(ClAlumno.PROP_ALU_AP_MATERNO)).
                addOrder(Order.asc(ClAlumno.PROP_ALU_NOMBRES));

        return findByCriteria(ClAlumno.class, criteria);
    }

    @Override
    public String maximoCodigo() {
        List<SpGenerarCodigo> results = findByNamedQuery(SpGenerarCodigo.class, "sp_Generar_Codigo");
        SpGenerarCodigo r = DataAccessUtils.requiredUniqueResult(results);
        return r.getCodigocl();
    }

    @Override
    public ClAlumno buscarAlumnoPorAluId(Integer aluId) {
        return this.getHibernateTemplate().get(ClAlumno.class, aluId);
    }

    @Override
    public List<ClAlumno> seleccionarAlumnosPorCodigo(String codigo) {
        return findByCriteria(ClAlumno.class, DetachedCriteria.forClass(ClAlumno.class).add(Restrictions.eq(ClAlumno.PROP_ALU_ACTIVO, "1")).
                add(Restrictions.like(ClAlumno.PROP_ALU_CODIGO, codigo, MatchMode.START)).
                addOrder(Order.asc(ClAlumno.PROP_ALU_AP_PATERNO)));
    }

    @Override
    public List<ClAlumno> seleccionarAlumnosPorApellidos(String apellidos) {
        return findByCriteria(ClAlumno.class, DetachedCriteria.forClass(ClAlumno.class).add(Restrictions.eq(ClAlumno.PROP_ALU_ACTIVO, "1")).
                add(Restrictions.sqlRestriction(
                        "concat({alias}.alu_appaterno,' ',{alias}.alu_apmaterno) like ?", "%" + apellidos + "%", StringType.INSTANCE)).
                addOrder(Order.asc(ClAlumno.PROP_ALU_AP_PATERNO)).
                addOrder(Order.asc(ClAlumno.PROP_ALU_AP_MATERNO)).
                addOrder(Order.asc(ClAlumno.PROP_ALU_NOMBRES)));
    }

    @Override
    public List<ClAlumno> listaAlumnosDatos(String code, String paterno, String materno, String nombres, int modId) {
        DetachedCriteria c = DetachedCriteria.forClass(ClAlumno.class, "alu").
                addOrder(Order.asc("alu.aluAppaterno")).
                addOrder(Order.asc("alu.aluApmaterno")).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).
                createCriteria("alu.clAlumnoTarifas", "alutar").
                createCriteria("alutar.clEstructuraPagosDetalle", "estpagdet").
                createCriteria("estpagdet.clEstructuraPagos", "estpag").
                add(Restrictions.eq("alu.aluActivo", "1")).
                add(Restrictions.eq("alutar.alutarActivo", "1")).
                add(Restrictions.eq("estpagdet.estpagdetActivo", "1")).
                add(Restrictions.eq("estpag.estpagActivo", "1"));

        if (!code.trim().isEmpty()) {
            c.add(Restrictions.like("alu.aluCodigo", code, MatchMode.START));
        }
        if (!paterno.trim().isEmpty()) {
            c.add(Restrictions.like("alu.aluAppaterno", paterno, MatchMode.START));
        }
        if (!materno.trim().isEmpty()) {
            c.add(Restrictions.like("alu.aluApmaterno", materno, MatchMode.START));
        }
        if (!nombres.trim().isEmpty()) {
            c.add(Restrictions.like("alu.aluNombres", nombres, MatchMode.ANYWHERE));
        }
        if (modId != 0) {
            c.add(Restrictions.eq("estpag.clModulo.modId", modId));
        }

        return findByCriteria(ClAlumno.class, c);
    }

    @Override
    public List<ClAlumnoTarifa> listaAlumnoTarifa(int aluId, int modId) {
        return findByCriteria(ClAlumnoTarifa.class, DetachedCriteria.forClass(ClAlumnoTarifa.class, "alutar").createCriteria("alutar.clEstructuraPagosDetalle", "estpagdet").
                createCriteria("estpagdet.clEstructuraPagos", "estpag").
                add(Restrictions.eq("alutar.alutarActivo", "1")).
                add(Restrictions.eq("estpagdet.estpagdetActivo", "1")).
                add(Restrictions.eq("estpag.estpagActivo", "1")).
                add(Restrictions.eq("alutar.clAlumno.aluId", aluId)).
                add(Restrictions.eq("estpag.clModulo.modId", modId)).
                addOrder(Order.asc("alutar.clEstructuraPagosDetalle.estpagdetId")));
    }

    @Override
    public boolean existeAlumnoUnivRegistrado(Integer id) {
        return this.getSessionFactory().getCurrentSession().createCriteria(ClAlumno.class).
                add(Restrictions.and(
                        Restrictions.eq(ClAlumno.PROP_ALU_ACTIVO, "1"),
                        Restrictions.eq("aluCp", id))).uniqueResult() != null;
    }
}
