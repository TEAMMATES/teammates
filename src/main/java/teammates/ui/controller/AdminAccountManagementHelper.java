package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;

public class AdminAccountManagementHelper extends Helper{
	public HashMap<String, ArrayList<InstructorAttributes>> instructorCoursesTable;
	public HashMap<String, AccountAttributes> instructorAccountsTable;
	
	public String getAccountDetailsLink(String googleId){
		String link = Common.PAGE_ADMIN_ACCOUNT_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_INSTRUCTOR_ID,googleId);
		return link;
	}
	
	public String getInstructorDeleteLink(String googleId){
		String link = Common.PAGE_ADMIN_ACCOUNT_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_INSTRUCTOR_ID,googleId);
		return link;
	}
	
	public String getAccountDeleteLink(String googleId){
		String link = Common.PAGE_ADMIN_ACCOUNT_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_INSTRUCTOR_ID,googleId);
		link = Common.addParamToUrl(link,"account","true");
		return link;
	}
}
