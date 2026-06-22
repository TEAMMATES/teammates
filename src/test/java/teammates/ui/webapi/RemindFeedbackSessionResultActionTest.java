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
 * Tests for {@link RemindFeedbackSessionResultAction}.
 */
public class RemindFeedbackSessionResultActionTest
        extends BaseActionTest<RemindFeedbackSessionResultAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void remindFeedbackSessionResultAction_validRequest_queuesPriorityPublishedEmails() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var student = given.student("student", s -> s.defaultCourse());
        var targetInstructor = given.instructor("target-instructor", i -> i.defaultCourse().noAccount());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator().published());
        persistGivenData(given);

        FeedbackSessionRespondentRemindRequest requestBody = new FeedbackSessionRespondentRemindRequest();
        requestBody.setUsersToRemind(new java.util.UUID[] { student.id(), targetInstructor.id() });

        MessageOutput result = execute(new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(instructorAccount.id())
                .withRequest(requestBody));

        assertEquals("Reminders sent", result.getMessage());
        List<EmailWrapper> queuedEmails = getQueuedEmails();
        assertEquals(3, queuedEmails.size());
        assertEquals(EmailType.FEEDBACK_PUBLISHED, queuedEmails.get(0).getType());
        assertEquals(EmailType.FEEDBACK_PUBLISHED, queuedEmails.get(1).getType());
        assertEquals(EmailType.FEEDBACK_PUBLISHED, queuedEmails.get(2).getType());
        assertTrue(queuedEmails.get(2).getIsCopy());
    }

    @Test(groups = GroupNames.ACTION)
    public void remindFeedbackSessionResultAction_sessionNotPublished_throwsInvalidOperationException() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var student = given.student("student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator().closed());
        persistGivenData(given);

        FeedbackSessionRespondentRemindRequest requestBody = new FeedbackSessionRespondentRemindRequest();
        requestBody.setUsersToRemind(new java.util.UUID[] { student.id() });

        InvalidOperationException exception = assertActionThrows(
                InvalidOperationException.class,
                new RequestContext()
                        .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                        .withAccountAuth(instructorAccount.id())
                        .withRequest(requestBody));

        assertEquals("Published email could not be resent as the feedback session is not published.",
                exception.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void remindFeedbackSessionResultAction_userFromDifferentCourse_throwsInvalidHttpRequestBodyException() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var otherCourse = given.course("other-course");
        var student = given.student("student", s -> s.course(otherCourse.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator().published());
        persistGivenData(given);

        FeedbackSessionRespondentRemindRequest requestBody = new FeedbackSessionRespondentRemindRequest();
        requestBody.setUsersToRemind(new java.util.UUID[] { student.id() });

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
    public void remindFeedbackSessionResultAction_missingUser_throwsEntityNotFoundException() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator().published());
        persistGivenData(given);

        FeedbackSessionRespondentRemindRequest requestBody = new FeedbackSessionRespondentRemindRequest();
        requestBody.setUsersToRemind(new java.util.UUID[] { java.util.UUID.randomUUID() });

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
