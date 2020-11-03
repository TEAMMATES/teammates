package teammates.e2e.cases;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
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
        testDatastoreTimeoutException();
        testUnauthorizedAccessException();
        testInvalidHttpParameterException();
        testEntityNotFoundException();
    }

    private void testAssertionError() {

        ______TS("AssertionError testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("AssertionError triggered, verify that you have received error logs via email");

    }

    private void testNullPointerException() {

        ______TS("NullPointerException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("NullPointerException triggered, verify that you have received error logs via email");

    }

    private void testDeadlineExceededException() {

        ______TS("DeadlineExceededException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, DeadlineExceededException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("DeadlineExceededException triggered, verify that you have received error logs via email");

    }

    private void testDatastoreTimeoutException() {

        ______TS("DatastoreTimeoutException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, DatastoreTimeoutException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("DatastoreTimeoutException triggered, verify that you have received error logs via email");

    }

    private void testUnauthorizedAccessException() {

        ______TS("UnauthorizedAccessException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, UnauthorizedAccessException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

    private void testInvalidHttpParameterException() {

        ______TS("InvalidHttpParamException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, InvalidHttpParameterException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

    private void testEntityNotFoundException() {

        ______TS("EntityNotFoundException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, EntityNotFoundException.class.getSimpleName())
                .toString();

        BACKDOOR.executeGetRequest(url, null);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

}
