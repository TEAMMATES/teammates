package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountRequestData;

/**
 * SUT: {@link GetAccountRequestAction}.
 */
public class GetAccountRequestActionTest extends BaseActionTest<GetAccountRequestAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        loginAsAdmin();

        AccountRequestAttributes accountRequest =
                logic.getAccountRequest("approvedUnregisteredInstructor1@tmt.tmt", "TMT, Singapore");

        ______TS("typical success case");

        String[] params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        GetAccountRequestAction action = getAction(params);
        JsonResult result = getJsonResult(action);

        AccountRequestData response = (AccountRequestData) result.getOutput();

        assertEquals(accountRequest.getName(), response.getName());
        assertEquals(accountRequest.getInstitute(), response.getInstitute());
        assertEquals(accountRequest.getEmail(), response.getEmail());
        assertEquals(accountRequest.getHomePageUrl(), response.getHomePageUrl());
        assertEquals(accountRequest.getComments(), response.getComments());
        assertEquals(accountRequest.getRegistrationKey(), response.getRegistrationKey());
        assertEquals(accountRequest.getStatus(), response.getStatus());
        assertEquals(accountRequest.getCreatedAt().toEpochMilli(), response.getCreatedAt());
        if (accountRequest.getLastProcessedAt() == null) {
            assertNull(response.getLastProcessedAt());
        } else {
            assertEquals((Long) accountRequest.getLastProcessedAt().toEpochMilli(), response.getLastProcessedAt());
        }
        if (accountRequest.getRegisteredAt() == null) {
            assertNull(response.getRegisteredAt());
        } else {
            assertEquals((Long) accountRequest.getRegisteredAt().toEpochMilli(), response.getRegisteredAt());
        }

        ______TS("failure: account request does not exist");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-existent@email",
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, "TMT, Singapore",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Account request for email: non-existent@email and institute: TMT, Singapore not found.",
                enfe.getMessage());

        ______TS("failure: null parameters");

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_INSTITUTION, accountRequest.getInstitute(),
        };
        InvalidHttpParameterException ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_EMAIL),
                ihpe.getMessage());

        params = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
        };
        ihpe = verifyHttpParameterFailure(params);
        assertEquals(String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.INSTRUCTOR_INSTITUTION),
                ihpe.getMessage());

        logoutUser();
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
