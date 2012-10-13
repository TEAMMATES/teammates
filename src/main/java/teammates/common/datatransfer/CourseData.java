package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.storage.entity.Course;

public class CourseData {
	public String id;
	public String name;
	public String coord;

	// these are marked transient because we don't want to involve them in
	// Json conversions.
	public transient int teamsTotal = Common.UNINITIALIZED_INT;
	public transient int studentsTotal = Common.UNINITIALIZED_INT;
	public transient int unregisteredTotal = Common.UNINITIALIZED_INT;
	public transient ArrayList<EvaluationData> evaluations = new ArrayList<EvaluationData>();
	public transient ArrayList<TeamData> teams = new ArrayList<TeamData>();
	// TODO: to be removed as we don't allow loners
	public transient ArrayList<StudentData> loners = new ArrayList<StudentData>();

	public static final int COURSE_NAME_MAX_LENGTH = 38;
	
	public static final String ERROR_FIELD_ID = "Course ID cannot be null or empty\n";
	public static final String ERROR_ID_TOOLONG = "Course ID cannot be more than " + Common.COURSE_ID_MAX_LENGTH + " characters\n";
	public static final String ERROR_ID_INVALIDCHARS = "Course ID can have only alphabets, numbers, dashes, underscores, and dollar sign\n";
	public static final String ERROR_FIELD_NAME = "Course name cannot be null or empty\n";
	public static final String ERROR_NAME_TOOLONG = "Course name cannot be more than " + COURSE_NAME_MAX_LENGTH + " characters\n";
	public static final String ERROR_FIELD_COORD = "Course must belong to a valid Coordinator\n";

	private static Logger log = Common.getLogger();

	public CourseData() {

	}

	public CourseData(String id, String name, String coordId) {
		this.id = id == null ? null : id.trim();
		this.name = name == null ? null : name.trim();
		this.coord = coordId == null ? null : coordId.trim();
	}

	public CourseData(Course course) {
		this.id = course.getID();
		this.name = course.getName();
		this.coord = course.getCoordinatorID();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\ncourse id: " + id);
		sb.append("\ncourse name: "+ name);
		sb.append("\ncoord: " + coord + "\n");
		return sb.toString();
	}
	public Course toEntity() {
		return new Course(id, name, coord);
	}

	public boolean isValid() {
		if (Common.isValidCourseId(id) &&
			Common.isValidName(name) &&
			name.length() <= COURSE_NAME_MAX_LENGTH  && 
			Common.isValidGoogleId(coord)) {
			return true;
		}
		return false;
	}

	public String getInvalidStateInfo() {
		String errorMessage = "";

		// Validate ID not null, empty
		if (!Common.isValidString(id)) {
			errorMessage += ERROR_FIELD_ID;
		} else {
			// ID greater than max length
			if (id.length() > Common.COURSE_ID_MAX_LENGTH) {
				errorMessage += ERROR_ID_TOOLONG;
			}

			// ID contains invalid chars
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

		if (!Common.isValidGoogleId(coord)) {
			errorMessage += ERROR_FIELD_COORD;
		}

		return errorMessage;
	}
}
