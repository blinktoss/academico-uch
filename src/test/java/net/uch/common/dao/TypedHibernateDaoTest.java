package net.uch.common.dao;

import net.uch.SpringConfigurationTest;
import net.uch.Stubs;
import net.uch.mapping.AcSemestre;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Integration test for {@link TypedHibernateDao}, run against the development DB.
 * Exercises every helper overload once so the 50+ DAOs built on top of it rest on a
 * verified base, using stable semester fixture rows from {@link Stubs}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TypedHibernateDaoTest extends SpringConfigurationTest {

    @Autowired
    private SessionFactory sessionFactory;
    private SemesterProbeDao dao;

    @Before
    public void setUpDao() {
        dao = new SemesterProbeDao();
        dao.setSessionFactory(sessionFactory);
    }

    @Test
    public void findWithoutParametersReturnsTypedList() {
        List<AcSemestre> semesters = dao.find(AcSemestre.class, "from AcSemestre where SemActivo = '1'");

        assertFalse(semesters.isEmpty());
    }

    @Test
    public void findWithSingleParameterFiltersById() {
        List<AcSemestre> semesters = dao.find(AcSemestre.class,
                "from AcSemestre where Id = :id", "id", Stubs.SEMESTRE_2010_2_ID);

        assertEquals(1, semesters.size());
        assertEquals(Integer.valueOf(Stubs.SEMESTRE_2010_2_ID), semesters.get(0).getId());
    }

    @Test
    public void findWithParallelArraysAppliesEveryParameter() {
        List<AcSemestre> semesters = dao.find(AcSemestre.class,
                "from AcSemestre where Id = :id and SemActivo = :active",
                new String[]{"id", "active"},
                new Object[]{Stubs.SEMESTRE_2010_2_ID, "1"});

        assertEquals(1, semesters.size());
    }

    @Test
    public void findWithParameterMapAppliesEveryParameter() {
        List<AcSemestre> semesters = dao.find(AcSemestre.class,
                "from AcSemestre where Id = :id and SemActivo = :active",
                Map.of("id", Stubs.SEMESTRE_2010_2_ID, "active", "1"));

        assertEquals(1, semesters.size());
    }

    @Test
    public void findExpandsCollectionValuesAsInLists() {
        List<AcSemestre> semesters = dao.find(AcSemestre.class,
                "from AcSemestre where Id in (:ids)", "ids",
                Arrays.asList(Stubs.SEMESTRE_2010_1_ID, Stubs.SEMESTRE_2010_2_ID));

        assertEquals(2, semesters.size());
    }

    @Test
    public void findSupportsScalarResultTypes() {
        List<Long> counts = dao.find(Long.class, "select count(s) from AcSemestre s");

        assertEquals(1, counts.size());
        assertTrue(counts.get(0) > 0);
    }

    @Test
    public void findByCriteriaReturnsTypedList() {
        DetachedCriteria criteria = DetachedCriteria.forClass(AcSemestre.class)
                .add(Restrictions.idEq(Stubs.SEMESTRE_2010_2_ID));

        List<AcSemestre> semesters = dao.findByCriteria(AcSemestre.class, criteria);

        assertEquals(1, semesters.size());
        assertEquals(Integer.valueOf(Stubs.SEMESTRE_2010_2_ID), semesters.get(0).getId());
    }

    /**
     * Minimal concrete subclass: the class under test is abstract by design.
     */
    private static class SemesterProbeDao extends TypedHibernateDao {
    }
}
