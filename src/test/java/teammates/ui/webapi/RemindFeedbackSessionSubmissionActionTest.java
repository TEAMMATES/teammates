package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;
import teammates.ui.request.SendEmailRequest;

/**
 * Tests for {@link RemindFeedbackSessionSubmissionAction}.
 */
public class RemindFeedbackSessionSubmissionActionTest
        extends BaseActionTest<RemindFeedbackSessionSubmissionAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void remindFeedbackSessionSubmissionAction_validRequest_queuesPriorityReminderEmails() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var student = given.student("student", s -> s.defaultCourse());
        var targetInstructor = given.instructor("target-instructor", i -> i.defaultCourse().noAccount());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator().opened());
        persistGivenData(given);

        FeedbackSessionRespondentRemindRequest requestBody = new FeedbackSessionRespondentRemindRequest();
        requestBody.setUsersToRemind(new java.util.UUID[] { student.id(), targetInstructor.id() });
        requestBody.setIsSendingCopyToInstructor(true);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(instructorAccount.id())
                .withRequest(requestBody);

        MessageOutput result = execute(request);

        assertEquals("Reminders sent", result.getMessage());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(3, queuedEmails.size());
        assertEquals(EmailType.FEEDBACK_SESSION_REMINDER, queuedEmails.get(0).getType());
        assertEquals(EmailType.FEEDBACK_SESSION_REMINDER, queuedEmails.get(1).getType());
        assertEquals(EmailType.FEEDBACK_SESSION_REMINDER, queuedEmails.get(2).getType());
        assertTrue(queuedEmails.get(2).getIsCopy());
    }

    @Test(groups = GroupNames.ACTION)
    public void remindFeedbackSessionSubmissionAction_sessionNotOpen_throwsInvalidOperationException() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var student = given.student("student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator().waitingToOpen());
        persistGivenData(given);

        FeedbackSessionRespondentRemindRequest requestBody = new FeedbackSessionRespondentRemindRequest();
        requestBody.setUsersToRemind(new java.util.UUID[] { student.id() });
        requestBody.setIsSendingCopyToInstructor(false);

        InvalidOperationException exception = assertActionThrows(
                InvalidOperationException.class,
                new RequestContext()
                        .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                        .withAccountAuth(instructorAccount.id())
                        .withRequest(requestBody));

        assertEquals("Reminder email could not be sent out as the feedback session is not open for submissions.",
                exception.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void remindFeedbackSessionSubmissionAction_userFromDifferentCourse_throwsInvalidOperationException() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var otherCourse = given.course("other-course");
        var student = given.student("student", s -> s.course(otherCourse.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator().opened());
        persistGivenData(given);

        FeedbackSessionRespondentRemindRequest requestBody = new FeedbackSessionRespondentRemindRequest();
        requestBody.setUsersToRemind(new java.util.UUID[] { student.id() });
        requestBody.setIsSendingCopyToInstructor(false);

        InvalidHttpRequestBodyException exception = assertActionThrows(
                InvalidHttpRequestBodyException.class,
                new RequestContext()
                        .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                        .withAccountAuth(instructorAccount.id())
                        .withRequest(requestBody));

        assertEquals("User with ID " + student.id() + " does not belong to the same course as the feedback session",
                exception.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void remindFeedbackSessionSubmissionAction_missingUser_throwsEntityNotFoundException() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator().opened());
        persistGivenData(given);

        FeedbackSessionRespondentRemindRequest requestBody = new FeedbackSessionRespondentRemindRequest();
        requestBody.setUsersToRemind(new java.util.UUID[] { java.util.UUID.randomUUID() });
        requestBody.setIsSendingCopyToInstructor(false);

        EntityNotFoundException exception = assertActionThrows(
                EntityNotFoundException.class,
                new RequestContext()
                        .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                        .withAccountAuth(instructorAccount.id())
                        .withRequest(requestBody));

        assertEquals("User with ID " + requestBody.getUsersToRemind()[0] + " not found", exception.getMessage());
    }

    private List<EmailWrapper> getQueuedEmails() {
        return mockTaskQueuer.getTasksAdded().stream()
                .map(TaskWrapper::getRequestBody)
                .map(SendEmailRequest.class::cast)
                .map(SendEmailRequest::getEmail)
                .toList();
    }
}
