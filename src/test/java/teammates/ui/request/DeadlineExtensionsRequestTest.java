package teammates.ui.request;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link DeadlineExtensionsRequest}.
 */
public class DeadlineExtensionsRequestTest extends BaseTestCase {

    @Test
    public void testConstructor_validRequest_shouldBuildWithCorrectValues() {
        FeedbackSessionAttributes feedbackSession = getTypicalDataBundle().feedbackSessions.get("session1InCourse1");

        Map<String, Instant> oldStudentDeadlines = feedbackSession.getStudentDeadlines();
        Map<String, Instant> newStudentDeadlines = new HashMap<>(oldStudentDeadlines);
        Map<String, Instant> oldInstructorDeadlines = feedbackSession.getInstructorDeadlines();
        Map<String, Instant> newInstructorDeadlines = new HashMap<>(oldInstructorDeadlines);

        DeadlineExtensionsRequest request = new DeadlineExtensionsRequest("course-id", "session-name", true,
                oldStudentDeadlines, newStudentDeadlines, oldInstructorDeadlines, newInstructorDeadlines);

        assertEquals("course-id", request.getCourseId());
        assertEquals("session-name", request.getFeedbackSessionName());
        assertTrue(request.getNotifyUsers());

        assertEquals(oldStudentDeadlines, request.getOldStudentDeadlines());
        assertEquals(newStudentDeadlines, request.getNewStudentDeadlines());
        assertEquals(oldInstructorDeadlines, request.getOldInstructorDeadlines());
        assertEquals(newInstructorDeadlines, request.getNewInstructorDeadlines());
    }

}
