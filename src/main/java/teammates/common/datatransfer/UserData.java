package teammates.common.datatransfer;


public class UserData {
	public String id = null;
	
	public boolean isAdmin = false;
	public boolean isCoord = false;
	public boolean isStudent = false;
	
	public UserData(){
	}
	
	public UserData(String googleId) {
		this.id = googleId;
	}

	@Deprecated
	public boolean isAdmin(){
		return isAdmin; 
	}
	
	@Deprecated
	public boolean isCoord(){
		return isCoord; 
	}
	
	@Deprecated
	public boolean isStudent(){
		return  isStudent; 
	}
	
}
