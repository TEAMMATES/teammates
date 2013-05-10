package teammates.common.datatransfer;

import java.util.Date;

import teammates.common.Assumption;
import teammates.common.Common;
import static teammates.common.Common.EOL;
import teammates.common.FieldValidator;
import teammates.common.FieldValidator.FieldType;
import teammates.storage.entity.Course;

public class CourseAttributes extends EntityAttributes {
	public String id;
	public String name;
	public Date createdAt;

	public static final int COURSE_NAME_MAX_LENGTH = 64;
	
	public static final String ERROR_FIELD_ID = "Course ID cannot be null or empty";
	public static final String ERROR_ID_TOOLONG = "Course ID cannot be more than " + Common.COURSE_ID_MAX_LENGTH + " characters\n";
	public static final String ERROR_ID_INVALIDCHARS = "Course ID can have only alphabets, numbers, dashes, underscores, and dollar sign";
	public static final String ERROR_FIELD_NAME = "Course name cannot be null or empty\n";
	public static final String ERROR_NAME_TOOLONG = "Course name cannot be more than " + COURSE_NAME_MAX_LENGTH + " characters\n";
	
	public CourseAttributes() {

	}

	public CourseAttributes(String id, String name) {
		this.id = Common.trimIfNotNull(id);
		this.name = Common.trimIfNotNull(name);
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
		Assumption.assertTrue(id!=null);
		Assumption.assertTrue(name!=null);
		
		FieldValidator validator = new FieldValidator();
		String errorMessage = 
				validator.getValidityInfo(FieldType.COURSE_ID, id) + EOL+
				validator.getValidityInfo(FieldType.COURSE_NAME, name) + EOL;

		return errorMessage.trim();
	}
}
