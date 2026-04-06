package teammates.ui.webapi;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.util.Const;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackSessionDeadlineExtensionsData;

/**
 * Gets the deadline extensions for a feedback session.
 */
public class GetFeedbackSessionDeadlineExtensionsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        Map<UUID, Student> studentsByUserId = sqlLogic.getStudentsForCourse(courseId).stream()
                .collect(Collectors.toMap(Student::getId, s -> s));
        Map<UUID, Instructor> instructorsByUserId = sqlLogic.getInstructorsByCourse(courseId).stream()
                .collect(Collectors.toMap(Instructor::getId, i -> i));

        List<DeadlineExtension> deadlineExtensions = feedbackSession.getDeadlineExtensions();
        String timeZone = feedbackSession.getCourse().getTimeZone();

        FeedbackSessionDeadlineExtensionsData responseData = new FeedbackSessionDeadlineExtensionsData(
                timeZone, deadlineExtensions, studentsByUserId, instructorsByUserId);

        return new JsonResult(responseData);
    }
}
