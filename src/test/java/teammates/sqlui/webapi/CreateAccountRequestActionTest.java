package teammates.sqlui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
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

    private AccountCreateRequest getTypicalAccountCreateRequest() {
        AccountCreateRequest createRequest = new AccountCreateRequest();

        createRequest.setInstructorName("JamesBond");
        createRequest.setInstructorEmail("jamesbond89@gmail.tmt");
        createRequest.setInstructorInstitution("TEAMMATES Test Institute 1");

        return createRequest;
    }
}
