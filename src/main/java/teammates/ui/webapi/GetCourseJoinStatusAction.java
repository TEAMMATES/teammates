package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
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
        String isCreatingAccount = getRequestParamValue(Const.ParamsNames.IS_CREATING_ACCOUNT);

        return getJoinStatus(regkey, "true".equals(isCreatingAccount));
    }

    private JsonResult getJoinStatus(String regkey, boolean isCreatingAccount) {
        if (isCreatingAccount) {
            AccountRequest accountRequest = logic.getAccountRequestByRegistrationKey(regkey);

            if (accountRequest == null) {
                throw new EntityNotFoundException("No account request with given registration key: " + regkey);
            }

            return getJoinStatusResult(accountRequest.getRegisteredAt() != null);
        }

        User user = logic.getUserByRegistrationKey(regkey);
        if (user == null) {
            throw new EntityNotFoundException("No user with given registration key: " + regkey);
        }

        if (user instanceof Student student) {
            return getJoinStatusResult(student.isRegistered());
        } else if (user instanceof Instructor instructor) {
            return getJoinStatusResult(instructor.isRegistered());
        } else {
            throw new EntityNotFoundException(
                "User with given registration key is neither a student nor an instructor: " + regkey);
        }
    }

    private JsonResult getJoinStatusResult(boolean hasJoined) {
        JoinStatus result = new JoinStatus(hasJoined);
        return new JsonResult(result);
    }
}
