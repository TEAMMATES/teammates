package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.HasResponsesData;

/**
 * Checks whether a course or question has responses for instructor.
 * Checks whether a student has responded a feedback session.
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
                    sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    sqlLogic.getCourse(courseId));

            return;
        }

        // A student can check whether he has submitted responses for a feedback session in his course.
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (feedbackSessionName != null) {
            gateKeeper.verifyAccessible(
                    sqlLogic.getStudentByGoogleId(courseId, userInfo.getId()),
                    getNonNullFeedbackSession(feedbackSessionName, courseId));
        }

        List<FeedbackSession> feedbackSessions = sqlLogic.getFeedbackSessionsForCourse(courseId);
        if (feedbackSessions.isEmpty()) {
            // Course has no sessions and therefore no response; access to responses is safe for all.
            return;
        }

        // Verify that all sessions are accessible to the user.
        for (FeedbackSession feedbackSession : feedbackSessions) {
            if (!feedbackSession.isVisible()) {
                // Skip invisible sessions.
                continue;
            }

            gateKeeper.verifyAccessible(
                    sqlLogic.getStudentByGoogleId(courseId, userInfo.getId()),
                    feedbackSession);
        }
    }

    @Override
    public JsonResult execute() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            return handleInstructorReq();
        }

        // Default path for student and admin
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (feedbackSessionName == null) {
            // check all sessions in the course
            List<FeedbackSession> feedbackSessions = sqlLogic.getFeedbackSessionsForCourse(courseId);
            Student student = sqlLogic.getStudentByGoogleId(courseId, userInfo.getId());

            Map<String, Boolean> sessionsHasResponses = new HashMap<>();
            for (FeedbackSession feedbackSession : feedbackSessions) {
                if (!feedbackSession.isVisible()) {
                    // Skip invisible sessions.
                    continue;
                }
                boolean hasResponses = sqlLogic.isFeedbackSessionAttemptedByStudent(
                        feedbackSession, student.getEmail(), student.getTeamName());
                sessionsHasResponses.put(feedbackSession.getName(), hasResponses);
            }
            return new JsonResult(new HasResponsesData(sessionsHasResponses));
        }

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        Student student = sqlLogic.getStudentByGoogleId(courseId, userInfo.getId());
        return new JsonResult(new HasResponsesData(
                sqlLogic.isFeedbackSessionAttemptedByStudent(
                        feedbackSession, student.getEmail(), student.getTeamName())));
    }

    private JsonResult handleInstructorReq() {
        String feedbackQuestionID = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        if (feedbackQuestionID != null) {
            FeedbackQuestion sqlFeedbackQuestion = null;

            UUID feedbackQuestionId = null;

            feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionId);

            if (sqlFeedbackQuestion == null) {
                throw new EntityNotFoundException("No feedback question with id: " + feedbackQuestionID);
            }

            boolean hasResponses = sqlLogic.areThereResponsesForQuestion(feedbackQuestionId);
            return new JsonResult(new HasResponsesData(hasResponses));
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (sqlLogic.getCourse(courseId) == null) {
            throw new EntityNotFoundException("No course with id: " + courseId);
        }

        boolean hasResponses = sqlLogic.hasResponsesForCourse(courseId);
        return new JsonResult(new HasResponsesData(hasResponses));
    }

    private void checkInstructorAccessControlUsingQuestion() throws UnauthorizedAccessException {
        FeedbackQuestion feedbackQuestion = null;
        String courseId;

        UUID feedbackQuestionId;

        feedbackQuestionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        feedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionId);

        if (feedbackQuestion != null) {
            courseId = feedbackQuestion.getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        FeedbackSession feedbackSession = feedbackQuestion.getFeedbackSession();
        gateKeeper.verifyAccessible(
                sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                feedbackSession);
    }
}
