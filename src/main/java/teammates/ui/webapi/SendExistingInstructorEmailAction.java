package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.Const;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Send emails to instructors whose GoogleID
 * is tied with an existing account.
 */
class SendExistingInstructorEmailAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Only admins can send this email to instructors
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String instructorEmail = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        taskQueuer.scheduleEmailsForSending(List.of(emailGenerator
                .generateExistingInstructorAccountEmail(instructorEmail)));

        return new JsonResult("An email has been sent to " + instructorEmail);
    }
}
