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

	public static final String ERROR_FIELD_ID = "Course ID cannot be null or empty\n";
	public static final String ERROR_ID_TOOLONG = "Course ID cannot be more than "
			+ Common.COURSE_ID_MAX_LENGTH + " characters\n";
	public static final String ERROR_ID_INVALIDCHARS = "Course ID can have only alphabets, numbers, dashes, underscores, and dollar sign\n";
	public static final String ERROR_FIELD_NAME = "Course name cannot be null or empty\n";
	public static final String ERROR_NAME_TOOLONG = "Course name cannot be more than "
			+ Common.COURSE_NAME_MAX_LENGTH + " characters\n";
	public static final String ERROR_FIELD_COORD = "Course must belong to a Coordinator\n";

	private static Logger log = Common.getLogger();

	public CourseData() {

	}

	public CourseData(String id, String name, String coordId) {
		this.id = id;
		this.name = name;
		this.coord = coordId;
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

		if (this.id == null || this.id == ""
				|| this.id.length() > Common.COURSE_ID_MAX_LENGTH
				|| this.name == null || this.name == ""
				|| this.name.length() > Common.COURSE_NAME_MAX_LENGTH
				|| !this.id.matches("^[a-zA-Z_$0-9.-]+$") || this.coord == null
				|| this.coord == "") {
			return false;
		}

		return true;
	}

	public String getInvalidStateInfo() {
		String errorMessage = "";

		// Validate ID not null, empty, less than max length and acceptable
		// format
		if (this.id == null || this.id == "") {
			errorMessage += ERROR_FIELD_ID;
		} else {

			if (this.id.length() > Common.COURSE_ID_MAX_LENGTH) {
				errorMessage += ERROR_ID_TOOLONG;
			}

			if (!this.id.matches("^[a-zA-Z_$0-9.-]+$")) {
				errorMessage += ERROR_ID_INVALIDCHARS;
			}
		}

		// Validate name not null, empty and less than max length
		if (this.name == null || this.name == "") {
			errorMessage += ERROR_FIELD_NAME;
		} else if (name.length() > Common.COURSE_NAME_MAX_LENGTH) {
			errorMessage += ERROR_NAME_TOOLONG;
		}

		if (this.coord == null || this.coord == "") {
			errorMessage += ERROR_FIELD_COORD;
		}

		return errorMessage;
	}
}
