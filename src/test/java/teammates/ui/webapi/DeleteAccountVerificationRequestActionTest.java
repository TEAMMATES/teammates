package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteAccountVerificationRequestAction}.
 */
public class DeleteAccountVerificationRequestActionTest
        extends BaseActionTest<DeleteAccountVerificationRequestAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteAccountVerificationRequestAction_existingRequest_deletesRequest() {
        var avr = given.accountVerificationRequest("avr");
        persistGivenData(given);

        verifyPresentInDatabase(AccountVerificationRequest.class, avr.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, avr.id().toString())
                .withAdminAuth();

        execute(request);

        verifyAbsentInDatabase(AccountVerificationRequest.class, avr.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteAccountVerificationRequestAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var avr = given.accountVerificationRequest("avr");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, avr.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
