package teammates.common.datatransfer;

/**
 * Represents a logged-in user.
 * <br> Contains the internal account id (UUID string), the login identifier,
 * and flags to indicate whether the user is an admin, instructor, student, or maintainer.
 */
public class UserInfo {

    /**
     * Internal {@link teammates.storage.sqlentity.Account} id (UUID string).
     */
    public String id;

    /**
     * Login identifier of the user (e.g. email address from OIDC provider).
     */
    public String loginIdentifier;

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

    public UserInfo(String accountId) {
        this.id = accountId;
    }

    public String getId() {
        return id;
    }

    public String getLoginIdentifier() {
        return loginIdentifier;
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
