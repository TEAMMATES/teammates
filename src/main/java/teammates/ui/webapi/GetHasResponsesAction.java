package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.output.HasResponsesData;

/**
 * Checks whether a course or question has responses for instructor.
 * Checks whether a student has responded a feedback session.
 */
class GetHasResponsesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {

        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(entityType.equals(Const.EntityType.STUDENT) || entityType.equals(Const.EntityType.INSTRUCTOR))) {
            throw new UnauthorizedAccessException("entity type not supported.");
        }

        if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
            //An instructor of the feedback session can check responses for questions within it.
            String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            if (questionId != null) {
                FeedbackQuestionAttributes feedbackQuestionAttributes = logic.getFeedbackQuestion(questionId);
                FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(
                        feedbackQuestionAttributes.getFeedbackSessionName(),
                        feedbackQuestionAttributes.getCourseId());

                gateKeeper.verifyAccessible(
                        logic.getInstructorForGoogleId(feedbackQuestionAttributes.getCourseId(), userInfo.getId()),
                        feedbackSession);

                //prefer question check over course checks
                return;
            }

            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    logic.getCourse(courseId));
            return;
        }

        //An student can check whether he has submitted responses for a feedback session in his course.
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (feedbackSessionName != null) {
            gateKeeper.verifyAccessible(
                    logic.getStudentForGoogleId(courseId, userInfo.getId()),
                    getNonNullFeedbackSession(feedbackSessionName, courseId));
        }

        List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
        if (feedbackSessions.isEmpty()) {
            // Course has no sessions and therefore no response; access to responses is safe for all.
            return;
        }

        // Verify that all sessions are accessible to the user.
        for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            if (!feedbackSession.isVisible()) {
                // Skip invisible sessions.
                continue;
            }

            gateKeeper.verifyAccessible(
                    logic.getStudentForGoogleId(courseId, userInfo.getId()),
                    feedbackSession);
        }
    }

    @Override
    public JsonResult execute() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
            return handleInstructorReq();
        }

        // Default path for student and admin
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (feedbackSessionName == null) {
            // check all sessions in the course
            List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);
            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());

            Map<String, Boolean> sessionsHasResponses = new HashMap<>();
            for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
                if (!feedbackSession.isVisible()) {
                    // Skip invisible sessions.
                    continue;
                }
                boolean hasResponses = logic.isFeedbackSessionAttemptedByStudent(
                        feedbackSession, student.getEmail(), student.getTeam());
                sessionsHasResponses.put(feedbackSession.getFeedbackSessionName(), hasResponses);
            }
            return new JsonResult(new HasResponsesData(sessionsHasResponses));
        }

        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());
        return new JsonResult(new HasResponsesData(
                logic.isFeedbackSessionAttemptedByStudent(feedbackSession, student.getEmail(), student.getTeam())));
    }

    private JsonResult handleInstructorReq() {
        String feedbackQuestionID = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        if (feedbackQuestionID != null) {
            if (logic.getFeedbackQuestion(feedbackQuestionID) == null) {
                throw new EntityNotFoundException("No feedback question with id: " + feedbackQuestionID);
            }

            boolean hasResponses = logic.areThereResponsesForQuestion(feedbackQuestionID);
            return new JsonResult(new HasResponsesData(hasResponses));
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (logic.getCourse(courseId) == null) {
            throw new EntityNotFoundException("No course with id: " + courseId);
        }

        boolean hasResponses = logic.hasResponsesForCourse(courseId);
        return new JsonResult(new HasResponsesData(hasResponses));
    }
}
