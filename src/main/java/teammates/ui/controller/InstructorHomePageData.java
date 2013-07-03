package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.util.Constants;
import teammates.common.util.Url;

public class InstructorHomePageData extends PageData {
	
	public InstructorHomePageData(AccountAttributes account) {
		super(account);
	}
	
	public List<CourseDetailsBundle> courses;
	
	public String getInstructorEvaluationLinkForCourse(String courseID) {
		String link = super.getInstructorEvaluationLink();
		link = Url.addParamToUrl(link, Constants.PARAM_COURSE_ID, courseID);
		return link;
	}

}
