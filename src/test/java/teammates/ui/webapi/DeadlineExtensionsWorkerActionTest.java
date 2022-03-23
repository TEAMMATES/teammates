package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.common.util.TimeHelper;
import teammates.ui.request.DeadlineExtensionsRequest;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link DeadlineExtensionWorkerAction}.
 */
public class DeadlineExtensionsWorkerActionTest extends BaseActionTest<DeadlineExtensionsWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.DEADLINE_EXTENSIONS_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");

        String courseId = feedbackSession.getCourseId();
        String feedbackSessionName = feedbackSession.getFeedbackSessionName();

        ______TS("Invalid parameters; throws InvalidHttpRequestBodyException");

        DeadlineExtensionsRequest request = new DeadlineExtensionsRequest(
                null, feedbackSessionName, true, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
        verifyHttpRequestBodyFailure(request);

        request = new DeadlineExtensionsRequest(
                courseId, null, true, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
        verifyHttpRequestBodyFailure(request);

        ______TS("Empty deadline extension maps; No tasks added");

        Map<String, Instant> oldStudentDeadlines = new HashMap<>();
        Map<String, Instant> newStudentDeadlines = new HashMap<>();
        Map<String, Instant> oldInstructorDeadlines = new HashMap<>();
        Map<String, Instant> newInstructorDeadlines = new HashMap<>();

        request = new DeadlineExtensionsRequest(courseId, feedbackSessionName, true,
                oldStudentDeadlines, newStudentDeadlines, oldInstructorDeadlines, newInstructorDeadlines);
        DeadlineExtensionsWorkerAction action = getAction(request);
        action.execute();

        verifyNoTasksAdded();

        ______TS("No extensions to modify; No tasks added");

        oldStudentDeadlines = feedbackSession.getStudentDeadlines();
        newStudentDeadlines = new HashMap<>(oldStudentDeadlines);
        oldInstructorDeadlines = feedbackSession.getInstructorDeadlines();
        newInstructorDeadlines = new HashMap<>(oldInstructorDeadlines);

        request = new DeadlineExtensionsRequest(courseId, feedbackSessionName, true,
                oldStudentDeadlines, newStudentDeadlines, oldInstructorDeadlines, newInstructorDeadlines);
        action = getAction(request);
        action.execute();

        verifyNoTasksAdded();

        ______TS("Typical success case");

        StudentAttributes studentCreate = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes instructorCreate = typicalBundle.instructors.get("instructor3OfCourse1");

        DeadlineExtensionAttributes studentExtensionUpdate =
                typicalBundle.deadlineExtensions.get("student3InCourse1Session1");
        DeadlineExtensionAttributes instructorExtensionUpdate =
                typicalBundle.deadlineExtensions.get("instructor1InCourse1Session1");
        DeadlineExtensionAttributes studentExtensionRevoke =
                typicalBundle.deadlineExtensions.get("student4InCourse1Session1");
        DeadlineExtensionAttributes instructorExtensionRevoke =
                typicalBundle.deadlineExtensions.get("instructor2InCourse1Session1");

        Instant originalEndTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                feedbackSession.getEndTime(), feedbackSession.getTimeZone(), false);
        Instant newEndTime1 = oldStudentDeadlines.get(studentExtensionUpdate.getUserEmail());
        Instant newEndTime2 = newEndTime1.plus(Duration.ofDays(2));

        // 1 student/instructor extension to create
        assertFalse(oldStudentDeadlines.containsKey(studentCreate.getEmail()));
        assertFalse(oldInstructorDeadlines.containsKey(instructorCreate.getEmail()));
        newStudentDeadlines.put(studentCreate.getEmail(), newEndTime1);
        newInstructorDeadlines.put(instructorCreate.getEmail(), newEndTime1);

        // 1 student/instructor extension to update
        assertEquals(oldStudentDeadlines.get(studentExtensionUpdate.getUserEmail()), newEndTime1);
        assertEquals(oldInstructorDeadlines.get(instructorExtensionUpdate.getUserEmail()), newEndTime1);
        newStudentDeadlines.put(studentExtensionUpdate.getUserEmail(), newEndTime2);
        newInstructorDeadlines.put(instructorExtensionUpdate.getUserEmail(), newEndTime2);

        // 1 student/instructor extension to revoke
        assertTrue(oldStudentDeadlines.containsKey(studentExtensionRevoke.getUserEmail()));
        assertTrue(oldInstructorDeadlines.containsKey(instructorExtensionRevoke.getUserEmail()));
        newStudentDeadlines.remove(studentExtensionRevoke.getUserEmail());
        newInstructorDeadlines.remove(instructorExtensionRevoke.getUserEmail());

        request = new DeadlineExtensionsRequest(courseId, feedbackSessionName, true,
                oldStudentDeadlines, newStudentDeadlines, oldInstructorDeadlines, newInstructorDeadlines);
        action = getAction(request);
        action.execute();

        // 1 student/instructor extension created
        assertEquals(newEndTime1, logic.getDeadlineExtension(
                courseId, feedbackSessionName, studentCreate.getEmail(), false).getEndTime());
        assertEquals(newEndTime1, logic.getDeadlineExtension(
                courseId, feedbackSessionName, instructorCreate.getEmail(), true).getEndTime());

        // 1 student/instructor extension updated
        assertEquals(newEndTime2, logic.getDeadlineExtension(
                courseId, feedbackSessionName, studentExtensionUpdate.getUserEmail(), false).getEndTime());
        assertEquals(newEndTime2, logic.getDeadlineExtension(
                courseId, feedbackSessionName, instructorExtensionUpdate.getUserEmail(), true).getEndTime());

        // 1 student/instructor extension revoked
        verifyAbsentInDatabase(studentExtensionRevoke);
        verifyAbsentInDatabase(instructorExtensionRevoke);

        // Verify emails sent correctly
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 6);

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        String courseName = course.getName();
        String datetimeDisplayFormat = "EEE, dd MMM yyyy, hh:mm a z";
        String originalEndTimeFormatted =
                TimeHelper.formatInstant(originalEndTime, "Africa/Johannesburg", datetimeDisplayFormat);
        String newEndTime1Formatted =
                TimeHelper.formatInstant(newEndTime1, "Africa/Johannesburg", datetimeDisplayFormat);
        String newEndTime2Formatted =
                TimeHelper.formatInstant(newEndTime2, "Africa/Johannesburg", datetimeDisplayFormat);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();

        for (var task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String userEmail = email.getRecipient();
            String expectedSubject;

            // content[0] contains old deadline, content[1] contains new deadline
            String[] content = email.getContent().split("New Deadline:");

            if (userEmail.equals(studentCreate.getEmail()) || userEmail.equals(instructorCreate.getEmail())) {
                expectedSubject = String.format(
                        EmailType.DEADLINE_EXTENSION_GIVEN.getSubject(), courseName, feedbackSessionName);
                assertEquals(expectedSubject, email.getSubject());
                assertTrue(content[0].contains(originalEndTimeFormatted));
                assertTrue(content[1].contains(newEndTime1Formatted));
                continue;
            }

            if (userEmail.equals(studentExtensionUpdate.getUserEmail())
                    || userEmail.equals(instructorExtensionUpdate.getUserEmail())) {
                expectedSubject = String.format(
                        EmailType.DEADLINE_EXTENSION_UPDATED.getSubject(), courseName, feedbackSessionName);
                assertEquals(expectedSubject, email.getSubject());
                assertTrue(content[0].contains(newEndTime1Formatted));
                assertTrue(content[1].contains(newEndTime2Formatted));
                continue;
            }

            if (userEmail.equals(studentExtensionRevoke.getUserEmail())
                    || userEmail.equals(instructorExtensionRevoke.getUserEmail())) {
                expectedSubject = String.format(
                        EmailType.DEADLINE_EXTENSION_REVOKED.getSubject(), courseName, feedbackSessionName);
                assertEquals(expectedSubject, email.getSubject());
                assertTrue(content[0].contains(newEndTime1Formatted));
                assertTrue(content[1].contains(originalEndTimeFormatted));
                continue;
            }

            fail("Email sent to wrong instructor: " + userEmail);
        }

        ______TS("notifyUsers set to false; no tasks added");

        removeAndRestoreTypicalDataBundle();

        assertNull(logic.getDeadlineExtension(courseId, feedbackSessionName, studentCreate.getEmail(), false));
        assertNull(logic.getDeadlineExtension(courseId, feedbackSessionName, instructorCreate.getEmail(), true));
        assertEquals(newEndTime1, logic.getDeadlineExtension(
                courseId, feedbackSessionName, studentExtensionUpdate.getUserEmail(), false).getEndTime());
        assertEquals(newEndTime1, logic.getDeadlineExtension(
                courseId, feedbackSessionName, instructorExtensionUpdate.getUserEmail(), true).getEndTime());
        verifyPresentInDatabase(studentExtensionRevoke);
        verifyPresentInDatabase(instructorExtensionRevoke);

        request = new DeadlineExtensionsRequest(courseId, feedbackSessionName, false,
                oldStudentDeadlines, newStudentDeadlines, oldInstructorDeadlines, newInstructorDeadlines);
        action = getAction(request);
        action.execute();

        // 1 student/instructor extension created
        assertEquals(newEndTime1, logic.getDeadlineExtension(
                courseId, feedbackSessionName, studentCreate.getEmail(), false).getEndTime());
        assertEquals(newEndTime1, logic.getDeadlineExtension(
                courseId, feedbackSessionName, instructorCreate.getEmail(), true).getEndTime());

        // 1 student/instructor extension updated
        assertEquals(newEndTime2, logic.getDeadlineExtension(
                courseId, feedbackSessionName, studentExtensionUpdate.getUserEmail(), false).getEndTime());
        assertEquals(newEndTime2, logic.getDeadlineExtension(
                courseId, feedbackSessionName, instructorExtensionUpdate.getUserEmail(), true).getEndTime());

        // 1 student/instructor extension revoked
        verifyAbsentInDatabase(studentExtensionRevoke);
        verifyAbsentInDatabase(instructorExtensionRevoke);

        // No emails sent
        verifyNoTasksAdded();
    }

}
