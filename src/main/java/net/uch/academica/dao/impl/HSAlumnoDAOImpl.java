package net.uch.academica.dao.impl;

import net.uch.academica.dao.HSAlumnoDAO;
import net.uch.common.dao.TypedHibernateDao;
import net.uch.mapping.AcAlumno;
import net.uch.mapping.AcEspecialidad;
import org.hibernate.criterion.*;
import org.springframework.dao.support.DataAccessUtils;

import java.sql.Blob;
import java.util.List;
import java.util.Objects;

import static net.uch.common.Constants.*;

public class HSAlumnoDAOImpl extends TypedHibernateDao implements HSAlumnoDAO {

    @Override
    public Integer insertarAlumno(AcAlumno alu) {
        this.getHibernateTemplate().save(alu);
        return alu.getId();
    }

    @Override
    public List<AcAlumno> seleccionarAlumnos(String codigo, String paterno, String materno, String nombre, int especialidad) {
        return findByCriteria(AcAlumno.class, buildAlumnoSearchCriteria(codigo, paterno, materno, nombre, especialidad));
    }

    @Override
    public List<AcAlumno> seleccionarAlumnosElegiblesParaMatricula(String codigo, String paterno, String materno, String nombre, int especialidad) {
        DetachedCriteria c = buildAlumnoSearchCriteria(codigo, paterno, materno, nombre, especialidad)
                .add(Restrictions.eq(AcAlumno.REF + "." + AcAlumno.PROP_ALU_TIPO, CAT_TIPO_ALUMNO_ESTUDIANTE))
                .add(Restrictions.eq(AcAlumno.REF + "." + AcAlumno.PROP_ALU_ESTADO, CAT_ESTADO_ALUMNO_REGULAR));
        return findByCriteria(AcAlumno.class, c);
    }

    @Override
    public boolean esElegibleParaMatricula(int alumnoId) {
        var hql = "select count(a.Id) from AcAlumno a where a.Id = :alumnoId and a.AluActivo = '1' " +
                "and a.AluTipo = :tipo and a.AluEstado = :estado";
        long count = DataAccessUtils.longResult(this.getHibernateTemplate().findByNamedParam(
                hql, new String[]{"alumnoId", "tipo", "estado"},
                new Object[]{alumnoId, CAT_TIPO_ALUMNO_ESTUDIANTE, CAT_ESTADO_ALUMNO_REGULAR}));
        return count > 0;
    }

    private DetachedCriteria buildAlumnoSearchCriteria(String codigo, String paterno, String materno, String nombre, int especialidad) {
        DetachedCriteria c = DetachedCriteria.forClass(AcAlumno.class, AcAlumno.REF)
                .createCriteria(AcAlumno.REF + "." + AcAlumno.PROP_ESP)
                .add(Restrictions.eq(AcAlumno.REF + "." + AcAlumno.PROP_ALU_ACTIVO, "1"));

        if (codigo.trim().length() != 0) {
            c.add(Restrictions.eq(AcAlumno.REF + "." + AcAlumno.PROP_ALU_CODIGO, codigo));
        }
        if (paterno.trim().length() != 0) {
            c.add(Restrictions.like(AcAlumno.REF + "." + AcAlumno.PROP_ALU_APPATERNO, paterno.trim(), MatchMode.START));
        }
        if (materno.trim().length() != 0) {
            c.add(Restrictions.like(AcAlumno.REF + "." + AcAlumno.PROP_ALU_APMATERNO, materno.trim(), MatchMode.START));
        }
        if (nombre.trim().length() != 0) {
            c.add(Restrictions.like(AcAlumno.REF + "." + AcAlumno.PROP_ALU_NOMBRES, nombre, MatchMode.ANYWHERE));
        }
        if (especialidad != 0) {
            c.add(Restrictions.eq(AcAlumno.REF + "." + "Esp.Id", especialidad));
        }

        c.addOrder(Order.asc(AcAlumno.REF + "." + AcAlumno.PROP_ALU_APPATERNO))
                .addOrder(Order.asc(AcAlumno.REF + "." + AcAlumno.PROP_ALU_APMATERNO));

        return c;
    }

    @Override
    public void eliminarAlumno(String id) {
        AcAlumno alu = this.getHibernateTemplate().get(AcAlumno.class, Integer.parseInt(id));
        if (alu != null) {
            alu.setAluActivo("0");
            this.getHibernateTemplate().update(alu);
            logger.trace("Student deactivated: " + id);
        }
    }

    @Override
    public void actualizarAlumno(AcAlumno alu) {
        this.getHibernateTemplate().update(alu);
    }

    @Override
    public List<AcAlumno> seleccionarAlumnoEstPago(int especialidadId, String tipo, int semestreId) {
        return findByCriteria(AcAlumno.class, DetachedCriteria.forClass(AcAlumno.class).add(Restrictions.eq("Esp.Id", especialidadId))
                .add(Restrictions.eq(AcAlumno.PROP_ALU_TIPO, tipo))
                .add(Restrictions.eq(AcAlumno.PROP_ALU_ACTIVO, "1"))
                .add(Restrictions.eq(AcAlumno.PROP_SEM_ID, semestreId)));
    }

