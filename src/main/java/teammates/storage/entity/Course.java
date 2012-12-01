package teammates.storage.entity;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Course is a persistent data class that holds information pertaining to a
 * course on Teammates.
 */
@PersistenceCapable
public class Course {
	@PrimaryKey
	@Persistent
	@SerializedName("id")
	private String ID;

	@Persistent
	private String name;

	@Persistent
	private String instructorID;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private transient boolean archived;

	/**
	 * Constructs a Course object.
	 * 
	 * @param ID
	 * @param name
	 * @param instructorID
	 */
	public Course(String ID, String name, String instructorID) {
		this.setID(ID);
		this.setName(name);
		this.setInstructorID(instructorID);
		this.setArchived(false);
	}

	public void setID(String ID) {
		this.ID = ID.trim();
	}

	public String getID() {
		return ID;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getName() {
		return name;
	}

	public void setInstructorID(String instructorID) {
		this.instructorID = instructorID.trim();
	}

	public String getInstructorID() {
		return instructorID;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isArchived() {
		return archived;
	}

}
