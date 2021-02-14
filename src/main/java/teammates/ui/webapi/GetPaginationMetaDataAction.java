package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.PaginationMetaData;

/**
 * Get pagination metadata.
 */
public class GetPaginationMetaDataAction extends Action {

    private static final String MESSAGE_NOT_INSTRUCTOR_ACCOUNT = "You did not login as an instructor,"
            + " so you cannot view your profile";
    private static final String MESSAGE_INSTRUCTOR_NOT_FOUND = "The instructor is not in the course you are given,"
            + " so you cannot access the profile.";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (userInfo.isAdmin) {
            return;
        }
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException(MESSAGE_NOT_INSTRUCTOR_ACCOUNT);
        }

        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForEmail(courseId, instructorEmail);
        if (instructor == null) {
            throw new UnauthorizedAccessException(MESSAGE_INSTRUCTOR_NOT_FOUND);
        }
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
    }

    @Override
    ActionResult execute() {
        String feedbackQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        int submittedResponseCount = logic.getSubmittedResponseCountForQuestion(feedbackQuestionId);
        PaginationMetaData data = new PaginationMetaData(submittedResponseCount, feedbackQuestionId);

        return new JsonResult(data);
    }
}
