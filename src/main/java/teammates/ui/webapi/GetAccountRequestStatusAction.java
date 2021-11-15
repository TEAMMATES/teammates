package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.output.JoinStatus;

/**
 * Get the join status of account request.
 */
class GetAccountRequestStatusAction extends Action {

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

        try {
            AccountRequestAttributes accountRequest = logic.getAccountRequestForRegistrationKey(regkey);
            boolean hasJoined = accountRequest.getRegisteredAt() != null;
            return getJoinStatusResult(hasJoined);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }

    private JsonResult getJoinStatusResult(boolean hasJoined) {
        JoinStatus result = new JoinStatus(hasJoined);
        return new JsonResult(result);
    }
}
