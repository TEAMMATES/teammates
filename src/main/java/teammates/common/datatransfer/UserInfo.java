package teammates.common.datatransfer;

/**
 * Represents a user type.
 * <br> Contains user's Google ID and flags to indicate whether the user
 *  is an admin, instructor, student.
 */
public class UserInfo {

    /**
     * The user's Google ID.
     */
    public String id;

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

    /**
     * True only for verified cron/worker internal callers (bearer + path), not for human app admins.
     * See {@link teammates.common.util.InternalRequestAuth#isTrustedCronOrWorkerRequest}.
     */
    public boolean isInternalService;

    public UserInfo(String googleId) {
        this.id = googleId;
    }

    /**
     * Whether the caller may use endpoints shared by human app administrators and verified internal jobs.
     */
    public boolean canAccessAsAdminOrInternalService() {
        return isAdmin || isInternalService;
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

    public boolean getIsMaintainer() {
        return isMaintainer;
    }

}
