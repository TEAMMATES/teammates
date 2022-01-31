package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.output.JoinStatus;

/**
 * Get the join status of a course.
 */
class GetCourseJoinStatusAction extends Action {

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
        boolean isCreatingAccount = getRequestParamValue(Const.ParamsNames.IS_CREATING_ACCOUNT).equals("true");

        switch (entityType) {
        case Const.EntityType.STUDENT:
            return getStudentJoinStatus(regkey);
        case Const.EntityType.INSTRUCTOR:
            return getInstructorJoinStatus(regkey, isCreatingAccount);
        default:
            throw new InvalidHttpParameterException("Error: invalid entity type");
        }
    }

    private JsonResult getStudentJoinStatus(String regkey) {
        StudentAttributes student = logic.getStudentForRegistrationKey(regkey);
        if (student == null) {
            throw new EntityNotFoundException("No student with given registration key: " + regkey);
        }
        return getJoinStatusResult(student.isRegistered());
    }

    private JsonResult getInstructorJoinStatus(String regkey, boolean isCreatingAccount) {
        if (isCreatingAccount) {
            try {
                AccountRequestAttributes accountRequest = logic.getAccountRequestForRegistrationKey(regkey);
                return getJoinStatusResult(accountRequest.getRegisteredAt() != null);
            } catch (EntityDoesNotExistException ednee) {
                throw new EntityNotFoundException(ednee);
            }
        } else {
            InstructorAttributes instructor = logic.getInstructorForRegistrationKey(regkey);
            if (instructor == null) {
                throw new EntityNotFoundException("No instructor with given registration key: " + regkey);
            }
            return getJoinStatusResult(instructor.isRegistered());
        }
    }

    private JsonResult getJoinStatusResult(boolean hasJoined) {
        JoinStatus result = new JoinStatus(hasJoined);
        return new JsonResult(result);
    }
}
