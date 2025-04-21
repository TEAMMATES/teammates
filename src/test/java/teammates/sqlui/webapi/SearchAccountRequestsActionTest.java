package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestsData;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.SearchAccountRequestsAction;

/**
 * SUT: {@link SearchAccountRequestsAction}.
 */
public class SearchAccountRequestsActionTest extends BaseActionTest<SearchAccountRequestsAction> {

    private String searchKey = "search-key";
    private List<AccountRequest> accountRequests;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_ACCOUNT_REQUESTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        reset(mockLogic);

        AccountRequest accountRequest1 = getTypicalAccountRequest();

        AccountRequest accountRequest2 = getTypicalAccountRequest();
        accountRequest2.setEmail("valid2@test.com");
        accountRequest2.setName("Test Name 2");
        accountRequest2.setInstitute("TEAMMATES Test Institute 2, Test Country 2");
        accountRequest2.setStatus(AccountRequestStatus.APPROVED);
        accountRequest2.setComments("Test comments 2");

        accountRequests = List.of(accountRequest1, accountRequest2);
    }

    @Test
    void testExecute_searchAccountRequests_success() throws SearchServiceException {
        when(mockLogic.searchAccountRequestsInWholeSystem(searchKey)).thenReturn(accountRequests);

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchAccountRequestsAction action = getAction(params);
        AccountRequestsData output = (AccountRequestsData) getJsonResult(action).getOutput();

        verify(mockLogic).searchAccountRequestsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        assertEquals(accountRequests.size(), output.getAccountRequests().size());

        for (int i = 0; i < accountRequests.size(); i++) {
            AccountRequest accountRequest = accountRequests.get(i);
            AccountRequestData accountRequestData = output.getAccountRequests().get(i);

            assertEquals(accountRequest.getId().toString(), accountRequestData.getId());
            assertEquals(accountRequest.getEmail(), accountRequestData.getEmail());
            assertEquals(accountRequest.getName(), accountRequestData.getName());
            assertEquals(accountRequest.getInstitute(), accountRequestData.getInstitute());
            assertEquals(accountRequest.getRegistrationKey(), accountRequestData.getRegistrationKey());
            assertEquals(accountRequest.getStatus(), accountRequestData.getStatus());
            assertEquals(accountRequest.getComments(), accountRequestData.getComments());
            assertEquals(accountRequest.getCreatedAt().toEpochMilli(), accountRequestData.getCreatedAt());
            assertEquals(
                    accountRequest.getRegisteredAt() == null ? null : accountRequest.getRegisteredAt().toEpochMilli(),
                    accountRequestData.getRegisteredAt());
        }
    }

    @Test
    void testExecute_searchAccountRequestsNoMatch_success() throws SearchServiceException {
        when(mockLogic.searchAccountRequestsInWholeSystem(searchKey)).thenReturn(List.of());

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchAccountRequestsAction action = getAction(params);
        AccountRequestsData output = (AccountRequestsData) getJsonResult(action).getOutput();

        verify(mockLogic).searchAccountRequestsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        assertEquals(0, output.getAccountRequests().size());
    }

    @Test
    void testExecute_searchServiceException_failure() throws SearchServiceException {
        when(mockLogic.searchAccountRequestsInWholeSystem(searchKey)).thenThrow(
                new SearchServiceException("Search service error", 500));

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchAccountRequestsAction action = getAction(params);
        MessageOutput output = (MessageOutput) getJsonResult(action, 500).getOutput();

        verify(mockLogic).searchAccountRequestsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        assertEquals("Search service error", output.getMessage());
    }

    @Test
    void testExecute_noParams_throwsInvalidHttpParameterException() {
        String[] params = {};

        verifyHttpParameterFailure(params);
    }

    @Test
    void testExecute_nullSearchKey_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.SEARCH_KEY, null,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_admin_canAccess() {
        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_notAdmin_cannotAccess() {
        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        loginAsUnregistered("unregistered");
        verifyCannotAccess(params);

        loginAsStudent("student");
        verifyCannotAccess(params);

        loginAsInstructor("instructor");
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
