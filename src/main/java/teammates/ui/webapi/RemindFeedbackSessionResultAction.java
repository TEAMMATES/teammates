package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Remind the student about the published result of a feedback session.
 */
public class RemindFeedbackSessionResultAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        Instructor instructor = logic.getInstructorByGoogleId(feedbackSession.getCourseId(), getCurrentUserGoogleId());
        gateKeeper.verifyAccessible(instructor, feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession;
        try {
            feedbackSession = logic.getFeedbackSessionForResultsReminder(feedbackSessionId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidOperationException(e);
        }

        FeedbackSessionRespondentRemindRequest remindRequest =
                getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
        UUID[] usersToRemind = remindRequest.getUsersToRemind();

        List<User> users;
        try {
            users = logic.getValidatedUsersForCourse(feedbackSession.getCourseId(), usersToRemind);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidOperationException(e);
        }

        // Generate reminder emails for specified users
        List<Student> studentsToRemindList = new ArrayList<>();
        List<Instructor> instructorsToRemindList = new ArrayList<>();
        Instructor instructorToNotify =
                logic.getInstructorByGoogleId(feedbackSession.getCourseId(), getCurrentUserGoogleId());

        for (User user : users) {
            if (user instanceof Student student) {
                studentsToRemindList.add(student);
            } else if (user instanceof Instructor instructor) {
                instructorsToRemindList.add(instructor);
            }
        }

        List<EmailWrapper> emails = emailGenerator.generateFeedbackSessionPublishedEmails(
                feedbackSession, studentsToRemindList, instructorsToRemindList,
                Collections.singletonList(instructorToNotify));

        taskQueuer.scheduleEmailsForPrioritySending(emails);

        return new JsonResult("Reminders sent");
    }
}
