package teammates.common.datatransfer;

import com.google.appengine.api.users.User;

/**
 * Represents a user type.
 * <br> Contains user's Google ID and flags to indicate whether the user
 *  is an admin, instructor, student.
 */
public class UserType {
    
    /** Represents the role played by the user. e.g. when loading 
     * instructorHomePage, the user is playing the role INSTRUCTOR */
    public enum Role {ADMIN, INSTRUCTOR, STUDENT};
    
    //TODO: add a com.google.appengine.api.users.User object here 
    public String id = null;
    
    public boolean isAdmin = false;
    public boolean isInstructor = false;
    public boolean isStudent = false;
    
    public UserType() {
    }
    
    public UserType(String googleId) {
        this.id = googleId;
    }
    
    public UserType(User user) {
        this.id = user.getNickname();
    }
}
