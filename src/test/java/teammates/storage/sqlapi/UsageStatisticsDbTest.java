package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;

import java.time.Instant;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
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

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateUsageStatistics_success() {
        UsageStatistics newUsageStatistics = new UsageStatistics(
                Instant.parse("2011-01-01T00:00:00Z"), 1, 0, 0, 0, 0, 0, 0, 0);

        usageStatisticsDb.createUsageStatistics(newUsageStatistics);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(newUsageStatistics));
    }

}
