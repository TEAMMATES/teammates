package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorData;

public class AdminAccountManagementHelper extends Helper{
	public HashMap<String, ArrayList<InstructorData>> accounts;
	
	public String getAccountDetailsLink(String googleId){
		String link = Common.PAGE_ADMIN_ACCOUNT_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_INSTRUCTOR_ID,googleId);
		return link;
	}
}
