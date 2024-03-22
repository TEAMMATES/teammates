package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;

import java.time.Instant;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code FeedbackSessionLogsDb}.
 */
public class FeedbackSessionLogsDbTest extends BaseTestCase {

    private FeedbackSessionLogsDb feedbackSessionLogsDb = FeedbackSessionLogsDb.inst();

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
    public void testCreateFeedbackSessionLog_success() {

        FeedbackSessionLog logToAdd = new FeedbackSessionLog(getTypicalStudent(),
                getTypicalFeedbackSessionForCourse(getTypicalCourse()), FeedbackSessionLogType.ACCESS,
                Instant.parse("2011-01-01T00:00:00Z"));
        feedbackSessionLogsDb.createFeedbackSessionLog(logToAdd);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(logToAdd));
    }
}
