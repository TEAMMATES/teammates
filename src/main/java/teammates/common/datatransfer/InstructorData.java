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
	
	// New
	public InstructorData(String id, String courseId) {
		this.googleId = id;
		this.courseId = courseId;
	}
	
	// New
	public InstructorData(Instructor instructor) {
		this.googleId = instructor.getGoogleId();
		this.courseId = instructor.getCourseId();
	}

	public InstructorData() {
		
	}

	public Instructor toEntity() {
		return new Instructor(googleId, courseId);
	}

	public String getInvalidStateInfo() {
		String errorMessage = "";

		if (!Common.isValidGoogleId(googleId)) {
			errorMessage += ERROR_FIELD_ID;
		}
		
		if (!Common.isValidCourseId(courseId)) {
			errorMessage += ERROR_FIELD_COURSEID;
		}

		return errorMessage;
	}
}
