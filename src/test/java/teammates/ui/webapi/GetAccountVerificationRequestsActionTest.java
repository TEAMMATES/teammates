package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.AccountVerificationRequestsData;

/**
 * Tests for {@link GetAccountVerificationRequestsAction}.
 */
public class GetAccountVerificationRequestsActionTest
        extends BaseActionTest<GetAccountVerificationRequestsAction, AccountVerificationRequestsData> {

    @Test(groups = GroupNames.ACTION)
    public void getAccountVerificationRequestsAction_filtersSearchesAndLimitsResults() {
        var account = given.account("account");
        var institute = given.institute("institute", i -> i.name("Shared Institute").country("SG"));
        given.accountVerificationRequest("older-request",
                ar -> ar.account(account.alias())
                        .institute(institute.alias())
                        .name("Older Request")
                        .email("older@test.tmt")
                        .comments("shared comment")
                        .pending()
                        .createdAt(Instant.parse("2024-01-01T00:00:00Z")));
        var newerMatchingRequest = given.accountVerificationRequest("newer-request",
                ar -> ar.account(account.alias())
                        .institute(institute.alias())
                        .name("Newer Request")
                        .email("newer@test.tmt")
                        .comments("shared comment")
                        .pending()
                        .createdAt(Instant.parse("2024-01-02T00:00:00Z")));
        given.accountVerificationRequest("approved-request",
                ar -> ar.account(account.alias())
                        .institute(institute.alias())
                        .name("Approved Request")
                        .email("approved@test.tmt")
                        .comments("shared comment")
                        .approved()
                        .createdAt(Instant.parse("2024-01-03T00:00:00Z")));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_STATUS, "PENDING")
                .withParam(Const.ParamsNames.SEARCH_KEY, "Shared Institute")
                .withParam(Const.ParamsNames.LIMIT, "1")
                .withAdminAuth();

        AccountVerificationRequestsData result = execute(request);

        assertEquals(1, result.getAccountVerificationRequests().size());
        assertEquals(newerMatchingRequest.id(),
                        result.getAccountVerificationRequests().get(0).getAccountVerificationRequestId());
        assertEquals("Newer Request", result.getAccountVerificationRequests().get(0).getName());
        assertEquals("newer@test.tmt", result.getAccountVerificationRequests().get(0).getEmail());
        assertEquals("Shared Institute", result.getAccountVerificationRequests().get(0).getInstitute());
    }

    @Test(groups = GroupNames.ACTION)
    public void getAccountVerificationRequestsAction_invalidLimit_throwsInvalidHttpParameterException() {
        var account = given.account("account");
        given.accountVerificationRequest("request", ar -> ar.account(account.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.LIMIT, "0")
                .withAdminAuth();

        assertActionThrows(InvalidHttpParameterException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getAccountVerificationRequestsAction_invalidStatus_throwsInvalidHttpParameterException() {
        var account = given.account("account");
        given.accountVerificationRequest("request", ar -> ar.account(account.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_STATUS, "invalid")
                .withAdminAuth();

        InvalidHttpParameterException ihpe = assertActionThrows(InvalidHttpParameterException.class, request);
        assertEquals("Invalid value for " + Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_STATUS
                + " parameter: [invalid]", ihpe.getMessage());
    }
}
