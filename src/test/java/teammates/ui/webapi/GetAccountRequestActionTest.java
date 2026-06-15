package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.AccountRequestData;

/**
 * Tests for {@link GetAccountRequestAction}.
 */
public class GetAccountRequestActionTest extends BaseActionTest<GetAccountRequestAction, AccountRequestData> {

    @Test(groups = GroupNames.ACTION)
    public void getAccountRequestAction_owner_returnsAccountRequestData() {
        var account = given.account("account");
        var institute = given.institute("institute", i -> i.name("Test Institute").country("SG"));
        var accountRequest = given.accountRequest("account-request", ar -> ar.account(account.alias())
                .institute(institute.alias())
                .name("Request Owner")
                .email("owner@test.tmt"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.id().toString())
                .withCookie(getAuthCookie(account.id()));

        AccountRequestData result = execute(request);

        assertEquals(accountRequest.id(), result.getAccountRequestId());
        assertEquals("Request Owner", result.getName());
        assertEquals("owner@test.tmt", result.getEmail());
        assertEquals("Test Institute", result.getInstitute());
    }

    @Test(groups = GroupNames.ACTION)
    public void getAccountRequestAction_adminBypass_returnsAccountRequestData() {
        var adminAccount = given.account("admin-account", a -> a.admin());
        var ownerAccount = given.account("owner-account");
        var accountRequest = given.accountRequest("account-request", ar -> ar.account(ownerAccount.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.id().toString())
                .withCookie(getAuthCookie(adminAccount.id()));

        AccountRequestData result = execute(request);

        assertEquals(accountRequest.id(), result.getAccountRequestId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getAccountRequestAction_differentLoggedInUser_throwsUnauthorizedAccessException() {
        var ownerAccount = given.account("owner-account");
        var otherAccount = given.account("other-account");
        var accountRequest = given.accountRequest("account-request", ar -> ar.account(ownerAccount.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_REQUEST_ID, accountRequest.id().toString())
                .withCookie(getAuthCookie(otherAccount.id()));

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
