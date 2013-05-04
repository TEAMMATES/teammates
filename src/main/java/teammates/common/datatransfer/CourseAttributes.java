package teammates.common.datatransfer;

import java.util.Date;

import teammates.common.Common;
import teammates.storage.entity.Course;

public class CourseAttributes extends EntityAttributes {
	public String id;
	public String name;
	public Date createdAt;

	public static final int COURSE_NAME_MAX_LENGTH = 64;
	
	public static final String ERROR_FIELD_ID = "Course ID cannot be null or empty\n";
	public static final String ERROR_ID_TOOLONG = "Course ID cannot be more than " + Common.COURSE_ID_MAX_LENGTH + " characters\n";
	public static final String ERROR_ID_INVALIDCHARS = "Course ID can have only alphabets, numbers, dashes, underscores, and dollar sign\n";
	public static final String ERROR_FIELD_NAME = "Course name cannot be null or empty\n";
	public static final String ERROR_NAME_TOOLONG = "Course name cannot be more than " + COURSE_NAME_MAX_LENGTH + " characters\n";
	
	public CourseAttributes() {

	}

	public CourseAttributes(String id, String name) {
		this.id = trimIfNotNull(id);
		this.name = trimIfNotNull(name);
	}

	public CourseAttributes(Course course) {
		this.id = course.getUniqueId();
		this.name = course.getName();
		this.createdAt = course.getCreatedAt();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\ncourse id: " + id);
		sb.append("\ncourse name: "+ name);
		return sb.toString();
	}
	
	public Course toEntity() {
		return new Course(id, name);
	}

	public String getInvalidStateInfo() {
		String errorMessage = "";

		if (!Common.isValidString(id)) {
			errorMessage += ERROR_FIELD_ID;
		} else {
			if (id.length() > Common.COURSE_ID_MAX_LENGTH) {
				errorMessage += (ERROR_ID_TOOLONG + ":" + id) ;
			}

			if (!id.matches("^[a-zA-Z_$0-9.-]+$")) {
				errorMessage += ERROR_ID_INVALIDCHARS;
			}
		}

		// Validate name not null, empty and less than max length
		if (!Common.isValidName(name)) {
			errorMessage += ERROR_FIELD_NAME;
		} else if (name.length() > COURSE_NAME_MAX_LENGTH) {
			errorMessage += ERROR_NAME_TOOLONG;
		}
		
		return errorMessage;
	}
}
