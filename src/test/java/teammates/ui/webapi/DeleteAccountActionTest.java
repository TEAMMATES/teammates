package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteAccountAction}.
 */
public class DeleteAccountActionTest extends BaseActionTest<DeleteAccountAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteAccountAction_existingAccount_deletesAccount() {
        var account = given.account("account");
        persistGivenData(given);

        verifyPresentInDatabase(Account.class, account.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_ID, account.id().toString())
                .withAdminAuth();

        execute(request);

        verifyAbsentInDatabase(Account.class, account.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteAccountAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester");
        var targetAccount = given.account("target");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_ID, targetAccount.id().toString())
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
