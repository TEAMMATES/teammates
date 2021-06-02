package teammates.common.datatransfer;

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
    public boolean isSeniorDeveloper;

    public UserInfo(String googleId) {
        this.id = googleId;
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

    public boolean getIsSeniorDeveloper() {
        return isSeniorDeveloper;
    }

}
