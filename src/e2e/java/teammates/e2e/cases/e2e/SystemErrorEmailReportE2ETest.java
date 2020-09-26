package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.e2e.util.BackDoor;

/**
 * Verifies that various system error report emails should or should not be sent to the admin.
 */
public class SystemErrorEmailReportE2ETest extends BaseE2ETestCase {


	@Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @Override
    protected void prepareBrowser() {
        // this test does not require any browser
    }

    @Test
    public void testAll() {
        testAssertionError();
        testNullPointerException();
        testDeadlineExceededException();
        testDatastoreTimeoutException();
        testUnauthorizedAccessException();
        testInvalidHttpParameterException();
        testEntityNotFoundException();
    }

    private void testAssertionError() {

        ______TS(Const.TestCase.ASSERTION_ERROR_TESTING);

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName())
                .toString();

        BackDoor.executeGetRequest(url, null);

        print("AssertionError triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    private void testNullPointerException() {

        ______TS(Const.TestCase.NULL_POINTER_EXCEPTION_TESTING);

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName())
                .toString();

        BackDoor.executeGetRequest(url, null);

        print("NullPointerException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    private void testDeadlineExceededException() {

        ______TS(Const.TestCase.DEADLINE_EXCEEDED_EXCEPTION_TESTING);

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, DeadlineExceededException.class.getSimpleName())
                .toString();

        BackDoor.executeGetRequest(url, null);

        print("DeadlineExceededException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    private void testDatastoreTimeoutException() {

        ______TS(Const.TestCase.DATASTORE_TIMEOUT_EXCEPTION_TESTING);

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, DatastoreTimeoutException.class.getSimpleName())
                .toString();

        BackDoor.executeGetRequest(url, null);

        print("DatastoreTimeoutException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    private void testUnauthorizedAccessException() {

        ______TS(Const.TestCase.UNAUTHORIZED_ACCESS_EXCEPTION_TESTING);

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, UnauthorizedAccessException.class.getSimpleName())
                .toString();

        BackDoor.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

    private void testInvalidHttpParameterException() {

        ______TS(Const.TestCase.INVALID_HTTP_PARAM_EXCEPTION_TESTING);

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, InvalidHttpParameterException.class.getSimpleName())
                .toString();

        BackDoor.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

    private void testEntityNotFoundException() {

        ______TS(Const.TestCase.ENTITY_NOT_FOUND_EXCEPTION_TESTING);

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, EntityNotFoundException.class.getSimpleName())
                .toString();

        BackDoor.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

}
