package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class InstructorHomePageData extends PageData {
	
	public InstructorHomePageData(AccountAttributes account) {
		super(account);
	}
	
	public InstructorAttributes instructor;
	public List<CourseSummaryBundle> courses;
	
	public String getInstructorEvaluationLinkForCourse(String courseID) {
		String link = super.getInstructorEvaluationLink();
		link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
		return link;
	}

}
