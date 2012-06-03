package teammates.jdo;

import java.util.ArrayList;

import teammates.api.Common;

/**
 * CourseSummaryForCoordinator is a data class that contains some information
 * from a Course object and an additional field which is the number of teams
 * currently in the course.
 * 
 * @author Gerald GOH
 * @deprecated Use CourseData instead.
 * 
 */
public class CourseSummaryForCoordinator {
	public String ID = null;
	public String name = null;
	public boolean archived = false;
	public int numberOfTeams = Common.UNINITIALIZED_INT;
	public int totalStudents = Common.UNINITIALIZED_INT;
	public int unregistered = Common.UNINITIALIZED_INT;
	public ArrayList<EvaluationDetailsForCoordinator> evaluations = new ArrayList<EvaluationDetailsForCoordinator>();

	/**
	 * Constructs a CourseSummaryForCoordinator object.
	 * 
	 * @param ID
	 * @param name
	 * @param archived
	 * @param numberOfTeams
	 * @param totalStudents
	 * @param unregistered
	 */
	public CourseSummaryForCoordinator(String ID, String name,
			boolean archived, int numberOfTeams, int totalStudents,
			int unregistered) {
		this.setID(ID);
		this.setName(name);
		this.setArchived(archived);
		this.setNumberOfTeams(numberOfTeams);
		this.setTotalStudents(totalStudents);
		this.setUnregistered(unregistered);
	}
	
	public CourseSummaryForCoordinator(String courseId){
		this.ID = courseId;
	}

	@Deprecated
	public void setID(String ID) {
		this.ID = ID;
	}

	@Deprecated
	public String getID() {
		return ID;
	}

	@Deprecated
	public void setName(String name) {
		this.name = name;
	}

	@Deprecated
	public String getName() {
		return name;
	}

	@Deprecated
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Deprecated
	public boolean isArchived() {
		return archived;
	}

	@Deprecated
	public void setNumberOfTeams(int numberOfTeams) {
		this.numberOfTeams = numberOfTeams;
	}

	@Deprecated
	public int getNumberOfTeams() {
		return numberOfTeams;
	}

	@Deprecated
	public int getTotalStudents() {
		return totalStudents;
	}

	@Deprecated
	public void setTotalStudents(int totalStudents) {
		this.totalStudents = totalStudents;
	}
	
	@Deprecated
	public int getUnregistered() {
		return unregistered;
	}

	@Deprecated
	public void setUnregistered(int unregistered) {
		this.unregistered = unregistered;
	}
}
