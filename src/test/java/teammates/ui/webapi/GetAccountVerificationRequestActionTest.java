package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.AccountVerificationRequestData;

/**
 * Tests for {@link GetAccountVerificationRequestAction}.
 */
public class GetAccountVerificationRequestActionTest
        extends BaseActionTest<GetAccountVerificationRequestAction, AccountVerificationRequestData> {

    @Test(groups = GroupNames.ACTION)
    public void getAccountVerificationRequestAction_owner_returnsAccountVerificationRequestData() {
        var account = given.account("account");
        var institute = given.institute("institute", i -> i.name("Test Institute").country("SG"));
        var accountVerificationRequest = given.accountVerificationRequest("account-request",
                ar -> ar.account(account.alias())
                .institute(institute.alias())
                .name("Request Owner")
                .email("owner@test.tmt"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, accountVerificationRequest.id().toString())
                .withCookie(getAuthCookie(account.id()));

        AccountVerificationRequestData result = execute(request);

        assertEquals(accountVerificationRequest.id(), result.getAccountVerificationRequestId());
        assertEquals("Request Owner", result.getName());
        assertEquals("owner@test.tmt", result.getEmail());
        assertEquals("Test Institute", result.getInstitute());
    }

    @Test(groups = GroupNames.ACTION)
    public void getAccountVerificationRequestAction_adminBypass_returnsAccountVerificationRequestData() {
        var adminAccount = given.account("admin-account", a -> a.admin());
        var ownerAccount = given.account("owner-account");
        var accountVerificationRequest = given.accountVerificationRequest("account-request",
                ar -> ar.account(ownerAccount.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, accountVerificationRequest.id().toString())
                .withCookie(getAuthCookie(adminAccount.id()));

        AccountVerificationRequestData result = execute(request);

        assertEquals(accountVerificationRequest.id(), result.getAccountVerificationRequestId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getAccountVerificationRequestAction_differentLoggedInUser_throwsUnauthorizedAccessException() {
        var ownerAccount = given.account("owner-account");
        var otherAccount = given.account("other-account");
        var accountVerificationRequest = given.accountVerificationRequest("account-request",
                ar -> ar.account(ownerAccount.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, accountVerificationRequest.id().toString())
                .withCookie(getAuthCookie(otherAccount.id()));

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
