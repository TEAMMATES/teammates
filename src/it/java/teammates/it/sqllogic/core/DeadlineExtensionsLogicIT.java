package teammates.it.sqllogic.core;

import java.time.Instant;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.DeadlineExtensionsLogic;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link DeadlineExtensionsLogic}.
 */
public class DeadlineExtensionsLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    private SqlDataBundle typicalDataBundle;

    @Override
    @BeforeClass
    public void setupClass() {
        super.setupClass();
        typicalDataBundle = getTypicalSqlDataBundle();
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalDataBundle);
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Test
    public void testGetExtendedDeadline_extensionExists_success() {
        FeedbackSession feedbackSession = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalDataBundle.students.get("student1InCourse1");

        assert student != null;
        Instant extendedDeadlineForStudent = deadlineExtensionsLogic.getExtendedDeadlineForUser(feedbackSession, student);

        assertNotNull(extendedDeadlineForStudent);
        assertEquals(Instant.parse("2027-04-30T23:00:00Z"), extendedDeadlineForStudent);
    }

    @Test
    public void testGetExtendedDeadline_extensionDoesNotExist_null() {
        FeedbackSession feedbackSession = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalDataBundle.students.get("student2InCourse1");
        Instant extendedDeadlineForStudent = deadlineExtensionsLogic.getExtendedDeadlineForUser(feedbackSession, student);

        assertNull(extendedDeadlineForStudent);
    }
}
