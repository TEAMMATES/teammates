package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class AdminHomePageData extends PageData {
	
	public String instructorId;
	public String instructorName;
	public String instructorEmail;
	public String instructorInstitution;

	public AdminHomePageData(AccountAttributes account) {
		super(account);
	}

}
