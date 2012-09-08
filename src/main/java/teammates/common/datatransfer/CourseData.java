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
	//TODO: to be removed as we don't allow loners
	public transient ArrayList<StudentData> loners = new ArrayList<StudentData>();

	public CourseData() {

	}

	public CourseData(String id, String name, String coordId) {
		this.id = id;
		this.name = name;
		this.coord = coordId;
		validate();
	}

	public CourseData(Course course) {
		this.id = course.getID();
		this.name = course.getName();
		this.coord = course.getCoordinatorID();
		validate();
	}
	
	public Course toEntity() throws InvalidParametersException{

		// This try and catch block is just here to make it work.
		// The verification method will be shifted in a separate issue, and this block will be removed accordingly
		try {
			return new Course(id, name, coord);
		} catch (InvalidParametersException e) {
			throw e;
		}
	}
	
	public void validate() {
		/*
		Assumption.assertThat(id != null);
		Assumption.assertThat(name != null);
		Assumption.assertThat(coord != null);
		*/
	}
}
