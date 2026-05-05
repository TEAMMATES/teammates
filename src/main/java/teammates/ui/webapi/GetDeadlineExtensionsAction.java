package teammates.ui.webapi;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.util.Const;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.DeadlineExtensionsData;

/**
 * Gets the deadline extensions for a feedback session.
 */
public class GetDeadlineExtensionsAction extends Action {

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

        gateKeeper.verifyAccessible(
                logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        String courseId = feedbackSession.getCourseId();

        Map<UUID, Student> studentsByUserId = logic.getStudentsForCourse(courseId).stream()
                .collect(Collectors.toMap(Student::getId, s -> s));
        Map<UUID, Instructor> instructorsByUserId = logic.getInstructorsByCourse(courseId).stream()
                .collect(Collectors.toMap(Instructor::getId, i -> i));

        Set<DeadlineExtension> deadlineExtensions = feedbackSession.getDeadlineExtensions();

        DeadlineExtensionsData responseData = new DeadlineExtensionsData(
                deadlineExtensions, studentsByUserId, instructorsByUserId);

        return new JsonResult(responseData);
    }
}
