package teammates.storage.entity;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Extension;

/**
 * AccountHolder is a persistent data class that holds information pertaining to
 * all types of Teammates accounts.
 * 
 * @author Gerald GOH
 * 
 */
@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class Account {
	@PrimaryKey
	@Persistent
	private String googleID;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String name;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String email;

	/**
	 * Constructs an Account object.
	 * 
	 * @param googleID
	 * @param name
	 * @param email
	 */
	//TODO:shouldn't these be trimmed too?
	public Account(String googleID, String name, String email) {
		this.setGoogleID(googleID);
		this.setName(name);
		this.setEmail(email);
	}

	public void setGoogleID(String googleID) {
		this.googleID = googleID.trim();
	}

	public String getGoogleID() {
		return googleID;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getName() {
		return name;
	}

	public void setEmail(String email) {
		this.email = email.trim();
	}

	public String getEmail() {
		return email;
	}
}
