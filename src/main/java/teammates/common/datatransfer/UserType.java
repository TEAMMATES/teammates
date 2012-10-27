package teammates.common.datatransfer;


public class UserType {
	public String id = null;
	
	public boolean isAdmin = false;
	public boolean isCoord = false;
	public boolean isStudent = false;
	
	public UserType() {
	}
	
	public UserType(String googleId) {
		this.id = googleId;
	}
}
