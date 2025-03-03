package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.CreateAccountRequestAction;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionTest extends BaseActionTest<CreateAccountRequestAction> {

    private String instructorName = "JamesBond";
    private String instructorEmail = "jamesbond89@gmail.tmt";
    private String instructorInstitution = "TEAMMATES Test Institute 1";
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
        loginAsAdmin();
        AccountRequest accountRequest = new AccountRequest(instructorEmail, instructorName, instructorInstitution, AccountRequestStatus.PENDING, null);

        when(mockLogic.createAccountRequestWithTransaction(instructorName, instructorEmail, instructorInstitution, AccountRequestStatus.PENDING, null)).thenReturn(accountRequest);

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME, 1);
    }

    private AccountCreateRequest getTypicalAccountCreateRequest() {
        AccountCreateRequest createRequest = new AccountCreateRequest();

        createRequest.setInstructorName(instructorName);
        createRequest.setInstructorEmail(instructorEmail);
        createRequest.setInstructorInstitution(instructorInstitution);

        return createRequest;
    }
}
