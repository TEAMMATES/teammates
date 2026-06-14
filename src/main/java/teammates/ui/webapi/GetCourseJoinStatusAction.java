package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.JoinStatus;

/**
 * Get the join status of a course.
 */
public class GetCourseJoinStatusAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() {
        // Any user can use a join link as long as its parameters are valid
    }

    @Override
    public JsonResult execute() {
        String regkey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);

        User user = logic.getUserByRegistrationKey(regkey);
        if (user == null) {
            throw new EntityNotFoundException("No user with given registration key: " + regkey);
        }

        JoinStatus result = new JoinStatus(user.isRegistered());
        return new JsonResult(result);
    }
}
