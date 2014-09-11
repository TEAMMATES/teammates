package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class AdminAccountManagementPageData extends PageData {

    public HashMap<String, ArrayList<InstructorAttributes>> instructorCoursesTable;
    public HashMap<String, AccountAttributes> instructorAccountsTable;
    /**
     * By default the testing accounts should not be shown
     */
    public boolean isToShowAll = false;
    
    public AdminAccountManagementPageData(AccountAttributes account) {
        super(account);
    }
    
    public String getAdminViewAccountDetailsLink(String googleId){
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.INSTRUCTOR_ID,googleId);
        return link;
    }
    
    public String getAdminDeleteInstructorStatusLink(String googleId){
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.INSTRUCTOR_ID,googleId);
        return link;
    }
    
    public String getAdminDeleteAccountLink(String googleId){
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.INSTRUCTOR_ID,googleId);
        link = Url.addParamToUrl(link,"account","true");
        return link;
    }
    public String getInstructorHomePageViewLink(String googleId){
        String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return link;
    }
    
    public boolean isTestingAccount(AccountAttributes account){
        boolean isTestingAccount = false;
        
        if(account.email.endsWith(".tmt") || account.institute.contains("TEAMMATES Test Institute")){
            isTestingAccount = true;
        }
        return isTestingAccount;
    }
}
