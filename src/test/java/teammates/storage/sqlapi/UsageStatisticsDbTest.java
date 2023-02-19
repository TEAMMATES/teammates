package teammates.storage.sqlapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code UsageStatisticsDb}.
 */
public class UsageStatisticsDbTest extends BaseTestCase {

    private UsageStatisticsDb usageStatisticsDb = UsageStatisticsDb.inst();

    private Session session;

    @BeforeMethod
    public void setUp() {
        session = mock(Session.class);
        SessionFactory sessionFactory = mock(SessionFactory.class);
        HibernateUtil.setSessionFactory(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void testCreateUsageStatistics_success() {
        UsageStatistics newUsageStatistics = new UsageStatistics(
                Instant.parse("2011-01-01T00:00:00Z"), 1, 0, 0, 0, 0, 0, 0, 0);

        usageStatisticsDb.createUsageStatistics(newUsageStatistics);

        verify(session, times(1)).persist(newUsageStatistics);
    }

}
