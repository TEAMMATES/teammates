package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;

public class InstructorFeedbackResultsPageData extends PageData {
	public FeedbackSessionResultsBundle bundle = null;
	public InstructorAttributes instructor = null;
	public String sortType = null;
	
	public InstructorFeedbackResultsPageData(AccountAttributes account) {
		super(account);
	}

}
