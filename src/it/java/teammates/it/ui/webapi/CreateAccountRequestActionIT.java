package teammates.it.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Const;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.webapi.CreateAccountRequestAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link CreateAccountRequestAction}.
 */
public class CreateAccountRequestActionIT extends BaseActionIT<CreateAccountRequestAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUEST;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testExecute() throws Exception {
        // This is separated into different test methods.
    }

    @Test
    void testExecute_nullEmail_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorName("Paul Atreides");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("email cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_nullName_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorInstitution("House Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("name cannot be null", ihrbException.getMessage());
    }

    @Test
    void testExecute_nullInstitute_throwsInvalidHttpRequestBodyException() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setInstructorEmail("kwisatz.haderach@atreides.org");
        request.setInstructorName("Paul Atreides");
        InvalidHttpRequestBodyException ihrbException = verifyHttpRequestBodyFailure(request);
        assertEquals("institute cannot be null", ihrbException.getMessage());
    }

    @Override
    protected void testAccessControl() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'testAccessControl'");
    }

}
