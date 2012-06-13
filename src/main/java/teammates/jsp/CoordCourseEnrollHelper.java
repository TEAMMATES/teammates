package teammates.jsp;

import java.util.List;

import teammates.datatransfer.StudentData;

public class CoordCourseEnrollHelper extends Helper{
	// Specific parameters
	public String courseID;
	
	/**
	 * Flag whether this page should show the result or the enrollment input
	 */
	public boolean isResult = false;
	public List<StudentData>[] students;
	
	public String getMessageForStudentsListID(int idx){
		switch(idx){
		case 0: return String.format("There were errors on %d student(s):",students[idx].size());
		case 1: return String.format("There are %d student(s) added:",students[idx].size());
		case 2: return String.format("There are %d student(s) modified:",students[idx].size());
		case 3: return String.format("There are %d student(s) unmodified:",students[idx].size());
		case 4: return String.format("There are %d other student(s) previously in the course:",students[idx].size());
		case 5: return String.format("There are %d unknown student(s):",students[idx].size());
		default: return "There are students:";
		}
	}
}
