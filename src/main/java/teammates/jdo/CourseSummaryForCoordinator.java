package teammates.jdo;

/**
 * CourseSummaryForCoordinator is a data class that contains some information
 * from a Course object and an additional field which is the number of teams
 * currently in the course.
 * 
 * @author Gerald GOH
 * 
 */
public class CourseSummaryForCoordinator {
	private String ID;
	private String name;
	private boolean archived;
	private int numberOfTeams;
	private int totalStudents;
	private int unregistered;

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

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setNumberOfTeams(int numberOfTeams) {
		this.numberOfTeams = numberOfTeams;
	}

	public int getNumberOfTeams() {
		return numberOfTeams;
	}

	public int getTotalStudents() {
		return totalStudents;
	}

	public void setTotalStudents(int totalStudents) {
		this.totalStudents = totalStudents;
	}

	public int getUnregistered() {
		return unregistered;
	}

	public void setUnregistered(int unregistered) {
		this.unregistered = unregistered;
	}
}
