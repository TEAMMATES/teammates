package teammates.test.cases.ui.browsertests;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AdminHomePage;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.GoogleLoginPage;
import teammates.test.util.Url;

public class BaseUiTestCase extends BaseTestCase {

    protected static final String appUrl = TestProperties.inst().TEAMMATES_URL;
    
    /**
     * Do an initial loginAdminToPage (may or may not involve explicit logging in action),
     * logs out, then logs in again (this time it will be an explicit logging in).
     * This is to handle the cases in admin UI tests where the admin username has to be the
     * one specified in <code>${test.admin}</code>.
     */
    protected static <T extends AppPage> T loginAdminToPageForAdminUiTests(Browser browser, Url url,
                                                                           Class<T> typeOfPage) {
        loginAdminToPage(browser, url, typeOfPage);
        AppPage.logout(browser);
        return loginAdminToPage(browser, url, typeOfPage);
    }

    /**
     * Logs in a page using admin credentials (i.e. in masquerade mode).
     */
    protected static <T extends AppPage> T loginAdminToPage(Browser browser, Url url, Class<T> typeOfPage) {
        
        String adminUsername = TestProperties.inst().TEST_ADMIN_ACCOUNT; 
        String adminPassword = TestProperties.inst().TEST_ADMIN_PASSWORD;
        
        String instructorId = url.get(Const.ParamsNames.USER_ID);
        
        if(instructorId==null){ //admin using system as admin
            instructorId = adminUsername;
        }
        
        if(browser.isAdminLoggedIn){
            browser.driver.get(url.toAbsoluteString());
            try {
                return AppPage.getNewPageInstance(browser, typeOfPage);
            } catch(Exception e) {
                //ignore and try to logout and login again if fail.
            }
        }
        
        //logout and attempt to load the requested URL. This will be 
        //  redirected to a dev-server/google login page
        AppPage.logout(browser);
        browser.driver.get(url.toAbsoluteString());
        String pageSource = browser.driver.getPageSource();
        
        //login based on the login page type
        if(DevServerLoginPage.containsExpectedPageContents(pageSource)){
            DevServerLoginPage loginPage = AppPage.getNewPageInstance(browser, DevServerLoginPage.class);
            loginPage.loginAdminAsInstructor(adminUsername, adminPassword, instructorId);

        } else if(GoogleLoginPage.containsExpectedPageContents(pageSource)){
            GoogleLoginPage loginPage = AppPage.getNewPageInstance(browser, GoogleLoginPage.class);
            loginPage.loginAdminAsInstructor(adminUsername, adminPassword, instructorId);
        
        } else {
            throw new IllegalStateException("Not a valid login page :" + pageSource);
        }
        
        //After login, the browser should be redirected to the page requested originally.
        //  No need to reload. In fact, reloading might results in duplicate request to the server.
        return AppPage.getNewPageInstance(browser, typeOfPage);
    }
    
    /**
     * Updates/creates the given data on the datastore.
     */
    protected static void restoreTestDataOnServer(DataBundle testData) {

        int counter = 0;
        String backDoorOperationStatus = "";
        int retryLimit;
        if(TestProperties.inst().isDevServer()){
            retryLimit = 5;
        } else {
            retryLimit = 1;
        }

        while(counter < retryLimit){
            counter++;
            backDoorOperationStatus = BackDoor.restoreDataBundle(testData);
            if(backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS)){
                break;
            }
            System.out.println("Re-trying restoreDataBundle - " + backDoorOperationStatus);
        }
        if(counter >= retryLimit){
            Assumption.assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
        }
    }

    /**
     * Updates/creates the given data on the datastore.
     */
    protected static void removeTestDataOnServer(DataBundle testData) {

        int counter = 0;
        String backDoorOperationStatus = "";
        int retryLimit;
        if(TestProperties.inst().isDevServer()){
            retryLimit = 5;
        } else {
            retryLimit = 1;
        }

        while(counter < retryLimit){
            counter++;
            backDoorOperationStatus = BackDoor.removeDataBundleFromDb(testData);
            if(backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS)){
                break;
            }
            System.out.println("Re-trying restoreDataBundle - " + backDoorOperationStatus);
        }
        if(counter >= retryLimit){
            Assumption.assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
        }
    }
    
    /**
     * Removes and then creates given data on the datastore.
     */
    protected static void removeAndRestoreTestDataOnServer(DataBundle testData) {
        int counter = 0;
        String backDoorOperationStatus = "";
        int retryLimit;
        if(TestProperties.inst().isDevServer()){
            retryLimit = 5;
        } else {
            retryLimit = 1;
        }

        while(counter < retryLimit){
            counter++;
            backDoorOperationStatus = BackDoor.removeAndRestoreDataBundleFromDb(testData);
            if(backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS)){
                break;
            }
            System.out.println("Re-trying restoreDataBundle - " + backDoorOperationStatus);
        }
        if(counter >= retryLimit){
            Assumption.assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
        }
    }
    
    protected static void putDocuments(DataBundle testData) {
        int counter = 0;
        String backDoorOperationStatus = "";
        int retryLimit;
        if(TestProperties.inst().isDevServer()){
            retryLimit = 5;
        } else {
            retryLimit = 1;
        }

        while(counter < retryLimit){
            counter++;
            backDoorOperationStatus = BackDoor.putDocuments(testData);
            if(backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS)){
                break;
            }
            System.out.println("Re-trying restoreDataBundle - " + backDoorOperationStatus);
        }
        if(counter >= retryLimit){
            Assumption.assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
        }
    }

    protected static AdminHomePage loginAdmin(Browser currentBrowser) {
        return loginAdminToPage(currentBrowser, new Url(Const.ActionURIs.ADMIN_HOME_PAGE), AdminHomePage.class);
    }

}
