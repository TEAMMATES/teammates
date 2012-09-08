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
	public String name; 	
	public String email;
	
	public CoordData(String id, String name, String email){
		this();
		this.id = id;
		this.name = name;
		this.email = email;
		validate();
	}
	
	public CoordData(Coordinator coord) {
		this();
		this.id = coord.getGoogleID();
		this.name = coord.getName();
		this.email = coord.getEmail();
		validate();
	}
	
	public CoordData(){
		isCoord = true;
	}
	
	public Coordinator toEntity() {
		return new Coordinator(id, name, email);
	}
	
	public void validate() {
		/*
		Assumption.assertThat(id!=null);
		Assumption.assertThat(name!=null);
		Assumption.assertThat(email!=null);
		*/
	}
}
