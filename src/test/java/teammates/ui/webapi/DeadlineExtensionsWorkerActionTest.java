package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.common.util.TimeHelper;
import teammates.ui.request.DeadlineExtensionRequest;
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
        Instant originalEndTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(
                TimeHelper.parseInstant("2027-04-30T22:00:00Z"), "Africa/Johannesburg", false);
        Instant newEndTime1 = TimeHelper.parseInstant("2027-04-30T23:00:00Z");
        Instant newEndTime2 = TimeHelper.parseInstant("2027-05-30T23:00:00Z");
        String courseId = "idOfTypicalCourse1";
        String courseName = "Typical Course 1 with 2 Evals";
        String feedbackSessionName = "First feedback session";

        ______TS("Invalid parameters; throws InvalidHttpRequestBodyException");
        DeadlineExtensionRequest request = new DeadlineExtensionRequest(
                null, feedbackSessionName, true, new HashMap<>(), new ArrayList<>(), new HashMap<>(), new ArrayList<>());
        verifyHttpRequestBodyFailure(request);

        request = new DeadlineExtensionRequest(
            courseId, null, true, new HashMap<>(), new ArrayList<>(), new HashMap<>(), new ArrayList<>());
        verifyHttpRequestBodyFailure(request);

        ______TS("No extensions to modify; No tasks added");
        Map<String, Long> studentExtensionsToModify = new HashMap<>();
        Map<String, Long> instructorExtensionsToModify = new HashMap<>();
        List<String> studentExtensionsToRevoke = new ArrayList<>();
        List<String> instructorExtensionsToRevoke = new ArrayList<>();

        request = new DeadlineExtensionRequest(courseId, feedbackSessionName, true,
                studentExtensionsToModify, studentExtensionsToRevoke,
                instructorExtensionsToModify, instructorExtensionsToRevoke);

        DeadlineExtensionsWorkerAction action = getAction(request);
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

        // 1 student/instructor extension to update
        // 1 student/instructor extension to create
        // 1 student/instructor extension to revoke
        studentExtensionsToModify.put(studentCreate.getEmail(), newEndTime1.toEpochMilli());
        studentExtensionsToModify.put(studentExtensionUpdate.getUserEmail(), newEndTime2.toEpochMilli());
        studentExtensionsToRevoke.add(studentExtensionRevoke.getUserEmail());
        instructorExtensionsToModify.put(instructorCreate.getEmail(), newEndTime1.toEpochMilli());
        instructorExtensionsToModify.put(instructorExtensionUpdate.getUserEmail(), newEndTime2.toEpochMilli());
        instructorExtensionsToRevoke.add(instructorExtensionRevoke.getUserEmail());

        request = new DeadlineExtensionRequest(
                courseId, feedbackSessionName, true,
                studentExtensionsToModify, studentExtensionsToRevoke,
                instructorExtensionsToModify, instructorExtensionsToRevoke);
        action = getAction(request);
        action.execute();

        assertEquals(newEndTime1,
                logic.getDeadlineExtension(courseId, feedbackSessionName, studentCreate.getEmail(), false).getEndTime());
        assertEquals(newEndTime2,
                logic.getDeadlineExtension(courseId, feedbackSessionName, studentExtensionUpdate.getUserEmail(), false)
                        .getEndTime());
        verifyAbsentInDatabase(studentExtensionRevoke);

        assertEquals(newEndTime1,
                logic.getDeadlineExtension(courseId, feedbackSessionName, instructorCreate.getEmail(), true).getEndTime());
        assertEquals(newEndTime2,
                logic.getDeadlineExtension(courseId, feedbackSessionName, instructorExtensionUpdate.getUserEmail(), true)
                        .getEndTime());
        verifyAbsentInDatabase(instructorExtensionRevoke);

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 6);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        String datetimeDisplayFormat = "EEE, dd MMM yyyy, hh:mm a z";
        for (var task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String expectedSubject;

            // content[0] contains old deadline, content[1] contains new deadline
            String[] content = email.getContent().split("New Deadline:");

            if (email.getRecipient().equals(studentCreate.getEmail())
                    || email.getRecipient().equals(instructorCreate.getEmail())) {
                expectedSubject = String.format(
                        EmailType.DEADLINE_EXTENSION_GIVEN.getSubject(), courseName, feedbackSessionName);
                assertEquals(expectedSubject, email.getSubject());
                assertTrue(content[0].contains(
                        TimeHelper.formatInstant(originalEndTime, "Africa/Johannesburg", datetimeDisplayFormat)));
                assertTrue(content[1].contains(
                        TimeHelper.formatInstant(newEndTime1, "Africa/Johannesburg", datetimeDisplayFormat)));
                continue;
            }

            if (email.getRecipient().equals(studentExtensionRevoke.getUserEmail())
                    || email.getRecipient().equals(instructorExtensionRevoke.getUserEmail())) {
                expectedSubject = String.format(
                        EmailType.DEADLINE_EXTENSION_REVOKED.getSubject(), courseName, feedbackSessionName);
                assertEquals(expectedSubject, email.getSubject());
                assertTrue(content[0].contains(
                        TimeHelper.formatInstant(newEndTime1, "Africa/Johannesburg", datetimeDisplayFormat)));
                assertTrue(content[1].contains(
                        TimeHelper.formatInstant(originalEndTime, "Africa/Johannesburg", datetimeDisplayFormat)));
                continue;
            }

            expectedSubject = String.format(
                    EmailType.DEADLINE_EXTENSION_UPDATED.getSubject(), courseName, feedbackSessionName);
            assertEquals(expectedSubject, email.getSubject());
            assertTrue(content[0].contains(
                    TimeHelper.formatInstant(newEndTime1, "Africa/Johannesburg", datetimeDisplayFormat)));
            assertTrue(content[1].contains(
                    TimeHelper.formatInstant(newEndTime2, "Africa/Johannesburg", datetimeDisplayFormat)));
        }

        ______TS("notifyUsers set to false; no tasks added");

        removeAndRestoreTypicalDataBundle();

        request = new DeadlineExtensionRequest(
                courseId, feedbackSessionName, false,
                studentExtensionsToModify, studentExtensionsToRevoke,
                instructorExtensionsToModify, instructorExtensionsToRevoke);
        action = getAction(request);
        action.execute();

        assertEquals(newEndTime1,
                logic.getDeadlineExtension(courseId, feedbackSessionName, studentCreate.getEmail(), false).getEndTime());
        assertEquals(newEndTime2,
                logic.getDeadlineExtension(courseId, feedbackSessionName, studentExtensionUpdate.getUserEmail(), false)
                        .getEndTime());
        verifyAbsentInDatabase(studentExtensionRevoke);

        assertEquals(newEndTime1,
                logic.getDeadlineExtension(courseId, feedbackSessionName, instructorCreate.getEmail(), true).getEndTime());
        assertEquals(newEndTime2,
                logic.getDeadlineExtension(courseId, feedbackSessionName, instructorExtensionUpdate.getUserEmail(), true)
                        .getEndTime());
        verifyAbsentInDatabase(instructorExtensionRevoke);

        verifyNoTasksAdded();
    }

}
