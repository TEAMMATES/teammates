package teammates.common.datatransfer;

import com.google.appengine.api.users.User;

/**
 * Represents a user type.
 * <br> Contains user's Google ID and flags to indicate whether the user
 *  is an admin, instructor, student.
 */
public class UserInfo {

    public String id;

    public boolean isAdmin;
    public boolean isInstructor;
    public boolean isStudent;

    public UserInfo(String googleId) {
        this.id = googleId;
    }

    public UserInfo(User user) {
        this.id = user.getNickname();
    }

    public String getId() {
        return id;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public boolean getIsInstructor() {
        return isInstructor;
    }

    public boolean getIsStudent() {
        return isStudent;
    }

}
