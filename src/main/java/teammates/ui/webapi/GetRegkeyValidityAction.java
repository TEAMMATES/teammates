package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.RegkeyValidityData;
import teammates.ui.request.Intent;

/**
 * Action: checks whether the provided registration key is valid for the logged in user.
 *
 * <p>This does not log in or log out the user.
 */
public class GetRegkeyValidityAction extends Action {

    @Override
    public AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // Regkey information is available to everyone
    }

    @Override
    public JsonResult execute() {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        String regKey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);

        boolean isValid = false;
        String googleId = null;

        if (intent == Intent.STUDENT_SUBMISSION || intent == Intent.STUDENT_RESULT) {
            // Try to get googleId for not migrated user
            StudentAttributes studentAttributes = logic.getStudentForRegistrationKey(regKey);
            if (studentAttributes != null && !isCourseMigrated(studentAttributes.getCourse())) {
                isValid = true;
                googleId = studentAttributes.getGoogleId();
            }

            // Try to get googleId for migrated user
            Student student = sqlLogic.getStudentByRegistrationKey(regKey);
            if (student != null) { // assume that if student has been migrated, course has been migrated
                isValid = true;
                googleId = student.getGoogleId();
            }
        } else if (intent == Intent.INSTRUCTOR_SUBMISSION || intent == Intent.INSTRUCTOR_RESULT) {
            // Try to get googleId for not migrated user
            InstructorAttributes instructorAttributes = logic.getInstructorForRegistrationKey(regKey);
            if (instructorAttributes != null && !isCourseMigrated(instructorAttributes.getCourseId())) {
                isValid = true;
                googleId = instructorAttributes.getGoogleId();
            }

            // Try to get googleId for migrated user
            Instructor instructor = sqlLogic.getInstructorByRegistrationKey(regKey);
            if (instructor != null) { // assume that if instructor has been migrated, course has been migrated
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
                isAllowedAccess = userInfo != null && googleId.equals(userInfo.id);
            }
        }

        return new JsonResult(new RegkeyValidityData(isValid, isUsed, isAllowedAccess));
    }

}
