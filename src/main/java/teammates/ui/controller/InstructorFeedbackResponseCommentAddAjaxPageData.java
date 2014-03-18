package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;

public class InstructorFeedbackResponseCommentAddAjaxPageData extends PageData {

	public FeedbackResponseCommentAttributes comment;
	public boolean isError;
	public String errorMessage;
	
	public InstructorFeedbackResponseCommentAddAjaxPageData(AccountAttributes account) {
		super(account);
	}
}
