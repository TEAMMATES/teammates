package teammates.e2e.cases.e2e;

import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.e2e.util.NewBackDoor;

/**
 * Verifies that various system error report emails should or should not be sent to the admin.
 */
public class SystemErrorEmailReportE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @Test
    public void testAll() {
        testAssertionError();
        testNullPointerException();
        testDeadlineExceededException();
        testDatastoreTimeoutException();
        testUnauthorizedAccessException();
        testInvalidHttpParameterException();
    }

    private void testAssertionError() {

        ______TS("AssertionError testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName())
                .toString();

        NewBackDoor.executeRequest(HttpGet.METHOD_NAME, url);

        print("AssertionError triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    private void testNullPointerException() {

        ______TS("NullPointerException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName())
                .toString();

        NewBackDoor.executeRequest(HttpGet.METHOD_NAME, url);

        print("NullPointerException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    private void testDeadlineExceededException() {

        ______TS("DeadlineExceededException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, DeadlineExceededException.class.getSimpleName())
                .toString();

        NewBackDoor.executeRequest(HttpGet.METHOD_NAME, url);

        print("DeadlineExceededException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    private void testDatastoreTimeoutException() {

        ______TS("DatastoreTimeoutException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, DatastoreTimeoutException.class.getSimpleName())
                .toString();

        NewBackDoor.executeRequest(HttpGet.METHOD_NAME, url);

        print("DatastoreTimeoutException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    private void testUnauthorizedAccessException() {

        ______TS("UnauthorizedAccessException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, UnauthorizedAccessException.class.getSimpleName())
                .toString();

        NewBackDoor.executeRequest(HttpGet.METHOD_NAME, url);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

    private void testInvalidHttpParameterException() {

        ______TS("NullHttpParamException testing");

        String url = createUrl(Const.ResourceURIs.EXCEPTION)
                .withParam(Const.ParamsNames.ERROR, InvalidHttpParameterException.class.getSimpleName())
                .toString();

        NewBackDoor.executeRequest(HttpGet.METHOD_NAME, url);

        print("This exception is handled by system, make sure you don't receive any emails");

    }

}
