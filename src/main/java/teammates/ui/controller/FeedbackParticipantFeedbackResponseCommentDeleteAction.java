package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
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

        FeedbackResponseCommentAjaxPageData data =
                new FeedbackResponseCommentAjaxPageData(account, sessionToken);

        if (!isModeration && !(session.isOpened() || session.isInGracePeriod())) {
            data.isError = true;
            data.errorMessage = "Session is not open for submission.";
            return createAjaxResult(data);
        }

        Long commentId = Long.parseLong(feedbackResponseCommentId);

        verifyDeletePermissionForUserToFeedbackResponseComment(session, response, commentId);

        logic.deleteDocumentByCommentId(commentId);
        logic.deleteFeedbackResponseCommentById(commentId);

        appendStatusToAdmin(commentId);

        return createAjaxResult(data);
    }

    private void verifyDeletePermissionForUserToFeedbackResponseComment(FeedbackSessionAttributes session,
            FeedbackResponseAttributes response, Long commentId) {
        if (isModeration) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            gateKeeper.verifyAccessible(instructor, session, false, response.giverSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, false, response.recipientSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            return;
        }
        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(commentId);
        if (frc == null) {
            return;
        }

        if (!frc.isCommentFromFeedbackParticipant) {
            throw new UnauthorizedAccessException("Comment [" + frc.getId() + "] not given by feedback participant.");
        }

        switch (frc.commentGiverType) {
        case INSTRUCTORS:
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            if (instructor == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
            }
            gateKeeper.verifyOwnership(frc, instructor.email);
            break;
        case STUDENTS:
            StudentAttributes student = getStudent();
            if (student == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
            }
            gateKeeper.verifyOwnership(frc, student.email);
            break;
        case TEAMS:
            StudentAttributes studentOfTeam = getStudent();
            if (studentOfTeam == null) {
                throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
            }
            gateKeeper.verifyOwnership(frc, studentOfTeam.team);
            break;
        default:
            Assumption.fail("Invalid comment giver type");
            break;
        }
    }

    private void appendStatusToAdmin(Long commentId) {
        statusToAdmin += "FeedbackParticipantFeedbackResponseCommentDeleteAction:<br>"
                + "Deleting feedback response comment: " + commentId + "<br>"
                + "in course/feedback session: " + courseId + "/" + feedbackSessionName + "<br>";
    }

    private StudentAttributes getStudent() {
        if (student == null) {
            return logic.getStudentForGoogleId(courseId, account.googleId);
        }
        return student;
    }
}
