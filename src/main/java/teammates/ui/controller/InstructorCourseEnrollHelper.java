package teammates.ui.controller;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.StudentData.UpdateStatus;

public class InstructorCourseEnrollHelper extends Helper {
	public String courseID;

	/**
	 * Flag whether this page should show the result or the enrollment input
	 */
	public boolean isResult = false;
	
	public List<StudentData>[] students;
	Logger log = Common.getLogger();

	public String getMessageForStudentsListID(int enrollmentStatus) {
		
		UpdateStatus status = UpdateStatus.enumRepresentation(enrollmentStatus);
		
		switch (status) {
		case ERROR:
			return String.format("There were errors on %d student(s):",
					students[UpdateStatus.ERROR.numericRepresentation].size());
		case NEW:
			return String.format("There are %d student(s) added:",
					students[UpdateStatus.NEW.numericRepresentation].size());
		case MODIFIED:
			return String.format("There are %d student(s) modified:",
					students[UpdateStatus.MODIFIED.numericRepresentation]
							.size());
		case UNMODIFIED:
			return String.format("There are %d student(s) unmodified:",
					students[UpdateStatus.UNMODIFIED.numericRepresentation]
							.size());
		case NOT_IN_ENROLL_LIST:
			return String
					.format("There are %d other student(s) previously in the course:",
							students[UpdateStatus.NOT_IN_ENROLL_LIST.numericRepresentation]
									.size());
		case UNKNOWN:
			return String
					.format("There are %d student(s) for which the enrollment status is unknown:",
							students[UpdateStatus.UNKNOWN.numericRepresentation]
									.size());
		default:
			log.severe("Unknown Enrollment status " + enrollmentStatus);
			return "There are students:";
		}
	}
}
