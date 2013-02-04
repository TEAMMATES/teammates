package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.storage.entity.Course;

public class CourseData extends BaseData {
	public String id;
	public String name;
	public Date createdAt;

	// these are marked transient because we don't want to involve them in
	// Json conversions.
	public transient int teamsTotal = Common.UNINITIALIZED_INT;
	public transient int studentsTotal = Common.UNINITIALIZED_INT;
	public transient int unregisteredTotal = Common.UNINITIALIZED_INT;
	public transient ArrayList<EvaluationData> evaluations = new ArrayList<EvaluationData>();
	public transient ArrayList<TeamData> teams = new ArrayList<TeamData>();
	// TODO: to be removed as we don't allow loners
	public transient ArrayList<StudentData> loners = new ArrayList<StudentData>();

	private static Logger log = Common.getLogger();
	
	public static final int COURSE_NAME_MAX_LENGTH = 38;
	
	public static final String ERROR_FIELD_ID = "Course ID cannot be null or empty\n";
	public static final String ERROR_ID_TOOLONG = "Course ID cannot be more than " + Common.COURSE_ID_MAX_LENGTH + " characters\n";
	public static final String ERROR_ID_INVALIDCHARS = "Course ID can have only alphabets, numbers, dashes, underscores, and dollar sign\n";
	public static final String ERROR_FIELD_NAME = "Course name cannot be null or empty\n";
	public static final String ERROR_NAME_TOOLONG = "Course name cannot be more than " + COURSE_NAME_MAX_LENGTH + " characters\n";
	
	public CourseData() {

	}

	public CourseData(String id, String name) {
		this.id = trimIfNotNull(id);
		this.name = trimIfNotNull(name);
	}

	public CourseData(Course course) {
		this.id = course.getID();
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
