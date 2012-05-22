package teammates.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import teammates.Common;
import teammates.exception.EntityAlreadyExistsException;
import teammates.exception.CourseInputInvalidException;
import teammates.exception.InvalidParametersException;

import com.google.gson.annotations.SerializedName;

/**
 * Course is a persistent data class that holds information pertaining to a
 * course on Teammates.
 * 
 * @author Gerald GOH
 * 
 */
@PersistenceCapable
public class Course {
	@PrimaryKey
	@Persistent
	@SerializedName("id")
	private String ID;

	//TODO: remove SerivalizedName annotation. It is for Json only
	@Persistent
	@SerializedName("name")
	private String name;

	@Persistent
	private String coordinatorID;

	@Persistent
	private transient boolean archived;

	/**
	 * Constructs a Course object.
	 * 
	 * @param ID
	 * @param name
	 * @param coordinatorID
	 * @throws InvalidParametersException 
	 */
	public Course(String ID, String name, String coordinatorID) throws InvalidParametersException {

		if (name.isEmpty()) {
			throw new InvalidParametersException("Course name cannot be empty");

		} else if (ID.isEmpty()) {
			throw new InvalidParametersException("Course id cannot be empty");

		} else if (ID.length() > Common.COURSE_ID_MAX_LENGTH) {
			throw new InvalidParametersException("Course ID cannot be more than "+Common.COURSE_ID_MAX_LENGTH+"characters");

		} else if (name.length() > Common.COURSE_NAME_MAX_LENGTH) {
			throw new InvalidParametersException("Course name cannot be more than "+Common.COURSE_NAME_MAX_LENGTH+"characters");

		} else if (!ID.matches("^[a-zA-Z_$0-9.-]+$")) {
			throw new InvalidParametersException(
					"Course id can have only alphabets, numbers, dashes, underscores, and dollar sign");
		}
		this.setID(ID);
		this.setName(name);
		this.setCoordinatorID(coordinatorID);
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

	public void setCoordinatorID(String coordinatorID) {
		this.coordinatorID = coordinatorID.trim();
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
