package teammates.common.datatransfer;

import java.util.HashMap;

import teammates.common.Common;
import teammates.storage.entity.Coordinator;
import teammates.storage.entity.Instructor;

public class InstructorData extends BaseData {
	public HashMap<String, CourseData> courses;
	public String googleId;
	public String name;
	public String email;
	
	// New
	public String courseId;
	
	public static final String ERROR_FIELD_ID = "Instructor ID is invalid\n";
	public static final String ERROR_FIELD_NAME = "Instructor name cannot be null or empty\n";
	public static final String ERROR_FIELD_EMAIL = "Instructor email is invalid\n";
	public static final String ERROR_FIELD_COURSEID = "Course ID is invalid\n";

	public InstructorData(Coordinator instructor) {
		this();
		this.googleId = instructor.getGoogleID();
		this.name = instructor.getName();
		this.email = instructor.getEmail();
	}
	
	public InstructorData(String id, String courseId, String name, String email) {
		this.googleId = id;
		this.courseId = courseId;
		this.name = name;
		this.email = email;
	}
	
	public InstructorData(Instructor instructor) {
		this.googleId = instructor.getGoogleId();
		this.courseId = instructor.getCourseId();
		this.name = instructor.getName();
		this.email = instructor.getEmail();
	}

	public InstructorData() {
		
	}

	public Instructor toEntity() {
		return new Instructor(googleId, courseId, name, email);
	}

	public String getInvalidStateInfo() {
		String errorMessage = "";

		if (!Common.isValidGoogleId(googleId)) {
			errorMessage += ERROR_FIELD_ID;
		}
		
		if (!Common.isValidCourseId(courseId)) {
			errorMessage += ERROR_FIELD_COURSEID;
		}
		
		if (!Common.isValidName(name)) {
			errorMessage += ERROR_FIELD_NAME;
		}
		
		if (!Common.isValidEmail(email)) {
			errorMessage += ERROR_FIELD_EMAIL;
		}

		return errorMessage;
	}
}
