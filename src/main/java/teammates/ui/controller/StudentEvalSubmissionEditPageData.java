package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class StudentEvalSubmissionEditPageData extends EvalSubmissionEditPageData {
	
	public StudentEvalSubmissionEditPageData(AccountAttributes account) {
		super(account);
		this.isPreview = false;
		this.previewName = account.name;
		this.previewEmail = account.email;
	}
	
	/**
	 * To allow previewing for students who have not joined the course
	 */
	public StudentEvalSubmissionEditPageData(String previewName, String previewEmail) {
		super(null);
		this.isPreview = true;
		this.previewName = previewName;
		this.previewEmail = previewEmail;
	}

}
