package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.pagedata.FeedbackResponseCommentAjaxPageData;

public class FeedbackParticipantFeedbackResponseCommentDeleteAction extends Action {

    protected String courseId;
    protected String feedbackSessionName;
    protected boolean isModeration;

    @Override
    protected ActionResult execute() {
        courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        String feedbackResponseId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseId);
        String feedbackResponseCommentId = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseCommentId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        isModeration = false;
        if (getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON) != null) {
            isModeration = true;
        }

        Long commentId = Long.parseLong(feedbackResponseCommentId);

        verifyAccessibleForUserToFeedbackResponseComment(session, response, commentId);

        logic.deleteDocumentByCommentId(commentId);
        logic.deleteFeedbackResponseCommentById(commentId);

        appendToStatusToAdmin(commentId);

        FeedbackResponseCommentAjaxPageData data =
                new FeedbackResponseCommentAjaxPageData(account, sessionToken);

        return createAjaxResult(data);
    }

    private void verifyAccessibleForUserToFeedbackResponseComment(FeedbackSessionAttributes session,
            FeedbackResponseAttributes response, Long commentId) {
        if (isModeration) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            gateKeeper.verifyAccessible(instructor, session, false, response.giverSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, false, response.recipientSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        } else {
            FeedbackResponseCommentAttributes frc =  logic.getFeedbackResponseComment(commentId);
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            StudentAttributes student = logic.getStudentForGoogleId(courseId, account.googleId);
            switch (frc.commentGiverType) {
            case INSTRUCTORS:
                gateKeeper.verifyAccessible(instructor, session, false,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
                gateKeeper.verifyAccessible(frc, instructor.email);
                break;
            case STUDENTS:
                gateKeeper.verifyAccessible(student, session);
                gateKeeper.verifyAccessible(frc, student.email);
                break;
            case TEAMS:
                gateKeeper.verifyAccessible(student, session);
                gateKeeper.verifyAccessible(frc, student.team);
                break;
            default:
                Assumption.fail("Invalid comment giver type");
                break;
            }
        }
    }

    private void appendToStatusToAdmin(Long commentId) {
        statusToAdmin += "FeedbackParticipantFeedbackResponseCommentDeleteAction:<br>"
                + "Deleting feedback response comment: " + commentId + "<br>"
                + "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";
    }
}
