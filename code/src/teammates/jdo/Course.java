package teammates.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Course is a persistent data class that holds information pertaining to a
 * course on Teammates.
 * @author Gerald GOH
 *
 */
@PersistenceCapable
public class Course 
{
	@PrimaryKey
	@Persistent
	@SerializedName("id")
	private String ID;
	
	@Persistent
	@SerializedName("name")
	private String name;
	
	@Persistent
	private transient String coordinatorID;
	
	@Persistent
	private transient boolean archived;

	public Course(String ID, String name, String coordinatorID)
	{
		this.ID = ID;
		this.name = name;
		this.coordinatorID = coordinatorID;
		this.archived = false;
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

	public void setCoordinatorID(String coordinatorID) {
		this.coordinatorID = coordinatorID;
	}

	public String getCoordinatorID() {
		return coordinatorID;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isArchived() {
		return archived;
	}

}
	
	