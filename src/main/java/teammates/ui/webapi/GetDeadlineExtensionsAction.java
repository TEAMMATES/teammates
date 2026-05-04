package teammates.ui.webapi;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.util.Const;
import teammates.logic.entity.DeadlineExtension;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
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

        List<DeadlineExtension> deadlineExtensions = feedbackSession.getDeadlineExtensions();

        DeadlineExtensionsData responseData = new DeadlineExtensionsData(
                deadlineExtensions, studentsByUserId, instructorsByUserId);

        return new JsonResult(responseData);
    }
}
