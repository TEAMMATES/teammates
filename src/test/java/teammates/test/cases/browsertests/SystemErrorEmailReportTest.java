package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.apphosting.api.DeadlineExceededException;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.test.pageobjects.AppPage;

/**
 * Verifies that various system error report emails should or should not be sent to the admin.
 */
public class SystemErrorEmailReportTest extends BaseUiTestCase {
    private AppPage page;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        page = loginAdmin();
    }

    @Test
    public void testAll() {
        testAssertionError();
        testEntityDoesNotExistException();
        testNullPointerException();
        testDeadlineExceededException();
        testUnauthorizedAccessException();
        testNullPostParamException();
    }

    private void testAssertionError() {

        ______TS("AssertionError testing");

        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName());
        page.navigateTo(url);
        print("AssertionError triggered, please check your crash report at "
                + Config.SUPPORT_EMAIL);
    }

    private void testEntityDoesNotExistException() {

        ______TS("EntityDoesNotExistException testing");

        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, EntityDoesNotExistException.class.getSimpleName());
        page.navigateTo(url);
        print("This exception is handled by system, make sure you don't receive any emails. ");
    }

    private void testNullPointerException() {

        ______TS("NullPointerException testing");

        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName());
        page.navigateTo(url);
        print("NullPointerException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);
    }

    private void testDeadlineExceededException() {

        ______TS("Deadline Exceeded testing");

        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, DeadlineExceededException.class.getSimpleName());
        page.navigateTo(url);
        print("DeadlineExceededException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);

    }

    //TODO: this test should be moved to the class testing access control
    private void testUnauthorizedAccessException() {

        ______TS("UnauthorizedAccessException testing");

        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, UnauthorizedAccessException.class.getSimpleName());
        page.navigateTo(url);
        print("This exception is handled by system, make sure you don't receive any emails. ");
    }

    private void testNullPostParamException() {
        ______TS("NullPostParamException testing");

        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, NullPostParameterException.class.getSimpleName());
        page.navigateTo(url);
        page.waitForTextsForAllStatusMessagesToUserEquals(
                Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE.replace("<br>", "\n"));
        print("This exception is handled by system, make sure you don't receive any emails. ");
    }

}
