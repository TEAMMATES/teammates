package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.JoinStatus;

/**
 * Get the join status of a course.
 */
public class GetCourseJoinStatusAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        // Any user can use a join link as long as its parameters are valid
    }

    @Override
    public JsonResult execute() {
        String regkey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        String isCreatingAccount = getRequestParamValue(Const.ParamsNames.IS_CREATING_ACCOUNT);

        switch (entityType) {
        case Const.EntityType.STUDENT:
            return getStudentJoinStatus(regkey);
        case Const.EntityType.INSTRUCTOR:
            return getInstructorJoinStatus(regkey, "true".equals(isCreatingAccount));
        default:
            throw new InvalidHttpParameterException("Error: invalid entity type");
        }
    }

    private JsonResult getStudentJoinStatus(String regkey) {
        Student student = sqlLogic.getStudentByRegistrationKey(regkey);

        if (student == null) {
            throw new EntityNotFoundException("No student with given registration key: " + regkey);
        }
        return getJoinStatusResult(student.isRegistered());
    }

    private JsonResult getInstructorJoinStatus(String regkey, boolean isCreatingAccount) {
        if (isCreatingAccount) {
            AccountRequest sqlAccountRequest = sqlLogic.getAccountRequestByRegistrationKey(regkey);

            if (sqlAccountRequest == null) {
                throw new EntityNotFoundException("No account request with given registration key: " + regkey);
            }

            return getJoinStatusResult(sqlAccountRequest.getRegisteredAt() != null);
        }

        Instructor instructor = sqlLogic.getInstructorByRegistrationKey(regkey);

        if (instructor == null) {
            throw new EntityNotFoundException("No instructor with given registration key: " + regkey);
        }
        return getJoinStatusResult(instructor.isRegistered());
    }

    private JsonResult getJoinStatusResult(boolean hasJoined) {
        JoinStatus result = new JoinStatus(hasJoined);
        return new JsonResult(result);
    }
}
