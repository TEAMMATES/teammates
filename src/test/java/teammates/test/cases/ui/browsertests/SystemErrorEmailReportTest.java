package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

import com.google.apphosting.api.DeadlineExceededException;

/**
 * Triggers various system errors that in turn triggers email error reports
 * to the admin. When run against a production server, this class triggers
 * three emails to the admin.
 */
public class SystemErrorEmailReportTest extends BaseUiTestCase {
    private static Browser browser;
    private static AppPage page;

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();

        browser = BrowserPool.getBrowser();
        page = loginAdmin(browser);

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

    public void testAssertionError() {
        
        ______TS("AssertionError testing");
        
        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, AssertionError.class.getSimpleName());
        page.navigateTo(url);
        print("AssertionError triggered, please check your crash report at "
                + Config.SUPPORT_EMAIL);
    }
    
    public void testEntityDoesNotExistException() {
        
        ______TS("EntityDoesNotExistException testing");
        
        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, EntityDoesNotExistException.class.getSimpleName());
        page.navigateTo(url);
        print("This exception is handled by system, make sure you don't receive any emails. ");
    }
    
    public void testNullPointerException() {
        
        ______TS("NullPointerException testing");
        
        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, NullPointerException.class.getSimpleName());
        page.navigateTo(url);
        print("NullPointerException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);
    }
    
    public void testDeadlineExceededException() {
        
        ______TS("Deadline Exceeded testing");
        
        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, DeadlineExceededException.class.getSimpleName());
        page.navigateTo(url);
        print("DeadlineExceededException triggered, please check your crash report at " + Config.SUPPORT_EMAIL);
        
    }
    
    //TODO: this test should be moved to the class testing access control
    public void testUnauthorizedAccessException() {
        
        ______TS("UnauthorizedAccessException testing");
        
        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, UnauthorizedAccessException.class.getSimpleName());
        page.navigateTo(url);
        print("This exception is handled by system, make sure you don't receive any emails. ");
    }
    
    public void testNullPostParamException() {
        ______TS("NullPostParamException testing");
        
        AppUrl url = createUrl(Const.ActionURIs.ADMIN_EXCEPTION_TEST)
                .withParam(Const.ParamsNames.ERROR, NullPostParameterException.class.getSimpleName());
        page.navigateTo(url);
        page.verifyStatus(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE.replace("<br>", "\n"));
        print("This exception is handled by system, make sure you don't receive any emails. ");
    }

    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
        BrowserPool.release(browser);
    }
}
