package teammates.storage.entity;

import javax.jdo.annotations.PersistenceCapable;

/**
 * Coordinator is a persistent data class that holds information pertaining to a
 * coordinator on Teammates. 
 */
@PersistenceCapable
public class Coordinator extends Account {
	
	public Coordinator(String googleID, String name, String email) {
		super(googleID, name, email);
	}
}
