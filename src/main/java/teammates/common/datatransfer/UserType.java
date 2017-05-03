package teammates.common.datatransfer;

import com.google.appengine.api.users.User;

/**
 * Represents a user type.
 * <br> Contains user's Google ID and flags to indicate whether the user
 *  is an admin, instructor, student.
 */
public class UserType {

    //TODO: add a com.google.appengine.api.users.User object here
    public String id;

    public boolean isAdmin;
    public boolean isInstructor;
    public boolean isStudent;

    public UserType(String googleId) {
        this.id = googleId;
    }

    public UserType(User user) {
        this.id = user.getNickname();
    }
}
