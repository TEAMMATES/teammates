package teammates.ui.controller;

import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;

public class InstructorHomeHelper extends Helper {
	public List<CourseData> courses;
	
	public String getInstructorEvaluationLink(String courseID) {
		String link = super.getInstructorEvaluationLink();
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, courseID);
		return link;
	}
}


