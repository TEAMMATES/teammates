package teammates.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link UpdateFeedbackSessionAction}.
 */
public class UpdateFeedbackSessionActionTest extends BaseActionTest<UpdateFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Missing request body");

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        verifyHttpRequestBodyFailure(null, param);

        ______TS("Not enough parameters");

        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());

        verifyHttpParameterFailure(updateRequest);
        verifyHttpParameterFailure(updateRequest,
                // Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false)
        );
        verifyHttpParameterFailure(updateRequest,
                // Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false)
        );
        verifyHttpParameterFailure(updateRequest,
                // Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName()
        );

        ______TS("success: Typical case");

        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        JsonResult r = getJsonResult(a);

        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        session = logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        assertEquals(session.getCourseId(), response.getCourseId());
        assertEquals(session.getTimeZone(), response.getTimeZone());
        assertEquals(session.getFeedbackSessionName(), response.getFeedbackSessionName());

        assertEquals(session.getInstructions(), response.getInstructions());

        assertEquals(session.getStartTime().toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(session.getEndTime().toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(session.getGracePeriodMinutes(), response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(session.getSessionVisibleFromTime().toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());
        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(session.getResultsVisibleFromTime().toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(session.isClosingEmailEnabled(), response.getIsClosingEmailEnabled());
        assertEquals(session.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

        assertEquals(session.getCreatedTime().toEpochMilli(), response.getCreatedAtTimestamp());
        assertNull(session.getDeletedTime());

        assertEquals("instructions", response.getInstructions());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, "Africa/Johannesburg").toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, "Africa/Johannesburg").toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(5, response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, "Africa/Johannesburg").toEpochMilli(), response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, "Africa/Johannesburg").toEpochMilli(), response.getCustomResponseVisibleTimestamp().longValue());

        assertFalse(response.getIsClosingEmailEnabled());
        assertFalse(response.getIsPublishedEmailEnabled());

        assertNotNull(response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        Map<String, Long> expectedStudentDeadlines = convertDeadlinesToLong(session.getStudentDeadlines());
        assertEquals(expectedStudentDeadlines, response.getStudentDeadlines());
        Map<String, Long> expectedInstructorDeadlines = convertDeadlinesToLong(session.getInstructorDeadlines());
        assertEquals(expectedInstructorDeadlines, response.getInstructorDeadlines());

        // The typical feedback session update request does not change any selective deadlines.
        verifyNoTasksAdded();
    }

    @Test
    public void testExecute_changeDeadlineForStudents_shouldChangeDeadlinesCorrectlyWhenAppropriate() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        final String studentAEmailAddress = "student1InCourse1@gmail.tmt";
        Map<String, Long> expectedStudentDeadlines = convertDeadlinesToLong(session.getStudentDeadlines());
        Instant endTime = session.getEndTime();
        // These are arbitrary.
        long endTimePlus1Day = endTime.plus(Duration.ofDays(1)).toEpochMilli();
        long endTimePlus2Days = endTime.plus(Duration.ofDays(2)).toEpochMilli();

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        verifyNoTasksAdded();

        ______TS("create new deadline extension for student");

        assertNull(expectedStudentDeadlines.get(studentAEmailAddress));

        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        Map<String, Long> newStudentDeadlines = convertDeadlinesToLong(updateRequest.getStudentDeadlines());
        newStudentDeadlines.put(studentAEmailAddress, endTimePlus1Day);
        updateRequest.setStudentDeadlines(newStudentDeadlines);

        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        expectedStudentDeadlines.put(studentAEmailAddress, endTimePlus1Day);
        assertEquals(expectedStudentDeadlines, response.getStudentDeadlines());
        assertEquals(endTimePlus1Day, logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), studentAEmailAddress, false).getEndTime().toEpochMilli());

        verifyNoTasksAdded();

        ______TS("update deadline extension for student");

        assertNotEquals(endTimePlus2Days, expectedStudentDeadlines.get(studentAEmailAddress));

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        newStudentDeadlines = convertDeadlinesToLong(updateRequest.getStudentDeadlines());
        newStudentDeadlines.put(studentAEmailAddress, endTimePlus2Days);
        updateRequest.setStudentDeadlines(newStudentDeadlines);

        a = getAction(updateRequest, param);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        expectedStudentDeadlines.put(studentAEmailAddress, endTimePlus2Days);
        assertEquals(expectedStudentDeadlines, response.getStudentDeadlines());
        assertEquals(endTimePlus2Days, logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), studentAEmailAddress, false).getEndTime().toEpochMilli());

        verifyNoTasksAdded();

        ______TS("delete deadline extension for student");

        assertNotNull(expectedStudentDeadlines.get(studentAEmailAddress));

        // The typical update request does not contain the course 1 student 1's email.
        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());

        a = getAction(updateRequest, param);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        // The deadline for course 1 student 1 was deleted; the map no longer contains a deadline for them.
        expectedStudentDeadlines.remove(studentAEmailAddress);
        assertEquals(expectedStudentDeadlines, response.getStudentDeadlines());
        assertNull(logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), studentAEmailAddress, false));

        verifyNoTasksAdded();

        ______TS("C_UD on extensions for different students within the same request");

        final String studentBEmailAddress = "student3InCourse1@gmail.tmt";
        final String studentCEmailAddress = "student4InCourse1@gmail.tmt";

        assertNull(expectedStudentDeadlines.get(studentAEmailAddress));
        assertNotEquals(endTimePlus2Days, expectedStudentDeadlines.get(studentBEmailAddress));
        assertNotNull(expectedStudentDeadlines.get(studentCEmailAddress));

        param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(true),
        };
        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        newStudentDeadlines = convertDeadlinesToLong(updateRequest.getStudentDeadlines());
        newStudentDeadlines.put(studentAEmailAddress, endTimePlus1Day);
        newStudentDeadlines.put(studentBEmailAddress, endTimePlus2Days);
        newStudentDeadlines.remove(studentCEmailAddress);
        updateRequest.setStudentDeadlines(newStudentDeadlines);

        a = getAction(updateRequest, param);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        // Create deadline.
        expectedStudentDeadlines.put(studentAEmailAddress, endTimePlus1Day);
        // Update deadline.
        expectedStudentDeadlines.put(studentBEmailAddress, endTimePlus2Days);
        // Delete deadline.
        expectedStudentDeadlines.remove(studentCEmailAddress);
        assertEquals(expectedStudentDeadlines, response.getStudentDeadlines());
        assertEquals(endTimePlus1Day, logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), studentAEmailAddress, false).getEndTime().toEpochMilli());
        assertEquals(endTimePlus2Days, logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), studentBEmailAddress, false).getEndTime().toEpochMilli());
        assertNull(logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), studentCEmailAddress, false));

        // Verify correct emails sent
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);
        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();

        CourseAttributes course = logic.getCourse(session.getCourseId());
        for (var task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String userEmail = email.getRecipient();
            String expectedSubject = "";
            String oldDeadline = "";
            String newDeadline = "";

            switch (userEmail) {
            case studentAEmailAddress:
                expectedSubject = String.format(EmailType.DEADLINE_EXTENSION_GRANTED.getSubject(),
                        course.getName(), session.getFeedbackSessionName());
                oldDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        updateRequest.getSubmissionEndTime(), session.getTimeZone());
                newDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        Instant.ofEpochMilli(endTimePlus1Day), session.getTimeZone());
                break;
            case studentBEmailAddress:
                expectedSubject = String.format(EmailType.DEADLINE_EXTENSION_UPDATED.getSubject(),
                        course.getName(), session.getFeedbackSessionName());
                oldDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        session.getStudentDeadlines().get(studentBEmailAddress), session.getTimeZone());
                newDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        Instant.ofEpochMilli(endTimePlus2Days), session.getTimeZone());
                break;
            case studentCEmailAddress:
                expectedSubject = String.format(EmailType.DEADLINE_EXTENSION_REVOKED.getSubject(),
                        course.getName(), session.getFeedbackSessionName());
                oldDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        session.getStudentDeadlines().get(studentCEmailAddress), session.getTimeZone());
                newDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        updateRequest.getSubmissionEndTime(), session.getTimeZone());
                break;
            default:
                fail("Email sent to wrong user: " + userEmail);
                break;
            }

            // content[0] contains old deadline, content[1] contains new deadline
            String[] content = email.getContent().split("New Deadline:");
            assertEquals(expectedSubject, email.getSubject());
            assertTrue(content[0].contains(oldDeadline));
            assertTrue(content[1].contains(newDeadline));
        }

        ______TS("change deadline extension for non-existent student; should throw EntityNotFoundException");

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        newStudentDeadlines = convertDeadlinesToLong(updateRequest.getStudentDeadlines());
        newStudentDeadlines.put("nonExistentStudent@gmail.tmt", endTimePlus1Day);
        updateRequest.setStudentDeadlines(newStudentDeadlines);

        verifyEntityNotFound(updateRequest, param);

        verifyNoTasksAdded();

        ______TS("change deadline extension for student to the same time as the end time; "
                + "should throw InvalidHttpRequestBodyException");

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        Instant newEndTime = updateRequest.getSubmissionEndTime();
        newStudentDeadlines = convertDeadlinesToLong(updateRequest.getStudentDeadlines());
        newStudentDeadlines.put(studentAEmailAddress, newEndTime.toEpochMilli());
        updateRequest.setStudentDeadlines(newStudentDeadlines);

        verifyHttpRequestBodyFailure(updateRequest, param);

        verifyNoTasksAdded();

        ______TS("change deadline extension for student to before end time; "
                + "should throw InvalidHttpRequestBodyException");

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        newEndTime = updateRequest.getSubmissionEndTime();
        newStudentDeadlines = convertDeadlinesToLong(updateRequest.getStudentDeadlines());
        newStudentDeadlines.put(studentAEmailAddress, newEndTime.plus(Duration.ofDays(-1)).toEpochMilli());
        updateRequest.setStudentDeadlines(newStudentDeadlines);

        verifyHttpRequestBodyFailure(updateRequest, param);

        verifyNoTasksAdded();

        logoutUser();
    }

    @Test
    public void testExecute_changeDeadlineForInstructors_shouldChangeDeadlinesCorrectlyWhenAppropriate() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        final String instructorAEmailAddress = "helper@course1.tmt";
        Map<String, Long> expectedInstructorDeadlines = convertDeadlinesToLong(session.getInstructorDeadlines());
        Instant endTime = session.getEndTime();
        // These are arbitrary.
        long endTimePlus1Day = endTime.plus(Duration.ofDays(1)).toEpochMilli();
        long endTimePlus2Days = endTime.plus(Duration.ofDays(2)).toEpochMilli();

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        verifyNoTasksAdded();

        ______TS("create new deadline extension for instructor");

        assertNull(expectedInstructorDeadlines.get(instructorAEmailAddress));

        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        Map<String, Long> newInstructorDeadlines = convertDeadlinesToLong(updateRequest.getInstructorDeadlines());
        newInstructorDeadlines.put(instructorAEmailAddress, endTimePlus1Day);
        updateRequest.setInstructorDeadlines(newInstructorDeadlines);

        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        JsonResult r = getJsonResult(a);
        FeedbackSessionData response = (FeedbackSessionData) r.getOutput();

        expectedInstructorDeadlines.put(instructorAEmailAddress, endTimePlus1Day);
        assertEquals(expectedInstructorDeadlines, response.getInstructorDeadlines());
        assertEquals(endTimePlus1Day, logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), instructorAEmailAddress, true).getEndTime().toEpochMilli());

        verifyNoTasksAdded();

        ______TS("update deadline extension for instructor");

        assertNotEquals(endTimePlus2Days, expectedInstructorDeadlines.get(instructorAEmailAddress));

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        newInstructorDeadlines = convertDeadlinesToLong(updateRequest.getInstructorDeadlines());
        newInstructorDeadlines.put(instructorAEmailAddress, endTimePlus2Days);
        updateRequest.setInstructorDeadlines(newInstructorDeadlines);

        a = getAction(updateRequest, param);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        expectedInstructorDeadlines.put(instructorAEmailAddress, endTimePlus2Days);
        assertEquals(expectedInstructorDeadlines, response.getInstructorDeadlines());
        assertEquals(endTimePlus2Days, logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), instructorAEmailAddress, true).getEndTime().toEpochMilli());

        verifyNoTasksAdded();

        ______TS("delete deadline extension for instructor");

        assertNotNull(expectedInstructorDeadlines.get(instructorAEmailAddress));

        // The typical update request does not contain the course 1 helper instructor's email.
        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());

        a = getAction(updateRequest, param);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        // The deadline for course 1 helper instructor was deleted; the map no longer contains a deadline for them.
        expectedInstructorDeadlines.remove(instructorAEmailAddress);
        assertEquals(expectedInstructorDeadlines, response.getInstructorDeadlines());
        assertNull(logic.getDeadlineExtension(
                session.getCourseId(), session.getFeedbackSessionName(), instructorAEmailAddress, true));

        verifyNoTasksAdded();

        ______TS("C_UD on extensions for different instructors within the same request");

        final String instructorBEmailAddress = "instructor1@course1.tmt";
        final String instructorCEmailAddress = "instructor2@course1.tmt";
        param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(true),
        };

        assertNull(expectedInstructorDeadlines.get(instructorAEmailAddress));
        assertNotEquals(endTimePlus2Days, expectedInstructorDeadlines.get(instructorBEmailAddress));
        assertNotNull(expectedInstructorDeadlines.get(instructorCEmailAddress));

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        newInstructorDeadlines = convertDeadlinesToLong(updateRequest.getInstructorDeadlines());
        newInstructorDeadlines.put(instructorAEmailAddress, endTimePlus1Day);
        newInstructorDeadlines.put(instructorBEmailAddress, endTimePlus2Days);
        newInstructorDeadlines.remove(instructorCEmailAddress);
        updateRequest.setInstructorDeadlines(newInstructorDeadlines);

        a = getAction(updateRequest, param);
        r = getJsonResult(a);
        response = (FeedbackSessionData) r.getOutput();

        // Create deadline.
        expectedInstructorDeadlines.put(instructorAEmailAddress, endTimePlus1Day);
        // Update deadline.
        expectedInstructorDeadlines.put(instructorBEmailAddress, endTimePlus2Days);
        // Delete deadline.
        expectedInstructorDeadlines.remove(instructorCEmailAddress);
        assertEquals(expectedInstructorDeadlines, response.getInstructorDeadlines());
        assertEquals(endTimePlus1Day, logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), instructorAEmailAddress, true).getEndTime().toEpochMilli());
        assertEquals(endTimePlus2Days, logic.getDeadlineExtension(session.getCourseId(),
                session.getFeedbackSessionName(), instructorBEmailAddress, true).getEndTime().toEpochMilli());
        assertNull(logic.getDeadlineExtension(
                session.getCourseId(), session.getFeedbackSessionName(), instructorCEmailAddress, true));

        // Verify correct emails sent
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 3);
        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();

        CourseAttributes course = logic.getCourse(session.getCourseId());
        for (var task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            String userEmail = email.getRecipient();
            String expectedSubject = "";
            String oldDeadline = "";
            String newDeadline = "";

            switch (userEmail) {
            case instructorAEmailAddress:
                expectedSubject = String.format(EmailType.DEADLINE_EXTENSION_GRANTED.getSubject(),
                        course.getName(), session.getFeedbackSessionName());
                oldDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        updateRequest.getSubmissionEndTime(), session.getTimeZone());
                newDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        Instant.ofEpochMilli(endTimePlus1Day), session.getTimeZone());
                break;
            case instructorBEmailAddress:
                expectedSubject = String.format(EmailType.DEADLINE_EXTENSION_UPDATED.getSubject(),
                        course.getName(), session.getFeedbackSessionName());
                oldDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        session.getInstructorDeadlines().get(instructorBEmailAddress), session.getTimeZone());
                newDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        Instant.ofEpochMilli(endTimePlus2Days), session.getTimeZone());
                break;
            case instructorCEmailAddress:
                expectedSubject = String.format(EmailType.DEADLINE_EXTENSION_REVOKED.getSubject(),
                        course.getName(), session.getFeedbackSessionName());
                oldDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        session.getInstructorDeadlines().get(instructorCEmailAddress), session.getTimeZone());
                newDeadline = getFormattedInstantForDeadlineExtensionEmail(
                        updateRequest.getSubmissionEndTime(), session.getTimeZone());
                break;
            default:
                fail("Email sent to wrong user: " + userEmail);
                break;
            }

            // content[0] contains old deadline, content[1] contains new deadline
            String[] content = email.getContent().split("New Deadline:");
            assertEquals(expectedSubject, email.getSubject());
            assertTrue(content[0].contains(oldDeadline));
            assertTrue(content[1].contains(newDeadline));
        }

        ______TS("change deadline extension for non-existent instructor; "
                + "should throw EntityNotFoundException");

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        newInstructorDeadlines = convertDeadlinesToLong(updateRequest.getInstructorDeadlines());
        newInstructorDeadlines.put("nonExistentInstructor@gmail.tmt", endTimePlus1Day);
        updateRequest.setInstructorDeadlines(newInstructorDeadlines);

        verifyEntityNotFound(updateRequest, param);

        verifyNoTasksAdded();

        ______TS("change deadline extension for instructor to the same time as the end time; "
                + "should throw InvalidHttpRequestBodyException");

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        Instant newEndTime = updateRequest.getSubmissionEndTime();
        newInstructorDeadlines = convertDeadlinesToLong(updateRequest.getInstructorDeadlines());
        newInstructorDeadlines.put(instructorAEmailAddress, newEndTime.toEpochMilli());
        updateRequest.setInstructorDeadlines(newInstructorDeadlines);

        verifyHttpRequestBodyFailure(updateRequest, param);

        verifyNoTasksAdded();

        ______TS("change deadline extension for instructor to before end time; "
                + "should throw InvalidHttpRequestBodyException");

        updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        newEndTime = updateRequest.getSubmissionEndTime();
        newInstructorDeadlines = convertDeadlinesToLong(updateRequest.getInstructorDeadlines());
        newInstructorDeadlines.put(instructorAEmailAddress, newEndTime.plus(Duration.ofDays(-1)).toEpochMilli());
        updateRequest.setInstructorDeadlines(newInstructorDeadlines);

        verifyHttpRequestBodyFailure(updateRequest, param);

        verifyNoTasksAdded();

        logoutUser();
    }

    @Test
    public void testExecute_startTimeEarlierThanVisibleTime_shouldGiveInvalidParametersError() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        updateRequest.setCustomSessionVisibleTimestamp(
                updateRequest.getSubmissionStartTime().plusSeconds(10).toEpochMilli());

        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(updateRequest, param);
        assertEquals("The start time for this feedback session cannot be "
                + "earlier than the time when the session will be visible.", ihrbe.getMessage());
    }

    @Test
    public void testExecute_differentFeedbackSessionVisibleResponseVisibleSetting_shouldConvertToSpecialTime()
            throws Exception {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("success: Custom time zone, At open show session, 'later' show results");

        logic.updateCourseCascade(
                CourseAttributes.updateOptionsBuilder(course.getId())
                        .withTimezone("Asia/Kathmandu")
                        .build());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest("Asia/Kathmandu");
        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.AT_OPEN);
        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.LATER);

        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        getJsonResult(a);

        session = logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        assertEquals(Const.TIME_REPRESENTS_FOLLOW_OPENING, session.getSessionVisibleFromTime());
        assertEquals(Const.TIME_REPRESENTS_LATER, session.getResultsVisibleFromTime());

        ______TS("success: At open session visible time, custom results visible time, UTC");

        logic.updateCourseCascade(
                CourseAttributes.updateOptionsBuilder(course.getId())
                        .withTimezone("UTC")
                        .build());

        param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };
        updateRequest = getTypicalFeedbackSessionUpdateRequest("UTC");
        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.AT_OPEN);

        a = getAction(updateRequest, param);
        getJsonResult(a);

        session = logic.getFeedbackSession(session.getFeedbackSessionName(), session.getCourseId());
        assertEquals(Const.TIME_REPRESENTS_FOLLOW_OPENING, session.getSessionVisibleFromTime());
        assertEquals(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, "UTC").toEpochMilli(), session.getResultsVisibleFromTime().toEpochMilli());
    }

    @Test
    public void testExecute_masqueradeModeWithManualReleaseResult_shouldEditSessionSuccessfully() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsAdmin();

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };
        param = addUserIdToParams(instructor1ofCourse1.getGoogleId(), param);
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.LATER);

        UpdateFeedbackSessionAction a = getAction(updateRequest, param);
        getJsonResult(a);
    }

    @Test
    public void testExecute_invalidRequestBody_shouldThrowException() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };
        FeedbackSessionUpdateRequest updateRequest = getTypicalFeedbackSessionUpdateRequest(session.getTimeZone());
        updateRequest.setInstructions(null);

        verifyHttpRequestBodyFailure(updateRequest, param);
    }

    private FeedbackSessionUpdateRequest getTypicalFeedbackSessionUpdateRequest(String timeZone) {
        FeedbackSessionUpdateRequest updateRequest = new FeedbackSessionUpdateRequest();
        updateRequest.setInstructions("instructions");

        updateRequest.setSubmissionStartTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, timeZone).toEpochMilli());
        updateRequest.setSubmissionEndTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, timeZone).toEpochMilli());
        updateRequest.setGracePeriod(5);

        updateRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        updateRequest.setCustomSessionVisibleTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                2, timeZone).toEpochMilli());

        updateRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        updateRequest.setCustomResponseVisibleTimestamp(TimeHelperExtension.getTimezoneInstantTruncatedDaysOffsetFromNow(
                7, timeZone).toEpochMilli());

        updateRequest.setClosingEmailEnabled(false);
        updateRequest.setPublishedEmailEnabled(false);

        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions
                .get("session1InCourse1");
        Map<String, Long> studentDeadlines = convertDeadlinesToLong(session1InCourse1.getStudentDeadlines());
        updateRequest.setStudentDeadlines(studentDeadlines);
        Map<String, Long> instructorDeadlines = convertDeadlinesToLong(session1InCourse1.getInstructorDeadlines());
        updateRequest.setInstructorDeadlines(instructorDeadlines);

        return updateRequest;
    }

    private Map<String, Long> convertDeadlinesToLong(Map<String, Instant> deadlines) {
        return deadlines.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toEpochMilli()));
    }

    private String getFormattedInstantForDeadlineExtensionEmail(Instant instant, String timezone) {
        String datetimeDisplayFormat = "EEE, dd MMM yyyy, hh:mm a z";
        Instant midnightAdjustedInstant = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instant, timezone, false);
        return TimeHelper.formatInstant(midnightAdjustedInstant, timezone, datetimeDisplayFormat);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "abcSession",
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyEntityNotFoundAcl(submissionParams);

        ______TS("inaccessible without ModifySessionPrivilege");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.NOTIFY_ABOUT_DEADLINES, String.valueOf(false),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(submissionParams);

        ______TS("only instructors of the same course with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

}
