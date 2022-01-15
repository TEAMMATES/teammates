package teammates.e2e.cases;

import org.testng.annotations.Test;

import com.google.cloud.datastore.DatastoreException;

import teammates.common.exception.DeadlineExceededException;
import teammates.common.util.Const;

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
    @Override
    public void testAll() {
        testAssertionError();
        testNullPointerException();
        testDeadlineExceededException();
        testDatastoreException();
        testUnauthorizedAccessException();
        testInvalidHttpParameterException();
        testEntityNotFoundException();
    }

    private void testAssertionError() {

        ______TS("AssertionError testing");

        String url = createBackendUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("AssertionError triggered, verify that you have received error logs via email");

    }

    private void testNullPointerException() {

        ______TS("NullPointerException testing");

        String url = createBackendUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("NullPointerException triggered, verify that you have received error logs via email");

    }

    private void testDeadlineExceededException() {

        ______TS("DeadlineExceededException testing");

        String url = createBackendUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, DeadlineExceededException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("DeadlineExceededException triggered, verify that you have received error logs via email");

    }

    private void testDatastoreException() {

        ______TS("DatastoreException testing");

        String url = createBackendUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, DatastoreException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("DatastoreException triggered, verify that you have received error logs via email");

    }

    private void testUnauthorizedAccessException() {

        ______TS("UnauthorizedAccessException testing");

        String url = createBackendUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, "UnauthorizedAccessException")
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

    private void testInvalidHttpParameterException() {

        ______TS("InvalidHttpParamException testing");

        String url = createBackendUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, "InvalidHttpParameterException")
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

    private void testEntityNotFoundException() {

        ______TS("EntityNotFoundException testing");

        String url = createBackendUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, "EntityNotFoundException")
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

}
