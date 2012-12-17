package teammates.common.datatransfer;

import java.util.HashMap;

import teammates.common.Common;
import teammates.storage.entity.Coordinator;
import teammates.storage.entity.Instructor;

public class InstructorData extends BaseData {
	public HashMap<String, CourseData> courses;
	public String id;
	public String name;
	public String email;
	
	// New
	public String courseId;
	public int accessLevelId;
	
	public static final String ERROR_FIELD_ID = "Instructor ID is invalid\n";
	public static final String ERROR_FIELD_NAME = "Instructor name cannot be null or empty\n";
	public static final String ERROR_FIELD_EMAIL = "Instructor email is invalid\n";
	public static final String ERROR_FIELD_COURSEID = "Course ID is invalid\n";
	
	public InstructorData(String id, String name, String email) {
		this();
		this.id = trimIfNotNull(id);
		this.name = trimIfNotNull(name);
		this.email = trimIfNotNull(email);
	}

	public InstructorData(Coordinator instructor) {
		this();
		this.id = instructor.getGoogleID();
		this.name = instructor.getName();
		this.email = instructor.getEmail();
	}
	
	// New
	public InstructorData(String id, String courseId, int accessLevelId) {
		this.id = id;
		this.courseId = courseId;
		this.accessLevelId = accessLevelId;
	}
	
	// New
	public InstructorData(Instructor instructor) {
		this.id = instructor.getGoogleId();
		this.courseId = instructor.getCourseId();
		this.accessLevelId = instructor.getAccessLevel();
	}

	public InstructorData() {
		
	}

	public Coordinator toEntity() {
		return new Coordinator(id, name, email);
	}

	public String getInvalidStateInfo() {
		String errorMessage = "";

		if (!Common.isValidGoogleId(id)) {
			errorMessage += ERROR_FIELD_ID;
		}

		if (!Common.isValidName(name)) {
			errorMessage += ERROR_FIELD_NAME;
		}

		if (!Common.isValidEmail(email)) {
			errorMessage += ERROR_FIELD_EMAIL;
		}
		
		/* 
		 * Temporarily left out as current code does not support Course ID yet.
		 * Future patch to implement backward compatability
		if (!Common.isValidCourseId(courseId)) {
			errorMessage += ERROR_FIELD_COURSEID;
		}
		*/

		return errorMessage;
	}
}
