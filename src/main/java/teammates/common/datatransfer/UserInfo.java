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
     * The user's account email address.
     */
    public String accountEmail;

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

    public UserInfo(String googleId, UUID accountId, String accountEmail) {
        this.id = googleId;
        this.accountId = accountId;
        this.accountEmail = accountEmail;
    }

    public String getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getAccountEmail() {
        return accountEmail;
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
