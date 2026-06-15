package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstitutesData;

/**
 * Tests for {@link GetInstitutesAction}.
 */
public class GetInstitutesActionTest extends BaseActionTest<GetInstitutesAction, InstitutesData> {
    @Test(groups = GroupNames.ACTION)
    public void getInstitutesAction_accountWithApprovedRequest_returnsInstitutes() {
        var account = given.account("account");
        var institute = given.institute("institute");
        given.accountVerificationRequest("avr", avr -> avr
                .account(account.alias())
                .institute(institute.alias())
                .approved());
        persistGivenData(given);

        RequestContext testRequest = new RequestContext()
                .withCookie(getAuthCookie(account.id()))
                .withParam(Const.ParamsNames.ACCOUNT_ID, account.id().toString());

        InstitutesData result = execute(testRequest);

        assertEquals(1, result.getInstitutes().size());
        assertEquals(institute.id(), result.getInstitutes().get(0).getId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstitutesAction_accountWithNoApprovedRequests_returnsEmpty() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext testRequest = new RequestContext()
                .withCookie(getAuthCookie(account.id()))
                .withParam(Const.ParamsNames.ACCOUNT_ID, account.id().toString());

        InstitutesData result = execute(testRequest);

        assertEquals(0, result.getInstitutes().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstitutesAction_differentAccount_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var other = given.account("other");
        persistGivenData(given);

        RequestContext testRequest = new RequestContext()
                .withCookie(getAuthCookie(account.id()))
                .withParam(Const.ParamsNames.ACCOUNT_ID, other.id().toString());

        assertActionThrows(UnauthorizedAccessException.class, testRequest);
    }
}
