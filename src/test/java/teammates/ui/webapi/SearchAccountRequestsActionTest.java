package teammates.ui.webapi;

import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Const;
import teammates.storage.entity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestsData;

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
    void testExecute_searchAccountRequests_success() {
        when(mockLogic.searchAccountRequestsInWholeSystem(searchKey)).thenReturn(accountRequests);

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchAccountRequestsAction action = getAction(params);
        AccountRequestsData output = (AccountRequestsData) getJsonResult(action).getOutput();

        verify(mockLogic).searchAccountRequestsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        Assertions.assertEquals(accountRequests.size(), output.getAccountRequests().size());

        for (int i = 0; i < accountRequests.size(); i++) {
            AccountRequest accountRequest = accountRequests.get(i);
            AccountRequestData accountRequestData = output.getAccountRequests().get(i);

            Assertions.assertEquals(accountRequest.getId(), accountRequestData.getId());
            Assertions.assertEquals(accountRequest.getEmail(), accountRequestData.getEmail());
            Assertions.assertEquals(accountRequest.getName(), accountRequestData.getName());
            Assertions.assertEquals(accountRequest.getInstitute(), accountRequestData.getInstitute());
            Assertions.assertEquals(accountRequest.getRegistrationKey(), accountRequestData.getRegistrationKey());
            Assertions.assertEquals(accountRequest.getStatus(), accountRequestData.getStatus());
            Assertions.assertEquals(accountRequest.getComments(), accountRequestData.getComments());
            Assertions.assertEquals(accountRequest.getCreatedAt().toEpochMilli(), accountRequestData.getCreatedAt());
            Assertions.assertEquals(
                    accountRequest.getRegisteredAt() == null ? null : accountRequest.getRegisteredAt().toEpochMilli(),
                    accountRequestData.getRegisteredAt());
        }
    }

    @Test
    void testExecute_searchAccountRequestsNoMatch_success() {
        when(mockLogic.searchAccountRequestsInWholeSystem(searchKey)).thenReturn(List.of());

        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };

        SearchAccountRequestsAction action = getAction(params);
        AccountRequestsData output = (AccountRequestsData) getJsonResult(action).getOutput();

        verify(mockLogic).searchAccountRequestsInWholeSystem(searchKey);
        verifyNoMoreInteractions(mockLogic);

        Assertions.assertEquals(0, output.getAccountRequests().size());
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
    void testAccessControl() {
        String[] params = {
                Const.ParamsNames.SEARCH_KEY, searchKey,
        };
        verifyOnlyAdminsCanAccess(params);
    }
}
