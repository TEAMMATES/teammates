package teammates.jdo;

import javax.jdo.annotations.PersistenceCapable;

/**
 * Coordinator is a persistent data class that holds information pertaining to a
 * coordinator on Teammates. It inherits from AccountHolder.
 * @author Gerald GOH
 * @see AccountHolder
 *
 */
@PersistenceCapable
public class Coordinator extends Account
{
	/**
	 * Constructs a Coordinator object.
	 * 
	 * @param googleID
	 * @param name
	 * @param email
	 */
	public Coordinator(String googleID, String name, String email) 
	{
		super(googleID, name, email);
	}

}
