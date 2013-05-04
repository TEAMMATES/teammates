package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.TeamDetailsBundle;

public class StudentCourseDetailsHelper extends Helper {
	public CourseDetailsBundle courseDetails;
	public StudentData student;
	public TeamDetailsBundle team;
	public List<InstructorAttributes> instructors;
}