    @Override
    public List<AcAlumno> seleccionarAlumnoCliente(String buscar) {
        return findByCriteria(AcAlumno.class, DetachedCriteria.forClass(AcAlumno.class)
                .add(Restrictions.eq(AcAlumno.PROP_ALU_ACTIVO, "1"))
                .add(Restrictions.like(AcAlumno.PROP_ALU_APPATERNO, buscar, MatchMode.ANYWHERE))
                .addOrder(Order.asc(AcAlumno.PROP_ALU_APPATERNO))
                .addOrder(Order.asc(AcAlumno.PROP_ALU_APMATERNO))
                .addOrder(Order.asc(AcAlumno.PROP_ALU_NOMBRES)));
    }

    @Override
    public AcAlumno seleccionarUnAlumno(int id) {
        List<AcAlumno> results = findByCriteria(AcAlumno.class, DetachedCriteria.forClass(AcAlumno.class).
                add(Restrictions.eq(AcAlumno.PROP_ALU_ACTIVO, "1")).
                add(Restrictions.eq(AcAlumno.PROP_ID, id)));
        return DataAccessUtils.uniqueResult(results);
    }

    @Override
    public String maximoCodigo() {
        String codMax;
        var hqlUpdate = "select max(substring(AluCodigo, 5, 8)) from AcAlumno";
        List<String> lista = find(String.class, hqlUpdate);
        if (lista.isEmpty()) {
            codMax = "0000";
        } else {
            var x = Integer.parseInt(lista.get(0));
            codMax = String.valueOf(x + 1);
            logger.trace(String.format("valor %s ====> %s", x, codMax));
        }
        return codMax;
    }

    @Override
    public List<AcAlumno> listarAlumnosMatriculaRegularPorSeccion(int seccionId) {
        return findByCriteria(AcAlumno.class, DetachedCriteria.forClass(AcAlumno.class, "alu").add(Restrictions.eq("alu.AluActivo", "1"))
                .createCriteria("alu.AcMatriculas", "mat")
                .add(Restrictions.eq("mat.MatTipo", CAT_TIPO_MATRICULA_REGULAR))
                .add(Restrictions.eq("mat.MatActivo", "1"))
                .createCriteria("mat.AcMatriculaCursos", "matriCur")
                .add(Restrictions.eq("matriCur.Sec.Id", seccionId))
                .addOrder(Order.asc("alu.AluAppaterno")));
    }

    @Override
    public AcAlumno buscarAlumnoCodigo(String codigo) {
        DetachedCriteria c = DetachedCriteria.forClass(AcAlumno.class, AcAlumno.REF).
                createCriteria(AcAlumno.REF + ".Esp", "esp").
                add(Restrictions.and(
                        Restrictions.eq(AcAlumno.REF + "." + AcAlumno.PROP_ALU_ACTIVO, "1"),
                        Restrictions.eq(AcAlumno.REF + "." + AcAlumno.PROP_ALU_CODIGO, codigo))).
                setProjection(Projections.projectionList().
                        add(Projections.property(AcAlumno.REF + "." + AcAlumno.PROP_ID)).
                        add(Projections.property(AcAlumno.REF + "." + AcAlumno.PROP_ALU_CODIGO)).
                        add(Projections.property(AcAlumno.REF + "." + AcAlumno.PROP_ALU_APPATERNO)).
                        add(Projections.property(AcAlumno.REF + "." + AcAlumno.PROP_ALU_APMATERNO)).
                        add(Projections.property(AcAlumno.REF + "." + AcAlumno.PROP_ALU_NOMBRES)).
                        add(Projections.property(AcAlumno.REF + "." + AcAlumno.PROP_ALU_FOTO)).
                        add(Projections.property("esp.EspNombre")));

        List<Object[]> results = findByCriteria(Object[].class, c);
        if (results.isEmpty()) {
            return null;
        } else {
            Object[] r = DataAccessUtils.uniqueResult(results);

            var a = new AcAlumno();
            a.setId(Integer.parseInt(r[0].toString()));
            a.setAluCodigo(Objects.toString(r[1], null));
            a.setAluAppaterno(Objects.toString(r[2], null));
            a.setAluApmaterno(Objects.toString(r[3], null));
            a.setAluNombres(Objects.toString(r[4], null));
            a.setAluFoto((Blob) r[5]);

            var e = new AcEspecialidad();
            e.setEspNombre(r[6].toString());
            a.setEsp(e);
            return a;
        }
    }

    @Override
    public long getActiveStudentCount() {
        var hql = "select count(a.Id) from AcAlumno a where a.AluActivo = '1' and a.AluTipo = :tipo";
        return DataAccessUtils.longResult(this.getHibernateTemplate().findByNamedParam(hql, "tipo", CAT_TIPO_ALUMNO_ESTUDIANTE));
    }
}
