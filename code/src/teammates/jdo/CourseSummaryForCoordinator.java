package teammates.jdo;

/**
 * CourseSummaryForCoordinator is a data class that contains some information from a
 * Course object and an additional field which is the number of teams currently in the course.
 *  
 * @author Gerald GOH
 *
 */
public class CourseSummaryForCoordinator
{
	private String ID;	
	private String name;
	private boolean archived;
	private int numberOfTeams;
	
	public CourseSummaryForCoordinator(String ID, String name, boolean archived, int numberOfTeams)
	{
		this.setID(ID);
		this.setName(name);
		this.setArchived(archived);
		this.setNumberOfTeams(numberOfTeams);
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
	
	
}