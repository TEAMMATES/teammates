package teammates.it.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.core.DeadlineExtensionsLogic;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Student;

/**
 * SUT: {@link DeadlineExtensionsLogic}.
 */
public class DeadlineExtensionsLogicIT extends BaseTestCaseWithDatabaseAccess {

    private DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    private DataBundle typicalDataBundle;

    @BeforeMethod
    protected void setUp() {
        typicalDataBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Test
    public void testGetDeadlineForUser_extensionExists_success() {
        FeedbackSession feedbackSession = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalDataBundle.students.get("student1InCourse1");

        assert student != null;
        Instant extendedDeadlineForStudent =
                inTransaction(() -> deadlineExtensionsLogic.getDeadlineForUser(feedbackSession, student));

        assertEquals(Instant.parse("2028-04-30T23:00:00Z"), extendedDeadlineForStudent);
    }

    @Test
    public void testGetDeadlineForUser_extensionDoesNotExist_success() {
        FeedbackSession feedbackSession = typicalDataBundle.feedbackSessions.get("session1InCourse1");
        Student student = typicalDataBundle.students.get("student2InCourse1");
        Instant extendedDeadlineForStudent =
                inTransaction(() -> deadlineExtensionsLogic.getDeadlineForUser(feedbackSession, student));

        assertEquals(feedbackSession.getEndTime(), extendedDeadlineForStudent);
    }
}
