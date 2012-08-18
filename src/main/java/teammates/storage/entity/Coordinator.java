package teammates.storage.entity;

import javax.jdo.annotations.PersistenceCapable;

import teammates.common.datatransfer.CoordData;

/**
 * Coordinator is a persistent data class that holds information pertaining to a
 * coordinator on Teammates. 
 */
@PersistenceCapable
public class Coordinator extends Account {
	
	public Coordinator(String googleID, String name, String email) {
		super(googleID, name, email);
	}
	
	public Coordinator(CoordData coordData) {
		super(coordData.getId(),coordData.getName(),coordData.getEmail());
	}
}
