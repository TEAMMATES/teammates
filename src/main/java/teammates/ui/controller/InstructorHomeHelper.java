package teammates.ui.controller;

import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.CourseDataDetails;

public class InstructorHomeHelper extends Helper {
	public List<CourseDataDetails> courses;
	
	public String getInstructorEvaluationLink(String courseID) {
		String link = super.getInstructorEvaluationLink();
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, courseID);
		return link;
	}
}


