package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
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
    void checkSpecificAccessControl() {

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
            if (courseId != null) {
                gateKeeper.verifyAccessible(
                        logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                        logic.getCourse(courseId));
            }
        } else {
            //An student can check whether he has submitted responses for a feedback session in his course.
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

            gateKeeper.verifyAccessible(
                    logic.getStudentForGoogleId(courseId, userInfo.getId()),
                    getNonNullFeedbackSession(feedbackSessionName, courseId));
        }
    }

    @Override
    JsonResult execute() {
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
            String feedbackQuestionID = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            if (feedbackQuestionID != null) {
                if (logic.getFeedbackQuestion(feedbackQuestionID) == null) {
                    return new JsonResult("No feedback question with id: " + feedbackQuestionID, HttpStatus.SC_NOT_FOUND);
                }

                boolean hasResponses = logic.areThereResponsesForQuestion(feedbackQuestionID);
                return new JsonResult(new HasResponsesData(hasResponses));
            }

            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            if (logic.getCourse(courseId) == null) {
                return new JsonResult("No course with id: " + courseId, HttpStatus.SC_NOT_FOUND);
            }

            boolean hasResponses = logic.hasResponsesForCourse(courseId);
            return new JsonResult(new HasResponsesData(hasResponses));
        } else {
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
            if (feedbackSession == null) {
                return new JsonResult("No feedback session found with name: " + feedbackSessionName,
                        HttpStatus.SC_NOT_FOUND);
            }

            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());

            boolean hasResponses = logic.hasStudentSubmittedFeedback(feedbackSession, student.email);
            return new JsonResult(new HasResponsesData(hasResponses));
        }
    }
}
