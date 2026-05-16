package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.RegkeyValidityData;
import teammates.ui.request.Intent;

/**
 * Action: checks whether the provided registration key is valid for the logged in user.
 *
 * <p>This does not log in or log out the user.
 */
public class GetRegkeyValidityAction extends PublicAction {

    @Override
    public JsonResult execute() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        String regKey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);

        boolean isValid = false;
        String googleId = null;

        if (intent == Intent.STUDENT_SUBMISSION || intent == Intent.STUDENT_RESULT) {
            Student student = logic.getStudentByRegistrationKey(regKey);
            if (student != null) {
                isValid = true;
                googleId = student.getGoogleId();
            }
        } else if (intent == Intent.INSTRUCTOR_SUBMISSION || intent == Intent.INSTRUCTOR_RESULT) {
            Instructor instructor = logic.getInstructorByRegistrationKey(regKey);
            if (instructor != null) {
                isValid = true;
                googleId = instructor.getGoogleId();
            }
        }

        boolean isUsed = false;
        boolean isAllowedAccess = false;

        if (isValid) {
            if (StringHelper.isEmpty(googleId)) {
                // If registration key has not been used, always allow access
                isAllowedAccess = true;
            } else {
                isUsed = true;
                // If the registration key has been used to register, the logged in user needs to match
                // Block access to not logged in user and mismatched user
                isAllowedAccess = authContext != null && googleId.equals(authContext.id());
            }
        }

        return new JsonResult(new RegkeyValidityData(isValid, isUsed, isAllowedAccess));
    }

}
