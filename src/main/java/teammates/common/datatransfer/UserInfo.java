package teammates.common.datatransfer;

import java.util.UUID;

/**
 * Represents a user type.
 */
public class UserInfo {

    /**
     * The user's Google ID.
     */
    public String id;

    /**
     * The user's account ID.
     */
    public UUID accountId;

    /**
     * The user's email address.
     */
    public String email;

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

    public UserInfo(String googleId, UUID accountId, String email) {
        this.id = googleId;
        this.accountId = accountId;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
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
