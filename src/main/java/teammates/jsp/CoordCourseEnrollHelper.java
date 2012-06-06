package teammates.jsp;

import java.util.List;

import teammates.datatransfer.StudentData;
import teammates.datatransfer.StudentData.UpdateStatus;

public class CoordCourseEnrollHelper extends Helper{
	// Specific parameters
	public String courseID;
	
	/**
	 * Flag whether this page should show the result or the enrollment input
	 */
	public boolean isResult = false;
	public List<StudentData> studentsError;
	public List<StudentData> studentsNew;
	public List<StudentData> studentsModified;
	public List<StudentData> studentsUnmodified;
	public List<StudentData> studentsOld;
	public List<StudentData> studentsUnknown;

	public CoordCourseEnrollHelper(Helper helper) {
		super(helper);
	}
	
	public static String getEnrollmentStatus(UpdateStatus en){
		switch(en){
		case ERROR: return "Error";
		case NEW: return "New";
		case MODIFIED: return "Modified";
		case UNMODIFIED: return "Unmodified";
		case NOT_IN_ENROLL_LIST: return "Existing";
		default: return "Unknown";
		}
	}
}
