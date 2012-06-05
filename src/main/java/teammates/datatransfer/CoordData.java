package teammates.datatransfer;

import java.util.HashMap;

import teammates.api.APIServlet.UserType;


public class CoordData extends UserData{
	public HashMap<String, CourseData> courses;
	public String name;
	public String email;
	
	public CoordData(String id, String name, String email){
		this();
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
	public CoordData(){
		isCoord = true;
	}
	
}
