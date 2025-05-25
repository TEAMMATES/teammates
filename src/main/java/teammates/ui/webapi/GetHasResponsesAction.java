package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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
                checkInstructorAccessControlUsingQuestion(questionId);
                //prefer question check over course checks
                return;
            }

            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            if (!isCourseMigrated(courseId)) {
                gateKeeper.verifyAccessible(
                        logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                        logic.getCourse(courseId));
                return;
            }

            gateKeeper.verifyAccessible(
                    sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    sqlLogic.getCourse(courseId));

            return;
        }

        // A student can check whether he has submitted responses for a feedback session in his course.
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (!isCourseMigrated(courseId)) {
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
            return;
        }

        if (feedbackSessionName != null) {
            gateKeeper.verifyAccessible(
                    sqlLogic.getStudentByGoogleId(courseId, userInfo.getId()),
                    getNonNullSqlFeedbackSession(feedbackSessionName, courseId));
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
        if (!isCourseMigrated(courseId)) {
            return handleOldStudentHasReponses(feedbackSessionName, courseId);
        }

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

        FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);

        Student student = sqlLogic.getStudentByGoogleId(courseId, userInfo.getId());
        return new JsonResult(new HasResponsesData(
                sqlLogic.isFeedbackSessionAttemptedByStudent(
                        feedbackSession, student.getEmail(), student.getTeamName())));
    }

    private JsonResult handleInstructorReq() {
        String feedbackQuestionID = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        if (feedbackQuestionID != null) {
            FeedbackQuestionAttributes questionAttributes = null;
            FeedbackQuestion sqlFeedbackQuestion = null;
            String courseId;

            UUID feedbackQuestionSqlId = null;

            try {
                feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
                sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
            } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
                // if the question id cannot be converted to UUID, we check the datastore for the question
                questionAttributes = logic.getFeedbackQuestion(feedbackQuestionID);
            }

            if (questionAttributes != null) {
                courseId = questionAttributes.getCourseId();
            } else if (sqlFeedbackQuestion != null) {
                courseId = sqlFeedbackQuestion.getCourseId();
            } else {
                throw new EntityNotFoundException("No feedback question with id: " + feedbackQuestionID);
            }

            if (!isCourseMigrated(courseId)) {
                boolean hasResponses = logic.areThereResponsesForQuestion(feedbackQuestionID);
                return new JsonResult(new HasResponsesData(hasResponses));
            }

            boolean hasResponses = sqlLogic.areThereResponsesForQuestion(feedbackQuestionSqlId);
            return new JsonResult(new HasResponsesData(hasResponses));
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            if (logic.getCourse(courseId) == null) {
                throw new EntityNotFoundException("No course with id: " + courseId);
            }

            boolean hasResponses = logic.hasResponsesForCourse(courseId);
            return new JsonResult(new HasResponsesData(hasResponses));
        }

        if (sqlLogic.getCourse(courseId) == null) {
            throw new EntityNotFoundException("No course with id: " + courseId);
        }

        boolean hasResponses = sqlLogic.hasResponsesForCourse(courseId);
        return new JsonResult(new HasResponsesData(hasResponses));
    }

    private void checkInstructorAccessControlUsingQuestion(String questionId) throws UnauthorizedAccessException {
        FeedbackQuestionAttributes feedbackQuestionAttributes = null;
        FeedbackQuestion sqlFeedbackQuestion = null;
        String courseId;

        UUID feedbackQuestionSqlId;

        try {
            feedbackQuestionSqlId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            sqlFeedbackQuestion = sqlLogic.getFeedbackQuestion(feedbackQuestionSqlId);
        } catch (InvalidHttpParameterException verifyHttpParameterFailure) {
            // if the question id cannot be converted to UUID, we check the datastore for the question
            feedbackQuestionAttributes = logic.getFeedbackQuestion(questionId);
        }

        if (feedbackQuestionAttributes != null) {
            courseId = feedbackQuestionAttributes.getCourseId();
        } else if (sqlFeedbackQuestion != null) {
            courseId = sqlFeedbackQuestion.getCourseId();
        } else {
            throw new EntityNotFoundException("Feedback Question not found");
        }

        if (!isCourseMigrated(courseId)) {
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(
                    feedbackQuestionAttributes.getFeedbackSessionName(),
                    feedbackQuestionAttributes.getCourseId());

            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(feedbackQuestionAttributes.getCourseId(), userInfo.getId()),
                    feedbackSession);
            return;
        }

        FeedbackSession feedbackSession = sqlFeedbackQuestion.getFeedbackSession();
        gateKeeper.verifyAccessible(
                sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                feedbackSession);
    }

    private JsonResult handleOldStudentHasReponses(String feedbackSessionName, String courseId) {
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
}
