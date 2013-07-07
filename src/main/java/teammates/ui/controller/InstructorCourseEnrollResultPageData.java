package teammates.ui.controller;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.util.Utils;

public class InstructorCourseEnrollResultPageData extends PageData {
	
	public InstructorCourseEnrollResultPageData(AccountAttributes account) {
		super(account);
	}

	protected static final Logger log = Utils.getLogger();
	
	public String courseId;

	public List<StudentAttributes>[] students;
	
	public String getMessageForEnrollmentStatus(int enrollmentStatus) {

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
