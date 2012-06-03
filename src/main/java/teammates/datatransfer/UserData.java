package teammates.datatransfer;

import teammates.api.APIServlet.UserType;

public class UserData {
	public String name;
	public String id;
	public String email;
	public UserType type = UserType.UNREGISTERED;
	
	public UserData(){
	}
	
	public UserData(String googleId) {
		this.id = googleId;
	}

	public boolean isAdmin(){
		return (type == UserType.ADMIN); 
	}
	
	public boolean isCoord(){
		return (type == UserType.COORDINATOR); 
	}
	
	public boolean isStudent(){
		return (type == UserType.STUDENT); 
	} 
	
}
