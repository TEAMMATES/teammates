package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;

public class AllActionAccessControl extends BaseActionTest {
    
    private String[] submissionParams = new String[]{};
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void AdminAccountDelete() throws Exception {
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminAccountDetailsPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminAccountManagementPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminActivityLogPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminExceptionTest() throws Exception{
        uri = Const.ActionURIs.ADMIN_EXCEPTION_TEST;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminHomePage() throws Exception{
        uri = Const.ActionURIs.ADMIN_HOME_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminInstructorAccountAdd() throws Exception{
        uri = Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminSearchPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_SEARCH_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }

}
