package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;
import teammates.ui.output.ConfirmationResponse;
import teammates.ui.output.ConfirmationResult;
import teammates.ui.request.Intent;

/**
 * Confirm the submission of a feedback session.
 */
class ConfirmFeedbackSessionSubmissionAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        verifySessionOpenExceptForModeration(feedbackSession);
        verifyNotPreview();

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackSession.getCourseId());
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackSession.getCourseId());
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        boolean isSubmissionEmailConfirmationEmailRequested =
                getBooleanRequestParamValue(Const.ParamsNames.SEND_SUBMISSION_EMAIL);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        EmailWrapper email = null;
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(feedbackSession.getCourseId());
            boolean hasStudentRespondedForSession =
                    logic.hasGiverRespondedForSession(studentAttributes.getEmail(), feedbackSessionName, courseId);
            if (hasStudentRespondedForSession) {
                taskQueuer.scheduleUpdateRespondentForSession(
                        courseId, feedbackSessionName, studentAttributes.getEmail(), false, false);
            } else {
                taskQueuer.scheduleUpdateRespondentForSession(
                        courseId, feedbackSessionName, studentAttributes.getEmail(), false, true);
            }
            if (isSubmissionEmailConfirmationEmailRequested) {
                email = new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForStudent(
                            feedbackSession, studentAttributes, Instant.now());
            }
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(feedbackSession.getCourseId());
            boolean hasInstructorRespondedForSession =
                    logic.hasGiverRespondedForSession(instructorAttributes.getEmail(), feedbackSessionName, courseId);
            if (hasInstructorRespondedForSession) {
                taskQueuer.scheduleUpdateRespondentForSession(
                        courseId, feedbackSessionName, instructorAttributes.getEmail(), true, false);
            } else {
                taskQueuer.scheduleUpdateRespondentForSession(
                        courseId, feedbackSessionName, instructorAttributes.getEmail(), true, true);
            }
            if (isSubmissionEmailConfirmationEmailRequested) {
                email = new EmailGenerator().generateFeedbackSubmissionConfirmationEmailForInstructor(
                        feedbackSession, instructorAttributes, Instant.now());
            }
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        if (email != null) {
            EmailSendingStatus status = emailSender.sendEmail(email);
            if (!status.isSuccess()) {
                return new JsonResult(new ConfirmationResponse(ConfirmationResult.SUCCESS_BUT_EMAIL_FAIL_TO_SEND,
                        "Submission confirmation email failed to send"));
            }
        }

        return new JsonResult(new ConfirmationResponse(ConfirmationResult.SUCCESS, "Submission confirmed"));
    }
}
