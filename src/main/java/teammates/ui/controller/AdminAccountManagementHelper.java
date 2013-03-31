package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorData;

public class AdminAccountManagementHelper extends Helper{
	public List<InstructorData> instructorList;
	public HashMap<Integer, List<String>> accountList;
	
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
