package teammates.common.datatransfer;

import static teammates.common.Common.EOL;

import java.util.Date;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.FieldValidator.FieldType;
import teammates.storage.entity.Course;

/**
 * The data transfer object for Course entities.
 */
public class CourseAttributes extends EntityAttributes {
	
	//Note: be careful when changing these variables as their names are used in *.json files.
	public String id;
	public String name;
	public Date createdAt;

	
	public CourseAttributes() {

	}

	public CourseAttributes(String id, String name) {
		//TODO: this method should follow our normal sanitization policy
		// (when we have one).
		this.id = Common.trimIfNotNull(id);
		this.name = Common.trimIfNotNull(name);
	}

	public CourseAttributes(Course course) {
		this.id = course.getUniqueId();
		this.name = course.getName();
		this.createdAt = course.getCreatedAt();
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

	public Course toEntity() {
		return new Course(id, name);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\ncourse id: " + id);
		sb.append("\ncourse name: "+ name);
		return sb.toString();
	}
}
