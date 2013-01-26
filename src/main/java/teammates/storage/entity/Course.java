package teammates.storage.entity;

import java.util.Date;

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
	
	// Store a single Date object when the entity is created
	// Time zone of the user will be stored in this object
	@Persistent
	private Date createdAt;
	
	/**
	 * Constructs a Course object.
	 * 
	 * @param ID
	 * @param name
	 * @param coordinatorID
	 */
	public Course(String ID, String name) {
		this.setID(ID);
		this.setName(name);
		
		this.createdAt = new Date();
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
	
	public Date getCreatedAt() {
		return this.createdAt;
	}
}
