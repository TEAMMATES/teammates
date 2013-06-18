package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.StudentAttributes;

public class StudentFeedbackResultsPageData extends PageData {

	public FeedbackSessionResultsBundle bundle = null;
	public StudentAttributes student = null;
	
	public StudentFeedbackResultsPageData(AccountAttributes account) {
		super(account);
	}

}
