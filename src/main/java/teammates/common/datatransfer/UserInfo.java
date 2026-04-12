package teammates.common.datatransfer;

import java.util.UUID;

/**
 * Represents a user type.
 * <br> Contains user's account ID and flags to indicate whether the user
 *  is an admin, instructor, student.
 */
public class UserInfo {

    /**
     * The user's account ID.
     */
    public UUID accountId;

    /**
     * Indicates whether the user has admin privilege.
     */
    public boolean isAdmin;

    /**
     * Indicates whether the user has instructor privilege.
     */
    public boolean isInstructor;

    /**
     * Indicates whether the user has student privilege.
     */
    public boolean isStudent;

    /**
     * Indicates whether the user has maintainer privilege.
     */
    public boolean isMaintainer;

    public UserInfo(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getAccountId() {
        return accountId;
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

    public boolean getIsMaintainer() {
        return isMaintainer;
    }

}
