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
