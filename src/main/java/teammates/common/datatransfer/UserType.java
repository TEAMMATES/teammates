package teammates.common.datatransfer;

/**
 * Represents a user type.
 * <br> Contains user's Google ID and flags to indicate whether the user
 *  is an admin, instructor, student.
 */
public class UserType {
	public String id = null;
	
	public boolean isAdmin = false;
	public boolean isInstructor = false;
	public boolean isStudent = false;
	
	public UserType() {
	}
	
	public UserType(String googleId) {
		this.id = googleId;
	}
}
