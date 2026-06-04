package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Student;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.TestGroups;

/**
 * SUT: {@link DeadlineExtensionsLogic}.
 */
public class DeadlineExtensionsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    private DataBundle typicalDataBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test(groups = TestGroups.INTEGRATION)
    public void testGetDeadlineForUser_extensionExists_success() {
        FeedbackSession feedbackSession = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalDataBundle.students.get("student1InCourse1");

        assert student != null;
        Instant extendedDeadlineForStudent =
                inTransaction(() -> deadlineExtensionsLogic.getDeadlineForUser(feedbackSession, student));

        assertEquals(Instant.parse("2028-04-30T23:00:00Z"), extendedDeadlineForStudent);
    }

    @Test(groups = TestGroups.INTEGRATION)
    public void testGetDeadlineForUser_extensionDoesNotExist_success() {
        FeedbackSession feedbackSession = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalDataBundle.students.get("student2InCourse1");
        Instant extendedDeadlineForStudent =
                inTransaction(() -> deadlineExtensionsLogic.getDeadlineForUser(feedbackSession, student));

        assertEquals(feedbackSession.getEndTime(), extendedDeadlineForStudent);
    }
}
