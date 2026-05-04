package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Student;
import teammates.ui.output.HasResponsesData;

/**
 * Checks whether a course or question has responses.
 */
public class GetHasResponsesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {

        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(Const.EntityType.STUDENT.equals(entityType) || Const.EntityType.INSTRUCTOR.equals(entityType))) {
            throw new UnauthorizedAccessException("entity type not supported.");
        }

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            //An instructor of the feedback session can check responses for questions within it.
            String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            if (questionId != null) {
                checkInstructorAccessControlUsingQuestion();
                //prefer question check over course checks
                return;
            }

            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

            gateKeeper.verifyAccessible(
                    logic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    logic.getCourse(courseId));

            return;
        }

        // A student can check whether he has submitted responses for a feedback session in his course.
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        List<FeedbackSession> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);

        // Verify that all sessions are accessible to the user.
        for (FeedbackSession feedbackSession : feedbackSessions) {
            if (!feedbackSession.isVisible()) {
                // Skip invisible sessions.
                continue;
            }

            gateKeeper.verifyAccessible(
                    logic.getStudentByGoogleId(courseId, userInfo.getId()),
                    feedbackSession);
        }
    }

    @Override
    public JsonResult execute() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            return handleInstructorReq();
        }

        return handleStudentReq();
    }

    private JsonResult handleStudentReq() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        List<FeedbackSession> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        Student student = logic.getStudentByGoogleId(courseId, userInfo.getId());

        Map<String, Boolean> sessionsHasResponses = new HashMap<>();
        for (FeedbackSession feedbackSession : feedbackSessions) {
            if (!feedbackSession.isVisible()) {
                // Skip invisible sessions.
                continue;
            }

            boolean hasResponses = logic.isFeedbackSessionAttemptedByStudent(
                    feedbackSession, student.getEmail(), student.getTeamName());
            sessionsHasResponses.put(feedbackSession.getName(), hasResponses);
        }
        return new JsonResult(new HasResponsesData(sessionsHasResponses));
    }

    private JsonResult handleInstructorReq() {
        UUID feedbackQuestionId = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        if (feedbackQuestionId != null) {
            FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
            if (feedbackQuestion == null) {
                throw new EntityNotFoundException("No feedback question with id: " + feedbackQuestionId);
            }

            boolean hasResponses = logic.areThereResponsesForQuestion(feedbackQuestionId);
            return new JsonResult(new HasResponsesData(hasResponses));
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (logic.getCourse(courseId) == null) {
            throw new EntityNotFoundException("No course with id: " + courseId);
        }

        boolean hasResponses = logic.hasResponsesForCourse(courseId);
        return new JsonResult(new HasResponsesData(hasResponses));
    }

    private void checkInstructorAccessControlUsingQuestion() throws UnauthorizedAccessException {
        UUID feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        if (feedbackQuestion == null) {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        String courseId = feedbackQuestion.getCourseId();
        FeedbackSession feedbackSession = feedbackQuestion.getFeedbackSession();
        gateKeeper.verifyAccessible(
                logic.getInstructorByGoogleId(courseId, userInfo.getId()),
                feedbackSession);
    }
}
