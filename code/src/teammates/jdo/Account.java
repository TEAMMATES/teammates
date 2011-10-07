package teammates.jdo;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * AccountHolder is a persistent data class that holds information pertaining to
 * all types of Teammates accounts. 
 * @author  Gerald GOH
 * @version 1.0, December 2010
 */
@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE) 
public class Account
{
	@PrimaryKey
	@Persistent
	private String googleID;
	
	@Persistent
	private String name;
	
	@Persistent
	private String email;
	
	/**
	 * Constructs an Account object.
	 * 
	 * @param googleID
	 * @param name
	 * @param email
	 */
	public Account(String googleID, String name, String email) 
	{
		this.googleID = googleID;
		this.name = name;
		this.email = email;
	}
		
	/**
	 * Returns the Google ID. 
	 * @return sGoogleID
	 */
	public String getGoogleID() 
	{
		return googleID;
	}
	
	/**
	 * Returns the name.
	 * @return sName
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the e-mail.
	 * @return sEmail
	 */
	public String getEmail()
	{
		return email;
	}
	
	/**
	 * Sets the Google ID.
	 * @param sGoogleID
	 */
	public void setGoogleID(String googleID) 
	{
		this.googleID = googleID;
	}
	
	/**
	 * Sets the name.
	 * @param sName
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Sets the e-mail.
	 * @param sEmail
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

}
