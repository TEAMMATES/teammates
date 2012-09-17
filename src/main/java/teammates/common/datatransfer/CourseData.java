package teammates.common.datatransfer;

import java.util.ArrayList;

import teammates.common.Common;
import teammates.common.exception.InvalidParametersException;
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

	public String getInvalidParametersInfo() {
		String fieldChecks = "";

		// Validate ID not null, empty, less than max length and acceptable format
		if (this.id == null || this.id == "") {
			fieldChecks += "Course ID cannot be null or empty\n";
		} else {

			if (this.id.length() > Common.COURSE_ID_MAX_LENGTH) {
				fieldChecks += "Course ID cannot be more than "
						+ Common.COURSE_ID_MAX_LENGTH + " characters\n";
			}

			if (!this.id.matches("^[a-zA-Z_$0-9.-]+$")) {
				fieldChecks += "Course ID can have only alphabets, numbers, dashes, underscores, and dollar sign\n";
			}
		}

		// Validate name not null, empty and less than max length
		if (this.name == null || this.name == "") {
			fieldChecks += "Course name cannot be null or empty\n";
		} else if (name.length() > Common.COURSE_NAME_MAX_LENGTH) {
			fieldChecks += "Course name cannot be more than "
					+ Common.COURSE_NAME_MAX_LENGTH + " characters\n";
		}

		if (this.coord == null || this.coord == "") {
			fieldChecks += "Course must belong to a Coordinator\n";
		}

		return fieldChecks;
	}
}
