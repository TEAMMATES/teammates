package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
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

	public CourseAttributes(String courseId, String name) {
		this.id = Sanitizer.sanitizeTitle(courseId);
		this.name = Sanitizer.sanitizeTitle(name);
	}

	public CourseAttributes(Course course) {
		this.id = course.getUniqueId();
		this.name = course.getName();
		this.createdAt = course.getCreatedAt();
	}
	
	public List<String> getInvalidityInfo() {
		
		FieldValidator validator = new FieldValidator();
		List<String> errors = new ArrayList<String>();
		String error;
		
		error= validator.getInvalidityInfo(FieldType.COURSE_ID, id);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.COURSE_NAME, name);
		if(!error.isEmpty()) { errors.add(error); }
		
		return errors;
	}

	public Course toEntity() {
		return new Course(id, name);
	}

	public String toString() {
		return "["+CourseAttributes.class.getSimpleName() +"] id: " + id +" name: "+ name;
	}

	@Override
	public String getIdentificationString() {
		return this.id;
	}

	@Override
	public String getEntityTypeAsString() {
		return "Course";
	}

	@Override
	public void sanitizeForSaving() {
		// TODO implement this
	}
}
