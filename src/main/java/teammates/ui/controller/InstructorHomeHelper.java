package teammates.ui.controller;

import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;

public class InstructorHomeHelper extends Helper {
	public List<CourseDetailsBundle> courses;
	
	public String getInstructorEvaluationLink(String courseID) {
		String link = super.getInstructorEvaluationLink();
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, courseID);
		return link;
	}
}


