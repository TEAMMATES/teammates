package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Constants;
import teammates.common.util.Url;

public class AdminAccountManagementPageData extends PageData {

	public HashMap<String, ArrayList<InstructorAttributes>> instructorCoursesTable;
	public HashMap<String, AccountAttributes> instructorAccountsTable;
	
	public AdminAccountManagementPageData(AccountAttributes account) {
		super(account);
	}
	
	public String getAdminViewAccountDetailsLink(String googleId){
		String link = Constants.ACTION_ADMIN_ACCOUNT_DETAILS;
		link = Url.addParamToUrl(link,Constants.PARAM_INSTRUCTOR_ID,googleId);
		return link;
	}
	
	public String getAdminDeleteInstructorStatusLink(String googleId){
		String link = Constants.ACTION_ADMIN_ACCOUNT_DELETE;
		link = Url.addParamToUrl(link,Constants.PARAM_INSTRUCTOR_ID,googleId);
		return link;
	}
	
	public String getAdminDeleteAccountLink(String googleId){
		String link = Constants.ACTION_ADMIN_ACCOUNT_DELETE;
		link = Url.addParamToUrl(link,Constants.PARAM_INSTRUCTOR_ID,googleId);
		link = Url.addParamToUrl(link,"account","true");
		return link;
	}

}
