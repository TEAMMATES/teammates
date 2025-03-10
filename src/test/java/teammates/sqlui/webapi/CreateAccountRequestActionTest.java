package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.CreateAccountRequestAction;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionTest extends BaseActionTest<CreateAccountRequestAction> {

    private AccountCreateRequest createRequest;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        createRequest = getTypicalAccountCreateRequest();
    }

    @Test
    void testExecute_nullInstructorName_throwsInvalidHttpRequestBodyException() {
        createRequest.setInstructorName(null);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(createRequest);
        assertEquals("name cannot be null", ex.getMessage());
        verifyNoTasksAdded();
    }

    @Test
    void testExecute_nullInstructorEmail_throwsInvalidHttpRequestBodyException() {
        createRequest.setInstructorEmail(null);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(createRequest);
        assertEquals("email cannot be null", ex.getMessage());
        verifyNoTasksAdded();
    }

    @Test
    void testExecute_nullInstructorInstitution_throwsInvalidHttpRequestBodyException() {
        createRequest.setInstructorInstitution(null);

        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(createRequest);
        assertEquals("institute cannot be null", ex.getMessage());
        verifyNoTasksAdded();
    }

    @Test
    void testExecute_validAccountCreateRequest_success() throws InvalidParametersException {
        String instructorEmail = "jamesbond89@gmail.tmt";
        String instructorName = "JamesBond";
        String instructorInstitution = "TEAMMATES Test Institute 1";
        String instructorComments = "comments";
        AccountRequest accountRequest = new AccountRequest(instructorEmail, instructorName, instructorInstitution,
                AccountRequestStatus.PENDING, instructorComments);

        when(mockLogic.createAccountRequestWithTransaction(instructorName, instructorEmail, instructorInstitution,
                AccountRequestStatus.PENDING, instructorComments)).thenReturn(accountRequest);

        CreateAccountRequestAction action = getAction(createRequest);
        AccountRequestData output = (AccountRequestData) getJsonResult(action).getOutput();

        verifyAccountRequestCreated(output, accountRequest);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);
        verifyNumberOfEmailsSent(2);
    }

    private AccountCreateRequest getTypicalAccountCreateRequest() {
        AccountCreateRequest createRequest = new AccountCreateRequest();

        createRequest.setInstructorEmail("  jamesbond89@gmail.tmt  ");
        createRequest.setInstructorName("  JamesBond  ");
        createRequest.setInstructorInstitution("  TEAMMATES Test Institute 1  ");
        createRequest.setInstructorComments("  comments  ");

        return createRequest;
    }

    private void verifyAccountRequestCreated(AccountRequestData output, AccountRequest accountRequest) {
        assertEquals(output.getId(), accountRequest.getId().toString());
        assertEquals(output.getEmail(), accountRequest.getEmail());
        assertEquals(output.getName(), accountRequest.getName());
        assertEquals(output.getInstitute(), accountRequest.getInstitute());
        assertEquals(output.getRegistrationKey(), accountRequest.getRegistrationKey());
        assertEquals(output.getStatus(), accountRequest.getStatus());
        assertEquals(output.getComments(), accountRequest.getComments());
    }
}
