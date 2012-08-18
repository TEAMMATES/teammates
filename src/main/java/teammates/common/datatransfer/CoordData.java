package teammates.common.datatransfer;

import java.util.HashMap;

import teammates.storage.entity.Coordinator;

/**
 * A shallow copy of the actual Coordinator entity
 * 
 * @author Kenny
 *
 */
public class CoordData extends UserData{
	public HashMap<String, CourseData> courses;
	//private String name;
	//private String email;
	public String name; 	// Used for test cases- Will change test cases in another issue
	public String email;	// As above
	
	public CoordData(String id, String name, String email){
		this();
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
	public CoordData(Coordinator coord) {
		this();
		this.id = coord.getGoogleID();
		this.name = coord.getName();
		this.email = coord.getEmail();
	}
	
	public CoordData(){
		isCoord = true;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getEmail() {
		return email;
	}
}
