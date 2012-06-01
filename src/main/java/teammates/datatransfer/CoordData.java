package teammates.datatransfer;

import java.util.HashMap;


public class CoordData {
	public String id;
	public String name;
	public String email;
	public HashMap<String, CourseData> courses;
	
	public CoordData(String data){
		
	}
	
	public CoordData(String id, String name, String email){
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
	public CoordData(){
		
	}
	
	public CoordData sId(String id){
		this.id = id;
		return this;
	}
	
	public CoordData sName(String name){
		this.name = name;
		return this;
	}
	
	public CoordData sEmail(String email){
		this.email = email;
		return this;
	}
	
}
